// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import javax.annotation.Nullable;
import java.util.UUID;

public class LogChatMessage implements Bundleable
{
    public final String chatterName;
    public final int chatterType;
    @Nullable
    public final UUID chatterUUID;
    public final long messageID;
    public final String messageText;
    
    public LogChatMessage(final int chatterType, @Nullable final UUID chatterUUID, final long messageID, final String chatterName, final String messageText) {
        this.chatterType = chatterType;
        this.chatterUUID = chatterUUID;
        this.messageID = messageID;
        this.chatterName = chatterName;
        this.messageText = messageText;
    }
    
    public LogChatMessage(final Bundle bundle) {
        this.chatterType = bundle.getInt("chatterType");
        UUID fromString;
        if (!bundle.containsKey("chatterUUID")) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(bundle.getString("chatterUUID"));
        }
        this.chatterUUID = fromString;
        this.messageID = bundle.getLong("messageID");
        this.chatterName = bundle.getString("chatterName");
        this.messageText = bundle.getString("messageText");
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
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
