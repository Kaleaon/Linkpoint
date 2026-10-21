using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;

namespace Linkpoint.LLSD
{
    public enum LLSDType
    {
        Undefined,
        Boolean,
        Integer,
        Real,
        String,
        UUID,
        Date,
        URI,
        Binary,
        Map,
        Array
    }

    /// <summary>
    /// Represents a Linden Lab Structured Data (LLSD) variant value node.
    /// </summary>
    public sealed class LLSDValue : IEquatable<LLSDValue>
    {
        public static readonly LLSDValue Undefined = new LLSDValue(LLSDType.Undefined, null);

        public LLSDType Type { get; }
        public object? RawValue { get; }

        private LLSDValue(LLSDType type, object? value)
        {
            Type = type;
            RawValue = value;
        }

        #region Factory Methods

        public static LLSDValue FromBoolean(bool value) => new LLSDValue(LLSDType.Boolean, value);
        public static LLSDValue FromInteger(int value) => new LLSDValue(LLSDType.Integer, value);
        public static LLSDValue FromReal(double value) => new LLSDValue(LLSDType.Real, value);
        public static LLSDValue FromString(string? value) => new LLSDValue(LLSDType.String, value ?? string.Empty);
        public static LLSDValue FromUUID(LLUUID value) => new LLSDValue(LLSDType.UUID, value);
        public static LLSDValue FromDate(LLDate value) => new LLSDValue(LLSDType.Date, value);
        public static LLSDValue FromURI(LLURI value) => new LLSDValue(LLSDType.URI, value);
        public static LLSDValue FromBinary(byte[]? value) => new LLSDValue(LLSDType.Binary, value ?? Array.Empty<byte>());
        public static LLSDValue FromMap(IDictionary<string, LLSDValue>? value)
        {
            var map = new Dictionary<string, LLSDValue>(StringComparer.Ordinal);
            if (value != null)
            {
                foreach (var kvp in value)
                {
                    map[kvp.Key] = kvp.Value ?? Undefined;
                }
            }
            return new LLSDValue(LLSDType.Map, map);
        }

        public static LLSDValue FromArray(IEnumerable<LLSDValue>? value)
        {
            var list = new List<LLSDValue>();
            if (value != null)
            {
                foreach (var item in value)
                {
                    list.Add(item ?? Undefined);
                }
            }
            return new LLSDValue(LLSDType.Array, list);
        }

        public static LLSDValue EmptyMap() => FromMap(null);
        public static LLSDValue EmptyArray() => FromArray(null);

        #endregion

        #region Implicit Operators

        public static implicit operator LLSDValue(bool value) => FromBoolean(value);
        public static implicit operator LLSDValue(int value) => FromInteger(value);
        public static implicit operator LLSDValue(double value) => FromReal(value);
        public static implicit operator LLSDValue(float value) => FromReal(value);
        public static implicit operator LLSDValue(string value) => FromString(value);
        public static implicit operator LLSDValue(LLUUID value) => FromUUID(value);
        public static implicit operator LLSDValue(LLDate value) => FromDate(value);
        public static implicit operator LLSDValue(LLURI value) => FromURI(value);
        public static implicit operator LLSDValue(byte[] value) => FromBinary(value);

        #endregion

        #region Type Checks

        public bool IsUndefined => Type == LLSDType.Undefined;
        public bool IsDefined => Type != LLSDType.Undefined;
        public bool IsBoolean => Type == LLSDType.Boolean;
        public bool IsInteger => Type == LLSDType.Integer;
        public bool IsReal => Type == LLSDType.Real;
        public bool IsString => Type == LLSDType.String;
        public bool IsUUID => Type == LLSDType.UUID;
        public bool IsDate => Type == LLSDType.Date;
        public bool IsURI => Type == LLSDType.URI;
        public bool IsBinary => Type == LLSDType.Binary;
        public bool IsMap => Type == LLSDType.Map;
        public bool IsArray => Type == LLSDType.Array;

        #endregion

        #region Value Conversion Helpers

