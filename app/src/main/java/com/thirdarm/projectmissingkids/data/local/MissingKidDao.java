package com.thirdarm.projectmissingkids.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.thirdarm.projectmissingkids.data.model.MissingKid;

import java.util.List;

/**
 * A DAO (Data Access Object) contains CRUD methods used to access the database
 * <p>
 * Note: Database access with DAOs is only possible when done asynchronously (on a background thread)
 * <p>
 * LiveData observable query methods should be used for returning data to your UI that would
 * automatically update when the data changes in the database
 * <p>
 * Cursor query methods are available for convenience, but you should be able to get your MissingKid
 * objects and properties directly by using the non-cursor methods. In fact, you should be able to
 * do all database operations without needing to directly access the cursor. If you are using
 * the cursor methods, remember to always check whether the rows exist before taking any action
 * on them
 */
@Dao
public interface MissingKidDao {

    // region Query

    /**
     * Gets all the MissingKids currently in the database
     *
     * @return List of all MissingKids
     */
    @Query("SELECT * FROM kids")
    List<MissingKid> getAllKidsAsync();

    /**
     * Gets all the MissingKids currently in the database
     *
     * @return List of all MissingKids
     */
    @Query("SELECT * FROM kids")
    LiveData<List<MissingKid>> getAllKids();

    /**
     * Gets the MissingKid with the provided NCMC id
     *
     * @param orgPrefixCaseNumber The unique identifier
     * @return The MissingKid associated with the NCMC id
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IS :orgPrefixCaseNumber")
    MissingKid getKidByOrgPrefixCaseNumAsync(String orgPrefixCaseNumber);

    /**
     * Gets the MissingKid with the provided NCMC id
     *
     * @param orgPrefixCaseNumber The unique identifier
     * @return The MissingKid associated with the NCMC id
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IS :orgPrefixCaseNumber")
    LiveData<MissingKid> getKidByOrgPrefixCaseNum(String orgPrefixCaseNumber);

    /**
     * Searches for MissingKids with the provided name
     *
     * @param search The name to search
     * @return The MissingKids with a first OR last name that matches the provided search parameter
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :search " +
            "OR last_name LIKE :search")
    LiveData<List<MissingKid>> getAllKidsByName(String search);

    // endregion

    // region Insert

    /**
     * Insert multiple kids into the database
     *
     * @param kids The list of MissingKids to insert
     * @return The array of rowIds for all inserted items
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insert(MissingKid... kids);

    /**
     * Insert only one kid into the database
     *
     * @param kid The MissingKid to insert
     * @return The rowId for the inserted item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(MissingKid kid);

    // endregion

    // region Update

    /**
     * Update multiple kids in the database. Matches against the primary key of each entity
     *
     * @param kids The list of MissingKids to update
     * @return The number of rows updated in the database
     */
    @Update
    int update(MissingKid... kids);

    /**
     * Update only one kid in the database. Matches against the primary key of the kid in the database
     *
     * @param kid The MissingKid to update
     * @return The number of rows updated in the database
     */
    @Update
    int update(MissingKid kid);

    // endregion

    // region Delete

    /**
     * Delete multiple kids from the database. Uses the primary keys to find the entities to delete
     *
     * @param kids The list of MissingKids to remove
     * @return The number of rows removed from the database
     */
    @Delete
    int delete(MissingKid... kids);

    /**
     * Delete only one kid from the database. Uses the primary key to find the kid to delete
     *
     * @param kid The MissingKid to remove
     * @return The number of rows removed from the database
     */
    @Delete
    int delete(MissingKid kid);

    /**
     * Delete all the kids from the database
     *
     * @return The number of rows removed from the database
     */
    @Query("DELETE FROM kids")
    int deleteAll();

    // endregion
}
