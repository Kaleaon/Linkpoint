package android.support.v4.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.support.v4.provider.SelfDestructiveThread;
import android.support.v4.util.LruCache;
import android.support.v4.util.Preconditions;
import android.support.v4.util.SimpleArrayMap;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FontsContractCompat {
    private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final String PARCEL_FONT_RESULTS = "font_results";
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
    private static final String TAG = "FontsContractCompat";
    private static final SelfDestructiveThread sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator<byte[]>() {
        public int compare(byte[] bArr, byte[] bArr2) {
            if (bArr.length != bArr2.length) {
                return bArr.length - bArr2.length;
            }
            for (int i = 0; i < bArr.length; i++) {
                if (bArr[i] != bArr2[i]) {
                    return bArr[i] - bArr2[i];
                }
            }
            return 0;
        }
    };
    /* access modifiers changed from: private */
    public static final Object sLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy("sLock")
    public static final SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<Typeface>>> sPendingReplies = new SimpleArrayMap<>();
    /* access modifiers changed from: private */
    public static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(16);

    public static final class Columns implements BaseColumns {
        public static final String FILE_ID = "file_id";
        public static final String ITALIC = "font_italic";
        public static final String RESULT_CODE = "result_code";
        public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
        public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
        public static final int RESULT_CODE_MALFORMED_QUERY = 3;
        public static final int RESULT_CODE_OK = 0;
        public static final String TTC_INDEX = "font_ttc_index";
        public static final String VARIATION_SETTINGS = "font_variation_settings";
        public static final String WEIGHT = "font_weight";
    }

    public static class FontFamilyResult {
        public static final int STATUS_OK = 0;
        public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
        public static final int STATUS_WRONG_CERTIFICATES = 1;
        private final FontInfo[] mFonts;
        private final int mStatusCode;

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        @Retention(RetentionPolicy.SOURCE)
        @interface FontResultStatus {
        }

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public FontFamilyResult(int i, @Nullable FontInfo[] fontInfoArr) {
            this.mStatusCode = i;
            this.mFonts = fontInfoArr;
        }

        public FontInfo[] getFonts() {
            return this.mFonts;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }
    }

    public static class FontInfo {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public FontInfo(@NonNull Uri uri, @IntRange(from = 0) int i, @IntRange(from = 1, to = 1000) int i2, boolean z, int i3) {
            this.mUri = (Uri) Preconditions.checkNotNull(uri);
            this.mTtcIndex = i;
            this.mWeight = i2;
            this.mItalic = z;
            this.mResultCode = i3;
        }

        public int getResultCode() {
            return this.mResultCode;
        }

        @IntRange(from = 0)
        public int getTtcIndex() {
            return this.mTtcIndex;
        }

        @NonNull
        public Uri getUri() {
            return this.mUri;
        }

        @IntRange(from = 1, to = 1000)
        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }
    }

    public static class FontRequestCallback {
        public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
        public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
        public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
        public static final int FAIL_REASON_MALFORMED_QUERY = 3;
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
        public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        @Retention(RetentionPolicy.SOURCE)
        @interface FontRequestFailReason {
        }

        public void onTypefaceRequestFailed(int i) {
        }

        public void onTypefaceRetrieved(Typeface typeface) {
        }
    }

    private FontsContractCompat() {
    }

    public static Typeface buildTypeface(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontInfo[] fontInfoArr) {
        return TypefaceCompat.createFromFontInfo(context, cancellationSignal, fontInfoArr, 0);
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatureArr) {
        ArrayList arrayList = new ArrayList();
        for (Signature byteArray : signatureArr) {
            arrayList.add(byteArray.toByteArray());
        }
        return arrayList;
    }

    private static boolean equalsByteArrayList(List<byte[]> list, List<byte[]> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!Arrays.equals(list.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public static FontFamilyResult fetchFonts(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontRequest fontRequest) throws PackageManager.NameNotFoundException {
        ProviderInfo provider = getProvider(context.getPackageManager(), fontRequest, context.getResources());
        return provider != null ? new FontFamilyResult(0, getFontFromProvider(context, fontRequest, provider.authority, cancellationSignal)) : new FontFamilyResult(1, (FontInfo[]) null);
    }

    private static List<List<byte[]>> getCertificates(FontRequest fontRequest, Resources resources) {
        return fontRequest.getCertificates() == null ? FontResourcesParserCompat.readCerts(resources, fontRequest.getCertificatesArrayResId()) : fontRequest.getCertificates();
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x0165  */
    @android.support.annotation.VisibleForTesting
    @android.support.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.support.v4.provider.FontsContractCompat.FontInfo[] getFontFromProvider(android.content.Context r18, android.support.v4.provider.FontRequest r19, java.lang.String r20, android.os.CancellationSignal r21) {
        /*
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            android.net.Uri$Builder r2 = new android.net.Uri$Builder
            r2.<init>()
            java.lang.String r3 = "content"
            android.net.Uri$Builder r2 = r2.scheme(r3)
            r0 = r20
            android.net.Uri$Builder r2 = r2.authority(r0)
            android.net.Uri r3 = r2.build()
            android.net.Uri$Builder r2 = new android.net.Uri$Builder
            r2.<init>()
            java.lang.String r4 = "content"
            android.net.Uri$Builder r2 = r2.scheme(r4)
            r0 = r20
            android.net.Uri$Builder r2 = r2.authority(r0)
            java.lang.String r4 = "file"
            android.net.Uri$Builder r2 = r2.appendPath(r4)
            android.net.Uri r11 = r2.build()
            r10 = 0
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x016e }
            r4 = 16
            if (r2 > r4) goto L_0x0091
            android.content.ContentResolver r2 = r18.getContentResolver()     // Catch:{ all -> 0x016e }
            r4 = 7
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "_id"
            r6 = 0
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "file_id"
            r6 = 1
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_ttc_index"
            r6 = 2
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_variation_settings"
            r6 = 3
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_weight"
            r6 = 4
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_italic"
            r6 = 5
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "result_code"
            r6 = 6
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            r5 = 1
            java.lang.String[] r6 = new java.lang.String[r5]     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "query = ?"
            r7 = 0
            java.lang.String r8 = r19.getQuery()     // Catch:{ all -> 0x016e }
            r6[r7] = r8     // Catch:{ all -> 0x016e }
            r7 = 0
            android.database.Cursor r10 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x016e }
        L_0x0082:
            if (r10 != 0) goto L_0x00d7
        L_0x0084:
            r2 = r9
        L_0x0085:
            if (r10 != 0) goto L_0x0165
        L_0x0087:
            r3 = 0
            android.support.v4.provider.FontsContractCompat$FontInfo[] r3 = new android.support.v4.provider.FontsContractCompat.FontInfo[r3]
            java.lang.Object[] r2 = r2.toArray(r3)
            android.support.v4.provider.FontsContractCompat$FontInfo[] r2 = (android.support.v4.provider.FontsContractCompat.FontInfo[]) r2
            return r2
        L_0x0091:
            android.content.ContentResolver r2 = r18.getContentResolver()     // Catch:{ all -> 0x016e }
            r4 = 7
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "_id"
            r6 = 0
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "file_id"
            r6 = 1
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_ttc_index"
            r6 = 2
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_variation_settings"
            r6 = 3
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_weight"
            r6 = 4
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "font_italic"
            r6 = 5
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "result_code"
            r6 = 6
            r4[r6] = r5     // Catch:{ all -> 0x016e }
            r5 = 1
            java.lang.String[] r6 = new java.lang.String[r5]     // Catch:{ all -> 0x016e }
            java.lang.String r5 = "query = ?"
            r7 = 0
            java.lang.String r8 = r19.getQuery()     // Catch:{ all -> 0x016e }
            r6[r7] = r8     // Catch:{ all -> 0x016e }
            r7 = 0
            r8 = r21
            android.database.Cursor r10 = r2.query(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x016e }
            goto L_0x0082
        L_0x00d7:
            int r2 = r10.getCount()     // Catch:{ all -> 0x013b }
            if (r2 <= 0) goto L_0x0084
            java.lang.String r2 = "result_code"
            int r12 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x013b }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x013b }
            r2.<init>()     // Catch:{ all -> 0x013b }
            java.lang.String r4 = "_id"
            int r13 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x013b }
            java.lang.String r4 = "file_id"
            int r14 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x013b }
            java.lang.String r4 = "font_ttc_index"
            int r15 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x013b }
            java.lang.String r4 = "font_weight"
            int r16 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x013b }
            java.lang.String r4 = "font_italic"
            int r17 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x013b }
        L_0x010c:
            boolean r4 = r10.moveToNext()     // Catch:{ all -> 0x013b }
            if (r4 == 0) goto L_0x0085
            r4 = -1
            if (r12 != r4) goto L_0x0140
            r9 = 0
        L_0x0116:
            r4 = -1
            if (r15 != r4) goto L_0x0145
            r6 = 0
        L_0x011a:
            r4 = -1
            if (r14 == r4) goto L_0x014a
            long r4 = r10.getLong(r14)     // Catch:{ all -> 0x013b }
            android.net.Uri r5 = android.content.ContentUris.withAppendedId(r11, r4)     // Catch:{ all -> 0x013b }
        L_0x0125:
            r4 = -1
            r0 = r16
            if (r0 != r4) goto L_0x0153
            r7 = 400(0x190, float:5.6E-43)
        L_0x012c:
            r4 = -1
            r0 = r17
            if (r0 != r4) goto L_0x015a
        L_0x0131:
            r8 = 0
        L_0x0132:
            android.support.v4.provider.FontsContractCompat$FontInfo r4 = new android.support.v4.provider.FontsContractCompat$FontInfo     // Catch:{ all -> 0x013b }
            r4.<init>(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x013b }
            r2.add(r4)     // Catch:{ all -> 0x013b }
            goto L_0x010c
        L_0x013b:
            r2 = move-exception
            r3 = r10
        L_0x013d:
            if (r3 != 0) goto L_0x016a
        L_0x013f:
            throw r2
        L_0x0140:
            int r9 = r10.getInt(r12)     // Catch:{ all -> 0x013b }
            goto L_0x0116
        L_0x0145:
            int r6 = r10.getInt(r15)     // Catch:{ all -> 0x013b }
            goto L_0x011a
        L_0x014a:
            long r4 = r10.getLong(r13)     // Catch:{ all -> 0x013b }
            android.net.Uri r5 = android.content.ContentUris.withAppendedId(r3, r4)     // Catch:{ all -> 0x013b }
            goto L_0x0125
        L_0x0153:
            r0 = r16
            int r7 = r10.getInt(r0)     // Catch:{ all -> 0x013b }
            goto L_0x012c
        L_0x015a:
            r0 = r17
            int r4 = r10.getInt(r0)     // Catch:{ all -> 0x013b }
            r8 = 1
            if (r4 != r8) goto L_0x0131
            r8 = 1
            goto L_0x0132
        L_0x0165:
            r10.close()
            goto L_0x0087
        L_0x016a:
            r3.close()
            goto L_0x013f
        L_0x016e:
            r2 = move-exception
            r3 = r10
            goto L_0x013d
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.FontsContractCompat.getFontFromProvider(android.content.Context, android.support.v4.provider.FontRequest, java.lang.String, android.os.CancellationSignal):android.support.v4.provider.FontsContractCompat$FontInfo[]");
    }

    /* access modifiers changed from: private */
    public static Typeface getFontInternal(Context context, FontRequest fontRequest, int i) {
        try {
            FontFamilyResult fetchFonts = fetchFonts(context, (CancellationSignal) null, fontRequest);
            if (fetchFonts.getStatusCode() != 0) {
                return null;
            }
            return TypefaceCompat.createFromFontInfo(context, (CancellationSignal) null, fetchFonts.getFonts(), i);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Typeface getFontSync(final Context context, final FontRequest fontRequest, @Nullable final TextView textView, int i, int i2, final int i3) {
        final String str = fontRequest.getIdentifier() + "-" + i3;
        Typeface typeface = sTypefaceCache.get(str);
        if (typeface != null) {
            return typeface;
        }
        boolean z = i == 0;
        if (z && i2 == -1) {
            return getFontInternal(context, fontRequest, i3);
        }
        AnonymousClass1 r1 = new Callable<Typeface>() {
            public Typeface call() throws Exception {
                Typeface access$000 = FontsContractCompat.getFontInternal(context, fontRequest, i3);
                if (access$000 != null) {
                    FontsContractCompat.sTypefaceCache.put(str, access$000);
                }
                return access$000;
            }
        };
        if (!z) {
            final WeakReference weakReference = new WeakReference(textView);
            AnonymousClass2 r3 = new SelfDestructiveThread.ReplyCallback<Typeface>() {
                public void onReply(Typeface typeface) {
                    if (((TextView) weakReference.get()) != null) {
                        textView.setTypeface(typeface, i3);
                    }
                }
            };
            synchronized (sLock) {
                if (!sPendingReplies.containsKey(str)) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(r3);
                    sPendingReplies.put(str, arrayList);
                    sBackgroundThread.postAndReply(r1, new SelfDestructiveThread.ReplyCallback<Typeface>() {
                        public void onReply(Typeface typeface) {
                            ArrayList arrayList;
                            synchronized (FontsContractCompat.sLock) {
                                arrayList = (ArrayList) FontsContractCompat.sPendingReplies.get(str);
                                FontsContractCompat.sPendingReplies.remove(str);
                            }
                            int i = 0;
                            while (true) {
                                int i2 = i;
                                if (i2 < arrayList.size()) {
                                    ((SelfDestructiveThread.ReplyCallback) arrayList.get(i2)).onReply(typeface);
                                    i = i2 + 1;
                                } else {
                                    return;
                                }
                            }
                        }
                    });
                    return null;
                }
                sPendingReplies.get(str).add(r3);
                return null;
            }
        }
        try {
            return (Typeface) sBackgroundThread.postAndWait(r1, i2);
        } catch (InterruptedException e) {
            return null;
        }
    }

    @VisibleForTesting
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Nullable
    public static ProviderInfo getProvider(@NonNull PackageManager packageManager, @NonNull FontRequest fontRequest, @Nullable Resources resources) throws PackageManager.NameNotFoundException {
        int i = 0;
        String providerAuthority = fontRequest.getProviderAuthority();
        ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(providerAuthority, 0);
        if (resolveContentProvider == null) {
            throw new PackageManager.NameNotFoundException("No package found for authority: " + providerAuthority);
        } else if (resolveContentProvider.packageName.equals(fontRequest.getProviderPackage())) {
            List<byte[]> convertToByteArrayList = convertToByteArrayList(packageManager.getPackageInfo(resolveContentProvider.packageName, 64).signatures);
            Collections.sort(convertToByteArrayList, sByteArrayComparator);
            List<List<byte[]>> certificates = getCertificates(fontRequest, resources);
            while (true) {
                int i2 = i;
                if (i2 >= certificates.size()) {
                    return null;
                }
                ArrayList arrayList = new ArrayList(certificates.get(i2));
                Collections.sort(arrayList, sByteArrayComparator);
                if (equalsByteArrayList(convertToByteArrayList, arrayList)) {
                    return resolveContentProvider;
                }
                i = i2 + 1;
            }
        } else {
            throw new PackageManager.NameNotFoundException("Found content provider " + providerAuthority + ", but package was not " + fontRequest.getProviderPackage());
        }
    }

    @RequiresApi(19)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Map<Uri, ByteBuffer> prepareFontData(Context context, FontInfo[] fontInfoArr, CancellationSignal cancellationSignal) {
        HashMap hashMap = new HashMap();
        for (FontInfo fontInfo : fontInfoArr) {
            if (fontInfo.getResultCode() == 0) {
                Uri uri = fontInfo.getUri();
                if (!hashMap.containsKey(uri)) {
                    hashMap.put(uri, TypefaceCompatUtil.mmap(context, cancellationSignal, uri));
                }
            }
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public static void requestFont(@NonNull final Context context, @NonNull final FontRequest fontRequest, @NonNull final FontRequestCallback fontRequestCallback, @NonNull Handler handler) {
        final Handler handler2 = new Handler();
        handler.post(new Runnable() {
            public void run() {
                int i = 0;
                try {
                    FontFamilyResult fetchFonts = FontsContractCompat.fetchFonts(context, (CancellationSignal) null, fontRequest);
                    if (fetchFonts.getStatusCode() == 0) {
                        FontInfo[] fonts = fetchFonts.getFonts();
                        if (fonts == null || fonts.length == 0) {
                            handler2.post(new Runnable() {
                                public void run() {
                                    fontRequestCallback.onTypefaceRequestFailed(1);
                                }
                            });
                            return;
                        }
                        int length = fonts.length;
                        while (i < length) {
                            FontInfo fontInfo = fonts[i];
                            if (fontInfo.getResultCode() == 0) {
                                i++;
                            } else {
                                final int resultCode = fontInfo.getResultCode();
                                if (resultCode >= 0) {
                                    handler2.post(new Runnable() {
                                        public void run() {
                                            fontRequestCallback.onTypefaceRequestFailed(resultCode);
                                        }
                                    });
                                    return;
                                } else {
                                    handler2.post(new Runnable() {
                                        public void run() {
                                            fontRequestCallback.onTypefaceRequestFailed(-3);
                                        }
                                    });
                                    return;
                                }
                            }
                        }
                        final Typeface buildTypeface = FontsContractCompat.buildTypeface(context, (CancellationSignal) null, fonts);
                        if (buildTypeface != null) {
                            handler2.post(new Runnable() {
                                public void run() {
                                    fontRequestCallback.onTypefaceRetrieved(buildTypeface);
                                }
                            });
                        } else {
                            handler2.post(new Runnable() {
                                public void run() {
                                    fontRequestCallback.onTypefaceRequestFailed(-3);
                                }
                            });
                        }
                    } else {
                        switch (fetchFonts.getStatusCode()) {
                            case 1:
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(-2);
                                    }
                                });
                                return;
                            case 2:
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(-3);
                                    }
                                });
                                return;
                            default:
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(-3);
                                    }
                                });
                                return;
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    handler2.post(new Runnable() {
                        public void run() {
                            fontRequestCallback.onTypefaceRequestFailed(-1);
                        }
                    });
                }
            }
        });
    }
}
