/**
 * JNI wrapper for OpenJPEG JPEG2000 decoder
 * Used to decode Second Life textures
 */

#include <jni.h>
#include <android/log.h>
#include <openjpeg-2.5/openjpeg.h>
#include <cstdlib>
#include <cstring>
#include <vector>

#ifdef LINKPOINT_HAVE_ETCPAK
#include "ProcessRGB.hpp"
#endif

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

    int numComponents = image->numcomps;
    if (numComponents <= 0 || image->comps == nullptr) {
        LOGE("Decoded image has no components");
        opj_image_destroy(image);
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }

    // With cp_reduce > 0 OpenJPEG decodes a sub-resolution image; the canvas
    // (x0/x1/y0/y1) stays at full size, but the actual pixel buffer is
    // comps[i].w x comps[i].h. Driving the copy loop from the canvas size
    // walks past the end of the component buffers and corrupts memory.
    // Use the smallest component extent so we never read past any plane.
    int width = image->comps[0].w;
    int height = image->comps[0].h;
    for (int c = 1; c < numComponents; ++c) {
        if (image->comps[c].w < (OPJ_UINT32)width) width = image->comps[c].w;
        if (image->comps[c].h < (OPJ_UINT32)height) height = image->comps[c].h;
    }

    if (width <= 0 || height <= 0) {
        LOGE("Decoded image has invalid dimensions: %dx%d", width, height);
        opj_image_destroy(image);
        opj_stream_destroy(stream);
        opj_destroy_codec(codec);
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }

    LOGI("Decoded image: %dx%d, %d components (canvas %dx%d, reduce=%d)",
         width, height, numComponents,
         image->x1 - image->x0, image->y1 - image->y0, discardLevel);

    // Some component data pointers can be null if decoding failed silently
    // for that plane; bail out before dereferencing them.
    for (int c = 0; c < numComponents; ++c) {
        if (image->comps[c].data == nullptr) {
            LOGE("Component %d has null data buffer", c);
            opj_image_destroy(image);
            opj_stream_destroy(stream);
            opj_destroy_codec(codec);
            env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
            return nullptr;
        }
    }

    // Convert to RGBA
    size_t pixelCount = (size_t)width * (size_t)height;
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

    // Normalise sample values to 8-bit. SL textures usually arrive as 8-bit
    // unsigned, but signed/high-precision streams must be shifted/saturated
    // before truncation, otherwise & 0xFF produces wrap-around garbage.
    auto sampleToByte = [](OPJ_INT32 v, int prec, OPJ_BOOL signedSample) -> uint8_t {
        if (signedSample) {
            v += 1 << (prec - 1);
        }
        if (prec > 8) {
            v >>= (prec - 8);
        } else if (prec < 8 && prec > 0) {
            v <<= (8 - prec);
        }
        if (v < 0) v = 0;
        if (v > 255) v = 255;
        return (uint8_t)v;
    };

    if (numComponents >= 3) {
        const auto& cR = image->comps[0];
        const auto& cG = image->comps[1];
        const auto& cB = image->comps[2];
        const opj_image_comp_t* cA = (numComponents >= 4) ? &image->comps[3] : nullptr;
        for (size_t i = 0; i < pixelCount; i++) {
            rgbaData[i * 4 + 0] = sampleToByte(cR.data[i], cR.prec, cR.sgnd);
            rgbaData[i * 4 + 1] = sampleToByte(cG.data[i], cG.prec, cG.sgnd);
            rgbaData[i * 4 + 2] = sampleToByte(cB.data[i], cB.prec, cB.sgnd);
            rgbaData[i * 4 + 3] = (cA != nullptr)
                ? sampleToByte(cA->data[i], cA->prec, cA->sgnd)
                : (uint8_t)255;
        }
    } else if (numComponents == 1) {
        const auto& c0 = image->comps[0];
        for (size_t i = 0; i < pixelCount; i++) {
            uint8_t gray = sampleToByte(c0.data[i], c0.prec, c0.sgnd);
            rgbaData[i * 4 + 0] = gray;
            rgbaData[i * 4 + 1] = gray;
            rgbaData[i * 4 + 2] = gray;
            rgbaData[i * 4 + 3] = 255;
        }
    } else { // numComponents == 2 (grayscale + alpha)
        const auto& c0 = image->comps[0];
        const auto& c1 = image->comps[1];
        for (size_t i = 0; i < pixelCount; i++) {
            uint8_t gray = sampleToByte(c0.data[i], c0.prec, c0.sgnd);
            rgbaData[i * 4 + 0] = gray;
            rgbaData[i * 4 + 1] = gray;
            rgbaData[i * 4 + 2] = gray;
            rgbaData[i * 4 + 3] = sampleToByte(c1.data[i], c1.prec, c1.sgnd);
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

extern "C" JNIEXPORT jboolean JNICALL
Java_com_linkpoint_assets_JPEG2000Decoder_nativeHealthCheck(
    JNIEnv* env,
    jobject thiz
) {
    // opj_set_default_decoder_parameters leaves decod_format at -1 by design
    // (the caller picks the format before decoding). The previous check
    // gated on decod_format >= 0, which always failed and made every
    // device fall back to placeholder textures. Just verify we can
    // create and destroy a J2K codec; that's all this self-test needs to
    // confirm before letting the real decode paths run.
    opj_dparameters_t params;
    opj_set_default_decoder_parameters(&params);

    opj_codec_t* codec = opj_create_decompress(OPJ_CODEC_J2K);
    if (!codec) {
        LOGE("OpenJPEG health-check failed: unable to create decoder");
        return JNI_FALSE;
    }
    opj_destroy_codec(codec);
    return JNI_TRUE;
}


#ifdef LINKPOINT_HAVE_ETCPAK
extern "C" JNIEXPORT jboolean JNICALL
Java_com_linkpoint_assets_NativeEtcpak_nativeHasEtcpak(
    JNIEnv* env,
    jclass clazz
) {
    (void)env;
    (void)clazz;
    return JNI_TRUE;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_linkpoint_assets_NativeEtcpak_nativeCompressEtc2Rgba(
    JNIEnv* env,
    jclass clazz,
    jbyteArray jrgba,
    jint width,
    jint height,
    jboolean hasAlpha
) {
    (void)clazz;

    if (width <= 0 || height <= 0 || (width % 4) != 0 || (height % 4) != 0) {
        return nullptr;
    }

    const jsize inSize = env->GetArrayLength(jrgba);
    const jsize expected = width * height * 4;
    if (inSize != expected) {
        return nullptr;
    }

    jbyte* rgba = env->GetByteArrayElements(jrgba, nullptr);
    if (!rgba) {
        return nullptr;
    }

    const size_t pixelCount = static_cast<size_t>(width) * static_cast<size_t>(height);
    std::vector<uint32_t> src(pixelCount);
    const uint8_t* in = reinterpret_cast<const uint8_t*>(rgba);
    for (size_t i = 0; i < pixelCount; ++i) {
        const uint8_t r = in[i * 4 + 0];
        const uint8_t g = in[i * 4 + 1];
        const uint8_t b = in[i * 4 + 2];
        const uint8_t a = in[i * 4 + 3];
        src[i] = static_cast<uint32_t>(b)
               | (static_cast<uint32_t>(g) << 8)
               | (static_cast<uint32_t>(r) << 16)
               | (static_cast<uint32_t>(a) << 24);
    }
    env->ReleaseByteArrayElements(jrgba, rgba, JNI_ABORT);

    const uint32_t blocks = static_cast<uint32_t>((width / 4) * (height / 4));
    const size_t blockBytes = hasAlpha ? 16 : 8;
    const size_t outSize = static_cast<size_t>(blocks) * blockBytes;
    std::vector<uint64_t> dst((outSize + 7) / 8);

    if (hasAlpha) {
        CompressEtc2Rgba(src.data(), dst.data(), blocks, static_cast<size_t>(width), true);
    } else {
        CompressEtc2Rgb(src.data(), dst.data(), blocks, static_cast<size_t>(width), true);
    }

    jbyteArray out = env->NewByteArray(static_cast<jsize>(outSize));
    if (!out) {
        return nullptr;
    }
    env->SetByteArrayRegion(out, 0, static_cast<jsize>(outSize),
                            reinterpret_cast<const jbyte*>(dst.data()));
    return out;
}
#endif

// ===================================================================
// JPEG2000 ENCODER
// ===================================================================
//
// Used by AvatarBaker.uploadBakedTexture() to compress baked layered
// textures into the codestream the simulator expects on the
// UploadBakedTexture capability. Without this the upload had to ship
// PNG bytes with an "image/x-j2c" MIME type, which the simulator
// rejects.
//
// Input:  raw RGBA8 pixels (width * height * 4 bytes), little-endian
//         component order R,G,B,A.
// Output: a fresh byte[] containing the J2K codestream, or null on
//         failure (caller falls back to PNG).

typedef struct {
    uint8_t* buf;
    size_t   capacity;
    size_t   size;
} EncBuf;

static OPJ_SIZE_T enc_stream_write(void* p_buffer, OPJ_SIZE_T n, void* userData) {
    EncBuf* eb = (EncBuf*)userData;
    if (eb->size + n > eb->capacity) {
        size_t newCap = eb->capacity == 0 ? 65536 : eb->capacity * 2;
        while (eb->size + n > newCap) newCap *= 2;
        uint8_t* newBuf = (uint8_t*)realloc(eb->buf, newCap);
        if (!newBuf) return (OPJ_SIZE_T)-1;
        eb->buf = newBuf;
        eb->capacity = newCap;
    }
    memcpy(eb->buf + eb->size, p_buffer, n);
    eb->size += n;
    return n;
}

static OPJ_OFF_T enc_stream_skip(OPJ_OFF_T n, void* userData) {
    // Skip-on-write is treated as zero-fill; harmless for OpenJPEG which
    // only seeks to overwrite headers it just wrote.
    EncBuf* eb = (EncBuf*)userData;
    if (eb->size + (size_t)n > eb->capacity) {
        size_t newCap = eb->capacity == 0 ? 65536 : eb->capacity * 2;
        while (eb->size + (size_t)n > newCap) newCap *= 2;
        uint8_t* newBuf = (uint8_t*)realloc(eb->buf, newCap);
        if (!newBuf) return -1;
        eb->buf = newBuf;
        eb->capacity = newCap;
    }
    memset(eb->buf + eb->size, 0, (size_t)n);
    eb->size += (size_t)n;
    return n;
}

static OPJ_BOOL enc_stream_seek(OPJ_OFF_T n, void* userData) {
    EncBuf* eb = (EncBuf*)userData;
    if ((size_t)n > eb->capacity) return OPJ_FALSE;
    eb->size = (size_t)n;
    return OPJ_TRUE;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_linkpoint_assets_JPEG2000Encoder_nativeEncode(
    JNIEnv* env,
    jobject thiz,
    jbyteArray rgbaBytes,
    jint width,
    jint height,
    jboolean lossless
) {
    if (width <= 0 || height <= 0) return nullptr;
    jsize inLen = env->GetArrayLength(rgbaBytes);
    if (inLen < width * height * 4) {
        LOGE("nativeEncode: input too short (%d, need %d)", inLen, width * height * 4);
        return nullptr;
    }
    jbyte* rgba = env->GetByteArrayElements(rgbaBytes, nullptr);
    if (!rgba) return nullptr;

    // Build OpenJPEG image with 4 separate component planes (R, G, B, A).
    opj_image_cmptparm_t cmpt[4];
    memset(cmpt, 0, sizeof(cmpt));
    for (int i = 0; i < 4; i++) {
        cmpt[i].dx = 1;
        cmpt[i].dy = 1;
        cmpt[i].w = width;
        cmpt[i].h = height;
        cmpt[i].x0 = 0;
        cmpt[i].y0 = 0;
        cmpt[i].prec = 8;
        cmpt[i].bpp = 8;
        cmpt[i].sgnd = 0;
    }
    opj_image_t* image = opj_image_create(4, cmpt, OPJ_CLRSPC_SRGB);
    if (!image) {
        env->ReleaseByteArrayElements(rgbaBytes, rgba, JNI_ABORT);
        return nullptr;
    }
    image->x0 = 0; image->y0 = 0;
    image->x1 = width; image->y1 = height;

    const int npix = width * height;
    for (int i = 0; i < npix; i++) {
        image->comps[0].data[i] = (OPJ_INT32)((uint8_t)rgba[i * 4]);
        image->comps[1].data[i] = (OPJ_INT32)((uint8_t)rgba[i * 4 + 1]);
        image->comps[2].data[i] = (OPJ_INT32)((uint8_t)rgba[i * 4 + 2]);
        image->comps[3].data[i] = (OPJ_INT32)((uint8_t)rgba[i * 4 + 3]);
    }
    env->ReleaseByteArrayElements(rgbaBytes, rgba, JNI_ABORT);

    opj_cparameters_t params;
    opj_set_default_encoder_parameters(&params);
    params.tcp_numlayers = 1;
    params.cp_disto_alloc = 1;
    if (lossless) {
        params.tcp_rates[0] = 0;          // 0 == lossless in OpenJPEG
        params.irreversible = 0;
    } else {
        params.tcp_rates[0] = 10.0f;      // ~10:1 quality (LL viewer default)
        params.irreversible = 1;
    }
    params.numresolution = 6;
    params.cod_format = 0;                // 0 = J2K codestream (not JP2)

    opj_codec_t* codec = opj_create_compress(OPJ_CODEC_J2K);
    if (!codec) {
        opj_image_destroy(image);
        return nullptr;
    }
    if (!opj_setup_encoder(codec, &params, image)) {
        opj_destroy_codec(codec);
        opj_image_destroy(image);
        LOGE("opj_setup_encoder failed");
        return nullptr;
    }

    EncBuf eb = { nullptr, 0, 0 };
    opj_stream_t* stream = opj_stream_default_create(OPJ_FALSE); // false = output
    if (!stream) {
        opj_destroy_codec(codec);
        opj_image_destroy(image);
        free(eb.buf);
        return nullptr;
    }
    opj_stream_set_user_data(stream, &eb, nullptr);
    opj_stream_set_user_data_length(stream, 0);
    opj_stream_set_write_function(stream, (opj_stream_write_fn)enc_stream_write);
    opj_stream_set_skip_function(stream, (opj_stream_skip_fn)enc_stream_skip);
    opj_stream_set_seek_function(stream, (opj_stream_seek_fn)enc_stream_seek);

    OPJ_BOOL ok = opj_start_compress(codec, image, stream)
        && opj_encode(codec, stream)
        && opj_end_compress(codec, stream);

    opj_stream_destroy(stream);
    opj_destroy_codec(codec);
    opj_image_destroy(image);

    if (!ok) {
        LOGE("OpenJPEG encode failed");
        free(eb.buf);
        return nullptr;
    }
    LOGI("Encoded J2K: %dx%d -> %zu bytes", width, height, eb.size);

    jbyteArray out = env->NewByteArray((jsize)eb.size);
    if (out) env->SetByteArrayRegion(out, 0, (jsize)eb.size, (jbyte*)eb.buf);
    free(eb.buf);
    return out;
}
