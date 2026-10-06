import 'dart:ffi';
import 'dart:io';
import 'dart:convert';
import 'package:ffi/ffi.dart';

final class LlsdResultBufferStruct extends Struct {
  external Pointer<Uint8> ptr;

  @IntPtr()
  external int len;

  @IntPtr()
  external int cap;

  @Int32()
  external int status;

  external Pointer<Utf8> errorPtr;
}

typedef NativeLinkpointFreeBuffer = Void Function(Pointer<LlsdResultBufferStruct>);
typedef DartLinkpointFreeBuffer = void Function(Pointer<LlsdResultBufferStruct>);

typedef NativeLinkpointParse = Pointer<LlsdResultBufferStruct> Function(Pointer<Uint8>, IntPtr);
typedef DartLinkpointParse = Pointer<LlsdResultBufferStruct> Function(Pointer<Uint8>, int);

class LinkpointProtocolFFI {
  static DynamicLibrary? _lib;

  static bool get isAvailable {
    try {
      _loadLibrary();
      return true;
    } catch (_) {
      return false;
    }
  }

  static bool isAvailable() => isAvailable;

  static DynamicLibrary _loadLibrary() {
    if (_lib != null) return _lib!;
    if (Platform.isLinux) {
      final searchPaths = [
        'liblinkpoint_protocol.so',
        '../../target/release/liblinkpoint_protocol.so',
        '../target/release/liblinkpoint_protocol.so',
        'target/release/liblinkpoint_protocol.so',
        '../../target/debug/liblinkpoint_protocol.so',
        '../target/debug/liblinkpoint_protocol.so',
        'target/debug/liblinkpoint_protocol.so',
        '/app/Linkpoint/target/release/liblinkpoint_protocol.so',
      ];
      for (final p in searchPaths) {
        if (File(p).existsSync()) {
          try {
            _lib = DynamicLibrary.open(p);
            return _lib!;
          } catch (_) {}
        }
      }
      _lib = DynamicLibrary.open('liblinkpoint_protocol.so');
    } else if (Platform.isMacOS) {
      final searchPaths = [
        'liblinkpoint_protocol.dylib',
        '../../target/release/liblinkpoint_protocol.dylib',
        '../target/release/liblinkpoint_protocol.dylib',
        'target/release/liblinkpoint_protocol.dylib',
        '../../target/debug/liblinkpoint_protocol.dylib',
        '../target/debug/liblinkpoint_protocol.dylib',
        'target/debug/liblinkpoint_protocol.dylib',
        '/app/Linkpoint/target/release/liblinkpoint_protocol.dylib',
      ];
      for (final p in searchPaths) {
        if (File(p).existsSync()) {
          try {
            _lib = DynamicLibrary.open(p);
            return _lib!;
          } catch (_) {}
        }
      }
      _lib = DynamicLibrary.open('liblinkpoint_protocol.dylib');
    } else if (Platform.isWindows) {
      final searchPaths = [
        'linkpoint_protocol.dll',
        '../../target/release/linkpoint_protocol.dll',
        '../target/release/linkpoint_protocol.dll',
        'target/release/linkpoint_protocol.dll',
        '../../target/debug/linkpoint_protocol.dll',
        '../target/debug/linkpoint_protocol.dll',
        'target/debug/linkpoint_protocol.dll',
        '/app/Linkpoint/target/release/linkpoint_protocol.dll',
      ];
      for (final p in searchPaths) {
        if (File(p).existsSync()) {
          try {
            _lib = DynamicLibrary.open(p);
            return _lib!;
          } catch (_) {}
        }
      }
      _lib = DynamicLibrary.open('linkpoint_protocol.dll');
    } else {
      _lib = DynamicLibrary.process();
    }
    return _lib!;
  }

  static Pointer<LlsdResultBufferStruct> _callFFI(
    String functionName,
    List<int> inputBytes,
  ) {
    final lib = _loadLibrary();
    final nativeFn = lib.lookupFunction<NativeLinkpointParse, DartLinkpointParse>(functionName);

    final inputPtr = calloc<Uint8>(inputBytes.length);
    final nativeList = inputPtr.asTypedList(inputBytes.length);
    nativeList.setAll(0, inputBytes);

    try {
      return nativeFn(inputPtr, inputBytes.length);
    } finally {
      calloc.free(inputPtr);
    }
  }

  static void freeBuffer(Pointer<LlsdResultBufferStruct> buf) {
    if (buf == nullptr) return;
    final lib = _loadLibrary();
    final freeFn = lib.lookupFunction<NativeLinkpointFreeBuffer, DartLinkpointFreeBuffer>('linkpoint_free_buffer');
    freeFn(buf);
  }

  static String _extractAndFreeResult(Pointer<LlsdResultBufferStruct> resBuf) {
    if (resBuf == nullptr) {
      throw Exception('FFI returned null buffer');
    }
    try {
      final ref = resBuf.ref;
      if (ref.status != 0) {
        final errMsg = ref.errorPtr != nullptr ? ref.errorPtr.toDartString() : 'Unknown FFI error';
        throw Exception(errMsg);
      }
      if (ref.ptr == nullptr || ref.len == 0) {
        return '';
      }
      final bytes = ref.ptr.asTypedList(ref.len);
      return utf8.decode(bytes);
    } finally {
      freeBuffer(resBuf);
    }
  }

  static List<int> _extractAndFreeBinaryResult(Pointer<LlsdResultBufferStruct> resBuf) {
    if (resBuf == nullptr) {
      throw Exception('FFI returned null buffer');
    }
    try {
      final ref = resBuf.ref;
      if (ref.status != 0) {
        final errMsg = ref.errorPtr != nullptr ? ref.errorPtr.toDartString() : 'Unknown FFI error';
        throw Exception(errMsg);
      }
      if (ref.ptr == nullptr || ref.len == 0) {
        return [];
      }
      return List<int>.from(ref.ptr.asTypedList(ref.len));
    } finally {
      freeBuffer(resBuf);
    }
  }

  static String parseXml(String xmlString) {
    final bytes = utf8.encode(xmlString);
    final res = _callFFI('linkpoint_llsd_parse_xml', bytes);
    return _extractAndFreeResult(res);
  }

  static String parseBinary(List<int> binaryBytes) {
    final res = _callFFI('linkpoint_llsd_parse_binary', binaryBytes);
    return _extractAndFreeResult(res);
  }

  static String parseNotation(String notationString) {
    final bytes = utf8.encode(notationString);
    final res = _callFFI('linkpoint_llsd_parse_notation', bytes);
    return _extractAndFreeResult(res);
  }

  static String serializeXml(String jsonString) {
    final bytes = utf8.encode(jsonString);
    final res = _callFFI('linkpoint_llsd_serialize_xml', bytes);
    return _extractAndFreeResult(res);
  }

  static List<int> serializeBinary(String jsonString) {
    final bytes = utf8.encode(jsonString);
    final res = _callFFI('linkpoint_llsd_serialize_binary', bytes);
    return _extractAndFreeBinaryResult(res);
  }

  static String serializeNotation(String jsonString) {
    final bytes = utf8.encode(jsonString);
    final res = _callFFI('linkpoint_llsd_serialize_notation', bytes);
    return _extractAndFreeResult(res);
  }
}
