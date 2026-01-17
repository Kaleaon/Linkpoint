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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
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
        if (queryParameter == null) {
            Log.w(TAG, "No Cardboard parameters in URI.");
            return null;
        }
        try {
            return (CardboardDevice.DeviceParams) MessageNano.mergeFrom(new CardboardDevice.DeviceParams(), Base64.decode(queryParameter, 11));
        } catch (Exception e) {
            String str = TAG;
            String valueOf = String.valueOf(e);
            Log.w(str, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Parsing cardboard parameters from URI failed: ").append(valueOf).toString());
            return null;
        }
    }

    private static <T extends MessageNano> T readFromExternalStorage(Class<T> cls, String str, int i, boolean z) {
        BufferedInputStream bufferedInputStream;
        try {
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(getConfigFile(str)));
                try {
                    T t = (T) readFromInputStream(cls, bufferedInputStream, i);
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                    }
                    return t;
                } catch (Throwable th) {
                    th = th;
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
                        return null;
                    }
                }
            } catch (IllegalStateException e4) {
                String str3 = TAG;
                String valueOf2 = String.valueOf(e4);
                Log.w(str3, new StringBuilder(String.valueOf(valueOf2).length() + 26).append("Error reading parameters: ").append(valueOf2).toString());
                return null;
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedInputStream = null;
        }
    }

    private static <T extends MessageNano> T readFromInputStream(Class<T> cls, InputStream inputStream, int i) {
        if (inputStream != null) {
            try {
                ByteBuffer allocate = ByteBuffer.allocate(8);
                if (inputStream.read(allocate.array(), 0, allocate.array().length) == -1) {
                    Log.e(TAG, "Error parsing param record: end of stream.");
                    return null;
                }
                int i2 = allocate.getInt();
                int i3 = allocate.getInt();
                if (i2 != i) {
                    Log.e(TAG, "Error parsing param record: incorrect sentinel.");
                    return null;
                }
                byte[] bArr = new byte[i3];
                if (inputStream.read(bArr, 0, i3) != -1) {
                    return (T) MessageNano.mergeFrom(cls.newInstance(), bArr);
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
            } catch (IllegalAccessException e3) {
                String str3 = TAG;
                String valueOf3 = String.valueOf(e3.toString());
                Log.w(str3, valueOf3.length() == 0 ? new String("Error accessing parameter type: ") : "Error accessing parameter type: ".concat(valueOf3));
                return null;
            } catch (InstantiationException e4) {
                String str4 = TAG;
                String valueOf4 = String.valueOf(e4.toString());
                Log.w(str4, valueOf4.length() == 0 ? new String("Error creating parameters: ") : "Error creating parameters: ".concat(valueOf4));
                return null;
            }
        }
        return null;
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
            phoneParams = phoneParams.mo6clone();
            phoneParams.dEPRECATEDGyroBias = new float[]{0.0f, 0.0f, 0.0f};
        }
        boolean writeToExternalStorage = writeToExternalStorage(phoneParams, "phone_params", CARDBOARD_PHONE_PARAMS_STREAM_SENTINEL);
        if (!writeToExternalStorage) {
            Log.e(TAG, "Could not write Phone parameters to external storage.");
        }
        return writeToExternalStorage;
    }

    /* JADX WARN: Not initialized variable reg: 1, insn: 0x0089: MOVE  (r2 I:??[OBJECT, ARRAY]) = (r1 I:??[OBJECT, ARRAY]), block:B:33:0x0089 */
    private static boolean writeToExternalStorage(MessageNano messageNano, String str, int i) {
        BufferedOutputStream bufferedOutputStream;
        BufferedOutputStream bufferedOutputStream2;
        BufferedOutputStream bufferedOutputStream3 = null;
        try {
            try {
                try {
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(getConfigFile(str)));
                } catch (Throwable th) {
                    th = th;
                    bufferedOutputStream3 = bufferedOutputStream2;
                    if (bufferedOutputStream3 != null) {
                        try {
                            bufferedOutputStream3.close();
                        } catch (IOException e) {
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e2) {
                e = e2;
                bufferedOutputStream = null;
            } catch (IllegalStateException e3) {
                e = e3;
            }
            try {
                boolean writeToOutputStream = writeToOutputStream(messageNano, bufferedOutputStream, i);
                try {
                    bufferedOutputStream.close();
                    return writeToOutputStream;
                } catch (IOException e4) {
                    return writeToOutputStream;
                }
            } catch (FileNotFoundException e5) {
                e = e5;
                String str2 = TAG;
                String valueOf = String.valueOf(e);
                Log.e(str2, new StringBuilder(String.valueOf(valueOf).length() + 39).append("Parameters file not found for writing: ").append(valueOf).toString());
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException e6) {
                    }
                    return false;
                }
                return false;
            } catch (IllegalStateException e7) {
                e = e7;
                bufferedOutputStream3 = bufferedOutputStream;
                String str3 = TAG;
                String valueOf2 = String.valueOf(e);
                Log.w(str3, new StringBuilder(String.valueOf(valueOf2).length() + 26).append("Error writing parameters: ").append(valueOf2).toString());
                if (bufferedOutputStream3 != null) {
                    try {
                        bufferedOutputStream3.close();
                    } catch (IOException e8) {
                    }
                    return false;
                }
                return false;
            }
        } catch (Throwable th2) {
            th = th2;
        }
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
