package com.thirdarm.projectmissingkids.util;

import android.text.TextUtils;
import android.util.Log;

import com.thirdarm.projectmissingkids.Utils.TestUtils;
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

    private static final String TAG = DataParsingUtils.class.getSimpleName();

    // JSON field names
    private static final String CASE_NUMBER = "caseNumber";
    private static final String ORG_PREFIX = "orgPrefix";
    private static final String ORG_NAME = "orgName";
    private static final String IS_CHILD = "isChild";
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
     * Converts a JSONArray of child data returned by NetworkUtils.getSearchResultsDataJsonArray()
     * into a List of ChildData objects.
     *
     * @param dataJson a JSONArray of child data
     * @return a List of ChildData objects parsed from the JSONArray
     */
    public static List<ChildData> getChildDataListFromJsonArray(JSONArray dataJson) {
        ArrayList<ChildData> dataList = new ArrayList<>(dataJson.length());
        for (int i = 0; i < dataJson.length(); i++) {
            try {
                JSONObject childDataJson = dataJson.getJSONObject(i);
                dataList.add(parseChildDataFromJson(childDataJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    /**
     * Parses a single JSONObject of child data from a search into a ChildData object.
     * @param childJson the JSONObject to be parsed
     * @return the resulting ChildData object
     * @throws JSONException if there was a problem parsing the data
     */
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
        String missingDateStr = childJson.optString(MISSING_DATE);
        Date missingDate = null;
        try {
            if (!TextUtils.isEmpty(missingDateStr)) {
                missingDate = formatter.parse(missingDateStr);
            }
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

    /**
     * Parses the detail data for a child and adds it to the ChildData object for that child
     * @param childJson the JSONObject containing the detail data to be parsed
     * @param childData the ChildData object to add the detail data to
     * @return the ChildData with the added detail data
     * @throws JSONException if there was a problem parsing the data
     */
    public static ChildData parseDetailDataForChild(JSONObject childJson, ChildData childData)
            throws JSONException {
        // TODO check to see if the detail data is for the same case number as the ChildData
        boolean hasAgedPhoto = childJson.optBoolean(HAS_AGED_PHOTO);
        boolean hasExtraPhoto = childJson.optBoolean(HAS_EXTRA_PHOTO);
        String possibleLocation = childJson.optString(POSSIBLE_LOCATION);
        String sex = childJson.optString(SEX);
        String birthDateStr = childJson.optString(BIRTH_DATE);
        Date birthDate = null;
        try {
            if (!TextUtils.isEmpty(birthDateStr)) {
                birthDate = formatter.parse(birthDateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int height = childJson.optInt(HEIGHT);
        boolean heightInInch = childJson.optBoolean(HEIGHT_IN_INCH);
        int weight = childJson.optInt(WEIGHT);
        boolean weightInPound = childJson.optBoolean(WEIGHT_IN_POUND);
        String eyeColor = childJson.optString(EYE_COLOR);
        String hairColor = childJson.optString(HAIR_COLOR);
        boolean hasPhoto = childJson.optBoolean(HAS_PHOTO);
        String missingProvince = childJson.optString(MISSING_PROVINCE);
        String circumstance = childJson.optString(CIRCUMSTANCE);
        String profileNarrative = childJson.optString(PROFILE_NARRATIVE);
        String orgContactInfo = childJson.optString(ORG_CONTACT_INFO);
        String orgLogo = childJson.optString(ORG_LOGO);
        boolean isClearinghouse = childJson.optBoolean(IS_CLEARINGHOUSE);
        String repSightURL = childJson.optString(REP_SIGHT_URL);
        String altContact = childJson.optString(ALT_CONTACT);
        String photoMap = childJson.optString(PHOTO_MAP);
        childData.addDetailData(hasAgedPhoto, hasExtraPhoto, possibleLocation, sex, birthDate,
                height, heightInInch, weight, weightInPound, eyeColor, hairColor, hasPhoto,
                missingProvince, circumstance, profileNarrative, orgContactInfo, orgLogo,
                isClearinghouse, repSightURL, altContact, photoMap);
        return childData;
    }
}
