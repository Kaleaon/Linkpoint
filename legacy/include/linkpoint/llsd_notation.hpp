#ifndef LINKPOINT_LLSD_NOTATION_HPP
#define LINKPOINT_LLSD_NOTATION_HPP

#include "llsd_types.hpp"
#include "llsd_xml.hpp" // for base64_decode, hex_decode, base64_encode
#include <algorithm>
#include <cctype>
#include <sstream>
#include <span>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

namespace linkpoint::llsd {

namespace detail {

constexpr std::string_view NOTATION_COOKIE = "<?llsd/notation?>\n";

class NotationDecoder {
    std::string_view src_;
    size_t pos_{0};

    void skip_whitespace() {
        while (pos_ < src_.size()) {
            char c = src_[pos_];
            if (std::isspace(static_cast<unsigned char>(c)) || c == ',') {
                pos_++;
            } else if (c == '#' || (c == '/' && pos_ + 1 < src_.size() && src_[pos_ + 1] == '/')) {
                size_t nl = src_.find('\n', pos_);
                if (nl == std::string_view::npos) pos_ = src_.size();
                else pos_ = nl + 1;
            } else {
                break;
            }
        }
    }

public:
    explicit NotationDecoder(std::string_view src) : src_(src) {}

    Value decode() {
        if (pos_ == 0 && src_.substr(pos_, NOTATION_COOKIE.size()) == NOTATION_COOKIE) {
            pos_ += NOTATION_COOKIE.size();
        }

        skip_whitespace();
        if (pos_ >= src_.size()) return Value{Undefined{}};

        char c = src_[pos_];
        if (c == '!') {
            pos_++;
            return Value{Undefined{}};
        }

        if (c == 't' || c == 'T') {
            if (src_.substr(pos_, 4) == "true" || src_.substr(pos_, 4) == "TRUE") {
                pos_ += 4;
            } else {
                pos_++;
            }
            return Value{true};
        }

        if (c == 'f' || c == 'F') {
            if (src_.substr(pos_, 5) == "false" || src_.substr(pos_, 5) == "FALSE") {
                pos_ += 5;
            } else {
                pos_++;
            }
            return Value{false};
        }

        if (c == '1' && (pos_ + 1 >= src_.size() || !std::isdigit(static_cast<unsigned char>(src_[pos_ + 1])))) {
            pos_++;
            return Value{true};
        }

        if (c == '0' && (pos_ + 1 >= src_.size() || !std::isdigit(static_cast<unsigned char>(src_[pos_ + 1])))) {
            pos_++;
            return Value{false};
        }

        if (c == 'i') {
            pos_++;
            return Value{parse_integer()};
        }

        if (c == 'r') {
            pos_++;
            return Value{parse_real()};
        }

        if (c == 'u') {
            pos_++;
            if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
                std::string s = parse_quoted_string();
                return Value{UUID::from_string(s)};
            } else {
                std::string s = parse_unquoted_uuid();
                return Value{UUID::from_string(s)};
            }
        }

        if (c == 'd') {
            pos_++;
            if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
                std::string s = parse_quoted_string();
                return Value{Date::from_iso8601(s)};
            } else {
                std::string s = parse_unquoted_token();
                return Value{Date::from_iso8601(s)};
            }
        }

        if (c == 'l') {
            pos_++;
            if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
                std::string s = parse_quoted_string();
                return Value{URI{s}};
            } else {
                std::string s = parse_unquoted_token();
                return Value{URI{s}};
            }
        }

        if (c == 's') {
            pos_++;
            if (pos_ < src_.size() && src_[pos_] == '(') {
                return Value{parse_sized_string()};
            } else if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
                return Value{parse_quoted_string()};
            } else {
                return Value{parse_unquoted_token()};
            }
        }

        if (c == 'b') {
            pos_++;
            if (src_.substr(pos_, 2) == "64") {
                pos_ += 2;
                std::string b64 = parse_quoted_string();
                return Value{base64_decode(b64)};
            } else if (src_.substr(pos_, 2) == "16") {
                pos_ += 2;
                std::string h16 = parse_quoted_string();
                return Value{hex_decode(h16)};
            } else if (pos_ < src_.size() && src_[pos_] == '(') {
                return Value{parse_sized_binary()};
            }
        }

        if (c == '"' || c == '\'') {
            return Value{parse_quoted_string()};
        }

        if (c == '[') {
            pos_++; // Skip '['
            Array arr;
            while (true) {
                skip_whitespace();
                if (pos_ >= src_.size()) break;
                if (src_[pos_] == ']') {
                    pos_++;
                    break;
                }
                arr.push_back(decode());
            }
            return Value{arr};
        }

        if (c == '{') {
            pos_++; // Skip '{'
            Map map;
            while (true) {
                skip_whitespace();
                if (pos_ >= src_.size()) break;
                if (src_[pos_] == '}') {
                    pos_++;
                    break;
                }
                std::string key = parse_key();
                skip_whitespace();
                if (pos_ < src_.size() && (src_[pos_] == ':' || src_[pos_] == '=')) {
                    pos_++; // Skip separator
                }
                Value val = decode();
                map[key] = val;
            }
            return Value{map};
        }

        if (std::isdigit(static_cast<unsigned char>(c)) || c == '-') {
            // Check if real or int
            return Value{parse_integer_or_real()};
        }

        // Unquoted identifier fallback
        return Value{parse_unquoted_token()};
    }

