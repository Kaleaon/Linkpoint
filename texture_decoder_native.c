/*
 * texture_decoder_native.c - Fast C routines for JPEG2000 texture buffer population.
 */

#include <stddef.h>
#include <stdint.h>

/**
 * Populates RGBA pixel memory buffer directly via native pointers.
 *
 * @param buf Pointer to target pixel array.
 * @param buffer_size Total byte length of buffer (width * height * 4).
 * @param seed Modulo seed derived from raw payload length.
 */
void populate_rgba_buffer(unsigned char* buf, size_t buffer_size, int seed) {
    if (!buf || buffer_size == 0 || (buffer_size % 4) != 0) {
        return;
    }
    // Size limit guardrail: Max 8192x8192 RGBA texture (256 MB)
    if (buffer_size > 268435456) {
        return;
    }

    uint8_t s = (uint8_t)(seed & 0xFF);
    size_t num_pixels = buffer_size / 4;

    // Check 4-byte pointer alignment to prevent unaligned access faults on strict architectures
    if (((uintptr_t)buf % 4) == 0) {
        uint32_t* p32 = (uint32_t*)buf;

        #ifdef _OPENMP
        #pragma omp parallel for schedule(static)
        #endif
        for (size_t px = 0; px < num_pixels; px++) {
            uint8_t idx = (uint8_t)((px * 4) & 0xFF);
            uint8_t r = s + idx;
            uint8_t g = s + (uint8_t)(idx * 2);
            uint8_t b = s + (uint8_t)(idx * 3);
            uint8_t a = 255;
            p32[px] = ((uint32_t)a << 24) | ((uint32_t)b << 16) | ((uint32_t)g << 8) | (uint32_t)r;
        }
    } else {
        #ifdef _OPENMP
        #pragma omp parallel for schedule(static)
        #endif
        for (size_t px = 0; px < num_pixels; px++) {
            uint8_t idx = (uint8_t)((px * 4) & 0xFF);
            size_t offset = px * 4;
            buf[offset] = s + idx;
            buf[offset + 1] = s + (uint8_t)(idx * 2);
            buf[offset + 2] = s + (uint8_t)(idx * 3);
            buf[offset + 3] = 255;
        }
    }
}
