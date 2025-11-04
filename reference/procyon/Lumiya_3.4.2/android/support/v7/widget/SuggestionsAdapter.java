// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.net.Uri$Builder;
import android.view.ViewGroup;
import java.util.List;
import android.content.res.Resources;
import java.io.FileNotFoundException;
import android.view.View;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.res.Resources$NotFoundException;
import android.support.v4.content.ContextCompat;
import android.net.Uri;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.content.ComponentName;
import android.text.style.TextAppearanceSpan;
import android.text.SpannableString;
import android.support.v7.appcompat.R;
import android.util.TypedValue;
import android.graphics.drawable.Drawable;
import android.database.Cursor;
import android.content.res.ColorStateList;
import android.app.SearchableInfo;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable$ConstantState;
import java.util.WeakHashMap;
import android.view.View$OnClickListener;
import android.support.v4.widget.ResourceCursorAdapter;

class SuggestionsAdapter extends ResourceCursorAdapter implements View$OnClickListener
{
    private static final boolean DBG = false;
    static final int INVALID_INDEX = -1;
    private static final String LOG_TAG = "SuggestionsAdapter";
    private static final int QUERY_LIMIT = 50;
    static final int REFINE_ALL = 2;
    static final int REFINE_BY_ENTRY = 1;
    static final int REFINE_NONE = 0;
    private boolean mClosed;
    private final int mCommitIconResId;
    private int mFlagsCol;
    private int mIconName1Col;
    private int mIconName2Col;
    private final WeakHashMap<String, Drawable$ConstantState> mOutsideDrawablesCache;
    private final Context mProviderContext;
    private int mQueryRefinement;
    private final SearchManager mSearchManager;
    private final SearchView mSearchView;
    private final SearchableInfo mSearchable;
    private int mText1Col;
    private int mText2Col;
    private int mText2UrlCol;
    private ColorStateList mUrlColor;
    
    public SuggestionsAdapter(final Context mProviderContext, final SearchView mSearchView, final SearchableInfo mSearchable, final WeakHashMap<String, Drawable$ConstantState> mOutsideDrawablesCache) {
        super(mProviderContext, mSearchView.getSuggestionRowLayout(), null, true);
        this.mClosed = false;
        this.mQueryRefinement = 1;
        this.mText1Col = -1;
        this.mText2Col = -1;
        this.mText2UrlCol = -1;
        this.mIconName1Col = -1;
        this.mIconName2Col = -1;
        this.mFlagsCol = -1;
        this.mSearchManager = (SearchManager)this.mContext.getSystemService("search");
        this.mSearchView = mSearchView;
        this.mSearchable = mSearchable;
        this.mCommitIconResId = mSearchView.getSuggestionCommitIconResId();
        this.mProviderContext = mProviderContext;
        this.mOutsideDrawablesCache = mOutsideDrawablesCache;
    }
    
    private Drawable checkIconCache(final String key) {
        final Drawable$ConstantState drawable$ConstantState = this.mOutsideDrawablesCache.get(key);
        if (drawable$ConstantState != null) {
            return drawable$ConstantState.newDrawable();
        }
        return null;
    }
    
    private CharSequence formatUrl(final CharSequence charSequence) {
        if (this.mUrlColor == null) {
            final TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(R.attr.textColorSearchUrl, typedValue, true);
            this.mUrlColor = this.mContext.getResources().getColorStateList(typedValue.resourceId);
        }
        final SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan((Object)new TextAppearanceSpan((String)null, 0, 0, this.mUrlColor, (ColorStateList)null), 0, charSequence.length(), 33);
        return (CharSequence)spannableString;
    }
    
