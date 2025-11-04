// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.app;

import java.lang.reflect.InvocationTargetException;
import android.support.annotation.Nullable;
import java.lang.reflect.Method;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.TintContextWrapper;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.support.v7.appcompat.R;
import android.view.InflateException;
import android.content.res.TypedArray;
import android.view.View$OnClickListener;
import android.support.v4.view.ViewCompat;
import android.os.Build$VERSION;
import android.content.ContextWrapper;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.lang.reflect.Constructor;
import java.util.Map;

class AppCompatViewInflater
{
    private static final String LOG_TAG = "AppCompatViewInflater";
    private static final String[] sClassPrefixList;
    private static final Map<String, Constructor<? extends View>> sConstructorMap;
    private static final Class<?>[] sConstructorSignature;
    private static final int[] sOnClickAttrs;
    private final Object[] mConstructorArgs;
    
    static {
        sConstructorSignature = new Class[] { Context.class, AttributeSet.class };
        sOnClickAttrs = new int[] { 16843375 };
        sClassPrefixList = new String[] { "android.widget.", "android.view.", "android.webkit." };
        sConstructorMap = new ArrayMap<String, Constructor<? extends View>>();
    }
    
    AppCompatViewInflater() {
        this.mConstructorArgs = new Object[2];
    }
    
