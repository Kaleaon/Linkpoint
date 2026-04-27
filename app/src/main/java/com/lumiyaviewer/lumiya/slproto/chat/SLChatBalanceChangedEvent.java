package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SLChatBalanceChangedEvent extends SLChatEvent {
    private final int newBalance;
    private final int transactionAmount;
    private final boolean transactionAmountValid;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLChatBalanceChangedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        int i = 0;
        this.transactionAmountValid = chatMessage.getTransactionAmount() != null;
        this.transactionAmount = chatMessage.getTransactionAmount() != null ? chatMessage.getTransactionAmount().intValue() : i;
        this.newBalance = chatMessage.getNewBalance().intValue();
    }

    public SLChatBalanceChangedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, boolean z, int i, int i2) {
        super(chatMessageSource, uuid);
        this.transactionAmountValid = z;
        this.transactionAmount = i;
        this.newBalance = i2;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.BalanceChanged;
    }

    public int getNewBalance() {
        return this.newBalance;
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, @Nonnull UserManager userManager) {
        if (this.transactionAmountValid) {
            String sourceName = this.source.getSourceName(userManager);
            if (sourceName != null) {
                if (this.transactionAmount >= 0) {
                    return context.getString(R.string.you_were_paid_by_agent, new Object[]{Integer.valueOf(this.transactionAmount), Integer.valueOf(getNewBalance())});
                }
                return context.getString(R.string.you_have_paid_to_agent, new Object[]{Integer.valueOf(-this.transactionAmount), sourceName, Integer.valueOf(getNewBalance())});
            } else if (this.transactionAmount >= 0) {
                return context.getString(R.string.you_were_paid, new Object[]{Integer.valueOf(this.transactionAmount), Integer.valueOf(this.newBalance)});
            } else {
                return context.getString(R.string.you_have_paid, new Object[]{Integer.valueOf(-this.transactionAmount), Integer.valueOf(this.newBalance)});
            }
        } else {
            return context.getString(R.string.your_account_balance_is_now, new Object[]{Integer.valueOf(this.newBalance)});
        }
    }

    public int getTransactionAmount() {
        return this.transactionAmount;
    }

    public boolean getTransactionAmountValid() {
        return this.transactionAmountValid;
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    /* access modifiers changed from: protected */
    public boolean isActionMessage(@Nonnull UserManager userManager) {
        return this.transactionAmountValid && this.source.getSourceName(userManager) != null && getTransactionAmount() >= 0;
    }

    public boolean opensNewChatter() {
        return false;
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setTransactionAmount(this.transactionAmountValid ? Integer.valueOf(this.transactionAmount) : null);
        chatMessage.setNewBalance(Integer.valueOf(this.newBalance));
    }
}
