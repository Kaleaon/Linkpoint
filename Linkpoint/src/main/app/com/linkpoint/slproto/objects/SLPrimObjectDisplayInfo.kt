package com.linkpoint.slproto.objects

import com.google.common.base.Strings

class SLPrimObjectDisplayInfo(
    objectInfo: SLObjectInfo,
    distance: Float,
) : SLObjectDisplayInfo(
        objectInfo.localID,
        if (objectInfo.nameKnown) Strings.nullToEmpty(objectInfo.name) else null,
        distance,
        objectInfo.hierLevel,
    ) {
    val localID: Int = objectInfo.localID
    val touchable: Boolean = objectInfo.isTouchable()
    val payable: Boolean = objectInfo.isPayable() || objectInfo.saleType != 0
}
