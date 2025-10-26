package com.lumiyaviewer.lumiya.ui.common

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lumiyaviewer.lumiya.Debug
import javax.annotation.Nullable

class FragmentWithTitle : StateAwareFragment : FragmentHasTitle {
    private String FRAGMENT_SUBTITLE_TAG = "FragmentWithTitle:fragmentSubTitle"
    private String FRAGMENT_TITLE_TAG = "FragmentWithTitle:fragmentTitle"
    @Nullable
    private String fragmentSubTitle = null
    @Nullable
    private String fragmentTitle = null

    @Nullable
    String getSubTitle() {
        return this.fragmentSubTitle
    }

    @Nullable
    String getTitle() {
        return this.fragmentTitle
    }

    Unit onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle)
        if (bundle != null) {
            this.fragmentTitle = bundle.getString(FRAGMENT_TITLE_TAG)
            this.fragmentSubTitle = bundle.getString(FRAGMENT_SUBTITLE_TAG)
        }
    }

    Unit onDetach() {
        super.onDetach()
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    Unit onHiddenChanged(Boolean z) {
        super.onHiddenChanged(z)
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    Unit onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(FRAGMENT_TITLE_TAG, this.fragmentTitle)
        bundle.putString(FRAGMENT_SUBTITLE_TAG, this.fragmentSubTitle)
    }

    Unit onStart() {
        super.onStart()
        FragmentActivity activity = getActivity()
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }

    Unit setTitle(@Nullable String str, @Nullable String str2) {
        this.fragmentTitle = str
        this.fragmentSubTitle = str2
        FragmentActivity activity = getActivity()
        Debug.Printf("updateTitle: title '%s', subTitle '%s', activity %s, fragment %s", str, str2, activity, this)
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated()
        }
    }
}
