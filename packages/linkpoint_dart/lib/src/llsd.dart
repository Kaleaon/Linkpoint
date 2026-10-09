import 'dart:convert';
import 'dart:typed_data';
import 'uuid.dart';

/// Abstract representation of Second Life Data (LLSD) value.
abstract class LLSDValue {
  const LLSDValue();

  factory LLSDValue.undef() = LLSDUndef;
  factory LLSDValue.boolean(bool value) = LLSDBoolean;
  factory LLSDValue.integer(int value) = LLSDInteger;
  factory LLSDValue.real(double value) = LLSDReal;
  factory LLSDValue.string(String value) = LLSDString;
  factory LLSDValue.uuid(LLUUID value) = LLSDUUID;
  factory LLSDValue.date(DateTime value) = LLSDDate;
  factory LLSDValue.uri(String value) = LLSDURI;
  factory LLSDValue.binary(Uint8List value) = LLSDBinary;
  factory LLSDValue.array(List<LLSDValue> value) = LLSDArray;
  factory LLSDValue.map(Map<String, LLSDValue> value) = LLSDMap;
}

class LLSDUndef extends LLSDValue {
  const LLSDUndef();

  @override
  bool operator ==(Object other) => other is LLSDUndef;

  @override
  int get hashCode => 0;

  @override
  String toString() => 'LLSDUndef()';
}

class LLSDBoolean extends LLSDValue {
  final bool value;
  const LLSDBoolean(this.value);

  @override
  bool operator ==(Object other) =>
      other is LLSDBoolean && other.value == value;

  @override
  int get hashCode => value.hashCode;

  @override
  String toString() => 'LLSDBoolean($value)';
}

class LLSDInteger extends LLSDValue {
  final int value;
  const LLSDInteger(this.value);

  @override
  bool operator ==(Object other) =>
      other is LLSDInteger && other.value == value;

  @override
  int get hashCode => value.hashCode;

  @override
  String toString() => 'LLSDInteger($value)';
}

class LLSDReal extends LLSDValue {
  final double value;
  const LLSDReal(this.value);

  @override
  bool operator ==(Object other) {
    if (other is! LLSDReal) return false;
    if (value.isNaN && other.value.isNaN) return true;
    return (value - other.value).abs() < 1e-6;
  }

  @override
  int get hashCode => value.hashCode;

  @override
  String toString() => 'LLSDReal($value)';
}

class LLSDString extends LLSDValue {
  final String value;
  const LLSDString(this.value);

  @override
  bool operator ==(Object other) =>
      other is LLSDString &&
      other.value.replaceAll('\r\n', '\n') == value.replaceAll('\r\n', '\n');

  @override
  int get hashCode => value.replaceAll('\r\n', '\n').hashCode;

  @override
  String toString() => 'LLSDString($value)';
}

class LLSDUUID extends LLSDValue {
  final LLUUID value;
  const LLSDUUID(this.value);

  @override
  bool operator ==(Object other) => other is LLSDUUID && other.value == value;

  @override
  int get hashCode => value.hashCode;

  @override
  String toString() => 'LLSDUUID($value)';
}

class LLSDDate extends LLSDValue {
  final DateTime value;
  const LLSDDate(this.value);

  @override
  bool operator ==(Object other) =>
      other is LLSDDate &&
      (other.value.millisecondsSinceEpoch ~/ 1000) ==
          (value.millisecondsSinceEpoch ~/ 1000);

  @override
  int get hashCode => (value.millisecondsSinceEpoch ~/ 1000).hashCode;

  @override
  String toString() => 'LLSDDate(${value.toIso8601String()})';
}

class LLSDURI extends LLSDValue {
  final String value;
  const LLSDURI(this.value);

  @override
  bool operator ==(Object other) => other is LLSDURI && other.value == value;

  @override
  int get hashCode => value.hashCode;

  @override
  String toString() => 'LLSDURI($value)';
}

class LLSDBinary extends LLSDValue {
  final Uint8List value;
  const LLSDBinary(this.value);

