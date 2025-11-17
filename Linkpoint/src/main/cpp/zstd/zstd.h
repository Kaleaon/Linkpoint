/*
 * Simple stub header for Zstandard decompression
 * This is a minimal implementation to satisfy the KTX2 transcoder requirements
 * For production use, include the full Zstandard library
 */

#ifndef ZSTD_H_235446
#define ZSTD_H_235446

#ifdef __cplusplus
extern "C" {
#endif

#include <stddef.h>

/* Simple error handling */
typedef size_t ZSTD_ErrorCode;
unsigned ZSTD_isError(ZSTD_ErrorCode code);

/* Decompression function - returns actual uncompressed size or error */
size_t ZSTD_decompress(void* dst, size_t dstCapacity, const void* src, size_t compressedSize);

/* Stub implementations - these will always fail in this stub version */
inline unsigned ZSTD_isError(ZSTD_ErrorCode code) {
    return (code > (size_t)(0-100)); // Consider large values as errors
}

inline size_t ZSTD_decompress(void* dst, size_t dstCapacity, const void* src, size_t compressedSize) {
    (void)dst; (void)dstCapacity; (void)src; (void)compressedSize;
    // Return an error code to indicate Zstd supercompression is not supported
    return (size_t)(0-1); // Generic error
}

#ifdef __cplusplus
}
#endif

#endif /* ZSTD_H_235446 */