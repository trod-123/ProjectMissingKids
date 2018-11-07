package com.thirdarm.projectmissingkids.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.projectmissingkids.data.local.LocalDataSource;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.data.remote.RemoteSync;
import com.thirdarm.projectmissingkids.util.NetworkState;

/**
 * A boundary callback for downloading data from remote source and syncing to local source
 */
public class KidsBoundaryCallback extends PagedList.BoundaryCallback<MissingKid> {

    /**
     * Keeps track of the last page loaded so that any previous page does not get loaded again
     */
    public static final String KEY_LAST_REQUESTED_PAGE = "last_requested_page";
    public static final int DEFAULT_LAST_REQUESTED_PAGE = 1;

    private int lastRequestedPage;
    private boolean stillMorePages = true;

    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    // Avoid triggering multiple requests at the same time
    private boolean isRequestInProgress = false;

    private RemoteSync mRemoteSync;
    private LocalDataSource mLocalDataSource;
    private SharedPreferences mSp;

    public KidsBoundaryCallback(RemoteSync remoteSync, LocalDataSource localDataSource, Context context) {
        mRemoteSync = remoteSync;
        mLocalDataSource = localDataSource;
        mSp = PreferenceManager.getDefaultSharedPreferences(context);
        lastRequestedPage = mSp.getInt(KEY_LAST_REQUESTED_PAGE, DEFAULT_LAST_REQUESTED_PAGE);
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        requestAndSaveData();
    }

    @Override
    public void onItemAtEndLoaded(@NonNull MissingKid itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
        requestAndSaveData();
    }

    /**
     * Makes a network call to load and display more data. Last requested page is used to
     * determine next page of data. Stops after we've reached the max number of pages
     */
    public void requestAndSaveData() {
        if (isRequestInProgress || !stillMorePages) return;

        isRequestInProgress = true;
        networkState.postValue(NetworkState.LOADING);
        mRemoteSync.fetchSinglePage(lastRequestedPage,
                new RemoteSync.RemoteSyncCallback<RemoteSync.PageResponse<MissingKid>>() {
                    @Override
                    public void onDataReceived(final RemoteSync.PageResponse<MissingKid> response) {
                        mLocalDataSource.upsert(response.results, new LocalDataSource.InsertFinishedListener() {
                            @Override
                            public void onInsertFinished() {
                                if (lastRequestedPage >= response.numPages) {
                                    // Don't get any more data if we're at the end
                                    stillMorePages = false;
                                } else {
                                    mSp.edit().putInt(KEY_LAST_REQUESTED_PAGE, ++lastRequestedPage).apply();
                                }
                                isRequestInProgress = false;
                            }
                        });
                        networkState.postValue(NetworkState.LOADED);
                    }

                    @Override
                    public void onNoDataReturned(String message) {
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, message));
                        isRequestInProgress = false;

                    }

                    @Override
                    public void onFailure(Exception e, @Nullable String message) {
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, message));
                        isRequestInProgress = false;
                    }
                });
    }
}
