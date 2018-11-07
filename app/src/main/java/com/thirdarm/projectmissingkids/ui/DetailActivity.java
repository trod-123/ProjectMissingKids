package com.thirdarm.projectmissingkids.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;

import com.squareup.picasso.Picasso;
import com.thirdarm.projectmissingkids.R;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.databinding.ActivityDetailBinding;
import com.thirdarm.projectmissingkids.viewmodel.KidViewModel;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mDetailDataBinding;

    private KidViewModel mViewModel;

    public static final String ORG_PREFIX_KEY = "ORG_PREFIX";
    public static final String CASE_NUM_KEY = "case_num";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Bundle intentBundle = getIntent().getExtras();
        if (intentBundle != null) {
            String orgPrefix = intentBundle.getString(ORG_PREFIX_KEY);
            String caseNum = intentBundle.getString(CASE_NUM_KEY);

            // get the instance of the Kids view model and then subscribe to events
            mViewModel = ViewModelProviders.of(this).get(KidViewModel.class);
            mViewModel.getKidByOrgPrefixCaseNum(orgPrefix, caseNum).observe(this, new Observer<MissingKid>() {
                @Override
                public void onChanged(@Nullable MissingKid missingKid) {
                    loadUI(missingKid);
                }
            });
        } else {
            throw new UnsupportedOperationException("DetailActivity/Intent bundle can't be null.");
        }
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
        if (data.firstName != null) {
            getSupportActionBar().setTitle(data.firstName);

            String name = data.firstName + " " +
                    data.middleName + " " +
                    data.lastName;

            // Below gets rid of double spaces if there's no middle name
            String formatedName = name.replaceAll("\\s{2,}", " ").trim();
            mDetailDataBinding.pictureCard.kidsName.setText(formatedName);
        }

        /** Detail */
        if (data.description != null) {
            mDetailDataBinding.picturesDetails.picturesDetails.setText(data.description);
        }

        /** Missing Since */
        if (data.dateMissing != -1L) {
            long millisecond = data.dateMissing;
            String missingDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond)).toString();
            mDetailDataBinding.extraDetails.missingSinceDate.setText(missingDateString);
        }

        if (data.locCity != null) {
            /** Missing From */
            String location = data.locCity + ", " +
                    data.locState + ", " +
                    data.locCountry;

            mDetailDataBinding.extraDetails.missingFromLocation.setText(location);
        }

        /** DOB */
        if (data.dateOfBirth != -1L) {
            long millisecond2 = data.dateOfBirth;
            String dobDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond2)).toString();
            mDetailDataBinding.extraDetails.dobDate.setText(dobDateString);
        }

        /** Age Now */
        if (data.age != -1) {
            mDetailDataBinding.extraDetails.ageNumber.setText(String.valueOf(data.age));
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
        if (data.heightImperial != 0) {
            double height = data.heightImperial;
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
        if (data.weightImperial != 0) {
            int roundedWeight = (int) Math.round(data.weightImperial);
            String weightImperial = String.valueOf(roundedWeight) + " lbs";
            mDetailDataBinding.extraDetails.weightValue.setText(String.valueOf(weightImperial));
        }
    }
}
