package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

class SLChatBalanceChangedEvent : SLChatEvent() {
    private val Int newBalance
    private val Int transactionAmount
    private val Boolean transactionAmountValid

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLChatBalanceChangedEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        Int i = 0
        this.transactionAmountValid = chatMessage.getTransactionAmount() != null
        this.transactionAmount = chatMessage.getTransactionAmount() != null ? chatMessage.getTransactionAmount().intValue() : i
        this.newBalance = chatMessage.getNewBalance().intValue()
    }

    public SLChatBalanceChangedEvent(ChatMessageSource chatMessageSource, UUID uuid, Boolean z, Int i, Int i2) {
        super(chatMessageSource, uuid)
        this.transactionAmountValid = z
        this.transactionAmount = i
        this.newBalance = i2
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.BalanceChanged
    }

    public Int getNewBalance() {
        return this.newBalance
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, UserManager userManager) {
        if (this.transactionAmountValid) {
            String sourceName = this.source.getSourceName(userManager)
            if (sourceName != null) {
                if (this.transactionAmount >= 0) {
                    return context.getString(R.string.you_were_paid_by_agent, Object[]{Integer.valueOf(this.transactionAmount), Integer.valueOf(getNewBalance())})
                }
                return context.getString(R.string.you_have_paid_to_agent, Object[]{Integer.valueOf(-this.transactionAmount), sourceName, Integer.valueOf(getNewBalance())})
            } else if (this.transactionAmount >= 0) {
                return context.getString(R.string.you_were_paid, Object[]{Integer.valueOf(this.transactionAmount), Integer.valueOf(this.newBalance)})
            } else {
                return context.getString(R.string.you_have_paid, Object[]{Integer.valueOf(-this.transactionAmount), Integer.valueOf(this.newBalance)})
            }
        } else {
            return context.getString(R.string.your_account_balance_is_now, Object[]{Integer.valueOf(this.newBalance)})
        }
    }

    public Int getTransactionAmount() {
        return this.transactionAmount
    }

    public Boolean getTransactionAmountValid() {
        return this.transactionAmountValid
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    public Boolean isActionMessage(UserManager userManager) {
        return this.transactionAmountValid && this.source.getSourceName(userManager) != null && getTransactionAmount() >= 0
    }

    public Boolean opensNewChatter() {
        return false
    }

    fun serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setTransactionAmount(this.transactionAmountValid ? Integer.valueOf(this.transactionAmount) : null)
        chatMessage.setNewBalance(Integer.valueOf(this.newBalance))
    }
}
