import Foundation

public enum SpatialCodecError: Error {
    case invalidRange
}

public struct Vector3U16 {
    public static func dequantize(
        u16X: UInt16,
        u16Y: UInt16,
        u16Z: UInt16,
        minVec: (Float, Float, Float) = (-128.0, -128.0, -128.0),
        maxVec: (Float, Float, Float) = (128.0, 128.0, 128.0)
    ) -> (Float, Float, Float) {
        let x = minVec.0 + (Float(u16X) / 65535.0) * (maxVec.0 - minVec.0)
        let y = minVec.1 + (Float(u16Y) / 65535.0) * (maxVec.1 - minVec.1)
        let z = minVec.2 + (Float(u16Z) / 65535.0) * (maxVec.2 - minVec.2)
        return (x, y, z)
    }

    public static func quantize(
        x: Float,
        y: Float,
        z: Float,
        minVec: (Float, Float, Float) = (-128.0, -128.0, -128.0),
        maxVec: (Float, Float, Float) = (128.0, 128.0, 128.0)
    ) -> (UInt16, UInt16, UInt16) {
        let qX = UInt16(clamping: Int(round(((x - minVec.0) / (maxVec.0 - minVec.0)) * 65535.0)))
        let qY = UInt16(clamping: Int(round(((y - minVec.1) / (maxVec.1 - minVec.1)) * 65535.0)))
        let qZ = UInt16(clamping: Int(round(((z - minVec.2) / (maxVec.2 - minVec.2)) * 65535.0)))
        return (qX, qY, qZ)
    }
}

public struct Vector3U8 {
    public static func dequantize(
        u8X: UInt8,
        u8Y: UInt8,
        u8Z: UInt8,
        minVec: (Float, Float, Float) = (0.0, 0.0, 0.0),
        maxVec: (Float, Float, Float) = (255.0, 255.0, 255.0)
    ) -> (Float, Float, Float) {
        let x = minVec.0 + (Float(u8X) / 255.0) * (maxVec.0 - minVec.0)
        let y = minVec.1 + (Float(u8Y) / 255.0) * (maxVec.1 - minVec.1)
        let z = minVec.2 + (Float(u8Z) / 255.0) * (maxVec.2 - minVec.2)
        return (x, y, z)
    }

    public static func quantize(
        x: Float,
        y: Float,
        z: Float,
        minVec: (Float, Float, Float) = (0.0, 0.0, 0.0),
        maxVec: (Float, Float, Float) = (255.0, 255.0, 255.0)
    ) -> (UInt8, UInt8, UInt8) {
        let qX = UInt8(clamping: Int(round(((x - minVec.0) / (maxVec.0 - minVec.0)) * 255.0)))
        let qY = UInt8(clamping: Int(round(((y - minVec.1) / (maxVec.1 - minVec.1)) * 255.0)))
        let qZ = UInt8(clamping: Int(round(((z - minVec.2) / (maxVec.2 - minVec.2)) * 255.0)))
        return (qX, qY, qZ)
    }
}

public struct PackedQuaternion {
    public static func unpack16(xI16: Int16, yI16: Int16, zI16: Int16) -> (Float, Float, Float, Float) {
        let x = Float(xI16) / 32767.0
        let y = Float(yI16) / 32767.0
        let z = Float(zI16) / 32767.0

        let wSq = 1.0 - (x * x + y * y + z * z)
        let w = wSq > 0.0 ? sqrt(wSq) : 0.0

        let mag = sqrt(x * x + y * y + z * z + w * w)
        if mag > 0 {
            return (x / mag, y / mag, z / mag, w / mag)
        }
        return (0.0, 0.0, 0.0, 1.0)
    }
}
