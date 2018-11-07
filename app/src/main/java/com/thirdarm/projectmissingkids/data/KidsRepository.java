package com.thirdarm.projectmissingkids.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.local.LocalDataSource;
import com.thirdarm.projectmissingkids.data.local.MissingKidDao;
import com.thirdarm.projectmissingkids.data.local.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.data.model.MissingKidSearchResult;
import com.thirdarm.projectmissingkids.data.remote.RemoteSync;
import com.thirdarm.projectmissingkids.util.DataParsingUtils;
import com.thirdarm.projectmissingkids.util.NetworkState;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Interface to all database sources (local and remote)
 * Created by trod 11/3/2018
 */
public class KidsRepository {

    private static final String TAG = KidsRepository.class.getSimpleName();
    private static final int PAGE_SIZE = 20;

    private MissingKidDao mDao;
    private Context mContext;
    private ExecutorService mExecutor;
    private RemoteSync mRemoteSyncHelper;
    private LocalDataSource mLocalDataSource;

    /**
     * Callback for passing in database results where LiveData can't be used.
     * To be used with {@link RunnableCallback}
     *
     * @param <T>
     */
    public interface DatabaseCallback<T> {
        void onResponse(T response);
    }

    /**
     * Runnable that takes a callback for passing in results. See {@link DatabaseCallback}
     *
     * @param <T>
     */
    private class RunnableCallback<T> implements Runnable {
        DatabaseCallback<T> callback;

        RunnableCallback(DatabaseCallback<T> callback) {
            this.callback = callback;
        }

        public void run() {
        }
    }

    public KidsRepository(Application application, ExecutorService executor) {
        mDao = getDao(application);
        mContext = application;
        mExecutor = executor;
        mRemoteSyncHelper = new RemoteSync(mExecutor);
        mLocalDataSource = new LocalDataSource(mDao, mExecutor);
    }

    private MissingKidDao getDao(Application application) {
        MissingKidsDatabase db = MissingKidsDatabase.getInstance(application);
        return db.missingKidDao();
    }

    //
    // region Local database CRUD
    //

    // region query

    /**
     * Gets a list of all kids in the database.
     * <p>
     * Needs to be called from a background thread
     *
     * @return
     */
    public List<MissingKid> getAllKidsAsync() {
        return mDao.getAllKidsAsync();
    }

    /**
     * Get a PagedList of all the kids in the local database. Makes no network calls, so this
     * request is primarily local
     *
     * @return
     */
    public LiveData<PagedList<MissingKid>> getAllKidsFromLocal() {
        return new LivePagedListBuilder<>(mDao.getAllKids(), PAGE_SIZE).build();
    }

    /**
     * Generates a {@link PagedList} that is wrapped inside a {@link MissingKidSearchResult},
     * containing a consolidated list of both locally and remotely sourced kids, as well as
     * the latest {@link NetworkState} for updating the UI.
     * <p>
     * Asynchronous network calls are facilitated via {@link KidsBoundaryCallback}, which is set
     * as the PagedList's {@link android.arch.paging.PagedList.BoundaryCallback}
     * <p>
     * TODO: Input a query to customize search results. See
     * {@link com.thirdarm.projectmissingkids.util.NetworkUtils#buildJsonDataBeginSearchUrl} for
     * current network lookup implementation
     *
     * @param query
     * @return
     */
    public MissingKidSearchResult getKidsFromSearch(String query) {
        // Get data source from local cache
        DataSource.Factory<Integer, MissingKid> dataSourceFactory = mLocalDataSource.getAllKids();

        // Get the boundary callback for refreshing data if needed, from the network
        KidsBoundaryCallback boundaryCallback = new KidsBoundaryCallback(mRemoteSyncHelper,
                mLocalDataSource, mContext);
        LiveData<NetworkState> networkState = boundaryCallback.getNetworkState();

        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setPageSize(PAGE_SIZE) // determines how many pages to get, based on # of items desired
                .build();

        // Build a LiveData<PagedList> using LivePagedListBuilder, passing in the data source as
        // well as the paging size for the database
        LiveData<PagedList<MissingKid>> data = (
                new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig))
                .setBoundaryCallback(boundaryCallback)
                .setFetchExecutor(mExecutor)
                .build();

