package com.linkpoint.dao

import com.linkpoint.utils.Identifiable
import java.util.Date
import java.util.UUID

class ChatMessage : Identifiable<Long> {
    private Boolean accepted
    private Integer assetType
    private Integer chatChannel
    private Long chatterID
    private ByteArray dialogButtons
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

    public ChatMessage(Long l, Long j, Date date, Int i, Date date2, Boolean bool, UUID uuid, Integer num, String str, String str2, String str3, Int i2, Integer num2, Integer num3, UUID uuid2, UUID uuid3, String str4, Integer num4, Integer num5, Integer num6, Integer num7, Boolean bool2, Boolean bool3, UUID uuid4, String str5, Integer num8, ByteArray bArr, String str6, Integer num9, Boolean z) {
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

     public fun getAccepted(): Boolean {
        return this.accepted
    }

     public fun getAssetType(): Integer {
        return this.assetType
    }

     public fun getChatChannel(): Integer {
        return this.chatChannel
    }

     public fun getChatterID(): Long {
        return this.chatterID
    }

     public fun getDialogButtons(): ByteArray {
        return this.dialogButtons
    }

     public fun getDialogIgnored(): Boolean {
        return this.dialogIgnored
    }

     public fun getDialogSelectedOption(): String {
        return this.dialogSelectedOption
    }

     public fun getEventState(): Integer {
        return this.eventState
    }

     public fun getId(): Long {
        return this.id
    }

     public fun getIsOffline(): Boolean {
        return this.isOffline
    }

     public fun getItemID(): UUID {
        return this.itemID
    }

     public fun getItemName(): String {
        return this.itemName
    }

     public fun getMessageText(): String {
        return this.messageText
    }

     public fun getMessageType(): Int {
        return this.messageType
    }

     public fun getNewBalance(): Integer {
        return this.newBalance
    }

     public fun getObjectName(): String {
        return this.objectName
    }

     public fun getOrigIMType(): Integer {
        return this.origIMType
    }

     public fun getOrigTimestamp(): Date {
        return this.origTimestamp
    }

     public fun getQuestionMask(): Integer {
        return this.questionMask
    }

     public fun getSenderLegacyName(): String {
        return this.senderLegacyName
    }

     public fun getSenderName(): String {
        return this.senderName
    }

     public fun getSenderType(): Integer {
        return this.senderType
    }

     public fun getSenderUUID(): UUID {
        return this.senderUUID
    }

     public fun getSessionID(): UUID {
        return this.sessionID
    }

     public fun getSyncedToGoogleDrive(): Boolean {
        return this.syncedToGoogleDrive
    }

     public fun getTextBoxButtonIndex(): Integer {
        return this.textBoxButtonIndex
    }

     public fun getTimestamp(): Date {
        return this.timestamp
    }

     public fun getTransactionAmount(): Integer {
        return this.transactionAmount
    }

     public fun getUserID(): UUID {
        return this.userID
    }

     public fun getViewType(): Int {
        return this.viewType
    }

    fun setAccepted(bool: Boolean) {
        this.accepted = bool
    }

    fun setAssetType(num: Integer) {
        this.assetType = num
    }

    fun setChatChannel(num: Integer) {
        this.chatChannel = num
    }

    fun setChatterID(j: Long) {
        this.chatterID = j
    }

    fun setDialogButtons(bArr: ByteArray) {
        this.dialogButtons = bArr
    }

    fun setDialogIgnored(bool: Boolean) {
        this.dialogIgnored = bool
    }

    fun setDialogSelectedOption(str: String) {
        this.dialogSelectedOption = str
    }

    fun setEventState(num: Integer) {
        this.eventState = num
    }

    fun setId(l: Long) {
        this.id = l
    }

    fun setIsOffline(bool: Boolean) {
        this.isOffline = bool
    }

    fun setItemID(uuid: UUID) {
        this.itemID = uuid
    }

    fun setItemName(str: String) {
        this.itemName = str
    }

    fun setMessageText(str: String) {
        this.messageText = str
    }

    fun setMessageType(i: Int) {
        this.messageType = i
    }

    fun setNewBalance(num: Integer) {
        this.newBalance = num
    }

    fun setObjectName(str: String) {
        this.objectName = str
    }

    fun setOrigIMType(num: Integer) {
        this.origIMType = num
    }

    fun setOrigTimestamp(date: Date) {
        this.origTimestamp = date
    }

    fun setQuestionMask(num: Integer) {
        this.questionMask = num
    }

    fun setSenderLegacyName(str: String) {
        this.senderLegacyName = str
    }

    fun setSenderName(str: String) {
        this.senderName = str
    }

    fun setSenderType(num: Integer) {
        this.senderType = num
    }

    fun setSenderUUID(uuid: UUID) {
        this.senderUUID = uuid
    }

    fun setSessionID(uuid: UUID) {
        this.sessionID = uuid
    }

    fun setSyncedToGoogleDrive(z: Boolean) {
        this.syncedToGoogleDrive = z
    }

    fun setTextBoxButtonIndex(num: Integer) {
        this.textBoxButtonIndex = num
    }

    fun setTimestamp(date: Date) {
        this.timestamp = date
    }

    fun setTransactionAmount(num: Integer) {
        this.transactionAmount = num
    }

    fun setUserID(uuid: UUID) {
        this.userID = uuid
    }

    fun setViewType(i: Int) {
        this.viewType = i
    }
}
