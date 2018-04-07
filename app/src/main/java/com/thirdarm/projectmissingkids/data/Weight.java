package com.thirdarm.projectmissingkids.data;

import android.arch.persistence.room.ColumnInfo;

public class Weight {
    /**
     * Child's weight, in imperial units (pounds) (double)
     */
    @ColumnInfo(name = "weight_imperial")
    public double weightImperial;

    /**
     * Child's weight, in metric units (kilograms) (double)
     */
    @ColumnInfo(name = "weight_metric")
    public double weightMetric;
}
