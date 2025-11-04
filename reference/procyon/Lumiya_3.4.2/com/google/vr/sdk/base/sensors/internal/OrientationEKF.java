// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors.internal;

public class OrientationEKF
{
    private static final double MAX_ACCEL_NOISE_SIGMA = 7.0;
    private static final double MIN_ACCEL_NOISE_SIGMA = 0.75;
    private static final float NS2S = 1.0E-9f;
    private Matrix3x3d accObservationFunctionForNumericalJacobianTempM;
    private boolean alignedToGravity;
    private boolean alignedToNorth;
    private Vector3d down;
    private float filteredGyroTimestep;
    private Matrix3x3d getPredictedGLMatrixTempM1;
    private Matrix3x3d getPredictedGLMatrixTempM2;
    private Vector3d getPredictedGLMatrixTempV1;
    private boolean gyroFilterValid;
    private final Vector3d lastGyro;
    private Matrix3x3d mH;
    private Matrix3x3d mK;
    private Vector3d mNu;
    private Matrix3x3d mP;
    private Matrix3x3d mQ;
    private Matrix3x3d mR;
    private Matrix3x3d mRaccel;
    private Matrix3x3d mS;
    private Matrix3x3d magObservationFunctionForNumericalJacobianTempM;
    private Vector3d mh;
    private double movingAverageAccelNormChange;
    private Vector3d mu;
    private Vector3d mx;
    private Vector3d mz;
    private Vector3d north;
    private int numGyroTimestepSamples;
    private double previousAccelNorm;
    private Matrix3x3d processAccTempM1;
    private Matrix3x3d processAccTempM2;
    private Matrix3x3d processAccTempM3;
    private Matrix3x3d processAccTempM4;
    private Matrix3x3d processAccTempM5;
    private Vector3d processAccTempV1;
    private Vector3d processAccTempV2;
    private Vector3d processAccVDelta;
    private Matrix3x3d processGyroTempM1;
    private Matrix3x3d processGyroTempM2;
    private Matrix3x3d processMagTempM1;
    private Matrix3x3d processMagTempM2;
    private Matrix3x3d processMagTempM4;
    private Matrix3x3d processMagTempM5;
    private Matrix3x3d processMagTempM6;
    private Vector3d processMagTempV1;
    private Vector3d processMagTempV2;
    private Vector3d processMagTempV3;
    private Vector3d processMagTempV4;
    private Vector3d processMagTempV5;
    private double[] rotationMatrix;
    private long sensorTimeStampGyro;
    private Matrix3x3d setHeadingDegreesTempM1;
    private Matrix3x3d so3LastMotion;
    private Matrix3x3d so3SensorFromWorld;
    private boolean timestepFilterInit;
    private Matrix3x3d updateCovariancesAfterMotionTempM1;
    private Matrix3x3d updateCovariancesAfterMotionTempM2;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!OrientationEKF.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
    }
    
    public OrientationEKF() {
        this.rotationMatrix = new double[16];
        this.so3SensorFromWorld = new Matrix3x3d();
        this.so3LastMotion = new Matrix3x3d();
        this.mP = new Matrix3x3d();
        this.mQ = new Matrix3x3d();
        this.mR = new Matrix3x3d();
        this.mRaccel = new Matrix3x3d();
        this.mS = new Matrix3x3d();
        this.mH = new Matrix3x3d();
        this.mK = new Matrix3x3d();
        this.mNu = new Vector3d();
        this.mz = new Vector3d();
        this.mh = new Vector3d();
        this.mu = new Vector3d();
        this.mx = new Vector3d();
        this.down = new Vector3d();
        this.north = new Vector3d();
        this.lastGyro = new Vector3d();
        this.previousAccelNorm = 0.0;
        this.movingAverageAccelNormChange = 0.0;
        this.timestepFilterInit = false;
        this.gyroFilterValid = true;
        this.getPredictedGLMatrixTempM1 = new Matrix3x3d();
        this.getPredictedGLMatrixTempM2 = new Matrix3x3d();
        this.getPredictedGLMatrixTempV1 = new Vector3d();
        this.setHeadingDegreesTempM1 = new Matrix3x3d();
        this.processGyroTempM1 = new Matrix3x3d();
        this.processGyroTempM2 = new Matrix3x3d();
        this.processAccTempM1 = new Matrix3x3d();
        this.processAccTempM2 = new Matrix3x3d();
        this.processAccTempM3 = new Matrix3x3d();
        this.processAccTempM4 = new Matrix3x3d();
        this.processAccTempM5 = new Matrix3x3d();
        this.processAccTempV1 = new Vector3d();
        this.processAccTempV2 = new Vector3d();
        this.processAccVDelta = new Vector3d();
        this.processMagTempV1 = new Vector3d();
        this.processMagTempV2 = new Vector3d();
        this.processMagTempV3 = new Vector3d();
        this.processMagTempV4 = new Vector3d();
        this.processMagTempV5 = new Vector3d();
        this.processMagTempM1 = new Matrix3x3d();
        this.processMagTempM2 = new Matrix3x3d();
        this.processMagTempM4 = new Matrix3x3d();
        this.processMagTempM5 = new Matrix3x3d();
        this.processMagTempM6 = new Matrix3x3d();
        this.updateCovariancesAfterMotionTempM1 = new Matrix3x3d();
        this.updateCovariancesAfterMotionTempM2 = new Matrix3x3d();
        this.accObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();
        this.magObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();
        this.reset();
    }
    
    private void accObservationFunctionForNumericalJacobian(final Matrix3x3d matrix3x3d, final Vector3d vector3d) {
        Matrix3x3d.mult(matrix3x3d, this.down, this.mh);
        So3Util.sO3FromTwoVec(this.mh, this.mz, this.accObservationFunctionForNumericalJacobianTempM);
        So3Util.muFromSO3(this.accObservationFunctionForNumericalJacobianTempM, vector3d);
    }
    
    public static void arrayAssign(final double[][] array, final Matrix3x3d matrix3x3d) {
        assert 3 == array.length;
        assert 3 == array[0].length;
        assert 3 == array[1].length;
        assert 3 == array[2].length;
        matrix3x3d.set(array[0][0], array[0][1], array[0][2], array[1][0], array[1][1], array[1][2], array[2][0], array[2][1], array[2][2]);
    }
    
    private void filterGyroTimestep(final float filteredGyroTimestep) {
        if (this.timestepFilterInit) {
            this.filteredGyroTimestep = this.filteredGyroTimestep * 0.95f + 0.050000012f * filteredGyroTimestep;
            final int numGyroTimestepSamples = this.numGyroTimestepSamples + 1;
            this.numGyroTimestepSamples = numGyroTimestepSamples;
            if (numGyroTimestepSamples > 10.0f) {
                this.gyroFilterValid = true;
            }
        }
        else {
            this.filteredGyroTimestep = filteredGyroTimestep;
            this.numGyroTimestepSamples = 1;
            this.timestepFilterInit = true;
        }
    }
    
    private double[] glMatrixFromSo3(final Matrix3x3d matrix3x3d) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.rotationMatrix[j * 4 + i] = matrix3x3d.get(i, j);
            }
        }
        final double[] rotationMatrix = this.rotationMatrix;
        final double[] rotationMatrix2 = this.rotationMatrix;
        this.rotationMatrix[11] = 0.0;
        rotationMatrix[3] = (rotationMatrix2[7] = 0.0);
        final double[] rotationMatrix3 = this.rotationMatrix;
        final double[] rotationMatrix4 = this.rotationMatrix;
        this.rotationMatrix[14] = 0.0;
        rotationMatrix3[12] = (rotationMatrix4[13] = 0.0);
        this.rotationMatrix[15] = 1.0;
        return this.rotationMatrix;
    }
    
    private void magObservationFunctionForNumericalJacobian(final Matrix3x3d matrix3x3d, final Vector3d vector3d) {
        Matrix3x3d.mult(matrix3x3d, this.north, this.mh);
        So3Util.sO3FromTwoVec(this.mh, this.mz, this.magObservationFunctionForNumericalJacobianTempM);
        So3Util.muFromSO3(this.magObservationFunctionForNumericalJacobianTempM, vector3d);
    }
    
    private void updateAccelCovariance(double min) {
        final double abs = Math.abs(min - this.previousAccelNorm);
        this.previousAccelNorm = min;
        this.movingAverageAccelNormChange = abs * 0.5 + this.movingAverageAccelNormChange * 0.5;
        min = Math.min(7.0, this.movingAverageAccelNormChange / 0.15 * 6.25 + 0.75);
        this.mRaccel.setSameDiagonal(min * min);
    }
    
    private void updateCovariancesAfterMotion() {
        this.so3LastMotion.transpose(this.updateCovariancesAfterMotionTempM1);
        Matrix3x3d.mult(this.mP, this.updateCovariancesAfterMotionTempM1, this.updateCovariancesAfterMotionTempM2);
        Matrix3x3d.mult(this.so3LastMotion, this.updateCovariancesAfterMotionTempM2, this.mP);
        this.so3LastMotion.setIdentity();
    }
    
    public double[] getGLMatrix() {
        synchronized (this) {
            return this.glMatrixFromSo3(this.so3SensorFromWorld);
        }
    }
    
    public double getHeadingDegrees() {
        synchronized (this) {
            final double value = this.so3SensorFromWorld.get(2, 0);
            final double value2 = this.so3SensorFromWorld.get(2, 1);
            if (Math.sqrt(value * value + value2 * value2) < 0.1) {
                return 0.0;
            }
            double n2;
            final double n = n2 = -90.0 - Math.atan2(value2, value) / 3.141592653589793 * 180.0;
            if (n < 0.0) {
                n2 = n + 360.0;
            }
            double n3 = n2;
            if (n2 >= 360.0) {
                n3 = n2 - 360.0;
            }
            return n3;
        }
    }
    
    public double[] getPredictedGLMatrix(final double n) {
        synchronized (this) {
            final Vector3d getPredictedGLMatrixTempV1 = this.getPredictedGLMatrixTempV1;
            getPredictedGLMatrixTempV1.set(this.lastGyro);
            getPredictedGLMatrixTempV1.scale(-n);
            final Matrix3x3d getPredictedGLMatrixTempM1 = this.getPredictedGLMatrixTempM1;
            So3Util.sO3FromMu(getPredictedGLMatrixTempV1, getPredictedGLMatrixTempM1);
            final Matrix3x3d getPredictedGLMatrixTempM2 = this.getPredictedGLMatrixTempM2;
            Matrix3x3d.mult(getPredictedGLMatrixTempM1, this.so3SensorFromWorld, getPredictedGLMatrixTempM2);
            return this.glMatrixFromSo3(getPredictedGLMatrixTempM2);
        }
    }
    
    public Matrix3x3d getRotationMatrix() {
        synchronized (this) {
            return this.so3SensorFromWorld;
        }
    }
    
    public boolean isAlignedToGravity() {
        synchronized (this) {
            return this.alignedToGravity;
        }
    }
    
    public boolean isAlignedToNorth() {
        synchronized (this) {
            return this.alignedToNorth;
        }
    }
    
    public boolean isReady() {
        synchronized (this) {
            return this.alignedToGravity;
        }
    }
    
    public void processAcc(Vector3d vector3d, final long n) {
        while (true) {
            int n2 = 0;
            while (true) {
                Label_0293: {
                    synchronized (this) {
                        this.mz.set(vector3d);
                        this.updateAccelCovariance(this.mz.length());
                        if (!this.alignedToGravity) {
                            So3Util.sO3FromTwoVec(this.down, this.mz, this.so3SensorFromWorld);
                            this.alignedToGravity = true;
                        }
                        else {
                            this.accObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu);
                            if (n2 < 3) {
                                break Label_0293;
                            }
                            this.mH.transpose(this.processAccTempM3);
                            Matrix3x3d.mult(this.mP, this.processAccTempM3, this.processAccTempM4);
                            Matrix3x3d.mult(this.mH, this.processAccTempM4, this.processAccTempM5);
                            Matrix3x3d.add(this.processAccTempM5, this.mRaccel, this.mS);
                            this.mS.invert(this.processAccTempM3);
                            this.mH.transpose(this.processAccTempM4);
                            Matrix3x3d.mult(this.processAccTempM4, this.processAccTempM3, this.processAccTempM5);
                            Matrix3x3d.mult(this.mP, this.processAccTempM5, this.mK);
                            Matrix3x3d.mult(this.mK, this.mNu, this.mx);
                            Matrix3x3d.mult(this.mK, this.mH, this.processAccTempM3);
                            this.processAccTempM4.setIdentity();
                            this.processAccTempM4.minusEquals(this.processAccTempM3);
                            Matrix3x3d.mult(this.processAccTempM4, this.mP, this.processAccTempM3);
                            this.mP.set(this.processAccTempM3);
                            So3Util.sO3FromMu(this.mx, this.so3LastMotion);
                            Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.so3SensorFromWorld);
                            this.updateCovariancesAfterMotion();
                        }
                        return;
                    }
                }
                vector3d = this.processAccVDelta;
                vector3d.setZero();
                vector3d.setComponent(n2, 1.0E-7);
                So3Util.sO3FromMu(vector3d, this.processAccTempM1);
                Matrix3x3d.mult(this.processAccTempM1, this.so3SensorFromWorld, this.processAccTempM2);
                this.accObservationFunctionForNumericalJacobian(this.processAccTempM2, this.processAccTempV1);
                vector3d = this.processAccTempV1;
                Vector3d.sub(this.mNu, vector3d, this.processAccTempV2);
                this.processAccTempV2.scale(1.0E7);
                this.mH.setColumn(n2, this.processAccTempV2);
                ++n2;
                continue;
            }
        }
    }
    
    public void processGyro(final Vector3d vector3d, final long sensorTimeStampGyro) {
        float filteredGyroTimestep = 0.01f;
        synchronized (this) {
            if (this.sensorTimeStampGyro != 0L) {
                final float n = (sensorTimeStampGyro - this.sensorTimeStampGyro) * 1.0E-9f;
                if (n > 0.04f) {
                    if (this.gyroFilterValid) {
                        filteredGyroTimestep = this.filteredGyroTimestep;
                    }
                }
                else {
                    this.filterGyroTimestep(n);
                    filteredGyroTimestep = n;
                }
                this.mu.set(vector3d);
                this.mu.scale(-filteredGyroTimestep);
                So3Util.sO3FromMu(this.mu, this.so3LastMotion);
                this.processGyroTempM1.set(this.so3SensorFromWorld);
                Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.processGyroTempM1);
                this.so3SensorFromWorld.set(this.processGyroTempM1);
                this.updateCovariancesAfterMotion();
                this.processGyroTempM2.set(this.mQ);
                this.processGyroTempM2.scale(filteredGyroTimestep * filteredGyroTimestep);
                this.mP.plusEquals(this.processGyroTempM2);
            }
            this.sensorTimeStampGyro = sensorTimeStampGyro;
            this.lastGyro.set(vector3d);
        }
    }
    
    public void processMag(final float[] array, final long n) {
        while (true) {
            int n2 = 0;
            while (true) {
                Label_0426: {
                    synchronized (this) {
                        if (this.alignedToGravity) {
                            this.mz.set(array[0], array[1], array[2]);
                            this.mz.normalize();
                            final Vector3d vector3d = new Vector3d();
                            this.so3SensorFromWorld.getColumn(2, vector3d);
                            Vector3d.cross(this.mz, vector3d, this.processMagTempV1);
                            final Vector3d processMagTempV1 = this.processMagTempV1;
                            processMagTempV1.normalize();
                            Vector3d.cross(vector3d, processMagTempV1, this.processMagTempV2);
                            final Vector3d processMagTempV2 = this.processMagTempV2;
                            processMagTempV2.normalize();
                            this.mz.set(processMagTempV2);
                            if (!this.alignedToNorth) {
                                this.magObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu);
                                So3Util.sO3FromMu(this.mNu, this.so3LastMotion);
                                Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.processMagTempM4);
                                this.so3SensorFromWorld.set(this.processMagTempM4);
                                this.updateCovariancesAfterMotion();
                                this.alignedToNorth = true;
                            }
                            else {
                                this.magObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu);
                                if (n2 < 3) {
                                    break Label_0426;
                                }
                                this.mH.transpose(this.processMagTempM4);
                                Matrix3x3d.mult(this.mP, this.processMagTempM4, this.processMagTempM5);
                                Matrix3x3d.mult(this.mH, this.processMagTempM5, this.processMagTempM6);
                                Matrix3x3d.add(this.processMagTempM6, this.mR, this.mS);
                                this.mS.invert(this.processMagTempM4);
                                this.mH.transpose(this.processMagTempM5);
                                Matrix3x3d.mult(this.processMagTempM5, this.processMagTempM4, this.processMagTempM6);
                                Matrix3x3d.mult(this.mP, this.processMagTempM6, this.mK);
                                Matrix3x3d.mult(this.mK, this.mNu, this.mx);
                                Matrix3x3d.mult(this.mK, this.mH, this.processMagTempM4);
                                this.processMagTempM5.setIdentity();
                                this.processMagTempM5.minusEquals(this.processMagTempM4);
                                Matrix3x3d.mult(this.processMagTempM5, this.mP, this.processMagTempM4);
                                this.mP.set(this.processMagTempM4);
                                So3Util.sO3FromMu(this.mx, this.so3LastMotion);
                                Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.processMagTempM4);
                                this.so3SensorFromWorld.set(this.processMagTempM4);
                                this.updateCovariancesAfterMotion();
                            }
                        }
                        return;
                    }
                }
                final Vector3d processMagTempV3 = this.processMagTempV3;
                processMagTempV3.setZero();
                processMagTempV3.setComponent(n2, 1.0E-7);
                So3Util.sO3FromMu(processMagTempV3, this.processMagTempM1);
                Matrix3x3d.mult(this.processMagTempM1, this.so3SensorFromWorld, this.processMagTempM2);
                this.magObservationFunctionForNumericalJacobian(this.processMagTempM2, this.processMagTempV4);
                Vector3d.sub(this.mNu, this.processMagTempV4, this.processMagTempV5);
                this.processMagTempV5.scale(1.0E7);
                this.mH.setColumn(n2, this.processMagTempV5);
                ++n2;
                continue;
            }
        }
    }
    
    public void reset() {
        synchronized (this) {
            this.sensorTimeStampGyro = 0L;
            this.so3SensorFromWorld.setIdentity();
            this.so3LastMotion.setIdentity();
            this.mP.setZero();
            this.mP.setSameDiagonal(25.0);
            this.mQ.setZero();
            this.mQ.setSameDiagonal(1.0);
            this.mR.setZero();
            this.mR.setSameDiagonal(0.0625);
            this.mRaccel.setZero();
            this.mRaccel.setSameDiagonal(0.5625);
            this.mS.setZero();
            this.mH.setZero();
            this.mK.setZero();
            this.mNu.setZero();
            this.mz.setZero();
            this.mh.setZero();
            this.mu.setZero();
            this.mx.setZero();
            this.down.set(0.0, 0.0, 9.81);
            this.north.set(0.0, 1.0, 0.0);
            this.alignedToGravity = false;
            this.alignedToNorth = false;
        }
    }
    
    public void setHeadingDegrees(double sin) {
        synchronized (this) {
            final double n = sin - this.getHeadingDegrees();
            sin = Math.sin(n / 180.0 * 3.141592653589793);
            final double cos = Math.cos(n / 180.0 * 3.141592653589793);
            arrayAssign(new double[][] { { cos, -sin, 0.0 }, { sin, cos, 0.0 }, { 0.0, 0.0, 1.0 } }, this.setHeadingDegreesTempM1);
            Matrix3x3d.mult(this.so3SensorFromWorld, this.setHeadingDegreesTempM1, this.so3SensorFromWorld);
        }
    }
}
