package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import com.lumiyaviewer.lumiya.dao.MoneyTransactionDao;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import de.greenrobot.dao.query.LazyList;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class BalanceManager {
    /* access modifiers changed from: private */
    public final SubscriptionPool<SubscriptionSingleKey, Integer> balancePool = new SubscriptionPool<>();
    private final SimpleRequestHandler<SubscriptionSingleKey> balanceRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() {
        public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
            SLFinancialInfo sLFinancialInfo = (SLFinancialInfo) BalanceManager.this.financialInfo.get();
            if (sLFinancialInfo == null) {
                BalanceManager.this.balancePool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
            } else if (sLFinancialInfo.getBalanceKnown()) {
                BalanceManager.this.balancePool.onResultData(SubscriptionSingleKey.Value, Integer.valueOf(sLFinancialInfo.getBalance()));
            } else {
                SLAgentCircuit activeAgentCircuit = BalanceManager.this.userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    activeAgentCircuit.execute(BalanceManager.this.requestBalanceRunnable);
                } else {
                    BalanceManager.this.balancePool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final AtomicReference<SLFinancialInfo> financialInfo = new AtomicReference<>((Object) null);
    /* access modifiers changed from: private */
    public final MoneyTransactionDao moneyTransactionDao;
    /* access modifiers changed from: private */
    public final SubscriptionPool<SubscriptionSingleKey, LazyList<MoneyTransaction>> moneyTransactionPool = new SubscriptionPool<>();
    /* access modifiers changed from: private */
    public final Runnable requestBalanceRunnable = new Runnable() {
        public void run() {
            SLFinancialInfo sLFinancialInfo = (SLFinancialInfo) BalanceManager.this.financialInfo.get();
            if (sLFinancialInfo != null) {
                sLFinancialInfo.AskForMoneyBalance();
            } else {
                BalanceManager.this.balancePool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
            }
        }
    };
    /* access modifiers changed from: private */
    public final UserManager userManager;

    BalanceManager(UserManager userManager2) {
        this.userManager = userManager2;
        this.moneyTransactionDao = userManager2.getDaoSession().getMoneyTransactionDao();
        this.balancePool.attachRequestHandler(this.balanceRequestHandler);
        this.moneyTransactionPool.attachRequestHandler(new AsyncRequestHandler(userManager2.getDatabaseExecutor(), new SimpleRequestHandler<SubscriptionSingleKey>() {
            public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
                BalanceManager.this.moneyTransactionPool.onResultData(subscriptionSingleKey, BalanceManager.this.moneyTransactionDao.queryBuilder().orderAsc(MoneyTransactionDao.Properties.Timestamp).listLazy());
            }
        }));
        this.moneyTransactionPool.setDisposeHandler(new $Lambda$xo_DO1h0hLJizWUYkWN5MuOYxk(), userManager2.getDatabaseExecutor());
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_BalanceManager_1705  reason: not valid java name */
    static /* synthetic */ void m286lambda$com_lumiyaviewer_lumiya_slproto_users_manager_BalanceManager_1705(LazyList lazyList) {
        if (!lazyList.isClosed()) {
            lazyList.close();
        }
    }

    public void clearFinancialInfo(SLFinancialInfo sLFinancialInfo) {
        this.financialInfo.compareAndSet(sLFinancialInfo, (Object) null);
    }

    public void clearMoneyTransactions() {
        this.userManager.getDatabaseExecutor().execute(new Runnable(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f224$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$xo_DO1h0hLJizWUYkWN5MuOY-xk.1.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$xo_DO1h0hLJizWUYkWN5MuOY-xk.1.$m$0():void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }

    public Subscribable<SubscriptionSingleKey, Integer> getBalance() {
        return this.balancePool;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_BalanceManager_4293  reason: not valid java name */
    public /* synthetic */ void m287lambda$com_lumiyaviewer_lumiya_slproto_users_manager_BalanceManager_4293() {
        this.moneyTransactionDao.deleteAll();
        updateMoneyTransactions();
    }

    public Subscribable<SubscriptionSingleKey, LazyList<MoneyTransaction>> moneyTransactions() {
        return this.moneyTransactionPool;
    }

    public void setFinancialInfo(SLFinancialInfo sLFinancialInfo) {
        this.financialInfo.set(sLFinancialInfo);
    }

    public void updateBalance(int i) {
        this.balancePool.onResultData(SubscriptionSingleKey.Value, Integer.valueOf(i));
    }

    public void updateMoneyTransactions() {
        this.moneyTransactionPool.requestUpdate(SubscriptionSingleKey.Value);
    }
}
