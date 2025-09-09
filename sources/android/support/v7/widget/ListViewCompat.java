package android.support.v7.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.reflect.Field;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class ListViewCompat extends ListView {
    public static final int INVALID_POSITION = -1;
    public static final int NO_POSITION = -1;
    private static final int[] STATE_SET_NOTHING = {0};
    private Field mIsChildViewEnabled;
    protected int mMotionPosition;
    int mSelectionBottomPadding;
    int mSelectionLeftPadding;
    int mSelectionRightPadding;
    int mSelectionTopPadding;
    private GateKeeperDrawable mSelector;
    final Rect mSelectorRect;

    private static class GateKeeperDrawable extends DrawableWrapper {
        private boolean mEnabled = true;

        public GateKeeperDrawable(Drawable drawable) {
            super(drawable);
        }

        public void draw(Canvas canvas) {
            if (this.mEnabled) {
                super.draw(canvas);
            }
        }

        /* access modifiers changed from: package-private */
        public void setEnabled(boolean z) {
            this.mEnabled = z;
        }

        public void setHotspot(float f, float f2) {
            if (this.mEnabled) {
                super.setHotspot(f, f2);
            }
        }

        public void setHotspotBounds(int i, int i2, int i3, int i4) {
            if (this.mEnabled) {
                super.setHotspotBounds(i, i2, i3, i4);
            }
        }

        public boolean setState(int[] iArr) {
            if (!this.mEnabled) {
                return false;
            }
            return super.setState(iArr);
        }

        public boolean setVisible(boolean z, boolean z2) {
            if (!this.mEnabled) {
                return false;
            }
            return super.setVisible(z, z2);
        }
    }

    public ListViewCompat(Context context) {
        this(context, (AttributeSet) null);
    }

    public ListViewCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ListViewCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSelectorRect = new Rect();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        try {
            this.mIsChildViewEnabled = AbsListView.class.getDeclaredField("mIsChildViewEnabled");
            this.mIsChildViewEnabled.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        drawSelectorCompat(canvas);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void drawSelectorCompat(Canvas canvas) {
        Drawable selector;
        if (!this.mSelectorRect.isEmpty() && (selector = getSelector()) != null) {
            selector.setBounds(this.mSelectorRect);
            selector.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        setSelectorEnabled(true);
        updateSelectorStateCompat();
    }

    public int lookForSelectablePosition(int i, boolean z) {
        int i2;
        ListAdapter adapter = getAdapter();
        if (adapter == null || isInTouchMode()) {
            return -1;
        }
        int count = adapter.getCount();
        if (!getAdapter().areAllItemsEnabled()) {
            if (!z) {
                i2 = Math.min(i, count - 1);
                while (i2 >= 0 && !adapter.isEnabled(i2)) {
                    i2--;
                }
            } else {
                int max = Math.max(0, i);
                while (i2 < count && !adapter.isEnabled(i2)) {
                    max = i2 + 1;
                }
            }
            if (i2 >= 0 && i2 < count) {
                return i2;
            }
            return -1;
        } else if (i >= 0 && i < count) {
            return i;
        } else {
            return -1;
        }
    }

    public int measureHeightOfChildrenCompat(int i, int i2, int i3, int i4, int i5) {
        View view;
        int listPaddingTop = getListPaddingTop();
        int listPaddingBottom = getListPaddingBottom();
        getListPaddingLeft();
        getListPaddingRight();
        int dividerHeight = getDividerHeight();
        Drawable divider = getDivider();
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return listPaddingTop + listPaddingBottom;
        }
        int i6 = listPaddingTop + listPaddingBottom;
        if (dividerHeight <= 0 || divider == null) {
            dividerHeight = 0;
        }
        int i7 = 0;
        View view2 = null;
        int count = adapter.getCount();
        int i8 = 0;
        int i9 = 0;
        while (i8 < count) {
            int itemViewType = adapter.getItemViewType(i8);
            if (itemViewType == i9) {
                view = view2;
            } else {
                int i10 = itemViewType;
                view = null;
                i9 = i10;
            }
            View view3 = adapter.getView(i8, view, this);
            ViewGroup.LayoutParams layoutParams = view3.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = generateDefaultLayoutParams();
                view3.setLayoutParams(layoutParams);
            }
            view3.measure(i, layoutParams.height <= 0 ? View.MeasureSpec.makeMeasureSpec(0, 0) : View.MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824));
            view3.forceLayout();
            int measuredHeight = (i8 <= 0 ? i6 : i6 + dividerHeight) + view3.getMeasuredHeight();
            if (measuredHeight >= i4) {
                return (i5 >= 0 && i8 > i5 && i7 > 0 && measuredHeight != i4) ? i7 : i4;
            }
            if (i5 >= 0 && i8 >= i5) {
                i7 = measuredHeight;
            }
            i8++;
            i6 = measuredHeight;
            view2 = view3;
        }
        return i6;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.mMotionPosition = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void positionSelectorCompat(int i, View view) {
        boolean z = false;
        Rect rect = this.mSelectorRect;
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        rect.left -= this.mSelectionLeftPadding;
        rect.top -= this.mSelectionTopPadding;
        rect.right += this.mSelectionRightPadding;
        rect.bottom += this.mSelectionBottomPadding;
        try {
            boolean z2 = this.mIsChildViewEnabled.getBoolean(this);
            if (view.isEnabled() != z2) {
                Field field = this.mIsChildViewEnabled;
                if (!z2) {
                    z = true;
                }
                field.set(this, Boolean.valueOf(z));
                if (i != -1) {
                    refreshDrawableState();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void positionSelectorLikeFocusCompat(int i, View view) {
        boolean z = true;
        Drawable selector = getSelector();
        boolean z2 = (selector == null || i == -1) ? false : true;
        if (z2) {
            selector.setVisible(false, false);
        }
        positionSelectorCompat(i, view);
        if (z2) {
            Rect rect = this.mSelectorRect;
            float exactCenterX = rect.exactCenterX();
            float exactCenterY = rect.exactCenterY();
            if (getVisibility() != 0) {
                z = false;
            }
            selector.setVisible(z, false);
            DrawableCompat.setHotspot(selector, exactCenterX, exactCenterY);
        }
    }

    /* access modifiers changed from: protected */
    public void positionSelectorLikeTouchCompat(int i, View view, float f, float f2) {
        positionSelectorLikeFocusCompat(i, view);
        Drawable selector = getSelector();
        if (selector != null && i != -1) {
            DrawableCompat.setHotspot(selector, f, f2);
        }
    }

    public void setSelector(Drawable drawable) {
        GateKeeperDrawable gateKeeperDrawable = null;
        if (drawable != null) {
            gateKeeperDrawable = new GateKeeperDrawable(drawable);
        }
        this.mSelector = gateKeeperDrawable;
        super.setSelector(this.mSelector);
        Rect rect = new Rect();
        if (drawable != null) {
            drawable.getPadding(rect);
        }
        this.mSelectionLeftPadding = rect.left;
        this.mSelectionTopPadding = rect.top;
        this.mSelectionRightPadding = rect.right;
        this.mSelectionBottomPadding = rect.bottom;
    }

    /* access modifiers changed from: protected */
    public void setSelectorEnabled(boolean z) {
        if (this.mSelector != null) {
            this.mSelector.setEnabled(z);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowSelectorCompat() {
        return touchModeDrawsInPressedStateCompat() && isPressed();
    }

    /* access modifiers changed from: protected */
    public boolean touchModeDrawsInPressedStateCompat() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void updateSelectorStateCompat() {
        Drawable selector = getSelector();
        if (selector != null && shouldShowSelectorCompat()) {
            selector.setState(getDrawableState());
        }
    }
}
