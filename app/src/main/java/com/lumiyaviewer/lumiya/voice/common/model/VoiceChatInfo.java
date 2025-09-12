package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceChatInfo {
    private static final VoiceChatInfo emptyChatState = interner.intern(new VoiceChatInfo(VoiceChatState.None, VoiceChatState.None, 0, (UUID) null, false, false));
    private static final Interner<VoiceChatInfo> interner = Interners.newWeakInterner();
    @Nullable
    public final UUID activeSpeakerID;
    public final boolean isConference;
    public final boolean localMicActive;
    public final int numActiveSpeakers;
    @Nonnull
    public final VoiceChatState previousState;
    @Nonnull
    public final VoiceChatState state;

    public enum VoiceChatState {
        None,
        Ringing,
        Connecting,
        Active
    }

    private VoiceChatInfo(Bundle bundle) {
        UUID uuid = null;
        this.state = VoiceChatState.valueOf(bundle.getString("state"));
        this.previousState = VoiceChatState.valueOf(bundle.getString("previousState"));
        this.numActiveSpeakers = bundle.getInt("numActiveSpeakers");
        String string = bundle.getString("activeSpeakerID");
        this.activeSpeakerID = string != null ? UUID.fromString(string) : uuid;
        this.isConference = bundle.getBoolean("isConference");
        this.localMicActive = bundle.getBoolean("localMicActive");
    }

    private VoiceChatInfo(@Nonnull VoiceChatState voiceChatState, @Nonnull VoiceChatState voiceChatState2, int i, @Nullable UUID uuid, boolean z, boolean z2) {
        this.state = voiceChatState;
        this.previousState = voiceChatState2;
        this.numActiveSpeakers = i;
        this.activeSpeakerID = uuid;
        this.isConference = z;
        this.localMicActive = z2;
    }

    @Nonnull
    public static VoiceChatInfo create(Bundle bundle) {
        return interner.intern(new VoiceChatInfo(bundle));
    }

    @Nonnull
    public static VoiceChatInfo create(@Nonnull VoiceChatState voiceChatState, @Nonnull VoiceChatState voiceChatState2, int i, @Nullable UUID uuid, boolean z, boolean z2) {
        return interner.intern(new VoiceChatInfo(voiceChatState, voiceChatState2, i, uuid, z, z2));
    }

    @Nonnull
    public static VoiceChatInfo empty() {
        return emptyChatState;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VoiceChatInfo voiceChatInfo = (VoiceChatInfo) obj;
        if (this.numActiveSpeakers == voiceChatInfo.numActiveSpeakers && this.isConference == voiceChatInfo.isConference && this.localMicActive == voiceChatInfo.localMicActive && this.state == voiceChatInfo.state && this.previousState == voiceChatInfo.previousState) {
            return this.activeSpeakerID == null ? voiceChatInfo.activeSpeakerID == null : this.activeSpeakerID.equals(voiceChatInfo.activeSpeakerID);
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((!this.isConference ? 0 : 1) + (((this.activeSpeakerID == null ? 0 : this.activeSpeakerID.hashCode()) + (((((this.state.hashCode() * 31) + this.previousState.hashCode()) * 31) + this.numActiveSpeakers) * 31)) * 31)) * 31;
        if (this.localMicActive) {
            i = 1;
        }
        return hashCode + i;
    }

    public Bundle toBundle() {
        String str = null;
        Bundle bundle = new Bundle();
        bundle.putString("state", this.state.toString());
        bundle.putString("previousState", this.previousState.toString());
        bundle.putInt("numActiveSpeakers", this.numActiveSpeakers);
        if (this.activeSpeakerID != null) {
            str = this.activeSpeakerID.toString();
        }
        bundle.putString("activeSpeakerID", str);
        bundle.putBoolean("isConference", this.isConference);
        bundle.putBoolean("localMicActive", this.localMicActive);
        return bundle;
    }

    public String toString() {
        return "VoiceChatInfo{state=" + this.state + ", previousState=" + this.previousState + ", numActiveSpeakers=" + this.numActiveSpeakers + ", activeSpeakerID=" + this.activeSpeakerID + ", isConference=" + this.isConference + ", localMicActive=" + this.localMicActive + '}';
    }
}
