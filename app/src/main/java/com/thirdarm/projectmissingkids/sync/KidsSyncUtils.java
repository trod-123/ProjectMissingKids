package com.thirdarm.projectmissingkids.sync;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;

import java.util.List;

/**
 * Currently checks whether local database is empty, and if so, calls the service to fetch the data
 */
public class KidsSyncUtils {

    /**
     * Keeps track of whether the database had already been through the initialization process
     */
    private static boolean sInitialized = false;

    /**
     * To be run only when the app is loaded. If the database is empty, load it with fresh data
     *
     * @param context
     */
    synchronized public static void initialize(@NonNull final Context context) {
        if (!sInitialized) {
            sInitialized = true;

            /* This AsyncTask is used to check if the database is empty */
            AsyncTask<Void, Void, List<MissingKid>> sync = new AsyncTask<Void, Void, List<MissingKid>>() {

                @Override
                protected List<MissingKid> doInBackground(Void... voids) {
                    return MissingKidsDatabase.getInstance(context).missingKidDao().loadAllKids();
                }

                @Override
                protected void onPostExecute(List<MissingKid> missingKids) {
                    if (missingKids == null || missingKids.size() == 0) {
                        // if there are no kids in the database, then sync with server
                        // TODO: Sync automatically when local database is outdated or "dirty"
                        syncDataWithServer(context);
                    } else {
                        // otherwise, do nothing
                    }
                }
            };
            sync.execute();
        }
    }

    /**
     * Helper method to call the service that would sync the data with the server
     *
     * @param context
     */
    public static void syncDataWithServer(@NonNull final Context context) {
        Intent syncWithServer = new Intent(context, KidsSyncIntentService.class);
        context.startService(syncWithServer);
    }
}
