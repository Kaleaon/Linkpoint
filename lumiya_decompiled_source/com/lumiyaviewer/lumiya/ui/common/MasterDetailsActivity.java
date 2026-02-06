package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
/* loaded from: classes.dex */
public abstract class MasterDetailsActivity extends DetailsActivity {
    protected static final String FROM_SAME_ACTIVITY = "fromSameActivity";
    private static final String IMPLICIT_DETAILS_TAG = "MasterDetailsActivityIsImplicitDetails";
    public static final String INTENT_SELECTION_KEY = "selection";
    public static final String WEAK_SELECTION_KEY = "weakSelection";
    private boolean isSplitScreen = false;

    protected abstract FragmentActivityFactory getDetailsFragmentFactory();

    /* JADX INFO: Access modifiers changed from: protected */
    public Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        return bundle2;
    }

    protected boolean isAlwaysImplicitFragment(Class<? extends Fragment> cls) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    public boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        return getDetailsFragmentFactory().getFragmentClass().isAssignableFrom(cls);
    }

    public boolean isSplitScreen() {
        return this.isSplitScreen;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:108:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00eb  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00f0  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0122  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0141  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x01a4  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x01c0  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x01e5  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x01f2  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0222  */
    @Override // com.lumiyaviewer.lumiya.ui.common.DetailsActivity, com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onCreate(@android.support.annotation.Nullable android.os.Bundle r15) {
        /*
            Method dump skipped, instructions count: 569
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity.onCreate(android.os.Bundle):void");
    }

    protected abstract Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    public boolean onDetailsStackEmpty() {
        FragmentManager supportFragmentManager;
        Fragment findFragmentById;
        if (this.isSplitScreen || (findFragmentById = (supportFragmentManager = getSupportFragmentManager()).findFragmentById(R.id.details)) == null || !(!findFragmentById.isDetached())) {
            return true;
        }
        Debug.Printf("MasterDetailsFragment: onDetailsStackEmpty has detailsFragment (%s), detached: %b", findFragmentById, Boolean.valueOf(findFragmentById.isDetached()));
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        beginTransaction.setCustomAnimations(17432576, R.anim.slide_to_right, 0, 17432577);
        beginTransaction.remove(findFragmentById);
        Fragment findFragmentById2 = supportFragmentManager.findFragmentById(R.id.selector);
        Object[] objArr = new Object[3];
        objArr[0] = Boolean.valueOf(findFragmentById2 != null);
        objArr[1] = Boolean.valueOf(findFragmentById2 != null ? findFragmentById2.isDetached() : false);
        objArr[2] = Boolean.valueOf(findFragmentById2 != null ? findFragmentById2.isHidden() : false);
        Debug.Printf("MasterDetailsFragment: existing selector %b, detached %b, hidden %b", objArr);
        if (findFragmentById2 == null) {
            beginTransaction.add(R.id.selector, onCreateMasterFragment(getIntent(), null));
        } else {
            if (findFragmentById2.isDetached()) {
                beginTransaction.attach(findFragmentById2);
            }
            if (findFragmentById2.isHidden()) {
                beginTransaction.show(findFragmentById2);
            }
        }
        beginTransaction.commit();
        updateTitle();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Debug.Printf("MasterDetailsActivity: onNewIntent, intent = %s", intent);
        Bundle bundleExtra = intent.hasExtra(INTENT_SELECTION_KEY) ? intent.getBundleExtra(INTENT_SELECTION_KEY) : null;
        Bundle bundleExtra2 = intent.hasExtra(WEAK_SELECTION_KEY) ? intent.getBundleExtra(WEAK_SELECTION_KEY) : null;
        if (bundleExtra != null) {
            showDetails(this, getDetailsFragmentFactory(), bundleExtra);
        } else if (this.isSplitScreen && bundleExtra2 != null) {
            showDetails(this, getDetailsFragmentFactory(), bundleExtra2);
        } else if (this.isSplitScreen) {
        } else {
            if (getSupportFragmentManager().findFragmentById(R.id.details) == null && bundleExtra2 != null && intent.getBooleanExtra(FROM_SAME_ACTIVITY, false)) {
                showDetails(this, getDetailsFragmentFactory(), bundleExtra2);
                return;
            }
            clearDetailsStack();
            onDetailsStackEmpty();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.DetailsActivity, com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    protected void replaceDetailsFragment(FragmentManager fragmentManager, Fragment fragment) {
        Fragment findFragmentById;
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.setCustomAnimations(R.anim.slide_from_right, 17432577, 0, 17432577);
        if (!this.isSplitScreen && (findFragmentById = fragmentManager.findFragmentById(R.id.selector)) != null && findFragmentById.isVisible()) {
            beginTransaction.hide(findFragmentById);
        }
        beginTransaction.replace(R.id.details, fragment);
        beginTransaction.commit();
        updateTitle();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    public Fragment showDetailsFragment(Class<? extends Fragment> cls, Intent intent, Bundle bundle) {
        Bundle arguments;
        Fragment showDetailsFragment = super.showDetailsFragment(cls, intent, bundle);
        if (showDetailsFragment != null && (arguments = showDetailsFragment.getArguments()) != null) {
            arguments.putBoolean(IMPLICIT_DETAILS_TAG, isAlwaysImplicitFragment(cls));
        }
        return showDetailsFragment;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    public void updateTitleNoDetails() {
        boolean z;
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.selector);
        if (findFragmentById != null && (findFragmentById instanceof FragmentHasTitle) && findFragmentById.isAdded()) {
            if (!findFragmentById.isDetached()) {
                String title = ((FragmentHasTitle) findFragmentById).getTitle();
                String subTitle = ((FragmentHasTitle) findFragmentById).getSubTitle();
                if (title != null) {
                    z = true;
                    setActivityTitle(title, subTitle);
                }
            }
            z = false;
        } else {
            z = false;
        }
        if (z) {
            return;
        }
        super.updateTitleNoDetails();
    }
}
