package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;

public class ConnectionFragment extends Fragment {
    public static final String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID";

    @Nullable
    public static UUID getActiveAgentID(@Nullable Intent intent) {
        String stringExtra;
        if (intent == null || (stringExtra = intent.getStringExtra("activeAgentUUID")) == null) {
            return null;
        }
        return UUIDPool.getUUID(stringExtra);
    }
}
