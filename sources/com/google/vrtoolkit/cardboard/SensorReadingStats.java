package com.google.vrtoolkit.cardboard;

import java.lang.reflect.Array;

class SensorReadingStats {
    private static final String TAG = SensorReadingStats.class.getSimpleName();
    private int numAxes;
    private float[][] sampleBuf;
    private int sampleBufSize;
    private int samplesAdded;
    private int writePos;

    SensorReadingStats(int i, int i2) {
        this.sampleBufSize = i;
        this.numAxes = i2;
        if (i <= 0) {
            throw new IllegalArgumentException("sampleBufSize is invalid.");
        } else if (i2 > 0) {
            this.sampleBuf = (float[][]) Array.newInstance(Float.TYPE, new int[]{i, i2});
        } else {
            throw new IllegalArgumentException("numAxes is invalid.");
        }
    }

    /* access modifiers changed from: package-private */
    public void addSample(float[] fArr) {
        if (fArr.length >= this.numAxes) {
            this.writePos = (this.writePos + 1) % this.sampleBufSize;
            for (int i = 0; i < this.numAxes; i++) {
                this.sampleBuf[this.writePos][i] = fArr[i];
            }
            this.samplesAdded++;
            return;
        }
        throw new IllegalArgumentException("values.length is less than # of axes.");
    }

    /* access modifiers changed from: package-private */
    public float getAverage(int i) {
        if (!statsAvailable()) {
            throw new IllegalStateException("Average not available. Not enough samples.");
        } else if (i >= 0 && i < this.numAxes) {
            float f = 0.0f;
            for (int i2 = 0; i2 < this.sampleBufSize; i2++) {
                f += this.sampleBuf[i2][i];
            }
            return f / ((float) this.sampleBufSize);
        } else {
            throw new IllegalStateException(new StringBuilder(38).append("axis must be between 0 and ").append(this.numAxes - 1).toString());
        }
    }

    /* access modifiers changed from: package-private */
    public float getMaxAbsoluteDeviation() {
        float f = 0.0f;
        for (int i = 0; i < this.numAxes; i++) {
            f = Math.max(f, getMaxAbsoluteDeviation(i));
        }
        return f;
    }

    /* access modifiers changed from: package-private */
    public float getMaxAbsoluteDeviation(int i) {
        if (i >= 0 && i < this.numAxes) {
            float average = getAverage(i);
            float f = 0.0f;
            for (int i2 = 0; i2 < this.sampleBufSize; i2++) {
                f = Math.max(Math.abs(this.sampleBuf[i2][i] - average), f);
            }
            return f;
        }
        throw new IllegalStateException(new StringBuilder(38).append("axis must be between 0 and ").append(this.numAxes - 1).toString());
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.samplesAdded = 0;
        this.writePos = 0;
    }

    /* access modifiers changed from: package-private */
    public boolean statsAvailable() {
        return this.samplesAdded >= this.sampleBufSize;
    }
}
