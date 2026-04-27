// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.utils.FileUtils;
import java.io.File;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.settings:
//            SettingsFragment

private class <init> extends AsyncTask
{

    private ImmutableList cacheDirs;
    private ProgressDialog progressDialog;
    final SettingsFragment this$0;

    protected volatile Object doInBackground(Object aobj[])
    {
        return doInBackground((Void[])aobj);
    }

    protected transient Void doInBackground(Void avoid[])
    {
        if (cacheDirs != null)
        {
            for (avoid = cacheDirs.iterator(); avoid.hasNext(); FileUtils.clearFolder((File)avoid.next())) { }
        }
        return null;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_SettingsFragment$ClearCacheTask_8620(DialogInterface dialoginterface)
    {
        cancel(false);
    }

    protected volatile void onPostExecute(Object obj)
    {
        onPostExecute((Void)obj);
    }

    protected void onPostExecute(Void void1)
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
        if (GlobalOptions.getInstance().isCacheDirUsed())
        {
            SettingsFragment._2D_wrap0(SettingsFragment.this);
        }
    }

    protected void onPreExecute()
    {
        cacheDirs = GlobalOptions.getInstance().getAvailableCacheDirs();
        progressDialog = ProgressDialog.show(getContext(), null, getString(0x7f0900bf), true, true, new AHM8._cls3(this));
    }

    private AHM8._cls3()
    {
        this$0 = SettingsFragment.this;
        super();
        progressDialog = null;
    }

    progressDialog(progressDialog progressdialog)
    {
        this();
    }
}
