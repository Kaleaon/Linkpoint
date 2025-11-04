// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vrtoolkit.cardboard;

class SensorReadingStats
{
    private static final String TAG;
    private int numAxes;
    private float[][] sampleBuf;
    private int sampleBufSize;
    private int samplesAdded;
    private int writePos;
    
    static {
        TAG = SensorReadingStats.class.getSimpleName();
    }
    
    SensorReadingStats(final int sampleBufSize, final int numAxes) {
        this.sampleBufSize = sampleBufSize;
        this.numAxes = numAxes;
        if (sampleBufSize <= 0) {
            throw new IllegalArgumentException("sampleBufSize is invalid.");
        }
        if (numAxes > 0) {
            this.sampleBuf = new float[sampleBufSize][numAxes];
            return;
        }
        throw new IllegalArgumentException("numAxes is invalid.");
    }
    
    void addSample(final float[] array) {
        if (array.length >= this.numAxes) {
            this.writePos = (this.writePos + 1) % this.sampleBufSize;
            for (int i = 0; i < this.numAxes; ++i) {
                this.sampleBuf[this.writePos][i] = array[i];
            }
            ++this.samplesAdded;
            return;
        }
        throw new IllegalArgumentException("values.length is less than # of axes.");
    }
    
    float getAverage(int numAxes) {
        int i = 0;
        if (!this.statsAvailable()) {
            throw new IllegalStateException("Average not available. Not enough samples.");
        }
        if (numAxes >= 0 && numAxes < this.numAxes) {
            float n = 0.0f;
            while (i < this.sampleBufSize) {
                n += this.sampleBuf[i][numAxes];
                ++i;
            }
            return n / this.sampleBufSize;
        }
        numAxes = this.numAxes;
        throw new IllegalStateException(new StringBuilder(38).append("axis must be between 0 and ").append(numAxes - 1).toString());
    }
    
    float getMaxAbsoluteDeviation() {
        float max = 0.0f;
        for (int i = 0; i < this.numAxes; ++i) {
            max = Math.max(max, this.getMaxAbsoluteDeviation(i));
        }
        return max;
    }
    
    float getMaxAbsoluteDeviation(int numAxes) {
        int i = 0;
        if (numAxes >= 0 && numAxes < this.numAxes) {
            final float average = this.getAverage(numAxes);
            float max = 0.0f;
            while (i < this.sampleBufSize) {
                max = Math.max(Math.abs(this.sampleBuf[i][numAxes] - average), max);
                ++i;
            }
            return max;
        }
        numAxes = this.numAxes;
        throw new IllegalStateException(new StringBuilder(38).append("axis must be between 0 and ").append(numAxes - 1).toString());
    }
    
    void reset() {
        this.samplesAdded = 0;
        this.writePos = 0;
    }
    
    boolean statsAvailable() {
        return this.samplesAdded >= this.sampleBufSize;
    }
}
