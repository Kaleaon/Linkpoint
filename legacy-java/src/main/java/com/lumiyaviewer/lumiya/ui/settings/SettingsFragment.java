package com.lumiyaviewer.lumiya.ui.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.ui.settings.SettingsFragment;
import com.lumiyaviewer.lumiya.utils.FileUtils;
import java.io.File;
import java.util.Iterator;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class SettingsFragment extends PreferenceFragmentCompat implements FragmentHasTitle {
    private static final String PREF_RESOURCE_KEY = "prefResourceId";
    private RingtonePreference requestedRingtonePreference = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ClearCacheTask extends AsyncTask<Void, Void, Void> {
        private ImmutableList<File> cacheDirs;
        private ProgressDialog progressDialog;

        private ClearCacheTask() {
            this.progressDialog = null;
        }

        /* synthetic */ ClearCacheTask(SettingsFragment settingsFragment, ClearCacheTask clearCacheTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            if (this.cacheDirs != null) {
                Iterator<File> it = this.cacheDirs.iterator();
                while (it.hasNext()) {
                    FileUtils.clearFolder(it.next());
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment$ClearCacheTask_8620, reason: not valid java name */
        public /* synthetic */ void m870x7613bcc1(DialogInterface dialogInterface) {
            cancel(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r2) {
            if (this.progressDialog != null) {
                this.progressDialog.dismiss();
            }
            if (GlobalOptions.getInstance().isCacheDirUsed()) {
                SettingsFragment.this.askForRestart();
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.cacheDirs = GlobalOptions.getInstance().getAvailableCacheDirs();
            this.progressDialog = ProgressDialog.show(SettingsFragment.this.getContext(), null, SettingsFragment.this.getString(R.string.clearing_cache), true, true, new DialogInterface.OnCancelListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.3
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface) {
                    ((SettingsFragment.ClearCacheTask) this).m870x7613bcc1(dialogInterface);
                }

                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    $m$0(dialogInterface);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void askForRestart() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.restart_after_changing_cache_location);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                SettingsFragment.m866xa8647b17(dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    @SuppressLint({"DefaultLocale"})
    private void handleCacheLocationPreference(final CacheLocationPreference cacheLocationPreference) {
        int i = -1;
        final ImmutableList<File> availableCacheDirs = GlobalOptions.getInstance().getAvailableCacheDirs();
        final File baseCacheDir = GlobalOptions.getInstance().getBaseCacheDir();
        String[] strArr = new String[availableCacheDirs.size()];
        int i2 = 0;
        while (i2 < availableCacheDirs.size()) {
            int i3 = Objects.equal(availableCacheDirs.get(i2), baseCacheDir) ? i2 : i;
            StatFs statFs = new StatFs(availableCacheDirs.get(i2).getAbsolutePath());
            strArr[i2] = String.format("%s (%.1f Gb free)", CacheLocationPreference.makeDisplayableCacheLocation(availableCacheDirs.get(i2).getAbsolutePath()), Float.valueOf(((float) (Build.VERSION.SDK_INT >= 18 ? statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong() : statFs.getAvailableBlocks() * statFs.getBlockSize())) / 1.0737418E9f));
            i2++;
            i = i3;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice, strArr);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.select_cache_location);
        builder.setSingleChoiceItems(arrayAdapter, i, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.5
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i4) {
                ((SettingsFragment) this).m868xa86405fa((ImmutableList) availableCacheDirs, (CacheLocationPreference) cacheLocationPreference, (File) baseCacheDir, dialogInterface, i4);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i4) {
                $m$0(dialogInterface, i4);
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    private void handleClearCachePreference() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.clear_cache_dialog_message);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.2
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.4
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((SettingsFragment) this).m869xa864df93(dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    private void handleRingtonePreference(RingtonePreference ringtonePreference) {
        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", NotificationSounds.getResourceUri(ringtonePreference.getDefaultRawResource()));
        String string = ringtonePreference.getSharedPreferences().getString(ringtonePreference.getKey(), null);
        if (string == null) {
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", NotificationSounds.getResourceUri(ringtonePreference.getDefaultRawResource()));
        } else if (string.length() == 0) {
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", (Uri) null);
        } else {
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", Uri.parse(string));
        }
        this.requestedRingtonePreference = ringtonePreference;
        startActivityForResult(intent, 2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_7498, reason: not valid java name */
    public static /* synthetic */ void m866xa8647b17(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        LumiyaApp.restartApp();
    }

    public static Bundle makeSelection(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt(PREF_RESOURCE_KEY, i);
        return bundle;
    }

    private void updatePreferencesDisplay() {
        RecyclerView.Adapter adapter;
        RecyclerView listView = getListView();
        if (listView == null || (adapter = listView.getAdapter()) == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle
    @Nullable
    public String getSubTitle() {
        return null;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle
    @Nullable
    public String getTitle() {
        CharSequence title;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null || (title = preferenceScreen.getTitle()) == null) {
            return null;
        }
        return title.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_6434, reason: not valid java name */
    public /* synthetic */ void m868xa86405fa(ImmutableList immutableList, CacheLocationPreference cacheLocationPreference, File file, DialogInterface dialogInterface, int i) {
        File file2 = new File(((File) immutableList.get(i)).toString());
        SharedPreferences.Editor edit = cacheLocationPreference.getSharedPreferences().edit();
        edit.putString(cacheLocationPreference.getKey(), ((File) immutableList.get(i)).toString());
        edit.commit();
        dialogInterface.dismiss();
        updatePreferencesDisplay();
        if (Objects.equal(file, file2) || !GlobalOptions.getInstance().isCacheDirUsed()) {
            return;
        }
        askForRestart();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_8022, reason: not valid java name */
    public /* synthetic */ void m869xa864df93(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        new ClearCacheTask(this, null).execute(new Void[0]);
    }

    @Override // android.support.v4.app.Fragment
    @SuppressLint({"CommitPrefEdits"})
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 2 || i2 != -1 || intent == null || this.requestedRingtonePreference == null) {
            if (i == 11) {
                updatePreferencesDisplay();
                return;
            } else {
                super.onActivityResult(i, i2, intent);
                return;
            }
        }
        Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
        String uri2 = uri != null ? uri.toString() : "";
        SharedPreferences.Editor edit = this.requestedRingtonePreference.getSharedPreferences().edit();
        edit.putString(this.requestedRingtonePreference.getKey(), uri2);
        edit.commit();
        updatePreferencesDisplay();
    }

    @Override // android.support.v7.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(getArguments().getInt(PREF_RESOURCE_KEY));
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
        Preference findPreference = findPreference("soundOnNotify");
        if (findPreference != null) {
            findPreference.setVisible(!NotificationChannels.getInstance().areNotificationsSystemControlled());
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onDetach() {
        super.onDetach();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }

    @Override // android.support.v7.preference.PreferenceFragmentCompat, android.support.v7.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        NotificationChannels.Channel channelByType;
        if (preference instanceof PreferenceSubPage) {
            PreferenceSubPage preferenceSubPage = (PreferenceSubPage) preference;
            NotificationType notificationType = preferenceSubPage.getNotificationType();
            if (notificationType != null && (channelByType = NotificationChannels.getInstance().getChannelByType(notificationType)) != null && NotificationChannels.getInstance().showSystemNotificationSettings(getContext(), this, channelByType)) {
                return true;
            }
            DetailsActivity.showEmbeddedDetails(getActivity(), SettingsSubPageFragment.class, SettingsSubPageFragment.makeSelection(preferenceSubPage.getPageResource()));
            return true;
        }
        if (preference instanceof RingtonePreference) {
            handleRingtonePreference((RingtonePreference) preference);
            return true;
        }
        if (preference instanceof CacheLocationPreference) {
            handleCacheLocationPreference((CacheLocationPreference) preference);
            return true;
        }
        if (!(preference instanceof ClearCachePreference)) {
            return super.onPreferenceTreeClick(preference);
        }
        handleClearCachePreference();
        return true;
    }

    @Override // android.support.v7.preference.PreferenceFragmentCompat, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }
}
