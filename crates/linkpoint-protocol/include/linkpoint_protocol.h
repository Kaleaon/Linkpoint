#ifndef LINKPOINT_PROTOCOL_H
#define LINKPOINT_PROTOCOL_H

#include <stdint.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    uint8_t *ptr;
    size_t len;
    size_t cap;
    int32_t status;
    char *error_ptr;
} LlsdResultBuffer;

void linkpoint_free_buffer(LlsdResultBuffer *buffer_ptr);

LlsdResultBuffer *linkpoint_llsd_parse_xml(const uint8_t *input_ptr, size_t input_len);
LlsdResultBuffer *linkpoint_llsd_parse_binary(const uint8_t *input_ptr, size_t input_len);
LlsdResultBuffer *linkpoint_llsd_parse_notation(const uint8_t *input_ptr, size_t input_len);

LlsdResultBuffer *linkpoint_llsd_serialize_xml(const uint8_t *json_ptr, size_t json_len);
LlsdResultBuffer *linkpoint_llsd_serialize_binary(const uint8_t *json_ptr, size_t json_len);
LlsdResultBuffer *linkpoint_llsd_serialize_notation(const uint8_t *json_ptr, size_t json_len);

#ifdef __cplusplus
}
#endif

#endif /* LINKPOINT_PROTOCOL_H */
