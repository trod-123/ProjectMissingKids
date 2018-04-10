package com.thirdarm.projectmissingkids;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;

import com.squareup.picasso.Picasso;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidDao;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.databinding.ActivityDetailBinding;
import com.thirdarm.projectmissingkids.util.DatabaseInitializer;

import java.util.Date;


public class DetailActivity extends AppCompatActivity implements
        LoaderCallbacks<MissingKid>,
        DatabaseInitializer.OnDbPopulationFinishedListener {

    private ActivityDetailBinding mDetailDataBinding;

    private Bundle intentBundle;
    private String uID;
    public static final String UID_KEY = "UID";

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


    }

    @Override
    public Loader<MissingKid> onCreateLoader(int id, final Bundle bundle) {

        return new AsyncTaskLoader<MissingKid>(this) {

            MissingKid mMissingKid = null;

            @Override
            protected void onStartLoading() {
                if (mMissingKidDao != null) {
                    deliverResult(mMissingKid);
                } else {
                    forceLoad();
                }
            }


            @Override
            public MissingKid loadInBackground() {
                uID = bundle.getString(UID_KEY);
                String mUID = uID;

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


    /**
     * Receives the MissingKid data and
     * sets to the DetailActivity UI
     *
     * @param loader Passed load from the deliverResult
     * @param data MissingKid data from deliverResult
     */
    @Override
    public void onLoadFinished(Loader<MissingKid> loader, MissingKid data) {

        /** Kid Image */
        String picUrl = "http://api.missingkids.org" + data.originalPhotoUrl;
        Picasso.get().load(picUrl).fit().into(mDetailDataBinding.picturesDetails.kidsImage);


        /** Detail */
        mDetailDataBinding.picturesDetails.picturesDetails.setText(data.description);

        /** Missing Since */
        long millisecond = data.date.dateMissing;
        String missingDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond)).toString();
        mDetailDataBinding.extraDetails.missingSinceDate.setText(missingDateString);

        /** DOB */
        long millisecond2 = data.date.dateOfBirth;
        String dobDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond2)).toString();
        mDetailDataBinding.extraDetails.dobDate.setText(dobDateString);

        /** Age Now */
        //mDetailDataBinding.extraDetails.ageNumber.setText(data.date.age);

        /** Sex */
        mDetailDataBinding.extraDetails.sexValue.setText(data.gender);

        /** Race */
        mDetailDataBinding.extraDetails.raceValue.setText(data.race);

        /** Hair Color */
        mDetailDataBinding.extraDetails.hairColorValue.setText(data.hairColor);

        /** Eye Color */
        mDetailDataBinding.extraDetails.eyeColorValue.setText(data.eyeColor);

        /** Height */
        mDetailDataBinding.extraDetails.heightValue.setText(String.valueOf(data.height.heightMetric));

        /** Weight */
        mDetailDataBinding.extraDetails.weightValue.setText(String.valueOf(data.weight.weightMetric));
    }

    @Override
    public void onLoaderReset(Loader<MissingKid> loader) {

    }


    /**
     * This gets called once the detail Database is loaded
     * From here, loader is initialized.
     *
     */
    @Override
    public void onFinishedLoading() {
        getSupportLoaderManager().initLoader(LOADER_ID, intentBundle, DetailActivity.this);
    }
}
