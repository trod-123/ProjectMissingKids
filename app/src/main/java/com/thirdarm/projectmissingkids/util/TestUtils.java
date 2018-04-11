package com.thirdarm.projectmissingkids.util;

import com.thirdarm.projectmissingkids.data.Address;
import com.thirdarm.projectmissingkids.data.Date;
import com.thirdarm.projectmissingkids.data.Height;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.data.Name;
import com.thirdarm.projectmissingkids.data.Weight;

import java.util.ArrayList;
import java.util.List;

/**
 *  Helper methods for testing
 */
public class TestUtils {

    public static MissingKid createOneKid() {
        MissingKid kid = new MissingKid();
        kid.uid = 1;
        kid.name = new Name();
        kid.name.firstName = "Jane";
        kid.name.middleName = "Davidson";
        kid.name.lastName = "Doe";
        kid.address = new Address();
        kid.address.locCity = "Los Angeles";
        kid.address.locState = "CA";
        kid.address.locCountry = "US";
        kid.date = new Date();
        kid.date.age = 16;
        kid.date.dateMissing = System.currentTimeMillis();
        kid.date.dateOfBirth = 1474588800000L;
        kid.description = "Amir's photo is shown age-progressed to 16 years. He may be in the company his mother. They are believed to have left the country and traveled to Egypt. The child is of Egyptian descent.";
        kid.eyeColor = "Blue";
        kid.hairColor = "Brown";
        kid.gender = "Female";
        kid.race = "White";
        kid.height = new Height();
        kid.height.heightImperial = 56;
        kid.weight = new Weight();
        kid.weight.weightImperial = 94;
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
