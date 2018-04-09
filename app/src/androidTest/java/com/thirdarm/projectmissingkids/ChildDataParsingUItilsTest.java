package com.thirdarm.projectmissingkids;

import android.support.test.runner.AndroidJUnit4;

import com.thirdarm.projectmissingkids.data.ChildData;
import com.thirdarm.projectmissingkids.util.DataParsingUtils;
import com.thirdarm.projectmissingkids.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ChildDataParsingUItilsTest {
    private JSONArray results;

    @Before
    public void getJsonFromJsonUrl() {
        results = null;
        try {
            results = NetworkUtils.getSearchResultsDataJsonArray();
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException thrown when getting the JSON array");
        }
        assertNotNull(results);
    }

    @Test
    public void getChildDataListFromJsonArray() {
        List<ChildData> data = null;
        try {
            data = DataParsingUtils.getChildDataListFromJsonArray(results);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException thrown when getting the Child Data list");
        }
        assertNotNull(data);
    }
}
