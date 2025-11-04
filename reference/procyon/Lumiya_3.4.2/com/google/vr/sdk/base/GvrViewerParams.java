// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.util.Base64;
import com.google.protobuf.nano.MessageNano;
import com.google.vr.cardboard.ConfigUtils;
import android.nfc.NdefRecord;
import android.util.Log;
import android.nfc.NdefMessage;
import android.net.Uri$Builder;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import android.net.Uri;

public class GvrViewerParams
{
    private static final float CARDBOARD_V1_INTER_LENS_DISTANCE = 0.06f;
    private static final String CARDBOARD_V1_MODEL = "Cardboard v1";
    private static final float CARDBOARD_V1_SCREEN_TO_LENS_DISTANCE = 0.042f;
    private static final String CARDBOARD_V1_VENDOR = "Google, Inc.";
    private static final VerticalAlignmentType CARDBOARD_V1_VERTICAL_ALIGNMENT;
    private static final float CARDBOARD_V1_VERTICAL_DISTANCE_TO_LENS_CENTER = 0.035f;
    private static final float CARDBOARD_V2_2_INTER_LENS_DISTANCE = 0.064f;
    private static final String CARDBOARD_V2_2_MODEL = "Default Cardboard";
    private static final float CARDBOARD_V2_2_SCREEN_TO_LENS_DISTANCE = 0.039f;
    private static final String CARDBOARD_V2_2_VENDOR = "Google, Inc.";
    private static final VerticalAlignmentType CARDBOARD_V2_2_VERTICAL_ALIGNMENT;
    private static final float CARDBOARD_V2_2_VERTICAL_DISTANCE_TO_LENS_CENTER = 0.035f;
    private static final GvrViewerParams DEFAULT_PARAMS;
    private static final String HTTP_SCHEME = "http";
    private static final String TAG = "GvrViewerParams";
    private static final String URI_HOST_GOOGLE = "google.com";
    private static final String URI_HOST_GOOGLE_SHORT = "g.co";
    private static final String URI_HOST_LEGACY_CARDBOARD = "v1.0.0";
    private static final Uri URI_ORIGINAL_CARDBOARD_NFC;
    private static final Uri URI_ORIGINAL_CARDBOARD_QR_CODE;
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
    
    static {
        URI_ORIGINAL_CARDBOARD_NFC = new Uri$Builder().scheme("cardboard").authority("v1.0.0").build();
        URI_ORIGINAL_CARDBOARD_QR_CODE = new Uri$Builder().scheme("http").authority("g.co").appendEncodedPath("cardboard").build();
        CARDBOARD_V2_2_VERTICAL_ALIGNMENT = VerticalAlignmentType.BOTTOM;
        CARDBOARD_V1_VERTICAL_ALIGNMENT = VerticalAlignmentType.BOTTOM;
        DEFAULT_PARAMS = new GvrViewerParams();
    }
    
    public GvrViewerParams() {
        this.setDefaultValues();
    }
    
    public GvrViewerParams(final GvrViewerParams gvrViewerParams) {
        this.copyFrom(gvrViewerParams);
    }
    
    public GvrViewerParams(final CardboardDevice.DeviceParams deviceParams) {
        this.setDefaultValues();
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
        final GvrViewerParams gvrViewerParams = new GvrViewerParams();
        gvrViewerParams.vendor = "Google, Inc.";
        gvrViewerParams.model = "Cardboard v1";
        gvrViewerParams.interLensDistance = 0.06f;
        gvrViewerParams.verticalAlignment = GvrViewerParams.CARDBOARD_V1_VERTICAL_ALIGNMENT;
        gvrViewerParams.verticalDistanceToLensCenter = 0.035f;
        gvrViewerParams.screenToLensDistance = 0.042f;
        gvrViewerParams.leftEyeMaxFov = FieldOfView.cardboardV1FieldOfView();
        gvrViewerParams.hasMagnet = true;
        gvrViewerParams.distortion = Distortion.cardboardV1Distortion();
        return gvrViewerParams;
    }
    
