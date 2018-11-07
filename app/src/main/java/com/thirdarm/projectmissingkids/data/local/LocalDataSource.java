package com.thirdarm.projectmissingkids.data.local;


import android.arch.paging.DataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.model.MissingKid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Holds accessors for data stored in the Room Database
 */
public class LocalDataSource {

    private static final String TAG = LocalDataSource.class.getSimpleName();

    private MissingKidDao dao;
    private Executor ioExecutor;

    public interface InsertFinishedListener {
        void onInsertFinished();
    }

    public LocalDataSource(MissingKidDao dao, Executor ioExecutor) {
        this.dao = dao;
        this.ioExecutor = ioExecutor;
    }

    /**
     * Asynchronously inserts or updates kids depending on if their
     * {@link MissingKid#orgPrefixCaseNumber} already exists in the database
     *
     * @param kids
     * @param listener
     */
    public void upsert(final List<MissingKid> kids, final InsertFinishedListener listener) {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, String.format("Inserting %s kids", kids.size()));
                upsert(kids);
                listener.onInsertFinished();
            }
        });
    }

    /**
     * Returns a DataSourceFactory of all kids
     *
     * @return
     */
    public DataSource.Factory<Integer, MissingKid> getAllKids() {
        return dao.getAllKids();
    }

    /**
     * Populates the database with the list of MissingKids, via an "Upsert" implementation
     * <p>
     * Needs to be called on a background thread
     *
     * @param missingKids The list of MissingKids data
     */
    private void upsert(@NonNull List<MissingKid> missingKids) {
        List<MissingKid> inserts = new ArrayList<>();
        List<MissingKid> updates = new ArrayList<>();
        for (MissingKid kid : missingKids) {
            // check whether kid already exists in db
            MissingKid kidFromDb = dao.getKidByOrgPrefixCaseNumAsync(kid.orgPrefixCaseNumber);
            if (kidFromDb != null) {
                // update kid only if it already exists in db
                kid.uid = kidFromDb.uid;
                updates.add(kid);
            } else {
                // otherwise, add the kid
                inserts.add(kid);
            }
        }
        Long[] ids = dao.insert(inserts.toArray(new MissingKid[]{}));
        Log.d(TAG + "/upsert", "Inserted: " + inserts.size());
        Log.d(TAG + "/upsert", Arrays.toString(ids));
        dao.update(updates.toArray(new MissingKid[]{}));
        Log.d(TAG + "/upsert", "Updated: " + updates.size());
    }
}
