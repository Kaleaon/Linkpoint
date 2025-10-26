package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatTextBoxViewHolder
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class SLChatTextBoxDialog : SLChatDialogEvent {
    private String enteredValue = null
    private Int textBoxButtonIndex

    SLChatTextBoxDialog(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.textBoxButtonIndex = chatMessage.getTextBoxButtonIndex().intValue()
        this.enteredValue = chatMessage.getDialogSelectedOption()
    }

    SLChatTextBoxDialog(ScriptDialog scriptDialog, @NonNull UUID uuid, Int i) {
        super(scriptDialog, uuid)
        this.textBoxButtonIndex = i
    }

    Unit bindViewHolder(ChatEventViewHolder chatEventViewHolder, UserManager userManager, @Nullable ChatEventTimestampUpdater chatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater)
        if (chatEventViewHolder instanceof ChatTextBoxViewHolder) {
            ChatTextBoxViewHolder chatTextBoxViewHolder = (ChatTextBoxViewHolder) chatEventViewHolder
            if (this.enteredValue != null || this.ignored) {
                if (this.ignored) {
                    chatTextBoxViewHolder.dialogResultTextView.setText(R.string.dialog_ignored)
                } else {
                    chatTextBoxViewHolder.dialogResultTextView.setText(chatTextBoxViewHolder.dialogResultTextView.getContext().getString(R.string.text_box_entered, Object[]{this.enteredValue}))
                }
                chatTextBoxViewHolder.dialogResultTextView.setVisibility(0)
                chatTextBoxViewHolder.dialogButtonsLayout.setVisibility(8)
            } else {
                chatTextBoxViewHolder.dialogResultTextView.setVisibility(8)
                chatTextBoxViewHolder.dialogButtonsLayout.setVisibility(0)
            }
            chatTextBoxViewHolder.setTextBoxEvent(this)
        }
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.TextBoxDialog
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_TEXTBOX
    }

    Boolean isObjectPopup() {
        return true
    }

    /* renamed from: onDialogIgnored */
    Unit m147lambda$com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4314(UserManager userManager) {
        super.onDialogIgnored(userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    /* renamed from: onEnteredText */
    Unit m146lambda$com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4223(UserManager userManager, String str) {
        this.enteredValue = str
        UUID sourceUUID = this.source.getSourceUUID()
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (!(sourceUUID == null || activeAgentCircuit == null)) {
            activeAgentCircuit.SendScriptDialogReply(sourceUUID, this.chatChannel, this.textBoxButtonIndex, str)
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setTextBoxButtonIndex(Integer.valueOf(this.textBoxButtonIndex))
        chatMessage.setDialogSelectedOption(this.enteredValue)
    }

    Unit showDialog(Context context, UserManager userManager) {
        TextFieldDialogBuilder(context).setTitle(this.text).setOnTextEnteredListener(TextFieldDialogBuilder.OnTextEnteredListener(this, userManager) {

            /* renamed from: -$f0 */
            private /* synthetic */ Object f67$f0

            /* renamed from: -$f1 */
            private /* synthetic */ Object f68$f1

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.chat.-$Lambda$Iyj6QpN-ZLoXueXenKuJvDVzcmI.1.$m$0(java.lang.String):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.chat.-$Lambda$Iyj6QpN-ZLoXueXenKuJvDVzcmI.1.$m$0(java.lang.String):Unit, class status: UNLOADED
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

        }).setOnTextCancelledListener($Lambda$Iyj6QpNZLoXueXenKuJvDVzcmI(this, userManager)).show()
    }
}
