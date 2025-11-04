// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import java.io.OutputStream;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import java.io.IOException;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import java.nio.ByteBuffer;
import java.io.InputStream;
import android.util.Log;
import com.google.protobuf.nano.MessageNano;
import android.util.Base64;
import android.net.Uri;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import android.os.Environment;
import java.io.File;

public class ConfigUtils
{
    public static final String CARDBOARD_CONFIG_FOLDER = "Cardboard";
    private static final String CARDBOARD_DEVICE_PARAMS_FILE = "current_device_params";
    private static final int CARDBOARD_DEVICE_PARAMS_STREAM_SENTINEL = 894990891;
    private static final String CARDBOARD_PHONE_PARAMS_FILE = "phone_params";
    private static final int CARDBOARD_PHONE_PARAMS_STREAM_SENTINEL = 779508118;
    private static final boolean DEBUG = false;
    private static final String TAG;
    public static final String URI_KEY_PARAMS = "p";
    
    static {
        TAG = ConfigUtils.class.getSimpleName();
    }
    
    private static File getConfigFile(String value) throws IllegalStateException {
        final File file = new File(Environment.getExternalStorageDirectory(), "Cardboard");
        if (file.exists()) {
            if (!file.isDirectory()) {
                value = String.valueOf(file);
                throw new IllegalStateException(new StringBuilder(String.valueOf(value).length() + 61).append(value).append(" already exists as a file, but is expected to be a directory.").toString());
            }
        }
        else {
            file.mkdirs();
        }
        return new File(file, value);
    }
    
    public static CardboardDevice.DeviceParams readDeviceParamsFromExternalStorage() {
        return readFromExternalStorage(CardboardDevice.DeviceParams.class, "current_device_params", 894990891, true);
    }
    
    public static CardboardDevice.DeviceParams readDeviceParamsFromUri(final Uri uri) {
        final String queryParameter = uri.getQueryParameter("p");
        Label_0037: {
            if (queryParameter == null) {
                break Label_0037;
            }
            try {
                return MessageNano.mergeFrom(new CardboardDevice.DeviceParams(), Base64.decode(queryParameter, 11));
                Log.w(ConfigUtils.TAG, "No Cardboard parameters in URI.");
                return null;
            }
            catch (final Exception obj) {
                final String tag = ConfigUtils.TAG;
                final String value = String.valueOf(obj);
                Log.w(tag, new StringBuilder(String.valueOf(value).length() + 46).append("Parsing cardboard parameters from URI failed: ").append(value).toString());
                return null;
            }
        }
    }
    
