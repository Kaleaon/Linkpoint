/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceChatInfo {
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
        emptyChatState = interner.intern(new VoiceChatInfo(VoiceChatState.None, VoiceChatState.None, 0, null, false, false));
    }

    /*
     * Enabled aggressive block sorting
     */
    private VoiceChatInfo(Bundle bundle) {
        this.state = VoiceChatState.valueOf(bundle.getString("state"));
        this.previousState = VoiceChatState.valueOf(bundle.getString("previousState"));
        this.numActiveSpeakers = bundle.getInt("numActiveSpeakers");
        Object object = bundle.getString("activeSpeakerID");
        object = object != null ? UUID.fromString((String)object) : null;
        this.activeSpeakerID = object;
        this.isConference = bundle.getBoolean("isConference");
        this.localMicActive = bundle.getBoolean("localMicActive");
    }

    private VoiceChatInfo(@Nonnull VoiceChatState voiceChatState, @Nonnull VoiceChatState voiceChatState2, int n, @Nullable UUID uUID, boolean bl, boolean bl2) {
        this.state = voiceChatState;
        this.previousState = voiceChatState2;
        this.numActiveSpeakers = n;
        this.activeSpeakerID = uUID;
        this.isConference = bl;
        this.localMicActive = bl2;
    }

    @Nonnull
    public static VoiceChatInfo create(Bundle bundle) {
        return interner.intern(new VoiceChatInfo(bundle));
    }

    @Nonnull
    public static VoiceChatInfo create(@Nonnull VoiceChatState voiceChatState, @Nonnull VoiceChatState voiceChatState2, int n, @Nullable UUID uUID, boolean bl, boolean bl2) {
        return interner.intern(new VoiceChatInfo(voiceChatState, voiceChatState2, n, uUID, bl, bl2));
    }

    @Nonnull
    public static VoiceChatInfo empty() {
        return emptyChatState;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        boolean bl = true;
        boolean bl2 = false;
        if (this == object) {
            return true;
        }
        boolean bl3 = bl2;
        if (object == null) return bl3;
        bl3 = bl2;
        if (this.getClass() != object.getClass()) return bl3;
        object = (VoiceChatInfo)object;
        bl3 = bl2;
        if (this.numActiveSpeakers != ((VoiceChatInfo)object).numActiveSpeakers) return bl3;
        bl3 = bl2;
        if (this.isConference != ((VoiceChatInfo)object).isConference) return bl3;
        bl3 = bl2;
        if (this.localMicActive != ((VoiceChatInfo)object).localMicActive) return bl3;
        bl3 = bl2;
        if (this.state != ((VoiceChatInfo)object).state) return bl3;
        bl3 = bl2;
        if (this.previousState != ((VoiceChatInfo)object).previousState) return bl3;
        if (this.activeSpeakerID != null) {
            return this.activeSpeakerID.equals(((VoiceChatInfo)object).activeSpeakerID);
        }
        bl3 = bl;
        if (((VoiceChatInfo)object).activeSpeakerID == null) return bl3;
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    public int hashCode() {
        int n = 1;
        int n2 = this.state.hashCode();
        int n3 = this.previousState.hashCode();
        int n4 = this.numActiveSpeakers;
        int n5 = this.activeSpeakerID != null ? this.activeSpeakerID.hashCode() : 0;
        int n6 = this.isConference ? 1 : 0;
        if (this.localMicActive) {
            return ((((n2 * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n6) * 31 + n;
        }
        n = 0;
        return ((((n2 * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n6) * 31 + n;
    }

    /*
     * Enabled aggressive block sorting
     */
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("state", this.state.toString());
        bundle.putString("previousState", this.previousState.toString());
        bundle.putInt("numActiveSpeakers", this.numActiveSpeakers);
        String string2 = this.activeSpeakerID != null ? this.activeSpeakerID.toString() : null;
        bundle.putString("activeSpeakerID", string2);
        bundle.putBoolean("isConference", this.isConference);
        bundle.putBoolean("localMicActive", this.localMicActive);
        return bundle;
    }

    public String toString() {
        return "VoiceChatInfo{state=" + (Object)((Object)this.state) + ", previousState=" + (Object)((Object)this.previousState) + ", numActiveSpeakers=" + this.numActiveSpeakers + ", activeSpeakerID=" + this.activeSpeakerID + ", isConference=" + this.isConference + ", localMicActive=" + this.localMicActive + '}';
    }

    public static enum VoiceChatState {
        None,
        Ringing,
        Connecting,
        Active;

    }
}

