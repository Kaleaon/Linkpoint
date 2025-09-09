package com.google.vrtoolkit.cardboard.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.ExtendableMessageNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface CardboardDevice {

    public static final class CardboardInternalParams extends ExtendableMessageNano<CardboardInternalParams> implements Cloneable {
        private static volatile CardboardInternalParams[] _emptyArray;
        private String accelerometer_;
        private int bitField0_;
        public int[] eyeOrientations;
        private String gyroscope_;
        private float screenCenterToLensDistance_;
        private float xPpiOverride_;
        private float yPpiOverride_;

        public interface OrientationType {
            public static final int CCW_0_DEGREES = 0;
            public static final int CCW_0_DEGREES_MIRRORED = 4;
            public static final int CCW_180_DEGREES = 2;
            public static final int CCW_180_DEGREES_MIRRORED = 6;
            public static final int CCW_270_DEGREES = 3;
            public static final int CCW_270_DEGREES_MIRRORED = 7;
            public static final int CCW_90_DEGREES = 1;
            public static final int CCW_90_DEGREES_MIRRORED = 5;
        }

        public CardboardInternalParams() {
            clear();
        }

        public static CardboardInternalParams[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new CardboardInternalParams[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static CardboardInternalParams parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new CardboardInternalParams().mergeFrom(codedInputByteBufferNano);
        }

        public static CardboardInternalParams parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (CardboardInternalParams) MessageNano.mergeFrom(new CardboardInternalParams(), bArr);
        }

        public final CardboardInternalParams clear() {
            this.bitField0_ = 0;
            this.eyeOrientations = WireFormatNano.EMPTY_INT_ARRAY;
            this.screenCenterToLensDistance_ = 0.0f;
            this.xPpiOverride_ = 0.0f;
            this.yPpiOverride_ = 0.0f;
            this.accelerometer_ = "";
            this.gyroscope_ = "";
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final CardboardInternalParams clearAccelerometer() {
            this.accelerometer_ = "";
            this.bitField0_ &= -9;
            return this;
        }

        public final CardboardInternalParams clearGyroscope() {
            this.gyroscope_ = "";
            this.bitField0_ &= -17;
            return this;
        }

        public final CardboardInternalParams clearScreenCenterToLensDistance() {
            this.screenCenterToLensDistance_ = 0.0f;
            this.bitField0_ &= -2;
            return this;
        }

        public final CardboardInternalParams clearXPpiOverride() {
            this.xPpiOverride_ = 0.0f;
            this.bitField0_ &= -3;
            return this;
        }

        public final CardboardInternalParams clearYPpiOverride() {
            this.yPpiOverride_ = 0.0f;
            this.bitField0_ &= -5;
            return this;
        }

        public final CardboardInternalParams clone() {
            try {
                CardboardInternalParams cardboardInternalParams = (CardboardInternalParams) super.clone();
                if (this.eyeOrientations != null && this.eyeOrientations.length > 0) {
                    cardboardInternalParams.eyeOrientations = (int[]) this.eyeOrientations.clone();
                }
                return cardboardInternalParams;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeRawVarint32Size;
            int computeSerializedSize = super.computeSerializedSize();
            if (this.eyeOrientations != null && this.eyeOrientations.length > 0) {
                int i = 0;
                for (int computeInt32SizeNoTag : this.eyeOrientations) {
                    i += CodedOutputByteBufferNano.computeInt32SizeNoTag(computeInt32SizeNoTag);
                }
                computeRawVarint32Size = computeSerializedSize + i + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(i);
            } else {
                computeRawVarint32Size = computeSerializedSize;
            }
            if ((this.bitField0_ & 1) != 0) {
                computeRawVarint32Size += CodedOutputByteBufferNano.computeFloatSize(2, this.screenCenterToLensDistance_);
            }
            if ((this.bitField0_ & 2) != 0) {
                computeRawVarint32Size += CodedOutputByteBufferNano.computeFloatSize(3, this.xPpiOverride_);
            }
            if ((this.bitField0_ & 4) != 0) {
                computeRawVarint32Size += CodedOutputByteBufferNano.computeFloatSize(4, this.yPpiOverride_);
            }
            if ((this.bitField0_ & 8) != 0) {
                computeRawVarint32Size += CodedOutputByteBufferNano.computeStringSize(5, this.accelerometer_);
            }
            return (this.bitField0_ & 16) == 0 ? computeRawVarint32Size : computeRawVarint32Size + CodedOutputByteBufferNano.computeStringSize(6, this.gyroscope_);
        }

        public final String getAccelerometer() {
            return this.accelerometer_;
        }

        public final String getGyroscope() {
            return this.gyroscope_;
        }

        public final float getScreenCenterToLensDistance() {
            return this.screenCenterToLensDistance_;
        }

        public final float getXPpiOverride() {
            return this.xPpiOverride_;
        }

        public final float getYPpiOverride() {
            return this.yPpiOverride_;
        }

        public final boolean hasAccelerometer() {
            return (this.bitField0_ & 8) != 0;
        }

        public final boolean hasGyroscope() {
            return (this.bitField0_ & 16) != 0;
        }

        public final boolean hasScreenCenterToLensDistance() {
            return (this.bitField0_ & 1) != 0;
        }

        public final boolean hasXPpiOverride() {
            return (this.bitField0_ & 2) != 0;
        }

        public final boolean hasYPpiOverride() {
            return (this.bitField0_ & 4) != 0;
        }

        public final CardboardInternalParams mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 8);
                        int[] iArr = new int[repeatedFieldArrayLength];
                        int i = 0;
                        for (int i2 = 0; i2 < repeatedFieldArrayLength; i2++) {
                            if (i2 != 0) {
                                codedInputByteBufferNano.readTag();
                            }
                            int readInt32 = codedInputByteBufferNano.readInt32();
                            switch (readInt32) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                    iArr[i] = readInt32;
                                    i++;
                                    break;
                            }
                        }
                        if (i != 0) {
                            int length = this.eyeOrientations != null ? this.eyeOrientations.length : 0;
                            if (length != 0 || i != repeatedFieldArrayLength) {
                                int[] iArr2 = new int[(length + i)];
                                if (length != 0) {
                                    System.arraycopy(this.eyeOrientations, 0, iArr2, 0, length);
                                }
                                System.arraycopy(iArr, 0, iArr2, length, i);
                                this.eyeOrientations = iArr2;
                                break;
                            } else {
                                this.eyeOrientations = iArr;
                                break;
                            }
                        } else {
                            break;
                        }
                    case 10:
                        int pushLimit = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
                        int position = codedInputByteBufferNano.getPosition();
                        int i3 = 0;
                        while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                            switch (codedInputByteBufferNano.readInt32()) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                    i3++;
                                    break;
                            }
                        }
                        if (i3 != 0) {
                            codedInputByteBufferNano.rewindToPosition(position);
                            int length2 = this.eyeOrientations != null ? this.eyeOrientations.length : 0;
                            int[] iArr3 = new int[(i3 + length2)];
                            if (length2 != 0) {
                                System.arraycopy(this.eyeOrientations, 0, iArr3, 0, length2);
                            }
                            while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                                int readInt322 = codedInputByteBufferNano.readInt32();
                                switch (readInt322) {
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                        iArr3[length2] = readInt322;
                                        length2++;
                                        break;
                                }
                            }
                            this.eyeOrientations = iArr3;
                        }
                        codedInputByteBufferNano.popLimit(pushLimit);
                        break;
                    case 21:
                        this.screenCenterToLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 1;
                        break;
                    case 29:
                        this.xPpiOverride_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 2;
                        break;
                    case 37:
                        this.yPpiOverride_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 4;
                        break;
                    case 42:
                        this.accelerometer_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 8;
                        break;
                    case 50:
                        this.gyroscope_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 16;
                        break;
                    default:
                        if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public final CardboardInternalParams setAccelerometer(String str) {
            if (str != null) {
                this.accelerometer_ = str;
                this.bitField0_ |= 8;
                return this;
            }
            throw new NullPointerException();
        }

        public final CardboardInternalParams setGyroscope(String str) {
            if (str != null) {
                this.gyroscope_ = str;
                this.bitField0_ |= 16;
                return this;
            }
            throw new NullPointerException();
        }

        public final CardboardInternalParams setScreenCenterToLensDistance(float f) {
            this.screenCenterToLensDistance_ = f;
            this.bitField0_ |= 1;
            return this;
        }

        public final CardboardInternalParams setXPpiOverride(float f) {
            this.xPpiOverride_ = f;
            this.bitField0_ |= 2;
            return this;
        }

        public final CardboardInternalParams setYPpiOverride(float f) {
            this.yPpiOverride_ = f;
            this.bitField0_ |= 4;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.eyeOrientations != null && this.eyeOrientations.length > 0) {
                int i = 0;
                for (int computeInt32SizeNoTag : this.eyeOrientations) {
                    i += CodedOutputByteBufferNano.computeInt32SizeNoTag(computeInt32SizeNoTag);
                }
                codedOutputByteBufferNano.writeRawVarint32(10);
                codedOutputByteBufferNano.writeRawVarint32(i);
                for (int writeRawVarint32 : this.eyeOrientations) {
                    codedOutputByteBufferNano.writeRawVarint32(writeRawVarint32);
                }
            }
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeFloat(2, this.screenCenterToLensDistance_);
            }
            if ((this.bitField0_ & 2) != 0) {
                codedOutputByteBufferNano.writeFloat(3, this.xPpiOverride_);
            }
            if ((this.bitField0_ & 4) != 0) {
                codedOutputByteBufferNano.writeFloat(4, this.yPpiOverride_);
            }
            if ((this.bitField0_ & 8) != 0) {
                codedOutputByteBufferNano.writeString(5, this.accelerometer_);
            }
            if ((this.bitField0_ & 16) != 0) {
                codedOutputByteBufferNano.writeString(6, this.gyroscope_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class DaydreamInternalParams extends ExtendableMessageNano<DaydreamInternalParams> implements Cloneable {
        private static volatile DaydreamInternalParams[] _emptyArray;
        public ScreenAlignmentMarker[] alignmentMarkers;
        private int bitField0_;
        private int version_;

        public DaydreamInternalParams() {
            clear();
        }

        public static DaydreamInternalParams[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DaydreamInternalParams[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static DaydreamInternalParams parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DaydreamInternalParams().mergeFrom(codedInputByteBufferNano);
        }

        public static DaydreamInternalParams parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (DaydreamInternalParams) MessageNano.mergeFrom(new DaydreamInternalParams(), bArr);
        }

        public final DaydreamInternalParams clear() {
            this.bitField0_ = 0;
            this.version_ = 0;
            this.alignmentMarkers = ScreenAlignmentMarker.emptyArray();
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final DaydreamInternalParams clearVersion() {
            this.version_ = 0;
            this.bitField0_ &= -2;
            return this;
        }

        public final DaydreamInternalParams clone() {
            try {
                DaydreamInternalParams daydreamInternalParams = (DaydreamInternalParams) super.clone();
                if (this.alignmentMarkers != null && this.alignmentMarkers.length > 0) {
                    daydreamInternalParams.alignmentMarkers = new ScreenAlignmentMarker[this.alignmentMarkers.length];
                    for (int i = 0; i < this.alignmentMarkers.length; i++) {
                        if (this.alignmentMarkers[i] != null) {
                            daydreamInternalParams.alignmentMarkers[i] = this.alignmentMarkers[i].clone();
                        }
                    }
                }
                return daydreamInternalParams;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.version_);
            }
            if (this.alignmentMarkers == null || this.alignmentMarkers.length <= 0) {
                return computeSerializedSize;
            }
            int i = computeSerializedSize;
            for (ScreenAlignmentMarker screenAlignmentMarker : this.alignmentMarkers) {
                if (screenAlignmentMarker != null) {
                    i += CodedOutputByteBufferNano.computeMessageSize(2, screenAlignmentMarker);
                }
            }
            return i;
        }

        public final int getVersion() {
            return this.version_;
        }

        public final boolean hasVersion() {
            return (this.bitField0_ & 1) != 0;
        }

        public final DaydreamInternalParams mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        this.version_ = codedInputByteBufferNano.readInt32();
                        this.bitField0_ |= 1;
                        break;
                    case 18:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                        int length = this.alignmentMarkers != null ? this.alignmentMarkers.length : 0;
                        ScreenAlignmentMarker[] screenAlignmentMarkerArr = new ScreenAlignmentMarker[(repeatedFieldArrayLength + length)];
                        if (length != 0) {
                            System.arraycopy(this.alignmentMarkers, 0, screenAlignmentMarkerArr, 0, length);
                        }
                        while (length < screenAlignmentMarkerArr.length - 1) {
                            screenAlignmentMarkerArr[length] = new ScreenAlignmentMarker();
                            codedInputByteBufferNano.readMessage(screenAlignmentMarkerArr[length]);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        screenAlignmentMarkerArr[length] = new ScreenAlignmentMarker();
                        codedInputByteBufferNano.readMessage(screenAlignmentMarkerArr[length]);
                        this.alignmentMarkers = screenAlignmentMarkerArr;
                        break;
                    default:
                        if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public final DaydreamInternalParams setVersion(int i) {
            this.version_ = i;
            this.bitField0_ |= 1;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.version_);
            }
            if (this.alignmentMarkers != null && this.alignmentMarkers.length > 0) {
                for (ScreenAlignmentMarker screenAlignmentMarker : this.alignmentMarkers) {
                    if (screenAlignmentMarker != null) {
                        codedOutputByteBufferNano.writeMessage(2, screenAlignmentMarker);
                    }
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class DeviceParams extends ExtendableMessageNano<DeviceParams> implements Cloneable {
        private static volatile DeviceParams[] _emptyArray;
        private int bitField0_;
        public DaydreamInternalParams daydreamInternal;
        public float[] distortionCoefficients;
        private boolean hasMagnet_;
        private float interLensDistance_;
        public CardboardInternalParams internal;
        public float[] leftEyeFieldOfViewAngles;
        private String model_;
        private int primaryButton_;
        private float screenToLensDistance_;
        private float trayToLensDistance_;
        private String vendor_;
        private int verticalAlignment_;

        public interface ButtonType {
            public static final int INDIRECT_TOUCH = 3;
            public static final int MAGNET = 1;
            public static final int NONE = 0;
            public static final int TOUCH = 2;
        }

        public interface VerticalAlignmentType {
            public static final int BOTTOM = 0;
            public static final int CENTER = 1;
            public static final int TOP = 2;
        }

        public DeviceParams() {
            clear();
        }

        public static DeviceParams[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DeviceParams[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static DeviceParams parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DeviceParams().mergeFrom(codedInputByteBufferNano);
        }

        public static DeviceParams parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (DeviceParams) MessageNano.mergeFrom(new DeviceParams(), bArr);
        }

        public final DeviceParams clear() {
            this.bitField0_ = 0;
            this.vendor_ = "";
            this.model_ = "";
            this.screenToLensDistance_ = 0.0f;
            this.interLensDistance_ = 0.0f;
            this.leftEyeFieldOfViewAngles = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.verticalAlignment_ = 0;
            this.trayToLensDistance_ = 0.0f;
            this.distortionCoefficients = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.hasMagnet_ = false;
            this.primaryButton_ = 1;
            this.internal = null;
            this.daydreamInternal = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final DeviceParams clearHasMagnet() {
            this.hasMagnet_ = false;
            this.bitField0_ &= -65;
            return this;
        }

        public final DeviceParams clearInterLensDistance() {
            this.interLensDistance_ = 0.0f;
            this.bitField0_ &= -9;
            return this;
        }

        public final DeviceParams clearModel() {
            this.model_ = "";
            this.bitField0_ &= -3;
            return this;
        }

        public final DeviceParams clearPrimaryButton() {
            this.primaryButton_ = 1;
            this.bitField0_ &= -129;
            return this;
        }

        public final DeviceParams clearScreenToLensDistance() {
            this.screenToLensDistance_ = 0.0f;
            this.bitField0_ &= -5;
            return this;
        }

        public final DeviceParams clearTrayToLensDistance() {
            this.trayToLensDistance_ = 0.0f;
            this.bitField0_ &= -33;
            return this;
        }

        public final DeviceParams clearVendor() {
            this.vendor_ = "";
            this.bitField0_ &= -2;
            return this;
        }

        public final DeviceParams clearVerticalAlignment() {
            this.verticalAlignment_ = 0;
            this.bitField0_ &= -17;
            return this;
        }

        public final DeviceParams clone() {
            try {
                DeviceParams deviceParams = (DeviceParams) super.clone();
                if (this.leftEyeFieldOfViewAngles != null && this.leftEyeFieldOfViewAngles.length > 0) {
                    deviceParams.leftEyeFieldOfViewAngles = (float[]) this.leftEyeFieldOfViewAngles.clone();
                }
                if (this.distortionCoefficients != null && this.distortionCoefficients.length > 0) {
                    deviceParams.distortionCoefficients = (float[]) this.distortionCoefficients.clone();
                }
                if (this.internal != null) {
                    deviceParams.internal = this.internal.clone();
                }
                if (this.daydreamInternal != null) {
                    deviceParams.daydreamInternal = this.daydreamInternal.clone();
                }
                return deviceParams;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.vendor_);
            }
            if ((this.bitField0_ & 2) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.model_);
            }
            if ((this.bitField0_ & 4) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.screenToLensDistance_);
            }
            if ((this.bitField0_ & 8) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.interLensDistance_);
            }
            if (this.leftEyeFieldOfViewAngles != null && this.leftEyeFieldOfViewAngles.length > 0) {
                int length = this.leftEyeFieldOfViewAngles.length * 4;
                computeSerializedSize = computeSerializedSize + length + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(length);
            }
            if ((this.bitField0_ & 32) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(6, this.trayToLensDistance_);
            }
            if (this.distortionCoefficients != null && this.distortionCoefficients.length > 0) {
                int length2 = this.distortionCoefficients.length * 4;
                computeSerializedSize = computeSerializedSize + length2 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(length2);
            }
            if ((this.bitField0_ & 64) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(10, this.hasMagnet_);
            }
            if ((this.bitField0_ & 16) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, this.verticalAlignment_);
            }
            if ((this.bitField0_ & 128) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, this.primaryButton_);
            }
            if (this.internal != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1729, this.internal);
            }
            return this.daydreamInternal == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(196883, this.daydreamInternal);
        }

        public final boolean getHasMagnet() {
            return this.hasMagnet_;
        }

        public final float getInterLensDistance() {
            return this.interLensDistance_;
        }

        public final String getModel() {
            return this.model_;
        }

        public final int getPrimaryButton() {
            return this.primaryButton_;
        }

        public final float getScreenToLensDistance() {
            return this.screenToLensDistance_;
        }

        public final float getTrayToLensDistance() {
            return this.trayToLensDistance_;
        }

        public final String getVendor() {
            return this.vendor_;
        }

        public final int getVerticalAlignment() {
            return this.verticalAlignment_;
        }

        public final boolean hasHasMagnet() {
            return (this.bitField0_ & 64) != 0;
        }

        public final boolean hasInterLensDistance() {
            return (this.bitField0_ & 8) != 0;
        }

        public final boolean hasModel() {
            return (this.bitField0_ & 2) != 0;
        }

        public final boolean hasPrimaryButton() {
            return (this.bitField0_ & 128) != 0;
        }

        public final boolean hasScreenToLensDistance() {
            return (this.bitField0_ & 4) != 0;
        }

        public final boolean hasTrayToLensDistance() {
            return (this.bitField0_ & 32) != 0;
        }

        public final boolean hasVendor() {
            return (this.bitField0_ & 1) != 0;
        }

        public final boolean hasVerticalAlignment() {
            return (this.bitField0_ & 16) != 0;
        }

        public final DeviceParams mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 10:
                        this.vendor_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 1;
                        break;
                    case 18:
                        this.model_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 2;
                        break;
                    case 29:
                        this.screenToLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 4;
                        break;
                    case 37:
                        this.interLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 8;
                        break;
                    case 42:
                        int readRawVarint32 = codedInputByteBufferNano.readRawVarint32();
                        int pushLimit = codedInputByteBufferNano.pushLimit(readRawVarint32);
                        int i = readRawVarint32 / 4;
                        int length = this.leftEyeFieldOfViewAngles != null ? this.leftEyeFieldOfViewAngles.length : 0;
                        float[] fArr = new float[(i + length)];
                        if (length != 0) {
                            System.arraycopy(this.leftEyeFieldOfViewAngles, 0, fArr, 0, length);
                        }
                        while (length < fArr.length) {
                            fArr[length] = codedInputByteBufferNano.readFloat();
                            length++;
                        }
                        this.leftEyeFieldOfViewAngles = fArr;
                        codedInputByteBufferNano.popLimit(pushLimit);
                        break;
                    case 45:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 45);
                        int length2 = this.leftEyeFieldOfViewAngles != null ? this.leftEyeFieldOfViewAngles.length : 0;
                        float[] fArr2 = new float[(repeatedFieldArrayLength + length2)];
                        if (length2 != 0) {
                            System.arraycopy(this.leftEyeFieldOfViewAngles, 0, fArr2, 0, length2);
                        }
                        while (length2 < fArr2.length - 1) {
                            fArr2[length2] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            length2++;
                        }
                        fArr2[length2] = codedInputByteBufferNano.readFloat();
                        this.leftEyeFieldOfViewAngles = fArr2;
                        break;
                    case 53:
                        this.trayToLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 32;
                        break;
                    case 58:
                        int readRawVarint322 = codedInputByteBufferNano.readRawVarint32();
                        int pushLimit2 = codedInputByteBufferNano.pushLimit(readRawVarint322);
                        int i2 = readRawVarint322 / 4;
                        int length3 = this.distortionCoefficients != null ? this.distortionCoefficients.length : 0;
                        float[] fArr3 = new float[(i2 + length3)];
                        if (length3 != 0) {
                            System.arraycopy(this.distortionCoefficients, 0, fArr3, 0, length3);
                        }
                        while (length3 < fArr3.length) {
                            fArr3[length3] = codedInputByteBufferNano.readFloat();
                            length3++;
                        }
                        this.distortionCoefficients = fArr3;
                        codedInputByteBufferNano.popLimit(pushLimit2);
                        break;
                    case 61:
                        int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 61);
                        int length4 = this.distortionCoefficients != null ? this.distortionCoefficients.length : 0;
                        float[] fArr4 = new float[(repeatedFieldArrayLength2 + length4)];
                        if (length4 != 0) {
                            System.arraycopy(this.distortionCoefficients, 0, fArr4, 0, length4);
                        }
                        while (length4 < fArr4.length - 1) {
                            fArr4[length4] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            length4++;
                        }
                        fArr4[length4] = codedInputByteBufferNano.readFloat();
                        this.distortionCoefficients = fArr4;
                        break;
                    case 80:
                        this.hasMagnet_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 64;
                        break;
                    case 88:
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        switch (readInt32) {
                            case 0:
                            case 1:
                            case 2:
                                this.verticalAlignment_ = readInt32;
                                this.bitField0_ |= 16;
                                break;
                        }
                    case 96:
                        int readInt322 = codedInputByteBufferNano.readInt32();
                        switch (readInt322) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.primaryButton_ = readInt322;
                                this.bitField0_ |= 128;
                                break;
                        }
                    case 13834:
                        if (this.internal == null) {
                            this.internal = new CardboardInternalParams();
                        }
                        codedInputByteBufferNano.readMessage(this.internal);
                        break;
                    case 1575066:
                        if (this.daydreamInternal == null) {
                            this.daydreamInternal = new DaydreamInternalParams();
                        }
                        codedInputByteBufferNano.readMessage(this.daydreamInternal);
                        break;
                    default:
                        if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public final DeviceParams setHasMagnet(boolean z) {
            this.hasMagnet_ = z;
            this.bitField0_ |= 64;
            return this;
        }

        public final DeviceParams setInterLensDistance(float f) {
            this.interLensDistance_ = f;
            this.bitField0_ |= 8;
            return this;
        }

        public final DeviceParams setModel(String str) {
            if (str != null) {
                this.model_ = str;
                this.bitField0_ |= 2;
                return this;
            }
            throw new NullPointerException();
        }

        public final DeviceParams setPrimaryButton(int i) {
            this.primaryButton_ = i;
            this.bitField0_ |= 128;
            return this;
        }

        public final DeviceParams setScreenToLensDistance(float f) {
            this.screenToLensDistance_ = f;
            this.bitField0_ |= 4;
            return this;
        }

        public final DeviceParams setTrayToLensDistance(float f) {
            this.trayToLensDistance_ = f;
            this.bitField0_ |= 32;
            return this;
        }

        public final DeviceParams setVendor(String str) {
            if (str != null) {
                this.vendor_ = str;
                this.bitField0_ |= 1;
                return this;
            }
            throw new NullPointerException();
        }

        public final DeviceParams setVerticalAlignment(int i) {
            this.verticalAlignment_ = i;
            this.bitField0_ |= 16;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeString(1, this.vendor_);
            }
            if ((this.bitField0_ & 2) != 0) {
                codedOutputByteBufferNano.writeString(2, this.model_);
            }
            if ((this.bitField0_ & 4) != 0) {
                codedOutputByteBufferNano.writeFloat(3, this.screenToLensDistance_);
            }
            if ((this.bitField0_ & 8) != 0) {
                codedOutputByteBufferNano.writeFloat(4, this.interLensDistance_);
            }
            if (this.leftEyeFieldOfViewAngles != null && this.leftEyeFieldOfViewAngles.length > 0) {
                codedOutputByteBufferNano.writeRawVarint32(42);
                codedOutputByteBufferNano.writeRawVarint32(this.leftEyeFieldOfViewAngles.length * 4);
                for (float writeFloatNoTag : this.leftEyeFieldOfViewAngles) {
                    codedOutputByteBufferNano.writeFloatNoTag(writeFloatNoTag);
                }
            }
            if ((this.bitField0_ & 32) != 0) {
                codedOutputByteBufferNano.writeFloat(6, this.trayToLensDistance_);
            }
            if (this.distortionCoefficients != null && this.distortionCoefficients.length > 0) {
                codedOutputByteBufferNano.writeRawVarint32(58);
                codedOutputByteBufferNano.writeRawVarint32(this.distortionCoefficients.length * 4);
                for (float writeFloatNoTag2 : this.distortionCoefficients) {
                    codedOutputByteBufferNano.writeFloatNoTag(writeFloatNoTag2);
                }
            }
            if ((this.bitField0_ & 64) != 0) {
                codedOutputByteBufferNano.writeBool(10, this.hasMagnet_);
            }
            if ((this.bitField0_ & 16) != 0) {
                codedOutputByteBufferNano.writeInt32(11, this.verticalAlignment_);
            }
            if ((this.bitField0_ & 128) != 0) {
                codedOutputByteBufferNano.writeInt32(12, this.primaryButton_);
            }
            if (this.internal != null) {
                codedOutputByteBufferNano.writeMessage(1729, this.internal);
            }
            if (this.daydreamInternal != null) {
                codedOutputByteBufferNano.writeMessage(196883, this.daydreamInternal);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class ScreenAlignmentMarker extends ExtendableMessageNano<ScreenAlignmentMarker> implements Cloneable {
        private static volatile ScreenAlignmentMarker[] _emptyArray;
        private int bitField0_;
        private float horizontal_;
        private float vertical_;

        public ScreenAlignmentMarker() {
            clear();
        }

        public static ScreenAlignmentMarker[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ScreenAlignmentMarker[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static ScreenAlignmentMarker parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new ScreenAlignmentMarker().mergeFrom(codedInputByteBufferNano);
        }

        public static ScreenAlignmentMarker parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (ScreenAlignmentMarker) MessageNano.mergeFrom(new ScreenAlignmentMarker(), bArr);
        }

        public final ScreenAlignmentMarker clear() {
            this.bitField0_ = 0;
            this.horizontal_ = 0.0f;
            this.vertical_ = 0.0f;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final ScreenAlignmentMarker clearHorizontal() {
            this.horizontal_ = 0.0f;
            this.bitField0_ &= -2;
            return this;
        }

        public final ScreenAlignmentMarker clearVertical() {
            this.vertical_ = 0.0f;
            this.bitField0_ &= -3;
            return this;
        }

        public final ScreenAlignmentMarker clone() {
            try {
                return (ScreenAlignmentMarker) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.horizontal_);
            }
            return (this.bitField0_ & 2) == 0 ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.vertical_);
        }

        public final float getHorizontal() {
            return this.horizontal_;
        }

        public final float getVertical() {
            return this.vertical_;
        }

        public final boolean hasHorizontal() {
            return (this.bitField0_ & 1) != 0;
        }

        public final boolean hasVertical() {
            return (this.bitField0_ & 2) != 0;
        }

        public final ScreenAlignmentMarker mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 13:
                        this.horizontal_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 1;
                        break;
                    case 21:
                        this.vertical_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 2;
                        break;
                    default:
                        if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public final ScreenAlignmentMarker setHorizontal(float f) {
            this.horizontal_ = f;
            this.bitField0_ |= 1;
            return this;
        }

        public final ScreenAlignmentMarker setVertical(float f) {
            this.vertical_ = f;
            this.bitField0_ |= 2;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeFloat(1, this.horizontal_);
            }
            if ((this.bitField0_ & 2) != 0) {
                codedOutputByteBufferNano.writeFloat(2, this.vertical_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
