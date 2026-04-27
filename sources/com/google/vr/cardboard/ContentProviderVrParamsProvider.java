package com.google.vr.cardboard;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import com.google.common.logging.nano.Vr;
import com.google.protobuf.nano.MessageNano;
import com.google.vr.ndk.base.SdkConfigurationReader;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.Preferences;

public class ContentProviderVrParamsProvider implements VrParamsProvider {
    private static final String TAG = ContentProviderVrParamsProvider.class.getSimpleName();
    private final ContentProviderClient client;
    private final Uri deviceParamsSettingUri;
    private final Uri phoneParamsSettingUri;
    private final Uri sdkConfigurationParamsSettingUri;
    private final Uri userPrefsUri;

    public ContentProviderVrParamsProvider(ContentProviderClient contentProviderClient, String str) {
        if (contentProviderClient == null) {
            throw new IllegalArgumentException("ContentProviderClient must not be null");
        } else if (str != null && !str.isEmpty()) {
            this.client = contentProviderClient;
            this.deviceParamsSettingUri = VrSettingsProviderContract.createUri(str, VrSettingsProviderContract.DEVICE_PARAMS_SETTING);
            this.userPrefsUri = VrSettingsProviderContract.createUri(str, VrSettingsProviderContract.USER_PREFS_SETTING);
            this.phoneParamsSettingUri = VrSettingsProviderContract.createUri(str, VrSettingsProviderContract.PHONE_PARAMS_SETTING);
            this.sdkConfigurationParamsSettingUri = VrSettingsProviderContract.createUri(str, VrSettingsProviderContract.SDK_CONFIGURATION_PARAMS_SETTING);
        } else {
            throw new IllegalArgumentException("Authority key must be non-null and non-empty");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0071  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private <T extends com.google.protobuf.nano.MessageNano> T readParams(T r8, android.net.Uri r9, java.lang.String r10) {
        /*
            r7 = this;
            r6 = 0
            android.content.ContentProviderClient r0 = r7.client     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x005b, all -> 0x006c }
            r2 = 0
            r4 = 0
            r5 = 0
            r1 = r9
            r3 = r10
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x005b, all -> 0x006c }
            if (r1 != 0) goto L_0x0038
        L_0x000e:
            java.lang.String r0 = TAG     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            java.lang.String r2 = java.lang.String.valueOf(r9)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            java.lang.String r3 = java.lang.String.valueOf(r2)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            int r3 = r3.length()     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            int r3 = r3 + 50
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            r4.<init>(r3)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            java.lang.String r3 = "Invalid params result from ContentProvider query: "
            java.lang.StringBuilder r3 = r4.append(r3)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            java.lang.StringBuilder r2 = r3.append(r2)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            java.lang.String r2 = r2.toString()     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            android.util.Log.e(r0, r2)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            if (r1 != 0) goto L_0x0057
        L_0x0037:
            return r6
        L_0x0038:
            boolean r0 = r1.moveToFirst()     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            if (r0 == 0) goto L_0x000e
            r0 = 0
            byte[] r0 = r1.getBlob(r0)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            if (r0 == 0) goto L_0x004c
            com.google.protobuf.nano.MessageNano r0 = com.google.protobuf.nano.MessageNano.mergeFrom(r8, r0)     // Catch:{ CursorIndexOutOfBoundsException | RemoteException | InvalidProtocolBufferNanoException | IllegalArgumentException -> 0x0077 }
            if (r1 != 0) goto L_0x0053
        L_0x004b:
            return r0
        L_0x004c:
            if (r1 != 0) goto L_0x004f
        L_0x004e:
            return r6
        L_0x004f:
            r1.close()
            goto L_0x004e
        L_0x0053:
            r1.close()
            goto L_0x004b
        L_0x0057:
            r1.close()
            goto L_0x0037
        L_0x005b:
            r0 = move-exception
            r1 = r6
        L_0x005d:
            java.lang.String r2 = TAG     // Catch:{ all -> 0x0075 }
            java.lang.String r3 = "Error reading params from ContentProvider"
            android.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0075 }
            if (r1 != 0) goto L_0x0068
        L_0x0067:
            return r6
        L_0x0068:
            r1.close()
            goto L_0x0067
        L_0x006c:
            r0 = move-exception
            r1 = r6
        L_0x006e:
            if (r1 != 0) goto L_0x0071
        L_0x0070:
            throw r0
        L_0x0071:
            r1.close()
            goto L_0x0070
        L_0x0075:
            r0 = move-exception
            goto L_0x006e
        L_0x0077:
            r0 = move-exception
            goto L_0x005d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.cardboard.ContentProviderVrParamsProvider.readParams(com.google.protobuf.nano.MessageNano, android.net.Uri, java.lang.String):com.google.protobuf.nano.MessageNano");
    }

    private boolean writeParams(MessageNano messageNano, Uri uri) {
        int delete;
        if (messageNano != null) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(VrSettingsProviderContract.SETTING_VALUE_KEY, MessageNano.toByteArray(messageNano));
                delete = this.client.update(uri, contentValues, (String) null, (String[]) null);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to write params to ContentProvider", e);
                return false;
            } catch (SecurityException e2) {
                Log.e(TAG, "Insufficient permissions to write params to ContentProvider", e2);
                return false;
            }
        } else {
            delete = this.client.delete(uri, (String) null, (String[]) null);
        }
        return delete > 0;
    }

    public void close() {
        this.client.release();
    }

    public CardboardDevice.DeviceParams readDeviceParams() {
        return (CardboardDevice.DeviceParams) readParams(new CardboardDevice.DeviceParams(), this.deviceParamsSettingUri, (String) null);
    }

    public Phone.PhoneParams readPhoneParams() {
        return (Phone.PhoneParams) readParams(new Phone.PhoneParams(), this.phoneParamsSettingUri, (String) null);
    }

    public Vr.VREvent.SdkConfigurationParams readSdkConfigurationParams(SdkConfiguration.SdkConfigurationRequest sdkConfigurationRequest) {
        return (Vr.VREvent.SdkConfigurationParams) readParams(SdkConfigurationReader.DEFAULT_PARAMS, this.sdkConfigurationParamsSettingUri, Base64.encodeToString(MessageNano.toByteArray(sdkConfigurationRequest), 0));
    }

    public Preferences.UserPrefs readUserPrefs() {
        return (Preferences.UserPrefs) readParams(new Preferences.UserPrefs(), this.userPrefsUri, (String) null);
    }

    public boolean updateUserPrefs(Preferences.UserPrefs userPrefs) {
        return writeParams(userPrefs, this.userPrefsUri);
    }

    public boolean writeDeviceParams(CardboardDevice.DeviceParams deviceParams) {
        return writeParams(deviceParams, this.deviceParamsSettingUri);
    }
}
