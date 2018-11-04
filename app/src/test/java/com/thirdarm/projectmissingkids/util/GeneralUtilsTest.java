package com.thirdarm.projectmissingkids.util;

import android.util.Pair;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by TROD on 20180408.
 */

public class GeneralUtilsTest {
    @Test
    public void testApproxAgeRangeConversionToInt() throws Exception {
        String ageRange = "15-25";
        Pair<Integer, Integer> ageRangeTuple = GeneralUtils.convertStringNumberRangeToInts(ageRange);

        assertEquals(15, (int) ageRangeTuple.first);
        assertEquals(25, (int) ageRangeTuple.second);
    }
}
