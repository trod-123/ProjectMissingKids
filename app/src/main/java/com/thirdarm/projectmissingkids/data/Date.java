package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;

public class Date {
    /**
     * UTC date missing (long)
     */
    @ColumnInfo(name = "date_missing")
    public long dateMissing;

    /**
     * UTC date found (long)
     * Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "date_found")
    public long dateFound;

    /**
     * Date of birth (long)
     */
    @ColumnInfo(name = "date_of_birth")
    public long dateOfBirth;

    /**
     * Age (int)
     */
    public int age;

    /**
     * Estimated age (for where DOB is not provided) (int): lower bound
     * Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_age_lower")
    public int estAgeLower;

    /**
     * Estimated age (for where DOB is not provided) (int): upper bound
     * Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_age_higher")
    public int estAgeHigher;
}
