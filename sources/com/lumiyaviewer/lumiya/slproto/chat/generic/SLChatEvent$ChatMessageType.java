// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            SLChatEvent

public static final class  extends Enum
{

    private static final MissedVoiceCall $VALUES[];
    public static final MissedVoiceCall BalanceChanged;
    public static final MissedVoiceCall EnableRLVOffer;
    public static final MissedVoiceCall FriendshipOffered;
    public static final MissedVoiceCall FriendshipResult;
    public static final MissedVoiceCall GroupInvitation;
    public static final MissedVoiceCall GroupInvitationSent;
    public static final MissedVoiceCall InventoryItemOffered;
    public static final MissedVoiceCall InventoryItemOfferedByGroupNotice;
    public static final MissedVoiceCall InventoryItemOfferedByYou;
    public static final MissedVoiceCall Lure;
    public static final MissedVoiceCall LureRequest;
    public static final MissedVoiceCall LureRequested;
    public static final MissedVoiceCall MissedVoiceCall;
    public static final MissedVoiceCall PermissionRequest;
    public static final MissedVoiceCall ScriptDialog;
    public static final MissedVoiceCall SessionMark;
    public static final MissedVoiceCall SystemMessage;
    public static final MissedVoiceCall Text;
    public static final MissedVoiceCall TextBoxDialog;
    public static final MissedVoiceCall VALUES[] = values();
    public static final values VoiceUpgrade;
    public static final values WentOffline;
    public static final values WentOnline;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent$ChatMessageType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        Text = new <init>("Text", 0);
        BalanceChanged = new <init>("BalanceChanged", 1);
        InventoryItemOffered = new <init>("InventoryItemOffered", 2);
        InventoryItemOfferedByGroupNotice = new <init>("InventoryItemOfferedByGroupNotice", 3);
        InventoryItemOfferedByYou = new <init>("InventoryItemOfferedByYou", 4);
        FriendshipOffered = new <init>("FriendshipOffered", 5);
        FriendshipResult = new <init>("FriendshipResult", 6);
        GroupInvitation = new <init>("GroupInvitation", 7);
        GroupInvitationSent = new <init>("GroupInvitationSent", 8);
        Lure = new <init>("Lure", 9);
        LureRequested = new <init>("LureRequested", 10);
        LureRequest = new <init>("LureRequest", 11);
        WentOnline = new <init>("WentOnline", 12);
        WentOffline = new <init>("WentOffline", 13);
        PermissionRequest = new <init>("PermissionRequest", 14);
        ScriptDialog = new <init>("ScriptDialog", 15);
        TextBoxDialog = new <init>("TextBoxDialog", 16);
        EnableRLVOffer = new <init>("EnableRLVOffer", 17);
        SessionMark = new <init>("SessionMark", 18);
        SystemMessage = new <init>("SystemMessage", 19);
        VoiceUpgrade = new <init>("VoiceUpgrade", 20);
        MissedVoiceCall = new <init>("MissedVoiceCall", 21);
        $VALUES = (new .VALUES[] {
            Text, BalanceChanged, InventoryItemOffered, InventoryItemOfferedByGroupNotice, InventoryItemOfferedByYou, FriendshipOffered, FriendshipResult, GroupInvitation, GroupInvitationSent, Lure, 
            LureRequested, LureRequest, WentOnline, WentOffline, PermissionRequest, ScriptDialog, TextBoxDialog, EnableRLVOffer, SessionMark, SystemMessage, 
            VoiceUpgrade, MissedVoiceCall
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
