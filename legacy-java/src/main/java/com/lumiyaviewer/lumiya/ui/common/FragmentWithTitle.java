package com.lumiyaviewer.lumiya.ui.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class FragmentWithTitle extends StateAwareFragment implements FragmentHasTitle {
    private static final String FRAGMENT_SUBTITLE_TAG = "FragmentWithTitle:fragmentSubTitle";
    private static final String FRAGMENT_TITLE_TAG = "FragmentWithTitle:fragmentTitle";

    @Nullable
    private String fragmentTitle = null;

    @Nullable
    private String fragmentSubTitle = null;

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle
    @Nullable
    public String getSubTitle() {
        return this.fragmentSubTitle;
    }

    @Nullable
    public String getTitle() {
        return this.fragmentTitle;
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.fragmentTitle = bundle.getString(FRAGMENT_TITLE_TAG);
            this.fragmentSubTitle = bundle.getString(FRAGMENT_SUBTITLE_TAG);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onDetach() {
        super.onDetach();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onHiddenChanged(boolean z) {
        super.onHiddenChanged(z);
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(FRAGMENT_TITLE_TAG, this.fragmentTitle);
        bundle.putString(FRAGMENT_SUBTITLE_TAG, this.fragmentSubTitle);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }

    public void setTitle(@Nullable String str, @Nullable String str2) {
        this.fragmentTitle = str;
        this.fragmentSubTitle = str2;
        FragmentActivity activity = getActivity();
        Debug.Printf("updateTitle: title '%s', subTitle '%s', activity %s, fragment %s", str, str2, activity, this);
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).onFragmentTitleUpdated();
        }
    }
}
