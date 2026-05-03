package lindenlab.llsd.viewer.secondlife.networking;

import java.time.Duration;

public interface ReliabilityPolicy {
    RetryPlan nextRetry(int attempt, NetworkModels.MessageClass messageClass);
    boolean shouldFailover(LinkSnapshot snapshot);

    record RetryPlan(Duration delay, boolean stopRetrying) {}
    record LinkSnapshot(double packetLossPercent, long rttMs, int consecutiveTimeouts) {}

    class DefaultReliabilityPolicy implements ReliabilityPolicy {
        @Override
        public RetryPlan nextRetry(int attempt, NetworkModels.MessageClass messageClass) {
            int clampedAttempt = Math.max(0, attempt);
            long base = switch (messageClass) {
                case CRITICAL_ORDERED -> 300L;
                case IMPORTANT_ORDERED -> 500L;
                case REALTIME_LOSS_TOLERANT -> 900L;
            };
            long maxAttempts = switch (messageClass) {
                case CRITICAL_ORDERED -> 8;
                case IMPORTANT_ORDERED -> 5;
                case REALTIME_LOSS_TOLERANT -> 2;
            };
            boolean stop = clampedAttempt >= maxAttempts;
            long exp = (long) Math.min(6, clampedAttempt);
            long backoff = base * (1L << exp);
            long jitter = (long) (Math.random() * (base / 3.0));
            return new RetryPlan(Duration.ofMillis(backoff + jitter), stop);
        }

        @Override
        public boolean shouldFailover(LinkSnapshot snapshot) {
            return snapshot.packetLossPercent() >= 10.0 || snapshot.rttMs() >= 1200 || snapshot.consecutiveTimeouts() >= 4;
        }
    }
}
