import Foundation

public struct Zerocode {
    /// Decompresses a zerocoded byte stream.
    /// In zerocoded packets, a 0x00 byte followed by N indicates N zero bytes.
    public static func decompress(_ data: Data) -> Data {
        var result = Data()
        result.reserveCapacity(data.count * 2)

        var i = data.startIndex
        let end = data.endIndex

        while i < end {
            let byte = data[i]
            if byte == 0x00 {
                i += 1
                if i < end {
                    let zeroCount = Int(data[i])
                    if zeroCount > 0 {
                        result.append(contentsOf: [UInt8](repeating: 0x00, count: zeroCount))
                    }
                    i += 1
                } else {
                    result.append(0x00)
                }
            } else {
                result.append(byte)
                i += 1
            }
        }
        return result
    }

    /// Compresses a byte stream using zerocode algorithm.
    public static func compress(_ data: Data) -> Data {
        var result = Data()
        var i = data.startIndex
        let end = data.endIndex

        while i < end {
            let byte = data[i]
            if byte == 0x00 {
                var zeroCount = 0
                while i < end && data[i] == 0x00 && zeroCount < 255 {
                    zeroCount += 1
                    i += 1
                }
                result.append(0x00)
                result.append(UInt8(zeroCount))
            } else {
                result.append(byte)
                i += 1
            }
        }
        return result
    }
}
