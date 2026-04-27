package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;

public abstract class MasterDetailsActivity extends DetailsActivity {
    protected static final String FROM_SAME_ACTIVITY = "fromSameActivity";
    private static final String IMPLICIT_DETAILS_TAG = "MasterDetailsActivityIsImplicitDetails";
    public static final String INTENT_SELECTION_KEY = "selection";
    public static final String WEAK_SELECTION_KEY = "weakSelection";
    private boolean isSplitScreen = false;

    /* access modifiers changed from: protected */
    public abstract FragmentActivityFactory getDetailsFragmentFactory();

    /* access modifiers changed from: protected */
    public Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        return bundle2;
    }

    /* access modifiers changed from: protected */
    public boolean isAlwaysImplicitFragment(Class<? extends Fragment> cls) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        return getDetailsFragmentFactory().getFragmentClass().isAssignableFrom(cls);
    }

    public boolean isSplitScreen() {
        return this.isSplitScreen;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:105:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0122  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01e5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0222 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(@android.support.annotation.Nullable android.os.Bundle r15) {
        /*
            r14 = this;
            r13 = 2131755654(0x7f100286, float:1.9142193E38)
            r10 = 2131755284(0x7f100114, float:1.9141443E38)
            r3 = 0
            r6 = 0
            r2 = 1
            super.onCreate(r15)
            boolean r1 = com.lumiyaviewer.lumiya.LumiyaApp.isSplitScreenNeeded(r14)
            r14.isSplitScreen = r1
            boolean r1 = r14.isSplitScreen
            if (r1 == 0) goto L_0x01a8
            r1 = 2130968740(0x7f0400a4, float:1.7546142E38)
            r14.setContentView((int) r1)
        L_0x001c:
            java.lang.String r4 = "MasterDetailsActivity: hasSelectorView = %b, sel fragment %b, hasDetailsView = %b, details fragment %b"
            r1 = 4
            java.lang.Object[] r5 = new java.lang.Object[r1]
            android.view.View r1 = r14.findViewById(r13)
            if (r1 == 0) goto L_0x01b0
            r1 = r2
        L_0x0029:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r1)
            r5[r3] = r1
            android.support.v4.app.FragmentManager r1 = r14.getSupportFragmentManager()
            android.support.v4.app.Fragment r1 = r1.findFragmentById(r13)
            if (r1 == 0) goto L_0x01b3
            r1 = r2
        L_0x003a:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r1)
            r5[r2] = r1
            android.view.View r1 = r14.findViewById(r10)
            if (r1 == 0) goto L_0x01b6
            r1 = r2
        L_0x0047:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r1)
            r7 = 2
            r5[r7] = r1
            android.support.v4.app.FragmentManager r1 = r14.getSupportFragmentManager()
            android.support.v4.app.Fragment r1 = r1.findFragmentById(r10)
            if (r1 == 0) goto L_0x01b9
            r1 = r2
        L_0x0059:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r1)
            r7 = 3
            r5[r7] = r1
            com.lumiyaviewer.lumiya.Debug.Printf(r4, r5)
            java.lang.String r1 = "MasterDetailsActivity: intent = %s"
            java.lang.Object[] r4 = new java.lang.Object[r2]
            android.content.Intent r5 = r14.getIntent()
            r4[r3] = r5
            com.lumiyaviewer.lumiya.Debug.Printf(r1, r4)
            android.support.v4.app.FragmentManager r1 = r14.getSupportFragmentManager()
            android.support.v4.app.FragmentTransaction r8 = r1.beginTransaction()
            android.support.v4.app.FragmentManager r1 = r14.getSupportFragmentManager()
            android.support.v4.app.Fragment r9 = r1.findFragmentById(r13)
            android.support.v4.app.FragmentManager r1 = r14.getSupportFragmentManager()
            android.support.v4.app.Fragment r10 = r1.findFragmentById(r10)
            if (r10 == 0) goto L_0x0236
            android.os.Bundle r1 = r10.getArguments()
            if (r1 == 0) goto L_0x0236
            java.lang.String r4 = "MasterDetailsActivity: implicit details tag = %b"
            java.lang.Object[] r5 = new java.lang.Object[r2]
            java.lang.String r7 = "MasterDetailsActivityIsImplicitDetails"
            boolean r7 = r1.getBoolean(r7, r3)
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r7)
            r5[r3] = r7
            com.lumiyaviewer.lumiya.Debug.Printf(r4, r5)
            java.lang.String r4 = "MasterDetailsActivityIsImplicitDetails"
            boolean r1 = r1.getBoolean(r4, r3)
            if (r1 != 0) goto L_0x0236
            r1 = r2
        L_0x00b0:
            java.lang.String r4 = "MasterDetailsActivity: hasExplicitDetails = %b"
            java.lang.Object[] r5 = new java.lang.Object[r2]
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r1)
            r5[r3] = r7
            com.lumiyaviewer.lumiya.Debug.Printf(r4, r5)
            if (r1 != 0) goto L_0x0233
            if (r15 != 0) goto L_0x0233
            android.content.Intent r4 = r14.getIntent()
            java.lang.String r5 = "selection"
            android.os.Bundle r5 = r4.getBundleExtra(r5)
            if (r5 == 0) goto L_0x0233
            r1 = r2
        L_0x00d0:
            if (r1 != 0) goto L_0x01bc
            if (r15 != 0) goto L_0x01bc
            boolean r4 = r14.isSplitScreen
            if (r4 == 0) goto L_0x022f
            android.content.Intent r4 = r14.getIntent()
            java.lang.String r7 = "weakSelection"
            android.os.Bundle r4 = r4.getBundleExtra(r7)
            if (r4 == 0) goto L_0x022f
            r1 = r4
            r4 = r2
        L_0x00e7:
            boolean r5 = r14.isSplitScreen
            if (r5 != 0) goto L_0x01c0
            r5 = r4 ^ 1
            r7 = r5
        L_0x00ee:
            if (r7 == 0) goto L_0x01e5
            java.lang.String r11 = "MasterDetailsActivity: existing fragment %s"
            java.lang.Object[] r12 = new java.lang.Object[r2]
            if (r9 == 0) goto L_0x01c3
            java.lang.String r5 = r9.toString()
        L_0x00fb:
            r12[r3] = r5
            com.lumiyaviewer.lumiya.Debug.Printf(r11, r12)
            if (r9 == 0) goto L_0x01d8
            java.lang.String r11 = "MasterDetailsActivity: existing fragment is %s"
            java.lang.Object[] r12 = new java.lang.Object[r2]
            boolean r5 = r9.isVisible()
            if (r5 == 0) goto L_0x01c8
            java.lang.String r5 = "visible"
        L_0x0110:
            r12[r3] = r5
            com.lumiyaviewer.lumiya.Debug.Printf(r11, r12)
            boolean r5 = r9.isDetached()
            if (r5 == 0) goto L_0x01cd
            r8.attach(r9)
        L_0x011e:
            boolean r5 = r14.isSplitScreen
            if (r5 != 0) goto L_0x01f2
            r5 = r4
        L_0x0123:
            java.lang.String r11 = "MasterDetailsActivity: selectorVisible %b, detailsVisible %b, hasExplicitDetails %b"
            r12 = 3
            java.lang.Object[] r12 = new java.lang.Object[r12]
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r7)
            r12[r3] = r7
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r5)
            r12[r2] = r7
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r4)
            r13 = 2
            r12[r13] = r7
            com.lumiyaviewer.lumiya.Debug.Printf(r11, r12)
            if (r5 == 0) goto L_0x0222
            if (r10 != 0) goto L_0x0202
            java.lang.String r2 = "MasterDetailsActivity: creating new details fragment"
            java.lang.Object[] r3 = new java.lang.Object[r3]
            com.lumiyaviewer.lumiya.Debug.Printf(r2, r3)
            if (r9 == 0) goto L_0x01f5
            android.os.Bundle r2 = r9.getArguments()     // Catch:{ Exception -> 0x01fd }
        L_0x0151:
            android.os.Bundle r3 = r14.getNewDetailsFragmentArguments(r2, r1)     // Catch:{ Exception -> 0x01fd }
            com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory r1 = r14.getDetailsFragmentFactory()     // Catch:{ Exception -> 0x01fd }
            java.lang.Class r1 = r1.getFragmentClass()     // Catch:{ Exception -> 0x01fd }
            java.lang.Object r1 = r1.newInstance()     // Catch:{ Exception -> 0x01fd }
            android.support.v4.app.Fragment r1 = (android.support.v4.app.Fragment) r1     // Catch:{ Exception -> 0x01fd }
            boolean r2 = r1 instanceof com.lumiyaviewer.lumiya.ui.common.ReloadableFragment     // Catch:{ Exception -> 0x01fd }
            if (r2 == 0) goto L_0x01f8
            android.os.Bundle r2 = new android.os.Bundle     // Catch:{ Exception -> 0x01fd }
            r2.<init>()     // Catch:{ Exception -> 0x01fd }
            r1.setArguments(r2)     // Catch:{ Exception -> 0x01fd }
            r0 = r1
            com.lumiyaviewer.lumiya.ui.common.ReloadableFragment r0 = (com.lumiyaviewer.lumiya.ui.common.ReloadableFragment) r0     // Catch:{ Exception -> 0x01fd }
            r2 = r0
            android.content.Intent r5 = r14.getIntent()     // Catch:{ Exception -> 0x01fd }
            r2.setFragmentArgs(r5, r3)     // Catch:{ Exception -> 0x01fd }
        L_0x017a:
            if (r4 != 0) goto L_0x0189
            android.os.Bundle r2 = r1.getArguments()     // Catch:{ Exception -> 0x01fd }
            if (r2 == 0) goto L_0x0189
            java.lang.String r3 = "MasterDetailsActivityIsImplicitDetails"
            r4 = 1
            r2.putBoolean(r3, r4)     // Catch:{ Exception -> 0x01fd }
        L_0x0189:
            java.lang.String r2 = "MasterDetailsActivity: adding new details fragment: %s"
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x01fd }
            r4 = 0
            r3[r4] = r1     // Catch:{ Exception -> 0x01fd }
            com.lumiyaviewer.lumiya.Debug.Printf(r2, r3)     // Catch:{ Exception -> 0x01fd }
            java.lang.String r2 = "defaultDetails"
            r3 = 2131755284(0x7f100114, float:1.9141443E38)
            r8.add(r3, r1, r2)     // Catch:{ Exception -> 0x01fd }
        L_0x019e:
            boolean r1 = r8.isEmpty()
            if (r1 != 0) goto L_0x01a7
            r8.commit()
        L_0x01a7:
            return
        L_0x01a8:
            r1 = 2130968737(0x7f0400a1, float:1.7546136E38)
            r14.setContentView((int) r1)
            goto L_0x001c
        L_0x01b0:
            r1 = r3
            goto L_0x0029
        L_0x01b3:
            r1 = r3
            goto L_0x003a
        L_0x01b6:
            r1 = r3
            goto L_0x0047
        L_0x01b9:
            r1 = r3
            goto L_0x0059
        L_0x01bc:
            r4 = r1
            r1 = r5
            goto L_0x00e7
        L_0x01c0:
            r7 = r2
            goto L_0x00ee
        L_0x01c3:
            java.lang.String r5 = "null"
            goto L_0x00fb
        L_0x01c8:
            java.lang.String r5 = "not visible"
            goto L_0x0110
        L_0x01cd:
            boolean r5 = r9.isHidden()
            if (r5 == 0) goto L_0x011e
            r8.show(r9)
            goto L_0x011e
        L_0x01d8:
            android.content.Intent r5 = r14.getIntent()
            android.support.v4.app.Fragment r5 = r14.onCreateMasterFragment(r5, r1)
            r8.add((int) r13, (android.support.v4.app.Fragment) r5)
            goto L_0x011e
        L_0x01e5:
            if (r9 == 0) goto L_0x011e
            boolean r5 = r9.isDetached()
            if (r5 != 0) goto L_0x011e
            r8.detach(r9)
            goto L_0x011e
        L_0x01f2:
            r5 = r2
            goto L_0x0123
        L_0x01f5:
            r2 = r6
            goto L_0x0151
        L_0x01f8:
            r1.setArguments(r3)     // Catch:{ Exception -> 0x01fd }
            goto L_0x017a
        L_0x01fd:
            r1 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r1)
            goto L_0x019e
        L_0x0202:
            java.lang.String r1 = "MasterDetailsActivity: not creating new details fragment. existing is detached: %b (%s)"
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            boolean r5 = r10.isDetached()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r3] = r5
            r4[r2] = r10
            com.lumiyaviewer.lumiya.Debug.Printf(r1, r4)
            boolean r1 = r10.isDetached()
            if (r1 == 0) goto L_0x019e
            r8.attach(r10)
            goto L_0x019e
        L_0x0222:
            if (r10 == 0) goto L_0x019e
            boolean r1 = r10.isDetached()
            if (r1 != 0) goto L_0x019e
            r8.remove(r10)
            goto L_0x019e
        L_0x022f:
            r4 = r1
            r1 = r5
            goto L_0x00e7
        L_0x0233:
            r5 = r6
            goto L_0x00d0
        L_0x0236:
            r1 = r3
            goto L_0x00b0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: protected */
    public abstract Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle);

    /* access modifiers changed from: protected */
    public boolean onDetailsStackEmpty() {
        FragmentManager supportFragmentManager;
        Fragment findFragmentById;
        if (this.isSplitScreen || (findFragmentById = supportFragmentManager.findFragmentById(R.id.details)) == null || !(!findFragmentById.isDetached())) {
            return true;
        }
        Debug.Printf("MasterDetailsFragment: onDetailsStackEmpty has detailsFragment (%s), detached: %b", findFragmentById, Boolean.valueOf(findFragmentById.isDetached()));
        FragmentTransaction beginTransaction = (supportFragmentManager = getSupportFragmentManager()).beginTransaction();
        beginTransaction.setCustomAnimations(17432576, R.anim.slide_to_right, 0, 17432577);
        beginTransaction.remove(findFragmentById);
        Fragment findFragmentById2 = supportFragmentManager.findFragmentById(R.id.selector);
        Object[] objArr = new Object[3];
        objArr[0] = Boolean.valueOf(findFragmentById2 != null);
        objArr[1] = Boolean.valueOf(findFragmentById2 != null ? findFragmentById2.isDetached() : false);
        objArr[2] = Boolean.valueOf(findFragmentById2 != null ? findFragmentById2.isHidden() : false);
        Debug.Printf("MasterDetailsFragment: existing selector %b, detached %b, hidden %b", objArr);
        if (findFragmentById2 == null) {
            beginTransaction.add((int) R.id.selector, onCreateMasterFragment(getIntent(), (Bundle) null));
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

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        Bundle bundle = null;
        super.onNewIntent(intent);
        Debug.Printf("MasterDetailsActivity: onNewIntent, intent = %s", intent);
        Bundle bundleExtra = intent.hasExtra(INTENT_SELECTION_KEY) ? intent.getBundleExtra(INTENT_SELECTION_KEY) : null;
        if (intent.hasExtra(WEAK_SELECTION_KEY)) {
            bundle = intent.getBundleExtra(WEAK_SELECTION_KEY);
        }
        if (bundleExtra != null) {
            showDetails(this, getDetailsFragmentFactory(), bundleExtra);
        } else if (this.isSplitScreen && bundle != null) {
            showDetails(this, getDetailsFragmentFactory(), bundle);
        } else if (this.isSplitScreen) {
        } else {
            if (getSupportFragmentManager().findFragmentById(R.id.details) != null || bundle == null || !intent.getBooleanExtra(FROM_SAME_ACTIVITY, false)) {
                clearDetailsStack();
                onDetailsStackEmpty();
                return;
            }
            showDetails(this, getDetailsFragmentFactory(), bundle);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void replaceDetailsFragment(FragmentManager fragmentManager, Fragment fragment) {
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

    public Fragment showDetailsFragment(Class<? extends Fragment> cls, Intent intent, Bundle bundle) {
        Bundle arguments;
        Fragment showDetailsFragment = super.showDetailsFragment(cls, intent, bundle);
        if (!(showDetailsFragment == null || (arguments = showDetailsFragment.getArguments()) == null)) {
            arguments.putBoolean(IMPLICIT_DETAILS_TAG, isAlwaysImplicitFragment(cls));
        }
        return showDetailsFragment;
    }

    /* access modifiers changed from: protected */
    public void updateTitleNoDetails() {
        boolean z;
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.selector);
        if (findFragmentById == null || !(findFragmentById instanceof FragmentHasTitle) || !findFragmentById.isAdded()) {
            z = false;
        } else {
            if (!findFragmentById.isDetached()) {
                String title = ((FragmentHasTitle) findFragmentById).getTitle();
                String subTitle = ((FragmentHasTitle) findFragmentById).getSubTitle();
                if (title != null) {
                    z = true;
                    setActivityTitle(title, subTitle);
                }
            }
            z = false;
        }
        if (!z) {
            super.updateTitleNoDetails();
        }
    }
}
