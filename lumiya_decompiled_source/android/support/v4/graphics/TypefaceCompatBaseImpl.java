package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.provider.FontsContractCompat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/* JADX INFO: Access modifiers changed from: package-private */
@RequiresApi(14)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class TypefaceCompatBaseImpl implements TypefaceCompat.TypefaceCompatImpl {
    private static final String CACHE_FILE_PREFIX = "cached_font_";
    private static final String TAG = "TypefaceCompatBaseImpl";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface StyleExtractor<T> {
        int getWeight(T t);

        boolean isItalic(T t);
    }

    private FontResourcesParserCompat.FontFileResourceEntry findBestEntry(FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, int i) {
        return (FontResourcesParserCompat.FontFileResourceEntry) findBestFont(fontFamilyFilesResourceEntry.getEntries(), i, new StyleExtractor<FontResourcesParserCompat.FontFileResourceEntry>() { // from class: android.support.v4.graphics.TypefaceCompatBaseImpl.2
            @Override // android.support.v4.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public int getWeight(FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry) {
                return fontFileResourceEntry.getWeight();
            }

            @Override // android.support.v4.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public boolean isItalic(FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry) {
                return fontFileResourceEntry.isItalic();
            }
        });
    }

    private static <T> T findBestFont(T[] tArr, int i, StyleExtractor<T> styleExtractor) {
        T t;
        T t2 = null;
        int i2 = (i & 1) != 0 ? 700 : 400;
        boolean z = (i & 2) != 0;
        int i3 = Integer.MAX_VALUE;
        int length = tArr.length;
        int i4 = 0;
        while (i4 < length) {
            T t3 = tArr[i4];
            int abs = (styleExtractor.isItalic(t3) != z ? 1 : 0) + (Math.abs(styleExtractor.getWeight(t3) - i2) * 2);
            if (t2 != null && i3 <= abs) {
                abs = i3;
                t = t2;
            } else {
                t = t3;
            }
            i4++;
            t2 = t;
            i3 = abs;
        }
        return t2;
    }

    @Override // android.support.v4.graphics.TypefaceCompat.TypefaceCompatImpl
    @Nullable
    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, Resources resources, int i) {
        FontResourcesParserCompat.FontFileResourceEntry findBestEntry = findBestEntry(fontFamilyFilesResourceEntry, i);
        if (findBestEntry != null) {
            return TypefaceCompat.createFromResourcesFontFile(context, resources, findBestEntry.getResourceId(), findBestEntry.getFileName(), i);
        }
        return null;
    }

    @Override // android.support.v4.graphics.TypefaceCompat.TypefaceCompatImpl
    public Typeface createFromFontInfo(Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontsContractCompat.FontInfo[] fontInfoArr, int i) {
        InputStream inputStream;
        InputStream inputStream2 = null;
        if (fontInfoArr.length >= 1) {
            try {
                inputStream = context.getContentResolver().openInputStream(findBestInfo(fontInfoArr, i).getUri());
            } catch (IOException e) {
                inputStream = null;
            } catch (Throwable th) {
                th = th;
            }
            try {
                Typeface createFromInputStream = createFromInputStream(context, inputStream);
                TypefaceCompatUtil.closeQuietly(inputStream);
                return createFromInputStream;
            } catch (IOException e2) {
                TypefaceCompatUtil.closeQuietly(inputStream);
                return null;
            } catch (Throwable th2) {
                inputStream2 = inputStream;
                th = th2;
                TypefaceCompatUtil.closeQuietly(inputStream2);
                throw th;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Typeface createFromInputStream(Context context, InputStream inputStream) {
        File tempFile = TypefaceCompatUtil.getTempFile(context);
        if (tempFile != null) {
            try {
                if (TypefaceCompatUtil.copyToFile(tempFile, inputStream)) {
                    return Typeface.createFromFile(tempFile.getPath());
                }
                return null;
            } catch (RuntimeException e) {
                return null;
            } finally {
                tempFile.delete();
            }
        }
        return null;
    }

    @Override // android.support.v4.graphics.TypefaceCompat.TypefaceCompatImpl
    @Nullable
    public Typeface createFromResourcesFontFile(Context context, Resources resources, int i, String str, int i2) {
        File tempFile = TypefaceCompatUtil.getTempFile(context);
        if (tempFile != null) {
            try {
                if (TypefaceCompatUtil.copyToFile(tempFile, resources, i)) {
                    return Typeface.createFromFile(tempFile.getPath());
                }
                return null;
            } catch (RuntimeException e) {
                return null;
            } finally {
                tempFile.delete();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FontsContractCompat.FontInfo findBestInfo(FontsContractCompat.FontInfo[] fontInfoArr, int i) {
        return (FontsContractCompat.FontInfo) findBestFont(fontInfoArr, i, new StyleExtractor<FontsContractCompat.FontInfo>() { // from class: android.support.v4.graphics.TypefaceCompatBaseImpl.1
            @Override // android.support.v4.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public int getWeight(FontsContractCompat.FontInfo fontInfo) {
                return fontInfo.getWeight();
            }

            @Override // android.support.v4.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public boolean isItalic(FontsContractCompat.FontInfo fontInfo) {
                return fontInfo.isItalic();
            }
        });
    }
}
