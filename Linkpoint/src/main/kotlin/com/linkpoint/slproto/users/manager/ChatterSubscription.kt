package com.linkpoint.slproto.users.manager
import java.util.*

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.voice.common.model.VoiceChatInfo
import javax.annotation.Nonnull

class ChatterSubscription {
    private val SortedChatterList chatterList
    ChatterDisplayData displayData
    Boolean isValid
    private val Subscription.OnData<UnreadMessageInfo> onUnreadCount = Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f223$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8.1.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    }
    private val Subscription.OnData<VoiceChatInfo> onVoiceStatusChanged = $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8(this)
    private val Subscription<ChatterID, UnreadMessageInfo> unreadCountSubscription
    private val Subscription<ChatterID, VoiceChatInfo> voiceChatInfoSubscription

    ChatterSubscription(SortedChatterList sortedChatterList, ChatterID chatterID, UserManager userManager) {
        this.chatterList = sortedChatterList
        this.displayData = ChatterDisplayData(chatterID, (String) null, false, 0, (SLChatEvent) null, Float.NaN, false)
        this.unreadCountSubscription = userManager.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(chatterID, this.onUnreadCount)
        this.voiceChatInfoSubscription = userManager.getVoiceChatInfo().subscribe(chatterID, this.onVoiceStatusChanged)
        this.isValid = true
        sortedChatterList.addChatter(this.displayData)
    }

    /* access modifiers changed from: private */
    /* renamed from: onUnreadCountChanged */
    fun m298com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscriptionmthref1(unreadMessageInfo: UnreadMessageInfo) {
        if (unreadMessageInfo != null) {
            setChatterDisplayData(this.displayData.withUnreadInfo(unreadMessageInfo))
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceChatInfoChanged */
    fun m297com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscriptionmthref0(voiceChatInfo: VoiceChatInfo) {
        val z: Boolean = false
        val chatterDisplayData: ChatterDisplayData = this.displayData
        if (!(voiceChatInfo == null || voiceChatInfo.state == VoiceChatInfo.VoiceChatState.None)) {
            z = true
        }
        setChatterDisplayData(chatterDisplayData.withVoiceActive(z))
    }

    fun dispose() {
        unsubscribe()
        this.chatterList.removeChatter(this.displayData)
    }

    /* access modifiers changed from: package-private */
    fun setChatterDisplayData(chatterDisplayData: ChatterDisplayData) {
        val chatterDisplayData2: ChatterDisplayData = this.displayData
        this.displayData = chatterDisplayData
        this.chatterList.replaceChatter(chatterDisplayData2, this.displayData)
    }

    fun unsubscribe() {
        this.unreadCountSubscription.unsubscribe()
        this.voiceChatInfoSubscription.unsubscribe()
    }
}
