package com.thirdarm.projectmissingkids.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class of general utility methods
 */
public class GeneralUtils {

    /**
     * Helper method to extract the lower and upper range of a given number range provided as a String.
     * @param numberRange Range must be in the form "[lower]-[upper]" (e.g. "12-22")
     * @return A Tuple of ints, with X = lower, and Y = upper
     */
    public static Tuple<Integer, Integer> convertStringNumberRangeToInts(String numberRange) {
        List<String> range = Arrays.asList(numberRange.trim().split("-"));
        int[] values = new int[2];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(range.get(i));
        }
        return new Tuple<>(values[0], values[1]);
    }
}
