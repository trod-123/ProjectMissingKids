package com.thirdarm.projectmissingkids.util;

import android.os.AsyncTask;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.ChildData;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes the database and loads it with online content
 */
public class DatabaseInitializer {

    private static final String TAG = FakeDatabaseInitializer.class.getSimpleName();

    private static MissingKidsDatabase mDb;
    private static OnDbPopulationFinishedListener mListener;

    public static void initializeDbWithOnlineData(MissingKidsDatabase db,
                                                  OnDbPopulationFinishedListener listener) {
        mDb = db;
        mListener = listener;

        // TODO: For testing only! Remove when done
        deleteAllData(mDb);

        FetchDataFromServer task = new FetchDataFromServer();
        task.execute();
    }

    public interface OnDbPopulationFinishedListener {
        void onFinishedLoading();
    }

    /**
     * Helper method to delete all data currently in the database
     * @param db
     * @return
     */
    public static void deleteAllData(final MissingKidsDatabase db) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int numRows = db.missingKidDao().deleteAll();
                Log.d(TAG, "Number of rows deleted from db: " + numRows);
            }
        }).run();


    /**
     * Gets the JSONArray data from the network. Must be run on a background thread
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
     * @param jsonData The json data
     * @return The list of ChildData
     */
    private static List<ChildData> convertJSONArrayToChildData(JSONArray jsonData) {
        try {
            return DataParsingUtils.getChildDataListFromJsonArray(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void populateAsync(List<MissingKid> missingKids) {
        mDb.missingKidDao().insertManyKids(missingKids.toArray(new MissingKid[]{}));
    }

    /**
     * Converts a list of ChildData into a list of MissingKid data
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

    private static class FetchDataFromServer extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... voids) {
            return getJsonFromJsonUrl();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            ConvertJsonToMissingKid task = new ConvertJsonToMissingKid();
            task.execute(jsonArray);
        }
    }

    private static class ConvertJsonToMissingKid extends AsyncTask<JSONArray, Void, List<MissingKid>> {

        @Override
        protected List<MissingKid> doInBackground(JSONArray... jsonArrays) {
            List<ChildData> childData = convertJSONArrayToChildData(jsonArrays[0]);
            return convertChildDataToMissingKid(childData);
        }

        @Override
        protected void onPostExecute(List<MissingKid> missingKids) {
            LoadDbWithMissingKidData task = new LoadDbWithMissingKidData();
            task.execute(missingKids);
        }
    }

    /**
     * AsyncTask to populate the db with fake MissingKid data
     */
    private static class LoadDbWithMissingKidData extends AsyncTask<List<MissingKid>, Void, Void> {

        @Override
        protected Void doInBackground(List<MissingKid>[] lists) {
            populateAsync(lists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mListener.onFinishedLoading();
        }
    }
}
