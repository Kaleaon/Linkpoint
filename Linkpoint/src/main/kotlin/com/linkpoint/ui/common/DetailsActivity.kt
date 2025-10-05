package com.linkpoint.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import com.linkpoint.Debug
import com.linkpoint.R
import java.lang.ref.SoftReference
import java.util.ArrayList
import java.util.List
import javax.annotation.Nonnull
import javax.annotation.Nullable

class DetailsActivity : ConnectedActivity() {
    const val DEFAULT_DETAILS_FRAGMENT_TAG: String = "defaultDetails"
    private const val DEFAULT_SUBTITLE_TAG: String = "DetailsActivity:defaultSubTitle"
    private const val DEFAULT_TITLE_TAG: String = "DetailsActivity:defaultTitle"
    private const val DETAILS_STACK_TAG: String = "DetailsActivity:DetailsStack"
    private String defaultSubTitle = null
    private String defaultTitle = null
    private val ArrayList<DetailsStackEntry> detailsStack = ArrayList<>()

    @JvmStatic
private class DetailsStackEntry : Parcelable {
        const val Parcelable.Creator<DetailsStackEntry> CREATOR = Parcelable.Creator<DetailsStackEntry>() {
            public DetailsStackEntry createFromParcel(Parcel parcel) {
                return DetailsStackEntry(parcel)
            }

            public DetailsStackEntry[] newArray(Int i) {
                return DetailsStackEntry[i]
            }
        }
        val Bundle arguments
        val String className
        val SoftReference<Fragment> fragment
        val Fragment.SavedState savedState

        protected DetailsStackEntry(Parcel parcel) {
            this.fragment = null
            this.className = parcel.readString()
            if (parcel.readByte() != 0) {
                this.arguments = parcel.readBundle(getClass().getClassLoader())
            } else {
                this.arguments = null
            }
            if (parcel.readByte() != 0) {
                this.savedState = (Fragment.SavedState) parcel.readBundle(getClass().getClassLoader()).getParcelable("savedState")
            } else {
                this.savedState = null
            }
        }

        private DetailsStackEntry(Fragment fragment2) {
            this.fragment = SoftReference<>(fragment2)
            this.className = fragment2.getClass().getName()
            this.arguments = fragment2.getArguments()
            FragmentManager fragmentManager = fragment2.getFragmentManager()
            if (fragmentManager != null) {
                this.savedState = fragmentManager.saveFragmentInstanceState(fragment2)
            } else {
                this.savedState = null
            }
        }

        /* synthetic */ DetailsStackEntry(Fragment fragment2, DetailsStackEntry detailsStackEntry) {
            this(fragment2)
        }

        public Int describeContents() {
            return 0
        }

        public Fragment getFragment(Context context) {
            Fragment fragment2 = this.fragment.get()
            if (fragment2 == null) {
                fragment2 = Fragment.instantiate(context, this.className, this.arguments)
                if (this.savedState != null) {
                    fragment2.setInitialSavedState(this.savedState)
                }
            }
            return fragment2
        }

        public Unit writeToParcel(Parcel parcel, Int i) {
            parcel.writeString(this.className)
            if (this.arguments != null) {
                parcel.writeByte((Byte) 1)
                parcel.writeBundle(this.arguments)
            } else {
                parcel.writeByte((Byte) 0)
            }
            if (this.savedState != null) {
                parcel.writeByte((Byte) 1)
                Bundle bundle = Bundle()
                bundle.putParcelable("savedState", this.savedState)
                parcel.writeBundle(bundle)
                return
            }
            parcel.writeByte((Byte) 0)
        }
    }

    private Boolean goBack(FragmentManager fragmentManager) {
        Debug.Printf("DetailsActivity: goBack, detailsStack size %d", Integer.valueOf(this.detailsStack.size()))
        if (this.detailsStack.size() != 0) {
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction()
            beginTransaction.replace(R.id.details, this.detailsStack.remove(this.detailsStack.size() - 1).getFragment(this))
            beginTransaction.commit()
            updateTitle()
            return true
        }
        Boolean onDetailsStackEmpty = onDetailsStackEmpty()
        Debug.Printf("DetailsActivity: goBack, onDetailsStackEmpty: really empty: %b", Boolean.valueOf(onDetailsStackEmpty))
        return !onDetailsStackEmpty
    }

    @JvmStatic
    Unit showDetails(Activity activity, FragmentActivityFactory fragmentActivityFactory, Bundle bundle) {
        if (!showEmbeddedDetails(activity, fragmentActivityFactory.getFragmentClass(), bundle)) {
            activity.startActivity(fragmentActivityFactory.createIntent(activity, bundle))
        }
    }

