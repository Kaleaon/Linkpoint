// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import com.google.common.collect.Interners;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import com.google.common.collect.Interner;

public class VoiceChatInfo
{
    private static final VoiceChatInfo emptyChatState;
    private static final Interner<VoiceChatInfo> interner;
    @Nullable
    public final UUID activeSpeakerID;
    public final boolean isConference;
    public final boolean localMicActive;
    public final int numActiveSpeakers;
    @Nonnull
    public final VoiceChatState previousState;
    @Nonnull
    public final VoiceChatState state;
    
    static {
        interner = Interners.newWeakInterner();
        emptyChatState = VoiceChatInfo.interner.intern(new VoiceChatInfo(VoiceChatState.None, VoiceChatState.None, 0, null, false, false));
    }
    
    private VoiceChatInfo(final Bundle bundle) {
        UUID fromString = null;
        this.state = VoiceChatState.valueOf(bundle.getString("state"));
        this.previousState = VoiceChatState.valueOf(bundle.getString("previousState"));
        this.numActiveSpeakers = bundle.getInt("numActiveSpeakers");
        final String string = bundle.getString("activeSpeakerID");
        if (string != null) {
            fromString = UUID.fromString(string);
        }
        this.activeSpeakerID = fromString;
        this.isConference = bundle.getBoolean("isConference");
        this.localMicActive = bundle.getBoolean("localMicActive");
    }
    
    private VoiceChatInfo(@Nonnull final VoiceChatState state, @Nonnull final VoiceChatState previousState, final int numActiveSpeakers, @Nullable final UUID activeSpeakerID, final boolean isConference, final boolean localMicActive) {
        this.state = state;
        this.previousState = previousState;
        this.numActiveSpeakers = numActiveSpeakers;
        this.activeSpeakerID = activeSpeakerID;
        this.isConference = isConference;
        this.localMicActive = localMicActive;
    }
    
    @Nonnull
    public static VoiceChatInfo create(final Bundle bundle) {
        return VoiceChatInfo.interner.intern(new VoiceChatInfo(bundle));
    }
    
    @Nonnull
    public static VoiceChatInfo create(@Nonnull final VoiceChatState voiceChatState, @Nonnull final VoiceChatState voiceChatState2, final int n, @Nullable final UUID uuid, final boolean b, final boolean b2) {
        return VoiceChatInfo.interner.intern(new VoiceChatInfo(voiceChatState, voiceChatState2, n, uuid, b, b2));
    }
    
    @Nonnull
    public static VoiceChatInfo empty() {
        return VoiceChatInfo.emptyChatState;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final VoiceChatInfo voiceChatInfo = (VoiceChatInfo)o;
        if (this.numActiveSpeakers != voiceChatInfo.numActiveSpeakers) {
            return false;
        }
        if (this.isConference != voiceChatInfo.isConference) {
            return false;
        }
        if (this.localMicActive != voiceChatInfo.localMicActive) {
            return false;
        }
        if (this.state != voiceChatInfo.state) {
            return false;
        }
        if (this.previousState == voiceChatInfo.previousState) {
            if (this.activeSpeakerID == null) {
                if (voiceChatInfo.activeSpeakerID == null) {
                    equals = true;
                }
            }
            else {
                equals = this.activeSpeakerID.equals(voiceChatInfo.activeSpeakerID);
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        final int hashCode = this.state.hashCode();
        final int hashCode2 = this.previousState.hashCode();
        final int numActiveSpeakers = this.numActiveSpeakers;
        int hashCode3;
        if (this.activeSpeakerID == null) {
            hashCode3 = 0;
        }
        else {
            hashCode3 = this.activeSpeakerID.hashCode();
        }
        int n2;
        if (!this.isConference) {
            n2 = 0;
        }
        else {
            n2 = 1;
        }
        if (this.localMicActive) {
            n = 1;
        }
        return (n2 + (hashCode3 + ((hashCode * 31 + hashCode2) * 31 + numActiveSpeakers) * 31) * 31) * 31 + n;
    }
    
    public Bundle toBundle() {
        String string = null;
        final Bundle bundle = new Bundle();
        bundle.putString("state", this.state.toString());
        bundle.putString("previousState", this.previousState.toString());
        bundle.putInt("numActiveSpeakers", this.numActiveSpeakers);
        if (this.activeSpeakerID != null) {
            string = this.activeSpeakerID.toString();
        }
        bundle.putString("activeSpeakerID", string);
        bundle.putBoolean("isConference", this.isConference);
        bundle.putBoolean("localMicActive", this.localMicActive);
        return bundle;
    }
    
    @Override
    public String toString() {
        return "VoiceChatInfo{state=" + this.state + ", previousState=" + this.previousState + ", numActiveSpeakers=" + this.numActiveSpeakers + ", activeSpeakerID=" + this.activeSpeakerID + ", isConference=" + this.isConference + ", localMicActive=" + this.localMicActive + '}';
    }
    
    public enum VoiceChatState
    {
        Active, 
        Connecting, 
        None, 
        Ringing;
    }
}
