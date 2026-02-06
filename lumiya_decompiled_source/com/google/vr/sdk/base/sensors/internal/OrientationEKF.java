package com.google.vr.sdk.base.sensors.internal;
/* loaded from: classes.dex */
public class OrientationEKF {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final double MAX_ACCEL_NOISE_SIGMA = 7.0d;
    private static final double MIN_ACCEL_NOISE_SIGMA = 0.75d;
    private static final float NS2S = 1.0E-9f;
    private boolean alignedToGravity;
    private boolean alignedToNorth;
    private float filteredGyroTimestep;
    private int numGyroTimestepSamples;
    private long sensorTimeStampGyro;
    private double[] rotationMatrix = new double[16];
    private Matrix3x3d so3SensorFromWorld = new Matrix3x3d();
    private Matrix3x3d so3LastMotion = new Matrix3x3d();
    private Matrix3x3d mP = new Matrix3x3d();
    private Matrix3x3d mQ = new Matrix3x3d();
    private Matrix3x3d mR = new Matrix3x3d();
    private Matrix3x3d mRaccel = new Matrix3x3d();
    private Matrix3x3d mS = new Matrix3x3d();
    private Matrix3x3d mH = new Matrix3x3d();
    private Matrix3x3d mK = new Matrix3x3d();
    private Vector3d mNu = new Vector3d();
    private Vector3d mz = new Vector3d();
    private Vector3d mh = new Vector3d();
    private Vector3d mu = new Vector3d();
    private Vector3d mx = new Vector3d();
    private Vector3d down = new Vector3d();
    private Vector3d north = new Vector3d();
    private final Vector3d lastGyro = new Vector3d();
    private double previousAccelNorm = 0.0d;
    private double movingAverageAccelNormChange = 0.0d;
    private boolean timestepFilterInit = false;
    private boolean gyroFilterValid = true;
    private Matrix3x3d getPredictedGLMatrixTempM1 = new Matrix3x3d();
    private Matrix3x3d getPredictedGLMatrixTempM2 = new Matrix3x3d();
    private Vector3d getPredictedGLMatrixTempV1 = new Vector3d();
    private Matrix3x3d setHeadingDegreesTempM1 = new Matrix3x3d();
    private Matrix3x3d processGyroTempM1 = new Matrix3x3d();
    private Matrix3x3d processGyroTempM2 = new Matrix3x3d();
    private Matrix3x3d processAccTempM1 = new Matrix3x3d();
    private Matrix3x3d processAccTempM2 = new Matrix3x3d();
    private Matrix3x3d processAccTempM3 = new Matrix3x3d();
    private Matrix3x3d processAccTempM4 = new Matrix3x3d();
    private Matrix3x3d processAccTempM5 = new Matrix3x3d();
    private Vector3d processAccTempV1 = new Vector3d();
    private Vector3d processAccTempV2 = new Vector3d();
    private Vector3d processAccVDelta = new Vector3d();
    private Vector3d processMagTempV1 = new Vector3d();
    private Vector3d processMagTempV2 = new Vector3d();
    private Vector3d processMagTempV3 = new Vector3d();
    private Vector3d processMagTempV4 = new Vector3d();
    private Vector3d processMagTempV5 = new Vector3d();
    private Matrix3x3d processMagTempM1 = new Matrix3x3d();
    private Matrix3x3d processMagTempM2 = new Matrix3x3d();
    private Matrix3x3d processMagTempM4 = new Matrix3x3d();
    private Matrix3x3d processMagTempM5 = new Matrix3x3d();
    private Matrix3x3d processMagTempM6 = new Matrix3x3d();
    private Matrix3x3d updateCovariancesAfterMotionTempM1 = new Matrix3x3d();
    private Matrix3x3d updateCovariancesAfterMotionTempM2 = new Matrix3x3d();
    private Matrix3x3d accObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();
    private Matrix3x3d magObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();

    static {
        $assertionsDisabled = OrientationEKF.class.desiredAssertionStatus() ? false : true;
    }

    public OrientationEKF() {
        reset();
    }

    private void accObservationFunctionForNumericalJacobian(Matrix3x3d matrix3x3d, Vector3d vector3d) {
        Matrix3x3d.mult(matrix3x3d, this.down, this.mh);
        So3Util.sO3FromTwoVec(this.mh, this.mz, this.accObservationFunctionForNumericalJacobianTempM);
        So3Util.muFromSO3(this.accObservationFunctionForNumericalJacobianTempM, vector3d);
    }

