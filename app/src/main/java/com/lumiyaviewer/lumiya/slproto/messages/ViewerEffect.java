package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ViewerEffect extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Effect> Effect_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Effect {
        public UUID AgentID;
        public byte[] Color;
        public float Duration;
        public UUID ID;
        public int Type;
        public byte[] TypeData;
    }

    public ViewerEffect() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int i = 35;
        Iterator<T> it = this.Effect_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((Effect) it.next()).TypeData.length + 42 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerEffect(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 17);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte) this.Effect_Fields.size());
        for (Effect effect : this.Effect_Fields) {
            packUUID(byteBuffer, effect.ID);
            packUUID(byteBuffer, effect.AgentID);
            packByte(byteBuffer, (byte) effect.Type);
            packFloat(byteBuffer, effect.Duration);
            packFixed(byteBuffer, effect.Color, 4);
            packVariable(byteBuffer, effect.TypeData, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Effect effect = new Effect();
            effect.ID = unpackUUID(byteBuffer);
            effect.AgentID = unpackUUID(byteBuffer);
            effect.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            effect.Duration = unpackFloat(byteBuffer);
            effect.Color = unpackFixed(byteBuffer, 4);
            effect.TypeData = unpackVariable(byteBuffer, 1);
            this.Effect_Fields.add(effect);
        }
    }
}
