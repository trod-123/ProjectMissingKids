package com.thirdarm.projectmissingkids.data.remote;

import android.support.annotation.Nullable;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.util.DataParsingUtils;
import com.thirdarm.projectmissingkids.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Responsible for fetching remote data
 * Created by trod 11/3/2018
 */
public class RemoteSync {

    private static final String TAG = RemoteSync.class.getSimpleName();
    public static final int NO_DATA_RETURNED = -1;

    private ExecutorService mExecutor;

    public RemoteSync(ExecutorService executor) {
        mExecutor = executor;
    }

    /**
     * Callback for fetching data remotely. To be used with {@link RunnableCallback}
     *
     * @param <T>
     */
    public interface RemoteSyncCallback<T> {
        /**
         * To be called when usable data is returned from a server
         *
         * @param response
         */
        void onDataReceived(T response);

        /**
         * To be called when there is no data returned, which could be due to no network connection,
         * no server response, or just simply nothing to be returned
         *
         * @param message
         */
        void onNoDataReturned(String message);

        /**
         * To be called when there is a general failure during or before getting a server response
         *
         * @param e
         * @param message
         */
        void onFailure(Exception e, @Nullable String message);
    }

    /**
     * Runnable that takes a callback for passing in fetched data
     *
     * @param <T>
     */
    private class RunnableCallback<T> implements Runnable {
        RemoteSyncCallback<T> callback;

        RunnableCallback(RemoteSyncCallback<T> callback) {
            this.callback = callback;
        }

        public void run() {
        }
    }

    /**
     * Response object for fetching data remotely, consisting of a list of results and
     * the total number of pages available in the response
     *
     * @param <T>
     */
    public class PageResponse<T> {
        public List<T> results;
        public int numPages;

        PageResponse(List<T> results, int numPages) {
            this.results = results;
            this.numPages = numPages;
        }
    }

    /**
     * Asynchronously fetches a single page of online data
     *
     * @param pageNum
     */
    public void fetchSinglePage(int pageNum, RemoteSyncCallback<PageResponse<MissingKid>> callback) {
        mExecutor.submit(new FetchKidsRunnable(pageNum, callback));
    }

    /**
     * Asynchronously fetches the num of total pages of search results. May return
     * {@link RemoteSync#NO_DATA_RETURNED} if there is no response
     *
     * @param callback
     */
    public void fetchNumPages(RemoteSyncCallback<Integer> callback) {
        mExecutor.submit(new FetchNumPagesRunnable(callback));
    }

    /**
     * Gets the number of pages in the query
     *
     * TODO: We should be able to get the number of pages from the individual page search results too
     */
    private class FetchNumPagesRunnable extends RunnableCallback<Integer> {
        FetchNumPagesRunnable(RemoteSyncCallback<Integer> callback) {
            super(callback);
        }

        @Override
        public void run() {
            try {
                int numPages = RemoteSync.getNumPages();
                if (numPages != NO_DATA_RETURNED) {
                    callback.onDataReceived(numPages);
                } else {
                    callback.onNoDataReturned("No data returned from server");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onFailure(e, "Error getting numPages");
            }
        }
    }

    /**
     * Fetches kids online
     */
    private class FetchKidsRunnable extends RunnableCallback<PageResponse<MissingKid>> {
        private int pageNum;
        private static final int ALL_PAGES = -1;

        FetchKidsRunnable(int pageNum, RemoteSyncCallback<PageResponse<MissingKid>> callback) {
            super(callback);
            this.pageNum = pageNum;
        }

        @Override
        public void run() {
            try {
                int numPages = getNumPages();
                List<MissingKid> kids = null;
                if (pageNum != ALL_PAGES) {
                    if (pageNum <= numPages) {
                        kids = RemoteSync.fetchSinglePageOfKids(pageNum);
                    }
                } else {
                    kids = RemoteSync.fetchAllPagesOfKids();
                }
                if (kids != null) {
                    callback.onDataReceived(new PageResponse<>(kids, numPages));
                } else {
                    callback.onNoDataReturned("No data returned from server");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onFailure(e, "Error getting search results");
            }
        }
    }

