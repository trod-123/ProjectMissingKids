package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;

public class Name {
    /**
     * Child's first name (String)
     */
    @ColumnInfo(name = "first_name")
    public String firstName;

    /**
     * Child's middle name, if any (String)
     */
    @ColumnInfo(name = "middle_name")
    public String middleName;

    /**
     * Child's last name (String)
     */
    @ColumnInfo(name = "last_name")
    public String lastName;
}
