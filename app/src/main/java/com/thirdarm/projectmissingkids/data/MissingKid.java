package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.thirdarm.projectmissingkids.util.GeneralUtils;
import com.thirdarm.projectmissingkids.util.Tuple;

/**
 * This entity represents a MissingKid table within the MissingKidsDatabase.  Contains all the "columns" of an SQLite database table
 */
@Entity(tableName = "kids",
        indices = {@Index(value={"orgPrefixCaseNumber"}, unique = true)})
public class MissingKid {

    // TODO (1): Confirm necessity of fields and clean out unnecessary fields
    // TODO (2): Fix all references to these fields if needed

    /**
     * Default constructor
     */
    public MissingKid() {
    }

    /**
     * Create a MissingKid object out of the preview data
     *
     * @param caseNumber
     * @param orgPrefix
     * @param orgName
     * @param isChild
     * @param seqNumber
     * @param langId
     * @param firstName
     * @param lastName
     * @param middleName
     * @param missingCity
     * @param missingCounty
     * @param missingState
     * @param missingCountry
     * @param missingDate
     * @param age
     * @param inMonth
     * @param inDay
     * @param approxAge
     * @param hasThumbnail
     * @param hasPoster
     * @param thumbnailURL
     * @param caseType
     * @param posterTitle
     * @param race
     */
    public MissingKid(String caseNumber, String orgPrefix, String orgName, boolean isChild,
                     int seqNumber, String langId, String firstName, String lastName,
                     String middleName, String missingCity, String missingCounty,
                     String missingState, String missingCountry, java.util.Date missingDate, int age,
                     boolean inMonth, boolean inDay, String approxAge, boolean hasThumbnail,
                     boolean hasPoster, String thumbnailURL, String caseType, String posterTitle,
                     String race) {
        this.caseNum = caseNumber;
        this.orgPrefix = orgPrefix;
        this.orgPrefixCaseNumber = orgPrefix + caseNumber;
        this.source = orgName;
        //this.isChild = isChild;
        //this.seqNumber = seqNumber;
        //this.langId = langId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.locCity = missingCity;
        //this.missingCounty = missingCounty;
        this.locState = missingState;
        this.locCountry= missingCountry;
        if (missingDate != null) {
            this.dateMissing = missingDate.getTime();
        }
        this.age = age;
        //this.inMonth = inMonth;
        //this.inDay = inDay;
        if (!approxAge.equals("")) {
            Tuple<Integer, Integer> ageRange = GeneralUtils.convertStringNumberRangeToInts(approxAge);
            this.estAgeLower = ageRange.x;
            this.estAgeHigher = ageRange.y;
        }
        //this.hasThumbnail = hasThumbnail;
        //this.hasPoster = hasPoster;
        this.originalPhotoUrl = thumbnailURL;
        //this.caseType = caseType;
        //this.posterTitle = posterTitle;
        this.race = race;
    }

    /**
     * Append detail data to the called MissingKid object
     *
     * @param hasAgedPhoto
     * @param hasExtraPhoto
     * @param possibleLocation
     * @param sex
     * @param race
     * @param birthDate
     * @param height
     * @param heightInInch
     * @param weight
     * @param weightInPound
     * @param eyeColor
     * @param hairColor
     * @param hasPhoto
     * @param missingProvince
     * @param circumstance
     * @param profileNarrative
     * @param orgContactInfo
     * @param orgLogo
     * @param isClearinghouse
     * @param repSightURL
     * @param altContact
     * @param photoMap
     */
    public void addDetailData(boolean hasAgedPhoto, boolean hasExtraPhoto, String possibleLocation,
                              String sex, String race, java.util.Date birthDate, int height, boolean heightInInch,
                              int weight, boolean weightInPound, String eyeColor, String hairColor,
                              boolean hasPhoto, String missingProvince, String circumstance,
                              String profileNarrative, String orgContactInfo, String orgLogo,
                              boolean isClearinghouse, String repSightURL, String altContact,
                              String photoMap) {
        //this.hasAgedPhoto = hasAgedPhoto;
        //this.hasExtraPhoto = hasExtraPhoto;
        //this.possibleLocation = possibleLocation;
        this.gender = sex;
        this.race = race;
        if (birthDate != null) {
            this.dateOfBirth = birthDate.getTime();
        }
        this.heightImperial = height;
        //this.heightInInch = heightInInch;
        this.weightImperial = weight;
        //this.weightInPound = weightInPound;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        //this.hasPhoto = hasPhoto;
        //this.missingProvince = missingProvince;
        this.description = circumstance;
        //this.profileNarrative = profileNarrative;
        //this.orgContactInfo = orgContactInfo;
        //this.orgLogo = orgLogo;
        //this.isClearinghouse = isClearinghouse;
        //this.repSightURL = repSightURL;
        //this.altContact = altContact;
        //this.photoMap = photoMap;
    }

    /**
     *  Primary key, for the local database. Not related to any ncmc/ncic/namus ids
     */
    @PrimaryKey(autoGenerate = true)
    public int uid;

    /**
     *  The case number
     */
    public String caseNum;

    /**
     * The organization prefix
     */
    public String orgPrefix;

    /**
     * Organization prefix combined with Case number (to be used as the unique id)
     */
    public String orgPrefixCaseNumber;

    /**
     *  List of associated NCMS ids (e.g. abductors) (long array)
     */
//    @ColumnInfo(name = "associated_ncms_ids")
//    public long[] associatedNCMSids;

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

    /**
     * UTC date missing (long)
     */
    @ColumnInfo(name = "date_missing")
    public long dateMissing;

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

    /**
     *  Child's height, in imperial units (inches) (double)
     */
    @ColumnInfo(name = "height_imperial")
    public double heightImperial;

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
     * Child's weight, in imperial units (pounds) (double)
     */
    @ColumnInfo(name = "weight_imperial")
    public double weightImperial;

    /**
     *  Information source (usually it's "National Center for Missing & Exploited Children", but can also be "NCMEC-Unidentified" if child status is "Unidentified")
     */
    public String source;
}