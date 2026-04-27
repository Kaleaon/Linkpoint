// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.caps;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.caps:
//            SLCapEventQueue

public static final class  extends Enum
{

    private static final UnknownCapsEvent $VALUES[];
    public static final UnknownCapsEvent AgentGroupDataUpdate;
    public static final UnknownCapsEvent AvatarGroupsReply;
    public static final UnknownCapsEvent BulkUpdateInventory;
    public static final UnknownCapsEvent ChatterBoxInvitation;
    public static final UnknownCapsEvent ChatterBoxSessionStartReply;
    public static final UnknownCapsEvent EstablishAgentCommunication;
    public static final UnknownCapsEvent ParcelProperties;
    public static final UnknownCapsEvent TeleportFailed;
    public static final UnknownCapsEvent TeleportFinish;
    public static final UnknownCapsEvent UnknownCapsEvent;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue$CapsEventType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        AgentGroupDataUpdate = new <init>("AgentGroupDataUpdate", 0);
        AvatarGroupsReply = new <init>("AvatarGroupsReply", 1);
        ChatterBoxInvitation = new <init>("ChatterBoxInvitation", 2);
        ChatterBoxSessionStartReply = new <init>("ChatterBoxSessionStartReply", 3);
        ParcelProperties = new <init>("ParcelProperties", 4);
        TeleportFailed = new <init>("TeleportFailed", 5);
        TeleportFinish = new <init>("TeleportFinish", 6);
        BulkUpdateInventory = new <init>("BulkUpdateInventory", 7);
        EstablishAgentCommunication = new <init>("EstablishAgentCommunication", 8);
        UnknownCapsEvent = new <init>("UnknownCapsEvent", 9);
        $VALUES = (new .VALUES[] {
            AgentGroupDataUpdate, AvatarGroupsReply, ChatterBoxInvitation, ChatterBoxSessionStartReply, ParcelProperties, TeleportFailed, TeleportFinish, BulkUpdateInventory, EstablishAgentCommunication, UnknownCapsEvent
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
