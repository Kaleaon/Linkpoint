// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;

public interface FragmentActivityFactory
{
    Intent createIntent(final Context p0, final Bundle p1);
    
    Class<? extends Fragment> getFragmentClass();
}
