// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.widget.TextView;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.widget.Button;
import android.text.TextUtils;
import android.view.View$OnClickListener;
import android.support.design.R;
import android.view.LayoutInflater;
import android.support.design.internal.SnackbarContentLayout;
import android.support.annotation.StringRes;
import android.support.annotation.NonNull;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;

public final class Snackbar extends BaseTransientBottomBar<Snackbar>
{
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    @Nullable
    private BaseCallback<Snackbar> mCallback;
    
    private Snackbar(final ViewGroup viewGroup, final View view, final ContentViewCallback contentViewCallback) {
        super(viewGroup, view, contentViewCallback);
    }
    
    private static ViewGroup findSuitableParent(View view) {
        ViewGroup viewGroup = null;
        Object o = view;
        while (!(o instanceof CoordinatorLayout)) {
            ViewGroup viewGroup2;
            if (!(o instanceof FrameLayout)) {
                viewGroup2 = viewGroup;
            }
            else {
                if (((View)o).getId() == 16908290) {
                    return (ViewGroup)o;
                }
                viewGroup2 = (ViewGroup)o;
            }
            if (o == null) {
                view = (View)o;
            }
            else {
                final ViewParent parent = ((View)o).getParent();
                if (!(parent instanceof View)) {
                    view = null;
                }
                else {
                    view = (View)parent;
                }
            }
            o = view;
            viewGroup = viewGroup2;
            if (view == null) {
                return viewGroup2;
            }
        }
        return (ViewGroup)o;
    }
    
    @NonNull
    public static Snackbar make(@NonNull final View view, @StringRes final int n, final int n2) {
        return make(view, view.getResources().getText(n), n2);
    }
    
    @NonNull
    public static Snackbar make(@NonNull final View view, @NonNull final CharSequence text, final int duration) {
        final ViewGroup suitableParent = findSuitableParent(view);
        if (suitableParent != null) {
            final SnackbarContentLayout snackbarContentLayout = (SnackbarContentLayout)LayoutInflater.from(suitableParent.getContext()).inflate(R.layout.design_layout_snackbar_include, suitableParent, false);
            final Snackbar snackbar = new Snackbar(suitableParent, (View)snackbarContentLayout, snackbarContentLayout);
            snackbar.setText(text);
            snackbar.setDuration(duration);
            return snackbar;
        }
        throw new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.");
    }
    
    @NonNull
    public Snackbar setAction(@StringRes final int n, final View$OnClickListener view$OnClickListener) {
        return this.setAction(this.getContext().getText(n), view$OnClickListener);
    }
    
    @NonNull
    public Snackbar setAction(final CharSequence text, final View$OnClickListener view$OnClickListener) {
        final Button actionView = ((SnackbarContentLayout)this.mView.getChildAt(0)).getActionView();
        if (!TextUtils.isEmpty(text) && view$OnClickListener != null) {
            ((TextView)actionView).setVisibility(0);
            ((TextView)actionView).setText(text);
            ((TextView)actionView).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    view$OnClickListener.onClick(view);
                    Snackbar.this.dispatchDismiss(1);
                }
            });
        }
        else {
            ((TextView)actionView).setVisibility(8);
            ((TextView)actionView).setOnClickListener((View$OnClickListener)null);
        }
        return this;
    }
    
    @NonNull
    public Snackbar setActionTextColor(@ColorInt final int textColor) {
        ((TextView)((SnackbarContentLayout)this.mView.getChildAt(0)).getActionView()).setTextColor(textColor);
        return this;
    }
    
    @NonNull
    public Snackbar setActionTextColor(final ColorStateList textColor) {
        ((TextView)((SnackbarContentLayout)this.mView.getChildAt(0)).getActionView()).setTextColor(textColor);
        return this;
    }
    
    @Deprecated
    @NonNull
    public Snackbar setCallback(final Callback mCallback) {
        if (this.mCallback != null) {
            this.removeCallback(this.mCallback);
        }
        if (mCallback != null) {
            this.addCallback((BaseCallback<Snackbar>)mCallback);
        }
        this.mCallback = mCallback;
        return this;
    }
    
    @NonNull
    public Snackbar setText(@StringRes final int n) {
        return this.setText(this.getContext().getText(n));
    }
    
    @NonNull
    public Snackbar setText(@NonNull final CharSequence text) {
        ((SnackbarContentLayout)this.mView.getChildAt(0)).getMessageView().setText(text);
        return this;
    }
    
    public static class Callback extends BaseCallback<Snackbar>
    {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        
        public void onDismissed(final Snackbar snackbar, final int n) {
        }
        
        public void onShown(final Snackbar snackbar) {
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static final class SnackbarLayout extends SnackbarBaseLayout
    {
        public SnackbarLayout(final Context context) {
            super(context);
        }
        
        public SnackbarLayout(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        protected void onMeasure(int i, int paddingRight) {
            super.onMeasure(i, paddingRight);
            final int childCount = this.getChildCount();
            final int measuredWidth = this.getMeasuredWidth();
            final int paddingLeft = this.getPaddingLeft();
            paddingRight = this.getPaddingRight();
            View child;
            for (i = 0; i < childCount; ++i) {
                child = this.getChildAt(i);
                if (child.getLayoutParams().width == -1) {
                    child.measure(View$MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, 1073741824), View$MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), 1073741824));
                }
            }
        }
    }
}
