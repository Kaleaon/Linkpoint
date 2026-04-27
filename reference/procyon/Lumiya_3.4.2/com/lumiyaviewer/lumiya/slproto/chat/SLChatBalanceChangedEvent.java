// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public class SLChatBalanceChangedEvent extends SLChatEvent
{
    private final int newBalance;
    private final int transactionAmount;
    private final boolean transactionAmountValid;
    
    public SLChatBalanceChangedEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        int intValue = 0;
        super(chatMessage, uuid);
        this.transactionAmountValid = (chatMessage.getTransactionAmount() != null);
        if (chatMessage.getTransactionAmount() != null) {
            intValue = chatMessage.getTransactionAmount();
        }
        this.transactionAmount = intValue;
        this.newBalance = chatMessage.getNewBalance();
    }
    
    public SLChatBalanceChangedEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final boolean transactionAmountValid, final int transactionAmount, final int newBalance) {
        super(chatMessageSource, uuid);
        this.transactionAmountValid = transactionAmountValid;
        this.transactionAmount = transactionAmount;
        this.newBalance = newBalance;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.BalanceChanged;
    }
    
    public int getNewBalance() {
        return this.newBalance;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        if (!this.transactionAmountValid) {
            return context.getString(2131297173, new Object[] { this.newBalance });
        }
        final String sourceName = this.source.getSourceName(userManager);
        if (sourceName != null) {
            if (this.transactionAmount >= 0) {
                return context.getString(2131297172, new Object[] { this.transactionAmount, this.getNewBalance() });
            }
            return context.getString(2131297170, new Object[] { -this.transactionAmount, sourceName, this.getNewBalance() });
        }
        else {
            if (this.transactionAmount >= 0) {
                return context.getString(2131297171, new Object[] { this.transactionAmount, this.newBalance });
            }
            return context.getString(2131297169, new Object[] { -this.transactionAmount, this.newBalance });
        }
    }
    
    public int getTransactionAmount() {
        return this.transactionAmount;
    }
    
    public boolean getTransactionAmountValid() {
        return this.transactionAmountValid;
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_NORMAL;
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        final boolean b = false;
        if (this.transactionAmountValid) {
            boolean b2 = b;
            if (this.source.getSourceName(userManager) != null) {
                b2 = b;
                if (this.getTransactionAmount() >= 0) {
                    b2 = true;
                }
            }
            return b2;
        }
        return false;
    }
    
    @Override
    public boolean opensNewChatter() {
        return false;
    }
    
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        Integer value;
        if (this.transactionAmountValid) {
            value = this.transactionAmount;
        }
        else {
            value = null;
        }
        chatMessage.setTransactionAmount(value);
        chatMessage.setNewBalance(this.newBalance);
    }
}
