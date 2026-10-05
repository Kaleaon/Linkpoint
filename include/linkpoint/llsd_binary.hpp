#ifndef LINKPOINT_LLSD_BINARY_HPP
#define LINKPOINT_LLSD_BINARY_HPP

#include "llsd_types.hpp"
#include <cstring>
#include <span>
#include <system_error>

namespace linkpoint::llsd {

namespace detail {

constexpr std::string_view BINARY_COOKIE = "<?llsd/binary?>\n";

inline uint32_t read_u32_be(const uint8_t* p) noexcept {
    return (static_cast<uint32_t>(p[0]) << 24) |
           (static_cast<uint32_t>(p[1]) << 16) |
           (static_cast<uint32_t>(p[2]) << 8)  |
            static_cast<uint32_t>(p[3]);
}

inline void write_u32_be(uint8_t* p, uint32_t val) noexcept {
    p[0] = static_cast<uint8_t>((val >> 24) & 0xFF);
    p[1] = static_cast<uint8_t>((val >> 16) & 0xFF);
    p[2] = static_cast<uint8_t>((val >> 8)  & 0xFF);
    p[3] = static_cast<uint8_t>(val & 0xFF);
}

inline double read_f64_be(const uint8_t* p) noexcept {
    uint64_t u = (static_cast<uint64_t>(p[0]) << 56) |
                 (static_cast<uint64_t>(p[1]) << 48) |
                 (static_cast<uint64_t>(p[2]) << 40) |
                 (static_cast<uint64_t>(p[3]) << 32) |
                 (static_cast<uint64_t>(p[4]) << 24) |
                 (static_cast<uint64_t>(p[5]) << 16) |
                 (static_cast<uint64_t>(p[6]) << 8)  |
                  static_cast<uint64_t>(p[7]);
    double d;
    std::memcpy(&d, &u, sizeof(d));
    return d;
}

inline void write_f64_be(uint8_t* p, double d) noexcept {
    uint64_t u;
    std::memcpy(&u, &d, sizeof(u));
    p[0] = static_cast<uint8_t>((u >> 56) & 0xFF);
    p[1] = static_cast<uint8_t>((u >> 48) & 0xFF);
    p[2] = static_cast<uint8_t>((u >> 40) & 0xFF);
    p[3] = static_cast<uint8_t>((u >> 32) & 0xFF);
    p[4] = static_cast<uint8_t>((u >> 24) & 0xFF);
    p[5] = static_cast<uint8_t>((u >> 16) & 0xFF);
    p[6] = static_cast<uint8_t>((u >> 8)  & 0xFF);
    p[7] = static_cast<uint8_t>(u & 0xFF);
}

inline double read_f64_le(const uint8_t* p) noexcept {
    uint64_t u =  static_cast<uint64_t>(p[0])        |
                 (static_cast<uint64_t>(p[1]) << 8)  |
                 (static_cast<uint64_t>(p[2]) << 16) |
                 (static_cast<uint64_t>(p[3]) << 24) |
                 (static_cast<uint64_t>(p[4]) << 32) |
                 (static_cast<uint64_t>(p[5]) << 40) |
                 (static_cast<uint64_t>(p[6]) << 48) |
                 (static_cast<uint64_t>(p[7]) << 56);
    double d;
    std::memcpy(&d, &u, sizeof(d));
    return d;
}

inline void write_f64_le(uint8_t* p, double d) noexcept {
    uint64_t u;
    std::memcpy(&u, &d, sizeof(u));
    p[0] = static_cast<uint8_t>(u & 0xFF);
    p[1] = static_cast<uint8_t>((u >> 8)  & 0xFF);
    p[2] = static_cast<uint8_t>((u >> 16) & 0xFF);
    p[3] = static_cast<uint8_t>((u >> 24) & 0xFF);
    p[4] = static_cast<uint8_t>((u >> 32) & 0xFF);
    p[5] = static_cast<uint8_t>((u >> 40) & 0xFF);
    p[6] = static_cast<uint8_t>((u >> 48) & 0xFF);
    p[7] = static_cast<uint8_t>((u >> 56) & 0xFF);
}

class BinaryDecoder {
    const uint8_t* data_;
    size_t size_;
    size_t pos_{0};

public:
    BinaryDecoder(const uint8_t* data, size_t size) : data_(data), size_(size) {}
    BinaryDecoder(std::span<const uint8_t> bytes) : data_(bytes.data()), size_(bytes.size()) {}

