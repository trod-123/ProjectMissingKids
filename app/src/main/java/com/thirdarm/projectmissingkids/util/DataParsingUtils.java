package com.thirdarm.projectmissingkids.util;

import com.thirdarm.projectmissingkids.data.ChildData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sobelman on 4/6/2018.
 */
public class DataParsingUtils {
    // JSON field names
    private static final String CASE_NUMBER = "caseNumber";
    private static final String ORG_PREFIX = "orgPrefix";
    private static final String ORG_NAME = "orgName";
    private static final String IS_CHILD = "isChild";
    private static final String SEQ_NUMBER = "seqNumber";
    private static final String LANG_ID = "langId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastNaem";
    private static final String MIDDLE_NAME = "middleName";
    private static final String MISSING_CITY = "missingCity";
    private static final String MISSING_COUNTY = "missingCounty";
    private static final String MISSING_STATE = "missingState";
    private static final String MISSING_COUNTRY = "missingCountry";
    private static final String MISSING_DATE = "missingDate";
    private static final String AGE = "age";
    private static final String IN_MONTH = "inMonth";
    private static final String IN_DAY = "inDay";
    private static final String APPROX_AGE = "approxAge";
    private static final String HAS_THUMBNAIL = "hasThumbnail";
    private static final String HAS_POSTER = "hasPoster";
    private static final String THUMBNAIL_URL = "thumbnailUrl";
    private static final String CASE_TYPE = "caseType";
    private static final String POSTER_TITLE = "posterTitle";
    private static final String RACE = "race";
    private static final String HAS_AGED_PHOTO = "hasAgedPhoto";
    private static final String HAS_EXTRA_PHOTO = "hasExtraPhoto";
    private static final String POSSIBLE_LOCATION = "possibleLocation";
    private static final String SEX = "sex";
    private static final String BIRTH_DATE = "birthDate";
    private static final String HEIGHT = "height";
    private static final String HEIGHT_IN_INCH = "heightInInch";
    private static final String WEIGHT = "weight";
    private static final String WEIGHT_IN_POUND = "weightInPound";
    private static final String EYE_COLOR = "eyeColor";
    private static final String HAIR_COLOR = "hairColor";
    private static final String HAS_PHOTO = "hasPhoto";
    private static final String MISSING_PROVINCE = "missingProvince";
    private static final String CIRCUMSTANCE = "circumstance";
    private static final String PROFILE_NARRATIVE = "profileNarrative";
    private static final String ORG_CONTACT_INFO = "orgContactInfo";
    private static final String ORG_LOGO = "orgLogo";
    private static final String IS_CLEARINGHOUSE = "isClearinghouse";
    private static final String REP_SIGHT_URL = "repSightURL";
    private static final String ALT_CONTACT = "altContact";
    private static final String PHOTO_MAP = "photoMap";

    private static DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");

    public static List<ChildData> getChildDataListFromJsonArray(JSONArray dataJson)
            throws JSONException {
        ArrayList<ChildData> dataList = new ArrayList<>(dataJson.length());
        for (int i = 0; i < dataJson.length(); i++) {
            JSONObject childDataJson = dataJson.getJSONObject(i);
            dataList.add(parseChildDataFromJson(childDataJson));
        }
        return dataList;
    }

    public static ChildData parseChildDataFromJson(JSONObject childJson) throws JSONException {
        String caseNumber = childJson.getString(CASE_NUMBER);
        String orgPrefix = childJson.getString(ORG_PREFIX);
        String orgName = childJson.getString(ORG_NAME);
        boolean isChild = childJson.getBoolean(IS_CHILD);
        int seqNumber = childJson.getInt(SEQ_NUMBER);
        String langId = childJson.getString(LANG_ID);
        String firstName = childJson.getString(FIRST_NAME);
        String lastName = childJson.getString(LAST_NAME);
        String middleName = childJson.getString(MIDDLE_NAME);
        String missingCity = childJson.getString(MISSING_CITY);
        String missingCounty = childJson.getString(MISSING_COUNTY);
        String missingState = childJson.getString(MISSING_STATE);
        String missingCountry = childJson.getString(MISSING_COUNTRY);
        String missingDateStr = childJson.getString(MISSING_DATE);
        Date missingDate = null;
        try {
            missingDate = formatter.parse(missingDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int age = childJson.getInt(AGE);
        boolean inMonth = childJson.getBoolean(IN_MONTH);
        boolean inDay = childJson.getBoolean(IN_DAY);
        String approxAge = childJson.getString(APPROX_AGE);
        boolean hasThumbnail = childJson.getBoolean(HAS_THUMBNAIL);
        boolean hasPoster = childJson.getBoolean(HAS_POSTER);
        String thumbnailUrl = childJson.getString(THUMBNAIL_URL);
        String caseType = childJson.getString(CASE_TYPE);
        String posterTitle = childJson.getString(POSTER_TITLE);
        String race = childJson.getString(RACE);
        return new ChildData(caseNumber, orgPrefix, orgName, isChild, seqNumber,
                langId, firstName, lastName, middleName, missingCity, missingCounty, missingState,
                missingCountry, missingDate, age, inMonth, inDay, approxAge, hasThumbnail,
                hasPoster, thumbnailUrl, caseType, posterTitle, race);
    }

    public static ChildData parseDetailDataForChild(JSONObject childJson, ChildData childData)
            throws JSONException {
        boolean hasAgedPhoto = childJson.getBoolean(HAS_AGED_PHOTO);
        boolean hasExtraPhoto = childJson.getBoolean(HAS_EXTRA_PHOTO);
        String possibleLocation = childJson.getString(POSSIBLE_LOCATION);
        String sex = childJson.getString(SEX);
        String birthDateStr = childJson.getString(BIRTH_DATE);
        Date birthDate = null;
        try {
            birthDate = formatter.parse(birthDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int height = childJson.getInt(HEIGHT);
        boolean heightInInch = childJson.getBoolean(HEIGHT_IN_INCH);
        int weight = childJson.getInt(WEIGHT);
        boolean weightInPound = childJson.getBoolean(WEIGHT_IN_POUND);
        String eyeColor = childJson.getString(EYE_COLOR);
        String hairColor = childJson.getString(HAIR_COLOR);
        boolean hasPhoto = childJson.getBoolean(HAS_PHOTO);
        String missingProvince = childJson.getString(MISSING_PROVINCE);
        String circumstance = childJson.getString(CIRCUMSTANCE);
        String profileNarrative = childJson.getString(PROFILE_NARRATIVE);
        String orgContactInfo = childJson.getString(ORG_CONTACT_INFO);
        String orgLogo = childJson.getString(ORG_LOGO);
        boolean isClearinghouse = childJson.getBoolean(IS_CLEARINGHOUSE);
        String repSightURL = childJson.getString(REP_SIGHT_URL);
        String altContact = childJson.getString(ALT_CONTACT);
        String photoMap = childJson.getString(PHOTO_MAP);
        childData.addDetailData(hasAgedPhoto, hasExtraPhoto, possibleLocation, sex, birthDate,
                height, heightInInch, weight, weightInPound, eyeColor, hairColor, hasPhoto,
                missingProvince, circumstance, profileNarrative, orgContactInfo, orgLogo,
                isClearinghouse, repSightURL, altContact, photoMap);
        return childData;
    }
}