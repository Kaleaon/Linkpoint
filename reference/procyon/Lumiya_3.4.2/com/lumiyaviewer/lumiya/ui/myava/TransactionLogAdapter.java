// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import butterknife.ButterKnife;
import android.view.View;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import java.util.Calendar;
import butterknife.BindView;
import android.widget.TextView;
import android.view.View$OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import de.greenrobot.dao.query.LazyList;
import android.content.Context;
import java.util.UUID;
import android.support.v7.widget.RecyclerView;

public class TransactionLogAdapter extends Adapter<TransactionViewHolder>
{
    private final UUID agentUUID;
    private final Context context;
    @Nullable
    private LazyList<MoneyTransaction> data;
    private final LayoutInflater inflater;
    private final OnTransactionClickListener onTransactionClickListener;
    
    TransactionLogAdapter(final Context context, final UUID agentUUID, final OnTransactionClickListener onTransactionClickListener) {
        this.context = context;
        this.agentUUID = agentUUID;
        this.inflater = LayoutInflater.from(context);
        this.onTransactionClickListener = onTransactionClickListener;
        ((RecyclerView.Adapter)this).setHasStableIds(true);
    }
    
    @Override
    public int getItemCount() {
        int size;
        if (this.data != null) {
            size = this.data.size();
        }
        else {
            size = 0;
        }
        return size;
    }
    
    @Override
    public long getItemId(final int n) {
        if (this.data != null && n >= 0 && n < this.data.size()) {
            return this.data.get(n).getId();
        }
        return -1L;
    }
    
    public void onBindViewHolder(final TransactionViewHolder transactionViewHolder, final int n) {
        if (this.data != null && n >= 0 && n < this.data.size()) {
            transactionViewHolder.bindToData(this.data.get(n));
        }
    }
    
    public TransactionViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new TransactionViewHolder(this.inflater.inflate(2130968752, viewGroup, false));
    }
    
    public void onViewRecycled(final TransactionViewHolder transactionViewHolder) {
        transactionViewHolder.onRecycled();
    }
    
    public void setData(@Nullable final LazyList<MoneyTransaction> data) {
        this.data = data;
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    interface OnTransactionClickListener
    {
        void onTransactionClicked(final MoneyTransaction p0);
    }
    
    class TransactionViewHolder extends ViewHolder implements View$OnClickListener
    {
        @BindView(2131755679)
        TextView amountTextView;
        private Calendar calendar;
        private final ChatterNameDisplayer chatterNameDisplayer;
        @BindView(2131755680)
        TextView finalBalanceTextView;
        private MoneyTransaction moneyTransaction;
        @BindView(2131755677)
        TextView timestampTextView;
        @BindView(2131755678)
        TextView userName;
        @BindView(2131755327)
        ChatterPicView userPicView;
        
        TransactionViewHolder(final View view) {
            super(view);
            this.chatterNameDisplayer = new ChatterNameDisplayer();
            ButterKnife.bind(this, view);
            this.chatterNameDisplayer.bindViews(this.userName, this.userPicView);
            view.setOnClickListener((View$OnClickListener)this);
            this.calendar = Calendar.getInstance();
        }
        
        @SuppressLint({ "DefaultLocale", "SetTextI18n" })
        void bindToData(final MoneyTransaction moneyTransaction) {
            this.moneyTransaction = moneyTransaction;
            this.chatterNameDisplayer.setChatterID(ChatterID.getUserChatterID(TransactionLogAdapter.this.agentUUID, moneyTransaction.getAgentUUID()));
            this.amountTextView.setText((CharSequence)TransactionLogAdapter.this.context.getString(2131297113, new Object[] { moneyTransaction.getTransactionAmount() }));
            this.finalBalanceTextView.setText((CharSequence)TransactionLogAdapter.this.context.getString(2131297116, new Object[] { moneyTransaction.getNewBalance() }));
            this.calendar.setTime(moneyTransaction.getTimestamp());
            this.timestampTextView.setText(DateUtils.getRelativeTimeSpanString(TransactionLogAdapter.this.context, this.calendar.getTimeInMillis(), false));
        }
        
        public void onClick(final View view) {
            if (TransactionLogAdapter.this.onTransactionClickListener != null && this.moneyTransaction != null) {
                TransactionLogAdapter.this.onTransactionClickListener.onTransactionClicked(this.moneyTransaction);
            }
        }
        
        void onRecycled() {
            this.chatterNameDisplayer.setChatterID(null);
            this.moneyTransaction = null;
        }
    }
}
