// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.print;

import android.os.CancellationSignal$OnCancelListener;
import android.print.PrintDocumentInfo;
import android.print.PrintDocumentInfo$Builder;
import android.os.Bundle;
import android.print.PrintDocumentAdapter$LayoutResultCallback;
import android.print.PrintDocumentAdapter;
import android.print.PrintAttributes$MediaSize;
import android.print.PrintManager;
import android.print.PrintAttributes$Builder;
import android.print.PageRange;
import android.os.AsyncTask;
import android.print.PrintAttributes$Margins;
import java.io.InputStream;
import java.io.IOException;
import android.util.Log;
import android.graphics.Rect;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.print.PrintDocumentAdapter$WriteResultCallback;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.graphics.BitmapFactory$Options;
import android.support.annotation.RequiresApi;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.io.FileNotFoundException;
import android.net.Uri;
import android.graphics.Bitmap;
import android.os.Build$VERSION;
import android.content.Context;

public final class PrintHelper
{
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    private final PrintHelperVersionImpl mImpl;
    
    public PrintHelper(final Context context) {
        if (Build$VERSION.SDK_INT < 24) {
            if (Build$VERSION.SDK_INT < 23) {
                if (Build$VERSION.SDK_INT < 20) {
                    if (Build$VERSION.SDK_INT < 19) {
                        this.mImpl = (PrintHelperVersionImpl)new PrintHelperStub();
                    }
                    else {
                        this.mImpl = (PrintHelperVersionImpl)new PrintHelperApi19(context);
                    }
                }
                else {
                    this.mImpl = (PrintHelperVersionImpl)new PrintHelperApi20(context);
                }
            }
            else {
                this.mImpl = (PrintHelperVersionImpl)new PrintHelperApi23(context);
            }
        }
        else {
            this.mImpl = (PrintHelperVersionImpl)new PrintHelperApi24(context);
        }
    }
    
    public static boolean systemSupportsPrint() {
        return Build$VERSION.SDK_INT >= 19;
    }
    
    public int getColorMode() {
        return this.mImpl.getColorMode();
    }
    
    public int getOrientation() {
        return this.mImpl.getOrientation();
    }
    
    public int getScaleMode() {
        return this.mImpl.getScaleMode();
    }
    
    public void printBitmap(final String s, final Bitmap bitmap) {
        this.mImpl.printBitmap(s, bitmap, null);
    }
    
    public void printBitmap(final String s, final Bitmap bitmap, final OnPrintFinishCallback onPrintFinishCallback) {
        this.mImpl.printBitmap(s, bitmap, onPrintFinishCallback);
    }
    
    public void printBitmap(final String s, final Uri uri) throws FileNotFoundException {
        this.mImpl.printBitmap(s, uri, null);
    }
    
    public void printBitmap(final String s, final Uri uri, final OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException {
        this.mImpl.printBitmap(s, uri, onPrintFinishCallback);
    }
    
    public void setColorMode(final int colorMode) {
        this.mImpl.setColorMode(colorMode);
    }
    
    public void setOrientation(final int orientation) {
        this.mImpl.setOrientation(orientation);
    }
    
    public void setScaleMode(final int scaleMode) {
        this.mImpl.setScaleMode(scaleMode);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private @interface ColorMode {
    }
    
    public interface OnPrintFinishCallback
    {
        void onFinish();
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }
    
    @RequiresApi(19)
    private static class PrintHelperApi19 implements PrintHelperVersionImpl
    {
        private static final String LOG_TAG = "PrintHelperApi19";
        private static final int MAX_PRINT_SIZE = 3500;
        int mColorMode;
        final Context mContext;
        BitmapFactory$Options mDecodeOptions;
        protected boolean mIsMinMarginsHandlingCorrect;
        private final Object mLock;
        int mOrientation;
        protected boolean mPrintActivityRespectsOrientation;
        int mScaleMode;
        
        PrintHelperApi19(final Context mContext) {
            this.mDecodeOptions = null;
            this.mLock = new Object();
            this.mScaleMode = 2;
            this.mColorMode = 2;
            this.mPrintActivityRespectsOrientation = true;
            this.mIsMinMarginsHandlingCorrect = true;
            this.mContext = mContext;
        }
        
        private Bitmap convertBitmapForColorMode(final Bitmap bitmap, final int n) {
            if (n == 1) {
                final Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap$Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap2);
                final Paint paint = new Paint();
                final ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0.0f);
                paint.setColorFilter((ColorFilter)new ColorMatrixColorFilter(colorMatrix));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                canvas.setBitmap((Bitmap)null);
                return bitmap2;
            }
            return bitmap;
        }
        
        private Matrix getMatrix(final int n, final int n2, final RectF rectF, final int n3) {
            final Matrix matrix = new Matrix();
            final float n4 = rectF.width() / n;
            float n5;
            if (n3 != 2) {
                n5 = Math.min(n4, rectF.height() / n2);
            }
            else {
                n5 = Math.max(n4, rectF.height() / n2);
            }
            matrix.postScale(n5, n5);
            matrix.postTranslate((rectF.width() - n * n5) / 2.0f, (rectF.height() - n5 * n2) / 2.0f);
            return matrix;
        }
        
        private static boolean isPortrait(final Bitmap bitmap) {
            return bitmap.getWidth() <= bitmap.getHeight();
        }
        
        private Bitmap loadBitmap(final Uri uri, BitmapFactory$Options decodeStream) throws FileNotFoundException {
            InputStream openInputStream = null;
            if (uri == null || this.mContext == null) {
                throw new IllegalArgumentException("bad argument to loadBitmap");
            }
            try {
                final InputStream inputStream = openInputStream = this.mContext.getContentResolver().openInputStream(uri);
                decodeStream = (BitmapFactory$Options)BitmapFactory.decodeStream(inputStream, (Rect)null, decodeStream);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (final IOException ex) {
                        Log.w("PrintHelperApi19", "close fail ", (Throwable)ex);
                    }
                }
                return (Bitmap)decodeStream;
            }
            finally {
                if (openInputStream != null) {
                    try {
                        openInputStream.close();
                    }
                    catch (final IOException ex2) {
                        Log.w("PrintHelperApi19", "close fail ", (Throwable)ex2);
                    }
                }
            }
        }
        
