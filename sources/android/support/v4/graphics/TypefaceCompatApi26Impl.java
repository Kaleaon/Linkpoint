package android.support.v4.graphics;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.FontVariationAxis;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.util.Log;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

@RequiresApi(26)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompatApi26Impl extends TypefaceCompatApi21Impl {
    private static final String ABORT_CREATION_METHOD = "abortCreation";
    private static final String ADD_FONT_FROM_ASSET_MANAGER_METHOD = "addFontFromAssetManager";
    private static final String ADD_FONT_FROM_BUFFER_METHOD = "addFontFromBuffer";
    private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
    private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
    private static final String FREEZE_METHOD = "freeze";
    private static final int RESOLVE_BY_FONT_TABLE = -1;
    private static final String TAG = "TypefaceCompatApi26Impl";
    private static final Method sAbortCreation;
    private static final Method sAddFontFromAssetManager;
    private static final Method sAddFontFromBuffer;
    private static final Method sCreateFromFamiliesWithDefault;
    private static final Class sFontFamily;
    private static final Constructor sFontFamilyCtor;
    private static final Method sFreeze;

    static {
        Method method;
        Method method2;
        Method method3;
        Method method4;
        Constructor<?> constructor;
        Class<?> cls;
        Method method5 = null;
        try {
            Class<?> cls2 = Class.forName(FONT_FAMILY_CLASS);
            Constructor<?> constructor2 = cls2.getConstructor(new Class[0]);
            Method method6 = cls2.getMethod(ADD_FONT_FROM_ASSET_MANAGER_METHOD, new Class[]{AssetManager.class, String.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, FontVariationAxis[].class});
            Method method7 = cls2.getMethod(ADD_FONT_FROM_BUFFER_METHOD, new Class[]{ByteBuffer.class, Integer.TYPE, FontVariationAxis[].class, Integer.TYPE, Integer.TYPE});
            Method method8 = cls2.getMethod(FREEZE_METHOD, new Class[0]);
            Method method9 = cls2.getMethod(ABORT_CREATION_METHOD, new Class[0]);
            method = Typeface.class.getDeclaredMethod(CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD, new Class[]{Array.newInstance(cls2, 1).getClass(), Integer.TYPE, Integer.TYPE});
            method.setAccessible(true);
            method5 = method9;
            method2 = method8;
            method3 = method7;
            method4 = method6;
            constructor = constructor2;
            cls = cls2;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Log.e(TAG, "Unable to collect necessary methods for class " + e.getClass().getName(), e);
            method = null;
            method2 = null;
            method3 = null;
            method4 = null;
            constructor = null;
            cls = null;
        }
        sFontFamilyCtor = constructor;
        sFontFamily = cls;
        sAddFontFromAssetManager = method4;
        sAddFontFromBuffer = method3;
        sFreeze = method2;
        sAbortCreation = method5;
        sCreateFromFamiliesWithDefault = method;
    }

    private static boolean abortCreation(Object obj) {
        try {
            return ((Boolean) sAbortCreation.invoke(obj, new Object[0])).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean addFontFromAssetManager(Context context, Object obj, String str, int i, int i2, int i3) {
        try {
            return ((Boolean) sAddFontFromAssetManager.invoke(obj, new Object[]{context.getAssets(), str, 0, false, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), null})).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean addFontFromBuffer(Object obj, ByteBuffer byteBuffer, int i, int i2, int i3) {
        try {
            return ((Boolean) sAddFontFromBuffer.invoke(obj, new Object[]{byteBuffer, Integer.valueOf(i), null, Integer.valueOf(i2), Integer.valueOf(i3)})).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Typeface createFromFamiliesWithDefault(Object obj) {
        try {
            Object newInstance = Array.newInstance(sFontFamily, 1);
            Array.set(newInstance, 0, obj);
            return (Typeface) sCreateFromFamiliesWithDefault.invoke((Object) null, new Object[]{newInstance, -1, -1});
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean freeze(Object obj) {
        try {
            return ((Boolean) sFreeze.invoke(obj, new Object[0])).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isFontFamilyPrivateAPIAvailable() {
        if (sAddFontFromAssetManager == null) {
            Log.w(TAG, "Unable to collect necessary private methods.Fallback to legacy implementation.");
        }
        return sAddFontFromAssetManager != null;
    }

    private static Object newFamily() {
        try {
            return sFontFamilyCtor.newInstance(new Object[0]);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, Resources resources, int i) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromFontFamilyFilesResourceEntry(context, fontFamilyFilesResourceEntry, resources, i);
        }
        Object newFamily = newFamily();
        FontResourcesParserCompat.FontFileResourceEntry[] entries = fontFamilyFilesResourceEntry.getEntries();
        int length = entries.length;
        int i2 = 0;
        while (i2 < length) {
            FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry = entries[i2];
            if (addFontFromAssetManager(context, newFamily, fontFileResourceEntry.getFileName(), 0, fontFileResourceEntry.getWeight(), !fontFileResourceEntry.isItalic() ? 0 : 1)) {
                i2++;
            } else {
                abortCreation(newFamily);
                return null;
            }
        }
        if (freeze(newFamily)) {
            return createFromFamiliesWithDefault(newFamily);
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0070, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0071, code lost:
        r9 = r1;
        r1 = r0;
        r0 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00c0, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Typeface createFromFontInfo(android.content.Context r11, @android.support.annotation.Nullable android.os.CancellationSignal r12, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] r13, int r14) {
        /*
            r10 = this;
            int r0 = r13.length
            r1 = 1
            if (r0 < r1) goto L_0x0025
            boolean r0 = isFontFamilyPrivateAPIAvailable()
            if (r0 == 0) goto L_0x0027
            java.util.Map r3 = android.support.v4.provider.FontsContractCompat.prepareFontData(r11, r13, r12)
            java.lang.Object r4 = newFamily()
            r1 = 0
            int r5 = r13.length
            r0 = 0
            r2 = r0
        L_0x0016:
            if (r2 < r5) goto L_0x0086
            if (r1 == 0) goto L_0x00b9
            boolean r0 = freeze(r4)
            if (r0 == 0) goto L_0x00be
            android.graphics.Typeface r0 = createFromFamiliesWithDefault(r4)
            return r0
        L_0x0025:
            r0 = 0
            return r0
        L_0x0027:
            android.support.v4.provider.FontsContractCompat$FontInfo r0 = r10.findBestInfo(r13, r14)
            android.content.ContentResolver r1 = r11.getContentResolver()
            android.net.Uri r2 = r0.getUri()     // Catch:{ IOException -> 0x0062 }
            java.lang.String r3 = "r"
            android.os.ParcelFileDescriptor r2 = r1.openFileDescriptor(r2, r3, r12)     // Catch:{ IOException -> 0x0062 }
            r1 = 0
            android.graphics.Typeface$Builder r3 = new android.graphics.Typeface$Builder     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            java.io.FileDescriptor r4 = r2.getFileDescriptor()     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            r3.<init>(r4)     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            int r4 = r0.getWeight()     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            android.graphics.Typeface$Builder r3 = r3.setWeight(r4)     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            boolean r0 = r0.isItalic()     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            android.graphics.Typeface$Builder r0 = r3.setItalic(r0)     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            android.graphics.Typeface r0 = r0.build()     // Catch:{ Throwable -> 0x006e, all -> 0x00c0 }
            if (r2 != 0) goto L_0x005b
        L_0x005a:
            return r0
        L_0x005b:
            r3 = 0
            if (r3 != 0) goto L_0x0065
            r2.close()     // Catch:{ IOException -> 0x0062 }
            goto L_0x005a
        L_0x0062:
            r0 = move-exception
            r0 = 0
            return r0
        L_0x0065:
            r2.close()     // Catch:{ Throwable -> 0x0069 }
            goto L_0x005a
        L_0x0069:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch:{ IOException -> 0x0062 }
            goto L_0x005a
        L_0x006e:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0070 }
        L_0x0070:
            r1 = move-exception
            r9 = r1
            r1 = r0
            r0 = r9
        L_0x0074:
            if (r2 != 0) goto L_0x0077
        L_0x0076:
            throw r0     // Catch:{ IOException -> 0x0062 }
        L_0x0077:
            if (r1 != 0) goto L_0x007d
            r2.close()     // Catch:{ IOException -> 0x0062 }
            goto L_0x0076
        L_0x007d:
            r2.close()     // Catch:{ Throwable -> 0x0081 }
            goto L_0x0076
        L_0x0081:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch:{ IOException -> 0x0062 }
            goto L_0x0076
        L_0x0086:
            r6 = r13[r2]
            android.net.Uri r0 = r6.getUri()
            java.lang.Object r0 = r3.get(r0)
            java.nio.ByteBuffer r0 = (java.nio.ByteBuffer) r0
            if (r0 == 0) goto L_0x00b0
            int r7 = r6.getTtcIndex()
            int r8 = r6.getWeight()
            boolean r1 = r6.isItalic()
            if (r1 != 0) goto L_0x00b2
            r1 = 0
        L_0x00a3:
            boolean r0 = addFontFromBuffer(r4, r0, r7, r8, r1)
            if (r0 == 0) goto L_0x00b4
            r0 = 1
        L_0x00aa:
            int r1 = r2 + 1
            r2 = r1
            r1 = r0
            goto L_0x0016
        L_0x00b0:
            r0 = r1
            goto L_0x00aa
        L_0x00b2:
            r1 = 1
            goto L_0x00a3
        L_0x00b4:
            abortCreation(r4)
            r0 = 0
            return r0
        L_0x00b9:
            abortCreation(r4)
            r0 = 0
            return r0
        L_0x00be:
            r0 = 0
            return r0
        L_0x00c0:
            r0 = move-exception
            goto L_0x0074
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatApi26Impl.createFromFontInfo(android.content.Context, android.os.CancellationSignal, android.support.v4.provider.FontsContractCompat$FontInfo[], int):android.graphics.Typeface");
    }

    @Nullable
    public Typeface createFromResourcesFontFile(Context context, Resources resources, int i, String str, int i2) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromResourcesFontFile(context, resources, i, str, i2);
        }
        Object newFamily = newFamily();
        if (!addFontFromAssetManager(context, newFamily, str, 0, -1, -1)) {
            abortCreation(newFamily);
            return null;
        } else if (freeze(newFamily)) {
            return createFromFamiliesWithDefault(newFamily);
        } else {
            return null;
        }
    }
}
