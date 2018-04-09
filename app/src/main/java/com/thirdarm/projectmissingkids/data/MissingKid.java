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

    public static MissingKid convertFromPartialChildData(ChildData data) {
        MissingKid kid = new MissingKid();
        kid.ncmcId = data.getCaseNumber();
        kid.source = data.getOrgName();
        kid.originalPhotoUrl = data.getThumbnailURL();
        kid.race = data.getRace();

        // name
        kid.name = new Name();
        kid.name.firstName = data.getFirstName();
        kid.name.middleName = data.getMiddleName();
        kid.name.lastName = data.getLastName();

        // address
        kid.address = new Address();
        kid.address.locCity = data.getMissingCity();
        kid.address.locState = data.getMissingState();
        kid.address.locCountry = data.getMissingCountry();

        kid.date = new Date();
        kid.date.age = data.getAge();

        // Approx age (ChildData regex: "lower-upper" e.g. "15-25")
        String approxAgeRange = data.getApproxAge();
        if (!approxAgeRange.equals("")) {
            Tuple<Integer, Integer> ageRange = GeneralUtils.convertStringNumberRangeToInts(approxAgeRange);
            kid.date.estAgeLower = ageRange.x;
            kid.date.estAgeHigher = ageRange.y;
        }

        // Date stuff
        java.util.Date missingDate = data.getMissingDate();
        kid.date.dateMissing = missingDate.getTime();

        return kid;
    }

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