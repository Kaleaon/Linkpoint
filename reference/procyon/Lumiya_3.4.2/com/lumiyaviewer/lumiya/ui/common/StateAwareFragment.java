// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.app.Fragment;

public class StateAwareFragment extends Fragment
{
    private boolean fragmentStarted;
    private boolean fragmentVisible;
    
    public StateAwareFragment() {
        this.fragmentStarted = false;
        this.fragmentVisible = false;
    }
    
    public boolean isFragmentStarted() {
        return this.fragmentStarted;
    }
    
    public boolean isFragmentVisible() {
        return this.fragmentVisible;
    }
    
    @Override
    public void onPause() {
        this.fragmentVisible = false;
        super.onPause();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        this.fragmentVisible = true;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        this.fragmentStarted = true;
    }
    
    @Override
    public void onStop() {
        this.fragmentStarted = false;
        super.onStop();
    }
}
