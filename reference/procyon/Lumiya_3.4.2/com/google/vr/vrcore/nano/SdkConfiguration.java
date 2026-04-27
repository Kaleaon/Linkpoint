// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.nano;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.common.logging.nano.Vr;
import com.google.protobuf.nano.ExtendableMessageNano;

public interface SdkConfiguration
{
    public static final class SdkConfigurationRequest extends ExtendableMessageNano<SdkConfigurationRequest>
    {
        private static volatile SdkConfigurationRequest[] _emptyArray;
        public Vr.VREvent.SdkConfigurationParams requestedParams;
        public String sdkVersion;
        
        public SdkConfigurationRequest() {
            this.clear();
        }
        
        public static SdkConfigurationRequest[] emptyArray() {
            if (SdkConfigurationRequest._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (SdkConfigurationRequest._emptyArray != null) {
                                break;
                            }
                        }
                        SdkConfigurationRequest._emptyArray = new SdkConfigurationRequest[0];
                        continue;
                    }
                }
            }
            return SdkConfigurationRequest._emptyArray;
        }
        
        public static SdkConfigurationRequest parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new SdkConfigurationRequest().mergeFrom(codedInputByteBufferNano);
        }
        
        public static SdkConfigurationRequest parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new SdkConfigurationRequest(), array);
        }
        
        public final SdkConfigurationRequest clear() {
            this.sdkVersion = null;
            this.requestedParams = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }
        
        @Override
        protected final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.sdkVersion != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.sdkVersion);
            }
            if (this.requestedParams != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.requestedParams);
            }
            return computeSerializedSize;
        }
        
        @Override
        public final SdkConfigurationRequest mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                        this.sdkVersion = codedInputByteBufferNano.readString();
                        continue;
                    }
                    case 18: {
                        if (this.requestedParams == null) {
                            this.requestedParams = new Vr.VREvent.SdkConfigurationParams();
                        }
                        codedInputByteBufferNano.readMessage(this.requestedParams);
                        continue;
                    }
                }
            }
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.sdkVersion != null) {
                codedOutputByteBufferNano.writeString(1, this.sdkVersion);
            }
            if (this.requestedParams != null) {
                codedOutputByteBufferNano.writeMessage(2, this.requestedParams);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
