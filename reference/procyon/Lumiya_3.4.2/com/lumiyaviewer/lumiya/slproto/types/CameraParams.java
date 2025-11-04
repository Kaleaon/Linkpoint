// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class CameraParams
{
    private static final float FLING_DECEL_PITCH = 100.0f;
    private static final float FLING_DECEL_YAW = 50.0f;
    private static final float MAX_PITCH = 85.0f;
    private static final float MIN_PITCH = -85.0f;
    private long flingStartTime;
    private float heading;
    private float headingFlingSpeed;
    private boolean isFlinging;
    private boolean isManualControl;
    private boolean isValid;
    private final Object lock;
    private long manualControlStartTime;
    private float manualFlySpeed;
    private float manualMoveSpeed;
    private float manualStrafeSpeed;
    private float manualTurnSpeed;
    private final LLVector3 offset;
    private final LLVector3 offsetVR;
    @Nonnull
    private LLVector3 position;
    private float tilt;
    private float tiltFlingSpeed;
    private boolean useOffset;
    
    public CameraParams() {
        this.lock = new Object();
        this.offset = new LLVector3(-2.0f, 0.0f, 1.0f);
        this.offsetVR = new LLVector3(0.0f, 0.0f, 1.0f);
        this.position = new LLVector3();
        this.heading = 0.0f;
        this.tilt = 0.0f;
        this.isValid = false;
        this.useOffset = false;
        this.isFlinging = false;
        this.headingFlingSpeed = 0.0f;
        this.tiltFlingSpeed = 0.0f;
        this.flingStartTime = 0L;
        this.isManualControl = false;
        this.manualControlStartTime = 0L;
        this.manualTurnSpeed = 0.0f;
        this.manualMoveSpeed = 0.0f;
        this.manualFlySpeed = 0.0f;
        this.manualStrafeSpeed = 0.0f;
    }
    
    public static float angleMinusAngle(final float n, final float n2) {
        return wrapAngle(wrapAngle(n) - wrapAngle(n2));
    }
    
    private void processFling() {
        if (this.isFlinging) {
            final long currentTimeMillis = System.currentTimeMillis();
            final float n = (currentTimeMillis - this.flingStartTime) / 1000.0f;
            this.heading = wrapAngle(this.heading + this.headingFlingSpeed * n);
            this.tilt = Math.max(Math.min(this.tilt + this.tiltFlingSpeed * n, 85.0f), -85.0f);
            if (this.headingFlingSpeed > 0.0f) {
                this.headingFlingSpeed -= 100.0f * n;
                if (this.headingFlingSpeed < 0.0f) {
                    this.headingFlingSpeed = 0.0f;
                }
            }
            else if (this.headingFlingSpeed < 0.0f) {
                this.headingFlingSpeed += 100.0f * n;
                if (this.headingFlingSpeed > 0.0f) {
                    this.headingFlingSpeed = 0.0f;
                }
            }
            if (this.tiltFlingSpeed > 0.0f) {
                this.tiltFlingSpeed -= n * 50.0f;
                if (this.tiltFlingSpeed < 0.0f) {
                    this.tiltFlingSpeed = 0.0f;
                }
            }
            else if (this.tiltFlingSpeed < 0.0f) {
                this.tiltFlingSpeed += n * 50.0f;
                if (this.tiltFlingSpeed > 0.0f) {
                    this.tiltFlingSpeed = 0.0f;
                }
            }
            this.flingStartTime = currentTimeMillis;
            if (this.tiltFlingSpeed == 0.0f && this.headingFlingSpeed == 0.0f) {
                this.isFlinging = false;
            }
        }
    }
    
    private void processManualControl(final HeadTransformCompat headTransformCompat) {
        if (this.isManualControl) {
            final long currentTimeMillis = System.currentTimeMillis();
            final float n = (currentTimeMillis - this.manualControlStartTime) / 1000.0f;
            float n2;
            float n3;
            if (headTransformCompat != null) {
                n2 = wrapAngle(headTransformCompat.yawDegrees + headTransformCompat.viewExtraYaw);
                n3 = headTransformCompat.pitchDegrees;
            }
            else {
                this.heading = wrapAngle(this.heading + this.manualTurnSpeed * n);
                n2 = this.heading;
                n3 = this.tilt;
            }
            if (this.manualMoveSpeed != 0.0f || this.manualFlySpeed != 0.0f || this.manualStrafeSpeed != 0.0f) {
                final LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, n3, n2, LLQuaternion.Order.YZX);
                final LLVector3 llVector3 = new LLVector3(1.0f, 0.0f, 0.0f);
                final LLVector3 llVector4 = new LLVector3(0.0f, 0.0f, 1.0f);
                final LLVector3 llVector5 = new LLVector3(0.0f, 1.0f, 0.0f);
                llVector3.mul(mayaQ);
                llVector4.mul(mayaQ);
                llVector5.mul(mayaQ);
                this.position.addMul(llVector3, this.manualMoveSpeed * n);
                this.position.addMul(llVector4, this.manualFlySpeed * n);
                this.position.addMul(llVector5, this.manualStrafeSpeed * n);
            }
            this.manualControlStartTime = currentTimeMillis;
        }
    }
    
    public static float wrapAngle(float n) {
        final float n2 = n = (n + 180.0f) % 360.0f;
        if (n2 < 0.0f) {
            n = n2 + 360.0f;
        }
        return n - 180.0f;
    }
    
    public void copyFrom(@Nullable final CameraParams p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnull          181
        //     4: aload_1        
        //     5: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.lock:Ljava/lang/Object;
        //     8: astore_2       
        //     9: aload_2        
        //    10: monitorenter   
        //    11: aload_1        
        //    12: invokespecial   com/lumiyaviewer/lumiya/slproto/types/CameraParams.processFling:()V
        //    15: aload_1        
        //    16: aconst_null    
        //    17: invokespecial   com/lumiyaviewer/lumiya/slproto/types/CameraParams.processManualControl:(Lcom/lumiyaviewer/lumiya/render/HeadTransformCompat;)V
        //    20: aload_1        
        //    21: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    24: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.x:F
        //    27: fstore_3       
        //    28: aload_1        
        //    29: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    32: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.y:F
        //    35: fstore          4
        //    37: aload_1        
        //    38: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    41: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.z:F
        //    44: fstore          5
        //    46: aload_1        
        //    47: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.heading:F
        //    50: fstore          6
        //    52: aload_1        
        //    53: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.tilt:F
        //    56: fstore          7
        //    58: aload_1        
        //    59: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.isValid:Z
        //    62: istore          8
        //    64: fload           5
        //    66: fstore          9
        //    68: fload           4
        //    70: fstore          10
        //    72: fload_3        
        //    73: fstore          11
        //    75: aload_1        
        //    76: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.useOffset:Z
        //    79: ifeq            139
        //    82: new             Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    85: astore_1       
        //    86: aload_1        
        //    87: aload_0        
        //    88: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.offset:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    91: invokespecial   com/lumiyaviewer/lumiya/slproto/types/LLVector3.<init>:(Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;)V
        //    94: aload_1        
        //    95: fconst_0       
        //    96: fload           7
        //    98: fload           6
        //   100: getstatic       com/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order.YZX:Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order;
        //   103: invokestatic    com/lumiyaviewer/lumiya/slproto/types/LLQuaternion.mayaQ:(FFFLcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order;)Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion;
        //   106: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/LLVector3.mul:(Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion;)V
        //   109: fload_3        
        //   110: aload_1        
        //   111: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.x:F
        //   114: fadd           
        //   115: fstore          11
        //   117: fload           4
        //   119: aload_1        
        //   120: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.y:F
        //   123: fadd           
        //   124: fstore          10
        //   126: aload_1        
        //   127: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.z:F
        //   130: fstore          9
        //   132: fload           5
        //   134: fload           9
        //   136: fadd           
        //   137: fstore          9
        //   139: aload_2        
        //   140: monitorexit    
        //   141: aload_0        
        //   142: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.lock:Ljava/lang/Object;
        //   145: astore_2       
        //   146: aload_2        
        //   147: monitorenter   
        //   148: aload_0        
        //   149: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   152: fload           11
        //   154: fload           10
        //   156: fload           9
        //   158: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/LLVector3.set:(FFF)V
        //   161: aload_0        
        //   162: fload           6
        //   164: putfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.heading:F
        //   167: aload_0        
        //   168: fload           7
        //   170: putfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.tilt:F
        //   173: aload_0        
        //   174: iload           8
        //   176: putfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.isValid:Z
        //   179: aload_2        
        //   180: monitorexit    
        //   181: return         
        //   182: astore_1       
        //   183: aload_2        
        //   184: monitorexit    
        //   185: aload_1        
        //   186: athrow         
        //   187: astore_1       
        //   188: aload_2        
        //   189: monitorexit    
        //   190: aload_1        
        //   191: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  11     64     182    187    Any
        //  75     132    182    187    Any
        //  148    179    187    192    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0181:
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
    
    public void fling(final float headingFlingSpeed, final float tiltFlingSpeed) {
        synchronized (this.lock) {
            this.headingFlingSpeed = headingFlingSpeed;
            this.tiltFlingSpeed = tiltFlingSpeed;
            this.flingStartTime = System.currentTimeMillis();
            this.isFlinging = true;
        }
    }
    
    public float getHeading() {
        synchronized (this.lock) {
            return this.heading;
        }
    }
    
    @Nonnull
    public LLVector3 getPosition() {
        synchronized (this.lock) {
            return this.position;
        }
    }
    
    public float getTilt() {
        synchronized (this.lock) {
            return this.tilt;
        }
    }
    
    public void getVRCamera(@Nullable final CameraParams p0, final HeadTransformCompat p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnull          180
        //     4: aload_1        
        //     5: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.lock:Ljava/lang/Object;
        //     8: astore_3       
        //     9: aload_3        
        //    10: monitorenter   
        //    11: aload_1        
        //    12: aload_2        
        //    13: invokespecial   com/lumiyaviewer/lumiya/slproto/types/CameraParams.processManualControl:(Lcom/lumiyaviewer/lumiya/render/HeadTransformCompat;)V
        //    16: aload_1        
        //    17: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    20: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.x:F
        //    23: fstore          4
        //    25: aload_1        
        //    26: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    29: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.y:F
        //    32: fstore          5
        //    34: aload_1        
        //    35: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    38: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.z:F
        //    41: fstore          6
        //    43: aload_1        
        //    44: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.heading:F
        //    47: fstore          7
        //    49: aload_1        
        //    50: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.tilt:F
        //    53: fstore          8
        //    55: aload_1        
        //    56: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.isValid:Z
        //    59: istore          9
        //    61: fload           6
        //    63: fstore          10
        //    65: fload           5
        //    67: fstore          11
        //    69: fload           4
        //    71: fstore          12
        //    73: aload_1        
        //    74: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.useOffset:Z
        //    77: ifeq            138
        //    80: new             Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    83: astore_1       
        //    84: aload_1        
        //    85: aload_0        
        //    86: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.offsetVR:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //    89: invokespecial   com/lumiyaviewer/lumiya/slproto/types/LLVector3.<init>:(Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;)V
        //    92: aload_1        
        //    93: fconst_0       
        //    94: fload           8
        //    96: fload           7
        //    98: getstatic       com/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order.YZX:Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order;
        //   101: invokestatic    com/lumiyaviewer/lumiya/slproto/types/LLQuaternion.mayaQ:(FFFLcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order;)Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion;
        //   104: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/LLVector3.mul:(Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion;)V
        //   107: fload           4
        //   109: aload_1        
        //   110: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.x:F
        //   113: fadd           
        //   114: fstore          12
        //   116: fload           5
        //   118: aload_1        
        //   119: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.y:F
        //   122: fadd           
        //   123: fstore          11
        //   125: aload_1        
        //   126: getfield        com/lumiyaviewer/lumiya/slproto/types/LLVector3.z:F
        //   129: fstore          10
        //   131: fload           6
        //   133: fload           10
        //   135: fadd           
        //   136: fstore          10
        //   138: aload_3        
        //   139: monitorexit    
        //   140: aload_0        
        //   141: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.lock:Ljava/lang/Object;
        //   144: astore_1       
        //   145: aload_1        
        //   146: monitorenter   
        //   147: aload_0        
        //   148: getfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.position:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   151: fload           12
        //   153: fload           11
        //   155: fload           10
        //   157: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/LLVector3.set:(FFF)V
        //   160: aload_0        
        //   161: fload           7
        //   163: putfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.heading:F
        //   166: aload_0        
        //   167: fload           8
        //   169: putfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.tilt:F
        //   172: aload_0        
        //   173: iload           9
        //   175: putfield        com/lumiyaviewer/lumiya/slproto/types/CameraParams.isValid:Z
        //   178: aload_1        
        //   179: monitorexit    
        //   180: return         
        //   181: astore_1       
        //   182: aload_3        
        //   183: monitorexit    
        //   184: aload_1        
        //   185: athrow         
        //   186: astore_2       
        //   187: aload_1        
        //   188: monitorexit    
        //   189: aload_2        
        //   190: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  11     61     181    186    Any
        //  73     131    181    186    Any
        //  147    178    186    191    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0180:
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
    
    public boolean isFlinging() {
        synchronized (this.lock) {
            return this.isFlinging;
        }
    }
    
    public boolean isValid() {
        synchronized (this.lock) {
            return this.isValid;
        }
    }
    
    public void rotate(final float n, final float n2) {
        synchronized (this.lock) {
            this.heading = wrapAngle(this.heading + n);
            this.tilt = Math.max(Math.min(this.tilt + n2, 85.0f), -85.0f);
            this.isFlinging = false;
        }
    }
    
    public void set(@Nullable final LLVector3 llVector3, final float heading, final float tilt) {
        final Object lock = this.lock;
        monitorenter(lock);
        Label_0021: {
            if (llVector3 == null) {
                break Label_0021;
            }
            try {
                this.position.set(llVector3);
                this.heading = heading;
                this.tilt = tilt;
                this.isValid = true;
            }
            finally {
                monitorexit(lock);
            }
        }
    }
    
    public void setHeading(final float heading) {
        synchronized (this.lock) {
            this.heading = heading;
        }
    }
    
    public void setPosition(@Nullable final LLVector3 llVector3) {
        final Object lock = this.lock;
        monitorenter(lock);
        Label_0019: {
            if (llVector3 == null) {
                break Label_0019;
            }
            try {
                this.position.set(llVector3);
                this.useOffset = true;
                this.isValid = true;
            }
            finally {
                monitorexit(lock);
            }
        }
    }
    
    public void setPosition(@Nullable final LLVector3 llVector3, final float heading) {
        final Object lock = this.lock;
        monitorenter(lock);
        Label_0019: {
            if (llVector3 == null) {
                break Label_0019;
            }
            try {
                this.position.set(llVector3);
                this.heading = heading;
                this.tilt = 0.0f;
                this.isFlinging = false;
                this.isValid = true;
                this.useOffset = true;
            }
            finally {
                monitorexit(lock);
            }
        }
    }
    
    public void startManualControl(final float manualTurnSpeed, final float manualMoveSpeed, final float manualFlySpeed, final float manualStrafeSpeed) {
        synchronized (this.lock) {
            if (!this.isManualControl) {
                final LLVector3 llVector3 = new LLVector3(this.position);
                final LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, this.tilt, this.heading, LLQuaternion.Order.YZX);
                if (!this.useOffset) {
                    final LLVector3 llVector4 = new LLVector3(this.offset);
                    llVector4.mul(mayaQ);
                    llVector3.add(llVector4);
                    this.useOffset = true;
                }
                this.position.set(llVector3);
                this.isManualControl = true;
                this.manualControlStartTime = System.currentTimeMillis();
            }
            this.manualMoveSpeed = manualMoveSpeed;
            this.manualTurnSpeed = manualTurnSpeed;
            this.manualFlySpeed = manualFlySpeed;
            this.manualStrafeSpeed = manualStrafeSpeed;
        }
    }
    
    public void stopManualControl() {
        synchronized (this.lock) {
            this.isManualControl = false;
        }
    }
    
    public void zoom(float n, final float n2, final float n3, final float n4, final float n5) {
        final Object lock = this.lock;
        monitorenter(lock);
        --n;
        try {
            final LLVector3 llVector3 = new LLVector3(this.position);
            final LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, this.tilt, this.heading, LLQuaternion.Order.YZX);
            if (!this.useOffset) {
                final LLVector3 llVector4 = new LLVector3(this.offset);
                llVector4.mul(mayaQ);
                llVector3.add(llVector4);
                this.useOffset = true;
            }
            final LLVector3 llVector5 = new LLVector3(1.0f, 0.0f, 0.0f);
            final LLVector3 llVector6 = new LLVector3(0.0f, 0.0f, 1.0f);
            final LLVector3 llVector7 = new LLVector3(0.0f, 1.0f, 0.0f);
            llVector5.mul(mayaQ);
            llVector5.mul(n);
            llVector6.mul(mayaQ);
            llVector6.mul(n * n3 + n5);
            llVector7.mul(mayaQ);
            llVector7.mul(n * n2 + n4);
            llVector3.add(llVector5);
            llVector3.add(llVector6);
            llVector3.add(llVector7);
            this.position.set(llVector3);
        }
        finally {
            monitorexit(lock);
        }
    }
}
