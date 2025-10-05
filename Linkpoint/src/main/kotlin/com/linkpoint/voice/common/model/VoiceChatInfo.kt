package com.linkpoint.voice.common.model

import android.os.Bundle
import com.google.common.collect.Interner
import com.google.common.collect.Interners
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceChatInfo {
    private const val VoiceChatInfo emptyChatState
    private const val Interner<VoiceChatInfo> interner
    val UUID activeSpeakerID
    val Boolean isConference
    val Boolean localMicActive
    val Int numActiveSpeakers
    val VoiceChatState previousState
    val VoiceChatState state

    static {
        interner = Interners.newWeakInterner()
        emptyChatState = interner.intern(VoiceChatInfo(VoiceChatState.None, VoiceChatState.None, 0, null, false, false))
    }

    /*
     * Enabled aggressive block sorting
     */
    private VoiceChatInfo(Bundle bundle) {
        this.state = VoiceChatState.valueOf(bundle.getString("state"))
        this.previousState = VoiceChatState.valueOf(bundle.getString("previousState"))
        this.numActiveSpeakers = bundle.getInt("numActiveSpeakers")
        Object object = bundle.getString("activeSpeakerID")
        object = object != null ? UUID.fromString((String)object) : null
        this.activeSpeakerID = object
        this.isConference = bundle.getBoolean("isConference")
        this.localMicActive = bundle.getBoolean("localMicActive")
    }

    private VoiceChatInfo(VoiceChatState voiceChatState, VoiceChatState voiceChatState2, Int n, UUID uUID, Boolean bl, Boolean bl2) {
        this.state = voiceChatState
        this.previousState = voiceChatState2
        this.numActiveSpeakers = n
        this.activeSpeakerID = uUID
        this.isConference = bl
        this.localMicActive = bl2
    }

    @JvmStatic
    VoiceChatInfo create(Bundle bundle) {
        return interner.intern(VoiceChatInfo(bundle))
    }

    @JvmStatic
    VoiceChatInfo create(VoiceChatState voiceChatState, VoiceChatState voiceChatState2, Int n, UUID uUID, Boolean bl, Boolean bl2) {
        return interner.intern(VoiceChatInfo(voiceChatState, voiceChatState2, n, uUID, bl, bl2))
    }

    @JvmStatic
    VoiceChatInfo empty() {
        return emptyChatState
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Boolean equals(Object object) {
        Boolean bl = true
        Boolean bl2 = false
        if (this == object) {
            return true
        }
        Boolean bl3 = bl2
        if (object == null) return bl3
        bl3 = bl2
        if (this.getClass() != object.getClass()) return bl3
        object = (VoiceChatInfo)object
        bl3 = bl2
        if (this.numActiveSpeakers != ((VoiceChatInfo)object).numActiveSpeakers) return bl3
        bl3 = bl2
        if (this.isConference != ((VoiceChatInfo)object).isConference) return bl3
        bl3 = bl2
        if (this.localMicActive != ((VoiceChatInfo)object).localMicActive) return bl3
        bl3 = bl2
        if (this.state != ((VoiceChatInfo)object).state) return bl3
        bl3 = bl2
        if (this.previousState != ((VoiceChatInfo)object).previousState) return bl3
        if (this.activeSpeakerID != null) {
            return this.activeSpeakerID.equals(((VoiceChatInfo)object).activeSpeakerID)
        }
        bl3 = bl
        if (((VoiceChatInfo)object).activeSpeakerID == null) return bl3
        return false
    }

    /*
     * Enabled aggressive block sorting
     */
    public Int hashCode() {
        Int n = 1
        Int n2 = this.state.hashCode()
        Int n3 = this.previousState.hashCode()
        Int n4 = this.numActiveSpeakers
        Int n5 = this.activeSpeakerID != null ? this.activeSpeakerID.hashCode() : 0
        Int n6 = this.isConference ? 1 : 0
        if (this.localMicActive) {
            return ((((n2 * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n6) * 31 + n
        }
        n = 0
        return ((((n2 * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n6) * 31 + n
    }

    /*
     * Enabled aggressive block sorting
     */
    public Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putString("state", this.state.toString())
        bundle.putString("previousState", this.previousState.toString())
        bundle.putInt("numActiveSpeakers", this.numActiveSpeakers)
        String string2 = this.activeSpeakerID != null ? this.activeSpeakerID.toString() : null
        bundle.putString("activeSpeakerID", string2)
        bundle.putBoolean("isConference", this.isConference)
        bundle.putBoolean("localMicActive", this.localMicActive)
        return bundle
    }

    public String toString() {
        return "VoiceChatInfo{state=" + (Object)((Object)this.state) + ", previousState=" + (Object)((Object)this.previousState) + ", numActiveSpeakers=" + this.numActiveSpeakers + ", activeSpeakerID=" + this.activeSpeakerID + ", isConference=" + this.isConference + ", localMicActive=" + this.localMicActive + '}'
    }

    @JvmStatic
    enum VoiceChatState {
        None,
        Ringing,
        Connecting,
        Active

    }
}

