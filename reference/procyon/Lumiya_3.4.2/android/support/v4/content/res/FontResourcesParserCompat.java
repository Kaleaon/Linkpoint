// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content.res;

import android.support.annotation.NonNull;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.util.Base64;
import android.support.v4.provider.FontRequest;
import android.support.compat.R;
import android.util.Xml;
import android.content.res.TypedArray;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.Resources;
import org.xmlpull.v1.XmlPullParser;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class FontResourcesParserCompat
{
    private static final int DEFAULT_TIMEOUT_MILLIS = 500;
    public static final int FETCH_STRATEGY_ASYNC = 1;
    public static final int FETCH_STRATEGY_BLOCKING = 0;
    public static final int INFINITE_TIMEOUT_VALUE = -1;
    private static final int ITALIC = 1;
    private static final int NORMAL_WEIGHT = 400;
    
    @Nullable
    public static FamilyResourceEntry parse(final XmlPullParser xmlPullParser, final Resources resources) throws XmlPullParserException, IOException {
        int next;
        do {
            next = xmlPullParser.next();
        } while (next != 2 && next != 1);
        if (next == 2) {
            return readFamilies(xmlPullParser, resources);
        }
        throw new XmlPullParserException("No start tag found");
    }
    
    public static List<List<byte[]>> readCerts(final Resources resources, @ArrayRes int i) {
        final ArrayList list = null;
        final ArrayList list2 = null;
        ArrayList list3;
        if (i == 0) {
            list3 = list2;
        }
        else {
            final TypedArray obtainTypedArray = resources.obtainTypedArray(i);
            if (obtainTypedArray.length() <= 0) {
                list3 = list;
            }
            else {
                final ArrayList list4 = new ArrayList();
                int n;
                if (obtainTypedArray.getResourceId(0, 0) == 0) {
                    n = 0;
                }
                else {
                    n = 1;
                }
                if (n == 0) {
                    list4.add(toByteArrayList(resources.getStringArray(i)));
                    list3 = list4;
                }
                else {
                    for (i = 0; i < obtainTypedArray.length(); ++i) {
                        list4.add(toByteArrayList(resources.getStringArray(obtainTypedArray.getResourceId(i, 0))));
                    }
                    list3 = list4;
                }
            }
            obtainTypedArray.recycle();
        }
        List<Object> emptyList = list3;
        if (list3 == null) {
            emptyList = Collections.emptyList();
        }
        return (List<List<byte[]>>)emptyList;
    }
    
    @Nullable
    private static FamilyResourceEntry readFamilies(final XmlPullParser xmlPullParser, final Resources resources) throws XmlPullParserException, IOException {
        xmlPullParser.require(2, (String)null, "font-family");
        if (!xmlPullParser.getName().equals("font-family")) {
            skip(xmlPullParser);
            return null;
        }
        return readFamily(xmlPullParser, resources);
    }
    
    @Nullable
    private static FamilyResourceEntry readFamily(final XmlPullParser xmlPullParser, final Resources resources) throws XmlPullParserException, IOException {
        final TypedArray obtainAttributes = resources.obtainAttributes(Xml.asAttributeSet(xmlPullParser), R.styleable.FontFamily);
        final String string = obtainAttributes.getString(R.styleable.FontFamily_fontProviderAuthority);
        final String string2 = obtainAttributes.getString(R.styleable.FontFamily_fontProviderPackage);
        final String string3 = obtainAttributes.getString(R.styleable.FontFamily_fontProviderQuery);
        final int resourceId = obtainAttributes.getResourceId(R.styleable.FontFamily_fontProviderCerts, 0);
        final int integer = obtainAttributes.getInteger(R.styleable.FontFamily_fontProviderFetchStrategy, 1);
        final int integer2 = obtainAttributes.getInteger(R.styleable.FontFamily_fontProviderFetchTimeout, 500);
        obtainAttributes.recycle();
        if (string != null && string2 != null && string3 != null) {
            while (xmlPullParser.next() != 3) {
                skip(xmlPullParser);
            }
            return (FamilyResourceEntry)new ProviderResourceEntry(new FontRequest(string, string2, string3, readCerts(resources, resourceId)), integer, integer2);
        }
        final ArrayList list = new ArrayList();
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                if (!xmlPullParser.getName().equals("font")) {
                    skip(xmlPullParser);
                }
                else {
                    list.add(readFont(xmlPullParser, resources));
                }
            }
        }
        if (!list.isEmpty()) {
            return (FamilyResourceEntry)new FontFamilyFilesResourceEntry((FontFileResourceEntry[])list.toArray(new FontFileResourceEntry[list.size()]));
        }
        return null;
    }
    
    private static FontFileResourceEntry readFont(final XmlPullParser xmlPullParser, final Resources resources) throws XmlPullParserException, IOException {
        boolean b = true;
        final TypedArray obtainAttributes = resources.obtainAttributes(Xml.asAttributeSet(xmlPullParser), R.styleable.FontFamilyFont);
        final int int1 = obtainAttributes.getInt(R.styleable.FontFamilyFont_fontWeight, 400);
        if (1 != obtainAttributes.getInt(R.styleable.FontFamilyFont_fontStyle, 0)) {
            b = false;
        }
        final int resourceId = obtainAttributes.getResourceId(R.styleable.FontFamilyFont_font, 0);
        final String string = obtainAttributes.getString(R.styleable.FontFamilyFont_font);
        obtainAttributes.recycle();
        while (xmlPullParser.next() != 3) {
            skip(xmlPullParser);
        }
        return new FontFileResourceEntry(string, int1, b, resourceId);
    }
    
    private static void skip(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int i = 1;
        while (i > 0) {
            switch (xmlPullParser.next()) {
                default: {
                    continue;
                }
                case 2: {
                    ++i;
                    continue;
                }
                case 3: {
                    --i;
                    continue;
                }
            }
        }
    }
    
    private static List<byte[]> toByteArrayList(final String[] array) {
        final ArrayList list = new ArrayList();
        for (int length = array.length, i = 0; i < length; ++i) {
            list.add(Base64.decode(array[i], 0));
        }
        return list;
    }
    
    public interface FamilyResourceEntry
    {
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public @interface FetchStrategy {
    }
    
    public static final class FontFamilyFilesResourceEntry implements FamilyResourceEntry
    {
        @NonNull
        private final FontFileResourceEntry[] mEntries;
        
        public FontFamilyFilesResourceEntry(@NonNull final FontFileResourceEntry[] mEntries) {
            this.mEntries = mEntries;
        }
        
        @NonNull
        public FontFileResourceEntry[] getEntries() {
            return this.mEntries;
        }
    }
    
    public static final class FontFileResourceEntry
    {
        @NonNull
        private final String mFileName;
        private boolean mItalic;
        private int mResourceId;
        private int mWeight;
        
        public FontFileResourceEntry(@NonNull final String mFileName, final int mWeight, final boolean mItalic, final int mResourceId) {
            this.mFileName = mFileName;
            this.mWeight = mWeight;
            this.mItalic = mItalic;
            this.mResourceId = mResourceId;
        }
        
        @NonNull
        public String getFileName() {
            return this.mFileName;
        }
        
        public int getResourceId() {
            return this.mResourceId;
        }
        
        public int getWeight() {
            return this.mWeight;
        }
        
        public boolean isItalic() {
            return this.mItalic;
        }
    }
    
    public static final class ProviderResourceEntry implements FamilyResourceEntry
    {
        @NonNull
        private final FontRequest mRequest;
        private final int mStrategy;
        private final int mTimeoutMs;
        
        public ProviderResourceEntry(@NonNull final FontRequest mRequest, final int mStrategy, final int mTimeoutMs) {
            this.mRequest = mRequest;
            this.mStrategy = mStrategy;
            this.mTimeoutMs = mTimeoutMs;
        }
        
        public int getFetchStrategy() {
            return this.mStrategy;
        }
        
        @NonNull
        public FontRequest getRequest() {
            return this.mRequest;
        }
        
        public int getTimeout() {
            return this.mTimeoutMs;
        }
    }
}
