package com.thirdarm.projectmissingkids.util;

import android.text.TextUtils;
import android.util.Log;

import com.thirdarm.projectmissingkids.data.MissingKid;

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

    private static final String TAG = DataParsingUtils.class.getSimpleName();

    // JSON field names
    private static final String CASE_NUMBER = "caseNumber";
    private static final String ORG_PREFIX = "orgPrefix";
    private static final String ORG_NAME = "orgName";
    private static final String IS_MissingKid = "isMissingKid";
    private static final String SEQ_NUMBER = "seqNumber";
    private static final String LANG_ID = "langId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
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

    /**
     * Converts a JSONArray returned by NetworkUtils.getSearchResultsDataJsonArray()
     * into a List of MissingKid objects.
     *
     * @param dataJson a JSONArray of MissingKid data
     * @return a List of MissingKid objects parsed from the JSONArray
     */
    public static List<MissingKid> getMissingKidListFromJsonArray(JSONArray dataJson) {
        ArrayList<MissingKid> dataList = new ArrayList<>(dataJson.length());
        for (int i = 0; i < dataJson.length(); i++) {
            try {
                JSONObject missingKidJson = dataJson.getJSONObject(i);
                dataList.add(parseMissingKidDataFromJson(missingKidJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    /**
     * Parses a single JSONObject of MissingKid data from a search into a MissingKid object.
     *
     * @param missingKidJson the JSONObject to be parsed
     * @return the resulting missingKid object
     * @throws JSONException if there was a problem parsing the data
     */
    private static MissingKid parseMissingKidDataFromJson(JSONObject missingKidJson) throws JSONException {
        String caseNumber = missingKidJson.getString(CASE_NUMBER);
        String orgPrefix = missingKidJson.getString(ORG_PREFIX);
        String orgName = missingKidJson.optString(ORG_NAME);
        boolean isMissingKid = missingKidJson.optBoolean(IS_MissingKid);
        int seqNumber = missingKidJson.optInt(SEQ_NUMBER);
        String langId = missingKidJson.optString(LANG_ID);
        String firstName = missingKidJson.optString(FIRST_NAME);
        String lastName = missingKidJson.optString(LAST_NAME);
        String middleName = missingKidJson.optString(MIDDLE_NAME);
        String missingCity = missingKidJson.optString(MISSING_CITY);
        String missingCounty = missingKidJson.optString(MISSING_COUNTY);
        String missingState = missingKidJson.optString(MISSING_STATE);
        String missingCountry = missingKidJson.optString(MISSING_COUNTRY);
        String missingDateStr = missingKidJson.optString(MISSING_DATE);
        Date missingDate = null;
        try {
            if (!TextUtils.isEmpty(missingDateStr)) {
                missingDate = formatter.parse(missingDateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int age = missingKidJson.optInt(AGE);
        boolean inMonth = missingKidJson.optBoolean(IN_MONTH);
        boolean inDay = missingKidJson.optBoolean(IN_DAY);
        String approxAge = missingKidJson.optString(APPROX_AGE);
        boolean hasThumbnail = missingKidJson.optBoolean(HAS_THUMBNAIL);
        boolean hasPoster = missingKidJson.optBoolean(HAS_POSTER);
        String thumbnailUrl = missingKidJson.optString(THUMBNAIL_URL);
        String caseType = missingKidJson.optString(CASE_TYPE);
        String posterTitle = missingKidJson.optString(POSTER_TITLE);
        String race = missingKidJson.optString(RACE);
        return new MissingKid(caseNumber, orgPrefix, orgName, isMissingKid, seqNumber,
                langId, firstName, lastName, middleName, missingCity, missingCounty, missingState,
                missingCountry, missingDate, age, inMonth, inDay, approxAge, hasThumbnail,
                hasPoster, thumbnailUrl, caseType, posterTitle, race);
    }

    /**
     * Parses the detail data for a MissingKid and adds it to the missingKid object for that MissingKid
     *
     * @param missingKidJson the JSONObject containing the detail data to be parsed
     * @param missingKid the missingKid object to add the detail data to
     * @return the missingKid with the added detail data
     */
    public static MissingKid parseDetailDataForMissingKid(JSONObject missingKidJson, MissingKid missingKid) {
        Log.d(TAG, "missingKidJson: " + missingKidJson.toString());
        // TODO check to see if the detail data is for the same case number as the missingKid
        boolean hasAgedPhoto = missingKidJson.optBoolean(HAS_AGED_PHOTO);
        boolean hasExtraPhoto = missingKidJson.optBoolean(HAS_EXTRA_PHOTO);
        String possibleLocation = missingKidJson.optString(POSSIBLE_LOCATION);
        String sex = missingKidJson.optString(SEX);
        String race = missingKidJson.optString(RACE);
        String birthDateStr = missingKidJson.optString(BIRTH_DATE);
        Date birthDate = null;
        try {
            if (!TextUtils.isEmpty(birthDateStr)) {
                birthDate = formatter.parse(birthDateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int height = missingKidJson.optInt(HEIGHT);
        boolean heightInInch = missingKidJson.optBoolean(HEIGHT_IN_INCH);
        int weight = missingKidJson.optInt(WEIGHT);
        boolean weightInPound = missingKidJson.optBoolean(WEIGHT_IN_POUND);
        String eyeColor = missingKidJson.optString(EYE_COLOR);
        String hairColor = missingKidJson.optString(HAIR_COLOR);
        boolean hasPhoto = missingKidJson.optBoolean(HAS_PHOTO);
        String missingProvince = missingKidJson.optString(MISSING_PROVINCE);
        String circumstance = missingKidJson.optString(CIRCUMSTANCE);
        String profileNarrative = missingKidJson.optString(PROFILE_NARRATIVE);
        String orgContactInfo = missingKidJson.optString(ORG_CONTACT_INFO);
        String orgLogo = missingKidJson.optString(ORG_LOGO);
        boolean isClearinghouse = missingKidJson.optBoolean(IS_CLEARINGHOUSE);
        String repSightURL = missingKidJson.optString(REP_SIGHT_URL);
        String altContact = missingKidJson.optString(ALT_CONTACT);
        String photoMap = missingKidJson.optString(PHOTO_MAP);
        missingKid.addDetailData(hasAgedPhoto, hasExtraPhoto, possibleLocation, sex, race, birthDate,
                height, heightInInch, weight, weightInPound, eyeColor, hairColor, hasPhoto,
                missingProvince, circumstance, profileNarrative, orgContactInfo, orgLogo,
                isClearinghouse, repSightURL, altContact, photoMap);
        return missingKid;
    }
}
