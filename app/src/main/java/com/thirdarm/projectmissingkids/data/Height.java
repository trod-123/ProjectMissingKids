package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;

public class Height {
    /**
     *  Child's height, in imperial units (inches) (double)
     */
    @ColumnInfo(name = "height_imperial")
    public double heightImperial;

    /**
     *  Child's height, in metric units (meters) (double)
     */
    @ColumnInfo(name = "height_metric")
    public double heightMetric;

    /**
     *  Estimated height, in imperial units (inches): lower bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_imperial_lower")
    public double estHeightImperialLower;

    /**
     *  Estimated height, in imperial units (inches): upper bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_imperial_higher")
    public double estHeightImperialHigher;

    /**
     *  Estimated height, in metric units (meters): lower bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_metric_lower")
    public double estHeightMetricLower;

    /**
     *  Estimated height, in metric units (meters): upper bound (double)
     *  Only provided for specific statuses (e.g. Unidentified Child)
     */
    @ColumnInfo(name = "est_height_metric_higher")
    public double estHeightMetricHigher;
}
