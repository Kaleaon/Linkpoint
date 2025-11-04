// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.view.LayoutInflater$Factory;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater$Factory2;
import android.view.LayoutInflater;
import android.os.Build$VERSION;
import java.lang.reflect.Field;

public final class LayoutInflaterCompat
{
    static final LayoutInflaterCompatBaseImpl IMPL;
    private static final String TAG = "LayoutInflaterCompatHC";
    private static boolean sCheckedField;
    private static Field sLayoutInflaterFactory2Field;
    
    static {
        if (Build$VERSION.SDK_INT < 21) {
            IMPL = new LayoutInflaterCompatBaseImpl();
        }
        else {
            IMPL = (LayoutInflaterCompatBaseImpl)new LayoutInflaterCompatApi21Impl();
        }
    }
    
    private LayoutInflaterCompat() {
    }
    
    static void forceSetFactory2(final LayoutInflater layoutInflater, final LayoutInflater$Factory2 value) {
        Label_0076: {
            Block_2: {
                Label_0006: {
                    if (!LayoutInflaterCompat.sCheckedField) {
                        while (true) {
                            try {
                                (LayoutInflaterCompat.sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2")).setAccessible(true);
                                LayoutInflaterCompat.sCheckedField = true;
                                break Label_0006;
                            }
                            catch (final NoSuchFieldException ex) {
                                Log.e("LayoutInflaterCompatHC", "forceSetFactory2 Could not find field 'mFactory2' on class " + LayoutInflater.class.getName() + "; inflation may have unexpected results.", (Throwable)ex);
                                continue;
                            }
                            break;
                        }
                        break Block_2;
                    }
                }
                if (LayoutInflaterCompat.sLayoutInflaterFactory2Field != null) {
                    break Label_0076;
                }
                return;
            }
            try {
                LayoutInflaterCompat.sLayoutInflaterFactory2Field.set(layoutInflater, value);
            }
            catch (final IllegalAccessException ex2) {
                Log.e("LayoutInflaterCompatHC", "forceSetFactory2 could not set the Factory2 on LayoutInflater " + layoutInflater + "; inflation may have unexpected results.", (Throwable)ex2);
            }
        }
    }
    
    @Deprecated
    public static LayoutInflaterFactory getFactory(final LayoutInflater layoutInflater) {
        return LayoutInflaterCompat.IMPL.getFactory(layoutInflater);
    }
    
    @Deprecated
    public static void setFactory(@NonNull final LayoutInflater layoutInflater, @NonNull final LayoutInflaterFactory layoutInflaterFactory) {
        LayoutInflaterCompat.IMPL.setFactory(layoutInflater, layoutInflaterFactory);
    }
    
    public static void setFactory2(@NonNull final LayoutInflater layoutInflater, @NonNull final LayoutInflater$Factory2 layoutInflater$Factory2) {
        LayoutInflaterCompat.IMPL.setFactory2(layoutInflater, layoutInflater$Factory2);
    }
    
    static class Factory2Wrapper implements LayoutInflater$Factory2
    {
        final LayoutInflaterFactory mDelegateFactory;
        
        Factory2Wrapper(final LayoutInflaterFactory mDelegateFactory) {
            this.mDelegateFactory = mDelegateFactory;
        }
        
        public View onCreateView(final View view, final String s, final Context context, final AttributeSet set) {
            return this.mDelegateFactory.onCreateView(view, s, context, set);
        }
        
        public View onCreateView(final String s, final Context context, final AttributeSet set) {
            return this.mDelegateFactory.onCreateView(null, s, context, set);
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "{" + this.mDelegateFactory + "}";
        }
    }
    
    @RequiresApi(21)
    static class LayoutInflaterCompatApi21Impl extends LayoutInflaterCompatBaseImpl
    {
        @Override
        public void setFactory(final LayoutInflater layoutInflater, final LayoutInflaterFactory layoutInflaterFactory) {
            final LayoutInflater$Factory2 layoutInflater$Factory2 = null;
            Object factory2;
            if (layoutInflaterFactory == null) {
                factory2 = layoutInflater$Factory2;
            }
            else {
                factory2 = new Factory2Wrapper(layoutInflaterFactory);
            }
            layoutInflater.setFactory2((LayoutInflater$Factory2)factory2);
        }
        
        @Override
        public void setFactory2(final LayoutInflater layoutInflater, final LayoutInflater$Factory2 factory2) {
            layoutInflater.setFactory2(factory2);
        }
    }
    
    static class LayoutInflaterCompatBaseImpl
    {
        public LayoutInflaterFactory getFactory(final LayoutInflater layoutInflater) {
            final LayoutInflater$Factory factory = layoutInflater.getFactory();
            if (!(factory instanceof Factory2Wrapper)) {
                return null;
            }
            return ((Factory2Wrapper)factory).mDelegateFactory;
        }
        
        public void setFactory(final LayoutInflater layoutInflater, final LayoutInflaterFactory layoutInflaterFactory) {
            final LayoutInflater$Factory2 layoutInflater$Factory2 = null;
            Object o;
            if (layoutInflaterFactory == null) {
                o = layoutInflater$Factory2;
            }
            else {
                o = new Factory2Wrapper(layoutInflaterFactory);
            }
            this.setFactory2(layoutInflater, (LayoutInflater$Factory2)o);
        }
        
        public void setFactory2(final LayoutInflater layoutInflater, final LayoutInflater$Factory2 factory2) {
            layoutInflater.setFactory2(factory2);
            final LayoutInflater$Factory factory3 = layoutInflater.getFactory();
            if (!(factory3 instanceof LayoutInflater$Factory2)) {
                LayoutInflaterCompat.forceSetFactory2(layoutInflater, factory2);
            }
            else {
                LayoutInflaterCompat.forceSetFactory2(layoutInflater, (LayoutInflater$Factory2)factory3);
            }
        }
    }
}