    bool has_more() const noexcept { return pos_ < size_; }
    size_t remaining() const noexcept { return size_ > pos_ ? size_ - pos_ : 0; }

    uint8_t peek_byte() const {
        if (pos_ >= size_) throw std::runtime_error("Unexpected end of binary LLSD stream");
        return data_[pos_];
    }

    uint8_t read_byte() {
        if (pos_ >= size_) throw std::runtime_error("Unexpected end of binary LLSD stream");
        return data_[pos_++];
    }

    void skip_bytes(size_t n) {
        if (pos_ + n > size_) throw std::runtime_error("Unexpected end of binary LLSD stream");
        pos_ += n;
    }

    Value decode() {
        // Check for cookie header
        if (pos_ == 0 && remaining() >= BINARY_COOKIE.size()) {
            if (std::memcmp(data_ + pos_, BINARY_COOKIE.data(), BINARY_COOKIE.size()) == 0) {
                pos_ += BINARY_COOKIE.size();
            }
        }

        if (!has_more()) return Value{Undefined{}};

        uint8_t marker = read_byte();
        switch (marker) {
            case '!':
                return Value{Undefined{}};
            case '1':
            case 't':
                return Value{true};
            case '0':
            case 'f':
                return Value{false};
            case 'i': {
                if (remaining() < 4) throw std::runtime_error("Binary LLSD integer truncated");
                uint32_t u = read_u32_be(data_ + pos_);
                pos_ += 4;
                return Value{static_cast<int32_t>(u)};
            }
            case 'r': {
                if (remaining() < 8) throw std::runtime_error("Binary LLSD real truncated");
                double d = read_f64_be(data_ + pos_);
                pos_ += 8;
                return Value{d};
            }
            case 'u': {
                if (remaining() < 16) throw std::runtime_error("Binary LLSD UUID truncated");
                UUID u{};
                std::memcpy(u.bytes.data(), data_ + pos_, 16);
                pos_ += 16;
                return Value{u};
            }
            case 'd': {
                if (remaining() < 8) throw std::runtime_error("Binary LLSD date truncated");
                double d = read_f64_le(data_ + pos_);
                pos_ += 8;
                return Value{Date{d}};
            }
            case 's': {
                if (remaining() < 4) throw std::runtime_error("Binary LLSD string length truncated");
                uint32_t len = read_u32_be(data_ + pos_);
                pos_ += 4;
                if (remaining() < len) throw std::runtime_error("Binary LLSD string content truncated");
                std::string s(reinterpret_cast<const char*>(data_ + pos_), len);
                pos_ += len;
                return Value{s};
            }
            case 'l': {
                if (remaining() < 4) throw std::runtime_error("Binary LLSD URI length truncated");
                uint32_t len = read_u32_be(data_ + pos_);
                pos_ += 4;
                if (remaining() < len) throw std::runtime_error("Binary LLSD URI content truncated");
                std::string s(reinterpret_cast<const char*>(data_ + pos_), len);
                pos_ += len;
                return Value{URI{s}};
            }
            case 'b': {
                if (remaining() < 4) throw std::runtime_error("Binary LLSD binary length truncated");
                uint32_t len = read_u32_be(data_ + pos_);
                pos_ += 4;
                if (remaining() < len) throw std::runtime_error("Binary LLSD binary content truncated");
                Binary b(data_ + pos_, data_ + pos_ + len);
                pos_ += len;
                return Value{b};
            }
            case '[': {
                if (remaining() < 4) throw std::runtime_error("Binary LLSD array count truncated");
                uint32_t count = read_u32_be(data_ + pos_);
                pos_ += 4;
                Array arr;
                arr.reserve(count);
                for (uint32_t i = 0; i < count; ++i) {
                    arr.push_back(decode());
                }
                if (has_more() && peek_byte() == ']') {
                    read_byte();
                }
                return Value{arr};
            }
            case '{': {
                if (remaining() < 4) throw std::runtime_error("Binary LLSD map count truncated");
                uint32_t count = read_u32_be(data_ + pos_);
                pos_ += 4;
                Map map;
                for (uint32_t i = 0; i < count; ++i) {
                    if (remaining() < 1) throw std::runtime_error("Binary LLSD map key missing");
                    uint8_t k_marker = read_byte();
                    if (k_marker != 'k') throw std::runtime_error("Binary LLSD map key missing 'k' marker");
                    if (remaining() < 4) throw std::runtime_error("Binary LLSD map key length truncated");
                    uint32_t k_len = read_u32_be(data_ + pos_);
                    pos_ += 4;
                    if (remaining() < k_len) throw std::runtime_error("Binary LLSD map key content truncated");
                    std::string key(reinterpret_cast<const char*>(data_ + pos_), k_len);
                    pos_ += k_len;
                    map[key] = decode();
                }
                if (has_more() && peek_byte() == '}') {
                    read_byte();
                }
                return Value{map};
            }
            default:
                throw std::runtime_error("Invalid binary LLSD marker: " + std::to_string(static_cast<int>(marker)));
        }
    }
};

class BinaryEncoder {
    std::vector<uint8_t> out_;

public:
    BinaryEncoder() = default;

