// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import com.google.vrtoolkit.cardboard.proto.nano.Preferences;
import android.util.Base64;
import com.google.vr.ndk.base.SdkConfigurationReader;
import com.google.common.logging.nano.Vr;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import android.os.RemoteException;
import android.util.Log;
import android.content.ContentValues;
import com.google.protobuf.nano.MessageNano;
import android.net.Uri;
import android.content.ContentProviderClient;

public class ContentProviderVrParamsProvider implements VrParamsProvider
{
    private static final String TAG;
    private final ContentProviderClient client;
    private final Uri deviceParamsSettingUri;
    private final Uri phoneParamsSettingUri;
    private final Uri sdkConfigurationParamsSettingUri;
    private final Uri userPrefsUri;
    
    static {
        TAG = ContentProviderVrParamsProvider.class.getSimpleName();
    }
    
    public ContentProviderVrParamsProvider(final ContentProviderClient client, final String s) {
        if (client == null) {
            throw new IllegalArgumentException("ContentProviderClient must not be null");
        }
        if (s != null && !s.isEmpty()) {
            this.client = client;
            this.deviceParamsSettingUri = VrSettingsProviderContract.createUri(s, "device_params");
            this.userPrefsUri = VrSettingsProviderContract.createUri(s, "user_prefs");
            this.phoneParamsSettingUri = VrSettingsProviderContract.createUri(s, "phone_params");
            this.sdkConfigurationParamsSettingUri = VrSettingsProviderContract.createUri(s, "sdk_configuration_params");
            return;
        }
        throw new IllegalArgumentException("Authority key must be non-null and non-empty");
    }
    
