// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.os;

import java.util.Arrays;
import android.support.annotation.IntRange;
import android.os.Build$VERSION;
import android.support.annotation.Size;
import android.support.annotation.Nullable;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import android.support.annotation.NonNull;
import android.support.annotation.GuardedBy;
import java.util.Locale;
import android.support.annotation.RestrictTo;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
final class LocaleListHelper
{
    private static final Locale EN_LATN;
    private static final Locale LOCALE_AR_XB;
    private static final Locale LOCALE_EN_XA;
    private static final int NUM_PSEUDO_LOCALES = 2;
    private static final String STRING_AR_XB = "ar-XB";
    private static final String STRING_EN_XA = "en-XA";
    @GuardedBy("sLock")
    private static LocaleListHelper sDefaultAdjustedLocaleList;
    @GuardedBy("sLock")
    private static LocaleListHelper sDefaultLocaleList;
    private static final Locale[] sEmptyList;
    private static final LocaleListHelper sEmptyLocaleList;
    @GuardedBy("sLock")
    private static Locale sLastDefaultLocale;
    @GuardedBy("sLock")
    private static LocaleListHelper sLastExplicitlySetLocaleList;
    private static final Object sLock;
    private final Locale[] mList;
    @NonNull
    private final String mStringRepresentation;
    
