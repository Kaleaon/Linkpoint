package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import com.google.common.base.Strings;
import java.util.UUID;
import javax.annotation.Nullable;

public class LogFlushMessages implements Bundleable {
    @Nullable
    public final String agentName;
    public final UUID agentUUID;
    @Nullable
    public final String chatterName;

    public LogFlushMessages(Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.agentName = Strings.nullToEmpty(bundle.getString("agentName"));
        this.chatterName = bundle.getString("chatterName");
    }

    public LogFlushMessages(UUID uuid, @Nullable String str, @Nullable String str2) {
        this.agentUUID = uuid;
        this.agentName = str;
        this.chatterName = str2;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putString("agentName", this.agentName);
        bundle.putString("chatterName", this.chatterName);
        return bundle;
    }
}
