package com.lumiyaviewer.lumiya.slproto.users;
import java.util.*;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE  reason: invalid class name */
final /* synthetic */ class $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f144$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ChatterID.OnChatterPictureIDListener) this.f144$f0).onChatterPictureID(((GroupProfileReply) obj).GroupData_Field.InsigniaID);
    }

    public /* synthetic */ $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(Object obj) {
        this.f144$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
