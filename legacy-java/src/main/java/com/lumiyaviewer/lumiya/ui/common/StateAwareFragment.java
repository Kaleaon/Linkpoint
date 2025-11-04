package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.app.Fragment;

/* loaded from: classes.dex */
public class StateAwareFragment extends Fragment {
    private boolean fragmentStarted = false;
    private boolean fragmentVisible = false;

    public boolean isFragmentStarted() {
        return this.fragmentStarted;
    }

    public boolean isFragmentVisible() {
        return this.fragmentVisible;
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        this.fragmentVisible = false;
        super.onPause();
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.fragmentVisible = true;
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        this.fragmentStarted = true;
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        this.fragmentStarted = false;
        super.onStop();
    }
}
