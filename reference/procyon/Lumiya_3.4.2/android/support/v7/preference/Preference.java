// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.view.AbsSavedState;
import android.support.annotation.CallSuper;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.annotation.RestrictTo;
import android.content.SharedPreferences;
import java.util.Set;
import android.support.v4.content.ContextCompat;
import android.os.Parcelable;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.annotation.NonNull;
import android.content.SharedPreferences$Editor;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.text.TextUtils;
import android.content.res.TypedArray;
import android.view.View;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import java.util.List;
import android.content.Context;
import android.view.View$OnClickListener;

public class Preference implements Comparable<Preference>
{
    public static final int DEFAULT_ORDER = Integer.MAX_VALUE;
    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private boolean mBaseMethodCalled;
    private final View$OnClickListener mClickListener;
    private Context mContext;
    private Object mDefaultValue;
    private String mDependencyKey;
    private boolean mDependencyMet;
    private List<Preference> mDependents;
    private boolean mEnabled;
    private Bundle mExtras;
    private String mFragment;
    private boolean mHasId;
    private boolean mHasSingleLineTitleAttr;
    private Drawable mIcon;
    private int mIconResId;
    private boolean mIconSpaceReserved;
    private long mId;
    private Intent mIntent;
    private String mKey;
    private int mLayoutResId;
    private OnPreferenceChangeInternalListener mListener;
    private OnPreferenceChangeListener mOnChangeListener;
    private OnPreferenceClickListener mOnClickListener;
    private int mOrder;
    private boolean mParentDependencyMet;
    private PreferenceGroup mParentGroup;
    private boolean mPersistent;
    @Nullable
    private PreferenceDataStore mPreferenceDataStore;
    @Nullable
    private PreferenceManager mPreferenceManager;
    private boolean mRequiresKey;
    private boolean mSelectable;
    private boolean mShouldDisableView;
    private boolean mSingleLineTitle;
    private CharSequence mSummary;
    private CharSequence mTitle;
    private int mViewId;
    private boolean mVisible;
    private boolean mWasDetached;
    private int mWidgetLayoutResId;
    
    public Preference(final Context context) {
        this(context, null);
    }
    
