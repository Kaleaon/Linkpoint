package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class SLChatBalanceChangedEvent extends SLChatEvent {
    private final int newBalance;
    private final int transactionAmount;
    private final boolean transactionAmountValid;

    public SLChatBalanceChangedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.transactionAmountValid = chatMessage.getTransactionAmount() != null;
        this.transactionAmount = chatMessage.getTransactionAmount() != null ? chatMessage.getTransactionAmount().intValue() : 0;
        this.newBalance = chatMessage.getNewBalance().intValue();
    }

    public SLChatBalanceChangedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, boolean z, int i, int i2) {
        super(chatMessageSource, uuid);
        this.transactionAmountValid = z;
        this.transactionAmount = i;
        this.newBalance = i2;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.BalanceChanged;
    }

    public int getNewBalance() {
        return this.newBalance;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected String getText(Context context, @Nonnull UserManager userManager) {
        if (!this.transactionAmountValid) {
            return context.getString(R.string.your_account_balance_is_now, Integer.valueOf(this.newBalance));
        }
        String sourceName = this.source.getSourceName(userManager);
        return sourceName != null ? this.transactionAmount >= 0 ? context.getString(R.string.you_were_paid_by_agent, Integer.valueOf(this.transactionAmount), Integer.valueOf(getNewBalance())) : context.getString(R.string.you_have_paid_to_agent, Integer.valueOf(-this.transactionAmount), sourceName, Integer.valueOf(getNewBalance())) : this.transactionAmount >= 0 ? context.getString(R.string.you_were_paid, Integer.valueOf(this.transactionAmount), Integer.valueOf(this.newBalance)) : context.getString(R.string.you_have_paid, Integer.valueOf(-this.transactionAmount), Integer.valueOf(this.newBalance));
    }

    public int getTransactionAmount() {
        return this.transactionAmount;
    }

    public boolean getTransactionAmountValid() {
        return this.transactionAmountValid;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected boolean isActionMessage(@Nonnull UserManager userManager) {
        return this.transactionAmountValid && this.source.getSourceName(userManager) != null && getTransactionAmount() >= 0;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public boolean opensNewChatter() {
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setTransactionAmount(this.transactionAmountValid ? Integer.valueOf(this.transactionAmount) : null);
        chatMessage.setNewBalance(Integer.valueOf(this.newBalance));
    }
}
