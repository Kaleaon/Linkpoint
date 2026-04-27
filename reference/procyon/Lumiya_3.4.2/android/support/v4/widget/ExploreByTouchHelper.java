// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.support.v4.view.ViewParentCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.support.annotation.Nullable;
import android.view.ViewParent;
import java.util.List;
import java.util.ArrayList;
import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityRecord;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.view.accessibility.AccessibilityEvent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.accessibility.AccessibilityManager;
import android.view.View;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.graphics.Rect;
import android.support.v4.view.AccessibilityDelegateCompat;

public abstract class ExploreByTouchHelper extends AccessibilityDelegateCompat
{
    private static final String DEFAULT_CLASS_NAME = "android.view.View";
    public static final int HOST_ID = -1;
    public static final int INVALID_ID = Integer.MIN_VALUE;
    private static final Rect INVALID_PARENT_BOUNDS;
    private static final FocusStrategy.BoundsAdapter<AccessibilityNodeInfoCompat> NODE_ADAPTER;
    private static final FocusStrategy.CollectionAdapter<SparseArrayCompat<AccessibilityNodeInfoCompat>, AccessibilityNodeInfoCompat> SPARSE_VALUES_ADAPTER;
    private int mAccessibilityFocusedVirtualViewId;
    private final View mHost;
    private int mHoveredVirtualViewId;
    private int mKeyboardFocusedVirtualViewId;
    private final AccessibilityManager mManager;
    private MyNodeProvider mNodeProvider;
    private final int[] mTempGlobalRect;
    private final Rect mTempParentRect;
    private final Rect mTempScreenRect;
    private final Rect mTempVisibleRect;
    
    static {
        INVALID_PARENT_BOUNDS = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        NODE_ADAPTER = new FocusStrategy.BoundsAdapter<AccessibilityNodeInfoCompat>() {
            public void obtainBounds(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final Rect rect) {
                accessibilityNodeInfoCompat.getBoundsInParent(rect);
            }
        };
        SPARSE_VALUES_ADAPTER = new FocusStrategy.CollectionAdapter<SparseArrayCompat<AccessibilityNodeInfoCompat>, AccessibilityNodeInfoCompat>() {
            public AccessibilityNodeInfoCompat get(final SparseArrayCompat<AccessibilityNodeInfoCompat> sparseArrayCompat, final int n) {
                return sparseArrayCompat.valueAt(n);
            }
            
            public int size(final SparseArrayCompat<AccessibilityNodeInfoCompat> sparseArrayCompat) {
                return sparseArrayCompat.size();
            }
        };
    }
    
    public ExploreByTouchHelper(final View mHost) {
        this.mTempScreenRect = new Rect();
        this.mTempParentRect = new Rect();
        this.mTempVisibleRect = new Rect();
        this.mTempGlobalRect = new int[2];
        this.mAccessibilityFocusedVirtualViewId = Integer.MIN_VALUE;
        this.mKeyboardFocusedVirtualViewId = Integer.MIN_VALUE;
        this.mHoveredVirtualViewId = Integer.MIN_VALUE;
        if (mHost != null) {
            this.mHost = mHost;
            this.mManager = (AccessibilityManager)mHost.getContext().getSystemService("accessibility");
            mHost.setFocusable(true);
            if (ViewCompat.getImportantForAccessibility(mHost) == 0) {
                ViewCompat.setImportantForAccessibility(mHost, 1);
            }
            return;
        }
        throw new IllegalArgumentException("View may not be null");
    }
    
    private boolean clearAccessibilityFocus(final int n) {
        if (this.mAccessibilityFocusedVirtualViewId != n) {
            return false;
        }
        this.mAccessibilityFocusedVirtualViewId = Integer.MIN_VALUE;
        this.mHost.invalidate();
        this.sendEventForVirtualView(n, 65536);
        return true;
    }
    
