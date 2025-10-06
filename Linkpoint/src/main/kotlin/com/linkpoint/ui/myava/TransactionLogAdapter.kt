package com.linkpoint.ui.myava

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.dao.MoneyTransaction
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.common.ChatterNameDisplayer
import de.greenrobot.dao.query.LazyList
import java.util.Calendar
import java.util.UUID
import javax.annotation.Nullable

class TransactionLogAdapter : RecyclerView().Adapter<TransactionViewHolder> {
    /* access modifiers changed from: private */
    val UUID agentUUID
    /* access modifiers changed from: private */
    val Context context
    private LazyList<MoneyTransaction> data
    private val LayoutInflater inflater
    /* access modifiers changed from: private */
    val OnTransactionClickListener onTransactionClickListener

    interface OnTransactionClickListener {
        Unit onTransactionClicked(MoneyTransaction moneyTransaction)
    }

    class TransactionViewHolder : RecyclerView().ViewHolder : View.OnClickListener {
        @BindView(2131755679)
        TextView amountTextView
        private Calendar calendar
        private val ChatterNameDisplayer chatterNameDisplayer = ChatterNameDisplayer()
        @BindView(2131755680)
        TextView finalBalanceTextView
        private MoneyTransaction moneyTransaction
        @BindView(2131755677)
        TextView timestampTextView
        @BindView(2131755678)
        TextView userName
        @BindView(2131755327)
        ChatterPicView userPicView

        TransactionViewHolder(View view) {
            super(view)
            ButterKnife.bind((Object) this, view)
            this.chatterNameDisplayer.bindViews(this.userName, this.userPicView)
            view.setOnClickListener(this)
            this.calendar = Calendar.getInstance()
        }

        /* access modifiers changed from: package-private */
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        fun bindToData(MoneyTransaction moneyTransaction2) {
            this.moneyTransaction = moneyTransaction2
            this.chatterNameDisplayer.setChatterID(ChatterID.getUserChatterID(TransactionLogAdapter.this.agentUUID, moneyTransaction2.getAgentUUID()))
            this.amountTextView.setText(TransactionLogAdapter.this.context.getString(R.string.transaction_amount_format, Object[]{Integer.valueOf(moneyTransaction2.getTransactionAmount())}))
            this.finalBalanceTextView.setText(TransactionLogAdapter.this.context.getString(R.string.transaction_balance_amount, Object[]{Integer.valueOf(moneyTransaction2.getNewBalance())}))
            this.calendar.setTime(moneyTransaction2.getTimestamp())
            this.timestampTextView.setText(DateUtils.getRelativeTimeSpanString(TransactionLogAdapter.this.context, this.calendar.getTimeInMillis(), false))
        }

        fun onClick(View view) {
            if (TransactionLogAdapter.this.onTransactionClickListener != null && this.moneyTransaction != null) {
                TransactionLogAdapter.this.onTransactionClickListener.onTransactionClicked(this.moneyTransaction)
            }
        }

        /* access modifiers changed from: package-private */
        fun onRecycled() {
            this.chatterNameDisplayer.setChatterID((ChatterID) null)
            this.moneyTransaction = null
        }
    }

    class TransactionViewHolder_ViewBinding : Unbinder {
        private TransactionViewHolder target

        @UiThread
        public TransactionViewHolder_ViewBinding(TransactionViewHolder transactionViewHolder, View view) {
            this.target = transactionViewHolder
            transactionViewHolder.userName = (TextView) Utils.findRequiredViewAsType(view, R.id.user_name, "field 'userName'", TextView.class)
            transactionViewHolder.userPicView = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.userPicView, "field 'userPicView'", ChatterPicView.class)
            transactionViewHolder.timestampTextView = (TextView) Utils.findRequiredViewAsType(view, R.id.timeStampTextView, "field 'timestampTextView'", TextView.class)
            transactionViewHolder.amountTextView = (TextView) Utils.findRequiredViewAsType(view, R.id.amountTextView, "field 'amountTextView'", TextView.class)
            transactionViewHolder.finalBalanceTextView = (TextView) Utils.findRequiredViewAsType(view, R.id.finalBalanceTextView, "field 'finalBalanceTextView'", TextView.class)
        }

        @CallSuper
        fun unbind() {
            TransactionViewHolder transactionViewHolder = this.target
            if (transactionViewHolder == null) {
                throw IllegalStateException("Bindings already cleared.")
            }
            this.target = null
            transactionViewHolder.userName = null
            transactionViewHolder.userPicView = null
            transactionViewHolder.timestampTextView = null
            transactionViewHolder.amountTextView = null
            transactionViewHolder.finalBalanceTextView = null
        }
    }

    TransactionLogAdapter(Context context2, UUID uuid, OnTransactionClickListener onTransactionClickListener2) {
        this.context = context2
        this.agentUUID = uuid
        this.inflater = LayoutInflater.from(context2)
        this.onTransactionClickListener = onTransactionClickListener2
        setHasStableIds(true)
    }

    public Int getItemCount() {
        if (this.data != null) {
            return this.data.size()
        }
        return 0
    }

    public Long getItemId(Int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return -1
        }
        return this.data.get(i).getId().longValue()
    }

    fun onBindViewHolder(TransactionViewHolder transactionViewHolder, Int i) {
        if (this.data != null && i >= 0 && i < this.data.size()) {
            transactionViewHolder.bindToData(this.data.get(i))
        }
    }

    public TransactionViewHolder onCreateViewHolder(ViewGroup viewGroup, Int i) {
        return TransactionViewHolder(this.inflater.inflate(R.layout.transaction_log_item, viewGroup, false))
    }

    fun onViewRecycled(TransactionViewHolder transactionViewHolder) {
        transactionViewHolder.onRecycled()
    }

    fun setData(LazyList<MoneyTransaction> lazyList) {
        this.data = lazyList
        notifyDataSetChanged()
    }
}
