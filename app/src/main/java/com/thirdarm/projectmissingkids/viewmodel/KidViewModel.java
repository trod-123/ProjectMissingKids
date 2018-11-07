package com.thirdarm.projectmissingkids.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.thirdarm.projectmissingkids.data.KidsRepository;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.data.model.MissingKidSearchResult;
import com.thirdarm.projectmissingkids.util.NetworkState;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mediates the passing of data between UI and local and remote data sources
 */
public class KidViewModel extends AndroidViewModel {
    private KidsRepository mRepository;
    private ExecutorService mExecutor; // dedicated to this instance of the view model

    public KidViewModel(@NonNull Application application) {
        super(application);
        mRepository = new KidsRepository(application, mExecutor = Executors.newCachedThreadPool());
    }

    // region Local/Remote search and sync

    public static final String DEFAULT_SEARCH_QUERY = "";

    /**
     * Posts a request to load local and remote data
     * <p>
     * TODO: Pass in query parameters to customize network fetch. Currently defaults to
     * <ol>
     * <li>California only</li>
     * <li>Increasing alphabetical order</li>
     * </ol>
     */
    public void fetchKidsLocalAndRemote(String query) {
        startSearchLiveData.postValue(query);
    }

    /**
     * Stores data gathered from a search called by {@link KidViewModel#fetchKidsLocalAndRemote}
     */
    private MutableLiveData<String> startSearchLiveData = new MutableLiveData<>();

    /**
     * Generates the {@link MissingKidSearchResult} from the search initiated by
     * {@link KidViewModel#fetchKidsLocalAndRemote}, via a mapping from {@link KidViewModel#startSearchLiveData}
     */
    private LiveData<MissingKidSearchResult> searchResult = Transformations.map(startSearchLiveData,
            new Function<String, MissingKidSearchResult>() {
                @Override
                public MissingKidSearchResult apply(String input) {
                    return mRepository.getKidsFromSearch(input);
                }
            });

    /**
     * Holds the list of kids returned from {@link KidViewModel#searchResult}
     */
    public LiveData<PagedList<MissingKid>> kidsFromSearch = Transformations.switchMap(searchResult,
            new Function<MissingKidSearchResult, LiveData<PagedList<MissingKid>>>() {
                @Override
                public LiveData<PagedList<MissingKid>> apply(MissingKidSearchResult input) {
                    return input.getData();
                }
            });

    /**
     * Holds the most recent {@link NetworkState} from {@link KidViewModel#searchResult}
     */
    public LiveData<NetworkState> networkStateFromSearch = Transformations.switchMap(searchResult,
            new Function<MissingKidSearchResult, LiveData<NetworkState>>() {
                @Override
                public LiveData<NetworkState> apply(MissingKidSearchResult input) {
                    return input.getNetworkState();
                }
            });

    // endregion

    //
    // Database accessors. Only query methods available - users will not need to add,
    // update, or delete any kid
    //

    public List<MissingKid> getAllKids_list() {
        return mRepository.getAllKidsAsync();
    }

    public LiveData<PagedList<MissingKid>> getAllKidsFromLocal() {
        return mRepository.getAllKidsFromLocal();
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
