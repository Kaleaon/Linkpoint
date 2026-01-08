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
import androidx.annotation.NonNull

class SLChatInventoryItemOfferedEvent : SLChatYesNoEvent {
    private SLAssetType assetType
    private UUID itemID
    private String itemName
    private Int origIMType
    private UUID sessionID

    SLChatInventoryItemOfferedEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.origIMType = chatMessage.getOrigIMType().intValue()
        this.sessionID = chatMessage.getSessionID()
        this.itemID = chatMessage.getItemID()
        this.itemName = chatMessage.getItemName()
        this.assetType = SLAssetType.getByType(chatMessage.getAssetType().intValue())
    }

    SLChatInventoryItemOfferedEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message))
        this.itemName = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
        this.itemID = extractItemID(improvedInstantMessage)
        this.assetType = extractAssetType(improvedInstantMessage)
    }

    SLChatInventoryItemOfferedEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str, UUID uuid2, SLAssetType sLAssetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, str)
        this.itemName = str
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
        this.itemID = uuid2
        this.assetType = sLAssetType
    }

    protected SLAssetType extractAssetType(ImprovedInstantMessage improvedInstantMessage) {
        return improvedInstantMessage.MessageBlock_Field.BinaryBucket.size >= 1 ? SLAssetType.getByType(improvedInstantMessage.MessageBlock_Field.BinaryBucket[0]) : SLAssetType.AT_UNKNOWN
    }

    protected UUID extractItemID(ImprovedInstantMessage improvedInstantMessage) {
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.size < 17) {
            return null
        }
        ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket)
        wrap.order(ByteOrder.BIG_ENDIAN)
        wrap.get()
        return UUID(wrap.getLong(), wrap.getLong())
    }

    fun getAssetType(): SLAssetType {
        return this.assetType
    }

    fun getItemID(): UUID {
        return this.itemID
    }

    fun getItemName(): String {
        return this.itemName
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOffered
    }

    fun getNoButton(Context context): String {
        return context.getString(R.string.inv_offer_no)
    }

    fun getNoMessage(Context context): String {
        return context.getString(R.string.inv_offer_declined)
    }

    fun getQuestion(Context context): String {
        return context.getString(R.string.inv_offer_question)
    }

    fun getText(Context context, @NonNull UserManager userManager): String {
        return context.getString(R.string.chat_inventory_other_offer_format, Object[]{this.itemName})
    }

    fun getYesButton(Context context): String {
        return context.getString(R.string.inv_offer_yes)
    }

    fun getYesMessage(Context context): String {
        return context.getString(R.string.inv_offer_accepted)
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun onNoAction(Context context, UserManager userManager): Unit {
        super.onNoAction(context, userManager)
        UUID sourceUUID = this.source.getSourceUUID()
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, false, sourceUUID, this.sessionID, (UUID) null)
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.DeleteInventoryItemRaw(this.itemID)
            }
        }
    }

    fun onOfferAccepted(Context context, UserManager userManager, UUID uuid): Unit {
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, true, this.source.getSourceUUID(), this.sessionID, uuid)
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.MoveInventoryItemRaw(this.itemID, this.itemName, uuid)
            }
        }
    }

    fun onYesAction(Context context, UserManager userManager): Unit {
        if (this.dbMessage != null) {
            context.startActivity(InventoryActivity.makeSaveItemIntent(context, this.agentUUID, InventorySaveInfo(InventorySaveInfo.InventorySaveType.InventoryOffer, this.itemID, getItemName(), (UUID) null, this.assetType, this.dbMessage.getId().longValue())))
        }
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage): Unit {
        Integer num = null
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
