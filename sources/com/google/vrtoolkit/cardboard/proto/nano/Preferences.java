package com.google.vrtoolkit.cardboard.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.ExtendableMessageNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;

public interface Preferences {

    public static final class DeveloperPrefs extends ExtendableMessageNano<DeveloperPrefs> implements Cloneable {
        private static volatile DeveloperPrefs[] _emptyArray;
        private int bitField0_;
        private boolean motophoPatchEnabled_;
        private boolean performanceMonitoringEnabled_;
        private boolean sensorLoggingEnabled_;

        public DeveloperPrefs() {
            clear();
        }

        public static DeveloperPrefs[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DeveloperPrefs[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static DeveloperPrefs parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DeveloperPrefs().mergeFrom(codedInputByteBufferNano);
        }

        public static DeveloperPrefs parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (DeveloperPrefs) MessageNano.mergeFrom(new DeveloperPrefs(), bArr);
        }

        public final DeveloperPrefs clear() {
            this.bitField0_ = 0;
            this.performanceMonitoringEnabled_ = false;
            this.sensorLoggingEnabled_ = false;
            this.motophoPatchEnabled_ = false;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final DeveloperPrefs clearMotophoPatchEnabled() {
            this.motophoPatchEnabled_ = false;
            this.bitField0_ &= -5;
            return this;
        }

        public final DeveloperPrefs clearPerformanceMonitoringEnabled() {
            this.performanceMonitoringEnabled_ = false;
            this.bitField0_ &= -2;
            return this;
        }

        public final DeveloperPrefs clearSensorLoggingEnabled() {
            this.sensorLoggingEnabled_ = false;
            this.bitField0_ &= -3;
            return this;
        }

        public final DeveloperPrefs clone() {
            try {
                return (DeveloperPrefs) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, this.performanceMonitoringEnabled_);
            }
            if ((this.bitField0_ & 2) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.sensorLoggingEnabled_);
            }
            return (this.bitField0_ & 4) == 0 ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(3, this.motophoPatchEnabled_);
        }

        public final boolean getMotophoPatchEnabled() {
            return this.motophoPatchEnabled_;
        }

        public final boolean getPerformanceMonitoringEnabled() {
            return this.performanceMonitoringEnabled_;
        }

        public final boolean getSensorLoggingEnabled() {
            return this.sensorLoggingEnabled_;
        }

        public final boolean hasMotophoPatchEnabled() {
            return (this.bitField0_ & 4) != 0;
        }

        public final boolean hasPerformanceMonitoringEnabled() {
            return (this.bitField0_ & 1) != 0;
        }

        public final boolean hasSensorLoggingEnabled() {
            return (this.bitField0_ & 2) != 0;
        }

        public final DeveloperPrefs mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        this.performanceMonitoringEnabled_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 1;
                        break;
                    case 16:
                        this.sensorLoggingEnabled_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 2;
                        break;
                    case 24:
                        this.motophoPatchEnabled_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 4;
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

        public final DeveloperPrefs setMotophoPatchEnabled(boolean z) {
            this.motophoPatchEnabled_ = z;
            this.bitField0_ |= 4;
            return this;
        }

        public final DeveloperPrefs setPerformanceMonitoringEnabled(boolean z) {
            this.performanceMonitoringEnabled_ = z;
            this.bitField0_ |= 1;
            return this;
        }

        public final DeveloperPrefs setSensorLoggingEnabled(boolean z) {
            this.sensorLoggingEnabled_ = z;
            this.bitField0_ |= 2;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeBool(1, this.performanceMonitoringEnabled_);
            }
            if ((this.bitField0_ & 2) != 0) {
                codedOutputByteBufferNano.writeBool(2, this.sensorLoggingEnabled_);
            }
            if ((this.bitField0_ & 4) != 0) {
                codedOutputByteBufferNano.writeBool(3, this.motophoPatchEnabled_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class UserPrefs extends ExtendableMessageNano<UserPrefs> implements Cloneable {
        private static volatile UserPrefs[] _emptyArray;
        private int bitField0_;
        private int controllerHandedness_;
        public DeveloperPrefs developerPrefs;

        public interface Handedness {
            public static final int LEFT_HANDED = 1;
            public static final int RIGHT_HANDED = 0;
        }

        public UserPrefs() {
            clear();
        }

        public static UserPrefs[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new UserPrefs[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static UserPrefs parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new UserPrefs().mergeFrom(codedInputByteBufferNano);
        }

        public static UserPrefs parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (UserPrefs) MessageNano.mergeFrom(new UserPrefs(), bArr);
        }

        public final UserPrefs clear() {
            this.bitField0_ = 0;
            this.controllerHandedness_ = 0;
            this.developerPrefs = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final UserPrefs clearControllerHandedness() {
            this.controllerHandedness_ = 0;
            this.bitField0_ &= -2;
            return this;
        }

        public final UserPrefs clone() {
            try {
                UserPrefs userPrefs = (UserPrefs) super.clone();
                if (this.developerPrefs != null) {
                    userPrefs.developerPrefs = this.developerPrefs.clone();
                }
                return userPrefs;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.controllerHandedness_);
            }
            return this.developerPrefs == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, this.developerPrefs);
        }

        public final int getControllerHandedness() {
            return this.controllerHandedness_;
        }

        public final boolean hasControllerHandedness() {
            return (this.bitField0_ & 1) != 0;
        }

        public final UserPrefs mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        switch (readInt32) {
                            case 0:
                            case 1:
                                this.controllerHandedness_ = readInt32;
                                this.bitField0_ |= 1;
                                break;
                        }
                    case 18:
                        if (this.developerPrefs == null) {
                            this.developerPrefs = new DeveloperPrefs();
                        }
                        codedInputByteBufferNano.readMessage(this.developerPrefs);
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

        public final UserPrefs setControllerHandedness(int i) {
            this.controllerHandedness_ = i;
            this.bitField0_ |= 1;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.controllerHandedness_);
            }
            if (this.developerPrefs != null) {
                codedOutputByteBufferNano.writeMessage(2, this.developerPrefs);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
