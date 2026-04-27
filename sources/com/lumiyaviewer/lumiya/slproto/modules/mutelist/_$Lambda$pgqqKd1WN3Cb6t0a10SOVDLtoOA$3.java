/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListData;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListKey;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA$3
implements Predicate {
    private final Object -$f0;

    private final /* synthetic */ boolean $m$0(Object object) {
        return MuteListData.lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217((MuteListKey)this.-$f0, (Map.Entry)object);
    }

    public /* synthetic */ _$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA$3(Object object) {
        this.-$f0 = object;
    }

    public final boolean apply(Object object) {
        return this.$m$0(object);
    }
}
