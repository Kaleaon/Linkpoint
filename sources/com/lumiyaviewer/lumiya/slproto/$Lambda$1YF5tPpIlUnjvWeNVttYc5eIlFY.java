package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import java.util.Comparator;

/* renamed from: com.lumiyaviewer.lumiya.slproto.-$Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY  reason: invalid class name */
final /* synthetic */ class $Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY implements Comparator {
    private final /* synthetic */ int $m$0(Object obj, Object obj2) {
        return Float.compare(((SLObjectDisplayInfo) obj).distance, ((SLObjectDisplayInfo) obj2).distance);
    }

    public final int compare(Object obj, Object obj2) {
        return $m$0(obj, obj2);
    }
}
