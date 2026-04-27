// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Strings;

public class SLPrimObjectDisplayInfo extends SLObjectDisplayInfo
{
    public final int localID;
    public final boolean payable;
    public final boolean touchable;
    
    public SLPrimObjectDisplayInfo(final SLObjectInfo slObjectInfo, final float n) {
        final int localID = slObjectInfo.localID;
        String nullToEmpty;
        if (slObjectInfo.nameKnown) {
            nullToEmpty = Strings.nullToEmpty(slObjectInfo.name);
        }
        else {
            nullToEmpty = null;
        }
        super(localID, nullToEmpty, n, slObjectInfo.hierLevel);
        this.localID = slObjectInfo.localID;
        this.touchable = slObjectInfo.isTouchable();
        this.payable = (slObjectInfo.isPayable() || slObjectInfo.saleType != 0);
    }
}
