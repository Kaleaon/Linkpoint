#ifndef LINKPOINT_LLSD_XML_HPP
#define LINKPOINT_LLSD_XML_HPP

#include "llsd_types.hpp"
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

inline std::string base64_encode(const std::vector<uint8_t>& data) {
    static constexpr char b64_table[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    std::string out;
    out.reserve(((data.size() + 2) / 3) * 4);

    size_t i = 0;
    while (i < data.size()) {
        uint32_t b0 = data[i++];
        uint32_t b1 = (i < data.size()) ? data[i++] : 0;
        uint32_t b2 = (i < data.size()) ? data[i++] : 0;

        uint32_t triple = (b0 << 16) | (b1 << 8) | b2;

        out.push_back(b64_table[(triple >> 18) & 0x3F]);
        out.push_back(b64_table[(triple >> 12) & 0x3F]);
        out.push_back((i - 1 > data.size()) ? '=' : b64_table[(triple >> 6) & 0x3F]);
        out.push_back((i > data.size()) ? '=' : b64_table[triple & 0x3F]);
    }
    return out;
}

inline std::vector<uint8_t> base64_decode(std::string_view in) {
    std::vector<uint8_t> out;
    std::vector<int> T(256, -1);
    for (int i = 0; i < 64; ++i) {
        T[static_cast<unsigned char>("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"[i])] = i;
    }

    int val = 0, valb = -8;
    for (char c : in) {
        if (std::isspace(static_cast<unsigned char>(c)) || c == '=') continue;
        int d = T[static_cast<unsigned char>(c)];
        if (d == -1) break;
        val = (val << 6) | d;
        valb += 6;
        if (valb >= 0) {
            out.push_back(static_cast<uint8_t>((val >> valb) & 0xFF));
            valb -= 8;
        }
    }
    return out;
}

inline std::string hex_encode(const std::vector<uint8_t>& data) {
    static constexpr char hex_table[] = "0123456789abcdef";
    std::string out;
    out.reserve(data.size() * 2);
    for (uint8_t b : data) {
        out.push_back(hex_table[(b >> 4) & 0x0F]);
        out.push_back(hex_table[b & 0x0F]);
    }
    return out;
}

inline std::vector<uint8_t> hex_decode(std::string_view in) {
    std::vector<uint8_t> out;
    out.reserve(in.size() / 2);
    uint8_t byte = 0;
    bool high = true;
    for (char c : in) {
        if (std::isspace(static_cast<unsigned char>(c))) continue;
        int val = -1;
        if (c >= '0' && c <= '9') val = c - '0';
        else if (c >= 'a' && c <= 'f') val = c - 'a' + 10;
        else if (c >= 'A' && c <= 'F') val = c - 'A' + 10;
        if (val >= 0) {
            if (high) {
                byte = static_cast<uint8_t>(val << 4);
                high = false;
            } else {
                byte |= static_cast<uint8_t>(val);
                out.push_back(byte);
                high = true;
            }
        }
    }
    return out;
}

inline std::string xml_unescape(std::string_view sv) {
    std::string out;
    out.reserve(sv.size());
    size_t i = 0;
    while (i < sv.size()) {
        if (sv[i] == '&') {
            if (sv.substr(i, 5) == "&amp;") { out += '&'; i += 5; }
            else if (sv.substr(i, 4) == "&lt;") { out += '<'; i += 4; }
            else if (sv.substr(i, 4) == "&gt;") { out += '>'; i += 4; }
            else if (sv.substr(i, 6) == "&quot;") { out += '"'; i += 6; }
            else if (sv.substr(i, 6) == "&apos;") { out += '\''; i += 6; }
            else { out += sv[i++]; }
        } else {
            out += sv[i++];
        }
    }
    return out;
}

inline std::string xml_escape(std::string_view sv) {
    std::string out;
    out.reserve(sv.size());
    for (char c : sv) {
        switch (c) {
            case '&': out += "&amp;"; break;
            case '<': out += "&lt;"; break;
            case '>': out += "&gt;"; break;
            case '"': out += "&quot;"; break;
            case '\'': out += "&apos;"; break;
            default: out += c; break;
        }
    }
    return out;
}

enum class XmlTokenType {
    StartTag,
    EndTag,
    EmptyTag,
    Text,
    Eof
};

struct XmlToken {
    XmlTokenType type{XmlTokenType::Eof};
    std::string tag_name;
    std::map<std::string, std::string> attributes;
    std::string text;
};

class XmlTokenizer {
    std::string_view src_;
    size_t pos_{0};

    void skip_whitespace_and_comments() {
        while (pos_ < src_.size()) {
            if (std::isspace(static_cast<unsigned char>(src_[pos_]))) {
                pos_++;
            } else if (src_.substr(pos_, 4) == "<!--") {
                size_t end_comment = src_.find("-->", pos_ + 4);
                if (end_comment == std::string_view::npos) {
                    pos_ = src_.size();
                } else {
                    pos_ = end_comment + 3;
                }
            } else if (src_.substr(pos_, 2) == "<?") {
                size_t end_pi = src_.find("?>", pos_ + 2);
                if (end_pi == std::string_view::npos) {
                    pos_ = src_.size();
                } else {
                    pos_ = end_pi + 2;
                }
            } else if (src_.substr(pos_, 2) == "<!") {
                if (src_.substr(pos_, 9) == "<![CDATA[") {
                    break; // Keep CDATA for text tokenizing
                }
                size_t end_doc = src_.find('>', pos_ + 2);
                if (end_doc == std::string_view::npos) {
                    pos_ = src_.size();
                } else {
                    pos_ = end_doc + 1;
                }
            } else {
                break;
            }
        }
    }

public:
    explicit XmlTokenizer(std::string_view src) : src_(src) {}

    XmlToken next() {
        skip_whitespace_and_comments();
        if (pos_ >= src_.size()) {
            return XmlToken{XmlTokenType::Eof, "", {}, ""};
        }

        if (src_[pos_] == '<') {
            if (src_.substr(pos_, 9) == "<![CDATA[") {
                size_t start = pos_ + 9;
                size_t end = src_.find("]]>", start);
                if (end == std::string_view::npos) end = src_.size();
                std::string cdata_text(src_.substr(start, end - start));
                pos_ = (end == src_.size()) ? end : end + 3;
                return XmlToken{XmlTokenType::Text, "", {}, cdata_text};
            }

            pos_++; // Skip '<'
            if (pos_ < src_.size() && src_[pos_] == '/') {
                pos_++; // Skip '/'
                size_t end_tag = src_.find('>', pos_);
                if (end_tag == std::string_view::npos) end_tag = src_.size();
                std::string tag_name(src_.substr(pos_, end_tag - pos_));
                // trim whitespace
                tag_name.erase(std::remove_if(tag_name.begin(), tag_name.end(), ::isspace), tag_name.end());
                pos_ = (end_tag == src_.size()) ? end_tag : end_tag + 1;
                return XmlToken{XmlTokenType::EndTag, tag_name, {}, ""};
            }

            size_t tag_end = src_.find_first_of(" />\t\r\n", pos_);
            if (tag_end == std::string_view::npos) tag_end = src_.size();
            std::string tag_name(src_.substr(pos_, tag_end - pos_));
            pos_ = tag_end;

            std::map<std::string, std::string> attrs;
            bool is_empty = false;

            while (pos_ < src_.size() && src_[pos_] != '>') {
                if (std::isspace(static_cast<unsigned char>(src_[pos_]))) {
                    pos_++;
                    continue;
                }
                if (src_[pos_] == '/') {
                    is_empty = true;
                    pos_++;
                    continue;
                }
                size_t eq = src_.find('=', pos_);
                if (eq != std::string_view::npos && eq < src_.find('>', pos_)) {
                    std::string attr_name(src_.substr(pos_, eq - pos_));
                    attr_name.erase(std::remove_if(attr_name.begin(), attr_name.end(), ::isspace), attr_name.end());
                    pos_ = eq + 1;
                    while (pos_ < src_.size() && std::isspace(static_cast<unsigned char>(src_[pos_]))) pos_++;
                    if (pos_ < src_.size() && (src_[pos_] == '"' || src_[pos_] == '\'')) {
                        char quote = src_[pos_++];
                        size_t val_end = src_.find(quote, pos_);
                        if (val_end == std::string_view::npos) val_end = src_.size();
                        std::string attr_val(src_.substr(pos_, val_end - pos_));
                        attrs[attr_name] = xml_unescape(attr_val);
                        pos_ = (val_end == src_.size()) ? val_end : val_end + 1;
                    }
                } else {
                    pos_++;
                }
            }

            if (pos_ < src_.size() && src_[pos_] == '>') {
                pos_++;
            }

            return XmlToken{is_empty ? XmlTokenType::EmptyTag : XmlTokenType::StartTag, tag_name, std::move(attrs), ""};
        }

        // Text token
        size_t next_angle = src_.find('<', pos_);
        if (next_angle == std::string_view::npos) next_angle = src_.size();
        std::string text_val = xml_unescape(src_.substr(pos_, next_angle - pos_));
        pos_ = next_angle;
        return XmlToken{XmlTokenType::Text, "", {}, text_val};
    }
};

class XmlDecoder {
    XmlTokenizer tokenizer_;

public:
    explicit XmlDecoder(std::string_view src) : tokenizer_(src) {}

    Value decode() {
        while (true) {
            XmlToken tok = tokenizer_.next();
            if (tok.type == XmlTokenType::Eof) return Value{Undefined{}};
            if (tok.type == XmlTokenType::StartTag) {
                if (tok.tag_name == "llsd") {
                    return parse_node();
                } else {
                    return parse_node_with_tag(tok);
                }
            }
        }
    }

private:
    Value parse_node() {
        while (true) {
            XmlToken tok = tokenizer_.next();
            if (tok.type == XmlTokenType::Eof || tok.type == XmlTokenType::EndTag) {
                return Value{Undefined{}};
            }
            if (tok.type == XmlTokenType::StartTag || tok.type == XmlTokenType::EmptyTag) {
                return parse_node_with_tag(tok);
            }
        }
    }

    Value parse_node_with_tag(const XmlToken& tag) {
        if (tag.type == XmlTokenType::EmptyTag) {
            if (tag.tag_name == "undef") return Value{Undefined{}};
            if (tag.tag_name == "boolean") return Value{false};
            if (tag.tag_name == "integer") return Value{0};
            if (tag.tag_name == "real") return Value{0.0};
            if (tag.tag_name == "string") return Value{""};
            if (tag.tag_name == "uuid") return Value{UUID::null()};
            if (tag.tag_name == "date") return Value{Date{0.0}};
            if (tag.tag_name == "uri") return Value{URI{""}};
            if (tag.tag_name == "binary") return Value{Binary{}};
            if (tag.tag_name == "array") return Value{Array{}};
            if (tag.tag_name == "map") return Value{Map{}};
            return Value{Undefined{}};
        }

        std::string tag_name = tag.tag_name;
        if (tag_name == "undef") {
            consume_until_end_tag("undef");
            return Value{Undefined{}};
        }
        if (tag_name == "boolean") {
            std::string text = read_text_until_end_tag("boolean");
            return Value{text == "true" || text == "1" || text == "t" || text == "TRUE"};
        }
        if (tag_name == "integer") {
            std::string text = read_text_until_end_tag("integer");
            try { return Value{std::stoi(text)}; } catch (...) { return Value{0}; }
        }
        if (tag_name == "real") {
            std::string text = read_text_until_end_tag("real");
            if (text == "inf" || text == "1.#INF") return Value{1.0 / 0.0};
            if (text == "-inf" || text == "-1.#INF") return Value{-1.0 / 0.0};
            if (text == "nan" || text == "1.#QNAN") return Value{0.0 / 0.0};
            try { return Value{std::stod(text)}; } catch (...) { return Value{0.0}; }
        }
        if (tag_name == "string") {
            std::string text = read_text_until_end_tag("string");
            return Value{text};
        }
        if (tag_name == "uuid") {
            std::string text = read_text_until_end_tag("uuid");
            return Value{UUID::from_string(text)};
        }
        if (tag_name == "date") {
            std::string text = read_text_until_end_tag("date");
            return Value{Date::from_iso8601(text)};
        }
        if (tag_name == "uri") {
            std::string text = read_text_until_end_tag("uri");
            return Value{URI{text}};
        }
        if (tag_name == "binary") {
            std::string encoding = "base64";
            auto it = tag.attributes.find("encoding");
            if (it != tag.attributes.end()) encoding = it->second;
            std::string text = read_text_until_end_tag("binary");
            if (encoding == "base16") {
                return Value{hex_decode(text)};
            } else {
                return Value{base64_decode(text)};
            }
        }
        if (tag_name == "array") {
            Array arr;
            while (true) {
                XmlToken tok = tokenizer_.next();
                if (tok.type == XmlTokenType::Eof) break;
                if (tok.type == XmlTokenType::EndTag && tok.tag_name == "array") break;
                if (tok.type == XmlTokenType::StartTag || tok.type == XmlTokenType::EmptyTag) {
                    arr.push_back(parse_node_with_tag(tok));
                }
            }
            return Value{arr};
        }
        if (tag_name == "map") {
            Map map;
            std::string current_key;
            while (true) {
                XmlToken tok = tokenizer_.next();
                if (tok.type == XmlTokenType::Eof) break;
                if (tok.type == XmlTokenType::EndTag && tok.tag_name == "map") break;
                if (tok.type == XmlTokenType::StartTag || tok.type == XmlTokenType::EmptyTag) {
                    if (tok.tag_name == "key") {
                        current_key = read_text_until_end_tag("key");
                    } else {
                        map[current_key] = parse_node_with_tag(tok);
                    }
                }
            }
            return Value{map};
        }

        consume_until_end_tag(tag_name);
        return Value{Undefined{}};
    }

    std::string read_text_until_end_tag(std::string_view tag_name) {
        std::string result;
        while (true) {
            XmlToken tok = tokenizer_.next();
            if (tok.type == XmlTokenType::Eof) break;
            if (tok.type == XmlTokenType::EndTag && tok.tag_name == tag_name) break;
            if (tok.type == XmlTokenType::Text) {
                result += tok.text;
            }
        }
        return result;
    }

    void consume_until_end_tag(std::string_view tag_name) {
        while (true) {
            XmlToken tok = tokenizer_.next();
            if (tok.type == XmlTokenType::Eof) break;
            if (tok.type == XmlTokenType::EndTag && tok.tag_name == tag_name) break;
        }
    }
};

class XmlEncoder {
    std::ostringstream ss_;

public:
    XmlEncoder() = default;

    std::string str() const { return ss_.str(); }

    void encode_header() {
        ss_ << "<?xml version=\"1.0\" ?>";
    }

    void encode(const Value& val) {
        ss_ << "<llsd>";
        encode_value(val);
        ss_ << "</llsd>";
    }

private:
    void encode_value(const Value& val) {
        switch (val.type()) {
            case Type::Undefined:
                ss_ << "<undef/>";
                break;
            case Type::Boolean:
                ss_ << "<boolean>" << (val.as_boolean() ? "true" : "false") << "</boolean>";
                break;
            case Type::Integer:
                ss_ << "<integer>" << val.as_integer() << "</integer>";
                break;
            case Type::Real:
                ss_ << "<real>" << val.as_real() << "</real>";
                break;
            case Type::UUID:
                ss_ << "<uuid>" << val.as_uuid().to_string() << "</uuid>";
                break;
            case Type::Date:
                ss_ << "<date>" << val.as_date().to_iso8601() << "</date>";
                break;
            case Type::String:
                ss_ << "<string>" << xml_escape(val.as_string()) << "</string>";
                break;
            case Type::URI:
                ss_ << "<uri>" << xml_escape(val.as_uri().value) << "</uri>";
                break;
            case Type::Binary:
                ss_ << "<binary encoding=\"base64\">" << base64_encode(val.as_binary()) << "</binary>";
                break;
            case Type::Array: {
                ss_ << "<array>";
                for (const auto& item : val.as_array()) {
                    encode_value(item);
                }
                ss_ << "</array>";
                break;
            }
            case Type::Map: {
                ss_ << "<map>";
                for (const auto& [key, item] : val.as_map()) {
                    ss_ << "<key>" << xml_escape(key) << "</key>";
                    encode_value(item);
                }
                ss_ << "</map>";
                break;
            }
        }
    }
};

} // namespace detail

inline Value decode_xml(std::string_view xml_data) {
    detail::XmlDecoder decoder(xml_data);
    return decoder.decode();
}

inline std::string encode_xml(const Value& val, bool include_header = true) {
    detail::XmlEncoder encoder;
    if (include_header) {
        encoder.encode_header();
    }
    encoder.encode(val);
    return encoder.str();
}

} // namespace linkpoint::llsd

#endif // LINKPOINT_LLSD_XML_HPP
