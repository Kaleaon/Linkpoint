package com.google.vr.sdk.base.sensors.internal;

import java.util.concurrent.TimeUnit;

public class LowPassFilter {
    private static final double NANOS_TO_SECONDS = (1.0d / ((double) TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS)));
    private final Vector3d filteredData = new Vector3d();
    private long lastTimestampNs;
    private int numSamples;
    private final Vector3d temp = new Vector3d();
    private final double timeConstantSecs;

    public LowPassFilter(double d) {
        this.timeConstantSecs = 1.0d / (6.283185307179586d * d);
    }

    public void addSample(Vector3d vector3d, long j) {
        addWeightedSample(vector3d, j, 1.0d);
    }

    public void addWeightedSample(Vector3d vector3d, long j, double d) {
        this.numSamples++;
        if (this.numSamples != 1) {
            double d2 = ((double) (j - this.lastTimestampNs)) * d * NANOS_TO_SECONDS;
            double d3 = d2 / (this.timeConstantSecs + d2);
            this.filteredData.scale(1.0d - d3);
            this.temp.set(vector3d);
            this.temp.scale(d3);
            Vector3d.add(this.temp, this.filteredData, this.filteredData);
            this.lastTimestampNs = j;
            return;
        }
        this.filteredData.set(vector3d);
        this.lastTimestampNs = j;
    }

    public Vector3d getFilteredData() {
        return this.filteredData;
    }

    public int getNumSamples() {
        return this.numSamples;
    }
}
