using System;
using System.Globalization;
using System.Text;

namespace Linkpoint.LLSD
{
    /// <summary>
    /// Represents a 128-bit Second Life / OpenSimulator UUID.
    /// </summary>
    public readonly struct LLUUID : IEquatable<LLUUID>, IComparable<LLUUID>
    {
        private readonly ulong _msb;
        private readonly ulong _lsb;

        public static readonly LLUUID Zero = new LLUUID(0, 0);

        public LLUUID(ulong msb, ulong lsb)
        {
            _msb = msb;
            _lsb = lsb;
        }

        public LLUUID(byte[] bytes) : this(bytes.AsSpan())
        {
        }

        public LLUUID(ReadOnlySpan<byte> bytes)
        {
            if (bytes.Length < 16)
            {
                throw new ArgumentException("UUID byte array must be at least 16 bytes.", nameof(bytes));
            }

            ulong msb = 0;
            for (int i = 0; i < 8; i++)
            {
                msb = (msb << 8) | bytes[i];
            }

            ulong lsb = 0;
            for (int i = 8; i < 16; i++)
            {
                lsb = (lsb << 8) | bytes[i];
            }

            _msb = msb;
            _lsb = lsb;
        }

        public ulong MostSignificantBits => _msb;
        public ulong LeastSignificantBits => _lsb;

        public bool IsNull => _msb == 0 && _lsb == 0;
        public bool NotNull => !IsNull;

        public static LLUUID Generate()
        {
            Guid g = Guid.NewGuid();
            byte[] bytes = g.ToByteArray();
            // Convert Guid byte array (which has mixed endianness in .NET) to big endian byte order
            byte[] bigEndian = new byte[16];
            bigEndian[0] = bytes[3];
            bigEndian[1] = bytes[2];
            bigEndian[2] = bytes[1];
            bigEndian[3] = bytes[0];
            bigEndian[4] = bytes[5];
            bigEndian[5] = bytes[4];
            bigEndian[6] = bytes[7];
            bigEndian[7] = bytes[6];
            Array.Copy(bytes, 8, bigEndian, 8, 8);
            return new LLUUID(bigEndian);
        }

        public static LLUUID FromString(string text)
        {
            if (string.IsNullOrWhiteSpace(text))
            {
                return Zero;
            }

            string clean = text.Trim();
            if (clean.StartsWith("{") && clean.EndsWith("}"))
            {
                clean = clean.Substring(1, clean.Length - 2);
            }

            string hexOnly = clean.Replace("-", "");
            if (hexOnly.Length != 32)
            {
                return Zero;
            }

            if (!ulong.TryParse(hexOnly.Substring(0, 16), NumberStyles.HexNumber, CultureInfo.InvariantCulture, out ulong msb))
            {
                return Zero;
            }

            if (!ulong.TryParse(hexOnly.Substring(16, 16), NumberStyles.HexNumber, CultureInfo.InvariantCulture, out ulong lsb))
            {
                return Zero;
            }

            return new LLUUID(msb, lsb);
        }

        public byte[] ToByteArray()
        {
            byte[] bytes = new byte[16];
            WriteToSpan(bytes);
            return bytes;
        }

        public void WriteToSpan(Span<byte> destination)
        {
            if (destination.Length < 16)
            {
                throw new ArgumentException("Destination span must be at least 16 bytes.", nameof(destination));
            }

            for (int i = 0; i < 8; i++)
            {
                destination[i] = (byte)((_msb >> ((7 - i) * 8)) & 0xFF);
            }

            for (int i = 0; i < 8; i++)
            {
                destination[8 + i] = (byte)((_lsb >> ((7 - i) * 8)) & 0xFF);
            }
        }

        public override string ToString()
        {
            byte[] b = ToByteArray();
            return string.Format(
                CultureInfo.InvariantCulture,
                "{0:x2}{1:x2}{2:x2}{3:x2}-{4:x2}{5:x2}-{6:x2}{7:x2}-{8:x2}{9:x2}-{10:x2}{11:x2}{12:x2}{13:x2}{14:x2}{15:x2}",
                b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7],
                b[8], b[9], b[10], b[11], b[12], b[13], b[14], b[15]
            );
        }

        public bool Equals(LLUUID other)
        {
            return _msb == other._msb && _lsb == other._lsb;
        }

        public override bool Equals(object? obj)
        {
            return obj is LLUUID other && Equals(other);
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(_msb, _lsb);
        }

        public int CompareTo(LLUUID other)
        {
            int msbComp = _msb.CompareTo(other._msb);
            if (msbComp != 0) return msbComp;
            return _lsb.CompareTo(other._lsb);
        }

        public static bool operator ==(LLUUID left, LLUUID right) => left.Equals(right);
        public static bool operator !=(LLUUID left, LLUUID right) => !left.Equals(right);
    }
}
