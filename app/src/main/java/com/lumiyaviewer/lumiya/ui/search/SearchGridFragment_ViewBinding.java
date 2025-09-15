package com.lumiyaviewer.lumiya.ui.search;
import java.util.*;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

public class SearchGridFragment_ViewBinding implements Unbinder {
    private SearchGridFragment target;
    private View view2131755639;
    private View view2131755640;

    @UiThread
    public SearchGridFragment_ViewBinding(final SearchGridFragment searchGridFragment, View view) {
        this.target = searchGridFragment;
        View findRequiredView = Utils.findRequiredView(view, R.id.search_string, "field 'searchString' and method 'onSearchTextAction'");
        searchGridFragment.searchString = (EditText) Utils.castView(findRequiredView, R.id.search_string, "field 'searchString'", EditText.class);
        this.view2131755639 = findRequiredView;
        ((TextView) findRequiredView).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return searchGridFragment.onSearchTextAction(i, keyEvent);
            }
        searchGridFragment.searchResultsList = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.search_results_list, "field 'searchResultsList'", RecyclerView.class);
        searchGridFragment.radioGroupSearchType = (RadioGroup) Utils.findRequiredViewAsType(view, R.id.radiogroup_search_type, "field 'radioGroupSearchType'", RadioGroup.class);
        View findRequiredView2 = Utils.findRequiredView(view, R.id.start_search_button, "method 'onSearchButtonClicked'");
        this.view2131755640 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                searchGridFragment.onSearchButtonClicked();
            }
    }

    @CallSuper
    public void unbind() {
        SearchGridFragment searchGridFragment = this.target;
        if (searchGridFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        searchGridFragment.searchString = null;
        searchGridFragment.searchResultsList = null;
        searchGridFragment.radioGroupSearchType = null;
        ((TextView) this.view2131755639).setOnEditorActionListener((TextView.OnEditorActionListener) null);
        this.view2131755639 = null;
        this.view2131755640.setOnClickListener((View.OnClickListener) null);
        this.view2131755640 = null;
    }
}
