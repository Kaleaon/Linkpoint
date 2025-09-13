package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import javax.annotation.Nonnull;

class ChatterSubscription {
    @Nonnull
    private final SortedChatterList chatterList;
    @Nonnull
    ChatterDisplayData displayData;
    boolean isValid;
    private final Subscription.OnData<UnreadMessageInfo> onUnreadCount = new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f223$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8.1.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8.1.$m$0(java.lang.Object):void, class status: UNLOADED
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

        public final void brokenMethod(
        // TODO: implement method
    }
    };
    private final Subscription.OnData<VoiceChatInfo> onVoiceStatusChanged = new $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8(this);
    @Nonnull
    private final Subscription<ChatterID, UnreadMessageInfo> unreadCountSubscription;
    @Nonnull
    private final Subscription<ChatterID, VoiceChatInfo> voiceChatInfoSubscription;

    ChatterSubscription(@Nonnull SortedChatterList sortedChatterList, @Nonnull ChatterID chatterID, @Nonnull UserManager userManager) {
        this.chatterList = sortedChatterList;
        this.displayData = new ChatterDisplayData(chatterID, (String) null, false, 0, (SLChatEvent) null, Float.NaN, false);
        this.unreadCountSubscription = userManager.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(chatterID, this.onUnreadCount);
        this.voiceChatInfoSubscription = userManager.getVoiceChatInfo().subscribe(chatterID, this.onVoiceStatusChanged);
        this.isValid = true;
        sortedChatterList.addChatter(this.displayData);
    }

    /* access modifiers changed from: private */
    /* renamed from: onUnreadCountChanged */
    public void m298com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscriptionmthref1(UnreadMessageInfo unreadMessageInfo) {
        if (unreadMessageInfo != null) {
            setChatterDisplayData(this.displayData.withUnreadInfo(unreadMessageInfo));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceChatInfoChanged */
    public void m297com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscriptionmthref0(VoiceChatInfo voiceChatInfo) {
        boolean z = false;
        ChatterDisplayData chatterDisplayData = this.displayData;
        if (!(voiceChatInfo == null || voiceChatInfo.state == VoiceChatInfo.VoiceChatState.None)) {
            z = true;
        }
        setChatterDisplayData(chatterDisplayData.withVoiceActive(z));
    }

    public void dispose() {
        unsubscribe();
        this.chatterList.removeChatter(this.displayData);
    }

    /* access modifiers changed from: package-private */
    public void setChatterDisplayData(@Nonnull ChatterDisplayData chatterDisplayData) {
        ChatterDisplayData chatterDisplayData2 = this.displayData;
        this.displayData = chatterDisplayData;
        this.chatterList.replaceChatter(chatterDisplayData2, this.displayData);
    }

    public void unsubscribe() {
        this.unreadCountSubscription.unsubscribe();
        this.voiceChatInfoSubscription.unsubscribe();
    }
}
