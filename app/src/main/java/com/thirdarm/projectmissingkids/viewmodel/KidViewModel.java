package com.thirdarm.projectmissingkids.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.thirdarm.projectmissingkids.data.KidsRepository;
import com.thirdarm.projectmissingkids.data.model.MissingKid;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KidViewModel extends AndroidViewModel {
    private KidsRepository mRepository;

    public KidViewModel(@NonNull Application application) {
        super(application);
        ExecutorService test = Executors.newCachedThreadPool();
        mRepository = new KidsRepository(application, test);
    }

    //
    // Only query methods available - users will not need to add, update, or delete any kid
    //

    public List<MissingKid> getAllKids_list() {
        return mRepository.getAllKidsAsync();
    }

    public LiveData<List<MissingKid>> getAllKids() {
        return mRepository.getAllKids();
    }

    /**
     * Gets a single MissingKid and fetches and merges its details in a background thread,
     * if missing
     *
     * @param orgPrefix
     * @param caseNum
     * @return
     */
    public LiveData<MissingKid> getKidByOrgPrefixCaseNum(String orgPrefix, String caseNum) {
        return mRepository.getKidByOrgPrefixCaseNum(orgPrefix, caseNum);
    }

    public LiveData<List<MissingKid>> getAllKidsByName(String name) {
        return mRepository.getAllKidsByName(name);
    }
}
