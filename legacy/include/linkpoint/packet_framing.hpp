#ifndef LINKPOINT_PACKET_FRAMING_HPP
#define LINKPOINT_PACKET_FRAMING_HPP

#include <cstdint>
#include <cstring>
#include <span>
#include <stdexcept>
#include <vector>

namespace linkpoint::protocol {

constexpr uint8_t PACKET_FLAG_ZEROCODED = 0x01;
constexpr uint8_t PACKET_FLAG_RELIABLE  = 0x02;
constexpr uint8_t PACKET_FLAG_RESENT    = 0x04;
constexpr uint8_t PACKET_FLAG_ACKS      = 0x08;

struct PacketHeader {
    uint8_t flags{0};
    uint32_t sequence_number{0};
    uint8_t extra_header_bytes{0};
    uint32_t message_id{0};
    size_t header_size{0};
    std::vector<uint32_t> acks{};

    bool is_zerocoded() const noexcept { return (flags & PACKET_FLAG_ZEROCODED) != 0; }
    bool is_reliable() const noexcept { return (flags & PACKET_FLAG_RELIABLE) != 0; }
    bool is_resent() const noexcept { return (flags & PACKET_FLAG_RESENT) != 0; }
    bool has_acks() const noexcept { return (flags & PACKET_FLAG_ACKS) != 0; }
};

struct DecodedPacket {
    PacketHeader header;
    std::vector<uint8_t> payload;
};

inline void unpack_zerocode(std::span<const uint8_t> src, std::vector<uint8_t>& dest) {
    dest.clear();
    dest.reserve(src.size());
    size_t i = 0;
    while (i < src.size()) {
        uint8_t b = src[i++];
        if (b != 0) {
            dest.push_back(b);
        } else {
            if (i >= src.size()) {
                dest.push_back(0);
                break;
            }
            uint8_t count = src[i++];
            if (count == 0) {
                dest.push_back(0);
            } else {
                dest.insert(dest.end(), static_cast<size_t>(count), static_cast<uint8_t>(0));
            }
        }
    }
}

inline void pack_zerocode(std::span<const uint8_t> src, std::vector<uint8_t>& dest) {
    dest.clear();
    dest.reserve(src.size());
    size_t i = 0;
    while (i < src.size()) {
        uint8_t b = src[i++];
        if (b != 0) {
            dest.push_back(b);
        } else {
            uint8_t zero_count = 1;
            while (i < src.size() && src[i] == 0 && zero_count < 255) {
                zero_count++;
                i++;
            }
            dest.push_back(0);
            dest.push_back(zero_count);
        }
    }
}

inline PacketHeader decode_packet_header(std::span<const uint8_t> raw) {
    if (raw.size() < 6) {
        throw std::runtime_error("Packet too short for header (minimum 6 bytes)");
    }

    PacketHeader header{};
    header.flags = raw[0];

    // Sequence number (4 bytes, big endian)
    header.sequence_number = (static_cast<uint32_t>(raw[1]) << 24) |
                             (static_cast<uint32_t>(raw[2]) << 16) |
                             (static_cast<uint32_t>(raw[3]) << 8)  |
                              static_cast<uint32_t>(raw[4]);

    header.extra_header_bytes = raw[5];
    size_t pos = 6 + static_cast<size_t>(header.extra_header_bytes);

    if (pos >= raw.size()) {
        throw std::runtime_error("Packet header extends past packet boundary");
    }

    // Message ID frequency decode
    uint8_t b0 = raw[pos++];
    if (b0 != 0xFF) {
        // High frequency (1 byte)
        header.message_id = static_cast<uint32_t>(b0);
    } else {
        if (pos >= raw.size()) throw std::runtime_error("Truncated medium/low frequency message ID");
        uint8_t b1 = raw[pos++];
        if (b1 != 0xFF) {
            // Medium frequency (2 bytes)
            header.message_id = (0xFF00) | static_cast<uint32_t>(b1);
        } else {
            // Low frequency (4 bytes)
            if (pos + 2 > raw.size()) throw std::runtime_error("Truncated low frequency message ID");
            uint8_t b2 = raw[pos++];
            uint8_t b3 = raw[pos++];
            header.message_id = (0xFFFF0000) | (static_cast<uint32_t>(b2) << 8) | static_cast<uint32_t>(b3);
        }
    }

    header.header_size = pos;

    // ACKs appended at the end if PACKET_FLAG_ACKS set
    if (header.has_acks() && raw.size() > pos) {
        size_t ack_count = static_cast<size_t>(raw.back());
        size_t acks_size = 1 + ack_count * 4;
        if (raw.size() >= pos + acks_size) {
            size_t acks_start = raw.size() - acks_size;
            for (size_t k = 0; k < ack_count; ++k) {
                size_t p = acks_start + k * 4;
                uint32_t ack_seq = (static_cast<uint32_t>(raw[p]) << 24) |
                                   (static_cast<uint32_t>(raw[p + 1]) << 16) |
                                   (static_cast<uint32_t>(raw[p + 2]) << 8)  |
                                    static_cast<uint32_t>(raw[p + 3]);
                header.acks.push_back(ack_seq);
            }
        }
    }

    return header;
}

inline DecodedPacket decode_packet(std::span<const uint8_t> raw) {
    if (raw.empty()) {
        throw std::runtime_error("Cannot decode empty packet");
    }

    bool is_zerocoded = (raw[0] & PACKET_FLAG_ZEROCODED) != 0;
    std::vector<uint8_t> uncompressed;

    std::span<const uint8_t> packet_span = raw;
    if (is_zerocoded) {
        // First 6 bytes (flags + seq_num + extra_len) are uncompressed
        if (raw.size() < 6) throw std::runtime_error("Zerocoded packet too short");
        uncompressed.insert(uncompressed.end(), raw.begin(), raw.begin() + 6);
        std::vector<uint8_t> body_uncompressed;
        unpack_zerocode(raw.subspan(6), body_uncompressed);
        uncompressed.insert(uncompressed.end(), body_uncompressed.begin(), body_uncompressed.end());
        packet_span = uncompressed;
    }

    PacketHeader header = decode_packet_header(packet_span);

    size_t payload_start = header.header_size;
    size_t payload_end = packet_span.size();
    if (header.has_acks() && !header.acks.empty()) {
        size_t acks_size = 1 + header.acks.size() * 4;
        if (payload_end >= payload_start + acks_size) {
            payload_end -= acks_size;
        }
    }

    std::vector<uint8_t> payload;
    if (payload_end > payload_start) {
        payload.assign(packet_span.begin() + payload_start, packet_span.begin() + payload_end);
    }

    return DecodedPacket{header, std::move(payload)};
}

} // namespace linkpoint::protocol

#endif // LINKPOINT_PACKET_FRAMING_HPP
