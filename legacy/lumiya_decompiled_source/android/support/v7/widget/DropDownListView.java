package android.support.v7.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.appcompat.R;
import android.view.View;
/* loaded from: classes.dex */
class DropDownListView extends ListViewCompat {
    private ViewPropertyAnimatorCompat mClickAnimation;
    private boolean mDrawsInPressedState;
    private boolean mHijackFocus;
    private boolean mListSelectionHidden;
    private ListViewAutoScrollHelper mScrollHelper;

    public DropDownListView(Context context, boolean z) {
        super(context, null, R.attr.dropDownListViewStyle);
        this.mHijackFocus = z;
        setCacheColorHint(0);
    }

    private void clearPressedItem() {
        this.mDrawsInPressedState = false;
        setPressed(false);
        drawableStateChanged();
        View childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
        if (childAt != null) {
            childAt.setPressed(false);
        }
        if (this.mClickAnimation == null) {
            return;
        }
        this.mClickAnimation.cancel();
        this.mClickAnimation = null;
    }

    private void clickPressedItem(View view, int i) {
        performItemClick(view, i, getItemIdAtPosition(i));
    }

    private void setPressedItem(View view, int i, float f, float f2) {
        View childAt;
        this.mDrawsInPressedState = true;
        if (Build.VERSION.SDK_INT >= 21) {
            drawableHotspotChanged(f, f2);
        }
        if (!isPressed()) {
            setPressed(true);
        }
        layoutChildren();
        if (this.mMotionPosition != -1 && (childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition())) != null && childAt != view && childAt.isPressed()) {
            childAt.setPressed(false);
        }
        this.mMotionPosition = i;
        float left = f - view.getLeft();
        float top = f2 - view.getTop();
        if (Build.VERSION.SDK_INT >= 21) {
            view.drawableHotspotChanged(left, top);
        }
        if (!view.isPressed()) {
            view.setPressed(true);
        }
        positionSelectorLikeTouchCompat(i, view, f, f2);
        setSelectorEnabled(false);
        refreshDrawableState();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean hasFocus() {
        return this.mHijackFocus || super.hasFocus();
    }

    @Override // android.view.View
    public boolean hasWindowFocus() {
        return this.mHijackFocus || super.hasWindowFocus();
    }

    @Override // android.view.View
    public boolean isFocused() {
        return this.mHijackFocus || super.isFocused();
    }

    @Override // android.view.View
    public boolean isInTouchMode() {
        return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x001e  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0048  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onForwardedEvent(android.view.MotionEvent r9, int r10) {
        /*
            r8 = this;
            r2 = 1
            r1 = 0
            int r3 = r9.getActionMasked()
            switch(r3) {
                case 1: goto L46;
                case 2: goto L17;
                case 3: goto L43;
                default: goto L9;
            }
        L9:
            r0 = r1
            r3 = r2
        Lb:
            if (r3 != 0) goto L54
        Ld:
            r8.clearPressedItem()
        L10:
            if (r3 != 0) goto L57
            android.support.v4.widget.ListViewAutoScrollHelper r0 = r8.mScrollHelper
            if (r0 != 0) goto L6e
        L16:
            return r3
        L17:
            r0 = r2
        L18:
            int r4 = r9.findPointerIndex(r10)
            if (r4 < 0) goto L48
            float r5 = r9.getX(r4)
            int r5 = (int) r5
            float r4 = r9.getY(r4)
            int r4 = (int) r4
            int r6 = r8.pointToPosition(r5, r4)
            r7 = -1
            if (r6 == r7) goto L4b
            int r0 = r8.getFirstVisiblePosition()
            int r0 = r6 - r0
            android.view.View r0 = r8.getChildAt(r0)
            float r5 = (float) r5
            float r4 = (float) r4
            r8.setPressedItem(r0, r6, r5, r4)
            if (r3 == r2) goto L4e
            r0 = r1
            r3 = r2
            goto Lb
        L43:
            r0 = r1
            r3 = r1
            goto Lb
        L46:
            r0 = r1
            goto L18
        L48:
            r0 = r1
            r3 = r1
            goto Lb
        L4b:
            r3 = r0
            r0 = r2
            goto Lb
        L4e:
            r8.clickPressedItem(r0, r6)
            r0 = r1
            r3 = r2
            goto Lb
        L54:
            if (r0 != 0) goto Ld
            goto L10
        L57:
            android.support.v4.widget.ListViewAutoScrollHelper r0 = r8.mScrollHelper
            if (r0 == 0) goto L66
        L5b:
            android.support.v4.widget.ListViewAutoScrollHelper r0 = r8.mScrollHelper
            r0.setEnabled(r2)
            android.support.v4.widget.ListViewAutoScrollHelper r0 = r8.mScrollHelper
            r0.onTouch(r8, r9)
            goto L16
        L66:
            android.support.v4.widget.ListViewAutoScrollHelper r0 = new android.support.v4.widget.ListViewAutoScrollHelper
            r0.<init>(r8)
            r8.mScrollHelper = r0
            goto L5b
        L6e:
            android.support.v4.widget.ListViewAutoScrollHelper r0 = r8.mScrollHelper
            r0.setEnabled(r1)
            goto L16
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.DropDownListView.onForwardedEvent(android.view.MotionEvent, int):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setListSelectionHidden(boolean z) {
        this.mListSelectionHidden = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.ListViewCompat
    public boolean touchModeDrawsInPressedStateCompat() {
        return this.mDrawsInPressedState || super.touchModeDrawsInPressedStateCompat();
    }
}
