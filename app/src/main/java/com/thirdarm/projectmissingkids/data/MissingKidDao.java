package com.thirdarm.projectmissingkids.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

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

    // <editor-fold defaultstate="collapsed" desc="Query methods">

    /**
     * Gets all the MissingKids currently in the database
     *
     * @return List of all MissingKids
     */
    @Query("SELECT * FROM kids")
    List<MissingKid> loadAllKids();

    /**
     * Gets all the MissingKids currently in the database
     *
     * @return List of all MissingKids
     */
    @Query("SELECT * FROM kids")
    LiveData<List<MissingKid>> loadAllKidsSync();

    /**
     * Gets all the MissingKids currently in the database
     *
     * @return List of all MissingKids
     */
    @Query("SELECT * FROM kids")
    Cursor loadAllKidsCursor();

//    /**
//     * Gets all the MissingKids currently in the database
//     *
//     * @return List of all MissingKids
//     */
//    @Query("SELECT * FROM kids")
//    LiveData<Cursor> loadAllKidsCursorSync();

    /**
     * Gets the MissingKid with the provided  uid
     *
     * @param uid The uid
     * @return The MissingKid associated with the uid
     */
    @Query("SELECT * FROM kids WHERE uid IS :uid")
    MissingKid findKidByUid(int uid);

    /**
     * Gets the MissingKid with the provided  uid
     *
     * @param uid The uid
     * @return The MissingKid associated with the uid
     */
    @Query("SELECT * FROM kids WHERE uid IS :uid")
    LiveData<MissingKid> findKidByUidSync(int uid);

    /**
     * Gets the MissingKid with the provided NCMC id
     *
     * @param orgPrefixCaseNumber The unique identifier
     * @return The MissingKid associated with the NCMC id
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IS :orgPrefixCaseNumber")
    MissingKid findKidByOrgPrefixCaseNum(String orgPrefixCaseNumber);

    /**
     * Gets the MissingKid with the provided NCMC id
     *
     * @param orgPrefixCaseNumber The unique identifier
     * @return The MissingKid associated with the NCMC id
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IS :orgPrefixCaseNumber")
    LiveData<MissingKid> findKidByOrgPrefixCaseNumSync(String orgPrefixCaseNumber);

    /**
     * Gets the MissingKid with the provided NCMC id
     *
     * @param orgPrefixCaseNumber The unique identifier
     * @return The MissingKid associated with the NCMC id
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IS :orgPrefixCaseNumber")
    Cursor findKidByOrgPrefixCaseNumCursor(String orgPrefixCaseNumber);

//    /**
//     * Gets the MissingKid with the provided NCMC id
//     *
//     * @param orgPrefixCaseNumber The unique identifier
//     * @return The MissingKid associated with the NCMC id
//     */
//    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IS :orgPrefixCaseNumber")
//    LiveData<Cursor> findKidByOrgPrefixCaseNumCursorSync(String orgPrefixCaseNumber);

    /**
     * Gets all the MissingKids with the provided array of NCMC ids (NCMC ids must be unique)
     *
     * @param orgPrefixCaseNumbers The list of The unique identifiers
     * @return The MissingKids associated with the unique identifiers
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IN (:orgPrefixCaseNumbers)")
    List<MissingKid> loadAllKidsByOrgPrefixCaseNum(List<String> orgPrefixCaseNumbers);

    /**
     * Gets all the MissingKids with the provided array of NCMC ids (NCMC ids must be unique)
     *
     * @param orgPrefixCaseNumbers The list of The unique identifiers
     * @return The MissingKids associated with the unique identifiers
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IN (:orgPrefixCaseNumbers)")
    LiveData<List<MissingKid>> loadAllKidsByOrgPrefixCaseNumSync(List<String> orgPrefixCaseNumbers);

    /**
     * Gets all the MissingKids with the provided array of NCMC ids (NCMC ids must be unique)
     *
     * @param orgPrefixCaseNumbers The list of The unique identifiers
     * @return The MissingKids associated with the unique identifiers
     */
    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IN (:orgPrefixCaseNumbers)")
    Cursor loadAllKidsByOrgPrefixCaseNumCursor(List<String> orgPrefixCaseNumbers);

