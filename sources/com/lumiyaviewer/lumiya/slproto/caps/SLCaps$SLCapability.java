// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.caps;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.caps:
//            SLCaps

public static final class  extends Enum
{

    private static final ChatSessionRequest $VALUES[];
    public static final ChatSessionRequest ChatSessionRequest;
    public static final ChatSessionRequest CopyInventoryFromNotecard;
    public static final ChatSessionRequest EventQueueGet;
    public static final ChatSessionRequest FetchInventoryDescendents2;
    public static final ChatSessionRequest GetDisplayNames;
    public static final ChatSessionRequest GetMesh;
    public static final ChatSessionRequest GetTexture;
    public static final ChatSessionRequest GroupMemberData;
    public static final ChatSessionRequest HomeLocation;
    public static final ChatSessionRequest NewFileAgentInventory;
    public static final ChatSessionRequest ParcelVoiceInfoRequest;
    public static final ChatSessionRequest ProvisionVoiceAccountRequest;
    public static final ChatSessionRequest UpdateAvatarAppearance;
    public static final ChatSessionRequest UpdateNotecardAgentInventory;
    public static final ChatSessionRequest UpdateNotecardTaskInventory;
    public static final ChatSessionRequest UpdateScriptAgent;
    public static final ChatSessionRequest UpdateScriptTask;
    public static final ChatSessionRequest UploadBakedTexture;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        EventQueueGet = new <init>("EventQueueGet", 0);
        GetTexture = new <init>("GetTexture", 1);
        UploadBakedTexture = new <init>("UploadBakedTexture", 2);
        FetchInventoryDescendents2 = new <init>("FetchInventoryDescendents2", 3);
        GetDisplayNames = new <init>("GetDisplayNames", 4);
        UpdateNotecardAgentInventory = new <init>("UpdateNotecardAgentInventory", 5);
        NewFileAgentInventory = new <init>("NewFileAgentInventory", 6);
        CopyInventoryFromNotecard = new <init>("CopyInventoryFromNotecard", 7);
        UpdateAvatarAppearance = new <init>("UpdateAvatarAppearance", 8);
        GetMesh = new <init>("GetMesh", 9);
        UpdateNotecardTaskInventory = new <init>("UpdateNotecardTaskInventory", 10);
        UpdateScriptTask = new <init>("UpdateScriptTask", 11);
        UpdateScriptAgent = new <init>("UpdateScriptAgent", 12);
        GroupMemberData = new <init>("GroupMemberData", 13);
        HomeLocation = new <init>("HomeLocation", 14);
        ProvisionVoiceAccountRequest = new <init>("ProvisionVoiceAccountRequest", 15);
        ParcelVoiceInfoRequest = new <init>("ParcelVoiceInfoRequest", 16);
        ChatSessionRequest = new <init>("ChatSessionRequest", 17);
        $VALUES = (new .VALUES[] {
            EventQueueGet, GetTexture, UploadBakedTexture, FetchInventoryDescendents2, GetDisplayNames, UpdateNotecardAgentInventory, NewFileAgentInventory, CopyInventoryFromNotecard, UpdateAvatarAppearance, GetMesh, 
            UpdateNotecardTaskInventory, UpdateScriptTask, UpdateScriptAgent, GroupMemberData, HomeLocation, ProvisionVoiceAccountRequest, ParcelVoiceInfoRequest, ChatSessionRequest
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
