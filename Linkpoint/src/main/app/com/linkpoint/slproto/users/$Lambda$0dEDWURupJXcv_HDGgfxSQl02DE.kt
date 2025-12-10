package com.linkpoint.slproto.users
import java.util.*

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.users.ChatterID

/* renamed from: com.linkpoint.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE  reason: invalid class name */
/* synthetic */ class $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f144$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((ChatterID.OnChatterPictureIDListener) this.f144$f0).onChatterPictureID(((GroupProfileReply) obj).GroupData_Field.InsigniaID)
    }

    /* synthetic */ $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(Any obj) {
        this.f144$f0 = obj
    }

    fun onData(Any obj): Unit {
        $m$0(obj)
    }
}