    static {
        sEmptyList = new Locale[0];
        sEmptyLocaleList = new LocaleListHelper(new Locale[0]);
        LOCALE_EN_XA = new Locale("en", "XA");
        LOCALE_AR_XB = new Locale("ar", "XB");
        EN_LATN = LocaleHelper.forLanguageTag("en-Latn");
        sLock = new Object();
        LocaleListHelper.sLastExplicitlySetLocaleList = null;
        LocaleListHelper.sDefaultLocaleList = null;
        LocaleListHelper.sDefaultAdjustedLocaleList = null;
        LocaleListHelper.sLastDefaultLocale = null;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    LocaleListHelper(@NonNull final Locale locale, final LocaleListHelper localeListHelper) {
        final int n = 0;
        if (locale != null) {
            int length;
            if (localeListHelper != null) {
                length = localeListHelper.mList.length;
            }
            else {
                length = 0;
            }
            while (true) {
                for (int i = 0; i < length; ++i) {
                    if (locale.equals(localeListHelper.mList[i])) {
                        int n2;
                        if (i != -1) {
                            n2 = 0;
                        }
                        else {
                            n2 = 1;
                        }
                        final int n3 = length + n2;
                        final Locale[] mList = new Locale[n3];
                        mList[0] = (Locale)locale.clone();
                        if (i != -1) {
                            for (int j = 0; j < i; ++j) {
                                mList[j + 1] = (Locale)localeListHelper.mList[j].clone();
                            }
                            ++i;
                            while (i < length) {
                                mList[i] = (Locale)localeListHelper.mList[i].clone();
                                ++i;
                            }
                        }
                        else {
                            for (int k = 0; k < length; ++k) {
                                mList[k + 1] = (Locale)localeListHelper.mList[k].clone();
                            }
                        }
                        final StringBuilder sb = new StringBuilder();
                        for (int l = n; l < n3; ++l) {
                            sb.append(LocaleHelper.toLanguageTag(mList[l]));
                            if (l < n3 - 1) {
                                sb.append(',');
                            }
                        }
                        this.mList = mList;
                        this.mStringRepresentation = sb.toString();
                        return;
                    }
                }
                int i = -1;
                continue;
            }
        }
        throw new NullPointerException("topLocale is null");
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    LocaleListHelper(@NonNull final Locale... array) {
        if (array.length != 0) {
            final Locale[] mList = new Locale[array.length];
            final HashSet set = new HashSet();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                final Locale o = array[i];
                if (o == null) {
                    throw new NullPointerException("list[" + i + "] is null");
                }
                if (set.contains(o)) {
                    throw new IllegalArgumentException("list[" + i + "] is a repetition");
                }
                final Locale e = (Locale)o.clone();
                mList[i] = e;
                sb.append(LocaleHelper.toLanguageTag(e));
                if (i < array.length - 1) {
                    sb.append(',');
                }
                set.add(e);
            }
            this.mList = mList;
            this.mStringRepresentation = sb.toString();
        }
        else {
            this.mList = LocaleListHelper.sEmptyList;
            this.mStringRepresentation = "";
        }
    }
    
    private Locale computeFirstMatch(final Collection<String> collection, final boolean b) {
        final int computeFirstMatchIndex = this.computeFirstMatchIndex(collection, b);
        Locale locale;
        if (computeFirstMatchIndex != -1) {
            locale = this.mList[computeFirstMatchIndex];
        }
        else {
            locale = null;
        }
        return locale;
    }
    
    private int computeFirstMatchIndex(final Collection<String> collection, final boolean b) {
        if (this.mList.length == 1) {
            return 0;
        }
        if (this.mList.length == 0) {
            return -1;
        }
        int n;
        if (!b) {
            n = Integer.MAX_VALUE;
        }
        else {
            final int firstMatchIndex = this.findFirstMatchIndex(LocaleListHelper.EN_LATN);
            if (firstMatchIndex == 0) {
                return 0;
            }
            if ((n = firstMatchIndex) >= Integer.MAX_VALUE) {
                n = Integer.MAX_VALUE;
            }
        }
        final Iterator<String> iterator = collection.iterator();
        int n2 = n;
        while (iterator.hasNext()) {
            final int firstMatchIndex2 = this.findFirstMatchIndex(LocaleHelper.forLanguageTag(iterator.next()));
            if (firstMatchIndex2 == 0) {
                return 0;
            }
            int n3;
            if ((n3 = firstMatchIndex2) >= n2) {
                n3 = n2;
            }
            n2 = n3;
        }
        if (n2 != Integer.MAX_VALUE) {
            return n2;
        }
        return 0;
    }
    
    private int findFirstMatchIndex(final Locale locale) {
        for (int i = 0; i < this.mList.length; ++i) {
            if (matchScore(locale, this.mList[i]) > 0) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }
    
    @NonNull
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    static LocaleListHelper forLanguageTags(@Nullable final String s) {
        int i = 0;
        if (s != null && !s.isEmpty()) {
            final String[] split = s.split(",");
            Locale[] array;
            for (array = new Locale[split.length]; i < array.length; ++i) {
                array[i] = LocaleHelper.forLanguageTag(split[i]);
            }
            return new LocaleListHelper(array);
        }
        return getEmptyLocaleList();
    }
    
    @NonNull
    @Size(min = 1L)
    static LocaleListHelper getAdjustedDefault() {
        getDefault();
        synchronized (LocaleListHelper.sLock) {
            return LocaleListHelper.sDefaultAdjustedLocaleList;
        }
    }
    
    @NonNull
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @Size(min = 1L)
    static LocaleListHelper getDefault() {
        while (true) {
            final Locale default1 = Locale.getDefault();
            while (true) {
                Label_0068: {
                    synchronized (LocaleListHelper.sLock) {
                        if (!default1.equals(LocaleListHelper.sLastDefaultLocale)) {
                            LocaleListHelper.sLastDefaultLocale = default1;
                            if (LocaleListHelper.sDefaultLocaleList != null) {
                                break Label_0068;
                            }
                            LocaleListHelper.sDefaultLocaleList = new LocaleListHelper(default1, LocaleListHelper.sLastExplicitlySetLocaleList);
                            LocaleListHelper.sDefaultAdjustedLocaleList = LocaleListHelper.sDefaultLocaleList;
                        }
                        return LocaleListHelper.sDefaultLocaleList;
                    }
                }
                if (default1.equals(LocaleListHelper.sDefaultLocaleList.get(0))) {
                    break;
                }
                continue;
            }
        }
        final LocaleListHelper sDefaultLocaleList = LocaleListHelper.sDefaultLocaleList;
        final Object o;
        monitorexit(o);
        return sDefaultLocaleList;
    }
    
    @NonNull
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    static LocaleListHelper getEmptyLocaleList() {
        return LocaleListHelper.sEmptyLocaleList;
    }
    
    private static String getLikelyScript(final Locale locale) {
        if (Build$VERSION.SDK_INT < 21) {
            return "";
        }
        final String script = locale.getScript();
        if (script.isEmpty()) {
            return "";
        }
        return script;
    }
    
    private static boolean isPseudoLocale(final String s) {
        boolean b = false;
        if ("en-XA".equals(s) || "ar-XB".equals(s)) {
            b = true;
        }
        return b;
    }
    
    private static boolean isPseudoLocale(final Locale locale) {
        boolean b = false;
        if (LocaleListHelper.LOCALE_EN_XA.equals(locale) || LocaleListHelper.LOCALE_AR_XB.equals(locale)) {
            b = true;
        }
        return b;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    static boolean isPseudoLocalesOnly(@Nullable final String[] array) {
        if (array == null) {
            return true;
        }
        if (array.length <= 3) {
            for (final String s : array) {
                if (!s.isEmpty() && !isPseudoLocale(s)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @IntRange(from = 0L, to = 1L)
    private static int matchScore(final Locale locale, final Locale obj) {
        final int n = 0;
        int n2 = 0;
        if (locale.equals(obj)) {
            return 1;
        }
        if (!locale.getLanguage().equals(obj.getLanguage())) {
            return 0;
        }
        if (isPseudoLocale(locale) || isPseudoLocale(obj)) {
            return 0;
        }
        final String likelyScript = getLikelyScript(locale);
        if (!likelyScript.isEmpty()) {
            if (likelyScript.equals(getLikelyScript(obj))) {
                n2 = 1;
            }
            return n2;
        }
        final String country = locale.getCountry();
        int n3;
        if (!country.isEmpty() && !country.equals(obj.getCountry())) {
            n3 = n;
        }
        else {
            n3 = 1;
        }
        return n3;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    static void setDefault(@NonNull @Size(min = 1L) final LocaleListHelper localeListHelper) {
        setDefault(localeListHelper, 0);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    static void setDefault(@NonNull @Size(min = 1L) final LocaleListHelper localeListHelper, final int n) {
        Label_0064: {
            if (localeListHelper == null) {
                break Label_0064;
            }
            Label_0075: {
                if (localeListHelper.isEmpty()) {
                    break Label_0075;
                }
                synchronized (LocaleListHelper.sLock) {
                    Locale.setDefault(LocaleListHelper.sLastDefaultLocale = localeListHelper.get(n));
                    LocaleListHelper.sLastExplicitlySetLocaleList = localeListHelper;
                    LocaleListHelper.sDefaultLocaleList = localeListHelper;
                    if (n != 0) {
                        LocaleListHelper.sDefaultAdjustedLocaleList = new LocaleListHelper(LocaleListHelper.sLastDefaultLocale, LocaleListHelper.sDefaultLocaleList);
                    }
                    else {
                        LocaleListHelper.sDefaultAdjustedLocaleList = LocaleListHelper.sDefaultLocaleList;
                    }
                    return;
                    throw new IllegalArgumentException("locales is empty");
                    throw new NullPointerException("locales is null");
                }
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LocaleListHelper)) {
            return false;
        }
        final Locale[] mList = ((LocaleListHelper)o).mList;
        if (this.mList.length == mList.length) {
            for (int i = 0; i < this.mList.length; ++i) {
                if (!this.mList[i].equals(mList[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    Locale get(final int n) {
        Locale locale;
        if (n >= 0 && n < this.mList.length) {
            locale = this.mList[n];
        }
        else {
            locale = null;
        }
        return locale;
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    Locale getFirstMatch(final String[] a) {
        return this.computeFirstMatch(Arrays.asList(a), false);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int getFirstMatchIndex(final String[] a) {
        return this.computeFirstMatchIndex(Arrays.asList(a), false);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int getFirstMatchIndexWithEnglishSupported(final Collection<String> collection) {
        return this.computeFirstMatchIndex(collection, true);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int getFirstMatchIndexWithEnglishSupported(final String[] a) {
        return this.getFirstMatchIndexWithEnglishSupported(Arrays.asList(a));
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    Locale getFirstMatchWithEnglishSupported(final String[] a) {
        return this.computeFirstMatch(Arrays.asList(a), true);
    }
    
    @Override
    public int hashCode() {
        int n = 1;
        for (int i = 0; i < this.mList.length; ++i) {
            n = n * 31 + this.mList[i].hashCode();
        }
        return n;
    }
    
    @IntRange(from = -1L)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int indexOf(final Locale obj) {
        for (int i = 0; i < this.mList.length; ++i) {
            if (this.mList[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    boolean isEmpty() {
        boolean b = false;
        if (this.mList.length == 0) {
            b = true;
        }
        return b;
    }
    
    @IntRange(from = 0L)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int size() {
        return this.mList.length;
    }
    
    @NonNull
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    String toLanguageTags() {
        return this.mStringRepresentation;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.mList.length; ++i) {
            sb.append(this.mList[i]);
            if (i < this.mList.length - 1) {
                sb.append(',');
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
