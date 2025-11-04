// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.proto.nano;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.ExtendableMessageNano;

public interface Nfc
{
    public static final class NfcParams extends ExtendableMessageNano<NfcParams> implements Cloneable
    {
        private static volatile NfcParams[] _emptyArray;
        private int bitField0_;
        private int viewerId_;
        
        public NfcParams() {
            this.clear();
        }
        
        public static NfcParams[] emptyArray() {
            if (NfcParams._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (NfcParams._emptyArray != null) {
                                break;
                            }
                        }
                        NfcParams._emptyArray = new NfcParams[0];
                        continue;
                    }
                }
            }
            return NfcParams._emptyArray;
        }
        
        public static NfcParams parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new NfcParams().mergeFrom(codedInputByteBufferNano);
        }
        
        public static NfcParams parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new NfcParams(), array);
        }
        
        public final NfcParams clear() {
            this.bitField0_ = 0;
            this.viewerId_ = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }
        
        public final NfcParams clearViewerId() {
            this.viewerId_ = 0;
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        @Override
        public final NfcParams clone() {
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
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.viewerId_);
            }
            return computeSerializedSize;
        }
        
        public final int getViewerId() {
            return this.viewerId_;
        }
        
        public final boolean hasViewerId() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        @Override
        public final NfcParams mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                        this.viewerId_ = codedInputByteBufferNano.readInt32();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                }
            }
        }
        
        public final NfcParams setViewerId(final int viewerId_) {
            this.viewerId_ = viewerId_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeInt32(1, this.viewerId_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
