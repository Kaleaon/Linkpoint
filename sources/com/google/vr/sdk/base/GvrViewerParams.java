package com.google.vr.sdk.base;

import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Base64;
import android.util.Log;
import com.google.protobuf.nano.MessageNano;
import com.google.vr.cardboard.ConfigUtils;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;

public class GvrViewerParams {
    private static final float CARDBOARD_V1_INTER_LENS_DISTANCE = 0.06f;
    private static final String CARDBOARD_V1_MODEL = "Cardboard v1";
    private static final float CARDBOARD_V1_SCREEN_TO_LENS_DISTANCE = 0.042f;
    private static final String CARDBOARD_V1_VENDOR = "Google, Inc.";
    private static final VerticalAlignmentType CARDBOARD_V1_VERTICAL_ALIGNMENT = VerticalAlignmentType.BOTTOM;
    private static final float CARDBOARD_V1_VERTICAL_DISTANCE_TO_LENS_CENTER = 0.035f;
    private static final float CARDBOARD_V2_2_INTER_LENS_DISTANCE = 0.064f;
    private static final String CARDBOARD_V2_2_MODEL = "Default Cardboard";
    private static final float CARDBOARD_V2_2_SCREEN_TO_LENS_DISTANCE = 0.039f;
    private static final String CARDBOARD_V2_2_VENDOR = "Google, Inc.";
    private static final VerticalAlignmentType CARDBOARD_V2_2_VERTICAL_ALIGNMENT = VerticalAlignmentType.BOTTOM;
    private static final float CARDBOARD_V2_2_VERTICAL_DISTANCE_TO_LENS_CENTER = 0.035f;
    private static final GvrViewerParams DEFAULT_PARAMS = new GvrViewerParams();
    private static final String HTTP_SCHEME = "http";
    private static final String TAG = "GvrViewerParams";
    private static final String URI_HOST_GOOGLE = "google.com";
    private static final String URI_HOST_GOOGLE_SHORT = "g.co";
    private static final String URI_HOST_LEGACY_CARDBOARD = "v1.0.0";
    private static final Uri URI_ORIGINAL_CARDBOARD_NFC = new Uri.Builder().scheme("cardboard").authority(URI_HOST_LEGACY_CARDBOARD).build();
    private static final Uri URI_ORIGINAL_CARDBOARD_QR_CODE = new Uri.Builder().scheme(HTTP_SCHEME).authority(URI_HOST_GOOGLE_SHORT).appendEncodedPath("cardboard").build();
    private static final String URI_PATH_CARDBOARD_CONFIG = "cardboard/cfg";
    private static final String URI_PATH_CARDBOARD_HOME = "cardboard";
    private static final String URI_SCHEME_LEGACY_CARDBOARD = "cardboard";
    private Distortion distortion;
    private boolean hasMagnet;
    private float interLensDistance;
    private FieldOfView leftEyeMaxFov;
    private String model;
    private CardboardDevice.DeviceParams originalDeviceProto;
    private float screenToLensDistance;
    private String vendor;
    private VerticalAlignmentType verticalAlignment;
    private float verticalDistanceToLensCenter;

    public enum VerticalAlignmentType {
        BOTTOM(0),
        CENTER(1),
        TOP(2);
        
        private final int protoValue;

        private VerticalAlignmentType(int i) {
            this.protoValue = i;
        }

        static VerticalAlignmentType fromProtoValue(int i) {
            for (VerticalAlignmentType verticalAlignmentType : values()) {
                if (verticalAlignmentType.protoValue == i) {
                    return verticalAlignmentType;
                }
            }
            Log.e(GvrViewerParams.TAG, String.format("Unknown alignment type from proto: %d", new Object[]{Integer.valueOf(i)}));
            return BOTTOM;
        }

        /* access modifiers changed from: package-private */
        public int toProtoValue() {
            return this.protoValue;
        }
    }

    public GvrViewerParams() {
        setDefaultValues();
    }

