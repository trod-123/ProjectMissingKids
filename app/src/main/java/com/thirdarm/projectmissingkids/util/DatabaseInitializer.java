package com.thirdarm.projectmissingkids.util;

import android.os.AsyncTask;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.ChildData;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidDao;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * [DEPRECATED IN FAVOR OF {@link com.thirdarm.projectmissingkids.sync.KidsSyncTask}]
 * Initializes the database and loads it with online content
 */
public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getSimpleName();

    private static MissingKidsDatabase mDb;

    /**
     * Initialize the database with fresh online data
     * <p>
     * Currently removes all the data from the database every time this is run
     * </p>
     *
     * @param db
     */
    public static void initializeDbWithOnlineData(MissingKidsDatabase db) {
        mDb = db;

        // TODO: For testing only! Remove when done
        //RemoveAllDataFromDatabase removeTask = new RemoveAllDataFromDatabase();
        //removeTask.execute();

        String previousHeadCaseNum = "";

        for (int i = 1; i < 70; i++) {
            FetchDataFromServerAndLoadIntoDb task = new FetchDataFromServerAndLoadIntoDb(previousHeadCaseNum);
            task.execute(i);
        }
    }

    /**
     * Load detail data into the partial kid data, provided by the kid's case number and the org prefix
     *
     * @param db
     * @param caseNumber
     * @param orgPrefix
     */
    public static void loadDetailDataIntoPartialKidData(MissingKidsDatabase db,
                                                        String caseNumber, String orgPrefix) {
        mDb = db;

        FetchDetailDataFromServerAndLoadIntoDb task =
                new FetchDetailDataFromServerAndLoadIntoDb(caseNumber, orgPrefix);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Helper method to delete all data currently in the database
     *
     * @param db
     * @return
     */
    private static void deleteAllData(final MissingKidsDatabase db) {
        int numRows = db.missingKidDao().deleteAll();
        Log.d(TAG, "Number of rows deleted from db: " + numRows);
    }

    /**
     * Gets the JSONArray data from the network. Must be run on a background thread.
     * <p>
     * This currently fetches all the pages one at a time
     * </p>
     *
     * @return The json data
     */
    private static JSONArray getJsonFromJsonUrl() {
        try {
            return NetworkUtils.getSearchResultsDataJsonArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts the JSONArray data into a list of ChildData
     *
     * @param jsonData The json data
     * @return The list of ChildData
     */
    private static List<ChildData> convertJSONArrayToChildData(JSONArray jsonData) {
        return DataParsingUtils.getChildDataListFromJsonArray(jsonData);
    }

    /**
     * Converts a list of ChildData into a list of MissingKid data
     *
     * @param childData The list of ChildData
     * @return The list of MissingKid data
     */
    private static List<MissingKid> convertChildDataToMissingKid(List<ChildData> childData) {
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
    private static void populateAsync(List<MissingKid> missingKids) {
        MissingKidDao dao = mDb.missingKidDao();
        int numUpdated = 0;
        int numInserted = 0;
        List<String> orgPrefixes = new ArrayList<>();
        for (MissingKid kid : missingKids) {
            MissingKid kidFromDb = dao.findKidByOrgPrefixCaseNum(kid.orgPrefixCaseNumber);
            boolean check = orgPrefixes.contains(kid.orgPrefixCaseNumber);
            if (check) {
                Log.e(TAG, "Duplicate ID found!");
            } else {
                orgPrefixes.add(kid.orgPrefixCaseNumber);
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

    private static class RemoveAllDataFromDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            deleteAllData(mDb);
            return null;
        }
    }

    private static class FetchDataFromServerAndLoadIntoDb extends AsyncTask<Integer, Void, String> {

        String previousHeadCaseNum;

        public FetchDataFromServerAndLoadIntoDb(String caseNum) {
            previousHeadCaseNum = caseNum;
        }

        @Override
        protected String doInBackground(Integer... pageNums) {
            // first get the JSONArray
            JSONArray searchResults;
            try {
                NetworkUtils.getSearchResultsMetadataJson();
                searchResults = NetworkUtils.getSearchResultPageJsonArray(pageNums[0]);
                if (searchResults != null) {
                    // then convert the JSONArray into ChildObjects
                    List<ChildData> childData = convertJSONArrayToChildData(searchResults);

                    // check to see if we should stop incrementing through pages, that is if
                    // the first case number in the current page is the same as the first case
                    // number in the previous page
                    String currentHeadCaseNum = childData.get(0).getCaseNumber();
                    if (currentHeadCaseNum.equals(previousHeadCaseNum)) {
                        Log.d(TAG, "Reached the end of the list");
                        return null;
                    }

                    // convert the ChildObjects into MissingKids
                    // TODO: Currently this replaces all detail data with null
                    List<MissingKid> missingKids = convertChildDataToMissingKid(childData);
                    // populate the database with the MissingKids
                    populateAsync(missingKids);
                } else {
                    Log.e(TAG, "There was no data returned from the server.");
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "There was an error getting the search results");
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }

    private static class FetchDetailDataFromServerAndLoadIntoDb extends AsyncTask<Void, Void, Void> {
        private boolean success = false;

        private String mCaseNumber;
        private String mOrgPrefix;

        public FetchDetailDataFromServerAndLoadIntoDb(String caseNumber, String orgPrefix) {
            mCaseNumber = caseNumber;
            mOrgPrefix = orgPrefix;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MissingKid completeKid = appendDetailDataWithPartialKid(mCaseNumber, mOrgPrefix);
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

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

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