  @override
  bool operator ==(Object other) {
    if (other is! LLSDBinary) return false;
    if (other.value.length != value.length) return false;
    for (var i = 0; i < value.length; i++) {
      if (other.value[i] != value[i]) return false;
    }
    return true;
  }

  @override
  int get hashCode => Object.hashAll(value);

  @override
  String toString() => 'LLSDBinary(${value.length} bytes)';
}

class LLSDArray extends LLSDValue {
  final List<LLSDValue> value;
  const LLSDArray(this.value);

  @override
  bool operator ==(Object other) {
    if (other is! LLSDArray) return false;
    if (other.value.length != value.length) return false;
    for (var i = 0; i < value.length; i++) {
      if (other.value[i] != value[i]) return false;
    }
    return true;
  }

  @override
  int get hashCode => Object.hashAll(value);

  @override
  String toString() => 'LLSDArray($value)';
}

class LLSDMap extends LLSDValue {
  final Map<String, LLSDValue> value;
  const LLSDMap(this.value);

  @override
  bool operator ==(Object other) {
    if (other is! LLSDMap) return false;
    if (other.value.length != value.length) return false;
    for (final key in value.keys) {
      if (!other.value.containsKey(key)) return false;
      if (other.value[key] != value[key]) return false;
    }
    return true;
  }

  @override
  int get hashCode => Object.hashAll(value.entries);

  @override
  String toString() => 'LLSDMap($value)';
}

/// LLSD XML, Binary, and Notation parser/serializer for Dart.
class LLSD {
  /// Serializes LLSDValue to XML.
  static String serializeXml(LLSDValue value) {
    final buffer = StringBuffer();
    buffer.write('<?xml version="1.0" encoding="UTF-8"?><llsd>');
    _writeXmlValue(value, buffer);
    buffer.write('</llsd>');
    return buffer.toString();
  }

  static void _writeXmlValue(LLSDValue value, StringBuffer buffer) {
    if (value is LLSDUndef) {
      buffer.write('<undef />');
    } else if (value is LLSDBoolean) {
      buffer.write('<boolean>${value.value}</boolean>');
    } else if (value is LLSDInteger) {
      buffer.write('<integer>${value.value}</integer>');
    } else if (value is LLSDReal) {
      buffer.write('<real>${value.value}</real>');
    } else if (value is LLSDString) {
      buffer.write('<string>${_escapeXml(value.value)}</string>');
    } else if (value is LLSDUUID) {
      buffer.write('<uuid>${value.value}</uuid>');
    } else if (value is LLSDDate) {
      buffer.write('<date>${value.value.toIso8601String()}</date>');
    } else if (value is LLSDURI) {
      buffer.write('<uri>${_escapeXml(value.value)}</uri>');
    } else if (value is LLSDBinary) {
      buffer.write(
        '<binary encoding="base64">${base64.encode(value.value)}</binary>',
      );
    } else if (value is LLSDArray) {
      buffer.write('<array>');
      for (final item in value.value) {
        _writeXmlValue(item, buffer);
      }
      buffer.write('</array>');
    } else if (value is LLSDMap) {
      buffer.write('<map>');
      for (final entry in value.value.entries) {
        buffer.write('<key>${_escapeXml(entry.key)}</key>');
        _writeXmlValue(entry.value, buffer);
      }
      buffer.write('</map>');
    }
  }

  static String _escapeXml(String input) {
    return input
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&apos;');
  }

  /// Parses LLSD XML string into LLSDValue.
  static LLSDValue parseXml(String xmlString) {
    return _XmlReader(xmlString).parseValue();
  }

  /// Parses LLSD Binary bytes into LLSDValue.
  static LLSDValue parseBinary(Uint8List bytes) {
    return _BinaryReader(bytes).parseValue();
  }

  /// Serializes LLSDValue to Binary format.
  static Uint8List serializeBinary(LLSDValue value) {
    final builder = BytesBuilder();
    builder.add(utf8.encode('<?llsd/binary?>\n'));
    _writeBinaryValue(value, builder);
    return builder.toBytes();
  }

