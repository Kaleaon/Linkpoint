package com.linkpoint.slproto.objects

import com.google.common.base.Strings

class SLPrimObjectDisplayInfo : SLObjectDisplayInfo() {
    val Int localID
    val Boolean payable
    val Boolean touchable

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLPrimObjectDisplayInfo(SLObjectInfo sLObjectInfo, Float f) {
        super(sLObjectInfo.localID, sLObjectInfo.nameKnown ? Strings.nullToEmpty(sLObjectInfo.name) : null, f, sLObjectInfo.hierLevel)
        this.localID = sLObjectInfo.localID
        this.touchable = sLObjectInfo.isTouchable()
        this.payable = sLObjectInfo.isPayable() || sLObjectInfo.saleType != 0
    }
}
