package com.linkpoint.ui.myava

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
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

class TransactionLogFragment : FragmentWithTitle : LoadableMonitor.OnLoadableDataChangedListener, TransactionLogAdapter.OnTransactionClickListener {
    /* access modifiers changed from: private */
    TransactionLogAdapter adapter
    private LoadableMonitor loadableMonitor = LoadableMonitor(this.moneyTransactions).withDataChangedListener(this)
    @BindView(2131755197)
    LoadingLayout loadingLayout
    /* access modifiers changed from: private */
    Handler mHandler = Handler()
    private SubscriptionData<SubscriptionSingleKey, LazyList<MoneyTransaction>> moneyTransactions = SubscriptionData<>(UIThreadExecutor.getInstance())
    /* access modifiers changed from: private */
    Runnable scrollToBottomRunnable = Runnable() {
        fun run(): Unit {
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
    Boolean scrollToBottomRunnablePosted = false
    @BindView(2131755676)
    RecyclerView transactionLogView
    /* access modifiers changed from: private */
    Unbinder unbinder

    private Unit clearTransactionLog() {
        AlertDialog.Builder builder = AlertDialog.Builder(getActivity())
        builder.setMessage(R.string.clear_transaction_log_message).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f456$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.myava.-$Lambda$N_xrT8AwWQ2OjPw50fSCa4Lhb58.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.myava.-$Lambda$N_xrT8AwWQ2OjPw50fSCa4Lhb58.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

    fun makeSelection(UUID uuid): Bundle {
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
    /* synthetic */ Unit m665lambda$com_lumiyaviewer_lumiya_ui_myava_TransactionLogFragment_4757(DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        performClearTransactionLog()
    }

    fun onCreate(@Nullable Bundle bundle): Unit {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    fun onCreateOptionsMenu(Menu menu, MenuInflater menuInflater): Unit {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.transaction_log_menu, menu)
    }

    fun onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.transaction_log, viewGroup, false)
        this.unbinder = ButterKnife.bind((Any) this, inflate)
        this.adapter = TransactionLogAdapter(getContext(), ActivityUtils.getActiveAgentID(getArguments()), this)
        this.transactionLogView.setAdapter(this.adapter)
        this.loadableMonitor.setLoadingLayout(this.loadingLayout, (String) null, getString(R.string.cannot_load_transaction_list))
        return inflate
    }

    fun onDestroyView(): Unit {
        if (this.unbinder != null) {
            this.unbinder.unbind()
            this.unbinder = null
        }
        super.onDestroyView()
    }

    fun onLoadableDataChanged(): Unit {
        LazyList data = this.moneyTransactions.getData()
        if (data != null) {
            this.loadableMonitor.setEmptyMessage(data.isEmpty(), getString(R.string.no_transactions_per_session))
            if (this.adapter != null) {
                this.adapter.setData(data)
                scrollToBottom()
            }
        }
    }

    fun onOptionsItemSelected(MenuItem menuItem): Boolean {
        switch (menuItem.getItemId()) {
            case R.id.item_clear_transaction_log:
                clearTransactionLog()
                return true
            default:
                return super.onOptionsItemSelected(menuItem)
        }
    }

    fun onStart(): Unit {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.moneyTransactions.subscribe(userManager.getBalanceManager().moneyTransactions(), SubscriptionSingleKey.Value)
        }
    }

    fun onStop(): Unit {
        this.loadableMonitor.unsubscribeAll()
        super.onStop()
    }

    fun onTransactionClicked(MoneyTransaction moneyTransaction): Unit {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments())
        if (activeAgentID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, moneyTransaction.getAgentUUID())))
        }
    }
}
