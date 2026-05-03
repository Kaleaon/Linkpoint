package lindenlab.llsd.viewer.secondlife.networking;

import lindenlab.llsd.LLSDException;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkOrchestrator {
    private final AtomicReference<NetworkModels.SessionState> state = new AtomicReference<>(NetworkModels.SessionState.OFFLINE);
    private final TransportCore transportCore;
    private final LLSDMessageCodec codec;
    private final ConcurrentLinkedQueue<NetworkModels.NetworkFrame> outbound = new ConcurrentLinkedQueue<>();
    private volatile Instant lastHealthUpdate = Instant.now();

    public NetworkOrchestrator() {
        this.codec = new LLSDMessageCodec();
        this.transportCore = new TransportCore(new ReliabilityPolicy.DefaultReliabilityPolicy());
    }

    public NetworkModels.SessionState getState() {
        return state.get();
    }

    public void bootstrap() {
        state.set(NetworkModels.SessionState.BOOTSTRAPPING);
    }

    public void onAuthenticated() {
        state.set(NetworkModels.SessionState.ACTIVE_STABLE);
    }

    public void onBackgrounded() {
        state.set(NetworkModels.SessionState.BACKGROUND_IDLE);
    }

    public void onSuspended() {
        state.set(NetworkModels.SessionState.SUSPENDED);
    }

    public void onResumed() {
        state.set(NetworkModels.SessionState.RECOVERING);
    }

    public NetworkModels.NetworkFrame enqueueLlsdMessage(NetworkModels.MessageClass messageClass, Map<String, Object> llsdPayload) throws LLSDException {
        byte[] payload = codec.encode(llsdPayload);
        NetworkModels.NetworkFrame frame = transportCore.buildFrame(messageClass, payload, Map.of("contentType", "application/llsd+json"));
        outbound.offer(frame);
        return frame;
    }

    public NetworkModels.NetworkFrame nextOutbound() {
        NetworkModels.NetworkFrame frame = outbound.poll();
        if (frame != null) {
            transportCore.registerSent(frame);
        }
        return frame;
    }

    public void onAck(long sequenceNumber) {
        transportCore.ack(sequenceNumber);
    }

    public NetworkModels.NetworkFrame retryIfReady() {
        return transportCore.pollRetryReady();
    }

    public void updateLinkHealth(double lossPercent, long rttMs, int consecutiveTimeouts) {
        lastHealthUpdate = Instant.now();
        ReliabilityPolicy.LinkSnapshot snapshot = new ReliabilityPolicy.LinkSnapshot(lossPercent, rttMs, consecutiveTimeouts);
        if (new ReliabilityPolicy.DefaultReliabilityPolicy().shouldFailover(snapshot)) {
            state.set(NetworkModels.SessionState.ACTIVE_DEGRADED);
        } else if (state.get() == NetworkModels.SessionState.ACTIVE_DEGRADED) {
            state.set(NetworkModels.SessionState.ACTIVE_STABLE);
        }
    }

    public int getPendingAckCount() {
        return transportCore.pendingCount();
    }

    public Instant getLastHealthUpdate() {
        return lastHealthUpdate;
    }
}
