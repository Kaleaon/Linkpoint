package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import java.util.UUID;
import javax.annotation.Nullable;

public class LogChatMessage implements Bundleable {
    public final String chatterName;
    public final int chatterType;
    @Nullable
    public final UUID chatterUUID;
    public final long messageID;
    public final String messageText;

    public LogChatMessage(int i, @Nullable UUID uuid, long j, String str, String str2) {
        this.chatterType = i;
        this.chatterUUID = uuid;
        this.messageID = j;
        this.chatterName = str;
        this.messageText = str2;
    }

    public LogChatMessage(Bundle bundle) {
        this.chatterType = bundle.getInt("chatterType");
        this.chatterUUID = !bundle.containsKey("chatterUUID") ? null : UUID.fromString(bundle.getString("chatterUUID"));
        this.messageID = bundle.getLong("messageID");
        this.chatterName = bundle.getString("chatterName");
        this.messageText = bundle.getString("messageText");
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("chatterType", this.chatterType);
        if (this.chatterUUID != null) {
            bundle.putString("chatterUUID", this.chatterUUID.toString());
        }
        bundle.putLong("messageID", this.messageID);
        bundle.putString("chatterName", this.chatterName);
        bundle.putString("messageText", this.messageText);
        return bundle;
    }
}
