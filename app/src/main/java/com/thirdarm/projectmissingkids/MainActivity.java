package com.thirdarm.projectmissingkids;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.sync.KidsSyncUtils;
import com.thirdarm.projectmissingkids.viewmodel.LiveDataKidsListViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        KidsAdapter.KidsAdapterOnClickHandler {

    // For keeping reference to db
    MissingKidsDatabase mDb;

    private KidsAdapter mKidsAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;

    private LiveDataKidsListViewModel mLiveDataKidsListViewModel;

    public static final String ORG_PREFIX_KEY = "ORG_PREFIX";
    public static final String UID_KEY = "UID";

    List<MissingKid> kids;

    private boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_kids);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* Divider between each item */
        RecyclerView.ItemDecoration mDivider =
                new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDivider);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mKidsAdapter = new KidsAdapter(kids, this, this);

        mRecyclerView.setAdapter(mKidsAdapter);

        // set up the database
        mDb = MissingKidsDatabase.getInstance(this);

        // get the instance of the Kids view model and then subscribe to events
        mLiveDataKidsListViewModel = ViewModelProviders.of(this).get(LiveDataKidsListViewModel.class);
        subscribe();
    }

    private class DiffUtilsCallbacks extends DiffUtil.Callback {

        private List<MissingKid> oldList, newList;

        public DiffUtilsCallbacks(List<MissingKid> oldList, List<MissingKid> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList != null && newList != null && oldList.get(oldItemPosition).uid == newList.get(newItemPosition).uid;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList != null && newList != null && oldList.get(oldItemPosition).name.equals(newList.get(newItemPosition).name);
        }
    }

    /**
     * Helper method to pair the UI observer with the ViewModel. The observer is used to monitor
     * changes to the list of missing kids. If the list changes, then the Observer's onChanged()
     * method is called, refreshing the KidAdapter's list with the new list
     */
    private void subscribe() {
        // load the kids
        mLiveDataKidsListViewModel.loadKidsFromLocalDbAsync(mDb);

        // load online data
        // TODO: This currently replaces all detail data with null values
        KidsSyncUtils.initialize(this);

        // set up the observer which updates the UI
        final Observer<List<MissingKid>> kidsObserver = new Observer<List<MissingKid>>() {

            @Override
            public void onChanged(@Nullable List<MissingKid> missingKids) {
                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtilsCallbacks(kids, missingKids));
                mKidsAdapter.swapList(missingKids);
                result.dispatchUpdatesTo(mKidsAdapter);
                kids = missingKids;
                //mKidsAdapter.swapList(missingKids);
                if (!visible) {
                    showView();
                    visible = true;
                }
            }
        };

        showLoading();
        mLiveDataKidsListViewModel.getMissingKids().observe(this, kidsObserver);

    }

    private void showView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void showLoading() {
        /* Then, hide the data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Get value for NCMC from the onClick
     * and pass the NCMC id to the detailActivity
     *
     * @param id The NCMC id for the row that was clicked
     */
    @Override
    public void onClick(String id, String orgPrefix) {
        // TODO: For some reason, clicks are not being registered while list is being updated via LiveData
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra(UID_KEY, id);
        detailIntent.putExtra(ORG_PREFIX_KEY, orgPrefix);
        startActivity(detailIntent);
    }
}
