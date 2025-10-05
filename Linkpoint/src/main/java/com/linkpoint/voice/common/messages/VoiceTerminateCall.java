/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.linkpoint.voice.common.messages;

import android.os.Bundle;
import com.linkpoint.voice.common.VoicePluginMessage;
import com.linkpoint.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nonnull;

public class VoiceTerminateCall
implements VoicePluginMessage {
    @Nonnull
    public final VoiceChannelInfo channelInfo;

    public VoiceTerminateCall(Bundle bundle) {
        this.channelInfo = new VoiceChannelInfo(bundle.getBundle("channelInfo"));
    }

    public VoiceTerminateCall(@Nonnull VoiceChannelInfo voiceChannelInfo) {
        this.channelInfo = voiceChannelInfo;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("channelInfo", this.channelInfo.toBundle());
        return bundle;
    }
}

