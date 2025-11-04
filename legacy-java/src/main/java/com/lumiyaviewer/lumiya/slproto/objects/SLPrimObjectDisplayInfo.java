package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Strings;

/* loaded from: classes.dex */
public class SLPrimObjectDisplayInfo extends SLObjectDisplayInfo {
    public final int localID;
    public final boolean payable;
    public final boolean touchable;

    public SLPrimObjectDisplayInfo(SLObjectInfo sLObjectInfo, float f) {
        super(sLObjectInfo.localID, sLObjectInfo.nameKnown ? Strings.nullToEmpty(sLObjectInfo.name) : null, f, sLObjectInfo.hierLevel);
        this.localID = sLObjectInfo.localID;
        this.touchable = sLObjectInfo.isTouchable();
        this.payable = sLObjectInfo.isPayable() || sLObjectInfo.saleType != 0;
    }
}
