package com.thirdarm.projectmissingkids.util;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by sobelman on 4/5/2018.
 */
public class NetworkUtils {

    /* tag for log messages */
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /* base URL for missing kids API */
    private static final String BASE_URL = "https://api.missingkids.org/missingkids/servlet/";
    /* path for HTML data */
    private static final String HTML_PATH = "PubCaseSearchServlet";
    /* path for JSON data, which is paginated */
    private static final String JSON_PATH = "JSONDataServlet";

    /* HTML data request parameter names and values */
    private static final String HTML_ACTION_PARAM = "act";
    private static final String HTML_ACTION_VALUE = "usMapSearch";
    /* JSON data request parameter names and values */
    private static final String JSON_ACTION_PARAM = "action";
    private static final String JSON_ACTION_SEARCH_VALUE = "publicSearch";
    private static final String JSON_ACTION_DETAIL_VALUE = "childDetail";
    private static final String JSON_SEARCH_PARAM = "search";
    private static final String JSON_SEARCH_VALUE = "new";
    private static final String JSON_SUBJ_TO_SEARCH_PARAM = "subjToSearch";
    private static final String JSON_SUBJ_TO_SEARCH_VALUE = "child";
    private static final String JSON_PAGE_PARAM = "goToPage";
    private static final String JSON_CASE_NUMBER_PARAM = "caseNum";
    private static final String JSON_ORG_PREFIX_PARAM = "orgPrefix";
    /* common request parameters and values */
    private static final String STATE_PARAM = "missState";
    private static final String STATE_VALUE = "CA";
    /* JSON result field names */
    private static final String TOTAL_RECORDS = "totalRecords";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String PERSONS = "persons";
    private static final String CHILD_BEAN = "childBean";

    // whether or not we have initialized the cookie manager for use in JSON format searches
    private static boolean sInitialized = false;

    /**
     * Builds the URL for starting a JSON format search. Used by getSearchResultsJsonArray().
     *
     * @return the URL to be used.
     */
    public static URL buildJsonDataBeginSearchUrl() {
        Uri jsonUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(JSON_PATH)
                .appendQueryParameter(JSON_ACTION_PARAM, JSON_ACTION_SEARCH_VALUE)
                .appendQueryParameter(JSON_SEARCH_PARAM, JSON_SEARCH_VALUE)
                .appendQueryParameter(JSON_SUBJ_TO_SEARCH_PARAM, JSON_SUBJ_TO_SEARCH_VALUE)
                .appendQueryParameter(STATE_PARAM, STATE_VALUE)
                .build();
        return convertUriToURL(jsonUri);
    }

    /**
     * Builds a URL for a particular page of JSON format search data. Used by
     * getSearchResultsDataJsonArray().
     *
     * @param pageNumber the page number in the results.
     * @return the URL to be used.
     */
    public static URL buildJsonDataSearchPageUrl(int pageNumber) {
        Uri jsonUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(JSON_PATH)
                .appendQueryParameter(JSON_ACTION_PARAM, JSON_ACTION_SEARCH_VALUE)
                .appendQueryParameter(JSON_PAGE_PARAM, String.valueOf(pageNumber))
                .appendQueryParameter(STATE_PARAM, STATE_VALUE)
                .build();
        return convertUriToURL(jsonUri);
    }

    /**
     * Builds a URL for getting child detail data in JSON format.
     *
     * @param caseNumber the case number to get detail information about.
     * @param orgPrefix the organization prefix for the case (e.g. "NCMC").
     * @return the URL to use.
     */
    public static URL buildJsonDataDetailUrl(String caseNumber, String orgPrefix) {
        Uri jsonUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(JSON_PATH)
                .appendQueryParameter(JSON_ACTION_PARAM, JSON_ACTION_DETAIL_VALUE)
                .appendQueryParameter(JSON_CASE_NUMBER_PARAM, caseNumber)
                .appendQueryParameter(JSON_ORG_PREFIX_PARAM, orgPrefix)
                .build();
        return convertUriToURL(jsonUri);
    }

    /**
     * Builds a URL for getting search results in HTML format.
     *
     * @return the URL to be used.
     */
    public static URL buildHtmlDataUrl() {
        Uri htmlUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(HTML_PATH)
                .appendQueryParameter(HTML_ACTION_PARAM, HTML_ACTION_VALUE)
                .appendQueryParameter(STATE_PARAM, STATE_VALUE)
                .build();
        return convertUriToURL(htmlUri);
    }

