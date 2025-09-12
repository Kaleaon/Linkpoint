// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySaveInfo;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class SLChatInventoryItemOfferedEvent extends SLChatYesNoEvent
{

    private final SLAssetType assetType;
    private final UUID itemID;
    private final String itemName;
    private final int origIMType;
    private final UUID sessionID;

    public SLChatInventoryItemOfferedEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        origIMType = chatmessage.getOrigIMType().intValue();
        sessionID = chatmessage.getSessionID();
        itemID = chatmessage.getItemID();
        itemName = chatmessage.getItemName();
        assetType = SLAssetType.getByType(chatmessage.getAssetType().intValue());
    }

    public SLChatInventoryItemOfferedEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, SLMessage.stringFromVariableUTF(improvedinstantmessage.MessageBlock_Field.Message));
        itemName = SLMessage.stringFromVariableUTF(improvedinstantmessage.MessageBlock_Field.Message);
        origIMType = improvedinstantmessage.MessageBlock_Field.Dialog;
        sessionID = improvedinstantmessage.MessageBlock_Field.ID;
        itemID = extractItemID(improvedinstantmessage);
        assetType = extractAssetType(improvedinstantmessage);
    }

    public SLChatInventoryItemOfferedEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage, String s, UUID uuid1, SLAssetType slassettype)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, s);
        itemName = s;
        origIMType = improvedinstantmessage.MessageBlock_Field.Dialog;
        sessionID = improvedinstantmessage.MessageBlock_Field.ID;
        itemID = uuid1;
        assetType = slassettype;
    }

    protected static SLAssetType extractAssetType(ImprovedInstantMessage improvedinstantmessage)
    {
        if (improvedinstantmessage.MessageBlock_Field.BinaryBucket.length >= 1)
        {
            return SLAssetType.getByType(improvedinstantmessage.MessageBlock_Field.BinaryBucket[0]);
        } else
        {
            return SLAssetType.AT_UNKNOWN;
        }
    }

    protected static UUID extractItemID(ImprovedInstantMessage improvedinstantmessage)
    {
        if (improvedinstantmessage.MessageBlock_Field.BinaryBucket.length >= 17)
        {
            improvedinstantmessage = ByteBuffer.wrap(improvedinstantmessage.MessageBlock_Field.BinaryBucket);
            improvedinstantmessage.order(ByteOrder.BIG_ENDIAN);
            improvedinstantmessage.get();
            return new UUID(improvedinstantmessage.getLong(), improvedinstantmessage.getLong());
        } else
        {
            return null;
        }
    }

    public SLAssetType getAssetType()
    {
        return assetType;
    }

    public UUID getItemID()
    {
        return itemID;
    }

    public String getItemName()
    {
        return itemName;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.InventoryItemOffered;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f090161);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f090160);
    }

    public String getQuestion(Context context)
    {
        return context.getString(0x7f090162);
    }

    public String getText(Context context, UserManager usermanager)
    {
        return context.getString(0x7f0900ae, new Object[] {
            itemName
        });
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f090163);
    }

    public String getYesMessage(Context context)
    {
        return context.getString(0x7f09015f);
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return true;
    }

    protected void onNoAction(Context context, UserManager usermanager)
    {
        super.onNoAction(context, usermanager);
        context = source.getSourceUUID();
        usermanager = usermanager.getActiveAgentCircuit();
        if (context != null && usermanager != null)
        {
            usermanager.AcceptInventoryOffer(origIMType, false, context, sessionID, null);
            if (itemID != null)
            {
                usermanager.getModules().inventory.DeleteInventoryItemRaw(itemID);
            }
        }
    }

    public void onOfferAccepted(Context context, UserManager usermanager, UUID uuid)
    {
        super.onYesAction(context, usermanager);
        context = usermanager.getActiveAgentCircuit();
        if (context != null)
        {
            context.AcceptInventoryOffer(origIMType, true, source.getSourceUUID(), sessionID, uuid);
            if (itemID != null)
            {
                context.getModules().inventory.MoveInventoryItemRaw(itemID, itemName, uuid);
            }
        }
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        if (dbMessage != null)
        {
            usermanager = new InventorySaveInfo(com.lumiyaviewer.lumiya.ui.inventory.InventorySaveInfo.InventorySaveType.InventoryOffer, itemID, getItemName(), null, assetType, dbMessage.getId().longValue());
            context.startActivity(InventoryActivity.makeSaveItemIntent(context, agentUUID, usermanager));
        }
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        Integer integer = null;
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setOrigIMType(Integer.valueOf(origIMType));
        chatmessage.setSessionID(sessionID);
        chatmessage.setItemID(itemID);
        chatmessage.setItemName(itemName);
        if (assetType != null)
        {
            integer = Integer.valueOf(assetType.getTypeCode());
        }
        chatmessage.setAssetType(integer);
    }
}
