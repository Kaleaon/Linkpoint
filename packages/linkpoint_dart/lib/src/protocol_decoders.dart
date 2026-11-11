import 'dart:convert';
import 'dart:typed_data';

/// Bitfield utilities for RegionHandshake flags
class RegionFlags {
  static const int allowDamage = 1 << 0;
  static const int allowLandResell = 1 << 1;
  static const int allowMoreResell = 1 << 2;
  static const int isSandbox = 1 << 3;
  static const int allowVoice = 1 << 28;

  static bool hasFlag(int flags, int flag) => (flags & flag) != 0;
}

/// Bitfield utilities for ObjectUpdate flags
class ObjectFlags {
  static const int physics = 0x00000001;
  static const int createSelected = 0x00000002;
  static const int allowInventoryDrop = 0x00000004;
  static const int phantom = 0x00000010;
  static const int castShadows = 0x00000020;

  static bool hasFlag(int flags, int flag) => (flags & flag) != 0;
}

class RegionHandshakeData {
  final int regionFlags;
  final int simAccess;
  final String simName;
  final String simOwner;
  final bool isEstateManager;
  final double waterHeight;
  final double billableFactor;
  final String cacheId;
  final List<String> terrainBaseTextures;
  final List<String> terrainDetailTextures;
  final List<double> terrainStartHeight;
  final List<double> terrainHeightRange;

  const RegionHandshakeData({
    required this.regionFlags,
    required this.simAccess,
    required this.simName,
    required this.simOwner,
    required this.isEstateManager,
    required this.waterHeight,
    required this.billableFactor,
    required this.cacheId,
    required this.terrainBaseTextures,
    required this.terrainDetailTextures,
    required this.terrainStartHeight,
    required this.terrainHeightRange,
  });
}

class RegionHandshakeDecoder {
  static RegionHandshakeData? decode(Uint8List payload) {
    if (payload.length < 189) return null;
    final bd = ByteData.sublistView(payload);
    var offset = 0;

    final regionFlags = bd.getUint32(offset, Endian.big);
    offset += 4;

    final simAccess = bd.getUint8(offset++);

    final nameLen = bd.getUint8(offset++);
    final simName = utf8
        .decode(payload.sublist(offset, offset + nameLen))
        .replaceAll('\x00', '')
        .trim();
    offset += nameLen;

    final simOwner = _readUuid(payload, offset);
    offset += 16;

    final isEstateManager = bd.getUint8(offset++) != 0;
    final waterHeight = bd.getFloat32(offset, Endian.little);
    offset += 4;

    final billableFactor = bd.getFloat32(offset, Endian.little);
    offset += 4;

    final cacheId = _readUuid(payload, offset);
    offset += 16;

    final baseTextures = <String>[];
    for (var i = 0; i < 4; i++) {
      baseTextures.add(_readUuid(payload, offset));
      offset += 16;
    }

    final detailTextures = <String>[];
    for (var i = 0; i < 4; i++) {
      detailTextures.add(_readUuid(payload, offset));
      offset += 16;
    }

    final startHeights = <double>[];
    for (var i = 0; i < 4; i++) {
      startHeights.add(bd.getFloat32(offset, Endian.little));
      offset += 4;
    }

    final heightRanges = <double>[];
    for (var i = 0; i < 4; i++) {
      heightRanges.add(bd.getFloat32(offset, Endian.little));
      offset += 4;
    }

    return RegionHandshakeData(
      regionFlags: regionFlags,
      simAccess: simAccess,
      simName: simName,
      simOwner: simOwner,
      isEstateManager: isEstateManager,
      waterHeight: waterHeight,
      billableFactor: billableFactor,
      cacheId: cacheId,
      terrainBaseTextures: baseTextures,
      terrainDetailTextures: detailTextures,
      terrainStartHeight: startHeights,
      terrainHeightRange: heightRanges,
    );
  }

  static String _readUuid(Uint8List bytes, int offset) {
    final sb = StringBuffer();
    for (var i = 0; i < 16; i++) {
      if (i == 4 || i == 6 || i == 8 || i == 10) sb.write('-');
      sb.write(bytes[offset + i].toRadixString(16).padLeft(2, '0'));
    }
    return sb.toString();
  }
}

class ObjectUpdateData {
  final int pCode;
  final int state;
  final String id;
  final int localId;
  final List<double> position;
  final List<double> rotation;
  final List<double> velocity;
  final int flags;

  const ObjectUpdateData({
    required this.pCode,
    required this.state,
    required this.id,
    required this.localId,
    required this.position,
    required this.rotation,
    required this.velocity,
    required this.flags,
  });
}

class ObjectUpdateDecoder {
  static ObjectUpdateData? decode(Uint8List payload) {
    if (payload.length < 60) return null;
    final bd = ByteData.sublistView(payload);
    var offset = 0;

    final pCode = bd.getUint8(offset++);
    final state = bd.getUint8(offset++);

    final id = RegionHandshakeDecoder._readUuid(payload, offset);
    offset += 16;

    final localId = bd.getUint32(offset, Endian.big);
    offset += 4;

    final posX = bd.getFloat32(offset, Endian.little);
    offset += 4;
    final posY = bd.getFloat32(offset, Endian.little);
    offset += 4;
    final posZ = bd.getFloat32(offset, Endian.little);
    offset += 4;

    final rotX = bd.getFloat32(offset, Endian.little);
    offset += 4;
    final rotY = bd.getFloat32(offset, Endian.little);
    offset += 4;
    final rotZ = bd.getFloat32(offset, Endian.little);
    offset += 4;
    final rotW = bd.getFloat32(offset, Endian.little);
    offset += 4;

    final velX = bd.getFloat32(offset, Endian.little);
    offset += 4;
    final velY = bd.getFloat32(offset, Endian.little);
    offset += 4;
    final velZ = bd.getFloat32(offset, Endian.little);
    offset += 4;

    final flags = bd.getUint32(offset, Endian.big);

    return ObjectUpdateData(
      pCode: pCode,
      state: state,
      id: id,
      localId: localId,
      position: [posX, posY, posZ],
      rotation: [rotX, rotY, rotZ, rotW],
      velocity: [velX, velY, velZ],
      flags: flags,
    );
  }
}
