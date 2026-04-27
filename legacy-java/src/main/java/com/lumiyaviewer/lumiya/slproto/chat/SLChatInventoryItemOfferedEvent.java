package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySaveInfo;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class SLChatInventoryItemOfferedEvent extends SLChatYesNoEvent {
    private final SLAssetType assetType;
    private final UUID itemID;
    private final String itemName;
    private final int origIMType;
    private final UUID sessionID;

    public SLChatInventoryItemOfferedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.origIMType = chatMessage.getOrigIMType().intValue();
        this.sessionID = chatMessage.getSessionID();
        this.itemID = chatMessage.getItemID();
        this.itemName = chatMessage.getItemName();
        this.assetType = SLAssetType.getByType(chatMessage.getAssetType().intValue());
    }

    public SLChatInventoryItemOfferedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message));
        this.itemName = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog;
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
        this.itemID = extractItemID(improvedInstantMessage);
        this.assetType = extractAssetType(improvedInstantMessage);
    }

    public SLChatInventoryItemOfferedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str, UUID uuid2, SLAssetType sLAssetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, str);
        this.itemName = str;
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog;
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
        this.itemID = uuid2;
        this.assetType = sLAssetType;
    }

    protected static SLAssetType extractAssetType(ImprovedInstantMessage improvedInstantMessage) {
        return improvedInstantMessage.MessageBlock_Field.BinaryBucket.length >= 1 ? SLAssetType.getByType(improvedInstantMessage.MessageBlock_Field.BinaryBucket[0]) : SLAssetType.AT_UNKNOWN;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static UUID extractItemID(ImprovedInstantMessage improvedInstantMessage) {
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.length < 17) {
            return null;
        }
        ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket);
        wrap.order(ByteOrder.BIG_ENDIAN);
        wrap.get();
        return new UUID(wrap.getLong(), wrap.getLong());
    }

    public SLAssetType getAssetType() {
        return this.assetType;
    }

    public UUID getItemID() {
        return this.itemID;
    }

    public String getItemName() {
        return this.itemName;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOffered;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoButton(Context context) {
        return context.getString(R.string.inv_offer_no);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoMessage(Context context) {
        return context.getString(R.string.inv_offer_declined);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getQuestion(Context context) {
        return context.getString(R.string.inv_offer_question);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(R.string.chat_inventory_other_offer_format, this.itemName);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesButton(Context context) {
        return context.getString(R.string.inv_offer_yes);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesMessage(Context context) {
        return context.getString(R.string.inv_offer_accepted);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected boolean isActionMessage(@Nonnull UserManager userManager) {
        return true;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    protected void onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager);
        UUID sourceUUID = this.source.getSourceUUID();
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID == null || activeAgentCircuit == null) {
            return;
        }
        activeAgentCircuit.AcceptInventoryOffer(this.origIMType, false, sourceUUID, this.sessionID, null);
        if (this.itemID != null) {
            activeAgentCircuit.getModules().inventory.DeleteInventoryItemRaw(this.itemID);
        }
    }

    public void onOfferAccepted(Context context, UserManager userManager, UUID uuid) {
        super.onYesAction(context, userManager);
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, true, this.source.getSourceUUID(), this.sessionID, uuid);
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.MoveInventoryItemRaw(this.itemID, this.itemName, uuid);
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public void onYesAction(Context context, UserManager userManager) {
        if (this.dbMessage != null) {
            context.startActivity(InventoryActivity.makeSaveItemIntent(context, this.agentUUID, new InventorySaveInfo(InventorySaveInfo.InventorySaveType.InventoryOffer, this.itemID, getItemName(), null, this.assetType, this.dbMessage.getId().longValue())));
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setOrigIMType(Integer.valueOf(this.origIMType));
        chatMessage.setSessionID(this.sessionID);
        chatMessage.setItemID(this.itemID);
        chatMessage.setItemName(this.itemName);
        chatMessage.setAssetType(this.assetType != null ? Integer.valueOf(this.assetType.getTypeCode()) : null);
    }
}