    @JvmStatic
    Boolean showEmbeddedDetails(Activity activity, Class<? : Fragment> cls, Bundle bundle) {
        if (!(activity instanceof DetailsActivity) || !((DetailsActivity) activity).acceptsDetailFragment(cls)) {
            return false
        }
        ((DetailsActivity) activity).showDetailsFragment(cls, activity.getIntent(), bundle)
        return true
    }

    /* access modifiers changed from: protected */
    public Boolean acceptsDetailFragment(Class<? : Fragment> cls) {
        return true
    }

    /* access modifiers changed from: protected */
    public Unit addDetailsToStack(FragmentManager fragmentManager) {
        Fragment findFragmentById = fragmentManager.findFragmentById(R.id.details)
        if (findFragmentById != null) {
            this.detailsStack.add(DetailsStackEntry(findFragmentById, (DetailsStackEntry) null))
        }
    }

    /* access modifiers changed from: package-private */
    public Unit clearDetailsStack() {
        this.detailsStack.clear()
    }

    public Boolean closeDetailsFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager.findFragmentById(R.id.details) == fragment) {
            return goBack(supportFragmentManager)
        }
        return false
    }

    public Fragment getCurrentDetailsFragment() {
        Fragment findFragmentById
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null || (findFragmentById = supportFragmentManager.findFragmentById(R.id.details)) == null || !findFragmentById.isAdded() || !(!findFragmentById.isDetached()) || !(!findFragmentById.isHidden())) {
            return null
        }
        return findFragmentById
    }

    public Boolean handleBackPressed() {
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.details)
        if ((findFragmentById instanceof BackButtonHandler) && findFragmentById.isAdded() && (!findFragmentById.isDetached()) && ((BackButtonHandler) findFragmentById).onBackButtonPressed()) {
            return true
        }
        if (supportFragmentManager.getBackStackEntryCount() != 0) {
            return false
        }
        return goBack(supportFragmentManager)
    }

    /* access modifiers changed from: protected */
    public Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        return true
    }

    /* access modifiers changed from: protected */
    public Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
        if (bundle != null) {
            ArrayList parcelableArrayList = bundle.getParcelableArrayList(DETAILS_STACK_TAG)
            if (parcelableArrayList != null) {
                this.detailsStack.addAll(parcelableArrayList)
            }
            this.defaultTitle = bundle.getString(DEFAULT_TITLE_TAG)
            this.defaultSubTitle = bundle.getString(DEFAULT_SUBTITLE_TAG)
        }
    }

    /* access modifiers changed from: protected */
    public Boolean onDetailsStackEmpty() {
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.details)
        if (findFragmentById == null) {
            return true
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.remove(findFragmentById)
        beginTransaction.commit()
        updateTitle()
        return false
    }

    public Unit onFragmentTitleUpdated() {
        updateTitle()
    }

    /* access modifiers changed from: protected */
    public Unit onPostCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onPostCreate(bundle)
        updateTitle()
    }

    public Unit onRequestPermissionsResult(Int i, String[] strArr, Int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        List<Fragment> fragments = getSupportFragmentManager().getFragments()
        if (fragments != null) {
            for (Fragment onRequestPermissionsResult : fragments) {
                onRequestPermissionsResult.onRequestPermissionsResult(i, strArr, iArr)
            }
        }
    }

    /* access modifiers changed from: protected */
    public Unit onSaveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList(DETAILS_STACK_TAG, this.detailsStack)
        bundle.putString(DEFAULT_TITLE_TAG, this.defaultTitle)
        bundle.putString(DEFAULT_SUBTITLE_TAG, this.defaultSubTitle)
        super.onSaveInstanceState(bundle)
    }

    /* access modifiers changed from: protected */
    public Unit removeAllDetails() {
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager.findFragmentById(R.id.details) != null) {
            clearDetailsStack()
            goBack(supportFragmentManager)
        }
    }

    /* access modifiers changed from: protected */
    public Unit replaceDetailsFragment(FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(R.anim.slide_from_right, 0, 0, R.anim.slide_to_right)
        beginTransaction.replace(R.id.details, fragment)
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        beginTransaction.commit()
        updateTitle()
    }

    /* access modifiers changed from: protected */
    public Unit setActivityTitle(String str, String str2) {
        ActionBar supportActionBar = getSupportActionBar()
        Debug.Printf("updateTitle: title '%s' actionBar %s", str, supportActionBar)
        if (supportActionBar != null) {
            supportActionBar.setTitle((CharSequence) str)
            supportActionBar.setSubtitle((CharSequence) str2)
        }
        setTitle(str)
    }

    public Boolean setCurrentDetailsArguments(Class<? : Fragment> cls, Bundle bundle) {
        Fragment findFragmentById
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null || (findFragmentById = supportFragmentManager.findFragmentById(R.id.details)) == null || !cls.isInstance(findFragmentById) || !(findFragmentById instanceof ReloadableFragment) || findFragmentById.getArguments() == null) {
            return false
        }
        ((ReloadableFragment) findFragmentById).setFragmentArgs(getIntent(), bundle)
        return true
    }

    public Unit setDefaultTitle(String str, String str2) {
        this.defaultTitle = str
        this.defaultSubTitle = str2
        updateTitle()
    }

    public Fragment showDetailsFragment(Class<? : Fragment> cls, Intent intent, Bundle bundle) {
        Debug.Printf("DetailsActivity: fragmentClass %s, intent %s, arguments %s", cls.toString(), intent, bundle)
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null) {
            return null
        }
        Boolean isRootDetailsFragment = isRootDetailsFragment(cls)
        Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.details)
        Debug.Printf("DetailsActivity: isRootFragment %b existing fragment: %s", Boolean.valueOf(isRootDetailsFragment), findFragmentById)
        if (findFragmentById != null) {
            Debug.Printf("DetailsActivity: is good instance: %b", Boolean.valueOf(cls.isInstance(findFragmentById)))
            Debug.Printf("DetailsActivity: is reloadable: %b", Boolean.valueOf(findFragmentById instanceof ReloadableFragment))
            Debug.Printf("DetailsActivity: has arguments: %b", findFragmentById.getArguments())
        }
        if (findFragmentById == null || !findFragmentById.isVisible() || !cls.isInstance(findFragmentById) || !(findFragmentById instanceof ReloadableFragment) || findFragmentById.getArguments() == null) {
            if (isRootDetailsFragment) {
                clearDetailsStack()
            } else {
                addDetailsToStack(supportFragmentManager)
            }
            try {
                Fragment fragment = (Fragment) cls.newInstance()
                try {
                    if (fragment instanceof ReloadableFragment) {
                        fragment.setArguments(Bundle())
                        ((ReloadableFragment) fragment).setFragmentArgs(intent, bundle)
                    } else {
                        fragment.setArguments(bundle)
                    }
                    replaceDetailsFragment(supportFragmentManager, fragment)
                    return fragment
                } catch (Exception e) {
                    Exception exc = e
                    findFragmentById = fragment
                    e = exc
                }
            } catch (Exception e2) {
                e = e2
                Debug.Warning(e)
                return findFragmentById
            }
        } else {
            ((ReloadableFragment) findFragmentById).setFragmentArgs(intent, bundle)
            invalidateOptionsMenu()
            return findFragmentById
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Unit updateTitle() {
        /*
            r7 = this
            r6 = 2
            r2 = 1
            r3 = 0
            android.support.v4.app.FragmentManager r0 = r7.getSupportFragmentManager()
            if (r0 == 0) goto L_0x0082
            r1 = 2131755284(0x7f100114, Float:1.9141443E38)
            android.support.v4.app.Fragment r1 = r0.findFragmentById(r1)
            java.lang.String r0 = "updateTitle: detailsFragment %s"
            java.lang.Object[] r4 = java.lang.Object[r2]
            r4[r3] = r1
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)
            Boolean r0 = r1 instanceof com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle
            if (r0 == 0) goto L_0x0082
            java.lang.String r0 = "updateTitle: detailsFragment added %b hidden %b detached %b"
            r4 = 3
            java.lang.Object[] r4 = java.lang.Object[r4]
            Boolean r5 = r1.isAdded()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r3] = r5
            Boolean r5 = r1.isHidden()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r2] = r5
            Boolean r5 = r1.isDetached()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r6] = r5
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)
            Boolean r0 = r1.isAdded()
            if (r0 == 0) goto L_0x0080
            Boolean r0 = r1.isHidden()
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x0080
            Boolean r0 = r1.isDetached()
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x0082
            r0 = r1
            com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle r0 = (com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle) r0
            java.lang.String r0 = r0.getTitle()
            com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle r1 = (com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle) r1
            java.lang.String r1 = r1.getSubTitle()
            java.lang.String r4 = "updateTitle: got title '%s', subtitle '%s'"
            java.lang.Object[] r5 = java.lang.Object[r6]
            r5[r3] = r0
            r5[r2] = r1
            com.lumiyaviewer.lumiya.Debug.Printf(r4, r5)
            if (r0 == 0) goto L_0x0082
            r7.setActivityTitle(r0, r1)
            r0 = r2
        L_0x007a:
            if (r0 != 0) goto L_0x007f
            r7.updateTitleNoDetails()
        L_0x007f:
            return
        L_0x0080:
            r0 = r3
            goto L_0x007a
        L_0x0082:
            r0 = r3
            goto L_0x007a
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.common.DetailsActivity.updateTitle():Unit")
    }

    /* access modifiers changed from: protected */
    public Unit updateTitleNoDetails() {
        if (this.defaultTitle != null) {
            setActivityTitle(this.defaultTitle, this.defaultSubTitle)
        }
    }
}
