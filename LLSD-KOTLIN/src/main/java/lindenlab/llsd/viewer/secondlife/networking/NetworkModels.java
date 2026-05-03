package lindenlab.llsd.viewer.secondlife.networking;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class NetworkModels {
    private NetworkModels() {}

    public enum MessageClass { CRITICAL_ORDERED, IMPORTANT_ORDERED, REALTIME_LOSS_TOLERANT }
    public enum SessionState { OFFLINE, BOOTSTRAPPING, CONNECTING_PRIMARY, AUTHENTICATING, ACTIVE_STABLE, ACTIVE_DEGRADED, RECOVERING, BACKGROUND_IDLE, SUSPENDED, TERMINATED }

    public record Endpoint(String host, int port, URI capsBaseUri) {}

    public record NetworkFrame(
            UUID frameId,
            MessageClass messageClass,
            long sequence,
            Instant createdAt,
            byte[] payload,
            Map<String, Object> metadata) {
        public NetworkFrame {
            Objects.requireNonNull(frameId);
            Objects.requireNonNull(messageClass);
            Objects.requireNonNull(createdAt);
            payload = payload == null ? new byte[0] : Arrays.copyOf(payload, payload.length);
            metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        }
    }
}
