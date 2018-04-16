package com.thirdarm.projectmissingkids.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.thirdarm.projectmissingkids.DetailActivity;
import com.thirdarm.projectmissingkids.MainActivity;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;

/**
 * ViewModel used for the {@link DetailActivity}. Strictly to be associated with the Detail Activity.
 * See {@link LiveDataKidsListViewModel} for the ViewModel for the {@link MainActivity}
 */
public class LiveDataDetailKidViewModel extends ViewModel {

    private LiveData<MissingKid> mLiveKid;

    /**
     * Loads kids from the Kids database stored on user device
     *
     * @param db The Database from which to retrieve the kid
     */
    public void loadKidDetailsFromLocalDbAsync(MissingKidsDatabase db, String orgPrefixCaseNum) {
        mLiveKid = db.missingKidDao().findKidByOrgPrefixCaseNumSync(orgPrefixCaseNum);
    }

    /**
     * Automatically called from UI observer when the kid is loaded or updated. This must not be
     * null when observe() is called on it
     *
     * @return The list of kids
     */
    public LiveData<MissingKid> getMissingKidDetails() {
        return mLiveKid;
    }
}
