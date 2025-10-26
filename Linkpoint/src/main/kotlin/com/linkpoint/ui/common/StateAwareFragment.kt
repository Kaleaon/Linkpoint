package com.linkpoint.ui.common

import android.support.v4.app.Fragment

class StateAwareFragment : Fragment() {
    private Boolean fragmentStarted = false
    private Boolean fragmentVisible = false

     public fun isFragmentStarted(): Boolean {
        return this.fragmentStarted
    }

     public fun isFragmentVisible(): Boolean {
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
