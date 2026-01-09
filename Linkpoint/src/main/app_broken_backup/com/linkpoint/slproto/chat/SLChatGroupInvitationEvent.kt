package com.linkpoint.slproto.chat

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import androidx.annotation.NonNull

class SLChatGroupInvitationEvent : SLChatYesNoEvent {
    private UUID groupID
    private Int joinFee
    private UUID sessionID

    SLChatGroupInvitationEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.joinFee = chatMessage.getTransactionAmount().intValue()
        this.sessionID = chatMessage.getSessionID()
        this.groupID = chatMessage.getItemID()
    }

    SLChatGroupInvitationEvent(ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
        this.groupID = improvedInstantMessage.AgentData_Field.AgentID
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.size < 4) {
            this.joinFee = 0
            return
        }
        ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket)
        wrap.order(ByteOrder.BIG_ENDIAN)
        this.joinFee = wrap.getInt()
    }

    private Unit DoAcceptGroupInvite(UUID uuid, UUID uuid2, Boolean z) {
        SLAgentCircuit activeAgentCircuit
        UserManager userManager = UserManager.getUserManager(this.agentUUID)
        if (userManager != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
            activeAgentCircuit.getModules().groupManager.AcceptGroupInvite(uuid, uuid2, z)
        }
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.GroupInvitation
    }

    fun getNoButton(Context context): String {
        return context.getString(R.string.join_group_no)
    }

    fun getNoMessage(Context context): String {
        return context.getString(R.string.join_group_declined)
    }

    fun getQuestion(Context context): String {
        if (this.joinFee == 0) {
            return context.getString(R.string.join_group_question_free)
        }
        return context.getString(R.string.join_group_question_not_free, Array<Any>{Integer.valueOf(this.joinFee)})
    }

    fun getYesButton(Context context): String {
        return context.getString(R.string.join_group_yes)
    }

    fun getYesMessage(Context context): String {
        return context.getString(R.string.join_group_accepted)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_chat_SLChatGroupInvitationEvent_3561  reason: not valid java name */
    /* synthetic */ Unit m143lambda$com_lumiyaviewer_lumiya_slproto_chat_SLChatGroupInvitationEvent_3561(DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        DoAcceptGroupInvite(this.groupID, this.sessionID, true)
    }

    /* access modifiers changed from: protected */
    fun onNoAction(Context context, UserManager userManager)  {
        super.onNoAction(context, userManager)
        DoAcceptGroupInvite(this.groupID, this.sessionID, false)
    }

    fun onYesAction(Context context, UserManager userManager)  {
        super.onYesAction(context, userManager)
        if (this.joinFee == 0) {
            DoAcceptGroupInvite(this.groupID, this.sessionID, true)
            return
        }
        AlertDialog.Builder builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.join_group_confirm, Array<Any>{Integer.valueOf(this.joinFee)})).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Object f69$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.slproto.chat.-$Lambda$hXLxI3fDexZfuKx5RzOoCtsGy3I.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.slproto.chat.-$Lambda$hXLxI3fDexZfuKx5RzOoCtsGy3I.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

        }).setNegativeButton("No", $Lambda$hXLxI3fDexZfuKx5RzOoCtsGy3I())
        builder.create().show()
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setTransactionAmount(Integer.valueOf(this.joinFee))
        chatMessage.setSessionID(this.sessionID)
        chatMessage.setItemID(this.groupID)
    }
}
