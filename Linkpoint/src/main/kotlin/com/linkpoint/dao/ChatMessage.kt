package com.linkpoint.dao

import com.linkpoint.utils.Identifiable
import java.util.Date
import java.util.UUID

class ChatMessage : Identifiable<Long> {
    private Boolean accepted
    private Integer assetType
    private Integer chatChannel
    private Long chatterID
    private Byte[] dialogButtons
    private Boolean dialogIgnored
    private String dialogSelectedOption
    private Integer eventState
    private Long id
    private Boolean isOffline
    private UUID itemID
    private String itemName
    private String messageText
    private Int messageType
    private Integer newBalance
    private String objectName
    private Integer origIMType
    private Date origTimestamp
    private Integer questionMask
    private String senderLegacyName
    private String senderName
    private Integer senderType
    private UUID senderUUID
    private UUID sessionID
    private Boolean syncedToGoogleDrive
    private Integer textBoxButtonIndex
    private Date timestamp
    private Integer transactionAmount
    private UUID userID
    private Int viewType

    public ChatMessage(Long l) {
        this.id = l
    }

    public ChatMessage(Long l, Long j, Date date, Int i, Date date2, Boolean bool, UUID uuid, Integer num, String str, String str2, String str3, Int i2, Integer num2, Integer num3, UUID uuid2, UUID uuid3, String str4, Integer num4, Integer num5, Integer num6, Integer num7, Boolean bool2, Boolean bool3, UUID uuid4, String str5, Integer num8, Byte[] bArr, String str6, Integer num9, Boolean z) {
        this.id = l
        this.chatterID = j
        this.timestamp = date
        this.viewType = i
        this.origTimestamp = date2
        this.isOffline = bool
        this.senderUUID = uuid
        this.senderType = num
        this.senderName = str
        this.senderLegacyName = str2
        this.messageText = str3
        this.messageType = i2
        this.eventState = num2
        this.origIMType = num3
        this.sessionID = uuid2
        this.itemID = uuid3
        this.itemName = str4
        this.assetType = num4
        this.transactionAmount = num5
        this.newBalance = num6
        this.chatChannel = num7
        this.dialogIgnored = bool2
        this.accepted = bool3
        this.userID = uuid4
        this.objectName = str5
        this.questionMask = num8
        this.dialogButtons = bArr
        this.dialogSelectedOption = str6
        this.textBoxButtonIndex = num9
        this.syncedToGoogleDrive = z
    }

    public Boolean getAccepted() {
        return this.accepted
    }

    public Integer getAssetType() {
        return this.assetType
    }

    public Integer getChatChannel() {
        return this.chatChannel
    }

    public Long getChatterID() {
        return this.chatterID
    }

    public Byte[] getDialogButtons() {
        return this.dialogButtons
    }

    public Boolean getDialogIgnored() {
        return this.dialogIgnored
    }

    public String getDialogSelectedOption() {
        return this.dialogSelectedOption
    }

    public Integer getEventState() {
        return this.eventState
    }

    public Long getId() {
        return this.id
    }

    public Boolean getIsOffline() {
        return this.isOffline
    }

    public UUID getItemID() {
        return this.itemID
    }

    public String getItemName() {
        return this.itemName
    }

    public String getMessageText() {
        return this.messageText
    }

    public Int getMessageType() {
        return this.messageType
    }

    public Integer getNewBalance() {
        return this.newBalance
    }

    public String getObjectName() {
        return this.objectName
    }

    public Integer getOrigIMType() {
        return this.origIMType
    }

    public Date getOrigTimestamp() {
        return this.origTimestamp
    }

    public Integer getQuestionMask() {
        return this.questionMask
    }

    public String getSenderLegacyName() {
        return this.senderLegacyName
    }

    public String getSenderName() {
        return this.senderName
    }

    public Integer getSenderType() {
        return this.senderType
    }

    public UUID getSenderUUID() {
        return this.senderUUID
    }

    public UUID getSessionID() {
        return this.sessionID
    }

    public Boolean getSyncedToGoogleDrive() {
        return this.syncedToGoogleDrive
    }

    public Integer getTextBoxButtonIndex() {
        return this.textBoxButtonIndex
    }

    public Date getTimestamp() {
        return this.timestamp
    }

    public Integer getTransactionAmount() {
        return this.transactionAmount
    }

    public UUID getUserID() {
        return this.userID
    }

    public Int getViewType() {
        return this.viewType
    }

    public Unit setAccepted(Boolean bool) {
        this.accepted = bool
    }

    public Unit setAssetType(Integer num) {
        this.assetType = num
    }

    public Unit setChatChannel(Integer num) {
        this.chatChannel = num
    }

    public Unit setChatterID(Long j) {
        this.chatterID = j
    }

    public Unit setDialogButtons(Byte[] bArr) {
        this.dialogButtons = bArr
    }

    public Unit setDialogIgnored(Boolean bool) {
        this.dialogIgnored = bool
    }

    public Unit setDialogSelectedOption(String str) {
        this.dialogSelectedOption = str
    }

    public Unit setEventState(Integer num) {
        this.eventState = num
    }

    public Unit setId(Long l) {
        this.id = l
    }

    public Unit setIsOffline(Boolean bool) {
        this.isOffline = bool
    }

    public Unit setItemID(UUID uuid) {
        this.itemID = uuid
    }

    public Unit setItemName(String str) {
        this.itemName = str
    }

    public Unit setMessageText(String str) {
        this.messageText = str
    }

    public Unit setMessageType(Int i) {
        this.messageType = i
    }

    public Unit setNewBalance(Integer num) {
        this.newBalance = num
    }

    public Unit setObjectName(String str) {
        this.objectName = str
    }

    public Unit setOrigIMType(Integer num) {
        this.origIMType = num
    }

    public Unit setOrigTimestamp(Date date) {
        this.origTimestamp = date
    }

    public Unit setQuestionMask(Integer num) {
        this.questionMask = num
    }

    public Unit setSenderLegacyName(String str) {
        this.senderLegacyName = str
    }

    public Unit setSenderName(String str) {
        this.senderName = str
    }

    public Unit setSenderType(Integer num) {
        this.senderType = num
    }

    public Unit setSenderUUID(UUID uuid) {
        this.senderUUID = uuid
    }

    public Unit setSessionID(UUID uuid) {
        this.sessionID = uuid
    }

    public Unit setSyncedToGoogleDrive(Boolean z) {
        this.syncedToGoogleDrive = z
    }

    public Unit setTextBoxButtonIndex(Integer num) {
        this.textBoxButtonIndex = num
    }

    public Unit setTimestamp(Date date) {
        this.timestamp = date
    }

    public Unit setTransactionAmount(Integer num) {
        this.transactionAmount = num
    }

    public Unit setUserID(UUID uuid) {
        this.userID = uuid
    }

    public Unit setViewType(Int i) {
        this.viewType = i
    }
}
