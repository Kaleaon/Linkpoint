// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors.internal;

import java.util.concurrent.TimeUnit;

public class LowPassFilter
{
    private static final double NANOS_TO_SECONDS;
    private final Vector3d filteredData;
    private long lastTimestampNs;
    private int numSamples;
    private final Vector3d temp;
    private final double timeConstantSecs;
    
    static {
        NANOS_TO_SECONDS = 1.0 / TimeUnit.NANOSECONDS.convert(1L, TimeUnit.SECONDS);
    }
    
    public LowPassFilter(final double n) {
        this.filteredData = new Vector3d();
        this.temp = new Vector3d();
        this.timeConstantSecs = 1.0 / (6.283185307179586 * n);
    }
    
    public void addSample(final Vector3d vector3d, final long n) {
        this.addWeightedSample(vector3d, n, 1.0);
    }
    
    public void addWeightedSample(final Vector3d vector3d, final long n, double n2) {
        ++this.numSamples;
        if (this.numSamples != 1) {
            n2 = (n - this.lastTimestampNs) * n2 * LowPassFilter.NANOS_TO_SECONDS;
            n2 /= this.timeConstantSecs + n2;
            this.filteredData.scale(1.0 - n2);
            this.temp.set(vector3d);
            this.temp.scale(n2);
            Vector3d.add(this.temp, this.filteredData, this.filteredData);
            this.lastTimestampNs = n;
            return;
        }
        this.filteredData.set(vector3d);
        this.lastTimestampNs = n;
    }
    
    public Vector3d getFilteredData() {
        return this.filteredData;
    }
    
    public int getNumSamples() {
        return this.numSamples;
    }
}
