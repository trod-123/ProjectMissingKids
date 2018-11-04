package com.thirdarm.projectmissingkids.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.local.MissingKidDao;
import com.thirdarm.projectmissingkids.data.local.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.data.remote.RemoteSync;
import com.thirdarm.projectmissingkids.util.DataParsingUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Interface to all database sources (local and remote)
 * Created by trod 11/3/2018
 */
public class KidsRepository {

    private static final String TAG = KidsRepository.class.getSimpleName();

    private static final int DEFAULT_PAGE_SIZE = 20;

    private MissingKidDao mDao;
    private Context mContext;
    private ExecutorService mExecutor;

    public KidsRepository(Application application, ExecutorService executor) {
        mDao = getDao(application);
        mContext = application;
        mExecutor = executor;
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
     * Get a list of all the kids in the database.
     *
     * @return
     */
    public LiveData<List<MissingKid>> getAllKids() {
        LiveData<List<MissingKid>> kidsInDb = mDao.getAllKids();
        if (kidsInDb.getValue() == null) {
            // Database hasn't been initialized. Sync now
            fetchAndStoreAllKidsOnlineParallel();
        }
        return kidsInDb;
    }

    /**
     * Returns the kid with the matching orgPrefixCaseNum
     *
     * @param orgPrefix
     * @param caseNum
     * @return
     */
    public LiveData<MissingKid> getKidByOrgPrefixCaseNum(String orgPrefix, String caseNum) {
        String orgPrefixCaseNum = orgPrefix + caseNum;
        return mDao.getKidByOrgPrefixCaseNum(orgPrefixCaseNum);
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

    /**
     * Helper that grabs a list of all the orgPrefixCaseNums from a provided list of kids
     *
     * @param kids
     * @return
     */
    public List<String> getAllOrgPrefixCaseNums(List<MissingKid> kids) {
        List<String> result = new ArrayList<>();
        for (MissingKid kid : kids) {
            result.add(kid.orgPrefixCaseNumber);
        }
        return result;
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
     * result pages asynchronously, and stores the kids once ALL of the kids are retrieved
     */
    public void fetchAndStoreAllKidsOnlineSerially() {
        Future<List<MissingKid>> response = mExecutor.submit(
                new FetchKidsCallable(FetchKidsCallable.ALL_PAGES));
        storeFetchedOnlineKids(response);
    }

    /**
     * Syncs the user's local database with the online database by iterating through each of the
     * result pages asynchronously, and stores the kids immediately upon each page's return
     */
    private void fetchAndStoreAllKidsOnlineParallel() {
        Future<Integer> response = mExecutor.submit(new FetchNumPagesCallable());
        try {
            int numPages = response.get();
            if (numPages != RemoteSync.NO_DATA_RETURNED) {
                for (int i = 1; i <= numPages; i++) {
                    fetchAndStoreSinglePageOnline(i);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.w(TAG, "Error getting numPages from future task. Not doing anything: " + e);
        }
    }

    /**
     * Fetches and stores a single page of online data locally, asynchronously
     *
     * @param pageNum
     */
    private void fetchAndStoreSinglePageOnline(int pageNum) {
        Future<List<MissingKid>> response = mExecutor.submit(new FetchKidsCallable(pageNum));
        storeFetchedOnlineKids(response);
    }

    /**
     * Stores Future kid lists
     *
     * @param callableResponse
     */
    private void storeFetchedOnlineKids(Future<List<MissingKid>> callableResponse) {
        mExecutor.execute(new StoreFetchedKidsRunnable(callableResponse));
    }

    private class StoreFetchedKidsRunnable implements Runnable {
        Future<List<MissingKid>> callableResponse;

        StoreFetchedKidsRunnable(Future<List<MissingKid>> callableResponse) {
            this.callableResponse = callableResponse;
        }

        @Override
        public void run() {
            try {
                @Nullable List<MissingKid> result = callableResponse.get();
                if (result != null) {
                    storeKidsInLocalDb(result);
                } else {
                    Log.e(TAG + "/storeFetchedOnlineKids", "No data returned to store");
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.w(TAG + "/storeFetchedOnlineKids", "Error getting kids from future task. Not doing anything: " + e);
            }
        }
    }

    /**
     * Gets the number of pages in the query
     */
    private class FetchNumPagesCallable implements Callable<Integer> {
        public Integer call() {
            try {
                return RemoteSync.getNumPages();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error getting search results");
            }
            return RemoteSync.NO_DATA_RETURNED;
        }
    }

    /**
     * Fetches kids online
     */
    private class FetchKidsCallable implements Callable<List<MissingKid>> {
        private int pageNum;
        private static final int ALL_PAGES = -1;

        FetchKidsCallable(int pageNum) {
            this.pageNum = pageNum;
        }

        @Override
        @Nullable
        public List<MissingKid> call() {
            List<MissingKid> kids;
            if (pageNum != ALL_PAGES) {
                kids = RemoteSync.fetchSinglePageOfKids(pageNum);
            } else {
                kids = RemoteSync.fetchAllPagesOfKids();
            }
            return kids;
        }
    }

    /**
     * Populates the database with the list of MissingKids
     *
     * @param missingKids The list of MissingKids data
     */
    private void storeKidsInLocalDb(@NonNull List<MissingKid> missingKids) {
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
     * Inserts locally if doesn't exist; updates if exists
     */
    private static class UpsertKidsTask extends AsyncTask<MissingKid[], Void, Void> {
        private MissingKidDao dao;

        UpsertKidsTask(MissingKidDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(MissingKid[]... missingKids) {
            List<MissingKid> inserts = new ArrayList<>();
            List<MissingKid> updates = new ArrayList<>();
            for (MissingKid kid : missingKids[0]) {
                // check whether kid already exists in db
                MissingKid kidFromDb = dao.getKidByOrgPrefixCaseNumAsync(kid.orgPrefixCaseNumber);
                if (kidFromDb != null) {
                    // update kid only if it already exists in db
                    kid.uid = kidFromDb.uid;
                    updates.add(kid);
                } else {
                    // otherwise, add the kid
                    inserts.add(kid);
                }
            }
            dao.insert(inserts.toArray(new MissingKid[]{}));
            Log.d(TAG + "/UpsertAsyncTask", "Inserted: " + inserts.size());
            dao.update(updates.toArray(new MissingKid[]{}));
            Log.d(TAG + "/UpsertAsyncTask", "Updated: " + updates.size());
            return null;
        }
    }

    /**
     * Fetches and stores detail data for a single kid
     */
    private void fetchAndStoreSingleDetailOnline(final String caseNum, final String orgPrefix) {
        mExecutor.execute(new FetchDetailRunnable(this, caseNum, orgPrefix));
    }

    /**
     * Fetches and consolidates detail kid info online into the database
     */
    private class FetchDetailRunnable implements Runnable {
        private KidsRepository repo;
        private String caseNum;
        private String orgPrefix;

        FetchDetailRunnable(KidsRepository repo, String caseNum, String orgPrefix) {
            this.repo = repo;
            this.caseNum = caseNum;
            this.orgPrefix = orgPrefix;
        }

        @Override
        public void run() {
            MissingKid completeKid = repo.getKidWithDetail(caseNum, orgPrefix);
            if (completeKid == null) {
                Log.e(TAG, "There was no data returned from the server.");
                return;
            }
            List<MissingKid> kids = new ArrayList<>(Arrays.asList(completeKid));
            repo.storeKidsInLocalDb(kids);
        }
    }

    /**
     * Helper for appending detail data into partial kid data that already exists
     * <p>
     * If the detail data is null, then this returns the original missing kid with the passed
     * {@code caseNumber} and {@code orgPrefix}
     * <p>
     * Needs to be called from a background thread since we're doing data ops
     *
     * @param caseNumber
     * @param orgPrefix
     * @return
     */
    private MissingKid getKidWithDetail(String caseNumber, String orgPrefix) {
        @NonNull MissingKid partialKidData = getKidByOrgPrefixCaseNumAsync(orgPrefix + caseNumber);
        @Nullable JSONObject detailChildJson = RemoteSync.fetchDetailData(caseNumber, orgPrefix);
        return DataParsingUtils.parseDetailDataForMissingKid(detailChildJson, partialKidData);
    }

    //
    // endregion
    //
}
