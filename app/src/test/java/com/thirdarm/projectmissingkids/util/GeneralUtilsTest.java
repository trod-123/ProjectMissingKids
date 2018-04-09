package com.thirdarm.projectmissingkids.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by TROD on 20180408.
 */

public class GeneralUtilsTest {
    @Test
    public void testApproxAgeRangeConversionToInt() throws Exception {
        String ageRange = "15-25";
        Tuple<Integer, Integer> ageRangeTuple = GeneralUtils.convertStringNumberRangeToInts(ageRange);

        assertEquals(15, (int) ageRangeTuple.x);
        assertEquals(25, (int) ageRangeTuple.y);
    }
}
