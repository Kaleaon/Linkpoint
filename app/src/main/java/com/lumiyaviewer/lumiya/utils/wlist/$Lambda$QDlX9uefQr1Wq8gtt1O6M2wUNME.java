package com.lumiyaviewer.lumiya.utils.wlist;

import com.lumiyaviewer.lumiya.utils.Identifiable;
import java.util.Comparator;

/* renamed from: com.lumiyaviewer.lumiya.utils.wlist.-$Lambda$QDlX9uefQr1Wq8gtt1O6M2wUNME  reason: invalid class name */
final /* synthetic */ class $Lambda$QDlX9uefQr1Wq8gtt1O6M2wUNME implements Comparator {
    private final /* synthetic */ int $m$0(Object obj, Object obj2) {
        return Long.signum(((Long) ((Identifiable) obj).getId()).longValue() - ((Long) ((Identifiable) obj2).getId()).longValue());
    }

    public final int compare(Object obj, Object obj2) {
        return $m$0(obj, obj2);
    }
}
