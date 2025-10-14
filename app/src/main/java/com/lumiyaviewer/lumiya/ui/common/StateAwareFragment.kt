package com.lumiyaviewer.lumiya.ui.common

import android.support.v4.app.Fragment

class StateAwareFragment : Fragment {
    private Boolean fragmentStarted = false
    private Boolean fragmentVisible = false

    Boolean isFragmentStarted() {
        return this.fragmentStarted
    }

    Boolean isFragmentVisible() {
        return this.fragmentVisible
    }

    Unit onPause() {
        this.fragmentVisible = false
        super.onPause()
    }

    Unit onResume() {
        super.onResume()
        this.fragmentVisible = true
    }

    Unit onStart() {
        super.onStart()
        this.fragmentStarted = true
    }

    Unit onStop() {
        this.fragmentStarted = false
        super.onStop()
    }
}