//    /**
//     * Gets all the MissingKids with the provided array of NCMC ids (NCMC ids must be unique)
//     *
//     * @param orgPrefixCaseNumbers The list of The unique identifiers
//     * @return The MissingKids associated with the unique identifiers
//     */
//    @Query("SELECT * FROM kids WHERE orgPrefixCaseNumber IN (:orgPrefixCaseNumbers)")
//    LiveData<Cursor> loadAllKidsByOrgPrefixCaseNumCursorSync(List<String> orgPrefixCaseNumbers);

    /**
     * Gets the MissingKid with the provided names
     *
     * @param first First name of the MissingKid
     * @param last  Last name of the MissingKid
     * @return The MissingKid with the provided first AND last names
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    MissingKid findKidByName(String first, String last);

    /**
     * Gets the MissingKid with the provided names
     *
     * @param first First name of the MissingKid
     * @param last  Last name of the MissingKid
     * @return The MissingKid with the provided first AND last names
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    LiveData<MissingKid> findKidByNameSync(String first, String last);

    /**
     * Gets the MissingKid with the provided names
     *
     * @param first First name of the MissingKid
     * @param last  Last name of the MissingKid
     * @return The MissingKid with the provided first AND last names
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    Cursor findKidByNameCursor(String first, String last);

//    /**
//     * Gets the MissingKid with the provided names
//     *
//     * @param first First name of the MissingKid
//     * @param last  Last name of the MissingKid
//     * @return The MissingKid with the provided first AND last names
//     */
//    @Query("SELECT * FROM kids WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    LiveData<Cursor> findKidByNameCursorSync(String first, String last);

    /**
     * Searches for MissingKids with the provided name
     *
     * @param search The name to search
     * @return The MissingKids with a first OR last name that matches the provided search parameter
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :search " +
            "OR last_name LIKE :search")
    List<MissingKid> findAllKidsByName(String search);

    /**
     * Searches for MissingKids with the provided name
     *
     * @param search The name to search
     * @return The MissingKids with a first OR last name that matches the provided search parameter
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :search " +
            "OR last_name LIKE :search")
    LiveData<List<MissingKid>> findAllKidsByNameSync(String search);

    /**
     * Searches for MissingKids with the provided name
     *
     * @param search The name to search
     * @return The MissingKids with a first OR last name that matches the provided search parameter
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :search " +
            "OR last_name LIKE :search")
    Cursor findAllKidsByNameCursor(String search);

//    /**
//     * Searches for MissingKids with the provided name
//     *
//     * @param search The name to search
//     * @return The MissingKids with a first OR last name that matches the provided search parameter
//     */
//    @Query("SELECT * FROM kids WHERE first_name LIKE :search " +
//            "OR last_name LIKE :search")
//    LiveData<Cursor> findAllKidsByNameCursorSync(String search);

    /**
     * Searches for MissingKids that are between the provided age range
     *
     * @param minAge The min age to search
     * @param maxAge The max age to search
     * @return The MissingKids whose ages are between the provided age range
     */
    @Query("SELECT * FROM kids WHERE age BETWEEN :minAge AND :maxAge")
    List<MissingKid> findAllKidsBetweenAges(int minAge, int maxAge);

    /**
     * Searches for MissingKids that are between the provided age range
     *
     * @param minAge The min age to search
     * @param maxAge The max age to search
     * @return The MissingKids whose ages are between the provided age range
     */
    @Query("SELECT * FROM kids WHERE age BETWEEN :minAge AND :maxAge")
    LiveData<List<MissingKid>> findAllKidsBetweenAgesSync(int minAge, int maxAge);

    /**
     * Searches for MissingKids that are between the provided age range
     *
     * @param minAge The min age to search
     * @param maxAge The max age to search
     * @return The MissingKids whose ages are between the provided age range
     */
    @Query("SELECT * FROM kids WHERE age BETWEEN :minAge AND :maxAge")
    Cursor findAllKidsBetweenAgesCursor(int minAge, int maxAge);

//    /**
//     * Searches for MissingKids that are between the provided age range
//     *
//     * @param minAge The min age to search
//     * @param maxAge The max age to search
//     * @return The MissingKids whose ages are between the provided age range
//     */
//    @Query("SELECT * FROM kids WHERE age BETWEEN :minAge AND :maxAge")
//    LiveData<Cursor> findAllKidsBetweenAgesCursorSync(int minAge, int maxAge);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of cities
     *
     * @param cities List of cities to search
     * @return The MissingKids who are last seen or found in the provided list of cities
     */
    @Query("SELECT * FROM kids WHERE loc_city IN (:cities)")
    List<MissingKid> findAllKidsFromCities(List<String> cities);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of cities
     *
     * @param cities List of cities to search
     * @return The MissingKids who are last seen or found in the provided list of cities
     */
    @Query("SELECT * FROM kids WHERE loc_city IN (:cities)")
    LiveData<List<MissingKid>> findAllKidsFromCitiesSync(List<String> cities);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of cities
     *
     * @param cities List of cities to search
     * @return The MissingKids who are last seen or found in the provided list of cities
     */
    @Query("SELECT * FROM kids WHERE loc_city IN (:cities)")
    Cursor findAllKidsFromCitiesCursor(List<String> cities);

