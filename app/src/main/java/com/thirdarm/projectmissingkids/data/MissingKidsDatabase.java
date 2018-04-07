package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * The database object serves as the main access point to the app's persisted data
 */
@Database(version = 1, entities = {MissingKid.class})
public abstract class MissingKidsDatabase extends RoomDatabase {

    private static MissingKidsDatabase INSTANCE;

    public abstract MissingKidDao missingKidDao();

    public static MissingKidsDatabase getMissingKidsDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MissingKidsDatabase.class, "missingKidsDatabase.db").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
