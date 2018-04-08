package com.thirdarm.projectmissingkids;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.thirdarm.projectmissingkids.Utils.TestUtils;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.MissingKidDao;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase;
import com.thirdarm.projectmissingkids.data.MissingKidsDatabase_Impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MissingKidsDatabaseTest {
    private MissingKidDao mMissingKidDao;
    private MissingKidsDatabase mDb;

    /**
     * Sets up the database
     */
    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, MissingKidsDatabase.class).build();
        mMissingKidDao = mDb.missingKidDao();
    }

    /**
     * Closes the database once testing is completed
     * @throws IOException
     */
    @After
    public void closeDb() throws IOException {
        if (mDb != null)
            mDb.close();
    }

    /**
     *  Inserts a dummy MissingKid object into the database, and accesses the object from the database via a search query that returns a list of MissingKids. Verifies equivalence between the MissingKid that was inserted, and what is accessed
     * @throws Exception
     */
    @Test
    public void writeMissingKidAndReadInList() throws Exception {
        // create a missing kid object
        MissingKid kid = TestUtils.createOneKid();
        // store missing kid object into the database
        mMissingKidDao.insertSingleKid(kid);

        // query the database for missing kids with the name "Jane"
        // notice the internals of the method take care of the sql stuff. instead of returning a cursor, we can return the MissingKid objects directly, which could be used to hold our objects in the RecyclerView. We can also return cursors, see writeMissingKidAndReadInCursor() below
        List<MissingKid> byName = mMissingKidDao.findAllKidsByName("Jane");
        MissingKid kid2 = byName.get(0);

        // check that the missing kid from the query is the same as the missing kid we created
        // check each property of kid
        assertEquals(kid.description, kid2.description);
        assertEquals(kid.eyeColor, kid2.eyeColor);
        assertEquals(kid.gender, kid2.gender);
        assertEquals(kid.hairColor, kid2.hairColor);
        assertEquals(kid.ncmcId, kid2.ncmcId);
        assertEquals(kid.name.firstName, kid2.name.firstName);
    }

    /**
     * Inserts a dummy MissingKid object into the database, and access the object from the database via a search query that returns a cursor of MissingKids. Verifies equivalence between the MissingKid that was inserted, and what is accessed
     * @throws Exception
     */
    @Test
    public void writeMissingKidAndReadInCursor() throws Exception {
        // create a missing kid object
        MissingKid kid = TestUtils.createOneKid();
        // store missing kid object into the database
        mMissingKidDao.insertSingleKid(kid);

        // query the database for missing kids with the name "Jane"
        Cursor cursor = mMissingKidDao.findAllKidsByNameCursor("Jane");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String eyeColor = cursor.getString(cursor.getColumnIndex("eye_color"));
                String gender = cursor.getString(cursor.getColumnIndex("gender"));
                String hairColor = cursor.getString(cursor.getColumnIndex("hair_color"));
                long ncmcId = cursor.getLong(cursor.getColumnIndex("ncmc_id"));
                String firstName = cursor.getString(cursor.getColumnIndex("first_name"));


                // check that the missing kid from the query is the same as the missing kid we created
                // check each property of kid
                assertEquals(kid.description, description);
                assertEquals(kid.eyeColor, eyeColor);
                assertEquals(kid.gender, gender);
                assertEquals(kid.hairColor, hairColor);
                assertEquals(kid.ncmcId, ncmcId);
                assertEquals(kid.name.firstName, firstName);
            }
            cursor.close();
        }
    }
}
