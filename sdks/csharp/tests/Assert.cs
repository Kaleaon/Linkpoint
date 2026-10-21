using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace Linkpoint.LLSD.Tests
{
    public static class Assert
    {
        public static void True(bool condition, string? message = null)
        {
            if (!condition)
            {
                throw new Exception($"Assert.True failed. {message}");
            }
        }

        public static void False(bool condition, string? message = null)
        {
            if (condition)
            {
                throw new Exception($"Assert.False failed. {message}");
            }
        }

        public static void Equal<T>(T expected, T actual, string? message = null)
        {
            if (expected is byte[] expBytes && actual is byte[] actBytes)
            {
                if (!expBytes.SequenceEqual(actBytes))
                {
                    throw new Exception($"Assert.Equal failed for byte arrays. {message}");
                }
                return;
            }

            if (!EqualityComparer<T>.Default.Equals(expected, actual))
            {
                throw new Exception($"Assert.Equal failed. Expected: '{expected}', Actual: '{actual}'. {message}");
            }
        }

        public static void Equal(double expected, double actual, int precision)
        {
            double diff = Math.Abs(expected - actual);
            double tolerance = Math.Pow(10, -precision);
            if (diff > tolerance && !(double.IsNaN(expected) && double.IsNaN(actual)))
            {
                throw new Exception($"Assert.Equal failed for double. Expected: '{expected}', Actual: '{actual}', Diff: '{diff}'.");
            }
        }

        public static void Contains<T>(T item, IEnumerable<T> collection)
        {
            if (!collection.Contains(item))
            {
                throw new Exception($"Assert.Contains failed. Item '{item}' not found in collection.");
            }
        }

        public static void Empty<T>(IEnumerable<T> collection)
        {
            if (collection.Any())
            {
                throw new Exception("Assert.Empty failed. Collection is not empty.");
            }
        }
    }
}