    private void copyFrom(final GvrViewerParams gvrViewerParams) {
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
    
    public static GvrViewerParams createFromNfcContents(final NdefMessage ndefMessage) {
        if (ndefMessage != null) {
            final NdefRecord[] records = ndefMessage.getRecords();
            for (int length = records.length, i = 0; i < length; ++i) {
                final GvrViewerParams fromUri = createFromUri(records[i].toUri());
                if (fromUri != null) {
                    return fromUri;
                }
            }
            return null;
        }
        Log.w("GvrViewerParams", "Could not get contents from NFC tag.");
        return null;
    }
    
    public static GvrViewerParams createFromUri(final Uri uri) {
        if (uri == null) {
            return null;
        }
        if (isOriginalCardboardDeviceUri(uri)) {
            return cardboardV1ViewerParams();
        }
        if (isCardboardDeviceUri(uri)) {
            return new GvrViewerParams(ConfigUtils.readDeviceParamsFromUri(uri));
        }
        Log.w("GvrViewerParams", String.format("URI \"%s\" not recognized as GVR viewer.", uri));
        return null;
    }
    
    private static boolean isCardboardDeviceUri(final Uri uri) {
        final boolean b = false;
        boolean b2;
        if (!"http".equals(uri.getScheme())) {
            b2 = b;
        }
        else {
            b2 = b;
            if ("google.com".equals(uri.getAuthority())) {
                b2 = b;
                if ("/cardboard/cfg".equals(uri.getPath())) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public static boolean isGvrUri(final Uri uri) {
        boolean b = false;
        if (isOriginalCardboardDeviceUri(uri) || isCardboardDeviceUri(uri)) {
            b = true;
        }
        return b;
    }
    
    public static boolean isOriginalCardboardDeviceUri(final Uri uri) {
        final boolean b = false;
        if (!GvrViewerParams.URI_ORIGINAL_CARDBOARD_QR_CODE.equals((Object)uri)) {
            boolean b2 = b;
            if (!GvrViewerParams.URI_ORIGINAL_CARDBOARD_NFC.getScheme().equals(uri.getScheme())) {
                return b2;
            }
            if (!GvrViewerParams.URI_ORIGINAL_CARDBOARD_NFC.getAuthority().equals(uri.getAuthority())) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    private void setDefaultValues() {
        this.vendor = "Google, Inc.";
        this.model = "Default Cardboard";
        this.interLensDistance = 0.064f;
        this.verticalAlignment = GvrViewerParams.CARDBOARD_V2_2_VERTICAL_ALIGNMENT;
        this.verticalDistanceToLensCenter = 0.035f;
        this.screenToLensDistance = 0.039f;
        this.leftEyeMaxFov = new FieldOfView();
        this.hasMagnet = false;
        this.distortion = new Distortion();
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof GvrViewerParams) {
            final GvrViewerParams gvrViewerParams = (GvrViewerParams)o;
            if (!this.vendor.equals(gvrViewerParams.vendor) || !this.model.equals(gvrViewerParams.model) || this.interLensDistance != gvrViewerParams.interLensDistance || this.verticalAlignment != gvrViewerParams.verticalAlignment || ((this.verticalAlignment != VerticalAlignmentType.CENTER && this.verticalDistanceToLensCenter != gvrViewerParams.verticalDistanceToLensCenter) || (this.screenToLensDistance != gvrViewerParams.screenToLensDistance || !this.leftEyeMaxFov.equals(gvrViewerParams.leftEyeMaxFov) || !this.distortion.equals(gvrViewerParams.distortion))) || this.hasMagnet != gvrViewerParams.hasMagnet) {
                b = false;
            }
            return b && MessageNano.messageNanoEquals(this.originalDeviceProto, gvrViewerParams.originalDeviceProto);
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
    
    float getYEyeOffsetMeters(final ScreenParams screenParams) {
        switch (this.getVerticalAlignment()) {
            default: {
                return screenParams.getHeightMeters() / 2.0f;
            }
            case BOTTOM: {
                return this.getVerticalDistanceToLensCenter() - screenParams.getBorderSizeMeters();
            }
            case TOP: {
                return screenParams.getHeightMeters() - (this.getVerticalDistanceToLensCenter() - screenParams.getBorderSizeMeters());
            }
        }
    }
    
    public boolean isDefault() {
        return GvrViewerParams.DEFAULT_PARAMS.equals(this);
    }
    
    public void setHasMagnet(final boolean hasMagnet) {
        this.hasMagnet = hasMagnet;
    }
    
    public void setInterLensDistance(final float interLensDistance) {
        this.interLensDistance = interLensDistance;
    }
    
    public void setModel(final String s) {
        String model = s;
        if (s == null) {
            model = "";
        }
        this.model = model;
    }
    
    public void setScreenToLensDistance(final float screenToLensDistance) {
        this.screenToLensDistance = screenToLensDistance;
    }
    
    public void setVendor(final String s) {
        String vendor = s;
        if (s == null) {
            vendor = "";
        }
        this.vendor = vendor;
    }
    
    public void setVerticalAlignment(final VerticalAlignmentType verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }
    
    public void setVerticalDistanceToLensCenter(final float verticalDistanceToLensCenter) {
        this.verticalDistanceToLensCenter = verticalDistanceToLensCenter;
    }
    
    byte[] toByteArray() {
        return MessageNano.toByteArray(this.toProtobuf());
    }
    
    public CardboardDevice.DeviceParams toProtobuf() {
        Cloneable clone;
        if (this.originalDeviceProto == null) {
            clone = new CardboardDevice.DeviceParams();
        }
        else {
            clone = this.originalDeviceProto.clone();
        }
        ((CardboardDevice.DeviceParams)clone).setVendor(this.vendor);
        ((CardboardDevice.DeviceParams)clone).setModel(this.model);
        ((CardboardDevice.DeviceParams)clone).setInterLensDistance(this.interLensDistance);
        ((CardboardDevice.DeviceParams)clone).setVerticalAlignment(this.verticalAlignment.toProtoValue());
        if (this.verticalAlignment != VerticalAlignmentType.CENTER) {
            ((CardboardDevice.DeviceParams)clone).setTrayToLensDistance(this.verticalDistanceToLensCenter);
        }
        else {
            ((CardboardDevice.DeviceParams)clone).setTrayToLensDistance(0.035f);
        }
        ((CardboardDevice.DeviceParams)clone).setScreenToLensDistance(this.screenToLensDistance);
        ((CardboardDevice.DeviceParams)clone).leftEyeFieldOfViewAngles = this.leftEyeMaxFov.toProtobuf();
        ((CardboardDevice.DeviceParams)clone).distortionCoefficients = this.distortion.toProtobuf();
        if (this.hasMagnet) {
            ((CardboardDevice.DeviceParams)clone).setHasMagnet(this.hasMagnet);
        }
        return (CardboardDevice.DeviceParams)clone;
    }
    
    @Override
    public String toString() {
        final StringBuilder append = new StringBuilder().append("{\n");
        final String vendor = this.vendor;
        final StringBuilder append2 = append.append(new StringBuilder(String.valueOf(vendor).length() + 12).append("  vendor: ").append(vendor).append(",\n").toString());
        final String model = this.model;
        final StringBuilder append3 = append2.append(new StringBuilder(String.valueOf(model).length() + 11).append("  model: ").append(model).append(",\n").toString()).append(new StringBuilder(40).append("  inter_lens_distance: ").append(this.interLensDistance).append(",\n").toString());
        final String value = String.valueOf(this.verticalAlignment);
        final StringBuilder append4 = append3.append(new StringBuilder(String.valueOf(value).length() + 24).append("  vertical_alignment: ").append(value).append(",\n").toString()).append(new StringBuilder(53).append("  vertical_distance_to_lens_center: ").append(this.verticalDistanceToLensCenter).append(",\n").toString()).append(new StringBuilder(44).append("  screen_to_lens_distance: ").append(this.screenToLensDistance).append(",\n").toString());
        final String value2 = String.valueOf(this.leftEyeMaxFov.toString().replace("\n", "\n  "));
        final StringBuilder append5 = append4.append(new StringBuilder(String.valueOf(value2).length() + 22).append("  left_eye_max_fov: ").append(value2).append(",\n").toString());
        final String value3 = String.valueOf(this.distortion.toString().replace("\n", "\n  "));
        return append5.append(new StringBuilder(String.valueOf(value3).length() + 16).append("  distortion: ").append(value3).append(",\n").toString()).append(new StringBuilder(17).append("  magnet: ").append(this.hasMagnet).append(",\n").toString()).append("}\n").toString();
    }
    
    public Uri toUri() {
        final byte[] byteArray = this.toByteArray();
        return new Uri$Builder().scheme("http").authority("google.com").appendEncodedPath("cardboard/cfg").appendQueryParameter("p", Base64.encodeToString(byteArray, 0, byteArray.length, 11)).build();
    }
    
    public enum VerticalAlignmentType
    {
        BOTTOM(0), 
        CENTER(1), 
        TOP(2);
        
        private final int protoValue;
        
        private VerticalAlignmentType(final int protoValue) {
            this.protoValue = protoValue;
        }
        
        static VerticalAlignmentType fromProtoValue(final int i) {
            for (final VerticalAlignmentType verticalAlignmentType : values()) {
                if (verticalAlignmentType.protoValue == i) {
                    return verticalAlignmentType;
                }
            }
            Log.e("GvrViewerParams", String.format("Unknown alignment type from proto: %d", i));
            return VerticalAlignmentType.BOTTOM;
        }
        
        int toProtoValue() {
            return this.protoValue;
        }
    }
}
