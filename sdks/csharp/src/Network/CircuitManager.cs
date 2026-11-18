using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;

namespace Linkpoint.LLSD.Network
{
    public class UnackedPacket
    {
        public uint SequenceNumber { get; set; }
        public byte[] PacketData { get; set; } = Array.Empty<byte>();
        public DateTime SentTime { get; set; }
        public int Retries { get; set; }
    }

    /// <summary>
    /// Manages circuit sequence numbers, packet acknowledgments, and retransmissions.
    /// Thread-safe for background network loop operations.
    /// </summary>
    public class CircuitManager
    {
        private readonly object _lock = new object();
        private uint _nextOutSequenceNumber = 1;
        private readonly HashSet<uint> _receivedSequences = new HashSet<uint>();
        private readonly Queue<uint> _pendingAcks = new Queue<uint>();
        private readonly Dictionary<uint, UnackedPacket> _unackedPackets = new Dictionary<uint, UnackedPacket>();

        public uint CircuitCode { get; }
        public bool IsConnected { get; private set; } = true;
        public DateTime LastPacketReceivedTime { get; private set; } = DateTime.UtcNow;

        public CircuitManager(uint circuitCode)
        {
            CircuitCode = circuitCode;
        }

        public uint GetNextSequenceNumber()
        {
            lock (_lock)
            {
                return _nextOutSequenceNumber++;
            }
        }

        public bool ProcessInboundPacket(uint sequenceNumber, bool reliable)
        {
            lock (_lock)
            {
                LastPacketReceivedTime = DateTime.UtcNow;

                if (_receivedSequences.Contains(sequenceNumber))
                {
                    // Duplicate packet
                    if (reliable)
                    {
                        _pendingAcks.Enqueue(sequenceNumber);
                    }
                    return false;
                }

                _receivedSequences.Add(sequenceNumber);

                // Keep memory usage bounded for received sequence window
                if (_receivedSequences.Count > 2048)
                {
                    uint min = _receivedSequences.Min();
                    _receivedSequences.Remove(min);
                }

                if (reliable)
                {
                    _pendingAcks.Enqueue(sequenceNumber);
                }

                return true;
            }
        }

        public void TrackOutgoingPacket(uint sequenceNumber, byte[] packetData)
        {
            lock (_lock)
            {
                _unackedPackets[sequenceNumber] = new UnackedPacket
                {
                    SequenceNumber = sequenceNumber,
                    PacketData = packetData,
                    SentTime = DateTime.UtcNow,
                    Retries = 0
                };
            }
        }

        public void ProcessAcks(IEnumerable<uint> acks)
        {
            lock (_lock)
            {
                foreach (uint ack in acks)
                {
                    _unackedPackets.Remove(ack);
                }
            }
        }

        public List<uint> GetPendingAcks(int maxCount = 255)
        {
            lock (_lock)
            {
                var result = new List<uint>();
                while (_pendingAcks.Count > 0 && result.Count < maxCount)
                {
                    result.Add(_pendingAcks.Dequeue());
                }
                return result;
            }
        }

        public List<UnackedPacket> GetPacketsToResend(TimeSpan timeout, int maxRetries = 3)
        {
            lock (_lock)
            {
                var now = DateTime.UtcNow;
                var resendList = new List<UnackedPacket>();
                var timedOutKeys = new List<uint>();

                foreach (var kvp in _unackedPackets)
                {
                    var packet = kvp.Value;
                    if (now - packet.SentTime > timeout)
                    {
                        if (packet.Retries >= maxRetries)
                        {
                            timedOutKeys.Add(kvp.Key);
                        }
                        else
                        {
                            packet.Retries++;
                            packet.SentTime = now;
                            resendList.Add(packet);
                        }
                    }
                }

                foreach (uint key in timedOutKeys)
                {
                    _unackedPackets.Remove(key);
                }

                return resendList;
            }
        }
    }
}