        return new MissingKidSearchResult(data, networkState);
    }

    /**
     * Returns the kid with the matching orgPrefixCaseNum. If there are missing Details, then this
     * downloads the details asynchronously. When details are finished downloading, the LiveData's
     * observer will be notified of the change automatically without any additional work needed
     * on our part
     *
     * @return
     */
    public LiveData<MissingKid> getKidByOrgPrefixCaseNum(String orgPrefixCaseNum) {
        mExecutor.execute(new GetKidByOrgPrefixCaseNumRunnable(orgPrefixCaseNum, new DatabaseCallback<MissingKid>() {
            @Override
            public void onResponse(MissingKid response) {
                fetchAndStoreSingleDetail(response); // async
            }
        }));
        return mDao.getKidByOrgPrefixCaseNum(orgPrefixCaseNum);
    }

    /**
     * Gets a kid locally for processing afterwards
     */
    private class GetKidByOrgPrefixCaseNumRunnable extends RunnableCallback<MissingKid> {
        private String orgPrefixCaseNum;

        GetKidByOrgPrefixCaseNumRunnable(String orgPrefixCaseNum, DatabaseCallback<MissingKid> callback) {
            super(callback);
            this.orgPrefixCaseNum = orgPrefixCaseNum;
        }

        @Override
        public void run() {
            callback.onResponse(getKidByOrgPrefixCaseNumAsync(orgPrefixCaseNum));
        }
    }

    /**
     * Returns the kid with the matching orgPrefixCaseNum
     * <p>
     * Needs to be called from a background thread
     *
     * @param orgPrefixCaseNum
     * @return
     */
    public MissingKid getKidByOrgPrefixCaseNumAsync(String orgPrefixCaseNum) {
        return mDao.getKidByOrgPrefixCaseNumAsync(orgPrefixCaseNum);
    }

    /**
     * Returns a list of kids with the provided name, matching first or last names
     *
     * @param name
     * @return
     */
    public LiveData<List<MissingKid>> getAllKidsByName(String name) {
        return mDao.getAllKidsByName(name);
    }

    // endregion

    // region insert

    public void insertKid(MissingKid kid) {
        new InsertAsyncTask(mDao).execute(new MissingKid[]{kid});
    }

    public void insertKids(MissingKid... kids) {
        new InsertAsyncTask(mDao).execute(kids);
    }

    /**
     * Inserts kids into the database
     */
    private static class InsertAsyncTask extends AsyncTask<MissingKid[], Void, Long[]> {
        MissingKidDao dao;

        InsertAsyncTask(MissingKidDao dao) {
            this.dao = dao;
        }

        @Override
        protected Long[] doInBackground(MissingKid[]... kids) {
            return dao.insert(kids[0]);
        }

        @Override
        protected void onPostExecute(Long[] ids) {
            if (ids != null) {
                Log.d("InsertAsyncTask", "Inserted " + ids.length + " kids");
            } else {
                Log.w("InsertKidsTask", "No kids inserted");
            }
        }
    }

    // endregion

    // region update

    public void updateKid(MissingKid kid) {
        new UpdateAsyncTask(mDao).execute(new MissingKid[]{kid});
    }

    public void updateKids(MissingKid... kids) {
        new UpdateAsyncTask(mDao).execute(kids);
    }

    /**
     * Updates kids in the database
     */
    private static class UpdateAsyncTask extends AsyncTask<MissingKid[], Void, Integer> {
        MissingKidDao dao;

        UpdateAsyncTask(MissingKidDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(MissingKid[]... missingKids) {
            return dao.update(missingKids[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("UpdateAsyncTask", "Updated " + integer + " kids");
        }
    }

    // endregion

    // region delete

    public void deleteKid(MissingKid kid) {
        new DeleteAsyncTask(mDao).execute(new MissingKid[]{kid});
    }

    public void deleteKids(MissingKid... kids) {
        new DeleteAsyncTask(mDao).execute(kids);
    }

    public void deleteAll() {
        new DeleteAllAsyncTask(mDao).execute();
    }

    /**
     * Deletes kids from the database
     */
    private static class DeleteAsyncTask extends AsyncTask<MissingKid[], Void, Integer> {
        MissingKidDao dao;

        DeleteAsyncTask(MissingKidDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(MissingKid[]... missingKids) {
            return dao.delete(missingKids[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("DeleteAsyncTask", "Deleted " + integer + " kids");
        }
    }

    /**
     * Deletes all kids from the database
     */
    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Integer> {
        MissingKidDao dao;

        DeleteAllAsyncTask(MissingKidDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return dao.deleteAll();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("DeleteAllAsyncTask", "Deleted " + integer + " kids");
        }
    }

    // endregion

    //
    // endregion
    //

    //
    // region Network fetch and local store
    //

    /**
     * Syncs the user's local database with the online database by iterating through each of the
     * result pages asynchronously, and stores the kids immediately upon each page's return
     */
    private void fetchAndStoreAllKidsOnlineParallel() {
        mRemoteSyncHelper.fetchNumPages(new RemoteSync.RemoteSyncCallback<Integer>() {
            @Override
            public void onDataReceived(Integer numPages) {
                if (numPages != RemoteSync.NO_DATA_RETURNED) {
                    for (int i = 1; i <= numPages; i++) {
                        fetchAndStoreSinglePage(i);
                    }
                }
            }

            @Override
            public void onNoDataReturned(String message) {
                Log.d(TAG + "/fetchAllKidsParallel", message);
            }

            @Override
            public void onFailure(Exception e, String message) {
                Log.w(TAG + "/fetchAllKidsParallel", "Error getting numPages. Not doing anything:\n" + e);
            }
        });
    }

    /**
     * Fetches and stores a single page of online data locally, asynchronously
     *
     * @param pageNum
     */
    private void fetchAndStoreSinglePage(int pageNum) {
        mRemoteSyncHelper.fetchSinglePage(pageNum,
                new RemoteSync.RemoteSyncCallback<RemoteSync.PageResponse<MissingKid>>() {
                    @Override
                    public void onDataReceived(@Nullable RemoteSync.PageResponse<MissingKid> response) {
                        if (response != null) {
                            storeFetchedKids(response.results);
                        } else {
                            Log.w(TAG + "/fetchSinglePage", "No data returned to store");
                        }
                    }

                    @Override
                    public void onNoDataReturned(String message) {
                        Log.d(TAG + "/fetchSinglePage", message);
                    }

                    @Override
                    public void onFailure(Exception e, String message) {
                        Log.w(TAG + "/fetchSinglePage", "Error getting kids. Not doing anything:\n" + e);
                    }
                });
    }

    /**
     * Stores kids asynchronously
     *
     * @param kids
     */
    private void storeFetchedKids(@NonNull List<MissingKid> kids) {
        mExecutor.execute(new StoreKidsRunnable(kids));
    }

    /**
     * Stores kids in the background. See {@link KidsRepository#storeKidsLocally(List)}
     */
    private class StoreKidsRunnable implements Runnable {
        List<MissingKid> kids;

        StoreKidsRunnable(@NonNull List<MissingKid> kids) {
            this.kids = kids;
        }

        @Override
        public void run() {
            storeKidsLocally(kids);
        }
    }

    /**
     * Populates the database with the list of MissingKids, via an "Upsert" implementation
     * <p>
     * Needs to be called on a background thread
     *
     * @param missingKids The list of MissingKids data
     */
    private void storeKidsLocally(@NonNull List<MissingKid> missingKids) {
        List<MissingKid> inserts = new ArrayList<>();
        List<MissingKid> updates = new ArrayList<>();
        for (MissingKid kid : missingKids) {
            // check whether kid already exists in db
            MissingKid kidFromDb = mDao.getKidByOrgPrefixCaseNumAsync(kid.orgPrefixCaseNumber);
            if (kidFromDb != null) {
                // update kid only if it already exists in db
                kid.uid = kidFromDb.uid;
                updates.add(kid);
            } else {
                // otherwise, add the kid
                inserts.add(kid);
            }
        }
        mDao.insert(inserts.toArray(new MissingKid[]{}));
        Log.d(TAG + "/UpsertAsyncTask", "Inserted: " + inserts.size());
        mDao.update(updates.toArray(new MissingKid[]{}));
        Log.d(TAG + "/UpsertAsyncTask", "Updated: " + updates.size());
    }

    /**
     * Fetches and stores detail data for a single kid asynchronously. If kid already has detail
     * data, then this does nothing, saving a network call
     */
    private void fetchAndStoreSingleDetail(final MissingKid partialKidData) {
        mRemoteSyncHelper.fetchSingleDetail(partialKidData, new RemoteSync.RemoteSyncCallback<JSONObject>() {
            @Override
            public void onDataReceived(JSONObject response) {
                MissingKid fullKid = DataParsingUtils.parseDetailDataForMissingKid(response, partialKidData);
                storeFetchedKids(Collections.singletonList(fullKid));
            }

            @Override
            public void onNoDataReturned(String message) {
                Log.d(TAG + "/fetchSingleDetail", message);
            }

            @Override
            public void onFailure(Exception e, @Nullable String message) {
                Log.w(TAG + "/fetchSingleDetail", "Error getting detail. Not doing anything.\n" + e);
            }
        });
    }

    //
    // endregion
    //
}
