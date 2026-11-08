#ifndef LINKPOINT_LLSD_TYPES_HPP
#define LINKPOINT_LLSD_TYPES_HPP

#include <algorithm>
#include <array>
#include <cctype>
#include <cmath>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <iomanip>
#include <map>
#include <optional>
#include <sstream>
#include <stdexcept>
#include <string>
#include <string_view>
#include <variant>
#include <vector>

namespace linkpoint::llsd {

struct Undefined {
    constexpr bool operator==(const Undefined&) const noexcept = default;
};

struct UUID {
    std::array<uint8_t, 16> bytes{};

    constexpr bool operator==(const UUID& other) const noexcept {
        return bytes == other.bytes;
    }

    std::string to_string() const {
        char buf[37];
        std::snprintf(buf, sizeof(buf),
            "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
            bytes[0], bytes[1], bytes[2], bytes[3],
            bytes[4], bytes[5],
            bytes[6], bytes[7],
            bytes[8], bytes[9],
            bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]);
        return std::string(buf, 36);
    }

    static UUID from_string(std::string_view sv) {
        UUID u{};
        size_t idx = 0;
        uint8_t current_byte = 0;
        bool high_nibble = true;

        for (char c : sv) {
            if (c == '-' || c == '{' || c == '}' || c == '"' || c == '\'') {
                continue;
            }
            int val = -1;
            if (c >= '0' && c <= '9') val = c - '0';
            else if (c >= 'a' && c <= 'f') val = c - 'a' + 10;
            else if (c >= 'A' && c <= 'F') val = c - 'A' + 10;

            if (val >= 0) {
                if (high_nibble) {
                    current_byte = static_cast<uint8_t>(val << 4);
                    high_nibble = false;
                } else {
                    current_byte |= static_cast<uint8_t>(val);
                    if (idx < 16) {
                        u.bytes[idx++] = current_byte;
                    }
                    high_nibble = true;
                }
            }
        }
        return u;
    }

    static UUID null() {
        return UUID{};
    }
};

struct Date {
    double seconds_since_epoch = 0.0;

    constexpr bool operator==(const Date& other) const noexcept {
        return seconds_since_epoch == other.seconds_since_epoch;
    }

    std::string to_iso8601() const {
        if (std::isnan(seconds_since_epoch) || std::isinf(seconds_since_epoch)) {
            return "1970-01-01T00:00:00Z";
        }
        auto total_sec = static_cast<int64_t>(seconds_since_epoch);
        double fract = seconds_since_epoch - static_cast<double>(total_sec);
        if (fract < 0.0) {
            fract += 1.0;
            total_sec -= 1;
        }

        // Days since 1970-01-01
        int64_t days = total_sec / 86400;
        int64_t rem_sec = total_sec % 86400;
        if (rem_sec < 0) {
            rem_sec += 86400;
            days -= 1;
        }

        int hour = static_cast<int>(rem_sec / 3600);
        int minute = static_cast<int>((rem_sec % 3600) / 60);
        int second = static_cast<int>(rem_sec % 60);

        // Civil date algorithm from Euclidean Affine Functions
        int64_t z = days + 719468;
        int64_t era = (z >= 0 ? z : z - 146096) / 146097;
        unsigned doe = static_cast<unsigned>(z - era * 146097);
        unsigned yoe = (doe - doe/1460 + doe/36524 - doe/146096) / 365;
        int64_t y = static_cast<int64_t>(yoe) + era * 400;
        unsigned doy = doe - (365*yoe + yoe/4 - yoe/100);
        unsigned mp = (5*doy + 2)/153;
        unsigned d = doy - (153*mp+2)/5 + 1;
        unsigned m = mp < 10 ? mp + 3 : mp - 9;
        if (m <= 2) y += 1;

        char buf[64];
        if (fract > 0.0001) {
            int millis = static_cast<int>(fract * 1000.0 + 0.5);
            std::snprintf(buf, sizeof(buf), "%04d-%02d-%02dT%02d:%02d:%02d.%03dZ",
                static_cast<int>(y), static_cast<int>(m), static_cast<int>(d),
                hour, minute, second, millis);
        } else {
            std::snprintf(buf, sizeof(buf), "%04d-%02d-%02dT%02d:%02d:%02dZ",
                static_cast<int>(y), static_cast<int>(m), static_cast<int>(d),
                hour, minute, second);
        }
        return std::string(buf);
    }

