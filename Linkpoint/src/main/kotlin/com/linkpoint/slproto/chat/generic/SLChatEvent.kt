package com.linkpoint.slproto.chat.generic

import android.content.ClipData
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.ClipboardManager
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.SLChatBalanceChangedEvent
import com.linkpoint.slproto.chat.SLChatFriendshipOfferedEvent
import com.linkpoint.slproto.chat.SLChatFriendshipResultEvent
import com.linkpoint.slproto.chat.SLChatGroupInvitationEvent
import com.linkpoint.slproto.chat.SLChatGroupInvitationSentEvent
import com.linkpoint.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent
import com.linkpoint.slproto.chat.SLChatInventoryItemOfferedByYouEvent
import com.linkpoint.slproto.chat.SLChatInventoryItemOfferedEvent
import com.linkpoint.slproto.chat.SLChatLureEvent
import com.linkpoint.slproto.chat.SLChatLureRequestEvent
import com.linkpoint.slproto.chat.SLChatLureRequestedEvent
import com.linkpoint.slproto.chat.SLChatOnlineOfflineEvent
import com.linkpoint.slproto.chat.SLChatPermissionRequestEvent
import com.linkpoint.slproto.chat.SLChatScriptDialog
import com.linkpoint.slproto.chat.SLChatSessionMarkEvent
import com.linkpoint.slproto.chat.SLChatSystemMessageEvent
import com.linkpoint.slproto.chat.SLChatTextBoxDialog
import com.linkpoint.slproto.chat.SLChatTextEvent
import com.linkpoint.slproto.chat.SLEnableRLVOfferEvent
import com.linkpoint.slproto.chat.SLMissedVoiceCallEvent
import com.linkpoint.slproto.chat.SLVoiceUpgradeEvent
import com.linkpoint.slproto.chat.generic.ChatEventViewHolder
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatEventTimestampUpdater
import com.linkpoint.ui.chat.ChatterPicView
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class SLChatEvent : View.OnLongClickListener {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ Int[] f72comlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues = null

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ Int[] f73comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = null
    const val Int CHAT_AUDIBLE_BARELY = 0
    const val Int CHAT_AUDIBLE_FULLY = 1
    const val Int CHAT_AUDIBLE_NOT = -1
    const val Int CHAT_SOURCE_AGENT = 1
    const val Int CHAT_SOURCE_OBJECT = 2
    const val Int CHAT_SOURCE_SYSTEM = 0
    const val Int CHAT_SOURCE_UNKNOWN = 3
    const val Int CHAT_TYPE_DEBUG_MSG = 6
    const val Int CHAT_TYPE_NORMAL = 1
    const val Int CHAT_TYPE_OWNER = 8
    const val Int CHAT_TYPE_REGION = 7
    const val Int CHAT_TYPE_SHOUT = 2
    const val Int CHAT_TYPE_START = 4
    const val Int CHAT_TYPE_STOP = 5
    const val Int CHAT_TYPE_WHISPER = 0
    const val Int IM_BUSY_AUTO_RESPONSE = 20
    const val Int IM_CONSOLE_AND_CHAT_HISTORY = 21
    const val Int IM_FRIENDSHIP_ACCEPTED = 39
    const val Int IM_FRIENDSHIP_DECLINED = 40
    const val Int IM_FRIENDSHIP_OFFERED = 38
    const val Int IM_FROM_TASK = 19
    const val Int IM_FROM_TASK_AS_ALERT = 31
    const val Int IM_GODLIKE_LURE_USER = 25
    const val Int IM_GOTO_URL = 28
    const val Int IM_GROUP_ELECTION_DEPRECATED = 27
    const val Int IM_GROUP_INVITATION = 3
    const val Int IM_GROUP_INVITATION_ACCEPT = 35
    const val Int IM_GROUP_INVITATION_DECLINE = 36
    const val Int IM_GROUP_MESSAGE_DEPRECATED = 8
    const val Int IM_GROUP_NOTICE = 32
    const val Int IM_GROUP_NOTICE_INVENTORY_ACCEPTED = 33
    const val Int IM_GROUP_NOTICE_INVENTORY_DECLINED = 34
    const val Int IM_GROUP_NOTICE_REQUESTED = 37
    const val Int IM_GROUP_VOTE = 7
    const val Int IM_INVENTORY_ACCEPTED = 5
    const val Int IM_INVENTORY_DECLINED = 6
    const val Int IM_INVENTORY_OFFERED = 4
    const val Int IM_LURE_ACCEPTED = 23
    const val Int IM_LURE_DECLINED = 24
    const val Int IM_LURE_USER = 22
    const val Int IM_MESSAGEBOX = 1
    const val Int IM_MESSAGEBOX_COUNTDOWN = 2
    const val Int IM_NEW_USER_DEFAULT = 12
    const val Int IM_NOTHING_SPECIAL = 0
    const val Int IM_SESSION_CONFERENCE_START = 16
    const val Int IM_SESSION_GROUP_START = 15
    const val Int IM_SESSION_INVITE = 13
    const val Int IM_SESSION_LEAVE = 18
    const val Int IM_SESSION_P2P_INVITE = 14
    const val Int IM_SESSION_SEND = 17
    const val Int IM_TASK_INVENTORY_ACCEPTED = 10
    const val Int IM_TASK_INVENTORY_DECLINED = 11
    const val Int IM_TASK_INVENTORY_OFFERED = 9
    const val Int IM_TELEPORT_REQUEST = 26
    const val Int IM_TYPING_START = 41
    const val Int IM_TYPING_STOP = 42
    protected val UUID agentUUID
    protected val ChatMessage dbMessage
    private val Boolean isOffline
    private val Date originalTimestamp
    protected val ChatMessageSource source
    private val Date timestamp

    enum class ChatMessageType {
        Text,
        BalanceChanged,
        InventoryItemOffered,
        InventoryItemOfferedByGroupNotice,
        InventoryItemOfferedByYou,
        FriendshipOffered,
        FriendshipResult,
        GroupInvitation,
        GroupInvitationSent,
        Lure,
        LureRequested,
        LureRequest,
        WentOnline,
        WentOffline,
        PermissionRequest,
        ScriptDialog,
        TextBoxDialog,
        EnableRLVOffer,
        SessionMark,
        SystemMessage,
        VoiceUpgrade,
        MissedVoiceCall
        
        const val ChatMessageType[] VALUES = null

        static {
            VALUES = values()
        }
    }

    enum class ChatMessageViewType : ChatEventViewHolder.Factory {
        VIEW_TYPE_NORMAL(R.layout.chat_message, false, $Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls()),
        VIEW_TYPE_YESNO(R.layout.chat_message_yesno, false, ChatEventViewHolder.Factory() {
            private val /* synthetic */ com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.1.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.1.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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

            val com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder createViewHolder(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.1.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.1.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
        }),
        VIEW_TYPE_DIALOG(R.layout.chat_message_dialog, false, ChatEventViewHolder.Factory() {
            private val /* synthetic */ com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.2.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.2.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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

            val com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder createViewHolder(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.2.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.2.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
        }),
        VIEW_TYPE_TEXTBOX(R.layout.chat_message_textbox, true, ChatEventViewHolder.Factory() {
            private val /* synthetic */ com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.3.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.3.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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

            val com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder createViewHolder(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.3.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.3.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
        }),
        VIEW_TYPE_SESSION_MARK(R.layout.chat_message_session_mark, false, ChatEventViewHolder.Factory() {
            private val /* synthetic */ com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.4.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.4.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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

            val com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder createViewHolder(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.4.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.4.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
        }),
        VIEW_TYPE_PLAIN(R.layout.chat_message_plain, false, ChatEventViewHolder.Factory() {
            private val /* synthetic */ com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.5.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.5.$m$0(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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

            val com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder createViewHolder(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.5.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.5.createViewHolder(android.view.View, android.support.v7.widget.RecyclerView$Adapter):com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder, class status: UNLOADED
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
            	at jadx.core.codegen.ClassGen.addEnumFields(ClassGen.java:409)
            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:346)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
        
        const val ChatMessageViewType[] VALUES = null
        private val Boolean alwaysInflate
        private val Int resourceId
        private val ChatEventViewHolder.Factory viewHolderFactory

        static {
            VALUES = values()
        }

        private ChatMessageViewType(Int i, Boolean z, ChatEventViewHolder.Factory factory) {
            this.resourceId = i
            this.alwaysInflate = z
            this.viewHolderFactory = factory
        }

        val ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
            return this.viewHolderFactory.createViewHolder(view, adapter)
        }

        val Boolean getAlwaysInflate() {
            return this.alwaysInflate
        }

        val Int getResourceId() {
            return this.resourceId
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ Int[] m148getcomlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues() {
        if (f72comlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues != null) {
            return f72comlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues
        }
        Int[] iArr = Int[ChatMessageType.values().length]
        try {
            iArr[ChatMessageType.BalanceChanged.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatMessageType.EnableRLVOffer.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatMessageType.FriendshipOffered.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ChatMessageType.FriendshipResult.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ChatMessageType.GroupInvitation.ordinal()] = 5
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ChatMessageType.GroupInvitationSent.ordinal()] = 6
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ChatMessageType.InventoryItemOffered.ordinal()] = 7
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ChatMessageType.InventoryItemOfferedByGroupNotice.ordinal()] = 8
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ChatMessageType.InventoryItemOfferedByYou.ordinal()] = 9
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ChatMessageType.Lure.ordinal()] = 10
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ChatMessageType.LureRequest.ordinal()] = 11
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ChatMessageType.LureRequested.ordinal()] = 12
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ChatMessageType.MissedVoiceCall.ordinal()] = 13
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ChatMessageType.PermissionRequest.ordinal()] = 14
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ChatMessageType.ScriptDialog.ordinal()] = 15
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ChatMessageType.SessionMark.ordinal()] = 16
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ChatMessageType.SystemMessage.ordinal()] = 17
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ChatMessageType.Text.ordinal()] = 18
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ChatMessageType.TextBoxDialog.ordinal()] = 19
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ChatMessageType.VoiceUpgrade.ordinal()] = 20
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ChatMessageType.WentOffline.ordinal()] = 21
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ChatMessageType.WentOnline.ordinal()] = 22
        } catch (NoSuchFieldError e22) {
        }
        f72comlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues = iArr
        return iArr
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ Int[] m149getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues() {
        if (f73comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues != null) {
            return f73comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues
        }
        Int[] iArr = Int[ChatMessageSource.ChatMessageSourceType.values().length]
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.Group.ordinal()] = 25
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.Object.ordinal()] = 1
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.System.ordinal()] = 26
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.Unknown.ordinal()] = 27
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.User.ordinal()] = 2
        } catch (NoSuchFieldError e5) {
        }
        f73comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = iArr
        return iArr
    }

    public SLChatEvent(ChatMessage chatMessage, UUID uuid) {
        this.dbMessage = chatMessage
        this.timestamp = chatMessage.getTimestamp()
        this.isOffline = chatMessage.getIsOffline().booleanValue()
        this.originalTimestamp = chatMessage.getOrigTimestamp()
        this.source = ChatMessageSource.loadFrom(chatMessage)
        this.agentUUID = uuid
    }

    public SLChatEvent(ImprovedInstantMessage improvedInstantMessage, UUID uuid, ChatMessageSource chatMessageSource) {
        this.timestamp = Date()
        this.source = chatMessageSource
        this.agentUUID = uuid
        if (improvedInstantMessage == null) {
            this.isOffline = false
            this.originalTimestamp = this.timestamp
        } else if (improvedInstantMessage.MessageBlock_Field.Offline == 0 || improvedInstantMessage.MessageBlock_Field.Dialog == 9) {
            this.isOffline = false
            this.originalTimestamp = this.timestamp
        } else {
            this.isOffline = true
            this.originalTimestamp = Date(((Long) improvedInstantMessage.MessageBlock_Field.Timestamp) * 1000)
        }
        this.dbMessage = null
    }

    public SLChatEvent(ChatMessageSource chatMessageSource, UUID uuid) {
        this.timestamp = Date()
        this.originalTimestamp = this.timestamp
        this.isOffline = false
        this.source = chatMessageSource
        this.agentUUID = uuid
        this.dbMessage = null
    }

    @JvmStatic
    ChatEventViewHolder createViewHolder(LayoutInflater layoutInflater, Int i, ViewGroup viewGroup, RecyclerView.Adapter adapter) {
        ChatMessageViewType chatMessageViewType = ChatMessageViewType.VALUES[i]
        return chatMessageViewType.createViewHolder(layoutInflater.inflate(chatMessageViewType.getResourceId(), viewGroup, false), adapter)
    }

    @JvmStatic
    SLChatEvent loadFromDatabaseObject(ChatMessage chatMessage, UUID uuid) {
        if (chatMessage == null) {
            return null
        }
        switch (m148getcomlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues()[ChatMessageType.VALUES[chatMessage.getMessageType()].ordinal()]) {
            case 1:
                return SLChatBalanceChangedEvent(chatMessage, uuid)
            case 2:
                return SLEnableRLVOfferEvent(chatMessage, uuid)
            case 3:
                return SLChatFriendshipOfferedEvent(chatMessage, uuid)
            case 4:
                return SLChatFriendshipResultEvent(chatMessage, uuid)
            case 5:
                return SLChatGroupInvitationEvent(chatMessage, uuid)
            case 6:
                return SLChatGroupInvitationSentEvent(chatMessage, uuid)
            case 7:
                return SLChatInventoryItemOfferedEvent(chatMessage, uuid)
            case 8:
                return SLChatInventoryItemOfferedByGroupNoticeEvent(chatMessage, uuid)
            case 9:
                return SLChatInventoryItemOfferedByYouEvent(chatMessage, uuid)
            case 10:
                return SLChatLureEvent(chatMessage, uuid)
            case 11:
                return SLChatLureRequestEvent(chatMessage, uuid)
            case 12:
                return SLChatLureRequestedEvent(chatMessage, uuid)
            case 13:
                return SLMissedVoiceCallEvent(chatMessage, uuid)
            case 14:
                return SLChatPermissionRequestEvent(chatMessage, uuid)
            case 15:
                return SLChatScriptDialog(chatMessage, uuid)
            case 16:
                return SLChatSessionMarkEvent(chatMessage, uuid)
            case 17:
                return SLChatSystemMessageEvent(chatMessage, uuid)
            case 18:
                return SLChatTextEvent(chatMessage, uuid)
            case 19:
                return SLChatTextBoxDialog(chatMessage, uuid)
            case 20:
                return SLVoiceUpgradeEvent(chatMessage, uuid)
            case 21:
                return SLChatOnlineOfflineEvent(chatMessage, uuid, false)
            case 22:
                return SLChatOnlineOfflineEvent(chatMessage, uuid, true)
            default:
                return null
        }
    }

    public Unit bindViewHolder(ChatEventViewHolder chatEventViewHolder, UserManager userManager, ChatEventTimestampUpdater chatEventTimestampUpdater) {
        ChatterPicView chatterPicView = chatEventViewHolder.chatSourceIcon
        Boolean equal = this.source.getSourceType() == ChatMessageSource.ChatMessageSourceType.User ? Objects.equal(this.source.getSourceUUID(), this.agentUUID) : false
        if (chatEventViewHolder.chatSourceIconRight != null && equal) {
            chatterPicView = chatEventViewHolder.chatSourceIconRight
        }
        if (!(chatEventViewHolder.chatSourceIcon == null || chatEventViewHolder.chatSourceIcon == chatterPicView)) {
            chatEventViewHolder.chatSourceIcon.setChatterID((ChatterID) null, (String) null)
            chatEventViewHolder.chatSourceIcon.setDefaultIcon(-1, false)
            chatEventViewHolder.chatSourceIcon.setForceIcon(-1)
            chatEventViewHolder.chatSourceIcon.setVisibility(8)
            chatEventViewHolder.chatSourceIcon.setAttachedMessageSource((ChatMessageSource) null)
        }
        if (!(chatEventViewHolder.chatSourceIconRight == null || chatEventViewHolder.chatSourceIconRight == chatterPicView)) {
            chatEventViewHolder.chatSourceIconRight.setChatterID((ChatterID) null, (String) null)
            chatEventViewHolder.chatSourceIconRight.setDefaultIcon(-1, false)
            chatEventViewHolder.chatSourceIconRight.setForceIcon(-1)
            chatEventViewHolder.chatSourceIconRight.setVisibility(8)
            chatEventViewHolder.chatSourceIconRight.setAttachedMessageSource((ChatMessageSource) null)
        }
        if (chatEventViewHolder.bubbleView != null) {
            if (equal) {
                chatEventViewHolder.bubbleView.setBackgroundResource(R.drawable.msg_bubble_right)
            } else {
                chatEventViewHolder.bubbleView.setBackgroundResource(R.drawable.msg_bubble_left)
            }
            TypedValue typedValue = TypedValue()
            chatEventViewHolder.bubbleView.getContext().getTheme().resolveAttribute(equal ? R.attr.chatBubbleMyBackground : R.attr.chatBubbleBackground, typedValue, true)
            Drawable background = chatEventViewHolder.bubbleView.getBackground()
            if (background != null) {
                background.setColorFilter(typedValue.data, PorterDuff.Mode.MULTIPLY)
            }
            chatEventViewHolder.bubbleView.setOnLongClickListener(this)
        }
        if (chatterPicView != null) {
            switch (m149getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues()[this.source.getSourceType().ordinal()]) {
                case 1:
                    chatterPicView.setChatterID((ChatterID) null, (String) null)
                    chatterPicView.setForceIcon(R.drawable.inv_object)
                    chatterPicView.setVisibility(0)
                    chatterPicView.setAttachedMessageSource(this.source)
                    break
                case 2:
                    UUID sourceUUID = this.source.getSourceUUID()
                    if (sourceUUID == null) {
                        chatterPicView.setChatterID((ChatterID) null, (String) null)
                        chatterPicView.setDefaultIcon(-1, false)
                        chatterPicView.setForceIcon(-1)
                        chatterPicView.setVisibility(8)
                        break
                    } else {
                        Debug.Printf("chatterBindPic: name %s, sourceUUID %s", this.source.getSourceName(userManager), sourceUUID.toString())
                        chatterPicView.setChatterID(ChatterID.getUserChatterID(userManager.getUserID(), sourceUUID), this.source.getSourceName(userManager))
                        chatterPicView.setVisibility(0)
                        chatterPicView.setAttachedMessageSource(this.source)
                        break
                    }
                default:
                    chatterPicView.setChatterID((ChatterID) null, (String) null)
                    chatterPicView.setDefaultIcon(-1, false)
                    chatterPicView.setForceIcon(-1)
                    chatterPicView.setVisibility(8)
                    chatterPicView.setAttachedMessageSource((ChatMessageSource) null)
                    break
            }
        }
        TextView textView = chatEventViewHolder.timestampView
        if (textView != null) {
            if (GlobalOptions.getInstance().getShowTimestamps()) {
                chatEventViewHolder.setupTimestampUpdate(textView.getContext(), this.timestamp.getTime())
                if (chatEventTimestampUpdater != null) {
                    chatEventTimestampUpdater.addViewHolder(chatEventViewHolder)
                }
            } else {
                textView.setVisibility(8)
            }
        }
        TextView textView2 = chatEventViewHolder.textView
        if (textView2 != null) {
            String sourceName = this.source.getSourceName(userManager)
            String text = getText(textView2.getContext(), userManager)
            SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder()
            if (!Strings.isNullOrEmpty(sourceName)) {
                spannableStringBuilder.append(sourceName)
                if (!Strings.isNullOrEmpty(text)) {
                    if (isActionMessage(userManager)) {
                        spannableStringBuilder.append(" ")
                    } else {
                        spannableStringBuilder.append(": ")
                    }
                    spannableStringBuilder.append(text)
                }
                spannableStringBuilder.setSpan(StyleSpan(1), 0, sourceName.length(), 33)
            } else if (!Strings.isNullOrEmpty(text)) {
                spannableStringBuilder.append(text)
            }
            if (this.isOffline) {
                String str = " (sent at " + DateFormat.getDateTimeInstance(2, 2).format(this.originalTimestamp) + ")"
                spannableStringBuilder.append(str)
                spannableStringBuilder.setSpan(StyleSpan(2), spannableStringBuilder.length() - str.length(), spannableStringBuilder.length(), 33)
            }
            try {
                textView2.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE)
            } catch (Exception e) {
                textView2.setText(spannableStringBuilder.toString())
            }
        }
    }

    public UUID getAgentUUID() {
        return this.agentUUID
    }

    public ChatMessage getDatabaseObject() {
        ChatMessage chatMessage = this.dbMessage
        if (chatMessage == null) {
            chatMessage = ChatMessage()
        }
        serializeToDatabaseObject(chatMessage)
        return chatMessage
    }

    /* access modifiers changed from: protected */
    public abstract ChatMessageType getMessageType()

    public CharSequence getPlainTextMessage(Context context, UserManager userManager, Boolean z) {
        return getPlainTextMessage(context, userManager, z, ": ", " ")
    }

    public CharSequence getPlainTextMessage(Context context, UserManager userManager, Boolean z, String str, String str2) {
        String sourceName = (!z || !(isActionMessage(userManager) ^ true)) ? this.source.getSourceName(userManager) : null
        String text = getText(context, userManager)
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder()
        if (Strings.isNullOrEmpty(sourceName)) {
            return text
        }
        spannableStringBuilder.append(sourceName)
        if (!Strings.isNullOrEmpty(text)) {
            if (isActionMessage(userManager)) {
                spannableStringBuilder.append(str2)
            } else {
                spannableStringBuilder.append(str)
            }
            spannableStringBuilder.append(text)
        }
        spannableStringBuilder.setSpan(StyleSpan(1), 0, sourceName.length(), 33)
        return spannableStringBuilder
    }

    public ChatMessageSource getSource() {
        return this.source
    }

    /* access modifiers changed from: protected */
    public abstract String getText(Context context, UserManager userManager)

    public Date getTimestamp() {
        return this.timestamp
    }

    public abstract ChatMessageViewType getViewType()

    /* access modifiers changed from: protected */
    public abstract Boolean isActionMessage(UserManager userManager)

    public Boolean isObjectPopup() {
        return false
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent_21084  reason: not valid java name */
    public /* synthetic */ Boolean m150lambda$com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent_21084(Context context, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_copy_message_text:
                UserManager userManager = UserManager.getUserManager(this.agentUUID)
                if (userManager != null) {
                    CharSequence plainTextMessage = getPlainTextMessage(context, userManager, true)
                    if (Build.VERSION.SDK_INT < 11) {
                        ((ClipboardManager) context.getSystemService("clipboard")).setText(plainTextMessage)
                    } else {
                        ((android.content.ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Message", plainTextMessage))
                    }
                    Toast.makeText(context, "Message copied to clipboard", 0).show()
                }
                return true
            default:
                return false
        }
    }

    /* access modifiers changed from: protected */
    public Unit notifyEventUpdated(UserManager userManager) {
        if (this.dbMessage != null) {
            userManager.getChatterList().getActiveChattersManager().notifyChatEventUpdated(this)
        }
    }

    val Boolean onLongClick(View view) {
        Context context = view.getContext()
        if (context == null) {
            return false
        }
        PopupMenu popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.chat_messages_context_menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener(this, context) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f70$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f71$f1

            private val /* synthetic */ Boolean $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.6.$m$0(android.view.MenuItem):Boolean, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.6.$m$0(android.view.MenuItem):Boolean, class status: UNLOADED
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

            val Boolean onMenuItemClick(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.6.onMenuItemClick(android.view.MenuItem):Boolean, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.6.onMenuItemClick(android.view.MenuItem):Boolean, class status: UNLOADED
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
        popupMenu.show()
        return true
    }

    public Boolean opensNewChatter() {
        return true
    }

    /* access modifiers changed from: protected */
    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        chatMessage.setTimestamp(this.timestamp)
        chatMessage.setIsOffline(Boolean.valueOf(this.isOffline))
        chatMessage.setOrigTimestamp(this.originalTimestamp)
        chatMessage.setMessageType(getMessageType().ordinal())
        chatMessage.setViewType(getViewType().ordinal())
        this.source.serializeTo(chatMessage)
    }
}
