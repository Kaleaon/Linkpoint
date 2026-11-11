package com.google.common.logging.nano

import com.google.protobuf.nano.CodedInputByteBufferNano
import com.google.protobuf.nano.CodedOutputByteBufferNano
import com.google.protobuf.nano.ExtendableMessageNano
import com.google.protobuf.nano.InternalNano
import com.google.protobuf.nano.InvalidProtocolBufferNanoException
import com.google.protobuf.nano.MessageNano
import java.io.IOException

// Segment 1: Kotlin translation for VREvent.Application and VREvent.AudioStats. Remaining nested
// types and VREvent fields will be translated in subsequent segments.
interface Vr {
    class VREvent : ExtendableMessageNano<VREvent>(), Cloneable {
        class Application : ExtendableMessageNano<Application>(), Cloneable {
            @JvmField
            var name: String? = null

            @JvmField
            var packageName: String? = null

            @JvmField
            var version: String? = null

            init {
                clear()
            }

            fun clear(): Application {
                packageName = null
                name = null
                version = null
                unknownFieldData = null
                cachedSize = -1
                return this
            }

            public override fun clone(): Application {
                return try {
                    super.clone() as Application
                } catch (e: CloneNotSupportedException) {
                    throw AssertionError(e)
                }
            }

            override fun computeSerializedSize(): Int {
                var size = super.computeSerializedSize()
                packageName?.let { size += CodedOutputByteBufferNano.computeStringSize(1, it) }
                name?.let { size += CodedOutputByteBufferNano.computeStringSize(2, it) }
                version?.let { size += CodedOutputByteBufferNano.computeStringSize(3, it) }
                return size
            }

            @Throws(IOException::class)
            override fun mergeFrom(input: CodedInputByteBufferNano): Application {
                while (true) {
                    when (val tag = input.readTag()) {
                        0 -> return this
                        10 -> packageName = input.readString()
                        18 -> name = input.readString()
                        26 -> version = input.readString()
                        else -> if (!storeUnknownField(input, tag)) {
                            return this
                        }
                    }
                }
            }

            @Throws(IOException::class)
            override fun writeTo(output: CodedOutputByteBufferNano) {
                packageName?.let { output.writeString(1, it) }
                name?.let { output.writeString(2, it) }
                version?.let { output.writeString(3, it) }
                super.writeTo(output)
            }

            companion object {
                @Volatile
                private var _emptyArray: Array<Application>? = null

                @JvmStatic
                fun emptyArray(): Array<Application> {
                    var result = _emptyArray
                    if (result == null) {
                        synchronized(InternalNano.LAZY_INIT_LOCK) {
                            result = _emptyArray
                            if (result == null) {
                                result = kotlin.emptyArray()
                                _emptyArray = result
                            }
                        }
                    }
                    return result ?: kotlin.emptyArray()
                }

                @JvmStatic
                @Throws(IOException::class)
                fun parseFrom(input: CodedInputByteBufferNano): Application = Application().mergeFrom(input)

                @JvmStatic
                @Throws(InvalidProtocolBufferNanoException::class)
                fun parseFrom(data: ByteArray): Application =
                    MessageNano.mergeFrom(Application(), data) as Application
            }
        }

        class AudioStats : ExtendableMessageNano<AudioStats>(), Cloneable {
            @JvmField
            var cpuMeasurementsPercent: Array<HistogramBucket> = HistogramBucket.emptyArray()

            @JvmField
            var framesPerBuffer: Int? = null

            @JvmField
            var numberOfSimultaneousSoundFields: Array<HistogramBucket> = HistogramBucket.emptyArray()

            @JvmField
            var numberOfSimultaneousSoundObjects: Array<HistogramBucket> = HistogramBucket.emptyArray()

            @JvmField
            var renderingMode: Int? = null

            @JvmField
            var renderingTimePerBufferMilliseconds: Array<HistogramBucket> = HistogramBucket.emptyArray()

            @JvmField
            var sampleRate: Int? = null

            interface RenderingMode {
                companion object {
                    const val UNKNOWN = 0
                    const val STEREO_PANNING = 1
                    const val BINAURAL_LOW_QUALITY = 2
                    const val BINAURAL_HIGH_QUALITY = 3
                }
            }

            init {
                clear()
            }

            fun clear(): AudioStats {
                sampleRate = null
                framesPerBuffer = null
                renderingTimePerBufferMilliseconds = HistogramBucket.emptyArray()
                numberOfSimultaneousSoundObjects = HistogramBucket.emptyArray()
                numberOfSimultaneousSoundFields = HistogramBucket.emptyArray()
                cpuMeasurementsPercent = HistogramBucket.emptyArray()
                unknownFieldData = null
                cachedSize = -1
                return this
            }

            public override fun clone(): AudioStats {
                return try {
                    super.clone() as AudioStats
                } catch (e: CloneNotSupportedException) {
                    throw AssertionError(e)
                }
            }

