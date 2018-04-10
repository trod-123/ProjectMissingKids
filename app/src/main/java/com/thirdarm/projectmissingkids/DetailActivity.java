package com.thirdarm.projectmissingkids;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidDao;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.databinding.ActivityDetailBinding;

import java.util.List;


public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mDetailDataBinding;

    private Bundle intentBundle;
    private long mNcmc;
    public static final String NCMC = "NCMC ID";

    private MissingKidDao mMissingKidDao;
    private MissingKidsDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        intentBundle = getIntent().getExtras();
        mNcmc = intentBundle.getLong(NCMC);

        mDb = MissingKidsDatabase.getMissingKidsDatabase(this);
        new DetailFetchTask(mDb);

    }

    private class DetailFetchTask extends AsyncTask<Long, Void, MissingKid> {

        public DetailFetchTask(MissingKidsDatabase db) {
            mMissingKidDao = db.missingKidDao();
        }

        @Override
        protected MissingKid doInBackground(Long... id) {

            long mId = id[0];
            return mMissingKidDao.findKidByNcmcId(mId);
        }

        @Override
        protected void onPostExecute(MissingKid missingKid) {

        }
    }
}
