package com.google.vr.sdk.base.sensors.internal;

public class GyroscopeBiasEstimator {
    private static final float ACCEL_DIFF_STATIC_THRESHOLD = 0.5f;
    private static final float ACCEL_LOWPASS_FREQ = 1.0f;
    private static final float GYRO_BIAS_LOWPASS_FREQ = 0.15f;
    private static final float GYRO_DIFF_STATIC_THRESHOLD = 0.008f;
    private static final float GYRO_FOR_BIAS_THRESHOLD = 0.35f;
    private static final float GYRO_LOWPASS_FREQ = 10.0f;
    private static final int IS_STATIC_NUM_FRAMES_THRESHOLD = 10;
    private static final int NUM_GYRO_BIAS_SAMPLES_INITIAL_SMOOTHING = 100;
    private static final int NUM_GYRO_BIAS_SAMPLES_THRESHOLD = 30;
    private LowPassFilter accelLowPass;
    private LowPassFilter gyroBiasLowPass;
    private LowPassFilter gyroLowPass;
    private IsStaticCounter isAccelStatic;
    private IsStaticCounter isGyroStatic;
    private Vector3d smoothedAccelDiff;
    private Vector3d smoothedGyroDiff;

    private static class IsStaticCounter {
        private int consecutiveIsStatic;
        private final int minStaticFrames;

        IsStaticCounter(int i) {
            this.minStaticFrames = i;
        }

        /* access modifiers changed from: package-private */
        public void appendFrame(boolean z) {
            if (z) {
                this.consecutiveIsStatic++;
            } else {
                this.consecutiveIsStatic = 0;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isRecentlyStatic() {
            return this.consecutiveIsStatic >= this.minStaticFrames;
        }
    }

    public GyroscopeBiasEstimator() {
        reset();
    }

    private void updateGyroBias(Vector3d vector3d, long j) {
        if (vector3d.length() < 0.3499999940395355d) {
            double max = Math.max(0.0d, 1.0d - (vector3d.length() / 0.3499999940395355d));
            this.gyroBiasLowPass.addWeightedSample(this.gyroLowPass.getFilteredData(), j, max * max);
        }
    }

    public void getGyroBias(Vector3d vector3d) {
        if (this.gyroBiasLowPass.getNumSamples() >= 30) {
            vector3d.set(this.gyroBiasLowPass.getFilteredData());
            vector3d.scale(Math.min(1.0d, ((double) (this.gyroBiasLowPass.getNumSamples() - 30)) / 100.0d));
            return;
        }
        vector3d.setZero();
    }

    public void processAccelerometer(Vector3d vector3d, long j) {
        this.accelLowPass.addSample(vector3d, j);
        Vector3d.sub(vector3d, this.accelLowPass.getFilteredData(), this.smoothedAccelDiff);
        this.isAccelStatic.appendFrame(this.smoothedAccelDiff.length() < 0.5d);
    }

    public void processGyroscope(Vector3d vector3d, long j) {
        boolean z = false;
        this.gyroLowPass.addSample(vector3d, j);
        Vector3d.sub(vector3d, this.gyroLowPass.getFilteredData(), this.smoothedGyroDiff);
        IsStaticCounter isStaticCounter = this.isGyroStatic;
        if (this.smoothedGyroDiff.length() < 0.00800000037997961d) {
            z = true;
        }
        isStaticCounter.appendFrame(z);
        if (this.isGyroStatic.isRecentlyStatic() && this.isAccelStatic.isRecentlyStatic()) {
            updateGyroBias(vector3d, j);
        }
    }

    public void reset() {
        this.smoothedGyroDiff = new Vector3d();
        this.smoothedAccelDiff = new Vector3d();
        this.accelLowPass = new LowPassFilter(1.0d);
        this.gyroLowPass = new LowPassFilter(10.0d);
        this.gyroBiasLowPass = new LowPassFilter(0.15000000596046448d);
        this.isAccelStatic = new IsStaticCounter(10);
        this.isGyroStatic = new IsStaticCounter(10);
    }
}
