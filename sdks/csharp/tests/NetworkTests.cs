using System;
using System.Buffers.Binary;
using System.Collections.Generic;
using Linkpoint.LLSD.Network;

namespace Linkpoint.LLSD.Tests
{
    public class NetworkTests
    {
        [Fact]
        public void TestPacketHeaderDecoding()
        {
            // Build a sample SL UDP packet header:
            // Flags: Reliable | ZeroCoded (0x40 | 0x80 = 0xC0)
            // Sequence: 1005 (0x00, 0x00, 0x03, 0xED)
            // ExtraBytes: 0
            // Message ID: High frequency 0x05
            byte[] packet = new byte[] { 0xC0, 0x00, 0x00, 0x03, 0xED, 0x00, 0x05, 0xAA, 0xBB, 0xCC };

            PacketHeader header = PacketCodec.DecodeHeader(packet, out int headerLength);

            Assert.True(header.IsReliable);
            Assert.True(header.IsZeroCoded);
            Assert.False(header.IsResent);
            Assert.False(header.HasAcks);
            Assert.Equal(1005u, header.SequenceNumber);
            Assert.Equal(0u, header.ExtraBytes);
            Assert.Equal(0x05u, header.MessageId);
            Assert.Equal(7, headerLength);
        }

        [Fact]
        public void TestZerocodeExpansionAndCompression()
        {
            byte[] rawBody = new byte[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x02 };

            byte[] zeroEncoded = PacketCodec.ZeroEncode(rawBody);

            // Raw zerocoded bytes should compress sequence of 0x00
            // Header length = 0 for body only
            byte[] zeroDecoded = PacketCodec.ZeroDecode(zeroEncoded, headerLen: 0);

            Assert.Equal(rawBody, zeroDecoded);
        }

        [Fact]
        public void TestCircuitManagerSequenceAndAckTracking()
        {
            var circuit = new CircuitManager(circuitCode: 123456);

            Assert.Equal(1u, circuit.GetNextSequenceNumber());
            Assert.Equal(2u, circuit.GetNextSequenceNumber());

            // Process inbound reliable packet 50
            bool isNew = circuit.ProcessInboundPacket(sequenceNumber: 50, reliable: true);
            Assert.True(isNew);

            // Process duplicate inbound reliable packet 50
            bool isDuplicateNew = circuit.ProcessInboundPacket(sequenceNumber: 50, reliable: true);
            Assert.False(isDuplicateNew);

            List<uint> pendingAcks = circuit.GetPendingAcks();
            Assert.Contains(50u, pendingAcks);

            // Track outgoing packet 10
            circuit.TrackOutgoingPacket(10, new byte[] { 0x01, 0x02 });

            // Process ACK for 10
            circuit.ProcessAcks(new uint[] { 10 });

            // Ensure no retransmission needed for ACKed packet
            var resends = circuit.GetPacketsToResend(TimeSpan.FromMilliseconds(10));
            Assert.Empty(resends);
        }
    }
}
