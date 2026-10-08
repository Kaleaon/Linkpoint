import Foundation

public struct RegionFlags: OptionSet {
    public let rawValue: UInt32
    public init(rawValue: UInt32) { self.rawValue = rawValue }

    public static let allowDamage     = RegionFlags(rawValue: 1 << 0)
    public static let allowLandResell = RegionFlags(rawValue: 1 << 1)
    public static let allowMoreResell = RegionFlags(rawValue: 1 << 2)
    public static let isSandbox       = RegionFlags(rawValue: 1 << 3)
    public static let allowVoice      = RegionFlags(rawValue: 1 << 28)
}

public struct ObjectFlags: OptionSet {
    public let rawValue: UInt32
    public init(rawValue: UInt32) { self.rawValue = rawValue }

    public static let physics            = ObjectFlags(rawValue: 0x00000001)
    public static let createSelected     = ObjectFlags(rawValue: 0x00000002)
    public static let allowInventoryDrop = ObjectFlags(rawValue: 0x00000004)
    public static let phantom            = ObjectFlags(rawValue: 0x00000010)
    public static let castShadows        = ObjectFlags(rawValue: 0x00000020)
}

public struct RegionHandshakeMessage {
    public let regionFlags: UInt32
    public let simAccess: UInt8
    public let simName: String
    public let simOwner: UUID
    public let isEstateManager: Bool
    public let waterHeight: Float
    public let billableFactor: Float
    public let cacheId: UUID
    public let terrainBaseTextures: [UUID]
    public let terrainDetailTextures: [UUID]
    public let terrainStartHeight: [Float]
    public let terrainHeightRange: [Float]
}

public struct RegionHandshakeDecoder {
    public static func decode(payload: Data) -> RegionHandshakeMessage? {
        guard payload.count >= 189 else { return nil }
        var offset = payload.startIndex

        let rFlags = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { UInt32(bigEndian: $0.load(as: UInt32.self)) }
        offset += 4

        let simAccess = payload[offset]
        offset += 1

        let nameLen = Int(payload[offset])
        offset += 1

        guard offset + nameLen <= payload.endIndex else { return nil }
        let nameData = payload.subdata(in: offset..<(offset + nameLen))
        let simName = (String(data: nameData, encoding: .utf8) ?? "").trimmingCharacters(in: CharacterSet(charactersIn: "\0 "))
        offset += nameLen

        guard offset + 16 <= payload.endIndex else { return nil }
        let simOwner = readUUID(data: payload, offset: offset)
        offset += 16

        let isEstateManager = payload[offset] != 0
        offset += 1

        let waterHeight = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }
        offset += 4

        let billableFactor = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }
        offset += 4

        let cacheId = readUUID(data: payload, offset: offset)
        offset += 16

        var baseTex: [UUID] = []
        for _ in 0..<4 {
            baseTex.append(readUUID(data: payload, offset: offset))
            offset += 16
        }

        var detailTex: [UUID] = []
        for _ in 0..<4 {
            detailTex.append(readUUID(data: payload, offset: offset))
            offset += 16
        }

        var startH: [Float] = []
        for _ in 0..<4 {
            startH.append(payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) })
            offset += 4
        }

        var rangeH: [Float] = []
        for _ in 0..<4 {
            rangeH.append(payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) })
            offset += 4
        }

        return RegionHandshakeMessage(
            regionFlags: rFlags,
            simAccess: simAccess,
            simName: simName,
            simOwner: simOwner,
            isEstateManager: isEstateManager,
            waterHeight: waterHeight,
            billableFactor: billableFactor,
            cacheId: cacheId,
            terrainBaseTextures: baseTex,
            terrainDetailTextures: detailTex,
            terrainStartHeight: startH,
            terrainHeightRange: rangeH
        )
    }

    private static func readUUID(data: Data, offset: Int) -> UUID {
        let bytes = data.subdata(in: offset..<(offset+16))
        let tuple: uuid_t = (
            bytes[bytes.startIndex], bytes[bytes.startIndex+1], bytes[bytes.startIndex+2], bytes[bytes.startIndex+3],
            bytes[bytes.startIndex+4], bytes[bytes.startIndex+5], bytes[bytes.startIndex+6], bytes[bytes.startIndex+7],
            bytes[bytes.startIndex+8], bytes[bytes.startIndex+9], bytes[bytes.startIndex+10], bytes[bytes.startIndex+11],
            bytes[bytes.startIndex+12], bytes[bytes.startIndex+13], bytes[bytes.startIndex+14], bytes[bytes.startIndex+15]
        )
        return UUID(uuid: tuple)
    }
}

public struct ObjectUpdateMessage {
    public let pCode: UInt8
    public let state: UInt8
    public let id: UUID
    public let localId: UInt32
    public let position: (Float, Float, Float)
    public let rotation: (Float, Float, Float, Float)
    public let velocity: (Float, Float, Float)
    public let flags: UInt32
}

public struct ObjectUpdateDecoder {
    public static func decode(payload: Data) -> ObjectUpdateMessage? {
        guard payload.count >= 60 else { return nil }
        var offset = payload.startIndex

        let pCode = payload[offset]; offset += 1
        let state = payload[offset]; offset += 1

        let bytes = payload.subdata(in: offset..<(offset+16))
        let tuple: uuid_t = (
            bytes[bytes.startIndex], bytes[bytes.startIndex+1], bytes[bytes.startIndex+2], bytes[bytes.startIndex+3],
            bytes[bytes.startIndex+4], bytes[bytes.startIndex+5], bytes[bytes.startIndex+6], bytes[bytes.startIndex+7],
            bytes[bytes.startIndex+8], bytes[bytes.startIndex+9], bytes[bytes.startIndex+10], bytes[bytes.startIndex+11],
            bytes[bytes.startIndex+12], bytes[bytes.startIndex+13], bytes[bytes.startIndex+14], bytes[bytes.startIndex+15]
        )
        let id = UUID(uuid: tuple)
        offset += 16

        let localId = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { UInt32(bigEndian: $0.load(as: UInt32.self)) }
        offset += 4

        let posX = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4
        let posY = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4
        let posZ = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4

        let rotX = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4
        let rotY = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4
        let rotZ = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4
        let rotW = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4

        let velX = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4
        let velY = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4
        let velZ = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { Float(bitPattern: UInt32(littleEndian: $0.load(as: UInt32.self))) }; offset += 4

        let flags = payload.subdata(in: offset..<(offset+4)).withUnsafeBytes { UInt32(bigEndian: $0.load(as: UInt32.self)) }

        return ObjectUpdateMessage(
            pCode: pCode,
            state: state,
            id: id,
            localId: localId,
            position: (posX, posY, posZ),
            rotation: (rotX, rotY, rotZ, rotW),
            velocity: (velX, velY, velZ),
            flags: flags
        )
    }
}
