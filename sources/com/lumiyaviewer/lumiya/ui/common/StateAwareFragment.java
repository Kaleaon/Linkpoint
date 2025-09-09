package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.app.Fragment;

public class StateAwareFragment extends Fragment {
    private boolean fragmentStarted = false;
    private boolean fragmentVisible = false;

    public boolean isFragmentStarted() {
        return this.fragmentStarted;
    }

    public boolean isFragmentVisible() {
        return this.fragmentVisible;
    }

    public void onPause() {
        this.fragmentVisible = false;
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        this.fragmentVisible = true;
    }

    public void onStart() {
        super.onStart();
        this.fragmentStarted = true;
    }

    public void onStop() {
        this.fragmentStarted = false;
        super.onStop();
    }
}
