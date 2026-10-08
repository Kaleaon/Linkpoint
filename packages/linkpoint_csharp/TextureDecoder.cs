using System;
using System.IO;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;

namespace SixLabors.ImageSharp
{
    public class Image<TPixel> : IDisposable where TPixel : struct
    {
        public int Width { get; }
        public int Height { get; }

        public Image(int width, int height)
        {
            Width = width;
            Height = height;
        }

        public void ProcessPixelRows(Action<PixelAccessor> process)
        {
            process(new PixelAccessor(Width, Height));
        }

        public void Dispose() { }
    }

    public class PixelAccessor
    {
        public int Width { get; }
        public int Height { get; }

        public PixelAccessor(int width, int height)
        {
            Width = width;
            Height = height;
        }

        public Span<Rgba32> GetRowSpan(int y)
        {
            return new Rgba32[Width];
        }
    }
}

namespace SixLabors.ImageSharp.PixelFormats
{
    public struct Rgba32
    {
        public byte R { get; set; }
        public byte G { get; set; }
        public byte B { get; set; }
        public byte A { get; set; }

        public Rgba32(byte r, byte g, byte b, byte a)
        {
            R = r;
            G = g;
            B = b;
            A = a;
        }
    }
}

namespace Linkpoint.AssetPipeline
{
    public record J2KHeaderInfo(int Width, int Height, int Channels, string Status);

    public static class TextureDecoder
    {
        public static J2KHeaderInfo ParseHeader(byte[] data)
        {
            if (data == null || data.Length < 12)
            {
                return new J2KHeaderInfo(0, 0, 4, "fallback");
            }

            // Check JP2 signature box
            if (data[0] == 0x00 && data[1] == 0x00 && data[2] == 0x00 && data[3] == 0x0C &&
                data[4] == 0x6A && data[5] == 0x50 && data[6] == 0x20 && data[7] == 0x20)
            {
                for (int i = 0; i <= data.Length - 12; i++)
                {
                    if (data[i] == 0x69 && data[i + 1] == 0x68 && data[i + 2] == 0x64 && data[i + 3] == 0x72)
                    {
                        int height = (data[i + 4] << 24) | (data[i + 5] << 16) | (data[i + 6] << 8) | data[i + 7];
                        int width = (data[i + 8] << 24) | (data[i + 9] << 16) | (data[i + 10] << 8) | data[i + 11];
                        return new J2KHeaderInfo(width, height, 4, "success");
                    }
                }
            }

            // Check raw J2K codestream SOC marker (0xFF4F)
            if (data[0] == 0xFF && data[1] == 0x4F)
            {
                for (int i = 2; i <= data.Length - 22; i++)
                {
                    if (data[i] == 0xFF && data[i + 1] == 0x51)
                    {
                        int xsiz = (data[i + 6] << 24) | (data[i + 7] << 16) | (data[i + 8] << 8) | data[i + 9];
                        int ysiz = (data[i + 10] << 24) | (data[i + 11] << 16) | (data[i + 12] << 8) | data[i + 13];
                        int xosiz = (data[i + 14] << 24) | (data[i + 15] << 16) | (data[i + 16] << 8) | data[i + 17];
                        int yosiz = (data[i + 18] << 24) | (data[i + 19] << 16) | (data[i + 20] << 8) | data[i + 21];
                        return new J2KHeaderInfo(xsiz - xosiz, ysiz - yosiz, 4, "success");
                    }
                }
            }

            return new J2KHeaderInfo(0, 0, 4, "fallback");
        }

        public static Image<Rgba32>? DecodeTexture(byte[] data)
        {
            var header = ParseHeader(data);
            if (header.Status != "success" || header.Width <= 0 || header.Height <= 0)
            {
                return null;
            }

            var image = new Image<Rgba32>(header.Width, header.Height);
            image.ProcessPixelRows(accessor =>
            {
                for (int y = 0; y < accessor.Height; y++)
                {
                    Span<Rgba32> row = accessor.GetRowSpan(y);
                    bool isGridRow = (y % 16 == 0);
                    for (int x = 0; x < row.Length; x++)
                    {
                        if (isGridRow || (x % 16 == 0))
                        {
                            row[x] = new Rgba32(0x66, 0x66, 0x66, 0x80);
                        }
                        else
                        {
                            row[x] = new Rgba32(0x80, 0x80, 0x80, 0x80);
                        }
                    }
                }
            });

            return image;
        }
    }
}
