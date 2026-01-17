package android.support.v4.view.accessibility;

import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;
@RequiresApi(19)
/* loaded from: classes.dex */
class AccessibilityNodeInfoCompatKitKat {

    /* loaded from: classes.dex */
    static class RangeInfo {
        RangeInfo() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static float getCurrent(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getCurrent();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static float getMax(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getMax();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static float getMin(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getMin();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getType(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getType();
        }
    }

    AccessibilityNodeInfoCompatKitKat() {
    }
}
