// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.Debug;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import javax.annotation.Nullable;

public class FragmentWithTitle extends StateAwareFragment implements FragmentHasTitle
{
    private static final String FRAGMENT_SUBTITLE_TAG = "FragmentWithTitle:fragmentSubTitle";
    private static final String FRAGMENT_TITLE_TAG = "FragmentWithTitle:fragmentTitle";
    @Nullable
    private String fragmentSubTitle;
    @Nullable
    private String fragmentTitle;
    
    public FragmentWithTitle() {
        this.fragmentTitle = null;
        this.fragmentSubTitle = null;
    }
    
    @Nullable
    @Override
    public String getSubTitle() {
        return this.fragmentSubTitle;
    }
    
    @Nullable
    @Override
    public String getTitle() {
        return this.fragmentTitle;
    }
    
    @Override
    public void onCreate(@android.support.annotation.Nullable final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.fragmentTitle = bundle.getString("FragmentWithTitle:fragmentTitle");
            this.fragmentSubTitle = bundle.getString("FragmentWithTitle:fragmentSubTitle");
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).onFragmentTitleUpdated();
        }
    }
    
    @Override
    public void onHiddenChanged(final boolean b) {
        super.onHiddenChanged(b);
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).onFragmentTitleUpdated();
        }
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("FragmentWithTitle:fragmentTitle", this.fragmentTitle);
        bundle.putString("FragmentWithTitle:fragmentSubTitle", this.fragmentSubTitle);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).onFragmentTitleUpdated();
        }
    }
    
    public void setTitle(@Nullable final String fragmentTitle, @Nullable final String fragmentSubTitle) {
        this.fragmentTitle = fragmentTitle;
        this.fragmentSubTitle = fragmentSubTitle;
        final FragmentActivity activity = this.getActivity();
        Debug.Printf("updateTitle: title '%s', subTitle '%s', activity %s, fragment %s", fragmentTitle, fragmentSubTitle, activity, this);
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).onFragmentTitleUpdated();
        }
    }
}
