package lindenlab.llsd.viewer.secondlife.networking;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TransportCore {
    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, PendingFrame> pendingAcks = new ConcurrentHashMap<>();
    private final PriorityQueue<PendingFrame> retryQueue = new PriorityQueue<>(Comparator.comparing(p -> p.nextAttemptAt));
    private final ReliabilityPolicy reliabilityPolicy;

    public TransportCore(ReliabilityPolicy reliabilityPolicy) {
        this.reliabilityPolicy = reliabilityPolicy;
    }

    public NetworkModels.NetworkFrame buildFrame(NetworkModels.MessageClass messageClass, byte[] payload, Map<String, Object> metadata) {
        long seq = sequence.incrementAndGet();
        return new NetworkModels.NetworkFrame(UUID.randomUUID(), messageClass, seq, Instant.now(), payload, metadata);
    }

    public synchronized void registerSent(NetworkModels.NetworkFrame frame) {
        PendingFrame pending = new PendingFrame(frame, 0, Instant.now());
        pendingAcks.put(frame.sequence(), pending);
        retryQueue.offer(pending);
    }

    public synchronized void ack(long sequenceNumber) {
        PendingFrame pending = pendingAcks.remove(sequenceNumber);
        if (pending != null) retryQueue.remove(pending);
    }

    public synchronized NetworkModels.NetworkFrame pollRetryReady() {
        PendingFrame top = retryQueue.peek();
        if (top == null || Instant.now().isBefore(top.nextAttemptAt)) return null;
        retryQueue.poll();

        ReliabilityPolicy.RetryPlan plan = reliabilityPolicy.nextRetry(top.attempt + 1, top.frame.messageClass());
        if (plan.stopRetrying()) {
            pendingAcks.remove(top.frame.sequence());
            return null;
        }

        PendingFrame retried = new PendingFrame(top.frame, top.attempt + 1, Instant.now().plus(plan.delay()));
        pendingAcks.put(retried.frame.sequence(), retried);
        retryQueue.offer(retried);
        return retried.frame;
    }

    public int pendingCount() {
        return pendingAcks.size();
    }

    private static class PendingFrame {
        final NetworkModels.NetworkFrame frame;
        final int attempt;
        final Instant nextAttemptAt;

        private PendingFrame(NetworkModels.NetworkFrame frame, int attempt, Instant nextAttemptAt) {
            this.frame = frame;
            this.attempt = attempt;
            this.nextAttemptAt = nextAttemptAt;
        }
    }
}
