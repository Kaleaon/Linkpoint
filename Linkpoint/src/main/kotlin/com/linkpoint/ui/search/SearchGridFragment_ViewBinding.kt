package com.linkpoint.ui.search
import java.util.*

import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R

class SearchGridFragment_ViewBinding : Unbinder {
    private SearchGridFragment target
    private View view2131755639
    private View view2131755640

    @UiThread
    public SearchGridFragment_ViewBinding(final SearchGridFragment searchGridFragment, View view) {
        this.target = searchGridFragment
        val findRequiredView: View = Utils.findRequiredView(view, R.id.search_string, "field 'searchString' and method 'onSearchTextAction'")
        searchGridFragment.searchString = (EditText) Utils.castView(findRequiredView, R.id.search_string, "field 'searchString'", EditText.class)
        this.view2131755639 = findRequiredView
        ((TextView) findRequiredView).setOnEditorActionListener(TextView.OnEditorActionListener() {
             public fun onEditorAction(textView: TextView, i: Int, keyEvent: KeyEvent): Boolean {
                return searchGridFragment.onSearchTextAction(i, keyEvent)
            }
        searchGridFragment.searchResultsList = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.search_results_list, "field 'searchResultsList'", RecyclerView.class)
        searchGridFragment.radioGroupSearchType = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.radiogroup_search_type, "field 'radioGroupSearchType'", RadioGroup.class)
        val findRequiredView2: View = Utils.findRequiredView(view, R.id.start_search_button, "method 'onSearchButtonClicked'")
        this.view2131755640 = findRequiredView2
        findRequiredView2.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(view: View) {
                searchGridFragment.onSearchButtonClicked()
            }
    }

    @CallSuper
    fun unbind() {
        val searchGridFragment: SearchGridFragment = this.target
        if (searchGridFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        searchGridFragment.searchString = null
        searchGridFragment.searchResultsList = null
        searchGridFragment.radioGroupSearchType = null
        ((TextView) this.view2131755639).setOnEditorActionListener((TextView.OnEditorActionListener) null)
        this.view2131755639 = null
        this.view2131755640.setOnClickListener((View.OnClickListener) null)
        this.view2131755640 = null
    }
}