    public static void arrayAssign(double[][] dArr, Matrix3x3d matrix3x3d) {
        if (!$assertionsDisabled && 3 != dArr.length) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && 3 != dArr[0].length) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && 3 != dArr[1].length) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && 3 != dArr[2].length) {
            throw new AssertionError();
        }
        matrix3x3d.set(dArr[0][0], dArr[0][1], dArr[0][2], dArr[1][0], dArr[1][1], dArr[1][2], dArr[2][0], dArr[2][1], dArr[2][2]);
    }

    private void filterGyroTimestep(float f) {
        if (!this.timestepFilterInit) {
            this.filteredGyroTimestep = f;
            this.numGyroTimestepSamples = 1;
            this.timestepFilterInit = true;
            return;
        }
        this.filteredGyroTimestep = (this.filteredGyroTimestep * 0.95f) + (0.050000012f * f);
        int i = this.numGyroTimestepSamples + 1;
        this.numGyroTimestepSamples = i;
        if (i > 10.0f) {
            this.gyroFilterValid = true;
        }
    }

    private double[] glMatrixFromSo3(Matrix3x3d matrix3x3d) {
        for (int i = 0; i < 3; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                this.rotationMatrix[(i2 * 4) + i] = matrix3x3d.get(i, i2);
            }
        }
        double[] dArr = this.rotationMatrix;
        double[] dArr2 = this.rotationMatrix;
        this.rotationMatrix[11] = 0.0d;
        dArr2[7] = 0.0d;
        dArr[3] = 0.0d;
        double[] dArr3 = this.rotationMatrix;
        double[] dArr4 = this.rotationMatrix;
        this.rotationMatrix[14] = 0.0d;
        dArr4[13] = 0.0d;
        dArr3[12] = 0.0d;
        this.rotationMatrix[15] = 1.0d;
        return this.rotationMatrix;
    }

    private void magObservationFunctionForNumericalJacobian(Matrix3x3d matrix3x3d, Vector3d vector3d) {
        Matrix3x3d.mult(matrix3x3d, this.north, this.mh);
        So3Util.sO3FromTwoVec(this.mh, this.mz, this.magObservationFunctionForNumericalJacobianTempM);
        So3Util.muFromSO3(this.magObservationFunctionForNumericalJacobianTempM, vector3d);
    }

    private void updateAccelCovariance(double d) {
        double abs = Math.abs(d - this.previousAccelNorm);
        this.previousAccelNorm = d;
        this.movingAverageAccelNormChange = (abs * 0.5d) + (this.movingAverageAccelNormChange * 0.5d);
        double min = Math.min((double) MAX_ACCEL_NOISE_SIGMA, ((this.movingAverageAccelNormChange / 0.15d) * 6.25d) + MIN_ACCEL_NOISE_SIGMA);
        this.mRaccel.setSameDiagonal(min * min);
    }

    private void updateCovariancesAfterMotion() {
        this.so3LastMotion.transpose(this.updateCovariancesAfterMotionTempM1);
        Matrix3x3d.mult(this.mP, this.updateCovariancesAfterMotionTempM1, this.updateCovariancesAfterMotionTempM2);
        Matrix3x3d.mult(this.so3LastMotion, this.updateCovariancesAfterMotionTempM2, this.mP);
        this.so3LastMotion.setIdentity();
    }

    public synchronized double[] getGLMatrix() {
        return glMatrixFromSo3(this.so3SensorFromWorld);
    }

    public synchronized double getHeadingDegrees() {
        double d = this.so3SensorFromWorld.get(2, 0);
        double d2 = this.so3SensorFromWorld.get(2, 1);
        if (Math.sqrt((d * d) + (d2 * d2)) < 0.1d) {
            return 0.0d;
        }
        double atan2 = (-90.0d) - ((Math.atan2(d2, d) / 3.141592653589793d) * 180.0d);
        if (atan2 < 0.0d) {
            atan2 += 360.0d;
        }
        if (atan2 >= 360.0d) {
            atan2 -= 360.0d;
        }
        return atan2;
    }

    public synchronized double[] getPredictedGLMatrix(double d) {
        Matrix3x3d matrix3x3d;
        Vector3d vector3d = this.getPredictedGLMatrixTempV1;
        vector3d.set(this.lastGyro);
        vector3d.scale(-d);
        Matrix3x3d matrix3x3d2 = this.getPredictedGLMatrixTempM1;
        So3Util.sO3FromMu(vector3d, matrix3x3d2);
        matrix3x3d = this.getPredictedGLMatrixTempM2;
        Matrix3x3d.mult(matrix3x3d2, this.so3SensorFromWorld, matrix3x3d);
        return glMatrixFromSo3(matrix3x3d);
    }

    public synchronized Matrix3x3d getRotationMatrix() {
        return this.so3SensorFromWorld;
    }

    public synchronized boolean isAlignedToGravity() {
        return this.alignedToGravity;
    }

    public synchronized boolean isAlignedToNorth() {
        return this.alignedToNorth;
    }

    public synchronized boolean isReady() {
        return this.alignedToGravity;
    }

    public synchronized void processAcc(Vector3d vector3d, long j) {
        synchronized (this) {
            this.mz.set(vector3d);
            updateAccelCovariance(this.mz.length());
            if (this.alignedToGravity) {
                accObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu);
                for (int i = 0; i < 3; i++) {
                    Vector3d vector3d2 = this.processAccVDelta;
                    vector3d2.setZero();
                    vector3d2.setComponent(i, 1.0E-7d);
                    So3Util.sO3FromMu(vector3d2, this.processAccTempM1);
                    Matrix3x3d.mult(this.processAccTempM1, this.so3SensorFromWorld, this.processAccTempM2);
                    accObservationFunctionForNumericalJacobian(this.processAccTempM2, this.processAccTempV1);
                    Vector3d.sub(this.mNu, this.processAccTempV1, this.processAccTempV2);
                    this.processAccTempV2.scale(1.0E7d);
                    this.mH.setColumn(i, this.processAccTempV2);
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
                updateCovariancesAfterMotion();
            } else {
                So3Util.sO3FromTwoVec(this.down, this.mz, this.so3SensorFromWorld);
                this.alignedToGravity = true;
            }
        }
    }

    public synchronized void processGyro(Vector3d vector3d, long j) {
        float f = 0.01f;
        synchronized (this) {
            if (this.sensorTimeStampGyro != 0) {
                float f2 = ((float) (j - this.sensorTimeStampGyro)) * NS2S;
                if (f2 <= 0.04f) {
                    filterGyroTimestep(f2);
                    f = f2;
                } else if (this.gyroFilterValid) {
                    f = this.filteredGyroTimestep;
                }
                this.mu.set(vector3d);
                this.mu.scale(-f);
                So3Util.sO3FromMu(this.mu, this.so3LastMotion);
                this.processGyroTempM1.set(this.so3SensorFromWorld);
                Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.processGyroTempM1);
                this.so3SensorFromWorld.set(this.processGyroTempM1);
                updateCovariancesAfterMotion();
                this.processGyroTempM2.set(this.mQ);
                this.processGyroTempM2.scale(f * f);
                this.mP.plusEquals(this.processGyroTempM2);
            }
            this.sensorTimeStampGyro = j;
            this.lastGyro.set(vector3d);
        }
    }

    public synchronized void processMag(float[] fArr, long j) {
        synchronized (this) {
            if (this.alignedToGravity) {
                this.mz.set(fArr[0], fArr[1], fArr[2]);
                this.mz.normalize();
                Vector3d vector3d = new Vector3d();
                this.so3SensorFromWorld.getColumn(2, vector3d);
                Vector3d.cross(this.mz, vector3d, this.processMagTempV1);
                Vector3d vector3d2 = this.processMagTempV1;
                vector3d2.normalize();
                Vector3d.cross(vector3d, vector3d2, this.processMagTempV2);
                Vector3d vector3d3 = this.processMagTempV2;
                vector3d3.normalize();
                this.mz.set(vector3d3);
                if (this.alignedToNorth) {
                    magObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu);
                    for (int i = 0; i < 3; i++) {
                        Vector3d vector3d4 = this.processMagTempV3;
                        vector3d4.setZero();
                        vector3d4.setComponent(i, 1.0E-7d);
                        So3Util.sO3FromMu(vector3d4, this.processMagTempM1);
                        Matrix3x3d.mult(this.processMagTempM1, this.so3SensorFromWorld, this.processMagTempM2);
                        magObservationFunctionForNumericalJacobian(this.processMagTempM2, this.processMagTempV4);
                        Vector3d.sub(this.mNu, this.processMagTempV4, this.processMagTempV5);
                        this.processMagTempV5.scale(1.0E7d);
                        this.mH.setColumn(i, this.processMagTempV5);
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
                    updateCovariancesAfterMotion();
                } else {
                    magObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu);
                    So3Util.sO3FromMu(this.mNu, this.so3LastMotion);
                    Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.processMagTempM4);
                    this.so3SensorFromWorld.set(this.processMagTempM4);
                    updateCovariancesAfterMotion();
                    this.alignedToNorth = true;
                }
            }
        }
    }

    public synchronized void reset() {
        this.sensorTimeStampGyro = 0L;
        this.so3SensorFromWorld.setIdentity();
        this.so3LastMotion.setIdentity();
        this.mP.setZero();
        this.mP.setSameDiagonal(25.0d);
        this.mQ.setZero();
        this.mQ.setSameDiagonal(1.0d);
        this.mR.setZero();
        this.mR.setSameDiagonal(0.0625d);
        this.mRaccel.setZero();
        this.mRaccel.setSameDiagonal(0.5625d);
        this.mS.setZero();
        this.mH.setZero();
        this.mK.setZero();
        this.mNu.setZero();
        this.mz.setZero();
        this.mh.setZero();
        this.mu.setZero();
        this.mx.setZero();
        this.down.set(0.0d, 0.0d, 9.81d);
        this.north.set(0.0d, 1.0d, 0.0d);
        this.alignedToGravity = false;
        this.alignedToNorth = false;
    }

    public synchronized void setHeadingDegrees(double d) {
        double headingDegrees = d - getHeadingDegrees();
        double sin = Math.sin((headingDegrees / 180.0d) * 3.141592653589793d);
        double cos = Math.cos((headingDegrees / 180.0d) * 3.141592653589793d);
        arrayAssign(new double[][]{new double[]{cos, -sin, 0.0d}, new double[]{sin, cos, 0.0d}, new double[]{0.0d, 0.0d, 1.0d}}, this.setHeadingDegreesTempM1);
        Matrix3x3d.mult(this.so3SensorFromWorld, this.setHeadingDegreesTempM1, this.so3SensorFromWorld);
    }
}