    static Date from_iso8601(std::string_view sv) {
        int year = 1970, month = 1, day = 1;
        int hour = 0, minute = 0, second = 0;
        double fract = 0.0;

        // Trim leading quotes/spaces
        while (!sv.empty() && (std::isspace(static_cast<unsigned char>(sv.front())) || sv.front() == '"' || sv.front() == '\'')) {
            sv.remove_prefix(1);
        }
        while (!sv.empty() && (std::isspace(static_cast<unsigned char>(sv.back())) || sv.back() == '"' || sv.back() == '\'')) {
            sv.remove_suffix(1);
        }

        if (sv.empty()) return Date{0.0};

        std::string s(sv);
        int parsed = std::sscanf(s.c_str(), "%d-%d-%dT%d:%d:%d", &year, &month, &day, &hour, &minute, &second);
        if (parsed >= 3) {
            size_t dot_pos = s.find('.');
            if (dot_pos != std::string::npos) {
                size_t z_pos = s.find_first_of("Zz+", dot_pos);
                std::string frac_str = s.substr(dot_pos, z_pos != std::string::npos ? z_pos - dot_pos : std::string::npos);
                try {
                    fract = std::stod(frac_str);
                } catch (...) {
                    fract = 0.0;
                }
            }
        }

        // Convert civil date to days
        int y = month <= 2 ? year - 1 : year;
        int m = month <= 2 ? month + 12 : month;
        int era = (y >= 0 ? y : y - 399) / 400;
        unsigned yoe = static_cast<unsigned>(y - era * 400);
        unsigned doy = (153 * (m - 3) + 2) / 5 + day - 1;
        unsigned doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
        int64_t days = era * 146097 + static_cast<int64_t>(doe) - 719468;

        int64_t total_sec = days * 86400 + hour * 3600 + minute * 60 + second;
        return Date{static_cast<double>(total_sec) + fract};
    }
};

struct URI {
    std::string value;

    constexpr bool operator==(const URI& other) const noexcept {
        return value == other.value;
    }
};

class Value;

using Binary = std::vector<uint8_t>;
using Array = std::vector<Value>;
using Map = std::map<std::string, Value>;

enum class Type {
    Undefined,
    Boolean,
    Integer,
    Real,
    String,
    UUID,
    Date,
    URI,
    Binary,
    Array,
    Map
};

class Value {
public:
    using VariantType = std::variant<
        Undefined,
        bool,
        int32_t,
        double,
        std::string,
        linkpoint::llsd::UUID,
        linkpoint::llsd::Date,
        linkpoint::llsd::URI,
        linkpoint::llsd::Binary,
        linkpoint::llsd::Array,
        linkpoint::llsd::Map
    >;

private:
    VariantType data_{Undefined{}};

public:
    Value() noexcept : data_(Undefined{}) {}
    Value(Undefined u) noexcept : data_(u) {}
    Value(bool b) noexcept : data_(b) {}
    Value(int32_t i) noexcept : data_(i) {}
    Value(uint32_t i) noexcept : data_(static_cast<int32_t>(i)) {}
    Value(int64_t i) noexcept : data_(static_cast<int32_t>(i)) {}
    Value(double d) noexcept : data_(d) {}
    Value(const char* s) : data_(std::string(s ? s : "")) {}
    Value(std::string_view sv) : data_(std::string(sv)) {}
    Value(std::string s) : data_(std::move(s)) {}
    Value(linkpoint::llsd::UUID u) noexcept : data_(u) {}
    Value(linkpoint::llsd::Date d) noexcept : data_(d) {}
    Value(linkpoint::llsd::URI u) : data_(std::move(u)) {}
    Value(linkpoint::llsd::Binary b) : data_(std::move(b)) {}
    Value(linkpoint::llsd::Array a) : data_(std::move(a)) {}
    Value(linkpoint::llsd::Map m) : data_(std::move(m)) {}

