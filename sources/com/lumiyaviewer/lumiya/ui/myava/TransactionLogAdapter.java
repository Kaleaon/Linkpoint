// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import de.greenrobot.dao.query.LazyList;
import java.util.Calendar;
import java.util.UUID;

public class TransactionLogAdapter extends android.support.v7.widget.RecyclerView.Adapter
{
    static interface OnTransactionClickListener
    {

        public abstract void onTransactionClicked(MoneyTransaction moneytransaction);
    }

    class TransactionViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
        implements android.view.View.OnClickListener
    {

        TextView amountTextView;
        private Calendar calendar;
        private final ChatterNameDisplayer chatterNameDisplayer = new ChatterNameDisplayer();
        TextView finalBalanceTextView;
        private MoneyTransaction moneyTransaction;
        final TransactionLogAdapter this$0;
        TextView timestampTextView;
        TextView userName;
        ChatterPicView userPicView;

        void bindToData(MoneyTransaction moneytransaction)
        {
            moneyTransaction = moneytransaction;
            chatterNameDisplayer.setChatterID(ChatterID.getUserChatterID(TransactionLogAdapter._2D_get0(TransactionLogAdapter.this), moneytransaction.getAgentUUID()));
            amountTextView.setText(TransactionLogAdapter._2D_get1(TransactionLogAdapter.this).getString(0x7f090359, new Object[] {
                Integer.valueOf(moneytransaction.getTransactionAmount())
            }));
            finalBalanceTextView.setText(TransactionLogAdapter._2D_get1(TransactionLogAdapter.this).getString(0x7f09035c, new Object[] {
                Integer.valueOf(moneytransaction.getNewBalance())
            }));
            calendar.setTime(moneytransaction.getTimestamp());
            timestampTextView.setText(DateUtils.getRelativeTimeSpanString(TransactionLogAdapter._2D_get1(TransactionLogAdapter.this), calendar.getTimeInMillis(), false));
        }

        public void onClick(View view)
        {
            if (TransactionLogAdapter._2D_get2(TransactionLogAdapter.this) != null && moneyTransaction != null)
            {
                TransactionLogAdapter._2D_get2(TransactionLogAdapter.this).onTransactionClicked(moneyTransaction);
            }
        }

        void onRecycled()
        {
            chatterNameDisplayer.setChatterID(null);
            moneyTransaction = null;
        }

        TransactionViewHolder(View view)
        {
            this$0 = TransactionLogAdapter.this;
            super(view);
            ButterKnife.bind(this, view);
            chatterNameDisplayer.bindViews(userName, userPicView);
            view.setOnClickListener(this);
            calendar = Calendar.getInstance();
        }
    }


    private final UUID agentUUID;
    private final Context context;
    private LazyList data;
    private final LayoutInflater inflater;
    private final OnTransactionClickListener onTransactionClickListener;

    static UUID _2D_get0(TransactionLogAdapter transactionlogadapter)
    {
        return transactionlogadapter.agentUUID;
    }

    static Context _2D_get1(TransactionLogAdapter transactionlogadapter)
    {
        return transactionlogadapter.context;
    }

    static OnTransactionClickListener _2D_get2(TransactionLogAdapter transactionlogadapter)
    {
        return transactionlogadapter.onTransactionClickListener;
    }

    TransactionLogAdapter(Context context1, UUID uuid, OnTransactionClickListener ontransactionclicklistener)
    {
        context = context1;
        agentUUID = uuid;
        inflater = LayoutInflater.from(context1);
        onTransactionClickListener = ontransactionclicklistener;
        setHasStableIds(true);
    }

    public int getItemCount()
    {
        if (data != null)
        {
            return data.size();
        } else
        {
            return 0;
        }
    }

    public long getItemId(int i)
    {
        if (data != null && i >= 0 && i < data.size())
        {
            return ((MoneyTransaction)data.get(i)).getId().longValue();
        } else
        {
            return -1L;
        }
    }

    public volatile void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
        onBindViewHolder((TransactionViewHolder)viewholder, i);
    }

    public void onBindViewHolder(TransactionViewHolder transactionviewholder, int i)
    {
        if (data != null && i >= 0 && i < data.size())
        {
            transactionviewholder.bindToData((MoneyTransaction)data.get(i));
        }
    }

    public volatile android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return onCreateViewHolder(viewgroup, i);
    }

    public TransactionViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return new TransactionViewHolder(inflater.inflate(0x7f0400b0, viewgroup, false));
    }

    public volatile void onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        onViewRecycled((TransactionViewHolder)viewholder);
    }

    public void onViewRecycled(TransactionViewHolder transactionviewholder)
    {
        transactionviewholder.onRecycled();
    }

    public void setData(LazyList lazylist)
    {
        data = lazylist;
        notifyDataSetChanged();
    }
}
