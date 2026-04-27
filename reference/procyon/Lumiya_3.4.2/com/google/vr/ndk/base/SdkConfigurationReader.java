// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import android.util.Log;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vr.cardboard.VrParamsProvider;
import android.content.Context;
import com.google.common.logging.nano.Vr;

public class SdkConfigurationReader
{
    public static final Vr.VREvent.SdkConfigurationParams DEFAULT_PARAMS;
    static final Vr.VREvent.SdkConfigurationParams REQUESTED_PARAMS;
    private static final String TAG;
    static Vr.VREvent.SdkConfigurationParams sParams;
    
    static {
        TAG = SdkConfigurationReader.class.getSimpleName();
        (REQUESTED_PARAMS = new Vr.VREvent.SdkConfigurationParams()).useSystemClockForSensorTimestamps = true;
        SdkConfigurationReader.REQUESTED_PARAMS.useMagnetometerInSensorFusion = true;
        SdkConfigurationReader.REQUESTED_PARAMS.allowDynamicLibraryLoading = true;
        SdkConfigurationReader.REQUESTED_PARAMS.cpuLateLatchingEnabled = true;
        SdkConfigurationReader.REQUESTED_PARAMS.daydreamImageAlignment = 1;
        SdkConfigurationReader.REQUESTED_PARAMS.asyncReprojectionConfig = new Vr.VREvent.SdkConfigurationParams.AsyncReprojectionConfig();
        SdkConfigurationReader.REQUESTED_PARAMS.useOnlineMagnetometerCalibration = true;
        (DEFAULT_PARAMS = new Vr.VREvent.SdkConfigurationParams()).useSystemClockForSensorTimestamps = false;
        SdkConfigurationReader.DEFAULT_PARAMS.useMagnetometerInSensorFusion = false;
        SdkConfigurationReader.DEFAULT_PARAMS.allowDynamicLibraryLoading = false;
        SdkConfigurationReader.DEFAULT_PARAMS.cpuLateLatchingEnabled = false;
        SdkConfigurationReader.DEFAULT_PARAMS.daydreamImageAlignment = 3;
        SdkConfigurationReader.DEFAULT_PARAMS.asyncReprojectionConfig = null;
        SdkConfigurationReader.DEFAULT_PARAMS.useOnlineMagnetometerCalibration = false;
    }
    
    public static Vr.VREvent.SdkConfigurationParams getParams(final Context p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: monitorenter   
        //     3: getstatic       com/google/vr/ndk/base/SdkConfigurationReader.sParams:Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
        //     6: ifnonnull       42
        //     9: ldc             Lcom/google/vr/ndk/base/SdkConfigurationReader;.class
        //    11: monitorexit    
        //    12: aload_0        
        //    13: invokestatic    com/google/vr/cardboard/VrParamsProviderFactory.create:(Landroid/content/Context;)Lcom/google/vr/cardboard/VrParamsProvider;
        //    16: astore_0       
        //    17: aload_0        
        //    18: invokestatic    com/google/vr/ndk/base/SdkConfigurationReader.readParamsFromProvider:(Lcom/google/vr/cardboard/VrParamsProvider;)Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
        //    21: astore_1       
        //    22: ldc             Lcom/google/vr/ndk/base/SdkConfigurationReader;.class
        //    24: monitorenter   
        //    25: aload_1        
        //    26: putstatic       com/google/vr/ndk/base/SdkConfigurationReader.sParams:Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
        //    29: ldc             Lcom/google/vr/ndk/base/SdkConfigurationReader;.class
        //    31: monitorexit    
        //    32: aload_0        
        //    33: invokeinterface com/google/vr/cardboard/VrParamsProvider.close:()V
        //    38: getstatic       com/google/vr/ndk/base/SdkConfigurationReader.sParams:Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
        //    41: areturn        
        //    42: getstatic       com/google/vr/ndk/base/SdkConfigurationReader.sParams:Lcom/google/common/logging/nano/Vr$VREvent$SdkConfigurationParams;
        //    45: astore_0       
        //    46: ldc             Lcom/google/vr/ndk/base/SdkConfigurationReader;.class
        //    48: monitorexit    
        //    49: aload_0        
        //    50: areturn        
        //    51: astore_0       
        //    52: ldc             Lcom/google/vr/ndk/base/SdkConfigurationReader;.class
        //    54: monitorexit    
        //    55: aload_0        
        //    56: athrow         
        //    57: astore_0       
        //    58: ldc             Lcom/google/vr/ndk/base/SdkConfigurationReader;.class
        //    60: monitorexit    
        //    61: aload_0        
        //    62: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  3      12     51     57     Any
        //  25     32     57     63     Any
        //  42     49     51     57     Any
        //  52     55     51     57     Any
        //  58     61     57     63     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0042:
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
    
    private static Vr.VREvent.SdkConfigurationParams readParamsFromProvider(final VrParamsProvider vrParamsProvider) {
        final SdkConfiguration.SdkConfigurationRequest sdkConfigurationRequest = new SdkConfiguration.SdkConfigurationRequest();
        sdkConfigurationRequest.requestedParams = SdkConfigurationReader.REQUESTED_PARAMS;
        sdkConfigurationRequest.sdkVersion = "1.10.0";
        Vr.VREvent.SdkConfigurationParams obj = vrParamsProvider.readSdkConfigurationParams(sdkConfigurationRequest);
        if (obj != null) {
            final String tag = SdkConfigurationReader.TAG;
            final String value = String.valueOf(obj);
            Log.d(tag, new StringBuilder(String.valueOf(value).length() + 38).append("Fetched params from VrParamsProvider: ").append(value).toString());
        }
        else {
            Log.w(SdkConfigurationReader.TAG, "VrParamsProvider returned null params, using defaults.");
            obj = SdkConfigurationReader.DEFAULT_PARAMS;
        }
        return obj;
    }
}
