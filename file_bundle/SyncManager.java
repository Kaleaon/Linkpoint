package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;
import de.greenrobot.dao.query.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyncManager {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f226comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues = null;
    private static final int MAX_MESSAGES_PER_BATCH = 100;
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nonnull
    private final ChatterDao chatterDao;
    private ChatterNameRetriever chatterNameRetriever = null;
    @Nonnull
    private final Context context;
    private final DateFormat dateFormat;
    @Nonnull
    private final Executor dbExecutor;
    private final Set<String> flushChatterNames = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Map<ChatterID, ChatterNameRetriever> flushChatters = new ConcurrentHashMap();
    private long lastConfirmedMessageID = 0;
    @Nonnull
    private final String localChatName;
    private final Query<ChatMessage> messagesQuery;
    private ChatterNameRetriever myNameRetriever = null;
    private final AtomicBoolean needsStopSyncing = new AtomicBoolean(false);
    private final AtomicBoolean syncMessageSent = new AtomicBoolean(false);
    private final AtomicReference<CloudSyncServiceConnection> syncServiceConnection = new AtomicReference<>();
    private final AtomicBoolean syncingEnabled = new AtomicBoolean(false);
    @Nonnull
    private final UserManager userManager;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m355getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues() {
        if (f226comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues != null) {
            return f226comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues;
        }
        int[] iArr = new int[ChatterID.ChatterType.values().length];
        try {
            iArr[ChatterID.ChatterType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterID.ChatterType.Local.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterID.ChatterType.User.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f226comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues = iArr;
        return iArr;
    }

    @SuppressLint({"SimpleDateFormat"})
    SyncManager(@Nonnull UserManager userManager2) {
        this.userManager = userManager2;
        this.dbExecutor = userManager2.getDatabaseExecutor();
        DaoSession daoSession = userManager2.getDaoSession();
        this.chatMessageDao = daoSession.getChatMessageDao();
        this.chatterDao = daoSession.getChatterDao();
        this.context = LumiyaApp.getContext();
        this.localChatName = this.context.getString(R.string.local_chat_title);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.messagesQuery = this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.Id.gt((Object) null), ChatMessageDao.Properties.SyncedToGoogleDrive.eq(false)).orderAsc(ChatMessageDao.Properties.Id).limit(100).build();
    }

    /* access modifiers changed from: private */
    /* renamed from: onChatterNameRetrieved */
    public void m361com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref5(ChatterNameRetriever chatterNameRetriever2) {
        this.dbExecutor.execute(new Runnable(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f157$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.3.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.3.$m$0():void, class status: UNLOADED
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

    /* access modifiers changed from: private */
    /* renamed from: onFlushChatterNameRetrieved */
    public void m362com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref6(ChatterNameRetriever chatterNameRetriever2) {
        String resolvedName = chatterNameRetriever2.getResolvedName();
        this.flushChatters.remove(chatterNameRetriever2.chatterID);
        chatterNameRetriever2.dispose();
        if (!Strings.isNullOrEmpty(resolvedName) && this.flushChatterNames.add(resolvedName)) {
            m360com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref4();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onMyNameRetrieved */
    public void m357com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref1(ChatterNameRetriever chatterNameRetriever2) {
        this.dbExecutor.execute(new Runnable(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f158$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.4.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.4.$m$0():void, class status: UNLOADED
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

    /* access modifiers changed from: private */
    /* renamed from: processMessagesFlushed */
    public void m366lambda$com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_9859(ImmutableList<Long> immutableList) {
        for (Long load : immutableList) {
            ChatMessage chatMessage = (ChatMessage) this.chatMessageDao.load(load);
            if (chatMessage != null && !chatMessage.getSyncedToGoogleDrive()) {
                chatMessage.setSyncedToGoogleDrive(true);
                this.chatMessageDao.update(chatMessage);
            }
        }
    }

    @Nullable
    private String resolveChatterName(@Nonnull Chatter chatter) {
        if (chatter.getType() != ChatterID.ChatterType.User.ordinal() && chatter.getType() != ChatterID.ChatterType.Group.ordinal()) {
            return this.localChatName;
        }
        ChatterID fromDatabaseObject = ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter);
        if (this.chatterNameRetriever == null || (!this.chatterNameRetriever.chatterID.equals(fromDatabaseObject))) {
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
            }
            this.chatterNameRetriever = new ChatterNameRetriever(fromDatabaseObject, new ChatterNameRetriever.OnChatterNameUpdated(this) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f153$f0;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
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
			at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
			at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
			at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
			at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
			at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
			at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
			at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
			at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
			at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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

            }, this.dbExecutor, true);
        }
        return this.chatterNameRetriever.getResolvedName();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x014f  */
    /* renamed from: syncMoreMessages */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void m360com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref4() {
        if (!this.syncMessageSent.getAndSet(true)) {
            boolean keepSyncMessageSent = false;
            String myName = null;
            if (this.myNameRetriever == null) {
                this.myNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(this.userManager.getUserID(), this.userManager.getUserID()), new ChatterNameRetriever.OnChatterNameUpdated() {
                    @Override
                    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                        SyncManager.this.m368x9b8293a7(chatterNameRetriever);
                    }
                }, this.dbExecutor, true);
            }
            myName = this.myNameRetriever.getResolvedName();
            if (myName != null) {
                Query<ChatMessage> query = this.messagesQuery.forCurrentThread();
                query.setParameter(0, Long.valueOf(this.lastConfirmedMessageID));
                de.greenrobot.dao.query.LazyList<ChatMessage> messages = query.listLazy();
                ImmutableList.Builder<com.lumiyaviewer.lumiya.cloud.common.LogChatMessage> builder = ImmutableList.builder();
                int count = 0;
                long lastMessageId = 0;
                for (ChatMessage chatMessage : messages) {
                    com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent chatEvent = com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.loadFromDatabaseObject(chatMessage, this.userManager.getUserID());
                    if (chatEvent != null) {
                        Chatter chatter = this.chatterDao.load(Long.valueOf(chatMessage.getChatterID()));
                        if (chatter != null) {
                            String chatterName = resolveChatterName(chatter);
                            if (chatterName == null) {
                                break;
                            }
                            CharSequence text = chatEvent.getPlainTextMessage(this.context, this.userManager, false);
                            com.lumiyaviewer.lumiya.cloud.common.LogChatMessage logChatMessage = new com.lumiyaviewer.lumiya.cloud.common.LogChatMessage(chatter.getType(), chatter.getUuid(), chatMessage.getId().longValue(), chatterName, "[" + this.dateFormat.format(chatMessage.getTimestamp()) + "] " + text);
                            builder.add(logChatMessage);
                            lastMessageId = logChatMessage.messageID;
                            count++;
                            if (count >= 100) {
                                break;
                            }
                        }
                    }
                }
                messages.close();
                if (count > 0) {
                    com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch batch = new com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch(this.userManager.getUserID(), myName, builder.build(), lastMessageId);
                    CloudSyncServiceConnection connection = this.syncServiceConnection.get();
                    if (connection != null) {
                        keepSyncMessageSent = connection.sendMessage(com.lumiyaviewer.lumiya.cloud.common.MessageType.LogMessageBatch, batch);
                    }
                }
                if (!this.flushChatterNames.isEmpty()) {
                    CloudSyncServiceConnection connection2 = this.syncServiceConnection.get();
                    if (connection2 != null) {
                        java.util.Iterator<String> iterator = this.flushChatterNames.iterator();
                        if (iterator.hasNext()) {
                            String chatterName2 = iterator.next();
                            iterator.remove();
                            connection2.sendMessage(com.lumiyaviewer.lumiya.cloud.common.MessageType.LogFlushMessages, new com.lumiyaviewer.lumiya.cloud.common.LogFlushMessages(this.userManager.getUserID(), myName, chatterName2));
                        }
                    }
                }
            }
            this.syncMessageSent.set(keepSyncMessageSent);
        }
        if (this.needsStopSyncing.getAndSet(false)) {
            this.syncingEnabled.set(false);
            CloudSyncServiceConnection connection3 = this.syncServiceConnection.getAndSet(null);
            this.syncMessageSent.set(false);
            if (connection3 != null) {
                connection3.sendMessage(com.lumiyaviewer.lumiya.cloud.common.MessageType.LogFlushMessages, new com.lumiyaviewer.lumiya.cloud.common.LogFlushMessages(this.userManager.getUserID(), null, null));
                connection3.disconnect();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void flushChatter(ChatterID chatterID) {
        if (this.syncingEnabled.get()) {
            this.dbExecutor.execute(new Runnable(this, chatterID) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f162$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f163$f1;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.8.$m$0():void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.8.$m$0():void, class status: UNLOADED
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
			at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
			at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
			at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10038  reason: not valid java name */
    public /* synthetic */ void m363lambda$com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10038() {
        this.needsStopSyncing.set(true);
        m360com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref4();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10254  reason: not valid java name */
    public /* synthetic */ void m364lambda$com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10254(ChatterID chatterID) {
        switch (m355getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues()[chatterID.getChatterType().ordinal()]) {
            case 1:
            case 3:
                if (!this.flushChatters.containsKey(chatterID)) {
                    new ChatterNameRetriever(chatterID, new $Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8(this), this.dbExecutor, false).subscribe();
                    return;
                }
                return;
            case 2:
                if (this.flushChatterNames.add(this.localChatName)) {
                    m360com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref4();
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_9602  reason: not valid java name */
    public /* synthetic */ void m365lambda$com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_9602(long j) {
        this.lastConfirmedMessageID = j;
        this.syncMessageSent.set(false);
        m360com_lumiyaviewer_lumiya_slproto_users_manager_SyncManagermthref4();
    }

    public void onMessagesFlushed(ImmutableList<Long> immutableList) {
        this.dbExecutor.execute(new Runnable(this, immutableList) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f164$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f165$f1;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.9.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.9.$m$0():void, class status: UNLOADED
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

    public void onMessagesWritten(long j) {
        this.dbExecutor.execute(new Runnable(j, this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ long f154$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f155$f1;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.10.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.10.$m$0():void, class status: UNLOADED
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

    public void startSyncing(CloudSyncServiceConnection cloudSyncServiceConnection) {
        this.syncServiceConnection.set(cloudSyncServiceConnection);
        this.syncingEnabled.set(true);
        this.needsStopSyncing.set(false);
        this.dbExecutor.execute(new Runnable(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f159$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.5.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.5.$m$0():void, class status: UNLOADED
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

    public void stopSyncing() {
        Debug.Printf("SyncManager: requested to stop syncing", new Object[0]);
        this.dbExecutor.execute(new Runnable(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f160$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.6.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.6.$m$0():void, class status: UNLOADED
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

    /* access modifiers changed from: package-private */
    public void syncNewMessages() {
        if (this.syncingEnabled.get()) {
            this.dbExecutor.execute(new Runnable(this) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f161$f0;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.7.$m$0():void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.7.$m$0():void, class status: UNLOADED
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
			at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
			at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
			at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
			at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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
}
