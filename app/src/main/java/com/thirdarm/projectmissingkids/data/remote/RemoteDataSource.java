package com.thirdarm.projectmissingkids.data.remote;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.util.NetworkState;

import java.util.concurrent.Executors;

/**
 * DataSource for the PagedList, which sets the behavior of when to fetch pages and when
 * to stop, as well as updates the NetworkState which is used for sending the UI sync status
 */
public class RemoteDataSource extends PageKeyedDataSource<Integer, MissingKid> {

    private static final int NO_MORE_PAGES = -100;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private RemoteSync mRemoteSync;

    public RemoteDataSource() {
        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();
        mRemoteSync = new RemoteSync(Executors.newCachedThreadPool());
    }

    public MutableLiveData getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData getInitialLoading() {
        return mInitialLoading;
    }

    /**
     * Loads the first page. Callback method links to UI
     *
     * @param params
     * @param callback
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            final @NonNull LoadInitialCallback<Integer, MissingKid> callback) {
        mInitialLoading.postValue(NetworkState.LOADING);
        mNetworkState.postValue(NetworkState.LOADING);

        mRemoteSync.fetchSinglePage(1,
                new RemoteSync.RemoteSyncCallback<RemoteSync.PageResponse<MissingKid>>() {
                    @Override
                    public void onDataReceived(RemoteSync.PageResponse<MissingKid> response) {
                        callback.onResult(response.results, null, 2);
                        mInitialLoading.postValue(NetworkState.LOADED);
                        mNetworkState.postValue(NetworkState.LOADED);
                    }

                    @Override
                    public void onNoDataReturned(String message) {
                        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, message));
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, message));
                    }

                    @Override
                    public void onFailure(Exception e, @Nullable String message) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, message));
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, MissingKid> callback) {

    }

    /**
     * Loads subsequent pages. Called in the background thread. {@code params.key} gives the
     * page number. Callback method links to UI
     *
     * @param params
     * @param callback
     */
    @Override
    public void loadAfter(final @NonNull LoadParams<Integer> params,
                          final @NonNull LoadCallback<Integer, MissingKid> callback) {
        mNetworkState.postValue(NetworkState.LOADING);

        mRemoteSync.fetchSinglePage(params.key,
                new RemoteSync.RemoteSyncCallback<RemoteSync.PageResponse<MissingKid>>() {
                    @Override
                    public void onDataReceived(RemoteSync.PageResponse<MissingKid> response) {
                        int nextKey = params.key >= response.numPages ? NO_MORE_PAGES : params.key + 1;
                        if (nextKey != NO_MORE_PAGES) {
                            callback.onResult(response.results, nextKey);
                        } else {
                            // Don't fetch any more pages beyond the max. Passing in null for
                            // adjacentPageKey prevents an extra network call from calling
                            // onNoDataReturned()
                            callback.onResult(response.results, null);
                        }
                        mNetworkState.postValue(NetworkState.LOADED);
                    }

                    @Override
                    public void onNoDataReturned(String message) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, message));
                    }

                    @Override
                    public void onFailure(Exception e, @Nullable String message) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, message));
                    }
                });
    }
}
