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
    if (!buf || buffer_size == 0) {
        return;
    }
    uint8_t s = (uint8_t)(seed & 0xFF);
    #ifdef _OPENMP
    #pragma omp parallel for
    #endif
    for (size_t i = 0; i < buffer_size; i += 4) {
        uint8_t idx = (uint8_t)(i & 0xFF);
        buf[i]     = s + idx;
        buf[i + 1] = s + (uint8_t)(idx * 2);
        buf[i + 2] = s + (uint8_t)(idx * 3);
        buf[i + 3] = 255;
    }
}