//    /**
//     * Searches for MissingKids that are last seen or found in any of the provided list of cities
//     *
//     * @param cities List of cities to search
//     * @return The MissingKids who are last seen or found in the provided list of cities
//     */
//    @Query("SELECT * FROM kids WHERE loc_city IN (:cities)")
//    LiveData<Cursor> findAllKidsFromCitiesCursorSync(List<String> cities);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of states. States must be formatted using the ANSI 2-letter abbreviation standard.
     *
     * @param states List of states to search, in ANSI 2-letter abbreviaion standard
     * @return The MissingKids who are last seen or found in the provided list of states
     */
    @Query("SELECT * FROM kids WHERE loc_state IN (:states)")
    List<MissingKid> findAllKidsFromStates(List<String> states);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of states. States must be formatted using the ANSI 2-letter abbreviation standard.
     *
     * @param states List of states to search, in ANSI 2-letter abbreviaion standard
     * @return The MissingKids who are last seen or found in the provided list of states
     */
    @Query("SELECT * FROM kids WHERE loc_state IN (:states)")
    LiveData<List<MissingKid>> findAllKidsFromStatesSync(List<String> states);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of states. States must be formatted using the ANSI 2-letter abbreviation standard.
     *
     * @param states List of states to search, in ANSI 2-letter abbreviaion standard
     * @return The MissingKids who are last seen or found in the provided list of states
     */
    @Query("SELECT * FROM kids WHERE loc_state IN (:states)")
    Cursor findAllKidsFromStatesCursor(List<String> states);

//    /**
//     * Searches for MissingKids that are last seen or found in any of the provided list of states. States must be formatted using the ANSI 2-letter abbreviation standard.
//     *
//     * @param states List of states to search, in ANSI 2-letter abbreviaion standard
//     * @return The MissingKids who are last seen or found in the provided list of states
//     */
//    @Query("SELECT * FROM kids WHERE loc_state IN (:states)")
//    LiveData<Cursor> findAllKidsFromStatesCursorSync(List<String> states);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of countries. Countries must be formatted using the 2-letter country code, based on ISO 3166-1 alpha-2 standard.
     *
     * @param countries List of countries to search, in 2-letter country code, based on ISO 3166-1 alpha-2 standard
     * @return The MissingKids who are last seen or found in the provided list of countries
     */
    @Query("SELECT * FROM kids WHERE loc_country IN (:countries)")
    List<MissingKid> findAllKidsFromCountries(List<String> countries);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of countries. Countries must be formatted using the 2-letter country code, based on ISO 3166-1 alpha-2 standard.
     *
     * @param countries List of countries to search, in 2-letter country code, based on ISO 3166-1 alpha-2 standard
     * @return The MissingKids who are last seen or found in the provided list of countries
     */
    @Query("SELECT * FROM kids WHERE loc_country IN (:countries)")
    LiveData<List<MissingKid>> findAllKidsFromCountriesSync(List<String> countries);

    /**
     * Searches for MissingKids that are last seen or found in any of the provided list of countries. Countries must be formatted using the 2-letter country code, based on ISO 3166-1 alpha-2 standard.
     *
     * @param countries List of countries to search, in 2-letter country code, based on ISO 3166-1 alpha-2 standard
     * @return The MissingKids who are last seen or found in the provided list of countries
     */
    @Query("SELECT * FROM kids WHERE loc_country IN (:countries)")
    Cursor findAllKidsFromCountriesCursor(List<String> countries);

//    /**
//     * Searches for MissingKids that are last seen or found in any of the provided list of countries. Countries must be formatted using the 2-letter country code, based on ISO 3166-1 alpha-2 standard.
//     *
//     * @param countries List of countries to search, in 2-letter country code, based on ISO 3166-1 alpha-2 standard
//     * @return The MissingKids who are last seen or found in the provided list of countries
//     */
//    @Query("SELECT * FROM kids WHERE loc_country IN (:countries)")
//    LiveData<Cursor> findAllKidsFromCountriesCursorSync(List<String> countries);

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Insert methods">

    /**
     * Insert multiple kids into the database
     *
     * @param kids The list of MissingKids to insert
     * @return The array of rowIds for all inserted items
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertManyKids(MissingKid... kids);

    /**
     * Insert only one kid into the database
     *
     * @param kid The MissingKid to insert
     * @return The rowId for the inserted item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSingleKid(MissingKid kid);

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Update methods">

    /**
     * Update multiple kids in the database. Matches against the primary key of each entity
     *
     * @param kids The list of MissingKids to update
     * @return The number of rows updated in the database
     */
    @Update
    int updateManyKids(MissingKid... kids);

    /**
     * Update only one kid in the database. Matches against the primary key of the kid in the database
     *
     * @param kid The MissingKid to update
     * @return The number of rows updated in the database
     */
    @Update
    int updateSingleKid(MissingKid kid);

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Delete methods">

    /**
     * Delete multiple kids from the database. Uses the primary keys to find the entities to delete
     *
     * @param kids The list of MissingKids to remove
     * @return The number of rows removed from the database
     */
    @Delete
    int deleteManyKids(MissingKid... kids);

    /**
     * Delete only one kid from the database. Uses the primary key to find the kid to delete
     *
     * @param kid The MissingKid to remove
     * @return The number of rows removed from the database
     */
    @Delete
    int deleteSingleKid(MissingKid kid);

    /**
     * Delete all the kids from the database
     *
     * @return The number of rows removed from the database
     */
    @Query("DELETE FROM kids")
    int deleteAll();

    // </editor-fold>
}
