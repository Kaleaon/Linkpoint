package android.support.transition;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(14)
class ViewGroupUtilsApi14 implements ViewGroupUtilsImpl {
    private static final int LAYOUT_TRANSITION_CHANGING = 4;
    private static final String TAG = "ViewGroupUtilsApi14";
    private static Method sCancelMethod;
    private static boolean sCancelMethodFetched;
    private static LayoutTransition sEmptyLayoutTransition;
    private static Field sLayoutSuppressedField;
    private static boolean sLayoutSuppressedFieldFetched;

    ViewGroupUtilsApi14() {
    }

    private static void cancelLayoutTransition(LayoutTransition layoutTransition) {
        if (!sCancelMethodFetched) {
            try {
                sCancelMethod = LayoutTransition.class.getDeclaredMethod("cancel", new Class[0]);
                sCancelMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to access cancel method by reflection");
            }
            sCancelMethodFetched = true;
        }
        if (sCancelMethod != null) {
            try {
                sCancelMethod.invoke(layoutTransition, new Object[0]);
            } catch (IllegalAccessException e2) {
                Log.i(TAG, "Failed to access cancel method by reflection");
            } catch (InvocationTargetException e3) {
                Log.i(TAG, "Failed to invoke cancel method by reflection");
            }
        }
    }

    public ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup viewGroup) {
        return ViewGroupOverlayApi14.createFrom(viewGroup);
    }

    public void suppressLayout(@NonNull ViewGroup viewGroup, boolean z) {
        boolean z2 = false;
        if (sEmptyLayoutTransition == null) {
            sEmptyLayoutTransition = new LayoutTransition() {
                public boolean isChangingLayout() {
                    return true;
                }
            };
            sEmptyLayoutTransition.setAnimator(2, (Animator) null);
            sEmptyLayoutTransition.setAnimator(0, (Animator) null);
            sEmptyLayoutTransition.setAnimator(1, (Animator) null);
            sEmptyLayoutTransition.setAnimator(3, (Animator) null);
            sEmptyLayoutTransition.setAnimator(4, (Animator) null);
        }
        if (!z) {
            viewGroup.setLayoutTransition((LayoutTransition) null);
            if (!sLayoutSuppressedFieldFetched) {
                try {
                    sLayoutSuppressedField = ViewGroup.class.getDeclaredField("mLayoutSuppressed");
                    sLayoutSuppressedField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    Log.i(TAG, "Failed to access mLayoutSuppressed field by reflection");
                }
                sLayoutSuppressedFieldFetched = true;
            }
            if (sLayoutSuppressedField != null) {
                try {
                    z2 = sLayoutSuppressedField.getBoolean(viewGroup);
                    if (z2) {
                        sLayoutSuppressedField.setBoolean(viewGroup, false);
                    }
                } catch (IllegalAccessException e2) {
                    Log.i(TAG, "Failed to get mLayoutSuppressed field by reflection");
                }
            }
            if (z2) {
                viewGroup.requestLayout();
            }
            LayoutTransition layoutTransition = (LayoutTransition) viewGroup.getTag(R.id.transition_layout_save);
            if (layoutTransition != null) {
                viewGroup.setTag(R.id.transition_layout_save, (Object) null);
                viewGroup.setLayoutTransition(layoutTransition);
                return;
            }
            return;
        }
        LayoutTransition layoutTransition2 = viewGroup.getLayoutTransition();
        if (layoutTransition2 != null) {
            if (layoutTransition2.isRunning()) {
                cancelLayoutTransition(layoutTransition2);
            }
            if (layoutTransition2 != sEmptyLayoutTransition) {
                viewGroup.setTag(R.id.transition_layout_save, layoutTransition2);
            }
        }
        viewGroup.setLayoutTransition(sEmptyLayoutTransition);
    }
}