    /**
     * See {@link FetchAppendedDetailRunnable}
     */
    public void fetchSingleDetail(MissingKid partialKidData,
                                  RemoteSyncCallback<JSONObject> callback) {
        mExecutor.execute(new FetchAppendedDetailRunnable(partialKidData, callback));
    }

    /**
     * Fetches detail kid info online. May return a null MissingKid in the event the kid does not
     * exist in the database, or if the kid already has details
     */
    private class FetchAppendedDetailRunnable extends RunnableCallback<JSONObject> {
        private MissingKid partialKidData;

        FetchAppendedDetailRunnable(@Nullable MissingKid partialKidData,
                                    RemoteSyncCallback<JSONObject> callback) {
            super(callback);
            this.partialKidData = partialKidData;
        }

        @Override
        public void run() {
            if (partialKidData != null) {
                if (partialKidData.description == null || partialKidData.description.trim().isEmpty()) {
                    callback.onDataReceived(fetchDetailData(partialKidData.caseNum, partialKidData.orgPrefix));
                } else {
                    callback.onNoDataReturned("Kid already has detail. Did not fetch");
                }
            } else {
                callback.onFailure(new Exception("Partial kid data was null"), null);
            }
        }
    }


    /**
     * Fetch search results from a single page and convert into a list MissingKid objects
     * <p>
     * Needs to be called on a background thread since we're downloading from a network
     * <p>
     * Logs an error and returns null if there is no data returned from the server, or if there was
     * something wrong with fetching the results
     *
     * @param pageNum
     */
    @Nullable
    synchronized private static List<MissingKid> fetchSinglePageOfKids(int pageNum)
            throws JSONException {
        JSONArray searchResults = NetworkUtils.getSearchResultPageJsonArray(pageNum);
        if (searchResults != null) {
            return DataParsingUtils.getMissingKidListFromJsonArray(searchResults);
        } else {
            Log.e(TAG + "/fetchSinglePage", "There was no data returned from the server.");
        }
        return null;
    }

    /**
     * Gets the number of pages. Returns {@link RemoteSync#NO_DATA_RETURNED} if there is no data
     * returned from the server
     *
     * TODO: We can get this easily from the same search result page, instead of having to call
     * the first url over and over again
     *
     * @return
     * @throws JSONException
     */
    synchronized private static int getNumPages() throws JSONException {
        JSONObject searchResults = NetworkUtils.getSearchResultsMetadataJson();
        if (searchResults != null) {
            return NetworkUtils.getTotalPagesFromMetadata(searchResults);
        } else {
            Log.e(TAG + "/getNumPages", "There was no data returned from the server.");
            return NO_DATA_RETURNED;
        }
    }

    /**
     * Fetches results from all pages, one at a time, and returns the list of all kids from
     * the search. Consider this a single-task operation, in which the return occurs after ALL
     * pages have been collected.
     * <p>
     * Needs to be called on a background thread since we're downloading from a network
     * <p>
     * Logs an error if there is no data returned from the server, or if there was something wrong
     * with fetching the results
     */
    @Nullable
    synchronized private static List<MissingKid> fetchAllPagesOfKids() throws JSONException {
        List<MissingKid> results = new ArrayList<>();
        // get the number of pages to iterate through
        int numPages = getNumPages();
        if (numPages != NO_DATA_RETURNED) {
            for (int i = 1; i <= numPages; i++) {
                results.addAll(fetchSinglePageOfKids(i));
            }
            return results;
        } else {
            Log.e(TAG + "/fetchAllPages", "There was no data returned from the server.");
            return null;
        }
    }

    /**
     * Fetches the JSONObject detail data for a single kid. This will need to be merged with
     * a MissingKid object fetched from the local data source
     * <p>
     * Needs to be called on a background thread since we're downloading from a network
     * <p>
     * Logs an error if there is no data returned from the server, or if there was something wrong
     * with fetching the results
     */
    @Nullable
    synchronized private static JSONObject fetchDetailData(final String caseNum, final String orgPrefix) {
        try {
            return NetworkUtils.getDetailDataJson(caseNum, orgPrefix);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG + "/fetchDetailData", "There was a problem grabbing the detail data from the provided caseNumber ("
                    + caseNum + ") and orgPrefix (" + orgPrefix + ")");
        }
        return null;
    }
}