  static void _writeBinaryValue(LLSDValue value, BytesBuilder builder) {
    final bd = ByteData(8);
    if (value is LLSDUndef) {
      builder.addByte(33); // '!'
    } else if (value is LLSDBoolean) {
      builder.addByte(value.value ? 49 : 48); // '1' or '0'
    } else if (value is LLSDInteger) {
      builder.addByte(105); // 'i'
      bd.setInt32(0, value.value, Endian.big);
      builder.add(bd.buffer.asUint8List(0, 4));
    } else if (value is LLSDReal) {
      builder.addByte(114); // 'r'
      bd.setFloat64(0, value.value, Endian.big);
      builder.add(bd.buffer.asUint8List(0, 8));
    } else if (value is LLSDString) {
      builder.addByte(115); // 's'
      final strBytes = utf8.encode(value.value);
      bd.setUint32(0, strBytes.length, Endian.big);
      builder.add(bd.buffer.asUint8List(0, 4));
      builder.add(strBytes);
    } else if (value is LLSDUUID) {
      builder.addByte(117); // 'u'
      builder.add(value.value.toBytes());
    } else if (value is LLSDDate) {
      builder.addByte(100); // 'd'
      final seconds = value.value.millisecondsSinceEpoch / 1000.0;
      bd.setFloat64(0, seconds, Endian.little);
      builder.add(bd.buffer.asUint8List(0, 8));
    } else if (value is LLSDURI) {
      builder.addByte(108); // 'l'
      final uriBytes = utf8.encode(value.value);
      bd.setUint32(0, uriBytes.length, Endian.big);
      builder.add(bd.buffer.asUint8List(0, 4));
      builder.add(uriBytes);
    } else if (value is LLSDBinary) {
      builder.addByte(98); // 'b'
      bd.setUint32(0, value.value.length, Endian.big);
      builder.add(bd.buffer.asUint8List(0, 4));
      builder.add(value.value);
    } else if (value is LLSDArray) {
      builder.addByte(91); // '['
      bd.setUint32(0, value.value.length, Endian.big);
      builder.add(bd.buffer.asUint8List(0, 4));
      for (final item in value.value) {
        _writeBinaryValue(item, builder);
      }
      builder.addByte(93); // ']'
    } else if (value is LLSDMap) {
      builder.addByte(123); // '{'
      bd.setUint32(0, value.value.length, Endian.big);
      builder.add(bd.buffer.asUint8List(0, 4));
      for (final entry in value.value.entries) {
        builder.addByte(107); // 'k'
        final keyBytes = utf8.encode(entry.key);
        bd.setUint32(0, keyBytes.length, Endian.big);
        builder.add(bd.buffer.asUint8List(0, 4));
        builder.add(keyBytes);
        _writeBinaryValue(entry.value, builder);
      }
      builder.addByte(125); // '}'
    }
  }
}

class _XmlReader {
  final String source;
  int pos = 0;
  _XmlReader(this.source);

  void skipWhitespace() {
    while (pos < source.length && source.codeUnitAt(pos) <= 32) {
      pos++;
    }
  }

