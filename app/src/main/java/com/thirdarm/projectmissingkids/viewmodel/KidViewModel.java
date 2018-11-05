package com.thirdarm.projectmissingkids.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.thirdarm.projectmissingkids.data.KidsRepository;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.data.remote.RemoteDataFactory;
import com.thirdarm.projectmissingkids.data.remote.RemoteDataSource;
import com.thirdarm.projectmissingkids.util.NetworkState;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KidViewModel extends AndroidViewModel {
    private KidsRepository mRepository;
    private ExecutorService mExecutor;

    private LiveData<NetworkState> mNetworkState;
    private LiveData<PagedList<MissingKid>> mKidLiveData;

    public KidViewModel(@NonNull Application application) {
        super(application);
        mRepository = new KidsRepository(application, mExecutor = Executors.newCachedThreadPool());
        initPagedRemoteDataSource();
    }

    /**
     * Loads up {@link KidViewModel#mKidLiveData} via Paged network sync
     */
    private void initPagedRemoteDataSource() {
        RemoteDataFactory dataFactory = new RemoteDataFactory();
        mNetworkState = Transformations.switchMap(dataFactory.getMutableLiveData(),
                new Function<RemoteDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(RemoteDataSource input) {
                        return input.getNetworkState();
                    }
                });
        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                //.setInitialLoadSizeHint(10)
                .setPageSize(20) // determines how many pages to get, based on # of items desired
                .build();
        mKidLiveData = (new LivePagedListBuilder<Integer, MissingKid>(dataFactory, pagedListConfig))
                .setFetchExecutor(mExecutor)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<PagedList<MissingKid>> getKidLiveData() {
        return mKidLiveData;
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
        return mRepository.getKidByOrgPrefixCaseNum(orgPrefix + caseNum);
    }

    public LiveData<List<MissingKid>> getAllKidsByName(String name) {
        return mRepository.getAllKidsByName(name);
    }
}