private:
    int32_t parse_integer() {
        skip_whitespace();
        size_t start = pos_;
        if (pos_ < src_.size() && (src_[pos_] == '-' || src_[pos_] == '+')) pos_++;
        while (pos_ < src_.size() && std::isdigit(static_cast<unsigned char>(src_[pos_]))) pos_++;
        std::string s(src_.substr(start, pos_ - start));
        try { return std::stoi(s); } catch (...) { return 0; }
    }

    double parse_real() {
        skip_whitespace();
        size_t start = pos_;
        if (src_.substr(pos_, 3) == "inf") { pos_ += 3; return 1.0 / 0.0; }
        if (src_.substr(pos_, 4) == "-inf") { pos_ += 4; return -1.0 / 0.0; }
        if (src_.substr(pos_, 3) == "nan") { pos_ += 3; return 0.0 / 0.0; }
        if (pos_ < src_.size() && (src_[pos_] == '-' || src_[pos_] == '+')) pos_++;
        while (pos_ < src_.size() && (std::isdigit(static_cast<unsigned char>(src_[pos_])) || src_[pos_] == '.' || src_[pos_] == 'e' || src_[pos_] == 'E')) pos_++;
        std::string s(src_.substr(start, pos_ - start));
        try { return std::stod(s); } catch (...) { return 0.0; }
    }

    Value parse_integer_or_real() {
        skip_whitespace();
        size_t start = pos_;
        bool is_float = false;
        if (pos_ < src_.size() && (src_[pos_] == '-' || src_[pos_] == '+')) pos_++;
        while (pos_ < src_.size() && (std::isdigit(static_cast<unsigned char>(src_[pos_])) || src_[pos_] == '.' || src_[pos_] == 'e' || src_[pos_] == 'E')) {
            if (src_[pos_] == '.' || src_[pos_] == 'e' || src_[pos_] == 'E') is_float = true;
            pos_++;
        }
        std::string s(src_.substr(start, pos_ - start));
        if (is_float) {
            try { return Value{std::stod(s)}; } catch (...) { return Value{0.0}; }
        } else {
            try { return Value{std::stoi(s)}; } catch (...) { return Value{0}; }
        }
    }

    std::string parse_quoted_string() {
        if (pos_ >= src_.size()) return "";
        char quote = src_[pos_++];
        std::string out;
        while (pos_ < src_.size()) {
            char c = src_[pos_++];
            if (c == quote) break;
            if (c == '\\' && pos_ < src_.size()) {
                char esc = src_[pos_++];
                switch (esc) {
                    case 'n': out += '\n'; break;
                    case 'r': out += '\r'; break;
                    case 't': out += '\t'; break;
                    case 'b': out += '\b'; break;
                    case 'f': out += '\f'; break;
                    case '\\': out += '\\'; break;
                    case '"': out += '"'; break;
                    case '\'': out += '\''; break;
                    default: out += esc; break;
                }
            } else {
                out += c;
            }
        }
        return out;
    }

    std::string parse_sized_string() {
        pos_++; // Skip '('
        size_t close_paren = src_.find(')', pos_);
        if (close_paren == std::string_view::npos) return "";
        size_t len = 0;
        try {
            len = std::stoul(std::string(src_.substr(pos_, close_paren - pos_)));
        } catch (...) {
            len = 0;
        }
        pos_ = close_paren + 1;
        if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
            pos_++; // Skip opening quote
        }
        std::string s(src_.substr(pos_, std::min(len, src_.size() - pos_)));
        pos_ += s.size();
        if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
            pos_++; // Skip closing quote
        }
        return s;
    }

    Binary parse_sized_binary() {
        pos_++; // Skip '('
        size_t close_paren = src_.find(')', pos_);
        if (close_paren == std::string_view::npos) return {};
        size_t len = 0;
        try {
            len = std::stoul(std::string(src_.substr(pos_, close_paren - pos_)));
        } catch (...) {
            len = 0;
        }
        pos_ = close_paren + 1;
        if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
            pos_++;
        }
        size_t actual_len = std::min(len, src_.size() - pos_);
        Binary b(src_.begin() + pos_, src_.begin() + pos_ + actual_len);
        pos_ += actual_len;
        if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
            pos_++;
        }
        return b;
    }

    std::string parse_unquoted_uuid() {
        size_t start = pos_;
        while (pos_ < src_.size() && (std::isxdigit(static_cast<unsigned char>(src_[pos_])) || src_[pos_] == '-')) {
            pos_++;
        }
        return std::string(src_.substr(start, pos_ - start));
    }

    std::string parse_unquoted_token() {
        skip_whitespace();
        size_t start = pos_;
        while (pos_ < src_.size() && !std::isspace(static_cast<unsigned char>(src_[pos_])) &&
               src_[pos_] != ',' && src_[pos_] != ':' && src_[pos_] != '=' &&
               src_[pos_] != ']' && src_[pos_] != '}') {
            pos_++;
        }
        return std::string(src_.substr(start, pos_ - start));
    }

    std::string parse_key() {
        skip_whitespace();
        if (pos_ >= src_.size()) return "";
        char c = src_[pos_];
        if (c == '"' || c == '\'') {
            return parse_quoted_string();
        }
        if (c == 's') {
            if (pos_ + 1 < src_.size() && (src_[pos_ + 1] == '"' || src_[pos_ + 1] == '\'')) {
                pos_++;
                return parse_quoted_string();
            }
            if (pos_ + 1 < src_.size() && src_[pos_ + 1] == '(') {
                pos_++;
                return parse_sized_string();
            }
        }
        return parse_unquoted_token();
    }
};

