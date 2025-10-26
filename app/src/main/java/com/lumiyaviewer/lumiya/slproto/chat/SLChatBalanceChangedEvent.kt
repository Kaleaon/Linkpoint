package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

class SLChatBalanceChangedEvent : SLChatEvent {
    private Int newBalance
    private Int transactionAmount
    private Boolean transactionAmountValid

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLChatBalanceChangedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid)
        Int i = 0
        this.transactionAmountValid = chatMessage.getTransactionAmount() != null
        this.transactionAmount = chatMessage.getTransactionAmount() != null ? chatMessage.getTransactionAmount().intValue() : i
        this.newBalance = chatMessage.getNewBalance().intValue()
    }

    SLChatBalanceChangedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, Boolean z, Int i, Int i2) {
        super(chatMessageSource, uuid)
        this.transactionAmountValid = z
        this.transactionAmount = i
        this.newBalance = i2
    }

    /* access modifiers changed from: protected */
    @Nonnull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.BalanceChanged
    }

    Int getNewBalance() {
        return this.newBalance
    }

    /* access modifiers changed from: protected */
    String getText(Context context, @Nonnull UserManager userManager) {
        if (this.transactionAmountValid) {
            String sourceName = this.source.getSourceName(userManager)
            if (sourceName != null) {
                if (this.transactionAmount >= 0) {
                    return context.getString(R.string.you_were_paid_by_agent, Array<Any>{Integer.valueOf(this.transactionAmount), Integer.valueOf(getNewBalance())})
                }
                return context.getString(R.string.you_have_paid_to_agent, Array<Any>{Integer.valueOf(-this.transactionAmount), sourceName, Integer.valueOf(getNewBalance())})
            } else if (this.transactionAmount >= 0) {
                return context.getString(R.string.you_were_paid, Array<Any>{Integer.valueOf(this.transactionAmount), Integer.valueOf(this.newBalance)})
            } else {
                return context.getString(R.string.you_have_paid, Array<Any>{Integer.valueOf(-this.transactionAmount), Integer.valueOf(this.newBalance)})
            }
        } else {
            return context.getString(R.string.your_account_balance_is_now, Array<Any>{Integer.valueOf(this.newBalance)})
        }
    }

    Int getTransactionAmount() {
        return this.transactionAmount
    }

    Boolean getTransactionAmountValid() {
        return this.transactionAmountValid
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    Boolean isActionMessage(@Nonnull UserManager userManager) {
        return this.transactionAmountValid && this.source.getSourceName(userManager) != null && getTransactionAmount() >= 0
    }

    Boolean opensNewChatter() {
        return false
    }

    Unit serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setTransactionAmount(this.transactionAmountValid ? Integer.valueOf(this.transactionAmount) : null)
        chatMessage.setNewBalance(Integer.valueOf(this.newBalance))
    }
}
