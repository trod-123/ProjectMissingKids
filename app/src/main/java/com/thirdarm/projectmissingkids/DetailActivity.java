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
    private String orgPrefixID;
    public static final String ORG_PREFIX_KEY = "ORG_PREFIX";
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
        orgPrefixID = intentBundle.getString(ORG_PREFIX_KEY);

        mDb = MissingKidsDatabase.getMissingKidsDatabase(this);
        DatabaseInitializer.loadDetailDataIntoPartialKidData(mDb, this, uID, orgPrefixID);
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

                mMissingKidDao = mDb.missingKidDao();
                MissingKid kid = mMissingKidDao.findKidByOrgPrefixCaseNum(orgPrefixID + uID);

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
        Picasso.get().load(picUrl).fit().into(mDetailDataBinding.kidsImage);

        /** Kid Name */
        String name = data.name.firstName + " " +
                data.name.middleName + " " +
                data.name.lastName;

        // Below gets rid of double spaces if there's no middle name
        String formatedName = name.replaceAll("\\s{2,}", " ").trim();
        //mDetailDataBinding.cardView.setText(formatedName);

        /** Detail */
        mDetailDataBinding.picturesDetails.picturesDetails.setText(data.description);

        /** Missing Since */
        long millisecond = data.date.dateMissing;
        String missingDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond)).toString();
        mDetailDataBinding.extraDetails.missingSinceDate.setText(missingDateString);

        /** Missing From */
        String location = data.address.locCity + ", " +
                data.address.locState + ", " +
                data.address.locCountry;

        mDetailDataBinding.extraDetails.missingFromLocation.setText(location);

        /** DOB */
        long millisecond2 = data.date.dateOfBirth;
        String dobDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond2)).toString();
        mDetailDataBinding.extraDetails.dobDate.setText(dobDateString);

        /** Age Now */
        mDetailDataBinding.extraDetails.ageNumber.setText(String.valueOf(data.date.age));

        /** Sex */
        mDetailDataBinding.extraDetails.sexValue.setText(data.gender);

        /** Race */
        mDetailDataBinding.extraDetails.raceValue.setText(data.race);

        /** Hair Color */
        mDetailDataBinding.extraDetails.hairColorValue.setText(data.hairColor);

        /** Eye Color */
        mDetailDataBinding.extraDetails.eyeColorValue.setText(data.eyeColor);

        /** Height */
        double height = data.height.heightImperial;
        String heightImperial;
        if (height > 24) {
            int inch = (int) Math.round(height % 12);
            int feet = (int) Math.round(height / 12);
            heightImperial = String.valueOf(feet) + "' " + String.valueOf(inch) + "\" ";
        } else {
            int inches = (int) Math.round(height);
            heightImperial = String.valueOf(inches) + "\"";
        }
        mDetailDataBinding.extraDetails.heightValue.setText(heightImperial);

        /** Weight */
        int roundedWeight = (int) Math.round(data.weight.weightImperial);
        String weightImperial = String.valueOf(roundedWeight) + " lbs";
        mDetailDataBinding.extraDetails.weightValue.setText(String.valueOf(weightImperial));
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
    public void onFinishedLoading(boolean success) {
        if (!success) {
            //TODO: Show user error message "There was a problem fetching data from the server. Please try again"
        } else {
            getSupportLoaderManager().initLoader(LOADER_ID, intentBundle, DetailActivity.this);
        }
    }
}
