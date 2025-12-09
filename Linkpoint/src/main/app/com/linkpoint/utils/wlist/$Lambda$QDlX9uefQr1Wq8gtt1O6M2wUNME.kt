package com.linkpoint.utils.wlist

import com.linkpoint.utils.Identifiable
import java.util.Comparator

/* renamed from: com.linkpoint.utils.wlist.-$Lambda$QDlX9uefQr1Wq8gtt1O6M2wUNME  reason: invalid class name */
/* synthetic */ class $Lambda$QDlX9uefQr1Wq8gtt1O6M2wUNME : Comparator {
    private /* synthetic */ Int $m$0(Any obj, Any obj2) {
        return Long.signum(((Long) ((Identifiable) obj).getId()).longValue() - ((Long) ((Identifiable) obj2).getId()).longValue())
    }

    fun compare(Any obj, Any obj2): Int {
        return $m$0(obj, obj2)
    }
}
