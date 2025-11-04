// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vrtoolkit.cardboard.proto.nano;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.ExtendableMessageNano;

public interface Preferences
{
    public static final class DeveloperPrefs extends ExtendableMessageNano<DeveloperPrefs> implements Cloneable
    {
        private static volatile DeveloperPrefs[] _emptyArray;
        private int bitField0_;
        private boolean motophoPatchEnabled_;
        private boolean performanceMonitoringEnabled_;
        private boolean sensorLoggingEnabled_;
        
        public DeveloperPrefs() {
            this.clear();
        }
        
        public static DeveloperPrefs[] emptyArray() {
            if (DeveloperPrefs._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (DeveloperPrefs._emptyArray != null) {
                                break;
                            }
                        }
                        DeveloperPrefs._emptyArray = new DeveloperPrefs[0];
                        continue;
                    }
                }
            }
            return DeveloperPrefs._emptyArray;
        }
        
        public static DeveloperPrefs parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DeveloperPrefs().mergeFrom(codedInputByteBufferNano);
        }
        
        public static DeveloperPrefs parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new DeveloperPrefs(), array);
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
            this.bitField0_ &= 0xFFFFFFFB;
            return this;
        }
        
        public final DeveloperPrefs clearPerformanceMonitoringEnabled() {
            this.performanceMonitoringEnabled_ = false;
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        public final DeveloperPrefs clearSensorLoggingEnabled() {
            this.sensorLoggingEnabled_ = false;
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }
        
        @Override
        public final DeveloperPrefs clone() {
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
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, this.performanceMonitoringEnabled_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.sensorLoggingEnabled_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.motophoPatchEnabled_);
            }
            return computeSerializedSize;
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
            return (this.bitField0_ & 0x4) != 0x0;
        }
        
        public final boolean hasPerformanceMonitoringEnabled() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        public final boolean hasSensorLoggingEnabled() {
            return (this.bitField0_ & 0x2) != 0x0;
        }
        
        @Override
        public final DeveloperPrefs mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                        this.performanceMonitoringEnabled_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                    case 16: {
                        this.sensorLoggingEnabled_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 0x2;
                        continue;
                    }
                    case 24: {
                        this.motophoPatchEnabled_ = codedInputByteBufferNano.readBool();
                        this.bitField0_ |= 0x4;
                        continue;
                    }
                }
            }
        }
        
        public final DeveloperPrefs setMotophoPatchEnabled(final boolean motophoPatchEnabled_) {
            this.motophoPatchEnabled_ = motophoPatchEnabled_;
            this.bitField0_ |= 0x4;
            return this;
        }
        
        public final DeveloperPrefs setPerformanceMonitoringEnabled(final boolean performanceMonitoringEnabled_) {
            this.performanceMonitoringEnabled_ = performanceMonitoringEnabled_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        public final DeveloperPrefs setSensorLoggingEnabled(final boolean sensorLoggingEnabled_) {
            this.sensorLoggingEnabled_ = sensorLoggingEnabled_;
            this.bitField0_ |= 0x2;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeBool(1, this.performanceMonitoringEnabled_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                codedOutputByteBufferNano.writeBool(2, this.sensorLoggingEnabled_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                codedOutputByteBufferNano.writeBool(3, this.motophoPatchEnabled_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
    
    public static final class UserPrefs extends ExtendableMessageNano<UserPrefs> implements Cloneable
    {
        private static volatile UserPrefs[] _emptyArray;
        private int bitField0_;
        private int controllerHandedness_;
        public DeveloperPrefs developerPrefs;
        
        public UserPrefs() {
            this.clear();
        }
        
        public static UserPrefs[] emptyArray() {
            if (UserPrefs._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (UserPrefs._emptyArray != null) {
                                break;
                            }
                        }
                        UserPrefs._emptyArray = new UserPrefs[0];
                        continue;
                    }
                }
            }
            return UserPrefs._emptyArray;
        }
        
        public static UserPrefs parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new UserPrefs().mergeFrom(codedInputByteBufferNano);
        }
        
        public static UserPrefs parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new UserPrefs(), array);
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
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        @Override
        public final UserPrefs clone() {
            while (true) {
                UserPrefs userPrefs;
                try {
                    userPrefs = super.clone();
                    if (this.developerPrefs == null) {
                        return userPrefs;
                    }
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
                userPrefs.developerPrefs = this.developerPrefs.clone();
                return userPrefs;
            }
        }
        
        @Override
        protected final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 0x1) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.controllerHandedness_);
            }
            if (this.developerPrefs != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.developerPrefs);
            }
            return computeSerializedSize;
        }
        
        public final int getControllerHandedness() {
            return this.controllerHandedness_;
        }
        
        public final boolean hasControllerHandedness() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        @Override
        public final UserPrefs mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                        final int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            default: {
                                continue;
                            }
                            case 0:
                            case 1: {
                                this.controllerHandedness_ = int32;
                                this.bitField0_ |= 0x1;
                                continue;
                            }
                        }
                        break;
                    }
                    case 18: {
                        if (this.developerPrefs == null) {
                            this.developerPrefs = new DeveloperPrefs();
                        }
                        codedInputByteBufferNano.readMessage(this.developerPrefs);
                        continue;
                    }
                }
            }
        }
        
        public final UserPrefs setControllerHandedness(final int controllerHandedness_) {
            this.controllerHandedness_ = controllerHandedness_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeInt32(1, this.controllerHandedness_);
            }
            if (this.developerPrefs != null) {
                codedOutputByteBufferNano.writeMessage(2, this.developerPrefs);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
        
        public interface Handedness
        {
            public static final int LEFT_HANDED = 1;
            public static final int RIGHT_HANDED = 0;
        }
    }
}