    private static <T extends MessageNano> T readFromExternalStorage(final Class<T> p0, final String p1, final int p2, final boolean p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore          4
        //     5: new             Ljava/io/FileInputStream;
        //     8: astore          5
        //    10: aload           5
        //    12: aload_1        
        //    13: invokestatic    com/google/vr/cardboard/ConfigUtils.getConfigFile:(Ljava/lang/String;)Ljava/io/File;
        //    16: invokespecial   java/io/FileInputStream.<init>:(Ljava/io/File;)V
        //    19: aload           4
        //    21: aload           5
        //    23: invokespecial   java/io/BufferedInputStream.<init>:(Ljava/io/InputStream;)V
        //    26: aload_0        
        //    27: aload           4
        //    29: iload_2        
        //    30: invokestatic    com/google/vr/cardboard/ConfigUtils.readFromInputStream:(Ljava/lang/Class;Ljava/io/InputStream;I)Lcom/google/protobuf/nano/MessageNano;
        //    33: astore_0       
        //    34: aload           4
        //    36: invokevirtual   java/io/InputStream.close:()V
        //    39: aload_0        
        //    40: areturn        
        //    41: astore_1       
        //    42: goto            39
        //    45: astore_0       
        //    46: aconst_null    
        //    47: astore_1       
        //    48: aload_1        
        //    49: ifnonnull       61
        //    52: aload_0        
        //    53: athrow         
        //    54: astore_1       
        //    55: iload_3        
        //    56: ifne            72
        //    59: aconst_null    
        //    60: areturn        
        //    61: aload_1        
        //    62: invokevirtual   java/io/InputStream.close:()V
        //    65: goto            52
        //    68: astore_1       
        //    69: goto            52
        //    72: getstatic       com/google/vr/cardboard/ConfigUtils.TAG:Ljava/lang/String;
        //    75: astore_0       
        //    76: aload_1        
        //    77: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    80: astore_1       
        //    81: aload_0        
        //    82: new             Ljava/lang/StringBuilder;
        //    85: dup            
        //    86: aload_1        
        //    87: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    90: invokevirtual   java/lang/String.length:()I
        //    93: bipush          39
        //    95: iadd           
        //    96: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //    99: ldc             "Parameters file not found for reading: "
        //   101: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   104: aload_1        
        //   105: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   108: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   111: invokestatic    android/util/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   114: pop            
        //   115: goto            59
        //   118: astore_1       
        //   119: getstatic       com/google/vr/cardboard/ConfigUtils.TAG:Ljava/lang/String;
        //   122: astore_0       
        //   123: aload_1        
        //   124: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   127: astore_1       
        //   128: aload_0        
        //   129: new             Ljava/lang/StringBuilder;
        //   132: dup            
        //   133: aload_1        
        //   134: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   137: invokevirtual   java/lang/String.length:()I
        //   140: bipush          26
        //   142: iadd           
        //   143: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //   146: ldc             "Error reading parameters: "
        //   148: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   151: aload_1        
        //   152: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   155: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   158: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //   161: pop            
        //   162: goto            59
        //   165: astore_0       
        //   166: aload           4
        //   168: astore_1       
        //   169: goto            48
        //    Signature:
        //  <T:Lcom/google/protobuf/nano/MessageNano;>(Ljava/lang/Class<TT;>;Ljava/lang/String;IZ)TT;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  0      26     45     48     Any
        //  26     34     165    172    Any
        //  34     39     41     45     Ljava/io/IOException;
        //  34     39     54     59     Ljava/io/FileNotFoundException;
        //  34     39     118    165    Ljava/lang/IllegalStateException;
        //  52     54     54     59     Ljava/io/FileNotFoundException;
        //  52     54     118    165    Ljava/lang/IllegalStateException;
        //  61     65     68     72     Ljava/io/IOException;
        //  61     65     54     59     Ljava/io/FileNotFoundException;
        //  61     65     118    165    Ljava/lang/IllegalStateException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0039:
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
    
    private static <T extends MessageNano> T readFromInputStream(final Class<T> clazz, final InputStream inputStream, final int n) {
        Label_0075: {
            if (inputStream == null) {
                break Label_0075;
            }
            try {
                final ByteBuffer allocate = ByteBuffer.allocate(8);
                if (inputStream.read(allocate.array(), 0, allocate.array().length) == -1) {
                    Log.e(ConfigUtils.TAG, "Error parsing param record: end of stream.");
                    return null;
                }
                final int int1 = allocate.getInt();
                final int int2 = allocate.getInt();
                if (int1 != n) {
                    Log.e(ConfigUtils.TAG, "Error parsing param record: incorrect sentinel.");
                    return null;
                }
                final byte[] b = new byte[int2];
                if (inputStream.read(b, 0, int2) != -1) {
                    return MessageNano.mergeFrom(clazz.newInstance(), b);
                }
                Log.e(ConfigUtils.TAG, "Error parsing param record: end of stream.");
                return null;
                return null;
            }
            catch (final InvalidProtocolBufferNanoException ex) {
                final String tag = ConfigUtils.TAG;
                if (String.valueOf(ex.toString()).length() != 0) {
                    goto Label_0148;
                }
                Log.w(tag, new String("Error parsing protocol buffer: "));
            }
            catch (final IOException ex2) {
                final String tag2 = ConfigUtils.TAG;
                final String value = String.valueOf(ex2.toString());
                String concat;
                if (value.length() == 0) {
                    concat = new String("Error reading parameters: ");
                }
                else {
                    concat = "Error reading parameters: ".concat(value);
                }
                Log.w(tag2, concat);
                goto Label_0146;
            }
            catch (final InstantiationException ex3) {
                final String tag3 = ConfigUtils.TAG;
                final String value2 = String.valueOf(ex3.toString());
                String concat2;
                if (value2.length() == 0) {
                    concat2 = new String("Error creating parameters: ");
                }
                else {
                    concat2 = "Error creating parameters: ".concat(value2);
                }
                Log.w(tag3, concat2);
                goto Label_0146;
            }
            catch (final IllegalAccessException ex4) {
                final String tag4 = ConfigUtils.TAG;
                final String value3 = String.valueOf(ex4.toString());
                String concat3;
                if (value3.length() == 0) {
                    concat3 = new String("Error accessing parameter type: ");
                }
                else {
                    concat3 = "Error accessing parameter type: ".concat(value3);
                }
                Log.w(tag4, concat3);
                goto Label_0146;
            }
        }
    }
    
    public static Phone.PhoneParams readPhoneParamsFromExternalStorage() {
        return readFromExternalStorage(Phone.PhoneParams.class, "phone_params", 779508118, false);
    }
    
    public static boolean removeDeviceParamsFromExternalStorage() {
        while (true) {
            boolean b;
            while (true) {
                try {
                    final File configFile = getConfigFile("current_device_params");
                    b = (!configFile.exists() || configFile.delete());
                    if (b) {
                        return b;
                    }
                }
                catch (final IllegalStateException obj) {
                    final String tag = ConfigUtils.TAG;
                    final String value = String.valueOf(obj);
                    Log.w(tag, new StringBuilder(String.valueOf(value).length() + 34).append("Error clearing device parameters: ").append(value).toString());
                    b = false;
                    continue;
                }
                break;
            }
            Log.e(ConfigUtils.TAG, "Could not clear Cardboard parameters from external storage.");
            return b;
        }
    }
    
    public static boolean writeDeviceParamsToExternalStorage(final CardboardDevice.DeviceParams deviceParams) {
        final boolean writeToExternalStorage = writeToExternalStorage(deviceParams, "current_device_params", 894990891);
        if (!writeToExternalStorage) {
            Log.e(ConfigUtils.TAG, "Could not write Cardboard parameters to external storage.");
        }
        return writeToExternalStorage;
    }
    
    public static boolean writePhoneParamsToExternalStorage(final Phone.PhoneParams phoneParams) {
        Cloneable clone;
        if (phoneParams.dEPRECATEDGyroBias == null) {
            clone = phoneParams;
        }
        else {
            clone = phoneParams;
            if (phoneParams.dEPRECATEDGyroBias.length == 0) {
                clone = phoneParams.clone();
                ((Phone.PhoneParams)clone).dEPRECATEDGyroBias = new float[] { 0.0f, 0.0f, 0.0f };
            }
        }
        final boolean writeToExternalStorage = writeToExternalStorage((MessageNano)clone, "phone_params", 779508118);
        if (!writeToExternalStorage) {
            Log.e(ConfigUtils.TAG, "Could not write Phone parameters to external storage.");
        }
        return writeToExternalStorage;
    }
    
    private static boolean writeToExternalStorage(final MessageNano p0, final String p1, final int p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_3       
        //     2: aconst_null    
        //     3: astore          4
        //     5: aload_3        
        //     6: astore          5
        //     8: new             Ljava/io/BufferedOutputStream;
        //    11: astore          6
        //    13: aload_3        
        //    14: astore          5
        //    16: new             Ljava/io/FileOutputStream;
        //    19: astore          7
        //    21: aload_3        
        //    22: astore          5
        //    24: aload           7
        //    26: aload_1        
        //    27: invokestatic    com/google/vr/cardboard/ConfigUtils.getConfigFile:(Ljava/lang/String;)Ljava/io/File;
        //    30: invokespecial   java/io/FileOutputStream.<init>:(Ljava/io/File;)V
        //    33: aload_3        
        //    34: astore          5
        //    36: aload           6
        //    38: aload           7
        //    40: invokespecial   java/io/BufferedOutputStream.<init>:(Ljava/io/OutputStream;)V
        //    43: aload           6
        //    45: astore_1       
        //    46: aload_0        
        //    47: aload           6
        //    49: iload_2        
        //    50: invokestatic    com/google/vr/cardboard/ConfigUtils.writeToOutputStream:(Lcom/google/protobuf/nano/MessageNano;Ljava/io/OutputStream;I)Z
        //    53: istore          8
        //    55: aload           6
        //    57: invokevirtual   java/io/OutputStream.close:()V
        //    60: iload           8
        //    62: ireturn        
        //    63: astore_0       
        //    64: goto            60
        //    67: astore_0       
        //    68: aconst_null    
        //    69: astore          6
        //    71: aload           6
        //    73: astore_1       
        //    74: getstatic       com/google/vr/cardboard/ConfigUtils.TAG:Ljava/lang/String;
        //    77: astore          5
        //    79: aload           6
        //    81: astore_1       
        //    82: aload_0        
        //    83: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    86: astore          4
        //    88: aload           6
        //    90: astore_1       
        //    91: aload           4
        //    93: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    96: invokevirtual   java/lang/String.length:()I
        //    99: istore_2       
        //   100: aload           6
        //   102: astore_1       
        //   103: new             Ljava/lang/StringBuilder;
        //   106: astore_0       
        //   107: aload           6
        //   109: astore_1       
        //   110: aload_0        
        //   111: iload_2        
        //   112: bipush          39
        //   114: iadd           
        //   115: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //   118: aload           6
        //   120: astore_1       
        //   121: aload           5
        //   123: aload_0        
        //   124: ldc_w           "Parameters file not found for writing: "
        //   127: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   130: aload           4
        //   132: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   135: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   138: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   141: pop            
        //   142: aload           6
        //   144: ifnonnull       153
        //   147: iconst_0       
        //   148: istore          8
        //   150: goto            60
        //   153: aload           6
        //   155: invokevirtual   java/io/OutputStream.close:()V
        //   158: iconst_0       
        //   159: istore          8
        //   161: goto            60
        //   164: astore_0       
        //   165: goto            158
        //   168: astore_1       
        //   169: aload           4
        //   171: astore_0       
        //   172: aload_0        
        //   173: astore          5
        //   175: getstatic       com/google/vr/cardboard/ConfigUtils.TAG:Ljava/lang/String;
        //   178: astore          6
        //   180: aload_0        
        //   181: astore          5
        //   183: aload_1        
        //   184: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   187: astore_1       
        //   188: aload_0        
        //   189: astore          5
        //   191: aload_1        
        //   192: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   195: invokevirtual   java/lang/String.length:()I
        //   198: istore_2       
        //   199: aload_0        
        //   200: astore          5
        //   202: new             Ljava/lang/StringBuilder;
        //   205: astore          4
        //   207: aload_0        
        //   208: astore          5
        //   210: aload           4
        //   212: iload_2        
        //   213: bipush          26
        //   215: iadd           
        //   216: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //   219: aload_0        
        //   220: astore          5
        //   222: aload           6
        //   224: aload           4
        //   226: ldc_w           "Error writing parameters: "
        //   229: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   232: aload_1        
        //   233: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   236: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   239: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //   242: pop            
        //   243: aload_0        
        //   244: ifnull          147
        //   247: aload_0        
        //   248: invokevirtual   java/io/OutputStream.close:()V
        //   251: goto            158
        //   254: astore_0       
        //   255: goto            158
        //   258: astore_0       
        //   259: aload           5
        //   261: ifnonnull       266
        //   264: aload_0        
        //   265: athrow         
        //   266: aload           5
        //   268: invokevirtual   java/io/OutputStream.close:()V
        //   271: goto            264
        //   274: astore_1       
        //   275: goto            264
        //   278: astore_0       
        //   279: aload_1        
        //   280: astore          5
        //   282: goto            259
        //   285: astore_1       
        //   286: aload           6
        //   288: astore_0       
        //   289: goto            172
        //   292: astore_0       
        //   293: goto            71
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  8      13     67     71     Ljava/io/FileNotFoundException;
        //  8      13     168    172    Ljava/lang/IllegalStateException;
        //  8      13     258    259    Any
        //  16     21     67     71     Ljava/io/FileNotFoundException;
        //  16     21     168    172    Ljava/lang/IllegalStateException;
        //  16     21     258    259    Any
        //  24     33     67     71     Ljava/io/FileNotFoundException;
        //  24     33     168    172    Ljava/lang/IllegalStateException;
        //  24     33     258    259    Any
        //  36     43     67     71     Ljava/io/FileNotFoundException;
        //  36     43     168    172    Ljava/lang/IllegalStateException;
        //  36     43     258    259    Any
        //  46     55     292    296    Ljava/io/FileNotFoundException;
        //  46     55     285    292    Ljava/lang/IllegalStateException;
        //  46     55     278    285    Any
        //  55     60     63     67     Ljava/io/IOException;
        //  74     79     278    285    Any
        //  82     88     278    285    Any
        //  91     100    278    285    Any
        //  103    107    278    285    Any
        //  110    118    278    285    Any
        //  121    142    278    285    Any
        //  153    158    164    168    Ljava/io/IOException;
        //  175    180    258    259    Any
        //  183    188    258    259    Any
        //  191    199    258    259    Any
        //  202    207    258    259    Any
        //  210    219    258    259    Any
        //  222    243    258    259    Any
        //  247    251    254    258    Ljava/io/IOException;
        //  266    271    274    278    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0060:
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
    
    private static boolean writeToOutputStream(final MessageNano messageNano, final OutputStream outputStream, final int n) {
        try {
            final byte[] byteArray = MessageNano.toByteArray(messageNano);
            final ByteBuffer allocate = ByteBuffer.allocate(8);
            allocate.putInt(n);
            allocate.putInt(byteArray.length);
            outputStream.write(allocate.array());
            outputStream.write(byteArray);
            return true;
        }
        catch (final IOException ex) {
            final String tag = ConfigUtils.TAG;
            final String value = String.valueOf(ex.toString());
            String concat;
            if (value.length() == 0) {
                concat = new String("Error writing parameters: ");
            }
            else {
                concat = "Error writing parameters: ".concat(value);
            }
            Log.w(tag, concat);
            return false;
        }
    }
}
