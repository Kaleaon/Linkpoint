#ifndef SPATIAL_AUDIO_ENGINE_H
#define SPATIAL_AUDIO_ENGINE_H

#include <cstdint>
#include <cmath>
#include <vector>
#include <mutex>
#include <algorithm>

namespace linkpoint {
namespace audio {

struct Vector3 {
    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;

    Vector3() = default;
    Vector3(float x_, float y_, float z_) : x(x_), y(y_), z(z_) {}

    Vector3 operator-(const Vector3& other) const {
        return Vector3(x - other.x, y - other.y, z - other.z);
    }

    float dot(const Vector3& other) const {
        return x * other.x + y * other.y + z * other.z;
    }

    Vector3 cross(const Vector3& other) const {
        return Vector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }

    float length() const {
        return std::sqrt(x * x + y * y + z * z);
    }

    Vector3 normalized() const {
        float len = length();
        if (len < 0.0001f) return Vector3(0, 0, 0);
        return Vector3(x / len, y / len, z / len);
    }
};

class SpatialAudioEngine {
public:
    SpatialAudioEngine(int sampleRate = 48000, int maxRingBufferFrames = 1440); // ~30ms buffer at 48kHz
    ~SpatialAudioEngine();

    void setListenerPosition(float x, float y, float z,
                            float lookX, float lookY, float lookZ,
                            float upX, float upY, float upZ);

    void setSourcePosition(float x, float y, float z);

    /**
     * Process raw decoded PCM audio packets and calculate 3D spatial matrix pan/gain.
     * Maintains ring buffer latency under 30ms.
     */
    int processAudioPcmBuffer(const int16_t* inputPcm, int inputSamples,
                              int inputChannels, int16_t* outputPcm, int maxOutputSamples);

    float getCurrentLatencyMs() const;

private:
    void calculate3DSpatialMatrix(float& outLeftGain, float& outRightGain);

    int mSampleRate;
    int mMaxRingBufferFrames;
    mutable std::mutex mEngineMutex;

    Vector3 mListenerPos{0, 0, 0};
    Vector3 mListenerLook{0, 1, 0};
    Vector3 mListenerUp{0, 0, 1};
    Vector3 mSourcePos{0, 0, 0};

    float mMinDistance = 1.0f;
    float mMaxDistance = 60.0f;
    float mRolloffFactor = 1.0f;

    std::vector<int16_t> mRingBuffer;
    size_t mBufferReadPos = 0;
    size_t mBufferWritePos = 0;
    size_t mBufferedFrames = 0;
};

} // namespace audio
} // namespace linkpoint

#endif // SPATIAL_AUDIO_ENGINE_H