    private boolean clickKeyboardFocusedVirtualView() {
        boolean b = false;
        if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE && this.onPerformActionForVirtualView(this.mKeyboardFocusedVirtualViewId, 16, null)) {
            b = true;
        }
        return b;
    }
    
    private AccessibilityEvent createEvent(final int n, final int n2) {
        switch (n) {
            default: {
                return this.createEventForChild(n, n2);
            }
            case -1: {
                return this.createEventForHost(n2);
            }
        }
    }
    
    private AccessibilityEvent createEventForChild(final int n, final int n2) {
        final AccessibilityEvent obtain = AccessibilityEvent.obtain(n2);
        final AccessibilityNodeInfoCompat obtainAccessibilityNodeInfo = this.obtainAccessibilityNodeInfo(n);
        obtain.getText().add(obtainAccessibilityNodeInfo.getText());
        obtain.setContentDescription(obtainAccessibilityNodeInfo.getContentDescription());
        obtain.setScrollable(obtainAccessibilityNodeInfo.isScrollable());
        obtain.setPassword(obtainAccessibilityNodeInfo.isPassword());
        obtain.setEnabled(obtainAccessibilityNodeInfo.isEnabled());
        obtain.setChecked(obtainAccessibilityNodeInfo.isChecked());
        this.onPopulateEventForVirtualView(n, obtain);
        if (obtain.getText().isEmpty() && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
        }
        obtain.setClassName(obtainAccessibilityNodeInfo.getClassName());
        AccessibilityRecordCompat.setSource((AccessibilityRecord)obtain, this.mHost, n);
        obtain.setPackageName((CharSequence)this.mHost.getContext().getPackageName());
        return obtain;
    }
    
    private AccessibilityEvent createEventForHost(final int n) {
        final AccessibilityEvent obtain = AccessibilityEvent.obtain(n);
        this.mHost.onInitializeAccessibilityEvent(obtain);
        return obtain;
    }
    
    @NonNull
    private AccessibilityNodeInfoCompat createNodeForChild(int i) {
        final AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain();
        obtain.setEnabled(true);
        obtain.setFocusable(true);
        obtain.setClassName("android.view.View");
        obtain.setBoundsInParent(ExploreByTouchHelper.INVALID_PARENT_BOUNDS);
        obtain.setBoundsInScreen(ExploreByTouchHelper.INVALID_PARENT_BOUNDS);
        obtain.setParent(this.mHost);
        this.onPopulateNodeForVirtualView(i, obtain);
        if (obtain.getText() == null && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
        }
        obtain.getBoundsInParent(this.mTempParentRect);
        if (this.mTempParentRect.equals((Object)ExploreByTouchHelper.INVALID_PARENT_BOUNDS)) {
            throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
        }
        final int actions = obtain.getActions();
        if ((actions & 0x40) != 0x0) {
            throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
        }
        if ((actions & 0x80) == 0x0) {
            obtain.setPackageName(this.mHost.getContext().getPackageName());
            obtain.setSource(this.mHost, i);
            if (this.mAccessibilityFocusedVirtualViewId != i) {
                obtain.setAccessibilityFocused(false);
                obtain.addAction(64);
            }
            else {
                obtain.setAccessibilityFocused(true);
                obtain.addAction(128);
            }
            final boolean focused = this.mKeyboardFocusedVirtualViewId == i;
            if (!focused) {
                if (obtain.isFocusable()) {
                    obtain.addAction(1);
                }
            }
            else {
                obtain.addAction(2);
            }
            obtain.setFocused(focused);
            this.mHost.getLocationOnScreen(this.mTempGlobalRect);
            obtain.getBoundsInScreen(this.mTempScreenRect);
            if (this.mTempScreenRect.equals((Object)ExploreByTouchHelper.INVALID_PARENT_BOUNDS)) {
                obtain.getBoundsInParent(this.mTempScreenRect);
                if (obtain.mParentVirtualDescendantId != -1) {
                    AccessibilityNodeInfoCompat obtain2;
                    for (obtain2 = AccessibilityNodeInfoCompat.obtain(), i = obtain.mParentVirtualDescendantId; i != -1; i = obtain2.mParentVirtualDescendantId) {
                        obtain2.setParent(this.mHost, -1);
                        obtain2.setBoundsInParent(ExploreByTouchHelper.INVALID_PARENT_BOUNDS);
                        this.onPopulateNodeForVirtualView(i, obtain2);
                        obtain2.getBoundsInParent(this.mTempParentRect);
                        this.mTempScreenRect.offset(this.mTempParentRect.left, this.mTempParentRect.top);
                    }
                    obtain2.recycle();
                }
                this.mTempScreenRect.offset(this.mTempGlobalRect[0] - this.mHost.getScrollX(), this.mTempGlobalRect[1] - this.mHost.getScrollY());
            }
            if (this.mHost.getLocalVisibleRect(this.mTempVisibleRect)) {
                this.mTempVisibleRect.offset(this.mTempGlobalRect[0] - this.mHost.getScrollX(), this.mTempGlobalRect[1] - this.mHost.getScrollY());
                if (this.mTempScreenRect.intersect(this.mTempVisibleRect)) {
                    obtain.setBoundsInScreen(this.mTempScreenRect);
                    if (this.isVisibleToUser(this.mTempScreenRect)) {
                        obtain.setVisibleToUser(true);
                    }
                }
            }
            return obtain;
        }
        throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }
    
    @NonNull
    private AccessibilityNodeInfoCompat createNodeForHost() {
        final AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(this.mHost);
        ViewCompat.onInitializeAccessibilityNodeInfo(this.mHost, obtain);
        final ArrayList list = new ArrayList();
        this.getVisibleVirtualViews(list);
        if (obtain.getChildCount() > 0 && list.size() > 0) {
            throw new RuntimeException("Views cannot have both real and virtual children");
        }
        for (int size = list.size(), i = 0; i < size; ++i) {
            obtain.addChild(this.mHost, (int)list.get(i));
        }
        return obtain;
    }
    
    private SparseArrayCompat<AccessibilityNodeInfoCompat> getAllNodes() {
        final ArrayList list = new ArrayList();
        this.getVisibleVirtualViews(list);
        final SparseArrayCompat sparseArrayCompat = new SparseArrayCompat();
        for (int i = 0; i < list.size(); ++i) {
            sparseArrayCompat.put(i, this.createNodeForChild(i));
        }
        return sparseArrayCompat;
    }
    
    private void getBoundsInParent(final int n, final Rect rect) {
        this.obtainAccessibilityNodeInfo(n).getBoundsInParent(rect);
    }
    
    private static Rect guessPreviouslyFocusedRect(@NonNull final View view, final int n, @NonNull final Rect rect) {
        final int width = view.getWidth();
        final int height = view.getHeight();
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17: {
                rect.set(width, 0, width, height);
                break;
            }
            case 33: {
                rect.set(0, height, width, height);
                break;
            }
            case 66: {
                rect.set(-1, 0, -1, height);
                break;
            }
            case 130: {
                rect.set(0, -1, width, -1);
                break;
            }
        }
        return rect;
    }
    
    private boolean isVisibleToUser(final Rect rect) {
        if (rect == null || rect.isEmpty()) {
            return false;
        }
        if (this.mHost.getWindowVisibility() == 0) {
            ViewParent viewParent;
            View view;
            for (viewParent = this.mHost.getParent(); viewParent instanceof View; viewParent = view.getParent()) {
                view = (View)viewParent;
                boolean b;
                if (view.getAlpha() <= 0.0f) {
                    b = true;
                }
                else {
                    b = false;
                }
                if (b || view.getVisibility() != 0) {
                    return false;
                }
            }
            return viewParent != null;
        }
        return false;
    }
    
    private static int keyToDirection(final int n) {
        switch (n) {
            default: {
                return 130;
            }
            case 21: {
                return 17;
            }
            case 19: {
                return 33;
            }
            case 22: {
                return 66;
            }
        }
    }
    
    private boolean moveFocus(int key, @Nullable final Rect rect) {
        boolean b = true;
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompat = null;
        final SparseArrayCompat<AccessibilityNodeInfoCompat> allNodes = this.getAllNodes();
        final int mKeyboardFocusedVirtualViewId = this.mKeyboardFocusedVirtualViewId;
        if (mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
            accessibilityNodeInfoCompat = (AccessibilityNodeInfoCompat)allNodes.get(mKeyboardFocusedVirtualViewId);
        }
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompat2 = null;
        switch (key) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD, FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 1:
            case 2: {
                if (ViewCompat.getLayoutDirection(this.mHost) != 1) {
                    b = false;
                }
                accessibilityNodeInfoCompat2 = FocusStrategy.findNextFocusInRelativeDirection((SparseArrayCompat)allNodes, (FocusStrategy.CollectionAdapter<SparseArrayCompat, AccessibilityNodeInfoCompat>)ExploreByTouchHelper.SPARSE_VALUES_ADAPTER, ExploreByTouchHelper.NODE_ADAPTER, accessibilityNodeInfoCompat, key, b, false);
                break;
            }
            case 17:
            case 33:
            case 66:
            case 130: {
                final Rect rect2 = new Rect();
                if (this.mKeyboardFocusedVirtualViewId == Integer.MIN_VALUE) {
                    if (rect == null) {
                        guessPreviouslyFocusedRect(this.mHost, key, rect2);
                    }
                    else {
                        rect2.set(rect);
                    }
                }
                else {
                    this.getBoundsInParent(this.mKeyboardFocusedVirtualViewId, rect2);
                }
                accessibilityNodeInfoCompat2 = FocusStrategy.findNextFocusInAbsoluteDirection((SparseArrayCompat)allNodes, (FocusStrategy.CollectionAdapter<SparseArrayCompat, AccessibilityNodeInfoCompat>)ExploreByTouchHelper.SPARSE_VALUES_ADAPTER, ExploreByTouchHelper.NODE_ADAPTER, accessibilityNodeInfoCompat, rect2, key);
                break;
            }
        }
        if (accessibilityNodeInfoCompat2 != null) {
            key = allNodes.keyAt(allNodes.indexOfValue(accessibilityNodeInfoCompat2));
        }
        else {
            key = Integer.MIN_VALUE;
        }
        return this.requestKeyboardFocusForVirtualView(key);
    }
    
    private boolean performActionForChild(final int n, final int n2, final Bundle bundle) {
        switch (n2) {
            default: {
                return this.onPerformActionForVirtualView(n, n2, bundle);
            }
            case 64: {
                return this.requestAccessibilityFocus(n);
            }
            case 128: {
                return this.clearAccessibilityFocus(n);
            }
            case 1: {
                return this.requestKeyboardFocusForVirtualView(n);
            }
            case 2: {
                return this.clearKeyboardFocusForVirtualView(n);
            }
        }
    }
    
    private boolean performActionForHost(final int n, final Bundle bundle) {
        return ViewCompat.performAccessibilityAction(this.mHost, n, bundle);
    }
    
    private boolean requestAccessibilityFocus(final int mAccessibilityFocusedVirtualViewId) {
        if (!this.mManager.isEnabled() || !this.mManager.isTouchExplorationEnabled()) {
            return false;
        }
        if (this.mAccessibilityFocusedVirtualViewId == mAccessibilityFocusedVirtualViewId) {
            return false;
        }
        if (this.mAccessibilityFocusedVirtualViewId != Integer.MIN_VALUE) {
            this.clearAccessibilityFocus(this.mAccessibilityFocusedVirtualViewId);
        }
        this.mAccessibilityFocusedVirtualViewId = mAccessibilityFocusedVirtualViewId;
        this.mHost.invalidate();
        this.sendEventForVirtualView(mAccessibilityFocusedVirtualViewId, 32768);
        return true;
    }
    
    private void updateHoveredVirtualView(final int mHoveredVirtualViewId) {
        if (this.mHoveredVirtualViewId != mHoveredVirtualViewId) {
            final int mHoveredVirtualViewId2 = this.mHoveredVirtualViewId;
            this.sendEventForVirtualView(this.mHoveredVirtualViewId = mHoveredVirtualViewId, 128);
            this.sendEventForVirtualView(mHoveredVirtualViewId2, 256);
        }
    }
    
    public final boolean clearKeyboardFocusForVirtualView(final int n) {
        if (this.mKeyboardFocusedVirtualViewId == n) {
            this.mKeyboardFocusedVirtualViewId = Integer.MIN_VALUE;
            this.onVirtualViewKeyboardFocusChanged(n, false);
            this.sendEventForVirtualView(n, 8);
            return true;
        }
        return false;
    }
    
    public final boolean dispatchHoverEvent(@NonNull final MotionEvent motionEvent) {
        boolean b = false;
        if (!this.mManager.isEnabled() || !this.mManager.isTouchExplorationEnabled()) {
            return false;
        }
        switch (motionEvent.getAction()) {
            default: {
                return false;
            }
            case 7:
            case 9: {
                final int virtualView = this.getVirtualViewAt(motionEvent.getX(), motionEvent.getY());
                this.updateHoveredVirtualView(virtualView);
                if (virtualView != Integer.MIN_VALUE) {
                    b = true;
                }
                return b;
            }
            case 10: {
                if (this.mAccessibilityFocusedVirtualViewId == Integer.MIN_VALUE) {
                    return false;
                }
                this.updateHoveredVirtualView(Integer.MIN_VALUE);
                return true;
            }
        }
    }
    
    public final boolean dispatchKeyEvent(@NonNull final KeyEvent keyEvent) {
        final boolean b = false;
        final boolean b2 = false;
        boolean b3 = false;
        Label_0015: {
            if (keyEvent.getAction() == 1) {
                b3 = b2;
            }
            else {
                final int keyCode = keyEvent.getKeyCode();
                switch (keyCode) {
                    default: {
                        b3 = b2;
                        break;
                    }
                    case 19:
                    case 20:
                    case 21:
                    case 22: {
                        b3 = b2;
                        if (!keyEvent.hasNoModifiers()) {
                            break;
                        }
                        final int keyToDirection = keyToDirection(keyCode);
                        final int repeatCount = keyEvent.getRepeatCount();
                        int n = 0;
                        boolean b4 = b;
                        while (true) {
                            b3 = b4;
                            if (n >= repeatCount + 1) {
                                break Label_0015;
                            }
                            b3 = b4;
                            if (!this.moveFocus(keyToDirection, null)) {
                                break Label_0015;
                            }
                            ++n;
                            b4 = true;
                        }
                        break;
                    }
                    case 23:
                    case 66: {
                        b3 = b2;
                        if (!keyEvent.hasNoModifiers()) {
                            break;
                        }
                        b3 = b2;
                        if (keyEvent.getRepeatCount() == 0) {
                            this.clickKeyboardFocusedVirtualView();
                            b3 = true;
                            break;
                        }
                        break;
                    }
                    case 61: {
                        if (keyEvent.hasNoModifiers()) {
                            b3 = this.moveFocus(2, null);
                            break;
                        }
                        b3 = b2;
                        if (keyEvent.hasModifiers(1)) {
                            b3 = this.moveFocus(1, null);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        return b3;
    }
    
    public final int getAccessibilityFocusedVirtualViewId() {
        return this.mAccessibilityFocusedVirtualViewId;
    }
    
    @Override
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(final View view) {
        if (this.mNodeProvider == null) {
            this.mNodeProvider = new MyNodeProvider();
        }
        return this.mNodeProvider;
    }
    
    @Deprecated
    public int getFocusedVirtualView() {
        return this.getAccessibilityFocusedVirtualViewId();
    }
    
    public final int getKeyboardFocusedVirtualViewId() {
        return this.mKeyboardFocusedVirtualViewId;
    }
    
    protected abstract int getVirtualViewAt(final float p0, final float p1);
    
    protected abstract void getVisibleVirtualViews(final List<Integer> p0);
    
    public final void invalidateRoot() {
        this.invalidateVirtualView(-1, 1);
    }
    
    public final void invalidateVirtualView(final int n) {
        this.invalidateVirtualView(n, 0);
    }
    
    public final void invalidateVirtualView(final int n, final int n2) {
        if (n != Integer.MIN_VALUE && this.mManager.isEnabled()) {
            final ViewParent parent = this.mHost.getParent();
            if (parent != null) {
                final AccessibilityEvent event = this.createEvent(n, 2048);
                AccessibilityEventCompat.setContentChangeTypes(event, n2);
                ViewParentCompat.requestSendAccessibilityEvent(parent, this.mHost, event);
            }
        }
    }
    
    @NonNull
    AccessibilityNodeInfoCompat obtainAccessibilityNodeInfo(final int n) {
        if (n != -1) {
            return this.createNodeForChild(n);
        }
        return this.createNodeForHost();
    }
    
    public final void onFocusChanged(final boolean b, final int n, @Nullable final Rect rect) {
        if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
            this.clearKeyboardFocusForVirtualView(this.mKeyboardFocusedVirtualViewId);
        }
        if (b) {
            this.moveFocus(n, rect);
        }
    }
    
    @Override
    public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(view, accessibilityEvent);
        this.onPopulateEventForHost(accessibilityEvent);
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
        this.onPopulateNodeForHost(accessibilityNodeInfoCompat);
    }
    
    protected abstract boolean onPerformActionForVirtualView(final int p0, final int p1, final Bundle p2);
    
    protected void onPopulateEventForHost(final AccessibilityEvent accessibilityEvent) {
    }
    
    protected void onPopulateEventForVirtualView(final int n, final AccessibilityEvent accessibilityEvent) {
    }
    
    protected void onPopulateNodeForHost(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
    }
    
    protected abstract void onPopulateNodeForVirtualView(final int p0, final AccessibilityNodeInfoCompat p1);
    
    protected void onVirtualViewKeyboardFocusChanged(final int n, final boolean b) {
    }
    
    boolean performAction(final int n, final int n2, final Bundle bundle) {
        switch (n) {
            default: {
                return this.performActionForChild(n, n2, bundle);
            }
            case -1: {
                return this.performActionForHost(n2, bundle);
            }
        }
    }
    
    public final boolean requestKeyboardFocusForVirtualView(final int mKeyboardFocusedVirtualViewId) {
        if (!this.mHost.isFocused() && !this.mHost.requestFocus()) {
            return false;
        }
        if (this.mKeyboardFocusedVirtualViewId != mKeyboardFocusedVirtualViewId) {
            if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
                this.clearKeyboardFocusForVirtualView(this.mKeyboardFocusedVirtualViewId);
            }
            this.onVirtualViewKeyboardFocusChanged(this.mKeyboardFocusedVirtualViewId = mKeyboardFocusedVirtualViewId, true);
            this.sendEventForVirtualView(mKeyboardFocusedVirtualViewId, 8);
            return true;
        }
        return false;
    }
    
    public final boolean sendEventForVirtualView(final int n, final int n2) {
        if (n != Integer.MIN_VALUE && this.mManager.isEnabled()) {
            final ViewParent parent = this.mHost.getParent();
            return parent != null && ViewParentCompat.requestSendAccessibilityEvent(parent, this.mHost, this.createEvent(n, n2));
        }
        return false;
    }
    
    private class MyNodeProvider extends AccessibilityNodeProviderCompat
    {
        MyNodeProvider() {
        }
        
        @Override
        public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(final int n) {
            return AccessibilityNodeInfoCompat.obtain(ExploreByTouchHelper.this.obtainAccessibilityNodeInfo(n));
        }
        
        @Override
        public AccessibilityNodeInfoCompat findFocus(int n) {
            if (n != 2) {
                n = ExploreByTouchHelper.this.mKeyboardFocusedVirtualViewId;
            }
            else {
                n = ExploreByTouchHelper.this.mAccessibilityFocusedVirtualViewId;
            }
            if (n != Integer.MIN_VALUE) {
                return this.createAccessibilityNodeInfo(n);
            }
            return null;
        }
        
        @Override
        public boolean performAction(final int n, final int n2, final Bundle bundle) {
            return ExploreByTouchHelper.this.performAction(n, n2, bundle);
        }
    }
}