    Type type() const noexcept {
        return static_cast<Type>(data_.index());
    }

    bool is_undefined() const noexcept { return std::holds_alternative<Undefined>(data_); }
    bool is_boolean() const noexcept { return std::holds_alternative<bool>(data_); }
    bool is_integer() const noexcept { return std::holds_alternative<int32_t>(data_); }
    bool is_real() const noexcept { return std::holds_alternative<double>(data_); }
    bool is_string() const noexcept { return std::holds_alternative<std::string>(data_); }
    bool is_uuid() const noexcept { return std::holds_alternative<linkpoint::llsd::UUID>(data_); }
    bool is_date() const noexcept { return std::holds_alternative<linkpoint::llsd::Date>(data_); }
    bool is_uri() const noexcept { return std::holds_alternative<linkpoint::llsd::URI>(data_); }
    bool is_binary() const noexcept { return std::holds_alternative<linkpoint::llsd::Binary>(data_); }
    bool is_array() const noexcept { return std::holds_alternative<linkpoint::llsd::Array>(data_); }
    bool is_map() const noexcept { return std::holds_alternative<linkpoint::llsd::Map>(data_); }

    const VariantType& variant() const noexcept { return data_; }
    VariantType& variant() noexcept { return data_; }

    bool as_boolean() const {
        if (auto p = std::get_if<bool>(&data_)) return *p;
        if (auto p = std::get_if<int32_t>(&data_)) return *p != 0;
        if (auto p = std::get_if<double>(&data_)) return *p != 0.0 && !std::isnan(*p);
        if (auto p = std::get_if<std::string>(&data_)) {
            return *p == "true" || *p == "1" || *p == "t" || *p == "TRUE" || *p == "T";
        }
        return false;
    }

    int32_t as_integer() const {
        if (auto p = std::get_if<int32_t>(&data_)) return *p;
        if (auto p = std::get_if<bool>(&data_)) return *p ? 1 : 0;
        if (auto p = std::get_if<double>(&data_)) return static_cast<int32_t>(*p);
        if (auto p = std::get_if<std::string>(&data_)) {
            try { return std::stoi(*p); } catch (...) { return 0; }
        }
        return 0;
    }

    double as_real() const {
        if (auto p = std::get_if<double>(&data_)) return *p;
        if (auto p = std::get_if<int32_t>(&data_)) return static_cast<double>(*p);
        if (auto p = std::get_if<bool>(&data_)) return *p ? 1.0 : 0.0;
        if (auto p = std::get_if<std::string>(&data_)) {
            try { return std::stod(*p); } catch (...) { return 0.0; }
        }
        return 0.0;
    }

    std::string as_string() const {
        if (auto p = std::get_if<std::string>(&data_)) return *p;
        if (auto p = std::get_if<bool>(&data_)) return *p ? "true" : "false";
        if (auto p = std::get_if<int32_t>(&data_)) return std::to_string(*p);
        if (auto p = std::get_if<double>(&data_)) {
            if (std::isnan(*p)) return "nan";
            if (std::isinf(*p)) return *p > 0 ? "inf" : "-inf";
            std::ostringstream ss;
            ss << *p;
            return ss.str();
        }
        if (auto p = std::get_if<linkpoint::llsd::UUID>(&data_)) return p->to_string();
        if (auto p = std::get_if<linkpoint::llsd::Date>(&data_)) return p->to_iso8601();
        if (auto p = std::get_if<linkpoint::llsd::URI>(&data_)) return p->value;
        if (auto p = std::get_if<linkpoint::llsd::Binary>(&data_)) {
            return std::string(p->begin(), p->end());
        }
        return "";
    }

