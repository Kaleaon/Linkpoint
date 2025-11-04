// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors.internal;

public class GyroscopeBiasEstimator
{
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
    
    public GyroscopeBiasEstimator() {
        this.reset();
    }
    
    private void updateGyroBias(final Vector3d vector3d, final long n) {
        if (vector3d.length() >= 0.3499999940395355) {
            return;
        }
        final double max = Math.max(0.0, 1.0 - vector3d.length() / 0.3499999940395355);
        this.gyroBiasLowPass.addWeightedSample(this.gyroLowPass.getFilteredData(), n, max * max);
    }
    
    public void getGyroBias(final Vector3d vector3d) {
        if (this.gyroBiasLowPass.getNumSamples() >= 30) {
            vector3d.set(this.gyroBiasLowPass.getFilteredData());
            vector3d.scale(Math.min(1.0, (this.gyroBiasLowPass.getNumSamples() - 30) / 100.0));
        }
        else {
            vector3d.setZero();
        }
    }
    
    public void processAccelerometer(final Vector3d vector3d, final long n) {
        this.accelLowPass.addSample(vector3d, n);
        Vector3d.sub(vector3d, this.accelLowPass.getFilteredData(), this.smoothedAccelDiff);
        this.isAccelStatic.appendFrame(this.smoothedAccelDiff.length() < 0.5);
    }
    
    public void processGyroscope(final Vector3d vector3d, final long n) {
        boolean b = false;
        this.gyroLowPass.addSample(vector3d, n);
        Vector3d.sub(vector3d, this.gyroLowPass.getFilteredData(), this.smoothedGyroDiff);
        final IsStaticCounter isGyroStatic = this.isGyroStatic;
        if (this.smoothedGyroDiff.length() < 0.00800000037997961) {
            b = true;
        }
        isGyroStatic.appendFrame(b);
        if (this.isGyroStatic.isRecentlyStatic() && this.isAccelStatic.isRecentlyStatic()) {
            this.updateGyroBias(vector3d, n);
        }
    }
    
    public void reset() {
        this.smoothedGyroDiff = new Vector3d();
        this.smoothedAccelDiff = new Vector3d();
        this.accelLowPass = new LowPassFilter(1.0);
        this.gyroLowPass = new LowPassFilter(10.0);
        this.gyroBiasLowPass = new LowPassFilter(0.15000000596046448);
        this.isAccelStatic = new IsStaticCounter(10);
        this.isGyroStatic = new IsStaticCounter(10);
    }
    
    private static class IsStaticCounter
    {
        private int consecutiveIsStatic;
        private final int minStaticFrames;
        
        IsStaticCounter(final int minStaticFrames) {
            this.minStaticFrames = minStaticFrames;
        }
        
        void appendFrame(final boolean b) {
            if (b) {
                ++this.consecutiveIsStatic;
            }
            else {
                this.consecutiveIsStatic = 0;
            }
        }
        
        boolean isRecentlyStatic() {
            return this.consecutiveIsStatic >= this.minStaticFrames;
        }
    }
}
