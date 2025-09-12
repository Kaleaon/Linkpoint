// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.search:
//            SearchGridFragment

public class SearchGridFragment_ViewBinding
    implements Unbinder
{

    private SearchGridFragment target;
    private View view2131755639;
    private View view2131755640;

    public SearchGridFragment_ViewBinding(final SearchGridFragment target, View view)
    {
        this.target = target;
        View view1 = Utils.findRequiredView(view, 0x7f100277, "field 'searchString' and method 'onSearchTextAction'");
        target.searchString = (EditText)Utils.castView(view1, 0x7f100277, "field 'searchString'", android/widget/EditText);
        view2131755639 = view1;
        ((TextView)view1).setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {

            final SearchGridFragment_ViewBinding this$0;
            final SearchGridFragment val$target;

            public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent)
            {
                return target.onSearchTextAction(i, keyevent);
            }

            
            {
                this$0 = SearchGridFragment_ViewBinding.this;
                target = searchgridfragment;
                super();
            }
        });
        target.searchResultsList = (RecyclerView)Utils.findRequiredViewAsType(view, 0x7f10027d, "field 'searchResultsList'", android/support/v7/widget/RecyclerView);
        target.radioGroupSearchType = (RadioGroup)Utils.findRequiredViewAsType(view, 0x7f100279, "field 'radioGroupSearchType'", android/widget/RadioGroup);
        view = Utils.findRequiredView(view, 0x7f100278, "method 'onSearchButtonClicked'");
        view2131755640 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final SearchGridFragment_ViewBinding this$0;
            final SearchGridFragment val$target;

            public void doClick(View view2)
            {
                target.onSearchButtonClicked();
            }

            
            {
                this$0 = SearchGridFragment_ViewBinding.this;
                target = searchgridfragment;
                super();
            }
        });
    }

    public void unbind()
    {
        SearchGridFragment searchgridfragment = target;
        if (searchgridfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            searchgridfragment.searchString = null;
            searchgridfragment.searchResultsList = null;
            searchgridfragment.radioGroupSearchType = null;
            ((TextView)view2131755639).setOnEditorActionListener(null);
            view2131755639 = null;
            view2131755640.setOnClickListener(null);
            view2131755640 = null;
            return;
        }
    }
}
