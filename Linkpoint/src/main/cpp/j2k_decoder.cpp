/**
 * JNI wrapper for OpenJPEG JPEG2000 decoder
 * Used to decode Second Life textures
 */

#include <jni.h>
#include <android/log.h>
#include <openjpeg-2.5/openjpeg.h>
#include <cstdlib>
#include <cstring>

#define LOG_TAG "J2KDecoder"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Stream read callback for OpenJPEG
typedef struct {
    const uint8_t* data;
    size_t size;
    size_t offset;
} MemoryStream;

static OPJ_SIZE_T stream_read(void* buffer, OPJ_SIZE_T numBytes, void* userData) {
    MemoryStream* stream = (MemoryStream*)userData;
    
    if (stream->offset >= stream->size) {
        return (OPJ_SIZE_T)-1;
    }
    
    OPJ_SIZE_T available = stream->size - stream->offset;
    OPJ_SIZE_T toRead = (numBytes < available) ? numBytes : available;
    
    memcpy(buffer, stream->data + stream->offset, toRead);
    stream->offset += toRead;
    
    return toRead;
}

static OPJ_OFF_T stream_skip(OPJ_OFF_T numBytes, void* userData) {
    MemoryStream* stream = (MemoryStream*)userData;
    
    if (numBytes < 0) {
        return -1;
    }
    
    OPJ_SIZE_T available = stream->size - stream->offset;
    OPJ_SIZE_T toSkip = ((OPJ_SIZE_T)numBytes < available) ? (OPJ_SIZE_T)numBytes : available;
    
    stream->offset += toSkip;
    return (OPJ_OFF_T)toSkip;
}

static OPJ_BOOL stream_seek(OPJ_OFF_T offset, void* userData) {
    MemoryStream* stream = (MemoryStream*)userData;
    
    if (offset < 0 || (OPJ_SIZE_T)offset > stream->size) {
        return OPJ_FALSE;
    }
    
    stream->offset = (size_t)offset;
    return OPJ_TRUE;
}

static void error_callback(const char* msg, void* userData) {
    LOGE("OpenJPEG error: %s", msg);
}

static void warning_callback(const char* msg, void* userData) {
    LOGW("OpenJPEG warning: %s", msg);
}

static void info_callback(const char* msg, void* userData) {
    // Suppress info messages in release
    // LOGI("OpenJPEG: %s", msg);
}

