// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors;

import android.hardware.SensorEvent;
import android.hardware.Sensor;
import java.util.concurrent.TimeUnit;
import com.google.vr.sdk.base.sensors.internal.Matrix3x3d;
import android.view.WindowManager;
import android.hardware.SensorManager;
import android.content.Context;
import android.opengl.Matrix;
import com.google.vr.sdk.base.sensors.internal.OrientationEKF;
import com.google.vr.sdk.base.sensors.internal.GyroscopeBiasEstimator;
import com.google.vr.sdk.base.sensors.internal.Vector3d;
import android.view.Display;
import android.hardware.SensorEventListener;

public class HeadTracker implements SensorEventListener
{
    private static final float DEFAULT_NECK_HORIZONTAL_OFFSET = 0.08f;
    private static final float DEFAULT_NECK_MODEL_FACTOR = 1.0f;
    private static final float DEFAULT_NECK_VERTICAL_OFFSET = 0.075f;
    private static final float PREDICTION_TIME_IN_SECONDS = 0.058f;
    private Clock clock;
    private final Display display;
    private float displayRotation;
    private final float[] ekfToHeadTracker;
    private volatile boolean firstGyroValue;
    private final Vector3d gyroBias;
    private GyroscopeBiasEstimator gyroBiasEstimator;
    private final Object gyroBiasEstimatorMutex;
    private float[] initialSystemGyroBias;
    private final Vector3d latestAcc;
    private final Vector3d latestGyro;
    private long latestGyroEventClockTimeNs;
    private float neckModelFactor;
    private final Object neckModelFactorMutex;
    private final float[] neckModelTranslation;
    private SensorEventProvider sensorEventProvider;
    private final float[] sensorToDisplay;
    private final float[] tmpHeadView;
    private final float[] tmpHeadView2;
    private final OrientationEKF tracker;
    private volatile boolean tracking;
    
    public HeadTracker(final SensorEventProvider sensorEventProvider, final Clock clock, final Display display) {
        this.ekfToHeadTracker = new float[16];
        this.sensorToDisplay = new float[16];
        this.displayRotation = Float.NaN;
        this.neckModelTranslation = new float[16];
        this.tmpHeadView = new float[16];
        this.tmpHeadView2 = new float[16];
        this.neckModelFactor = 1.0f;
        this.neckModelFactorMutex = new Object();
        this.gyroBiasEstimatorMutex = new Object();
        this.firstGyroValue = true;
        this.initialSystemGyroBias = new float[3];
        this.gyroBias = new Vector3d();
        this.latestGyro = new Vector3d();
        this.latestAcc = new Vector3d();
        this.clock = clock;
        this.sensorEventProvider = sensorEventProvider;
        this.tracker = new OrientationEKF();
        this.display = display;
        this.gyroBiasEstimator = new GyroscopeBiasEstimator();
        Matrix.setIdentityM(this.neckModelTranslation, 0);
    }
    
    public static HeadTracker createFromContext(final Context context) {
        return new HeadTracker(new DeviceSensorLooper((SensorManager)context.getSystemService("sensor")), new SystemClock(), ((WindowManager)context.getSystemService("window")).getDefaultDisplay());
    }
    
    Matrix3x3d getCurrentPoseForTest() {
        return new Matrix3x3d(this.tracker.getRotationMatrix());
    }
    
