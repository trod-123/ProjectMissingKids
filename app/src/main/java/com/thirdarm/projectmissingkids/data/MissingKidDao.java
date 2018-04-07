package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * A DAO (Data Access Object) contains CRUD methods used to access the database
 * <p>
 * Note: Database access with DAOs is only possible when done asynchronously (on a background thread)
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
     * Gets the MissingKid with the provided NCMC id
     *
     * @param ncmcId The NCMC id
     * @return The MissingKid associated with the NCMC id
     */
    @Query("SELECT * FROM kids WHERE ncmc_id IS :ncmcId")
    MissingKid findKidByNcmcId(long ncmcId);

    /**
     * Gets all the MissingKids with the provided array of NCMC ids (NCMC ids must be unique)
     *
     * @param ncmcIds The list of NCMC ids
     * @return The MissingKids associated with the NCMC ids
     */
    @Query("SELECT * FROM kids WHERE ncmc_id IN (:ncmcIds)")
    List<MissingKid> loadAllKidsByNcmcIds(List<Long> ncmcIds);

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
     * Searches for MissingKids with the provided name
     *
     * @param search The name to search
     * @return The MissingKids with a first OR last name that matches the provided search parameter
     */
    @Query("SELECT * FROM kids WHERE first_name LIKE :search " +
            "OR last_name LIKE :search")
    List<MissingKid> findAllKidsByName(String search);

    /**
     * Searches for MissingKids that are older than the provided age
     *
     * @param minAge The age to search
     * @return The MissingKids who are older than the provided age
     */
    @Query("SELECT * FROM kids WHERE age > :minAge")
    List<MissingKid> findAllKidsOlderThanAge(int minAge);

    /**
     *  Searches for MissingKids that are younger than the provided age
     *
     * @param maxAge The age to search
     * @return The MissingKids who are younger than the provided age
     */
    @Query("SELECT * FROM kids WHERE age < :maxAge")
    List<MissingKid> findAllKidsYoungerThanAge(int maxAge);

    /**
     *  Searches for MissingKids that are between the provided age range
     *
     * @param minAge The min age to search
     * @param maxAge The max age to search
     * @return The MissingKids whose ages are between the provided age range
     */
    @Query("SELECT * FROM kids WHERE age BETWEEN :minAge AND :maxAge")
    List<MissingKid> findAllKidsBetweenAges(int minAge, int maxAge);

    /**
     *  Searches for MissingKids that are last seen or found in any of the provided list of cities
     * @param cities List of cities to search
     * @return The MissingKids who are last seen or found in the provided list of cities
     */
    @Query("SELECT * FROM kids WHERE loc_city IN (:cities)")
    List<MissingKid> findAllKidsFromCities(List<String> cities);

    /**
     *  Searches for MissingKids that are last seen or found in any of the provided list of states. States must be formatted using the ANSI 2-letter abbreviation standard.
     * @param states List of states to search, in ANSI 2-letter abbreviaion standard
     * @return The MissingKids who are last seen or found in the provided list of states
     */
    @Query("SELECT * FROM kids WHERE loc_state IN (:states)")
    List<MissingKid> findAllKidsFromStates(List<String> states);

    /**
     *  Searches for MissingKids that are last seen or found in any of the provided list of countries. Countries must be formatted using the 2-letter country code, based on ISO 3166-1 alpha-2 standard.
     * @param countries List of countries to search, in 2-letter country code, based on ISO 3166-1 alpha-2 standard
     * @return The MissingKids who are last seen or found in the provided list of countries
     */
    @Query("SELECT * FROM kids WHERE loc_country IN (:countries)")
    List<MissingKid> findAllKidsFromCountries(List<String> countries);

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

    // </editor-fold>
}