    const std::vector<uint8_t>& bytes() const noexcept { return out_; }
    std::vector<uint8_t> take_bytes() noexcept { return std::move(out_); }

    void encode_header() {
        out_.insert(out_.end(), BINARY_COOKIE.begin(), BINARY_COOKIE.end());
    }

    void encode(const Value& val) {
        switch (val.type()) {
            case Type::Undefined:
                out_.push_back('!');
                break;
            case Type::Boolean:
                out_.push_back(val.as_boolean() ? '1' : '0');
                break;
            case Type::Integer: {
                out_.push_back('i');
                size_t p = out_.size();
                out_.resize(p + 4);
                write_u32_be(out_.data() + p, static_cast<uint32_t>(val.as_integer()));
                break;
            }
            case Type::Real: {
                out_.push_back('r');
                size_t p = out_.size();
                out_.resize(p + 8);
                write_f64_be(out_.data() + p, val.as_real());
                break;
            }
            case Type::UUID: {
                out_.push_back('u');
                const auto& bytes = val.as_uuid().bytes;
                out_.insert(out_.end(), bytes.begin(), bytes.end());
                break;
            }
            case Type::Date: {
                out_.push_back('d');
                size_t p = out_.size();
                out_.resize(p + 8);
                write_f64_le(out_.data() + p, val.as_date().seconds_since_epoch);
                break;
            }
            case Type::String: {
                out_.push_back('s');
                std::string s = val.as_string();
                size_t p = out_.size();
                out_.resize(p + 4);
                write_u32_be(out_.data() + p, static_cast<uint32_t>(s.size()));
                out_.insert(out_.end(), s.begin(), s.end());
                break;
            }
            case Type::URI: {
                out_.push_back('l');
                std::string u = val.as_uri().value;
                size_t p = out_.size();
                out_.resize(p + 4);
                write_u32_be(out_.data() + p, static_cast<uint32_t>(u.size()));
                out_.insert(out_.end(), u.begin(), u.end());
                break;
            }
            case Type::Binary: {
                out_.push_back('b');
                Binary b = val.as_binary();
                size_t p = out_.size();
                out_.resize(p + 4);
                write_u32_be(out_.data() + p, static_cast<uint32_t>(b.size()));
                out_.insert(out_.end(), b.begin(), b.end());
                break;
            }
            case Type::Array: {
                out_.push_back('[');
                const auto& arr = val.as_array();
                size_t p = out_.size();
                out_.resize(p + 4);
                write_u32_be(out_.data() + p, static_cast<uint32_t>(arr.size()));
                for (const auto& item : arr) {
                    encode(item);
                }
                out_.push_back(']');
                break;
            }
            case Type::Map: {
                out_.push_back('{');
                const auto& map = val.as_map();
                size_t p = out_.size();
                out_.resize(p + 4);
                write_u32_be(out_.data() + p, static_cast<uint32_t>(map.size()));
                for (const auto& [key, item] : map) {
                    out_.push_back('k');
                    size_t kp = out_.size();
                    out_.resize(kp + 4);
                    write_u32_be(out_.data() + kp, static_cast<uint32_t>(key.size()));
                    out_.insert(out_.end(), key.begin(), key.end());
                    encode(item);
                }
                out_.push_back('}');
                break;
            }
        }
    }
};

} // namespace detail

inline Value decode_binary(std::span<const uint8_t> data) {
    detail::BinaryDecoder decoder(data);
    return decoder.decode();
}

inline std::vector<uint8_t> encode_binary(const Value& val, bool include_header = true) {
    detail::BinaryEncoder encoder;
    if (include_header) {
        encoder.encode_header();
    }
    encoder.encode(val);
    return encoder.take_bytes();
}

} // namespace linkpoint::llsd

#endif // LINKPOINT_LLSD_BINARY_HPP