    public GvrViewerParams(GvrViewerParams gvrViewerParams) {
        copyFrom(gvrViewerParams);
    }

    public GvrViewerParams(CardboardDevice.DeviceParams deviceParams) {
        setDefaultValues();
        if (deviceParams != null) {
            this.originalDeviceProto = deviceParams.clone();
            this.vendor = deviceParams.getVendor();
            this.model = deviceParams.getModel();
            this.interLensDistance = deviceParams.getInterLensDistance();
            this.verticalAlignment = VerticalAlignmentType.fromProtoValue(deviceParams.getVerticalAlignment());
            this.verticalDistanceToLensCenter = deviceParams.getTrayToLensDistance();
            this.screenToLensDistance = deviceParams.getScreenToLensDistance();
            this.leftEyeMaxFov = FieldOfView.parseFromProtobuf(deviceParams.leftEyeFieldOfViewAngles);
            if (this.leftEyeMaxFov == null) {
                this.leftEyeMaxFov = new FieldOfView();
            }
            this.distortion = Distortion.parseFromProtobuf(deviceParams.distortionCoefficients);
            if (this.distortion == null) {
                this.distortion = new Distortion();
            }
            this.hasMagnet = deviceParams.getHasMagnet();
        }
    }

    public static GvrViewerParams cardboardV1ViewerParams() {
        GvrViewerParams gvrViewerParams = new GvrViewerParams();
        gvrViewerParams.vendor = "Google, Inc.";
        gvrViewerParams.model = CARDBOARD_V1_MODEL;
        gvrViewerParams.interLensDistance = CARDBOARD_V1_INTER_LENS_DISTANCE;
        gvrViewerParams.verticalAlignment = CARDBOARD_V1_VERTICAL_ALIGNMENT;
        gvrViewerParams.verticalDistanceToLensCenter = 0.035f;
        gvrViewerParams.screenToLensDistance = CARDBOARD_V1_SCREEN_TO_LENS_DISTANCE;
        gvrViewerParams.leftEyeMaxFov = FieldOfView.cardboardV1FieldOfView();
        gvrViewerParams.hasMagnet = true;
        gvrViewerParams.distortion = Distortion.cardboardV1Distortion();
        return gvrViewerParams;
    }

    private void copyFrom(GvrViewerParams gvrViewerParams) {
        this.vendor = gvrViewerParams.vendor;
        this.model = gvrViewerParams.model;
        this.interLensDistance = gvrViewerParams.interLensDistance;
        this.verticalAlignment = gvrViewerParams.verticalAlignment;
        this.verticalDistanceToLensCenter = gvrViewerParams.verticalDistanceToLensCenter;
        this.screenToLensDistance = gvrViewerParams.screenToLensDistance;
        this.leftEyeMaxFov = new FieldOfView(gvrViewerParams.leftEyeMaxFov);
        this.hasMagnet = gvrViewerParams.hasMagnet;
        this.distortion = new Distortion(gvrViewerParams.distortion);
        this.originalDeviceProto = gvrViewerParams.originalDeviceProto;
    }

    public static GvrViewerParams createFromNfcContents(NdefMessage ndefMessage) {
        if (ndefMessage != null) {
            for (NdefRecord uri : ndefMessage.getRecords()) {
                GvrViewerParams createFromUri = createFromUri(uri.toUri());
                if (createFromUri != null) {
                    return createFromUri;
                }
            }
            return null;
        }
        Log.w(TAG, "Could not get contents from NFC tag.");
        return null;
    }