// Detect codec type from data
static OPJ_CODEC_FORMAT detectCodecFormat(const uint8_t* data, size_t size) {
    if (size < 12) {
        return OPJ_CODEC_UNKNOWN;
    }
    
    // Check for JP2 file format (starts with JP2 signature box)
    if (data[0] == 0x00 && data[1] == 0x00 && data[2] == 0x00 && data[3] == 0x0C &&
        data[4] == 0x6A && data[5] == 0x50 && data[6] == 0x20 && data[7] == 0x20) {
        return OPJ_CODEC_JP2;
    }
    
    // Check for J2K codestream (starts with SOC marker)
    if (data[0] == 0xFF && data[1] == 0x4F && data[2] == 0xFF && data[3] == 0x51) {
        return OPJ_CODEC_J2K;
    }
    
    // Try J2K anyway if unclear
    if (data[0] == 0xFF && data[1] == 0x4F) {
        return OPJ_CODEC_J2K;
    }
    
    return OPJ_CODEC_UNKNOWN;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_linkpoint_assets_JPEG2000Decoder_nativeDecode(
    JNIEnv* env,
    jobject thiz,
    jbyteArray jdata,
    jint discardLevel
) {
    jsize dataSize = env->GetArrayLength(jdata);
    if (dataSize < 12) {
        LOGE("Input data too small: %d bytes", dataSize);
        return nullptr;
    }
    
    jbyte* dataPtr = env->GetByteArrayElements(jdata, nullptr);
    if (!dataPtr) {
        LOGE("Failed to get byte array elements");
        return nullptr;
    }
    
    const uint8_t* data = reinterpret_cast<const uint8_t*>(dataPtr);
    
    // Detect format
    OPJ_CODEC_FORMAT format = detectCodecFormat(data, dataSize);
    if (format == OPJ_CODEC_UNKNOWN) {
        LOGE("Unknown JPEG2000 format");
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    // Create decoder
    opj_codec_t* codec = opj_create_decompress(format);
    if (!codec) {
        LOGE("Failed to create decoder");
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    // Set callbacks
    opj_set_error_handler(codec, error_callback, nullptr);
    opj_set_warning_handler(codec, warning_callback, nullptr);
    opj_set_info_handler(codec, info_callback, nullptr);
    
    // Setup decoder parameters
    opj_dparameters_t params;
    opj_set_default_decoder_parameters(&params);
    params.cp_reduce = discardLevel; // Reduction factor
    
    if (!opj_setup_decoder(codec, &params)) {
        LOGE("Failed to setup decoder");
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    // Create memory stream
    MemoryStream memStream = { data, (size_t)dataSize, 0 };
    
    opj_stream_t* stream = opj_stream_default_create(OPJ_TRUE);
    if (!stream) {
        LOGE("Failed to create stream");
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    opj_stream_set_read_function(stream, stream_read);
    opj_stream_set_skip_function(stream, stream_skip);
    opj_stream_set_seek_function(stream, stream_seek);
    opj_stream_set_user_data(stream, &memStream, nullptr);
    opj_stream_set_user_data_length(stream, dataSize);
    
    // Read header
    opj_image_t* image = nullptr;
    if (!opj_read_header(stream, codec, &image)) {
        LOGE("Failed to read header");
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    // Decode the image
    if (!opj_decode(codec, stream, image)) {
        LOGE("Failed to decode image");
        opj_image_destroy(image);
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    opj_end_decompress(codec, stream);
    
    // Get image info
    int width = image->x1 - image->x0;
    int height = image->y1 - image->y0;
    int numComponents = image->numcomps;
    
    LOGI("Decoded image: %dx%d, %d components", width, height, numComponents);
    
    // Convert to RGBA
    size_t pixelCount = width * height;
    size_t rgbaSize = pixelCount * 4;
    uint8_t* rgbaData = (uint8_t*)malloc(rgbaSize);
    
    if (!rgbaData) {
        LOGE("Failed to allocate output buffer");
        opj_image_destroy(image);
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    // Handle different component counts
    if (numComponents >= 3) {
        // RGB or RGBA
        for (size_t i = 0; i < pixelCount; i++) {
            rgbaData[i * 4 + 0] = (uint8_t)(image->comps[0].data[i] & 0xFF); // R
            rgbaData[i * 4 + 1] = (uint8_t)(image->comps[1].data[i] & 0xFF); // G
            rgbaData[i * 4 + 2] = (uint8_t)(image->comps[2].data[i] & 0xFF); // B
            rgbaData[i * 4 + 3] = (numComponents >= 4) ? 
                (uint8_t)(image->comps[3].data[i] & 0xFF) : 255; // A
        }
    } else if (numComponents == 1) {
        // Grayscale
        for (size_t i = 0; i < pixelCount; i++) {
            uint8_t gray = (uint8_t)(image->comps[0].data[i] & 0xFF);
            rgbaData[i * 4 + 0] = gray;
            rgbaData[i * 4 + 1] = gray;
            rgbaData[i * 4 + 2] = gray;
            rgbaData[i * 4 + 3] = 255;
        }
    } else if (numComponents == 2) {
        // Grayscale + Alpha
        for (size_t i = 0; i < pixelCount; i++) {
            uint8_t gray = (uint8_t)(image->comps[0].data[i] & 0xFF);
            rgbaData[i * 4 + 0] = gray;
            rgbaData[i * 4 + 1] = gray;
            rgbaData[i * 4 + 2] = gray;
            rgbaData[i * 4 + 3] = (uint8_t)(image->comps[1].data[i] & 0xFF);
        }
    }
    
    // Cleanup OpenJPEG
    opj_image_destroy(image);
    opj_stream_destroy(stream);
    opj_destroy_codec(codec);
    env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
    
    // Create result object
    jclass resultClass = env->FindClass("com/linkpoint/assets/JPEG2000Decoder$DecodeResult");
    if (!resultClass) {
        LOGE("Failed to find DecodeResult class");
        free(rgbaData);
        return nullptr;
    }
    
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(III[B)V");
    if (!constructor) {
        LOGE("Failed to find DecodeResult constructor");
        free(rgbaData);
        return nullptr;
    }
    
    // Create byte array for pixels
    jbyteArray pixelArray = env->NewByteArray(rgbaSize);
    if (!pixelArray) {
        LOGE("Failed to create pixel array");
        free(rgbaData);
        return nullptr;
    }
    
    env->SetByteArrayRegion(pixelArray, 0, rgbaSize, reinterpret_cast<jbyte*>(rgbaData));
    free(rgbaData);
    
    // Create result object
    jobject result = env->NewObject(resultClass, constructor, width, height, 4, pixelArray);
    
    return result;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_linkpoint_assets_JPEG2000Decoder_nativeGetImageSize(
    JNIEnv* env,
    jobject thiz,
    jbyteArray jdata
) {
    jsize dataSize = env->GetArrayLength(jdata);
    if (dataSize < 50) {
        return nullptr;
    }
    
    jbyte* dataPtr = env->GetByteArrayElements(jdata, nullptr);
    if (!dataPtr) {
        return nullptr;
    }
    
    const uint8_t* data = reinterpret_cast<const uint8_t*>(dataPtr);
    
    // Detect format
    OPJ_CODEC_FORMAT format = detectCodecFormat(data, dataSize);
    if (format == OPJ_CODEC_UNKNOWN) {
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    // Create decoder
    opj_codec_t* codec = opj_create_decompress(format);
    if (!codec) {
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    opj_dparameters_t params;
    opj_set_default_decoder_parameters(&params);
    opj_setup_decoder(codec, &params);
    
    // Create memory stream
    MemoryStream memStream = { data, (size_t)dataSize, 0 };
    
    opj_stream_t* stream = opj_stream_default_create(OPJ_TRUE);
    opj_stream_set_read_function(stream, stream_read);
    opj_stream_set_skip_function(stream, stream_skip);
    opj_stream_set_seek_function(stream, stream_seek);
    opj_stream_set_user_data(stream, &memStream, nullptr);
    opj_stream_set_user_data_length(stream, dataSize);
    
    // Read header only
    opj_image_t* image = nullptr;
    if (!opj_read_header(stream, codec, &image)) {
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }
    
    int width = image->x1 - image->x0;
    int height = image->y1 - image->y0;
    
    opj_image_destroy(image);
    opj_stream_destroy(stream);
    opj_destroy_codec(codec);
    env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
    
    // Create Pair<Int, Int>
    jclass pairClass = env->FindClass("kotlin/Pair");
    jmethodID pairConstructor = env->GetMethodID(pairClass, "<init>", 
        "(Ljava/lang/Object;Ljava/lang/Object;)V");
    
    jclass intClass = env->FindClass("java/lang/Integer");
    jmethodID intConstructor = env->GetMethodID(intClass, "<init>", "(I)V");
    
    jobject widthObj = env->NewObject(intClass, intConstructor, width);
    jobject heightObj = env->NewObject(intClass, intConstructor, height);
    
    return env->NewObject(pairClass, pairConstructor, widthObj, heightObj);
}
