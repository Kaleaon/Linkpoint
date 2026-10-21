using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace Linkpoint.LLSD.Codecs
{
    public static class LLSDJsonCodec
    {
        public static string Encode(LLSDValue value)
        {
            var sb = new StringBuilder();
            WriteJson(sb, value);
            return sb.ToString();
        }

        private static void WriteJson(StringBuilder sb, LLSDValue value)
        {
            switch (value.Type)
            {
                case LLSDType.Undefined:
                    sb.Append("null");
                    break;

                case LLSDType.Boolean:
                    sb.Append(value.AsBoolean() ? "true" : "false");
                    break;

                case LLSDType.Integer:
                    sb.Append(value.AsInteger().ToString(CultureInfo.InvariantCulture));
                    break;

                case LLSDType.Real:
                    double r = value.AsReal();
                    if (double.IsNaN(r) || double.IsInfinity(r)) sb.Append("null");
                    else sb.Append(r.ToString(CultureInfo.InvariantCulture));
                    break;

                case LLSDType.String:
                    AppendJsonString(sb, value.AsString());
                    break;

                case LLSDType.UUID:
                    AppendJsonString(sb, value.AsUUID().ToString());
                    break;

                case LLSDType.Date:
                    AppendJsonString(sb, value.AsDate().ToISOString());
                    break;

                case LLSDType.URI:
                    AppendJsonString(sb, value.AsURI().AsString());
                    break;

                case LLSDType.Binary:
                    AppendJsonString(sb, Convert.ToBase64String(value.AsBinary()));
                    break;

                case LLSDType.Map:
                    sb.Append('{');
                    bool first = true;
                    foreach (var kvp in value.AsMap())
                    {
                        if (!first) sb.Append(',');
                        first = false;
                        AppendJsonString(sb, kvp.Key);
                        sb.Append(':');
                        WriteJson(sb, kvp.Value);
                    }
                    sb.Append('}');
                    break;

                case LLSDType.Array:
                    sb.Append('[');
                    bool arrFirst = true;
                    foreach (var item in value.AsArray())
                    {
                        if (!arrFirst) sb.Append(',');
                        arrFirst = false;
                        WriteJson(sb, item);
                    }
                    sb.Append(']');
                    break;

                default:
                    sb.Append("null");
                    break;
            }
        }

        private static void AppendJsonString(StringBuilder sb, string s)
        {
            sb.Append('"');
            foreach (char c in s)
            {
                switch (c)
                {
                    case '"': sb.Append("\\\""); break;
                    case '\\': sb.Append("\\\\"); break;
                    case '\b': sb.Append("\\b"); break;
                    case '\f': sb.Append("\\f"); break;
                    case '\n': sb.Append("\\n"); break;
                    case '\r': sb.Append("\\r"); break;
                    case '\t': sb.Append("\\t"); break;
                    default:
                        if (c < ' ') sb.AppendFormat(CultureInfo.InvariantCulture, "\\u{0:x4}", (int)c);
                        else sb.Append(c);
                        break;
                }
            }
            sb.Append('"');
        }

        public static LLSDValue Decode(string json)
        {
            if (string.IsNullOrWhiteSpace(json)) return LLSDValue.Undefined;
            var parser = new JsonParser(json.Trim());
            return parser.Parse();
        }

        private class JsonParser
        {
            private readonly string _text;
            private int _pos;

            public JsonParser(string text)
            {
                _text = text;
                _pos = 0;
            }

            public LLSDValue Parse()
            {
                SkipWs();
                return ParseValue();
            }

            private void SkipWs()
            {
                while (_pos < _text.Length && char.IsWhiteSpace(_text[_pos])) _pos++;
            }

            private char Peek() => _pos < _text.Length ? _text[_pos] : '\0';
            private char Consume() => _text[_pos++];

            private LLSDValue ParseValue()
            {
                SkipWs();
                char c = Peek();
                switch (c)
                {
                    case 'n':
                        ConsumeWord("null");
                        return LLSDValue.Undefined;

                    case 't':
                        ConsumeWord("true");
                        return LLSDValue.FromBoolean(true);

                    case 'f':
                        ConsumeWord("false");
                        return LLSDValue.FromBoolean(false);

                    case '"':
                        return LLSDValue.FromString(ParseString());

                    case '{':
                        return ParseObject();

                    case '[':
                        return ParseArray();

                    default:
                        if (char.IsDigit(c) || c == '-')
                        {
                            return ParseNumber();
                        }
                        if (c != '\0') Consume();
                        return LLSDValue.Undefined;
                }
            }

            private void ConsumeWord(string word)
            {
                if (_pos + word.Length <= _text.Length &&
                    _text.Substring(_pos, word.Length).Equals(word, StringComparison.OrdinalIgnoreCase))
                {
                    _pos += word.Length;
                }
                else
                {
                    while (_pos < _text.Length && char.IsLetter(_text[_pos])) _pos++;
                }
            }

            private string ParseString()
            {
                Consume(); // '"'
                var sb = new StringBuilder();
                while (_pos < _text.Length && _text[_pos] != '"')
                {
                    if (_text[_pos] == '\\' && _pos + 1 < _text.Length)
                    {
                        _pos++;
                        char esc = _text[_pos];
                        switch (esc)
                        {
                            case '"': sb.Append('"'); break;
                            case '\\': sb.Append('\\'); break;
                            case '/': sb.Append('/'); break;
                            case 'b': sb.Append('\b'); break;
                            case 'f': sb.Append('\f'); break;
                            case 'n': sb.Append('\n'); break;
                            case 'r': sb.Append('\r'); break;
                            case 't': sb.Append('\t'); break;
                            case 'u':
                                if (_pos + 4 < _text.Length)
                                {
                                    string hex = _text.Substring(_pos + 1, 4);
                                    if (ushort.TryParse(hex, NumberStyles.HexNumber, CultureInfo.InvariantCulture, out ushort codePoint))
                                    {
                                        sb.Append((char)codePoint);
                                        _pos += 4;
                                    }
                                }
                                break;
                            default:
                                sb.Append(esc);
                                break;
                        }
                    }
                    else
                    {
                        sb.Append(_text[_pos]);
                    }
                    _pos++;
                }
                if (_pos < _text.Length) _pos++; // '"'
                return sb.ToString();
            }

            private LLSDValue ParseNumber()
            {
                int start = _pos;
                bool isFloat = false;
                while (_pos < _text.Length)
                {
                    char c = _text[_pos];
                    if (c == '.' || c == 'e' || c == 'E') isFloat = true;
                    if (!char.IsDigit(c) && c != '-' && c != '+' && c != '.' && c != 'e' && c != 'E') break;
                    _pos++;
                }

                string numStr = _text.Substring(start, _pos - start);
                if (isFloat)
                {
                    double.TryParse(numStr, NumberStyles.Any, CultureInfo.InvariantCulture, out double rVal);
                    return LLSDValue.FromReal(rVal);
                }
                else
                {
                    int.TryParse(numStr, NumberStyles.Any, CultureInfo.InvariantCulture, out int iVal);
                    return LLSDValue.FromInteger(iVal);
                }
            }

            private LLSDValue ParseObject()
            {
                Consume(); // '{'
                var map = new Dictionary<string, LLSDValue>(StringComparer.Ordinal);
                SkipWs();
                while (_pos < _text.Length && Peek() != '}')
                {
                    SkipWs();
                    if (Peek() != '"') break;
                    string key = ParseString();
                    SkipWs();
                    if (Peek() == ':') Consume();
                    SkipWs();
                    map[key] = ParseValue();
                    SkipWs();
                    if (Peek() == ',') Consume();
                    SkipWs();
                }
                if (_pos < _text.Length) Consume(); // '}'
                return LLSDValue.FromMap(map);
            }

            private LLSDValue ParseArray()
            {
                Consume(); // '['
                var list = new List<LLSDValue>();
                SkipWs();
                while (_pos < _text.Length && Peek() != ']')
                {
                    list.Add(ParseValue());
                    SkipWs();
                    if (Peek() == ',') Consume();
                    SkipWs();
                }
                if (_pos < _text.Length) Consume(); // ']'
                return LLSDValue.FromArray(list);
            }
        }
    }
}
