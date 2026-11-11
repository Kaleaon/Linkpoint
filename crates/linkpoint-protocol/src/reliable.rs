use std::collections::{BTreeMap, BTreeSet, HashSet, VecDeque};
use std::time::{Duration, Instant};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ReliablePacket {
    pub sequence: u32,
    pub payload: Vec<u8>,
    pub attempts: u8,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum AckResult {
    Acknowledged,
    Unknown,
}

#[derive(Debug)]
struct Pending {
    packet: ReliablePacket,
    sent_at: Instant,
}

/// Transport-independent reliable-UDP sequencing, duplicate suppression and
/// exponential retransmission scheduling.
#[derive(Debug)]
pub struct ReliableUdp {
    next: u32,
    pending: BTreeMap<u32, Pending>,
    received_set: HashSet<u32>,
    received_order: VecDeque<u32>,
    pending_acks: BTreeSet<u32>,
    base_timeout: Duration,
    max_attempts: u8,
}

impl Default for ReliableUdp {
    fn default() -> Self {
        Self::new(Duration::from_millis(500), 5)
    }
}

impl ReliableUdp {
    pub fn new(base_timeout: Duration, max_attempts: u8) -> Self {
        Self {
            next: 0,
            pending: BTreeMap::new(),
            received_set: HashSet::new(),
            received_order: VecDeque::new(),
            pending_acks: BTreeSet::new(),
            base_timeout,
            max_attempts,
        }
    }

    pub fn queue(&mut self, payload: Vec<u8>, now: Instant) -> ReliablePacket {
        let packet = ReliablePacket {
            sequence: self.next,
            payload,
            attempts: 1,
        };
        self.next = self.next.wrapping_add(1);
        self.pending.insert(
            packet.sequence,
            Pending {
                packet: packet.clone(),
                sent_at: now,
            },
        );
        packet
    }

    pub fn acknowledge(&mut self, sequence: u32) -> AckResult {
        if self.pending.remove(&sequence).is_some() {
            AckResult::Acknowledged
        } else {
            AckResult::Unknown
        }
    }

    /// Returns `true` once for a newly received reliable sequence and `false`
    /// for retransmitted duplicates. A bounded window avoids unbounded growth.
    pub fn accept_incoming(&mut self, sequence: u32) -> bool {
        let fresh = self.received_set.insert(sequence);
        if fresh {
            self.received_order.push_back(sequence);
            self.pending_acks.insert(sequence);
            while self.received_order.len() > 4096 {
                if let Some(oldest) = self.received_order.pop_front() {
                    self.received_set.remove(&oldest);
                }
            }
        }
        fresh
    }

    /// Drains up to `limit` ACKs for packet-level appended ACKs or a
    /// PacketAck message. A sorted set makes batches deterministic in tests.
    pub fn take_ack_batch(&mut self, limit: usize) -> Vec<u32> {
        let batch: Vec<_> = self.pending_acks.iter().take(limit).copied().collect();
        for sequence in &batch {
            self.pending_acks.remove(sequence);
        }
        batch
    }

    pub fn retransmit_due(&mut self, now: Instant) -> Vec<ReliablePacket> {
        let mut result = Vec::new();
        for pending in self.pending.values_mut() {
            let multiplier = 1u32 << u32::from(pending.packet.attempts.saturating_sub(1).min(8));
            if now.duration_since(pending.sent_at) >= self.base_timeout.saturating_mul(multiplier)
                && pending.packet.attempts < self.max_attempts
            {
                pending.packet.attempts += 1;
                pending.sent_at = now;
                result.push(pending.packet.clone());
            }
        }
        result
    }

    pub fn expired(&self) -> impl Iterator<Item = u32> + '_ {
        self.pending
            .values()
            .filter(|p| p.packet.attempts >= self.max_attempts)
            .map(|p| p.packet.sequence)
    }

    pub fn pending_count(&self) -> usize {
        self.pending.len()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn sequence_wraps_and_ack_removes_pending_packet() {
        let now = Instant::now();
        let mut udp = ReliableUdp {
            next: u32::MAX,
            ..ReliableUdp::default()
        };
        assert_eq!(udp.queue(vec![1], now).sequence, u32::MAX);
        assert_eq!(udp.queue(vec![2], now).sequence, 0);
        assert_eq!(udp.acknowledge(u32::MAX), AckResult::Acknowledged);
        assert_eq!(udp.pending_count(), 1);
    }

    #[test]
    fn retries_with_backoff_and_suppresses_duplicates() {
        let now = Instant::now();
        let mut udp = ReliableUdp::new(Duration::from_millis(100), 3);
        udp.queue(vec![7], now);
        assert!(
            udp.retransmit_due(now + Duration::from_millis(99))
                .is_empty()
        );
        assert_eq!(
            udp.retransmit_due(now + Duration::from_millis(100))[0].attempts,
            2
        );
        assert!(udp.accept_incoming(8));
        assert!(!udp.accept_incoming(8));
        assert_eq!(udp.take_ack_batch(10), vec![8]);
        assert!(udp.take_ack_batch(10).is_empty());
    }

    #[test]
    fn sequence_rollover_preserves_zero_and_rejects_duplicates() {
        let mut udp = ReliableUdp::default();
        let start_seq = u32::MAX - 100;
        for seq in start_seq..=u32::MAX {
            assert!(udp.accept_incoming(seq));
        }
        // Rollover across u32::MAX to 0 and 1
        assert!(udp.accept_incoming(0));
        assert!(udp.accept_incoming(1));

        // Duplicate sequence 0 must be rejected as duplicate
        assert!(!udp.accept_incoming(0));
        assert!(!udp.accept_incoming(u32::MAX));
    }

    #[test]
    fn fifo_eviction_evicts_oldest_arrival_not_smallest_value() {
        let mut udp = ReliableUdp::default();
        let start_seq = u32::MAX - 100;
        // Insert 4096 sequences starting near u32::MAX wrapping around 0
        for i in 0..4096 {
            let seq = start_seq.wrapping_add(i as u32);
            assert!(udp.accept_incoming(seq));
        }
        // Sequence 0 was inserted earlier and is still in window -> rejected as duplicate
        assert!(!udp.accept_incoming(0));

        // Insert 100 more sequence numbers to push start_seq..=(start_seq + 99) out of the window
        for i in 4096..(4096 + 100) {
            let seq = start_seq.wrapping_add(i as u32);
            assert!(udp.accept_incoming(seq));
        }

        // The oldest sequence (start_seq) was evicted, so accepting it now succeeds
        assert!(udp.accept_incoming(start_seq));
        // Sequence 0 arrived later than start_seq so it remains in window and is rejected as duplicate
        assert!(!udp.accept_incoming(0));
    }
}
