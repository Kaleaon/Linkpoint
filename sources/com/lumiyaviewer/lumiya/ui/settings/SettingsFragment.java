// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.utils.FileUtils;
import java.io.File;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.settings:
//            CacheLocationPreference, RingtonePreference, PreferenceSubPage, SettingsSubPageFragment, 
//            ClearCachePreference

public class SettingsFragment extends PreferenceFragmentCompat
    implements FragmentHasTitle
{
    private class ClearCacheTask extends AsyncTask
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
            progressDialog = ProgressDialog.show(getContext(), null, getString(0x7f0900bf), true, true, new _2D_.Lambda.WG8cuhk2hT2A9U0oVctOmx_AHM8._cls3(this));
        }

        private ClearCacheTask()
        {
            this$0 = SettingsFragment.this;
            super();
            progressDialog = null;
        }

        ClearCacheTask(ClearCacheTask clearcachetask)
        {
            this();
        }
    }


    private static final String PREF_RESOURCE_KEY = "prefResourceId";
    private RingtonePreference requestedRingtonePreference;

    static void _2D_wrap0(SettingsFragment settingsfragment)
    {
        settingsfragment.askForRestart();
    }

    public SettingsFragment()
    {
        requestedRingtonePreference = null;
    }

    private void askForRestart()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage(0x7f0902a6);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new _2D_.Lambda.WG8cuhk2hT2A9U0oVctOmx_AHM8());
        builder.setPositiveButton("Yes", new _2D_.Lambda.WG8cuhk2hT2A9U0oVctOmx_AHM8._cls1());
        builder.create().show();
    }

    private void handleCacheLocationPreference(CacheLocationPreference cachelocationpreference)
    {
        int j = -1;
        ImmutableList immutablelist = GlobalOptions.getInstance().getAvailableCacheDirs();
        File file = GlobalOptions.getInstance().getBaseCacheDir();
        String as[] = new String[immutablelist.size()];
        int i = 0;
        while (i < immutablelist.size()) 
        {
            if (Objects.equal(immutablelist.get(i), file))
            {
                j = i;
            }
            StatFs statfs = new StatFs(((File)immutablelist.get(i)).getAbsolutePath());
            float f;
            long l;
            if (android.os.Build.VERSION.SDK_INT >= 18)
            {
                l = statfs.getAvailableBlocksLong();
                l = statfs.getBlockSizeLong() * l;
            } else
            {
                l = statfs.getAvailableBlocks() * statfs.getBlockSize();
            }
            f = (float)l / 1.073742E+09F;
            as[i] = String.format("%s (%.1f Gb free)", new Object[] {
                CacheLocationPreference.makeDisplayableCacheLocation(((File)immutablelist.get(i)).getAbsolutePath()), Float.valueOf(f)
            });
            i++;
        }
        ArrayAdapter arrayadapter = new ArrayAdapter(getContext(), 0x1090012, as);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle(0x7f0902f1);
        builder.setSingleChoiceItems(arrayadapter, j, new _2D_.Lambda.WG8cuhk2hT2A9U0oVctOmx_AHM8._cls5(this, immutablelist, cachelocationpreference, file));
        builder.setCancelable(true);
        builder.create().show();
    }

    private void handleClearCachePreference()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage(0x7f0900ba);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new _2D_.Lambda.WG8cuhk2hT2A9U0oVctOmx_AHM8._cls2());
        builder.setPositiveButton("Yes", new _2D_.Lambda.WG8cuhk2hT2A9U0oVctOmx_AHM8._cls4(this));
        builder.create().show();
    }

    private void handleRingtonePreference(RingtonePreference ringtonepreference)
    {
        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", NotificationSounds.getResourceUri(ringtonepreference.getDefaultRawResource()));
        String s = ringtonepreference.getSharedPreferences().getString(ringtonepreference.getKey(), null);
        if (s != null)
        {
            if (s.length() == 0)
            {
                intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", (Uri)null);
            } else
            {
                intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", Uri.parse(s));
            }
        } else
        {
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", NotificationSounds.getResourceUri(ringtonepreference.getDefaultRawResource()));
        }
        requestedRingtonePreference = ringtonepreference;
        startActivityForResult(intent, 2);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_7404(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_7498(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        LumiyaApp.restartApp();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_7928(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(int i)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("prefResourceId", i);
        return bundle;
    }

    private void updatePreferencesDisplay()
    {
        Object obj = getListView();
        if (obj != null)
        {
            obj = ((RecyclerView) (obj)).getAdapter();
            if (obj != null)
            {
                ((android.support.v7.widget.RecyclerView.Adapter) (obj)).notifyDataSetChanged();
            }
        }
    }

    public String getSubTitle()
    {
        return null;
    }

    public String getTitle()
    {
        Object obj = getPreferenceScreen();
        if (obj != null)
        {
            obj = ((PreferenceScreen) (obj)).getTitle();
            if (obj != null)
            {
                return ((CharSequence) (obj)).toString();
            }
        }
        return null;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_6434(ImmutableList immutablelist, CacheLocationPreference cachelocationpreference, File file, DialogInterface dialoginterface, int i)
    {
        File file1 = new File(((File)immutablelist.get(i)).toString());
        android.content.SharedPreferences.Editor editor = cachelocationpreference.getSharedPreferences().edit();
        editor.putString(cachelocationpreference.getKey(), ((File)immutablelist.get(i)).toString());
        editor.commit();
        dialoginterface.dismiss();
        updatePreferencesDisplay();
        if (!Objects.equal(file, file1) && GlobalOptions.getInstance().isCacheDirUsed())
        {
            askForRestart();
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_8022(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        (new ClearCacheTask(null)).execute(new Void[0]);
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        if (i == 2 && j == -1 && intent != null && requestedRingtonePreference != null)
        {
            intent = (Uri)intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            android.content.SharedPreferences.Editor editor;
            if (intent != null)
            {
                intent = intent.toString();
            } else
            {
                intent = "";
            }
            editor = requestedRingtonePreference.getSharedPreferences().edit();
            editor.putString(requestedRingtonePreference.getKey(), intent);
            editor.commit();
            updatePreferencesDisplay();
            return;
        }
        if (i == 11)
        {
            updatePreferencesDisplay();
            return;
        } else
        {
            super.onActivityResult(i, j, intent);
            return;
        }
    }

    public void onCreatePreferences(Bundle bundle, String s)
    {
        addPreferencesFromResource(getArguments().getInt("prefResourceId"));
        bundle = getActivity();
        if (bundle instanceof DetailsActivity)
        {
            ((DetailsActivity)bundle).onFragmentTitleUpdated();
        }
        bundle = findPreference("soundOnNotify");
        if (bundle != null)
        {
            bundle.setVisible(NotificationChannels.getInstance().areNotificationsSystemControlled() ^ true);
        }
    }

    public void onDetach()
    {
        super.onDetach();
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).onFragmentTitleUpdated();
        }
    }

    public boolean onPreferenceTreeClick(Preference preference)
    {
        if (preference instanceof PreferenceSubPage)
        {
            preference = (PreferenceSubPage)preference;
            Object obj = preference.getNotificationType();
            if (obj != null)
            {
                obj = NotificationChannels.getInstance().getChannelByType(((NotificationType) (obj)));
                if (obj != null && NotificationChannels.getInstance().showSystemNotificationSettings(getContext(), this, ((com.lumiyaviewer.lumiya.ui.notify.NotificationChannels.Channel) (obj))))
                {
                    return true;
                }
            }
            int i = preference.getPageResource();
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/settings/SettingsSubPageFragment, SettingsSubPageFragment.makeSelection(i));
            return true;
        }
        if (preference instanceof RingtonePreference)
        {
            handleRingtonePreference((RingtonePreference)preference);
            return true;
        }
        if (preference instanceof CacheLocationPreference)
        {
            handleCacheLocationPreference((CacheLocationPreference)preference);
            return true;
        }
        if (preference instanceof ClearCachePreference)
        {
            handleClearCachePreference();
            return true;
        } else
        {
            return super.onPreferenceTreeClick(preference);
        }
    }

    public void onStart()
    {
        super.onStart();
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).onFragmentTitleUpdated();
        }
    }
}
