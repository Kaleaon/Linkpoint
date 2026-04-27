package com.google.vr.cardboard;

import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ConfigUtils {
    public static final String CARDBOARD_CONFIG_FOLDER = "Cardboard";
    private static final String CARDBOARD_DEVICE_PARAMS_FILE = "current_device_params";
    private static final int CARDBOARD_DEVICE_PARAMS_STREAM_SENTINEL = 894990891;
    private static final String CARDBOARD_PHONE_PARAMS_FILE = "phone_params";
    private static final int CARDBOARD_PHONE_PARAMS_STREAM_SENTINEL = 779508118;
    private static final boolean DEBUG = false;
    private static final String TAG = ConfigUtils.class.getSimpleName();
    public static final String URI_KEY_PARAMS = "p";

    private static File getConfigFile(String str) throws IllegalStateException {
        File file = new File(Environment.getExternalStorageDirectory(), CARDBOARD_CONFIG_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            String valueOf = String.valueOf(file);
            throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 61).append(valueOf).append(" already exists as a file, but is expected to be a directory.").toString());
        }
        return new File(file, str);
    }

    public static CardboardDevice.DeviceParams readDeviceParamsFromExternalStorage() {
        return (CardboardDevice.DeviceParams) readFromExternalStorage(CardboardDevice.DeviceParams.class, CARDBOARD_DEVICE_PARAMS_FILE, CARDBOARD_DEVICE_PARAMS_STREAM_SENTINEL, true);
    }

    public static CardboardDevice.DeviceParams readDeviceParamsFromUri(Uri uri) {
        String queryParameter = uri.getQueryParameter(URI_KEY_PARAMS);
        if (queryParameter != null) {
            try {
                return (CardboardDevice.DeviceParams) MessageNano.mergeFrom(new CardboardDevice.DeviceParams(), Base64.decode(queryParameter, 11));
            } catch (Exception e) {
                String str = TAG;
                String valueOf = String.valueOf(e);
                Log.w(str, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Parsing cardboard parameters from URI failed: ").append(valueOf).toString());
                return null;
            }
        } else {
            Log.w(TAG, "No Cardboard parameters in URI.");
            return null;
        }
    }

    private static <T extends MessageNano> T readFromExternalStorage(Class<T> cls, String str, int i, boolean z) {
        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(getConfigFile(str)));
            try {
                T readFromInputStream = readFromInputStream(cls, bufferedInputStream, i);
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                }
                return readFromInputStream;
            } catch (Throwable th) {
                th = th;
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedInputStream = null;
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e2) {
                }
            }
            try {
                throw th;
            } catch (FileNotFoundException e3) {
                if (z) {
                    String str2 = TAG;
                    String valueOf = String.valueOf(e3);
                    Log.d(str2, new StringBuilder(String.valueOf(valueOf).length() + 39).append("Parameters file not found for reading: ").append(valueOf).toString());
                }
            } catch (IllegalStateException e4) {
                String str3 = TAG;
                String valueOf2 = String.valueOf(e4);
                Log.w(str3, new StringBuilder(String.valueOf(valueOf2).length() + 26).append("Error reading parameters: ").append(valueOf2).toString());
            }
        }
        return null;
    }

    private static <T extends MessageNano> T readFromInputStream(Class<T> cls, InputStream inputStream, int i) {
        if (inputStream == null) {
            return null;
        }
        try {
            ByteBuffer allocate = ByteBuffer.allocate(8);
            if (inputStream.read(allocate.array(), 0, allocate.array().length) != -1) {
                int i2 = allocate.getInt();
                int i3 = allocate.getInt();
                if (i2 == i) {
                    byte[] bArr = new byte[i3];
                    if (inputStream.read(bArr, 0, i3) != -1) {
                        return MessageNano.mergeFrom((MessageNano) cls.newInstance(), bArr);
                    }
                    Log.e(TAG, "Error parsing param record: end of stream.");
                    return null;
                }
                Log.e(TAG, "Error parsing param record: incorrect sentinel.");
                return null;
            }
            Log.e(TAG, "Error parsing param record: end of stream.");
            return null;
        } catch (InvalidProtocolBufferNanoException e) {
            String str = TAG;
            String valueOf = String.valueOf(e.toString());
            Log.w(str, valueOf.length() == 0 ? new String("Error parsing protocol buffer: ") : "Error parsing protocol buffer: ".concat(valueOf));
            return null;
        } catch (IOException e2) {
            String str2 = TAG;
            String valueOf2 = String.valueOf(e2.toString());
            Log.w(str2, valueOf2.length() == 0 ? new String("Error reading parameters: ") : "Error reading parameters: ".concat(valueOf2));
            return null;
        } catch (InstantiationException e3) {
            String str3 = TAG;
            String valueOf3 = String.valueOf(e3.toString());
            Log.w(str3, valueOf3.length() == 0 ? new String("Error creating parameters: ") : "Error creating parameters: ".concat(valueOf3));
            return null;
        } catch (IllegalAccessException e4) {
            String str4 = TAG;
            String valueOf4 = String.valueOf(e4.toString());
            Log.w(str4, valueOf4.length() == 0 ? new String("Error accessing parameter type: ") : "Error accessing parameter type: ".concat(valueOf4));
            return null;
        }
    }

    public static Phone.PhoneParams readPhoneParamsFromExternalStorage() {
        return (Phone.PhoneParams) readFromExternalStorage(Phone.PhoneParams.class, "phone_params", CARDBOARD_PHONE_PARAMS_STREAM_SENTINEL, false);
    }

    public static boolean removeDeviceParamsFromExternalStorage() {
        boolean z;
        try {
            File configFile = getConfigFile(CARDBOARD_DEVICE_PARAMS_FILE);
            z = !configFile.exists() ? true : configFile.delete();
        } catch (IllegalStateException e) {
            String str = TAG;
            String valueOf = String.valueOf(e);
            Log.w(str, new StringBuilder(String.valueOf(valueOf).length() + 34).append("Error clearing device parameters: ").append(valueOf).toString());
            z = false;
        }
        if (!z) {
            Log.e(TAG, "Could not clear Cardboard parameters from external storage.");
        }
        return z;
    }

    public static boolean writeDeviceParamsToExternalStorage(CardboardDevice.DeviceParams deviceParams) {
        boolean writeToExternalStorage = writeToExternalStorage(deviceParams, CARDBOARD_DEVICE_PARAMS_FILE, CARDBOARD_DEVICE_PARAMS_STREAM_SENTINEL);
        if (!writeToExternalStorage) {
            Log.e(TAG, "Could not write Cardboard parameters to external storage.");
        }
        return writeToExternalStorage;
    }

    public static boolean writePhoneParamsToExternalStorage(Phone.PhoneParams phoneParams) {
        if (phoneParams.dEPRECATEDGyroBias != null && phoneParams.dEPRECATEDGyroBias.length == 0) {
            phoneParams = phoneParams.clone();
            phoneParams.dEPRECATEDGyroBias = new float[]{0.0f, 0.0f, 0.0f};
        }
        boolean writeToExternalStorage = writeToExternalStorage(phoneParams, "phone_params", CARDBOARD_PHONE_PARAMS_STREAM_SENTINEL);
        if (!writeToExternalStorage) {
            Log.e(TAG, "Could not write Phone parameters to external storage.");
        }
        return writeToExternalStorage;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0078 A[SYNTHETIC, Splitter:B:22:0x0078] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0082 A[SYNTHETIC, Splitter:B:28:0x0082] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean writeToExternalStorage(com.google.protobuf.nano.MessageNano r6, java.lang.String r7, int r8) {
        /*
            r2 = 0
            r3 = 0
            java.io.BufferedOutputStream r1 = new java.io.BufferedOutputStream     // Catch:{ FileNotFoundException -> 0x001a, IllegalStateException -> 0x004e }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x001a, IllegalStateException -> 0x004e }
            java.io.File r4 = getConfigFile(r7)     // Catch:{ FileNotFoundException -> 0x001a, IllegalStateException -> 0x004e }
            r0.<init>(r4)     // Catch:{ FileNotFoundException -> 0x001a, IllegalStateException -> 0x004e }
            r1.<init>(r0)     // Catch:{ FileNotFoundException -> 0x001a, IllegalStateException -> 0x004e }
            boolean r0 = writeToOutputStream(r6, r1, r8)     // Catch:{ FileNotFoundException -> 0x008e, IllegalStateException -> 0x008b }
            r1.close()     // Catch:{ IOException -> 0x0018 }
        L_0x0017:
            return r0
        L_0x0018:
            r1 = move-exception
            goto L_0x0017
        L_0x001a:
            r0 = move-exception
            r1 = r2
        L_0x001c:
            java.lang.String r2 = TAG     // Catch:{ all -> 0x0088 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x0088 }
            java.lang.String r4 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x0088 }
            int r4 = r4.length()     // Catch:{ all -> 0x0088 }
            int r4 = r4 + 39
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0088 }
            r5.<init>(r4)     // Catch:{ all -> 0x0088 }
            java.lang.String r4 = "Parameters file not found for writing: "
            java.lang.StringBuilder r4 = r5.append(r4)     // Catch:{ all -> 0x0088 }
            java.lang.StringBuilder r0 = r4.append(r0)     // Catch:{ all -> 0x0088 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0088 }
            android.util.Log.e(r2, r0)     // Catch:{ all -> 0x0088 }
            if (r1 != 0) goto L_0x0047
        L_0x0045:
            r0 = r3
            goto L_0x0017
        L_0x0047:
            r1.close()     // Catch:{ IOException -> 0x004c }
        L_0x004a:
            r0 = r3
            goto L_0x0017
        L_0x004c:
            r0 = move-exception
            goto L_0x004a
        L_0x004e:
            r0 = move-exception
        L_0x004f:
            java.lang.String r1 = TAG     // Catch:{ all -> 0x007e }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x007e }
            java.lang.String r4 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x007e }
            int r4 = r4.length()     // Catch:{ all -> 0x007e }
            int r4 = r4 + 26
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x007e }
            r5.<init>(r4)     // Catch:{ all -> 0x007e }
            java.lang.String r4 = "Error writing parameters: "
            java.lang.StringBuilder r4 = r5.append(r4)     // Catch:{ all -> 0x007e }
            java.lang.StringBuilder r0 = r4.append(r0)     // Catch:{ all -> 0x007e }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x007e }
            android.util.Log.w(r1, r0)     // Catch:{ all -> 0x007e }
            if (r2 == 0) goto L_0x0045
            r2.close()     // Catch:{ IOException -> 0x007c }
            goto L_0x004a
        L_0x007c:
            r0 = move-exception
            goto L_0x004a
        L_0x007e:
            r0 = move-exception
        L_0x007f:
            if (r2 != 0) goto L_0x0082
        L_0x0081:
            throw r0
        L_0x0082:
            r2.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x0081
        L_0x0086:
            r1 = move-exception
            goto L_0x0081
        L_0x0088:
            r0 = move-exception
            r2 = r1
            goto L_0x007f
        L_0x008b:
            r0 = move-exception
            r2 = r1
            goto L_0x004f
        L_0x008e:
            r0 = move-exception
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.cardboard.ConfigUtils.writeToExternalStorage(com.google.protobuf.nano.MessageNano, java.lang.String, int):boolean");
    }

    private static boolean writeToOutputStream(MessageNano messageNano, OutputStream outputStream, int i) {
        try {
            byte[] byteArray = MessageNano.toByteArray(messageNano);
            ByteBuffer allocate = ByteBuffer.allocate(8);
            allocate.putInt(i);
            allocate.putInt(byteArray.length);
            outputStream.write(allocate.array());
            outputStream.write(byteArray);
            return true;
        } catch (IOException e) {
            String str = TAG;
            String valueOf = String.valueOf(e.toString());
            Log.w(str, valueOf.length() == 0 ? new String("Error writing parameters: ") : "Error writing parameters: ".concat(valueOf));
            return false;
        }
    }
}