        public bool AsBoolean()
        {
            return Type switch
            {
                LLSDType.Undefined => false,
                LLSDType.Boolean => (bool)RawValue!,
                LLSDType.Integer => (int)RawValue! != 0,
                LLSDType.Real => Math.Abs((double)RawValue!) > double.Epsilon,
                LLSDType.String => !string.IsNullOrEmpty((string)RawValue!),
                LLSDType.UUID => ((LLUUID)RawValue!).NotNull,
                LLSDType.Date => ((LLDate)RawValue!).NotNull,
                LLSDType.URI => !string.IsNullOrEmpty(((LLURI)RawValue!).AsString()),
                LLSDType.Binary => ((byte[])RawValue!).Length > 0,
                LLSDType.Map => ((Dictionary<string, LLSDValue>)RawValue!).Count > 0,
                LLSDType.Array => ((List<LLSDValue>)RawValue!).Count > 0,
                _ => false
            };
        }

        public int AsInteger()
        {
            return Type switch
            {
                LLSDType.Undefined => 0,
                LLSDType.Boolean => (bool)RawValue! ? 1 : 0,
                LLSDType.Integer => (int)RawValue!,
                LLSDType.Real => Convert.ToInt32((double)RawValue!),
                LLSDType.String => int.TryParse((string)RawValue!, NumberStyles.Any, CultureInfo.InvariantCulture, out int val) ? val : 0,
                _ => 0
            };
        }

        public double AsReal()
        {
            return Type switch
            {
                LLSDType.Undefined => 0.0,
                LLSDType.Boolean => (bool)RawValue! ? 1.0 : 0.0,
                LLSDType.Integer => (int)RawValue!,
                LLSDType.Real => (double)RawValue!,
                LLSDType.String => double.TryParse((string)RawValue!, NumberStyles.Any, CultureInfo.InvariantCulture, out double val) ? val : 0.0,
                _ => 0.0
            };
        }

        public float AsFloat() => (float)AsReal();

        public string AsString()
        {
            return Type switch
            {
                LLSDType.Undefined => string.Empty,
                LLSDType.Boolean => (bool)RawValue! ? "true" : "false",
                LLSDType.Integer => ((int)RawValue!).ToString(CultureInfo.InvariantCulture),
                LLSDType.Real => ((double)RawValue!).ToString(CultureInfo.InvariantCulture),
                LLSDType.String => (string)RawValue!,
                LLSDType.UUID => ((LLUUID)RawValue!).ToString(),
                LLSDType.Date => ((LLDate)RawValue!).ToISOString(),
                LLSDType.URI => ((LLURI)RawValue!).AsString(),
                LLSDType.Binary => Convert.ToBase64String((byte[])RawValue!),
                _ => string.Empty
            };
        }

        public LLUUID AsUUID()
        {
            return Type switch
            {
                LLSDType.UUID => (LLUUID)RawValue!,
                LLSDType.String => LLUUID.FromString((string)RawValue!),
                _ => LLUUID.Zero
            };
        }

        public LLDate AsDate()
        {
            return Type switch
            {
                LLSDType.Date => (LLDate)RawValue!,
                LLSDType.String => LLDate.FromISOString((string)RawValue!),
                _ => LLDate.Null
            };
        }

        public LLURI AsURI()
        {
            return Type switch
            {
                LLSDType.URI => (LLURI)RawValue!,
                LLSDType.String => LLURI.FromString((string)RawValue!),
                _ => LLURI.Empty
            };
        }

        public byte[] AsBinary()
        {
            return Type switch
            {
                LLSDType.Binary => (byte[])RawValue!,
                LLSDType.String => Convert.FromBase64String((string)RawValue!),
                _ => Array.Empty<byte>()
            };
        }

        public Dictionary<string, LLSDValue> AsMap()
        {
            if (Type == LLSDType.Map)
            {
                return (Dictionary<string, LLSDValue>)RawValue!;
            }
            return new Dictionary<string, LLSDValue>(StringComparer.Ordinal);
        }

        public List<LLSDValue> AsArray()
        {
            if (Type == LLSDType.Array)
            {
                return (List<LLSDValue>)RawValue!;
            }
            return new List<LLSDValue>();
        }

