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
             public fun createFromParcel(parcel: Parcel): DetailsStackEntry {
                return DetailsStackEntry(parcel)
            }

            public Array<DetailsStackEntry> newArray(Int i) {
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
            val fragmentManager: FragmentManager = fragment2.getFragmentManager()
            if (fragmentManager != null) {
                this.savedState = fragmentManager.saveFragmentInstanceState(fragment2)
            } else {
                this.savedState = null
            }
        }

        /* synthetic */ DetailsStackEntry(Fragment fragment2, DetailsStackEntry detailsStackEntry) {
            this(fragment2)
        }

         public fun describeContents(): Int {
            return 0
        }

         public fun getFragment(context: Context): Fragment {
            val fragment2: Fragment = this.fragment.get()
            if (fragment2 == null) {
                fragment2 = Fragment.instantiate(context, this.className, this.arguments)
                if (this.savedState != null) {
                    fragment2.setInitialSavedState(this.savedState)
                }
            }
            return fragment2
        }

        fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(this.className)
            if (this.arguments != null) {
                parcel.writeByte((Byte) 1)
                parcel.writeBundle(this.arguments)
            } else {
                parcel.writeByte((Byte) 0)
            }
            if (this.savedState != null) {
                parcel.writeByte((Byte) 1)
                val bundle: Bundle = Bundle()
                bundle.putParcelable("savedState", this.savedState)
                parcel.writeBundle(bundle)
                return
            }
            parcel.writeByte((Byte) 0)
        }
    }

     private fun goBack(fragmentManager: FragmentManager): Boolean {
        Debug.Printf("DetailsActivity: goBack, detailsStack size %d", Integer.valueOf(this.detailsStack.size()))
        if (this.detailsStack.size() != 0) {
            val beginTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            beginTransaction.replace(R.id.details, this.detailsStack.remove(this.detailsStack.size() - 1).getFragment(this))
            beginTransaction.commit()
            updateTitle()
            return true
        }
        val onDetailsStackEmpty: Boolean = onDetailsStackEmpty()
        Debug.Printf("DetailsActivity: goBack, onDetailsStackEmpty: really empty: %b", Boolean.valueOf(onDetailsStackEmpty))
        return !onDetailsStackEmpty
    }

    @JvmStatic
     fun showDetails(activity: Activity, fragmentActivityFactory: FragmentActivityFactory, bundle: Bundle) {
        if (!showEmbeddedDetails(activity, fragmentActivityFactory.getFragmentClass(), bundle)) {
            activity.startActivity(fragmentActivityFactory.createIntent(activity, bundle))
        }
    }

    @JvmStatic
     fun showEmbeddedDetails(activity: Activity, cls: Class<? : Fragment>, bundle: Bundle): Boolean {
        if (!(activity instanceof DetailsActivity) || !((DetailsActivity) activity).acceptsDetailFragment(cls)) {
            return false
        }
        ((DetailsActivity) activity).showDetailsFragment(cls, activity.getIntent(), bundle)
        return true
    }

    /* access modifiers changed from: protected */
     public fun acceptsDetailFragment(cls: Class<? : Fragment>): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun addDetailsToStack(fragmentManager: FragmentManager) {
        val findFragmentById: Fragment = fragmentManager.findFragmentById(R.id.details)
        if (findFragmentById != null) {
            this.detailsStack.add(DetailsStackEntry(findFragmentById, (DetailsStackEntry) null))
        }
    }

    /* access modifiers changed from: package-private */
    fun clearDetailsStack() {
        this.detailsStack.clear()
    }

     public fun closeDetailsFragment(fragment: Fragment): Boolean {
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        if (supportFragmentManager.findFragmentById(R.id.details) == fragment) {
            return goBack(supportFragmentManager)
        }
        return false
    }

     public fun getCurrentDetailsFragment(): Fragment {
        Fragment findFragmentById
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null || (findFragmentById = supportFragmentManager.findFragmentById(R.id.details)) == null || !findFragmentById.isAdded() || !(!findFragmentById.isDetached()) || !(!findFragmentById.isHidden())) {
            return null
        }
        return findFragmentById
    }

     public fun handleBackPressed(): Boolean {
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.details)
        if ((findFragmentById instanceof BackButtonHandler) && findFragmentById.isAdded() && (!findFragmentById.isDetached()) && ((BackButtonHandler) findFragmentById).onBackButtonPressed()) {
            return true
        }
        if (supportFragmentManager.getBackStackEntryCount() != 0) {
            return false
        }
        return goBack(supportFragmentManager)
    }

    /* access modifiers changed from: protected */
     public fun isRootDetailsFragment(cls: Class<? : Fragment>): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        if (bundle != null) {
            val parcelableArrayList: ArrayList = bundle.getParcelableArrayList(DETAILS_STACK_TAG)
            if (parcelableArrayList != null) {
                this.detailsStack.addAll(parcelableArrayList)
            }
            this.defaultTitle = bundle.getString(DEFAULT_TITLE_TAG)
            this.defaultSubTitle = bundle.getString(DEFAULT_SUBTITLE_TAG)
        }
    }

    /* access modifiers changed from: protected */
     public fun onDetailsStackEmpty(): Boolean {
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.details)
        if (findFragmentById == null) {
            return true
        }
        val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.remove(findFragmentById)
        beginTransaction.commit()
        updateTitle()
        return false
    }

    fun onFragmentTitleUpdated() {
        updateTitle()
    }

    /* access modifiers changed from: protected */
    fun onPostCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onPostCreate(bundle)
        updateTitle()
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        val fragments: List<Fragment> = getSupportFragmentManager().getFragments()
        if (fragments != null) {
            for (Fragment onRequestPermissionsResult : fragments) {
                onRequestPermissionsResult.onRequestPermissionsResult(i, strArr, iArr)
            }
        }
    }

    /* access modifiers changed from: protected */
    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putParcelableArrayList(DETAILS_STACK_TAG, this.detailsStack)
        bundle.putString(DEFAULT_TITLE_TAG, this.defaultTitle)
        bundle.putString(DEFAULT_SUBTITLE_TAG, this.defaultSubTitle)
        super.onSaveInstanceState(bundle)
    }

    /* access modifiers changed from: protected */
    fun removeAllDetails() {
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        if (supportFragmentManager.findFragmentById(R.id.details) != null) {
            clearDetailsStack()
            goBack(supportFragmentManager)
        }
    }

    /* access modifiers changed from: protected */
    fun replaceDetailsFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        val beginTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(R.anim.slide_from_right, 0, 0, R.anim.slide_to_right)
        beginTransaction.replace(R.id.details, fragment)
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        beginTransaction.commit()
        updateTitle()
    }

    /* access modifiers changed from: protected */
    fun setActivityTitle(str: String, str2: String) {
        val supportActionBar: ActionBar = getSupportActionBar()
        Debug.Printf("updateTitle: title '%s' actionBar %s", str, supportActionBar)
        if (supportActionBar != null) {
            supportActionBar.setTitle((CharSequence) str)
            supportActionBar.setSubtitle((CharSequence) str2)
        }
        setTitle(str)
    }

     public fun setCurrentDetailsArguments(cls: Class<? : Fragment>, bundle: Bundle): Boolean {
        Fragment findFragmentById
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null || (findFragmentById = supportFragmentManager.findFragmentById(R.id.details)) == null || !cls.isInstance(findFragmentById) || !(findFragmentById instanceof ReloadableFragment) || findFragmentById.getArguments() == null) {
            return false
        }
        ((ReloadableFragment) findFragmentById).setFragmentArgs(getIntent(), bundle)
        return true
    }

    fun setDefaultTitle(str: String, str2: String) {
        this.defaultTitle = str
        this.defaultSubTitle = str2
        updateTitle()
    }

     public fun showDetailsFragment(cls: Class<? : Fragment>, intent: Intent, bundle: Bundle): Fragment {
        Debug.Printf("DetailsActivity: fragmentClass %s, intent %s, arguments %s", cls.toString(), intent, bundle)
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null) {
            return null
        }
        val isRootDetailsFragment: Boolean = isRootDetailsFragment(cls)
        val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.details)
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
                val fragment: Fragment = (Fragment) cls.newInstance()
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
                    val exc: Exception = e
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
    fun updateTitle() {
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
            java.lang.Array<Any> r4 = java.lang.Object[r2]
            r4[r3] = r1
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)
            val r0: Boolean = r1 instanceof com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle
            if (r0 == 0) goto L_0x0082
            java.lang.String r0 = "updateTitle: detailsFragment added %b hidden %b detached %b"
            r4 = 3
            java.lang.Array<Any> r4 = java.lang.Object[r4]
            val r5: Boolean = r1.isAdded()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r3] = r5
            val r5: Boolean = r1.isHidden()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r2] = r5
            val r5: Boolean = r1.isDetached()
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r6] = r5
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)
            val r0: Boolean = r1.isAdded()
            if (r0 == 0) goto L_0x0080
            val r0: Boolean = r1.isHidden()
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x0080
            val r0: Boolean = r1.isDetached()
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x0082
            r0 = r1
            com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle r0 = (com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle) r0
            java.lang.String r0 = r0.getTitle()
            com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle r1 = (com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle) r1
            java.lang.String r1 = r1.getSubTitle()
            java.lang.String r4 = "updateTitle: got title '%s', subtitle '%s'"
            java.lang.Array<Any> r5 = java.lang.Object[r6]
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
    fun updateTitleNoDetails() {
        if (this.defaultTitle != null) {
            setActivityTitle(this.defaultTitle, this.defaultSubTitle)
        }
    }
}
