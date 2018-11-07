package com.thirdarm.projectmissingkids.data.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.thirdarm.projectmissingkids.util.NetworkState;

/**
 * Wrapper encasing LiveData of a list of {@link MissingKid}s and a {@link NetworkState}. This is
 * to be used for queries involving remote data sources (can also be used alongside with local
 * queries, as long as remote queries are involved)
 */
public class MissingKidSearchResult {
    private LiveData<PagedList<MissingKid>> data;
    private LiveData<NetworkState> networkState;

    public MissingKidSearchResult(LiveData<PagedList<MissingKid>> data, LiveData<NetworkState> networkState) {
        this.data = data;
        this.networkState = networkState;
    }

    public LiveData<PagedList<MissingKid>> getData() {
        return data;
    }

    public void setData(LiveData<PagedList<MissingKid>> data) {
        this.data = data;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public void setNetworkState(LiveData<NetworkState> networkState) {
        this.networkState = networkState;
    }
}
