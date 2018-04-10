package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.thirdarm.projectmissingkids.util.GeneralUtils;
import com.thirdarm.projectmissingkids.util.Tuple;

/**
 * This entity represents a MissingKid table within the MissingKidsDatabase.  Contains all the "columns" of an SQLite database table
 */
@Entity(tableName = "kids")
public class MissingKid {

    /**
     * Create a MissingKid object with partial data, but no detail data
     * @param partialChildData
     * @return
     */
    public static MissingKid convertFromPartialChildData(ChildData partialChildData) {
        MissingKid partialKidData = new MissingKid();
        partialKidData.ncmcId = partialChildData.getCaseNumber();
        partialKidData.source = partialChildData.getOrgName();
        partialKidData.originalPhotoUrl = partialChildData.getThumbnailURL();
        partialKidData.race = partialChildData.getRace();

        // name
        partialKidData.name = new Name();
        partialKidData.name.firstName = partialChildData.getFirstName();
        partialKidData.name.middleName = partialChildData.getMiddleName();
        partialKidData.name.lastName = partialChildData.getLastName();

        // address
        partialKidData.address = new Address();
        partialKidData.address.locCity = partialChildData.getMissingCity();
        partialKidData.address.locState = partialChildData.getMissingState();
        partialKidData.address.locCountry = partialChildData.getMissingCountry();

        partialKidData.date = new Date();
        partialKidData.date.age = partialChildData.getAge();

        // Approx age (ChildData regex: "lower-upper" e.g. "15-25")
        String approxAgeRange = partialChildData.getApproxAge();
        if (!approxAgeRange.equals("")) {
            Tuple<Integer, Integer> ageRange = GeneralUtils.convertStringNumberRangeToInts(approxAgeRange);
            partialKidData.date.estAgeLower = ageRange.x;
            partialKidData.date.estAgeHigher = ageRange.y;
        }

        // Date stuff
        if (partialChildData.getMissingDate() != null) {
            java.util.Date missingDate = partialChildData.getMissingDate();
            partialKidData.date.dateMissing = missingDate.getTime();
        }
        if (partialChildData.getBirthDate() != null) {
            java.util.Date birthDate = partialChildData.getBirthDate();
            partialKidData.date.dateOfBirth = birthDate.getTime();
        }

        return partialKidData;
    }

    /**
     * Create a MissingKid object with detail data, but no partial data
     * @param detailChildData
     * @return
     */
    public static MissingKid convertFromDetailChildData(ChildData detailChildData) {
        MissingKid detailKidData = new MissingKid();

        detailKidData.gender = detailChildData.getSex();


        // Height stuff
        detailKidData.height = new Height();

        // store height based on unit of measurement
        // UI needs to check both: if heightImperial is provided, then use inches. otherwise, use meters
        if (detailChildData.isHeightInInch()) {
            detailKidData.height.heightImperial = detailChildData.getHeight();
        } else {
            detailKidData.height.heightMetric = detailChildData.getHeight();
        }

        // Weight stuff
        detailKidData.weight = new Weight();

        // store weight based on unit of measurement
        // UI needs to check both: if weightImperial is provided, then use lbs. otherwise, use kg
        if (detailChildData.isWeightInPound()) {
            detailKidData.weight.weightImperial = detailChildData.getWeight();
        } else {
            detailKidData.weight.weightMetric = detailChildData.getWeight();
        }

        detailKidData.eyeColor = detailChildData.getEyeColor();
        detailKidData.hairColor = detailChildData.getHairColor();

        return detailKidData;
    }

    /**
     * Create a MissingKid object with full ChildData, with detail ChildData
     * @param fullChildData
     * @return
     */
    public static MissingKid convertFromFullChildData(ChildData fullChildData) {
        // Get partialKidData
        MissingKid partialKidData = convertFromPartialChildData(fullChildData);
        // Get detailKidData
        MissingKid detailKidData = convertFromDetailChildData(fullChildData);
        // merge the kid data
        return mergePartialDetailMissingKidData(partialKidData, detailKidData);
    }

    /**
     * Merge partial and detail MissingKid data into one complete MissingKid object
     * @param partialKidData
     * @param detailKidData
     * @return
     */
    public static MissingKid mergePartialDetailMissingKidData(MissingKid partialKidData, MissingKid detailKidData) {
        MissingKid completeKidData = partialKidData;

        completeKidData.gender = detailKidData.gender;

        // Height stuff
        completeKidData.height = new Height();
        completeKidData.height.heightImperial = detailKidData.height.heightImperial;
        completeKidData.height.heightMetric = detailKidData.height.heightMetric;

        // Weight stuff
        completeKidData.weight = new Weight();
        completeKidData.weight.weightImperial = detailKidData.weight.weightImperial;
        completeKidData.weight.weightMetric = detailKidData.weight.weightMetric;

        completeKidData.eyeColor = detailKidData.eyeColor;
        completeKidData.hairColor = detailKidData.hairColor;

        return completeKidData;
    }

    /**
     * Append detail ChildData with partial MissingKid data into one complete MissingKid object
     * @param detailChildData
     * @param partialKidData
     * @return
     */
    public static MissingKid appendDetailChildDataWithPartialKidData(MissingKid partialKidData, ChildData detailChildData) {
        MissingKid detailKidData = convertFromDetailChildData(detailChildData);
        return mergePartialDetailMissingKidData(partialKidData, detailKidData);
    }

    /**
     * Default constructor
     */
    public MissingKid() {
    }

    /**
     *  Primary key, for the local database. Not related to any ncmc/ncic/namus ids
     */
    @PrimaryKey(autoGenerate = true)
    public int uid;

    /**
     *  National Center for Missing and Exploited Children (NCMEC/NCMC) id (String)
     *  <p>
     *  To be appended to "NCMEC" or "NCMC"
     */
    @ColumnInfo(name = "ncmc_id")
    public String ncmcId;

    /**
     *  National Crime Information Center (NCIC) id (String)
     *  <p>
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "ncic_id")
    public String ncicId;

    /**
     *  National Missing and Unidentified Persons System (NAMUS) (String)
     *  <p>
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "namus_id")
    public String namusId;

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