// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import com.lumiyaviewer.lumiya.Debug;
import android.content.Context;
import com.lumiyaviewer.lumiya.LumiyaApp;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;

public abstract class MasterDetailsActivity extends DetailsActivity
{
    protected static final String FROM_SAME_ACTIVITY = "fromSameActivity";
    private static final String IMPLICIT_DETAILS_TAG = "MasterDetailsActivityIsImplicitDetails";
    public static final String INTENT_SELECTION_KEY = "selection";
    public static final String WEAK_SELECTION_KEY = "weakSelection";
    private boolean isSplitScreen;
    
    public MasterDetailsActivity() {
        this.isSplitScreen = false;
    }
    
    protected abstract FragmentActivityFactory getDetailsFragmentFactory();
    
    protected Bundle getNewDetailsFragmentArguments(@Nullable final Bundle bundle, @Nullable final Bundle bundle2) {
        return bundle2;
    }
    
    protected boolean isAlwaysImplicitFragment(final Class<? extends Fragment> clazz) {
        return false;
    }
    
    @Override
    protected boolean isRootDetailsFragment(final Class<? extends Fragment> clazz) {
        return this.getDetailsFragmentFactory().getFragmentClass().isAssignableFrom(clazz);
    }
    
    public boolean isSplitScreen() {
        return this.isSplitScreen;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle bundleExtra) {
        super.onCreate(bundleExtra);
        this.isSplitScreen = LumiyaApp.isSplitScreenNeeded((Context)this);
    Label_0560:
        while (true) {
            FragmentTransaction beginTransaction = null;
            Fragment fragmentById2 = null;
            Label_0575: {
                if (!this.isSplitScreen) {
                    break Label_0575;
                }
                this.setContentView(2130968740);
            Label_0037_Outer:
                while (true) {
                    Label_0584: {
                        if (this.findViewById(2131755654) == null) {
                            break Label_0584;
                        }
                        boolean b = true;
                    Label_0051_Outer:
                        while (true) {
                            Label_0589: {
                                if (this.getSupportFragmentManager().findFragmentById(2131755654) == null) {
                                    break Label_0589;
                                }
                                boolean b2 = true;
                            Label_0063_Outer:
                                while (true) {
                                    Label_0594: {
                                        if (this.findViewById(2131755284) == null) {
                                            break Label_0594;
                                        }
                                        boolean b3 = true;
                                    Label_0078_Outer:
                                        while (true) {
                                            Label_0600: {
                                                if (this.getSupportFragmentManager().findFragmentById(2131755284) == null) {
                                                    break Label_0600;
                                                }
                                                boolean b4 = true;
                                                Fragment fragmentById;
                                                Bundle arguments;
                                                int n;
                                                Bundle bundleExtra2;
                                                int b5;
                                                String string;
                                                String s;
                                                boolean b6;
                                                Bundle arguments2;
                                                Bundle newDetailsFragmentArguments;
                                                Fragment fragment;
                                                Bundle arguments3;
                                                Label_0217_Outer:Label_0290_Outer:
                                                while (true) {
                                                    Debug.Printf("MasterDetailsActivity: hasSelectorView = %b, sel fragment %b, hasDetailsView = %b, details fragment %b", b, b2, b3, b4);
                                                    Debug.Printf("MasterDetailsActivity: intent = %s", this.getIntent());
                                                    beginTransaction = this.getSupportFragmentManager().beginTransaction();
                                                    fragmentById = this.getSupportFragmentManager().findFragmentById(2131755654);
                                                    fragmentById2 = this.getSupportFragmentManager().findFragmentById(2131755284);
                                                Label_0290:
                                                    while (true) {
                                                        Label_0606: {
                                                            while (true) {
                                                                Label_0803: {
                                                                    if (fragmentById2 == null) {
                                                                        break Label_0803;
                                                                    }
                                                                    arguments = fragmentById2.getArguments();
                                                                    if (arguments == null) {
                                                                        break Label_0803;
                                                                    }
                                                                    Debug.Printf("MasterDetailsActivity: implicit details tag = %b", arguments.getBoolean("MasterDetailsActivityIsImplicitDetails", false));
                                                                    if (arguments.getBoolean("MasterDetailsActivityIsImplicitDetails", false)) {
                                                                        break Label_0803;
                                                                    }
                                                                    n = 1;
                                                                    Debug.Printf("MasterDetailsActivity: hasExplicitDetails = %b", Boolean.valueOf((boolean)(n != 0)));
                                                                    while (true) {
                                                                        Label_0797: {
                                                                            if (n || bundleExtra != null) {
                                                                                break Label_0797;
                                                                            }
                                                                            bundleExtra2 = this.getIntent().getBundleExtra("selection");
                                                                            if (bundleExtra2 == null) {
                                                                                break Label_0797;
                                                                            }
                                                                            n = (true ? 1 : 0);
                                                                            if (n == 0 && bundleExtra == null) {
                                                                                if (this.isSplitScreen) {
                                                                                    bundleExtra = this.getIntent().getBundleExtra("weakSelection");
                                                                                    if (bundleExtra != null) {
                                                                                        n = 1;
                                                                                        break Label_0290;
                                                                                    }
                                                                                }
                                                                                bundleExtra = bundleExtra2;
                                                                                break Label_0290;
                                                                            }
                                                                            break Label_0606;
                                                                        }
                                                                        bundleExtra2 = null;
                                                                        continue Label_0290_Outer;
                                                                    }
                                                                }
                                                                n = 0;
                                                                continue Label_0290_Outer;
                                                            }
                                                            Label_0612: {
                                                                if (this.isSplitScreen) {
                                                                    break Label_0612;
                                                                }
                                                                b5 = (n ^ 0x1);
                                                            Label_0317_Outer:
                                                                while (true) {
                                                                    Label_0670: {
                                                                        if (b5 == 0) {
                                                                            break Label_0670;
                                                                        }
                                                                        Label_0617: {
                                                                            if (fragmentById == null) {
                                                                                break Label_0617;
                                                                            }
                                                                            string = fragmentById.toString();
                                                                        Label_0348_Outer:
                                                                            while (true) {
                                                                                Debug.Printf("MasterDetailsActivity: existing fragment %s", string);
                                                                                Label_0650: {
                                                                                    if (fragmentById == null) {
                                                                                        break Label_0650;
                                                                                    }
                                                                                    Label_0624: {
                                                                                        if (!fragmentById.isVisible()) {
                                                                                            break Label_0624;
                                                                                        }
                                                                                        s = "visible";
                                                                                    Label_0378_Outer:
                                                                                        while (true) {
                                                                                            Debug.Printf("MasterDetailsActivity: existing fragment is %s", s);
                                                                                            Label_0631: {
                                                                                                if (!fragmentById.isDetached()) {
                                                                                                    break Label_0631;
                                                                                                }
                                                                                                beginTransaction.attach(fragmentById);
                                                                                            Label_0388_Outer:
                                                                                                while (true) {
                                                                                                    Label_0694: {
                                                                                                        if (this.isSplitScreen) {
                                                                                                            break Label_0694;
                                                                                                        }
                                                                                                        b6 = (n != 0);
                                                                                                        Label_0450_Outer:Block_34_Outer:
                                                                                                        while (true) {
                                                                                                            Debug.Printf("MasterDetailsActivity: selectorVisible %b, detailsVisible %b, hasExplicitDetails %b", Boolean.valueOf((boolean)(b5 != 0)), b6, Boolean.valueOf((boolean)(n != 0)));
                                                                                                            Label_0700: {
                                                                                                                if (b6) {
                                                                                                                    if (fragmentById2 != null) {
                                                                                                                        break Label_0575;
                                                                                                                    }
                                                                                                                    Debug.Printf("MasterDetailsActivity: creating new details fragment", new Object[0]);
                                                                                                                    if (fragmentById == null) {
                                                                                                                        break Label_0700;
                                                                                                                    }
                                                                                                                }
                                                                                                                else {
                                                                                                                    if (fragmentById2 != null && !fragmentById2.isDetached()) {
                                                                                                                        beginTransaction.remove(fragmentById2);
                                                                                                                        break Label_0560;
                                                                                                                    }
                                                                                                                    break Label_0560;
                                                                                                                }
                                                                                                                try {
                                                                                                                    arguments2 = fragmentById.getArguments();
                                                                                                                Block_36:
                                                                                                                    while (true) {
                                                                                                                        while (true) {
                                                                                                                            newDetailsFragmentArguments = this.getNewDetailsFragmentArguments(arguments2, bundleExtra);
                                                                                                                            fragment = (Fragment)this.getDetailsFragmentFactory().getFragmentClass().newInstance();
                                                                                                                            if (fragment instanceof ReloadableFragment) {
                                                                                                                                fragment.setArguments(new Bundle());
                                                                                                                                ((ReloadableFragment)fragment).setFragmentArgs(this.getIntent(), newDetailsFragmentArguments);
                                                                                                                            }
                                                                                                                            else {
                                                                                                                                fragment.setArguments(newDetailsFragmentArguments);
                                                                                                                            }
                                                                                                                            if (n == 0) {
                                                                                                                                arguments3 = fragment.getArguments();
                                                                                                                                if (arguments3 != null) {
                                                                                                                                    arguments3.putBoolean("MasterDetailsActivityIsImplicitDetails", true);
                                                                                                                                }
                                                                                                                            }
                                                                                                                            Debug.Printf("MasterDetailsActivity: adding new details fragment: %s", fragment);
                                                                                                                            beginTransaction.add(2131755284, fragment, "defaultDetails");
                                                                                                                            if (!beginTransaction.isEmpty()) {
                                                                                                                                beginTransaction.commit();
                                                                                                                            }
                                                                                                                            return;
                                                                                                                            b = false;
                                                                                                                            continue Label_0051_Outer;
                                                                                                                            b2 = false;
                                                                                                                            continue Label_0063_Outer;
                                                                                                                            beginTransaction.show(fragmentById);
                                                                                                                            continue Label_0388_Outer;
                                                                                                                            arguments2 = null;
                                                                                                                            continue Block_34_Outer;
                                                                                                                        }
                                                                                                                        iftrue(Label_0378:)(fragmentById == null || fragmentById.isDetached());
                                                                                                                        break Block_36;
                                                                                                                        string = "null";
                                                                                                                        continue Label_0348_Outer;
                                                                                                                        bundleExtra = bundleExtra2;
                                                                                                                        continue Label_0290;
                                                                                                                        this.setContentView(2130968737);
                                                                                                                        continue Label_0037_Outer;
                                                                                                                        b4 = false;
                                                                                                                        continue Label_0217_Outer;
                                                                                                                        b5 = (true ? 1 : 0);
                                                                                                                        continue Label_0317_Outer;
                                                                                                                        b6 = true;
                                                                                                                        continue Label_0450_Outer;
                                                                                                                        beginTransaction.add(2131755654, this.onCreateMasterFragment(this.getIntent(), bundleExtra));
                                                                                                                        continue Label_0388_Outer;
                                                                                                                        b3 = false;
                                                                                                                        continue Label_0078_Outer;
                                                                                                                        s = "not visible";
                                                                                                                        continue Label_0378_Outer;
                                                                                                                        iftrue(Label_0378:)(!fragmentById.isHidden());
                                                                                                                        continue;
                                                                                                                    }
                                                                                                                    beginTransaction.detach(fragmentById);
                                                                                                                    continue Label_0388_Outer;
                                                                                                                }
                                                                                                                catch (final Exception ex) {
                                                                                                                    Debug.Warning(ex);
                                                                                                                    continue Label_0560;
                                                                                                                }
                                                                                                            }
                                                                                                            break;
                                                                                                        }
                                                                                                    }
                                                                                                    break;
                                                                                                }
                                                                                            }
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                }
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            Debug.Printf("MasterDetailsActivity: not creating new details fragment. existing is detached: %b (%s)", fragmentById2.isDetached(), fragmentById2);
            if (fragmentById2.isDetached()) {
                beginTransaction.attach(fragmentById2);
                continue Label_0560;
            }
            continue Label_0560;
        }
    }
    
    protected abstract Fragment onCreateMasterFragment(final Intent p0, @Nullable final Bundle p1);
    
    @Override
    protected boolean onDetailsStackEmpty() {
        if (this.isSplitScreen) {
            return true;
        }
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755284);
        if (fragmentById != null && (fragmentById.isDetached() ^ true)) {
            Debug.Printf("MasterDetailsFragment: onDetailsStackEmpty has detailsFragment (%s), detached: %b", fragmentById, fragmentById.isDetached());
            final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.setCustomAnimations(17432576, 2131034130, 0, 17432577);
            beginTransaction.remove(fragmentById);
            final Fragment fragmentById2 = supportFragmentManager.findFragmentById(2131755654);
            Debug.Printf("MasterDetailsFragment: existing selector %b, detached %b, hidden %b", fragmentById2 != null, fragmentById2 != null && fragmentById2.isDetached(), fragmentById2 != null && fragmentById2.isHidden());
            if (fragmentById2 == null) {
                beginTransaction.add(2131755654, this.onCreateMasterFragment(this.getIntent(), null));
            }
            else {
                if (fragmentById2.isDetached()) {
                    beginTransaction.attach(fragmentById2);
                }
                if (fragmentById2.isHidden()) {
                    beginTransaction.show(fragmentById2);
                }
            }
            beginTransaction.commit();
            this.updateTitle();
            return false;
        }
        return true;
    }
    
    @Override
    protected void onNewIntent(final Intent intent) {
        Bundle bundleExtra = null;
        super.onNewIntent(intent);
        Debug.Printf("MasterDetailsActivity: onNewIntent, intent = %s", intent);
        Bundle bundleExtra2;
        if (intent.hasExtra("selection")) {
            bundleExtra2 = intent.getBundleExtra("selection");
        }
        else {
            bundleExtra2 = null;
        }
        if (intent.hasExtra("weakSelection")) {
            bundleExtra = intent.getBundleExtra("weakSelection");
        }
        if (bundleExtra2 != null) {
            DetailsActivity.showDetails(this, this.getDetailsFragmentFactory(), bundleExtra2);
        }
        else if (this.isSplitScreen && bundleExtra != null) {
            DetailsActivity.showDetails(this, this.getDetailsFragmentFactory(), bundleExtra);
        }
        else if (!this.isSplitScreen) {
            if (this.getSupportFragmentManager().findFragmentById(2131755284) == null && bundleExtra != null && intent.getBooleanExtra("fromSameActivity", false)) {
                DetailsActivity.showDetails(this, this.getDetailsFragmentFactory(), bundleExtra);
            }
            else {
                this.clearDetailsStack();
                this.onDetailsStackEmpty();
            }
        }
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }
    
    @Override
    protected void replaceDetailsFragment(final FragmentManager fragmentManager, final Fragment fragment) {
        final FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.setCustomAnimations(2131034128, 17432577, 0, 17432577);
        if (!this.isSplitScreen) {
            final Fragment fragmentById = fragmentManager.findFragmentById(2131755654);
            if (fragmentById != null && fragmentById.isVisible()) {
                beginTransaction.hide(fragmentById);
            }
        }
        beginTransaction.replace(2131755284, fragment);
        beginTransaction.commit();
        this.updateTitle();
    }
    
    @Override
    public Fragment showDetailsFragment(final Class<? extends Fragment> clazz, final Intent intent, final Bundle bundle) {
        final Fragment showDetailsFragment = super.showDetailsFragment(clazz, intent, bundle);
        if (showDetailsFragment != null) {
            final Bundle arguments = showDetailsFragment.getArguments();
            if (arguments != null) {
                arguments.putBoolean("MasterDetailsActivityIsImplicitDetails", this.isAlwaysImplicitFragment(clazz));
            }
        }
        return showDetailsFragment;
    }
    
    @Override
    protected void updateTitleNoDetails() {
        final Fragment fragmentById = this.getSupportFragmentManager().findFragmentById(2131755654);
        int n = 0;
        Label_0069: {
            if (fragmentById != null && fragmentById instanceof FragmentHasTitle && fragmentById.isAdded()) {
                if (fragmentById.isDetached() ^ true) {
                    final String title = ((FragmentHasTitle)fragmentById).getTitle();
                    final String subTitle = ((FragmentHasTitle)fragmentById).getSubTitle();
                    if (title != null) {
                        n = 1;
                        this.setActivityTitle(title, subTitle);
                        break Label_0069;
                    }
                }
                n = 0;
            }
            else {
                n = 0;
            }
        }
        if (n == 0) {
            super.updateTitleNoDetails();
        }
    }
}
