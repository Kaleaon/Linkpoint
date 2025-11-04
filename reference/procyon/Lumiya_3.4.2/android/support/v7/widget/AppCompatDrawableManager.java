// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import java.lang.ref.Reference;
import android.support.v4.util.LruCache;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.annotation.Nullable;
import android.content.res.Resources$Theme;
import android.support.annotation.RequiresApi;
import android.support.v7.content.res.AppCompatResources;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.graphics.ColorFilter;
import android.util.AttributeSet;
import android.content.res.XmlResourceParser;
import android.content.res.Resources;
import android.util.Log;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.os.Build$VERSION;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.appcompat.R;
import android.util.TypedValue;
import android.content.res.ColorStateList;
import android.support.v4.util.SparseArrayCompat;
import android.graphics.drawable.Drawable$ConstantState;
import java.lang.ref.WeakReference;
import android.support.v4.util.LongSparseArray;
import android.content.Context;
import java.util.WeakHashMap;
import android.support.v4.util.ArrayMap;
import android.graphics.PorterDuff$Mode;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public final class AppCompatDrawableManager
{
    private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY;
    private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED;
    private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL;
    private static final ColorFilterLruCache COLOR_FILTER_CACHE;
    private static final boolean DEBUG = false;
    private static final PorterDuff$Mode DEFAULT_MODE;
    private static AppCompatDrawableManager INSTANCE;
    private static final String PLATFORM_VD_CLAZZ = "android.graphics.drawable.VectorDrawable";
    private static final String SKIP_DRAWABLE_TAG = "appcompat_skip_skip";
    private static final String TAG = "AppCompatDrawableManager";
    private static final int[] TINT_CHECKABLE_BUTTON_LIST;
    private static final int[] TINT_COLOR_CONTROL_NORMAL;
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST;
    private ArrayMap<String, InflateDelegate> mDelegates;
    private final Object mDrawableCacheLock;
    private final WeakHashMap<Context, LongSparseArray<WeakReference<Drawable$ConstantState>>> mDrawableCaches;
    private boolean mHasCheckedVectorDrawableSetup;
    private SparseArrayCompat<String> mKnownDrawableIdTags;
    private WeakHashMap<Context, SparseArrayCompat<ColorStateList>> mTintLists;
    private TypedValue mTypedValue;
    
    static {
        DEFAULT_MODE = PorterDuff$Mode.SRC_IN;
        COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
        COLORFILTER_TINT_COLOR_CONTROL_NORMAL = new int[] { R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha };
        TINT_COLOR_CONTROL_NORMAL = new int[] { R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_seekbar_tick_mark_material, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha };
        COLORFILTER_COLOR_CONTROL_ACTIVATED = new int[] { R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_material, R.drawable.abc_text_select_handle_left_mtrl_dark, R.drawable.abc_text_select_handle_middle_mtrl_dark, R.drawable.abc_text_select_handle_right_mtrl_dark, R.drawable.abc_text_select_handle_left_mtrl_light, R.drawable.abc_text_select_handle_middle_mtrl_light, R.drawable.abc_text_select_handle_right_mtrl_light };
        COLORFILTER_COLOR_BACKGROUND_MULTIPLY = new int[] { R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult };
        TINT_COLOR_CONTROL_STATE_LIST = new int[] { R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material };
        TINT_CHECKABLE_BUTTON_LIST = new int[] { R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material };
    }
    
    public AppCompatDrawableManager() {
        this.mDrawableCacheLock = new Object();
        this.mDrawableCaches = new WeakHashMap<Context, LongSparseArray<WeakReference<Drawable$ConstantState>>>(0);
    }
    
    private void addDelegate(@NonNull final String s, @NonNull final InflateDelegate inflateDelegate) {
        if (this.mDelegates == null) {
            this.mDelegates = new ArrayMap<String, InflateDelegate>();
        }
        this.mDelegates.put(s, inflateDelegate);
    }
    
    private boolean addDrawableToCache(@NonNull final Context context, final long n, @NonNull final Drawable drawable) {
        final Drawable$ConstantState constantState = drawable.getConstantState();
        if (constantState == null) {
            return false;
        }
        synchronized (this.mDrawableCacheLock) {
            final LongSparseArray longSparseArray = this.mDrawableCaches.get(context);
            LongSparseArray<WeakReference<Drawable$ConstantState>> longSparseArray2;
            if (longSparseArray != null) {
                longSparseArray2 = longSparseArray;
            }
            else {
                final LongSparseArray<WeakReference<Drawable$ConstantState>> value = new LongSparseArray<WeakReference<Drawable$ConstantState>>();
                this.mDrawableCaches.put(context, value);
                longSparseArray2 = value;
            }
            longSparseArray2.put(n, new WeakReference<Drawable$ConstantState>(constantState));
            return true;
        }
    }
    
    private void addTintListToCache(@NonNull final Context context, @DrawableRes final int n, @NonNull final ColorStateList list) {
        if (this.mTintLists == null) {
            this.mTintLists = new WeakHashMap<Context, SparseArrayCompat<ColorStateList>>();
        }
        final SparseArrayCompat sparseArrayCompat = this.mTintLists.get(context);
        SparseArrayCompat sparseArrayCompat2;
        if (sparseArrayCompat != null) {
            sparseArrayCompat2 = sparseArrayCompat;
        }
        else {
            final SparseArrayCompat value = new SparseArrayCompat();
            this.mTintLists.put(context, value);
            sparseArrayCompat2 = value;
        }
        sparseArrayCompat2.append(n, list);
    }
    
    private static boolean arrayContains(final int[] array, final int n) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    private void checkVectorDrawableSetup(@NonNull final Context context) {
        if (this.mHasCheckedVectorDrawableSetup) {
            return;
        }
        this.mHasCheckedVectorDrawableSetup = true;
        final Drawable drawable = this.getDrawable(context, R.drawable.abc_vector_test);
        if (drawable != null && isVectorDrawable(drawable)) {
            return;
        }
        this.mHasCheckedVectorDrawableSetup = false;
        throw new IllegalStateException("This app has been built with an incorrect configuration. Please configure your build for VectorDrawableCompat.");
    }
    
    private ColorStateList createBorderlessButtonColorStateList(@NonNull final Context context) {
        return this.createButtonColorStateList(context, 0);
    }
    
    private ColorStateList createButtonColorStateList(@NonNull final Context context, @ColorInt final int n) {
        final int themeAttrColor = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlHighlight);
        return new ColorStateList(new int[][] { ThemeUtils.DISABLED_STATE_SET, ThemeUtils.PRESSED_STATE_SET, ThemeUtils.FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET }, new int[] { ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorButtonNormal), ColorUtils.compositeColors(themeAttrColor, n), ColorUtils.compositeColors(themeAttrColor, n), n });
    }
    
    private static long createCacheKey(final TypedValue typedValue) {
        return (long)typedValue.assetCookie << 32 | (long)typedValue.data;
    }
    
    private ColorStateList createColoredButtonColorStateList(@NonNull final Context context) {
        return this.createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, R.attr.colorAccent));
    }
    
    private ColorStateList createDefaultButtonColorStateList(@NonNull final Context context) {
        return this.createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, R.attr.colorButtonNormal));
    }
    
    private Drawable createDrawableIfNeeded(@NonNull final Context context, @DrawableRes final int n) {
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        final TypedValue mTypedValue = this.mTypedValue;
        context.getResources().getValue(n, mTypedValue, true);
        final long cacheKey = createCacheKey(mTypedValue);
        Object cachedDrawable = this.getCachedDrawable(context, cacheKey);
        if (cachedDrawable == null) {
            if (n == R.drawable.abc_cab_background_top_material) {
                cachedDrawable = new LayerDrawable(new Drawable[] { this.getDrawable(context, R.drawable.abc_cab_background_internal_bg), this.getDrawable(context, R.drawable.abc_cab_background_top_mtrl_alpha) });
            }
            if (cachedDrawable != null) {
                ((Drawable)cachedDrawable).setChangingConfigurations(mTypedValue.changingConfigurations);
                this.addDrawableToCache(context, cacheKey, (Drawable)cachedDrawable);
            }
            return (Drawable)cachedDrawable;
        }
        return (Drawable)cachedDrawable;
    }
    
    private ColorStateList createSwitchThumbColorStateList(final Context context) {
        final int[][] array = new int[3][];
        final int[] array2 = new int[3];
        final ColorStateList themeAttrColorStateList = ThemeUtils.getThemeAttrColorStateList(context, R.attr.colorSwitchThumbNormal);
        if (themeAttrColorStateList != null && themeAttrColorStateList.isStateful()) {
            array[0] = ThemeUtils.DISABLED_STATE_SET;
            array2[0] = themeAttrColorStateList.getColorForState(array[0], 0);
            array[1] = ThemeUtils.CHECKED_STATE_SET;
            array2[1] = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
            array[2] = ThemeUtils.EMPTY_STATE_SET;
            array2[2] = themeAttrColorStateList.getDefaultColor();
        }
        else {
            array[0] = ThemeUtils.DISABLED_STATE_SET;
            array2[0] = ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorSwitchThumbNormal);
            array[1] = ThemeUtils.CHECKED_STATE_SET;
            array2[1] = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
            array[2] = ThemeUtils.EMPTY_STATE_SET;
            array2[2] = ThemeUtils.getThemeAttrColor(context, R.attr.colorSwitchThumbNormal);
        }
        return new ColorStateList(array, array2);
    }
    
    private static PorterDuffColorFilter createTintFilter(final ColorStateList list, final PorterDuff$Mode porterDuff$Mode, final int[] array) {
        if (list != null && porterDuff$Mode != null) {
            return getPorterDuffColorFilter(list.getColorForState(array, 0), porterDuff$Mode);
        }
        return null;
    }
    
    public static AppCompatDrawableManager get() {
        if (AppCompatDrawableManager.INSTANCE == null) {
            installDefaultInflateDelegates(AppCompatDrawableManager.INSTANCE = new AppCompatDrawableManager());
        }
        return AppCompatDrawableManager.INSTANCE;
    }
    
    private Drawable getCachedDrawable(@NonNull final Context key, final long n) {
        Object o = null;
        Label_0083: {
            synchronized (this.mDrawableCacheLock) {
                final LongSparseArray longSparseArray = this.mDrawableCaches.get(key);
                if (longSparseArray != null) {
                    o = longSparseArray.get(n);
                    if (o != null) {
                        o = ((Reference)o).get();
                        if (o != null) {
                            break Label_0083;
                        }
                        longSparseArray.delete(n);
                    }
                    return null;
                }
                return null;
            }
        }
        final Context context;
        final Drawable drawable = ((Drawable$ConstantState)o).newDrawable(context.getResources());
        final Object o2;
        monitorexit(o2);
        return drawable;
    }
    
    public static PorterDuffColorFilter getPorterDuffColorFilter(final int n, final PorterDuff$Mode porterDuff$Mode) {
        final PorterDuffColorFilter value = AppCompatDrawableManager.COLOR_FILTER_CACHE.get(n, porterDuff$Mode);
        PorterDuffColorFilter porterDuffColorFilter;
        if (value != null) {
            porterDuffColorFilter = value;
        }
        else {
            final PorterDuffColorFilter porterDuffColorFilter2 = new PorterDuffColorFilter(n, porterDuff$Mode);
            AppCompatDrawableManager.COLOR_FILTER_CACHE.put(n, porterDuff$Mode, porterDuffColorFilter2);
            porterDuffColorFilter = porterDuffColorFilter2;
        }
        return porterDuffColorFilter;
    }
    
    private ColorStateList getTintListFromCache(@NonNull final Context key, @DrawableRes final int n) {
        if (this.mTintLists == null) {
            return null;
        }
        final SparseArrayCompat sparseArrayCompat = this.mTintLists.get(key);
        ColorStateList list;
        if (sparseArrayCompat == null) {
            list = null;
        }
        else {
            list = (ColorStateList)sparseArrayCompat.get(n);
        }
        return list;
    }
    
    static PorterDuff$Mode getTintMode(final int n) {
        PorterDuff$Mode multiply = null;
        if (n == R.drawable.abc_switch_thumb_material) {
            multiply = PorterDuff$Mode.MULTIPLY;
        }
        return multiply;
    }
    
    private static void installDefaultInflateDelegates(@NonNull final AppCompatDrawableManager appCompatDrawableManager) {
        if (Build$VERSION.SDK_INT < 24) {
            appCompatDrawableManager.addDelegate("vector", (InflateDelegate)new VdcInflateDelegate());
            if (Build$VERSION.SDK_INT >= 11) {
                appCompatDrawableManager.addDelegate("animated-vector", (InflateDelegate)new AvdcInflateDelegate());
            }
        }
    }
    
    private static boolean isVectorDrawable(@NonNull final Drawable drawable) {
        boolean b = false;
        if (drawable instanceof VectorDrawableCompat || "android.graphics.drawable.VectorDrawable".equals(drawable.getClass().getName())) {
            b = true;
        }
        return b;
    }
    
    private Drawable loadDrawableFromDelegates(@NonNull final Context context, @DrawableRes final int n) {
        if (this.mDelegates == null || this.mDelegates.isEmpty()) {
            return null;
        }
        Label_0037: {
            if (this.mKnownDrawableIdTags != null) {
                final String anObject = this.mKnownDrawableIdTags.get(n);
                if (!"appcompat_skip_skip".equals(anObject)) {
                    if (anObject == null) {
                        break Label_0037;
                    }
                    if (this.mDelegates.get(anObject) != null) {
                        break Label_0037;
                    }
                }
                return null;
            }
            this.mKnownDrawableIdTags = new SparseArrayCompat<String>();
        }
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        final TypedValue mTypedValue = this.mTypedValue;
        final Resources resources = context.getResources();
        resources.getValue(n, mTypedValue, true);
        final long cacheKey = createCacheKey(mTypedValue);
        Drawable drawable = this.getCachedDrawable(context, cacheKey);
        if (drawable == null) {
            Drawable drawable2;
            if (mTypedValue.string == null) {
                drawable2 = drawable;
            }
            else {
                drawable2 = drawable;
                if (mTypedValue.string.toString().endsWith(".xml")) {
                    while (true) {
                        drawable2 = drawable;
                        while (true) {
                            XmlResourceParser xml;
                            AttributeSet attributeSet;
                            InflateDelegate inflateDelegate;
                            try {
                                xml = resources.getXml(n);
                                drawable2 = drawable;
                                attributeSet = Xml.asAttributeSet((XmlPullParser)xml);
                                int next;
                                do {
                                    drawable2 = drawable;
                                    next = ((XmlPullParser)xml).next();
                                } while (next != 2 && next != 1);
                                if (next != 2) {
                                    drawable2 = drawable;
                                    drawable2 = drawable;
                                    final XmlPullParserException ex = new XmlPullParserException("No start tag found");
                                    drawable2 = drawable;
                                    throw ex;
                                }
                                drawable2 = drawable;
                                final String name = ((XmlPullParser)xml).getName();
                                drawable2 = drawable;
                                this.mKnownDrawableIdTags.append(n, name);
                                drawable2 = drawable;
                                inflateDelegate = this.mDelegates.get(name);
                                if (inflateDelegate == null) {
                                    if ((drawable2 = drawable) != null) {
                                        drawable2 = drawable;
                                        drawable.setChangingConfigurations(mTypedValue.changingConfigurations);
                                        drawable2 = drawable;
                                        this.addDrawableToCache(context, cacheKey, drawable);
                                        drawable2 = drawable;
                                        break;
                                    }
                                    break;
                                }
                            }
                            catch (final Exception ex2) {
                                Log.e("AppCompatDrawableManager", "Exception while inflating drawable", (Throwable)ex2);
                                break;
                            }
                            drawable2 = drawable;
                            drawable = inflateDelegate.createFromXmlInner(context, (XmlPullParser)xml, attributeSet, context.getTheme());
                            continue;
                        }
                    }
                }
            }
            if (drawable2 == null) {
                this.mKnownDrawableIdTags.append(n, "appcompat_skip_skip");
            }
            return drawable2;
        }
        return drawable;
    }
    
    private void removeDelegate(@NonNull final String s, @NonNull final InflateDelegate inflateDelegate) {
        if (this.mDelegates != null && this.mDelegates.get(s) == inflateDelegate) {
            this.mDelegates.remove(s);
        }
    }
    
    private static void setPorterDuffColorFilter(Drawable mutate, final int n, PorterDuff$Mode default_MODE) {
        if (DrawableUtils.canSafelyMutateDrawable(mutate)) {
            mutate = mutate.mutate();
        }
        if (default_MODE == null) {
            default_MODE = AppCompatDrawableManager.DEFAULT_MODE;
        }
        mutate.setColorFilter((ColorFilter)getPorterDuffColorFilter(n, default_MODE));
    }
    
    private Drawable tintDrawable(@NonNull final Context context, @DrawableRes final int n, final boolean b, @NonNull Drawable mutate) {
        final ColorStateList tintList = this.getTintList(context, n);
        Drawable drawable;
        if (tintList == null) {
            if (n != R.drawable.abc_seekbar_track_material) {
                if (n != R.drawable.abc_ratingbar_material && n != R.drawable.abc_ratingbar_indicator_material && n != R.drawable.abc_ratingbar_small_material) {
                    drawable = mutate;
                    if (!tintDrawableUsingColorFilter(context, n, mutate)) {
                        drawable = mutate;
                        if (b) {
                            drawable = null;
                        }
                    }
                }
                else {
                    final LayerDrawable layerDrawable = (LayerDrawable)mutate;
                    setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908288), ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), AppCompatDrawableManager.DEFAULT_MODE);
                    setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), AppCompatDrawableManager.DEFAULT_MODE);
                    setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), AppCompatDrawableManager.DEFAULT_MODE);
                    drawable = mutate;
                }
            }
            else {
                final LayerDrawable layerDrawable2 = (LayerDrawable)mutate;
                setPorterDuffColorFilter(layerDrawable2.findDrawableByLayerId(16908288), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), AppCompatDrawableManager.DEFAULT_MODE);
                setPorterDuffColorFilter(layerDrawable2.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), AppCompatDrawableManager.DEFAULT_MODE);
                setPorterDuffColorFilter(layerDrawable2.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), AppCompatDrawableManager.DEFAULT_MODE);
                drawable = mutate;
            }
        }
        else {
            if (DrawableUtils.canSafelyMutateDrawable(mutate)) {
                mutate = mutate.mutate();
            }
            final Drawable wrap = DrawableCompat.wrap(mutate);
            DrawableCompat.setTintList(wrap, tintList);
            final PorterDuff$Mode tintMode = getTintMode(n);
            drawable = wrap;
            if (tintMode != null) {
                DrawableCompat.setTintMode(wrap, tintMode);
                drawable = wrap;
            }
        }
        return drawable;
    }
    
    static void tintDrawable(final Drawable drawable, final TintInfo tintInfo, final int[] array) {
        if (DrawableUtils.canSafelyMutateDrawable(drawable) && drawable.mutate() != drawable) {
            Log.d("AppCompatDrawableManager", "Mutated drawable is not the same instance as the input.");
            return;
        }
        if (!tintInfo.mHasTintList && !tintInfo.mHasTintMode) {
            drawable.clearColorFilter();
        }
        else {
            ColorStateList mTintList;
            if (!tintInfo.mHasTintList) {
                mTintList = null;
            }
            else {
                mTintList = tintInfo.mTintList;
            }
            PorterDuff$Mode porterDuff$Mode;
            if (!tintInfo.mHasTintMode) {
                porterDuff$Mode = AppCompatDrawableManager.DEFAULT_MODE;
            }
            else {
                porterDuff$Mode = tintInfo.mTintMode;
            }
            drawable.setColorFilter((ColorFilter)createTintFilter(mTintList, porterDuff$Mode, array));
        }
        if (Build$VERSION.SDK_INT <= 23) {
            drawable.invalidateSelf();
        }
    }
    
    static boolean tintDrawableUsingColorFilter(@NonNull final Context context, @DrawableRes int round, @NonNull Drawable mutate) {
        PorterDuff$Mode porterDuff$Mode = AppCompatDrawableManager.DEFAULT_MODE;
        int n;
        int n2;
        if (!arrayContains(AppCompatDrawableManager.COLORFILTER_TINT_COLOR_CONTROL_NORMAL, round)) {
            if (!arrayContains(AppCompatDrawableManager.COLORFILTER_COLOR_CONTROL_ACTIVATED, round)) {
                if (!arrayContains(AppCompatDrawableManager.COLORFILTER_COLOR_BACKGROUND_MULTIPLY, round)) {
                    if (round != R.drawable.abc_list_divider_mtrl_alpha) {
                        if (round != R.drawable.abc_dialog_material_background) {
                            round = -1;
                            n = 0;
                            n2 = 0;
                        }
                        else {
                            n = 16842801;
                            n2 = 1;
                            round = -1;
                        }
                    }
                    else {
                        n = 16842800;
                        round = Math.round(40.8f);
                        n2 = 1;
                    }
                }
                else {
                    porterDuff$Mode = PorterDuff$Mode.MULTIPLY;
                    n2 = 1;
                    n = 16842801;
                    round = -1;
                }
            }
            else {
                n = R.attr.colorControlActivated;
                n2 = 1;
                round = -1;
            }
        }
        else {
            n = R.attr.colorControlNormal;
            n2 = 1;
            round = -1;
        }
        if (n2 == 0) {
            return false;
        }
        if (DrawableUtils.canSafelyMutateDrawable(mutate)) {
            mutate = mutate.mutate();
        }
        mutate.setColorFilter((ColorFilter)getPorterDuffColorFilter(ThemeUtils.getThemeAttrColor(context, n), porterDuff$Mode));
        if (round != -1) {
            mutate.setAlpha(round);
        }
        return true;
    }
    
    public Drawable getDrawable(@NonNull final Context context, @DrawableRes final int n) {
        return this.getDrawable(context, n, false);
    }
    
    Drawable getDrawable(@NonNull final Context context, @DrawableRes final int n, final boolean b) {
        this.checkVectorDrawableSetup(context);
        Drawable drawable = this.loadDrawableFromDelegates(context, n);
        if (drawable == null) {
            drawable = this.createDrawableIfNeeded(context, n);
        }
        if (drawable == null) {
            drawable = ContextCompat.getDrawable(context, n);
        }
        if (drawable != null) {
            drawable = this.tintDrawable(context, n, b, drawable);
        }
        if (drawable != null) {
            DrawableUtils.fixDrawable(drawable);
        }
        return drawable;
    }
    
    ColorStateList getTintList(@NonNull final Context context, @DrawableRes final int n) {
        ColorStateList list = this.getTintListFromCache(context, n);
        ColorStateList list2;
        if (list != null) {
            list2 = list;
        }
        else {
            if (n != R.drawable.abc_edit_text_material) {
                if (n != R.drawable.abc_switch_track_mtrl_alpha) {
                    if (n != R.drawable.abc_switch_thumb_material) {
                        if (n != R.drawable.abc_btn_default_mtrl_shape) {
                            if (n != R.drawable.abc_btn_borderless_material) {
                                if (n != R.drawable.abc_btn_colored_material) {
                                    if (n != R.drawable.abc_spinner_mtrl_am_alpha && n != R.drawable.abc_spinner_textfield_background_material) {
                                        if (!arrayContains(AppCompatDrawableManager.TINT_COLOR_CONTROL_NORMAL, n)) {
                                            if (!arrayContains(AppCompatDrawableManager.TINT_COLOR_CONTROL_STATE_LIST, n)) {
                                                if (!arrayContains(AppCompatDrawableManager.TINT_CHECKABLE_BUTTON_LIST, n)) {
                                                    if (n == R.drawable.abc_seekbar_thumb_material) {
                                                        list = AppCompatResources.getColorStateList(context, R.color.abc_tint_seek_thumb);
                                                    }
                                                }
                                                else {
                                                    list = AppCompatResources.getColorStateList(context, R.color.abc_tint_btn_checkable);
                                                }
                                            }
                                            else {
                                                list = AppCompatResources.getColorStateList(context, R.color.abc_tint_default);
                                            }
                                        }
                                        else {
                                            list = ThemeUtils.getThemeAttrColorStateList(context, R.attr.colorControlNormal);
                                        }
                                    }
                                    else {
                                        list = AppCompatResources.getColorStateList(context, R.color.abc_tint_spinner);
                                    }
                                }
                                else {
                                    list = this.createColoredButtonColorStateList(context);
                                }
                            }
                            else {
                                list = this.createBorderlessButtonColorStateList(context);
                            }
                        }
                        else {
                            list = this.createDefaultButtonColorStateList(context);
                        }
                    }
                    else {
                        list = this.createSwitchThumbColorStateList(context);
                    }
                }
                else {
                    list = AppCompatResources.getColorStateList(context, R.color.abc_tint_switch_track);
                }
            }
            else {
                list = AppCompatResources.getColorStateList(context, R.color.abc_tint_edittext);
            }
            if ((list2 = list) != null) {
                this.addTintListToCache(context, n, list);
                list2 = list;
            }
        }
        return list2;
    }
    
    public void onConfigurationChanged(@NonNull final Context key) {
        synchronized (this.mDrawableCacheLock) {
            final LongSparseArray longSparseArray = this.mDrawableCaches.get(key);
            if (longSparseArray != null) {
                longSparseArray.clear();
            }
        }
    }
    
    Drawable onDrawableLoadedFromResources(@NonNull final Context context, @NonNull final VectorEnabledTintResources vectorEnabledTintResources, @DrawableRes final int n) {
        final Drawable loadDrawableFromDelegates = this.loadDrawableFromDelegates(context, n);
        Drawable superGetDrawable;
        if (loadDrawableFromDelegates != null) {
            superGetDrawable = loadDrawableFromDelegates;
        }
        else {
            superGetDrawable = vectorEnabledTintResources.superGetDrawable(n);
        }
        if (superGetDrawable == null) {
            return null;
        }
        return this.tintDrawable(context, n, false, superGetDrawable);
    }
    
    @RequiresApi(11)
    private static class AvdcInflateDelegate implements InflateDelegate
    {
        AvdcInflateDelegate() {
        }
        
        @Override
        public Drawable createFromXmlInner(@NonNull final Context context, @NonNull final XmlPullParser xmlPullParser, @NonNull final AttributeSet set, @Nullable final Resources$Theme resources$Theme) {
            try {
                return AnimatedVectorDrawableCompat.createFromXmlInner(context, context.getResources(), xmlPullParser, set, resources$Theme);
            }
            catch (final Exception ex) {
                Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", (Throwable)ex);
                return null;
            }
        }
    }
    
    private interface InflateDelegate
    {
        Drawable createFromXmlInner(@NonNull final Context p0, @NonNull final XmlPullParser p1, @NonNull final AttributeSet p2, @Nullable final Resources$Theme p3);
    }
    
    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter>
    {
        public ColorFilterLruCache(final int n) {
            super(n);
        }
        
        private static int generateCacheKey(final int n, final PorterDuff$Mode porterDuff$Mode) {
            return (n + 31) * 31 + porterDuff$Mode.hashCode();
        }
        
        PorterDuffColorFilter get(final int n, final PorterDuff$Mode porterDuff$Mode) {
            return this.get(generateCacheKey(n, porterDuff$Mode));
        }
        
        PorterDuffColorFilter put(final int n, final PorterDuff$Mode porterDuff$Mode, final PorterDuffColorFilter porterDuffColorFilter) {
            return this.put(generateCacheKey(n, porterDuff$Mode), porterDuffColorFilter);
        }
    }
    
    private static class VdcInflateDelegate implements InflateDelegate
    {
        VdcInflateDelegate() {
        }
        
        @Override
        public Drawable createFromXmlInner(@NonNull final Context context, @NonNull final XmlPullParser xmlPullParser, @NonNull final AttributeSet set, @Nullable final Resources$Theme resources$Theme) {
            try {
                return VectorDrawableCompat.createFromXmlInner(context.getResources(), xmlPullParser, set, resources$Theme);
            }
            catch (final Exception ex) {
                Log.e("VdcInflateDelegate", "Exception while inflating <vector>", (Throwable)ex);
                return null;
            }
        }
    }
}
