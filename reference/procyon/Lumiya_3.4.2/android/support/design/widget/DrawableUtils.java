// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.util.Log;
import android.graphics.drawable.DrawableContainer$DrawableContainerState;
import android.graphics.drawable.Drawable$ConstantState;
import android.graphics.drawable.DrawableContainer;
import java.lang.reflect.Method;

class DrawableUtils
{
    private static final String LOG_TAG = "DrawableUtils";
    private static Method sSetConstantStateMethod;
    private static boolean sSetConstantStateMethodFetched;
    
    private DrawableUtils() {
    }
    
    static boolean setContainerConstantState(final DrawableContainer drawableContainer, final Drawable$ConstantState drawable$ConstantState) {
        return setContainerConstantStateV9(drawableContainer, drawable$ConstantState);
    }
    
    private static boolean setContainerConstantStateV9(final DrawableContainer obj, final Drawable$ConstantState drawable$ConstantState) {
        Label_0059: {
            Block_2: {
                Label_0006: {
                    if (!DrawableUtils.sSetConstantStateMethodFetched) {
                        while (true) {
                            try {
                                (DrawableUtils.sSetConstantStateMethod = DrawableContainer.class.getDeclaredMethod("setConstantState", DrawableContainer$DrawableContainerState.class)).setAccessible(true);
                                DrawableUtils.sSetConstantStateMethodFetched = true;
                                break Label_0006;
                            }
                            catch (final NoSuchMethodException ex) {
                                Log.e("DrawableUtils", "Could not fetch setConstantState(). Oh well.");
                                continue;
                            }
                            break;
                        }
                        break Block_2;
                    }
                }
                if (DrawableUtils.sSetConstantStateMethod != null) {
                    break Label_0059;
                }
                return false;
            }
            try {
                DrawableUtils.sSetConstantStateMethod.invoke(obj, drawable$ConstantState);
                return true;
            }
            catch (final Exception ex2) {
                Log.e("DrawableUtils", "Could not invoke setConstantState(). Oh well.");
                return false;
            }
        }
    }
}
