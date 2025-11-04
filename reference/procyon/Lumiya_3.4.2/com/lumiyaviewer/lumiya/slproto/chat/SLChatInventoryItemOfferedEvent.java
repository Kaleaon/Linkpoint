// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySaveInfo;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public class SLChatInventoryItemOfferedEvent extends SLChatYesNoEvent
{
    private final SLAssetType assetType;
    private final UUID itemID;
    private final String itemName;
    private final int origIMType;
    private final UUID sessionID;
    
    public SLChatInventoryItemOfferedEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.origIMType = chatMessage.getOrigIMType();
        this.sessionID = chatMessage.getSessionID();
        this.itemID = chatMessage.getItemID();
        this.itemName = chatMessage.getItemName();
        this.assetType = SLAssetType.getByType(chatMessage.getAssetType());
    }
    
    public SLChatInventoryItemOfferedEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message));
        this.itemName = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog;
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
        this.itemID = extractItemID(improvedInstantMessage);
        this.assetType = extractAssetType(improvedInstantMessage);
    }
    
    public SLChatInventoryItemOfferedEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage, final String itemName, final UUID itemID, final SLAssetType assetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, itemName);
        this.itemName = itemName;
        this.origIMType = improvedInstantMessage.MessageBlock_Field.Dialog;
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
        this.itemID = itemID;
        this.assetType = assetType;
    }
    
    protected static SLAssetType extractAssetType(final ImprovedInstantMessage improvedInstantMessage) {
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.length >= 1) {
            return SLAssetType.getByType(improvedInstantMessage.MessageBlock_Field.BinaryBucket[0]);
        }
        return SLAssetType.AT_UNKNOWN;
    }
    
    protected static UUID extractItemID(final ImprovedInstantMessage improvedInstantMessage) {
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.length >= 17) {
            final ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket);
            wrap.order(ByteOrder.BIG_ENDIAN);
            wrap.get();
            return new UUID(wrap.getLong(), wrap.getLong());
        }
        return null;
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
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.InventoryItemOffered;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131296609);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131296608);
    }
    
    public String getQuestion(final Context context) {
        return context.getString(2131296610);
    }
    
    @Override
    public String getText(final Context context, @Nonnull final UserManager userManager) {
        return context.getString(2131296430, new Object[] { this.itemName });
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131296611);
    }
    
    public String getYesMessage(final Context context) {
        return context.getString(2131296607);
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return true;
    }
    
    @Override
    protected void onNoAction(final Context context, final UserManager userManager) {
        super.onNoAction(context, userManager);
        final UUID sourceUUID = this.source.getSourceUUID();
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, false, sourceUUID, this.sessionID, null);
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.DeleteInventoryItemRaw(this.itemID);
            }
        }
    }
    
    public void onOfferAccepted(final Context context, final UserManager userManager, final UUID uuid) {
        super.onYesAction(context, userManager);
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            activeAgentCircuit.AcceptInventoryOffer(this.origIMType, true, this.source.getSourceUUID(), this.sessionID, uuid);
            if (this.itemID != null) {
                activeAgentCircuit.getModules().inventory.MoveInventoryItemRaw(this.itemID, this.itemName, uuid);
            }
        }
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        if (this.dbMessage != null) {
            context.startActivity(InventoryActivity.makeSaveItemIntent(context, this.agentUUID, new InventorySaveInfo(InventorySaveInfo.InventorySaveType.InventoryOffer, this.itemID, this.getItemName(), null, this.assetType, this.dbMessage.getId())));
        }
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        Integer value = null;
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setOrigIMType(this.origIMType);
        chatMessage.setSessionID(this.sessionID);
        chatMessage.setItemID(this.itemID);
        chatMessage.setItemName(this.itemName);
        if (this.assetType != null) {
            value = this.assetType.getTypeCode();
        }
        chatMessage.setAssetType(value);
    }
}