  LLSDValue parseValue() {
    skipWhitespace();
    if (pos >= source.length) return const LLSDUndef();

    int startTagPos = source.indexOf('<', pos);
    if (startTagPos == -1) return const LLSDUndef();

    while (startTagPos != -1) {
      int tagEnd = source.indexOf('>', startTagPos);
      if (tagEnd == -1) break;
      String tag = source.substring(startTagPos + 1, tagEnd).trim();
      if (tag.startsWith('?xml') || tag.startsWith('!--')) {
        pos = tagEnd + 1;
        skipWhitespace();
        startTagPos = source.indexOf('<', pos);
      } else if (tag.toLowerCase() == 'llsd') {
        pos = tagEnd + 1;
        skipWhitespace();
        startTagPos = source.indexOf('<', pos);
      } else if (tag.toLowerCase() == '/llsd') {
        pos = tagEnd + 1;
        return const LLSDUndef();
      } else {
        break;
      }
    }

    if (pos >= source.length) return const LLSDUndef();
    startTagPos = source.indexOf('<', pos);
    if (startTagPos == -1) return const LLSDUndef();

    int tagEnd = source.indexOf('>', startTagPos);
    if (tagEnd == -1) return const LLSDUndef();

    String rawTag = source.substring(startTagPos + 1, tagEnd).trim();
    bool selfClosing = rawTag.endsWith('/');
    if (selfClosing) {
      rawTag = rawTag.substring(0, rawTag.length - 1).trim();
    }

    String tagName = rawTag;
    int spaceIdx = rawTag.indexOf(' ');
    if (spaceIdx != -1) {
      tagName = rawTag.substring(0, spaceIdx).trim();
    }
    tagName = tagName.toLowerCase();

    pos = tagEnd + 1;

    if (selfClosing || tagName == 'undef') {
      if (tagName == 'boolean') return const LLSDBoolean(false);
      if (tagName == 'integer') return const LLSDInteger(0);
      if (tagName == 'real') return const LLSDReal(0.0);
      if (tagName == 'uuid') return LLSDUUID(LLUUID.zero);
      if (tagName == 'string') return const LLSDString('');
      if (tagName == 'uri') return const LLSDURI('');
      if (tagName == 'binary') return LLSDBinary(Uint8List(0));
      if (tagName == 'date')
        return LLSDDate(DateTime.fromMillisecondsSinceEpoch(0, isUtc: true));
      return const LLSDUndef();
    }

    if (tagName == 'map') {
      return _parseMap();
    }
    if (tagName == 'array') {
      return _parseArray();
    }

    String closeTag = '</$tagName>';
    int closePos = source.indexOf(closeTag, pos);
    if (closePos == -1) {
      closePos = source.toLowerCase().indexOf(closeTag.toLowerCase(), pos);
    }
    String content = '';
    if (closePos != -1) {
      content = source.substring(pos, closePos);
      pos = closePos + closeTag.length;
    }

    switch (tagName) {
      case 'boolean':
        content = content.trim();
        return LLSDBoolean(content == 'true' || content == '1');
      case 'integer':
        content = content.trim();
        return LLSDInteger(int.tryParse(content) ?? 0);
      case 'real':
        content = content.trim();
        return LLSDReal(double.tryParse(content) ?? 0.0);
      case 'uuid':
        content = content.trim();
        return LLSDUUID(content.isEmpty ? LLUUID.zero : LLUUID.parse(content));
      case 'string':
        return LLSDString(_unescapeXml(content));
      case 'date':
        content = content.trim();
        return LLSDDate(_parseIsoDate(content));
      case 'uri':
        return LLSDURI(_unescapeXml(content.trim()));
      case 'binary':
        content = content.trim();
        return LLSDBinary(
          content.isEmpty
              ? Uint8List(0)
              : base64.decode(content.replaceAll(RegExp(r'\s+'), '')),
        );
      default:
        return const LLSDUndef();
    }
  }

  LLSDMap _parseMap() {
    final map = <String, LLSDValue>{};
    while (pos < source.length) {
      skipWhitespace();
      if (source.startsWith('</map>', pos) ||
          source.startsWith('</MAP>', pos)) {
        pos += 6;
        break;
      }
      int keyStart = source.indexOf('<key>', pos);
      if (keyStart == -1) keyStart = source.indexOf('<KEY>', pos);
      if (keyStart == -1) break;
      int keyEnd = source.indexOf('</key>', keyStart);
      if (keyEnd == -1) keyEnd = source.indexOf('</KEY>', keyStart);
      if (keyEnd == -1) break;

      String key = _unescapeXml(source.substring(keyStart + 5, keyEnd).trim());
      pos = keyEnd + 6;

      LLSDValue val = parseValue();
      map[key] = val;
    }
    return LLSDMap(map);
  }

  LLSDArray _parseArray() {
    final list = <LLSDValue>[];
    while (pos < source.length) {
      skipWhitespace();
      if (source.startsWith('</array>', pos) ||
          source.startsWith('</ARRAY>', pos)) {
        pos += 8;
        break;
      }
      LLSDValue val = parseValue();
      list.add(val);
    }
    return LLSDArray(list);
  }

