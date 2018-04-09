package com.thirdarm.projectmissingkids.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.Address;
import com.thirdarm.projectmissingkids.data.Date;
import com.thirdarm.projectmissingkids.data.Height;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.data.Name;
import com.thirdarm.projectmissingkids.data.Weight;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to fill the db with fake data. If data already exists in the db, that data is deleted beforehand
 */
public class FakeDatabaseInitializer {

    private static final String TAG = FakeDatabaseInitializer.class.getSimpleName();

    private static final int DELAY_MILLIS = 0;

    /**
     * Populates the database via an AsyncTask, with a simulated delay provided by DELAY_MILLIS for internet fetches
     *
     * @param db The MissingKidsDatabase to populate
     */
    public static void populateAsync(MissingKidsDatabase db, OnDbPopulationFinishedListener listener) {
        PopulateDbAsync task = new PopulateDbAsync(db, listener);
        task.execute();
    }

    public static void populateSync(@NonNull final MissingKidsDatabase db) {
        populateWithFakeData(db, getFakeKids());
    }

    public static List<MissingKid> getFakeKids() {

        List<MissingKid> kids = new ArrayList<>();

        String[] firstNames = new String[]{"Amir", "Jazibi", "Jane", "Christine", "Jorge", "Evelyn", "Rachel", "Christina"};
        String[] middleNames = new String[]{"Kareem", "", "", "", "", "Michelle", "Rae", "James"};
        String[] lastNames = new String[]{"Abdou", "Agapito", "Doe 1969", "Aguirre", "Acosta", "Aguilar", "Aguilar", "Albertson"};
        String[] ncmcIds = new String[]{"982012", "1180655", "1318681", "1275372", "780684", "1276912", "1317601", "1316322"};
        long[] dateOfBirth = new long[]{1474588800000L, 1474588800000L, 1474588800000L, 1474588800000L, 1474588800000L, 1474588800000L, 1474588800000L, 1474588800000L};
        long[] dateMissing = new long[]{System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis()};
        int[] ages = new int[]{16, 16, -1, 16, 16, 16, 16, 16};
        int[] estimatedAgeLowers = new int[]{-1, -1, 16, -1, -1, -1, -1, -1};
        int[] estimatedAgeUppers = new int[]{-1, -1, 21, -1, -1, -1, -1, -1};
        String[] races = new String[]{"White", "Hispanic", "Unknown", "Hispanic", "Hispanic", "Biracial", "Hispanic", "Am. Ind."};
        String[] cities = new String[]{"San Ramon", "Van Nuys", "Los Angeles", "Los Angeles", "Van Nuys", "Wilmington", "Wilmington", "Oceanside"};
        String[] states = new String[]{"CA", "CA", "CA", "CA", "CA", "CA", "CA", "CA"};
        String[] countries = new String[]{"US", "US", "US", "US", "US", "US", "US", "US"};
        String[] sources = new String[]{"National Center for Missing & Exploited Children", "National Center for Missing & Exploited Children", "National Center for Missing & Exploited Children", "National Center for Missing & Exploited Children", "National Center for Missing & Exploited Children", "National Center for Missing & Exploited Children", "National Center for Missing & Exploited Children", "National Center for Missing & Exploited Children"};

        String[] genders = new String[]{"Male", "Female", "Female", "Female", "Male", "Female", "Female", "Female"};
        String[] hairColors = new String[]{"Black", "Brown", "Black", "Black", "Brown", "Blonde", "White", "Brown"};
        String[] eyeColors = new String[]{"Brown", "Brown", "Brown", "Black", "Brown", "Brown", "Black", "Blue"};

        int[] heights = new int[]{24, 52, -1, 60, 40, 34, 70, 55};
        int[] estimatedHeightLowers = new int[]{-1, -1, 40, -1, -1, -1, -1, -1};
        int[] estimatedHeightUppers = new int[]{-1, -1, 60, -1, -1, -1, -1, -1};
        int[] weights = new int[]{30, 80, -1, 90, 102, 70, 30, 55};

        String[] statuses = new String[]{"Missing", "Missing", "Unidentified Child", "Have you seen these children?", "Non-Family Abduction", "Endangered Runaway", "Endangered Missing", "Family Abduction"};

        String[] descriptions = new String[]{"Amir's photo is shown age-progressed to 16 years. He may be in the company his mother. They are believed to have left the country and traveled to Egypt. The child is of Egyptian descent.", "Jazibi's photo is shown age-progressed to 14 years. They were last seen on May 7, 2010. She may be in the company of their mother and other adult female relatives. They may have traveled to Mexico.", "Luis was last seen on November 8, 2017.", "Christine may still be in the local area.", "Jorge's photo is shown age-progressed to 37 years. He was last known to be traveling to a friend's house. Jorge has a birthmark on his right hand.", "Evelyn was last seen on July 25, 2016. She may still be in the local area. Evelyn is biracial. She is Hispanic and Native American. Evelyn wears glasses. She has a scar on her chin. Evelyn has a tattoo of a music note with a heart and the letters \"EKG\" on her wrist.", "Both photos shown are of Rachel. She may still be in the local area.", "Both photos shown are of Christina. She may travel to Tulsa, Oklahoma. When Christina was last seen, she had braces on her teeth."};

        String[] originalPhotoUrls = new String[]{"http://api.missingkids.org/photographs/NCMC982012c1.jpg", "http://api.missingkids.org/photographs/NCMC1180655c1.jpg", "http://api.missingkids.org/photographs/NCMC1318681c1.jpg", "http://api.missingkids.org/photographs/NCMC1275372c1.jpg", "http://api.missingkids.org/photographs/NCMC780684c1.jpg", "http://api.missingkids.org/photographs/NCMC1276912c1.jpg", "http://api.missingkids.org/photographs/NCMC1317601c1.jpg", "http://api.missingkids.org/photographs/NCMC1316322c1.jpg"};

        String[] additionalPhotoUrls = new String[]{"http://api.missingkids.org/photographs/NCMC982012e1.jpg", "http://api.missingkids.org/photographs/NCMC1180655e1.jpg", "", "", "http://api.missingkids.org/photographs/NCMC780684e1.jpg", "", "http://api.missingkids.org/photographs/NCMC1317601x1.jpg", "http://api.missingkids.org/photographs/NCMC1316322x1.jpg"};

        for (int i = 0; i < firstNames.length; i++) {
            MissingKid kid = new MissingKid();
            kid.name = new Name();
            kid.name.firstName = firstNames[i];
            kid.name.middleName = middleNames[i];
            kid.name.lastName = lastNames[i];
            kid.address = new Address();
            kid.address.locCity = cities[i];
            kid.address.locState = states[i];
            kid.address.locCountry = countries[i];
            kid.date = new Date();
            kid.date.age = ages[i]; // if age not available, stored as -1
            kid.date.estAgeLower = estimatedAgeLowers[i]; // if age not available, stored as -1
            kid.date.estAgeHigher = estimatedAgeUppers[i]; // if age not available, stored as -1
            kid.date.dateMissing = dateMissing[i];
            kid.date.dateOfBirth = dateOfBirth[i];
            kid.description = descriptions[i];
            kid.eyeColor = eyeColors[i];
            kid.hairColor = hairColors[i];
            kid.gender = genders[i];
            kid.race = races[i];
            kid.height = new Height();
            kid.height.heightImperial = heights[i]; // if height not available, stored as -1
            kid.height.estHeightImperialLower = estimatedHeightLowers[i]; // if height not available, stored as -1
            kid.height.estHeightImperialHigher = estimatedHeightUppers[i]; // if height not available, stored as -1
            kid.weight = new Weight();
            kid.weight.weightImperial = weights[i]; // if weight not available, stored as -1
            kid.ncmcId = ncmcIds[i];
            kid.posterUrl = "http://api.missingkids.org/poster/NCMC/" + ncmcIds[i];
            kid.originalPhotoUrl = originalPhotoUrls[i];
            kid.source = sources[i];
            kid.status = statuses[i];

            kids.add(kid);
            Log.d(TAG, "Added one kid to list");
        }
        return kids;
    }

