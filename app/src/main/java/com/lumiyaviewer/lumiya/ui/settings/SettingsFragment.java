package com.lumiyaviewer.lumiya.ui.settings;
import java.util.*;

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
import com.lumiyaviewer.lumiya.utils.FileUtils;
import java.io.File;
import javax.annotation.Nullable;

public class SettingsFragment extends PreferenceFragmentCompat implements FragmentHasTitle {
    private static final String PREF_RESOURCE_KEY = "prefResourceId";
    private RingtonePreference requestedRingtonePreference = null;

    private class ClearCacheTask extends AsyncTask<Void, Void, Void> {
        private ImmutableList<File> cacheDirs;
        private ProgressDialog progressDialog;

        private ClearCacheTask() {
            this.progressDialog = null;
        }

        /* synthetic */ ClearCacheTask(SettingsFragment settingsFragment, ClearCacheTask clearCacheTask) {
            this();
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            if (this.cacheDirs != null) {
                for (File clearFolder : this.cacheDirs) {
                    FileUtils.clearFolder(clearFolder);
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment$ClearCacheTask_8620  reason: not valid java name */
        public /* synthetic */ void m860lambda$com_lumiyaviewer_lumiya_ui_settings_SettingsFragment$ClearCacheTask_8620(DialogInterface dialogInterface) {
            cancel(false);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void voidR) {
            if (this.progressDialog != null) {
                this.progressDialog.dismiss();
            }
            if (GlobalOptions.getInstance().isCacheDirUsed()) {
                SettingsFragment.this.askForRestart();
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.cacheDirs = GlobalOptions.getInstance().getAvailableCacheDirs();
            this.progressDialog = ProgressDialog.show(SettingsFragment.this.getContext(), (CharSequence) null, SettingsFragment.this.getString(R.string.clearing_cache), true, true, new DialogInterface.OnCancelListener(this) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f587$f0;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.3.$m$0(android.content.DialogInterface):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.3.$m$0(android.content.DialogInterface):void, class status: UNLOADED
                	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/

        }
    }

    /* access modifiers changed from: private */
    public void askForRestart() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.restart_after_changing_cache_location);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new $Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        builder.create().show();
    }

    @SuppressLint({"DefaultLocale"})
    private void handleCacheLocationPreference(CacheLocationPreference cacheLocationPreference) {
        int i = -1;
        ImmutableList<File> availableCacheDirs = GlobalOptions.getInstance().getAvailableCacheDirs();
        File baseCacheDir = GlobalOptions.getInstance().getBaseCacheDir();
        String[] strArr = new String[availableCacheDirs.size()];
        int i2 = 0;
        while (i2 < availableCacheDirs.size()) {
            int i3 = Objects.equal(availableCacheDirs.get(i2), baseCacheDir) ? i2 : i;
            StatFs statFs = new StatFs(((File) availableCacheDirs.get(i2)).getAbsolutePath());
            strArr[i2] = String.format("%s (%.1f Gb free)", new Object[]{CacheLocationPreference.makeDisplayableCacheLocation(((File) availableCacheDirs.get(i2)).getAbsolutePath()), Float.valueOf(((float) (Build.VERSION.SDK_INT >= 18 ? statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong() : (long) (statFs.getAvailableBlocks() * statFs.getBlockSize()))) / 1.07374182E9f)});
            i2++;
            i = i3;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), 17367058, strArr);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.select_cache_location);
        builder.setSingleChoiceItems(arrayAdapter, i, new DialogInterface.OnClickListener(this, availableCacheDirs, cacheLocationPreference, baseCacheDir) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f589$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f590$f1;

            /* renamed from: -$f2 */
            private final /* synthetic */ Object f591$f2;

            /* renamed from: -$f3 */
            private final /* synthetic */ Object f592$f3;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.5.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.5.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        builder.setCancelable(true);
        builder.create().show();
    }

    private void handleClearCachePreference() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.clear_cache_dialog_message);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.2.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.2.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f588$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.4.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$WG8cuhk2hT2A9U0oVctOmx_AHM8.4.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        builder.create().show();
    }

    private void handleRingtonePreference(RingtonePreference ringtonePreference) {
        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", NotificationSounds.getResourceUri(ringtonePreference.getDefaultRawResource()));
        String string = ringtonePreference.getSharedPreferences().getString(ringtonePreference.getKey(), (String) null);
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

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_7498  reason: not valid java name */
    static /* synthetic */ void m856lambda$com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_7498(DialogInterface dialogInterface, int i) {
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
        if (listView != null && (adapter = listView.getAdapter()) != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    public String getSubTitle() {
        return null;
    }

    @Nullable
    public String getTitle() {
        CharSequence title;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null || (title = preferenceScreen.getTitle()) == null) {
            return null;
        }
        return title.toString();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_6434  reason: not valid java name */
    public /* synthetic */ void m858lambda$com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_6434(ImmutableList immutableList, CacheLocationPreference cacheLocationPreference, File file, DialogInterface dialogInterface, int i) {
        File file2 = new File(((File) immutableList.get(i)).toString());
        SharedPreferences.Editor edit = cacheLocationPreference.getSharedPreferences().edit();
        edit.putString(cacheLocationPreference.getKey(), ((File) immutableList.get(i)).toString());
        edit.commit();
        dialogInterface.dismiss();
        updatePreferencesDisplay();
        if (!Objects.equal(file, file2) && GlobalOptions.getInstance().isCacheDirUsed()) {
            askForRestart();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_8022  reason: not valid java name */
    public /* synthetic */ void m859lambda$com_lumiyaviewer_lumiya_ui_settings_SettingsFragment_8022(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        new ClearCacheTask(this, (ClearCacheTask) null).execute(new Void[0]);
    }

    @SuppressLint({"CommitPrefEdits"})
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 2 && i2 == -1 && intent != null && this.requestedRingtonePreference != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String uri2 = uri != null ? uri.toString() : "";
            SharedPreferences.Editor edit = this.requestedRingtonePreference.getSharedPreferences().edit();
            edit.putString(this.requestedRingtonePreference.getKey(), uri2);
            edit.commit();
            updatePreferencesDisplay();
        } else if (i == 11) {
            updatePreferencesDisplay();
        } else {
            super.onActivityResult(i, i2, intent);
        }
    }

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

    public void onDetach() {
        super.onDetach();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }

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
        } else if (preference instanceof RingtonePreference) {
            handleRingtonePreference((RingtonePreference) preference);
            return true;
        } else if (preference instanceof CacheLocationPreference) {
            handleCacheLocationPreference((CacheLocationPreference) preference);
            return true;
        } else if (!(preference instanceof ClearCachePreference)) {
            return super.onPreferenceTreeClick(preference);
        } else {
            handleClearCachePreference();
            return true;
        }
    }

    public void onStart() {
        super.onStart();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }
}
