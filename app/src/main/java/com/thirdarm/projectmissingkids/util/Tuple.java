package com.thirdarm.projectmissingkids.util;

/**
 * Custom tuple class
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
