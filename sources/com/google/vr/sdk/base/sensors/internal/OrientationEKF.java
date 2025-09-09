package com.google.vr.sdk.base.sensors.internal;

public class OrientationEKF {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final double MAX_ACCEL_NOISE_SIGMA = 7.0d;
    private static final double MIN_ACCEL_NOISE_SIGMA = 0.75d;
    private static final float NS2S = 1.0E-9f;
    private Matrix3x3d accObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();
    private boolean alignedToGravity;
    private boolean alignedToNorth;
    private Vector3d down = new Vector3d();
    private float filteredGyroTimestep;
    private Matrix3x3d getPredictedGLMatrixTempM1 = new Matrix3x3d();
    private Matrix3x3d getPredictedGLMatrixTempM2 = new Matrix3x3d();
    private Vector3d getPredictedGLMatrixTempV1 = new Vector3d();
    private boolean gyroFilterValid = true;
    private final Vector3d lastGyro = new Vector3d();
    private Matrix3x3d mH = new Matrix3x3d();
    private Matrix3x3d mK = new Matrix3x3d();
    private Vector3d mNu = new Vector3d();
    private Matrix3x3d mP = new Matrix3x3d();
    private Matrix3x3d mQ = new Matrix3x3d();
    private Matrix3x3d mR = new Matrix3x3d();
    private Matrix3x3d mRaccel = new Matrix3x3d();
    private Matrix3x3d mS = new Matrix3x3d();
    private Matrix3x3d magObservationFunctionForNumericalJacobianTempM = new Matrix3x3d();
    private Vector3d mh = new Vector3d();
    private double movingAverageAccelNormChange = 0.0d;
    private Vector3d mu = new Vector3d();
    private Vector3d mx = new Vector3d();
    private Vector3d mz = new Vector3d();
    private Vector3d north = new Vector3d();
    private int numGyroTimestepSamples;
    private double previousAccelNorm = 0.0d;
    private Matrix3x3d processAccTempM1 = new Matrix3x3d();
    private Matrix3x3d processAccTempM2 = new Matrix3x3d();
    private Matrix3x3d processAccTempM3 = new Matrix3x3d();
    private Matrix3x3d processAccTempM4 = new Matrix3x3d();
    private Matrix3x3d processAccTempM5 = new Matrix3x3d();
    private Vector3d processAccTempV1 = new Vector3d();
    private Vector3d processAccTempV2 = new Vector3d();
    private Vector3d processAccVDelta = new Vector3d();
    private Matrix3x3d processGyroTempM1 = new Matrix3x3d();
    private Matrix3x3d processGyroTempM2 = new Matrix3x3d();
    private Matrix3x3d processMagTempM1 = new Matrix3x3d();
    private Matrix3x3d processMagTempM2 = new Matrix3x3d();
    private Matrix3x3d processMagTempM4 = new Matrix3x3d();
    private Matrix3x3d processMagTempM5 = new Matrix3x3d();
    private Matrix3x3d processMagTempM6 = new Matrix3x3d();
    private Vector3d processMagTempV1 = new Vector3d();
    private Vector3d processMagTempV2 = new Vector3d();
    private Vector3d processMagTempV3 = new Vector3d();
    private Vector3d processMagTempV4 = new Vector3d();
    private Vector3d processMagTempV5 = new Vector3d();
    private double[] rotationMatrix = new double[16];
    private long sensorTimeStampGyro;
    private Matrix3x3d setHeadingDegreesTempM1 = new Matrix3x3d();
    private Matrix3x3d so3LastMotion = new Matrix3x3d();
    private Matrix3x3d so3SensorFromWorld = new Matrix3x3d();
    private boolean timestepFilterInit = false;
    private Matrix3x3d updateCovariancesAfterMotionTempM1 = new Matrix3x3d();
    private Matrix3x3d updateCovariancesAfterMotionTempM2 = new Matrix3x3d();

