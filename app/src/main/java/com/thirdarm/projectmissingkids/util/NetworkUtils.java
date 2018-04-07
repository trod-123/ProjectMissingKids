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
    private static final String JSON_ACTION_DETAIL_VALUE = "childSearch";
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

    private static boolean sInitialized = false;

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

    public static URL buildJsonDataSearchPageUrl(int pageNumber) {
        Uri jsonUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(JSON_PATH)
                .appendQueryParameter(JSON_ACTION_PARAM, JSON_ACTION_SEARCH_VALUE)
                .appendQueryParameter(JSON_PAGE_PARAM, String.valueOf(pageNumber))
                .appendQueryParameter(STATE_PARAM, STATE_VALUE)
                .build();
        return convertUriToURL(jsonUri);
    }

    public static URL buildJsonDataDetailUrl(int caseNumber, String orgPrefix) {
        Uri jsonUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(JSON_PATH)
                .appendQueryParameter(JSON_ACTION_PARAM, JSON_ACTION_DETAIL_VALUE)
                .appendQueryParameter(JSON_CASE_NUMBER_PARAM, String.valueOf(caseNumber))
                .appendQueryParameter(JSON_ORG_PREFIX_PARAM, orgPrefix)
                .build();
        return convertUriToURL(jsonUri);
    }

    public static URL buildHtmlDataUrl() {
        Uri htmlUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(HTML_PATH)
                .appendQueryParameter(HTML_ACTION_PARAM, HTML_ACTION_VALUE)
                .appendQueryParameter(STATE_PARAM, STATE_VALUE)
                .build();
        return convertUriToURL(htmlUri);
    }

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

    public static JSONArray getSearchResultsDataJsonArray() throws JSONException {
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
