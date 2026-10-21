using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace Linkpoint.LLSD.Codecs
{
    public static class LLSDNotationCodec
    {
        public const string Header = "<? llsd/notation ?>\n";

        public static string Encode(LLSDValue value, bool includeHeader = false)
        {
            var sb = new StringBuilder();
            if (includeHeader)
            {
                sb.Append(Header);
            }
            WriteNotation(sb, value);
            return sb.ToString();
        }

        private static void WriteNotation(StringBuilder sb, LLSDValue value)
        {
            switch (value.Type)
            {
                case LLSDType.Undefined:
                    sb.Append('!');
                    break;

                case LLSDType.Boolean:
                    sb.Append(value.AsBoolean() ? "true" : "false");
                    break;

                case LLSDType.Integer:
                    sb.Append('i').Append(value.AsInteger().ToString(CultureInfo.InvariantCulture));
                    break;

                case LLSDType.Real:
                    sb.Append('r');
                    double r = value.AsReal();
                    if (double.IsNaN(r)) sb.Append("nan");
                    else if (double.IsPositiveInfinity(r)) sb.Append("inf");
                    else if (double.IsNegativeInfinity(r)) sb.Append("-inf");
                    else sb.Append(r.ToString(CultureInfo.InvariantCulture));
                    break;

                case LLSDType.String:
                    sb.Append('\'');
                    foreach (char c in value.AsString())
                    {
                        if (c == '\\') sb.Append("\\\\");
                        else if (c == '\'') sb.Append("\\'");
                        else sb.Append(c);
                    }
                    sb.Append('\'');
                    break;

                case LLSDType.UUID:
                    sb.Append('u').Append(value.AsUUID().ToString());
                    break;

                case LLSDType.Date:
                    sb.Append("d\"").Append(value.AsDate().ToISOString()).Append('"');
                    break;

                case LLSDType.URI:
                    sb.Append("l\"");
                    foreach (char c in value.AsURI().AsString())
                    {
                        if (c == '\\') sb.Append("\\\\");
                        else if (c == '"') sb.Append("\\\"");
                        else sb.Append(c);
                    }
                    sb.Append('"');
                    break;

                case LLSDType.Binary:
                    sb.Append("b64\"").Append(Convert.ToBase64String(value.AsBinary())).Append('"');
                    break;

                case LLSDType.Map:
                    sb.Append('{');
                    bool first = true;
                    foreach (var kvp in value.AsMap())
                    {
                        if (!first) sb.Append(',');
                        first = false;

                        sb.Append('\'');
                        foreach (char c in kvp.Key)
                        {
                            if (c == '\\') sb.Append("\\\\");
                            else if (c == '\'') sb.Append("\\'");
                            else sb.Append(c);
                        }
                        sb.Append("':");
                        WriteNotation(sb, kvp.Value);
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
                        WriteNotation(sb, item);
                    }
                    sb.Append(']');
                    break;

                default:
                    sb.Append('!');
                    break;
            }
        }

        public static LLSDValue Decode(string text)
        {
            if (string.IsNullOrWhiteSpace(text)) return LLSDValue.Undefined;

            string stripped = text.TrimStart();
            if (stripped.StartsWith("<?llsd/notation?>") || stripped.StartsWith("<? llsd/notation ?>"))
            {
                int nl = stripped.IndexOf('\n');
                if (nl >= 0) stripped = stripped.Substring(nl + 1);
            }

            var parser = new NotationParser(stripped);
            return parser.Parse();
        }

        private class NotationParser
        {
            private readonly string _text;
            private int _pos;

            public NotationParser(string text)
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
            private char PeekAt(int offset) => _pos + offset < _text.Length ? _text[_pos + offset] : '\0';
            private char Consume() => _text[_pos++];

            private void Expect(char c)
            {
                if (_pos < _text.Length && _text[_pos] == c)
                {
                    _pos++;
                }
            }

            private LLSDValue ParseValue()
            {
                SkipWs();
                char c = Peek();
                switch (c)
                {
                    case '!':
                        Consume();
                        return LLSDValue.Undefined;

                    case 'T': case 't':
                        return ParseBoolWord(true);

                    case 'F': case 'f':
                        return ParseBoolWord(false);

                    case '1':
                        Consume();
                        return LLSDValue.FromBoolean(true);

                    case '0':
                        Consume();
                        return LLSDValue.FromBoolean(false);

                    case 'i':
                        Consume();
                        string iStr = ParseNumberWord();
                        int.TryParse(iStr, NumberStyles.Any, CultureInfo.InvariantCulture, out int iVal);
                        return LLSDValue.FromInteger(iVal);

                    case 'r':
                        Consume();
                        string rStr = ParseNumberWord().ToLowerInvariant();
                        double rVal = rStr switch
                        {
                            "nan" => double.NaN,
                            "inf" or "+inf" or "infinity" => double.PositiveInfinity,
                            "-inf" or "-infinity" => double.NegativeInfinity,
                            _ => double.TryParse(rStr, NumberStyles.Any, CultureInfo.InvariantCulture, out double v) ? v : 0.0
                        };
                        return LLSDValue.FromReal(rVal);

                    case 'u':
                        Consume();
                        string uuidStr = ParseUuidLiteral();
                        return LLSDValue.FromUUID(LLUUID.FromString(uuidStr));

                    case 'd':
                        Consume();
                        string dateStr = ParseQuotedAfterTag();
                        return LLSDValue.FromDate(LLDate.FromISOString(dateStr));

                    case 'l':
                        Consume();
                        string uriStr = ParseQuotedAfterTag();
                        return LLSDValue.FromURI(LLURI.FromString(uriStr));

                    case 'b':
                        return ParseBinary();

                    case 's':
                        return ParseSizedString();

                    case '\'':
                        return LLSDValue.FromString(ParseSingleQuoted());

                    case '"':
                        return LLSDValue.FromString(ParseDoubleQuoted());

                    case '{':
                        return ParseMap();

                    case '[':
                        return ParseArray();

                    default:
                        if (c != '\0') Consume();
                        return LLSDValue.Undefined;
                }
            }

            private LLSDValue ParseBoolWord(bool expected)
            {
                string word = expected ? "true" : "false";
                if (_pos + word.Length <= _text.Length &&
                    _text.Substring(_pos, word.Length).Equals(word, StringComparison.OrdinalIgnoreCase))
                {
                    _pos += word.Length;
                }
                else
                {
                    Consume();
                }
                return LLSDValue.FromBoolean(expected);
            }

            private string ParseNumberWord()
            {
                int start = _pos;
                while (_pos < _text.Length)
                {
                    char c = _text[_pos];
                    if (char.IsWhiteSpace(c) || c == ',' || c == '}' || c == ']') break;
                    _pos++;
                }
                return _text.Substring(start, _pos - start);
            }

            private string ParseUuidLiteral()
            {
                int start = _pos;
                int end = Math.Min(start + 36, _text.Length);
                _pos = end;
                return _text.Substring(start, end - start);
            }

            private string ParseQuotedAfterTag()
            {
                SkipWs();
                char c = Peek();
                if (c == '"') return ParseDoubleQuoted();
                if (c == '\'') return ParseSingleQuoted();
                return "";
            }

            private string ParseDoubleQuoted()
            {
                Expect('"');
                var sb = new StringBuilder();
                while (_pos < _text.Length && _text[_pos] != '"')
                {
                    if (_text[_pos] == '\\' && _pos + 1 < _text.Length)
                    {
                        _pos++;
                        sb.Append(DecodeEscape(_text[_pos]));
                    }
                    else
                    {
                        sb.Append(_text[_pos]);
                    }
                    _pos++;
                }
                if (_pos < _text.Length) _pos++;
                return sb.ToString();
            }

            private string ParseSingleQuoted()
            {
                Expect('\'');
                var sb = new StringBuilder();
                while (_pos < _text.Length && _text[_pos] != '\'')
                {
                    if (_text[_pos] == '\\' && _pos + 1 < _text.Length)
                    {
                        _pos++;
                        sb.Append(DecodeEscape(_text[_pos]));
                    }
                    else
                    {
                        sb.Append(_text[_pos]);
                    }
                    _pos++;
                }
                if (_pos < _text.Length) _pos++;
                return sb.ToString();
            }

            private char DecodeEscape(char c)
            {
                return c switch
                {
                    'n' => '\n',
                    't' => '\t',
                    'r' => '\r',
                    _ => c
                };
            }

            private LLSDValue ParseSizedString()
            {
                Consume(); // 's'
                if (Peek() == '(')
                {
                    Consume();
                    int numStart = _pos;
                    while (_pos < _text.Length && _text[_pos] != ')') _pos++;
                    int.TryParse(_text.Substring(numStart, _pos - numStart), out int size);
                    if (_pos < _text.Length) Consume(); // ')'
                    if (_pos < _text.Length && (_text[_pos] == '"' || _text[_pos] == '\'')) Consume();
                    int end = Math.Min(_pos + size, _text.Length);
                    string s = _text.Substring(_pos, end - _pos);
                    _pos = end;
                    if (_pos < _text.Length && (_text[_pos] == '"' || _text[_pos] == '\'')) Consume();
                    return LLSDValue.FromString(s);
                }

                char p = Peek();
                if (p == '"') return LLSDValue.FromString(ParseDoubleQuoted());
                if (p == '\'') return LLSDValue.FromString(ParseSingleQuoted());
                return LLSDValue.FromString("");
            }

            private LLSDValue ParseBinary()
            {
                Consume(); // 'b'
                if (PeekAt(0) == '6' && PeekAt(1) == '4')
                {
                    _pos += 2;
                    string b64 = ParseQuotedAfterTag();
                    return LLSDValue.FromBinary(string.IsNullOrEmpty(b64) ? Array.Empty<byte>() : Convert.FromBase64String(b64));
                }
                if (PeekAt(0) == '1' && PeekAt(1) == '6')
                {
                    _pos += 2;
                    string hex = ParseQuotedAfterTag();
                    return LLSDValue.FromBinary(string.IsNullOrEmpty(hex) ? Array.Empty<byte>() : ParseHex(hex));
                }
                if (PeekAt(0) == '(')
                {
                    Consume(); // '('
                    int numStart = _pos;
                    while (_pos < _text.Length && _text[_pos] != ')') _pos++;
                    int.TryParse(_text.Substring(numStart, _pos - numStart), out int size);
                    if (_pos < _text.Length) Consume(); // ')'
                    if (_pos < _text.Length && (_text[_pos] == '"' || _text[_pos] == '\'')) Consume();
                    int end = Math.Min(_pos + size, _text.Length);
                    byte[] bytes = new byte[end - _pos];
                    for (int i = 0; i < bytes.Length; i++) bytes[i] = (byte)_text[_pos + i];
                    _pos = end;
                    if (_pos < _text.Length && (_text[_pos] == '"' || _text[_pos] == '\'')) Consume();
                    return LLSDValue.FromBinary(bytes);
                }

                return LLSDValue.FromBinary(Array.Empty<byte>());
            }

            private static byte[] ParseHex(string hex)
            {
                if (hex.Length % 2 != 0) return Array.Empty<byte>();
                byte[] bytes = new byte[hex.Length / 2];
                for (int i = 0; i < bytes.Length; i++)
                {
                    bytes[i] = byte.Parse(hex.Substring(i * 2, 2), NumberStyles.HexNumber, CultureInfo.InvariantCulture);
                }
                return bytes;
            }

            private LLSDValue ParseMap()
            {
                Consume(); // '{'
                var map = new Dictionary<string, LLSDValue>(StringComparer.Ordinal);
                SkipWs();
                while (_pos < _text.Length && Peek() != '}')
                {
                    SkipWs();
                    char c = Peek();
                    string key = c switch
                    {
                        '\'' => ParseSingleQuoted(),
                        '"' => ParseDoubleQuoted(),
                        's' => ParseSizedString().AsString(),
                        _ => ParseNumberWord()
                    };
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
