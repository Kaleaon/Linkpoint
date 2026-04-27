// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.DialogInterface$OnCancelListener;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.utils.FileUtils;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.content.SharedPreferences$Editor;
import android.support.v7.preference.PreferenceScreen;
import javax.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.LumiyaApp;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import android.content.Intent;
import android.annotation.SuppressLint;
import com.google.common.collect.ImmutableList;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
import android.os.Build$VERSION;
import android.os.StatFs;
import java.io.File;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.GlobalOptions;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements FragmentHasTitle
{
    private static final String PREF_RESOURCE_KEY = "prefResourceId";
    private RingtonePreference requestedRingtonePreference;
    
    public SettingsFragment() {
        this.requestedRingtonePreference = null;
    }
    
    private void askForRestart() {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
        alertDialog$Builder.setMessage(2131296934);
        alertDialog$Builder.setCancelable(true);
        alertDialog$Builder.setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8());
        alertDialog$Builder.setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$1());
        alertDialog$Builder.create().show();
    }
    
    @SuppressLint({ "DefaultLocale" })
    private void handleCacheLocationPreference(final CacheLocationPreference cacheLocationPreference) {
        int n = -1;
        final ImmutableList<File> availableCacheDirs = GlobalOptions.getInstance().getAvailableCacheDirs();
        final File baseCacheDir = GlobalOptions.getInstance().getBaseCacheDir();
        final String[] array = new String[availableCacheDirs.size()];
        for (int i = 0; i < availableCacheDirs.size(); ++i) {
            if (Objects.equal(availableCacheDirs.get(i), baseCacheDir)) {
                n = i;
            }
            final StatFs statFs = new StatFs(((File)availableCacheDirs.get(i)).getAbsolutePath());
            long n2;
            if (Build$VERSION.SDK_INT >= 18) {
                n2 = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
            }
            else {
                n2 = statFs.getAvailableBlocks() * statFs.getBlockSize();
            }
            array[i] = String.format("%s (%.1f Gb free)", CacheLocationPreference.makeDisplayableCacheLocation(((File)availableCacheDirs.get(i)).getAbsolutePath()), n2 / 1.0737418E9f);
        }
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), 17367058, (Object[])array);
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
        alertDialog$Builder.setTitle(2131297009);
        alertDialog$Builder.setSingleChoiceItems((ListAdapter)arrayAdapter, n, (DialogInterface$OnClickListener)new _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$5(this, availableCacheDirs, cacheLocationPreference, baseCacheDir));
        alertDialog$Builder.setCancelable(true);
        alertDialog$Builder.create().show();
    }
    
    private void handleClearCachePreference() {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
        alertDialog$Builder.setMessage(2131296442);
        alertDialog$Builder.setCancelable(true);
        alertDialog$Builder.setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$2());
        alertDialog$Builder.setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$4(this));
        alertDialog$Builder.create().show();
    }
    
    private void handleRingtonePreference(final RingtonePreference requestedRingtonePreference) {
        final Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", (Parcelable)NotificationSounds.getResourceUri(requestedRingtonePreference.getDefaultRawResource()));
        final String string = requestedRingtonePreference.getSharedPreferences().getString(requestedRingtonePreference.getKey(), (String)null);
        if (string != null) {
            if (string.length() == 0) {
                intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", (Parcelable)null);
            }
            else {
                intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", (Parcelable)Uri.parse(string));
            }
        }
        else {
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", (Parcelable)NotificationSounds.getResourceUri(requestedRingtonePreference.getDefaultRawResource()));
        }
        this.requestedRingtonePreference = requestedRingtonePreference;
        this.startActivityForResult(intent, 2);
    }
    
    public static Bundle makeSelection(final int n) {
        final Bundle bundle = new Bundle();
        bundle.putInt("prefResourceId", n);
        return bundle;
    }
    
    private void updatePreferencesDisplay() {
        final RecyclerView listView = this.getListView();
        if (listView != null) {
            final RecyclerView.Adapter adapter = listView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
    
    @Nullable
    @Override
    public String getSubTitle() {
        return null;
    }
    
    @Nullable
    @Override
    public String getTitle() {
        final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        if (preferenceScreen != null) {
            final CharSequence title = preferenceScreen.getTitle();
            if (title != null) {
                return title.toString();
            }
        }
        return null;
    }
    
    @SuppressLint({ "CommitPrefEdits" })
    @Override
    public void onActivityResult(final int n, final int n2, final Intent intent) {
        if (n == 2 && n2 == -1 && intent != null && this.requestedRingtonePreference != null) {
            final Uri uri = (Uri)intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String string;
            if (uri != null) {
                string = uri.toString();
            }
            else {
                string = "";
            }
            final SharedPreferences$Editor edit = this.requestedRingtonePreference.getSharedPreferences().edit();
            edit.putString(this.requestedRingtonePreference.getKey(), string);
            edit.commit();
            this.updatePreferencesDisplay();
        }
        else if (n == 11) {
            this.updatePreferencesDisplay();
        }
        else {
            super.onActivityResult(n, n2, intent);
        }
    }
    
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        this.addPreferencesFromResource(this.getArguments().getInt("prefResourceId"));
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).onFragmentTitleUpdated();
        }
        final Preference preference = this.findPreference("soundOnNotify");
        if (preference != null) {
            preference.setVisible(NotificationChannels.getInstance().areNotificationsSystemControlled() ^ true);
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).onFragmentTitleUpdated();
        }
    }
    
    @Override
    public boolean onPreferenceTreeClick(final Preference preference) {
        if (preference instanceof PreferenceSubPage) {
            final PreferenceSubPage preferenceSubPage = (PreferenceSubPage)preference;
            final NotificationType notificationType = preferenceSubPage.getNotificationType();
            if (notificationType != null) {
                final NotificationChannels.Channel channelByType = NotificationChannels.getInstance().getChannelByType(notificationType);
                if (channelByType != null && NotificationChannels.getInstance().showSystemNotificationSettings(this.getContext(), this, channelByType)) {
                    return true;
                }
            }
            DetailsActivity.showEmbeddedDetails(this.getActivity(), SettingsSubPageFragment.class, makeSelection(preferenceSubPage.getPageResource()));
            return true;
        }
        if (preference instanceof RingtonePreference) {
            this.handleRingtonePreference((RingtonePreference)preference);
            return true;
        }
        if (preference instanceof CacheLocationPreference) {
            this.handleCacheLocationPreference((CacheLocationPreference)preference);
            return true;
        }
        if (preference instanceof ClearCachePreference) {
            this.handleClearCachePreference();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).onFragmentTitleUpdated();
        }
    }
    
    private class ClearCacheTask extends AsyncTask<Void, Void, Void>
    {
        private ImmutableList<File> cacheDirs;
        private ProgressDialog progressDialog;
        
        private ClearCacheTask() {
            this.progressDialog = null;
        }
        
        protected Void doInBackground(final Void... array) {
            if (this.cacheDirs != null) {
                final Iterator<Object> iterator = this.cacheDirs.iterator();
                while (iterator.hasNext()) {
                    FileUtils.clearFolder(iterator.next());
                }
            }
            return null;
        }
        
        protected void onPostExecute(final Void void1) {
            if (this.progressDialog != null) {
                this.progressDialog.dismiss();
            }
            if (GlobalOptions.getInstance().isCacheDirUsed()) {
                SettingsFragment.this.askForRestart();
            }
        }
        
        protected void onPreExecute() {
            this.cacheDirs = GlobalOptions.getInstance().getAvailableCacheDirs();
            this.progressDialog = ProgressDialog.show(SettingsFragment.this.getContext(), (CharSequence)null, (CharSequence)SettingsFragment.this.getString(2131296447), true, true, (DialogInterface$OnCancelListener)new _$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8$3(this));
        }
    }
}
