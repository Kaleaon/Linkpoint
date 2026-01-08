package com.linkpoint.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.ActionBar
import com.linkpoint.Debug
import com.linkpoint.R
import java.lang.ref.SoftReference
import java.util.ArrayList
import java.util.List
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class DetailsActivity : ConnectedActivity {
    val DEFAULT_DETAILS_FRAGMENT_TAG: String = "defaultDetails"
    private val DEFAULT_SUBTITLE_TAG: String = "DetailsActivity:defaultSubTitle"
    private val DEFAULT_TITLE_TAG: String = "DetailsActivity:defaultTitle"
    private val DETAILS_STACK_TAG: String = "DetailsActivity:DetailsStack"
    @Nullable
    private val defaultSubTitle: String = null
    @Nullable
    private val defaultTitle: String = null
    private ArrayList<DetailsStackEntry> detailsStack = ArrayList<>()

    private class DetailsStackEntry : Parcelable {
        Parcelable.Creator<DetailsStackEntry> CREATOR = Parcelable.Creator<DetailsStackEntry>() {
            fun createFromParcel(Parcel parcel): DetailsStackEntry {
                return DetailsStackEntry(parcel)
            }

            DetailsStackEntry[] newArray(Int i) {
                return DetailsStackEntry[i]
            }
        }
        Bundle arguments
        String className
        SoftReference<Fragment> fragment
        Fragment.SavedState savedState

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

        private DetailsStackEntry(@NonNull Fragment fragment2) {
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

        fun describeContents(): Int {
            return 0
        }

        fun getFragment(Context context): Fragment {
            Fragment fragment2 = this.fragment.get()
            if (fragment2 == null) {
                fragment2 = Fragment.instantiate(context, this.className, this.arguments)
                if (this.savedState != null) {
                    fragment2.setInitialSavedState(this.savedState)
                }
            }
            return fragment2
        }

        fun writeToParcel(Parcel parcel, Int i): Unit {
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
        Debug.Printf("DetailsActivity: goBack, detailsStack size %d", Int.valueOf(this.detailsStack.size()))
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

    fun showDetails(Activity activity, FragmentActivityFactory fragmentActivityFactory, Bundle bundle): Unit {
        if (!showEmbeddedDetails(activity, fragmentActivityFactory.getFragmentClass(), bundle)) {
            activity.startActivity(fragmentActivityFactory.createIntent(activity, bundle))
        }
    }

    fun showEmbeddedDetails(Activity activity, Class<? : Fragment> cls, Bundle bundle): Boolean {
        if (!(activity instanceof DetailsActivity) || !((DetailsActivity) activity).acceptsDetailFragment(cls)) {
            return false
        }
        ((DetailsActivity) activity).showDetailsFragment(cls, activity.getIntent(), bundle)
        return true
    }

    /* access modifiers changed from: protected */
    fun acceptsDetailFragment(Class<? : Fragment> cls): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun addDetailsToStack(FragmentManager fragmentManager): Unit {
        Fragment findFragmentById = fragmentManager.findFragmentById(R.id.details)
        if (findFragmentById != null) {
            this.detailsStack.add(DetailsStackEntry(findFragmentById, (DetailsStackEntry) null))
        }
    }

    /* access modifiers changed from: package-private */
    fun clearDetailsStack(): Unit {
        this.detailsStack.clear()
    }

    fun closeDetailsFragment(Fragment fragment): Boolean {
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager.findFragmentById(R.id.details) == fragment) {
            return goBack(supportFragmentManager)
        }
        return false
    }

    @Nullable
    fun getCurrentDetailsFragment(): Fragment {
        Fragment findFragmentById
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null || (findFragmentById = supportFragmentManager.findFragmentById(R.id.details)) == null || !findFragmentById.isAdded() || !(!findFragmentById.isDetached()) || !(!findFragmentById.isHidden())) {
            return null
        }
        return findFragmentById
    }

    fun handleBackPressed(): Boolean {
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
    fun isRootDetailsFragment(Class<? : Fragment> cls): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun onCreate(Bundle bundle): Unit {
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
    fun onDetailsStackEmpty(): Boolean {
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

    fun onFragmentTitleUpdated(): Unit {
        updateTitle()
    }

    /* access modifiers changed from: protected */
    fun onPostCreate(@android.support.annotation.Nullable Bundle bundle): Unit {
        super.onPostCreate(bundle)
        updateTitle()
    }

    fun onRequestPermissionsResult(Int i, @NonNull Array<String> strArr, @NonNull IntArray iArr): Unit {
        super.onRequestPermissionsResult(i, strArr, iArr)
        List<Fragment> fragments = getSupportFragmentManager().getFragments()
        if (fragments != null) {
            for (Fragment onRequestPermissionsResult : fragments) {
                onRequestPermissionsResult.onRequestPermissionsResult(i, strArr, iArr)
            }
        }
    }

    /* access modifiers changed from: protected */
    fun onSaveInstanceState(Bundle bundle): Unit {
        bundle.putParcelableArrayList(DETAILS_STACK_TAG, this.detailsStack)
        bundle.putString(DEFAULT_TITLE_TAG, this.defaultTitle)
        bundle.putString(DEFAULT_SUBTITLE_TAG, this.defaultSubTitle)
        super.onSaveInstanceState(bundle)
    }

    /* access modifiers changed from: protected */
    fun removeAllDetails(): Unit {
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager.findFragmentById(R.id.details) != null) {
            clearDetailsStack()
            goBack(supportFragmentManager)
        }
    }

    /* access modifiers changed from: protected */
    fun replaceDetailsFragment(FragmentManager fragmentManager, Fragment fragment): Unit {
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(R.anim.slide_from_right, 0, 0, R.anim.slide_to_right)
        beginTransaction.replace(R.id.details, fragment)
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        beginTransaction.commit()
        updateTitle()
    }

    /* access modifiers changed from: protected */
    fun setActivityTitle(@Nullable String str, @Nullable String str2): Unit {
        ActionBar supportActionBar = getSupportActionBar()
        Debug.Printf("updateTitle: title '%s' actionBar %s", str, supportActionBar)
        if (supportActionBar != null) {
            supportActionBar.setTitle((CharSequence) str)
            supportActionBar.setSubtitle((CharSequence) str2)
        }
        setTitle(str)
    }

    fun setCurrentDetailsArguments(Class<? : Fragment> cls, Bundle bundle): Boolean {
        Fragment findFragmentById
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null || (findFragmentById = supportFragmentManager.findFragmentById(R.id.details)) == null || !cls.isInstance(findFragmentById) || !(findFragmentById instanceof ReloadableFragment) || findFragmentById.getArguments() == null) {
            return false
        }
        ((ReloadableFragment) findFragmentById).setFragmentArgs(getIntent(), bundle)
        return true
    }

    fun setDefaultTitle(@Nullable String str, @Nullable String str2): Unit {
        this.defaultTitle = str
        this.defaultSubTitle = str2
        updateTitle()
    }

    @Nullable
    fun showDetailsFragment(Class<? : Fragment> cls, Intent intent, Bundle bundle): Fragment {
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
    fun updateTitle(): Unit {
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
            java.lang.Any[] r4 = java.lang.Any[r2]
            r4[r3] = r1
            com.linkpoint.Debug.Printf(r0, r4)
            Boolean r0 = r1 instanceof com.linkpoint.ui.common.FragmentHasTitle
            if (r0 == 0) goto L_0x0082
            java.lang.String r0 = "updateTitle: detailsFragment added %b hidden %b detached %b"
            r4 = 3
            java.lang.Any[] r4 = java.lang.Any[r4]
            Boolean r5 = r1.isAdded()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r3] = r5
            Boolean r5 = r1.isHidden()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r2] = r5
            Boolean r5 = r1.isDetached()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r6] = r5
            com.linkpoint.Debug.Printf(r0, r4)
            Boolean r0 = r1.isAdded()
            if (r0 == 0) goto L_0x0080
            Boolean r0 = r1.isHidden()
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x0080
            Boolean r0 = r1.isDetached()
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x0082
            r0 = r1
            com.linkpoint.ui.common.FragmentHasTitle r0 = (com.linkpoint.ui.common.FragmentHasTitle) r0
            java.lang.String r0 = r0.getTitle()
            com.linkpoint.ui.common.FragmentHasTitle r1 = (com.linkpoint.ui.common.FragmentHasTitle) r1
            java.lang.String r1 = r1.getSubTitle()
            java.lang.String r4 = "updateTitle: got title '%s', subtitle '%s'"
            java.lang.Any[] r5 = java.lang.Any[r6]
            r5[r3] = r0
            r5[r2] = r1
            com.linkpoint.Debug.Printf(r4, r5)
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
        throw UnsupportedOperationException("Method not decompiled: com.linkpoint.ui.common.DetailsActivity.updateTitle():Unit")
    }

    /* access modifiers changed from: protected */
    fun updateTitleNoDetails(): Unit {
        if (this.defaultTitle != null) {
            setActivityTitle(this.defaultTitle, this.defaultSubTitle)
        }
    }
}
