package com.linkpoint.ui.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.utils.UUIDPool
import java.util.UUID

class LogoutDialog : ProgressDialog {
    private val DISCONNECT_TIMEOUT: Long = 5000
    private UUID agentUUID
    private EventBus eventBus
    private Handler handler
    private Runnable onDisconnectTimeout

    LogoutDialog(Context context) {
        super(context)
        this.handler = Handler()
        this.eventBus = EventBus.getInstance()
        this.onDisconnectTimeout = $Lambda$Ido4EAnXE9yUsM2nDeFKnyTfU7w(this)
    }

    LogoutDialog(Context context, Int i) {
        super(context, i)
        this.handler = Handler()
        this.eventBus = EventBus.getInstance()
        this.onDisconnectTimeout = Runnable(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f448$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.login.-$Lambda$Ido4EAnXE9yUsM2nDeFKnyTfU7w.1.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.login.-$Lambda$Ido4EAnXE9yUsM2nDeFKnyTfU7w.1.$m$0():Unit, class status: UNLOADED
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
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
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
    }

    private UserManager getUserManager() {
        if (this.agentUUID != null) {
            return UserManager.getUserManager(this.agentUUID)
        }
        return null
    }

    fun show(Activity activity): Unit {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(activity.getIntent())
        if (activeAgentID != null) {
            LogoutDialog logoutDialog = LogoutDialog(activity)
            logoutDialog.setAgentUUID(activeAgentID)
            logoutDialog.show()
        }
    }

    @EventHandler
    fun handleDisconnectEvent(SLDisconnectEvent sLDisconnectEvent): Unit {
        Debug.Printf("LogoutDialog: disconnect event", Any[0])
        dismiss()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_login_LogoutDialog_3137  reason: not valid java name */
    /* synthetic */ Unit m637lambda$com_lumiyaviewer_lumiya_ui_login_LogoutDialog_3137() {
        SLAgentCircuit activeAgentCircuit
        SLGridConnection gridConnection
        Boolean z = true
        UserManager userManager = getUserManager()
        if (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (gridConnection = activeAgentCircuit.getGridConnection()) == null) {
            z = false
        } else {
            gridConnection.forceDisconnect(true)
        }
        if (!z) {
            dismiss()
        }
    }

    /* access modifiers changed from: protected */
    fun onCreate(Bundle bundle): Unit {
        super.onCreate(bundle)
        setProgressStyle(0)
        setMessage(getContext().getString(R.string.logging_out))
        if (bundle != null) {
            this.agentUUID = UUIDPool.getUUID(bundle.getString("agentUUID"))
        }
    }

    fun onSaveInstanceState(): Bundle {
        Bundle onSaveInstanceState = super.onSaveInstanceState()
        if (onSaveInstanceState == null) {
            onSaveInstanceState = Bundle()
        }
        if (this.agentUUID != null) {
            onSaveInstanceState.putString("agentUUID", this.agentUUID.toString())
        }
        return onSaveInstanceState
    }

    fun onStart(): Unit {
        SLAgentCircuit activeAgentCircuit
        SLGridConnection gridConnection
        Boolean z = false
        super.onStart()
        this.eventBus.subscribe(this, (Activity) null, this.handler)
        UserManager userManager = getUserManager()
        if (!(userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (gridConnection = activeAgentCircuit.getGridConnection()) == null)) {
            Debug.Printf("LogoutDialog: connection is not null", Any[0])
            this.handler.postDelayed(this.onDisconnectTimeout, DISCONNECT_TIMEOUT)
            gridConnection.Disconnect()
            z = true
        }
        if (!z) {
            dismiss()
            EventBus.getInstance().publish(SLDisconnectEvent(true, "Disconnected"))
        }
    }

    /* access modifiers changed from: protected */
    fun onStop(): Unit {
        this.handler.removeCallbacks(this.onDisconnectTimeout)
        this.eventBus.unsubscribe(this)
        super.onStop()
    }

    fun setAgentUUID(UUID uuid): Unit {
        this.agentUUID = uuid
    }
}
