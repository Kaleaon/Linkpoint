package com.linkpoint.ui.myava

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.annotation.Nullable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.linkpoint.R
import com.linkpoint.dao.MoneyTransaction
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.ui.myava.TransactionLogAdapter
import de.greenrobot.dao.query.LazyList
import java.util.UUID

class TransactionLogFragment : FragmentWithTitle(), LoadableMonitor.OnLoadableDataChangedListener, TransactionLogAdapter.OnTransactionClickListener {
    /* access modifiers changed from: private */
    public TransactionLogAdapter adapter
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.moneyTransactions).withDataChangedListener(this)
    @BindView(2131755197)
    LoadingLayout loadingLayout
    /* access modifiers changed from: private */
    val Handler mHandler = Handler()
    private val SubscriptionData<SubscriptionSingleKey, LazyList<MoneyTransaction>> moneyTransactions = SubscriptionData<>(UIThreadExecutor.getInstance())
    /* access modifiers changed from: private */
    val Runnable scrollToBottomRunnable = Runnable() {
        public Unit run() {
            Int itemCount
            Boolean unused = TransactionLogFragment.this.scrollToBottomRunnablePosted = false
            if (TransactionLogFragment.this.unbinder != null) {
                RecyclerView recyclerView = TransactionLogFragment.this.transactionLogView
                if (recyclerView.hasPendingAdapterUpdates()) {
                    Boolean unused2 = TransactionLogFragment.this.scrollToBottomRunnablePosted = true
                    TransactionLogFragment.this.mHandler.post(TransactionLogFragment.this.scrollToBottomRunnable)
                } else if (TransactionLogFragment.this.adapter != null && (itemCount = TransactionLogFragment.this.adapter.getItemCount()) > 0) {
                    recyclerView.scrollToPosition(itemCount - 1)
                }
            }
        }
    }
    /* access modifiers changed from: private */
    public Boolean scrollToBottomRunnablePosted = false
    @BindView(2131755676)
    RecyclerView transactionLogView
    /* access modifiers changed from: private */
    public Unbinder unbinder

    private Unit clearTransactionLog() {
        AlertDialog.Builder builder = AlertDialog.Builder(getActivity())
        builder.setMessage(R.string.clear_transaction_log_message).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f456$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.myava.-$Lambda$N_xrT8AwWQ2OjPw50fSCa4Lhb58.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.myava.-$Lambda$N_xrT8AwWQ2OjPw50fSCa4Lhb58.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
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

        }).setNegativeButton("No", $Lambda$N_xrT8AwWQ2OjPw50fSCa4Lhb58())
        builder.create().show()
    }

    @JvmStatic
    Bundle makeSelection(UUID uuid) {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        return bundle
    }

    private Unit performClearTransactionLog() {
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            userManager.getBalanceManager().clearMoneyTransactions()
        }
    }

    private Unit scrollToBottom() {
        if (!this.scrollToBottomRunnablePosted) {
            this.scrollToBottomRunnablePosted = true
            this.mHandler.post(this.scrollToBottomRunnable)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_myava_TransactionLogFragment_4757  reason: not valid java name */
    public /* synthetic */ Unit m665lambda$com_lumiyaviewer_lumiya_ui_myava_TransactionLogFragment_4757(DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        performClearTransactionLog()
    }

    public Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    public Unit onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.transaction_log_menu, menu)
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.transaction_log, viewGroup, false)
        this.unbinder = ButterKnife.bind((Object) this, inflate)
        this.adapter = TransactionLogAdapter(getContext(), ActivityUtils.getActiveAgentID(getArguments()), this)
        this.transactionLogView.setAdapter(this.adapter)
        this.loadableMonitor.setLoadingLayout(this.loadingLayout, (String) null, getString(R.string.cannot_load_transaction_list))
        return inflate
    }

    public Unit onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind()
            this.unbinder = null
        }
        super.onDestroyView()
    }

    public Unit onLoadableDataChanged() {
        LazyList data = this.moneyTransactions.getData()
        if (data != null) {
            this.loadableMonitor.setEmptyMessage(data.isEmpty(), getString(R.string.no_transactions_per_session))
            if (this.adapter != null) {
                this.adapter.setData(data)
                scrollToBottom()
            }
        }
    }

    public Boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_clear_transaction_log:
                clearTransactionLog()
                return true
            default:
                return super.onOptionsItemSelected(menuItem)
        }
    }

    public Unit onStart() {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.moneyTransactions.subscribe(userManager.getBalanceManager().moneyTransactions(), SubscriptionSingleKey.Value)
        }
    }

    public Unit onStop() {
        this.loadableMonitor.unsubscribeAll()
        super.onStop()
    }

    public Unit onTransactionClicked(MoneyTransaction moneyTransaction) {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments())
        if (activeAgentID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, moneyTransaction.getAgentUUID())))
        }
    }
}
