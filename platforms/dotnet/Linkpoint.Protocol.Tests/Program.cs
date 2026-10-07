using System;
using Linkpoint.Protocol;

namespace Linkpoint.Protocol.Tests
{
    class Program
    {
        static int Main(string[] args)
        {
            Console.WriteLine("=== Running C# Linkpoint Protocol Tests ===");

            // Test 1: Catalog metadata
            var version = GeneratedProtocolCatalog.TemplateVersion;
            if (version != "2.0")
            {
                Console.Error.WriteLine("FAILED: TemplateVersion mismatch");
                return 1;
            }
            if (GeneratedProtocolCatalog.RegisteredMessages.Count == 0)
            {
                Console.Error.WriteLine("FAILED: RegisteredMessages empty");
                return 1;
            }
            if (!GeneratedProtocolCatalog.RegisteredMessages.ContainsKey("StartPingCheck"))
            {
                Console.Error.WriteLine("FAILED: RegisteredMessages missing StartPingCheck");
                return 1;
            }
            Console.WriteLine("PASSED: Catalog metadata");

            // Test 2: Zerocoded decompression
            byte[] compressed = new byte[] { 0x01, 0x00, 0x03, 0x02 };
            byte[] decompressed = GeneratedProtocolCatalog.DecompressZerocoded(compressed);
            if (decompressed.Length != 5 || decompressed[1] != 0 || decompressed[2] != 0 || decompressed[3] != 0 || decompressed[4] != 2)
            {
                Console.Error.WriteLine("FAILED: DecompressZerocoded invalid output");
                return 1;
            }
            Console.WriteLine("PASSED: Zerocoded decompression");

            // Test 3: Packet serialization
            var packet = new StartPingCheckPacket();
            if (packet.Name != "StartPingCheck" || packet.MessageNumber != 1u)
            {
                Console.Error.WriteLine("FAILED: StartPingCheckPacket metadata");
                return 1;
            }
            byte[] bytes = packet.Serialize();
            if (bytes.Length != 4)
            {
                Console.Error.WriteLine("FAILED: StartPingCheckPacket serialized length");
                return 1;
            }
            Console.WriteLine("PASSED: Packet serialization");

            Console.WriteLine("All C# unit tests passed successfully!");
            return 0;
        }
    }
}
