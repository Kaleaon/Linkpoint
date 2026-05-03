package lindenlab.llsd.viewer.secondlife.networking;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDException;
import lindenlab.llsd.LLSDJsonParser;
import lindenlab.llsd.LLSDJsonSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class LLSDMessageCodec {
    private final LLSDJsonParser parser = new LLSDJsonParser();
    private final LLSDJsonSerializer serializer = new LLSDJsonSerializer();

    public byte[] encode(Map<String, Object> payload) throws LLSDException {
        Objects.requireNonNull(payload, "payload");
        try (StringWriter writer = new StringWriter()) {
            serializer.serialize(new LLSD(payload), writer);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new LLSDException("Failed to encode LLSD payload", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> decode(byte[] payload) throws LLSDException {
        if (payload == null || payload.length == 0) {
            return Map.of();
        }
        try {
            LLSD llsd = parser.parse(new ByteArrayInputStream(payload));
            Object content = llsd.getContent();
            if (!(content instanceof Map<?, ?> map)) {
                throw new LLSDException("Expected LLSD map payload");
            }
            return (Map<String, Object>) map;
        } catch (IOException e) {
            throw new LLSDException("Failed to decode LLSD payload", e);
        }
    }
}
