package com.thirdarm.projectmissingkids.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * For handling asynchronous task requests in a service on a separate thread. Used to call
 * different off-thread tasks that would run in a service, including fetching data from server
 */
public class KidsSyncIntentService extends IntentService {

    public KidsSyncIntentService() {
        super("KidsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // run the sync task (TODO: Add other sync stuff here, and use intent.getAction() to switch based on action given)
        new KidsSyncTask(this).syncKidsFromOnline();
    }
}
