using System;

namespace Linkpoint.LLSD
{
    /// <summary>
    /// Represents an LLSD Uniform Resource Identifier value.
    /// </summary>
    public readonly struct LLURI : IEquatable<LLURI>
    {
        private readonly string _value;

        public static readonly LLURI Empty = new LLURI("");

        public LLURI(string? uri)
        {
            _value = uri ?? "";
        }

        public string Value => _value;

        public static LLURI FromString(string? uri) => new LLURI(uri);

        public string AsString() => _value;

        public override string ToString() => _value;

        public bool Equals(LLURI other) => string.Equals(_value, other._value, StringComparison.Ordinal);

        public override bool Equals(object? obj) => obj is LLURI other && Equals(other);

        public override int GetHashCode() => _value != null ? StringComparer.Ordinal.GetHashCode(_value) : 0;

        public static bool operator ==(LLURI left, LLURI right) => left.Equals(right);
        public static bool operator !=(LLURI left, LLURI right) => !left.Equals(right);
    }
}
