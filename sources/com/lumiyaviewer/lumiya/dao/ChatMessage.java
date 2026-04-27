// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import com.lumiyaviewer.lumiya.utils.Identifiable;
import java.util.Date;
import java.util.UUID;

public class ChatMessage
    implements Identifiable
{

    private Boolean accepted;
    private Integer assetType;
    private Integer chatChannel;
    private long chatterID;
    private byte dialogButtons[];
    private Boolean dialogIgnored;
    private String dialogSelectedOption;
    private Integer eventState;
    private Long id;
    private Boolean isOffline;
    private UUID itemID;
    private String itemName;
    private String messageText;
    private int messageType;
    private Integer newBalance;
    private String objectName;
    private Integer origIMType;
    private Date origTimestamp;
    private Integer questionMask;
    private String senderLegacyName;
    private String senderName;
    private Integer senderType;
    private UUID senderUUID;
    private UUID sessionID;
    private boolean syncedToGoogleDrive;
    private Integer textBoxButtonIndex;
    private Date timestamp;
    private Integer transactionAmount;
    private UUID userID;
    private int viewType;

    public ChatMessage()
    {
    }

    public ChatMessage(Long long1)
    {
        id = long1;
    }

    public ChatMessage(Long long1, long l, Date date, int i, Date date1, Boolean boolean1, 
            UUID uuid, Integer integer, String s, String s1, String s2, int j, Integer integer1, 
            Integer integer2, UUID uuid1, UUID uuid2, String s3, Integer integer3, Integer integer4, Integer integer5, 
            Integer integer6, Boolean boolean2, Boolean boolean3, UUID uuid3, String s4, Integer integer7, byte abyte0[], 
            String s5, Integer integer8, boolean flag)
    {
        id = long1;
        chatterID = l;
        timestamp = date;
        viewType = i;
        origTimestamp = date1;
        isOffline = boolean1;
        senderUUID = uuid;
        senderType = integer;
        senderName = s;
        senderLegacyName = s1;
        messageText = s2;
        messageType = j;
        eventState = integer1;
        origIMType = integer2;
        sessionID = uuid1;
        itemID = uuid2;
        itemName = s3;
        assetType = integer3;
        transactionAmount = integer4;
        newBalance = integer5;
        chatChannel = integer6;
        dialogIgnored = boolean2;
        accepted = boolean3;
        userID = uuid3;
        objectName = s4;
        questionMask = integer7;
        dialogButtons = abyte0;
        dialogSelectedOption = s5;
        textBoxButtonIndex = integer8;
        syncedToGoogleDrive = flag;
    }

    public Boolean getAccepted()
    {
        return accepted;
    }

    public Integer getAssetType()
    {
        return assetType;
    }

    public Integer getChatChannel()
    {
        return chatChannel;
    }

    public long getChatterID()
    {
        return chatterID;
    }

    public byte[] getDialogButtons()
    {
        return dialogButtons;
    }

    public Boolean getDialogIgnored()
    {
        return dialogIgnored;
    }

    public String getDialogSelectedOption()
    {
        return dialogSelectedOption;
    }

    public Integer getEventState()
    {
        return eventState;
    }

    public Long getId()
    {
        return id;
    }

    public volatile Object getId()
    {
        return getId();
    }

    public Boolean getIsOffline()
    {
        return isOffline;
    }

    public UUID getItemID()
    {
        return itemID;
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getMessageText()
    {
        return messageText;
    }

    public int getMessageType()
    {
        return messageType;
    }

    public Integer getNewBalance()
    {
        return newBalance;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public Integer getOrigIMType()
    {
        return origIMType;
    }

    public Date getOrigTimestamp()
    {
        return origTimestamp;
    }

    public Integer getQuestionMask()
    {
        return questionMask;
    }

    public String getSenderLegacyName()
    {
        return senderLegacyName;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public Integer getSenderType()
    {
        return senderType;
    }

    public UUID getSenderUUID()
    {
        return senderUUID;
    }

    public UUID getSessionID()
    {
        return sessionID;
    }

    public boolean getSyncedToGoogleDrive()
    {
        return syncedToGoogleDrive;
    }

    public Integer getTextBoxButtonIndex()
    {
        return textBoxButtonIndex;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public Integer getTransactionAmount()
    {
        return transactionAmount;
    }

    public UUID getUserID()
    {
        return userID;
    }

    public int getViewType()
    {
        return viewType;
    }

    public void setAccepted(Boolean boolean1)
    {
        accepted = boolean1;
    }

    public void setAssetType(Integer integer)
    {
        assetType = integer;
    }

    public void setChatChannel(Integer integer)
    {
        chatChannel = integer;
    }

    public void setChatterID(long l)
    {
        chatterID = l;
    }

    public void setDialogButtons(byte abyte0[])
    {
        dialogButtons = abyte0;
    }

    public void setDialogIgnored(Boolean boolean1)
    {
        dialogIgnored = boolean1;
    }

    public void setDialogSelectedOption(String s)
    {
        dialogSelectedOption = s;
    }

    public void setEventState(Integer integer)
    {
        eventState = integer;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setIsOffline(Boolean boolean1)
    {
        isOffline = boolean1;
    }

    public void setItemID(UUID uuid)
    {
        itemID = uuid;
    }

    public void setItemName(String s)
    {
        itemName = s;
    }

    public void setMessageText(String s)
    {
        messageText = s;
    }

    public void setMessageType(int i)
    {
        messageType = i;
    }

    public void setNewBalance(Integer integer)
    {
        newBalance = integer;
    }

    public void setObjectName(String s)
    {
        objectName = s;
    }

    public void setOrigIMType(Integer integer)
    {
        origIMType = integer;
    }

    public void setOrigTimestamp(Date date)
    {
        origTimestamp = date;
    }

    public void setQuestionMask(Integer integer)
    {
        questionMask = integer;
    }

    public void setSenderLegacyName(String s)
    {
        senderLegacyName = s;
    }

    public void setSenderName(String s)
    {
        senderName = s;
    }

    public void setSenderType(Integer integer)
    {
        senderType = integer;
    }

    public void setSenderUUID(UUID uuid)
    {
        senderUUID = uuid;
    }

    public void setSessionID(UUID uuid)
    {
        sessionID = uuid;
    }

    public void setSyncedToGoogleDrive(boolean flag)
    {
        syncedToGoogleDrive = flag;
    }

    public void setTextBoxButtonIndex(Integer integer)
    {
        textBoxButtonIndex = integer;
    }

    public void setTimestamp(Date date)
    {
        timestamp = date;
    }

    public void setTransactionAmount(Integer integer)
    {
        transactionAmount = integer;
    }

    public void setUserID(UUID uuid)
    {
        userID = uuid;
    }

    public void setViewType(int i)
    {
        viewType = i;
    }
}