    private Drawable getActivityIcon(final ComponentName componentName) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        int iconResource = 0;
        Label_0064: {
            try {
                final ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 128);
                iconResource = activityInfo.getIconResource();
                if (iconResource != 0) {
                    final Drawable drawable = packageManager.getDrawable(componentName.getPackageName(), iconResource, activityInfo.applicationInfo);
                    if (drawable != null) {
                        return drawable;
                    }
                    break Label_0064;
                }
            }
            catch (final PackageManager$NameNotFoundException ex) {
                Log.w("SuggestionsAdapter", ex.toString());
                return null;
            }
            return null;
        }
        Log.w("SuggestionsAdapter", "Invalid icon resource " + iconResource + " for " + componentName.flattenToShortString());
        return null;
    }
    
    private Drawable getActivityIconWithCache(final ComponentName componentName) {
        final Drawable$ConstantState drawable$ConstantState = null;
        final String flattenToShortString = componentName.flattenToShortString();
        if (!this.mOutsideDrawablesCache.containsKey(flattenToShortString)) {
            final Drawable activityIcon = this.getActivityIcon(componentName);
            Drawable$ConstantState constantState = drawable$ConstantState;
            if (activityIcon != null) {
                constantState = activityIcon.getConstantState();
            }
            this.mOutsideDrawablesCache.put(flattenToShortString, constantState);
            return activityIcon;
        }
        final Drawable$ConstantState drawable$ConstantState2 = this.mOutsideDrawablesCache.get(flattenToShortString);
        Drawable drawable;
        if (drawable$ConstantState2 != null) {
            drawable = drawable$ConstantState2.newDrawable(this.mProviderContext.getResources());
        }
        else {
            drawable = null;
        }
        return drawable;
    }
    
    public static String getColumnString(final Cursor cursor, final String s) {
        return getStringOrNull(cursor, cursor.getColumnIndex(s));
    }
    
    private Drawable getDefaultIcon1(final Cursor cursor) {
        final Drawable activityIconWithCache = this.getActivityIconWithCache(this.mSearchable.getSearchActivity());
        if (activityIconWithCache == null) {
            return this.mContext.getPackageManager().getDefaultActivityIcon();
        }
        return activityIconWithCache;
    }
    
    private Drawable getDrawable(final Uri p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: aload_1        
        //     4: invokevirtual   android/net/Uri.getScheme:()Ljava/lang/String;
        //     7: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    10: ifne            41
        //    13: aload_0        
        //    14: getfield        android/support/v7/widget/SuggestionsAdapter.mProviderContext:Landroid/content/Context;
        //    17: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    20: aload_1        
        //    21: invokevirtual   android/content/ContentResolver.openInputStream:(Landroid/net/Uri;)Ljava/io/InputStream;
        //    24: astore_2       
        //    25: aload_2        
        //    26: ifnull          124
        //    29: aload_2        
        //    30: aconst_null    
        //    31: invokestatic    android/graphics/drawable/Drawable.createFromStream:(Ljava/io/InputStream;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
        //    34: astore_3       
        //    35: aload_2        
        //    36: invokevirtual   java/io/InputStream.close:()V
        //    39: aload_3        
        //    40: areturn        
        //    41: aload_0        
        //    42: aload_1        
        //    43: invokevirtual   android/support/v7/widget/SuggestionsAdapter.getDrawableFromResourceUri:(Landroid/net/Uri;)Landroid/graphics/drawable/Drawable;
        //    46: astore_3       
        //    47: aload_3        
        //    48: areturn        
        //    49: astore_3       
        //    50: new             Ljava/io/FileNotFoundException;
        //    53: astore_2       
        //    54: new             Ljava/lang/StringBuilder;
        //    57: astore_3       
        //    58: aload_3        
        //    59: invokespecial   java/lang/StringBuilder.<init>:()V
        //    62: aload_2        
        //    63: aload_3        
        //    64: ldc_w           "Resource does not exist: "
        //    67: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    70: aload_1        
        //    71: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    74: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    77: invokespecial   java/io/FileNotFoundException.<init>:(Ljava/lang/String;)V
        //    80: aload_2        
        //    81: athrow         
        //    82: astore_3       
        //    83: ldc             "SuggestionsAdapter"
        //    85: new             Ljava/lang/StringBuilder;
        //    88: dup            
        //    89: invokespecial   java/lang/StringBuilder.<init>:()V
        //    92: ldc_w           "Icon not found: "
        //    95: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    98: aload_1        
        //    99: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   102: ldc_w           ", "
        //   105: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   108: aload_3        
        //   109: invokevirtual   java/io/FileNotFoundException.getMessage:()Ljava/lang/String;
        //   112: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   115: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   118: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //   121: pop            
        //   122: aconst_null    
        //   123: areturn        
        //   124: new             Ljava/io/FileNotFoundException;
        //   127: astore_2       
        //   128: new             Ljava/lang/StringBuilder;
        //   131: astore_3       
        //   132: aload_3        
        //   133: invokespecial   java/lang/StringBuilder.<init>:()V
        //   136: aload_2        
        //   137: aload_3        
        //   138: ldc_w           "Failed to open "
        //   141: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   144: aload_1        
        //   145: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   148: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   151: invokespecial   java/io/FileNotFoundException.<init>:(Ljava/lang/String;)V
        //   154: aload_2        
        //   155: athrow         
        //   156: astore_2       
        //   157: new             Ljava/lang/StringBuilder;
        //   160: astore          4
        //   162: aload           4
        //   164: invokespecial   java/lang/StringBuilder.<init>:()V
        //   167: ldc             "SuggestionsAdapter"
        //   169: aload           4
        //   171: ldc_w           "Error closing icon stream for "
        //   174: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   177: aload_1        
        //   178: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   181: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   184: aload_2        
        //   185: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   188: pop            
        //   189: goto            39
        //   192: astore_3       
        //   193: aload_2        
        //   194: invokevirtual   java/io/InputStream.close:()V
        //   197: aload_3        
        //   198: athrow         
        //   199: astore_2       
        //   200: new             Ljava/lang/StringBuilder;
        //   203: astore          4
        //   205: aload           4
        //   207: invokespecial   java/lang/StringBuilder.<init>:()V
        //   210: ldc             "SuggestionsAdapter"
        //   212: aload           4
        //   214: ldc_w           "Error closing icon stream for "
        //   217: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   220: aload_1        
        //   221: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   224: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   227: aload_2        
        //   228: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   231: pop            
        //   232: goto            197
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                             
        //  -----  -----  -----  -----  -------------------------------------------------
        //  0      25     82     124    Ljava/io/FileNotFoundException;
        //  29     35     192    235    Any
        //  35     39     156    192    Ljava/io/IOException;
        //  35     39     82     124    Ljava/io/FileNotFoundException;
        //  41     47     49     82     Landroid/content/res/Resources$NotFoundException;
        //  41     47     82     124    Ljava/io/FileNotFoundException;
        //  50     82     82     124    Ljava/io/FileNotFoundException;
        //  124    156    82     124    Ljava/io/FileNotFoundException;
        //  157    189    82     124    Ljava/io/FileNotFoundException;
        //  193    197    199    235    Ljava/io/IOException;
        //  193    197    82     124    Ljava/io/FileNotFoundException;
        //  197    199    82     124    Ljava/io/FileNotFoundException;
        //  200    232    82     124    Ljava/io/FileNotFoundException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 116 out of bounds for length 116
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3611)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private Drawable getDrawableFromResourceValue(final String str) {
        if (str == null || str.isEmpty() || "0".equals(str)) {
            return null;
        }
        try {
            final int int1 = Integer.parseInt(str);
            final String string = "android.resource://" + this.mProviderContext.getPackageName() + "/" + int1;
            final Drawable checkIconCache = this.checkIconCache(string);
            if (checkIconCache == null) {
                final Drawable drawable = ContextCompat.getDrawable(this.mProviderContext, int1);
                this.storeInIconCache(string, drawable);
                return drawable;
            }
            return checkIconCache;
        }
        catch (final NumberFormatException ex) {
            final Drawable checkIconCache2 = this.checkIconCache(str);
            if (checkIconCache2 == null) {
                final Drawable drawable2 = this.getDrawable(Uri.parse(str));
                this.storeInIconCache(str, drawable2);
                return drawable2;
            }
            return checkIconCache2;
        }
        catch (final Resources$NotFoundException ex2) {
            Log.w("SuggestionsAdapter", "Icon resource not found: " + str);
            return null;
        }
    }
    
    private Drawable getIcon1(final Cursor cursor) {
        if (this.mIconName1Col == -1) {
            return null;
        }
        final Drawable drawableFromResourceValue = this.getDrawableFromResourceValue(cursor.getString(this.mIconName1Col));
        if (drawableFromResourceValue == null) {
            return this.getDefaultIcon1(cursor);
        }
        return drawableFromResourceValue;
    }
    
    private Drawable getIcon2(final Cursor cursor) {
        if (this.mIconName2Col != -1) {
            return this.getDrawableFromResourceValue(cursor.getString(this.mIconName2Col));
        }
        return null;
    }
    
    private static String getStringOrNull(final Cursor cursor, final int n) {
        Label_0015: {
            if (n == -1) {
                break Label_0015;
            }
            try {
                return cursor.getString(n);
                return null;
            }
            catch (final Exception ex) {
                Log.e("SuggestionsAdapter", "unexpected error retrieving valid column from cursor, did the remote process die?", (Throwable)ex);
                return null;
            }
        }
    }
    
    private void setViewDrawable(final ImageView imageView, final Drawable imageDrawable, final int visibility) {
        imageView.setImageDrawable(imageDrawable);
        if (imageDrawable != null) {
            imageView.setVisibility(0);
            imageDrawable.setVisible(false, false);
            imageDrawable.setVisible(true, false);
        }
        else {
            imageView.setVisibility(visibility);
        }
    }
    
    private void setViewText(final TextView textView, final CharSequence text) {
        textView.setText(text);
        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(0);
        }
        else {
            textView.setVisibility(8);
        }
    }
    
    private void storeInIconCache(final String key, final Drawable drawable) {
        if (drawable != null) {
            this.mOutsideDrawablesCache.put(key, drawable.getConstantState());
        }
    }
    
    private void updateSpinnerState(final Cursor cursor) {
        final Bundle bundle = null;
        Bundle extras;
        if (cursor == null) {
            extras = bundle;
        }
        else {
            extras = cursor.getExtras();
        }
        if (extras != null && extras.getBoolean("in_progress")) {
            return;
        }
    }
    
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final ChildViewCache childViewCache = (ChildViewCache)view.getTag();
        int int1;
        if (this.mFlagsCol == -1) {
            int1 = 0;
        }
        else {
            int1 = cursor.getInt(this.mFlagsCol);
        }
        if (childViewCache.mText1 != null) {
            this.setViewText(childViewCache.mText1, getStringOrNull(cursor, this.mText1Col));
        }
        if (childViewCache.mText2 != null) {
            final String stringOrNull = getStringOrNull(cursor, this.mText2UrlCol);
            CharSequence charSequence;
            if (stringOrNull == null) {
                charSequence = getStringOrNull(cursor, this.mText2Col);
            }
            else {
                charSequence = this.formatUrl(stringOrNull);
            }
            if (!TextUtils.isEmpty(charSequence)) {
                if (childViewCache.mText1 != null) {
                    childViewCache.mText1.setSingleLine(true);
                    childViewCache.mText1.setMaxLines(1);
                }
            }
            else if (childViewCache.mText1 != null) {
                childViewCache.mText1.setSingleLine(false);
                childViewCache.mText1.setMaxLines(2);
            }
            this.setViewText(childViewCache.mText2, charSequence);
        }
        if (childViewCache.mIcon1 != null) {
            this.setViewDrawable(childViewCache.mIcon1, this.getIcon1(cursor), 4);
        }
        if (childViewCache.mIcon2 != null) {
            this.setViewDrawable(childViewCache.mIcon2, this.getIcon2(cursor), 8);
        }
        if (this.mQueryRefinement != 2 && (this.mQueryRefinement != 1 || (int1 & 0x1) == 0x0)) {
            childViewCache.mIconRefine.setVisibility(8);
        }
        else {
            childViewCache.mIconRefine.setVisibility(0);
            childViewCache.mIconRefine.setTag((Object)childViewCache.mText1.getText());
            childViewCache.mIconRefine.setOnClickListener((View$OnClickListener)this);
        }
    }
    
    public void changeCursor(final Cursor cursor) {
        Label_0017: {
            if (this.mClosed) {
                break Label_0017;
            }
            while (true) {
                try {
                    super.changeCursor(cursor);
                    if (cursor != null) {
                        this.mText1Col = cursor.getColumnIndex("suggest_text_1");
                        this.mText2Col = cursor.getColumnIndex("suggest_text_2");
                        this.mText2UrlCol = cursor.getColumnIndex("suggest_text_2_url");
                        this.mIconName1Col = cursor.getColumnIndex("suggest_icon_1");
                        this.mIconName2Col = cursor.getColumnIndex("suggest_icon_2");
                        this.mFlagsCol = cursor.getColumnIndex("suggest_flags");
                    }
                    return;
                    Label_0031: {
                        cursor.close();
                    }
                    return;
                    Log.w("SuggestionsAdapter", "Tried to change cursor after adapter was closed.");
                    iftrue(Label_0031:)(cursor != null);
                }
                catch (final Exception ex) {
                    Log.e("SuggestionsAdapter", "error changing cursor and caching columns", (Throwable)ex);
                }
            }
        }
    }
    
    public void close() {
        this.changeCursor(null);
        this.mClosed = true;
    }
    
    public CharSequence convertToString(final Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        final String columnString = getColumnString(cursor, "suggest_intent_query");
        if (columnString == null) {
            if (this.mSearchable.shouldRewriteQueryFromData()) {
                final String columnString2 = getColumnString(cursor, "suggest_intent_data");
                if (columnString2 != null) {
                    return columnString2;
                }
            }
            if (this.mSearchable.shouldRewriteQueryFromText()) {
                final String columnString3 = getColumnString(cursor, "suggest_text_1");
                if (columnString3 != null) {
                    return columnString3;
                }
            }
            return null;
        }
        return columnString;
    }
    
    Drawable getDrawableFromResourceUri(final Uri obj) throws FileNotFoundException {
        final String authority = obj.getAuthority();
        while (true) {
            if (!TextUtils.isEmpty((CharSequence)authority)) {
                while (true) {
                    Resources resourcesForApplication = null;
                    List pathSegments = null;
                    Label_0170: {
                        try {
                            resourcesForApplication = this.mContext.getPackageManager().getResourcesForApplication(authority);
                            pathSegments = obj.getPathSegments();
                            if (pathSegments == null) {
                                throw new FileNotFoundException("No path: " + obj);
                            }
                            final int size = pathSegments.size();
                            if (size == 1) {
                                break Label_0170;
                            }
                            if (size != 2) {
                                throw new FileNotFoundException("More than two path segments: " + obj);
                            }
                            break Label_0170;
                            throw new FileNotFoundException("No authority: " + obj);
                        }
                        catch (final PackageManager$NameNotFoundException ex) {
                            throw new FileNotFoundException("No package found for authority: " + obj);
                        }
                        throw new FileNotFoundException("No path: " + obj);
                        try {
                            final int n = Integer.parseInt(pathSegments.get(0));
                            if (n != 0) {
                                return resourcesForApplication.getDrawable(n);
                            }
                            throw new FileNotFoundException("No resource found for: " + obj);
                        }
                        catch (final NumberFormatException ex2) {
                            throw new FileNotFoundException("Single path segment is not a resource ID: " + obj);
                        }
                    }
                    final int n = resourcesForApplication.getIdentifier((String)pathSegments.get(1), (String)pathSegments.get(0), authority);
                    continue;
                }
            }
            continue;
        }
    }
    
    public View getDropDownView(final int n, View dropDownView, final ViewGroup viewGroup) {
        try {
            dropDownView = super.getDropDownView(n, dropDownView, viewGroup);
            return dropDownView;
        }
        catch (final RuntimeException ex) {
            Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", (Throwable)ex);
            final View dropDownView2 = this.newDropDownView(this.mContext, this.mCursor, viewGroup);
            if (dropDownView2 != null) {
                ((ChildViewCache)dropDownView2.getTag()).mText1.setText((CharSequence)ex.toString());
            }
            return dropDownView2;
        }
    }
    
    public int getQueryRefinement() {
        return this.mQueryRefinement;
    }
    
    Cursor getSearchManagerSuggestions(final SearchableInfo searchableInfo, final String s, final int i) {
        if (searchableInfo == null) {
            return null;
        }
        final String suggestAuthority = searchableInfo.getSuggestAuthority();
        if (suggestAuthority != null) {
            final Uri$Builder fragment = new Uri$Builder().scheme("content").authority(suggestAuthority).query("").fragment("");
            final String suggestPath = searchableInfo.getSuggestPath();
            if (suggestPath != null) {
                fragment.appendEncodedPath(suggestPath);
            }
            fragment.appendPath("search_suggest_query");
            final String suggestSelection = searchableInfo.getSuggestSelection();
            String[] array;
            if (suggestSelection == null) {
                fragment.appendPath(s);
                array = null;
            }
            else {
                array = new String[] { s };
            }
            if (i > 0) {
                fragment.appendQueryParameter("limit", String.valueOf(i));
            }
            return this.mContext.getContentResolver().query(fragment.build(), (String[])null, suggestSelection, array, (String)null);
        }
        return null;
    }
    
    public View getView(final int n, View view, final ViewGroup viewGroup) {
        try {
            view = super.getView(n, view, viewGroup);
            return view;
        }
        catch (final RuntimeException ex) {
            Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", (Throwable)ex);
            final View view2 = this.newView(this.mContext, this.mCursor, viewGroup);
            if (view2 != null) {
                ((ChildViewCache)view2.getTag()).mText1.setText((CharSequence)ex.toString());
            }
            return view2;
        }
    }
    
    public boolean hasStableIds() {
        return false;
    }
    
    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        final View view = super.newView(context, cursor, viewGroup);
        view.setTag((Object)new ChildViewCache(view));
        ((ImageView)view.findViewById(R.id.edit_query)).setImageResource(this.mCommitIconResId);
        return view;
    }
    
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.updateSpinnerState(this.getCursor());
    }
    
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        this.updateSpinnerState(this.getCursor());
    }
    
    public void onClick(final View view) {
        final Object tag = view.getTag();
        if (tag instanceof CharSequence) {
            this.mSearchView.onQueryRefine((CharSequence)tag);
        }
    }
    
    public Cursor runQueryOnBackgroundThread(final CharSequence charSequence) {
        String string;
        if (charSequence != null) {
            string = charSequence.toString();
        }
        else {
            string = "";
        }
        if (this.mSearchView.getVisibility() != 0 || this.mSearchView.getWindowVisibility() != 0) {
            return null;
        }
        try {
            final Cursor searchManagerSuggestions = this.getSearchManagerSuggestions(this.mSearchable, string, 50);
            if (searchManagerSuggestions == null) {
                return null;
            }
            searchManagerSuggestions.getCount();
            return searchManagerSuggestions;
        }
        catch (final RuntimeException ex) {
            Log.w("SuggestionsAdapter", "Search suggestions query threw an exception.", (Throwable)ex);
            return null;
        }
    }
    
    public void setQueryRefinement(final int mQueryRefinement) {
        this.mQueryRefinement = mQueryRefinement;
    }
    
    private static final class ChildViewCache
    {
        public final ImageView mIcon1;
        public final ImageView mIcon2;
        public final ImageView mIconRefine;
        public final TextView mText1;
        public final TextView mText2;
        
        public ChildViewCache(final View view) {
            this.mText1 = (TextView)view.findViewById(16908308);
            this.mText2 = (TextView)view.findViewById(16908309);
            this.mIcon1 = (ImageView)view.findViewById(16908295);
            this.mIcon2 = (ImageView)view.findViewById(16908296);
            this.mIconRefine = (ImageView)view.findViewById(R.id.edit_query);
        }
    }
}
