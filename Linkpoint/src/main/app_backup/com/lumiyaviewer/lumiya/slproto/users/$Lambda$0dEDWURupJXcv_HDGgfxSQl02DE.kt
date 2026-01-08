package com.lumiyaviewer.lumiya.slproto.users
import java.util.*

import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply
import com.lumiyaviewer.lumiya.slproto.users.ChatterID

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE  reason: invalid class name */
/* synthetic */ class $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f144$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((ChatterID.OnChatterPictureIDListener) this.f144$f0).onChatterPictureID(((GroupProfileReply) obj).GroupData_Field.InsigniaID)
    }

    /* synthetic */ $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(Any obj) {
        this.f144$f0 = obj
    }

    Unit onData(Any obj) {
        $m$0(obj)
    }
}