    linkpoint::llsd::UUID as_uuid() const {
        if (auto p = std::get_if<linkpoint::llsd::UUID>(&data_)) return *p;
        if (auto p = std::get_if<std::string>(&data_)) return linkpoint::llsd::UUID::from_string(*p);
        return linkpoint::llsd::UUID::null();
    }

    linkpoint::llsd::Date as_date() const {
        if (auto p = std::get_if<linkpoint::llsd::Date>(&data_)) return *p;
        if (auto p = std::get_if<double>(&data_)) return linkpoint::llsd::Date{*p};
        if (auto p = std::get_if<int32_t>(&data_)) return linkpoint::llsd::Date{static_cast<double>(*p)};
        if (auto p = std::get_if<std::string>(&data_)) return linkpoint::llsd::Date::from_iso8601(*p);
        return linkpoint::llsd::Date{0.0};
    }

    linkpoint::llsd::URI as_uri() const {
        if (auto p = std::get_if<linkpoint::llsd::URI>(&data_)) return *p;
        if (auto p = std::get_if<std::string>(&data_)) return linkpoint::llsd::URI{*p};
        return linkpoint::llsd::URI{""};
    }

    linkpoint::llsd::Binary as_binary() const {
        if (auto p = std::get_if<linkpoint::llsd::Binary>(&data_)) return *p;
        if (auto p = std::get_if<std::string>(&data_)) {
            return linkpoint::llsd::Binary(p->begin(), p->end());
        }
        return {};
    }

    const linkpoint::llsd::Array& as_array() const {
        static const linkpoint::llsd::Array empty_array{};
        if (auto p = std::get_if<linkpoint::llsd::Array>(&data_)) return *p;
        return empty_array;
    }

    linkpoint::llsd::Array& as_array_mut() {
        if (is_undefined()) {
            data_ = linkpoint::llsd::Array{};
        }
        if (auto p = std::get_if<linkpoint::llsd::Array>(&data_)) return *p;
        throw std::runtime_error("Value is not an array");
    }

    const linkpoint::llsd::Map& as_map() const {
        static const linkpoint::llsd::Map empty_map{};
        if (auto p = std::get_if<linkpoint::llsd::Map>(&data_)) return *p;
        return empty_map;
    }

    linkpoint::llsd::Map& as_map_mut() {
        if (is_undefined()) {
            data_ = linkpoint::llsd::Map{};
        }
        if (auto p = std::get_if<linkpoint::llsd::Map>(&data_)) return *p;
        throw std::runtime_error("Value is not a map");
    }

    // Indexing
    Value& operator[](size_t index) {
        auto& arr = as_array_mut();
        if (index >= arr.size()) {
            arr.resize(index + 1);
        }
        return arr[index];
    }

    const Value& operator[](size_t index) const {
        static const Value undef_val{Undefined{}};
        const auto& arr = as_array();
        if (index < arr.size()) {
            return arr[index];
        }
        return undef_val;
    }

    Value& operator[](std::string_view key) {
        auto& m = as_map_mut();
        return m[std::string(key)];
    }

    const Value& operator[](std::string_view key) const {
        static const Value undef_val{Undefined{}};
        const auto& m = as_map();
        auto it = m.find(std::string(key));
        if (it != m.end()) {
            return it->second;
        }
        return undef_val;
    }

    bool operator==(const Value& other) const {
        return data_ == other.data_;
    }

    bool operator!=(const Value& other) const {
        return !(*this == other);
    }
};

} // namespace linkpoint::llsd

#endif // LINKPOINT_LLSD_TYPES_HPP
