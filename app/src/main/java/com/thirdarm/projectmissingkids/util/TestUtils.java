package com.thirdarm.projectmissingkids.util;

import com.thirdarm.projectmissingkids.data.MissingKid;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for testing
 */
public class TestUtils {

    public static MissingKid createOneKid() {
        MissingKid kid = new MissingKid();
        kid.uid = 1;
        kid.firstName = "Jane";
        kid.middleName = "Davidson";
        kid.lastName = "Doe";
        kid.locCity = "Los Angeles";
        kid.locState = "CA";
        kid.locCountry = "US";
        kid.age = 16;
        kid.dateMissing = System.currentTimeMillis();
        kid.dateOfBirth = 1474588800000L;
        kid.description = "Amir's photo is shown age-progressed to 16 years. He may be in the company his mother. They are believed to have left the country and traveled to Egypt. The child is of Egyptian descent.";
        kid.eyeColor = "Blue";
        kid.hairColor = "Brown";
        kid.gender = "Female";
        kid.race = "White";
        kid.heightImperial = 56;
        kid.weightImperial = 94;
        kid.caseNum = "982012";
        kid.posterUrl = "http://api.missingkids.org/poster/NCMC/982012";
        kid.originalPhotoUrl = "http://api.missingkids.org/photographs/NCMC982012c1.jpg";
        kid.source = "National Center for Missing & Exploited Children";
        kid.status = "Missing";

        return kid;
    }

    public static List<MissingKid> createManyKids() {
        List<MissingKid> kids = new ArrayList<>();

        // TODO: Implement

        return kids;
    }
}
