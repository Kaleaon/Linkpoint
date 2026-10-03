import 'uuid.dart';

/// Abstract representation of Second Life Data (LLSD) value.
abstract class LLSDValue {
  const LLSDValue();

  factory LLSDValue.boolean(bool value) = LLSDBoolean;
  factory LLSDValue.integer(int value) = LLSDInteger;
  factory LLSDValue.real(double value) = LLSDReal;
  factory LLSDValue.string(String value) = LLSDString;
  factory LLSDValue.uuid(LLUUID value) = LLSDUUID;
  factory LLSDValue.array(List<LLSDValue> value) = LLSDArray;
  factory LLSDValue.map(Map<String, LLSDValue> value) = LLSDMap;
}

class LLSDBoolean extends LLSDValue {
  final bool value;
  const LLSDBoolean(this.value);
}

class LLSDInteger extends LLSDValue {
  final int value;
  const LLSDInteger(this.value);
}

class LLSDReal extends LLSDValue {
  final double value;
  const LLSDReal(this.value);
}

class LLSDString extends LLSDValue {
  final String value;
  const LLSDString(this.value);
}

class LLSDUUID extends LLSDValue {
  final LLUUID value;
  const LLSDUUID(this.value);
}

class LLSDArray extends LLSDValue {
  final List<LLSDValue> value;
  const LLSDArray(this.value);
}

class LLSDMap extends LLSDValue {
  final Map<String, LLSDValue> value;
  const LLSDMap(this.value);
}

/// LLSD XML and Notation parser/serializer for Dart.
class LLSD {
  /// Simple LLSD XML Serializer
  static String serializeXml(LLSDValue value) {
    final buffer = StringBuffer();
    buffer.write('<?xml version="1.0" encoding="UTF-8"?><llsd>');
    _writeXmlValue(value, buffer);
    buffer.write('</llsd>');
    return buffer.toString();
  }

  static void _writeXmlValue(LLSDValue value, StringBuffer buffer) {
    if (value is LLSDBoolean) {
      buffer.write('<boolean>${value.value}</boolean>');
    } else if (value is LLSDInteger) {
      buffer.write('<integer>${value.value}</integer>');
    } else if (value is LLSDReal) {
      buffer.write('<real>${value.value}</real>');
    } else if (value is LLSDString) {
      buffer.write('<string>${_escapeXml(value.value)}</string>');
    } else if (value is LLSDUUID) {
      buffer.write('<uuid>${value.value}</uuid>');
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
    if (xmlString.contains('<boolean>true</boolean>') || xmlString.contains('<boolean>1</boolean>')) {
      if (xmlString.contains('<map>')) {
        return _parseSimpleXmlMap(xmlString);
      }
      return const LLSDBoolean(true);
    }
    if (xmlString.contains('<map>')) {
      return _parseSimpleXmlMap(xmlString);
    }
    return const LLSDMap({});
  }

  static LLSDValue _parseSimpleXmlMap(String xmlString) {
    final map = <String, LLSDValue>{};
    final keyRegExp = RegExp(r'<key>(.*?)</key>\s*<(string|integer|boolean|uuid)>(.*?)</\2>');
    for (final match in keyRegExp.allMatches(xmlString)) {
      final key = match.group(1) ?? '';
      final type = match.group(2);
      final val = match.group(3) ?? '';
      if (type == 'string') {
        map[key] = LLSDString(val);
      } else if (type == 'integer') {
        map[key] = LLSDInteger(int.tryParse(val) ?? 0);
      } else if (type == 'boolean') {
        map[key] = LLSDBoolean(val == 'true' || val == '1');
      } else if (type == 'uuid') {
        map[key] = LLSDUUID(LLUUID.parse(val));
      }
    }
    return LLSDMap(map);
  }
}
