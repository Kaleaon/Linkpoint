// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.MoneyTransactionDao;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager

public class BalanceManager
{

    private final SubscriptionPool balancePool = new SubscriptionPool();
    private final SimpleRequestHandler balanceRequestHandler = new SimpleRequestHandler() {

        final BalanceManager this$0;

        public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
        {
            subscriptionsinglekey = (SLFinancialInfo)BalanceManager._2D_get1(BalanceManager.this).get();
            if (subscriptionsinglekey != null)
            {
                if (subscriptionsinglekey.getBalanceKnown())
                {
                    int i = subscriptionsinglekey.getBalance();
                    BalanceManager._2D_get0(BalanceManager.this).onResultData(SubscriptionSingleKey.Value, Integer.valueOf(i));
                    return;
                }
                subscriptionsinglekey = BalanceManager._2D_get5(BalanceManager.this).getActiveAgentCircuit();
                if (subscriptionsinglekey != null)
                {
                    subscriptionsinglekey.execute(BalanceManager._2D_get4(BalanceManager.this));
                    return;
                } else
                {
                    BalanceManager._2D_get0(BalanceManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException());
                    return;
                }
            } else
            {
                BalanceManager._2D_get0(BalanceManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException());
                return;
            }
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((SubscriptionSingleKey)obj);
        }

            
            {
                this$0 = BalanceManager.this;
                super();
            }
    };
    private final AtomicReference financialInfo = new AtomicReference(null);
    private final MoneyTransactionDao moneyTransactionDao;
    private final SubscriptionPool moneyTransactionPool = new SubscriptionPool();
    private final Runnable requestBalanceRunnable = new Runnable() {

        final BalanceManager this$0;

        public void run()
        {
            SLFinancialInfo slfinancialinfo = (SLFinancialInfo)BalanceManager._2D_get1(BalanceManager.this).get();
            if (slfinancialinfo != null)
            {
                slfinancialinfo.AskForMoneyBalance();
                return;
            } else
            {
                BalanceManager._2D_get0(BalanceManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException());
                return;
            }
        }

            
            {
                this$0 = BalanceManager.this;
                super();
            }
    };
    private final UserManager userManager;

    static SubscriptionPool _2D_get0(BalanceManager balancemanager)
    {
        return balancemanager.balancePool;
    }

    static AtomicReference _2D_get1(BalanceManager balancemanager)
    {
        return balancemanager.financialInfo;
    }

    static MoneyTransactionDao _2D_get2(BalanceManager balancemanager)
    {
        return balancemanager.moneyTransactionDao;
    }

    static SubscriptionPool _2D_get3(BalanceManager balancemanager)
    {
        return balancemanager.moneyTransactionPool;
    }

    static Runnable _2D_get4(BalanceManager balancemanager)
    {
        return balancemanager.requestBalanceRunnable;
    }

    static UserManager _2D_get5(BalanceManager balancemanager)
    {
        return balancemanager.userManager;
    }

    BalanceManager(UserManager usermanager)
    {
        userManager = usermanager;
        moneyTransactionDao = usermanager.getDaoSession().getMoneyTransactionDao();
        balancePool.attachRequestHandler(balanceRequestHandler);
        moneyTransactionPool.attachRequestHandler(new AsyncRequestHandler(usermanager.getDatabaseExecutor(), new SimpleRequestHandler() {

            final BalanceManager this$0;

            public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
            {
                LazyList lazylist = BalanceManager._2D_get2(BalanceManager.this).queryBuilder().orderAsc(new Property[] {
                    com.lumiyaviewer.lumiya.dao.MoneyTransactionDao.Properties.Timestamp
                }).listLazy();
                BalanceManager._2D_get3(BalanceManager.this).onResultData(subscriptionsinglekey, lazylist);
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((SubscriptionSingleKey)obj);
            }

            
            {
                this$0 = BalanceManager.this;
                super();
            }
        }));
        moneyTransactionPool.setDisposeHandler(new _2D_.Lambda.xo_DO1h0hLJizWUYkWN5MuOY_2D_xk(), usermanager.getDatabaseExecutor());
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_BalanceManager_1705(LazyList lazylist)
    {
        if (!lazylist.isClosed())
        {
            lazylist.close();
        }
    }

    public void clearFinancialInfo(SLFinancialInfo slfinancialinfo)
    {
        financialInfo.compareAndSet(slfinancialinfo, null);
    }

    public void clearMoneyTransactions()
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.xo_DO1h0hLJizWUYkWN5MuOY_2D_xk._cls1(this));
    }

    public Subscribable getBalance()
    {
        return balancePool;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_BalanceManager_4293()
    {
        moneyTransactionDao.deleteAll();
        updateMoneyTransactions();
    }

    public Subscribable moneyTransactions()
    {
        return moneyTransactionPool;
    }

    public void setFinancialInfo(SLFinancialInfo slfinancialinfo)
    {
        financialInfo.set(slfinancialinfo);
    }

    public void updateBalance(int i)
    {
        balancePool.onResultData(SubscriptionSingleKey.Value, Integer.valueOf(i));
    }

    public void updateMoneyTransactions()
    {
        moneyTransactionPool.requestUpdate(SubscriptionSingleKey.Value);
    }
}
