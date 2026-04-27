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
    private const val /* synthetic */ IntArray f72comlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues = null

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f73comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = null
    const val CHAT_AUDIBLE_BARELY: Int = 0
    const val CHAT_AUDIBLE_FULLY: Int = 1
    const val CHAT_AUDIBLE_NOT: Int = -1
    const val CHAT_SOURCE_AGENT: Int = 1
    const val CHAT_SOURCE_OBJECT: Int = 2
    const val CHAT_SOURCE_SYSTEM: Int = 0
    const val CHAT_SOURCE_UNKNOWN: Int = 3
    const val CHAT_TYPE_DEBUG_MSG: Int = 6
    const val CHAT_TYPE_NORMAL: Int = 1
    const val CHAT_TYPE_OWNER: Int = 8
    const val CHAT_TYPE_REGION: Int = 7
    const val CHAT_TYPE_SHOUT: Int = 2
    const val CHAT_TYPE_START: Int = 4
    const val CHAT_TYPE_STOP: Int = 5
    const val CHAT_TYPE_WHISPER: Int = 0
    const val IM_BUSY_AUTO_RESPONSE: Int = 20
    const val IM_CONSOLE_AND_CHAT_HISTORY: Int = 21
    const val IM_FRIENDSHIP_ACCEPTED: Int = 39
    const val IM_FRIENDSHIP_DECLINED: Int = 40
    const val IM_FRIENDSHIP_OFFERED: Int = 38
    const val IM_FROM_TASK: Int = 19
    const val IM_FROM_TASK_AS_ALERT: Int = 31
    const val IM_GODLIKE_LURE_USER: Int = 25
    const val IM_GOTO_URL: Int = 28
    const val IM_GROUP_ELECTION_DEPRECATED: Int = 27
    const val IM_GROUP_INVITATION: Int = 3
    const val IM_GROUP_INVITATION_ACCEPT: Int = 35
    const val IM_GROUP_INVITATION_DECLINE: Int = 36
    const val IM_GROUP_MESSAGE_DEPRECATED: Int = 8
    const val IM_GROUP_NOTICE: Int = 32
    const val IM_GROUP_NOTICE_INVENTORY_ACCEPTED: Int = 33
    const val IM_GROUP_NOTICE_INVENTORY_DECLINED: Int = 34
    const val IM_GROUP_NOTICE_REQUESTED: Int = 37
    const val IM_GROUP_VOTE: Int = 7
    const val IM_INVENTORY_ACCEPTED: Int = 5
    const val IM_INVENTORY_DECLINED: Int = 6
    const val IM_INVENTORY_OFFERED: Int = 4
    const val IM_LURE_ACCEPTED: Int = 23
    const val IM_LURE_DECLINED: Int = 24
    const val IM_LURE_USER: Int = 22
    const val IM_MESSAGEBOX: Int = 1
    const val IM_MESSAGEBOX_COUNTDOWN: Int = 2
    const val IM_NEW_USER_DEFAULT: Int = 12
    const val IM_NOTHING_SPECIAL: Int = 0
    const val IM_SESSION_CONFERENCE_START: Int = 16
    const val IM_SESSION_GROUP_START: Int = 15
    const val IM_SESSION_INVITE: Int = 13
    const val IM_SESSION_LEAVE: Int = 18
    const val IM_SESSION_P2P_INVITE: Int = 14
    const val IM_SESSION_SEND: Int = 17
    const val IM_TASK_INVENTORY_ACCEPTED: Int = 10
    const val IM_TASK_INVENTORY_DECLINED: Int = 11
    const val IM_TASK_INVENTORY_OFFERED: Int = 9
    const val IM_TELEPORT_REQUEST: Int = 26
    const val IM_TYPING_START: Int = 41
    const val IM_TYPING_STOP: Int = 42
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
        
        const val Array<ChatMessageType> VALUES = null

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
        
        const val Array<ChatMessageViewType> VALUES = null
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
private /* synthetic */ IntArray m148getcomlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues() {
        if (f72comlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues != null) {
            return f72comlumiyaviewerlumiyaslprotochatgenericSLChatEvent$ChatMessageTypeSwitchesValues
        }
        val iArr: IntArray = Int[ChatMessageType.values().length]
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
private /* synthetic */ IntArray m149getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues() {
        if (f73comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues != null) {
            return f73comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues
        }
        val iArr: IntArray = Int[ChatMessageSource.ChatMessageSourceType.values().length]
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
     fun createViewHolder(layoutInflater: LayoutInflater, i: Int, viewGroup: ViewGroup, RecyclerView.Adapter adapter): ChatEventViewHolder {
        val chatMessageViewType: ChatMessageViewType = ChatMessageViewType.VALUES[i]
        return chatMessageViewType.createViewHolder(layoutInflater.inflate(chatMessageViewType.getResourceId(), viewGroup, false), adapter)
    }

    @JvmStatic
     fun loadFromDatabaseObject(chatMessage: ChatMessage, uuid: UUID): SLChatEvent {
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

    fun bindViewHolder(chatEventViewHolder: ChatEventViewHolder, userManager: UserManager, chatEventTimestampUpdater: ChatEventTimestampUpdater) {
        val chatterPicView: ChatterPicView = chatEventViewHolder.chatSourceIcon
        val equal: Boolean = this.source.getSourceType() == ChatMessageSource.ChatMessageSourceType.User ? Objects.equal(this.source.getSourceUUID(), this.agentUUID) : false
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
            val typedValue: TypedValue = TypedValue()
            chatEventViewHolder.bubbleView.getContext().getTheme().resolveAttribute(equal ? R.attr.chatBubbleMyBackground : R.attr.chatBubbleBackground, typedValue, true)
            val background: Drawable = chatEventViewHolder.bubbleView.getBackground()
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
                    val sourceUUID: UUID = this.source.getSourceUUID()
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
        val textView: TextView = chatEventViewHolder.timestampView
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
        val textView2: TextView = chatEventViewHolder.textView
        if (textView2 != null) {
            val sourceName: String = this.source.getSourceName(userManager)
            val text: String = getText(textView2.getContext(), userManager)
            val spannableStringBuilder: SpannableStringBuilder = SpannableStringBuilder()
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
                val str: String = " (sent at " + DateFormat.getDateTimeInstance(2, 2).format(this.originalTimestamp) + ")"
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

     public fun getAgentUUID(): UUID {
        return this.agentUUID
    }

     public fun getDatabaseObject(): ChatMessage {
        val chatMessage: ChatMessage = this.dbMessage
        if (chatMessage == null) {
            chatMessage = ChatMessage()
        }
        serializeToDatabaseObject(chatMessage)
        return chatMessage
    }

    /* access modifiers changed from: protected */
    public abstract ChatMessageType getMessageType()

     public fun getPlainTextMessage(context: Context, userManager: UserManager, z: Boolean): CharSequence {
        return getPlainTextMessage(context, userManager, z, ": ", " ")
    }

     public fun getPlainTextMessage(context: Context, userManager: UserManager, z: Boolean, str: String, str2: String): CharSequence {
        val sourceName: String = (!z || !(isActionMessage(userManager) ^ true)) ? this.source.getSourceName(userManager) : null
        val text: String = getText(context, userManager)
        val spannableStringBuilder: SpannableStringBuilder = SpannableStringBuilder()
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

     public fun getSource(): ChatMessageSource {
        return this.source
    }

    /* access modifiers changed from: protected */
    public abstract String getText(Context context, UserManager userManager)

     public fun getTimestamp(): Date {
        return this.timestamp
    }

    public abstract ChatMessageViewType getViewType()

    /* access modifiers changed from: protected */
    public abstract Boolean isActionMessage(UserManager userManager)

     public fun isObjectPopup(): Boolean {
        return false
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent_21084  reason: not valid java name */
    public /* synthetic */ Boolean m150lambda$com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent_21084(Context context, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_copy_message_text:
                val userManager: UserManager = UserManager.getUserManager(this.agentUUID)
                if (userManager != null) {
                    val plainTextMessage: CharSequence = getPlainTextMessage(context, userManager, true)
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
    fun notifyEventUpdated(userManager: UserManager) {
        if (this.dbMessage != null) {
            userManager.getChatterList().getActiveChattersManager().notifyChatEventUpdated(this)
        }
    }

    val Boolean onLongClick(View view) {
        val context: Context = view.getContext()
        if (context == null) {
            return false
        }
        val popupMenu: PopupMenu = PopupMenu(context, view)
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

     public fun opensNewChatter(): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        chatMessage.setTimestamp(this.timestamp)
        chatMessage.setIsOffline(Boolean.valueOf(this.isOffline))
        chatMessage.setOrigTimestamp(this.originalTimestamp)
        chatMessage.setMessageType(getMessageType().ordinal())
        chatMessage.setViewType(getViewType().ordinal())
        this.source.serializeTo(chatMessage)
    }
}
