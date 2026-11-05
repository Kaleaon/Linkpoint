using System;
using System.Buffers.Binary;
using System.Collections.Generic;

namespace Linkpoint.LLSD.Network
{
    [Flags]
    public enum PacketFlags : byte
    {
        None = 0x00,
        Acks = 0x10,
        Resent = 0x20,
        Reliable = 0x40,
        ZeroCoded = 0x80
    }

    public class PacketHeader
    {
        public byte Flags { get; set; }
        public uint SequenceNumber { get; set; }
        public byte ExtraBytes { get; set; }
        public uint MessageId { get; set; }
        public List<uint> Acks { get; } = new List<uint>();

        public bool IsReliable => (Flags & 0x40) != 0;
        public bool IsZeroCoded => (Flags & 0x80) != 0;
        public bool IsResent => (Flags & 0x20) != 0;
        public bool HasAcks => (Flags & 0x10) != 0;
    }

    /// <summary>
    /// Helper for Second Life UDP packet header encoding/decoding and Zerocoded expansion.
    /// </summary>
    public static class PacketCodec
    {
        public static PacketHeader DecodeHeader(ReadOnlySpan<byte> buffer, out int headerLength)
        {
            if (buffer.Length < 6)
            {
                throw new ArgumentException("Buffer too small for packet header.", nameof(buffer));
            }

            var header = new PacketHeader
            {
                Flags = buffer[0],
                SequenceNumber = BinaryPrimitives.ReadUInt32BigEndian(buffer.Slice(1, 4)),
                ExtraBytes = buffer[5]
            };

            int offset = 6 + header.ExtraBytes;

            // Decode Message ID (High, Medium, or Low frequency)
            if (offset < buffer.Length)
            {
                byte first = buffer[offset];
                if (first != 0xFF)
                {
                    // High frequency (1 byte)
                    header.MessageId = first;
                    offset += 1;
                }
                else if (offset + 1 < buffer.Length && buffer[offset + 1] != 0xFF)
                {
                    // Medium frequency (2 bytes)
                    header.MessageId = (uint)((0xFF << 8) | buffer[offset + 1]);
                    offset += 2;
                }
                else if (offset + 3 < buffer.Length)
                {
                    // Low frequency (4 bytes)
                    header.MessageId = BinaryPrimitives.ReadUInt32BigEndian(buffer.Slice(offset, 4));
                    offset += 4;
                }
            }

            // Extract trailing ACKs if Acks flag is set
            if (header.HasAcks && buffer.Length > 0)
            {
                int ackCount = buffer[buffer.Length - 1];
                int ackStart = buffer.Length - 1 - (ackCount * 4);
                if (ackStart >= offset)
                {
                    for (int i = 0; i < ackCount; i++)
                    {
                        uint ackSeq = BinaryPrimitives.ReadUInt32BigEndian(buffer.Slice(ackStart + (i * 4), 4));
                        header.Acks.Add(ackSeq);
                    }
                }
            }

            headerLength = offset;
            return header;
        }

        /// <summary>
        /// Expands Zerocoded byte stream where 0x00 is followed by a count byte indicating run length of zero bytes.
        /// </summary>
        public static byte[] ZeroDecode(ReadOnlySpan<byte> src, int headerLen)
        {
            if (src.Length <= headerLen) return src.ToArray();

            var dst = new List<byte>(src.Length * 2);
            for (int i = 0; i < headerLen; i++)
            {
                dst.Add(src[i]);
            }

            int pos = headerLen;
            while (pos < src.Length)
            {
                byte b = src[pos++];
                if (b == 0x00)
                {
                    if (pos < src.Length)
                    {
                        int zeroCount = src[pos++];
                        for (int z = 0; z < zeroCount; z++)
                        {
                            dst.Add(0x00);
                        }
                    }
                    else
                    {
                        dst.Add(0x00);
                    }
                }
                else
                {
                    dst.Add(b);
                }
            }

            return dst.ToArray();
        }

        /// <summary>
        /// Compresses body bytes using Zerocode run-length encoding.
        /// </summary>
        public static byte[] ZeroEncode(ReadOnlySpan<byte> body)
        {
            var dst = new List<byte>(body.Length);
            int i = 0;
            while (i < body.Length)
            {
                if (body[i] == 0x00)
                {
                    int zeroStart = i;
                    while (i < body.Length && body[i] == 0x00 && (i - zeroStart) < 255)
                    {
                        i++;
                    }
                    int count = i - zeroStart;
                    dst.Add(0x00);
                    dst.Add((byte)count);
                }
                else
                {
                    dst.Add(body[i++]);
                }
            }
            return dst.ToArray();
        }
    }
}
