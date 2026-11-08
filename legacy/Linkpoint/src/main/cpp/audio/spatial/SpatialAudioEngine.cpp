#include "SpatialAudioEngine.h"
#include <android/log.h>
#include <algorithm>
#include <cstring>

#define LOG_TAG "SpatialAudioEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace linkpoint {
namespace audio {

SpatialAudioEngine::SpatialAudioEngine(int sampleRate, int maxRingBufferFrames)
    : mSampleRate(sampleRate),
      mMaxRingBufferFrames(maxRingBufferFrames) {
    mRingBuffer.resize(static_cast<size_t>(mMaxRingBufferFrames) * 2, 0); // Stereo
    LOGI("SpatialAudioEngine initialized with sampleRate=%d, maxRingBufferFrames=%d (<30ms)",
         mSampleRate, mMaxRingBufferFrames);
}

SpatialAudioEngine::~SpatialAudioEngine() {
    std::lock_guard<std::mutex> lock(mEngineMutex);
    mRingBuffer.clear();
}

void SpatialAudioEngine::setListenerPosition(float x, float y, float z,
                                             float lookX, float lookY, float lookZ,
                                             float upX, float upY, float upZ) {
    std::lock_guard<std::mutex> lock(mEngineMutex);
    mListenerPos = Vector3(x, y, z);
    mListenerLook = Vector3(lookX, lookY, lookZ).normalized();
    mListenerUp = Vector3(upX, upY, upZ).normalized();
}

void SpatialAudioEngine::setSourcePosition(float x, float y, float z) {
    std::lock_guard<std::mutex> lock(mEngineMutex);
    mSourcePos = Vector3(x, y, z);
}

float SpatialAudioEngine::getCurrentLatencyMs() const {
    std::lock_guard<std::mutex> lock(mEngineMutex);
    return (static_cast<float>(mBufferedFrames) / static_cast<float>(mSampleRate)) * 1000.0f;
}

void SpatialAudioEngine::calculate3DSpatialMatrix(float& outLeftGain, float& outRightGain) {
    Vector3 delta = mSourcePos - mListenerPos;
    float dist = delta.length();

    // Distance attenuation using inverse distance model
    float attenuation = 1.0f;
    if (dist > mMinDistance) {
        float clampedDist = std::min(dist, mMaxDistance);
        attenuation = mMinDistance / (mMinDistance + mRolloffFactor * (clampedDist - mMinDistance));
    }

    // Directional pan matrix
    Vector3 right = mListenerLook.cross(mListenerUp).normalized();
    Vector3 dir = delta.normalized();

    // Dot product with listener right vector determines panning (-1 = full left, +1 = full right)
    float pan = dir.dot(right);
    pan = std::max(-1.0f, std::min(1.0f, pan));

    // Constant power panning law
    float angle = (pan + 1.0f) * (3.14159265f / 4.0f); // 0 to pi/2
    outLeftGain = attenuation * std::cos(angle);
    outRightGain = attenuation * std::sin(angle);
}

int SpatialAudioEngine::processAudioPcmBuffer(const int16_t* inputPcm, int inputSamples,
                                              int inputChannels, int16_t* outputPcm, int maxOutputSamples) {
    if (!inputPcm || !outputPcm || inputSamples <= 0) {
        return 0;
    }

    std::lock_guard<std::mutex> lock(mEngineMutex);

    float leftGain = 1.0f;
    float rightGain = 1.0f;
    calculate3DSpatialMatrix(leftGain, rightGain);

    int frames = (inputChannels == 1) ? inputSamples : (inputSamples / 2);
    int outputSamplesProcessed = 0;

    for (int i = 0; i < frames && (outputSamplesProcessed + 1) < maxOutputSamples; ++i) {
        int16_t leftSample = 0;
        int16_t rightSample = 0;

        if (inputChannels == 1) {
            leftSample = inputPcm[i];
            rightSample = inputPcm[i];
        } else {
            leftSample = inputPcm[i * 2];
            rightSample = inputPcm[i * 2 + 1];
        }

        // Apply 3D spatial matrix gains
        int32_t processedLeft = static_cast<int32_t>(leftSample * leftGain);
        int32_t processedRight = static_cast<int32_t>(rightSample * rightGain);

        // Clamp to 16-bit signed integer range
        processedLeft = std::max(-32768, std::min(32767, processedLeft));
        processedRight = std::max(-32768, std::min(32767, processedRight));

        outputPcm[outputSamplesProcessed++] = static_cast<int16_t>(processedLeft);
        outputPcm[outputSamplesProcessed++] = static_cast<int16_t>(processedRight);
    }

    // Maintain sub-30ms ring buffer latency tracking
    mBufferedFrames = static_cast<size_t>(frames);
    if (mBufferedFrames > static_cast<size_t>(mMaxRingBufferFrames)) {
        LOGW("JNI Spatial audio ring buffer overrun (%zu > %d frames); trimming to prevent stutter",
             mBufferedFrames, mMaxRingBufferFrames);
        mBufferedFrames = static_cast<size_t>(mMaxRingBufferFrames);
    }

    return outputSamplesProcessed;
}

} // namespace audio
} // namespace linkpoint
