package com.linkpoint.ui.common

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.linkpoint.Debug
import androidx.annotation.Nullable

class FragmentWithTitle : StateAwareFragment : FragmentHasTitle {
    private val FRAGMENT_SUBTITLE_TAG: String = "FragmentWithTitle:fragmentSubTitle"
    private val FRAGMENT_TITLE_TAG: String = "FragmentWithTitle:fragmentTitle"
    @Nullable
    private val fragmentSubTitle: String = null
    @Nullable
    private val fragmentTitle: String = null

    @Nullable
    fun getSubTitle(): String {
        return this.fragmentSubTitle
    }

    @Nullable
    fun getTitle(): String {
        return this.fragmentTitle
    }

    fun onCreate(@android.support.annotation.Nullable Bundle bundle): Unit {
        super.onCreate(bundle)
        if (bundle != null) {
            this.fragmentTitle = bundle.getString(FRAGMENT_TITLE_TAG)
            this.fragmentSubTitle = bundle.getString(FRAGMENT_SUBTITLE_TAG)
        }
    }

    fun onDetach(): Unit {
        super.onDetach()
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    fun onHiddenChanged(Boolean z): Unit {
        super.onHiddenChanged(z)
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    fun onSaveInstanceState(Bundle bundle): Unit {
        super.onSaveInstanceState(bundle)
        bundle.putString(FRAGMENT_TITLE_TAG, this.fragmentTitle)
        bundle.putString(FRAGMENT_SUBTITLE_TAG, this.fragmentSubTitle)
    }

    fun onStart(): Unit {
        super.onStart()
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    fun setTitle(@Nullable String str, @Nullable String str2): Unit {
        this.fragmentTitle = str
        this.fragmentSubTitle = str2
        FragmentActivity activity = getActivity()
        Debug.Printf("updateTitle: title '%s', subTitle '%s', activity %s, fragment %s", str, str2, activity, this)
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }
}
