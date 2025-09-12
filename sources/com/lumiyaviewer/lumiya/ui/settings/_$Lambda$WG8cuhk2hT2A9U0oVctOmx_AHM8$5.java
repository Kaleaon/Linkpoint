/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.settings;

import android.content.DialogInterface;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.ui.settings.CacheLocationPreference;
import com.lumiyaviewer.lumiya.ui.settings.SettingsFragment;
import java.io.File;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$5
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((SettingsFragment)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_6434((ImmutableList)this.-$f1, (CacheLocationPreference)this.-$f2, (File)this.-$f3, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$5(Object object, Object object2, Object object3, Object object4) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
        this.-$f3 = object4;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
