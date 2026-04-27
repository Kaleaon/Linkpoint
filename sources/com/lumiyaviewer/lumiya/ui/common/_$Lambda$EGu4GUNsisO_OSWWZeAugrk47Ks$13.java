/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$13
implements TextFieldDialogBuilder.OnTextEnteredListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(String string) {
        UserFunctionsFragment.lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_30583((SLAgentCircuit)this.-$f0, (ChatterID.ChatterIDUser)this.-$f1, string);
    }

    public /* synthetic */ _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$13(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final void onTextEntered(String string) {
        this.$m$0(string);
    }
}
