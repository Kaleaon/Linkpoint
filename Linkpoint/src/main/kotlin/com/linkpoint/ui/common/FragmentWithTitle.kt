package com.linkpoint.ui.common

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.linkpoint.Debug
import javax.annotation.Nullable

class FragmentWithTitle : StateAwareFragment(), FragmentHasTitle {
    private const val FRAGMENT_SUBTITLE_TAG: String = "FragmentWithTitle:fragmentSubTitle"
    private const val FRAGMENT_TITLE_TAG: String = "FragmentWithTitle:fragmentTitle"
    private String fragmentSubTitle = null
    private String fragmentTitle = null

     public fun getSubTitle(): String {
        return this.fragmentSubTitle
    }

     public fun getTitle(): String {
        return this.fragmentTitle
    }

    override fun onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle)
        if (bundle != null) {
            this.fragmentTitle = bundle.getString(FRAGMENT_TITLE_TAG)
            this.fragmentSubTitle = bundle.getString(FRAGMENT_SUBTITLE_TAG)
        }
    }

    override fun onDetach() {
        super.onDetach()
        val activity: FragmentActivity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    fun onHiddenChanged(z: Boolean) {
        super.onHiddenChanged(z)
        val activity: FragmentActivity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(FRAGMENT_TITLE_TAG, this.fragmentTitle)
        bundle.putString(FRAGMENT_SUBTITLE_TAG, this.fragmentSubTitle)
    }

    override fun onStart() {
        super.onStart()
        val activity: FragmentActivity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    fun setTitle(str: String, str2: String) {
        this.fragmentTitle = str
        this.fragmentSubTitle = str2
        val activity: FragmentActivity = getActivity()
        Debug.Printf("updateTitle: title '%s', subTitle '%s', activity %s, fragment %s", str, str2, activity, this)
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }
}
