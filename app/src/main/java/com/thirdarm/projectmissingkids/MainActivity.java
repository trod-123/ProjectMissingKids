package com.thirdarm.projectmissingkids;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.util.FakeDatabaseInitializer;

public class MainActivity extends AppCompatActivity implements KidsAdapter.KidsAdapterOnClickHandler {

    // For keeping reference to db
    MissingKidsDatabase mDb;

    private KidsAdapter mKidsAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;

    public static final int INDEX_NCMC_ID = 123456;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_kids);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mKidsAdapter = new KidsAdapter(this, this);
        mRecyclerView.setAdapter(mKidsAdapter);

        showLoading();

        // set up the database with fake data
        initializeDatabase();

    }

    /**
     * Initialize the database then fill it with fake data
     */
    private void initializeDatabase() {
        mDb = MissingKidsDatabase.getMissingKidsDatabase(this);
        FakeDatabaseInitializer.populateAsync(mDb);

        // TODO: Use cursor loader to read data from database
    }

    @Override
    public void onClick(long id) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        // Get Uri for NCMC from MissingKidsContract
        // and set the uri data to the Intent
        startActivity(detailIntent);
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
}
