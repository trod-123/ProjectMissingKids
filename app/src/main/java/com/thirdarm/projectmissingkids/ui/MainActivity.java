package com.thirdarm.projectmissingkids.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.thirdarm.projectmissingkids.R;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.util.NetworkState;
import com.thirdarm.projectmissingkids.viewmodel.KidViewModel;

public class MainActivity extends AppCompatActivity implements
        KidsAdapter.KidsAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private KidsAdapter mKidsAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;

    private KidViewModel mViewModel;

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
        mKidsAdapter = new KidsAdapter(this, this);
        mRecyclerView.setAdapter(mKidsAdapter);

        // get the instance of the Kids view model and then subscribe to events
        mViewModel = ViewModelProviders.of(this).get(KidViewModel.class);
        mViewModel.kidsFromSearch.observe(this, new Observer<PagedList<MissingKid>>() {
            @Override
            public void onChanged(@Nullable PagedList<MissingKid> missingKids) {
                Log.d("KidsAdapter", "OnChanged - submitting list...");
                mKidsAdapter.submitList(missingKids);
                if (!visible) {
                    showView();
                    visible = true;
                }
            }
        });
        // for the network state
        mViewModel.networkStateFromSearch.observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState == NetworkState.LOADING) {
                    // TODO: Wrap network state in via a SwipeRefreshListener and its onRefresh()
                    // loading animation
                    //showLoading();
                } else {
                    // showView();
                }
            }
        });

        if (savedInstanceState == null) {
            // Loads up the ViewModel and the UI with data // TODO: Enable manual refresh of all data from page 1
            // Only do this the first time the activity is loaded
            mViewModel.fetchKidsLocalAndRemote(KidViewModel.DEFAULT_SEARCH_QUERY);
            showLoading();
        }
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
     * @param orgPrefix
     * @param caseNum
     */
    @Override
    public void onClick(String orgPrefix, String caseNum) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.ORG_PREFIX_KEY, orgPrefix);
        detailIntent.putExtra(DetailActivity.CASE_NUM_KEY, caseNum);
        startActivity(detailIntent);
    }
}
