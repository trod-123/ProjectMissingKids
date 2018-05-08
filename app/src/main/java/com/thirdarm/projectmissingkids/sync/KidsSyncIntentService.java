package com.thirdarm.projectmissingkids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * For handling asynchronous task requests in a service on a separate thread. Used to call
 * different off-thread tasks that would run in a service, including fetching data from server
 */
public class KidsSyncIntentService extends IntentService {

    private static final String TAG = KidsSyncIntentService.class.getSimpleName();

    public static final String INTENT_ACTION_COMPLETE_SYNC = "intent_action_complete_sync";
    public static final String INTENT_ACTION_SINGLE_DETAILS_SYNC = "intent_action_single_details_sync";
    public static final String STRING_EXTRA_CASE_NUMBER = "string_extra_case_number";
    public static final String STRING_EXTRA_ORG_PREFIX = "string_extra_org_prefix";

    public KidsSyncIntentService() {
        super("KidsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case INTENT_ACTION_COMPLETE_SYNC:
                    new KidsSyncTasks(this).syncKidsFromOnline();
                    break;
                case INTENT_ACTION_SINGLE_DETAILS_SYNC:
                    if (intent.hasExtra(STRING_EXTRA_CASE_NUMBER) && intent.hasExtra(STRING_EXTRA_ORG_PREFIX)) {
                        String caseNumber = intent.getStringExtra(STRING_EXTRA_CASE_NUMBER);
                        String orgPrefix = intent.getStringExtra(STRING_EXTRA_ORG_PREFIX);
                        new KidsSyncTasks(this).syncDetailDataFromOnline(caseNumber, orgPrefix);
                    } else {
                        Log.w(TAG, "There were no caseNumber or orgPrefix provided. Will not sync.");
                    }
                    break;
            }
        }
    }
}
