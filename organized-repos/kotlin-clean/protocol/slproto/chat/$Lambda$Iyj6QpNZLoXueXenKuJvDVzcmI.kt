package com.linkpoint.slproto.chat
import java.util.*

import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.TextFieldDialogBuilder

/* renamed from: com.lumiyaviewer.lumiya.slproto.chat.-$Lambda$Iyj6QpN-ZLoXueXenKuJvDVzcmI  reason: invalid class name */
final /* synthetic */ class $Lambda$Iyj6QpNZLoXueXenKuJvDVzcmI : TextFieldDialogBuilder.OnTextCancelledListener {

    /* renamed from: -$f0  reason: not valid java name */
    private val /* synthetic */ Object f65$f0

    /* renamed from: -$f1  reason: not valid java name */
    private val /* synthetic */ Object f66$f1

    private val /* synthetic */ Unit $m$0() {
        ((SLChatTextBoxDialog) this.f65$f0).m147lambda$com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4314((UserManager) this.f66$f1)
    }

    public /* synthetic */ $Lambda$Iyj6QpNZLoXueXenKuJvDVzcmI(Object obj, Object obj2) {
        this.f65$f0 = obj
        this.f66$f1 = obj2
    }

    val Unit onTextCancelled() {
        $m$0()
    }
}
