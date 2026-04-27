package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build;
import android.view.accessibility.AccessibilityWindowInfo;

public class AccessibilityWindowInfoCompat {
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;
    public static final int TYPE_APPLICATION = 1;
    public static final int TYPE_INPUT_METHOD = 2;
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;
    public static final int TYPE_SYSTEM = 3;
    private static final int UNDEFINED = -1;
    private Object mInfo;

    private AccessibilityWindowInfoCompat(Object obj) {
        this.mInfo = obj;
    }

    public static AccessibilityWindowInfoCompat obtain() {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        return wrapNonNullInstance(AccessibilityWindowInfo.obtain());
    }

    public static AccessibilityWindowInfoCompat obtain(AccessibilityWindowInfoCompat accessibilityWindowInfoCompat) {
        if (Build.VERSION.SDK_INT >= 21 && accessibilityWindowInfoCompat != null) {
            return wrapNonNullInstance(AccessibilityWindowInfo.obtain((AccessibilityWindowInfo) accessibilityWindowInfoCompat.mInfo));
        }
        return null;
    }

    private static String typeToString(int i) {
        switch (i) {
            case 1:
                return "TYPE_APPLICATION";
            case 2:
                return "TYPE_INPUT_METHOD";
            case 3:
                return "TYPE_SYSTEM";
            case 4:
                return "TYPE_ACCESSIBILITY_OVERLAY";
            default:
                return "<UNKNOWN>";
        }
    }

    static AccessibilityWindowInfoCompat wrapNonNullInstance(Object obj) {
        if (obj == null) {
            return null;
        }
        return new AccessibilityWindowInfoCompat(obj);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityWindowInfoCompat accessibilityWindowInfoCompat = (AccessibilityWindowInfoCompat) obj;
        if (this.mInfo != null) {
            return this.mInfo.equals(accessibilityWindowInfoCompat.mInfo);
        }
        if (accessibilityWindowInfoCompat.mInfo != null) {
            return false;
        }
    }

    public AccessibilityNodeInfoCompat getAnchor() {
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        return AccessibilityNodeInfoCompat.wrapNonNullInstance(((AccessibilityWindowInfo) this.mInfo).getAnchor());
    }

    public void getBoundsInScreen(Rect rect) {
        if (Build.VERSION.SDK_INT >= 21) {
            ((AccessibilityWindowInfo) this.mInfo).getBoundsInScreen(rect);
        }
    }

    public AccessibilityWindowInfoCompat getChild(int i) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        return wrapNonNullInstance(((AccessibilityWindowInfo) this.mInfo).getChild(i));
    }

    public int getChildCount() {
        if (Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((AccessibilityWindowInfo) this.mInfo).getChildCount();
    }

    public int getId() {
        if (Build.VERSION.SDK_INT < 21) {
            return -1;
        }
        return ((AccessibilityWindowInfo) this.mInfo).getId();
    }

    public int getLayer() {
        if (Build.VERSION.SDK_INT < 21) {
            return -1;
        }
        return ((AccessibilityWindowInfo) this.mInfo).getLayer();
    }

    public AccessibilityWindowInfoCompat getParent() {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        return wrapNonNullInstance(((AccessibilityWindowInfo) this.mInfo).getParent());
    }

    public AccessibilityNodeInfoCompat getRoot() {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        return AccessibilityNodeInfoCompat.wrapNonNullInstance(((AccessibilityWindowInfo) this.mInfo).getRoot());
    }

    public CharSequence getTitle() {
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        return ((AccessibilityWindowInfo) this.mInfo).getTitle();
    }

    public int getType() {
        if (Build.VERSION.SDK_INT < 21) {
            return -1;
        }
        return ((AccessibilityWindowInfo) this.mInfo).getType();
    }

    public int hashCode() {
        if (this.mInfo != null) {
            return this.mInfo.hashCode();
        }
        return 0;
    }

    public boolean isAccessibilityFocused() {
        if (Build.VERSION.SDK_INT < 21) {
            return true;
        }
        return ((AccessibilityWindowInfo) this.mInfo).isAccessibilityFocused();
    }

    public boolean isActive() {
        if (Build.VERSION.SDK_INT < 21) {
            return true;
        }
        return ((AccessibilityWindowInfo) this.mInfo).isActive();
    }

    public boolean isFocused() {
        if (Build.VERSION.SDK_INT < 21) {
            return true;
        }
        return ((AccessibilityWindowInfo) this.mInfo).isFocused();
    }

    public void recycle() {
        if (Build.VERSION.SDK_INT >= 21) {
            ((AccessibilityWindowInfo) this.mInfo).recycle();
        }
    }

    public String toString() {
        boolean z = false;
        StringBuilder sb = new StringBuilder();
        Rect rect = new Rect();
        getBoundsInScreen(rect);
        sb.append("AccessibilityWindowInfo[");
        sb.append("id=").append(getId());
        sb.append(", type=").append(typeToString(getType()));
        sb.append(", layer=").append(getLayer());
        sb.append(", bounds=").append(rect);
        sb.append(", focused=").append(isFocused());
        sb.append(", active=").append(isActive());
        sb.append(", hasParent=").append(getParent() != null);
        StringBuilder append = sb.append(", hasChildren=");
        if (getChildCount() > 0) {
            z = true;
        }
        append.append(z);
        sb.append(']');
        return sb.toString();
    }
}
