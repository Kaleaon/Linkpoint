package com.google.vr.sdk.base.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.view.Display;
import android.view.WindowManager;
import com.google.vr.sdk.base.sensors.internal.GyroscopeBiasEstimator;
import com.google.vr.sdk.base.sensors.internal.Matrix3x3d;
import com.google.vr.sdk.base.sensors.internal.OrientationEKF;
import com.google.vr.sdk.base.sensors.internal.Vector3d;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class HeadTracker implements SensorEventListener {
    private static final float DEFAULT_NECK_HORIZONTAL_OFFSET = 0.08f;
    private static final float DEFAULT_NECK_MODEL_FACTOR = 1.0f;
    private static final float DEFAULT_NECK_VERTICAL_OFFSET = 0.075f;
    private static final float PREDICTION_TIME_IN_SECONDS = 0.058f;
    private Clock clock;
    private final Display display;
    private long latestGyroEventClockTimeNs;
    private SensorEventProvider sensorEventProvider;
    private volatile boolean tracking;
    private final float[] ekfToHeadTracker = new float[16];
    private final float[] sensorToDisplay = new float[16];
    private float displayRotation = Float.NaN;
    private final float[] neckModelTranslation = new float[16];
    private final float[] tmpHeadView = new float[16];
    private final float[] tmpHeadView2 = new float[16];
    private float neckModelFactor = 1.0f;
    private final Object neckModelFactorMutex = new Object();
    private final Object gyroBiasEstimatorMutex = new Object();
    private volatile boolean firstGyroValue = true;
    private float[] initialSystemGyroBias = new float[3];
    private final Vector3d gyroBias = new Vector3d();
    private final Vector3d latestGyro = new Vector3d();
    private final Vector3d latestAcc = new Vector3d();
    private final OrientationEKF tracker = new OrientationEKF();
    private GyroscopeBiasEstimator gyroBiasEstimator = new GyroscopeBiasEstimator();

    public HeadTracker(SensorEventProvider sensorEventProvider, Clock clock, Display display) {
        this.clock = clock;
        this.sensorEventProvider = sensorEventProvider;
        this.display = display;
        Matrix.setIdentityM(this.neckModelTranslation, 0);
    }

    public static HeadTracker createFromContext(Context context) {
        return new HeadTracker(new DeviceSensorLooper((SensorManager) context.getSystemService("sensor")), new SystemClock(), ((WindowManager) context.getSystemService("window")).getDefaultDisplay());
    }

    Matrix3x3d getCurrentPoseForTest() {
        return new Matrix3x3d(this.tracker.getRotationMatrix());
    }

    public void getLastHeadView(float[] fArr, int i) {
        float f;
        if (i + 16 > fArr.length) {
            throw new IllegalArgumentException("Not enough space to write the result");
        }
        switch (this.display.getRotation()) {
            case 0:
                f = 0.0f;
                break;
            case 1:
                f = 90.0f;
                break;
            case 2:
                f = 180.0f;
                break;
            case 3:
                f = 270.0f;
                break;
            default:
                f = 0.0f;
                break;
        }
        if (f != this.displayRotation) {
            this.displayRotation = f;
            Matrix.setRotateEulerM(this.sensorToDisplay, 0, 0.0f, 0.0f, -f);
            Matrix.setRotateEulerM(this.ekfToHeadTracker, 0, -90.0f, 0.0f, f);
        }
        synchronized (this.tracker) {
            if (this.tracker.isReady()) {
                double[] predictedGLMatrix = this.tracker.getPredictedGLMatrix(TimeUnit.NANOSECONDS.toSeconds(this.clock.nanoTime() - this.latestGyroEventClockTimeNs) + 0.057999998331069946d);
                for (int i2 = 0; i2 < fArr.length; i2++) {
                    this.tmpHeadView[i2] = (float) predictedGLMatrix[i2];
                }
                Matrix.multiplyMM(this.tmpHeadView2, 0, this.sensorToDisplay, 0, this.tmpHeadView, 0);
                Matrix.multiplyMM(fArr, i, this.tmpHeadView2, 0, this.ekfToHeadTracker, 0);
                Matrix.setIdentityM(this.neckModelTranslation, 0);
                Matrix.translateM(this.neckModelTranslation, 0, 0.0f, (-this.neckModelFactor) * DEFAULT_NECK_VERTICAL_OFFSET, this.neckModelFactor * DEFAULT_NECK_HORIZONTAL_OFFSET);
                Matrix.multiplyMM(this.tmpHeadView, 0, this.neckModelTranslation, 0, fArr, i);
                Matrix.translateM(fArr, i, this.tmpHeadView, 0, 0.0f, this.neckModelFactor * DEFAULT_NECK_VERTICAL_OFFSET, 0.0f);
            }
        }
    }

    public float getNeckModelFactor() {
        float f;
        synchronized (this.neckModelFactorMutex) {
            f = this.neckModelFactor;
        }
        return f;
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 1) {
            this.latestAcc.set(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            this.tracker.processAcc(this.latestAcc, sensorEvent.timestamp);
            synchronized (this.gyroBiasEstimatorMutex) {
                if (this.gyroBiasEstimator != null) {
                    this.gyroBiasEstimator.processAccelerometer(this.latestAcc, sensorEvent.timestamp);
                }
            }
        } else if (sensorEvent.sensor.getType() == 4 || sensorEvent.sensor.getType() == 16) {
            this.latestGyroEventClockTimeNs = this.clock.nanoTime();
            if (sensorEvent.sensor.getType() != 16) {
                this.latestGyro.set(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            } else {
                if (this.firstGyroValue && sensorEvent.values.length == 6) {
                    this.initialSystemGyroBias[0] = sensorEvent.values[3];
                    this.initialSystemGyroBias[1] = sensorEvent.values[4];
                    this.initialSystemGyroBias[2] = sensorEvent.values[5];
                }
                this.latestGyro.set(sensorEvent.values[0] - this.initialSystemGyroBias[0], sensorEvent.values[1] - this.initialSystemGyroBias[1], sensorEvent.values[2] - this.initialSystemGyroBias[2]);
            }
            this.firstGyroValue = false;
            synchronized (this.gyroBiasEstimatorMutex) {
                if (this.gyroBiasEstimator != null) {
                    this.gyroBiasEstimator.processGyroscope(this.latestGyro, sensorEvent.timestamp);
                    this.gyroBiasEstimator.getGyroBias(this.gyroBias);
                    Vector3d.sub(this.latestGyro, this.gyroBias, this.latestGyro);
                }
            }
            this.tracker.processGyro(this.latestGyro, sensorEvent.timestamp);
        }
    }

    public void resetTracker() {
        this.tracker.reset();
    }

    void setGyroBiasEstimator(GyroscopeBiasEstimator gyroscopeBiasEstimator) {
        synchronized (this.gyroBiasEstimatorMutex) {
            this.gyroBiasEstimator = gyroscopeBiasEstimator;
        }
    }

    public void setNeckModelEnabled(boolean z) {
        if (z) {
            setNeckModelFactor(1.0f);
        } else {
            setNeckModelFactor(0.0f);
        }
    }

    public void setNeckModelFactor(float f) {
        synchronized (this.neckModelFactorMutex) {
            if ((f < 0.0f) || f > 1.0f) {
                throw new IllegalArgumentException("factor should be within [0.0, 1.0]");
            }
            this.neckModelFactor = f;
        }
    }

    public void startTracking() {
        if (this.tracking) {
            return;
        }
        this.tracker.reset();
        synchronized (this.gyroBiasEstimatorMutex) {
            if (this.gyroBiasEstimator != null) {
                this.gyroBiasEstimator.reset();
            }
        }
        this.firstGyroValue = true;
        this.sensorEventProvider.registerListener(this);
        this.sensorEventProvider.start();
        this.tracking = true;
    }

    public void stopTracking() {
        if (this.tracking) {
            this.sensorEventProvider.unregisterListener(this);
            this.sensorEventProvider.stop();
            this.tracking = false;
        }
    }
}
