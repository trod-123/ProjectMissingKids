package com.thirdarm.projectmissingkids.data.remote;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

/**
 * Responsible for creating the RemoteDataSource used for loading data to a PagedList
 */
public class RemoteDataFactory extends DataSource.Factory {
    private MutableLiveData<RemoteDataSource> mMutableLiveData;
    private RemoteDataSource mRemoteDataSource;

    public RemoteDataFactory() {
        mMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<RemoteDataSource> getMutableLiveData() {
        return mMutableLiveData;
    }

    @Override
    public DataSource create() {
        mRemoteDataSource = new RemoteDataSource();
        mMutableLiveData.postValue(mRemoteDataSource);
        return mRemoteDataSource;
    }
}