        private Bitmap loadConstrainedBitmap(Uri uri) throws FileNotFoundException {
            int inSampleSize = 1;
            if (uri == null || this.mContext == null) {
                throw new IllegalArgumentException("bad argument to getScaledBitmap");
            }
            final BitmapFactory$Options bitmapFactory$Options = new BitmapFactory$Options();
            bitmapFactory$Options.inJustDecodeBounds = true;
            this.loadBitmap(uri, bitmapFactory$Options);
            final int outWidth = bitmapFactory$Options.outWidth;
            final int outHeight = bitmapFactory$Options.outHeight;
            if (outWidth <= 0 || outHeight <= 0) {
                return null;
            }
            for (int i = Math.max(outWidth, outHeight); i > 3500; i >>>= 1, inSampleSize <<= 1) {}
            if (inSampleSize <= 0 || Math.min(outWidth, outHeight) / inSampleSize <= 0) {
                return null;
            }
            final Object mLock = this.mLock;
            monitorenter(mLock);
            BitmapFactory$Options mDecodeOptions;
            try {
                this.mDecodeOptions = new BitmapFactory$Options();
                this.mDecodeOptions.inMutable = true;
                this.mDecodeOptions.inSampleSize = inSampleSize;
                mDecodeOptions = this.mDecodeOptions;
                monitorexit(mLock);
                final PrintHelperApi19 printHelperApi19 = this;
                final Uri uri2 = uri;
                final BitmapFactory$Options bitmapFactory$Options2 = mDecodeOptions;
                final Bitmap bitmap = printHelperApi19.loadBitmap(uri2, bitmapFactory$Options2);
                final PrintHelperApi19 printHelperApi20 = this;
                final Object o = printHelperApi20.mLock;
                final Object o2;
                uri = (Uri)(o2 = o);
                monitorenter(o2);
                final PrintHelperApi19 printHelperApi21 = this;
                final BitmapFactory$Options bitmapFactory$Options3 = null;
                printHelperApi21.mDecodeOptions = bitmapFactory$Options3;
                return bitmap;
            }
            finally {
                final Uri uri3;
                uri = uri3;
                monitorexit(mLock);
            }
            try {
                final PrintHelperApi19 printHelperApi19 = this;
                final Uri uri2 = uri;
                final BitmapFactory$Options bitmapFactory$Options2 = mDecodeOptions;
                final Bitmap bitmap = printHelperApi19.loadBitmap(uri2, bitmapFactory$Options2);
                final PrintHelperApi19 printHelperApi20 = this;
                final Object o = printHelperApi20.mLock;
                final Object o2;
                uri = (Uri)(o2 = o);
                synchronized (o2) {
                    final PrintHelperApi19 printHelperApi21 = this;
                    final BitmapFactory$Options bitmapFactory$Options3 = null;
                    printHelperApi21.mDecodeOptions = bitmapFactory$Options3;
                    return bitmap;
                }
            }
            finally {
                synchronized (this.mLock) {
                    this.mDecodeOptions = null;
                    monitorexit(this.mLock);
                }
            }
        }
        
