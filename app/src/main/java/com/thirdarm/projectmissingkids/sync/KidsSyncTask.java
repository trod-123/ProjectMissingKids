package com.thirdarm.projectmissingkids.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.ChildData;
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
public class KidsSyncTask {

    /**
     * Tag for debugging
     */
    private static final String TAG = KidsSyncTask.class.getSimpleName();

    private static MissingKidsDatabase mDb;

    public KidsSyncTask(Context context) {
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
                    // iterate through every page via an asynctask to fetch all pages and store into local db quickly and asynchronously
                    // TODO: This loads up kids in an arbitrary order. Consider loading a sorted list
                    AsyncTask<Integer, Void, Void> sync = new AsyncTask<Integer, Void, Void>() {
                        @Override
                        protected Void doInBackground(Integer... integers) {
                            syncFromIndividualPage(integers[0]);
                            return null;
                        }
                    };
                    sync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
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
     * search results into ChildObjects and MissingKid objects
     * <p>
     * Logs an error if there is no data returned from the server, or if there was something wrong
     * with fetching the results
     *
     * @param pageNum
     */
    synchronized private void syncFromIndividualPage(int pageNum) {
        JSONArray searchResults;
        try {
            searchResults = NetworkUtils.getSearchResultPageJsonArray(pageNum);
            if (searchResults != null) {
                // then convert the JSONArray into ChildObjects
                List<ChildData> childData = convertJSONArrayToChildData(searchResults);

                // convert the ChildObjects into MissingKids
                List<MissingKid> missingKids = convertChildDataToMissingKid(childData);
                // populate the database with the MissingKids
                populateAsync(missingKids);
            } else {
                Log.e(TAG, "There was no data returned from the server.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "There was an error getting the search results");
        }
    }

    /**
     * Converts the JSONArray data into a list of ChildData
     *
     * @param jsonData The json data
     * @return The list of ChildData
     */
    private List<ChildData> convertJSONArrayToChildData(JSONArray jsonData) {
        return DataParsingUtils.getChildDataListFromJsonArray(jsonData);
    }

    /**
     * Converts a list of ChildData into a list of MissingKid data
     * TODO: Currently this replaces all detail data with null
     *
     * @param childData The list of ChildData
     * @return The list of MissingKid data
     */
    private List<MissingKid> convertChildDataToMissingKid(List<ChildData> childData) {
        List<MissingKid> kids = new ArrayList<>(childData.size());
        for (ChildData data : childData) {
            MissingKid kid = MissingKid.convertFromPartialChildData(data);
            kids.add(kid);
        }
        return kids;
    }

    /**
     * Populates the database with the list of MissingKids
     *
     * @param missingKids The list of MissingKids data
     */
    private void populateAsync(List<MissingKid> missingKids) {
        MissingKidDao dao = mDb.missingKidDao();

        // for debugging
        int numUpdated = 0;
        int numInserted = 0;
        List<String> orgPrefixCaseNums = new ArrayList<>();

        for (MissingKid kid : missingKids) {
            // for checking whether kid already exists in db
            MissingKid kidFromDb = dao.findKidByOrgPrefixCaseNum(kid.orgPrefixCaseNumber);

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
                numUpdated += dao.updateSingleKid(kid);
            } else {
                // otherwise, add the kid
                dao.insertSingleKid(kid);
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
            boolean success = false;

            @Override
            protected Void doInBackground(Void... voids) {
                MissingKid completeKid = appendDetailDataWithPartialKid(caseNum, orgPrefix);
                if (completeKid != null) {
                    success = true;
                } else {
                    Log.e(TAG, "There was no data returned from the server.");
                    return null;
                }
                List<MissingKid> kids = new ArrayList<>(Arrays.asList(completeKid));
                populateAsync(kids);
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
        MissingKid partialKidData = mDb.missingKidDao().findKidByOrgPrefixCaseNum(orgPrefix + caseNumber);
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

        ChildData detailChildData = DataParsingUtils.parseDetailDataForChild(detailChildJson, new ChildData());
        MissingKid completeKidData = MissingKid.appendDetailChildDataWithPartialKidData(partialKidData, detailChildData);
        return completeKidData;
    }
}