    static {
        boolean z = false;
        if (!OrientationEKF.class.desiredAssertionStatus()) {
            z = true;
        }
        $assertionsDisabled = z;
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
        } else if (!$assertionsDisabled && 3 != dArr[0].length) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && 3 != dArr[1].length) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && 3 != dArr[2].length) {
            throw new AssertionError();
        } else {
            matrix3x3d.set(dArr[0][0], dArr[0][1], dArr[0][2], dArr[1][0], dArr[1][1], dArr[1][2], dArr[2][0], dArr[2][1], dArr[2][2]);
        }
    }

    private void filterGyroTimestep(float f) {
        if (this.timestepFilterInit) {
            this.filteredGyroTimestep = (this.filteredGyroTimestep * 0.95f) + (0.050000012f * f);
            int i = this.numGyroTimestepSamples + 1;
            this.numGyroTimestepSamples = i;
            if (((float) i) > 10.0f) {
                this.gyroFilterValid = true;
                return;
            }
            return;
        }
        this.filteredGyroTimestep = f;
        this.numGyroTimestepSamples = 1;
        this.timestepFilterInit = true;
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
        double min = Math.min(MAX_ACCEL_NOISE_SIGMA, ((this.movingAverageAccelNormChange / 0.15d) * 6.25d) + MIN_ACCEL_NOISE_SIGMA);
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

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004e, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized double getHeadingDegrees() {
        /*
            r12 = this;
            r10 = 0
            r8 = 4645040803167600640(0x4076800000000000, double:360.0)
            monitor-enter(r12)
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r12.so3SensorFromWorld     // Catch:{ all -> 0x004f }
            r1 = 2
            r2 = 0
            double r0 = r0.get(r1, r2)     // Catch:{ all -> 0x004f }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r12.so3SensorFromWorld     // Catch:{ all -> 0x004f }
            r3 = 2
            r4 = 1
            double r2 = r2.get(r3, r4)     // Catch:{ all -> 0x004f }
            double r4 = r0 * r0
            double r6 = r2 * r2
            double r4 = r4 + r6
            double r4 = java.lang.Math.sqrt(r4)     // Catch:{ all -> 0x004f }
            r6 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x002c
            monitor-exit(r12)
            return r10
        L_0x002c:
            double r0 = java.lang.Math.atan2(r2, r0)     // Catch:{ all -> 0x004f }
            r2 = 4614256656552045848(0x400921fb54442d18, double:3.141592653589793)
            double r0 = r0 / r2
            r2 = 4640537203540230144(0x4066800000000000, double:180.0)
            double r0 = r0 * r2
            r2 = -4587338432941916160(0xc056800000000000, double:-90.0)
            double r0 = r2 - r0
            int r2 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r2 >= 0) goto L_0x0048
            double r0 = r0 + r8
        L_0x0048:
            int r2 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r2 < 0) goto L_0x004d
            double r0 = r0 - r8
        L_0x004d:
            monitor-exit(r12)
            return r0
        L_0x004f:
            r0 = move-exception
            monitor-exit(r12)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.sdk.base.sensors.internal.OrientationEKF.getHeadingDegrees():double");
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
            if (!this.alignedToGravity) {
                So3Util.sO3FromTwoVec(this.down, this.mz, this.so3SensorFromWorld);
                this.alignedToGravity = true;
            } else {
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
                this.mu.scale((double) (-f));
                So3Util.sO3FromMu(this.mu, this.so3LastMotion);
                this.processGyroTempM1.set(this.so3SensorFromWorld);
                Matrix3x3d.mult(this.so3LastMotion, this.so3SensorFromWorld, this.processGyroTempM1);
                this.so3SensorFromWorld.set(this.processGyroTempM1);
                updateCovariancesAfterMotion();
                this.processGyroTempM2.set(this.mQ);
                this.processGyroTempM2.scale((double) (f * f));
                this.mP.plusEquals(this.processGyroTempM2);
            }
            this.sensorTimeStampGyro = j;
            this.lastGyro.set(vector3d);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x006b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void processMag(float[] r9, long r10) {
        /*
            r8 = this;
            r0 = 0
            monitor-enter(r8)
            boolean r1 = r8.alignedToGravity     // Catch:{ all -> 0x0104 }
            if (r1 == 0) goto L_0x006c
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.mz     // Catch:{ all -> 0x0104 }
            r2 = 0
            r2 = r9[r2]     // Catch:{ all -> 0x0104 }
            double r2 = (double) r2     // Catch:{ all -> 0x0104 }
            r4 = 1
            r4 = r9[r4]     // Catch:{ all -> 0x0104 }
            double r4 = (double) r4     // Catch:{ all -> 0x0104 }
            r6 = 2
            r6 = r9[r6]     // Catch:{ all -> 0x0104 }
            double r6 = (double) r6     // Catch:{ all -> 0x0104 }
            r1.set(r2, r4, r6)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.mz     // Catch:{ all -> 0x0104 }
            r1.normalize()     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = new com.google.vr.sdk.base.sensors.internal.Vector3d     // Catch:{ all -> 0x0104 }
            r1.<init>()     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            r3 = 2
            r2.getColumn(r3, r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.mz     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r3 = r8.processMagTempV1     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d.cross(r2, r1, r3)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.processMagTempV1     // Catch:{ all -> 0x0104 }
            r2.normalize()     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r3 = r8.processMagTempV2     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d.cross(r1, r2, r3)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.processMagTempV2     // Catch:{ all -> 0x0104 }
            r1.normalize()     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.mz     // Catch:{ all -> 0x0104 }
            r2.set(r1)     // Catch:{ all -> 0x0104 }
            boolean r1 = r8.alignedToNorth     // Catch:{ all -> 0x0104 }
            if (r1 != 0) goto L_0x006e
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.mNu     // Catch:{ all -> 0x0104 }
            r8.magObservationFunctionForNumericalJacobian(r0, r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r0 = r8.mNu     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.so3LastMotion     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.So3Util.sO3FromMu(r0, r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.so3LastMotion     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            r0.set(r1)     // Catch:{ all -> 0x0104 }
            r8.updateCovariancesAfterMotion()     // Catch:{ all -> 0x0104 }
            r0 = 1
            r8.alignedToNorth = r0     // Catch:{ all -> 0x0104 }
        L_0x006a:
            monitor-exit(r8)
            return
        L_0x006c:
            monitor-exit(r8)
            return
        L_0x006e:
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.mNu     // Catch:{ all -> 0x0104 }
            r8.magObservationFunctionForNumericalJacobian(r1, r2)     // Catch:{ all -> 0x0104 }
        L_0x0075:
            r1 = 3
            if (r0 < r1) goto L_0x0107
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mH     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            r0.transpose(r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mP     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM5     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mH     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM5     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM6     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.processMagTempM6     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.mR     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.mS     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.add(r0, r1, r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mS     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            r0.invert(r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mH     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM5     // Catch:{ all -> 0x0104 }
            r0.transpose(r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.processMagTempM5     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM6     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mP     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM6     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.mK     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mK     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.mNu     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.mx     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Vector3d) r1, (com.google.vr.sdk.base.sensors.internal.Vector3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mK     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.mH     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.processMagTempM5     // Catch:{ all -> 0x0104 }
            r0.setIdentity()     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.processMagTempM5     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            r0.minusEquals(r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.processMagTempM5     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.mP     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.mP     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            r0.set(r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r0 = r8.mx     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.so3LastMotion     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.So3Util.sO3FromMu(r0, r1)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.so3LastMotion     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r0, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r0 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM4     // Catch:{ all -> 0x0104 }
            r0.set(r1)     // Catch:{ all -> 0x0104 }
            r8.updateCovariancesAfterMotion()     // Catch:{ all -> 0x0104 }
            goto L_0x006a
        L_0x0104:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        L_0x0107:
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.processMagTempV3     // Catch:{ all -> 0x0104 }
            r1.setZero()     // Catch:{ all -> 0x0104 }
            r2 = 4502148214488346440(0x3e7ad7f29abcaf48, double:1.0E-7)
            r1.setComponent(r0, r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.processMagTempM1     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.So3Util.sO3FromMu(r1, r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM1     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r2 = r8.so3SensorFromWorld     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r3 = r8.processMagTempM2     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d.mult((com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r1, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r2, (com.google.vr.sdk.base.sensors.internal.Matrix3x3d) r3)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.processMagTempM2     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.processMagTempV4     // Catch:{ all -> 0x0104 }
            r8.magObservationFunctionForNumericalJacobian(r1, r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.processMagTempV4     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.mNu     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r3 = r8.processMagTempV5     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d.sub(r2, r1, r3)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r1 = r8.processMagTempV5     // Catch:{ all -> 0x0104 }
            r2 = 4711630319722168320(0x416312d000000000, double:1.0E7)
            r1.scale(r2)     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Matrix3x3d r1 = r8.mH     // Catch:{ all -> 0x0104 }
            com.google.vr.sdk.base.sensors.internal.Vector3d r2 = r8.processMagTempV5     // Catch:{ all -> 0x0104 }
            r1.setColumn(r0, r2)     // Catch:{ all -> 0x0104 }
            int r0 = r0 + 1
            goto L_0x0075
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.sdk.base.sensors.internal.OrientationEKF.processMag(float[], long):void");
    }

    public synchronized void reset() {
        this.sensorTimeStampGyro = 0;
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
