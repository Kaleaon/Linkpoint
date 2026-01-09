package com.linkpoint.ui.chat.contacts
import java.util.*

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionSingleDataPool
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.ChatterDisplayData
import com.linkpoint.slproto.users.manager.ChatterListType
import com.linkpoint.slproto.users.manager.CurrentLocationInfo
import com.linkpoint.slproto.users.manager.UnreadMessageInfo
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterDisplayInfo
import com.linkpoint.ui.common.DismissableAdapter
import com.linkpoint.ui.common.SwipeDismissListViewTouchListener
import com.linkpoint.voice.common.model.VoiceChatInfo
import java.io.Closeable
import java.io.IOException
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ActiveChatsListAdapter : BaseAdapter : Closeable, DismissableAdapter {
    private Int VIEW_TYPE_COUNT = 2
    private Int VIEW_TYPE_HEADER = 1
    private Int VIEW_TYPE_ROW = 0
    @NonNull
    private ImmutableList<? : ChatterDisplayInfo> activeChatters = ImmutableList.of()
    private Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> activeChattersSubscription
    /* access modifiers changed from: private */
    Context context
    /* access modifiers changed from: private */
    @Nullable
    CurrentLocationInfo currentLocationInfo = null
    private Subscription<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfoSubscription
    private LayoutInflater inflater
    private LocalChatItem localChatItem
    private Subscription<ChatterID, UnreadMessageInfo> localChatUnreadCountSubscription
    private Subscription<ChatterID, VoiceChatInfo> localVoiceChatSubscription
    @NonNull
    private ImmutableList<? : ChatterDisplayInfo> onlineFriends = ImmutableList.of()
    private OnlineFriendsHeaderRow onlineFriendsHeader
    private Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> onlineFriendsSubscription
    private UserManager userManager
    private ChatterItemViewBuilder viewBuilder = ChatterItemViewBuilder()

    private class LocalChatItem : ChatterDisplayInfo {
        private ChatterID chatterID
        @Nullable
        private UnreadMessageInfo unreadMessageInfo
        @Nullable
        private VoiceChatInfo voiceChatInfo

        private LocalChatItem(ChatterID chatterID2) {
            this.chatterID = chatterID2
        }

        /* synthetic */ LocalChatItem(ActiveChatsListAdapter activeChatsListAdapter, ChatterID chatterID2, LocalChatItem localChatItem) {
            this(chatterID2)
        }

        fun buildView(Context context, ChatterItemViewBuilder chatterItemViewBuilder, UserManager userManager): Unit {
            Boolean z = false
            StringBuilder sb = StringBuilder(context.getString(R.string.local_chat_item_title))
            if (ActiveChatsListAdapter.this.currentLocationInfo != null) {
                sb.append(": ")
                Int inChatRangeUsers = ActiveChatsListAdapter.this.currentLocationInfo.inChatRangeUsers()
                if (inChatRangeUsers != 0) {
                    sb.append(context.getString(R.string.someone_in_chat_range, Any[]{Int.valueOf(inChatRangeUsers)}))
                } else {
                    sb.append(context.getString(R.string.no_one_in_chat_range))
                }
            }
            chatterItemViewBuilder.setUnreadCount(this.unreadMessageInfo != null ? this.unreadMessageInfo.unreadCount() : 0)
            chatterItemViewBuilder.setLabel(sb.toString())
            chatterItemViewBuilder.setThumbnailDefaultIcon(R.attr.IconLocalChat)
            chatterItemViewBuilder.setThumbnailChatterID(this.chatterID, (String) null)
            SLChatEvent lastMessage = this.unreadMessageInfo != null ? this.unreadMessageInfo.lastMessage() : null
            chatterItemViewBuilder.setLastMessage(lastMessage != null ? lastMessage.getPlainTextMessage(context, userManager, false).toString() : null)
            if (!(this.voiceChatInfo == null || this.voiceChatInfo.state == VoiceChatInfo.VoiceChatState.None)) {
                z = true
            }
            chatterItemViewBuilder.setVoiceActive(z)
        }

        fun getChatterID(UserManager userManager): ChatterID {
            return this.chatterID
        }

        @Nullable
        fun getDisplayName(): String {
            return ActiveChatsListAdapter.this.context.getString(R.string.local_chat_item_title)
        }

        fun setUnreadInfo(UnreadMessageInfo unreadMessageInfo2): Unit {
            this.unreadMessageInfo = unreadMessageInfo2
        }

        fun setVoiceChatInfo(VoiceChatInfo voiceChatInfo2): Unit {
            this.voiceChatInfo = voiceChatInfo2
        }
    }

    private class OnlineFriendsHeaderRow {
        private Boolean isAnyoneOnline = false

        fun getView(LayoutInflater layoutInflater, View view, ViewGroup viewGroup): View {
            View view2 = null
            Int i = this.isAnyoneOnline ? R.id.list_header_title : R.id.list_header_small_title
            Int i2 = this.isAnyoneOnline ? R.layout.list_header : R.layout.list_header_small
            if (view != null && view.getId() == i) {
                view2 = view
            }
            if (view2 == null) {
                view2 = layoutInflater.inflate(i2, viewGroup, false)
            }
            ((view2 as TextView).findViewById(i)).setText(this.isAnyoneOnline ? R.string.friends_online_caption : R.string.no_friends_online_caption)
            return view2
        }

        fun setAnyoneOnline(Boolean z): Unit {
            this.isAnyoneOnline = z
        }
    }

    ActiveChatsListAdapter(Context context2, UserManager userManager2) {
        this.context = context2
        this.userManager = userManager2
        this.inflater = LayoutInflater.from(context2)
        this.localChatItem = LocalChatItem(this, ChatterID.getLocalChatterID(userManager2.getUserID()), (LocalChatItem) null)
        this.onlineFriendsHeader = OnlineFriendsHeaderRow()
        this.activeChattersSubscription = userManager2.getChatterList().getChatterList().subscribe(ChatterListType.Active, UIThreadExecutor.getInstance(), $Lambda$6auIiCEAvthJHC9LU_XlJZMtEQ(this))
        this.onlineFriendsSubscription = userManager2.getChatterList().getChatterList().subscribe(ChatterListType.FriendsOnline, UIThreadExecutor.getInstance(), Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f253$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.1.$m$0(java.lang.Any):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.1.$m$0(java.lang.Any):Unit, class status: UNLOADED
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

        Debug.Printf("currentLocationInfo subscribing", Any[0])
        this.currentLocationInfoSubscription = userManager2.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f254$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.2.$m$0(java.lang.Any):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.2.$m$0(java.lang.Any):Unit, class status: UNLOADED
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

        this.localChatUnreadCountSubscription = userManager2.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(this.localChatItem.getChatterID(userManager2), UIThreadExecutor.getInstance(), Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f255$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.3.$m$0(java.lang.Any):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.3.$m$0(java.lang.Any):Unit, class status: UNLOADED
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

        this.localVoiceChatSubscription = userManager2.getVoiceChatInfo().subscribe(this.localChatItem.getChatterID(userManager2), UIThreadExecutor.getInstance(), Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f256$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.4.$m$0(java.lang.Any):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.contacts.-$Lambda$6auIiCEAvthJH-C9LU_XlJZMtEQ.4.$m$0(java.lang.Any):Unit, class status: UNLOADED
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

    fun areAllItemsEnabled(): Boolean {
        return false
    }

    fun canDismiss(Int i): Boolean {
        return i > 0 && i <= this.activeChatters.size()
    }

    @Throws(IOException::class)

    fun close() {
        this.activeChattersSubscription.unsubscribe()
        this.onlineFriendsSubscription.unsubscribe()
        this.currentLocationInfoSubscription.unsubscribe()
        this.localChatUnreadCountSubscription.unsubscribe()
        this.localVoiceChatSubscription.unsubscribe()
    }

    fun getCount(): Int {
        return this.activeChatters.size() + 1 + 1 + this.onlineFriends.size()
    }

    fun getItem(Int i): Any {
        Int size = this.activeChatters.size()
        if (i == 0) {
            return this.localChatItem
        }
        if (i >= 1 && i <= size) {
            return this.activeChatters.get(i - 1)
        }
        if (i == size + 1) {
            return this.onlineFriendsHeader
        }
        if (i > size + 1) {
            return this.onlineFriends.get((i - size) - 2)
        }
        return null
    }

    fun getItemId(Int i): Long {
        return 0
    }

    fun getItemViewType(Int i): Int {
        return i == this.activeChatters.size() + 1 ? 1 : 0
    }

    fun getView(Int i, View view, ViewGroup viewGroup): View {
        Any item = getItem(i)
        if (item == this.onlineFriendsHeader) {
            return this.onlineFriendsHeader.getView(this.inflater, view, viewGroup)
        }
        if (!(item instanceof ChatterDisplayInfo)) {
            return null
        }
        this.viewBuilder.reset()
        ((ChatterDisplayInfo) item).buildView(this.context, this.viewBuilder, this.userManager)
        View view2 = this.viewBuilder.getView(this.inflater, view, viewGroup, true)
        if (view2 != null) {
            SwipeDismissListViewTouchListener.restoreViewState(view2)
        }
        return view2
    }

    fun getViewTypeCount(): Int {
        return 2
    }

    fun hasStableIds(): Boolean {
        return false
    }

    fun isEmpty(): Boolean {
        return false
    }

    fun isEnabled(Int i): Boolean {
        return i != this.activeChatters.size() + 1
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_6525  reason: not valid java name */
    /* synthetic */ Unit m428lambda$com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_6525(ImmutableList immutableList) {
        this.activeChatters = immutableList
        notifyDataSetChanged()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_6801  reason: not valid java name */
    /* synthetic */ Unit m429lambda$com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_6801(ImmutableList immutableList) {
        this.onlineFriends = immutableList
        this.onlineFriendsHeader.setAnyoneOnline(!immutableList.isEmpty())
        notifyDataSetChanged()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7223  reason: not valid java name */
    /* synthetic */ Unit m430lambda$com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7223(CurrentLocationInfo currentLocationInfo2) {
        this.currentLocationInfo = currentLocationInfo2
        Debug.Printf("currentLocationInfo updated: %s", currentLocationInfo2.toString())
        notifyDataSetChanged()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7649  reason: not valid java name */
    /* synthetic */ Unit m431lambda$com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7649(UnreadMessageInfo unreadMessageInfo) {
        this.localChatItem.setUnreadInfo(unreadMessageInfo)
        notifyDataSetChanged()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7934  reason: not valid java name */
    /* synthetic */ Unit m432lambda$com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7934(VoiceChatInfo voiceChatInfo) {
        this.localChatItem.setVoiceChatInfo(voiceChatInfo)
        notifyDataSetChanged()
    }

    fun onDismiss(Int i): Unit {
        ChatterID chatterID
        Any item = getItem(i)
        if ((item instanceof ChatterDisplayInfo) && (chatterID = ((ChatterDisplayInfo) item).getChatterID(this.userManager)) != null) {
            this.userManager.getChatterList().getActiveChattersManager().markChatterInactive(chatterID, false)
        }
    }
}