            override fun computeSerializedSize(): Int {
                var size = super.computeSerializedSize()
                sampleRate?.let { size += CodedOutputByteBufferNano.computeInt32Size(1, it) }
                framesPerBuffer?.let { size += CodedOutputByteBufferNano.computeInt32Size(2, it) }
                if (renderingTimePerBufferMilliseconds.isNotEmpty()) {
                    for (value in renderingTimePerBufferMilliseconds) {
                        size += CodedOutputByteBufferNano.computeMessageSize(3, value)
                    }
                }
                if (numberOfSimultaneousSoundObjects.isNotEmpty()) {
                    for (value in numberOfSimultaneousSoundObjects) {
                        size += CodedOutputByteBufferNano.computeMessageSize(4, value)
                    }
                }
                if (numberOfSimultaneousSoundFields.isNotEmpty()) {
                    for (value in numberOfSimultaneousSoundFields) {
                        size += CodedOutputByteBufferNano.computeMessageSize(5, value)
                    }
                }
                if (cpuMeasurementsPercent.isNotEmpty()) {
                    for (value in cpuMeasurementsPercent) {
                        size += CodedOutputByteBufferNano.computeMessageSize(6, value)
                    }
                }
                renderingMode?.let { size += CodedOutputByteBufferNano.computeInt32Size(7, it) }
                return size
            }

            @Throws(IOException::class)
            override fun mergeFrom(input: CodedInputByteBufferNano): AudioStats {
                while (true) {
                    when (val tag = input.readTag()) {
                        0 -> return this
                        8 -> sampleRate = input.readInt32()
                        16 -> framesPerBuffer = input.readInt32()
                        26 -> {
                            val array = renderingTimePerBufferMilliseconds
                            val newArray = arrayOfNulls<HistogramBucket>(array.size + 1)
                            if (array.isNotEmpty()) {
                                System.arraycopy(array, 0, newArray, 0, array.size)
                            }
                            val item = HistogramBucket()
                            input.readMessage(item)
                            newArray[newArray.size - 1] = item
                            renderingTimePerBufferMilliseconds = newArray.filterNotNull().toTypedArray()
                        }
                        34 -> {
                            val array = numberOfSimultaneousSoundObjects
                            val newArray = arrayOfNulls<HistogramBucket>(array.size + 1)
                            if (array.isNotEmpty()) {
                                System.arraycopy(array, 0, newArray, 0, array.size)
                            }
                            val item = HistogramBucket()
                            input.readMessage(item)
                            newArray[newArray.size - 1] = item
                            numberOfSimultaneousSoundObjects = newArray.filterNotNull().toTypedArray()
                        }
                        42 -> {
                            val array = numberOfSimultaneousSoundFields
                            val newArray = arrayOfNulls<HistogramBucket>(array.size + 1)
                            if (array.isNotEmpty()) {
                                System.arraycopy(array, 0, newArray, 0, array.size)
                            }
                            val item = HistogramBucket()
                            input.readMessage(item)
                            newArray[newArray.size - 1] = item
                            numberOfSimultaneousSoundFields = newArray.filterNotNull().toTypedArray()
                        }
                        50 -> {
                            val array = cpuMeasurementsPercent
                            val newArray = arrayOfNulls<HistogramBucket>(array.size + 1)
                            if (array.isNotEmpty()) {
                                System.arraycopy(array, 0, newArray, 0, array.size)
                            }
                            val item = HistogramBucket()
                            input.readMessage(item)
                            newArray[newArray.size - 1] = item
                            cpuMeasurementsPercent = newArray.filterNotNull().toTypedArray()
                        }
                        56 -> renderingMode = input.readInt32()
                        else -> if (!storeUnknownField(input, tag)) {
                            return this
                        }
                    }
                }
            }

            @Throws(IOException::class)
            override fun writeTo(output: CodedOutputByteBufferNano) {
                sampleRate?.let { output.writeInt32(1, it) }
                framesPerBuffer?.let { output.writeInt32(2, it) }
                for (value in renderingTimePerBufferMilliseconds) {
                    output.writeMessage(3, value)
                }
                for (value in numberOfSimultaneousSoundObjects) {
                    output.writeMessage(4, value)
                }
                for (value in numberOfSimultaneousSoundFields) {
                    output.writeMessage(5, value)
                }
                for (value in cpuMeasurementsPercent) {
                    output.writeMessage(6, value)
                }
                renderingMode?.let { output.writeInt32(7, it) }
                super.writeTo(output)
            }

            companion object {
                @Volatile
                private var _emptyArray: Array<AudioStats>? = null

                @JvmStatic
                fun emptyArray(): Array<AudioStats> {
                    var result = _emptyArray
                    if (result == null) {
                        synchronized(InternalNano.LAZY_INIT_LOCK) {
                            result = _emptyArray
                            if (result == null) {
                                result = kotlin.emptyArray()
                                _emptyArray = result
                            }
                        }
                    }
                    return result ?: kotlin.emptyArray()
                }

                @JvmStatic
                @Throws(IOException::class)
                fun parseFrom(input: CodedInputByteBufferNano): AudioStats = AudioStats().mergeFrom(input)

                @JvmStatic
                @Throws(InvalidProtocolBufferNanoException::class)
                fun parseFrom(data: ByteArray): AudioStats =
                    MessageNano.mergeFrom(AudioStats(), data) as AudioStats
            }
        }
    }
}
