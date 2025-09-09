package com.lumiyaviewer.lumiya.dao;

import com.lumiyaviewer.lumiya.utils.Identifiable;
import java.util.Date;
import java.util.UUID;

public class ChatMessage implements Identifiable<Long> {
    private Boolean accepted;
    private Integer assetType;
    private Integer chatChannel;
    private long chatterID;
    private byte[] dialogButtons;
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

    public ChatMessage(Long l) {
        this.id = l;
    }

    public ChatMessage(Long l, long j, Date date, int i, Date date2, Boolean bool, UUID uuid, Integer num, String str, String str2, String str3, int i2, Integer num2, Integer num3, UUID uuid2, UUID uuid3, String str4, Integer num4, Integer num5, Integer num6, Integer num7, Boolean bool2, Boolean bool3, UUID uuid4, String str5, Integer num8, byte[] bArr, String str6, Integer num9, boolean z) {
        this.id = l;
        this.chatterID = j;
        this.timestamp = date;
        this.viewType = i;
        this.origTimestamp = date2;
        this.isOffline = bool;
        this.senderUUID = uuid;
        this.senderType = num;
        this.senderName = str;
        this.senderLegacyName = str2;
        this.messageText = str3;
        this.messageType = i2;
        this.eventState = num2;
        this.origIMType = num3;
        this.sessionID = uuid2;
        this.itemID = uuid3;
        this.itemName = str4;
        this.assetType = num4;
        this.transactionAmount = num5;
        this.newBalance = num6;
        this.chatChannel = num7;
        this.dialogIgnored = bool2;
        this.accepted = bool3;
        this.userID = uuid4;
        this.objectName = str5;
        this.questionMask = num8;
        this.dialogButtons = bArr;
        this.dialogSelectedOption = str6;
        this.textBoxButtonIndex = num9;
        this.syncedToGoogleDrive = z;
    }

    public Boolean getAccepted() {
        return this.accepted;
    }

    public Integer getAssetType() {
        return this.assetType;
    }

    public Integer getChatChannel() {
        return this.chatChannel;
    }

    public long getChatterID() {
        return this.chatterID;
    }

    public byte[] getDialogButtons() {
        return this.dialogButtons;
    }

    public Boolean getDialogIgnored() {
        return this.dialogIgnored;
    }

    public String getDialogSelectedOption() {
        return this.dialogSelectedOption;
    }

    public Integer getEventState() {
        return this.eventState;
    }

    public Long getId() {
        return this.id;
    }

    public Boolean getIsOffline() {
        return this.isOffline;
    }

    public UUID getItemID() {
        return this.itemID;
    }

    public String getItemName() {
        return this.itemName;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public Integer getNewBalance() {
        return this.newBalance;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public Integer getOrigIMType() {
        return this.origIMType;
    }

    public Date getOrigTimestamp() {
        return this.origTimestamp;
    }

    public Integer getQuestionMask() {
        return this.questionMask;
    }

    public String getSenderLegacyName() {
        return this.senderLegacyName;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public Integer getSenderType() {
        return this.senderType;
    }

    public UUID getSenderUUID() {
        return this.senderUUID;
    }

    public UUID getSessionID() {
        return this.sessionID;
    }

    public boolean getSyncedToGoogleDrive() {
        return this.syncedToGoogleDrive;
    }

    public Integer getTextBoxButtonIndex() {
        return this.textBoxButtonIndex;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public Integer getTransactionAmount() {
        return this.transactionAmount;
    }

    public UUID getUserID() {
        return this.userID;
    }

    public int getViewType() {
        return this.viewType;
    }

    public void setAccepted(Boolean bool) {
        this.accepted = bool;
    }

    public void setAssetType(Integer num) {
        this.assetType = num;
    }

    public void setChatChannel(Integer num) {
        this.chatChannel = num;
    }

    public void setChatterID(long j) {
        this.chatterID = j;
    }

    public void setDialogButtons(byte[] bArr) {
        this.dialogButtons = bArr;
    }

    public void setDialogIgnored(Boolean bool) {
        this.dialogIgnored = bool;
    }

    public void setDialogSelectedOption(String str) {
        this.dialogSelectedOption = str;
    }

    public void setEventState(Integer num) {
        this.eventState = num;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setIsOffline(Boolean bool) {
        this.isOffline = bool;
    }

    public void setItemID(UUID uuid) {
        this.itemID = uuid;
    }

    public void setItemName(String str) {
        this.itemName = str;
    }

    public void setMessageText(String str) {
        this.messageText = str;
    }

    public void setMessageType(int i) {
        this.messageType = i;
    }

    public void setNewBalance(Integer num) {
        this.newBalance = num;
    }

    public void setObjectName(String str) {
        this.objectName = str;
    }

    public void setOrigIMType(Integer num) {
        this.origIMType = num;
    }

    public void setOrigTimestamp(Date date) {
        this.origTimestamp = date;
    }

    public void setQuestionMask(Integer num) {
        this.questionMask = num;
    }

    public void setSenderLegacyName(String str) {
        this.senderLegacyName = str;
    }

    public void setSenderName(String str) {
        this.senderName = str;
    }

    public void setSenderType(Integer num) {
        this.senderType = num;
    }

    public void setSenderUUID(UUID uuid) {
        this.senderUUID = uuid;
    }

    public void setSessionID(UUID uuid) {
        this.sessionID = uuid;
    }

    public void setSyncedToGoogleDrive(boolean z) {
        this.syncedToGoogleDrive = z;
    }

    public void setTextBoxButtonIndex(Integer num) {
        this.textBoxButtonIndex = num;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    public void setTransactionAmount(Integer num) {
        this.transactionAmount = num;
    }

    public void setUserID(UUID uuid) {
        this.userID = uuid;
    }

    public void setViewType(int i) {
        this.viewType = i;
    }
}
