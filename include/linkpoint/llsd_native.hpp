#ifndef LINKPOINT_LLSD_NATIVE_HPP
#define LINKPOINT_LLSD_NATIVE_HPP

/**
 * Linkpoint Native LLSD & Wire Protocol Codec Library
 * Standalone C++20 Header-Only Library
 */

#include "llsd_types.hpp"
#include "llsd_binary.hpp"
#include "llsd_xml.hpp"
#include "llsd_notation.hpp"
#include "packet_framing.hpp"

#include <cstdint>
#include <span>
#include <string_view>

namespace linkpoint::llsd {

enum class Format {
    Unknown,
    Binary,
    XML,
    Notation
};

inline Format detect_format(std::span<const uint8_t> data) {
    if (data.empty()) return Format::Unknown;

    std::string_view sv(reinterpret_cast<const char*>(data.data()), data.size());

    if (sv.starts_with("<?llsd/binary?>")) return Format::Binary;
    if (sv.starts_with("<?llsd/notation?>")) return Format::Notation;
    if (sv.starts_with("<?xml") || sv.starts_with("<llsd")) return Format::XML;

    // Skip leading whitespace to check structural character
    size_t i = 0;
    while (i < sv.size() && std::isspace(static_cast<unsigned char>(sv[i]))) i++;
    if (i < sv.size()) {
        char c = sv[i];
        if (c == '<') return Format::XML;
        if (c == '{' || c == '[' || c == '!' || c == 't' || c == 'f' || c == '1' || c == '0' ||
            c == '\'' || c == '"' || c == 'i' || c == 'r' || c == 'u' || c == 'd' || c == 'l' ||
            c == 'b' || c == 's') {
            return Format::Notation;
        }
    }

    return Format::Unknown;
}

inline Value decode(std::span<const uint8_t> data) {
    Format fmt = detect_format(data);
    switch (fmt) {
        case Format::Binary:
            return decode_binary(data);
        case Format::XML:
            return decode_xml(std::string_view(reinterpret_cast<const char*>(data.data()), data.size()));
        case Format::Notation:
            return decode_notation(std::string_view(reinterpret_cast<const char*>(data.data()), data.size()));
        case Format::Unknown:
            // Try Binary, XML, Notation in sequence
            try { return decode_binary(data); } catch (...) {}
            try { return decode_xml(std::string_view(reinterpret_cast<const char*>(data.data()), data.size())); } catch (...) {}
            try { return decode_notation(std::string_view(reinterpret_cast<const char*>(data.data()), data.size())); } catch (...) {}
            return Value{Undefined{}};
    }
    return Value{Undefined{}};
}

inline Value decode(std::string_view data) {
    return decode(std::span<const uint8_t>(reinterpret_cast<const uint8_t*>(data.data()), data.size()));
}

} // namespace linkpoint::llsd

#endif // LINKPOINT_LLSD_NATIVE_HPP
