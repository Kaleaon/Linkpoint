package com.linkpoint.ui.common

import android.support.v4.app.Fragment

class StateAwareFragment : Fragment() {
    private Boolean fragmentStarted = false
    private Boolean fragmentVisible = false

    public Boolean isFragmentStarted() {
        return this.fragmentStarted
    }

    public Boolean isFragmentVisible() {
        return this.fragmentVisible
    }

    public Unit onPause() {
        this.fragmentVisible = false
        super.onPause()
    }

    public Unit onResume() {
        super.onResume()
        this.fragmentVisible = true
    }

    public Unit onStart() {
        super.onStart()
        this.fragmentStarted = true
    }

    public Unit onStop() {
        this.fragmentStarted = false
        super.onStop()
    }
}