    private void checkOnClickListener(final View view, final AttributeSet set) {
        final Context context = view.getContext();
        if (context instanceof ContextWrapper && (Build$VERSION.SDK_INT < 15 || ViewCompat.hasOnClickListeners(view))) {
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, AppCompatViewInflater.sOnClickAttrs);
            final String string = obtainStyledAttributes.getString(0);
            if (string != null) {
                view.setOnClickListener((View$OnClickListener)new DeclaredOnClickListener(view, string));
            }
            obtainStyledAttributes.recycle();
        }
    }
    
    private View createView(final Context context, final String str, final String str2) throws ClassNotFoundException, InflateException {
        final Constructor constructor = AppCompatViewInflater.sConstructorMap.get(str);
        Label_0039: {
            if (constructor == null) {
                break Label_0039;
            }
            Constructor<? extends View> constructor2 = constructor;
        Label_0051_Outer:
            while (true) {
            Block_3_Outer:
                while (true) {
                    try {
                        constructor2.setAccessible(true);
                        return (View)constructor2.newInstance(this.mConstructorArgs);
                        ClassLoader classLoader = null;
                        String string = null;
                        constructor2 = classLoader.loadClass(string).asSubclass(View.class).getConstructor(AppCompatViewInflater.sConstructorSignature);
                        AppCompatViewInflater.sConstructorMap.put(str, constructor2);
                        continue Label_0051_Outer;
                        while (true) {
                            string = str;
                            continue Block_3_Outer;
                            classLoader = context.getClassLoader();
                            iftrue(Label_0086:)(str2 != null);
                            continue;
                        }
                    }
                    catch (final Exception ex) {
                        return null;
                    }
                    Label_0086: {
                        final String string = str2 + str;
                    }
                    continue;
                }
            }
        }
    }
    
    private View createViewFromTag(final Context context, String attributeValue, final AttributeSet set) {
        Label_0057: {
            if (attributeValue.equals("view")) {
                break Label_0057;
            }
            try {
                while (true) {
                    this.mConstructorArgs[0] = context;
                    this.mConstructorArgs[1] = set;
                    if (-1 != attributeValue.indexOf(46)) {
                        return this.createView(context, attributeValue, null);
                    }
                    for (int i = 0; i < AppCompatViewInflater.sClassPrefixList.length; ++i) {
                        final View view = this.createView(context, attributeValue, AppCompatViewInflater.sClassPrefixList[i]);
                        if (view != null) {
                            return view;
                        }
                    }
                    return null;
                    attributeValue = set.getAttributeValue((String)null, "class");
                    continue;
                }
            }
            catch (final Exception ex) {
                return null;
            }
            finally {
                this.mConstructorArgs[0] = null;
                this.mConstructorArgs[1] = null;
            }
        }
    }
    
    private static Context themifyContext(Context context, final AttributeSet set, final boolean b, final boolean b2) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.View, 0, 0);
        int n;
        if (!b) {
            n = 0;
        }
        else {
            n = obtainStyledAttributes.getResourceId(R.styleable.View_android_theme, 0);
        }
        if (b2 && n == 0) {
            n = obtainStyledAttributes.getResourceId(R.styleable.View_theme, 0);
            if (n != 0) {
                Log.i("AppCompatViewInflater", "app:theme is now deprecated. Please move to using android:theme instead.");
            }
        }
        obtainStyledAttributes.recycle();
        if (n != 0) {
            if (!(context instanceof ContextThemeWrapper) || ((ContextThemeWrapper)context).getThemeResId() != n) {
                context = (Context)new ContextThemeWrapper(context, n);
            }
        }
        return context;
    }
    
    public final View createView(View viewFromTag, final String s, @NonNull final Context context, @NonNull final AttributeSet set, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final Object o = null;
        Context context2;
        if (b && viewFromTag != null) {
            context2 = ((View)viewFromTag).getContext();
        }
        else {
            context2 = context;
        }
        if (b2 || b3) {
            context2 = themifyContext(context2, set, b2, b3);
        }
        Context wrap;
        if (!b4) {
            wrap = context2;
        }
        else {
            wrap = TintContextWrapper.wrap(context2);
        }
        switch (s) {
            default: {
                viewFromTag = o;
                break;
            }
            case "TextView": {
                viewFromTag = new AppCompatTextView(wrap, set);
                break;
            }
            case "ImageView": {
                viewFromTag = new AppCompatImageView(wrap, set);
                break;
            }
            case "Button": {
                viewFromTag = new AppCompatButton(wrap, set);
                break;
            }
            case "EditText": {
                viewFromTag = new AppCompatEditText(wrap, set);
                break;
            }
            case "Spinner": {
                viewFromTag = new AppCompatSpinner(wrap, set);
                break;
            }
            case "ImageButton": {
                viewFromTag = new AppCompatImageButton(wrap, set);
                break;
            }
            case "CheckBox": {
                viewFromTag = new AppCompatCheckBox(wrap, set);
                break;
            }
            case "RadioButton": {
                viewFromTag = new AppCompatRadioButton(wrap, set);
                break;
            }
            case "CheckedTextView": {
                viewFromTag = new AppCompatCheckedTextView(wrap, set);
                break;
            }
            case "AutoCompleteTextView": {
                viewFromTag = new AppCompatAutoCompleteTextView(wrap, set);
                break;
            }
            case "MultiAutoCompleteTextView": {
                viewFromTag = new AppCompatMultiAutoCompleteTextView(wrap, set);
                break;
            }
            case "RatingBar": {
                viewFromTag = new AppCompatRatingBar(wrap, set);
                break;
            }
            case "SeekBar": {
                viewFromTag = new AppCompatSeekBar(wrap, set);
                break;
            }
        }
        if (viewFromTag == null && context != wrap) {
            viewFromTag = this.createViewFromTag(wrap, s, set);
        }
        if (viewFromTag != null) {
            this.checkOnClickListener((View)viewFromTag, set);
        }
        return (View)viewFromTag;
    }
    
    private static class DeclaredOnClickListener implements View$OnClickListener
    {
        private final View mHostView;
        private final String mMethodName;
        private Context mResolvedContext;
        private Method mResolvedMethod;
        
        public DeclaredOnClickListener(@NonNull final View mHostView, @NonNull final String mMethodName) {
            this.mHostView = mHostView;
            this.mMethodName = mMethodName;
        }
        
        @NonNull
        private void resolveMethod(@Nullable Context context, @NonNull final String s) {
        Label_0120_Outer:
            while (true) {
                Label_0179: {
                    if (context == null) {
                        final int id = this.mHostView.getId();
                        if (id != -1) {
                            context = (Context)(" with id '" + this.mHostView.getContext().getResources().getResourceEntryName(id) + "'");
                            break;
                        }
                        break Label_0179;
                    }
                    while (true) {
                        try {
                            if (!context.isRestricted()) {
                                final Method method = context.getClass().getMethod(this.mMethodName, View.class);
                                if (method != null) {
                                    this.mResolvedMethod = method;
                                    this.mResolvedContext = context;
                                    return;
                                }
                            }
                            if (!(context instanceof ContextWrapper)) {
                                context = null;
                                continue Label_0120_Outer;
                            }
                            context = ((ContextWrapper)context).getBaseContext();
                            continue Label_0120_Outer;
                            context = (Context)"";
                            break;
                        }
                        catch (final NoSuchMethodException ex) {
                            continue;
                        }
                        break;
                    }
                }
            }
            throw new IllegalStateException("Could not find method " + this.mMethodName + "(View) in a parent or ancestor Context for android:onClick " + "attribute defined on view " + this.mHostView.getClass() + (String)context);
        }
        
        public void onClick(@NonNull final View view) {
            Label_0028: {
                if (this.mResolvedMethod == null) {
                    break Label_0028;
                }
                try {
                    while (true) {
                        this.mResolvedMethod.invoke(this.mResolvedContext, view);
                        return;
                        this.resolveMethod(this.mHostView.getContext(), this.mMethodName);
                        continue;
                    }
                }
                catch (final IllegalAccessException cause) {
                    throw new IllegalStateException("Could not execute non-public method for android:onClick", cause);
                }
                catch (final InvocationTargetException cause2) {
                    throw new IllegalStateException("Could not execute method for android:onClick", cause2);
                }
            }
        }
    }
}
