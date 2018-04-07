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

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work
}

class Name {
    /**
     *  Child's first name (String)
     */
    @ColumnInfo(name = "first_name")
    public String firstName;

    /**
     *  Child's middle name, if any (String)
     */
    @ColumnInfo(name = "middle_name")
    public String middleName;

    /**
     *  Child's last name (String)
     */
    @ColumnInfo(name = "last_name")
    public String lastName;
}

class Height {
    /**
     *  Child's height, in imperial units (inches) (double)
     */
    @ColumnInfo(name = "height_imperial")
    public double heightImperial;

    /**
     *  Child's height, in metric units (meters) (double)
     */
    @ColumnInfo(name = "height_metric")
    public double heightMetric;

    /**
     *  Estimated height, in imperial units (inches): lower bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_imperial_lower")
    public double estHeightImperialLower;

    /**
     *  Estimated height, in imperial units (inches): upper bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_imperial_higher")
    public double estHeightImperialHigher;

    /**
     *  Estimated height, in metric units (meters): lower bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_metric_lower")
    public double estHeightMetricLower;

    /**
     *  Estimated height, in metric units (meters): upper bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_metric_higher")
    public double estHeightMetricHigher;
}

class Weight {
    /**
     * Child's weight, in imperial units (pounds) (double)
     */
    @ColumnInfo(name = "weight_imperial")
    public double weightImperial;

    /**
     * Child's weight, in metric units (kilograms) (double)
     */
    @ColumnInfo(name = "weight_metric")
    public double weightMetric;
}

class Address {
    /**
     *  State location from which child is missing or found. Stored as the ANSI 2-letter abbreviation  (String)
     */
    @ColumnInfo(name = "loc_state")
    public String locState;

    /**
     *  City location from which child is missing or found (String)
     */
    @ColumnInfo(name = "loc_city")
    public String locCity;

    /**
     *  Country location from which child is missing or found. Stored as 2-letter country code, based on ISO 3166-1 alpha-2 (String)
     */
    @ColumnInfo(name = "loc_country")
    public String locCountry;
}

class Date {
    /**
     *  UTC date missing (long)
     */
    @ColumnInfo(name = "date_missing")
    public long dateMissing;

    /**
     *  UTC date found (long)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "date_found")
    public long dateFound;

    /**
     *  Date of birth (long)
     */
    @ColumnInfo(name = "date_of_birth")
    public long dateOfBirth;

    /**
     *  Age (int)
     */
    public int age;

    /**
     *  Estimated age (for where DOB is not provided) (int): lower bound
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_age_lower")
    public int estAgeLower;

    /**
     *  Estimated age (for where DOB is not provided) (int): upper bound
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_age_higher")
    public int estAgeHigher;
}