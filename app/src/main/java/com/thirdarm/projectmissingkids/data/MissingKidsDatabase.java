package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * The database object serves as the main access point to the app's persisted data
 */
@Database(version = 1, entities = {MissingKid.class})
abstract class MissingKidsDatabase extends RoomDatabase {
    abstract public MissingKidDao missingKidDao();
}
