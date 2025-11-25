package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

class SLChatBalanceChangedEvent : SLChatEvent {
    private val newBalance: Int
    private val transactionAmount: Int
    private val transactionAmountValid: Boolean

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.transactionAmountValid = chatMessage.transactionAmount != null
        this.transactionAmount = chatMessage.transactionAmount ?: 0
        this.newBalance = chatMessage.newBalance ?: 0
    }

    constructor(source: ChatMessageSource, uuid: UUID, valid: Boolean, amount: Int, balance: Int) : super(source, uuid) {
        this.transactionAmountValid = valid
        this.transactionAmount = amount
        this.newBalance = balance
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.BalanceChanged
    }

    fun getNewBalance(): Int {
        return newBalance
    }

    override fun getText(context: Context, userManager: UserManager): String {
        if (transactionAmountValid) {
            val sourceName = source.getSourceName(userManager)
            if (sourceName != null) {
                return if (transactionAmount >= 0) {
                    context.getString(R.string.you_were_paid_by_agent, transactionAmount, newBalance)
                } else {
                    context.getString(R.string.you_have_paid_to_agent, -transactionAmount, sourceName, newBalance)
                }
            } else if (transactionAmount >= 0) {
                return context.getString(R.string.you_were_paid, transactionAmount, newBalance)
            } else {
                return context.getString(R.string.you_have_paid, -transactionAmount, newBalance)
            }
        } else {
            return context.getString(R.string.your_account_balance_is_now, newBalance)
        }
    }

    fun getTransactionAmount(): Int {
        return transactionAmount
    }

    fun getTransactionAmountValid(): Boolean {
        return transactionAmountValid
    }

    override fun getViewType(): ChatMessageViewType {
        return ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    override fun isActionMessage(userManager: UserManager): Boolean {
        return transactionAmountValid && source.getSourceName(userManager) != null && getTransactionAmount() >= 0
    }

    override fun opensNewChatter(): Boolean {
        return false
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.transactionAmount = if (transactionAmountValid) transactionAmount else null
        chatMessage.newBalance = newBalance
    }
}
