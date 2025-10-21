package com.linkpoint.slproto.users
import java.util.*

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.users.ChatterID

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE  reason: invalid class name */
final /* synthetic */ class $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private val /* synthetic */ Object f144$f0

    private val /* synthetic */ Unit $m$0(Object obj) {
        ((ChatterID.OnChatterPictureIDListener) this.f144$f0).onChatterPictureID(((GroupProfileReply) obj).GroupData_Field.InsigniaID)
    }

    public /* synthetic */ $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(Object obj) {
        this.f144$f0 = obj
    }

    val Unit onData(Object obj) {
        $m$0(obj)
    }
}
