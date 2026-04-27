package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/* loaded from: classes.dex */
public interface FragmentActivityFactory {
    Intent createIntent(Context context, Bundle bundle);

    Class<? extends Fragment> getFragmentClass();
}