        #endregion

        #region Container Accessors

        public int Count
        {
            get
            {
                if (Type == LLSDType.Map) return ((Dictionary<string, LLSDValue>)RawValue!).Count;
                if (Type == LLSDType.Array) return ((List<LLSDValue>)RawValue!).Count;
                return 0;
            }
        }

        public LLSDValue this[string key]
        {
            get
            {
                if (Type == LLSDType.Map && ((Dictionary<string, LLSDValue>)RawValue!).TryGetValue(key, out var value))
                {
                    return value;
                }
                return Undefined;
            }
            set
            {
                if (Type == LLSDType.Map)
                {
                    ((Dictionary<string, LLSDValue>)RawValue!)[key] = value ?? Undefined;
                }
            }
        }

        public LLSDValue this[int index]
        {
            get
            {
                if (Type == LLSDType.Array)
                {
                    var list = (List<LLSDValue>)RawValue!;
                    if (index >= 0 && index < list.Count)
                    {
                        return list[index];
                    }
                }
                return Undefined;
            }
            set
            {
                if (Type == LLSDType.Array)
                {
                    var list = (List<LLSDValue>)RawValue!;
                    if (index >= 0 && index < list.Count)
                    {
                        list[index] = value ?? Undefined;
                    }
                }
            }
        }

        public bool Has(string key)
        {
            return Type == LLSDType.Map && ((Dictionary<string, LLSDValue>)RawValue!).ContainsKey(key);
        }

        #endregion

        #region Equality and HashCode

        public bool Equals(LLSDValue? other)
        {
            if (ReferenceEquals(null, other)) return false;
            if (ReferenceEquals(this, other)) return true;
            if (Type != other.Type) return false;

            return Type switch
            {
                LLSDType.Undefined => true,
                LLSDType.Boolean => (bool)RawValue! == (bool)other.RawValue!,
                LLSDType.Integer => (int)RawValue! == (int)other.RawValue!,
                LLSDType.Real => Math.Abs((double)RawValue! - (double)other.RawValue!) < 1e-9 || (double.IsNaN((double)RawValue!) && double.IsNaN((double)other.RawValue!)),
                LLSDType.String => string.Equals((string)RawValue!, (string)other.RawValue!, StringComparison.Ordinal),
                LLSDType.UUID => ((LLUUID)RawValue!).Equals((LLUUID)other.RawValue!),
                LLSDType.Date => ((LLDate)RawValue!).Equals((LLDate)other.RawValue!),
                LLSDType.URI => ((LLURI)RawValue!).Equals((LLURI)other.RawValue!),
                LLSDType.Binary => ((byte[])RawValue!).SequenceEqual((byte[])other.RawValue!),
                LLSDType.Map => MapEquals((Dictionary<string, LLSDValue>)RawValue!, (Dictionary<string, LLSDValue>)other.RawValue!),
                LLSDType.Array => ArrayEquals((List<LLSDValue>)RawValue!, (List<LLSDValue>)other.RawValue!),
                _ => false
            };
        }

        private static bool MapEquals(Dictionary<string, LLSDValue> a, Dictionary<string, LLSDValue> b)
        {
            if (a.Count != b.Count) return false;
            foreach (var kvp in a)
            {
                if (!b.TryGetValue(kvp.Key, out var bVal) || !kvp.Value.Equals(bVal)) return false;
            }
            return true;
        }

        private static bool ArrayEquals(List<LLSDValue> a, List<LLSDValue> b)
        {
            if (a.Count != b.Count) return false;
            for (int i = 0; i < a.Count; i++)
            {
                if (!a[i].Equals(b[i])) return false;
            }
            return true;
        }

        public override bool Equals(object? obj) => Equals(obj as LLSDValue);

        public override int GetHashCode()
        {
            return Type switch
            {
                LLSDType.Undefined => 0,
                _ => HashCode.Combine(Type, RawValue)
            };
        }

        #endregion

        public override string ToString() => AsString();
    }
}
