// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vrtoolkit.cardboard.proto.nano;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.ExtendableMessageNano;

public interface CardboardDevice
{
    public static final class CardboardInternalParams extends ExtendableMessageNano<CardboardInternalParams> implements Cloneable
    {
        private static volatile CardboardInternalParams[] _emptyArray;
        private String accelerometer_;
        private int bitField0_;
        public int[] eyeOrientations;
        private String gyroscope_;
        private float screenCenterToLensDistance_;
        private float xPpiOverride_;
        private float yPpiOverride_;
        
        public CardboardInternalParams() {
            this.clear();
        }
        
        public static CardboardInternalParams[] emptyArray() {
            if (CardboardInternalParams._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (CardboardInternalParams._emptyArray != null) {
                                break;
                            }
                        }
                        CardboardInternalParams._emptyArray = new CardboardInternalParams[0];
                        continue;
                    }
                }
            }
            return CardboardInternalParams._emptyArray;
        }
        
        public static CardboardInternalParams parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new CardboardInternalParams().mergeFrom(codedInputByteBufferNano);
        }
        
        public static CardboardInternalParams parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new CardboardInternalParams(), array);
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
            this.bitField0_ &= 0xFFFFFFF7;
            return this;
        }
        
        public final CardboardInternalParams clearGyroscope() {
            this.gyroscope_ = "";
            this.bitField0_ &= 0xFFFFFFEF;
            return this;
        }
        
        public final CardboardInternalParams clearScreenCenterToLensDistance() {
            this.screenCenterToLensDistance_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        public final CardboardInternalParams clearXPpiOverride() {
            this.xPpiOverride_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }
        
        public final CardboardInternalParams clearYPpiOverride() {
            this.yPpiOverride_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFB;
            return this;
        }
        
        @Override
        public final CardboardInternalParams clone() {
            while (true) {
                CardboardInternalParams cardboardInternalParams;
                try {
                    cardboardInternalParams = super.clone();
                    if (this.eyeOrientations == null) {
                        return cardboardInternalParams;
                    }
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
                if (this.eyeOrientations.length > 0) {
                    cardboardInternalParams.eyeOrientations = this.eyeOrientations.clone();
                    return cardboardInternalParams;
                }
                return cardboardInternalParams;
            }
        }
        
        @Override
        protected final int computeSerializedSize() {
            int i = 0;
            final int computeSerializedSize = super.computeSerializedSize();
            int n2;
            if (this.eyeOrientations != null && this.eyeOrientations.length > 0) {
                int n = 0;
                while (i < this.eyeOrientations.length) {
                    n += CodedOutputByteBufferNano.computeInt32SizeNoTag(this.eyeOrientations[i]);
                    ++i;
                }
                n2 = computeSerializedSize + n + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n);
            }
            else {
                n2 = computeSerializedSize;
            }
            if ((this.bitField0_ & 0x1) != 0x0) {
                n2 += CodedOutputByteBufferNano.computeFloatSize(2, this.screenCenterToLensDistance_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                n2 += CodedOutputByteBufferNano.computeFloatSize(3, this.xPpiOverride_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                n2 += CodedOutputByteBufferNano.computeFloatSize(4, this.yPpiOverride_);
            }
            if ((this.bitField0_ & 0x8) != 0x0) {
                n2 += CodedOutputByteBufferNano.computeStringSize(5, this.accelerometer_);
            }
            if ((this.bitField0_ & 0x10) != 0x0) {
                n2 += CodedOutputByteBufferNano.computeStringSize(6, this.gyroscope_);
            }
            return n2;
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
            return (this.bitField0_ & 0x8) != 0x0;
        }
        
        public final boolean hasGyroscope() {
            return (this.bitField0_ & 0x10) != 0x0;
        }
        
        public final boolean hasScreenCenterToLensDistance() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        public final boolean hasXPpiOverride() {
            return (this.bitField0_ & 0x2) != 0x0;
        }
        
        public final boolean hasYPpiOverride() {
            return (this.bitField0_ & 0x4) != 0x0;
        }
        
        @Override
        public final CardboardInternalParams mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    default: {
                        if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    case 0: {
                        return this;
                    }
                    case 8: {
                        final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 8);
                        final int[] eyeOrientations = new int[repeatedFieldArrayLength];
                        int i = 0;
                        int n = 0;
                        while (i < repeatedFieldArrayLength) {
                            if (i != 0) {
                                codedInputByteBufferNano.readTag();
                            }
                            final int int32 = codedInputByteBufferNano.readInt32();
                            switch (int32) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7: {
                                    eyeOrientations[n] = int32;
                                    ++n;
                                    break;
                                }
                            }
                            ++i;
                        }
                        if (n == 0) {
                            continue;
                        }
                        int length;
                        if (this.eyeOrientations != null) {
                            length = this.eyeOrientations.length;
                        }
                        else {
                            length = 0;
                        }
                        if (length == 0 && n == repeatedFieldArrayLength) {
                            this.eyeOrientations = eyeOrientations;
                            continue;
                        }
                        final int[] eyeOrientations2 = new int[length + n];
                        if (length != 0) {
                            System.arraycopy(this.eyeOrientations, 0, eyeOrientations2, 0, length);
                        }
                        System.arraycopy(eyeOrientations, 0, eyeOrientations2, length, n);
                        this.eyeOrientations = eyeOrientations2;
                        continue;
                    }
                    case 10: {
                        final int pushLimit = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
                        final int position = codedInputByteBufferNano.getPosition();
                        int n2 = 0;
                        while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                            switch (codedInputByteBufferNano.readInt32()) {
                                default: {
                                    continue;
                                }
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7: {
                                    ++n2;
                                    continue;
                                }
                            }
                        }
                        if (n2 != 0) {
                            codedInputByteBufferNano.rewindToPosition(position);
                            int length2;
                            if (this.eyeOrientations != null) {
                                length2 = this.eyeOrientations.length;
                            }
                            else {
                                length2 = 0;
                            }
                            final int[] eyeOrientations3 = new int[n2 + length2];
                            if (length2 != 0) {
                                System.arraycopy(this.eyeOrientations, 0, eyeOrientations3, 0, length2);
                            }
                            while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                                final int int33 = codedInputByteBufferNano.readInt32();
                                switch (int33) {
                                    default: {
                                        continue;
                                    }
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7: {
                                        eyeOrientations3[length2] = int33;
                                        ++length2;
                                        continue;
                                    }
                                }
                            }
                            this.eyeOrientations = eyeOrientations3;
                        }
                        codedInputByteBufferNano.popLimit(pushLimit);
                        continue;
                    }
                    case 21: {
                        this.screenCenterToLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                    case 29: {
                        this.xPpiOverride_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x2;
                        continue;
                    }
                    case 37: {
                        this.yPpiOverride_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x4;
                        continue;
                    }
                    case 42: {
                        this.accelerometer_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 0x8;
                        continue;
                    }
                    case 50: {
                        this.gyroscope_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 0x10;
                        continue;
                    }
                }
            }
        }
        
        public final CardboardInternalParams setAccelerometer(final String accelerometer_) {
            if (accelerometer_ != null) {
                this.accelerometer_ = accelerometer_;
                this.bitField0_ |= 0x8;
                return this;
            }
            throw new NullPointerException();
        }
        
        public final CardboardInternalParams setGyroscope(final String gyroscope_) {
            if (gyroscope_ != null) {
                this.gyroscope_ = gyroscope_;
                this.bitField0_ |= 0x10;
                return this;
            }
            throw new NullPointerException();
        }
        
        public final CardboardInternalParams setScreenCenterToLensDistance(final float screenCenterToLensDistance_) {
            this.screenCenterToLensDistance_ = screenCenterToLensDistance_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        public final CardboardInternalParams setXPpiOverride(final float xPpiOverride_) {
            this.xPpiOverride_ = xPpiOverride_;
            this.bitField0_ |= 0x2;
            return this;
        }
        
        public final CardboardInternalParams setYPpiOverride(final float yPpiOverride_) {
            this.yPpiOverride_ = yPpiOverride_;
            this.bitField0_ |= 0x4;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            final int n = 0;
            if (this.eyeOrientations != null && this.eyeOrientations.length > 0) {
                int i = 0;
                int n2 = 0;
                while (i < this.eyeOrientations.length) {
                    n2 += CodedOutputByteBufferNano.computeInt32SizeNoTag(this.eyeOrientations[i]);
                    ++i;
                }
                codedOutputByteBufferNano.writeRawVarint32(10);
                codedOutputByteBufferNano.writeRawVarint32(n2);
                for (int j = n; j < this.eyeOrientations.length; ++j) {
                    codedOutputByteBufferNano.writeRawVarint32(this.eyeOrientations[j]);
                }
            }
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeFloat(2, this.screenCenterToLensDistance_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                codedOutputByteBufferNano.writeFloat(3, this.xPpiOverride_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                codedOutputByteBufferNano.writeFloat(4, this.yPpiOverride_);
            }
            if ((this.bitField0_ & 0x8) != 0x0) {
                codedOutputByteBufferNano.writeString(5, this.accelerometer_);
            }
            if ((this.bitField0_ & 0x10) != 0x0) {
                codedOutputByteBufferNano.writeString(6, this.gyroscope_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
        
        public interface OrientationType
        {
            public static final int CCW_0_DEGREES = 0;
            public static final int CCW_0_DEGREES_MIRRORED = 4;
            public static final int CCW_180_DEGREES = 2;
            public static final int CCW_180_DEGREES_MIRRORED = 6;
            public static final int CCW_270_DEGREES = 3;
            public static final int CCW_270_DEGREES_MIRRORED = 7;
            public static final int CCW_90_DEGREES = 1;
            public static final int CCW_90_DEGREES_MIRRORED = 5;
        }
    }
    
    public static final class DaydreamInternalParams extends ExtendableMessageNano<DaydreamInternalParams> implements Cloneable
    {
        private static volatile DaydreamInternalParams[] _emptyArray;
        public ScreenAlignmentMarker[] alignmentMarkers;
        private int bitField0_;
        private int version_;
        
        public DaydreamInternalParams() {
            this.clear();
        }
        
        public static DaydreamInternalParams[] emptyArray() {
            if (DaydreamInternalParams._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (DaydreamInternalParams._emptyArray != null) {
                                break;
                            }
                        }
                        DaydreamInternalParams._emptyArray = new DaydreamInternalParams[0];
                        continue;
                    }
                }
            }
            return DaydreamInternalParams._emptyArray;
        }
        
        public static DaydreamInternalParams parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DaydreamInternalParams().mergeFrom(codedInputByteBufferNano);
        }
        
        public static DaydreamInternalParams parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new DaydreamInternalParams(), array);
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
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        @Override
        public final DaydreamInternalParams clone() {
            while (true) {
                int i = 0;
                DaydreamInternalParams daydreamInternalParams;
                try {
                    daydreamInternalParams = super.clone();
                    if (this.alignmentMarkers == null) {
                        return daydreamInternalParams;
                    }
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
                if (this.alignmentMarkers.length > 0) {
                    daydreamInternalParams.alignmentMarkers = new ScreenAlignmentMarker[this.alignmentMarkers.length];
                    while (i < this.alignmentMarkers.length) {
                        if (this.alignmentMarkers[i] != null) {
                            daydreamInternalParams.alignmentMarkers[i] = this.alignmentMarkers[i].clone();
                        }
                        ++i;
                    }
                    return daydreamInternalParams;
                }
                return daydreamInternalParams;
            }
        }
        
        @Override
        protected final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 0x1) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.version_);
            }
            int n;
            if (this.alignmentMarkers == null) {
                n = computeSerializedSize;
            }
            else {
                n = computeSerializedSize;
                if (this.alignmentMarkers.length > 0) {
                    for (int i = 0; i < this.alignmentMarkers.length; ++i) {
                        final ScreenAlignmentMarker screenAlignmentMarker = this.alignmentMarkers[i];
                        if (screenAlignmentMarker != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, screenAlignmentMarker);
                        }
                    }
                    n = computeSerializedSize;
                }
            }
            return n;
        }
        
        public final int getVersion() {
            return this.version_;
        }
        
        public final boolean hasVersion() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        @Override
        public final DaydreamInternalParams mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    default: {
                        if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    case 0: {
                        return this;
                    }
                    case 8: {
                        this.version_ = codedInputByteBufferNano.readInt32();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                    case 18: {
                        final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                        int i;
                        if (this.alignmentMarkers != null) {
                            i = this.alignmentMarkers.length;
                        }
                        else {
                            i = 0;
                        }
                        final ScreenAlignmentMarker[] alignmentMarkers = new ScreenAlignmentMarker[repeatedFieldArrayLength + i];
                        if (i != 0) {
                            System.arraycopy(this.alignmentMarkers, 0, alignmentMarkers, 0, i);
                        }
                        while (i < alignmentMarkers.length - 1) {
                            codedInputByteBufferNano.readMessage(alignmentMarkers[i] = new ScreenAlignmentMarker());
                            codedInputByteBufferNano.readTag();
                            ++i;
                        }
                        codedInputByteBufferNano.readMessage(alignmentMarkers[i] = new ScreenAlignmentMarker());
                        this.alignmentMarkers = alignmentMarkers;
                        continue;
                    }
                }
            }
        }
        
        public final DaydreamInternalParams setVersion(final int version_) {
            this.version_ = version_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = 0;
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeInt32(1, this.version_);
            }
            if (this.alignmentMarkers != null && this.alignmentMarkers.length > 0) {
                while (i < this.alignmentMarkers.length) {
                    final ScreenAlignmentMarker screenAlignmentMarker = this.alignmentMarkers[i];
                    if (screenAlignmentMarker != null) {
                        codedOutputByteBufferNano.writeMessage(2, screenAlignmentMarker);
                    }
                    ++i;
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
    
    public static final class DeviceParams extends ExtendableMessageNano<DeviceParams> implements Cloneable
    {
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
        
        public DeviceParams() {
            this.clear();
        }
        
        public static DeviceParams[] emptyArray() {
            if (DeviceParams._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (DeviceParams._emptyArray != null) {
                                break;
                            }
                        }
                        DeviceParams._emptyArray = new DeviceParams[0];
                        continue;
                    }
                }
            }
            return DeviceParams._emptyArray;
        }
        
        public static DeviceParams parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DeviceParams().mergeFrom(codedInputByteBufferNano);
        }
        
        public static DeviceParams parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new DeviceParams(), array);
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
            this.bitField0_ &= 0xFFFFFFBF;
            return this;
        }
        
        public final DeviceParams clearInterLensDistance() {
            this.interLensDistance_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFF7;
            return this;
        }
        
        public final DeviceParams clearModel() {
            this.model_ = "";
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }
        
        public final DeviceParams clearPrimaryButton() {
            this.primaryButton_ = 1;
            this.bitField0_ &= 0xFFFFFF7F;
            return this;
        }
        
        public final DeviceParams clearScreenToLensDistance() {
            this.screenToLensDistance_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFB;
            return this;
        }
        
        public final DeviceParams clearTrayToLensDistance() {
            this.trayToLensDistance_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFDF;
            return this;
        }
        
        public final DeviceParams clearVendor() {
            this.vendor_ = "";
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        public final DeviceParams clearVerticalAlignment() {
            this.verticalAlignment_ = 0;
            this.bitField0_ &= 0xFFFFFFEF;
            return this;
        }
        
        @Override
        public final DeviceParams clone() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
            //     4: checkcast       Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams;
            //     7: astore_1       
            //     8: aload_0        
            //     9: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.leftEyeFieldOfViewAngles:[F
            //    12: ifnonnull       48
            //    15: aload_0        
            //    16: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.distortionCoefficients:[F
            //    19: ifnonnull       73
            //    22: aload_0        
            //    23: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.internal:Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$CardboardInternalParams;
            //    26: ifnonnull       98
            //    29: aload_0        
            //    30: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.daydreamInternal:Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DaydreamInternalParams;
            //    33: ifnonnull       112
            //    36: aload_1        
            //    37: areturn        
            //    38: astore_1       
            //    39: new             Ljava/lang/AssertionError;
            //    42: dup            
            //    43: aload_1        
            //    44: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
            //    47: athrow         
            //    48: aload_0        
            //    49: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.leftEyeFieldOfViewAngles:[F
            //    52: arraylength    
            //    53: ifle            15
            //    56: aload_1        
            //    57: aload_0        
            //    58: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.leftEyeFieldOfViewAngles:[F
            //    61: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
            //    64: checkcast       [F
            //    67: putfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.leftEyeFieldOfViewAngles:[F
            //    70: goto            15
            //    73: aload_0        
            //    74: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.distortionCoefficients:[F
            //    77: arraylength    
            //    78: ifle            22
            //    81: aload_1        
            //    82: aload_0        
            //    83: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.distortionCoefficients:[F
            //    86: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
            //    89: checkcast       [F
            //    92: putfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.distortionCoefficients:[F
            //    95: goto            22
            //    98: aload_1        
            //    99: aload_0        
            //   100: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.internal:Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$CardboardInternalParams;
            //   103: invokevirtual   com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$CardboardInternalParams.clone:()Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$CardboardInternalParams;
            //   106: putfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.internal:Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$CardboardInternalParams;
            //   109: goto            29
            //   112: aload_1        
            //   113: aload_0        
            //   114: getfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.daydreamInternal:Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DaydreamInternalParams;
            //   117: invokevirtual   com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DaydreamInternalParams.clone:()Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DaydreamInternalParams;
            //   120: putfield        com/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DeviceParams.daydreamInternal:Lcom/google/vrtoolkit/cardboard/proto/nano/CardboardDevice$DaydreamInternalParams;
            //   123: goto            36
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                  
            //  -----  -----  -----  -----  --------------------------------------
            //  0      8      38     48     Ljava/lang/CloneNotSupportedException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:float[](DeviceParams::distortionCoefficients, this:DeviceParams)), ldc:int(0))
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
            //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
        
        @Override
        protected final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 0x1) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.vendor_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.model_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.screenToLensDistance_);
            }
            if ((this.bitField0_ & 0x8) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.interLensDistance_);
            }
            int n;
            if (this.leftEyeFieldOfViewAngles == null) {
                n = computeSerializedSize;
            }
            else {
                n = computeSerializedSize;
                if (this.leftEyeFieldOfViewAngles.length > 0) {
                    final int n2 = this.leftEyeFieldOfViewAngles.length * 4;
                    n = computeSerializedSize + n2 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n2);
                }
            }
            int n3;
            if ((this.bitField0_ & 0x20) == 0x0) {
                n3 = n;
            }
            else {
                n3 = n + CodedOutputByteBufferNano.computeFloatSize(6, this.trayToLensDistance_);
            }
            int n4;
            if (this.distortionCoefficients == null) {
                n4 = n3;
            }
            else {
                n4 = n3;
                if (this.distortionCoefficients.length > 0) {
                    final int n5 = this.distortionCoefficients.length * 4;
                    n4 = n3 + n5 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n5);
                }
            }
            int n6;
            if ((this.bitField0_ & 0x40) == 0x0) {
                n6 = n4;
            }
            else {
                n6 = n4 + CodedOutputByteBufferNano.computeBoolSize(10, this.hasMagnet_);
            }
            if ((this.bitField0_ & 0x10) != 0x0) {
                n6 += CodedOutputByteBufferNano.computeInt32Size(11, this.verticalAlignment_);
            }
            if ((this.bitField0_ & 0x80) != 0x0) {
                n6 += CodedOutputByteBufferNano.computeInt32Size(12, this.primaryButton_);
            }
            if (this.internal != null) {
                n6 += CodedOutputByteBufferNano.computeMessageSize(1729, this.internal);
            }
            if (this.daydreamInternal != null) {
                n6 += CodedOutputByteBufferNano.computeMessageSize(196883, this.daydreamInternal);
            }
            return n6;
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
            return (this.bitField0_ & 0x40) != 0x0;
        }
        
        public final boolean hasInterLensDistance() {
            return (this.bitField0_ & 0x8) != 0x0;
        }
        
        public final boolean hasModel() {
            return (this.bitField0_ & 0x2) != 0x0;
        }
        
        public final boolean hasPrimaryButton() {
            return (this.bitField0_ & 0x80) != 0x0;
        }
        
        public final boolean hasScreenToLensDistance() {
            return (this.bitField0_ & 0x4) != 0x0;
        }
        
        public final boolean hasTrayToLensDistance() {
            return (this.bitField0_ & 0x20) != 0x0;
        }
        
        public final boolean hasVendor() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        public final boolean hasVerticalAlignment() {
            return (this.bitField0_ & 0x10) != 0x0;
        }
        
        @Override
        public final DeviceParams mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    default: {
                        if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    case 0: {
                        return this;
                    }
                    case 10: {
                        this.vendor_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                    case 18: {
                        this.model_ = codedInputByteBufferNano.readString();
                        this.bitField0_ |= 0x2;
                        continue;
                    }
                    case 29: {
                        this.screenToLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x4;
                        continue;
                    }
                    case 37: {
                        this.interLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x8;
                        continue;
                    }
                    case 45: {
                        final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 45);
                        int i;
                        if (this.leftEyeFieldOfViewAngles != null) {
                            i = this.leftEyeFieldOfViewAngles.length;
                        }
                        else {
                            i = 0;
                        }
                        final float[] leftEyeFieldOfViewAngles = new float[repeatedFieldArrayLength + i];
                        if (i != 0) {
                            System.arraycopy(this.leftEyeFieldOfViewAngles, 0, leftEyeFieldOfViewAngles, 0, i);
                        }
                        while (i < leftEyeFieldOfViewAngles.length - 1) {
                            leftEyeFieldOfViewAngles[i] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            ++i;
                        }
                        leftEyeFieldOfViewAngles[i] = codedInputByteBufferNano.readFloat();
                        this.leftEyeFieldOfViewAngles = leftEyeFieldOfViewAngles;
                        continue;
                    }
                    case 42: {
                        final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                        final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                        final int n = rawVarint32 / 4;
                        int j;
                        if (this.leftEyeFieldOfViewAngles != null) {
                            j = this.leftEyeFieldOfViewAngles.length;
                        }
                        else {
                            j = 0;
                        }
                        final float[] leftEyeFieldOfViewAngles2 = new float[n + j];
                        if (j != 0) {
                            System.arraycopy(this.leftEyeFieldOfViewAngles, 0, leftEyeFieldOfViewAngles2, 0, j);
                        }
                        while (j < leftEyeFieldOfViewAngles2.length) {
                            leftEyeFieldOfViewAngles2[j] = codedInputByteBufferNano.readFloat();
                            ++j;
                        }
                        this.leftEyeFieldOfViewAngles = leftEyeFieldOfViewAngles2;
                        codedInputByteBufferNano.popLimit(pushLimit);
                        continue;
                    }
                    case 53: {
                        this.trayToLensDistance_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x20;
                        continue;
                    }
                    case 61: {
                        final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 61);
                        int k;
                        if (this.distortionCoefficients != null) {
                            k = this.distortionCoefficients.length;
                        }
                        else {
                            k = 0;
                        }
                        final float[] distortionCoefficients = new float[repeatedFieldArrayLength2 + k];
                        if (k != 0) {
                            System.arraycopy(this.distortionCoefficients, 0, distortionCoefficients, 0, k);
                        }
                        while (k < distortionCoefficients.length - 1) {
                            distortionCoefficients[k] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            ++k;
                        }
                        distortionCoefficients[k] = codedInputByteBufferNano.readFloat();
                        this.distortionCoefficients = distortionCoefficients;
                        continue;
                    }
                    case 58: {
                        final int rawVarint33 = codedInputByteBufferNano.readRawVarint32();
                        final int pushLimit2 = codedInputByteBufferNano.pushLimit(rawVarint33);
                        final int n2 = rawVarint33 / 4;
                        int l;
                        if (this.distortionCoefficients != null) {
                            l = this.distortionCoefficients.length;
                        }
                        else {
                            l = 0;
                        }
                        final float[] distortionCoefficients2 = new float[n2 + l];
                        if (l != 0) {
                            System.arraycopy(this.distortionCoefficients, 0, distortionCoefficients2, 0, l);
                        }
                        while (l < distortionCoefficients2.length) {
                            distortionCoefficients2[l] = codedInputByteBufferNano.readFloat();
                            ++l;
                        }
                        this.distortionCoefficients = distortionCoefficients2;
                        codedInputByteBufferNano.popLimit(pushLimit2);
                        continue;
                    }
                    case 80: {
                        this.hasMagnet_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 0x40;
                        continue;
                    }
                    case 88: {
                        final int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            default: {
                                continue;
                            }
                            case 0:
                            case 1:
                            case 2: {
                                this.verticalAlignment_ = int32;
                                this.bitField0_ |= 0x10;
                                continue;
                            }
                        }
                        break;
                    }
                    case 96: {
                        final int int33 = codedInputByteBufferNano.readInt32();
                        switch (int33) {
                            default: {
                                continue;
                            }
                            case 0:
                            case 1:
                            case 2:
                            case 3: {
                                this.primaryButton_ = int33;
                                this.bitField0_ |= 0x80;
                                continue;
                            }
                        }
                        break;
                    }
                    case 13834: {
                        if (this.internal == null) {
                            this.internal = new CardboardInternalParams();
                        }
                        codedInputByteBufferNano.readMessage(this.internal);
                        continue;
                    }
                    case 1575066: {
                        if (this.daydreamInternal == null) {
                            this.daydreamInternal = new DaydreamInternalParams();
                        }
                        codedInputByteBufferNano.readMessage(this.daydreamInternal);
                        continue;
                    }
                }
            }
        }
        
        public final DeviceParams setHasMagnet(final boolean hasMagnet_) {
            this.hasMagnet_ = hasMagnet_;
            this.bitField0_ |= 0x40;
            return this;
        }
        
        public final DeviceParams setInterLensDistance(final float interLensDistance_) {
            this.interLensDistance_ = interLensDistance_;
            this.bitField0_ |= 0x8;
            return this;
        }
        
        public final DeviceParams setModel(final String model_) {
            if (model_ != null) {
                this.model_ = model_;
                this.bitField0_ |= 0x2;
                return this;
            }
            throw new NullPointerException();
        }
        
        public final DeviceParams setPrimaryButton(final int primaryButton_) {
            this.primaryButton_ = primaryButton_;
            this.bitField0_ |= 0x80;
            return this;
        }
        
        public final DeviceParams setScreenToLensDistance(final float screenToLensDistance_) {
            this.screenToLensDistance_ = screenToLensDistance_;
            this.bitField0_ |= 0x4;
            return this;
        }
        
        public final DeviceParams setTrayToLensDistance(final float trayToLensDistance_) {
            this.trayToLensDistance_ = trayToLensDistance_;
            this.bitField0_ |= 0x20;
            return this;
        }
        
        public final DeviceParams setVendor(final String vendor_) {
            if (vendor_ != null) {
                this.vendor_ = vendor_;
                this.bitField0_ |= 0x1;
                return this;
            }
            throw new NullPointerException();
        }
        
        public final DeviceParams setVerticalAlignment(final int verticalAlignment_) {
            this.verticalAlignment_ = verticalAlignment_;
            this.bitField0_ |= 0x10;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            final int n = 0;
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeString(1, this.vendor_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                codedOutputByteBufferNano.writeString(2, this.model_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                codedOutputByteBufferNano.writeFloat(3, this.screenToLensDistance_);
            }
            if ((this.bitField0_ & 0x8) != 0x0) {
                codedOutputByteBufferNano.writeFloat(4, this.interLensDistance_);
            }
            if (this.leftEyeFieldOfViewAngles != null && this.leftEyeFieldOfViewAngles.length > 0) {
                final int length = this.leftEyeFieldOfViewAngles.length;
                codedOutputByteBufferNano.writeRawVarint32(42);
                codedOutputByteBufferNano.writeRawVarint32(length * 4);
                for (int i = 0; i < this.leftEyeFieldOfViewAngles.length; ++i) {
                    codedOutputByteBufferNano.writeFloatNoTag(this.leftEyeFieldOfViewAngles[i]);
                }
            }
            if ((this.bitField0_ & 0x20) != 0x0) {
                codedOutputByteBufferNano.writeFloat(6, this.trayToLensDistance_);
            }
            if (this.distortionCoefficients != null && this.distortionCoefficients.length > 0) {
                final int length2 = this.distortionCoefficients.length;
                codedOutputByteBufferNano.writeRawVarint32(58);
                codedOutputByteBufferNano.writeRawVarint32(length2 * 4);
                for (int j = n; j < this.distortionCoefficients.length; ++j) {
                    codedOutputByteBufferNano.writeFloatNoTag(this.distortionCoefficients[j]);
                }
            }
            if ((this.bitField0_ & 0x40) != 0x0) {
                codedOutputByteBufferNano.writeBool(10, this.hasMagnet_);
            }
            if ((this.bitField0_ & 0x10) != 0x0) {
                codedOutputByteBufferNano.writeInt32(11, this.verticalAlignment_);
            }
            if ((this.bitField0_ & 0x80) != 0x0) {
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
        
        public interface ButtonType
        {
            public static final int INDIRECT_TOUCH = 3;
            public static final int MAGNET = 1;
            public static final int NONE = 0;
            public static final int TOUCH = 2;
        }
        
        public interface VerticalAlignmentType
        {
            public static final int BOTTOM = 0;
            public static final int CENTER = 1;
            public static final int TOP = 2;
        }
    }
    
    public static final class ScreenAlignmentMarker extends ExtendableMessageNano<ScreenAlignmentMarker> implements Cloneable
    {
        private static volatile ScreenAlignmentMarker[] _emptyArray;
        private int bitField0_;
        private float horizontal_;
        private float vertical_;
        
        public ScreenAlignmentMarker() {
            this.clear();
        }
        
        public static ScreenAlignmentMarker[] emptyArray() {
            if (ScreenAlignmentMarker._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (ScreenAlignmentMarker._emptyArray != null) {
                                break;
                            }
                        }
                        ScreenAlignmentMarker._emptyArray = new ScreenAlignmentMarker[0];
                        continue;
                    }
                }
            }
            return ScreenAlignmentMarker._emptyArray;
        }
        
        public static ScreenAlignmentMarker parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new ScreenAlignmentMarker().mergeFrom(codedInputByteBufferNano);
        }
        
        public static ScreenAlignmentMarker parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new ScreenAlignmentMarker(), array);
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
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        public final ScreenAlignmentMarker clearVertical() {
            this.vertical_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }
        
        @Override
        public final ScreenAlignmentMarker clone() {
            try {
                return super.clone();
            }
            catch (final CloneNotSupportedException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        protected final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 0x1) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.horizontal_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.vertical_);
            }
            return computeSerializedSize;
        }
        
        public final float getHorizontal() {
            return this.horizontal_;
        }
        
        public final float getVertical() {
            return this.vertical_;
        }
        
        public final boolean hasHorizontal() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        public final boolean hasVertical() {
            return (this.bitField0_ & 0x2) != 0x0;
        }
        
        @Override
        public final ScreenAlignmentMarker mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    default: {
                        if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    case 0: {
                        return this;
                    }
                    case 13: {
                        this.horizontal_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                    case 21: {
                        this.vertical_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x2;
                        continue;
                    }
                }
            }
        }
        
        public final ScreenAlignmentMarker setHorizontal(final float horizontal_) {
            this.horizontal_ = horizontal_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        public final ScreenAlignmentMarker setVertical(final float vertical_) {
            this.vertical_ = vertical_;
            this.bitField0_ |= 0x2;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeFloat(1, this.horizontal_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                codedOutputByteBufferNano.writeFloat(2, this.vertical_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
