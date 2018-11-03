package com.thirdarm.projectmissingkids.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.thirdarm.projectmissingkids.DetailActivity;
import com.thirdarm.projectmissingkids.MainActivity;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;

import java.util.List;

/**
 * ViewModel used for the {@link MainActivity}. Strictly to be associated with the List of Kids.
 * See {@link LiveDataDetailKidViewModel} for the ViewModel for the {@link DetailActivity}
 */
public class LiveDataKidsListViewModel extends ViewModel {

    private LiveData<List<MissingKid>> mKidsList;

    /**
     * Loads mKidsList from the Kids database stored on user device
     *
     * @param db The Database from which to retrieve the mKidsList
     */
    public void loadKidsFromLocalDbAsync(MissingKidsDatabase db) {
        mKidsList = db.missingKidDao().getAllKids();
    }

    /**
     * This is only called once, when the Observer links with the ViewModel. This must not be null
     * when observe() is called on it
     *
     * @return The list of kids
     */
    public LiveData<List<MissingKid>> getMissingKids() {
        return mKidsList;
    }
}