    public void getLastHeadView(final float[] array, final int n) {
        Label_0258: {
            if (n + 16 > array.length) {
                break Label_0258;
            }
            Label_0285: {
                float displayRotation = 0.0f;
                switch (this.display.getRotation()) {
                    default: {
                        displayRotation = 0.0f;
                        break;
                    }
                    case 0: {
                        break Label_0285;
                    }
                    case 1: {
                        break Label_0285;
                    }
                    case 2: {
                        break Label_0285;
                    }
                    case 3: {
                        break Label_0285;
                    }
                }
                while (true) {
                    if (displayRotation != this.displayRotation) {
                        this.displayRotation = displayRotation;
                        Matrix.setRotateEulerM(this.sensorToDisplay, 0, 0.0f, 0.0f, -displayRotation);
                        Matrix.setRotateEulerM(this.ekfToHeadTracker, 0, -90.0f, 0.0f, displayRotation);
                    }
                    synchronized (this.tracker) {
                        if (this.tracker.isReady()) {
                            final double[] predictedGLMatrix = this.tracker.getPredictedGLMatrix(TimeUnit.NANOSECONDS.toSeconds(this.clock.nanoTime() - this.latestGyroEventClockTimeNs) + 0.057999998331069946);
                            for (int i = 0; i < array.length; ++i) {
                                this.tmpHeadView[i] = (float)predictedGLMatrix[i];
                            }
                            monitorexit(this.tracker);
                            Matrix.multiplyMM(this.tmpHeadView2, 0, this.sensorToDisplay, 0, this.tmpHeadView, 0);
                            Matrix.multiplyMM(array, n, this.tmpHeadView2, 0, this.ekfToHeadTracker, 0);
                            Matrix.setIdentityM(this.neckModelTranslation, 0);
                            Matrix.translateM(this.neckModelTranslation, 0, 0.0f, -this.neckModelFactor * 0.075f, this.neckModelFactor * 0.08f);
                            Matrix.multiplyMM(this.tmpHeadView, 0, this.neckModelTranslation, 0, array, n);
                            Matrix.translateM(array, n, this.tmpHeadView, 0, 0.0f, this.neckModelFactor * 0.075f, 0.0f);
                            return;
                        }
                        return;
                        displayRotation = 0.0f;
                        continue;
                        displayRotation = 270.0f;
                        continue;
                        throw new IllegalArgumentException("Not enough space to write the result");
                        displayRotation = 90.0f;
                        continue;
                        displayRotation = 180.0f;
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    public float getNeckModelFactor() {
        synchronized (this.neckModelFactorMutex) {
            return this.neckModelFactor;
        }
    }
    
    public void onAccuracyChanged(final Sensor sensor, final int n) {
    }
    
    public void onSensorChanged(final SensorEvent p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        android/hardware/SensorEvent.sensor:Landroid/hardware/Sensor;
        //     4: invokevirtual   android/hardware/Sensor.getType:()I
        //     7: iconst_1       
        //     8: if_icmpeq       112
        //    11: aload_1        
        //    12: getfield        android/hardware/SensorEvent.sensor:Landroid/hardware/Sensor;
        //    15: invokevirtual   android/hardware/Sensor.getType:()I
        //    18: iconst_4       
        //    19: if_icmpne       197
        //    22: aload_0        
        //    23: aload_0        
        //    24: getfield        com/google/vr/sdk/base/sensors/HeadTracker.clock:Lcom/google/vr/sdk/base/sensors/Clock;
        //    27: invokeinterface com/google/vr/sdk/base/sensors/Clock.nanoTime:()J
        //    32: putfield        com/google/vr/sdk/base/sensors/HeadTracker.latestGyroEventClockTimeNs:J
        //    35: aload_1        
        //    36: getfield        android/hardware/SensorEvent.sensor:Landroid/hardware/Sensor;
        //    39: invokevirtual   android/hardware/Sensor.getType:()I
        //    42: bipush          16
        //    44: if_icmpeq       212
        //    47: aload_0        
        //    48: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestGyro:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //    51: aload_1        
        //    52: getfield        android/hardware/SensorEvent.values:[F
        //    55: iconst_0       
        //    56: faload         
        //    57: f2d            
        //    58: aload_1        
        //    59: getfield        android/hardware/SensorEvent.values:[F
        //    62: iconst_1       
        //    63: faload         
        //    64: f2d            
        //    65: aload_1        
        //    66: getfield        android/hardware/SensorEvent.values:[F
        //    69: iconst_2       
        //    70: faload         
        //    71: f2d            
        //    72: invokevirtual   com/google/vr/sdk/base/sensors/internal/Vector3d.set:(DDD)V
        //    75: aload_0        
        //    76: iconst_0       
        //    77: putfield        com/google/vr/sdk/base/sensors/HeadTracker.firstGyroValue:Z
        //    80: aload_0        
        //    81: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBiasEstimatorMutex:Ljava/lang/Object;
        //    84: astore_2       
        //    85: aload_2        
        //    86: monitorenter   
        //    87: aload_0        
        //    88: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBiasEstimator:Lcom/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator;
        //    91: ifnonnull       320
        //    94: aload_2        
        //    95: monitorexit    
        //    96: aload_0        
        //    97: getfield        com/google/vr/sdk/base/sensors/HeadTracker.tracker:Lcom/google/vr/sdk/base/sensors/internal/OrientationEKF;
        //   100: aload_0        
        //   101: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestGyro:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   104: aload_1        
        //   105: getfield        android/hardware/SensorEvent.timestamp:J
        //   108: invokevirtual   com/google/vr/sdk/base/sensors/internal/OrientationEKF.processGyro:(Lcom/google/vr/sdk/base/sensors/internal/Vector3d;J)V
        //   111: return         
        //   112: aload_0        
        //   113: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestAcc:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   116: aload_1        
        //   117: getfield        android/hardware/SensorEvent.values:[F
        //   120: iconst_0       
        //   121: faload         
        //   122: f2d            
        //   123: aload_1        
        //   124: getfield        android/hardware/SensorEvent.values:[F
        //   127: iconst_1       
        //   128: faload         
        //   129: f2d            
        //   130: aload_1        
        //   131: getfield        android/hardware/SensorEvent.values:[F
        //   134: iconst_2       
        //   135: faload         
        //   136: f2d            
        //   137: invokevirtual   com/google/vr/sdk/base/sensors/internal/Vector3d.set:(DDD)V
        //   140: aload_0        
        //   141: getfield        com/google/vr/sdk/base/sensors/HeadTracker.tracker:Lcom/google/vr/sdk/base/sensors/internal/OrientationEKF;
        //   144: aload_0        
        //   145: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestAcc:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   148: aload_1        
        //   149: getfield        android/hardware/SensorEvent.timestamp:J
        //   152: invokevirtual   com/google/vr/sdk/base/sensors/internal/OrientationEKF.processAcc:(Lcom/google/vr/sdk/base/sensors/internal/Vector3d;J)V
        //   155: aload_0        
        //   156: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBiasEstimatorMutex:Ljava/lang/Object;
        //   159: astore_2       
        //   160: aload_2        
        //   161: monitorenter   
        //   162: aload_0        
        //   163: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBiasEstimator:Lcom/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator;
        //   166: ifnonnull       179
        //   169: aload_2        
        //   170: monitorexit    
        //   171: goto            111
        //   174: astore_1       
        //   175: aload_2        
        //   176: monitorexit    
        //   177: aload_1        
        //   178: athrow         
        //   179: aload_0        
        //   180: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBiasEstimator:Lcom/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator;
        //   183: aload_0        
        //   184: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestAcc:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   187: aload_1        
        //   188: getfield        android/hardware/SensorEvent.timestamp:J
        //   191: invokevirtual   com/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator.processAccelerometer:(Lcom/google/vr/sdk/base/sensors/internal/Vector3d;J)V
        //   194: goto            169
        //   197: aload_1        
        //   198: getfield        android/hardware/SensorEvent.sensor:Landroid/hardware/Sensor;
        //   201: invokevirtual   android/hardware/Sensor.getType:()I
        //   204: bipush          16
        //   206: if_icmpeq       22
        //   209: goto            111
        //   212: aload_0        
        //   213: getfield        com/google/vr/sdk/base/sensors/HeadTracker.firstGyroValue:Z
        //   216: ifne            271
        //   219: aload_0        
        //   220: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestGyro:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   223: aload_1        
        //   224: getfield        android/hardware/SensorEvent.values:[F
        //   227: iconst_0       
        //   228: faload         
        //   229: aload_0        
        //   230: getfield        com/google/vr/sdk/base/sensors/HeadTracker.initialSystemGyroBias:[F
        //   233: iconst_0       
        //   234: faload         
        //   235: fsub           
        //   236: f2d            
        //   237: aload_1        
        //   238: getfield        android/hardware/SensorEvent.values:[F
        //   241: iconst_1       
        //   242: faload         
        //   243: aload_0        
        //   244: getfield        com/google/vr/sdk/base/sensors/HeadTracker.initialSystemGyroBias:[F
        //   247: iconst_1       
        //   248: faload         
        //   249: fsub           
        //   250: f2d            
        //   251: aload_1        
        //   252: getfield        android/hardware/SensorEvent.values:[F
        //   255: iconst_2       
        //   256: faload         
        //   257: aload_0        
        //   258: getfield        com/google/vr/sdk/base/sensors/HeadTracker.initialSystemGyroBias:[F
        //   261: iconst_2       
        //   262: faload         
        //   263: fsub           
        //   264: f2d            
        //   265: invokevirtual   com/google/vr/sdk/base/sensors/internal/Vector3d.set:(DDD)V
        //   268: goto            75
        //   271: aload_1        
        //   272: getfield        android/hardware/SensorEvent.values:[F
        //   275: arraylength    
        //   276: bipush          6
        //   278: if_icmpne       219
        //   281: aload_0        
        //   282: getfield        com/google/vr/sdk/base/sensors/HeadTracker.initialSystemGyroBias:[F
        //   285: iconst_0       
        //   286: aload_1        
        //   287: getfield        android/hardware/SensorEvent.values:[F
        //   290: iconst_3       
        //   291: faload         
        //   292: fastore        
        //   293: aload_0        
        //   294: getfield        com/google/vr/sdk/base/sensors/HeadTracker.initialSystemGyroBias:[F
        //   297: iconst_1       
        //   298: aload_1        
        //   299: getfield        android/hardware/SensorEvent.values:[F
        //   302: iconst_4       
        //   303: faload         
        //   304: fastore        
        //   305: aload_0        
        //   306: getfield        com/google/vr/sdk/base/sensors/HeadTracker.initialSystemGyroBias:[F
        //   309: iconst_2       
        //   310: aload_1        
        //   311: getfield        android/hardware/SensorEvent.values:[F
        //   314: iconst_5       
        //   315: faload         
        //   316: fastore        
        //   317: goto            219
        //   320: aload_0        
        //   321: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBiasEstimator:Lcom/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator;
        //   324: aload_0        
        //   325: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestGyro:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   328: aload_1        
        //   329: getfield        android/hardware/SensorEvent.timestamp:J
        //   332: invokevirtual   com/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator.processGyroscope:(Lcom/google/vr/sdk/base/sensors/internal/Vector3d;J)V
        //   335: aload_0        
        //   336: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBiasEstimator:Lcom/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator;
        //   339: aload_0        
        //   340: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBias:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   343: invokevirtual   com/google/vr/sdk/base/sensors/internal/GyroscopeBiasEstimator.getGyroBias:(Lcom/google/vr/sdk/base/sensors/internal/Vector3d;)V
        //   346: aload_0        
        //   347: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestGyro:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   350: aload_0        
        //   351: getfield        com/google/vr/sdk/base/sensors/HeadTracker.gyroBias:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   354: aload_0        
        //   355: getfield        com/google/vr/sdk/base/sensors/HeadTracker.latestGyro:Lcom/google/vr/sdk/base/sensors/internal/Vector3d;
        //   358: invokestatic    com/google/vr/sdk/base/sensors/internal/Vector3d.sub:(Lcom/google/vr/sdk/base/sensors/internal/Vector3d;Lcom/google/vr/sdk/base/sensors/internal/Vector3d;Lcom/google/vr/sdk/base/sensors/internal/Vector3d;)V
        //   361: goto            94
        //   364: astore_1       
        //   365: aload_2        
        //   366: monitorexit    
        //   367: aload_1        
        //   368: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  87     94     364    369    Any
        //  94     96     364    369    Any
        //  162    169    174    179    Any
        //  169    171    174    179    Any
        //  175    177    174    179    Any
        //  179    194    174    179    Any
        //  320    361    364    369    Any
        //  365    367    364    369    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(invokevirtual:int(Sensor::getType, getfield:Sensor(SensorEvent::sensor, var_1_16C:Object[expected:SensorEvent])), ldc:int(16))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void resetTracker() {
        this.tracker.reset();
    }
    
    void setGyroBiasEstimator(final GyroscopeBiasEstimator gyroBiasEstimator) {
        synchronized (this.gyroBiasEstimatorMutex) {
            this.gyroBiasEstimator = gyroBiasEstimator;
        }
    }
    
    public void setNeckModelEnabled(final boolean b) {
        if (!b) {
            this.setNeckModelFactor(0.0f);
        }
        else {
            this.setNeckModelFactor(1.0f);
        }
    }
    
    public void setNeckModelFactor(final float neckModelFactor) {
        final Object neckModelFactorMutex = this.neckModelFactorMutex;
        monitorenter(neckModelFactorMutex);
        while (true) {
            Label_0048: {
                if (neckModelFactor >= 0.0f) {
                    break Label_0048;
                }
                final int n = 1;
                if (n == 0) {
                    if (neckModelFactor <= 1.0f) {
                        this.neckModelFactor = neckModelFactor;
                        monitorexit(neckModelFactorMutex);
                        return;
                    }
                }
                try {
                    throw new IllegalArgumentException("factor should be within [0.0, 1.0]");
                }
                finally {
                    monitorexit(neckModelFactorMutex);
                }
            }
            final int n = 0;
            continue;
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
            monitorexit(this.gyroBiasEstimatorMutex);
            this.firstGyroValue = true;
            this.sensorEventProvider.registerListener((SensorEventListener)this);
            this.sensorEventProvider.start();
            this.tracking = true;
        }
    }
    
    public void stopTracking() {
        if (this.tracking) {
            this.sensorEventProvider.unregisterListener((SensorEventListener)this);
            this.sensorEventProvider.stop();
            this.tracking = false;
        }
    }
}
