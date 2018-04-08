package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * This entity represents a MissingKid table within the MissingKidsDatabase.  Contains all the "columns" of an SQLite database table
 */
@Entity(tableName = "kids")
public class MissingKid {

    /**
     *  Primary key, for the local database. Not related to any ncmc/ncic/namus ids
     */
    @PrimaryKey(autoGenerate = true)
    public int uid;

    /**
     *  National Center for Missing and Exploited Children (NCMEC/NCMC) id (long)
     *  <p>
     *  To be appended to "NCMEC" or "NCMC"
     */
    @ColumnInfo(name = "ncmc_id")
    public long ncmcId;

    /**
     *  National Crime Information Center (NCIC) id (long)
     *  <p>
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "ncic_id")
    public long ncicId;

    /**
     *  National Missing and Unidentified Persons System (NAMUS) (long)
     *  <p>
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "namus_id")
    public long namusId;

    /**
     *  List of associated NCMS ids (e.g. abductors) (long array)
     */
//    @ColumnInfo(name = "associated_ncms_ids")
//    public long[] associatedNCMSids;

    @Embedded
    public Name name;

    @Embedded
    public Date date;

    @Embedded
    public Address address;

    /**
     *  Url linking to detailed information about child (String)
     *  (this can be generated using "api.missingkids.org/poster/NCMC/[ncmec_id]", CP entry may not be needed)
     */
    @ColumnInfo(name = "poster_url")
    public String posterUrl;

    /**
     *  Url linking to child's original photo, if any (String)
     */
    @ColumnInfo(name = "original_photo_url")
    public String originalPhotoUrl;

    /**
     *  Url linking to child's extra photos, if any (String array / blob)
     */
//    @ColumnInfo(name = "extra_photos_url")
//    public String[] extraPhotosUrl;

    /**
     *  Blurb of additional details of child (String)
     */
    public String description;

    /**
     *  Status (STATUS enumeration / constant)
     *  - Missing
     *  - Unidentified child
     *  - NonFamily Abduction
     *  - Endangered Missing
     *  - Family Abduction
     *  - Endangered Runaway
     *  - Endangered Missing
     *  - ...
     */
    public String status;

    /**
     *  Child's gender (male, female) (GENDER enumeration / constant)
     */
    public String gender;

    /**
     *  Child's race (String)
     */
    public String race;

    /**
     *  Child's hair color (String)
     */
    @ColumnInfo(name = "hair_color")
    public String hairColor;

    /**
     *  Child's eye color (String)
     */
    @ColumnInfo(name = "eye_color")
    public String eyeColor;

    @Embedded
    public Height height;

    @Embedded
    public Weight weight;

    /**
     *  Information source (usually it's "National Center for Missing & Exploited Children", but can also be "NCMEC-Unidentified" if child status is "Unidentified")
     */
    public String source;
}