        private void writeBitmap(final PrintAttributes printAttributes, final int n, final Bitmap bitmap, final ParcelFileDescriptor parcelFileDescriptor, final CancellationSignal cancellationSignal, final PrintDocumentAdapter$WriteResultCallback printDocumentAdapter$WriteResultCallback) {
            PrintAttributes build;
            if (!this.mIsMinMarginsHandlingCorrect) {
                build = this.copyAttributes(printAttributes).setMinMargins(new PrintAttributes$Margins(0, 0, 0, 0)).build();
            }
            else {
                build = printAttributes;
            }
            new AsyncTask<Void, Void, Throwable>() {
                protected Throwable doInBackground(final Void... p0) {
                    // 
                    // This method could not be decompiled.
                    // 
                    // Original Bytecode:
                    // 
                    //     1: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$cancellationSignal:Landroid/os/CancellationSignal;
                    //     4: invokevirtual   android/os/CancellationSignal.isCanceled:()Z
                    //     7: ifne            263
                    //    10: new             Landroid/print/pdf/PrintedPdfDocument;
                    //    13: astore_2       
                    //    14: aload_2        
                    //    15: aload_0        
                    //    16: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                    //    19: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19.mContext:Landroid/content/Context;
                    //    22: aload_0        
                    //    23: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$pdfAttributes:Landroid/print/PrintAttributes;
                    //    26: invokespecial   android/print/pdf/PrintedPdfDocument.<init>:(Landroid/content/Context;Landroid/print/PrintAttributes;)V
                    //    29: aload_0        
                    //    30: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                    //    33: aload_0        
                    //    34: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$bitmap:Landroid/graphics/Bitmap;
                    //    37: aload_0        
                    //    38: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$pdfAttributes:Landroid/print/PrintAttributes;
                    //    41: invokevirtual   android/print/PrintAttributes.getColorMode:()I
                    //    44: invokestatic    android/support/v4/print/PrintHelper$PrintHelperApi19.access$100:(Landroid/support/v4/print/PrintHelper$PrintHelperApi19;Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
                    //    47: astore_3       
                    //    48: aload_0        
                    //    49: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$cancellationSignal:Landroid/os/CancellationSignal;
                    //    52: invokevirtual   android/os/CancellationSignal.isCanceled:()Z
                    //    55: istore          4
                    //    57: iload           4
                    //    59: ifne            265
                    //    62: aload_2        
                    //    63: iconst_1       
                    //    64: invokevirtual   android/print/pdf/PrintedPdfDocument.startPage:(I)Landroid/graphics/pdf/PdfDocument$Page;
                    //    67: astore          5
                    //    69: aload_0        
                    //    70: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                    //    73: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19.mIsMinMarginsHandlingCorrect:Z
                    //    76: ifne            267
                    //    79: new             Landroid/print/pdf/PrintedPdfDocument;
                    //    82: astore          6
                    //    84: aload           6
                    //    86: aload_0        
                    //    87: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                    //    90: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19.mContext:Landroid/content/Context;
                    //    93: aload_0        
                    //    94: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$attributes:Landroid/print/PrintAttributes;
                    //    97: invokespecial   android/print/pdf/PrintedPdfDocument.<init>:(Landroid/content/Context;Landroid/print/PrintAttributes;)V
                    //   100: aload           6
                    //   102: iconst_1       
                    //   103: invokevirtual   android/print/pdf/PrintedPdfDocument.startPage:(I)Landroid/graphics/pdf/PdfDocument$Page;
                    //   106: astore          7
                    //   108: new             Landroid/graphics/RectF;
                    //   111: astore_1       
                    //   112: aload_1        
                    //   113: aload           7
                    //   115: invokevirtual   android/graphics/pdf/PdfDocument$Page.getInfo:()Landroid/graphics/pdf/PdfDocument$PageInfo;
                    //   118: invokevirtual   android/graphics/pdf/PdfDocument$PageInfo.getContentRect:()Landroid/graphics/Rect;
                    //   121: invokespecial   android/graphics/RectF.<init>:(Landroid/graphics/Rect;)V
                    //   124: aload           6
                    //   126: aload           7
                    //   128: invokevirtual   android/print/pdf/PrintedPdfDocument.finishPage:(Landroid/graphics/pdf/PdfDocument$Page;)V
                    //   131: aload           6
                    //   133: invokevirtual   android/print/pdf/PrintedPdfDocument.close:()V
                    //   136: aload_0        
                    //   137: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                    //   140: aload_3        
                    //   141: invokevirtual   android/graphics/Bitmap.getWidth:()I
                    //   144: aload_3        
                    //   145: invokevirtual   android/graphics/Bitmap.getHeight:()I
                    //   148: aload_1        
                    //   149: aload_0        
                    //   150: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fittingMode:I
                    //   153: invokestatic    android/support/v4/print/PrintHelper$PrintHelperApi19.access$200:(Landroid/support/v4/print/PrintHelper$PrintHelperApi19;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
                    //   156: astore          7
                    //   158: aload_0        
                    //   159: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                    //   162: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19.mIsMinMarginsHandlingCorrect:Z
                    //   165: ifne            192
                    //   168: aload           7
                    //   170: aload_1        
                    //   171: getfield        android/graphics/RectF.left:F
                    //   174: aload_1        
                    //   175: getfield        android/graphics/RectF.top:F
                    //   178: invokevirtual   android/graphics/Matrix.postTranslate:(FF)Z
                    //   181: pop            
                    //   182: aload           5
                    //   184: invokevirtual   android/graphics/pdf/PdfDocument$Page.getCanvas:()Landroid/graphics/Canvas;
                    //   187: aload_1        
                    //   188: invokevirtual   android/graphics/Canvas.clipRect:(Landroid/graphics/RectF;)Z
                    //   191: pop            
                    //   192: aload           5
                    //   194: invokevirtual   android/graphics/pdf/PdfDocument$Page.getCanvas:()Landroid/graphics/Canvas;
                    //   197: aload_3        
                    //   198: aload           7
                    //   200: aconst_null    
                    //   201: invokevirtual   android/graphics/Canvas.drawBitmap:(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
                    //   204: aload_2        
                    //   205: aload           5
                    //   207: invokevirtual   android/print/pdf/PrintedPdfDocument.finishPage:(Landroid/graphics/pdf/PdfDocument$Page;)V
                    //   210: aload_0        
                    //   211: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$cancellationSignal:Landroid/os/CancellationSignal;
                    //   214: invokevirtual   android/os/CancellationSignal.isCanceled:()Z
                    //   217: ifne            311
                    //   220: new             Ljava/io/FileOutputStream;
                    //   223: astore_1       
                    //   224: aload_1        
                    //   225: aload_0        
                    //   226: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fileDescriptor:Landroid/os/ParcelFileDescriptor;
                    //   229: invokevirtual   android/os/ParcelFileDescriptor.getFileDescriptor:()Ljava/io/FileDescriptor;
                    //   232: invokespecial   java/io/FileOutputStream.<init>:(Ljava/io/FileDescriptor;)V
                    //   235: aload_2        
                    //   236: aload_1        
                    //   237: invokevirtual   android/print/pdf/PrintedPdfDocument.writeTo:(Ljava/io/OutputStream;)V
                    //   240: aload_2        
                    //   241: invokevirtual   android/print/pdf/PrintedPdfDocument.close:()V
                    //   244: aload_0        
                    //   245: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fileDescriptor:Landroid/os/ParcelFileDescriptor;
                    //   248: ifnonnull       355
                    //   251: aload_0        
                    //   252: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$bitmap:Landroid/graphics/Bitmap;
                    //   255: astore_1       
                    //   256: aload_3        
                    //   257: aload_1        
                    //   258: if_acmpne       369
                    //   261: aconst_null    
                    //   262: areturn        
                    //   263: aconst_null    
                    //   264: areturn        
                    //   265: aconst_null    
                    //   266: areturn        
                    //   267: new             Landroid/graphics/RectF;
                    //   270: dup            
                    //   271: aload           5
                    //   273: invokevirtual   android/graphics/pdf/PdfDocument$Page.getInfo:()Landroid/graphics/pdf/PdfDocument$PageInfo;
                    //   276: invokevirtual   android/graphics/pdf/PdfDocument$PageInfo.getContentRect:()Landroid/graphics/Rect;
                    //   279: invokespecial   android/graphics/RectF.<init>:(Landroid/graphics/Rect;)V
                    //   282: astore_1       
                    //   283: goto            136
                    //   286: astore_1       
                    //   287: aload_2        
                    //   288: invokevirtual   android/print/pdf/PrintedPdfDocument.close:()V
                    //   291: aload_0        
                    //   292: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fileDescriptor:Landroid/os/ParcelFileDescriptor;
                    //   295: ifnonnull       376
                    //   298: aload_3        
                    //   299: aload_0        
                    //   300: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$bitmap:Landroid/graphics/Bitmap;
                    //   303: if_acmpne       390
                    //   306: aload_1        
                    //   307: athrow         
                    //   308: astore_1       
                    //   309: aload_1        
                    //   310: areturn        
                    //   311: aload_2        
                    //   312: invokevirtual   android/print/pdf/PrintedPdfDocument.close:()V
                    //   315: aload_0        
                    //   316: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fileDescriptor:Landroid/os/ParcelFileDescriptor;
                    //   319: ifnonnull       334
                    //   322: aload_0        
                    //   323: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$bitmap:Landroid/graphics/Bitmap;
                    //   326: astore_1       
                    //   327: aload_3        
                    //   328: aload_1        
                    //   329: if_acmpne       348
                    //   332: aconst_null    
                    //   333: areturn        
                    //   334: aload_0        
                    //   335: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fileDescriptor:Landroid/os/ParcelFileDescriptor;
                    //   338: invokevirtual   android/os/ParcelFileDescriptor.close:()V
                    //   341: goto            322
                    //   344: astore_1       
                    //   345: goto            322
                    //   348: aload_3        
                    //   349: invokevirtual   android/graphics/Bitmap.recycle:()V
                    //   352: goto            332
                    //   355: aload_0        
                    //   356: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fileDescriptor:Landroid/os/ParcelFileDescriptor;
                    //   359: invokevirtual   android/os/ParcelFileDescriptor.close:()V
                    //   362: goto            251
                    //   365: astore_1       
                    //   366: goto            251
                    //   369: aload_3        
                    //   370: invokevirtual   android/graphics/Bitmap.recycle:()V
                    //   373: goto            261
                    //   376: aload_0        
                    //   377: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$2.val$fileDescriptor:Landroid/os/ParcelFileDescriptor;
                    //   380: invokevirtual   android/os/ParcelFileDescriptor.close:()V
                    //   383: goto            298
                    //   386: astore_2       
                    //   387: goto            298
                    //   390: aload_3        
                    //   391: invokevirtual   android/graphics/Bitmap.recycle:()V
                    //   394: goto            306
                    //    Exceptions:
                    //  Try           Handler
                    //  Start  End    Start  End    Type                 
                    //  -----  -----  -----  -----  ---------------------
                    //  0      57     308    311    Ljava/lang/Throwable;
                    //  62     136    286    397    Any
                    //  136    192    286    397    Any
                    //  192    240    286    397    Any
                    //  240    251    308    311    Ljava/lang/Throwable;
                    //  251    256    308    311    Ljava/lang/Throwable;
                    //  267    283    286    397    Any
                    //  287    298    308    311    Ljava/lang/Throwable;
                    //  298    306    308    311    Ljava/lang/Throwable;
                    //  306    308    308    311    Ljava/lang/Throwable;
                    //  311    322    308    311    Ljava/lang/Throwable;
                    //  322    327    308    311    Ljava/lang/Throwable;
                    //  334    341    344    348    Ljava/io/IOException;
                    //  334    341    308    311    Ljava/lang/Throwable;
                    //  348    352    308    311    Ljava/lang/Throwable;
                    //  355    362    365    369    Ljava/io/IOException;
                    //  355    362    308    311    Ljava/lang/Throwable;
                    //  369    373    308    311    Ljava/lang/Throwable;
                    //  376    383    386    390    Ljava/io/IOException;
                    //  376    383    308    311    Ljava/lang/Throwable;
                    //  390    394    308    311    Ljava/lang/Throwable;
                    // 
                    // The error that occurred was:
                    // 
                    // java.lang.IndexOutOfBoundsException: Index 191 out of bounds for length 191
                    //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
                    //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
                    //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
                    //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
                    //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
                    //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
                    //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
                
                protected void onPostExecute(final Throwable t) {
                    if (!cancellationSignal.isCanceled()) {
                        if (t != null) {
                            Log.e("PrintHelperApi19", "Error writing printed content", t);
                            printDocumentAdapter$WriteResultCallback.onWriteFailed((CharSequence)null);
                        }
                        else {
                            printDocumentAdapter$WriteResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
                        }
                    }
                    else {
                        printDocumentAdapter$WriteResultCallback.onWriteCancelled();
                    }
                }
            }.execute((Object[])new Void[0]);
        }
        
        protected PrintAttributes$Builder copyAttributes(final PrintAttributes printAttributes) {
            final PrintAttributes$Builder setMinMargins = new PrintAttributes$Builder().setMediaSize(printAttributes.getMediaSize()).setResolution(printAttributes.getResolution()).setMinMargins(printAttributes.getMinMargins());
            if (printAttributes.getColorMode() != 0) {
                setMinMargins.setColorMode(printAttributes.getColorMode());
            }
            return setMinMargins;
        }
        
        @Override
        public int getColorMode() {
            return this.mColorMode;
        }
        
        @Override
        public int getOrientation() {
            if (this.mOrientation != 0) {
                return this.mOrientation;
            }
            return 1;
        }
        
        @Override
        public int getScaleMode() {
            return this.mScaleMode;
        }
        
        @Override
        public void printBitmap(final String s, final Bitmap bitmap, final OnPrintFinishCallback onPrintFinishCallback) {
            if (bitmap != null) {
                final int mScaleMode = this.mScaleMode;
                final PrintManager printManager = (PrintManager)this.mContext.getSystemService("print");
                PrintAttributes$MediaSize mediaSize;
                if (!isPortrait(bitmap)) {
                    mediaSize = PrintAttributes$MediaSize.UNKNOWN_LANDSCAPE;
                }
                else {
                    mediaSize = PrintAttributes$MediaSize.UNKNOWN_PORTRAIT;
                }
                printManager.print(s, (PrintDocumentAdapter)new PrintDocumentAdapter() {
                    private PrintAttributes mAttributes;
                    
                    public void onFinish() {
                        if (onPrintFinishCallback != null) {
                            onPrintFinishCallback.onFinish();
                        }
                    }
                    
                    public void onLayout(final PrintAttributes printAttributes, final PrintAttributes mAttributes, final CancellationSignal cancellationSignal, final PrintDocumentAdapter$LayoutResultCallback printDocumentAdapter$LayoutResultCallback, final Bundle bundle) {
                        boolean b = false;
                        this.mAttributes = mAttributes;
                        final PrintDocumentInfo build = new PrintDocumentInfo$Builder(s).setContentType(1).setPageCount(1).build();
                        if (!mAttributes.equals((Object)printAttributes)) {
                            b = true;
                        }
                        printDocumentAdapter$LayoutResultCallback.onLayoutFinished(build, b);
                    }
                    
                    public void onWrite(final PageRange[] array, final ParcelFileDescriptor parcelFileDescriptor, final CancellationSignal cancellationSignal, final PrintDocumentAdapter$WriteResultCallback printDocumentAdapter$WriteResultCallback) {
                        PrintHelperApi19.this.writeBitmap(this.mAttributes, mScaleMode, bitmap, parcelFileDescriptor, cancellationSignal, printDocumentAdapter$WriteResultCallback);
                    }
                }, new PrintAttributes$Builder().setMediaSize(mediaSize).setColorMode(this.mColorMode).build());
            }
        }
        
        @Override
        public void printBitmap(final String s, final Uri uri, final OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException {
            final PrintDocumentAdapter printDocumentAdapter = new PrintDocumentAdapter() {
                private PrintAttributes mAttributes;
                Bitmap mBitmap = null;
                AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
                final /* synthetic */ int val$fittingMode = PrintHelperApi19.this.mScaleMode;
                
                private void cancelLoad() {
                    synchronized (PrintHelperApi19.this.mLock) {
                        if (PrintHelperApi19.this.mDecodeOptions != null) {
                            PrintHelperApi19.this.mDecodeOptions.requestCancelDecode();
                            PrintHelperApi19.this.mDecodeOptions = null;
                        }
                    }
                }
                
                public void onFinish() {
                    super.onFinish();
                    this.cancelLoad();
                    if (this.mLoadBitmap != null) {
                        this.mLoadBitmap.cancel(true);
                    }
                    if (onPrintFinishCallback != null) {
                        onPrintFinishCallback.onFinish();
                    }
                    if (this.mBitmap != null) {
                        this.mBitmap.recycle();
                        this.mBitmap = null;
                    }
                }
                
                public void onLayout(final PrintAttributes printAttributes, final PrintAttributes mAttributes, final CancellationSignal cancellationSignal, final PrintDocumentAdapter$LayoutResultCallback printDocumentAdapter$LayoutResultCallback, final Bundle bundle) {
                    boolean b = true;
                    Label_0062: {
                        synchronized (this) {
                            this.mAttributes = mAttributes;
                            monitorexit(this);
                            if (!cancellationSignal.isCanceled()) {
                                if (this.mBitmap == null) {
                                    this.mLoadBitmap = (AsyncTask<Uri, Boolean, Bitmap>)new AsyncTask<Uri, Boolean, Bitmap>() {
                                        protected Bitmap doInBackground(final Uri... array) {
                                            try {
                                                return PrintHelperApi19.this.loadConstrainedBitmap(uri);
                                            }
                                            catch (final FileNotFoundException ex) {
                                                return null;
                                            }
                                        }
                                        
                                        protected void onCancelled(final Bitmap bitmap) {
                                            printDocumentAdapter$LayoutResultCallback.onLayoutCancelled();
                                            PrintDocumentAdapter.this.mLoadBitmap = null;
                                        }
                                        
                                        protected void onPostExecute(final Bitmap p0) {
                                            // 
                                            // This method could not be decompiled.
                                            // 
                                            // Original Bytecode:
                                            // 
                                            //     1: istore_2       
                                            //     2: aload_0        
                                            //     3: aload_1        
                                            //     4: invokespecial   android/os/AsyncTask.onPostExecute:(Ljava/lang/Object;)V
                                            //     7: aload_1        
                                            //     8: ifnonnull       42
                                            //    11: aload_1        
                                            //    12: astore_3       
                                            //    13: aload_0        
                                            //    14: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.this$1:Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
                                            //    17: aload_3        
                                            //    18: putfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3.mBitmap:Landroid/graphics/Bitmap;
                                            //    21: aload_3        
                                            //    22: ifnonnull       150
                                            //    25: aload_0        
                                            //    26: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.val$layoutResultCallback:Landroid/print/PrintDocumentAdapter$LayoutResultCallback;
                                            //    29: aconst_null    
                                            //    30: invokevirtual   android/print/PrintDocumentAdapter$LayoutResultCallback.onLayoutFailed:(Ljava/lang/CharSequence;)V
                                            //    33: aload_0        
                                            //    34: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.this$1:Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
                                            //    37: aconst_null    
                                            //    38: putfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3.mLoadBitmap:Landroid/os/AsyncTask;
                                            //    41: return         
                                            //    42: aload_0        
                                            //    43: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.this$1:Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
                                            //    46: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                                            //    49: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19.mPrintActivityRespectsOrientation:Z
                                            //    52: ifne            127
                                            //    55: aload_0        
                                            //    56: monitorenter   
                                            //    57: aload_0        
                                            //    58: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.this$1:Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
                                            //    61: invokestatic    android/support/v4/print/PrintHelper$PrintHelperApi19$3.access$500:(Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;)Landroid/print/PrintAttributes;
                                            //    64: invokevirtual   android/print/PrintAttributes.getMediaSize:()Landroid/print/PrintAttributes$MediaSize;
                                            //    67: astore          4
                                            //    69: aload_0        
                                            //    70: monitorexit    
                                            //    71: aload_1        
                                            //    72: astore_3       
                                            //    73: aload           4
                                            //    75: ifnull          13
                                            //    78: aload_1        
                                            //    79: astore_3       
                                            //    80: aload           4
                                            //    82: invokevirtual   android/print/PrintAttributes$MediaSize.isPortrait:()Z
                                            //    85: aload_1        
                                            //    86: invokestatic    android/support/v4/print/PrintHelper$PrintHelperApi19.access$600:(Landroid/graphics/Bitmap;)Z
                                            //    89: if_icmpeq       13
                                            //    92: new             Landroid/graphics/Matrix;
                                            //    95: dup            
                                            //    96: invokespecial   android/graphics/Matrix.<init>:()V
                                            //    99: astore_3       
                                            //   100: aload_3        
                                            //   101: ldc             90.0
                                            //   103: invokevirtual   android/graphics/Matrix.postRotate:(F)Z
                                            //   106: pop            
                                            //   107: aload_1        
                                            //   108: iconst_0       
                                            //   109: iconst_0       
                                            //   110: aload_1        
                                            //   111: invokevirtual   android/graphics/Bitmap.getWidth:()I
                                            //   114: aload_1        
                                            //   115: invokevirtual   android/graphics/Bitmap.getHeight:()I
                                            //   118: aload_3        
                                            //   119: iconst_1       
                                            //   120: invokestatic    android/graphics/Bitmap.createBitmap:(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
                                            //   123: astore_3       
                                            //   124: goto            13
                                            //   127: aload_0        
                                            //   128: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.this$1:Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
                                            //   131: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3.this$0:Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
                                            //   134: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19.mOrientation:I
                                            //   137: ifeq            55
                                            //   140: aload_1        
                                            //   141: astore_3       
                                            //   142: goto            13
                                            //   145: astore_1       
                                            //   146: aload_0        
                                            //   147: monitorexit    
                                            //   148: aload_1        
                                            //   149: athrow         
                                            //   150: new             Landroid/print/PrintDocumentInfo$Builder;
                                            //   153: dup            
                                            //   154: aload_0        
                                            //   155: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.this$1:Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
                                            //   158: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3.val$jobName:Ljava/lang/String;
                                            //   161: invokespecial   android/print/PrintDocumentInfo$Builder.<init>:(Ljava/lang/String;)V
                                            //   164: iconst_1       
                                            //   165: invokevirtual   android/print/PrintDocumentInfo$Builder.setContentType:(I)Landroid/print/PrintDocumentInfo$Builder;
                                            //   168: iconst_1       
                                            //   169: invokevirtual   android/print/PrintDocumentInfo$Builder.setPageCount:(I)Landroid/print/PrintDocumentInfo$Builder;
                                            //   172: invokevirtual   android/print/PrintDocumentInfo$Builder.build:()Landroid/print/PrintDocumentInfo;
                                            //   175: astore_1       
                                            //   176: aload_0        
                                            //   177: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.val$newPrintAttributes:Landroid/print/PrintAttributes;
                                            //   180: aload_0        
                                            //   181: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.val$oldPrintAttributes:Landroid/print/PrintAttributes;
                                            //   184: invokevirtual   android/print/PrintAttributes.equals:(Ljava/lang/Object;)Z
                                            //   187: ifeq            202
                                            //   190: aload_0        
                                            //   191: getfield        android/support/v4/print/PrintHelper$PrintHelperApi19$3$1.val$layoutResultCallback:Landroid/print/PrintDocumentAdapter$LayoutResultCallback;
                                            //   194: aload_1        
                                            //   195: iload_2        
                                            //   196: invokevirtual   android/print/PrintDocumentAdapter$LayoutResultCallback.onLayoutFinished:(Landroid/print/PrintDocumentInfo;Z)V
                                            //   199: goto            33
                                            //   202: iconst_1       
                                            //   203: istore_2       
                                            //   204: goto            190
                                            //    Exceptions:
                                            //  Try           Handler
                                            //  Start  End    Start  End    Type
                                            //  -----  -----  -----  -----  ----
                                            //  57     71     145    150    Any
                                            //  146    148    145    150    Any
                                            // 
                                            // The error that occurred was:
                                            // 
                                            // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(getfield:int(PrintHelperApi19::mOrientation, getfield:PrintHelperApi19(PrintHelper$PrintHelperApi19$3::this$0, getfield:PrintHelper$PrintHelperApi19$3(PrintHelper$PrintHelperApi19$3$1::this$1, this:PrintHelper$PrintHelperApi19$3$1))), ldc:int(0))
                                            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                                            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
                                            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
                                            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:425)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:425)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformSynchronized(AstMethodBodyBuilder.java:523)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:360)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                                            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                                            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
                                        
                                        protected void onPreExecute() {
                                            cancellationSignal.setOnCancelListener((CancellationSignal$OnCancelListener)new CancellationSignal$OnCancelListener() {
                                                public void onCancel() {
                                                    PrintHelper$PrintHelperApi19$3.this.cancelLoad();
                                                    AsyncTask.this.cancel(false);
                                                }
                                            });
                                        }
                                    }.execute((Object[])new Uri[0]);
                                    return;
                                }
                                break Label_0062;
                            }
                        }
                        printDocumentAdapter$LayoutResultCallback.onLayoutCancelled();
                        return;
                    }
                    final PrintDocumentInfo build = new PrintDocumentInfo$Builder(s).setContentType(1).setPageCount(1).build();
                    final Throwable t;
                    if (mAttributes.equals((Object)t)) {
                        b = false;
                    }
                    printDocumentAdapter$LayoutResultCallback.onLayoutFinished(build, b);
                }
                
                public void onWrite(final PageRange[] array, final ParcelFileDescriptor parcelFileDescriptor, final CancellationSignal cancellationSignal, final PrintDocumentAdapter$WriteResultCallback printDocumentAdapter$WriteResultCallback) {
                    PrintHelperApi19.this.writeBitmap(this.mAttributes, this.val$fittingMode, this.mBitmap, parcelFileDescriptor, cancellationSignal, printDocumentAdapter$WriteResultCallback);
                }
            };
            final PrintManager printManager = (PrintManager)this.mContext.getSystemService("print");
            final PrintAttributes$Builder printAttributes$Builder = new PrintAttributes$Builder();
            printAttributes$Builder.setColorMode(this.mColorMode);
            if (this.mOrientation != 1 && this.mOrientation != 0) {
                if (this.mOrientation == 2) {
                    printAttributes$Builder.setMediaSize(PrintAttributes$MediaSize.UNKNOWN_PORTRAIT);
                }
            }
            else {
                printAttributes$Builder.setMediaSize(PrintAttributes$MediaSize.UNKNOWN_LANDSCAPE);
            }
            printManager.print(s, (PrintDocumentAdapter)printDocumentAdapter, printAttributes$Builder.build());
        }
        
