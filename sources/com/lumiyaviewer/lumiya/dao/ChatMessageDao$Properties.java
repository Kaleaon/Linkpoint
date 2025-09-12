// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.Date;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            ChatMessageDao

public static class 
{

    public static final Property Accepted = new Property(22, java/lang/Boolean, "accepted", false, "ACCEPTED");
    public static final Property AssetType = new Property(17, java/lang/Integer, "assetType", false, "ASSET_TYPE");
    public static final Property ChatChannel = new Property(20, java/lang/Integer, "chatChannel", false, "CHAT_CHANNEL");
    public static final Property ChatterID;
    public static final Property DialogButtons = new Property(26, [B, "dialogButtons", false, "DIALOG_BUTTONS");
    public static final Property DialogIgnored = new Property(21, java/lang/Boolean, "dialogIgnored", false, "DIALOG_IGNORED");
    public static final Property DialogSelectedOption = new Property(27, java/lang/String, "dialogSelectedOption", false, "DIALOG_SELECTED_OPTION");
    public static final Property EventState = new Property(12, java/lang/Integer, "eventState", false, "EVENT_STATE");
    public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
    public static final Property IsOffline = new Property(5, java/lang/Boolean, "isOffline", false, "IS_OFFLINE");
    public static final Property ItemID = new Property(15, java/util/UUID, "itemID", false, "ITEM_ID");
    public static final Property ItemName = new Property(16, java/lang/String, "itemName", false, "ITEM_NAME");
    public static final Property MessageText = new Property(10, java/lang/String, "messageText", false, "MESSAGE_TEXT");
    public static final Property MessageType;
    public static final Property NewBalance = new Property(19, java/lang/Integer, "newBalance", false, "NEW_BALANCE");
    public static final Property ObjectName = new Property(24, java/lang/String, "objectName", false, "OBJECT_NAME");
    public static final Property OrigIMType = new Property(13, java/lang/Integer, "origIMType", false, "ORIG_IMTYPE");
    public static final Property OrigTimestamp = new Property(4, java/util/Date, "origTimestamp", false, "ORIG_TIMESTAMP");
    public static final Property QuestionMask = new Property(25, java/lang/Integer, "questionMask", false, "QUESTION_MASK");
    public static final Property SenderLegacyName = new Property(9, java/lang/String, "senderLegacyName", false, "SENDER_LEGACY_NAME");
    public static final Property SenderName = new Property(8, java/lang/String, "senderName", false, "SENDER_NAME");
    public static final Property SenderType = new Property(7, java/lang/Integer, "senderType", false, "SENDER_TYPE");
    public static final Property SenderUUID = new Property(6, java/util/UUID, "senderUUID", false, "SENDER_UUID");
    public static final Property SessionID = new Property(14, java/util/UUID, "sessionID", false, "SESSION_ID");
    public static final Property SyncedToGoogleDrive;
    public static final Property TextBoxButtonIndex = new Property(28, java/lang/Integer, "textBoxButtonIndex", false, "TEXT_BOX_BUTTON_INDEX");
    public static final Property Timestamp = new Property(2, java/util/Date, "timestamp", false, "TIMESTAMP");
    public static final Property TransactionAmount = new Property(18, java/lang/Integer, "transactionAmount", false, "TRANSACTION_AMOUNT");
    public static final Property UserID = new Property(23, java/util/UUID, "userID", false, "USER_ID");
    public static final Property ViewType;

    static 
    {
        ChatterID = new Property(1, Long.TYPE, "chatterID", false, "CHATTER_ID");
        ViewType = new Property(3, Integer.TYPE, "viewType", false, "VIEW_TYPE");
        MessageType = new Property(11, Integer.TYPE, "messageType", false, "MESSAGE_TYPE");
        SyncedToGoogleDrive = new Property(29, Boolean.TYPE, "syncedToGoogleDrive", false, "SYNCED_TO_GOOGLE_DRIVE");
    }

    public ()
    {
    }
}