  static String _unescapeXml(String input) {
    return input
        .replaceAll('&lt;', '<')
        .replaceAll('&gt;', '>')
        .replaceAll('&quot;', '"')
        .replaceAll('&apos;', "'")
        .replaceAll('&amp;', '&');
  }

  static DateTime _parseIsoDate(String dateStr) {
    if (dateStr.isEmpty)
      return DateTime.fromMillisecondsSinceEpoch(0, isUtc: true);
    var clean = dateStr.trim();
    if (clean.endsWith('Z') && clean.contains('+00:00')) {
      clean = clean.substring(0, clean.length - 1);
    }
    return DateTime.parse(clean).toUtc();
  }
}

class _BinaryReader {
  final Uint8List bytes;
  int pos = 0;
  late final ByteData view;

  _BinaryReader(this.bytes) {
    view = ByteData.sublistView(bytes);
    const magic = '<?llsd/binary?>';
    if (bytes.length >= magic.length) {
      final header = String.fromCharCodes(bytes.sublist(0, magic.length));
      if (header == magic) {
        pos = magic.length;
        if (pos < bytes.length && bytes[pos] == 13) pos++;
        if (pos < bytes.length && bytes[pos] == 10) pos++;
      }
    }
  }

  LLSDValue parseValue() {
    if (pos >= bytes.length) return const LLSDUndef();
    final marker = String.fromCharCode(bytes[pos++]);
    switch (marker) {
      case '!':
        return const LLSDUndef();
      case '1':
      case 't':
      case 'T':
        return const LLSDBoolean(true);
      case '0':
      case 'f':
      case 'F':
        return const LLSDBoolean(false);
      case 'i':
        final val = view.getInt32(pos, Endian.big);
        pos += 4;
        return LLSDInteger(val);
      case 'r':
        final val = view.getFloat64(pos, Endian.big);
        pos += 8;
        return LLSDReal(val);
      case 'u':
        final uuidBytes = bytes.sublist(pos, pos + 16);
        pos += 16;
        return LLSDUUID(LLUUID.fromBytes(uuidBytes));
      case 's':
        final len = view.getUint32(pos, Endian.big);
        pos += 4;
        final strBytes = bytes.sublist(pos, pos + len);
        pos += len;
        return LLSDString(utf8.decode(strBytes));
      case 'b':
        final len = view.getUint32(pos, Endian.big);
        pos += 4;
        final binBytes = bytes.sublist(pos, pos + len);
        pos += len;
        return LLSDBinary(binBytes);
      case 'd':
        final seconds = view.getFloat64(pos, Endian.little);
        pos += 8;
        final ms = (seconds * 1000).round();
        return LLSDDate(DateTime.fromMillisecondsSinceEpoch(ms, isUtc: true));
      case 'l':
        final len = view.getUint32(pos, Endian.big);
        pos += 4;
        final uriBytes = bytes.sublist(pos, pos + len);
        pos += len;
        return LLSDURI(utf8.decode(uriBytes));
      case '{':
        pos += 4;
        final map = <String, LLSDValue>{};
        while (pos < bytes.length) {
          if (bytes[pos] == 125) {
            // '}'
            pos++;
            break;
          }
          if (bytes[pos] == 107) {
            // 'k'
            pos++;
            final keyLen = view.getUint32(pos, Endian.big);
            pos += 4;
            final key = utf8.decode(bytes.sublist(pos, pos + keyLen));
            pos += keyLen;
            final val = parseValue();
            map[key] = val;
          } else {
            break;
          }
        }
        return LLSDMap(map);
      case '[':
        pos += 4;
        final list = <LLSDValue>[];
        while (pos < bytes.length) {
          if (bytes[pos] == 93) {
            // ']'
            pos++;
            break;
          }
          list.add(parseValue());
        }
        return LLSDArray(list);
      default:
        return const LLSDUndef();
    }
  }
}
