// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import java.lang.reflect.Field;
import android.animation.LayoutTransition;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class ViewGroupUtilsApi14 implements ViewGroupUtilsImpl
{
    private static final int LAYOUT_TRANSITION_CHANGING = 4;
    private static final String TAG = "ViewGroupUtilsApi14";
    private static Method sCancelMethod;
    private static boolean sCancelMethodFetched;
    private static LayoutTransition sEmptyLayoutTransition;
    private static Field sLayoutSuppressedField;
    private static boolean sLayoutSuppressedFieldFetched;
    
    private static void cancelLayoutTransition(final LayoutTransition obj) {
        Label_0053: {
            Block_2: {
                Label_0006: {
                    if (!ViewGroupUtilsApi14.sCancelMethodFetched) {
                        while (true) {
                            try {
                                (ViewGroupUtilsApi14.sCancelMethod = LayoutTransition.class.getDeclaredMethod("cancel", (Class<?>[])new Class[0])).setAccessible(true);
                                ViewGroupUtilsApi14.sCancelMethodFetched = true;
                                break Label_0006;
                            }
                            catch (final NoSuchMethodException ex) {
                                Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
                                continue;
                            }
                            break;
                        }
                        break Block_2;
                    }
                }
                if (ViewGroupUtilsApi14.sCancelMethod != null) {
                    break Label_0053;
                }
                return;
            }
            try {
                ViewGroupUtilsApi14.sCancelMethod.invoke(obj, new Object[0]);
            }
            catch (final IllegalAccessException ex2) {
                Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
            }
            catch (final InvocationTargetException ex3) {
                Log.i("ViewGroupUtilsApi14", "Failed to invoke cancel method by reflection");
            }
        }
    }
    
    @Override
    public ViewGroupOverlayImpl getOverlay(@NonNull final ViewGroup viewGroup) {
        return ViewGroupOverlayApi14.createFrom(viewGroup);
    }
    
    @Override
    public void suppressLayout(@NonNull final ViewGroup viewGroup, boolean boolean1) {
        final boolean b = false;
        final boolean b2 = false;
        if (ViewGroupUtilsApi14.sEmptyLayoutTransition == null) {
            (ViewGroupUtilsApi14.sEmptyLayoutTransition = new LayoutTransition() {
                public boolean isChangingLayout() {
                    return true;
                }
            }).setAnimator(2, (Animator)null);
            ViewGroupUtilsApi14.sEmptyLayoutTransition.setAnimator(0, (Animator)null);
            ViewGroupUtilsApi14.sEmptyLayoutTransition.setAnimator(1, (Animator)null);
            ViewGroupUtilsApi14.sEmptyLayoutTransition.setAnimator(3, (Animator)null);
            ViewGroupUtilsApi14.sEmptyLayoutTransition.setAnimator(4, (Animator)null);
        }
        if (!boolean1) {
            viewGroup.setLayoutTransition((LayoutTransition)null);
            while (true) {
                Label_0205: {
                    Label_0026: {
                        if (!ViewGroupUtilsApi14.sLayoutSuppressedFieldFetched) {
                            while (true) {
                                try {
                                    (ViewGroupUtilsApi14.sLayoutSuppressedField = ViewGroup.class.getDeclaredField("mLayoutSuppressed")).setAccessible(true);
                                    ViewGroupUtilsApi14.sLayoutSuppressedFieldFetched = true;
                                    break Label_0026;
                                }
                                catch (final NoSuchFieldException ex) {
                                    Log.i("ViewGroupUtilsApi14", "Failed to access mLayoutSuppressed field by reflection");
                                    continue;
                                }
                                break;
                            }
                            break Label_0205;
                        }
                    }
                    if (ViewGroupUtilsApi14.sLayoutSuppressedField != null) {
                        break Label_0205;
                    }
                    boolean1 = b2;
                    if (boolean1) {
                        viewGroup.requestLayout();
                    }
                    final LayoutTransition layoutTransition = (LayoutTransition)viewGroup.getTag(R.id.transition_layout_save);
                    if (layoutTransition == null) {
                        return;
                    }
                    viewGroup.setTag(R.id.transition_layout_save, (Object)null);
                    viewGroup.setLayoutTransition(layoutTransition);
                    return;
                }
                boolean1 = b;
                try {
                    final boolean b3 = boolean1 = ViewGroupUtilsApi14.sLayoutSuppressedField.getBoolean(viewGroup);
                    if (b3) {
                        boolean1 = b3;
                        ViewGroupUtilsApi14.sLayoutSuppressedField.setBoolean(viewGroup, false);
                        boolean1 = b3;
                    }
                }
                catch (final IllegalAccessException ex2) {
                    Log.i("ViewGroupUtilsApi14", "Failed to get mLayoutSuppressed field by reflection");
                }
                continue;
            }
        }
        final LayoutTransition layoutTransition2 = viewGroup.getLayoutTransition();
        if (layoutTransition2 != null) {
            if (layoutTransition2.isRunning()) {
                cancelLayoutTransition(layoutTransition2);
            }
            if (layoutTransition2 != ViewGroupUtilsApi14.sEmptyLayoutTransition) {
                viewGroup.setTag(R.id.transition_layout_save, (Object)layoutTransition2);
            }
        }
        viewGroup.setLayoutTransition(ViewGroupUtilsApi14.sEmptyLayoutTransition);
    }
}
