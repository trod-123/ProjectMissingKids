package com.thirdarm.projectmissingkids;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidDao;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.util.DatabaseInitializer;
import com.thirdarm.projectmissingkids.util.FakeDatabaseInitializer;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        KidsAdapter.KidsAdapterOnClickHandler,
        DatabaseInitializer.OnDbPopulationFinishedListener {

    // For keeping reference to db
    MissingKidsDatabase mDb;

    private KidsAdapter mKidsAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;

    public static final int INDEX_NCMC_ID = 123456;
    public static final String NCMC = "NCMC ID";

    List<MissingKid> kids;

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

        showLoading();

        // set up the database
        mDb = MissingKidsDatabase.getMissingKidsDatabase(this);
        DatabaseInitializer.initializeDbWithOnlineData(mDb, this);

    }

    /**
     * Initialize the database then fill it with fake data
     */
//    private void initializeDatabase() {
//        mDb = MissingKidsDatabase.getMissingKidsDatabase(this);
//        FakeDatabaseInitializer.populateAsync(mDb, this);
//
//        // TODO: AsyncTask will load data. See overridden onFinishedLoading() method below
//    }


    /**
     * Get value for NCMC from the onClick
     * and pass the NCMC id to the detailActivity
     *
     * @param id The NCMC id for the row that was clicked
     */
    @Override
    public void onClick(long id) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra(NCMC, id);
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

    @Override
    public void onFinishedLoading() {
        // TODO: this is called when FakeDatabaseInitializer.populateAsync() is complete
        (new MissingKidsFetchTask(mDb)).execute();
    }


    private class MissingKidsFetchTask extends AsyncTask<Void, Void, List<MissingKid>> {
        MissingKidDao dao;

        public MissingKidsFetchTask(MissingKidsDatabase db) {
            dao = db.missingKidDao();
        }

        @Override
        protected List<MissingKid> doInBackground(Void... voids) {
            return dao.loadAllKids();
        }

        @Override
        protected void onPostExecute(List<MissingKid> missingKids) {

            // TODO: Swap empty list or cursor in RecyclerView
            mKidsAdapter.swapList(missingKids);
            showView();
        }
    }
}
