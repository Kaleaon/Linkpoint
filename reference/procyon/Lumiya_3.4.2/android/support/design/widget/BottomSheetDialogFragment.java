// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class BottomSheetDialogFragment extends AppCompatDialogFragment
{
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {
        return new BottomSheetDialog(this.getContext(), this.getTheme());
    }
}
