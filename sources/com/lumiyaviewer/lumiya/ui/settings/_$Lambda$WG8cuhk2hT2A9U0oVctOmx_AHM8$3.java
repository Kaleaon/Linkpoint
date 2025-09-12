/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnCancelListener
 */
package com.lumiyaviewer.lumiya.ui.settings;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.settings.SettingsFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$3
implements DialogInterface.OnCancelListener {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface) {
        ((SettingsFragment.ClearCacheTask)((Object)this.-$f0)).lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment$ClearCacheTask_8620(dialogInterface);
    }

    public /* synthetic */ _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$3(Object object) {
        this.-$f0 = object;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.$m$0(dialogInterface);
    }
}
