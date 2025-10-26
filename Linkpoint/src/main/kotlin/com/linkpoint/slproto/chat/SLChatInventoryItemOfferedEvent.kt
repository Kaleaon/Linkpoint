package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.inventory.InventoryActivity
import com.linkpoint.ui.inventory.InventorySaveInfo
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import javax.annotation.Nonnull

class SLChatInventoryItemOfferedEvent : SLChatYesNoEvent() {
    private val SLAssetType assetType
    private val UUID itemID
    private val String itemName
    private val Int origIMType
    private val UUID sessionID

    public SLChatInventoryItemOfferedEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.origIMType = chatMessage.getOrigIMType().intValue()
        this.sessionID = chatMessage.getSessionID()
        this.itemID = chatMessage.getItemID()
        this.itemName = chatMessage.getItemName()
        this.assetType = SLAssetType.getByType(chatMessage.getAssetType().intValue())
    }

    public SLChatInventoryItemOfferedEvent(ChatMessageSource chatMessageSource, UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message))
        this.itemName = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
        this.itemID = extractItemID(improvedInstantMessage)
        this.assetType = extractAssetType(improvedInstantMessage)
    }

    public SLChatInventoryItemOfferedEvent(ChatMessageSource chatMessageSource, UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str, UUID uuid2, SLAssetType sLAssetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, str)
        this.itemName = str
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
        this.itemID = uuid2
        this.assetType = sLAssetType
    }

    @JvmStatic
 protected fun extractAssetType(improvedInstantMessage: ImprovedInstantMessage): SLAssetType {
        return improvedInstantMessage.MessageBlock_Field.BinaryBucket.length >= 1 ? SLAssetType.getByType(improvedInstantMessage.MessageBlock_Field.BinaryBucket[0]) : SLAssetType.AT_UNKNOWN
    }

    @JvmStatic
 protected fun extractItemID(improvedInstantMessage: ImprovedInstantMessage): UUID {
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.length < 17) {
            return null
        }
        val wrap: ByteBuffer = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket)
        wrap.order(ByteOrder.BIG_ENDIAN)
        wrap.get()
        return UUID(wrap.getLong(), wrap.getLong())
    }

     public fun getAssetType(): SLAssetType {
        return this.assetType
    }

     public fun getItemID(): UUID {
        return this.itemID
    }

     public fun getItemName(): String {
        return this.itemName
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOffered
    }

     public fun getNoButton(context: Context): String {
        return context.getString(R.string.inv_offer_no)
    }

     public fun getNoMessage(context: Context): String {
        return context.getString(R.string.inv_offer_declined)
    }

     public fun getQuestion(context: Context): String {
        return context.getString(R.string.inv_offer_question)
    }

     public fun getText(context: Context, userManager: UserManager): String {
        return context.getString(R.string.chat_inventory_other_offer_format, Array<Any>{this.itemName})
    }

     public fun getYesButton(context: Context): String {
        return context.getString(R.string.inv_offer_yes)
    }

     public fun getYesMessage(context: Context): String {
        return context.getString(R.string.inv_offer_accepted)
    }

    /* access modifiers changed from: protected */
     public fun isActionMessage(userManager: UserManager): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun onNoAction(context: Context, userManager: UserManager) {
        super.onNoAction(context, userManager)
        val sourceUUID: UUID = this.source.getSourceUUID()
        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, false, sourceUUID, this.sessionID, (UUID) null)
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.DeleteInventoryItemRaw(this.itemID)
            }
        }
    }

    fun onOfferAccepted(context: Context, userManager: UserManager, uuid: UUID) {
        super.onYesAction(context, userManager)
        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, true, this.source.getSourceUUID(), this.sessionID, uuid)
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.MoveInventoryItemRaw(this.itemID, this.itemName, uuid)
            }
        }
    }

    fun onYesAction(context: Context, userManager: UserManager) {
        if (this.dbMessage != null) {
            context.startActivity(InventoryActivity.makeSaveItemIntent(context, this.agentUUID, InventorySaveInfo(InventorySaveInfo.InventorySaveType.InventoryOffer, this.itemID, getItemName(), (UUID) null, this.assetType, this.dbMessage.getId().longValue())))
        }
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        val num: Integer = null
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setOrigIMType(Integer.valueOf(this.origIMType))
        chatMessage.setSessionID(this.sessionID)
        chatMessage.setItemID(this.itemID)
        chatMessage.setItemName(this.itemName)
        if (this.assetType != null) {
            num = Integer.valueOf(this.assetType.getTypeCode())
        }
        chatMessage.setAssetType(num)
    }
}
