// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public class SLChatBalanceChangedEvent extends SLChatEvent
{

    private final int newBalance;
    private final int transactionAmount;
    private final boolean transactionAmountValid;

    public SLChatBalanceChangedEvent(ChatMessage chatmessage, UUID uuid)
    {
        int i = 0;
        super(chatmessage, uuid);
        boolean flag;
        if (chatmessage.getTransactionAmount() != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        transactionAmountValid = flag;
        if (chatmessage.getTransactionAmount() != null)
        {
            i = chatmessage.getTransactionAmount().intValue();
        }
        transactionAmount = i;
        newBalance = chatmessage.getNewBalance().intValue();
    }

    public SLChatBalanceChangedEvent(ChatMessageSource chatmessagesource, UUID uuid, boolean flag, int i, int j)
    {
        super(chatmessagesource, uuid);
        transactionAmountValid = flag;
        transactionAmount = i;
        newBalance = j;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.BalanceChanged;
    }

    public int getNewBalance()
    {
        return newBalance;
    }

    protected String getText(Context context, UserManager usermanager)
    {
        if (transactionAmountValid)
        {
            usermanager = source.getSourceName(usermanager);
            if (usermanager != null)
            {
                if (transactionAmount >= 0)
                {
                    return context.getString(0x7f090394, new Object[] {
                        Integer.valueOf(transactionAmount), Integer.valueOf(getNewBalance())
                    });
                } else
                {
                    return context.getString(0x7f090392, new Object[] {
                        Integer.valueOf(-transactionAmount), usermanager, Integer.valueOf(getNewBalance())
                    });
                }
            }
            if (transactionAmount >= 0)
            {
                return context.getString(0x7f090393, new Object[] {
                    Integer.valueOf(transactionAmount), Integer.valueOf(newBalance)
                });
            } else
            {
                return context.getString(0x7f090391, new Object[] {
                    Integer.valueOf(-transactionAmount), Integer.valueOf(newBalance)
                });
            }
        } else
        {
            return context.getString(0x7f090395, new Object[] {
                Integer.valueOf(newBalance)
            });
        }
    }

    public int getTransactionAmount()
    {
        return transactionAmount;
    }

    public boolean getTransactionAmountValid()
    {
        return transactionAmountValid;
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        boolean flag1 = false;
        if (transactionAmountValid)
        {
            boolean flag = flag1;
            if (source.getSourceName(usermanager) != null)
            {
                flag = flag1;
                if (getTransactionAmount() >= 0)
                {
                    flag = true;
                }
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public boolean opensNewChatter()
    {
        return false;
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        Integer integer;
        if (transactionAmountValid)
        {
            integer = Integer.valueOf(transactionAmount);
        } else
        {
            integer = null;
        }
        chatmessage.setTransactionAmount(integer);
        chatmessage.setNewBalance(Integer.valueOf(newBalance));
    }
}