    /**
     * Helper method to delete all data currently in the database
     * @param db
     * @return
     */
    public static int deleteAllData(MissingKidsDatabase db) {
        int numRows = db.missingKidDao().deleteAll();
        Log.d(TAG, "Number of rows deleted from db: " + numRows);

        return numRows;
    }

    /**
     * Helper method that inserts fake data in the database. Must be run outside of the main thread since it provides a simulated delay for internet fetches
     *
     * @param db The MissingKidsDatabase to populate
     */
    private static void populateWithFakeData(MissingKidsDatabase db, List<MissingKid> kids) {
        try {
            for (MissingKid kid : kids) {
                long rowId = db.missingKidDao().insertSingleKid(kid);
                Thread.sleep(DELAY_MILLIS);
                Log.d(TAG, "Added one kid to db at row: " + rowId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface OnDbPopulationFinishedListener {
        void onFinishedLoading();
    }

    /**
     * AsyncTask to populate the db with fake MissingKid data
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final MissingKidsDatabase mDb;
        private OnDbPopulationFinishedListener mListener;

        PopulateDbAsync(MissingKidsDatabase db, OnDbPopulationFinishedListener listener) {
            mDb = db;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithFakeData(mDb, getFakeKids());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mListener.onFinishedLoading();
        }
    }

}
