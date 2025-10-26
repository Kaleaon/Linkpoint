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

    override fun onPause() {
        this.fragmentVisible = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        this.fragmentVisible = true
    }

    override fun onStart() {
        super.onStart()
        this.fragmentStarted = true
    }

    override fun onStop() {
        this.fragmentStarted = false
        super.onStop()
    }
}
