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

    fun onPause() {
        this.fragmentVisible = false
        super.onPause()
    }

    fun onResume() {
        super.onResume()
        this.fragmentVisible = true
    }

    fun onStart() {
        super.onStart()
        this.fragmentStarted = true
    }

    fun onStop() {
        this.fragmentStarted = false
        super.onStop()
    }
}
