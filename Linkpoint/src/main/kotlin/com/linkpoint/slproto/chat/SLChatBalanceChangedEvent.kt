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
        val i: Int = 0
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

     public fun getNewBalance(): Int {
        return this.newBalance
    }

    /* access modifiers changed from: protected */
     public fun getText(context: Context, userManager: UserManager): String {
        if (this.transactionAmountValid) {
            val sourceName: String = this.source.getSourceName(userManager)
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

     public fun getTransactionAmount(): Int {
        return this.transactionAmount
    }

     public fun getTransactionAmountValid(): Boolean {
        return this.transactionAmountValid
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
     public fun isActionMessage(userManager: UserManager): Boolean {
        return this.transactionAmountValid && this.source.getSourceName(userManager) != null && getTransactionAmount() >= 0
    }

     public fun opensNewChatter(): Boolean {
        return false
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setTransactionAmount(this.transactionAmountValid ? Integer.valueOf(this.transactionAmount) : null)
        chatMessage.setNewBalance(Integer.valueOf(this.newBalance))
    }
}
