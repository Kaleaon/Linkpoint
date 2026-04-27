package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity
import com.lumiyaviewer.lumiya.ui.inventory.InventorySaveInfo
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
        return improvedInstantMessage.MessageBlock_Field.BinaryBucket.length >= 1 ? SLAssetType.getByType(improvedInstantMessage.MessageBlock_Field.BinaryBucket[0]) : SLAssetType.AT_UNKNOWN
    }

    protected UUID extractItemID(ImprovedInstantMessage improvedInstantMessage) {
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.length < 17) {
            return null
        }
        ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket)
        wrap.order(ByteOrder.BIG_ENDIAN)
        wrap.get()
        return UUID(wrap.getLong(), wrap.getLong())
    }

    SLAssetType getAssetType() {
        return this.assetType
    }

    UUID getItemID() {
        return this.itemID
    }

    String getItemName() {
        return this.itemName
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOffered
    }

    String getNoButton(Context context) {
        return context.getString(R.string.inv_offer_no)
    }

    String getNoMessage(Context context) {
        return context.getString(R.string.inv_offer_declined)
    }

    String getQuestion(Context context) {
        return context.getString(R.string.inv_offer_question)
    }

    String getText(Context context, @NonNull UserManager userManager) {
        return context.getString(R.string.chat_inventory_other_offer_format, Object[]{this.itemName})
    }

    String getYesButton(Context context) {
        return context.getString(R.string.inv_offer_yes)
    }

    String getYesMessage(Context context) {
        return context.getString(R.string.inv_offer_accepted)
    }

    /* access modifiers changed from: protected */
    Boolean isActionMessage(@NonNull UserManager userManager) {
        return true
    }

    /* access modifiers changed from: protected */
    Unit onNoAction(Context context, UserManager userManager) {
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

    Unit onOfferAccepted(Context context, UserManager userManager, UUID uuid) {
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, true, this.source.getSourceUUID(), this.sessionID, uuid)
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.MoveInventoryItemRaw(this.itemID, this.itemName, uuid)
            }
        }
    }

    Unit onYesAction(Context context, UserManager userManager) {
        if (this.dbMessage != null) {
            context.startActivity(InventoryActivity.makeSaveItemIntent(context, this.agentUUID, InventorySaveInfo(InventorySaveInfo.InventorySaveType.InventoryOffer, this.itemID, getItemName(), (UUID) null, this.assetType, this.dbMessage.getId().longValue())))
        }
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
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
