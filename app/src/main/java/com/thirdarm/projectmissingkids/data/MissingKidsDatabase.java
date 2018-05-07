package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * The database object serves as the main access point to the app's persisted data
 */
@Database(version = 2, entities = {MissingKid.class})
public abstract class MissingKidsDatabase extends RoomDatabase {

    private static MissingKidsDatabase INSTANCE;

    public abstract MissingKidDao missingKidDao();

    /**
     * Used to get the singleton instance of the database. This persists across the lifecycle of the
     * app. Use this instead of constructing a new database object
     * @param context
     * @return
     */
    public static synchronized MissingKidsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MissingKidsDatabase.class, "missingKidsDatabase.db")
                    .fallbackToDestructiveMigrationFrom(1)
                    .build();
        }
        return INSTANCE;
    }

    /**
     * Destroys the current instance of the MissingKidsDatabase
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
