package com.linkpoint.ui.common

import androidx.fragment.app.Fragment

class StateAwareFragment : Fragment {
    private Boolean fragmentStarted = false
    private Boolean fragmentVisible = false

    fun isFragmentStarted(): Boolean {
        return this.fragmentStarted
    }

    fun isFragmentVisible(): Boolean {
        return this.fragmentVisible
    }

    fun onPause(): Unit {
        this.fragmentVisible = false
        super.onPause()
    }

    fun onResume(): Unit {
        super.onResume()
        this.fragmentVisible = true
    }

    fun onStart(): Unit {
        super.onStart()
        this.fragmentStarted = true
    }

    fun onStop(): Unit {
        this.fragmentStarted = false
        super.onStop()
    }
}
