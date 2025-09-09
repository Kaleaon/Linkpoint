package com.google.common.logging.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.ExtendableMessageNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface Vr {

    public static final class VREvent extends ExtendableMessageNano<VREvent> implements Cloneable {
        private static volatile VREvent[] _emptyArray;
        public Application application;
        public AudioStats audioStats;
        public String cohort;
        public Cyclops cyclops;
        public Long durationMs;
        public EarthVr earthVr;
        public EmbedVrWidget embedVrWidget;
        public HeadMount headMount;
        public Application[] installedVrApplications;
        public Keyboard keyboard;
        public Launcher launcher;
        public Integer lifetimeCountBucket;
        public Lullaby lullaby;
        public PerformanceStats performanceStats;
        public Photos photos;
        public QrCodeScan qrCodeScan;
        public Renderer renderer;
        public SdkConfigurationParams sdkConfiguration;
        public SensorStats sensorStats;
        public StreetView streetView;
        public VrCore vrCore;
        public VrHome vrHome;

        public static final class Application extends ExtendableMessageNano<Application> implements Cloneable {
            private static volatile Application[] _emptyArray;
            public String name;
            public String packageName;
            public String version;

            public Application() {
                clear();
            }

            public static Application[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Application[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Application parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Application().mergeFrom(codedInputByteBufferNano);
            }

            public static Application parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Application) MessageNano.mergeFrom(new Application(), bArr);
            }

            public final Application clear() {
                this.packageName = null;
                this.name = null;
                this.version = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Application clone() {
                try {
                    return (Application) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.packageName != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.packageName);
                }
                if (this.name != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.name);
                }
                return this.version == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(3, this.version);
            }

            public final Application mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            this.packageName = codedInputByteBufferNano.readString();
                            break;
                        case 18:
                            this.name = codedInputByteBufferNano.readString();
                            break;
                        case 26:
                            this.version = codedInputByteBufferNano.readString();
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.packageName != null) {
                    codedOutputByteBufferNano.writeString(1, this.packageName);
                }
                if (this.name != null) {
                    codedOutputByteBufferNano.writeString(2, this.name);
                }
                if (this.version != null) {
                    codedOutputByteBufferNano.writeString(3, this.version);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class AudioStats extends ExtendableMessageNano<AudioStats> implements Cloneable {
            private static volatile AudioStats[] _emptyArray;
            public HistogramBucket[] cpuMeasurementsPercent;
            public Integer framesPerBuffer;
            public HistogramBucket[] numberOfSimultaneousSoundFields;
            public HistogramBucket[] numberOfSimultaneousSoundObjects;
            public Integer renderingMode;
            public HistogramBucket[] renderingTimePerBufferMilliseconds;
            public Integer sampleRate;

            public interface RenderingMode {
                public static final int BINAURAL_HIGH_QUALITY = 3;
                public static final int BINAURAL_LOW_QUALITY = 2;
                public static final int STEREO_PANNING = 1;
                public static final int UNKNOWN = 0;
            }

            public AudioStats() {
                clear();
            }

            public static AudioStats[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new AudioStats[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static AudioStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new AudioStats().mergeFrom(codedInputByteBufferNano);
            }

            public static AudioStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (AudioStats) MessageNano.mergeFrom(new AudioStats(), bArr);
            }

            public final AudioStats clear() {
                this.sampleRate = null;
                this.framesPerBuffer = null;
                this.renderingTimePerBufferMilliseconds = HistogramBucket.emptyArray();
                this.numberOfSimultaneousSoundObjects = HistogramBucket.emptyArray();
                this.numberOfSimultaneousSoundFields = HistogramBucket.emptyArray();
                this.cpuMeasurementsPercent = HistogramBucket.emptyArray();
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final AudioStats clone() {
                try {
                    AudioStats audioStats = (AudioStats) super.clone();
                    if (this.renderingTimePerBufferMilliseconds != null && this.renderingTimePerBufferMilliseconds.length > 0) {
                        audioStats.renderingTimePerBufferMilliseconds = new HistogramBucket[this.renderingTimePerBufferMilliseconds.length];
                        for (int i = 0; i < this.renderingTimePerBufferMilliseconds.length; i++) {
                            if (this.renderingTimePerBufferMilliseconds[i] != null) {
                                audioStats.renderingTimePerBufferMilliseconds[i] = this.renderingTimePerBufferMilliseconds[i].clone();
                            }
                        }
                    }
                    if (this.numberOfSimultaneousSoundObjects != null && this.numberOfSimultaneousSoundObjects.length > 0) {
                        audioStats.numberOfSimultaneousSoundObjects = new HistogramBucket[this.numberOfSimultaneousSoundObjects.length];
                        for (int i2 = 0; i2 < this.numberOfSimultaneousSoundObjects.length; i2++) {
                            if (this.numberOfSimultaneousSoundObjects[i2] != null) {
                                audioStats.numberOfSimultaneousSoundObjects[i2] = this.numberOfSimultaneousSoundObjects[i2].clone();
                            }
                        }
                    }
                    if (this.numberOfSimultaneousSoundFields != null && this.numberOfSimultaneousSoundFields.length > 0) {
                        audioStats.numberOfSimultaneousSoundFields = new HistogramBucket[this.numberOfSimultaneousSoundFields.length];
                        for (int i3 = 0; i3 < this.numberOfSimultaneousSoundFields.length; i3++) {
                            if (this.numberOfSimultaneousSoundFields[i3] != null) {
                                audioStats.numberOfSimultaneousSoundFields[i3] = this.numberOfSimultaneousSoundFields[i3].clone();
                            }
                        }
                    }
                    if (this.cpuMeasurementsPercent != null && this.cpuMeasurementsPercent.length > 0) {
                        audioStats.cpuMeasurementsPercent = new HistogramBucket[this.cpuMeasurementsPercent.length];
                        for (int i4 = 0; i4 < this.cpuMeasurementsPercent.length; i4++) {
                            if (this.cpuMeasurementsPercent[i4] != null) {
                                audioStats.cpuMeasurementsPercent[i4] = this.cpuMeasurementsPercent[i4].clone();
                            }
                        }
                    }
                    return audioStats;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.renderingMode != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.renderingMode.intValue());
                }
                if (this.sampleRate != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.sampleRate.intValue());
                }
                if (this.framesPerBuffer != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.framesPerBuffer.intValue());
                }
                if (this.renderingTimePerBufferMilliseconds != null && this.renderingTimePerBufferMilliseconds.length > 0) {
                    int i = computeSerializedSize;
                    for (HistogramBucket histogramBucket : this.renderingTimePerBufferMilliseconds) {
                        if (histogramBucket != null) {
                            i += CodedOutputByteBufferNano.computeMessageSize(4, histogramBucket);
                        }
                    }
                    computeSerializedSize = i;
                }
                if (this.numberOfSimultaneousSoundObjects != null && this.numberOfSimultaneousSoundObjects.length > 0) {
                    int i2 = computeSerializedSize;
                    for (HistogramBucket histogramBucket2 : this.numberOfSimultaneousSoundObjects) {
                        if (histogramBucket2 != null) {
                            i2 += CodedOutputByteBufferNano.computeMessageSize(5, histogramBucket2);
                        }
                    }
                    computeSerializedSize = i2;
                }
                if (this.numberOfSimultaneousSoundFields != null && this.numberOfSimultaneousSoundFields.length > 0) {
                    int i3 = computeSerializedSize;
                    for (HistogramBucket histogramBucket3 : this.numberOfSimultaneousSoundFields) {
                        if (histogramBucket3 != null) {
                            i3 += CodedOutputByteBufferNano.computeMessageSize(6, histogramBucket3);
                        }
                    }
                    computeSerializedSize = i3;
                }
                if (this.cpuMeasurementsPercent != null && this.cpuMeasurementsPercent.length > 0) {
                    for (HistogramBucket histogramBucket4 : this.cpuMeasurementsPercent) {
                        if (histogramBucket4 != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, histogramBucket4);
                        }
                    }
                }
                return computeSerializedSize;
            }

            public final AudioStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                case 2:
                                case 3:
                                    this.renderingMode = Integer.valueOf(readInt32);
                                    break;
                            }
                        case 16:
                            this.sampleRate = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 24:
                            this.framesPerBuffer = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 34:
                            int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                            int length = this.renderingTimePerBufferMilliseconds != null ? this.renderingTimePerBufferMilliseconds.length : 0;
                            HistogramBucket[] histogramBucketArr = new HistogramBucket[(repeatedFieldArrayLength + length)];
                            if (length != 0) {
                                System.arraycopy(this.renderingTimePerBufferMilliseconds, 0, histogramBucketArr, 0, length);
                            }
                            while (length < histogramBucketArr.length - 1) {
                                histogramBucketArr[length] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr[length]);
                                codedInputByteBufferNano.readTag();
                                length++;
                            }
                            histogramBucketArr[length] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr[length]);
                            this.renderingTimePerBufferMilliseconds = histogramBucketArr;
                            break;
                        case 42:
                            int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                            int length2 = this.numberOfSimultaneousSoundObjects != null ? this.numberOfSimultaneousSoundObjects.length : 0;
                            HistogramBucket[] histogramBucketArr2 = new HistogramBucket[(repeatedFieldArrayLength2 + length2)];
                            if (length2 != 0) {
                                System.arraycopy(this.numberOfSimultaneousSoundObjects, 0, histogramBucketArr2, 0, length2);
                            }
                            while (length2 < histogramBucketArr2.length - 1) {
                                histogramBucketArr2[length2] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr2[length2]);
                                codedInputByteBufferNano.readTag();
                                length2++;
                            }
                            histogramBucketArr2[length2] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr2[length2]);
                            this.numberOfSimultaneousSoundObjects = histogramBucketArr2;
                            break;
                        case 50:
                            int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                            int length3 = this.numberOfSimultaneousSoundFields != null ? this.numberOfSimultaneousSoundFields.length : 0;
                            HistogramBucket[] histogramBucketArr3 = new HistogramBucket[(repeatedFieldArrayLength3 + length3)];
                            if (length3 != 0) {
                                System.arraycopy(this.numberOfSimultaneousSoundFields, 0, histogramBucketArr3, 0, length3);
                            }
                            while (length3 < histogramBucketArr3.length - 1) {
                                histogramBucketArr3[length3] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr3[length3]);
                                codedInputByteBufferNano.readTag();
                                length3++;
                            }
                            histogramBucketArr3[length3] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr3[length3]);
                            this.numberOfSimultaneousSoundFields = histogramBucketArr3;
                            break;
                        case 58:
                            int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 58);
                            int length4 = this.cpuMeasurementsPercent != null ? this.cpuMeasurementsPercent.length : 0;
                            HistogramBucket[] histogramBucketArr4 = new HistogramBucket[(repeatedFieldArrayLength4 + length4)];
                            if (length4 != 0) {
                                System.arraycopy(this.cpuMeasurementsPercent, 0, histogramBucketArr4, 0, length4);
                            }
                            while (length4 < histogramBucketArr4.length - 1) {
                                histogramBucketArr4[length4] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr4[length4]);
                                codedInputByteBufferNano.readTag();
                                length4++;
                            }
                            histogramBucketArr4[length4] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr4[length4]);
                            this.cpuMeasurementsPercent = histogramBucketArr4;
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.renderingMode != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.renderingMode.intValue());
                }
                if (this.sampleRate != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.sampleRate.intValue());
                }
                if (this.framesPerBuffer != null) {
                    codedOutputByteBufferNano.writeInt32(3, this.framesPerBuffer.intValue());
                }
                if (this.renderingTimePerBufferMilliseconds != null && this.renderingTimePerBufferMilliseconds.length > 0) {
                    for (HistogramBucket histogramBucket : this.renderingTimePerBufferMilliseconds) {
                        if (histogramBucket != null) {
                            codedOutputByteBufferNano.writeMessage(4, histogramBucket);
                        }
                    }
                }
                if (this.numberOfSimultaneousSoundObjects != null && this.numberOfSimultaneousSoundObjects.length > 0) {
                    for (HistogramBucket histogramBucket2 : this.numberOfSimultaneousSoundObjects) {
                        if (histogramBucket2 != null) {
                            codedOutputByteBufferNano.writeMessage(5, histogramBucket2);
                        }
                    }
                }
                if (this.numberOfSimultaneousSoundFields != null && this.numberOfSimultaneousSoundFields.length > 0) {
                    for (HistogramBucket histogramBucket3 : this.numberOfSimultaneousSoundFields) {
                        if (histogramBucket3 != null) {
                            codedOutputByteBufferNano.writeMessage(6, histogramBucket3);
                        }
                    }
                }
                if (this.cpuMeasurementsPercent != null && this.cpuMeasurementsPercent.length > 0) {
                    for (HistogramBucket histogramBucket4 : this.cpuMeasurementsPercent) {
                        if (histogramBucket4 != null) {
                            codedOutputByteBufferNano.writeMessage(7, histogramBucket4);
                        }
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public interface Bucket {
            public static final int ELEVEN_TO_TWENTY = 11;
            public static final int FIVE = 5;
            public static final int FOUR = 4;
            public static final int ONE = 1;
            public static final int SIX_TO_TEN = 6;
            public static final int THREE = 3;
            public static final int TWENTYONE_PLUS = 21;
            public static final int TWO = 2;
            public static final int UNKNOWN_BUCKET = 0;
        }

        public static final class Cyclops extends ExtendableMessageNano<Cyclops> implements Cloneable {
            private static volatile Cyclops[] _emptyArray;
            public Capture capture;
            public Share share;
            public ShareStart shareStart;
            public View view;

            public static final class Capture extends ExtendableMessageNano<Capture> implements Cloneable {
                private static volatile Capture[] _emptyArray;
                public Float angleDegrees;
                public Long captureTimeMs;
                public Boolean captureWarnings;
                public Long compositionTimeMs;
                public Integer outcome;
                public Long processingTimeMs;
                public Boolean withSound;

                public interface Outcome {
                    public static final int FAILURE_CAPTURE = 2;
                    public static final int FAILURE_PROCESSING = 3;
                    public static final int SUCCESS = 1;
                    public static final int UNKNOWN = 0;
                    public static final int USER_CANCELLED = 4;
                }

                public Capture() {
                    clear();
                }

                public static Capture[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Capture[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Capture parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Capture().mergeFrom(codedInputByteBufferNano);
                }

                public static Capture parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Capture) MessageNano.mergeFrom(new Capture(), bArr);
                }

                public final Capture clear() {
                    this.angleDegrees = null;
                    this.withSound = null;
                    this.captureWarnings = null;
                    this.compositionTimeMs = null;
                    this.captureTimeMs = null;
                    this.processingTimeMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Capture clone() {
                    try {
                        return (Capture) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.outcome != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.outcome.intValue());
                    }
                    if (this.angleDegrees != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.angleDegrees.floatValue());
                    }
                    if (this.withSound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.withSound.booleanValue());
                    }
                    if (this.captureWarnings != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, this.captureWarnings.booleanValue());
                    }
                    if (this.compositionTimeMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(5, this.compositionTimeMs.longValue());
                    }
                    if (this.captureTimeMs != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(6, this.captureTimeMs.longValue());
                    }
                    return this.processingTimeMs == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(7, this.processingTimeMs.longValue());
                }

                public final Capture mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                    case 3:
                                    case 4:
                                        this.outcome = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 21:
                                this.angleDegrees = Float.valueOf(codedInputByteBufferNano.readFloat());
                                break;
                            case 24:
                                this.withSound = Boolean.valueOf(codedInputByteBufferNano.readBool());
                                break;
                            case 32:
                                this.captureWarnings = Boolean.valueOf(codedInputByteBufferNano.readBool());
                                break;
                            case 40:
                                this.compositionTimeMs = Long.valueOf(codedInputByteBufferNano.readInt64());
                                break;
                            case 48:
                                this.captureTimeMs = Long.valueOf(codedInputByteBufferNano.readInt64());
                                break;
                            case 56:
                                this.processingTimeMs = Long.valueOf(codedInputByteBufferNano.readInt64());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.outcome != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.outcome.intValue());
                    }
                    if (this.angleDegrees != null) {
                        codedOutputByteBufferNano.writeFloat(2, this.angleDegrees.floatValue());
                    }
                    if (this.withSound != null) {
                        codedOutputByteBufferNano.writeBool(3, this.withSound.booleanValue());
                    }
                    if (this.captureWarnings != null) {
                        codedOutputByteBufferNano.writeBool(4, this.captureWarnings.booleanValue());
                    }
                    if (this.compositionTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(5, this.compositionTimeMs.longValue());
                    }
                    if (this.captureTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(6, this.captureTimeMs.longValue());
                    }
                    if (this.processingTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(7, this.processingTimeMs.longValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class Share extends ExtendableMessageNano<Share> implements Cloneable {
                private static volatile Share[] _emptyArray;
                public Integer numPhotos;
                public Integer type;
                public Boolean withSound;

                public interface Type {
                    public static final int CANCELLED = 1;
                    public static final int COPY_LINK = 2;
                    public static final int EMAIL = 7;
                    public static final int SOCIAL_FACEBOOK = 4;
                    public static final int SOCIAL_GPLUS = 6;
                    public static final int SOCIAL_OTHER = 3;
                    public static final int SOCIAL_TWITTER = 5;
                    public static final int UNKNOWN = 0;
                }

                public Share() {
                    clear();
                }

                public static Share[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Share[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Share parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Share().mergeFrom(codedInputByteBufferNano);
                }

                public static Share parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Share) MessageNano.mergeFrom(new Share(), bArr);
                }

                public final Share clear() {
                    this.withSound = null;
                    this.numPhotos = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Share clone() {
                    try {
                        return (Share) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.type != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type.intValue());
                    }
                    if (this.withSound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.withSound.booleanValue());
                    }
                    return this.numPhotos == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, this.numPhotos.intValue());
                }

                public final Share mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                        this.type = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                this.withSound = Boolean.valueOf(codedInputByteBufferNano.readBool());
                                break;
                            case 24:
                                this.numPhotos = Integer.valueOf(codedInputByteBufferNano.readInt32());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.type != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.type.intValue());
                    }
                    if (this.withSound != null) {
                        codedOutputByteBufferNano.writeBool(2, this.withSound.booleanValue());
                    }
                    if (this.numPhotos != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.numPhotos.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class ShareStart extends ExtendableMessageNano<ShareStart> implements Cloneable {
                private static volatile ShareStart[] _emptyArray;
                public Integer numPhotos;
                public Integer originScreen;

                public interface OriginScreen {
                    public static final int GALLERY = 1;
                    public static final int ONE_UP_VIEW = 2;
                    public static final int UNKNOWN = 0;
                }

                public ShareStart() {
                    clear();
                }

                public static ShareStart[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new ShareStart[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static ShareStart parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new ShareStart().mergeFrom(codedInputByteBufferNano);
                }

                public static ShareStart parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (ShareStart) MessageNano.mergeFrom(new ShareStart(), bArr);
                }

                public final ShareStart clear() {
                    this.numPhotos = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final ShareStart clone() {
                    try {
                        return (ShareStart) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.originScreen != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.originScreen.intValue());
                    }
                    return this.numPhotos == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, this.numPhotos.intValue());
                }

                public final ShareStart mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                        this.originScreen = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                this.numPhotos = Integer.valueOf(codedInputByteBufferNano.readInt32());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.originScreen != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.originScreen.intValue());
                    }
                    if (this.numPhotos != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.numPhotos.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class View extends ExtendableMessageNano<View> implements Cloneable {
                private static volatile View[] _emptyArray;
                public Boolean interaction;
                public Integer numPanos;
                public Integer orientation;
                public Boolean withSound;

                public interface Orientation {
                    public static final int LANDSCAPE = 1;
                    public static final int PORTRAIT = 2;
                    public static final int UNKNOWN = 0;
                }

                public View() {
                    clear();
                }

                public static View[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new View[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static View parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new View().mergeFrom(codedInputByteBufferNano);
                }

                public static View parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (View) MessageNano.mergeFrom(new View(), bArr);
                }

                public final View clear() {
                    this.interaction = null;
                    this.withSound = null;
                    this.numPanos = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final View clone() {
                    try {
                        return (View) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.orientation != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.orientation.intValue());
                    }
                    if (this.interaction != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.interaction.booleanValue());
                    }
                    if (this.withSound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.withSound.booleanValue());
                    }
                    return this.numPanos == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, this.numPanos.intValue());
                }

                public final View mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                        this.orientation = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                this.interaction = Boolean.valueOf(codedInputByteBufferNano.readBool());
                                break;
                            case 24:
                                this.withSound = Boolean.valueOf(codedInputByteBufferNano.readBool());
                                break;
                            case 32:
                                this.numPanos = Integer.valueOf(codedInputByteBufferNano.readInt32());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.orientation != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.orientation.intValue());
                    }
                    if (this.interaction != null) {
                        codedOutputByteBufferNano.writeBool(2, this.interaction.booleanValue());
                    }
                    if (this.withSound != null) {
                        codedOutputByteBufferNano.writeBool(3, this.withSound.booleanValue());
                    }
                    if (this.numPanos != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.numPanos.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public Cyclops() {
                clear();
            }

            public static Cyclops[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Cyclops[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Cyclops parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Cyclops().mergeFrom(codedInputByteBufferNano);
            }

            public static Cyclops parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Cyclops) MessageNano.mergeFrom(new Cyclops(), bArr);
            }

            public final Cyclops clear() {
                this.capture = null;
                this.view = null;
                this.share = null;
                this.shareStart = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Cyclops clone() {
                try {
                    Cyclops cyclops = (Cyclops) super.clone();
                    if (this.capture != null) {
                        cyclops.capture = this.capture.clone();
                    }
                    if (this.view != null) {
                        cyclops.view = this.view.clone();
                    }
                    if (this.share != null) {
                        cyclops.share = this.share.clone();
                    }
                    if (this.shareStart != null) {
                        cyclops.shareStart = this.shareStart.clone();
                    }
                    return cyclops;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.capture != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.capture);
                }
                if (this.view != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.view);
                }
                if (this.share != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.share);
                }
                return this.shareStart == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(4, this.shareStart);
            }

            public final Cyclops mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            if (this.capture == null) {
                                this.capture = new Capture();
                            }
                            codedInputByteBufferNano.readMessage(this.capture);
                            break;
                        case 18:
                            if (this.view == null) {
                                this.view = new View();
                            }
                            codedInputByteBufferNano.readMessage(this.view);
                            break;
                        case 26:
                            if (this.share == null) {
                                this.share = new Share();
                            }
                            codedInputByteBufferNano.readMessage(this.share);
                            break;
                        case 34:
                            if (this.shareStart == null) {
                                this.shareStart = new ShareStart();
                            }
                            codedInputByteBufferNano.readMessage(this.shareStart);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.capture != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.capture);
                }
                if (this.view != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.view);
                }
                if (this.share != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.share);
                }
                if (this.shareStart != null) {
                    codedOutputByteBufferNano.writeMessage(4, this.shareStart);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class DoublePrecisionTransform extends ExtendableMessageNano<DoublePrecisionTransform> implements Cloneable {
            private static volatile DoublePrecisionTransform[] _emptyArray;
            public Double rotationQx;
            public Double rotationQy;
            public Double rotationQz;
            public Double scale;
            public Double translationX;
            public Double translationY;
            public Double translationZ;

            public DoublePrecisionTransform() {
                clear();
            }

            public static DoublePrecisionTransform[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new DoublePrecisionTransform[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static DoublePrecisionTransform parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new DoublePrecisionTransform().mergeFrom(codedInputByteBufferNano);
            }

            public static DoublePrecisionTransform parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (DoublePrecisionTransform) MessageNano.mergeFrom(new DoublePrecisionTransform(), bArr);
            }

            public final DoublePrecisionTransform clear() {
                this.translationX = null;
                this.translationY = null;
                this.translationZ = null;
                this.rotationQx = null;
                this.rotationQy = null;
                this.rotationQz = null;
                this.scale = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final DoublePrecisionTransform clone() {
                try {
                    return (DoublePrecisionTransform) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.translationX != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(1, this.translationX.doubleValue());
                }
                if (this.translationY != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(2, this.translationY.doubleValue());
                }
                if (this.translationZ != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(3, this.translationZ.doubleValue());
                }
                if (this.rotationQx != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(4, this.rotationQx.doubleValue());
                }
                if (this.rotationQy != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(5, this.rotationQy.doubleValue());
                }
                if (this.rotationQz != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(6, this.rotationQz.doubleValue());
                }
                return this.scale == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeDoubleSize(7, this.scale.doubleValue());
            }

            public final DoublePrecisionTransform mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 9:
                            this.translationX = Double.valueOf(codedInputByteBufferNano.readDouble());
                            break;
                        case 17:
                            this.translationY = Double.valueOf(codedInputByteBufferNano.readDouble());
                            break;
                        case 25:
                            this.translationZ = Double.valueOf(codedInputByteBufferNano.readDouble());
                            break;
                        case 33:
                            this.rotationQx = Double.valueOf(codedInputByteBufferNano.readDouble());
                            break;
                        case 41:
                            this.rotationQy = Double.valueOf(codedInputByteBufferNano.readDouble());
                            break;
                        case 49:
                            this.rotationQz = Double.valueOf(codedInputByteBufferNano.readDouble());
                            break;
                        case 57:
                            this.scale = Double.valueOf(codedInputByteBufferNano.readDouble());
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.translationX != null) {
                    codedOutputByteBufferNano.writeDouble(1, this.translationX.doubleValue());
                }
                if (this.translationY != null) {
                    codedOutputByteBufferNano.writeDouble(2, this.translationY.doubleValue());
                }
                if (this.translationZ != null) {
                    codedOutputByteBufferNano.writeDouble(3, this.translationZ.doubleValue());
                }
                if (this.rotationQx != null) {
                    codedOutputByteBufferNano.writeDouble(4, this.rotationQx.doubleValue());
                }
                if (this.rotationQy != null) {
                    codedOutputByteBufferNano.writeDouble(5, this.rotationQy.doubleValue());
                }
                if (this.rotationQz != null) {
                    codedOutputByteBufferNano.writeDouble(6, this.rotationQz.doubleValue());
                }
                if (this.scale != null) {
                    codedOutputByteBufferNano.writeDouble(7, this.scale.doubleValue());
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class EarthVr extends ExtendableMessageNano<EarthVr> implements Cloneable {
            private static volatile EarthVr[] _emptyArray;
            public Actor[] actors;
            public AppState appState;
            public ControllerState[] controllerStates;
            public Environment environment;
            public Menu menu;
            public Preferences preferences;
            public SplashScreen splashScreen;
            public Transform startFromHeadTransform;
            public DoublePrecisionTransform startFromKeyholeTransform;
            public Tour tour;
            public Tutorial tutorial;
            public View view;

            public static final class Actor extends ExtendableMessageNano<Actor> implements Cloneable {
                private static volatile Actor[] _emptyArray;
                public ControllerState[] controllerStates;
                public Transform startFromHeadTransform;

                public static final class ControllerState extends ExtendableMessageNano<ControllerState> implements Cloneable {
                    private static volatile ControllerState[] _emptyArray;
                    public Integer role;
                    public Transform startFromControllerTransform;

                    public interface Role {
                        public static final int PRIMARY = 1;
                        public static final int SECONDARY = 2;
                        public static final int UNKNOWN = 0;
                    }

                    public ControllerState() {
                        clear();
                    }

                    public static ControllerState[] emptyArray() {
                        if (_emptyArray == null) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (_emptyArray == null) {
                                    _emptyArray = new ControllerState[0];
                                }
                            }
                        }
                        return _emptyArray;
                    }

                    public static ControllerState parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        return new ControllerState().mergeFrom(codedInputByteBufferNano);
                    }

                    public static ControllerState parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                        return (ControllerState) MessageNano.mergeFrom(new ControllerState(), bArr);
                    }

                    public final ControllerState clear() {
                        this.startFromControllerTransform = null;
                        this.unknownFieldData = null;
                        this.cachedSize = -1;
                        return this;
                    }

                    public final ControllerState clone() {
                        try {
                            ControllerState controllerState = (ControllerState) super.clone();
                            if (this.startFromControllerTransform != null) {
                                controllerState.startFromControllerTransform = this.startFromControllerTransform.clone();
                            }
                            return controllerState;
                        } catch (CloneNotSupportedException e) {
                            throw new AssertionError(e);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public final int computeSerializedSize() {
                        int computeSerializedSize = super.computeSerializedSize();
                        if (this.role != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.role.intValue());
                        }
                        return this.startFromControllerTransform == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, this.startFromControllerTransform);
                    }

                    public final ControllerState mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                        case 2:
                                            this.role = Integer.valueOf(readInt32);
                                            break;
                                    }
                                case 18:
                                    if (this.startFromControllerTransform == null) {
                                        this.startFromControllerTransform = new Transform();
                                    }
                                    codedInputByteBufferNano.readMessage(this.startFromControllerTransform);
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

                    public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                        if (this.role != null) {
                            codedOutputByteBufferNano.writeInt32(1, this.role.intValue());
                        }
                        if (this.startFromControllerTransform != null) {
                            codedOutputByteBufferNano.writeMessage(2, this.startFromControllerTransform);
                        }
                        super.writeTo(codedOutputByteBufferNano);
                    }
                }

                public Actor() {
                    clear();
                }

                public static Actor[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Actor[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Actor parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Actor().mergeFrom(codedInputByteBufferNano);
                }

                public static Actor parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Actor) MessageNano.mergeFrom(new Actor(), bArr);
                }

                public final Actor clear() {
                    this.startFromHeadTransform = null;
                    this.controllerStates = ControllerState.emptyArray();
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Actor clone() {
                    try {
                        Actor actor = (Actor) super.clone();
                        if (this.startFromHeadTransform != null) {
                            actor.startFromHeadTransform = this.startFromHeadTransform.clone();
                        }
                        if (this.controllerStates != null && this.controllerStates.length > 0) {
                            actor.controllerStates = new ControllerState[this.controllerStates.length];
                            for (int i = 0; i < this.controllerStates.length; i++) {
                                if (this.controllerStates[i] != null) {
                                    actor.controllerStates[i] = this.controllerStates[i].clone();
                                }
                            }
                        }
                        return actor;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.startFromHeadTransform != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromHeadTransform);
                    }
                    if (this.controllerStates == null || this.controllerStates.length <= 0) {
                        return computeSerializedSize;
                    }
                    int i = computeSerializedSize;
                    for (ControllerState controllerState : this.controllerStates) {
                        if (controllerState != null) {
                            i += CodedOutputByteBufferNano.computeMessageSize(3, controllerState);
                        }
                    }
                    return i;
                }

                public final Actor mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 18:
                                if (this.startFromHeadTransform == null) {
                                    this.startFromHeadTransform = new Transform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromHeadTransform);
                                break;
                            case 26:
                                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                                int length = this.controllerStates != null ? this.controllerStates.length : 0;
                                ControllerState[] controllerStateArr = new ControllerState[(repeatedFieldArrayLength + length)];
                                if (length != 0) {
                                    System.arraycopy(this.controllerStates, 0, controllerStateArr, 0, length);
                                }
                                while (length < controllerStateArr.length - 1) {
                                    controllerStateArr[length] = new ControllerState();
                                    codedInputByteBufferNano.readMessage(controllerStateArr[length]);
                                    codedInputByteBufferNano.readTag();
                                    length++;
                                }
                                controllerStateArr[length] = new ControllerState();
                                codedInputByteBufferNano.readMessage(controllerStateArr[length]);
                                this.controllerStates = controllerStateArr;
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.startFromHeadTransform != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.startFromHeadTransform);
                    }
                    if (this.controllerStates != null && this.controllerStates.length > 0) {
                        for (ControllerState controllerState : this.controllerStates) {
                            if (controllerState != null) {
                                codedOutputByteBufferNano.writeMessage(3, controllerState);
                            }
                        }
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class AppState extends ExtendableMessageNano<AppState> implements Cloneable {
                private static volatile AppState[] _emptyArray;
                public Long appModeId;

                public AppState() {
                    clear();
                }

                public static AppState[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new AppState[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static AppState parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new AppState().mergeFrom(codedInputByteBufferNano);
                }

                public static AppState parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (AppState) MessageNano.mergeFrom(new AppState(), bArr);
                }

                public final AppState clear() {
                    this.appModeId = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final AppState clone() {
                    try {
                        return (AppState) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    return this.appModeId == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(1, this.appModeId.longValue());
                }

                public final AppState mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                this.appModeId = Long.valueOf(codedInputByteBufferNano.readInt64());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.appModeId != null) {
                        codedOutputByteBufferNano.writeInt64(1, this.appModeId.longValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class ControllerState extends ExtendableMessageNano<ControllerState> implements Cloneable {
                private static volatile ControllerState[] _emptyArray;
                public Integer role;
                public Transform startFromControllerTransform;

                public interface Role {
                    public static final int PRIMARY = 1;
                    public static final int SECONDARY = 2;
                    public static final int UNKNOWN = 0;
                }

                public ControllerState() {
                    clear();
                }

                public static ControllerState[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new ControllerState[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static ControllerState parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new ControllerState().mergeFrom(codedInputByteBufferNano);
                }

                public static ControllerState parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (ControllerState) MessageNano.mergeFrom(new ControllerState(), bArr);
                }

                public final ControllerState clear() {
                    this.startFromControllerTransform = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final ControllerState clone() {
                    try {
                        ControllerState controllerState = (ControllerState) super.clone();
                        if (this.startFromControllerTransform != null) {
                            controllerState.startFromControllerTransform = this.startFromControllerTransform.clone();
                        }
                        return controllerState;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.role != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.role.intValue());
                    }
                    return this.startFromControllerTransform == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, this.startFromControllerTransform);
                }

                public final ControllerState mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                        this.role = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 18:
                                if (this.startFromControllerTransform == null) {
                                    this.startFromControllerTransform = new Transform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromControllerTransform);
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.role != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.role.intValue());
                    }
                    if (this.startFromControllerTransform != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.startFromControllerTransform);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class Environment extends ExtendableMessageNano<Environment> implements Cloneable {
                private static volatile Environment[] _emptyArray;
                public Transform startFromEnvironmentTransform;

                public Environment() {
                    clear();
                }

                public static Environment[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Environment[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Environment parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Environment().mergeFrom(codedInputByteBufferNano);
                }

                public static Environment parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Environment) MessageNano.mergeFrom(new Environment(), bArr);
                }

                public final Environment clear() {
                    this.startFromEnvironmentTransform = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Environment clone() {
                    try {
                        Environment environment = (Environment) super.clone();
                        if (this.startFromEnvironmentTransform != null) {
                            environment.startFromEnvironmentTransform = this.startFromEnvironmentTransform.clone();
                        }
                        return environment;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    return this.startFromEnvironmentTransform == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, this.startFromEnvironmentTransform);
                }

                public final Environment mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 10:
                                if (this.startFromEnvironmentTransform == null) {
                                    this.startFromEnvironmentTransform = new Transform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromEnvironmentTransform);
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.startFromEnvironmentTransform != null) {
                        codedOutputByteBufferNano.writeMessage(1, this.startFromEnvironmentTransform);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class Menu extends ExtendableMessageNano<Menu> implements Cloneable {
                private static volatile Menu[] _emptyArray;
                public String categoryName;
                public String contentKey;
                public Integer pageIndex;

                public Menu() {
                    clear();
                }

                public static Menu[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Menu[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Menu parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Menu().mergeFrom(codedInputByteBufferNano);
                }

                public static Menu parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Menu) MessageNano.mergeFrom(new Menu(), bArr);
                }

                public final Menu clear() {
                    this.categoryName = null;
                    this.pageIndex = null;
                    this.contentKey = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Menu clone() {
                    try {
                        return (Menu) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.categoryName != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.categoryName);
                    }
                    if (this.pageIndex != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.pageIndex.intValue());
                    }
                    return this.contentKey == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(3, this.contentKey);
                }

                public final Menu mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 10:
                                this.categoryName = codedInputByteBufferNano.readString();
                                break;
                            case 16:
                                this.pageIndex = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 26:
                                this.contentKey = codedInputByteBufferNano.readString();
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.categoryName != null) {
                        codedOutputByteBufferNano.writeString(1, this.categoryName);
                    }
                    if (this.pageIndex != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.pageIndex.intValue());
                    }
                    if (this.contentKey != null) {
                        codedOutputByteBufferNano.writeString(3, this.contentKey);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class Preferences extends ExtendableMessageNano<Preferences> implements Cloneable {
                private static volatile Preferences[] _emptyArray;
                public Integer comfortModeState;
                public Integer guestMode;
                public Integer humanScaleMode;
                public Integer labelsState;
                public Integer startConfiguration;

                public interface ComfortModeState {
                    public static final int COMFORT_MODE_DISABLED = 1;
                    public static final int COMFORT_MODE_ENABLED = 2;
                    public static final int COMFORT_MODE_UNKNOWN = 0;
                }

                public interface GuestMode {
                    public static final int GUEST_MODE_OFF = 1;
                    public static final int GUEST_MODE_ON = 2;
                    public static final int GUEST_MODE_UNKNOWN = 0;
                }

                public interface HumanScaleMode {
                    public static final int ALLOW_HUMAN_SCALE = 2;
                    public static final int DISALLOW_HUMAN_SCALE = 1;
                    public static final int HUMAN_SCALE_UNKNOWN = 0;
                }

                public interface LabelsState {
                    public static final int LABELS_DISABLED = 1;
                    public static final int LABELS_ENABLED = 2;
                    public static final int LABELS_UNKNOWN = 0;
                }

                public interface MusicMode {
                    public static final int MUSIC_OFF = 1;
                    public static final int MUSIC_ON = 2;
                    public static final int MUSIC_UNKNOWN = 0;
                }

                public interface StartConfiguration {
                    public static final int APP_HAS_BEEN_RUN_BEFORE = 2;
                    public static final int APP_HAS_NEVER_BEEN_RUN = 1;
                    public static final int CONFIGURATION_UNKNOWN = 0;
                }

                public Preferences() {
                    clear();
                }

                public static Preferences[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Preferences[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Preferences parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Preferences().mergeFrom(codedInputByteBufferNano);
                }

                public static Preferences parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Preferences) MessageNano.mergeFrom(new Preferences(), bArr);
                }

                public final Preferences clear() {
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Preferences clone() {
                    try {
                        return (Preferences) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.labelsState != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.labelsState.intValue());
                    }
                    if (this.comfortModeState != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.comfortModeState.intValue());
                    }
                    if (this.startConfiguration != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.startConfiguration.intValue());
                    }
                    if (this.guestMode != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.guestMode.intValue());
                    }
                    return this.humanScaleMode == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(5, this.humanScaleMode.intValue());
                }

                public final Preferences mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                        this.labelsState = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                int readInt322 = codedInputByteBufferNano.readInt32();
                                switch (readInt322) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        this.comfortModeState = Integer.valueOf(readInt322);
                                        break;
                                }
                            case 24:
                                int readInt323 = codedInputByteBufferNano.readInt32();
                                switch (readInt323) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        this.startConfiguration = Integer.valueOf(readInt323);
                                        break;
                                }
                            case 32:
                                int readInt324 = codedInputByteBufferNano.readInt32();
                                switch (readInt324) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        this.guestMode = Integer.valueOf(readInt324);
                                        break;
                                }
                            case 40:
                                int readInt325 = codedInputByteBufferNano.readInt32();
                                switch (readInt325) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        this.humanScaleMode = Integer.valueOf(readInt325);
                                        break;
                                }
                            default:
                                if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                                    break;
                                } else {
                                    return this;
                                }
                        }
                    }
                }

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.labelsState != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.labelsState.intValue());
                    }
                    if (this.comfortModeState != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.comfortModeState.intValue());
                    }
                    if (this.startConfiguration != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.startConfiguration.intValue());
                    }
                    if (this.guestMode != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.guestMode.intValue());
                    }
                    if (this.humanScaleMode != null) {
                        codedOutputByteBufferNano.writeInt32(5, this.humanScaleMode.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class SplashScreen extends ExtendableMessageNano<SplashScreen> implements Cloneable {
                private static volatile SplashScreen[] _emptyArray;
                public Integer exitType;
                public Long numberOfViewPreloadsAttempted;
                public Long numberOfViewPreloadsSucceeded;
                public Long viewPreloadDurationMs;

                public interface ExitType {
                    public static final int EXIT_TYPE_START_ACTION = 1;
                    public static final int EXIT_TYPE_UNKNOWN = 0;
                }

                public SplashScreen() {
                    clear();
                }

                public static SplashScreen[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new SplashScreen[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static SplashScreen parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new SplashScreen().mergeFrom(codedInputByteBufferNano);
                }

                public static SplashScreen parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (SplashScreen) MessageNano.mergeFrom(new SplashScreen(), bArr);
                }

                public final SplashScreen clear() {
                    this.numberOfViewPreloadsAttempted = null;
                    this.numberOfViewPreloadsSucceeded = null;
                    this.viewPreloadDurationMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final SplashScreen clone() {
                    try {
                        return (SplashScreen) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.exitType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.exitType.intValue());
                    }
                    if (this.numberOfViewPreloadsAttempted != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, this.numberOfViewPreloadsAttempted.longValue());
                    }
                    if (this.numberOfViewPreloadsSucceeded != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, this.numberOfViewPreloadsSucceeded.longValue());
                    }
                    return this.viewPreloadDurationMs == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(4, this.viewPreloadDurationMs.longValue());
                }

                public final SplashScreen mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                        this.exitType = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                this.numberOfViewPreloadsAttempted = Long.valueOf(codedInputByteBufferNano.readInt64());
                                break;
                            case 24:
                                this.numberOfViewPreloadsSucceeded = Long.valueOf(codedInputByteBufferNano.readInt64());
                                break;
                            case 32:
                                this.viewPreloadDurationMs = Long.valueOf(codedInputByteBufferNano.readInt64());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.exitType != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.exitType.intValue());
                    }
                    if (this.numberOfViewPreloadsAttempted != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.numberOfViewPreloadsAttempted.longValue());
                    }
                    if (this.numberOfViewPreloadsSucceeded != null) {
                        codedOutputByteBufferNano.writeInt64(3, this.numberOfViewPreloadsSucceeded.longValue());
                    }
                    if (this.viewPreloadDurationMs != null) {
                        codedOutputByteBufferNano.writeInt64(4, this.viewPreloadDurationMs.longValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class Tour extends ExtendableMessageNano<Tour> implements Cloneable {
                private static volatile Tour[] _emptyArray;
                public String name;
                public Long playbackMs;

                public Tour() {
                    clear();
                }

                public static Tour[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Tour[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Tour parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Tour().mergeFrom(codedInputByteBufferNano);
                }

                public static Tour parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Tour) MessageNano.mergeFrom(new Tour(), bArr);
                }

                public final Tour clear() {
                    this.name = null;
                    this.playbackMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Tour clone() {
                    try {
                        return (Tour) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.name != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.name);
                    }
                    return this.playbackMs == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(2, this.playbackMs.longValue());
                }

                public final Tour mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 10:
                                this.name = codedInputByteBufferNano.readString();
                                break;
                            case 16:
                                this.playbackMs = Long.valueOf(codedInputByteBufferNano.readInt64());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.name != null) {
                        codedOutputByteBufferNano.writeString(1, this.name);
                    }
                    if (this.playbackMs != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.playbackMs.longValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class Tutorial extends ExtendableMessageNano<Tutorial> implements Cloneable {
                private static volatile Tutorial[] _emptyArray;
                public Integer stage;
                public String stageName;

                public Tutorial() {
                    clear();
                }

                public static Tutorial[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Tutorial[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Tutorial parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Tutorial().mergeFrom(codedInputByteBufferNano);
                }

                public static Tutorial parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Tutorial) MessageNano.mergeFrom(new Tutorial(), bArr);
                }

                public final Tutorial clear() {
                    this.stage = null;
                    this.stageName = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Tutorial clone() {
                    try {
                        return (Tutorial) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.stage != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.stage.intValue());
                    }
                    return this.stageName == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(2, this.stageName);
                }

                public final Tutorial mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                this.stage = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 18:
                                this.stageName = codedInputByteBufferNano.readString();
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.stage != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.stage.intValue());
                    }
                    if (this.stageName != null) {
                        codedOutputByteBufferNano.writeString(2, this.stageName);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class View extends ExtendableMessageNano<View> implements Cloneable {
                private static volatile View[] _emptyArray;
                public Integer mode;
                public Long simulationSecondsSinceEpoch;
                public DoublePrecisionTransform startFromKeyholeTransform;

                public interface Mode {
                    public static final int PLANET = 1;
                    public static final int TERRAIN = 2;
                    public static final int TRANSITION = 3;
                    public static final int UNKNOWN = 0;
                }

                public View() {
                    clear();
                }

                public static View[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new View[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static View parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new View().mergeFrom(codedInputByteBufferNano);
                }

                public static View parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (View) MessageNano.mergeFrom(new View(), bArr);
                }

                public final View clear() {
                    this.startFromKeyholeTransform = null;
                    this.simulationSecondsSinceEpoch = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final View clone() {
                    try {
                        View view = (View) super.clone();
                        if (this.startFromKeyholeTransform != null) {
                            view.startFromKeyholeTransform = this.startFromKeyholeTransform.clone();
                        }
                        return view;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.mode != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.mode.intValue());
                    }
                    if (this.startFromKeyholeTransform != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromKeyholeTransform);
                    }
                    return this.simulationSecondsSinceEpoch == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(3, this.simulationSecondsSinceEpoch.longValue());
                }

                public final View mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                    case 3:
                                        this.mode = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 18:
                                if (this.startFromKeyholeTransform == null) {
                                    this.startFromKeyholeTransform = new DoublePrecisionTransform();
                                }
                                codedInputByteBufferNano.readMessage(this.startFromKeyholeTransform);
                                break;
                            case 24:
                                this.simulationSecondsSinceEpoch = Long.valueOf(codedInputByteBufferNano.readInt64());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.mode != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.mode.intValue());
                    }
                    if (this.startFromKeyholeTransform != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.startFromKeyholeTransform);
                    }
                    if (this.simulationSecondsSinceEpoch != null) {
                        codedOutputByteBufferNano.writeInt64(3, this.simulationSecondsSinceEpoch.longValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public EarthVr() {
                clear();
            }

            public static EarthVr[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new EarthVr[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static EarthVr parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new EarthVr().mergeFrom(codedInputByteBufferNano);
            }

            public static EarthVr parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (EarthVr) MessageNano.mergeFrom(new EarthVr(), bArr);
            }

            public final EarthVr clear() {
                this.startFromKeyholeTransform = null;
                this.startFromHeadTransform = null;
                this.controllerStates = ControllerState.emptyArray();
                this.appState = null;
                this.view = null;
                this.menu = null;
                this.preferences = null;
                this.tour = null;
                this.tutorial = null;
                this.actors = Actor.emptyArray();
                this.environment = null;
                this.splashScreen = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final EarthVr clone() {
                try {
                    EarthVr earthVr = (EarthVr) super.clone();
                    if (this.startFromKeyholeTransform != null) {
                        earthVr.startFromKeyholeTransform = this.startFromKeyholeTransform.clone();
                    }
                    if (this.startFromHeadTransform != null) {
                        earthVr.startFromHeadTransform = this.startFromHeadTransform.clone();
                    }
                    if (this.controllerStates != null && this.controllerStates.length > 0) {
                        earthVr.controllerStates = new ControllerState[this.controllerStates.length];
                        for (int i = 0; i < this.controllerStates.length; i++) {
                            if (this.controllerStates[i] != null) {
                                earthVr.controllerStates[i] = this.controllerStates[i].clone();
                            }
                        }
                    }
                    if (this.appState != null) {
                        earthVr.appState = this.appState.clone();
                    }
                    if (this.view != null) {
                        earthVr.view = this.view.clone();
                    }
                    if (this.menu != null) {
                        earthVr.menu = this.menu.clone();
                    }
                    if (this.preferences != null) {
                        earthVr.preferences = this.preferences.clone();
                    }
                    if (this.tour != null) {
                        earthVr.tour = this.tour.clone();
                    }
                    if (this.tutorial != null) {
                        earthVr.tutorial = this.tutorial.clone();
                    }
                    if (this.actors != null && this.actors.length > 0) {
                        earthVr.actors = new Actor[this.actors.length];
                        for (int i2 = 0; i2 < this.actors.length; i2++) {
                            if (this.actors[i2] != null) {
                                earthVr.actors[i2] = this.actors[i2].clone();
                            }
                        }
                    }
                    if (this.environment != null) {
                        earthVr.environment = this.environment.clone();
                    }
                    if (this.splashScreen != null) {
                        earthVr.splashScreen = this.splashScreen.clone();
                    }
                    return earthVr;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.startFromKeyholeTransform != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.startFromKeyholeTransform);
                }
                if (this.startFromHeadTransform != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.startFromHeadTransform);
                }
                if (this.controllerStates != null && this.controllerStates.length > 0) {
                    int i = computeSerializedSize;
                    for (ControllerState controllerState : this.controllerStates) {
                        if (controllerState != null) {
                            i += CodedOutputByteBufferNano.computeMessageSize(3, controllerState);
                        }
                    }
                    computeSerializedSize = i;
                }
                if (this.appState != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.appState);
                }
                if (this.view != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, this.view);
                }
                if (this.menu != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, this.menu);
                }
                if (this.preferences != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, this.preferences);
                }
                if (this.tour != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, this.tour);
                }
                if (this.tutorial != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(9, this.tutorial);
                }
                if (this.actors != null && this.actors.length > 0) {
                    for (Actor actor : this.actors) {
                        if (actor != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(10, actor);
                        }
                    }
                }
                if (this.environment != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(11, this.environment);
                }
                return this.splashScreen == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(12, this.splashScreen);
            }

            public final EarthVr mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            if (this.startFromKeyholeTransform == null) {
                                this.startFromKeyholeTransform = new DoublePrecisionTransform();
                            }
                            codedInputByteBufferNano.readMessage(this.startFromKeyholeTransform);
                            break;
                        case 18:
                            if (this.startFromHeadTransform == null) {
                                this.startFromHeadTransform = new Transform();
                            }
                            codedInputByteBufferNano.readMessage(this.startFromHeadTransform);
                            break;
                        case 26:
                            int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                            int length = this.controllerStates != null ? this.controllerStates.length : 0;
                            ControllerState[] controllerStateArr = new ControllerState[(repeatedFieldArrayLength + length)];
                            if (length != 0) {
                                System.arraycopy(this.controllerStates, 0, controllerStateArr, 0, length);
                            }
                            while (length < controllerStateArr.length - 1) {
                                controllerStateArr[length] = new ControllerState();
                                codedInputByteBufferNano.readMessage(controllerStateArr[length]);
                                codedInputByteBufferNano.readTag();
                                length++;
                            }
                            controllerStateArr[length] = new ControllerState();
                            codedInputByteBufferNano.readMessage(controllerStateArr[length]);
                            this.controllerStates = controllerStateArr;
                            break;
                        case 34:
                            if (this.appState == null) {
                                this.appState = new AppState();
                            }
                            codedInputByteBufferNano.readMessage(this.appState);
                            break;
                        case 42:
                            if (this.view == null) {
                                this.view = new View();
                            }
                            codedInputByteBufferNano.readMessage(this.view);
                            break;
                        case 50:
                            if (this.menu == null) {
                                this.menu = new Menu();
                            }
                            codedInputByteBufferNano.readMessage(this.menu);
                            break;
                        case 58:
                            if (this.preferences == null) {
                                this.preferences = new Preferences();
                            }
                            codedInputByteBufferNano.readMessage(this.preferences);
                            break;
                        case 66:
                            if (this.tour == null) {
                                this.tour = new Tour();
                            }
                            codedInputByteBufferNano.readMessage(this.tour);
                            break;
                        case 74:
                            if (this.tutorial == null) {
                                this.tutorial = new Tutorial();
                            }
                            codedInputByteBufferNano.readMessage(this.tutorial);
                            break;
                        case 82:
                            int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 82);
                            int length2 = this.actors != null ? this.actors.length : 0;
                            Actor[] actorArr = new Actor[(repeatedFieldArrayLength2 + length2)];
                            if (length2 != 0) {
                                System.arraycopy(this.actors, 0, actorArr, 0, length2);
                            }
                            while (length2 < actorArr.length - 1) {
                                actorArr[length2] = new Actor();
                                codedInputByteBufferNano.readMessage(actorArr[length2]);
                                codedInputByteBufferNano.readTag();
                                length2++;
                            }
                            actorArr[length2] = new Actor();
                            codedInputByteBufferNano.readMessage(actorArr[length2]);
                            this.actors = actorArr;
                            break;
                        case 90:
                            if (this.environment == null) {
                                this.environment = new Environment();
                            }
                            codedInputByteBufferNano.readMessage(this.environment);
                            break;
                        case 98:
                            if (this.splashScreen == null) {
                                this.splashScreen = new SplashScreen();
                            }
                            codedInputByteBufferNano.readMessage(this.splashScreen);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.startFromKeyholeTransform != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.startFromKeyholeTransform);
                }
                if (this.startFromHeadTransform != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.startFromHeadTransform);
                }
                if (this.controllerStates != null && this.controllerStates.length > 0) {
                    for (ControllerState controllerState : this.controllerStates) {
                        if (controllerState != null) {
                            codedOutputByteBufferNano.writeMessage(3, controllerState);
                        }
                    }
                }
                if (this.appState != null) {
                    codedOutputByteBufferNano.writeMessage(4, this.appState);
                }
                if (this.view != null) {
                    codedOutputByteBufferNano.writeMessage(5, this.view);
                }
                if (this.menu != null) {
                    codedOutputByteBufferNano.writeMessage(6, this.menu);
                }
                if (this.preferences != null) {
                    codedOutputByteBufferNano.writeMessage(7, this.preferences);
                }
                if (this.tour != null) {
                    codedOutputByteBufferNano.writeMessage(8, this.tour);
                }
                if (this.tutorial != null) {
                    codedOutputByteBufferNano.writeMessage(9, this.tutorial);
                }
                if (this.actors != null && this.actors.length > 0) {
                    for (Actor actor : this.actors) {
                        if (actor != null) {
                            codedOutputByteBufferNano.writeMessage(10, actor);
                        }
                    }
                }
                if (this.environment != null) {
                    codedOutputByteBufferNano.writeMessage(11, this.environment);
                }
                if (this.splashScreen != null) {
                    codedOutputByteBufferNano.writeMessage(12, this.splashScreen);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class EmbedVrWidget extends ExtendableMessageNano<EmbedVrWidget> implements Cloneable {
            private static volatile EmbedVrWidget[] _emptyArray;
            public String errorMsg;
            public Pano pano;
            public Video video;
            public Integer viewMode;

            public static final class Pano extends ExtendableMessageNano<Pano> implements Cloneable {
                private static volatile Pano[] _emptyArray;
                public Integer heightPixels;
                public Integer stereoFormat;
                public Integer widthPixels;

                public Pano() {
                    clear();
                }

                public static Pano[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Pano[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Pano parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Pano().mergeFrom(codedInputByteBufferNano);
                }

                public static Pano parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Pano) MessageNano.mergeFrom(new Pano(), bArr);
                }

                public final Pano clear() {
                    this.widthPixels = null;
                    this.heightPixels = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Pano clone() {
                    try {
                        return (Pano) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.widthPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.widthPixels.intValue());
                    }
                    if (this.heightPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.heightPixels.intValue());
                    }
                    return this.stereoFormat == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, this.stereoFormat.intValue());
                }

                public final Pano mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                this.widthPixels = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 16:
                                this.heightPixels = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 24:
                                int readInt32 = codedInputByteBufferNano.readInt32();
                                switch (readInt32) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        this.stereoFormat = Integer.valueOf(readInt32);
                                        break;
                                }
                            default:
                                if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                                    break;
                                } else {
                                    return this;
                                }
                        }
                    }
                }

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.widthPixels != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.widthPixels.intValue());
                    }
                    if (this.heightPixels != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.heightPixels.intValue());
                    }
                    if (this.stereoFormat != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.stereoFormat.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public interface StereoFormat {
                public static final int MONO = 1;
                public static final int STEREO_OVER_UNDER = 2;
                public static final int UNKNOWN_FORMAT = 0;
            }

            public static final class Video extends ExtendableMessageNano<Video> implements Cloneable {
                private static volatile Video[] _emptyArray;
                public Integer heightPixels;
                public Integer stereoFormat;
                public Integer videoDurationMs;
                public Integer widthPixels;

                public Video() {
                    clear();
                }

                public static Video[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Video[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Video parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Video().mergeFrom(codedInputByteBufferNano);
                }

                public static Video parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Video) MessageNano.mergeFrom(new Video(), bArr);
                }

                public final Video clear() {
                    this.widthPixels = null;
                    this.heightPixels = null;
                    this.videoDurationMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Video clone() {
                    try {
                        return (Video) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.widthPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.widthPixels.intValue());
                    }
                    if (this.heightPixels != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.heightPixels.intValue());
                    }
                    if (this.stereoFormat != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.stereoFormat.intValue());
                    }
                    return this.videoDurationMs == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, this.videoDurationMs.intValue());
                }

                public final Video mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                this.widthPixels = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 16:
                                this.heightPixels = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 24:
                                int readInt32 = codedInputByteBufferNano.readInt32();
                                switch (readInt32) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        this.stereoFormat = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 32:
                                this.videoDurationMs = Integer.valueOf(codedInputByteBufferNano.readInt32());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.widthPixels != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.widthPixels.intValue());
                    }
                    if (this.heightPixels != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.heightPixels.intValue());
                    }
                    if (this.stereoFormat != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.stereoFormat.intValue());
                    }
                    if (this.videoDurationMs != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.videoDurationMs.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public interface ViewMode {
                public static final int EMBEDDED = 1;
                public static final int FULLSCREEN_MONO = 2;
                public static final int FULLSCREEN_VR = 3;
                public static final int UNKNOWN_MODE = 0;
            }

            public EmbedVrWidget() {
                clear();
            }

            public static EmbedVrWidget[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new EmbedVrWidget[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static EmbedVrWidget parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new EmbedVrWidget().mergeFrom(codedInputByteBufferNano);
            }

            public static EmbedVrWidget parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (EmbedVrWidget) MessageNano.mergeFrom(new EmbedVrWidget(), bArr);
            }

            public final EmbedVrWidget clear() {
                this.pano = null;
                this.video = null;
                this.errorMsg = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final EmbedVrWidget clone() {
                try {
                    EmbedVrWidget embedVrWidget = (EmbedVrWidget) super.clone();
                    if (this.pano != null) {
                        embedVrWidget.pano = this.pano.clone();
                    }
                    if (this.video != null) {
                        embedVrWidget.video = this.video.clone();
                    }
                    return embedVrWidget;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.viewMode != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.viewMode.intValue());
                }
                if (this.pano != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.pano);
                }
                if (this.video != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.video);
                }
                return this.errorMsg == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(4, this.errorMsg);
            }

            public final EmbedVrWidget mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                case 2:
                                case 3:
                                    this.viewMode = Integer.valueOf(readInt32);
                                    break;
                            }
                        case 18:
                            if (this.pano == null) {
                                this.pano = new Pano();
                            }
                            codedInputByteBufferNano.readMessage(this.pano);
                            break;
                        case 26:
                            if (this.video == null) {
                                this.video = new Video();
                            }
                            codedInputByteBufferNano.readMessage(this.video);
                            break;
                        case 34:
                            this.errorMsg = codedInputByteBufferNano.readString();
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.viewMode != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.viewMode.intValue());
                }
                if (this.pano != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.pano);
                }
                if (this.video != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.video);
                }
                if (this.errorMsg != null) {
                    codedOutputByteBufferNano.writeString(4, this.errorMsg);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public interface EventType {
            public static final int AUDIO_INITIALIZATION = 4000;
            public static final int AUDIO_PERFORMANCE_REPORT = 4002;
            public static final int AUDIO_SHUTDOWN = 4001;
            public static final int CYCLOPS_ACCOUNT_SWITCH = 1011;
            public static final int CYCLOPS_APPLICATION = 1000;
            public static final int CYCLOPS_CAPTURE = 1006;
            public static final int CYCLOPS_DELETE = 1003;
            public static final int CYCLOPS_FEEDBACK = 1012;
            public static final int CYCLOPS_GALLERY_CONTEXT_MENU = 1007;
            public static final int CYCLOPS_GALLERY_TO_CAPTURE = 1009;
            public static final int CYCLOPS_GALLERY_TO_GALLERY_HMD = 1008;
            public static final int CYCLOPS_RECEIVE = 1002;
            public static final int CYCLOPS_SETTINGS = 1010;
            public static final int CYCLOPS_SHARE = 1001;
            public static final int CYCLOPS_SHARE_START = 1013;
            public static final int CYCLOPS_VIEW = 1004;
            public static final int CYCLOPS_VIEW_HMD = 1005;
            public static final int EARTHVR_APP_ENVIRONMENT_CHANGED = 8006;
            public static final int EARTHVR_APP_IDLE = 8002;
            public static final int EARTHVR_APP_INITIALIZED = 8007;
            public static final int EARTHVR_APP_MODE_ENTERED = 8003;
            public static final int EARTHVR_APP_MODE_EXITED = 8004;
            public static final int EARTHVR_APP_PREFERENCES_CHANGED = 8005;
            public static final int EARTHVR_APP_STARTED = 8000;
            public static final int EARTHVR_APP_STOPPED = 8001;
            public static final int EARTHVR_DESKTOP_WINDOW_MENU_ITEM_SELECTED = 8308;
            public static final int EARTHVR_MENU_CARD_CLICKED = 8306;
            public static final int EARTHVR_MENU_CATEGORY_SELECTED = 8304;
            public static final int EARTHVR_MENU_CLOSED = 8301;
            public static final int EARTHVR_MENU_OPENED = 8300;
            public static final int EARTHVR_MENU_PAGE_CHANGED = 8305;
            public static final int EARTHVR_MENU_PLACE_DELETION_REQUESTED = 8307;
            public static final int EARTHVR_MENU_PREFERENCES_CLOSED = 8303;
            public static final int EARTHVR_MENU_PREFERENCES_OPENED = 8302;
            public static final int EARTHVR_MINIGLOBE_BECAME_LARGE = 8503;
            public static final int EARTHVR_MINIGLOBE_BECAME_SMALL = 8504;
            public static final int EARTHVR_MINIGLOBE_ROTATED = 8505;
            public static final int EARTHVR_MINIGLOBE_TELEPORT_TRIGGERED = 8506;
            public static final int EARTHVR_NAVIGATION_ARCBALL_STARTED = 8100;
            public static final int EARTHVR_NAVIGATION_ARCBALL_STOPPED = 8102;
            public static final int EARTHVR_NAVIGATION_ARCBALL_UPDATED = 8101;
            public static final int EARTHVR_NAVIGATION_FLYING_STARTED = 8103;
            public static final int EARTHVR_NAVIGATION_FLYING_STOPPED = 8105;
            public static final int EARTHVR_NAVIGATION_FLYING_UPDATED = 8104;
            public static final int EARTHVR_NAVIGATION_ROTATING_STARTED = 8109;
            public static final int EARTHVR_NAVIGATION_ROTATING_STOPPED = 8111;
            public static final int EARTHVR_NAVIGATION_ROTATING_UPDATED = 8110;
            public static final int EARTHVR_NAVIGATION_SKY_TIME_TRAVELING_STARTED = 8106;
            public static final int EARTHVR_NAVIGATION_SKY_TIME_TRAVELING_STOPPED = 8108;
            public static final int EARTHVR_NAVIGATION_SKY_TIME_TRAVELING_UPDATED = 8107;
            public static final int EARTHVR_NAVIGATION_TRANSITION_STARTED = 8150;
            public static final int EARTHVR_NAVIGATION_TRANSITION_STOPPED = 8151;
            public static final int EARTHVR_PLACE_SAVED = 8500;
            public static final int EARTHVR_RENDERING_TUNNEL_VISION_APPEARED = 8200;
            public static final int EARTHVR_RENDERING_TUNNEL_VISION_DISAPPEARED = 8201;
            public static final int EARTHVR_REVEAL_QUERY_RESULT_READY = 8502;
            public static final int EARTHVR_REVEAL_QUERY_STARTED = 8501;
            public static final int EARTHVR_SPLASH_SCREEN_EXITED = 8405;
            public static final int EARTHVR_TOUR_COMPLETED = 8402;
            public static final int EARTHVR_TOUR_PAUSED = 8400;
            public static final int EARTHVR_TOUR_RESUMED = 8401;
            public static final int EARTHVR_TOUR_STARTED = 8404;
            public static final int EARTHVR_TUTORIAL_STAGE_CHANGED = 8403;
            public static final int EMBEDVR_LOAD_ERROR = 6003;
            public static final int EMBEDVR_LOAD_SUCCESS = 6002;
            public static final int EMBEDVR_PERFORMANCE_REPORT = 6008;
            public static final int EMBEDVR_START_SESSION = 6000;
            public static final int EMBEDVR_STOP_SESSION = 6001;
            public static final int EMBEDVR_VIDEO_PAUSE = 6006;
            public static final int EMBEDVR_VIDEO_PLAY = 6005;
            public static final int EMBEDVR_VIDEO_SEEK_TO = 6007;
            public static final int EMBEDVR_VIEW_CLICK = 6004;
            public static final int FEATURED_APPS_TAB = 23;
            public static final int GET_INSTALLED_APPLICATIONS = 2;
            public static final int GO_TO_STORE = 5;
            public static final int KEYBOARD_EVENT = 9000;
            public static final int LAUNCHER_START_APPLICATION = 3001;
            public static final int LAUNCHER_STOP_APPLICATION = 3002;
            public static final int LULLABY_ACTION = 5003;
            public static final int LULLABY_IMPRESSION = 5002;
            public static final int LULLABY_LOAD = 5004;
            public static final int LULLABY_MUTE = 5000;
            public static final int LULLABY_UNMUTE = 5001;
            public static final int MY_APPS_TAB = 22;
            public static final int NAV_ITEM_SELECTED = 24;
            public static final int PHOTOS_ACCOUNT_CHANGE = 11020;
            public static final int PHOTOS_ACCOUNT_INVALID = 11021;
            public static final int PHOTOS_APPLICATION = 11000;
            public static final int PHOTOS_BACKUP_CARD_DISMISS = 11051;
            public static final int PHOTOS_BACKUP_CARD_SHOWN = 11050;
            public static final int PHOTOS_BACKUP_CARD_SUCCESS = 11052;
            public static final int PHOTOS_CAROUSEL_NEXT = 11031;
            public static final int PHOTOS_CAROUSEL_PREV = 11032;
            public static final int PHOTOS_CC_CARD_DISMISS = 11041;
            public static final int PHOTOS_CC_CARD_SHOWN = 11040;
            public static final int PHOTOS_CC_CARD_SUCCESS = 11042;
            public static final int PHOTOS_GALLERY_NEXT = 11010;
            public static final int PHOTOS_GALLERY_PREV = 11011;
            public static final int PHOTOS_OPEN_MEDIA = 11030;
            public static final int PHOTOS_SIGN_CARD_SHOWN = 11060;
            public static final int PHOTOS_WARM_WELCOME_SHOWN = 11061;
            public static final int SDK_PERFORMANCE_REPORT = 2003;
            public static final int SDK_SCANLINE_RACING_ENABLED = 2007;
            public static final int SDK_SCANLINE_RACING_VSYNC_OVERSHOOT_FATAL = 2008;
            public static final int SDK_SENSOR_REPORT = 2004;
            public static final int SDK_SENSOR_STALL = 2009;
            public static final int SETTINGS_QR_CODE_HELP = 21;
            public static final int SETTINGS_QR_CODE_SCAN = 9;
            public static final int SETTINGS_QR_CODE_SCAN_SKIP = 10;
            public static final int SETUP_HEAD_MOUNT_INSERTED = 17;
            public static final int SETUP_HEAD_MOUNT_SWITCH = 18;
            public static final int SETUP_PAIRED_NEXT = 16;
            public static final int SETUP_QR_CODE_HELP = 15;
            public static final int SETUP_QR_CODE_SCAN = 8;
            public static final int SETUP_QR_CODE_SCAN_SKIP = 7;
            public static final int SETUP_VR_INTRO_START = 19;
            public static final int SETUP_VR_INTRO_STOP = 20;
            public static final int SETUP_WELCOME_GET_VIEWER = 12;
            public static final int SETUP_WELCOME_NEXT = 11;
            public static final int SETUP_WELCOME_SWITCH_VIEWER = 13;
            public static final int SETUP_WELCOME_USE_VIEWER = 14;
            public static final int START_APPLICATION = 3;
            public static final int START_LAUNCHER = 1;
            public static final int START_SDK_APPLICATION = 2000;
            public static final int START_VR_APPLICATION = 2005;
            public static final int START_VR_LAUNCHER_COLD = 3000;
            public static final int STOP_APPLICATION = 4;
            public static final int STOP_VR_APPLICATION = 2006;
            public static final int STREET_VIEW_APP_BUTTON = 10010;
            public static final int STREET_VIEW_COLLECTION = 10000;
            public static final int STREET_VIEW_NO_KEYBOARD = 10003;
            public static final int STREET_VIEW_PANO_IN_COLLECTION = 10001;
            public static final int STREET_VIEW_PANO_IN_SEARCH_RESULTS = 10002;
            public static final int STREET_VIEW_PANO_NAV_SESSION = 10009;
            public static final int STREET_VIEW_SEARCH_DISMISS = 10007;
            public static final int STREET_VIEW_SEARCH_EDIT = 10008;
            public static final int STREET_VIEW_SEARCH_NO_PANOS = 10011;
            public static final int STREET_VIEW_SEARCH_NO_RESULTS = 10006;
            public static final int STREET_VIEW_SEARCH_RESULTS = 10005;
            public static final int STREET_VIEW_SEARCH_START = 10004;
            public static final int SWITCH_HEAD_MOUNT = 6;
            public static final int TRANSITION_HEAD_MOUNT_INSERTED = 2001;
            public static final int TRANSITION_HEAD_MOUNT_SWITCH = 2002;
            public static final int UNKNOWN_EVENT_TYPE = 0;
            public static final int VRCORE_COMMON_CONTINUE_VRMODE = 7002;
            public static final int VRCORE_COMMON_DISABLE_VRMODE = 7001;
            public static final int VRCORE_COMMON_ENABLE_VRMODE = 7000;
            public static final int VRCORE_COMMON_ERROR = 7049;
            public static final int VRCORE_COMMON_PERMISSION_DENIED = 7204;
            public static final int VRCORE_COMMON_PERMISSION_GRANTED = 7203;
            public static final int VRCORE_CONTROLLER_CONNECTED = 7050;
            public static final int VRCORE_CONTROLLER_EMULATOR_CONNECTED = 7065;
            public static final int VRCORE_CONTROLLER_EMULATOR_ERROR = 7066;
            public static final int VRCORE_CONTROLLER_ERROR = 7051;
            public static final int VRCORE_CONTROLLER_OTA_DATA_TRANSFER_SUCCESS = 7056;
            public static final int VRCORE_CONTROLLER_OTA_ERROR = 7052;
            public static final int VRCORE_CONTROLLER_OTA_STARTED = 7053;
            public static final int VRCORE_CONTROLLER_OTA_SUCCESS = 7054;
            public static final int VRCORE_CONTROLLER_OTA_SUCCESS_TRIVIAL = 7055;
            public static final int VRCORE_CONTROLLER_PAIRING_ERROR = 7057;
            public static final int VRCORE_CONTROLLER_PAIRING_STARTED = 7058;
            public static final int VRCORE_CONTROLLER_PAIRING_SUCCESS = 7059;
            public static final int VRCORE_CONTROLLER_RAIL_REPORT = 7067;
            public static final int VRCORE_CONTROLLER_RECALIBRATED = 7060;
            public static final int VRCORE_CONTROLLER_RECALIBRATION_ERROR = 7061;
            public static final int VRCORE_CONTROLLER_SLEPT_END = 7062;
            public static final int VRCORE_CONTROLLER_SLEPT_IDLE = 7063;
            public static final int VRCORE_CONTROLLER_STUCK_EXITED_VR = 7069;
            public static final int VRCORE_CONTROLLER_STUCK_NOTIFICATION_SHOWN = 7068;
            public static final int VRCORE_CONTROLLER_STUCK_NOTIFICATION_TAPPED = 7070;
            public static final int VRCORE_CONTROLLER_VOLUME_ADJUSTED = 7064;
            public static final int VRCORE_DAYDREAM_DON_ERROR = 7252;
            public static final int VRCORE_DAYDREAM_DON_STARTED = 7250;
            public static final int VRCORE_DAYDREAM_DON_SUCCESS = 7251;
            public static final int VRCORE_DAYDREAM_HOME_LAUNCHED = 7255;
            public static final int VRCORE_DAYDREAM_METAWORLD_DISMISSED = 7257;
            public static final int VRCORE_DAYDREAM_METAWORLD_STARTED = 7256;
            public static final int VRCORE_DAYDREAM_SESSION_ENDED = 7254;
            public static final int VRCORE_DAYDREAM_SESSION_STARTED = 7253;
            public static final int VRCORE_DAYDREAM_SYSTEM_UPDATE_ACCEPTED = 7258;
            public static final int VRCORE_DAYDREAM_SYSTEM_UPDATE_DECLINED = 7259;
            public static final int VRCORE_NFC_ERROR = 7149;
            public static final int VRCORE_NFC_TRIGGER_INTENT = 7100;
            public static final int VRCORE_NOTIFICATION_ERROR = 7199;
            public static final int VRCORE_NOTIFICATION_POSTED = 7150;
            public static final int VRCORE_NOTIFICATION_REMOVED = 7151;
            public static final int VRCORE_PERFORMANCE_REPORT = 7999;
            public static final int VRCORE_SETTINGS_DISABLE_NOTIFICATION = 7202;
            public static final int VRCORE_SETTINGS_ENABLE_NOTIFICATION = 7201;
            public static final int VRCORE_SETTINGS_ERROR = 7249;
            public static final int VRCORE_SETTINGS_LAUNCHED = 7200;
            public static final int VRHOME_SETUP_STEP_END = 12001;
            public static final int VRHOME_SETUP_STEP_START = 12000;
            public static final int VRHOME_SETUP_STEP_STATE_CHANGE = 12002;
        }

        public static final class HeadMount extends ExtendableMessageNano<HeadMount> implements Cloneable {
            private static volatile HeadMount[] _emptyArray;
            public String model;
            public String vendor;

            public HeadMount() {
                clear();
            }

            public static HeadMount[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new HeadMount[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static HeadMount parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new HeadMount().mergeFrom(codedInputByteBufferNano);
            }

            public static HeadMount parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (HeadMount) MessageNano.mergeFrom(new HeadMount(), bArr);
            }

            public final HeadMount clear() {
                this.vendor = null;
                this.model = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final HeadMount clone() {
                try {
                    return (HeadMount) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.vendor != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.vendor);
                }
                return this.model == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(2, this.model);
            }

            public final HeadMount mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            this.vendor = codedInputByteBufferNano.readString();
                            break;
                        case 18:
                            this.model = codedInputByteBufferNano.readString();
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.vendor != null) {
                    codedOutputByteBufferNano.writeString(1, this.vendor);
                }
                if (this.model != null) {
                    codedOutputByteBufferNano.writeString(2, this.model);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class HistogramBucket extends ExtendableMessageNano<HistogramBucket> implements Cloneable {
            private static volatile HistogramBucket[] _emptyArray;
            public Integer count;
            public Integer minimumValue;

            public HistogramBucket() {
                clear();
            }

            public static HistogramBucket[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new HistogramBucket[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static HistogramBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new HistogramBucket().mergeFrom(codedInputByteBufferNano);
            }

            public static HistogramBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (HistogramBucket) MessageNano.mergeFrom(new HistogramBucket(), bArr);
            }

            public final HistogramBucket clear() {
                this.minimumValue = null;
                this.count = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final HistogramBucket clone() {
                try {
                    return (HistogramBucket) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.minimumValue != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.minimumValue.intValue());
                }
                return this.count == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, this.count.intValue());
            }

            public final HistogramBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 8:
                            this.minimumValue = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 16:
                            this.count = Integer.valueOf(codedInputByteBufferNano.readInt32());
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.minimumValue != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.minimumValue.intValue());
                }
                if (this.count != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.count.intValue());
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class Keyboard extends ExtendableMessageNano<Keyboard> implements Cloneable {
            private static volatile Keyboard[] _emptyArray;
            public KeyboardEvent[] keyboardEvents;

            public static final class KeyboardEvent extends ExtendableMessageNano<KeyboardEvent> implements Cloneable {
                private static volatile KeyboardEvent[] _emptyArray;
                public Long clientTimestamp;
                public String[] enabledLanguages;
                public Integer eventType;
                public Integer inputType;
                public Application keyboardService;
                public String language;
                public String layout;
                public String[] systemLanguages;
                public KeyboardTextEntry textEntry;

                public KeyboardEvent() {
                    clear();
                }

                public static KeyboardEvent[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new KeyboardEvent[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static KeyboardEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new KeyboardEvent().mergeFrom(codedInputByteBufferNano);
                }

                public static KeyboardEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (KeyboardEvent) MessageNano.mergeFrom(new KeyboardEvent(), bArr);
                }

                public final KeyboardEvent clear() {
                    this.clientTimestamp = null;
                    this.textEntry = null;
                    this.keyboardService = null;
                    this.systemLanguages = WireFormatNano.EMPTY_STRING_ARRAY;
                    this.enabledLanguages = WireFormatNano.EMPTY_STRING_ARRAY;
                    this.language = null;
                    this.layout = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final KeyboardEvent clone() {
                    try {
                        KeyboardEvent keyboardEvent = (KeyboardEvent) super.clone();
                        if (this.textEntry != null) {
                            keyboardEvent.textEntry = this.textEntry.clone();
                        }
                        if (this.keyboardService != null) {
                            keyboardEvent.keyboardService = this.keyboardService.clone();
                        }
                        if (this.systemLanguages != null && this.systemLanguages.length > 0) {
                            keyboardEvent.systemLanguages = (String[]) this.systemLanguages.clone();
                        }
                        if (this.enabledLanguages != null && this.enabledLanguages.length > 0) {
                            keyboardEvent.enabledLanguages = (String[]) this.enabledLanguages.clone();
                        }
                        return keyboardEvent;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.clientTimestamp != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, this.clientTimestamp.longValue());
                    }
                    if (this.eventType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.eventType.intValue());
                    }
                    if (this.textEntry != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.textEntry);
                    }
                    if (this.keyboardService != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.keyboardService);
                    }
                    if (this.systemLanguages != null && this.systemLanguages.length > 0) {
                        int i = 0;
                        int i2 = 0;
                        for (String str : this.systemLanguages) {
                            if (str != null) {
                                i2++;
                                i += CodedOutputByteBufferNano.computeStringSizeNoTag(str);
                            }
                        }
                        computeSerializedSize = computeSerializedSize + i + (i2 * 1);
                    }
                    if (this.enabledLanguages != null && this.enabledLanguages.length > 0) {
                        int i3 = 0;
                        int i4 = 0;
                        for (String str2 : this.enabledLanguages) {
                            if (str2 != null) {
                                i4++;
                                i3 += CodedOutputByteBufferNano.computeStringSizeNoTag(str2);
                            }
                        }
                        computeSerializedSize = computeSerializedSize + i3 + (i4 * 1);
                    }
                    if (this.language != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.language);
                    }
                    if (this.inputType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, this.inputType.intValue());
                    }
                    return this.layout == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(9, this.layout);
                }

                public final KeyboardEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                this.clientTimestamp = Long.valueOf(codedInputByteBufferNano.readInt64());
                                break;
                            case 16:
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
                                    case 8:
                                    case 1000:
                                    case 1001:
                                    case 2000:
                                        this.eventType = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 26:
                                if (this.textEntry == null) {
                                    this.textEntry = new KeyboardTextEntry();
                                }
                                codedInputByteBufferNano.readMessage(this.textEntry);
                                break;
                            case 34:
                                if (this.keyboardService == null) {
                                    this.keyboardService = new Application();
                                }
                                codedInputByteBufferNano.readMessage(this.keyboardService);
                                break;
                            case 42:
                                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                                int length = this.systemLanguages != null ? this.systemLanguages.length : 0;
                                String[] strArr = new String[(repeatedFieldArrayLength + length)];
                                if (length != 0) {
                                    System.arraycopy(this.systemLanguages, 0, strArr, 0, length);
                                }
                                while (length < strArr.length - 1) {
                                    strArr[length] = codedInputByteBufferNano.readString();
                                    codedInputByteBufferNano.readTag();
                                    length++;
                                }
                                strArr[length] = codedInputByteBufferNano.readString();
                                this.systemLanguages = strArr;
                                break;
                            case 50:
                                int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                                int length2 = this.enabledLanguages != null ? this.enabledLanguages.length : 0;
                                String[] strArr2 = new String[(repeatedFieldArrayLength2 + length2)];
                                if (length2 != 0) {
                                    System.arraycopy(this.enabledLanguages, 0, strArr2, 0, length2);
                                }
                                while (length2 < strArr2.length - 1) {
                                    strArr2[length2] = codedInputByteBufferNano.readString();
                                    codedInputByteBufferNano.readTag();
                                    length2++;
                                }
                                strArr2[length2] = codedInputByteBufferNano.readString();
                                this.enabledLanguages = strArr2;
                                break;
                            case 58:
                                this.language = codedInputByteBufferNano.readString();
                                break;
                            case 64:
                                int readInt322 = codedInputByteBufferNano.readInt32();
                                switch (readInt322) {
                                    case 0:
                                    case 1:
                                        this.inputType = Integer.valueOf(readInt322);
                                        break;
                                }
                            case 74:
                                this.layout = codedInputByteBufferNano.readString();
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.clientTimestamp != null) {
                        codedOutputByteBufferNano.writeInt64(1, this.clientTimestamp.longValue());
                    }
                    if (this.eventType != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.eventType.intValue());
                    }
                    if (this.textEntry != null) {
                        codedOutputByteBufferNano.writeMessage(3, this.textEntry);
                    }
                    if (this.keyboardService != null) {
                        codedOutputByteBufferNano.writeMessage(4, this.keyboardService);
                    }
                    if (this.systemLanguages != null && this.systemLanguages.length > 0) {
                        for (String str : this.systemLanguages) {
                            if (str != null) {
                                codedOutputByteBufferNano.writeString(5, str);
                            }
                        }
                    }
                    if (this.enabledLanguages != null && this.enabledLanguages.length > 0) {
                        for (String str2 : this.enabledLanguages) {
                            if (str2 != null) {
                                codedOutputByteBufferNano.writeString(6, str2);
                            }
                        }
                    }
                    if (this.language != null) {
                        codedOutputByteBufferNano.writeString(7, this.language);
                    }
                    if (this.inputType != null) {
                        codedOutputByteBufferNano.writeInt32(8, this.inputType.intValue());
                    }
                    if (this.layout != null) {
                        codedOutputByteBufferNano.writeString(9, this.layout);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public interface KeyboardEventType {
                public static final int KEYBOARD_ERROR_NOT_INSTALLED = 1000;
                public static final int KEYBOARD_ERROR_NO_LOCALES = 1001;
                public static final int KEYBOARD_EVENT_CONNECTED = 2000;
                public static final int KEYBOARD_EVENT_UNKNOWN = 0;
                public static final int KEYBOARD_USER_EVENT_CHANGE_LANGUAGE = 7;
                public static final int KEYBOARD_USER_EVENT_CHANGE_LAYOUT = 8;
                public static final int KEYBOARD_USER_EVENT_COMMIT = 3;
                public static final int KEYBOARD_USER_EVENT_DELETE = 4;
                public static final int KEYBOARD_USER_EVENT_DISMISS = 6;
                public static final int KEYBOARD_USER_EVENT_HIDE = 2;
                public static final int KEYBOARD_USER_EVENT_RETURN = 5;
                public static final int KEYBOARD_USER_EVENT_SHOW = 1;
            }

            public interface KeyboardInputType {
                public static final int KEYBOARD_INPUT_TYPE_DEFAULT = 1;
                public static final int KEYBOARD_INPUT_TYPE_UNKNOWN = 0;
            }

            public static final class KeyboardTextEntry extends ExtendableMessageNano<KeyboardTextEntry> implements Cloneable {
                private static volatile KeyboardTextEntry[] _emptyArray;
                public String language;
                public String layout;
                public Integer length;
                public Integer type;

                public KeyboardTextEntry() {
                    clear();
                }

                public static KeyboardTextEntry[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new KeyboardTextEntry[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static KeyboardTextEntry parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new KeyboardTextEntry().mergeFrom(codedInputByteBufferNano);
                }

                public static KeyboardTextEntry parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (KeyboardTextEntry) MessageNano.mergeFrom(new KeyboardTextEntry(), bArr);
                }

                public final KeyboardTextEntry clear() {
                    this.length = null;
                    this.layout = null;
                    this.language = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final KeyboardTextEntry clone() {
                    try {
                        return (KeyboardTextEntry) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.type != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type.intValue());
                    }
                    if (this.length != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.length.intValue());
                    }
                    if (this.layout != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.layout);
                    }
                    return this.language == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(4, this.language);
                }

                public final KeyboardTextEntry mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                int readInt32 = codedInputByteBufferNano.readInt32();
                                switch (readInt32) {
                                    case 0:
                                    case 4:
                                    case 5:
                                        this.type = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                this.length = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 26:
                                this.layout = codedInputByteBufferNano.readString();
                                break;
                            case 34:
                                this.language = codedInputByteBufferNano.readString();
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.type != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.type.intValue());
                    }
                    if (this.length != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.length.intValue());
                    }
                    if (this.layout != null) {
                        codedOutputByteBufferNano.writeString(3, this.layout);
                    }
                    if (this.language != null) {
                        codedOutputByteBufferNano.writeString(4, this.language);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public interface KeyboardTextType {
                public static final int TEXT_SPACE = 4;
                public static final int TEXT_SUGGESTION = 5;
                public static final int TEXT_UNKNOWN = 0;
            }

            public Keyboard() {
                clear();
            }

            public static Keyboard[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Keyboard[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Keyboard parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Keyboard().mergeFrom(codedInputByteBufferNano);
            }

            public static Keyboard parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Keyboard) MessageNano.mergeFrom(new Keyboard(), bArr);
            }

            public final Keyboard clear() {
                this.keyboardEvents = KeyboardEvent.emptyArray();
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Keyboard clone() {
                try {
                    Keyboard keyboard = (Keyboard) super.clone();
                    if (this.keyboardEvents != null && this.keyboardEvents.length > 0) {
                        keyboard.keyboardEvents = new KeyboardEvent[this.keyboardEvents.length];
                        for (int i = 0; i < this.keyboardEvents.length; i++) {
                            if (this.keyboardEvents[i] != null) {
                                keyboard.keyboardEvents[i] = this.keyboardEvents[i].clone();
                            }
                        }
                    }
                    return keyboard;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.keyboardEvents != null && this.keyboardEvents.length > 0) {
                    for (KeyboardEvent keyboardEvent : this.keyboardEvents) {
                        if (keyboardEvent != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, keyboardEvent);
                        }
                    }
                }
                return computeSerializedSize;
            }

            public final Keyboard mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 18:
                            int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                            int length = this.keyboardEvents != null ? this.keyboardEvents.length : 0;
                            KeyboardEvent[] keyboardEventArr = new KeyboardEvent[(repeatedFieldArrayLength + length)];
                            if (length != 0) {
                                System.arraycopy(this.keyboardEvents, 0, keyboardEventArr, 0, length);
                            }
                            while (length < keyboardEventArr.length - 1) {
                                keyboardEventArr[length] = new KeyboardEvent();
                                codedInputByteBufferNano.readMessage(keyboardEventArr[length]);
                                codedInputByteBufferNano.readTag();
                                length++;
                            }
                            keyboardEventArr[length] = new KeyboardEvent();
                            codedInputByteBufferNano.readMessage(keyboardEventArr[length]);
                            this.keyboardEvents = keyboardEventArr;
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.keyboardEvents != null && this.keyboardEvents.length > 0) {
                    for (KeyboardEvent keyboardEvent : this.keyboardEvents) {
                        if (keyboardEvent != null) {
                            codedOutputByteBufferNano.writeMessage(2, keyboardEvent);
                        }
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class Launcher extends ExtendableMessageNano<Launcher> implements Cloneable {
            private static volatile Launcher[] _emptyArray;
            public Integer navItem;

            public interface NavItem {
                public static final int DISCOVERY = 6;
                public static final int FEEDBACK = 2;
                public static final int HELP = 3;
                public static final int LIBRARY = 7;
                public static final int SETTINGS = 1;
                public static final int UNKNOWN = 0;
                public static final int WISHLIST = 8;
            }

            public Launcher() {
                clear();
            }

            public static Launcher[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Launcher[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Launcher parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Launcher().mergeFrom(codedInputByteBufferNano);
            }

            public static Launcher parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Launcher) MessageNano.mergeFrom(new Launcher(), bArr);
            }

            public final Launcher clear() {
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Launcher clone() {
                try {
                    return (Launcher) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                return this.navItem == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, this.navItem.intValue());
            }

            public final Launcher mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                case 2:
                                case 3:
                                case 6:
                                case 7:
                                case 8:
                                    this.navItem = Integer.valueOf(readInt32);
                                    break;
                            }
                        default:
                            if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                                break;
                            } else {
                                return this;
                            }
                    }
                }
            }

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.navItem != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.navItem.intValue());
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class Lullaby extends ExtendableMessageNano<Lullaby> implements Cloneable {
            private static volatile Lullaby[] _emptyArray;
            public String contentId;
            public Integer index;
            public LoadTime loadTime;
            public Integer uiElement;

            public static final class LoadTime extends ExtendableMessageNano<LoadTime> implements Cloneable {
                private static volatile LoadTime[] _emptyArray;
                public Integer assetType;
                public Long loadTimeMs;

                public interface AssetType {
                    public static final int PAGE = 1;
                    public static final int SOUND_OGG = 4;
                    public static final int SOUND_WAV = 3;
                    public static final int TEXT = 2;
                    public static final int TEXTURE_FIFE_JPEG = 9;
                    public static final int TEXTURE_FIFE_PNG = 8;
                    public static final int TEXTURE_FIFE_WEBP = 10;
                    public static final int TEXTURE_JPEG = 6;
                    public static final int TEXTURE_PNG = 5;
                    public static final int TEXTURE_WEBP = 7;
                    public static final int UNKNOWN = 0;
                }

                public LoadTime() {
                    clear();
                }

                public static LoadTime[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new LoadTime[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static LoadTime parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new LoadTime().mergeFrom(codedInputByteBufferNano);
                }

                public static LoadTime parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (LoadTime) MessageNano.mergeFrom(new LoadTime(), bArr);
                }

                public final LoadTime clear() {
                    this.loadTimeMs = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final LoadTime clone() {
                    try {
                        return (LoadTime) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.assetType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.assetType.intValue());
                    }
                    return this.loadTimeMs == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(2, this.loadTimeMs.longValue());
                }

                public final LoadTime mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                    case 10:
                                        this.assetType = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                this.loadTimeMs = Long.valueOf(codedInputByteBufferNano.readInt64());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.assetType != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.assetType.intValue());
                    }
                    if (this.loadTimeMs != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.loadTimeMs.longValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public interface UiElement {
                public static final int BACK_BUTTON = 1;
                public static final int LAUNCHER_DISCOVERY_WINDOW = 1001;
                public static final int LAUNCHER_LIBRARY = 1006;
                public static final int LAUNCHER_LIBRARY_APP_CARD = 1007;
                public static final int LAUNCHER_LIBRARY_BUTTON = 1004;
                public static final int LAUNCHER_MAIN = 1000;
                public static final int LAUNCHER_PLAY_STORE_BUTTON = 1005;
                public static final int LAUNCHER_RECENT_APP_CARD = 1002;
                public static final int LAUNCHER_SETTINGS_BUTTON = 1003;
                public static final int PLAY_STORE_COLLECTION_APP_CARD = 2008;
                public static final int PLAY_STORE_COLLECTION_PAGE = 2007;
                public static final int PLAY_STORE_DETAILS = 2009;
                public static final int PLAY_STORE_DETAILS_BUY_BUTTON = 2014;
                public static final int PLAY_STORE_DETAILS_INSTALL_BUTTON = 2013;
                public static final int PLAY_STORE_DETAILS_MORE_DETAILS_BUTTON = 2011;
                public static final int PLAY_STORE_DETAILS_NO_DAYDREAM = 2016;
                public static final int PLAY_STORE_DETAILS_OPEN_BUTTON = 2012;
                public static final int PLAY_STORE_DETAILS_PANO = 2010;
                public static final int PLAY_STORE_DETAILS_UPDATE_BUTTON = 2015;
                public static final int PLAY_STORE_MAIN = 2000;
                public static final int PLAY_STORE_MAIN_COLLECTION = 2003;
                public static final int PLAY_STORE_MAIN_COLLECTION_APP_CARD = 2005;
                public static final int PLAY_STORE_MAIN_COLLECTION_TITLE_CARD = 2004;
                public static final int PLAY_STORE_MAIN_FEATURED_ACTIVE_CARD = 2002;
                public static final int PLAY_STORE_MAIN_FEATURED_CAROUSEL_CARD = 2001;
                public static final int PLAY_STORE_MAIN_FEATURED_COLLECTION = 2006;
                public static final int PLAY_STORE_MORE_DETAILS = 2017;
                public static final int PLAY_STORE_NETWORK_ERROR = 2020;
                public static final int PLAY_STORE_NETWORK_ERROR_RETRY_BUTTON = 2021;
                public static final int PLAY_STORE_TOS_DIALOG = 2018;
                public static final int PLAY_STORE_TOS_DIALOG_EXIT_VR_BUTTON = 2019;
                public static final int UNKNOWN = 0;
            }

            public Lullaby() {
                clear();
            }

            public static Lullaby[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Lullaby[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Lullaby parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Lullaby().mergeFrom(codedInputByteBufferNano);
            }

            public static Lullaby parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Lullaby) MessageNano.mergeFrom(new Lullaby(), bArr);
            }

            public final Lullaby clear() {
                this.index = null;
                this.contentId = null;
                this.loadTime = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Lullaby clone() {
                try {
                    Lullaby lullaby = (Lullaby) super.clone();
                    if (this.loadTime != null) {
                        lullaby.loadTime = this.loadTime.clone();
                    }
                    return lullaby;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.uiElement != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.uiElement.intValue());
                }
                if (this.index != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.index.intValue());
                }
                if (this.contentId != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.contentId);
                }
                return this.loadTime == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(4, this.loadTime);
            }

            public final Lullaby mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                case 1000:
                                case 1001:
                                case 1002:
                                case 1003:
                                case 1004:
                                case 1005:
                                case 1006:
                                case 1007:
                                case 2000:
                                case 2001:
                                case 2002:
                                case 2003:
                                case 2004:
                                case 2005:
                                case 2006:
                                case 2007:
                                case 2008:
                                case 2009:
                                case UiElement.PLAY_STORE_DETAILS_PANO /*2010*/:
                                case UiElement.PLAY_STORE_DETAILS_MORE_DETAILS_BUTTON /*2011*/:
                                case UiElement.PLAY_STORE_DETAILS_OPEN_BUTTON /*2012*/:
                                case UiElement.PLAY_STORE_DETAILS_INSTALL_BUTTON /*2013*/:
                                case UiElement.PLAY_STORE_DETAILS_BUY_BUTTON /*2014*/:
                                case UiElement.PLAY_STORE_DETAILS_UPDATE_BUTTON /*2015*/:
                                case UiElement.PLAY_STORE_DETAILS_NO_DAYDREAM /*2016*/:
                                case UiElement.PLAY_STORE_MORE_DETAILS /*2017*/:
                                case UiElement.PLAY_STORE_TOS_DIALOG /*2018*/:
                                case UiElement.PLAY_STORE_TOS_DIALOG_EXIT_VR_BUTTON /*2019*/:
                                case UiElement.PLAY_STORE_NETWORK_ERROR /*2020*/:
                                case UiElement.PLAY_STORE_NETWORK_ERROR_RETRY_BUTTON /*2021*/:
                                    this.uiElement = Integer.valueOf(readInt32);
                                    break;
                            }
                        case 16:
                            this.index = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 26:
                            this.contentId = codedInputByteBufferNano.readString();
                            break;
                        case 34:
                            if (this.loadTime == null) {
                                this.loadTime = new LoadTime();
                            }
                            codedInputByteBufferNano.readMessage(this.loadTime);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.uiElement != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.uiElement.intValue());
                }
                if (this.index != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.index.intValue());
                }
                if (this.contentId != null) {
                    codedOutputByteBufferNano.writeString(3, this.contentId);
                }
                if (this.loadTime != null) {
                    codedOutputByteBufferNano.writeMessage(4, this.loadTime);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class PerformanceStats extends ExtendableMessageNano<PerformanceStats> implements Cloneable {
            private static volatile PerformanceStats[] _emptyArray;
            public HistogramBucket[] appRenderTime;
            public Integer averageFps;
            public float[] batteryShutdownTemperature;
            public float[] batteryThrottlingTemperature;
            public HistogramBucket[] consecutiveDroppedFrames;
            public float[] cpuShutdownTemperature;
            public float[] cpuThrottlingTemperature;
            public HistogramBucket[] frameTime;
            public float[] gpuShutdownTemperature;
            public float[] gpuThrottlingTemperature;
            public Integer memoryConsumptionKilobytes;
            public HistogramBucket[] postFrameTime;
            public HistogramBucket[] presentTime;
            public HistogramBucket[] scanlineRacingVsyncOvershootUs;
            public Float shutdownSkinTemperatureCelsius;
            public Integer thermalExitFlowShown;
            public Float throttleSkinTemperatureCelsius;
            public TimeSeriesData timeSeriesData;
            public HistogramBucket[] totalRenderTime;
            public Float vrMaxSkinTemperatureCelsius;

            public PerformanceStats() {
                clear();
            }

            public static PerformanceStats[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new PerformanceStats[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static PerformanceStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new PerformanceStats().mergeFrom(codedInputByteBufferNano);
            }

            public static PerformanceStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (PerformanceStats) MessageNano.mergeFrom(new PerformanceStats(), bArr);
            }

            public final PerformanceStats clear() {
                this.averageFps = null;
                this.frameTime = HistogramBucket.emptyArray();
                this.memoryConsumptionKilobytes = null;
                this.throttleSkinTemperatureCelsius = null;
                this.vrMaxSkinTemperatureCelsius = null;
                this.shutdownSkinTemperatureCelsius = null;
                this.timeSeriesData = null;
                this.appRenderTime = HistogramBucket.emptyArray();
                this.presentTime = HistogramBucket.emptyArray();
                this.totalRenderTime = HistogramBucket.emptyArray();
                this.postFrameTime = HistogramBucket.emptyArray();
                this.consecutiveDroppedFrames = HistogramBucket.emptyArray();
                this.scanlineRacingVsyncOvershootUs = HistogramBucket.emptyArray();
                this.thermalExitFlowShown = null;
                this.cpuThrottlingTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.gpuThrottlingTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.batteryThrottlingTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.cpuShutdownTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.gpuShutdownTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.batteryShutdownTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final PerformanceStats clone() {
                try {
                    PerformanceStats performanceStats = (PerformanceStats) super.clone();
                    if (this.frameTime != null && this.frameTime.length > 0) {
                        performanceStats.frameTime = new HistogramBucket[this.frameTime.length];
                        for (int i = 0; i < this.frameTime.length; i++) {
                            if (this.frameTime[i] != null) {
                                performanceStats.frameTime[i] = this.frameTime[i].clone();
                            }
                        }
                    }
                    if (this.timeSeriesData != null) {
                        performanceStats.timeSeriesData = this.timeSeriesData.clone();
                    }
                    if (this.appRenderTime != null && this.appRenderTime.length > 0) {
                        performanceStats.appRenderTime = new HistogramBucket[this.appRenderTime.length];
                        for (int i2 = 0; i2 < this.appRenderTime.length; i2++) {
                            if (this.appRenderTime[i2] != null) {
                                performanceStats.appRenderTime[i2] = this.appRenderTime[i2].clone();
                            }
                        }
                    }
                    if (this.presentTime != null && this.presentTime.length > 0) {
                        performanceStats.presentTime = new HistogramBucket[this.presentTime.length];
                        for (int i3 = 0; i3 < this.presentTime.length; i3++) {
                            if (this.presentTime[i3] != null) {
                                performanceStats.presentTime[i3] = this.presentTime[i3].clone();
                            }
                        }
                    }
                    if (this.totalRenderTime != null && this.totalRenderTime.length > 0) {
                        performanceStats.totalRenderTime = new HistogramBucket[this.totalRenderTime.length];
                        for (int i4 = 0; i4 < this.totalRenderTime.length; i4++) {
                            if (this.totalRenderTime[i4] != null) {
                                performanceStats.totalRenderTime[i4] = this.totalRenderTime[i4].clone();
                            }
                        }
                    }
                    if (this.postFrameTime != null && this.postFrameTime.length > 0) {
                        performanceStats.postFrameTime = new HistogramBucket[this.postFrameTime.length];
                        for (int i5 = 0; i5 < this.postFrameTime.length; i5++) {
                            if (this.postFrameTime[i5] != null) {
                                performanceStats.postFrameTime[i5] = this.postFrameTime[i5].clone();
                            }
                        }
                    }
                    if (this.consecutiveDroppedFrames != null && this.consecutiveDroppedFrames.length > 0) {
                        performanceStats.consecutiveDroppedFrames = new HistogramBucket[this.consecutiveDroppedFrames.length];
                        for (int i6 = 0; i6 < this.consecutiveDroppedFrames.length; i6++) {
                            if (this.consecutiveDroppedFrames[i6] != null) {
                                performanceStats.consecutiveDroppedFrames[i6] = this.consecutiveDroppedFrames[i6].clone();
                            }
                        }
                    }
                    if (this.scanlineRacingVsyncOvershootUs != null && this.scanlineRacingVsyncOvershootUs.length > 0) {
                        performanceStats.scanlineRacingVsyncOvershootUs = new HistogramBucket[this.scanlineRacingVsyncOvershootUs.length];
                        for (int i7 = 0; i7 < this.scanlineRacingVsyncOvershootUs.length; i7++) {
                            if (this.scanlineRacingVsyncOvershootUs[i7] != null) {
                                performanceStats.scanlineRacingVsyncOvershootUs[i7] = this.scanlineRacingVsyncOvershootUs[i7].clone();
                            }
                        }
                    }
                    if (this.cpuThrottlingTemperature != null && this.cpuThrottlingTemperature.length > 0) {
                        performanceStats.cpuThrottlingTemperature = (float[]) this.cpuThrottlingTemperature.clone();
                    }
                    if (this.gpuThrottlingTemperature != null && this.gpuThrottlingTemperature.length > 0) {
                        performanceStats.gpuThrottlingTemperature = (float[]) this.gpuThrottlingTemperature.clone();
                    }
                    if (this.batteryThrottlingTemperature != null && this.batteryThrottlingTemperature.length > 0) {
                        performanceStats.batteryThrottlingTemperature = (float[]) this.batteryThrottlingTemperature.clone();
                    }
                    if (this.cpuShutdownTemperature != null && this.cpuShutdownTemperature.length > 0) {
                        performanceStats.cpuShutdownTemperature = (float[]) this.cpuShutdownTemperature.clone();
                    }
                    if (this.gpuShutdownTemperature != null && this.gpuShutdownTemperature.length > 0) {
                        performanceStats.gpuShutdownTemperature = (float[]) this.gpuShutdownTemperature.clone();
                    }
                    if (this.batteryShutdownTemperature != null && this.batteryShutdownTemperature.length > 0) {
                        performanceStats.batteryShutdownTemperature = (float[]) this.batteryShutdownTemperature.clone();
                    }
                    return performanceStats;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.averageFps != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.averageFps.intValue());
                }
                if (this.frameTime != null && this.frameTime.length > 0) {
                    int i = computeSerializedSize;
                    for (HistogramBucket histogramBucket : this.frameTime) {
                        if (histogramBucket != null) {
                            i += CodedOutputByteBufferNano.computeMessageSize(2, histogramBucket);
                        }
                    }
                    computeSerializedSize = i;
                }
                if (this.memoryConsumptionKilobytes != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.memoryConsumptionKilobytes.intValue());
                }
                if (this.throttleSkinTemperatureCelsius != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.throttleSkinTemperatureCelsius.floatValue());
                }
                if (this.vrMaxSkinTemperatureCelsius != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(5, this.vrMaxSkinTemperatureCelsius.floatValue());
                }
                if (this.shutdownSkinTemperatureCelsius != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(6, this.shutdownSkinTemperatureCelsius.floatValue());
                }
                if (this.timeSeriesData != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, this.timeSeriesData);
                }
                if (this.appRenderTime != null && this.appRenderTime.length > 0) {
                    int i2 = computeSerializedSize;
                    for (HistogramBucket histogramBucket2 : this.appRenderTime) {
                        if (histogramBucket2 != null) {
                            i2 += CodedOutputByteBufferNano.computeMessageSize(8, histogramBucket2);
                        }
                    }
                    computeSerializedSize = i2;
                }
                if (this.presentTime != null && this.presentTime.length > 0) {
                    int i3 = computeSerializedSize;
                    for (HistogramBucket histogramBucket3 : this.presentTime) {
                        if (histogramBucket3 != null) {
                            i3 += CodedOutputByteBufferNano.computeMessageSize(9, histogramBucket3);
                        }
                    }
                    computeSerializedSize = i3;
                }
                if (this.totalRenderTime != null && this.totalRenderTime.length > 0) {
                    int i4 = computeSerializedSize;
                    for (HistogramBucket histogramBucket4 : this.totalRenderTime) {
                        if (histogramBucket4 != null) {
                            i4 += CodedOutputByteBufferNano.computeMessageSize(10, histogramBucket4);
                        }
                    }
                    computeSerializedSize = i4;
                }
                if (this.postFrameTime != null && this.postFrameTime.length > 0) {
                    int i5 = computeSerializedSize;
                    for (HistogramBucket histogramBucket5 : this.postFrameTime) {
                        if (histogramBucket5 != null) {
                            i5 += CodedOutputByteBufferNano.computeMessageSize(11, histogramBucket5);
                        }
                    }
                    computeSerializedSize = i5;
                }
                if (this.consecutiveDroppedFrames != null && this.consecutiveDroppedFrames.length > 0) {
                    int i6 = computeSerializedSize;
                    for (HistogramBucket histogramBucket6 : this.consecutiveDroppedFrames) {
                        if (histogramBucket6 != null) {
                            i6 += CodedOutputByteBufferNano.computeMessageSize(12, histogramBucket6);
                        }
                    }
                    computeSerializedSize = i6;
                }
                if (this.scanlineRacingVsyncOvershootUs != null && this.scanlineRacingVsyncOvershootUs.length > 0) {
                    for (HistogramBucket histogramBucket7 : this.scanlineRacingVsyncOvershootUs) {
                        if (histogramBucket7 != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(13, histogramBucket7);
                        }
                    }
                }
                if (this.thermalExitFlowShown != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, this.thermalExitFlowShown.intValue());
                }
                if (this.cpuThrottlingTemperature != null && this.cpuThrottlingTemperature.length > 0) {
                    computeSerializedSize = computeSerializedSize + (this.cpuThrottlingTemperature.length * 4) + (this.cpuThrottlingTemperature.length * 1);
                }
                if (this.gpuThrottlingTemperature != null && this.gpuThrottlingTemperature.length > 0) {
                    computeSerializedSize = computeSerializedSize + (this.gpuThrottlingTemperature.length * 4) + (this.gpuThrottlingTemperature.length * 2);
                }
                if (this.batteryThrottlingTemperature != null && this.batteryThrottlingTemperature.length > 0) {
                    computeSerializedSize = computeSerializedSize + (this.batteryThrottlingTemperature.length * 4) + (this.batteryThrottlingTemperature.length * 2);
                }
                if (this.cpuShutdownTemperature != null && this.cpuShutdownTemperature.length > 0) {
                    computeSerializedSize = computeSerializedSize + (this.cpuShutdownTemperature.length * 4) + (this.cpuShutdownTemperature.length * 2);
                }
                if (this.gpuShutdownTemperature != null && this.gpuShutdownTemperature.length > 0) {
                    computeSerializedSize = computeSerializedSize + (this.gpuShutdownTemperature.length * 4) + (this.gpuShutdownTemperature.length * 2);
                }
                return (this.batteryShutdownTemperature != null && this.batteryShutdownTemperature.length > 0) ? computeSerializedSize + (this.batteryShutdownTemperature.length * 4) + (this.batteryShutdownTemperature.length * 2) : computeSerializedSize;
            }

            public final PerformanceStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 8:
                            this.averageFps = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 18:
                            int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                            int length = this.frameTime != null ? this.frameTime.length : 0;
                            HistogramBucket[] histogramBucketArr = new HistogramBucket[(repeatedFieldArrayLength + length)];
                            if (length != 0) {
                                System.arraycopy(this.frameTime, 0, histogramBucketArr, 0, length);
                            }
                            while (length < histogramBucketArr.length - 1) {
                                histogramBucketArr[length] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr[length]);
                                codedInputByteBufferNano.readTag();
                                length++;
                            }
                            histogramBucketArr[length] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr[length]);
                            this.frameTime = histogramBucketArr;
                            break;
                        case 24:
                            this.memoryConsumptionKilobytes = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 37:
                            this.throttleSkinTemperatureCelsius = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 45:
                            this.vrMaxSkinTemperatureCelsius = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 53:
                            this.shutdownSkinTemperatureCelsius = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 58:
                            if (this.timeSeriesData == null) {
                                this.timeSeriesData = new TimeSeriesData();
                            }
                            codedInputByteBufferNano.readMessage(this.timeSeriesData);
                            break;
                        case 66:
                            int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 66);
                            int length2 = this.appRenderTime != null ? this.appRenderTime.length : 0;
                            HistogramBucket[] histogramBucketArr2 = new HistogramBucket[(repeatedFieldArrayLength2 + length2)];
                            if (length2 != 0) {
                                System.arraycopy(this.appRenderTime, 0, histogramBucketArr2, 0, length2);
                            }
                            while (length2 < histogramBucketArr2.length - 1) {
                                histogramBucketArr2[length2] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr2[length2]);
                                codedInputByteBufferNano.readTag();
                                length2++;
                            }
                            histogramBucketArr2[length2] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr2[length2]);
                            this.appRenderTime = histogramBucketArr2;
                            break;
                        case 74:
                            int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 74);
                            int length3 = this.presentTime != null ? this.presentTime.length : 0;
                            HistogramBucket[] histogramBucketArr3 = new HistogramBucket[(repeatedFieldArrayLength3 + length3)];
                            if (length3 != 0) {
                                System.arraycopy(this.presentTime, 0, histogramBucketArr3, 0, length3);
                            }
                            while (length3 < histogramBucketArr3.length - 1) {
                                histogramBucketArr3[length3] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr3[length3]);
                                codedInputByteBufferNano.readTag();
                                length3++;
                            }
                            histogramBucketArr3[length3] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr3[length3]);
                            this.presentTime = histogramBucketArr3;
                            break;
                        case 82:
                            int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 82);
                            int length4 = this.totalRenderTime != null ? this.totalRenderTime.length : 0;
                            HistogramBucket[] histogramBucketArr4 = new HistogramBucket[(repeatedFieldArrayLength4 + length4)];
                            if (length4 != 0) {
                                System.arraycopy(this.totalRenderTime, 0, histogramBucketArr4, 0, length4);
                            }
                            while (length4 < histogramBucketArr4.length - 1) {
                                histogramBucketArr4[length4] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr4[length4]);
                                codedInputByteBufferNano.readTag();
                                length4++;
                            }
                            histogramBucketArr4[length4] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr4[length4]);
                            this.totalRenderTime = histogramBucketArr4;
                            break;
                        case 90:
                            int repeatedFieldArrayLength5 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 90);
                            int length5 = this.postFrameTime != null ? this.postFrameTime.length : 0;
                            HistogramBucket[] histogramBucketArr5 = new HistogramBucket[(repeatedFieldArrayLength5 + length5)];
                            if (length5 != 0) {
                                System.arraycopy(this.postFrameTime, 0, histogramBucketArr5, 0, length5);
                            }
                            while (length5 < histogramBucketArr5.length - 1) {
                                histogramBucketArr5[length5] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr5[length5]);
                                codedInputByteBufferNano.readTag();
                                length5++;
                            }
                            histogramBucketArr5[length5] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr5[length5]);
                            this.postFrameTime = histogramBucketArr5;
                            break;
                        case 98:
                            int repeatedFieldArrayLength6 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 98);
                            int length6 = this.consecutiveDroppedFrames != null ? this.consecutiveDroppedFrames.length : 0;
                            HistogramBucket[] histogramBucketArr6 = new HistogramBucket[(repeatedFieldArrayLength6 + length6)];
                            if (length6 != 0) {
                                System.arraycopy(this.consecutiveDroppedFrames, 0, histogramBucketArr6, 0, length6);
                            }
                            while (length6 < histogramBucketArr6.length - 1) {
                                histogramBucketArr6[length6] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr6[length6]);
                                codedInputByteBufferNano.readTag();
                                length6++;
                            }
                            histogramBucketArr6[length6] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr6[length6]);
                            this.consecutiveDroppedFrames = histogramBucketArr6;
                            break;
                        case 106:
                            int repeatedFieldArrayLength7 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 106);
                            int length7 = this.scanlineRacingVsyncOvershootUs != null ? this.scanlineRacingVsyncOvershootUs.length : 0;
                            HistogramBucket[] histogramBucketArr7 = new HistogramBucket[(repeatedFieldArrayLength7 + length7)];
                            if (length7 != 0) {
                                System.arraycopy(this.scanlineRacingVsyncOvershootUs, 0, histogramBucketArr7, 0, length7);
                            }
                            while (length7 < histogramBucketArr7.length - 1) {
                                histogramBucketArr7[length7] = new HistogramBucket();
                                codedInputByteBufferNano.readMessage(histogramBucketArr7[length7]);
                                codedInputByteBufferNano.readTag();
                                length7++;
                            }
                            histogramBucketArr7[length7] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr7[length7]);
                            this.scanlineRacingVsyncOvershootUs = histogramBucketArr7;
                            break;
                        case 112:
                            this.thermalExitFlowShown = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case VrCore.ErrorCode.CONTROLLER_GATT_SERVICE_NOT_FOUND /*122*/:
                            int readRawVarint32 = codedInputByteBufferNano.readRawVarint32();
                            int pushLimit = codedInputByteBufferNano.pushLimit(readRawVarint32);
                            int i = readRawVarint32 / 4;
                            int length8 = this.cpuThrottlingTemperature != null ? this.cpuThrottlingTemperature.length : 0;
                            float[] fArr = new float[(i + length8)];
                            if (length8 != 0) {
                                System.arraycopy(this.cpuThrottlingTemperature, 0, fArr, 0, length8);
                            }
                            while (length8 < fArr.length) {
                                fArr[length8] = codedInputByteBufferNano.readFloat();
                                length8++;
                            }
                            this.cpuThrottlingTemperature = fArr;
                            codedInputByteBufferNano.popLimit(pushLimit);
                            break;
                        case VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED /*125*/:
                            int repeatedFieldArrayLength8 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED);
                            int length9 = this.cpuThrottlingTemperature != null ? this.cpuThrottlingTemperature.length : 0;
                            float[] fArr2 = new float[(repeatedFieldArrayLength8 + length9)];
                            if (length9 != 0) {
                                System.arraycopy(this.cpuThrottlingTemperature, 0, fArr2, 0, length9);
                            }
                            while (length9 < fArr2.length - 1) {
                                fArr2[length9] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                length9++;
                            }
                            fArr2[length9] = codedInputByteBufferNano.readFloat();
                            this.cpuThrottlingTemperature = fArr2;
                            break;
                        case 130:
                            int readRawVarint322 = codedInputByteBufferNano.readRawVarint32();
                            int pushLimit2 = codedInputByteBufferNano.pushLimit(readRawVarint322);
                            int i2 = readRawVarint322 / 4;
                            int length10 = this.gpuThrottlingTemperature != null ? this.gpuThrottlingTemperature.length : 0;
                            float[] fArr3 = new float[(i2 + length10)];
                            if (length10 != 0) {
                                System.arraycopy(this.gpuThrottlingTemperature, 0, fArr3, 0, length10);
                            }
                            while (length10 < fArr3.length) {
                                fArr3[length10] = codedInputByteBufferNano.readFloat();
                                length10++;
                            }
                            this.gpuThrottlingTemperature = fArr3;
                            codedInputByteBufferNano.popLimit(pushLimit2);
                            break;
                        case 133:
                            int repeatedFieldArrayLength9 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 133);
                            int length11 = this.gpuThrottlingTemperature != null ? this.gpuThrottlingTemperature.length : 0;
                            float[] fArr4 = new float[(repeatedFieldArrayLength9 + length11)];
                            if (length11 != 0) {
                                System.arraycopy(this.gpuThrottlingTemperature, 0, fArr4, 0, length11);
                            }
                            while (length11 < fArr4.length - 1) {
                                fArr4[length11] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                length11++;
                            }
                            fArr4[length11] = codedInputByteBufferNano.readFloat();
                            this.gpuThrottlingTemperature = fArr4;
                            break;
                        case 138:
                            int readRawVarint323 = codedInputByteBufferNano.readRawVarint32();
                            int pushLimit3 = codedInputByteBufferNano.pushLimit(readRawVarint323);
                            int i3 = readRawVarint323 / 4;
                            int length12 = this.batteryThrottlingTemperature != null ? this.batteryThrottlingTemperature.length : 0;
                            float[] fArr5 = new float[(i3 + length12)];
                            if (length12 != 0) {
                                System.arraycopy(this.batteryThrottlingTemperature, 0, fArr5, 0, length12);
                            }
                            while (length12 < fArr5.length) {
                                fArr5[length12] = codedInputByteBufferNano.readFloat();
                                length12++;
                            }
                            this.batteryThrottlingTemperature = fArr5;
                            codedInputByteBufferNano.popLimit(pushLimit3);
                            break;
                        case 141:
                            int repeatedFieldArrayLength10 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 141);
                            int length13 = this.batteryThrottlingTemperature != null ? this.batteryThrottlingTemperature.length : 0;
                            float[] fArr6 = new float[(repeatedFieldArrayLength10 + length13)];
                            if (length13 != 0) {
                                System.arraycopy(this.batteryThrottlingTemperature, 0, fArr6, 0, length13);
                            }
                            while (length13 < fArr6.length - 1) {
                                fArr6[length13] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                length13++;
                            }
                            fArr6[length13] = codedInputByteBufferNano.readFloat();
                            this.batteryThrottlingTemperature = fArr6;
                            break;
                        case 146:
                            int readRawVarint324 = codedInputByteBufferNano.readRawVarint32();
                            int pushLimit4 = codedInputByteBufferNano.pushLimit(readRawVarint324);
                            int i4 = readRawVarint324 / 4;
                            int length14 = this.cpuShutdownTemperature != null ? this.cpuShutdownTemperature.length : 0;
                            float[] fArr7 = new float[(i4 + length14)];
                            if (length14 != 0) {
                                System.arraycopy(this.cpuShutdownTemperature, 0, fArr7, 0, length14);
                            }
                            while (length14 < fArr7.length) {
                                fArr7[length14] = codedInputByteBufferNano.readFloat();
                                length14++;
                            }
                            this.cpuShutdownTemperature = fArr7;
                            codedInputByteBufferNano.popLimit(pushLimit4);
                            break;
                        case 149:
                            int repeatedFieldArrayLength11 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 149);
                            int length15 = this.cpuShutdownTemperature != null ? this.cpuShutdownTemperature.length : 0;
                            float[] fArr8 = new float[(repeatedFieldArrayLength11 + length15)];
                            if (length15 != 0) {
                                System.arraycopy(this.cpuShutdownTemperature, 0, fArr8, 0, length15);
                            }
                            while (length15 < fArr8.length - 1) {
                                fArr8[length15] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                length15++;
                            }
                            fArr8[length15] = codedInputByteBufferNano.readFloat();
                            this.cpuShutdownTemperature = fArr8;
                            break;
                        case 154:
                            int readRawVarint325 = codedInputByteBufferNano.readRawVarint32();
                            int pushLimit5 = codedInputByteBufferNano.pushLimit(readRawVarint325);
                            int i5 = readRawVarint325 / 4;
                            int length16 = this.gpuShutdownTemperature != null ? this.gpuShutdownTemperature.length : 0;
                            float[] fArr9 = new float[(i5 + length16)];
                            if (length16 != 0) {
                                System.arraycopy(this.gpuShutdownTemperature, 0, fArr9, 0, length16);
                            }
                            while (length16 < fArr9.length) {
                                fArr9[length16] = codedInputByteBufferNano.readFloat();
                                length16++;
                            }
                            this.gpuShutdownTemperature = fArr9;
                            codedInputByteBufferNano.popLimit(pushLimit5);
                            break;
                        case 157:
                            int repeatedFieldArrayLength12 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 157);
                            int length17 = this.gpuShutdownTemperature != null ? this.gpuShutdownTemperature.length : 0;
                            float[] fArr10 = new float[(repeatedFieldArrayLength12 + length17)];
                            if (length17 != 0) {
                                System.arraycopy(this.gpuShutdownTemperature, 0, fArr10, 0, length17);
                            }
                            while (length17 < fArr10.length - 1) {
                                fArr10[length17] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                length17++;
                            }
                            fArr10[length17] = codedInputByteBufferNano.readFloat();
                            this.gpuShutdownTemperature = fArr10;
                            break;
                        case 162:
                            int readRawVarint326 = codedInputByteBufferNano.readRawVarint32();
                            int pushLimit6 = codedInputByteBufferNano.pushLimit(readRawVarint326);
                            int i6 = readRawVarint326 / 4;
                            int length18 = this.batteryShutdownTemperature != null ? this.batteryShutdownTemperature.length : 0;
                            float[] fArr11 = new float[(i6 + length18)];
                            if (length18 != 0) {
                                System.arraycopy(this.batteryShutdownTemperature, 0, fArr11, 0, length18);
                            }
                            while (length18 < fArr11.length) {
                                fArr11[length18] = codedInputByteBufferNano.readFloat();
                                length18++;
                            }
                            this.batteryShutdownTemperature = fArr11;
                            codedInputByteBufferNano.popLimit(pushLimit6);
                            break;
                        case 165:
                            int repeatedFieldArrayLength13 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 165);
                            int length19 = this.batteryShutdownTemperature != null ? this.batteryShutdownTemperature.length : 0;
                            float[] fArr12 = new float[(repeatedFieldArrayLength13 + length19)];
                            if (length19 != 0) {
                                System.arraycopy(this.batteryShutdownTemperature, 0, fArr12, 0, length19);
                            }
                            while (length19 < fArr12.length - 1) {
                                fArr12[length19] = codedInputByteBufferNano.readFloat();
                                codedInputByteBufferNano.readTag();
                                length19++;
                            }
                            fArr12[length19] = codedInputByteBufferNano.readFloat();
                            this.batteryShutdownTemperature = fArr12;
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.averageFps != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.averageFps.intValue());
                }
                if (this.frameTime != null && this.frameTime.length > 0) {
                    for (HistogramBucket histogramBucket : this.frameTime) {
                        if (histogramBucket != null) {
                            codedOutputByteBufferNano.writeMessage(2, histogramBucket);
                        }
                    }
                }
                if (this.memoryConsumptionKilobytes != null) {
                    codedOutputByteBufferNano.writeInt32(3, this.memoryConsumptionKilobytes.intValue());
                }
                if (this.throttleSkinTemperatureCelsius != null) {
                    codedOutputByteBufferNano.writeFloat(4, this.throttleSkinTemperatureCelsius.floatValue());
                }
                if (this.vrMaxSkinTemperatureCelsius != null) {
                    codedOutputByteBufferNano.writeFloat(5, this.vrMaxSkinTemperatureCelsius.floatValue());
                }
                if (this.shutdownSkinTemperatureCelsius != null) {
                    codedOutputByteBufferNano.writeFloat(6, this.shutdownSkinTemperatureCelsius.floatValue());
                }
                if (this.timeSeriesData != null) {
                    codedOutputByteBufferNano.writeMessage(7, this.timeSeriesData);
                }
                if (this.appRenderTime != null && this.appRenderTime.length > 0) {
                    for (HistogramBucket histogramBucket2 : this.appRenderTime) {
                        if (histogramBucket2 != null) {
                            codedOutputByteBufferNano.writeMessage(8, histogramBucket2);
                        }
                    }
                }
                if (this.presentTime != null && this.presentTime.length > 0) {
                    for (HistogramBucket histogramBucket3 : this.presentTime) {
                        if (histogramBucket3 != null) {
                            codedOutputByteBufferNano.writeMessage(9, histogramBucket3);
                        }
                    }
                }
                if (this.totalRenderTime != null && this.totalRenderTime.length > 0) {
                    for (HistogramBucket histogramBucket4 : this.totalRenderTime) {
                        if (histogramBucket4 != null) {
                            codedOutputByteBufferNano.writeMessage(10, histogramBucket4);
                        }
                    }
                }
                if (this.postFrameTime != null && this.postFrameTime.length > 0) {
                    for (HistogramBucket histogramBucket5 : this.postFrameTime) {
                        if (histogramBucket5 != null) {
                            codedOutputByteBufferNano.writeMessage(11, histogramBucket5);
                        }
                    }
                }
                if (this.consecutiveDroppedFrames != null && this.consecutiveDroppedFrames.length > 0) {
                    for (HistogramBucket histogramBucket6 : this.consecutiveDroppedFrames) {
                        if (histogramBucket6 != null) {
                            codedOutputByteBufferNano.writeMessage(12, histogramBucket6);
                        }
                    }
                }
                if (this.scanlineRacingVsyncOvershootUs != null && this.scanlineRacingVsyncOvershootUs.length > 0) {
                    for (HistogramBucket histogramBucket7 : this.scanlineRacingVsyncOvershootUs) {
                        if (histogramBucket7 != null) {
                            codedOutputByteBufferNano.writeMessage(13, histogramBucket7);
                        }
                    }
                }
                if (this.thermalExitFlowShown != null) {
                    codedOutputByteBufferNano.writeInt32(14, this.thermalExitFlowShown.intValue());
                }
                if (this.cpuThrottlingTemperature != null && this.cpuThrottlingTemperature.length > 0) {
                    for (float writeFloat : this.cpuThrottlingTemperature) {
                        codedOutputByteBufferNano.writeFloat(15, writeFloat);
                    }
                }
                if (this.gpuThrottlingTemperature != null && this.gpuThrottlingTemperature.length > 0) {
                    for (float writeFloat2 : this.gpuThrottlingTemperature) {
                        codedOutputByteBufferNano.writeFloat(16, writeFloat2);
                    }
                }
                if (this.batteryThrottlingTemperature != null && this.batteryThrottlingTemperature.length > 0) {
                    for (float writeFloat3 : this.batteryThrottlingTemperature) {
                        codedOutputByteBufferNano.writeFloat(17, writeFloat3);
                    }
                }
                if (this.cpuShutdownTemperature != null && this.cpuShutdownTemperature.length > 0) {
                    for (float writeFloat4 : this.cpuShutdownTemperature) {
                        codedOutputByteBufferNano.writeFloat(18, writeFloat4);
                    }
                }
                if (this.gpuShutdownTemperature != null && this.gpuShutdownTemperature.length > 0) {
                    for (float writeFloat5 : this.gpuShutdownTemperature) {
                        codedOutputByteBufferNano.writeFloat(19, writeFloat5);
                    }
                }
                if (this.batteryShutdownTemperature != null && this.batteryShutdownTemperature.length > 0) {
                    for (float writeFloat6 : this.batteryShutdownTemperature) {
                        codedOutputByteBufferNano.writeFloat(20, writeFloat6);
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class Photos extends ExtendableMessageNano<Photos> implements Cloneable {
            private static volatile Photos[] _emptyArray;
            public Integer numPhotos;
            public OpenMedia openMedia;
            public WarmWelcome warmWelcome;

            public static final class OpenMedia extends ExtendableMessageNano<OpenMedia> implements Cloneable {
                private static volatile OpenMedia[] _emptyArray;
                public Boolean isSample;
                public Integer source;
                public Integer type;

                public interface MediaSource {
                    public static final int CAROUSEL = 2;
                    public static final int GALLERY = 1;
                    public static final int INTENT = 3;
                    public static final int UNSPECIFIED = 0;
                }

                public interface MediaType {
                    public static final int FLAT_PANORAMA = 2;
                    public static final int PANORAMA = 3;
                    public static final int PHOTO = 1;
                    public static final int UNKNOWN = 0;
                    public static final int VR = 4;
                }

                public OpenMedia() {
                    clear();
                }

                public static OpenMedia[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new OpenMedia[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static OpenMedia parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new OpenMedia().mergeFrom(codedInputByteBufferNano);
                }

                public static OpenMedia parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (OpenMedia) MessageNano.mergeFrom(new OpenMedia(), bArr);
                }

                public final OpenMedia clear() {
                    this.isSample = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final OpenMedia clone() {
                    try {
                        return (OpenMedia) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.type != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type.intValue());
                    }
                    if (this.source != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.source.intValue());
                    }
                    return this.isSample == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(3, this.isSample.booleanValue());
                }

                public final OpenMedia mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                    case 3:
                                    case 4:
                                        this.type = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                int readInt322 = codedInputByteBufferNano.readInt32();
                                switch (readInt322) {
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                        this.source = Integer.valueOf(readInt322);
                                        break;
                                }
                            case 24:
                                this.isSample = Boolean.valueOf(codedInputByteBufferNano.readBool());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.type != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.type.intValue());
                    }
                    if (this.source != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.source.intValue());
                    }
                    if (this.isSample != null) {
                        codedOutputByteBufferNano.writeBool(3, this.isSample.booleanValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class WarmWelcome extends ExtendableMessageNano<WarmWelcome> implements Cloneable {
                private static volatile WarmWelcome[] _emptyArray;
                public Float exitProgress;

                public WarmWelcome() {
                    clear();
                }

                public static WarmWelcome[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new WarmWelcome[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static WarmWelcome parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new WarmWelcome().mergeFrom(codedInputByteBufferNano);
                }

                public static WarmWelcome parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (WarmWelcome) MessageNano.mergeFrom(new WarmWelcome(), bArr);
                }

                public final WarmWelcome clear() {
                    this.exitProgress = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final WarmWelcome clone() {
                    try {
                        return (WarmWelcome) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    return this.exitProgress == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(1, this.exitProgress.floatValue());
                }

                public final WarmWelcome mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 13:
                                this.exitProgress = Float.valueOf(codedInputByteBufferNano.readFloat());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.exitProgress != null) {
                        codedOutputByteBufferNano.writeFloat(1, this.exitProgress.floatValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public Photos() {
                clear();
            }

            public static Photos[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Photos[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Photos parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Photos().mergeFrom(codedInputByteBufferNano);
            }

            public static Photos parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Photos) MessageNano.mergeFrom(new Photos(), bArr);
            }

            public final Photos clear() {
                this.numPhotos = null;
                this.openMedia = null;
                this.warmWelcome = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Photos clone() {
                try {
                    Photos photos = (Photos) super.clone();
                    if (this.openMedia != null) {
                        photos.openMedia = this.openMedia.clone();
                    }
                    if (this.warmWelcome != null) {
                        photos.warmWelcome = this.warmWelcome.clone();
                    }
                    return photos;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.numPhotos != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.numPhotos.intValue());
                }
                if (this.openMedia != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.openMedia);
                }
                return this.warmWelcome == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(3, this.warmWelcome);
            }

            public final Photos mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 8:
                            this.numPhotos = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 18:
                            if (this.openMedia == null) {
                                this.openMedia = new OpenMedia();
                            }
                            codedInputByteBufferNano.readMessage(this.openMedia);
                            break;
                        case 26:
                            if (this.warmWelcome == null) {
                                this.warmWelcome = new WarmWelcome();
                            }
                            codedInputByteBufferNano.readMessage(this.warmWelcome);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.numPhotos != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.numPhotos.intValue());
                }
                if (this.openMedia != null) {
                    codedOutputByteBufferNano.writeMessage(2, this.openMedia);
                }
                if (this.warmWelcome != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.warmWelcome);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class QrCodeScan extends ExtendableMessageNano<QrCodeScan> implements Cloneable {
            private static volatile QrCodeScan[] _emptyArray;
            public String headMountConfigUrl;
            public Integer status;

            public interface Status {
                public static final int CONNECTION_ERROR = 3;
                public static final int OK = 1;
                public static final int UNEXPECTED_FORMAT = 2;
                public static final int UNKNOWN = 0;
            }

            public QrCodeScan() {
                clear();
            }

            public static QrCodeScan[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new QrCodeScan[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static QrCodeScan parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new QrCodeScan().mergeFrom(codedInputByteBufferNano);
            }

            public static QrCodeScan parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (QrCodeScan) MessageNano.mergeFrom(new QrCodeScan(), bArr);
            }

            public final QrCodeScan clear() {
                this.headMountConfigUrl = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final QrCodeScan clone() {
                try {
                    return (QrCodeScan) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.status != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.status.intValue());
                }
                return this.headMountConfigUrl == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(2, this.headMountConfigUrl);
            }

            public final QrCodeScan mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                case 2:
                                case 3:
                                    this.status = Integer.valueOf(readInt32);
                                    break;
                            }
                        case 18:
                            this.headMountConfigUrl = codedInputByteBufferNano.readString();
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.status != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.status.intValue());
                }
                if (this.headMountConfigUrl != null) {
                    codedOutputByteBufferNano.writeString(2, this.headMountConfigUrl);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class Renderer extends ExtendableMessageNano<Renderer> implements Cloneable {
            private static volatile Renderer[] _emptyArray;
            public String glRenderer;
            public String glVendor;
            public String glVersion;

            public Renderer() {
                clear();
            }

            public static Renderer[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Renderer[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Renderer parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Renderer().mergeFrom(codedInputByteBufferNano);
            }

            public static Renderer parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Renderer) MessageNano.mergeFrom(new Renderer(), bArr);
            }

            public final Renderer clear() {
                this.glVendor = null;
                this.glRenderer = null;
                this.glVersion = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Renderer clone() {
                try {
                    return (Renderer) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.glVendor != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.glVendor);
                }
                if (this.glRenderer != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.glRenderer);
                }
                return this.glVersion == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(3, this.glVersion);
            }

            public final Renderer mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            this.glVendor = codedInputByteBufferNano.readString();
                            break;
                        case 18:
                            this.glRenderer = codedInputByteBufferNano.readString();
                            break;
                        case 26:
                            this.glVersion = codedInputByteBufferNano.readString();
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.glVendor != null) {
                    codedOutputByteBufferNano.writeString(1, this.glVendor);
                }
                if (this.glRenderer != null) {
                    codedOutputByteBufferNano.writeString(2, this.glRenderer);
                }
                if (this.glVersion != null) {
                    codedOutputByteBufferNano.writeString(3, this.glVersion);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class SdkConfigurationParams extends ExtendableMessageNano<SdkConfigurationParams> implements Cloneable {
            private static volatile SdkConfigurationParams[] _emptyArray;
            public Boolean allowDynamicLibraryLoading;
            public AsyncReprojectionConfig asyncReprojectionConfig;
            public Boolean cpuLateLatchingEnabled;
            public Integer daydreamImageAlignment;
            public Boolean daydreamImageAlignmentEnabled;
            public Boolean useMagnetometerInSensorFusion;
            public Boolean useOnlineMagnetometerCalibration;
            public Boolean useSystemClockForSensorTimestamps;

            public static final class AsyncReprojectionConfig extends ExtendableMessageNano<AsyncReprojectionConfig> implements Cloneable {
                private static volatile AsyncReprojectionConfig[] _emptyArray;
                public Long displayLatencyMicros;
                public Long flags;

                public interface Flags {
                    public static final int AGGRESSIVE_SCHEDULING = 4;
                    public static final int CONFIGURE_BINNING = 1;
                    public static final int EXCLUSIVE_CORE = 8;
                    public static final int LATE_LATCHING = 2;
                    public static final int UNSPECIFIED = 0;
                }

                public AsyncReprojectionConfig() {
                    clear();
                }

                public static AsyncReprojectionConfig[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new AsyncReprojectionConfig[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static AsyncReprojectionConfig parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new AsyncReprojectionConfig().mergeFrom(codedInputByteBufferNano);
                }

                public static AsyncReprojectionConfig parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (AsyncReprojectionConfig) MessageNano.mergeFrom(new AsyncReprojectionConfig(), bArr);
                }

                public final AsyncReprojectionConfig clear() {
                    this.flags = null;
                    this.displayLatencyMicros = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final AsyncReprojectionConfig clone() {
                    try {
                        return (AsyncReprojectionConfig) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.flags != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, this.flags.longValue());
                    }
                    return this.displayLatencyMicros == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(2, this.displayLatencyMicros.longValue());
                }

                public final AsyncReprojectionConfig mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                this.flags = Long.valueOf(codedInputByteBufferNano.readInt64());
                                break;
                            case 16:
                                this.displayLatencyMicros = Long.valueOf(codedInputByteBufferNano.readInt64());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.flags != null) {
                        codedOutputByteBufferNano.writeInt64(1, this.flags.longValue());
                    }
                    if (this.displayLatencyMicros != null) {
                        codedOutputByteBufferNano.writeInt64(2, this.displayLatencyMicros.longValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public interface DaydreamImageAlignment {
                public static final int DISABLED = 1;
                public static final int ENABLED_NO_FILTERING = 2;
                public static final int ENABLED_WITH_MEDIAN_FILTER = 3;
                public static final int UNKNOWN_ALIGNMENT = 0;
            }

            public SdkConfigurationParams() {
                clear();
            }

            public static SdkConfigurationParams[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new SdkConfigurationParams[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static SdkConfigurationParams parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new SdkConfigurationParams().mergeFrom(codedInputByteBufferNano);
            }

            public static SdkConfigurationParams parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (SdkConfigurationParams) MessageNano.mergeFrom(new SdkConfigurationParams(), bArr);
            }

            public final SdkConfigurationParams clear() {
                this.daydreamImageAlignmentEnabled = null;
                this.useSystemClockForSensorTimestamps = null;
                this.useMagnetometerInSensorFusion = null;
                this.allowDynamicLibraryLoading = null;
                this.cpuLateLatchingEnabled = null;
                this.asyncReprojectionConfig = null;
                this.useOnlineMagnetometerCalibration = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final SdkConfigurationParams clone() {
                try {
                    SdkConfigurationParams sdkConfigurationParams = (SdkConfigurationParams) super.clone();
                    if (this.asyncReprojectionConfig != null) {
                        sdkConfigurationParams.asyncReprojectionConfig = this.asyncReprojectionConfig.clone();
                    }
                    return sdkConfigurationParams;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.daydreamImageAlignmentEnabled != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, this.daydreamImageAlignmentEnabled.booleanValue());
                }
                if (this.useSystemClockForSensorTimestamps != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, this.useSystemClockForSensorTimestamps.booleanValue());
                }
                if (this.useMagnetometerInSensorFusion != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, this.useMagnetometerInSensorFusion.booleanValue());
                }
                if (this.allowDynamicLibraryLoading != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, this.allowDynamicLibraryLoading.booleanValue());
                }
                if (this.cpuLateLatchingEnabled != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, this.cpuLateLatchingEnabled.booleanValue());
                }
                if (this.daydreamImageAlignment != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.daydreamImageAlignment.intValue());
                }
                if (this.asyncReprojectionConfig != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, this.asyncReprojectionConfig);
                }
                return this.useOnlineMagnetometerCalibration == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(8, this.useOnlineMagnetometerCalibration.booleanValue());
            }

            public final SdkConfigurationParams mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 8:
                            this.daydreamImageAlignmentEnabled = Boolean.valueOf(codedInputByteBufferNano.readBool());
                            break;
                        case 16:
                            this.useSystemClockForSensorTimestamps = Boolean.valueOf(codedInputByteBufferNano.readBool());
                            break;
                        case 24:
                            this.useMagnetometerInSensorFusion = Boolean.valueOf(codedInputByteBufferNano.readBool());
                            break;
                        case 32:
                            this.allowDynamicLibraryLoading = Boolean.valueOf(codedInputByteBufferNano.readBool());
                            break;
                        case 40:
                            this.cpuLateLatchingEnabled = Boolean.valueOf(codedInputByteBufferNano.readBool());
                            break;
                        case 48:
                            int readInt32 = codedInputByteBufferNano.readInt32();
                            switch (readInt32) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                    this.daydreamImageAlignment = Integer.valueOf(readInt32);
                                    break;
                            }
                        case 58:
                            if (this.asyncReprojectionConfig == null) {
                                this.asyncReprojectionConfig = new AsyncReprojectionConfig();
                            }
                            codedInputByteBufferNano.readMessage(this.asyncReprojectionConfig);
                            break;
                        case 64:
                            this.useOnlineMagnetometerCalibration = Boolean.valueOf(codedInputByteBufferNano.readBool());
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.daydreamImageAlignmentEnabled != null) {
                    codedOutputByteBufferNano.writeBool(1, this.daydreamImageAlignmentEnabled.booleanValue());
                }
                if (this.useSystemClockForSensorTimestamps != null) {
                    codedOutputByteBufferNano.writeBool(2, this.useSystemClockForSensorTimestamps.booleanValue());
                }
                if (this.useMagnetometerInSensorFusion != null) {
                    codedOutputByteBufferNano.writeBool(3, this.useMagnetometerInSensorFusion.booleanValue());
                }
                if (this.allowDynamicLibraryLoading != null) {
                    codedOutputByteBufferNano.writeBool(4, this.allowDynamicLibraryLoading.booleanValue());
                }
                if (this.cpuLateLatchingEnabled != null) {
                    codedOutputByteBufferNano.writeBool(5, this.cpuLateLatchingEnabled.booleanValue());
                }
                if (this.daydreamImageAlignment != null) {
                    codedOutputByteBufferNano.writeInt32(6, this.daydreamImageAlignment.intValue());
                }
                if (this.asyncReprojectionConfig != null) {
                    codedOutputByteBufferNano.writeMessage(7, this.asyncReprojectionConfig);
                }
                if (this.useOnlineMagnetometerCalibration != null) {
                    codedOutputByteBufferNano.writeBool(8, this.useOnlineMagnetometerCalibration.booleanValue());
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class SensorStats extends ExtendableMessageNano<SensorStats> implements Cloneable {
            private static volatile SensorStats[] _emptyArray;
            public GyroscopeStats gyroscopeStats;

            public static final class GyroscopeStats extends ExtendableMessageNano<GyroscopeStats> implements Cloneable {
                private static volatile GyroscopeStats[] _emptyArray;
                public Vector3 bias;
                public Vector3 lowerBound;
                public Vector3 standardDeviation;
                public Vector3 upperBound;

                public GyroscopeStats() {
                    clear();
                }

                public static GyroscopeStats[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new GyroscopeStats[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static GyroscopeStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new GyroscopeStats().mergeFrom(codedInputByteBufferNano);
                }

                public static GyroscopeStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (GyroscopeStats) MessageNano.mergeFrom(new GyroscopeStats(), bArr);
                }

                public final GyroscopeStats clear() {
                    this.bias = null;
                    this.lowerBound = null;
                    this.upperBound = null;
                    this.standardDeviation = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final GyroscopeStats clone() {
                    try {
                        GyroscopeStats gyroscopeStats = (GyroscopeStats) super.clone();
                        if (this.bias != null) {
                            gyroscopeStats.bias = this.bias.clone();
                        }
                        if (this.lowerBound != null) {
                            gyroscopeStats.lowerBound = this.lowerBound.clone();
                        }
                        if (this.upperBound != null) {
                            gyroscopeStats.upperBound = this.upperBound.clone();
                        }
                        if (this.standardDeviation != null) {
                            gyroscopeStats.standardDeviation = this.standardDeviation.clone();
                        }
                        return gyroscopeStats;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.bias != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.bias);
                    }
                    if (this.lowerBound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.lowerBound);
                    }
                    if (this.upperBound != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.upperBound);
                    }
                    return this.standardDeviation == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(4, this.standardDeviation);
                }

                public final GyroscopeStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 10:
                                if (this.bias == null) {
                                    this.bias = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.bias);
                                break;
                            case 18:
                                if (this.lowerBound == null) {
                                    this.lowerBound = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.lowerBound);
                                break;
                            case 26:
                                if (this.upperBound == null) {
                                    this.upperBound = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.upperBound);
                                break;
                            case 34:
                                if (this.standardDeviation == null) {
                                    this.standardDeviation = new Vector3();
                                }
                                codedInputByteBufferNano.readMessage(this.standardDeviation);
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.bias != null) {
                        codedOutputByteBufferNano.writeMessage(1, this.bias);
                    }
                    if (this.lowerBound != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.lowerBound);
                    }
                    if (this.upperBound != null) {
                        codedOutputByteBufferNano.writeMessage(3, this.upperBound);
                    }
                    if (this.standardDeviation != null) {
                        codedOutputByteBufferNano.writeMessage(4, this.standardDeviation);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public static final class Vector3 extends ExtendableMessageNano<Vector3> implements Cloneable {
                private static volatile Vector3[] _emptyArray;
                public Float x;
                public Float y;
                public Float z;

                public Vector3() {
                    clear();
                }

                public static Vector3[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Vector3[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Vector3 parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Vector3().mergeFrom(codedInputByteBufferNano);
                }

                public static Vector3 parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Vector3) MessageNano.mergeFrom(new Vector3(), bArr);
                }

                public final Vector3 clear() {
                    this.x = null;
                    this.y = null;
                    this.z = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Vector3 clone() {
                    try {
                        return (Vector3) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.x != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.x.floatValue());
                    }
                    if (this.y != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.y.floatValue());
                    }
                    return this.z == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(3, this.z.floatValue());
                }

                public final Vector3 mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 13:
                                this.x = Float.valueOf(codedInputByteBufferNano.readFloat());
                                break;
                            case 21:
                                this.y = Float.valueOf(codedInputByteBufferNano.readFloat());
                                break;
                            case 29:
                                this.z = Float.valueOf(codedInputByteBufferNano.readFloat());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.x != null) {
                        codedOutputByteBufferNano.writeFloat(1, this.x.floatValue());
                    }
                    if (this.y != null) {
                        codedOutputByteBufferNano.writeFloat(2, this.y.floatValue());
                    }
                    if (this.z != null) {
                        codedOutputByteBufferNano.writeFloat(3, this.z.floatValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public SensorStats() {
                clear();
            }

            public static SensorStats[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new SensorStats[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static SensorStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new SensorStats().mergeFrom(codedInputByteBufferNano);
            }

            public static SensorStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (SensorStats) MessageNano.mergeFrom(new SensorStats(), bArr);
            }

            public final SensorStats clear() {
                this.gyroscopeStats = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final SensorStats clone() {
                try {
                    SensorStats sensorStats = (SensorStats) super.clone();
                    if (this.gyroscopeStats != null) {
                        sensorStats.gyroscopeStats = this.gyroscopeStats.clone();
                    }
                    return sensorStats;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                return this.gyroscopeStats == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, this.gyroscopeStats);
            }

            public final SensorStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            if (this.gyroscopeStats == null) {
                                this.gyroscopeStats = new GyroscopeStats();
                            }
                            codedInputByteBufferNano.readMessage(this.gyroscopeStats);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.gyroscopeStats != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.gyroscopeStats);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class StreetView extends ExtendableMessageNano<StreetView> implements Cloneable {
            private static volatile StreetView[] _emptyArray;
            public PanoSession panoSession;

            public static final class PanoSession extends ExtendableMessageNano<PanoSession> implements Cloneable {
                private static volatile PanoSession[] _emptyArray;
                public Integer infoClicks;
                public Integer nextClicks;
                public Integer nodesNavigated;
                public Integer panosAvailable;
                public Integer panosViewed;
                public Integer playPauseClicks;
                public Integer prevClicks;
                public Integer source;

                public interface Source {
                    public static final int SOURCE_COLLECTION = 1;
                    public static final int SOURCE_PANO = 2;
                    public static final int SOURCE_SEARCH = 3;
                    public static final int SOURCE_SEARCH_RESULTS = 4;
                    public static final int SOURCE_UNKNOWN = 0;
                }

                public PanoSession() {
                    clear();
                }

                public static PanoSession[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new PanoSession[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static PanoSession parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new PanoSession().mergeFrom(codedInputByteBufferNano);
                }

                public static PanoSession parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (PanoSession) MessageNano.mergeFrom(new PanoSession(), bArr);
                }

                public final PanoSession clear() {
                    this.panosAvailable = null;
                    this.panosViewed = null;
                    this.nodesNavigated = null;
                    this.nextClicks = null;
                    this.prevClicks = null;
                    this.playPauseClicks = null;
                    this.infoClicks = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final PanoSession clone() {
                    try {
                        return (PanoSession) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.source != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.source.intValue());
                    }
                    if (this.panosAvailable != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.panosAvailable.intValue());
                    }
                    if (this.panosViewed != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.panosViewed.intValue());
                    }
                    if (this.nodesNavigated != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.nodesNavigated.intValue());
                    }
                    if (this.nextClicks != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, this.nextClicks.intValue());
                    }
                    if (this.prevClicks != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.prevClicks.intValue());
                    }
                    if (this.playPauseClicks != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, this.playPauseClicks.intValue());
                    }
                    return this.infoClicks == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(8, this.infoClicks.intValue());
                }

                public final PanoSession mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                    case 2:
                                    case 3:
                                    case 4:
                                        this.source = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 16:
                                this.panosAvailable = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 24:
                                this.panosViewed = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 32:
                                this.nodesNavigated = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 40:
                                this.nextClicks = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 48:
                                this.prevClicks = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 56:
                                this.playPauseClicks = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 64:
                                this.infoClicks = Integer.valueOf(codedInputByteBufferNano.readInt32());
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.source != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.source.intValue());
                    }
                    if (this.panosAvailable != null) {
                        codedOutputByteBufferNano.writeInt32(2, this.panosAvailable.intValue());
                    }
                    if (this.panosViewed != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.panosViewed.intValue());
                    }
                    if (this.nodesNavigated != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.nodesNavigated.intValue());
                    }
                    if (this.nextClicks != null) {
                        codedOutputByteBufferNano.writeInt32(5, this.nextClicks.intValue());
                    }
                    if (this.prevClicks != null) {
                        codedOutputByteBufferNano.writeInt32(6, this.prevClicks.intValue());
                    }
                    if (this.playPauseClicks != null) {
                        codedOutputByteBufferNano.writeInt32(7, this.playPauseClicks.intValue());
                    }
                    if (this.infoClicks != null) {
                        codedOutputByteBufferNano.writeInt32(8, this.infoClicks.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public StreetView() {
                clear();
            }

            public static StreetView[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new StreetView[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static StreetView parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new StreetView().mergeFrom(codedInputByteBufferNano);
            }

            public static StreetView parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (StreetView) MessageNano.mergeFrom(new StreetView(), bArr);
            }

            public final StreetView clear() {
                this.panoSession = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final StreetView clone() {
                try {
                    StreetView streetView = (StreetView) super.clone();
                    if (this.panoSession != null) {
                        streetView.panoSession = this.panoSession.clone();
                    }
                    return streetView;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                return this.panoSession == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, this.panoSession);
            }

            public final StreetView mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            if (this.panoSession == null) {
                                this.panoSession = new PanoSession();
                            }
                            codedInputByteBufferNano.readMessage(this.panoSession);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.panoSession != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.panoSession);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class TimeSeriesData extends ExtendableMessageNano<TimeSeriesData> implements Cloneable {
            private static volatile TimeSeriesData[] _emptyArray;
            public TimeIntervalData[] timeIntervalData;
            public Integer timeIntervalSeconds;

            public static final class TimeIntervalData extends ExtendableMessageNano<TimeIntervalData> implements Cloneable {
                private static volatile TimeIntervalData[] _emptyArray;
                public Integer batteryLevel;
                public Integer batteryLevelDelta;
                public float[] batteryTemperature;
                public float[] cpuTemperature;
                public Integer edsThreadFrameDropCount;
                public float[] gpuTemperature;
                public Integer intervalStartTimeSeconds;
                public Float skinTemperature;
                public Integer thermalWarningsShown;

                public TimeIntervalData() {
                    clear();
                }

                public static TimeIntervalData[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new TimeIntervalData[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static TimeIntervalData parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new TimeIntervalData().mergeFrom(codedInputByteBufferNano);
                }

                public static TimeIntervalData parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (TimeIntervalData) MessageNano.mergeFrom(new TimeIntervalData(), bArr);
                }

                public final TimeIntervalData clear() {
                    this.intervalStartTimeSeconds = null;
                    this.skinTemperature = null;
                    this.edsThreadFrameDropCount = null;
                    this.batteryLevel = null;
                    this.batteryLevelDelta = null;
                    this.thermalWarningsShown = null;
                    this.cpuTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                    this.gpuTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                    this.batteryTemperature = WireFormatNano.EMPTY_FLOAT_ARRAY;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final TimeIntervalData clone() {
                    try {
                        TimeIntervalData timeIntervalData = (TimeIntervalData) super.clone();
                        if (this.cpuTemperature != null && this.cpuTemperature.length > 0) {
                            timeIntervalData.cpuTemperature = (float[]) this.cpuTemperature.clone();
                        }
                        if (this.gpuTemperature != null && this.gpuTemperature.length > 0) {
                            timeIntervalData.gpuTemperature = (float[]) this.gpuTemperature.clone();
                        }
                        if (this.batteryTemperature != null && this.batteryTemperature.length > 0) {
                            timeIntervalData.batteryTemperature = (float[]) this.batteryTemperature.clone();
                        }
                        return timeIntervalData;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.intervalStartTimeSeconds != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.intervalStartTimeSeconds.intValue());
                    }
                    if (this.skinTemperature != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.skinTemperature.floatValue());
                    }
                    if (this.edsThreadFrameDropCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.edsThreadFrameDropCount.intValue());
                    }
                    if (this.batteryLevel != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.batteryLevel.intValue());
                    }
                    if (this.batteryLevelDelta != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, this.batteryLevelDelta.intValue());
                    }
                    if (this.thermalWarningsShown != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.thermalWarningsShown.intValue());
                    }
                    if (this.cpuTemperature != null && this.cpuTemperature.length > 0) {
                        computeSerializedSize = computeSerializedSize + (this.cpuTemperature.length * 4) + (this.cpuTemperature.length * 1);
                    }
                    if (this.gpuTemperature != null && this.gpuTemperature.length > 0) {
                        computeSerializedSize = computeSerializedSize + (this.gpuTemperature.length * 4) + (this.gpuTemperature.length * 1);
                    }
                    return (this.batteryTemperature != null && this.batteryTemperature.length > 0) ? computeSerializedSize + (this.batteryTemperature.length * 4) + (this.batteryTemperature.length * 1) : computeSerializedSize;
                }

                public final TimeIntervalData mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 8:
                                this.intervalStartTimeSeconds = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 21:
                                this.skinTemperature = Float.valueOf(codedInputByteBufferNano.readFloat());
                                break;
                            case 24:
                                this.edsThreadFrameDropCount = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 32:
                                this.batteryLevel = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 40:
                                this.batteryLevelDelta = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 48:
                                this.thermalWarningsShown = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 58:
                                int readRawVarint32 = codedInputByteBufferNano.readRawVarint32();
                                int pushLimit = codedInputByteBufferNano.pushLimit(readRawVarint32);
                                int i = readRawVarint32 / 4;
                                int length = this.cpuTemperature != null ? this.cpuTemperature.length : 0;
                                float[] fArr = new float[(i + length)];
                                if (length != 0) {
                                    System.arraycopy(this.cpuTemperature, 0, fArr, 0, length);
                                }
                                while (length < fArr.length) {
                                    fArr[length] = codedInputByteBufferNano.readFloat();
                                    length++;
                                }
                                this.cpuTemperature = fArr;
                                codedInputByteBufferNano.popLimit(pushLimit);
                                break;
                            case 61:
                                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 61);
                                int length2 = this.cpuTemperature != null ? this.cpuTemperature.length : 0;
                                float[] fArr2 = new float[(repeatedFieldArrayLength + length2)];
                                if (length2 != 0) {
                                    System.arraycopy(this.cpuTemperature, 0, fArr2, 0, length2);
                                }
                                while (length2 < fArr2.length - 1) {
                                    fArr2[length2] = codedInputByteBufferNano.readFloat();
                                    codedInputByteBufferNano.readTag();
                                    length2++;
                                }
                                fArr2[length2] = codedInputByteBufferNano.readFloat();
                                this.cpuTemperature = fArr2;
                                break;
                            case 66:
                                int readRawVarint322 = codedInputByteBufferNano.readRawVarint32();
                                int pushLimit2 = codedInputByteBufferNano.pushLimit(readRawVarint322);
                                int i2 = readRawVarint322 / 4;
                                int length3 = this.gpuTemperature != null ? this.gpuTemperature.length : 0;
                                float[] fArr3 = new float[(i2 + length3)];
                                if (length3 != 0) {
                                    System.arraycopy(this.gpuTemperature, 0, fArr3, 0, length3);
                                }
                                while (length3 < fArr3.length) {
                                    fArr3[length3] = codedInputByteBufferNano.readFloat();
                                    length3++;
                                }
                                this.gpuTemperature = fArr3;
                                codedInputByteBufferNano.popLimit(pushLimit2);
                                break;
                            case 69:
                                int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 69);
                                int length4 = this.gpuTemperature != null ? this.gpuTemperature.length : 0;
                                float[] fArr4 = new float[(repeatedFieldArrayLength2 + length4)];
                                if (length4 != 0) {
                                    System.arraycopy(this.gpuTemperature, 0, fArr4, 0, length4);
                                }
                                while (length4 < fArr4.length - 1) {
                                    fArr4[length4] = codedInputByteBufferNano.readFloat();
                                    codedInputByteBufferNano.readTag();
                                    length4++;
                                }
                                fArr4[length4] = codedInputByteBufferNano.readFloat();
                                this.gpuTemperature = fArr4;
                                break;
                            case 74:
                                int readRawVarint323 = codedInputByteBufferNano.readRawVarint32();
                                int pushLimit3 = codedInputByteBufferNano.pushLimit(readRawVarint323);
                                int i3 = readRawVarint323 / 4;
                                int length5 = this.batteryTemperature != null ? this.batteryTemperature.length : 0;
                                float[] fArr5 = new float[(i3 + length5)];
                                if (length5 != 0) {
                                    System.arraycopy(this.batteryTemperature, 0, fArr5, 0, length5);
                                }
                                while (length5 < fArr5.length) {
                                    fArr5[length5] = codedInputByteBufferNano.readFloat();
                                    length5++;
                                }
                                this.batteryTemperature = fArr5;
                                codedInputByteBufferNano.popLimit(pushLimit3);
                                break;
                            case 77:
                                int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 77);
                                int length6 = this.batteryTemperature != null ? this.batteryTemperature.length : 0;
                                float[] fArr6 = new float[(repeatedFieldArrayLength3 + length6)];
                                if (length6 != 0) {
                                    System.arraycopy(this.batteryTemperature, 0, fArr6, 0, length6);
                                }
                                while (length6 < fArr6.length - 1) {
                                    fArr6[length6] = codedInputByteBufferNano.readFloat();
                                    codedInputByteBufferNano.readTag();
                                    length6++;
                                }
                                fArr6[length6] = codedInputByteBufferNano.readFloat();
                                this.batteryTemperature = fArr6;
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.intervalStartTimeSeconds != null) {
                        codedOutputByteBufferNano.writeInt32(1, this.intervalStartTimeSeconds.intValue());
                    }
                    if (this.skinTemperature != null) {
                        codedOutputByteBufferNano.writeFloat(2, this.skinTemperature.floatValue());
                    }
                    if (this.edsThreadFrameDropCount != null) {
                        codedOutputByteBufferNano.writeInt32(3, this.edsThreadFrameDropCount.intValue());
                    }
                    if (this.batteryLevel != null) {
                        codedOutputByteBufferNano.writeInt32(4, this.batteryLevel.intValue());
                    }
                    if (this.batteryLevelDelta != null) {
                        codedOutputByteBufferNano.writeInt32(5, this.batteryLevelDelta.intValue());
                    }
                    if (this.thermalWarningsShown != null) {
                        codedOutputByteBufferNano.writeInt32(6, this.thermalWarningsShown.intValue());
                    }
                    if (this.cpuTemperature != null && this.cpuTemperature.length > 0) {
                        for (float writeFloat : this.cpuTemperature) {
                            codedOutputByteBufferNano.writeFloat(7, writeFloat);
                        }
                    }
                    if (this.gpuTemperature != null && this.gpuTemperature.length > 0) {
                        for (float writeFloat2 : this.gpuTemperature) {
                            codedOutputByteBufferNano.writeFloat(8, writeFloat2);
                        }
                    }
                    if (this.batteryTemperature != null && this.batteryTemperature.length > 0) {
                        for (float writeFloat3 : this.batteryTemperature) {
                            codedOutputByteBufferNano.writeFloat(9, writeFloat3);
                        }
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public TimeSeriesData() {
                clear();
            }

            public static TimeSeriesData[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new TimeSeriesData[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static TimeSeriesData parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new TimeSeriesData().mergeFrom(codedInputByteBufferNano);
            }

            public static TimeSeriesData parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (TimeSeriesData) MessageNano.mergeFrom(new TimeSeriesData(), bArr);
            }

            public final TimeSeriesData clear() {
                this.timeIntervalSeconds = null;
                this.timeIntervalData = TimeIntervalData.emptyArray();
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final TimeSeriesData clone() {
                try {
                    TimeSeriesData timeSeriesData = (TimeSeriesData) super.clone();
                    if (this.timeIntervalData != null && this.timeIntervalData.length > 0) {
                        timeSeriesData.timeIntervalData = new TimeIntervalData[this.timeIntervalData.length];
                        for (int i = 0; i < this.timeIntervalData.length; i++) {
                            if (this.timeIntervalData[i] != null) {
                                timeSeriesData.timeIntervalData[i] = this.timeIntervalData[i].clone();
                            }
                        }
                    }
                    return timeSeriesData;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.timeIntervalSeconds != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.timeIntervalSeconds.intValue());
                }
                if (this.timeIntervalData == null || this.timeIntervalData.length <= 0) {
                    return computeSerializedSize;
                }
                int i = computeSerializedSize;
                for (TimeIntervalData timeIntervalData2 : this.timeIntervalData) {
                    if (timeIntervalData2 != null) {
                        i += CodedOutputByteBufferNano.computeMessageSize(2, timeIntervalData2);
                    }
                }
                return i;
            }

            public final TimeSeriesData mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 8:
                            this.timeIntervalSeconds = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 18:
                            int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                            int length = this.timeIntervalData != null ? this.timeIntervalData.length : 0;
                            TimeIntervalData[] timeIntervalDataArr = new TimeIntervalData[(repeatedFieldArrayLength + length)];
                            if (length != 0) {
                                System.arraycopy(this.timeIntervalData, 0, timeIntervalDataArr, 0, length);
                            }
                            while (length < timeIntervalDataArr.length - 1) {
                                timeIntervalDataArr[length] = new TimeIntervalData();
                                codedInputByteBufferNano.readMessage(timeIntervalDataArr[length]);
                                codedInputByteBufferNano.readTag();
                                length++;
                            }
                            timeIntervalDataArr[length] = new TimeIntervalData();
                            codedInputByteBufferNano.readMessage(timeIntervalDataArr[length]);
                            this.timeIntervalData = timeIntervalDataArr;
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.timeIntervalSeconds != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.timeIntervalSeconds.intValue());
                }
                if (this.timeIntervalData != null && this.timeIntervalData.length > 0) {
                    for (TimeIntervalData timeIntervalData2 : this.timeIntervalData) {
                        if (timeIntervalData2 != null) {
                            codedOutputByteBufferNano.writeMessage(2, timeIntervalData2);
                        }
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class Transform extends ExtendableMessageNano<Transform> implements Cloneable {
            private static volatile Transform[] _emptyArray;
            public Float rotationQx;
            public Float rotationQy;
            public Float rotationQz;
            public Float scale;
            public Float translationX;
            public Float translationY;
            public Float translationZ;

            public Transform() {
                clear();
            }

            public static Transform[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Transform[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static Transform parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Transform().mergeFrom(codedInputByteBufferNano);
            }

            public static Transform parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Transform) MessageNano.mergeFrom(new Transform(), bArr);
            }

            public final Transform clear() {
                this.translationX = null;
                this.translationY = null;
                this.translationZ = null;
                this.rotationQx = null;
                this.rotationQy = null;
                this.rotationQz = null;
                this.scale = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final Transform clone() {
                try {
                    return (Transform) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.translationX != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.translationX.floatValue());
                }
                if (this.translationY != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.translationY.floatValue());
                }
                if (this.translationZ != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.translationZ.floatValue());
                }
                if (this.rotationQx != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.rotationQx.floatValue());
                }
                if (this.rotationQy != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(5, this.rotationQy.floatValue());
                }
                if (this.rotationQz != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(6, this.rotationQz.floatValue());
                }
                return this.scale == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(7, this.scale.floatValue());
            }

            public final Transform mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 13:
                            this.translationX = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 21:
                            this.translationY = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 29:
                            this.translationZ = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 37:
                            this.rotationQx = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 45:
                            this.rotationQy = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 53:
                            this.rotationQz = Float.valueOf(codedInputByteBufferNano.readFloat());
                            break;
                        case 61:
                            this.scale = Float.valueOf(codedInputByteBufferNano.readFloat());
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.translationX != null) {
                    codedOutputByteBufferNano.writeFloat(1, this.translationX.floatValue());
                }
                if (this.translationY != null) {
                    codedOutputByteBufferNano.writeFloat(2, this.translationY.floatValue());
                }
                if (this.translationZ != null) {
                    codedOutputByteBufferNano.writeFloat(3, this.translationZ.floatValue());
                }
                if (this.rotationQx != null) {
                    codedOutputByteBufferNano.writeFloat(4, this.rotationQx.floatValue());
                }
                if (this.rotationQy != null) {
                    codedOutputByteBufferNano.writeFloat(5, this.rotationQy.floatValue());
                }
                if (this.rotationQz != null) {
                    codedOutputByteBufferNano.writeFloat(6, this.rotationQz.floatValue());
                }
                if (this.scale != null) {
                    codedOutputByteBufferNano.writeFloat(7, this.scale.floatValue());
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class VrCore extends ExtendableMessageNano<VrCore> implements Cloneable {
            private static volatile VrCore[] _emptyArray;
            public Integer clientApiVersion;
            public Controller controller;
            public Integer errorCode;
            public Application foregroundApplication;
            public Integer permission;
            public Application previousForegroundApplication;

            public static final class Controller extends ExtendableMessageNano<Controller> implements Cloneable {
                private static volatile Controller[] _emptyArray;
                public String availableFirmware;
                public Integer axis;
                public Integer batteryLevel;
                public String firmware;
                public String hardwareRevision;
                public String manufacturer;
                public String model;
                public Integer sampleCount;
                public Integer sensorType;
                public String softwareRevision;
                public Integer xRailCount;
                public Integer yRailCount;
                public Integer zRailCount;

                public interface ControllerAxis {
                    public static final int AXIS_UNKNOWN = 0;
                    public static final int AXIS_X = 1;
                    public static final int AXIS_Y = 2;
                    public static final int AXIS_Z = 3;
                }

                public interface SensorType {
                    public static final int SENSOR_ACCEL = 2;
                    public static final int SENSOR_GYRO = 1;
                    public static final int SENSOR_UNKNOWN = 0;
                }

                public Controller() {
                    clear();
                }

                public static Controller[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Controller[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Controller parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Controller().mergeFrom(codedInputByteBufferNano);
                }

                public static Controller parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Controller) MessageNano.mergeFrom(new Controller(), bArr);
                }

                public final Controller clear() {
                    this.manufacturer = null;
                    this.model = null;
                    this.firmware = null;
                    this.availableFirmware = null;
                    this.softwareRevision = null;
                    this.batteryLevel = null;
                    this.hardwareRevision = null;
                    this.xRailCount = null;
                    this.yRailCount = null;
                    this.zRailCount = null;
                    this.sampleCount = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Controller clone() {
                    try {
                        return (Controller) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.manufacturer != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.manufacturer);
                    }
                    if (this.model != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.model);
                    }
                    if (this.firmware != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.firmware);
                    }
                    if (this.availableFirmware != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(4, this.availableFirmware);
                    }
                    if (this.softwareRevision != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(5, this.softwareRevision);
                    }
                    if (this.batteryLevel != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.batteryLevel.intValue());
                    }
                    if (this.hardwareRevision != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.hardwareRevision);
                    }
                    if (this.xRailCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, this.xRailCount.intValue());
                    }
                    if (this.yRailCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, this.yRailCount.intValue());
                    }
                    if (this.zRailCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, this.zRailCount.intValue());
                    }
                    if (this.sampleCount != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, this.sampleCount.intValue());
                    }
                    if (this.sensorType != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, this.sensorType.intValue());
                    }
                    return this.axis == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(13, this.axis.intValue());
                }

                public final Controller mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 10:
                                this.manufacturer = codedInputByteBufferNano.readString();
                                break;
                            case 18:
                                this.model = codedInputByteBufferNano.readString();
                                break;
                            case 26:
                                this.firmware = codedInputByteBufferNano.readString();
                                break;
                            case 34:
                                this.availableFirmware = codedInputByteBufferNano.readString();
                                break;
                            case 42:
                                this.softwareRevision = codedInputByteBufferNano.readString();
                                break;
                            case 48:
                                this.batteryLevel = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 58:
                                this.hardwareRevision = codedInputByteBufferNano.readString();
                                break;
                            case 64:
                                this.xRailCount = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 72:
                                this.yRailCount = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 80:
                                this.zRailCount = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 88:
                                this.sampleCount = Integer.valueOf(codedInputByteBufferNano.readInt32());
                                break;
                            case 96:
                                int readInt32 = codedInputByteBufferNano.readInt32();
                                switch (readInt32) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        this.sensorType = Integer.valueOf(readInt32);
                                        break;
                                }
                            case 104:
                                int readInt322 = codedInputByteBufferNano.readInt32();
                                switch (readInt322) {
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                        this.axis = Integer.valueOf(readInt322);
                                        break;
                                }
                            default:
                                if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                                    break;
                                } else {
                                    return this;
                                }
                        }
                    }
                }

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.manufacturer != null) {
                        codedOutputByteBufferNano.writeString(1, this.manufacturer);
                    }
                    if (this.model != null) {
                        codedOutputByteBufferNano.writeString(2, this.model);
                    }
                    if (this.firmware != null) {
                        codedOutputByteBufferNano.writeString(3, this.firmware);
                    }
                    if (this.availableFirmware != null) {
                        codedOutputByteBufferNano.writeString(4, this.availableFirmware);
                    }
                    if (this.softwareRevision != null) {
                        codedOutputByteBufferNano.writeString(5, this.softwareRevision);
                    }
                    if (this.batteryLevel != null) {
                        codedOutputByteBufferNano.writeInt32(6, this.batteryLevel.intValue());
                    }
                    if (this.hardwareRevision != null) {
                        codedOutputByteBufferNano.writeString(7, this.hardwareRevision);
                    }
                    if (this.xRailCount != null) {
                        codedOutputByteBufferNano.writeInt32(8, this.xRailCount.intValue());
                    }
                    if (this.yRailCount != null) {
                        codedOutputByteBufferNano.writeInt32(9, this.yRailCount.intValue());
                    }
                    if (this.zRailCount != null) {
                        codedOutputByteBufferNano.writeInt32(10, this.zRailCount.intValue());
                    }
                    if (this.sampleCount != null) {
                        codedOutputByteBufferNano.writeInt32(11, this.sampleCount.intValue());
                    }
                    if (this.sensorType != null) {
                        codedOutputByteBufferNano.writeInt32(12, this.sensorType.intValue());
                    }
                    if (this.axis != null) {
                        codedOutputByteBufferNano.writeInt32(13, this.axis.intValue());
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public interface ErrorCode {
                public static final int BAD_STATE = 1;
                public static final int CONTROLLER_BATTERY_READ_FAILED = 125;
                public static final int CONTROLLER_BATTERY_TOO_LOW = 101;
                public static final int CONTROLLER_BLUETOOTH_ERROR = 102;
                public static final int CONTROLLER_BLUETOOTH_OFF = 103;
                public static final int CONTROLLER_BLUETOOTH_SCAN_FAILURE = 116;
                public static final int CONTROLLER_BOND_FAILED = 104;
                public static final int CONTROLLER_CONNECTION_FAILURE = 105;
                public static final int CONTROLLER_CREATE_BOND_FAILURE = 117;
                public static final int CONTROLLER_FIRMWARE_ERROR = 113;
                public static final int CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND = 123;
                public static final int CONTROLLER_GATT_DISCOVERY_FAILED = 121;
                public static final int CONTROLLER_GATT_NOTIFY_FAILED = 124;
                public static final int CONTROLLER_GATT_SERVICE_NOT_FOUND = 122;
                public static final int CONTROLLER_HANDSHAKE_FAILURE = 106;
                public static final int CONTROLLER_INFO_READ_ERROR = 120;
                public static final int CONTROLLER_INSUFFICIENT_PERMS = 107;
                public static final int CONTROLLER_INVALID_MANUFACTURER = 114;
                public static final int CONTROLLER_LOST_CONNECTION = 108;
                public static final int CONTROLLER_MISMATCH = 109;
                public static final int CONTROLLER_NOT_BONDED = 118;
                public static final int CONTROLLER_NOT_FOUND = 110;
                public static final int CONTROLLER_OTA_SERVICE_ERROR = 115;
                public static final int CONTROLLER_PROTOCOL_FAILURE = 111;
                public static final int CONTROLLER_STUCK = 126;
                public static final int CONTROLLER_TIMEOUT = 112;
                public static final int CONTROLLER_UNSTUCK = 127;
                public static final int CONTROLLER_UNSUPPORTED = 119;
                public static final int DAYDREAM_TRACKING_ACQUISITION_FAILED = 151;
                public static final int DAYDREAM_TRACKING_HANDOFF_FAILED = 153;
                public static final int DAYDREAM_TRACKING_TRANSITIONAL_FAILED = 152;
                public static final int DISALLOWED_WRITE = 402;
                public static final int DON_APP_INCOMPATIBLE = 190;
                public static final int DON_BAD_POSE = 176;
                public static final int DON_BLUETOOTH_OFF = 177;
                public static final int DON_CANCELLED = 178;
                public static final int DON_DAYDREAM_APP_INSTALLING = 179;
                public static final int DON_DAYDREAM_APP_NOT_PRESENT = 180;
                public static final int DON_DAYDREAM_APP_OUT_OF_DATE = 181;
                public static final int DON_DAYDREAM_SETUP_NOT_COMPLETE = 182;
                public static final int DON_DEVICE_INCOMPATIBLE = 183;
                public static final int DON_HEADSET_INCOMPATIBLE = 184;
                public static final int DON_LOCATION_OFF = 185;
                public static final int DON_MISSING_PERMISSION = 186;
                public static final int DON_NFC_OFF = 187;
                public static final int DON_SYSTEM_UPDATE_REQUESTED = 191;
                public static final int DON_VRCORE_OUT_OF_DATE = 188;
                public static final int DON_VR_KEYBOARD_NOT_PRESENT = 189;
                public static final int EMPTY_PLAYLOAD = 201;
                public static final int INVALID_PLAYLOAD = 202;
                public static final int INVALID_READ = 401;
                public static final int LAUNCH_FAILURE = 203;
                public static final int NO_ZEN_RULE = 301;
                public static final int UNKNOWN_ERROR = 0;
            }

            public interface Permission {
                public static final int ACCESS_COARSE_LOCATION = 1;
                public static final int ACCESS_NOTIFICATION_POLICY = 8;
                public static final int BIND_CONDITION_PROVIDER_SERVICE = 7;
                public static final int BIND_NOTIFICATION_LISTENER_SERVICE = 6;
                public static final int CAMERA = 2;
                public static final int READ_EXTERNAL_STORAGE = 3;
                public static final int SYSTEM_ALERT_WINDOW = 5;
                public static final int UNKNOWN_PERMISSION = 0;
                public static final int WRITE_EXTERNAL_STORAGE = 4;
            }

            public VrCore() {
                clear();
            }

            public static VrCore[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new VrCore[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static VrCore parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new VrCore().mergeFrom(codedInputByteBufferNano);
            }

            public static VrCore parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (VrCore) MessageNano.mergeFrom(new VrCore(), bArr);
            }

            public final VrCore clear() {
                this.foregroundApplication = null;
                this.clientApiVersion = null;
                this.previousForegroundApplication = null;
                this.controller = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final VrCore clone() {
                try {
                    VrCore vrCore = (VrCore) super.clone();
                    if (this.foregroundApplication != null) {
                        vrCore.foregroundApplication = this.foregroundApplication.clone();
                    }
                    if (this.previousForegroundApplication != null) {
                        vrCore.previousForegroundApplication = this.previousForegroundApplication.clone();
                    }
                    if (this.controller != null) {
                        vrCore.controller = this.controller.clone();
                    }
                    return vrCore;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (this.errorCode != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.errorCode.intValue());
                }
                if (this.permission != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.permission.intValue());
                }
                if (this.foregroundApplication != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.foregroundApplication);
                }
                if (this.clientApiVersion != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.clientApiVersion.intValue());
                }
                if (this.previousForegroundApplication != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, this.previousForegroundApplication);
                }
                return this.controller == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(6, this.controller);
            }

            public final VrCore mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                case 101:
                                case 102:
                                case 103:
                                case 104:
                                case 105:
                                case 106:
                                case 107:
                                case 108:
                                case 109:
                                case 110:
                                case 111:
                                case 112:
                                case 113:
                                case 114:
                                case 115:
                                case 116:
                                case 117:
                                case 118:
                                case ErrorCode.CONTROLLER_UNSUPPORTED /*119*/:
                                case ErrorCode.CONTROLLER_INFO_READ_ERROR /*120*/:
                                case ErrorCode.CONTROLLER_GATT_DISCOVERY_FAILED /*121*/:
                                case ErrorCode.CONTROLLER_GATT_SERVICE_NOT_FOUND /*122*/:
                                case ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND /*123*/:
                                case ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED /*124*/:
                                case ErrorCode.CONTROLLER_BATTERY_READ_FAILED /*125*/:
                                case ErrorCode.CONTROLLER_STUCK /*126*/:
                                case ErrorCode.CONTROLLER_UNSTUCK /*127*/:
                                case ErrorCode.DAYDREAM_TRACKING_ACQUISITION_FAILED /*151*/:
                                case ErrorCode.DAYDREAM_TRACKING_TRANSITIONAL_FAILED /*152*/:
                                case ErrorCode.DAYDREAM_TRACKING_HANDOFF_FAILED /*153*/:
                                case ErrorCode.DON_BAD_POSE /*176*/:
                                case ErrorCode.DON_BLUETOOTH_OFF /*177*/:
                                case ErrorCode.DON_CANCELLED /*178*/:
                                case ErrorCode.DON_DAYDREAM_APP_INSTALLING /*179*/:
                                case ErrorCode.DON_DAYDREAM_APP_NOT_PRESENT /*180*/:
                                case ErrorCode.DON_DAYDREAM_APP_OUT_OF_DATE /*181*/:
                                case ErrorCode.DON_DAYDREAM_SETUP_NOT_COMPLETE /*182*/:
                                case ErrorCode.DON_DEVICE_INCOMPATIBLE /*183*/:
                                case ErrorCode.DON_HEADSET_INCOMPATIBLE /*184*/:
                                case ErrorCode.DON_LOCATION_OFF /*185*/:
                                case ErrorCode.DON_MISSING_PERMISSION /*186*/:
                                case ErrorCode.DON_NFC_OFF /*187*/:
                                case ErrorCode.DON_VRCORE_OUT_OF_DATE /*188*/:
                                case ErrorCode.DON_VR_KEYBOARD_NOT_PRESENT /*189*/:
                                case ErrorCode.DON_APP_INCOMPATIBLE /*190*/:
                                case ErrorCode.DON_SYSTEM_UPDATE_REQUESTED /*191*/:
                                case ErrorCode.EMPTY_PLAYLOAD /*201*/:
                                case ErrorCode.INVALID_PLAYLOAD /*202*/:
                                case ErrorCode.LAUNCH_FAILURE /*203*/:
                                case ErrorCode.NO_ZEN_RULE /*301*/:
                                case ErrorCode.INVALID_READ /*401*/:
                                case ErrorCode.DISALLOWED_WRITE /*402*/:
                                    this.errorCode = Integer.valueOf(readInt32);
                                    break;
                            }
                        case 16:
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
                                case 8:
                                    this.permission = Integer.valueOf(readInt322);
                                    break;
                            }
                        case 26:
                            if (this.foregroundApplication == null) {
                                this.foregroundApplication = new Application();
                            }
                            codedInputByteBufferNano.readMessage(this.foregroundApplication);
                            break;
                        case 32:
                            this.clientApiVersion = Integer.valueOf(codedInputByteBufferNano.readInt32());
                            break;
                        case 42:
                            if (this.previousForegroundApplication == null) {
                                this.previousForegroundApplication = new Application();
                            }
                            codedInputByteBufferNano.readMessage(this.previousForegroundApplication);
                            break;
                        case 50:
                            if (this.controller == null) {
                                this.controller = new Controller();
                            }
                            codedInputByteBufferNano.readMessage(this.controller);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.errorCode != null) {
                    codedOutputByteBufferNano.writeInt32(1, this.errorCode.intValue());
                }
                if (this.permission != null) {
                    codedOutputByteBufferNano.writeInt32(2, this.permission.intValue());
                }
                if (this.foregroundApplication != null) {
                    codedOutputByteBufferNano.writeMessage(3, this.foregroundApplication);
                }
                if (this.clientApiVersion != null) {
                    codedOutputByteBufferNano.writeInt32(4, this.clientApiVersion.intValue());
                }
                if (this.previousForegroundApplication != null) {
                    codedOutputByteBufferNano.writeMessage(5, this.previousForegroundApplication);
                }
                if (this.controller != null) {
                    codedOutputByteBufferNano.writeMessage(6, this.controller);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public static final class VrHome extends ExtendableMessageNano<VrHome> implements Cloneable {
            private static volatile VrHome[] _emptyArray;
            public Setup setup;

            public static final class Setup extends ExtendableMessageNano<Setup> implements Cloneable {
                private static volatile Setup[] _emptyArray;
                public StepStateChange stepStateChange;
                public View view;

                public interface Step {
                    public static final int ACCOUNT_SELECTION = 1;
                    public static final int CHECKING_ACCOUNT_INFO = 4;
                    public static final int DOWNLOADING_VR_COMPONENTS = 7;
                    public static final int FOP = 6;
                    public static final int GETTING_STARTED = 2;
                    public static final int HEALTH_AND_SAFETY = 3;
                    public static final int OVERALL = 8;
                    public static final int PIN = 5;
                    public static final int UNKNOWN = 0;
                }

                public static final class StepStateChange extends ExtendableMessageNano<StepStateChange> implements Cloneable {
                    private static volatile StepStateChange[] _emptyArray;
                    public Integer newStepState;
                    public Integer previousStepState;
                    public Integer step;

                    public interface StepState {
                        public static final int COMPLETE = 3;
                        public static final int ERROR = 1;
                        public static final int LOADING = 2;
                        public static final int SHOW = 4;
                        public static final int UNKNOWN = 0;
                    }

                    public StepStateChange() {
                        clear();
                    }

                    public static StepStateChange[] emptyArray() {
                        if (_emptyArray == null) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (_emptyArray == null) {
                                    _emptyArray = new StepStateChange[0];
                                }
                            }
                        }
                        return _emptyArray;
                    }

                    public static StepStateChange parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        return new StepStateChange().mergeFrom(codedInputByteBufferNano);
                    }

                    public static StepStateChange parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                        return (StepStateChange) MessageNano.mergeFrom(new StepStateChange(), bArr);
                    }

                    public final StepStateChange clear() {
                        this.unknownFieldData = null;
                        this.cachedSize = -1;
                        return this;
                    }

                    public final StepStateChange clone() {
                        try {
                            return (StepStateChange) super.clone();
                        } catch (CloneNotSupportedException e) {
                            throw new AssertionError(e);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public final int computeSerializedSize() {
                        int computeSerializedSize = super.computeSerializedSize();
                        if (this.step != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.step.intValue());
                        }
                        if (this.previousStepState != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.previousStepState.intValue());
                        }
                        return this.newStepState == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, this.newStepState.intValue());
                    }

                    public final StepStateChange mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                        case 2:
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 7:
                                        case 8:
                                            this.step = Integer.valueOf(readInt32);
                                            break;
                                    }
                                case 16:
                                    int readInt322 = codedInputByteBufferNano.readInt32();
                                    switch (readInt322) {
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4:
                                            this.previousStepState = Integer.valueOf(readInt322);
                                            break;
                                    }
                                case 24:
                                    int readInt323 = codedInputByteBufferNano.readInt32();
                                    switch (readInt323) {
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4:
                                            this.newStepState = Integer.valueOf(readInt323);
                                            break;
                                    }
                                default:
                                    if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                                        break;
                                    } else {
                                        return this;
                                    }
                            }
                        }
                    }

                    public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                        if (this.step != null) {
                            codedOutputByteBufferNano.writeInt32(1, this.step.intValue());
                        }
                        if (this.previousStepState != null) {
                            codedOutputByteBufferNano.writeInt32(2, this.previousStepState.intValue());
                        }
                        if (this.newStepState != null) {
                            codedOutputByteBufferNano.writeInt32(3, this.newStepState.intValue());
                        }
                        super.writeTo(codedOutputByteBufferNano);
                    }
                }

                public static final class View extends ExtendableMessageNano<View> implements Cloneable {
                    private static volatile View[] _emptyArray;
                    public Integer page;
                    public Integer step;

                    public View() {
                        clear();
                    }

                    public static View[] emptyArray() {
                        if (_emptyArray == null) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (_emptyArray == null) {
                                    _emptyArray = new View[0];
                                }
                            }
                        }
                        return _emptyArray;
                    }

                    public static View parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        return new View().mergeFrom(codedInputByteBufferNano);
                    }

                    public static View parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                        return (View) MessageNano.mergeFrom(new View(), bArr);
                    }

                    public final View clear() {
                        this.page = null;
                        this.unknownFieldData = null;
                        this.cachedSize = -1;
                        return this;
                    }

                    public final View clone() {
                        try {
                            return (View) super.clone();
                        } catch (CloneNotSupportedException e) {
                            throw new AssertionError(e);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public final int computeSerializedSize() {
                        int computeSerializedSize = super.computeSerializedSize();
                        if (this.step != null) {
                            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.step.intValue());
                        }
                        return this.page == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, this.page.intValue());
                    }

                    public final View mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                                        case 2:
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 7:
                                        case 8:
                                            this.step = Integer.valueOf(readInt32);
                                            break;
                                    }
                                case 16:
                                    this.page = Integer.valueOf(codedInputByteBufferNano.readInt32());
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

                    public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                        if (this.step != null) {
                            codedOutputByteBufferNano.writeInt32(1, this.step.intValue());
                        }
                        if (this.page != null) {
                            codedOutputByteBufferNano.writeInt32(2, this.page.intValue());
                        }
                        super.writeTo(codedOutputByteBufferNano);
                    }
                }

                public Setup() {
                    clear();
                }

                public static Setup[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Setup[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public static Setup parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Setup().mergeFrom(codedInputByteBufferNano);
                }

                public static Setup parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Setup) MessageNano.mergeFrom(new Setup(), bArr);
                }

                public final Setup clear() {
                    this.view = null;
                    this.stepStateChange = null;
                    this.unknownFieldData = null;
                    this.cachedSize = -1;
                    return this;
                }

                public final Setup clone() {
                    try {
                        Setup setup = (Setup) super.clone();
                        if (this.view != null) {
                            setup.view = this.view.clone();
                        }
                        if (this.stepStateChange != null) {
                            setup.stepStateChange = this.stepStateChange.clone();
                        }
                        return setup;
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError(e);
                    }
                }

                /* access modifiers changed from: protected */
                public final int computeSerializedSize() {
                    int computeSerializedSize = super.computeSerializedSize();
                    if (this.view != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.view);
                    }
                    return this.stepStateChange == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, this.stepStateChange);
                }

                public final Setup mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int readTag = codedInputByteBufferNano.readTag();
                        switch (readTag) {
                            case 0:
                                return this;
                            case 10:
                                if (this.view == null) {
                                    this.view = new View();
                                }
                                codedInputByteBufferNano.readMessage(this.view);
                                break;
                            case 18:
                                if (this.stepStateChange == null) {
                                    this.stepStateChange = new StepStateChange();
                                }
                                codedInputByteBufferNano.readMessage(this.stepStateChange);
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

                public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (this.view != null) {
                        codedOutputByteBufferNano.writeMessage(1, this.view);
                    }
                    if (this.stepStateChange != null) {
                        codedOutputByteBufferNano.writeMessage(2, this.stepStateChange);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
            }

            public VrHome() {
                clear();
            }

            public static VrHome[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new VrHome[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public static VrHome parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new VrHome().mergeFrom(codedInputByteBufferNano);
            }

            public static VrHome parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (VrHome) MessageNano.mergeFrom(new VrHome(), bArr);
            }

            public final VrHome clear() {
                this.setup = null;
                this.unknownFieldData = null;
                this.cachedSize = -1;
                return this;
            }

            public final VrHome clone() {
                try {
                    VrHome vrHome = (VrHome) super.clone();
                    if (this.setup != null) {
                        vrHome.setup = this.setup.clone();
                    }
                    return vrHome;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }

            /* access modifiers changed from: protected */
            public final int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                return this.setup == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, this.setup);
            }

            public final VrHome mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    switch (readTag) {
                        case 0:
                            return this;
                        case 10:
                            if (this.setup == null) {
                                this.setup = new Setup();
                            }
                            codedInputByteBufferNano.readMessage(this.setup);
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

            public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (this.setup != null) {
                    codedOutputByteBufferNano.writeMessage(1, this.setup);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }

        public VREvent() {
            clear();
        }

        public static VREvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new VREvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static VREvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new VREvent().mergeFrom(codedInputByteBufferNano);
        }

        public static VREvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (VREvent) MessageNano.mergeFrom(new VREvent(), bArr);
        }

        public final VREvent clear() {
            this.headMount = null;
            this.application = null;
            this.durationMs = null;
            this.installedVrApplications = Application.emptyArray();
            this.cyclops = null;
            this.qrCodeScan = null;
            this.cohort = null;
            this.performanceStats = null;
            this.sensorStats = null;
            this.audioStats = null;
            this.embedVrWidget = null;
            this.vrCore = null;
            this.earthVr = null;
            this.launcher = null;
            this.keyboard = null;
            this.renderer = null;
            this.lullaby = null;
            this.streetView = null;
            this.photos = null;
            this.vrHome = null;
            this.sdkConfiguration = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final VREvent clone() {
            try {
                VREvent vREvent = (VREvent) super.clone();
                if (this.headMount != null) {
                    vREvent.headMount = this.headMount.clone();
                }
                if (this.application != null) {
                    vREvent.application = this.application.clone();
                }
                if (this.installedVrApplications != null && this.installedVrApplications.length > 0) {
                    vREvent.installedVrApplications = new Application[this.installedVrApplications.length];
                    for (int i = 0; i < this.installedVrApplications.length; i++) {
                        if (this.installedVrApplications[i] != null) {
                            vREvent.installedVrApplications[i] = this.installedVrApplications[i].clone();
                        }
                    }
                }
                if (this.cyclops != null) {
                    vREvent.cyclops = this.cyclops.clone();
                }
                if (this.qrCodeScan != null) {
                    vREvent.qrCodeScan = this.qrCodeScan.clone();
                }
                if (this.performanceStats != null) {
                    vREvent.performanceStats = this.performanceStats.clone();
                }
                if (this.sensorStats != null) {
                    vREvent.sensorStats = this.sensorStats.clone();
                }
                if (this.audioStats != null) {
                    vREvent.audioStats = this.audioStats.clone();
                }
                if (this.embedVrWidget != null) {
                    vREvent.embedVrWidget = this.embedVrWidget.clone();
                }
                if (this.vrCore != null) {
                    vREvent.vrCore = this.vrCore.clone();
                }
                if (this.earthVr != null) {
                    vREvent.earthVr = this.earthVr.clone();
                }
                if (this.launcher != null) {
                    vREvent.launcher = this.launcher.clone();
                }
                if (this.keyboard != null) {
                    vREvent.keyboard = this.keyboard.clone();
                }
                if (this.renderer != null) {
                    vREvent.renderer = this.renderer.clone();
                }
                if (this.lullaby != null) {
                    vREvent.lullaby = this.lullaby.clone();
                }
                if (this.streetView != null) {
                    vREvent.streetView = this.streetView.clone();
                }
                if (this.photos != null) {
                    vREvent.photos = this.photos.clone();
                }
                if (this.vrHome != null) {
                    vREvent.vrHome = this.vrHome.clone();
                }
                if (this.sdkConfiguration != null) {
                    vREvent.sdkConfiguration = this.sdkConfiguration.clone();
                }
                return vREvent;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.headMount != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.headMount);
            }
            if (this.application != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, this.application);
            }
            if (this.durationMs != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, this.durationMs.longValue());
            }
            if (this.installedVrApplications != null && this.installedVrApplications.length > 0) {
                int i = computeSerializedSize;
                for (Application application2 : this.installedVrApplications) {
                    if (application2 != null) {
                        i += CodedOutputByteBufferNano.computeMessageSize(4, application2);
                    }
                }
                computeSerializedSize = i;
            }
            if (this.cyclops != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, this.cyclops);
            }
            if (this.qrCodeScan != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, this.qrCodeScan);
            }
            if (this.cohort != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.cohort);
            }
            if (this.lifetimeCountBucket != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, this.lifetimeCountBucket.intValue());
            }
            if (this.performanceStats != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(9, this.performanceStats);
            }
            if (this.sensorStats != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(10, this.sensorStats);
            }
            if (this.audioStats != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(11, this.audioStats);
            }
            if (this.embedVrWidget != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(12, this.embedVrWidget);
            }
            if (this.vrCore != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(13, this.vrCore);
            }
            if (this.earthVr != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(14, this.earthVr);
            }
            if (this.launcher != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(15, this.launcher);
            }
            if (this.keyboard != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(16, this.keyboard);
            }
            if (this.renderer != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(17, this.renderer);
            }
            if (this.lullaby != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(18, this.lullaby);
            }
            if (this.streetView != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(19, this.streetView);
            }
            if (this.photos != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(20, this.photos);
            }
            if (this.vrHome != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(21, this.vrHome);
            }
            return this.sdkConfiguration == null ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(22, this.sdkConfiguration);
        }

        public final VREvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 10:
                        if (this.headMount == null) {
                            this.headMount = new HeadMount();
                        }
                        codedInputByteBufferNano.readMessage(this.headMount);
                        break;
                    case 18:
                        if (this.application == null) {
                            this.application = new Application();
                        }
                        codedInputByteBufferNano.readMessage(this.application);
                        break;
                    case 24:
                        this.durationMs = Long.valueOf(codedInputByteBufferNano.readInt64());
                        break;
                    case 34:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                        int length = this.installedVrApplications != null ? this.installedVrApplications.length : 0;
                        Application[] applicationArr = new Application[(repeatedFieldArrayLength + length)];
                        if (length != 0) {
                            System.arraycopy(this.installedVrApplications, 0, applicationArr, 0, length);
                        }
                        while (length < applicationArr.length - 1) {
                            applicationArr[length] = new Application();
                            codedInputByteBufferNano.readMessage(applicationArr[length]);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        applicationArr[length] = new Application();
                        codedInputByteBufferNano.readMessage(applicationArr[length]);
                        this.installedVrApplications = applicationArr;
                        break;
                    case 42:
                        if (this.cyclops == null) {
                            this.cyclops = new Cyclops();
                        }
                        codedInputByteBufferNano.readMessage(this.cyclops);
                        break;
                    case 50:
                        if (this.qrCodeScan == null) {
                            this.qrCodeScan = new QrCodeScan();
                        }
                        codedInputByteBufferNano.readMessage(this.qrCodeScan);
                        break;
                    case 58:
                        this.cohort = codedInputByteBufferNano.readString();
                        break;
                    case 64:
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        switch (readInt32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 11:
                            case 21:
                                this.lifetimeCountBucket = Integer.valueOf(readInt32);
                                break;
                        }
                    case 74:
                        if (this.performanceStats == null) {
                            this.performanceStats = new PerformanceStats();
                        }
                        codedInputByteBufferNano.readMessage(this.performanceStats);
                        break;
                    case 82:
                        if (this.sensorStats == null) {
                            this.sensorStats = new SensorStats();
                        }
                        codedInputByteBufferNano.readMessage(this.sensorStats);
                        break;
                    case 90:
                        if (this.audioStats == null) {
                            this.audioStats = new AudioStats();
                        }
                        codedInputByteBufferNano.readMessage(this.audioStats);
                        break;
                    case 98:
                        if (this.embedVrWidget == null) {
                            this.embedVrWidget = new EmbedVrWidget();
                        }
                        codedInputByteBufferNano.readMessage(this.embedVrWidget);
                        break;
                    case 106:
                        if (this.vrCore == null) {
                            this.vrCore = new VrCore();
                        }
                        codedInputByteBufferNano.readMessage(this.vrCore);
                        break;
                    case 114:
                        if (this.earthVr == null) {
                            this.earthVr = new EarthVr();
                        }
                        codedInputByteBufferNano.readMessage(this.earthVr);
                        break;
                    case VrCore.ErrorCode.CONTROLLER_GATT_SERVICE_NOT_FOUND /*122*/:
                        if (this.launcher == null) {
                            this.launcher = new Launcher();
                        }
                        codedInputByteBufferNano.readMessage(this.launcher);
                        break;
                    case 130:
                        if (this.keyboard == null) {
                            this.keyboard = new Keyboard();
                        }
                        codedInputByteBufferNano.readMessage(this.keyboard);
                        break;
                    case 138:
                        if (this.renderer == null) {
                            this.renderer = new Renderer();
                        }
                        codedInputByteBufferNano.readMessage(this.renderer);
                        break;
                    case 146:
                        if (this.lullaby == null) {
                            this.lullaby = new Lullaby();
                        }
                        codedInputByteBufferNano.readMessage(this.lullaby);
                        break;
                    case 154:
                        if (this.streetView == null) {
                            this.streetView = new StreetView();
                        }
                        codedInputByteBufferNano.readMessage(this.streetView);
                        break;
                    case 162:
                        if (this.photos == null) {
                            this.photos = new Photos();
                        }
                        codedInputByteBufferNano.readMessage(this.photos);
                        break;
                    case 170:
                        if (this.vrHome == null) {
                            this.vrHome = new VrHome();
                        }
                        codedInputByteBufferNano.readMessage(this.vrHome);
                        break;
                    case VrCore.ErrorCode.DON_CANCELLED /*178*/:
                        if (this.sdkConfiguration == null) {
                            this.sdkConfiguration = new SdkConfigurationParams();
                        }
                        codedInputByteBufferNano.readMessage(this.sdkConfiguration);
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

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.headMount != null) {
                codedOutputByteBufferNano.writeMessage(1, this.headMount);
            }
            if (this.application != null) {
                codedOutputByteBufferNano.writeMessage(2, this.application);
            }
            if (this.durationMs != null) {
                codedOutputByteBufferNano.writeInt64(3, this.durationMs.longValue());
            }
            if (this.installedVrApplications != null && this.installedVrApplications.length > 0) {
                for (Application application2 : this.installedVrApplications) {
                    if (application2 != null) {
                        codedOutputByteBufferNano.writeMessage(4, application2);
                    }
                }
            }
            if (this.cyclops != null) {
                codedOutputByteBufferNano.writeMessage(5, this.cyclops);
            }
            if (this.qrCodeScan != null) {
                codedOutputByteBufferNano.writeMessage(6, this.qrCodeScan);
            }
            if (this.cohort != null) {
                codedOutputByteBufferNano.writeString(7, this.cohort);
            }
            if (this.lifetimeCountBucket != null) {
                codedOutputByteBufferNano.writeInt32(8, this.lifetimeCountBucket.intValue());
            }
            if (this.performanceStats != null) {
                codedOutputByteBufferNano.writeMessage(9, this.performanceStats);
            }
            if (this.sensorStats != null) {
                codedOutputByteBufferNano.writeMessage(10, this.sensorStats);
            }
            if (this.audioStats != null) {
                codedOutputByteBufferNano.writeMessage(11, this.audioStats);
            }
            if (this.embedVrWidget != null) {
                codedOutputByteBufferNano.writeMessage(12, this.embedVrWidget);
            }
            if (this.vrCore != null) {
                codedOutputByteBufferNano.writeMessage(13, this.vrCore);
            }
            if (this.earthVr != null) {
                codedOutputByteBufferNano.writeMessage(14, this.earthVr);
            }
            if (this.launcher != null) {
                codedOutputByteBufferNano.writeMessage(15, this.launcher);
            }
            if (this.keyboard != null) {
                codedOutputByteBufferNano.writeMessage(16, this.keyboard);
            }
            if (this.renderer != null) {
                codedOutputByteBufferNano.writeMessage(17, this.renderer);
            }
            if (this.lullaby != null) {
                codedOutputByteBufferNano.writeMessage(18, this.lullaby);
            }
            if (this.streetView != null) {
                codedOutputByteBufferNano.writeMessage(19, this.streetView);
            }
            if (this.photos != null) {
                codedOutputByteBufferNano.writeMessage(20, this.photos);
            }
            if (this.vrHome != null) {
                codedOutputByteBufferNano.writeMessage(21, this.vrHome);
            }
            if (this.sdkConfiguration != null) {
                codedOutputByteBufferNano.writeMessage(22, this.sdkConfiguration);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
