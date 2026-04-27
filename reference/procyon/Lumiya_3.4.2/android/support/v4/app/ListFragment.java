// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.content.Context;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView$OnItemClickListener;
import android.widget.ListView;
import android.os.Handler;
import android.view.View;
import android.widget.ListAdapter;

public class ListFragment extends Fragment
{
    static final int INTERNAL_EMPTY_ID = 16711681;
    static final int INTERNAL_LIST_CONTAINER_ID = 16711683;
    static final int INTERNAL_PROGRESS_CONTAINER_ID = 16711682;
    ListAdapter mAdapter;
    CharSequence mEmptyText;
    View mEmptyView;
    private final Handler mHandler;
    ListView mList;
    View mListContainer;
    boolean mListShown;
    private final AdapterView$OnItemClickListener mOnClickListener;
    View mProgressContainer;
    private final Runnable mRequestFocus;
    TextView mStandardEmptyView;
    
    public ListFragment() {
        this.mHandler = new Handler();
        this.mRequestFocus = new Runnable() {
            @Override
            public void run() {
                ListFragment.this.mList.focusableViewAvailable((View)ListFragment.this.mList);
            }
        };
        this.mOnClickListener = (AdapterView$OnItemClickListener)new AdapterView$OnItemClickListener() {
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                ListFragment.this.onListItemClick((ListView)adapterView, view, n, n2);
            }
        };
    }
    
    private void ensureList() {
        if (this.mList != null) {
            return;
        }
        final View view = this.getView();
        if (view != null) {
            if (!(view instanceof ListView)) {
                this.mStandardEmptyView = (TextView)view.findViewById(16711681);
                if (this.mStandardEmptyView != null) {
                    this.mStandardEmptyView.setVisibility(8);
                }
                else {
                    this.mEmptyView = view.findViewById(16908292);
                }
                this.mProgressContainer = view.findViewById(16711682);
                this.mListContainer = view.findViewById(16711683);
                final View viewById = view.findViewById(16908298);
                if (viewById instanceof ListView) {
                    this.mList = (ListView)viewById;
                    if (this.mEmptyView == null) {
                        if (this.mEmptyText != null) {
                            this.mStandardEmptyView.setText(this.mEmptyText);
                            this.mList.setEmptyView((View)this.mStandardEmptyView);
                        }
                    }
                    else {
                        this.mList.setEmptyView(this.mEmptyView);
                    }
                }
                else {
                    if (viewById != null) {
                        throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
                    }
                    throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
                }
            }
            else {
                this.mList = (ListView)view;
            }
            this.mListShown = true;
            this.mList.setOnItemClickListener(this.mOnClickListener);
            if (this.mAdapter == null) {
                if (this.mProgressContainer != null) {
                    this.setListShown(false, false);
                }
            }
            else {
                final ListAdapter mAdapter = this.mAdapter;
                this.mAdapter = null;
                this.setListAdapter(mAdapter);
            }
            this.mHandler.post(this.mRequestFocus);
            return;
        }
        throw new IllegalStateException("Content view not yet created");
    }
    
    private void setListShown(final boolean mListShown, final boolean b) {
        this.ensureList();
        if (this.mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (this.mListShown != mListShown) {
            if (!(this.mListShown = mListShown)) {
                if (!b) {
                    this.mProgressContainer.clearAnimation();
                    this.mListContainer.clearAnimation();
                }
                else {
                    this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(this.getContext(), 17432576));
                    this.mListContainer.startAnimation(AnimationUtils.loadAnimation(this.getContext(), 17432577));
                }
                this.mProgressContainer.setVisibility(0);
                this.mListContainer.setVisibility(8);
            }
            else {
                if (!b) {
                    this.mProgressContainer.clearAnimation();
                    this.mListContainer.clearAnimation();
                }
                else {
                    this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(this.getContext(), 17432577));
                    this.mListContainer.startAnimation(AnimationUtils.loadAnimation(this.getContext(), 17432576));
                }
                this.mProgressContainer.setVisibility(8);
                this.mListContainer.setVisibility(0);
            }
        }
    }
    
    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }
    
    public ListView getListView() {
        this.ensureList();
        return this.mList;
    }
    
    public long getSelectedItemId() {
        this.ensureList();
        return this.mList.getSelectedItemId();
    }
    
    public int getSelectedItemPosition() {
        this.ensureList();
        return this.mList.getSelectedItemPosition();
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        final Context context = this.getContext();
        final FrameLayout frameLayout = new FrameLayout(context);
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(16711682);
        linearLayout.setOrientation(1);
        linearLayout.setVisibility(8);
        linearLayout.setGravity(17);
        linearLayout.addView((View)new ProgressBar(context, (AttributeSet)null, 16842874), (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2));
        frameLayout.addView((View)linearLayout, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        final FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.setId(16711683);
        final TextView textView = new TextView(context);
        textView.setId(16711681);
        textView.setGravity(17);
        frameLayout2.addView((View)textView, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        final ListView listView = new ListView(context);
        listView.setId(16908298);
        listView.setDrawSelectorOnTop(false);
        frameLayout2.addView((View)listView, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        frameLayout.addView((View)frameLayout2, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        frameLayout.setLayoutParams((ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        return (View)frameLayout;
    }
    
    @Override
    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mList = null;
        this.mListShown = false;
        this.mListContainer = null;
        this.mProgressContainer = null;
        this.mEmptyView = null;
        this.mStandardEmptyView = null;
        super.onDestroyView();
    }
    
    public void onListItemClick(final ListView listView, final View view, final int n, final long n2) {
    }
    
    @Override
    public void onViewCreated(final View view, final Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.ensureList();
    }
    
    public void setEmptyText(final CharSequence charSequence) {
        this.ensureList();
        if (this.mStandardEmptyView != null) {
            this.mStandardEmptyView.setText(charSequence);
            if (this.mEmptyText == null) {
                this.mList.setEmptyView((View)this.mStandardEmptyView);
            }
            this.mEmptyText = charSequence;
            return;
        }
        throw new IllegalStateException("Can't be used with a custom content view");
    }
    
    public void setListAdapter(final ListAdapter listAdapter) {
        boolean b = false;
        final boolean b2 = this.mAdapter != null;
        this.mAdapter = listAdapter;
        if (this.mList != null) {
            this.mList.setAdapter(listAdapter);
            if (!this.mListShown && !b2) {
                if (this.getView().getWindowToken() != null) {
                    b = true;
                }
                this.setListShown(true, b);
            }
        }
    }
    
    public void setListShown(final boolean b) {
        this.setListShown(b, true);
    }
    
    public void setListShownNoAnimation(final boolean b) {
        this.setListShown(b, false);
    }
    
    public void setSelection(final int selection) {
        this.ensureList();
        this.mList.setSelection(selection);
    }
}
