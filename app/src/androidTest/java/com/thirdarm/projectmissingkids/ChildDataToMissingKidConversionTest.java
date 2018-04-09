package com.thirdarm.projectmissingkids;

import android.support.test.runner.AndroidJUnit4;

import com.thirdarm.projectmissingkids.data.ChildData;
import com.thirdarm.projectmissingkids.data.MissingKid;
import com.thirdarm.projectmissingkids.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.thirdarm.projectmissingkids.util.DataParsingUtils.parseChildDataFromJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by TROD on 20180408.
 */
@RunWith(AndroidJUnit4.class)
public class ChildDataToMissingKidConversionTest {
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
    public void testChildDataToMissingKidConversion() throws Exception {
        ChildData data = getFirstChildDataFromJsonArray();
        MissingKid kid = MissingKid.convertFromPartialChildData(data);

        assertEquals(data.getFirstName(), kid.name.firstName);
        assertEquals(data.getThumbnailURL(), kid.originalPhotoUrl);
    }

    private ChildData getFirstChildDataFromJsonArray() {
        ChildData data = null;
        try {
            JSONObject childDataJson = results.getJSONObject(0);
            data = parseChildDataFromJson(childDataJson);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException thrown when getting the Child Data");
        }
        return data;
    }
}
