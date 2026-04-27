package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

/* loaded from: classes.dex */
public enum MuteType {
    BY_NAME(2),
    AGENT(0),
    OBJECT(1),
    GROUP(3),
    EXTERNAL(4);

    private int viewOrder;

    MuteType(int i) {
        this.viewOrder = i;
    }

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static MuteType[] valuesCustom() {
        return values();
    }

    public int getViewOrder() {
        return this.viewOrder;
    }
}
