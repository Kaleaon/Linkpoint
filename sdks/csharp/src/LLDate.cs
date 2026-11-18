using System;
using System.Globalization;

namespace Linkpoint.LLSD
{
    /// <summary>
    /// Represents an LLSD date value with epoch-second precision and ISO-8601 formatting.
    /// </summary>
    public readonly struct LLDate : IEquatable<LLDate>, IComparable<LLDate>
    {
        private static readonly DateTime Epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

        public static readonly LLDate Null = new LLDate(0.0);

        public double SecondsSinceEpoch { get; }

        public LLDate(double secondsSinceEpoch)
        {
            SecondsSinceEpoch = secondsSinceEpoch;
        }

        public LLDate(DateTime dateTime)
        {
            DateTime utc = dateTime.ToUniversalTime();
            SecondsSinceEpoch = (utc - Epoch).TotalMilliseconds / 1000.0;
        }

        public DateTime ToDateTime()
        {
            long millis = (long)Math.Round(SecondsSinceEpoch * 1000.0);
            return Epoch.AddMilliseconds(millis);
        }

        public bool IsNull => Math.Abs(SecondsSinceEpoch) < 0.0000001;
        public bool NotNull => !IsNull;

        public static LLDate FromISOString(string? iso)
        {
            if (string.IsNullOrWhiteSpace(iso))
            {
                return Null;
            }

            string clean = iso.Trim();
            if (DateTime.TryParse(clean, CultureInfo.InvariantCulture, DateTimeStyles.AdjustToUniversal | DateTimeStyles.AssumeUniversal, out DateTime parsed))
            {
                return new LLDate(parsed);
            }

            return Null;
        }

        public string ToISOString()
        {
            DateTime dt = ToDateTime();
            if (dt.Millisecond == 0)
            {
                return dt.ToString("yyyy-MM-ddTHH:mm:ssZ", CultureInfo.InvariantCulture);
            }
            return dt.ToString("yyyy-MM-ddTHH:mm:ss.fffZ", CultureInfo.InvariantCulture);
        }

        public override string ToString() => ToISOString();

        public bool Equals(LLDate other) => Math.Abs(SecondsSinceEpoch - other.SecondsSinceEpoch) < 1e-6;

        public override bool Equals(object? obj) => obj is LLDate other && Equals(other);

        public override int GetHashCode() => SecondsSinceEpoch.GetHashCode();

        public int CompareTo(LLDate other) => SecondsSinceEpoch.CompareTo(other.SecondsSinceEpoch);

        public static bool operator ==(LLDate left, LLDate right) => left.Equals(right);
        public static bool operator !=(LLDate left, LLDate right) => !left.Equals(right);
    }
}
