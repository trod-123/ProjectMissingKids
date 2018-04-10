package com.thirdarm.projectmissingkids;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidDao;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.databinding.ActivityDetailBinding;
import com.thirdarm.projectmissingkids.util.DatabaseInitializer;

import java.util.List;


public class DetailActivity extends AppCompatActivity implements
        LoaderCallbacks<MissingKid>,
        DatabaseInitializer.OnDbPopulationFinishedListener {

    private ActivityDetailBinding mDetailDataBinding;

    private Bundle intentBundle;
    private String uID;
    public static final String UID_KEY = "UID";

    private MissingKid mKid;
    private MissingKidDao mMissingKidDao;
    private MissingKidsDatabase mDb;

    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        intentBundle = getIntent().getExtras();

        uID = intentBundle.getString(UID_KEY);
        mDb = MissingKidsDatabase.getMissingKidsDatabase(this);
        DatabaseInitializer.loadDetailDataIntoPartialKidData(mDb, this, uID, "NCMC");

        LoaderCallbacks<MissingKid> callback = DetailActivity.this;
        getSupportLoaderManager().initLoader(LOADER_ID, intentBundle, callback);
    }

    @Override
    public Loader<MissingKid> onCreateLoader(int id, final Bundle bundle) {

        return new AsyncTaskLoader<MissingKid>(this) {

            MissingKid mMissingKid = null;

            @Override
            public MissingKid loadInBackground() {
                uID = bundle.getString(UID_KEY);
                String mUID = "NCMC" + uID;

                mMissingKidDao = mDb.missingKidDao();
                MissingKid kid = mMissingKidDao.findKidByNcmcId(mUID);

                return kid;
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(MissingKid data) {
                mMissingKid = data;
                super.deliverResult(mMissingKid);
            }
        };


    }

    @Override
    public void onLoadFinished(Loader<MissingKid> loader, MissingKid data) {
        mKid = data;
    }

    @Override
    public void onLoaderReset(Loader<MissingKid> loader) {

    }

    @Override
    public void onFinishedLoading() {
        mDetailDataBinding.extraDetails.ageNumber.setText(mKid.name.firstName);

    }
}
