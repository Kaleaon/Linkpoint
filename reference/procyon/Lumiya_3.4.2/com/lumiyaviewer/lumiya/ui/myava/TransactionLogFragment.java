// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import android.view.MenuItem;
import butterknife.ButterKnife;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.support.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import java.util.UUID;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import butterknife.Unbinder;
import android.support.v7.widget.RecyclerView;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.os.Handler;
import butterknife.BindView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class TransactionLogFragment extends FragmentWithTitle implements OnLoadableDataChangedListener, OnTransactionClickListener
{
    private TransactionLogAdapter adapter;
    private final LoadableMonitor loadableMonitor;
    @BindView(2131755197)
    LoadingLayout loadingLayout;
    private final Handler mHandler;
    private final SubscriptionData<SubscriptionSingleKey, LazyList<MoneyTransaction>> moneyTransactions;
    private final Runnable scrollToBottomRunnable;
    private boolean scrollToBottomRunnablePosted;
    @BindView(2131755676)
    RecyclerView transactionLogView;
    private Unbinder unbinder;
    
    public TransactionLogFragment() {
        this.moneyTransactions = new SubscriptionData<SubscriptionSingleKey, LazyList<MoneyTransaction>>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.moneyTransactions }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.scrollToBottomRunnablePosted = false;
        this.mHandler = new Handler();
        this.scrollToBottomRunnable = new Runnable() {
            @Override
            public void run() {
                TransactionLogFragment.this.scrollToBottomRunnablePosted = false;
                if (TransactionLogFragment.this.unbinder != null) {
                    final RecyclerView transactionLogView = TransactionLogFragment.this.transactionLogView;
                    if (!transactionLogView.hasPendingAdapterUpdates()) {
                        if (TransactionLogFragment.this.adapter != null) {
                            final int itemCount = TransactionLogFragment.this.adapter.getItemCount();
                            if (itemCount > 0) {
                                transactionLogView.scrollToPosition(itemCount - 1);
                            }
                        }
                    }
                    else {
                        TransactionLogFragment.this.scrollToBottomRunnablePosted = true;
                        TransactionLogFragment.this.mHandler.post(TransactionLogFragment.this.scrollToBottomRunnable);
                    }
                }
            }
        };
    }
    
    private void clearTransactionLog() {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this.getActivity());
        alertDialog$Builder.setMessage(2131296446).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$N_xrT8AwWQ2OjPw50fSCa4Lhb58$1(this)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$N_xrT8AwWQ2OjPw50fSCa4Lhb58());
        alertDialog$Builder.create().show();
    }
    
    public static Bundle makeSelection(final UUID uuid) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }
    
    private void performClearTransactionLog() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            userManager.getBalanceManager().clearMoneyTransactions();
        }
    }
    
    private void scrollToBottom() {
        if (!this.scrollToBottomRunnablePosted) {
            this.scrollToBottomRunnablePosted = true;
            this.mHandler.post(this.scrollToBottomRunnable);
        }
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886113, menu);
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968751, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.adapter = new TransactionLogAdapter(this.getContext(), ActivityUtils.getActiveAgentID(this.getArguments()), (TransactionLogAdapter.OnTransactionClickListener)this);
        this.transactionLogView.setAdapter((RecyclerView.Adapter)this.adapter);
        this.loadableMonitor.setLoadingLayout(this.loadingLayout, null, this.getString(2131296425));
        return inflate;
    }
    
    @Override
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    @Override
    public void onLoadableDataChanged() {
        final LazyList data = this.moneyTransactions.getData();
        if (data != null) {
            this.loadableMonitor.setEmptyMessage(data.isEmpty(), this.getString(2131296752));
            if (this.adapter != null) {
                this.adapter.setData(data);
                this.scrollToBottom();
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755859: {
                this.clearTransactionLog();
                return true;
            }
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            this.moneyTransactions.subscribe(userManager.getBalanceManager().moneyTransactions(), SubscriptionSingleKey.Value);
        }
    }
    
    @Override
    public void onStop() {
        this.loadableMonitor.unsubscribeAll();
        super.onStop();
    }
    
    @Override
    public void onTransactionClicked(final MoneyTransaction moneyTransaction) {
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getArguments());
        if (activeAgentID != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, moneyTransaction.getAgentUUID())));
        }
    }
}
