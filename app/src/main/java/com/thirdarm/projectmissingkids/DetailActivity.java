package com.thirdarm.projectmissingkids;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;

import com.squareup.picasso.Picasso;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.databinding.ActivityDetailBinding;
import com.thirdarm.projectmissingkids.viewmodel.LiveDataDetailKidViewModel;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mDetailDataBinding;

    private LiveDataDetailKidViewModel mLiveDataDetailKidViewModel;

    private Bundle intentBundle;
    private String uID;
    private String orgPrefixID;
    public static final String ORG_PREFIX_KEY = "ORG_PREFIX";
    public static final String UID_KEY = "UID";

    private MissingKidsDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        intentBundle = getIntent().getExtras();

        uID = intentBundle.getString(UID_KEY);
        orgPrefixID = intentBundle.getString(ORG_PREFIX_KEY);

        mDb = MissingKidsDatabase.getInstance(this);
        // get the instance of the Kids view model and then subscribe to events
        mLiveDataDetailKidViewModel = ViewModelProviders.of(this).get(LiveDataDetailKidViewModel.class);
        subscribe();
    }

    /**
     * Helper method to pair the UI observer with the ViewModel. The observer is used to monitor
     * changes to the list of missing kids. If the list changes, then the Observer's onChanged()
     * method is called, refreshing the KidAdapter's list with the new list
     */
    private void subscribe() {
        // load the kids
        mLiveDataDetailKidViewModel.loadKidDetailsFromLocalDbAsync(mDb, orgPrefixID + uID);

        // set up the observer which updates the UI
        final Observer<MissingKid> detailKidObserver = new Observer<MissingKid>() {

            @Override
            public void onChanged(@Nullable MissingKid detailKid) {
                loadUI(detailKid);
            }
        };

        mLiveDataDetailKidViewModel.getMissingKidDetails().observe(this, detailKidObserver);
    }

    public void loadUI(MissingKid data) {

        /** Kid Image */
        if (data.originalPhotoUrl != null) {
            String picUrl = "http://api.missingkids.org" + data.originalPhotoUrl;
            String hiResPic = picUrl.substring(0, picUrl.length() - 5);
            hiResPic += ".jpg";

            System.out.println(hiResPic);

            Picasso.get().load(hiResPic).fit().into(mDetailDataBinding.pictureCard.kidsImage);
        }
        /** Kid Name */

        // App bar shows kid's first name
        if (data.name.firstName != null) {
            getSupportActionBar().setTitle(data.name.firstName);

            String name = data.name.firstName + " " +
                    data.name.middleName + " " +
                    data.name.lastName;

            // Below gets rid of double spaces if there's no middle name
            String formatedName = name.replaceAll("\\s{2,}", " ").trim();
            mDetailDataBinding.pictureCard.kidsName.setText(formatedName);
        }

        /** Detail */
        if (data.description != null) {
            mDetailDataBinding.picturesDetails.picturesDetails.setText(data.description);
        }

        /** Missing Since */
        if (data.date.dateMissing != -1L) {
            long millisecond = data.date.dateMissing;
            String missingDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond)).toString();
            mDetailDataBinding.extraDetails.missingSinceDate.setText(missingDateString);
        }

        if (data.address.locCity != null) {
            /** Missing From */
            String location = data.address.locCity + ", " +
                    data.address.locState + ", " +
                    data.address.locCountry;

            mDetailDataBinding.extraDetails.missingFromLocation.setText(location);
        }

        /** DOB */
        if (data.date.dateOfBirth != -1L) {
            long millisecond2 = data.date.dateOfBirth;
            String dobDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond2)).toString();
            mDetailDataBinding.extraDetails.dobDate.setText(dobDateString);
        }

        /** Age Now */
        if (data.date.age != -1) {
            mDetailDataBinding.extraDetails.ageNumber.setText(String.valueOf(data.date.age));
        }

        /** Sex */
        if (data.gender != null) {
            mDetailDataBinding.extraDetails.sexValue.setText((data.gender).toUpperCase());
        }

        /** Race */
        if (data.race != null) {
            mDetailDataBinding.extraDetails.raceValue.setText((data.race).toUpperCase());
        }

        /** Hair Color */
        if (data.hairColor != null) {
            mDetailDataBinding.extraDetails.hairColorValue.setText((data.hairColor).toUpperCase());
        }

        /** Eye Color */
        if (data.eyeColor != null) {
            mDetailDataBinding.extraDetails.eyeColorValue.setText((data.eyeColor).toUpperCase());
        }

        /** Height */
        if (data.height != null) {
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
        }

        /** Weight */
        if (data.weight != null) {
            int roundedWeight = (int) Math.round(data.weight.weightImperial);
            String weightImperial = String.valueOf(roundedWeight) + " lbs";
            mDetailDataBinding.extraDetails.weightValue.setText(String.valueOf(weightImperial));
        }
    }
}
