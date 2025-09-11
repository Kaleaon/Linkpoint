package com.lumiyaviewer.lumiya.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatRecyclerAdapter;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import java.util.UUID;
import javax.annotation.Nullable;

public class ChatFragment extends UserFunctionsFragment implements View.OnClickListener, View.OnKeyListener, ChatRecyclerAdapter.OnAdapterDataChanged, ChatRecyclerAdapter.OnUserPicClickedListener {
    private static final int PERMISSION_REQUEST_CODE = 500;
    private static final long typingTimeout = 5000;
    /* access modifiers changed from: private */
    @Nullable
    public ChatRecyclerAdapter adapter = null;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f247$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.2.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.2.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
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

        public final void onData(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.2.onData(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.2.onData(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
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
    });
    @Nullable
    private MenuItem clearChatHistoryMenuItem = null;
    @Nullable
    private MenuItem exportChatHistoryMenuItem = null;
    private boolean hasMoreItems = false;
    private long lastTypingEventSent;
    /* access modifiers changed from: private */
    @Nullable
    public ChatLayoutManager layoutManager = null;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    @Nullable
    private ChatterID markDisplayedChatterID = null;
    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0 || i == 1) {
                boolean unused = ChatFragment.this.scrollToBottomNeeded = false;
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            ChatFragment.this.updateVisibleRange();
        }
    };
    /* access modifiers changed from: private */
    public boolean scrollToBottomForceDown = false;
    /* access modifiers changed from: private */
    public boolean scrollToBottomNeeded = false;
    /* access modifiers changed from: private */
    public final Runnable scrollToBottomRunnable = new Runnable() {
        public void run() {
            boolean unused = ChatFragment.this.scrollToBottomRunnablePosted = false;
            if (ChatFragment.this.getView() != null) {
                RecyclerView recyclerView = (RecyclerView) ChatFragment.this.getView().findViewById(R.id.chatLogView);
                if (recyclerView.hasPendingAdapterUpdates()) {
                    boolean unused2 = ChatFragment.this.scrollToBottomRunnablePosted = true;
                    ChatFragment.this.mHandler.post(ChatFragment.this.scrollToBottomRunnable);
                } else if (ChatFragment.this.adapter != null && ChatFragment.this.layoutManager != null) {
                    if (ChatFragment.this.scrollToBottomForceDown && ChatFragment.this.adapter.hasMoreItemsAtBottom()) {
                        boolean unused3 = ChatFragment.this.scrollToBottomNeeded = false;
                        ChatFragment.this.adapter.restartAtBottom();
                    }
                    int itemCount = ChatFragment.this.adapter.getItemCount();
                    if (itemCount > 0) {
                        ChatFragment.this.layoutManager.setScrollMode(ChatFragment.this.scrollToBottomForceDown);
                        recyclerView.smoothScrollToPosition(itemCount - 1);
                        boolean unused4 = ChatFragment.this.scrollToBottomNeeded = true;
                        boolean unused5 = ChatFragment.this.scrollToBottomForceDown = false;
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean scrollToBottomRunnablePosted = false;
    private final TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.length() != 0) {
                ChatFragment.this.setTypingNotify(true);
            } else {
                ChatFragment.this.setTypingNotify(false);
            }
        }
    };
    @Nullable
    private ChatterID typingNotifiedChatter = null;
    /* access modifiers changed from: private */
    public boolean updateRunnablePosted = false;
    /* access modifiers changed from: private */
    public final Runnable updateVisibleRangeRunnable = new Runnable() {
        public void run() {
            boolean unused = ChatFragment.this.updateRunnablePosted = false;
            View view = ChatFragment.this.getView();
            if (ChatFragment.this.adapter != null && ChatFragment.this.layoutManager != null && view != null) {
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chatLogView);
                Debug.Printf("UpdateVisibleRange: pending %b, first %d, last %d", Boolean.valueOf(recyclerView.hasPendingAdapterUpdates()), Integer.valueOf(ChatFragment.this.layoutManager.findFirstVisibleItemPosition()), Integer.valueOf(ChatFragment.this.layoutManager.findLastVisibleItemPosition()));
                if (!recyclerView.hasPendingAdapterUpdates()) {
                    ChatFragment.this.updateHasMoreItems();
                    int findFirstVisibleItemPosition = ChatFragment.this.layoutManager.findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition = ChatFragment.this.layoutManager.findLastVisibleItemPosition();
                    if (ChatFragment.this.scrollToBottomNeeded) {
                        findLastVisibleItemPosition = ChatFragment.this.adapter.getItemCount() - 1;
                    }
                    ChatFragment.this.adapter.setVisibleRange(findFirstVisibleItemPosition, findLastVisibleItemPosition);
                    return;
                }
                boolean unused2 = ChatFragment.this.updateRunnablePosted = true;
                ChatFragment.this.mHandler.post(ChatFragment.this.updateVisibleRangeRunnable);
            }
        }
    };
    private final SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f248$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.3.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.3.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
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

        public final void onData(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.3.onData(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.3.onData(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
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
    });
    private final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f249$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.4.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.4.$m$0(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
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

        public final void onData(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.4.onData(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.4.onData(java.lang.Object):void, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
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
    });
    private boolean vrMode = false;

    private void clearChatHistory() {
        new AlertDialog.Builder(getContext()).setMessage(R.string.clear_chat_history_confirm).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f246$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
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

            public final void onClick(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.1.onClick(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.1.onClick(android.content.DialogInterface, int):void, class status: UNLOADED
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
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
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
        }).setNegativeButton("No", new $Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg()).create().show();
    }

    private void exportChatHistory() {
        if (this.chatterID == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 500);
            return;
        }
        new ExportChatHistoryTask(getActivity()).execute(new ChatterID[]{this.chatterID});
    }

    public static Bundle makeSelection(ChatterID chatterID) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        makeSelection.putParcelable(ChatterFragment.CHATTER_ID_KEY, chatterID);
        return makeSelection;
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m410com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref0(SLAgentCircuit sLAgentCircuit) {
        int i = 0;
        View view = getView();
        if (view != null) {
            Object[] objArr = new Object[1];
            objArr[0] = sLAgentCircuit != null ? "present" : "not present";
            Debug.Printf("agentCircuit is now %s", objArr);
            view.findViewById(R.id.sendMessageButton).setVisibility((sLAgentCircuit == null || !(this.vrMode ^ true)) ? 8 : 0);
            View findViewById = view.findViewById(R.id.chat_speak_button);
            if (sLAgentCircuit == null || !this.vrMode) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceActiveChatter */
    public void m411com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref1(ChatterID chatterID) {
        if (chatterID == null || this.userManager == null || !this.vrMode) {
            this.voiceChatInfo.unsubscribe();
        } else {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID);
        }
        updateVrModeControls();
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceChatInfo */
    public void m412com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref2(VoiceChatInfo voiceChatInfo2) {
        updateVrModeControls();
    }

    private void scrollToBottom(boolean z) {
        if (getView() != null && (!this.scrollToBottomRunnablePosted)) {
            this.scrollToBottomNeeded = true;
            this.scrollToBottomRunnablePosted = true;
            this.scrollToBottomForceDown |= z;
            this.mHandler.post(this.scrollToBottomRunnable);
        }
    }

    private void sendMessage() {
        SLAgentCircuit activeAgentCircuit;
        setTypingNotify(false);
        View view = getView();
        if (this.chatterID != null && view != null) {
            String charSequence = ((TextView) view.findViewById(R.id.sendMessageText)).getText().toString();
            if (!charSequence.equals("")) {
                if (!(this.userManager == null || (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) == null)) {
                    activeAgentCircuit.SendChatMessage(this.chatterID, charSequence);
                    ((TextView) getView().findViewById(R.id.sendMessageText)).setText("");
                }
                scrollToBottom(false);
            }
        }
    }

    private boolean sendTypingNotify(@Nullable ChatterID chatterID, boolean z) {
        UserManager userManager;
        SLAgentCircuit activeAgentCircuit;
        if (!(chatterID instanceof ChatterID.ChatterIDUser) || (userManager = chatterID.getUserManager()) == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null) {
            return false;
        }
        activeAgentCircuit.sendTypingNotify(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), z);
        return true;
    }

    /* access modifiers changed from: private */
    public void setTypingNotify(boolean z) {
        if (z) {
            long currentTimeMillis = System.currentTimeMillis();
            if (!Objects.equal(this.chatterID, this.typingNotifiedChatter)) {
                if (this.typingNotifiedChatter != null) {
                    sendTypingNotify(this.typingNotifiedChatter, false);
                    this.typingNotifiedChatter = null;
                }
                if (sendTypingNotify(this.chatterID, true)) {
                    this.typingNotifiedChatter = this.chatterID;
                    this.lastTypingEventSent = currentTimeMillis;
                }
            } else if (this.typingNotifiedChatter != null && currentTimeMillis >= this.lastTypingEventSent + typingTimeout) {
                this.lastTypingEventSent = currentTimeMillis;
                sendTypingNotify(this.typingNotifiedChatter, true);
            }
        } else if (this.typingNotifiedChatter != null) {
            sendTypingNotify(this.typingNotifiedChatter, false);
            this.typingNotifiedChatter = null;
        }
    }

    private void updateChatHistoryExists() {
        boolean z = this.adapter != null ? this.adapter.getItemCount() != 0 : false;
        Debug.Printf("ChatHistory: chat history exists %b", Boolean.valueOf(z));
        if (this.clearChatHistoryMenuItem != null) {
            this.clearChatHistoryMenuItem.setVisible(z);
        }
        if (this.exportChatHistoryMenuItem != null) {
            this.exportChatHistoryMenuItem.setVisible(z);
        }
    }

    /* access modifiers changed from: private */
    public void updateHasMoreItems() {
        boolean z = true;
        View view = getView();
        if (view != null) {
            this.hasMoreItems = false;
            if (!(this.layoutManager == null || this.adapter == null)) {
                int itemCount = this.adapter.getItemCount();
                int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
                if (this.scrollToBottomNeeded) {
                    findLastVisibleItemPosition = this.adapter.getItemCount() - 1;
                }
                if (itemCount > 1 && findLastVisibleItemPosition != -1) {
                    if (findLastVisibleItemPosition >= itemCount - 2) {
                        z = this.adapter.hasMoreItemsAtBottom();
                    }
                    this.hasMoreItems = z;
                }
            }
            view.findViewById(R.id.scroll_to_bottom_btn).setVisibility(this.hasMoreItems ? 0 : 8);
        }
    }

    /* access modifiers changed from: private */
    public void updateVisibleRange() {
        if (this.adapter != null && this.layoutManager != null && getView() != null && (!this.updateRunnablePosted)) {
            this.updateRunnablePosted = true;
            this.mHandler.post(this.updateVisibleRangeRunnable);
        }
    }

    private void updateVrModeControls() {
        boolean z;
        CurrentLocationInfo currentLocationInfoSnapshot;
        boolean z2 = true;
        int i = 0;
        View view = getView();
        if (view == null) {
            return;
        }
        if (this.vrMode) {
            view.findViewById(R.id.chat_vr_mode_controls).setVisibility(0);
            if (isVoiceLoggedIn()) {
                if (this.voiceActiveChatter.getData() != null) {
                    VoiceChatInfo data = this.voiceChatInfo.getData();
                    z = (data == null || data.state == VoiceChatInfo.VoiceChatState.None) ? false : true;
                } else {
                    z = false;
                }
                if (z) {
                    z2 = false;
                } else if (this.chatterID == null || this.userManager == null) {
                    z2 = false;
                } else if ((this.chatterID instanceof ChatterID.ChatterIDLocal) && ((currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot()) == null || currentLocationInfoSnapshot.parcelVoiceChannel() == null)) {
                    z2 = false;
                }
            } else {
                z = false;
                z2 = false;
            }
            view.findViewById(R.id.chat_speak_button).setVisibility(z ? 8 : 0);
            View findViewById = view.findViewById(R.id.chat_voice_call_button);
            if (!z2) {
                i = 8;
            }
            findViewById.setVisibility(i);
            return;
        }
        view.findViewById(R.id.chat_vr_mode_controls).setVisibility(8);
    }

    /* synthetic */ void handleClearChatHistory(DialogInterface dialogInterface, int i) {
        if (this.chatterID != null) {
            UserManager userManager = this.chatterID.getUserManager();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().clearChatHistory(this.chatterID);
            }
            dialogInterface.dismiss();
        }
    }

    public void onAdapterDataAddedAtEnd() {
        if (this.layoutManager != null && (!this.hasMoreItems || this.layoutManager.isSmoothScrolling() || this.scrollToBottomNeeded)) {
            scrollToBottom(false);
        }
        updateChatHistoryExists();
    }

    public void onAdapterDataChanged() {
        updateVisibleRange();
        updateChatHistoryExists();
    }

    public void onAdapterDataReloaded() {
        this.scrollToBottomNeeded = false;
        updateVisibleRange();
        updateChatHistoryExists();
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        super.onChatterNameUpdated(chatterNameRetriever);
        View view = getView();
        if (view != null) {
            String resolvedName = this.chatterID instanceof ChatterID.ChatterIDLocal ? "local chat" : chatterNameRetriever.getResolvedName();
            ((EditText) view.findViewById(R.id.sendMessageText)).setHint(resolvedName != null ? getString(R.string.chat_hint_message_format, resolvedName) : null);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scroll_to_bottom_btn:
                scrollToBottom(true);
                return;
            case R.id.chat_speak_button:
                FragmentActivity activity = getActivity();
                if ((activity instanceof CardboardActivity) && this.chatterID != null) {
                    ((CardboardActivity) activity).startDictation(this.chatterID);
                    return;
                }
                return;
            case R.id.chat_voice_call_button:
                if (this.chatterID != null) {
                    handleStartVoice(this.chatterID);
                    return;
                }
                return;
            case R.id.sendMessageButton:
                sendMessage();
                return;
            default:
                return;
        }
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.getBoolean(CardboardActivity.VR_MODE_TAG)) {
            this.vrMode = true;
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.chat_history_menu, menu);
        this.exportChatHistoryMenuItem = menu.findItem(R.id.item_chat_history_export);
        this.clearChatHistoryMenuItem = menu.findItem(R.id.item_chat_history_clear);
        updateChatHistoryExists();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int i = 0;
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.chat, viewGroup, false);
        inflate.findViewById(R.id.scroll_to_bottom_btn).setOnClickListener(this);
        this.layoutManager = new ChatLayoutManager(viewGroup.getContext(), 1, false);
        this.layoutManager.setStackFromEnd(true);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.chatLogView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(this.scrollListener);
        recyclerView.setLayoutManager(this.layoutManager);
        inflate.findViewById(R.id.sendMessageButton).setOnClickListener(this);
        inflate.findViewById(R.id.chat_speak_button).setOnClickListener(this);
        inflate.findViewById(R.id.chat_voice_call_button).setOnClickListener(this);
        EditText editText = (EditText) inflate.findViewById(R.id.sendMessageText);
        editText.setOnKeyListener(this);
        editText.addTextChangedListener(this.textWatcher);
        editText.setVisibility(this.vrMode ? 8 : 0);
        inflate.findViewById(R.id.sendMessageButton).setVisibility(this.vrMode ? 8 : 0);
        View findViewById = inflate.findViewById(R.id.chat_vr_mode_controls);
        if (!this.vrMode) {
            i = 8;
        }
        findViewById.setVisibility(i);
        return inflate;
    }

    /* access modifiers changed from: protected */
    public void onCurrentLocationChanged(CurrentLocationInfo currentLocationInfo) {
        super.m574com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragmentmthref1(currentLocationInfo);
        updateVrModeControls();
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && i == 66) {
            sendMessage();
            return true;
        }
        if (view instanceof TextView) {
            if (((TextView) view).getText().length() != 0) {
                setTypingNotify(true);
            } else {
                setTypingNotify(false);
            }
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_chat_history_export:
                exportChatHistory();
                return true;
            case R.id.item_chat_history_clear:
                clearChatHistory();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onPause() {
        if (this.markDisplayedChatterID != null) {
            UserManager userManager = this.markDisplayedChatterID.getUserManager();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().removeDisplayedChatter(this.markDisplayedChatterID);
            }
            this.markDisplayedChatterID = null;
        }
        setTypingNotify(false);
        super.onPause();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateChatHistoryExists();
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i == 500 && iArr.length > 0 && iArr[0] == 0) {
            new ExportChatHistoryTask(getActivity()).execute(new ChatterID[]{this.chatterID});
        }
    }

    public void onResume() {
        UserManager userManager;
        super.onResume();
        this.markDisplayedChatterID = this.chatterID;
        if (!(this.markDisplayedChatterID == null || (userManager = this.markDisplayedChatterID.getUserManager()) == null)) {
            userManager.getChatterList().getActiveChattersManager().addDisplayedChatter(this.markDisplayedChatterID);
        }
        updateVisibleRange();
        updateChatHistoryExists();
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        UserManager userManager;
        UserManager userManager2;
        Debug.Printf("ChatFragment: displaying for user %s", chatterID);
        if (!Objects.equal(this.markDisplayedChatterID, chatterID) && isFragmentVisible()) {
            if (!(this.markDisplayedChatterID == null || (userManager2 = this.markDisplayedChatterID.getUserManager()) == null)) {
                userManager2.getChatterList().getActiveChattersManager().removeDisplayedChatter(this.markDisplayedChatterID);
            }
            this.markDisplayedChatterID = chatterID;
            if (!(this.markDisplayedChatterID == null || (userManager = this.markDisplayedChatterID.getUserManager()) == null)) {
                userManager.getChatterList().getActiveChattersManager().addDisplayedChatter(this.markDisplayedChatterID);
            }
        }
        if (this.adapter != null) {
            this.adapter.stopLoading();
            this.adapter = null;
        }
        if (chatterID == null || this.userManager == null) {
            this.agentCircuit.unsubscribe();
            this.voiceActiveChatter.unsubscribe();
            this.voiceChatInfo.unsubscribe();
        } else {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.adapter = new ChatRecyclerAdapter(getActivity(), this.userManager, chatterID);
            this.adapter.setOnUserPicClickedListener(this);
            if (this.vrMode) {
                this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
            } else {
                this.voiceActiveChatter.unsubscribe();
            }
        }
        View view = getView();
        if (view != null) {
            Debug.Printf("ChatFragment: setting adapter to %s", this.adapter);
            ((RecyclerView) view.findViewById(R.id.chatLogView)).setAdapter(this.adapter);
            if (this.adapter != null) {
                this.adapter.startLoading(this);
            }
            TypingIndicatorView typingIndicatorView = (TypingIndicatorView) view.findViewById(R.id.typing_indicator);
            if (typingIndicatorView != null) {
                typingIndicatorView.setChatterID(chatterID);
            }
            VoiceStatusView voiceStatusView = (VoiceStatusView) view.findViewById(R.id.voice_status_view);
            if (voiceStatusView != null) {
                voiceStatusView.setChatterID(chatterID);
            }
            updateVisibleRange();
            updateChatHistoryExists();
            updateVrModeControls();
        }
        setTypingNotify(false);
    }

    public void onUserPicClicked(ChatMessageSource chatMessageSource) {
        int objectLocalID;
        if (this.userManager == null) {
            return;
        }
        if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User || chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.Group) {
            handleUserViewProfile(chatMessageSource.getDefaultChatter(this.userManager.getUserID()));
        } else if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.Object && (chatMessageSource instanceof ChatMessageSourceObject)) {
            ChatMessageSourceObject chatMessageSourceObject = (ChatMessageSourceObject) chatMessageSource;
            SLAgentCircuit data = this.agentCircuit.getData();
            if (data != null && (objectLocalID = data.getGridConnection().parcelInfo.getObjectLocalID(chatMessageSourceObject.uuid)) != -1) {
                DetailsActivity.showEmbeddedDetails(getActivity(), ObjectDetailsFragment.class, ObjectDetailsFragment.makeSelection(this.userManager.getUserID(), objectLocalID));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onVoiceLoginStatusChanged(Boolean bool) {
        super.m573com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragmentmthref0(bool);
        updateVrModeControls();
    }
}
