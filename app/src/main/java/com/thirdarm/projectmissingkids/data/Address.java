package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;

public class Address {
    /**
     * State location from which child is missing or found. Stored as the ANSI 2-letter abbreviation  (String)
     */
    @ColumnInfo(name = "loc_state")
    public String locState;

    /**
     * City location from which child is missing or found (String)
     */
    @ColumnInfo(name = "loc_city")
    public String locCity;

    /**
     * Country location from which child is missing or found. Stored as 2-letter country code, based on ISO 3166-1 alpha-2 (String)
     */
    @ColumnInfo(name = "loc_country")
    public String locCountry;
}
