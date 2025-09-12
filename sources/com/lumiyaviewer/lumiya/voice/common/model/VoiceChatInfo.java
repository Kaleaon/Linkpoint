// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import java.util.UUID;

public class VoiceChatInfo
{
    public static final class VoiceChatState extends Enum
    {

        private static final VoiceChatState $VALUES[];
        public static final VoiceChatState Active;
        public static final VoiceChatState Connecting;
        public static final VoiceChatState None;
        public static final VoiceChatState Ringing;

        public static VoiceChatState valueOf(String s)
        {
            return (VoiceChatState)Enum.valueOf(com/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState, s);
        }

        public static VoiceChatState[] values()
        {
            return (VoiceChatState[])$VALUES.clone();
        }

        static 
        {
            None = new VoiceChatState("None", 0);
            Ringing = new VoiceChatState("Ringing", 1);
            Connecting = new VoiceChatState("Connecting", 2);
            Active = new VoiceChatState("Active", 3);
            $VALUES = (new VoiceChatState[] {
                None, Ringing, Connecting, Active
            });
        }

        private VoiceChatState(String s, int i)
        {
            super(s, i);
        }
    }


    private static final VoiceChatInfo emptyChatState;
    private static final Interner interner;
    public final UUID activeSpeakerID;
    public final boolean isConference;
    public final boolean localMicActive;
    public final int numActiveSpeakers;
    public final VoiceChatState previousState;
    public final VoiceChatState state;

    private VoiceChatInfo(Bundle bundle)
    {
        UUID uuid = null;
        super();
        state = VoiceChatState.valueOf(bundle.getString("state"));
        previousState = VoiceChatState.valueOf(bundle.getString("previousState"));
        numActiveSpeakers = bundle.getInt("numActiveSpeakers");
        String s = bundle.getString("activeSpeakerID");
        if (s != null)
        {
            uuid = UUID.fromString(s);
        }
        activeSpeakerID = uuid;
        isConference = bundle.getBoolean("isConference");
        localMicActive = bundle.getBoolean("localMicActive");
    }

    private VoiceChatInfo(VoiceChatState voicechatstate, VoiceChatState voicechatstate1, int i, UUID uuid, boolean flag, boolean flag1)
    {
        state = voicechatstate;
        previousState = voicechatstate1;
        numActiveSpeakers = i;
        activeSpeakerID = uuid;
        isConference = flag;
        localMicActive = flag1;
    }

    public static VoiceChatInfo create(Bundle bundle)
    {
        return (VoiceChatInfo)interner.intern(new VoiceChatInfo(bundle));
    }

    public static VoiceChatInfo create(VoiceChatState voicechatstate, VoiceChatState voicechatstate1, int i, UUID uuid, boolean flag, boolean flag1)
    {
        return (VoiceChatInfo)interner.intern(new VoiceChatInfo(voicechatstate, voicechatstate1, i, uuid, flag, flag1));
    }

    public static VoiceChatInfo empty()
    {
        return emptyChatState;
    }

    public boolean equals(Object obj)
    {
        if (this != obj)
        {
            if (obj == null || getClass() != obj.getClass())
            {
                return false;
            }
            obj = (VoiceChatInfo)obj;
        } else
        {
            return true;
        }
        if (numActiveSpeakers == ((VoiceChatInfo) (obj)).numActiveSpeakers)
        {
            if (isConference == ((VoiceChatInfo) (obj)).isConference)
            {
                if (localMicActive == ((VoiceChatInfo) (obj)).localMicActive)
                {
                    if (state == ((VoiceChatInfo) (obj)).state)
                    {
                        if (previousState == ((VoiceChatInfo) (obj)).previousState)
                        {
                            if (activeSpeakerID == null)
                            {
                                return ((VoiceChatInfo) (obj)).activeSpeakerID == null;
                            } else
                            {
                                return activeSpeakerID.equals(((VoiceChatInfo) (obj)).activeSpeakerID);
                            }
                        } else
                        {
                            return false;
                        }
                    } else
                    {
                        return false;
                    }
                } else
                {
                    return false;
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int k = 0;
        int l = state.hashCode();
        int i1 = previousState.hashCode();
        int j1 = numActiveSpeakers;
        int i;
        int j;
        if (activeSpeakerID == null)
        {
            i = 0;
        } else
        {
            i = activeSpeakerID.hashCode();
        }
        if (!isConference)
        {
            j = 0;
        } else
        {
            j = 1;
        }
        if (localMicActive)
        {
            k = 1;
        }
        return (j + (i + ((l * 31 + i1) * 31 + j1) * 31) * 31) * 31 + k;
    }

    public Bundle toBundle()
    {
        String s = null;
        Bundle bundle = new Bundle();
        bundle.putString("state", state.toString());
        bundle.putString("previousState", previousState.toString());
        bundle.putInt("numActiveSpeakers", numActiveSpeakers);
        if (activeSpeakerID != null)
        {
            s = activeSpeakerID.toString();
        }
        bundle.putString("activeSpeakerID", s);
        bundle.putBoolean("isConference", isConference);
        bundle.putBoolean("localMicActive", localMicActive);
        return bundle;
    }

    public String toString()
    {
        return (new StringBuilder()).append("VoiceChatInfo{state=").append(state).append(", previousState=").append(previousState).append(", numActiveSpeakers=").append(numActiveSpeakers).append(", activeSpeakerID=").append(activeSpeakerID).append(", isConference=").append(isConference).append(", localMicActive=").append(localMicActive).append('}').toString();
    }

    static 
    {
        interner = Interners.newWeakInterner();
        emptyChatState = (VoiceChatInfo)interner.intern(new VoiceChatInfo(VoiceChatState.None, VoiceChatState.None, 0, null, false, false));
    }
}
