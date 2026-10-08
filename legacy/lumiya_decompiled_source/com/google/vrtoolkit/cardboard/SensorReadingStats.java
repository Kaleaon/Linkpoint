package com.google.vrtoolkit.cardboard;

import java.lang.reflect.Array;
/* loaded from: classes.dex */
class SensorReadingStats {
    private static final String TAG = SensorReadingStats.class.getSimpleName();
    private int numAxes;
    private float[][] sampleBuf;
    private int sampleBufSize;
    private int samplesAdded;
    private int writePos;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SensorReadingStats(int i, int i2) {
        this.sampleBufSize = i;
        this.numAxes = i2;
        if (i <= 0) {
            throw new IllegalArgumentException("sampleBufSize is invalid.");
        }
        if (i2 <= 0) {
            throw new IllegalArgumentException("numAxes is invalid.");
        }
        this.sampleBuf = (float[][]) Array.newInstance(Float.TYPE, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addSample(float[] fArr) {
        if (fArr.length < this.numAxes) {
            throw new IllegalArgumentException("values.length is less than # of axes.");
        }
        this.writePos = (this.writePos + 1) % this.sampleBufSize;
        for (int i = 0; i < this.numAxes; i++) {
            this.sampleBuf[this.writePos][i] = fArr[i];
        }
        this.samplesAdded++;
    }

    float getAverage(int i) {
        if (statsAvailable()) {
            if (i < 0 || i >= this.numAxes) {
                throw new IllegalStateException(new StringBuilder(38).append("axis must be between 0 and ").append(this.numAxes - 1).toString());
            }
            float f = 0.0f;
            for (int i2 = 0; i2 < this.sampleBufSize; i2++) {
                f += this.sampleBuf[i2][i];
            }
            return f / this.sampleBufSize;
        }
        throw new IllegalStateException("Average not available. Not enough samples.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getMaxAbsoluteDeviation() {
        float f = 0.0f;
        for (int i = 0; i < this.numAxes; i++) {
            f = Math.max(f, getMaxAbsoluteDeviation(i));
        }
        return f;
    }

    float getMaxAbsoluteDeviation(int i) {
        if (i < 0 || i >= this.numAxes) {
            throw new IllegalStateException(new StringBuilder(38).append("axis must be between 0 and ").append(this.numAxes - 1).toString());
        }
        float average = getAverage(i);
        float f = 0.0f;
        for (int i2 = 0; i2 < this.sampleBufSize; i2++) {
            f = Math.max(Math.abs(this.sampleBuf[i2][i] - average), f);
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        this.samplesAdded = 0;
        this.writePos = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean statsAvailable() {
        return this.samplesAdded >= this.sampleBufSize;
    }
}
