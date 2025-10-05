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

    public String getSubTitle() {
        return this.fragmentSubTitle
    }

    public String getTitle() {
        return this.fragmentTitle
    }

    public Unit onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle)
        if (bundle != null) {
            this.fragmentTitle = bundle.getString(FRAGMENT_TITLE_TAG)
            this.fragmentSubTitle = bundle.getString(FRAGMENT_SUBTITLE_TAG)
        }
    }

    public Unit onDetach() {
        super.onDetach()
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    public Unit onHiddenChanged(Boolean z) {
        super.onHiddenChanged(z)
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    public Unit onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(FRAGMENT_TITLE_TAG, this.fragmentTitle)
        bundle.putString(FRAGMENT_SUBTITLE_TAG, this.fragmentSubTitle)
    }

    public Unit onStart() {
        super.onStart()
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    public Unit setTitle(String str, String str2) {
        this.fragmentTitle = str
        this.fragmentSubTitle = str2
        FragmentActivity activity = getActivity()
        Debug.Printf("updateTitle: title '%s', subTitle '%s', activity %s, fragment %s", str, str2, activity, this)
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }
}
