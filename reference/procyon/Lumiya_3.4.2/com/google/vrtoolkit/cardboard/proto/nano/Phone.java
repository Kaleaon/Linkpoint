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

public interface Phone
{
    public static final class PhoneParams extends ExtendableMessageNano<PhoneParams> implements Cloneable
    {
        private static volatile PhoneParams[] _emptyArray;
        private int bitField0_;
        private float bottomBezelHeight_;
        public float[] dEPRECATEDGyroBias;
        private float xPpi_;
        private float yPpi_;
        
        public PhoneParams() {
            this.clear();
        }
        
        public static PhoneParams[] emptyArray() {
            if (PhoneParams._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (PhoneParams._emptyArray != null) {
                                break;
                            }
                        }
                        PhoneParams._emptyArray = new PhoneParams[0];
                        continue;
                    }
                }
            }
            return PhoneParams._emptyArray;
        }
        
        public static PhoneParams parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new PhoneParams().mergeFrom(codedInputByteBufferNano);
        }
        
        public static PhoneParams parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new PhoneParams(), array);
        }
        
        public final PhoneParams clear() {
            this.bitField0_ = 0;
            this.xPpi_ = 0.0f;
            this.yPpi_ = 0.0f;
            this.bottomBezelHeight_ = 0.0f;
            this.dEPRECATEDGyroBias = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }
        
        public final PhoneParams clearBottomBezelHeight() {
            this.bottomBezelHeight_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFB;
            return this;
        }
        
        public final PhoneParams clearXPpi() {
            this.xPpi_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        public final PhoneParams clearYPpi() {
            this.yPpi_ = 0.0f;
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }
        
        @Override
        public final PhoneParams clone() {
            while (true) {
                PhoneParams phoneParams;
                try {
                    phoneParams = super.clone();
                    if (this.dEPRECATEDGyroBias == null) {
                        return phoneParams;
                    }
                }
                catch (final CloneNotSupportedException detailMessage) {
                    throw new AssertionError((Object)detailMessage);
                }
                if (this.dEPRECATEDGyroBias.length > 0) {
                    phoneParams.dEPRECATEDGyroBias = this.dEPRECATEDGyroBias.clone();
                    return phoneParams;
                }
                return phoneParams;
            }
        }
        
        @Override
        protected final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if ((this.bitField0_ & 0x1) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.xPpi_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.yPpi_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.bottomBezelHeight_);
            }
            int n;
            if (this.dEPRECATEDGyroBias == null) {
                n = computeSerializedSize;
            }
            else {
                n = computeSerializedSize;
                if (this.dEPRECATEDGyroBias.length > 0) {
                    final int n2 = this.dEPRECATEDGyroBias.length * 4;
                    n = computeSerializedSize + n2 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n2);
                }
            }
            return n;
        }
        
        public final float getBottomBezelHeight() {
            return this.bottomBezelHeight_;
        }
        
        public final float getXPpi() {
            return this.xPpi_;
        }
        
        public final float getYPpi() {
            return this.yPpi_;
        }
        
        public final boolean hasBottomBezelHeight() {
            return (this.bitField0_ & 0x4) != 0x0;
        }
        
        public final boolean hasXPpi() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        public final boolean hasYPpi() {
            return (this.bitField0_ & 0x2) != 0x0;
        }
        
        @Override
        public final PhoneParams mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                        this.xPpi_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                    case 21: {
                        this.yPpi_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x2;
                        continue;
                    }
                    case 29: {
                        this.bottomBezelHeight_ = codedInputByteBufferNano.readFloat();
                        this.bitField0_ |= 0x4;
                        continue;
                    }
                    case 37: {
                        final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 37);
                        int i;
                        if (this.dEPRECATEDGyroBias != null) {
                            i = this.dEPRECATEDGyroBias.length;
                        }
                        else {
                            i = 0;
                        }
                        final float[] deprecatedGyroBias = new float[repeatedFieldArrayLength + i];
                        if (i != 0) {
                            System.arraycopy(this.dEPRECATEDGyroBias, 0, deprecatedGyroBias, 0, i);
                        }
                        while (i < deprecatedGyroBias.length - 1) {
                            deprecatedGyroBias[i] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            ++i;
                        }
                        deprecatedGyroBias[i] = codedInputByteBufferNano.readFloat();
                        this.dEPRECATEDGyroBias = deprecatedGyroBias;
                        continue;
                    }
                    case 34: {
                        final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                        final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                        final int n = rawVarint32 / 4;
                        int j;
                        if (this.dEPRECATEDGyroBias != null) {
                            j = this.dEPRECATEDGyroBias.length;
                        }
                        else {
                            j = 0;
                        }
                        final float[] deprecatedGyroBias2 = new float[n + j];
                        if (j != 0) {
                            System.arraycopy(this.dEPRECATEDGyroBias, 0, deprecatedGyroBias2, 0, j);
                        }
                        while (j < deprecatedGyroBias2.length) {
                            deprecatedGyroBias2[j] = codedInputByteBufferNano.readFloat();
                            ++j;
                        }
                        this.dEPRECATEDGyroBias = deprecatedGyroBias2;
                        codedInputByteBufferNano.popLimit(pushLimit);
                        continue;
                    }
                }
            }
        }
        
        public final PhoneParams setBottomBezelHeight(final float bottomBezelHeight_) {
            this.bottomBezelHeight_ = bottomBezelHeight_;
            this.bitField0_ |= 0x4;
            return this;
        }
        
        public final PhoneParams setXPpi(final float xPpi_) {
            this.xPpi_ = xPpi_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        public final PhoneParams setYPpi(final float yPpi_) {
            this.yPpi_ = yPpi_;
            this.bitField0_ |= 0x2;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = 0;
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeFloat(1, this.xPpi_);
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                codedOutputByteBufferNano.writeFloat(2, this.yPpi_);
            }
            if ((this.bitField0_ & 0x4) != 0x0) {
                codedOutputByteBufferNano.writeFloat(3, this.bottomBezelHeight_);
            }
            if (this.dEPRECATEDGyroBias != null && this.dEPRECATEDGyroBias.length > 0) {
                final int length = this.dEPRECATEDGyroBias.length;
                codedOutputByteBufferNano.writeRawVarint32(34);
                codedOutputByteBufferNano.writeRawVarint32(length * 4);
                while (i < this.dEPRECATEDGyroBias.length) {
                    codedOutputByteBufferNano.writeFloatNoTag(this.dEPRECATEDGyroBias[i]);
                    ++i;
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