    public Preference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, 16842894));
    }
    
    public Preference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public Preference(final Context mContext, final AttributeSet set, final int n, final int n2) {
        this.mOrder = Integer.MAX_VALUE;
        this.mViewId = 0;
        this.mEnabled = true;
        this.mSelectable = true;
        this.mPersistent = true;
        this.mDependencyMet = true;
        this.mParentDependencyMet = true;
        this.mVisible = true;
        this.mAllowDividerAbove = true;
        this.mAllowDividerBelow = true;
        this.mSingleLineTitle = true;
        this.mShouldDisableView = true;
        this.mLayoutResId = R.layout.preference;
        this.mClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                Preference.this.performClick(view);
            }
        };
        this.mContext = mContext;
        final TypedArray obtainStyledAttributes = mContext.obtainStyledAttributes(set, R.styleable.Preference, n, n2);
        this.mIconResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R.styleable.Preference_icon, R.styleable.Preference_android_icon, 0);
        this.mKey = TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.Preference_key, R.styleable.Preference_android_key);
        this.mTitle = TypedArrayUtils.getText(obtainStyledAttributes, R.styleable.Preference_title, R.styleable.Preference_android_title);
        this.mSummary = TypedArrayUtils.getText(obtainStyledAttributes, R.styleable.Preference_summary, R.styleable.Preference_android_summary);
        this.mOrder = TypedArrayUtils.getInt(obtainStyledAttributes, R.styleable.Preference_order, R.styleable.Preference_android_order, Integer.MAX_VALUE);
        this.mFragment = TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.Preference_fragment, R.styleable.Preference_android_fragment);
        this.mLayoutResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R.styleable.Preference_layout, R.styleable.Preference_android_layout, R.layout.preference);
        this.mWidgetLayoutResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R.styleable.Preference_widgetLayout, R.styleable.Preference_android_widgetLayout, 0);
        this.mEnabled = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_enabled, R.styleable.Preference_android_enabled, true);
        this.mSelectable = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_selectable, R.styleable.Preference_android_selectable, true);
        this.mPersistent = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_persistent, R.styleable.Preference_android_persistent, true);
        this.mDependencyKey = TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.Preference_dependency, R.styleable.Preference_android_dependency);
        this.mAllowDividerAbove = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_allowDividerAbove, R.styleable.Preference_allowDividerAbove, this.mSelectable);
        this.mAllowDividerBelow = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_allowDividerBelow, R.styleable.Preference_allowDividerBelow, this.mSelectable);
        if (!obtainStyledAttributes.hasValue(R.styleable.Preference_defaultValue)) {
            if (obtainStyledAttributes.hasValue(R.styleable.Preference_android_defaultValue)) {
                this.mDefaultValue = this.onGetDefaultValue(obtainStyledAttributes, R.styleable.Preference_android_defaultValue);
            }
        }
        else {
            this.mDefaultValue = this.onGetDefaultValue(obtainStyledAttributes, R.styleable.Preference_defaultValue);
        }
        this.mShouldDisableView = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_shouldDisableView, R.styleable.Preference_android_shouldDisableView, true);
        if (this.mHasSingleLineTitleAttr = obtainStyledAttributes.hasValue(R.styleable.Preference_singleLineTitle)) {
            this.mSingleLineTitle = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_singleLineTitle, R.styleable.Preference_android_singleLineTitle, true);
        }
        this.mIconSpaceReserved = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.Preference_iconSpaceReserved, R.styleable.Preference_android_iconSpaceReserved, false);
        obtainStyledAttributes.recycle();
    }
    
    private void dispatchSetInitialValue() {
        if (this.getPreferenceDataStore() == null) {
            if (this.shouldPersist() && this.getSharedPreferences().contains(this.mKey)) {
                this.onSetInitialValue(true, null);
            }
            else if (this.mDefaultValue != null) {
                this.onSetInitialValue(false, this.mDefaultValue);
            }
            return;
        }
        this.onSetInitialValue(true, this.mDefaultValue);
    }
    
    private void registerDependency() {
        if (TextUtils.isEmpty((CharSequence)this.mDependencyKey)) {
            return;
        }
        final Preference preferenceInHierarchy = this.findPreferenceInHierarchy(this.mDependencyKey);
        if (preferenceInHierarchy == null) {
            throw new IllegalStateException("Dependency \"" + this.mDependencyKey + "\" not found for preference \"" + this.mKey + "\" (title: \"" + (Object)this.mTitle + "\"");
        }
        preferenceInHierarchy.registerDependent(this);
    }
    
    private void registerDependent(final Preference preference) {
        if (this.mDependents == null) {
            this.mDependents = new ArrayList<Preference>();
        }
        this.mDependents.add(preference);
        preference.onDependencyChanged(this, this.shouldDisableDependents());
    }
    
    private void setEnabledStateOnViews(final View view, final boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            int childCount = viewGroup.getChildCount();
            while (--childCount >= 0) {
                this.setEnabledStateOnViews(viewGroup.getChildAt(childCount), enabled);
            }
        }
    }
    
    private void tryCommit(@NonNull final SharedPreferences$Editor sharedPreferences$Editor) {
        if (this.mPreferenceManager.shouldCommit()) {
            SharedPreferencesCompat.EditorCompat.getInstance().apply(sharedPreferences$Editor);
        }
    }
    
    private void unregisterDependency() {
        if (this.mDependencyKey != null) {
            final Preference preferenceInHierarchy = this.findPreferenceInHierarchy(this.mDependencyKey);
            if (preferenceInHierarchy != null) {
                preferenceInHierarchy.unregisterDependent(this);
            }
        }
    }
    
    private void unregisterDependent(final Preference preference) {
        if (this.mDependents != null) {
            this.mDependents.remove(preference);
        }
    }
    
    void assignParent(@Nullable final PreferenceGroup mParentGroup) {
        this.mParentGroup = mParentGroup;
    }
    
    public boolean callChangeListener(final Object o) {
        boolean b = false;
        if (this.mOnChangeListener == null || this.mOnChangeListener.onPreferenceChange(this, o)) {
            b = true;
        }
        return b;
    }
    
    public final void clearWasDetached() {
        this.mWasDetached = false;
    }
    
    @Override
    public int compareTo(@NonNull final Preference preference) {
        if (this.mOrder != preference.mOrder) {
            return this.mOrder - preference.mOrder;
        }
        if (this.mTitle == preference.mTitle) {
            return 0;
        }
        if (this.mTitle == null) {
            return 1;
        }
        if (preference.mTitle != null) {
            return this.mTitle.toString().compareToIgnoreCase(preference.mTitle.toString());
        }
        return -1;
    }
    
    void dispatchRestoreInstanceState(final Bundle bundle) {
        if (this.hasKey()) {
            final Parcelable parcelable = bundle.getParcelable(this.mKey);
            if (parcelable != null) {
                this.mBaseMethodCalled = false;
                this.onRestoreInstanceState(parcelable);
                if (!this.mBaseMethodCalled) {
                    throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
                }
            }
        }
    }
    
    void dispatchSaveInstanceState(final Bundle bundle) {
        if (this.hasKey()) {
            this.mBaseMethodCalled = false;
            final Parcelable onSaveInstanceState = this.onSaveInstanceState();
            if (!this.mBaseMethodCalled) {
                throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
            }
            if (onSaveInstanceState != null) {
                bundle.putParcelable(this.mKey, onSaveInstanceState);
            }
        }
    }
    
    protected Preference findPreferenceInHierarchy(final String s) {
        if (!TextUtils.isEmpty((CharSequence)s) && this.mPreferenceManager != null) {
            return this.mPreferenceManager.findPreference(s);
        }
        return null;
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    public String getDependency() {
        return this.mDependencyKey;
    }
    
    public Bundle getExtras() {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        return this.mExtras;
    }
    
    StringBuilder getFilterableStringBuilder() {
        final StringBuilder sb = new StringBuilder();
        final CharSequence title = this.getTitle();
        if (!TextUtils.isEmpty(title)) {
            sb.append(title).append(' ');
        }
        final CharSequence summary = this.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            sb.append(summary).append(' ');
        }
        if (sb.length() > 0) {
            sb.setLength();
        }
        return sb;
    }
    
    public String getFragment() {
        return this.mFragment;
    }
    
    public Drawable getIcon() {
        if (this.mIcon == null && this.mIconResId != 0) {
            this.mIcon = ContextCompat.getDrawable(this.mContext, this.mIconResId);
        }
        return this.mIcon;
    }
    
    long getId() {
        return this.mId;
    }
    
    public Intent getIntent() {
        return this.mIntent;
    }
    
    public String getKey() {
        return this.mKey;
    }
    
    public final int getLayoutResource() {
        return this.mLayoutResId;
    }
    
    public OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return this.mOnChangeListener;
    }
    
    public OnPreferenceClickListener getOnPreferenceClickListener() {
        return this.mOnClickListener;
    }
    
    public int getOrder() {
        return this.mOrder;
    }
    
    @Nullable
    public PreferenceGroup getParent() {
        return this.mParentGroup;
    }
    
    protected boolean getPersistedBoolean(final boolean b) {
        if (!this.shouldPersist()) {
            return b;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            return this.mPreferenceManager.getSharedPreferences().getBoolean(this.mKey, b);
        }
        return preferenceDataStore.getBoolean(this.mKey, b);
    }
    
    protected float getPersistedFloat(final float n) {
        if (!this.shouldPersist()) {
            return n;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            return this.mPreferenceManager.getSharedPreferences().getFloat(this.mKey, n);
        }
        return preferenceDataStore.getFloat(this.mKey, n);
    }
    
    protected int getPersistedInt(final int n) {
        if (!this.shouldPersist()) {
            return n;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            return this.mPreferenceManager.getSharedPreferences().getInt(this.mKey, n);
        }
        return preferenceDataStore.getInt(this.mKey, n);
    }
    
    protected long getPersistedLong(final long n) {
        if (!this.shouldPersist()) {
            return n;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            return this.mPreferenceManager.getSharedPreferences().getLong(this.mKey, n);
        }
        return preferenceDataStore.getLong(this.mKey, n);
    }
    
    protected String getPersistedString(final String s) {
        if (!this.shouldPersist()) {
            return s;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            return this.mPreferenceManager.getSharedPreferences().getString(this.mKey, s);
        }
        return preferenceDataStore.getString(this.mKey, s);
    }
    
    public Set<String> getPersistedStringSet(final Set<String> set) {
        if (!this.shouldPersist()) {
            return set;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            return this.mPreferenceManager.getSharedPreferences().getStringSet(this.mKey, (Set)set);
        }
        return preferenceDataStore.getStringSet(this.mKey, set);
    }
    
    @Nullable
    public PreferenceDataStore getPreferenceDataStore() {
        if (this.mPreferenceDataStore != null) {
            return this.mPreferenceDataStore;
        }
        if (this.mPreferenceManager == null) {
            return null;
        }
        return this.mPreferenceManager.getPreferenceDataStore();
    }
    
    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }
    
    public SharedPreferences getSharedPreferences() {
        if (this.mPreferenceManager != null && this.getPreferenceDataStore() == null) {
            return this.mPreferenceManager.getSharedPreferences();
        }
        return null;
    }
    
    public boolean getShouldDisableView() {
        return this.mShouldDisableView;
    }
    
    public CharSequence getSummary() {
        return this.mSummary;
    }
    
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    public final int getWidgetLayoutResource() {
        return this.mWidgetLayoutResId;
    }
    
    public boolean hasKey() {
        boolean b = false;
        if (!TextUtils.isEmpty((CharSequence)this.mKey)) {
            b = true;
        }
        return b;
    }
    
    public boolean isEnabled() {
        final boolean b = false;
        boolean b2;
        if (!this.mEnabled) {
            b2 = b;
        }
        else {
            b2 = b;
            if (this.mDependencyMet) {
                b2 = b;
                if (this.mParentDependencyMet) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public boolean isIconSpaceReserved() {
        return this.mIconSpaceReserved;
    }
    
    public boolean isPersistent() {
        return this.mPersistent;
    }
    
    public boolean isSelectable() {
        return this.mSelectable;
    }
    
    public boolean isSingleLineTitle() {
        return this.mSingleLineTitle;
    }
    
    public final boolean isVisible() {
        return this.mVisible;
    }
    
    protected void notifyChanged() {
        if (this.mListener != null) {
            this.mListener.onPreferenceChange(this);
        }
    }
    
    public void notifyDependencyChange(final boolean b) {
        final List<Preference> mDependents = this.mDependents;
        if (mDependents != null) {
            for (int size = mDependents.size(), i = 0; i < size; ++i) {
                ((Preference)mDependents.get(i)).onDependencyChanged(this, b);
            }
        }
    }
    
    protected void notifyHierarchyChanged() {
        if (this.mListener != null) {
            this.mListener.onPreferenceHierarchyChange(this);
        }
    }
    
    public void onAttached() {
        this.registerDependency();
    }
    
    protected void onAttachedToHierarchy(final PreferenceManager mPreferenceManager) {
        this.mPreferenceManager = mPreferenceManager;
        if (!this.mHasId) {
            this.mId = mPreferenceManager.getNextId();
        }
        this.dispatchSetInitialValue();
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void onAttachedToHierarchy(final PreferenceManager preferenceManager, final long mId) {
        this.mId = mId;
        this.mHasId = true;
        try {
            this.onAttachedToHierarchy(preferenceManager);
        }
        finally {
            this.mHasId = false;
        }
    }
    
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        final int n = 8;
        preferenceViewHolder.itemView.setOnClickListener(this.mClickListener);
        preferenceViewHolder.itemView.setId(this.mViewId);
        final TextView textView = (TextView)preferenceViewHolder.findViewById(16908310);
        if (textView != null) {
            final CharSequence title = this.getTitle();
            if (TextUtils.isEmpty(title)) {
                textView.setVisibility(8);
            }
            else {
                textView.setText(title);
                textView.setVisibility(0);
                if (this.mHasSingleLineTitleAttr) {
                    textView.setSingleLine(this.mSingleLineTitle);
                }
            }
        }
        final TextView textView2 = (TextView)preferenceViewHolder.findViewById(16908304);
        if (textView2 != null) {
            final CharSequence summary = this.getSummary();
            if (TextUtils.isEmpty(summary)) {
                textView2.setVisibility(8);
            }
            else {
                textView2.setText(summary);
                textView2.setVisibility(0);
            }
        }
        final ImageView imageView = (ImageView)preferenceViewHolder.findViewById(16908294);
        if (imageView != null) {
            if (this.mIconResId != 0 || this.mIcon != null) {
                if (this.mIcon == null) {
                    this.mIcon = ContextCompat.getDrawable(this.getContext(), this.mIconResId);
                }
                if (this.mIcon != null) {
                    imageView.setImageDrawable(this.mIcon);
                }
            }
            if (this.mIcon == null) {
                int visibility;
                if (!this.mIconSpaceReserved) {
                    visibility = 8;
                }
                else {
                    visibility = 4;
                }
                imageView.setVisibility(visibility);
            }
            else {
                imageView.setVisibility(0);
            }
        }
        View view = preferenceViewHolder.findViewById(R.id.icon_frame);
        if (view == null) {
            view = preferenceViewHolder.findViewById(16908350);
        }
        if (view != null) {
            if (this.mIcon == null) {
                int visibility2;
                if (!this.mIconSpaceReserved) {
                    visibility2 = n;
                }
                else {
                    visibility2 = 4;
                }
                view.setVisibility(visibility2);
            }
            else {
                view.setVisibility(0);
            }
        }
        if (!this.mShouldDisableView) {
            this.setEnabledStateOnViews(preferenceViewHolder.itemView, true);
        }
        else {
            this.setEnabledStateOnViews(preferenceViewHolder.itemView, this.isEnabled());
        }
        final boolean selectable = this.isSelectable();
        preferenceViewHolder.itemView.setFocusable(selectable);
        preferenceViewHolder.itemView.setClickable(selectable);
        preferenceViewHolder.setDividerAllowedAbove(this.mAllowDividerAbove);
        preferenceViewHolder.setDividerAllowedBelow(this.mAllowDividerBelow);
    }
    
    protected void onClick() {
    }
    
    public void onDependencyChanged(final Preference preference, final boolean b) {
        final boolean b2 = false;
        if (this.mDependencyMet == b) {
            this.mDependencyMet = (!b || b2);
            this.notifyDependencyChange(this.shouldDisableDependents());
            this.notifyChanged();
        }
    }
    
    public void onDetached() {
        this.unregisterDependency();
        this.mWasDetached = true;
    }
    
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return null;
    }
    
    @CallSuper
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
    }
    
    public void onParentChanged(final Preference preference, final boolean b) {
        final boolean b2 = false;
        if (this.mParentDependencyMet == b) {
            this.mParentDependencyMet = (!b || b2);
            this.notifyDependencyChange(this.shouldDisableDependents());
            this.notifyChanged();
        }
    }
    
    protected void onPrepareForRemoval() {
        this.unregisterDependency();
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        this.mBaseMethodCalled = true;
        if (parcelable != BaseSavedState.EMPTY_STATE && parcelable != null) {
            throw new IllegalArgumentException("Wrong state class -- expecting Preference State");
        }
    }
    
    protected Parcelable onSaveInstanceState() {
        this.mBaseMethodCalled = true;
        return (Parcelable)BaseSavedState.EMPTY_STATE;
    }
    
    protected void onSetInitialValue(final boolean b, final Object o) {
    }
    
    public Bundle peekExtras() {
        return this.mExtras;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void performClick() {
        if (!this.isEnabled()) {
            return;
        }
        this.onClick();
        if (this.mOnClickListener != null && this.mOnClickListener.onPreferenceClick(this)) {
            return;
        }
        final PreferenceManager preferenceManager = this.getPreferenceManager();
        if (preferenceManager != null) {
            final PreferenceManager.OnPreferenceTreeClickListener onPreferenceTreeClickListener = preferenceManager.getOnPreferenceTreeClickListener();
            if (onPreferenceTreeClickListener != null && onPreferenceTreeClickListener.onPreferenceTreeClick(this)) {
                return;
            }
        }
        if (this.mIntent != null) {
            this.getContext().startActivity(this.mIntent);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void performClick(final View view) {
        this.performClick();
    }
    
    protected boolean persistBoolean(final boolean b) {
        boolean b2 = false;
        if (!this.shouldPersist()) {
            return false;
        }
        if (!b) {
            b2 = true;
        }
        if (b != this.getPersistedBoolean(b2)) {
            final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
            if (preferenceDataStore == null) {
                final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
                editor.putBoolean(this.mKey, b);
                this.tryCommit(editor);
            }
            else {
                preferenceDataStore.putBoolean(this.mKey, b);
            }
            return true;
        }
        return true;
    }
    
    protected boolean persistFloat(final float n) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (n == this.getPersistedFloat(Float.NaN)) {
            return true;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
            editor.putFloat(this.mKey, n);
            this.tryCommit(editor);
        }
        else {
            preferenceDataStore.putFloat(this.mKey, n);
        }
        return true;
    }
    
    protected boolean persistInt(final int n) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (n != this.getPersistedInt(~n)) {
            final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
            if (preferenceDataStore == null) {
                final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
                editor.putInt(this.mKey, n);
                this.tryCommit(editor);
            }
            else {
                preferenceDataStore.putInt(this.mKey, n);
            }
            return true;
        }
        return true;
    }
    
    protected boolean persistLong(final long n) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (n == this.getPersistedLong(-1L ^ n)) {
            return true;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore == null) {
            final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
            editor.putLong(this.mKey, n);
            this.tryCommit(editor);
        }
        else {
            preferenceDataStore.putLong(this.mKey, n);
        }
        return true;
    }
    
    protected boolean persistString(final String s) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (!TextUtils.equals((CharSequence)s, (CharSequence)this.getPersistedString(null))) {
            final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
            if (preferenceDataStore == null) {
                final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
                editor.putString(this.mKey, s);
                this.tryCommit(editor);
            }
            else {
                preferenceDataStore.putString(this.mKey, s);
            }
            return true;
        }
        return true;
    }
    
    public boolean persistStringSet(final Set<String> set) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (!set.equals(this.getPersistedStringSet(null))) {
            final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
            if (preferenceDataStore == null) {
                final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
                editor.putStringSet(this.mKey, (Set)set);
                this.tryCommit(editor);
            }
            else {
                preferenceDataStore.putStringSet(this.mKey, set);
            }
            return true;
        }
        return true;
    }
    
    void requireKey() {
        if (!TextUtils.isEmpty((CharSequence)this.mKey)) {
            this.mRequiresKey = true;
            return;
        }
        throw new IllegalStateException("Preference does not have a key assigned.");
    }
    
    public void restoreHierarchyState(final Bundle bundle) {
        this.dispatchRestoreInstanceState(bundle);
    }
    
    public void saveHierarchyState(final Bundle bundle) {
        this.dispatchSaveInstanceState(bundle);
    }
    
    public void setDefaultValue(final Object mDefaultValue) {
        this.mDefaultValue = mDefaultValue;
    }
    
    public void setDependency(final String mDependencyKey) {
        this.unregisterDependency();
        this.mDependencyKey = mDependencyKey;
        this.registerDependency();
    }
    
    public void setEnabled(final boolean mEnabled) {
        if (this.mEnabled != mEnabled) {
            this.mEnabled = mEnabled;
            this.notifyDependencyChange(this.shouldDisableDependents());
            this.notifyChanged();
        }
    }
    
    public void setFragment(final String mFragment) {
        this.mFragment = mFragment;
    }
    
    public void setIcon(final int mIconResId) {
        this.setIcon(ContextCompat.getDrawable(this.mContext, mIconResId));
        this.mIconResId = mIconResId;
    }
    
    public void setIcon(final Drawable mIcon) {
        Label_0016: {
            if (mIcon == null && this.mIcon != null) {
                break Label_0016;
            }
            if (mIcon != null) {
                if (this.mIcon != mIcon) {
                    break Label_0016;
                }
            }
            return;
        }
        this.mIcon = mIcon;
        this.mIconResId = 0;
        this.notifyChanged();
    }
    
    public void setIconSpaceReserved(final boolean mIconSpaceReserved) {
        this.mIconSpaceReserved = mIconSpaceReserved;
        this.notifyChanged();
    }
    
    public void setIntent(final Intent mIntent) {
        this.mIntent = mIntent;
    }
    
    public void setKey(final String mKey) {
        this.mKey = mKey;
        if (this.mRequiresKey && !this.hasKey()) {
            this.requireKey();
        }
    }
    
    public void setLayoutResource(final int mLayoutResId) {
        this.mLayoutResId = mLayoutResId;
    }
    
    final void setOnPreferenceChangeInternalListener(final OnPreferenceChangeInternalListener mListener) {
        this.mListener = mListener;
    }
    
    public void setOnPreferenceChangeListener(final OnPreferenceChangeListener mOnChangeListener) {
        this.mOnChangeListener = mOnChangeListener;
    }
    
    public void setOnPreferenceClickListener(final OnPreferenceClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
    
    public void setOrder(final int mOrder) {
        if (mOrder != this.mOrder) {
            this.mOrder = mOrder;
            this.notifyHierarchyChanged();
        }
    }
    
    public void setPersistent(final boolean mPersistent) {
        this.mPersistent = mPersistent;
    }
    
    public void setPreferenceDataStore(final PreferenceDataStore mPreferenceDataStore) {
        this.mPreferenceDataStore = mPreferenceDataStore;
    }
    
    public void setSelectable(final boolean mSelectable) {
        if (this.mSelectable != mSelectable) {
            this.mSelectable = mSelectable;
            this.notifyChanged();
        }
    }
    
    public void setShouldDisableView(final boolean mShouldDisableView) {
        this.mShouldDisableView = mShouldDisableView;
        this.notifyChanged();
    }
    
    public void setSingleLineTitle(final boolean mSingleLineTitle) {
        this.mHasSingleLineTitleAttr = true;
        this.mSingleLineTitle = mSingleLineTitle;
    }
    
    public void setSummary(final int n) {
        this.setSummary(this.mContext.getString(n));
    }
    
    public void setSummary(final CharSequence mSummary) {
        Label_0016: {
            if (mSummary == null && this.mSummary != null) {
                break Label_0016;
            }
            if (mSummary != null) {
                if (!mSummary.equals(this.mSummary)) {
                    break Label_0016;
                }
            }
            return;
        }
        this.mSummary = mSummary;
        this.notifyChanged();
    }
    
    public void setTitle(final int n) {
        this.setTitle(this.mContext.getString(n));
    }
    
    public void setTitle(final CharSequence mTitle) {
        Label_0016: {
            if (mTitle == null && this.mTitle != null) {
                break Label_0016;
            }
            if (mTitle != null) {
                if (!mTitle.equals(this.mTitle)) {
                    break Label_0016;
                }
            }
            return;
        }
        this.mTitle = mTitle;
        this.notifyChanged();
    }
    
    public void setViewId(final int mViewId) {
        this.mViewId = mViewId;
    }
    
    public final void setVisible(final boolean mVisible) {
        if (this.mVisible != mVisible) {
            this.mVisible = mVisible;
            if (this.mListener != null) {
                this.mListener.onPreferenceVisibilityChange(this);
            }
        }
    }
    
    public void setWidgetLayoutResource(final int mWidgetLayoutResId) {
        this.mWidgetLayoutResId = mWidgetLayoutResId;
    }
    
    public boolean shouldDisableDependents() {
        boolean b = false;
        if (!this.isEnabled()) {
            b = true;
        }
        return b;
    }
    
    protected boolean shouldPersist() {
        final boolean b = false;
        boolean b2;
        if (this.mPreferenceManager == null) {
            b2 = b;
        }
        else {
            b2 = b;
            if (this.isPersistent()) {
                b2 = b;
                if (this.hasKey()) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    @Override
    public String toString() {
        return this.getFilterableStringBuilder().toString();
    }
    
    public final boolean wasDetached() {
        return this.mWasDetached;
    }
    
    public static class BaseSavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<BaseSavedState> CREATOR;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<BaseSavedState>() {
                public BaseSavedState createFromParcel(final Parcel parcel) {
                    return new BaseSavedState(parcel);
                }
                
                public BaseSavedState[] newArray(final int n) {
                    return new BaseSavedState[n];
                }
            };
        }
        
        public BaseSavedState(final Parcel parcel) {
            super(parcel);
        }
        
        public BaseSavedState(final Parcelable parcelable) {
            super(parcelable);
        }
    }
    
    interface OnPreferenceChangeInternalListener
    {
        void onPreferenceChange(final Preference p0);
        
        void onPreferenceHierarchyChange(final Preference p0);
        
        void onPreferenceVisibilityChange(final Preference p0);
    }
    
    public interface OnPreferenceChangeListener
    {
        boolean onPreferenceChange(final Preference p0, final Object p1);
    }
    
    public interface OnPreferenceClickListener
    {
        boolean onPreferenceClick(final Preference p0);
    }
}
