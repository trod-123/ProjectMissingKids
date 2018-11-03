package com.thirdarm.projectmissingkids.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidDao;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.util.DataParsingUtils;
import com.thirdarm.projectmissingkids.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fetch Missing Kids data from the NCMEC database, and store in user's local database
 */
public class KidsSyncTasks {

    /**
     * Tag for debugging
     */
    private static final String TAG = KidsSyncTasks.class.getSimpleName();

    private static MissingKidsDatabase mDb;

    public KidsSyncTasks(Context context) {
        mDb = MissingKidsDatabase.getInstance(context);
    }

    /**
     * Syncs the user's local database with the online database by iterating through each of the
     * result pages asynchronously.
     * <p>
     * Logs an error if there is no data returned from the server, or if there was something wrong
     * with fetching the results
     */
    synchronized public void syncKidsFromOnline() {
        JSONObject searchResults;
        try {
            // get the number of pages
            searchResults = NetworkUtils.getSearchResultsMetadataJson();
            if (searchResults != null) {
                int numPages = NetworkUtils.getTotalPagesFromMetadata(searchResults);
                for (int i = 1; i <= numPages; i++) {
                    // iterate through every page via an asynctask to fetch all pages one at a time
                    AsyncTask<Integer, Void, Void> sync = new AsyncTask<Integer, Void, Void>() {
                        @Override
                        protected Void doInBackground(Integer... integers) {
                            List<MissingKid> missingKids = syncFromIndividualPage(integers[0]);
                            if (missingKids != null) storeKidsInLocalDb(missingKids);
                            return null;
                        }
                    };
                    sync.execute(i);
                }
            } else {
                Log.e(TAG, "There was no data returned from the server.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "There was an error getting the search results");
        }
    }

    /**
     * Helper method to sync and store data from individual Json response pages. First converts the
     * search results into MissingKid objects
     * <p>
     * Logs an error if there is no data returned from the server, or if there was something wrong
     * with fetching the results
     *
     * @param pageNum
     */
    synchronized private List<MissingKid> syncFromIndividualPage(int pageNum) {
        JSONArray searchResults;
        try {
            searchResults = NetworkUtils.getSearchResultPageJsonArray(pageNum);
            if (searchResults != null) {
                return DataParsingUtils.getMissingKidListFromJsonArray(searchResults);
            } else {
                Log.e(TAG, "There was no data returned from the server.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "There was an error getting the search results");
        }
        return null;
    }

    /**
     * Populates the database with the list of MissingKids
     *
     * @param missingKids The list of MissingKids data
     */
    private void storeKidsInLocalDb(List<MissingKid> missingKids) {
        MissingKidDao dao = mDb.missingKidDao();

        // for debugging
        int numUpdated = 0;
        int numInserted = 0;
        List<String> orgPrefixCaseNums = new ArrayList<>();

        for (MissingKid kid : missingKids) {
            // for checking whether kid already exists in db
            MissingKid kidFromDb = dao.getKidByOrgPrefixCaseNum(kid.orgPrefixCaseNumber);

            // for debugging: checks for duplicate ids
            boolean check = orgPrefixCaseNums.contains(kid.orgPrefixCaseNumber);
            if (check) {
                Log.w(TAG, "Duplicate ID found!");
            } else {
                orgPrefixCaseNums.add(kid.orgPrefixCaseNumber);
            }

            if (kidFromDb != null) {
                // update kid only if it already exists in db
                kid.uid = kidFromDb.uid;
                numUpdated += dao.update(kid);
            } else {
                // otherwise, add the kid
                dao.insert(kid);
                numInserted++;
            }
        }
        Log.d(TAG, "Total number updated: " + numUpdated);
        Log.d(TAG, "Total number inserted: " + numInserted);
    }

    /**
     * Fetches detail data for a single kid, then loads it into the user's local database.
     * <p>
     * Logs an error if there is no data returned from the server, or if there was something wrong
     * with fetching the results
     */
    synchronized public void syncDetailDataFromOnline(final String caseNum, final String orgPrefix) {
        AsyncTask<Void, Void, Void> sync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                MissingKid completeKid = appendDetailDataWithPartialKid(caseNum, orgPrefix);
                if (completeKid == null) {
                    Log.e(TAG, "There was no data returned from the server.");
                    return null;
                }
                List<MissingKid> kids = new ArrayList<>(Arrays.asList(completeKid));
                storeKidsInLocalDb(kids);
                return null;
            }
        };
        sync.execute();
    }

    /**
     * Helper method for appending detail data into partial kid data that already exists
     *
     * @param caseNumber
     * @param orgPrefix
     * @return
     */
    private static MissingKid appendDetailDataWithPartialKid(String caseNumber, String orgPrefix) {
        MissingKid partialKidData = mDb.missingKidDao().getKidByOrgPrefixCaseNum(orgPrefix + caseNumber);
        JSONObject detailChildJson = null;
        try {
            detailChildJson = NetworkUtils.getDetailDataJson(caseNumber, orgPrefix);
            if (detailChildJson == null) {
                // return null if there is no detailed data available. handle nulls accordingly
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "There was a problem grabbing the detail data from the provided caseNumber ("
                    + caseNumber + ") and orgPrefix (" + orgPrefix + ")");
        }

        return DataParsingUtils.parseDetailDataForMissingKid(detailChildJson, partialKidData);
        }
}
