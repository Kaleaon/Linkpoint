package android.support.v4.graphics;

import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import java.io.File;

@RequiresApi(21)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl {
    private static final String TAG = "TypefaceCompatApi21Impl";

    TypefaceCompatApi21Impl() {
    }

    private File getFile(ParcelFileDescriptor parcelFileDescriptor) {
        try {
            String readlink = Os.readlink("/proc/self/fd/" + parcelFileDescriptor.getFd());
            if (!OsConstants.S_ISREG(Os.stat(readlink).st_mode)) {
                return null;
            }
            return new File(readlink);
        } catch (ErrnoException e) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x006e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x009f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x00a0, code lost:
        r1 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Typeface createFromFontInfo(android.content.Context r8, android.os.CancellationSignal r9, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] r10, int r11) {
        /*
            r7 = this;
            r2 = 0
            int r0 = r10.length
            r1 = 1
            if (r0 < r1) goto L_0x0032
            android.support.v4.provider.FontsContractCompat$FontInfo r0 = r7.findBestInfo(r10, r11)
            android.content.ContentResolver r1 = r8.getContentResolver()
            android.net.Uri r0 = r0.getUri()     // Catch:{ IOException -> 0x004f }
            java.lang.String r3 = "r"
            android.os.ParcelFileDescriptor r3 = r1.openFileDescriptor(r0, r3, r9)     // Catch:{ IOException -> 0x004f }
            r0 = 0
            java.io.File r1 = r7.getFile(r3)     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            if (r1 != 0) goto L_0x0033
        L_0x001f:
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            java.io.FileDescriptor r1 = r3.getFileDescriptor()     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            r4.<init>(r1)     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            r1 = 0
            android.graphics.Typeface r5 = super.createFromInputStream(r8, r4)     // Catch:{ Throwable -> 0x006c, all -> 0x009f }
            if (r4 != 0) goto L_0x0040
        L_0x002f:
            if (r3 != 0) goto L_0x005d
        L_0x0031:
            return r5
        L_0x0032:
            return r2
        L_0x0033:
            boolean r4 = r1.canRead()     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            if (r4 == 0) goto L_0x001f
            android.graphics.Typeface r1 = android.graphics.Typeface.createFromFile(r1)     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            if (r3 != 0) goto L_0x0081
        L_0x003f:
            return r1
        L_0x0040:
            if (r2 != 0) goto L_0x0051
            r4.close()     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            goto L_0x002f
        L_0x0046:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0048 }
        L_0x0048:
            r1 = move-exception
            r6 = r1
            r1 = r0
            r0 = r6
        L_0x004c:
            if (r3 != 0) goto L_0x0090
        L_0x004e:
            throw r0     // Catch:{ IOException -> 0x004f }
        L_0x004f:
            r0 = move-exception
            return r2
        L_0x0051:
            r4.close()     // Catch:{ Throwable -> 0x0055 }
            goto L_0x002f
        L_0x0055:
            r4 = move-exception
            r1.addSuppressed(r4)     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            goto L_0x002f
        L_0x005a:
            r0 = move-exception
            r1 = r2
            goto L_0x004c
        L_0x005d:
            if (r2 != 0) goto L_0x0063
            r3.close()     // Catch:{ IOException -> 0x004f }
            goto L_0x0031
        L_0x0063:
            r3.close()     // Catch:{ Throwable -> 0x0067 }
            goto L_0x0031
        L_0x0067:
            r1 = move-exception
            r0.addSuppressed(r1)     // Catch:{ IOException -> 0x004f }
            goto L_0x0031
        L_0x006c:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x006e }
        L_0x006e:
            r0 = move-exception
        L_0x006f:
            if (r4 != 0) goto L_0x0072
        L_0x0071:
            throw r0     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
        L_0x0072:
            if (r1 != 0) goto L_0x0078
            r4.close()     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            goto L_0x0071
        L_0x0078:
            r4.close()     // Catch:{ Throwable -> 0x007c }
            goto L_0x0071
        L_0x007c:
            r4 = move-exception
            r1.addSuppressed(r4)     // Catch:{ Throwable -> 0x0046, all -> 0x005a }
            goto L_0x0071
        L_0x0081:
            if (r2 != 0) goto L_0x0087
            r3.close()     // Catch:{ IOException -> 0x004f }
            goto L_0x003f
        L_0x0087:
            r3.close()     // Catch:{ Throwable -> 0x008b }
            goto L_0x003f
        L_0x008b:
            r3 = move-exception
            r0.addSuppressed(r3)     // Catch:{ IOException -> 0x004f }
            goto L_0x003f
        L_0x0090:
            if (r1 != 0) goto L_0x0096
            r3.close()     // Catch:{ IOException -> 0x004f }
            goto L_0x004e
        L_0x0096:
            r3.close()     // Catch:{ Throwable -> 0x009a }
            goto L_0x004e
        L_0x009a:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ IOException -> 0x004f }
            goto L_0x004e
        L_0x009f:
            r0 = move-exception
            r1 = r2
            goto L_0x006f
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatApi21Impl.createFromFontInfo(android.content.Context, android.os.CancellationSignal, android.support.v4.provider.FontsContractCompat$FontInfo[], int):android.graphics.Typeface");
    }
}