    public static GvrViewerParams createFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        if (isOriginalCardboardDeviceUri(uri)) {
            return cardboardV1ViewerParams();
        }
        if (isCardboardDeviceUri(uri)) {
            return new GvrViewerParams(ConfigUtils.readDeviceParamsFromUri(uri));
        }
        Log.w(TAG, String.format("URI \"%s\" not recognized as GVR viewer.", new Object[]{uri}));
        return null;
    }

    private static boolean isCardboardDeviceUri(Uri uri) {
        return HTTP_SCHEME.equals(uri.getScheme()) && URI_HOST_GOOGLE.equals(uri.getAuthority()) && "/cardboard/cfg".equals(uri.getPath());
    }

    public static boolean isGvrUri(Uri uri) {
        return isOriginalCardboardDeviceUri(uri) || isCardboardDeviceUri(uri);
    }

    public static boolean isOriginalCardboardDeviceUri(Uri uri) {
        return URI_ORIGINAL_CARDBOARD_QR_CODE.equals(uri) || (URI_ORIGINAL_CARDBOARD_NFC.getScheme().equals(uri.getScheme()) && URI_ORIGINAL_CARDBOARD_NFC.getAuthority().equals(uri.getAuthority()));
    }

    private void setDefaultValues() {
        this.vendor = "Google, Inc.";
        this.model = CARDBOARD_V2_2_MODEL;
        this.interLensDistance = CARDBOARD_V2_2_INTER_LENS_DISTANCE;
        this.verticalAlignment = CARDBOARD_V2_2_VERTICAL_ALIGNMENT;
        this.verticalDistanceToLensCenter = 0.035f;
        this.screenToLensDistance = CARDBOARD_V2_2_SCREEN_TO_LENS_DISTANCE;
        this.leftEyeMaxFov = new FieldOfView();
        this.hasMagnet = false;
        this.distortion = new Distortion();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GvrViewerParams)) {
            return false;
        }
        GvrViewerParams gvrViewerParams = (GvrViewerParams) obj;
        if (!(this.vendor.equals(gvrViewerParams.vendor) && this.model.equals(gvrViewerParams.model) && this.interLensDistance == gvrViewerParams.interLensDistance && this.verticalAlignment == gvrViewerParams.verticalAlignment && ((this.verticalAlignment == VerticalAlignmentType.CENTER || this.verticalDistanceToLensCenter == gvrViewerParams.verticalDistanceToLensCenter) && this.screenToLensDistance == gvrViewerParams.screenToLensDistance && this.leftEyeMaxFov.equals(gvrViewerParams.leftEyeMaxFov) && this.distortion.equals(gvrViewerParams.distortion) && this.hasMagnet == gvrViewerParams.hasMagnet))) {
            z = false;
        }
        if (z) {
            return MessageNano.messageNanoEquals(this.originalDeviceProto, gvrViewerParams.originalDeviceProto);
        }
        return false;
    }

    public Distortion getDistortion() {
        return this.distortion;
    }

    public boolean getHasMagnet() {
        return this.hasMagnet;
    }

    public float getInterLensDistance() {
        return this.interLensDistance;
    }

    public FieldOfView getLeftEyeMaxFov() {
        return this.leftEyeMaxFov;
    }

    public String getModel() {
        return this.model;
    }

    public float getScreenToLensDistance() {
        return this.screenToLensDistance;
    }

    public String getVendor() {
        return this.vendor;
    }

    public VerticalAlignmentType getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public float getVerticalDistanceToLensCenter() {
        return this.verticalDistanceToLensCenter;
    }

    /* access modifiers changed from: package-private */
    public float getYEyeOffsetMeters(ScreenParams screenParams) {
        switch (getVerticalAlignment()) {
            case BOTTOM:
                return getVerticalDistanceToLensCenter() - screenParams.getBorderSizeMeters();
            case TOP:
                return screenParams.getHeightMeters() - (getVerticalDistanceToLensCenter() - screenParams.getBorderSizeMeters());
            default:
                return screenParams.getHeightMeters() / 2.0f;
        }
    }

    public boolean isDefault() {
        return DEFAULT_PARAMS.equals(this);
    }

    public void setHasMagnet(boolean z) {
        this.hasMagnet = z;
    }

    public void setInterLensDistance(float f) {
        this.interLensDistance = f;
    }

    public void setModel(String str) {
        if (str == null) {
            str = "";
        }
        this.model = str;
    }

    public void setScreenToLensDistance(float f) {
        this.screenToLensDistance = f;
    }

    public void setVendor(String str) {
        if (str == null) {
            str = "";
        }
        this.vendor = str;
    }

    public void setVerticalAlignment(VerticalAlignmentType verticalAlignmentType) {
        this.verticalAlignment = verticalAlignmentType;
    }

    public void setVerticalDistanceToLensCenter(float f) {
        this.verticalDistanceToLensCenter = f;
    }

    /* access modifiers changed from: package-private */
    public byte[] toByteArray() {
        return MessageNano.toByteArray(toProtobuf());
    }

    public CardboardDevice.DeviceParams toProtobuf() {
        CardboardDevice.DeviceParams deviceParams = this.originalDeviceProto == null ? new CardboardDevice.DeviceParams() : this.originalDeviceProto.clone();
        deviceParams.setVendor(this.vendor);
        deviceParams.setModel(this.model);
        deviceParams.setInterLensDistance(this.interLensDistance);
        deviceParams.setVerticalAlignment(this.verticalAlignment.toProtoValue());
        if (this.verticalAlignment != VerticalAlignmentType.CENTER) {
            deviceParams.setTrayToLensDistance(this.verticalDistanceToLensCenter);
        } else {
            deviceParams.setTrayToLensDistance(0.035f);
        }
        deviceParams.setScreenToLensDistance(this.screenToLensDistance);
        deviceParams.leftEyeFieldOfViewAngles = this.leftEyeMaxFov.toProtobuf();
        deviceParams.distortionCoefficients = this.distortion.toProtobuf();
        if (this.hasMagnet) {
            deviceParams.setHasMagnet(this.hasMagnet);
        }
        return deviceParams;
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append("{\n");
        String str = this.vendor;
        StringBuilder append2 = append.append(new StringBuilder(String.valueOf(str).length() + 12).append("  vendor: ").append(str).append(",\n").toString());
        String str2 = this.model;
        StringBuilder append3 = append2.append(new StringBuilder(String.valueOf(str2).length() + 11).append("  model: ").append(str2).append(",\n").toString()).append(new StringBuilder(40).append("  inter_lens_distance: ").append(this.interLensDistance).append(",\n").toString());
        String valueOf = String.valueOf(this.verticalAlignment);
        StringBuilder append4 = append3.append(new StringBuilder(String.valueOf(valueOf).length() + 24).append("  vertical_alignment: ").append(valueOf).append(",\n").toString()).append(new StringBuilder(53).append("  vertical_distance_to_lens_center: ").append(this.verticalDistanceToLensCenter).append(",\n").toString()).append(new StringBuilder(44).append("  screen_to_lens_distance: ").append(this.screenToLensDistance).append(",\n").toString());
        String valueOf2 = String.valueOf(this.leftEyeMaxFov.toString().replace("\n", "\n  "));
        StringBuilder append5 = append4.append(new StringBuilder(String.valueOf(valueOf2).length() + 22).append("  left_eye_max_fov: ").append(valueOf2).append(",\n").toString());
        String valueOf3 = String.valueOf(this.distortion.toString().replace("\n", "\n  "));
        return append5.append(new StringBuilder(String.valueOf(valueOf3).length() + 16).append("  distortion: ").append(valueOf3).append(",\n").toString()).append(new StringBuilder(17).append("  magnet: ").append(this.hasMagnet).append(",\n").toString()).append("}\n").toString();
    }

    public Uri toUri() {
        byte[] byteArray = toByteArray();
        return new Uri.Builder().scheme(HTTP_SCHEME).authority(URI_HOST_GOOGLE).appendEncodedPath(URI_PATH_CARDBOARD_CONFIG).appendQueryParameter(ConfigUtils.URI_KEY_PARAMS, Base64.encodeToString(byteArray, 0, byteArray.length, 11)).build();
    }
}