        @Override
        public void setColorMode(final int mColorMode) {
            this.mColorMode = mColorMode;
        }
        
        @Override
        public void setOrientation(final int mOrientation) {
            this.mOrientation = mOrientation;
        }
        
        @Override
        public void setScaleMode(final int mScaleMode) {
            this.mScaleMode = mScaleMode;
        }
    }
    
    interface PrintHelperVersionImpl
    {
        int getColorMode();
        
        int getOrientation();
        
        int getScaleMode();
        
        void printBitmap(final String p0, final Bitmap p1, final OnPrintFinishCallback p2);
        
        void printBitmap(final String p0, final Uri p1, final OnPrintFinishCallback p2) throws FileNotFoundException;
        
        void setColorMode(final int p0);
        
        void setOrientation(final int p0);
        
        void setScaleMode(final int p0);
    }
    
    @RequiresApi(20)
    private static class PrintHelperApi20 extends PrintHelperApi19
    {
        PrintHelperApi20(final Context context) {
            super(context);
            this.mPrintActivityRespectsOrientation = false;
        }
    }
    
    @RequiresApi(23)
    private static class PrintHelperApi23 extends PrintHelperApi20
    {
        PrintHelperApi23(final Context context) {
            super(context);
            this.mIsMinMarginsHandlingCorrect = false;
        }
        
        @Override
        protected PrintAttributes$Builder copyAttributes(final PrintAttributes printAttributes) {
            final PrintAttributes$Builder copyAttributes = super.copyAttributes(printAttributes);
            if (printAttributes.getDuplexMode() != 0) {
                copyAttributes.setDuplexMode(printAttributes.getDuplexMode());
            }
            return copyAttributes;
        }
    }
    
    @RequiresApi(24)
    private static class PrintHelperApi24 extends PrintHelperApi23
    {
        PrintHelperApi24(final Context context) {
            super(context);
            this.mIsMinMarginsHandlingCorrect = true;
            this.mPrintActivityRespectsOrientation = true;
        }
    }
    
    private static final class PrintHelperStub implements PrintHelperVersionImpl
    {
        int mColorMode;
        int mOrientation;
        int mScaleMode;
        
        private PrintHelperStub() {
            this.mScaleMode = 2;
            this.mColorMode = 2;
            this.mOrientation = 1;
        }
        
        @Override
        public int getColorMode() {
            return this.mColorMode;
        }
        
        @Override
        public int getOrientation() {
            return this.mOrientation;
        }
        
        @Override
        public int getScaleMode() {
            return this.mScaleMode;
        }
        
        @Override
        public void printBitmap(final String s, final Bitmap bitmap, final OnPrintFinishCallback onPrintFinishCallback) {
        }
        
        @Override
        public void printBitmap(final String s, final Uri uri, final OnPrintFinishCallback onPrintFinishCallback) {
        }
        
        @Override
        public void setColorMode(final int mColorMode) {
            this.mColorMode = mColorMode;
        }
        
        @Override
        public void setOrientation(final int mOrientation) {
            this.mOrientation = mOrientation;
        }
        
        @Override
        public void setScaleMode(final int mScaleMode) {
            this.mScaleMode = mScaleMode;
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private @interface ScaleMode {
    }
}
