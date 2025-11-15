package com.lumiyaviewer.lumiya.slproto.modules.mutelist

import com.google.common.base.Predicate
import java.util.Map

/* renamed from: com.lumiyaviewer.lumiya.slproto.modules.mutelist.-$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA  reason: invalid class name */
/* synthetic */ class $Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA : Predicate {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f123$f0

    private /* synthetic */ Boolean $m$0(Any obj) {
        return MuteListData.m228lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795((MuteListEntry) this.f123$f0, (Map.Entry) obj)
    }

    /* synthetic */ $Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA(Any obj) {
        this.f123$f0 = obj
    }

    Boolean apply(Any obj) {
        return $m$0(obj)
    }
}
