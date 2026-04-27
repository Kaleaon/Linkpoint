// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.DisposeHandler;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import de.greenrobot.dao.Property;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.dao.MoneyTransactionDao;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;

public class BalanceManager
{
    private final SubscriptionPool<SubscriptionSingleKey, Integer> balancePool;
    private final SimpleRequestHandler<SubscriptionSingleKey> balanceRequestHandler;
    private final AtomicReference<SLFinancialInfo> financialInfo;
    private final MoneyTransactionDao moneyTransactionDao;
    private final SubscriptionPool<SubscriptionSingleKey, LazyList<MoneyTransaction>> moneyTransactionPool;
    private final Runnable requestBalanceRunnable;
    private final UserManager userManager;
    
    BalanceManager(final UserManager userManager) {
        this.financialInfo = new AtomicReference<SLFinancialInfo>(null);
        this.moneyTransactionPool = new SubscriptionPool<SubscriptionSingleKey, LazyList<MoneyTransaction>>();
        this.balanceRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                final SLFinancialInfo slFinancialInfo = BalanceManager.this.financialInfo.get();
                if (slFinancialInfo != null) {
                    if (slFinancialInfo.getBalanceKnown()) {
                        BalanceManager.this.balancePool.onResultData(SubscriptionSingleKey.Value, slFinancialInfo.getBalance());
                    }
                    else {
                        final SLAgentCircuit activeAgentCircuit = BalanceManager.this.userManager.getActiveAgentCircuit();
                        if (activeAgentCircuit != null) {
                            activeAgentCircuit.execute(BalanceManager.this.requestBalanceRunnable);
                        }
                        else {
                            BalanceManager.this.balancePool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
                        }
                    }
                }
                else {
                    BalanceManager.this.balancePool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
                }
            }
        };
        this.requestBalanceRunnable = new Runnable() {
            @Override
            public void run() {
                final SLFinancialInfo slFinancialInfo = BalanceManager.this.financialInfo.get();
                if (slFinancialInfo != null) {
                    slFinancialInfo.AskForMoneyBalance();
                }
                else {
                    BalanceManager.this.balancePool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
                }
            }
        };
        this.balancePool = new SubscriptionPool<SubscriptionSingleKey, Integer>();
        this.userManager = userManager;
        this.moneyTransactionDao = userManager.getDaoSession().getMoneyTransactionDao();
        this.balancePool.attachRequestHandler(this.balanceRequestHandler);
        this.moneyTransactionPool.attachRequestHandler(new AsyncRequestHandler<SubscriptionSingleKey>(userManager.getDatabaseExecutor(), new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                BalanceManager.this.moneyTransactionPool.onResultData(subscriptionSingleKey, ((AbstractDao<MoneyTransaction, K>)BalanceManager.this.moneyTransactionDao).queryBuilder().orderAsc(MoneyTransactionDao.Properties.Timestamp).listLazy());
            }
        }));
        this.moneyTransactionPool.setDisposeHandler(new _$Lambda$xo_DO1h0hLJizWUYkWN5MuOY_xk(), userManager.getDatabaseExecutor());
    }
    
    public void clearFinancialInfo(final SLFinancialInfo expectedValue) {
        this.financialInfo.compareAndSet(expectedValue, null);
    }
    
    public void clearMoneyTransactions() {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$xo_DO1h0hLJizWUYkWN5MuOY_xk$1(this));
    }
    
    public Subscribable<SubscriptionSingleKey, Integer> getBalance() {
        return this.balancePool;
    }
    
    public Subscribable<SubscriptionSingleKey, LazyList<MoneyTransaction>> moneyTransactions() {
        return this.moneyTransactionPool;
    }
    
    public void setFinancialInfo(final SLFinancialInfo newValue) {
        this.financialInfo.set(newValue);
    }
    
    public void updateBalance(final int i) {
        this.balancePool.onResultData(SubscriptionSingleKey.Value, i);
    }
    
    public void updateMoneyTransactions() {
        this.moneyTransactionPool.requestUpdate(SubscriptionSingleKey.Value);
    }
}