    private <T extends MessageNano> T readParams(final T p0, final Uri p1, final String p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/google/vr/cardboard/ContentProviderVrParamsProvider.client:Landroid/content/ContentProviderClient;
        //     4: aload_2        
        //     5: aconst_null    
        //     6: aload_3        
        //     7: aconst_null    
        //     8: aconst_null    
        //     9: invokevirtual   android/content/ContentProviderClient.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //    12: astore          4
        //    14: aload           4
        //    16: ifnonnull       96
        //    19: aload           4
        //    21: astore_3       
        //    22: getstatic       com/google/vr/cardboard/ContentProviderVrParamsProvider.TAG:Ljava/lang/String;
        //    25: astore_1       
        //    26: aload           4
        //    28: astore_3       
        //    29: aload_2        
        //    30: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    33: astore          5
        //    35: aload           4
        //    37: astore_3       
        //    38: aload           5
        //    40: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    43: invokevirtual   java/lang/String.length:()I
        //    46: istore          6
        //    48: aload           4
        //    50: astore_3       
        //    51: new             Ljava/lang/StringBuilder;
        //    54: astore_2       
        //    55: aload           4
        //    57: astore_3       
        //    58: aload_2        
        //    59: iload           6
        //    61: bipush          50
        //    63: iadd           
        //    64: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //    67: aload           4
        //    69: astore_3       
        //    70: aload_1        
        //    71: aload_2        
        //    72: ldc             "Invalid params result from ContentProvider query: "
        //    74: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    77: aload           5
        //    79: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    82: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    85: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //    88: pop            
        //    89: aload           4
        //    91: ifnonnull       168
        //    94: aconst_null    
        //    95: areturn        
        //    96: aload           4
        //    98: astore_3       
        //    99: aload           4
        //   101: invokeinterface android/database/Cursor.moveToFirst:()Z
        //   106: ifeq            19
        //   109: aload           4
        //   111: astore_3       
        //   112: aload           4
        //   114: iconst_0       
        //   115: invokeinterface android/database/Cursor.getBlob:(I)[B
        //   120: astore_2       
        //   121: aload_2        
        //   122: ifnull          141
        //   125: aload           4
        //   127: astore_3       
        //   128: aload_1        
        //   129: aload_2        
        //   130: invokestatic    com/google/protobuf/nano/MessageNano.mergeFrom:(Lcom/google/protobuf/nano/MessageNano;[B)Lcom/google/protobuf/nano/MessageNano;
        //   133: astore_1       
        //   134: aload           4
        //   136: ifnonnull       158
        //   139: aload_1        
        //   140: areturn        
        //   141: aload           4
        //   143: ifnonnull       148
        //   146: aconst_null    
        //   147: areturn        
        //   148: aload           4
        //   150: invokeinterface android/database/Cursor.close:()V
        //   155: goto            146
        //   158: aload           4
        //   160: invokeinterface android/database/Cursor.close:()V
        //   165: goto            139
        //   168: aload           4
        //   170: invokeinterface android/database/Cursor.close:()V
        //   175: goto            94
        //   178: astore_1       
        //   179: aconst_null    
        //   180: astore          4
        //   182: aload           4
        //   184: astore_3       
        //   185: getstatic       com/google/vr/cardboard/ContentProviderVrParamsProvider.TAG:Ljava/lang/String;
        //   188: ldc             "Error reading params from ContentProvider"
        //   190: aload_1        
        //   191: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   194: pop            
        //   195: aload           4
        //   197: ifnonnull       202
        //   200: aconst_null    
        //   201: areturn        
        //   202: aload           4
        //   204: invokeinterface android/database/Cursor.close:()V
        //   209: goto            200
        //   212: astore_1       
        //   213: aconst_null    
        //   214: astore_3       
        //   215: aload_3        
        //   216: ifnonnull       221
        //   219: aload_1        
        //   220: athrow         
        //   221: aload_3        
        //   222: invokeinterface android/database/Cursor.close:()V
        //   227: goto            219
        //   230: astore_1       
        //   231: goto            215
        //   234: astore_1       
        //   235: goto            182
        //    Signature:
        //  <T:Lcom/google/protobuf/nano/MessageNano;>(TT;Landroid/net/Uri;Ljava/lang/String;)TT;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                         
        //  -----  -----  -----  -----  -------------------------------------------------------------
        //  0      14     178    182    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  0      14     178    182    Landroid/database/CursorIndexOutOfBoundsException;
        //  0      14     178    182    Ljava/lang/IllegalArgumentException;
        //  0      14     178    182    Landroid/os/RemoteException;
        //  0      14     178    182    Landroid/database/CursorIndexOutOfBoundsException;
        //  0      14     178    182    Ljava/lang/IllegalArgumentException;
        //  0      14     178    182    Landroid/os/RemoteException;
        //  0      14     178    182    Landroid/database/CursorIndexOutOfBoundsException;
        //  0      14     178    182    Ljava/lang/IllegalArgumentException;
        //  0      14     178    182    Landroid/os/RemoteException;
        //  0      14     212    215    Any
        //  22     26     234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  22     26     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  22     26     234    238    Ljava/lang/IllegalArgumentException;
        //  22     26     234    238    Landroid/os/RemoteException;
        //  22     26     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  22     26     234    238    Ljava/lang/IllegalArgumentException;
        //  22     26     234    238    Landroid/os/RemoteException;
        //  22     26     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  22     26     234    238    Ljava/lang/IllegalArgumentException;
        //  22     26     234    238    Landroid/os/RemoteException;
        //  22     26     230    234    Any
        //  29     35     234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  29     35     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  29     35     234    238    Ljava/lang/IllegalArgumentException;
        //  29     35     234    238    Landroid/os/RemoteException;
        //  29     35     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  29     35     234    238    Ljava/lang/IllegalArgumentException;
        //  29     35     234    238    Landroid/os/RemoteException;
        //  29     35     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  29     35     234    238    Ljava/lang/IllegalArgumentException;
        //  29     35     234    238    Landroid/os/RemoteException;
        //  29     35     230    234    Any
        //  38     48     234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  38     48     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  38     48     234    238    Ljava/lang/IllegalArgumentException;
        //  38     48     234    238    Landroid/os/RemoteException;
        //  38     48     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  38     48     234    238    Ljava/lang/IllegalArgumentException;
        //  38     48     234    238    Landroid/os/RemoteException;
        //  38     48     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  38     48     234    238    Ljava/lang/IllegalArgumentException;
        //  38     48     234    238    Landroid/os/RemoteException;
        //  38     48     230    234    Any
        //  51     55     234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  51     55     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  51     55     234    238    Ljava/lang/IllegalArgumentException;
        //  51     55     234    238    Landroid/os/RemoteException;
        //  51     55     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  51     55     234    238    Ljava/lang/IllegalArgumentException;
        //  51     55     234    238    Landroid/os/RemoteException;
        //  51     55     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  51     55     234    238    Ljava/lang/IllegalArgumentException;
        //  51     55     234    238    Landroid/os/RemoteException;
        //  51     55     230    234    Any
        //  58     67     234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  58     67     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  58     67     234    238    Ljava/lang/IllegalArgumentException;
        //  58     67     234    238    Landroid/os/RemoteException;
        //  58     67     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  58     67     234    238    Ljava/lang/IllegalArgumentException;
        //  58     67     234    238    Landroid/os/RemoteException;
        //  58     67     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  58     67     234    238    Ljava/lang/IllegalArgumentException;
        //  58     67     234    238    Landroid/os/RemoteException;
        //  58     67     230    234    Any
        //  70     89     234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  70     89     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  70     89     234    238    Ljava/lang/IllegalArgumentException;
        //  70     89     234    238    Landroid/os/RemoteException;
        //  70     89     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  70     89     234    238    Ljava/lang/IllegalArgumentException;
        //  70     89     234    238    Landroid/os/RemoteException;
        //  70     89     234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  70     89     234    238    Ljava/lang/IllegalArgumentException;
        //  70     89     234    238    Landroid/os/RemoteException;
        //  70     89     230    234    Any
        //  99     109    234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  99     109    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  99     109    234    238    Ljava/lang/IllegalArgumentException;
        //  99     109    234    238    Landroid/os/RemoteException;
        //  99     109    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  99     109    234    238    Ljava/lang/IllegalArgumentException;
        //  99     109    234    238    Landroid/os/RemoteException;
        //  99     109    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  99     109    234    238    Ljava/lang/IllegalArgumentException;
        //  99     109    234    238    Landroid/os/RemoteException;
        //  99     109    230    234    Any
        //  112    121    234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  112    121    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  112    121    234    238    Ljava/lang/IllegalArgumentException;
        //  112    121    234    238    Landroid/os/RemoteException;
        //  112    121    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  112    121    234    238    Ljava/lang/IllegalArgumentException;
        //  112    121    234    238    Landroid/os/RemoteException;
        //  112    121    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  112    121    234    238    Ljava/lang/IllegalArgumentException;
        //  112    121    234    238    Landroid/os/RemoteException;
        //  112    121    230    234    Any
        //  128    134    234    238    Lcom/google/protobuf/nano/InvalidProtocolBufferNanoException;
        //  128    134    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  128    134    234    238    Ljava/lang/IllegalArgumentException;
        //  128    134    234    238    Landroid/os/RemoteException;
        //  128    134    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  128    134    234    238    Ljava/lang/IllegalArgumentException;
        //  128    134    234    238    Landroid/os/RemoteException;
        //  128    134    234    238    Landroid/database/CursorIndexOutOfBoundsException;
        //  128    134    234    238    Ljava/lang/IllegalArgumentException;
        //  128    134    234    238    Landroid/os/RemoteException;
        //  128    134    230    234    Any
        //  185    195    230    234    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0094:
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
    
    private boolean writeParams(final MessageNano messageNano, final Uri uri) {
        Label_0042: {
            if (messageNano == null) {
                break Label_0042;
            }
            try {
                final ContentValues contentValues = new ContentValues();
                contentValues.put("value", MessageNano.toByteArray(messageNano));
                int n = this.client.update(uri, contentValues, (String)null, (String[])null);
                return n > 0;
                n = this.client.delete(uri, (String)null, (String[])null);
                return n > 0;
            }
            catch (final RemoteException ex) {
                Log.e(ContentProviderVrParamsProvider.TAG, "Failed to write params to ContentProvider", (Throwable)ex);
                return false;
            }
            catch (final SecurityException ex2) {
                Log.e(ContentProviderVrParamsProvider.TAG, "Insufficient permissions to write params to ContentProvider", (Throwable)ex2);
                return false;
            }
        }
    }
    
    @Override
    public void close() {
        this.client.release();
    }
    
    @Override
    public CardboardDevice.DeviceParams readDeviceParams() {
        return this.readParams(new CardboardDevice.DeviceParams(), this.deviceParamsSettingUri, null);
    }
    
    @Override
    public Phone.PhoneParams readPhoneParams() {
        return this.readParams(new Phone.PhoneParams(), this.phoneParamsSettingUri, null);
    }
    
    @Override
    public Vr.VREvent.SdkConfigurationParams readSdkConfigurationParams(final SdkConfiguration.SdkConfigurationRequest sdkConfigurationRequest) {
        return this.readParams(SdkConfigurationReader.DEFAULT_PARAMS, this.sdkConfigurationParamsSettingUri, Base64.encodeToString(MessageNano.toByteArray(sdkConfigurationRequest), 0));
    }
    
    @Override
    public Preferences.UserPrefs readUserPrefs() {
        return this.readParams(new Preferences.UserPrefs(), this.userPrefsUri, null);
    }
    
    @Override
    public boolean updateUserPrefs(final Preferences.UserPrefs userPrefs) {
        return this.writeParams(userPrefs, this.userPrefsUri);
    }
    
    @Override
    public boolean writeDeviceParams(final CardboardDevice.DeviceParams deviceParams) {
        return this.writeParams(deviceParams, this.deviceParamsSettingUri);
    }
}
