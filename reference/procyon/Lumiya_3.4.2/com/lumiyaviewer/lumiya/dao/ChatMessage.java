// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.Date;
import java.util.UUID;
import com.lumiyaviewer.lumiya.utils.Identifiable;

public class ChatMessage implements Identifiable<Long>
{
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
    
    public ChatMessage() {
    }
    
    public ChatMessage(final Long id) {
        this.id = id;
    }
    
    public ChatMessage(final Long id, final long chatterID, final Date timestamp, final int viewType, final Date origTimestamp, final Boolean isOffline, final UUID senderUUID, final Integer senderType, final String senderName, final String senderLegacyName, final String messageText, final int messageType, final Integer eventState, final Integer origIMType, final UUID sessionID, final UUID itemID, final String itemName, final Integer assetType, final Integer transactionAmount, final Integer newBalance, final Integer chatChannel, final Boolean dialogIgnored, final Boolean accepted, final UUID userID, final String objectName, final Integer questionMask, final byte[] dialogButtons, final String dialogSelectedOption, final Integer textBoxButtonIndex, final boolean syncedToGoogleDrive) {
        this.id = id;
        this.chatterID = chatterID;
        this.timestamp = timestamp;
        this.viewType = viewType;
        this.origTimestamp = origTimestamp;
        this.isOffline = isOffline;
        this.senderUUID = senderUUID;
        this.senderType = senderType;
        this.senderName = senderName;
        this.senderLegacyName = senderLegacyName;
        this.messageText = messageText;
        this.messageType = messageType;
        this.eventState = eventState;
        this.origIMType = origIMType;
        this.sessionID = sessionID;
        this.itemID = itemID;
        this.itemName = itemName;
        this.assetType = assetType;
        this.transactionAmount = transactionAmount;
        this.newBalance = newBalance;
        this.chatChannel = chatChannel;
        this.dialogIgnored = dialogIgnored;
        this.accepted = accepted;
        this.userID = userID;
        this.objectName = objectName;
        this.questionMask = questionMask;
        this.dialogButtons = dialogButtons;
        this.dialogSelectedOption = dialogSelectedOption;
        this.textBoxButtonIndex = textBoxButtonIndex;
        this.syncedToGoogleDrive = syncedToGoogleDrive;
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
    
    @Override
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
    
    public void setAccepted(final Boolean accepted) {
        this.accepted = accepted;
    }
    
    public void setAssetType(final Integer assetType) {
        this.assetType = assetType;
    }
    
    public void setChatChannel(final Integer chatChannel) {
        this.chatChannel = chatChannel;
    }
    
    public void setChatterID(final long chatterID) {
        this.chatterID = chatterID;
    }
    
    public void setDialogButtons(final byte[] dialogButtons) {
        this.dialogButtons = dialogButtons;
    }
    
    public void setDialogIgnored(final Boolean dialogIgnored) {
        this.dialogIgnored = dialogIgnored;
    }
    
    public void setDialogSelectedOption(final String dialogSelectedOption) {
        this.dialogSelectedOption = dialogSelectedOption;
    }
    
    public void setEventState(final Integer eventState) {
        this.eventState = eventState;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public void setIsOffline(final Boolean isOffline) {
        this.isOffline = isOffline;
    }
    
    public void setItemID(final UUID itemID) {
        this.itemID = itemID;
    }
    
    public void setItemName(final String itemName) {
        this.itemName = itemName;
    }
    
    public void setMessageText(final String messageText) {
        this.messageText = messageText;
    }
    
    public void setMessageType(final int messageType) {
        this.messageType = messageType;
    }
    
    public void setNewBalance(final Integer newBalance) {
        this.newBalance = newBalance;
    }
    
    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }
    
    public void setOrigIMType(final Integer origIMType) {
        this.origIMType = origIMType;
    }
    
    public void setOrigTimestamp(final Date origTimestamp) {
        this.origTimestamp = origTimestamp;
    }
    
    public void setQuestionMask(final Integer questionMask) {
        this.questionMask = questionMask;
    }
    
    public void setSenderLegacyName(final String senderLegacyName) {
        this.senderLegacyName = senderLegacyName;
    }
    
    public void setSenderName(final String senderName) {
        this.senderName = senderName;
    }
    
    public void setSenderType(final Integer senderType) {
        this.senderType = senderType;
    }
    
    public void setSenderUUID(final UUID senderUUID) {
        this.senderUUID = senderUUID;
    }
    
    public void setSessionID(final UUID sessionID) {
        this.sessionID = sessionID;
    }
    
    public void setSyncedToGoogleDrive(final boolean syncedToGoogleDrive) {
        this.syncedToGoogleDrive = syncedToGoogleDrive;
    }
    
    public void setTextBoxButtonIndex(final Integer textBoxButtonIndex) {
        this.textBoxButtonIndex = textBoxButtonIndex;
    }
    
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setTransactionAmount(final Integer transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    
    public void setUserID(final UUID userID) {
        this.userID = userID;
    }
    
    public void setViewType(final int viewType) {
        this.viewType = viewType;
    }
}
