package com.thirdarm.projectmissingkids.util;

import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Created by sobelman on 4/6/2018.
 */
public class NetworkUtilsTest {

    @Test
    public void getResponseFromHttpUrl() {
        String jsonUrl = "https://api.missingkids.org/missingkids/servlet/" +
                "JSONDataServlet?action=publicSearch&search=new&missState=CA";
        URL url = null;
        try {
            url = new URL(jsonUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertNotNull(url);
        try {
            String result = NetworkUtils.getResponseFromHttpUrl(url);
            System.out.println(result);
            assertTrue(result.contains("success"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException thrown");
        }

        String htmlUrl = "https://api.missingkids.org/missingkids/servlet/" +
                "PubCaseSearchServlet?act=usMapSearch&missState=CA";
        url = null;
        try {
            url = new URL(htmlUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertNotNull(url);
        try {
            String result = NetworkUtils.getResponseFromHttpUrl(url);
            System.out.println(result);
            assertTrue(result.contains("Records Found:"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException thrown");
        }
    }
}