    /**
     * Utility method to convert Uris to URLs for use in getResponseFromHttpUrl().
     *
     * @param uri
     * @return URL.
     */
    private static URL convertUriToURL(Uri uri) {
        try {
            URL url = new URL(uri.toString());
            Log.d(TAG, "URL: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all the search results in JSONArray format (array of JSONObjects).
     * Loops through all the result pages and combines all results into one JSONArray
     * that is suitable for parsing into ChildData.
     *
     * @return a JSONArray of child data JSONObjects.
     * @throws JSONException
     */
    public static JSONArray getSearchResultsDataJsonArray() throws JSONException {
        NetworkUtils.getSearchResultsMetadataJson(); // needed to "wake up" the server, or else we get no results
        URL beginSearchURL = buildJsonDataBeginSearchUrl();
        try {
            String beginSearchJsonStr = getResponseFromHttpUrl(beginSearchURL);
            JSONObject searchResultJson = new JSONObject(beginSearchJsonStr);
            if (!searchResultJson.has("status")) {
                return null;
            } else if (!searchResultJson.getString("status").equals("success")) {
                return null;
            }
            int totalRecords = 0;
            if (searchResultJson.has(TOTAL_RECORDS)) {
                totalRecords = searchResultJson.getInt(TOTAL_RECORDS);
            }
            int totalPages = 0;
            if (searchResultJson.has(TOTAL_PAGES)) {
                totalPages = searchResultJson.getInt(TOTAL_PAGES);
            }
            JSONArray personsArray = new JSONArray();
            for (int i = 1; i <= totalPages; i++) {
                URL pageUrl = buildJsonDataSearchPageUrl(i);
                String pageJsonStr = getResponseFromHttpUrl(pageUrl);
                JSONObject pageJson = new JSONObject(pageJsonStr);
                if (pageJson.has(PERSONS)) {
                    JSONArray pagePersons = pageJson.getJSONArray(PERSONS);
                    for (int j = 0; j < pagePersons.length(); j++) {
                        personsArray.put(pagePersons.get(j));
                    }
                }
            }
            // sanity check
            if (personsArray.length() != totalRecords) {
                Log.d(TAG, "Total records is " + totalRecords + " but array length is " +
                        personsArray.length());
            }
            return personsArray;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets metadata about a search such as total records (field name "totalRecords")
     * and total pages (field name "totalPages").
     *
     * @return a JSONObject containing the metadata, or null if no data was available from the
     * server.
     * @throws JSONException if there was a problem parsing the server response.
     */
    public static JSONObject getSearchResultsMetadataJson() throws JSONException {
        URL beginSearchURL = buildJsonDataBeginSearchUrl();
        try {
            String beginSearchJsonStr = getResponseFromHttpUrl(beginSearchURL);
            JSONObject searchResultMetadataJson = new JSONObject(beginSearchJsonStr);
            if (!searchResultMetadataJson.has("status")) {
                return null;
            } else if (!searchResultMetadataJson.getString("status").equals("success")) {
                return null;
            } else {
                return searchResultMetadataJson;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the number of total pages from a search metadata JSONObject.
     *
     * @param metadata the JSONObject containing the metadata
     * @return the number of total pages of search data, or 0 if the data is unavailable.
     */
    public static int getTotalPagesFromMetadata(JSONObject metadata) {
        return metadata.optInt(TOTAL_PAGES);
    }

    /**
     * Retrieves the number of total records from a search metadata JSONObject.
     *
     * @param metadata the JSONObject containing the metadata
     * @return the number of total records in the search data, or 0 if the data is unavailable.
     */
    public static int getTotalRecordsFromMetadata(JSONObject metadata) {
        return metadata.optInt(TOTAL_RECORDS);
    }

    /**
     * Gets one page of search results.
     *
     * @param pageNumber the page number to get.
     * @return a JSONArray containing the given page of search results, or null if the data was
     * unavailable or the server could not be reached.
     * @throws JSONException if there was a problem parsing the server response.
     */
    public static JSONArray getSearchResultPageJsonArray(int pageNumber) throws JSONException {
        NetworkUtils.getSearchResultsMetadataJson(); // needed to "wake up" the server, or else we get no results
        URL pageUrl = buildJsonDataSearchPageUrl(pageNumber);
        String pageJsonStr = null;
        try {
            pageJsonStr = getResponseFromHttpUrl(pageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject pageJson = new JSONObject(pageJsonStr);
        JSONArray pagePersons = null;
        if (pageJson.has(PERSONS)) {
            pagePersons = pageJson.optJSONArray(PERSONS);
        }
        return pagePersons;
    }

    /**
     * Retrieves child detail data for a particular case.
     *
     * @param caseNumber the case number of the child for whom detail data is being
     *                   requested.
     * @param orgPrefix the organization prefix for the organization the case belongs to.
     * @return a JSONObject containing the detail data, suitable for parsing into ChildData.
     * @throws JSONException if there was a problem parsing the server response.
     */
    public static JSONObject getDetailDataJson(String caseNumber, String orgPrefix)
            throws JSONException {
        URL detailURL = buildJsonDataDetailUrl(caseNumber, orgPrefix);
        try {
            String responseStr =  getResponseFromHttpUrl(detailURL);
            JSONObject responseJson = new JSONObject(responseStr);
            String status = responseJson.optString("status");
            if ("success".equals(status)) {
                if (responseJson.has(CHILD_BEAN)) {
                    return responseJson.getJSONObject(CHILD_BEAN);
                } else {
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        // the following makes it so we maintain a session with the missing kids servlet
        if (!sInitialized) {
            CookieHandler.setDefault(new CookieManager());
            sInitialized = true;
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