class NotationEncoder {
    std::ostringstream ss_;

public:
    NotationEncoder() = default;

    std::string str() const { return ss_.str(); }

    void encode_header() {
        ss_ << "<?llsd/notation?>\n";
    }

    void encode(const Value& val) {
        switch (val.type()) {
            case Type::Undefined:
                ss_ << "!";
                break;
            case Type::Boolean:
                ss_ << (val.as_boolean() ? "1" : "0");
                break;
            case Type::Integer:
                ss_ << "i" << val.as_integer();
                break;
            case Type::Real:
                ss_ << "r" << val.as_real();
                break;
            case Type::UUID:
                ss_ << "u" << val.as_uuid().to_string();
                break;
            case Type::Date:
                ss_ << "d\"" << val.as_date().to_iso8601() << "\"";
                break;
            case Type::String:
                ss_ << "s'" << escape_string(val.as_string()) << "'";
                break;
            case Type::URI:
                ss_ << "l\"" << escape_string(val.as_uri().value) << "\"";
                break;
            case Type::Binary:
                ss_ << "b64\"" << base64_encode(val.as_binary()) << "\"";
                break;
            case Type::Array: {
                ss_ << "[";
                bool first = true;
                for (const auto& item : val.as_array()) {
                    if (!first) ss_ << ",";
                    first = false;
                    encode(item);
                }
                ss_ << "]";
                break;
            }
            case Type::Map: {
                ss_ << "{";
                bool first = true;
                for (const auto& [key, item] : val.as_map()) {
                    if (!first) ss_ << ",";
                    first = false;
                    if (is_valid_identifier(key)) {
                        ss_ << key << ":";
                    } else {
                        ss_ << "s'" << escape_string(key) << "':";
                    }
                    encode(item);
                }
                ss_ << "}";
                break;
            }
        }
    }

private:
    std::string escape_string(std::string_view sv) {
        std::string out;
        out.reserve(sv.size());
        for (char c : sv) {
            switch (c) {
                case '\'': out += "\\'"; break;
                case '\\': out += "\\\\"; break;
                case '\n': out += "\\n"; break;
                case '\r': out += "\\r"; break;
                case '\t': out += "\\t"; break;
                default: out += c; break;
            }
        }
        return out;
    }

    bool is_valid_identifier(std::string_view sv) {
        if (sv.empty()) return false;
        if (!std::isalpha(static_cast<unsigned char>(sv[0])) && sv[0] != '_') return false;
        for (char c : sv) {
            if (!std::isalnum(static_cast<unsigned char>(c)) && c != '_') return false;
        }
        return true;
    }
};

} // namespace detail

inline Value decode_notation(std::string_view notation_data) {
    detail::NotationDecoder decoder(notation_data);
    return decoder.decode();
}

inline std::string encode_notation(const Value& val, bool include_header = false) {
    detail::NotationEncoder encoder;
    if (include_header) {
        encoder.encode_header();
    }
    encoder.encode(val);
    return encoder.str();
}

} // namespace linkpoint::llsd

#endif // LINKPOINT_LLSD_NOTATION_HPP
