// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import java.util.Iterator;
import java.util.Map;
import android.net.Uri$Builder;
import android.text.TextUtils;
import android.database.MatrixCursor;
import android.database.Cursor;
import java.io.FileNotFoundException;
import android.os.ParcelFileDescriptor;
import android.content.ContentValues;
import android.webkit.MimeTypeMap;
import android.content.pm.ProviderInfo;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.net.Uri;
import android.content.Context;
import android.support.annotation.GuardedBy;
import java.util.HashMap;
import java.io.File;
import android.content.ContentProvider;

public class FileProvider extends ContentProvider
{
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PATH = "path";
    private static final String[] COLUMNS;
    private static final File DEVICE_ROOT;
    private static final String META_DATA_FILE_PROVIDER_PATHS = "android.support.FILE_PROVIDER_PATHS";
    private static final String TAG_CACHE_PATH = "cache-path";
    private static final String TAG_EXTERNAL = "external-path";
    private static final String TAG_EXTERNAL_CACHE = "external-cache-path";
    private static final String TAG_EXTERNAL_FILES = "external-files-path";
    private static final String TAG_FILES_PATH = "files-path";
    private static final String TAG_ROOT_PATH = "root-path";
    @GuardedBy("sCache")
    private static HashMap<String, PathStrategy> sCache;
    private PathStrategy mStrategy;
    
    static {
        COLUMNS = new String[] { "_display_name", "_size" };
        DEVICE_ROOT = new File("/");
        FileProvider.sCache = new HashMap<String, PathStrategy>();
    }
    
    private static File buildPath(File parent, final String... array) {
        for (final String child : array) {
            if (child != null) {
                parent = new File(parent, child);
            }
        }
        return parent;
    }
    
    private static Object[] copyOf(final Object[] array, final int n) {
        final Object[] array2 = new Object[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    private static String[] copyOf(final String[] array, final int n) {
        final String[] array2 = new String[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    private static PathStrategy getPathStrategy(final Context p0, final String p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore_2       
        //     4: aload_2        
        //     5: monitorenter   
        //     6: getstatic       android/support/v4/content/FileProvider.sCache:Ljava/util/HashMap;
        //     9: aload_1        
        //    10: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    13: checkcast       Landroid/support/v4/content/FileProvider$PathStrategy;
        //    16: astore_3       
        //    17: aload_3        
        //    18: ifnull          27
        //    21: aload_3        
        //    22: astore_0       
        //    23: aload_2        
        //    24: monitorexit    
        //    25: aload_0        
        //    26: areturn        
        //    27: aload_0        
        //    28: aload_1        
        //    29: invokestatic    android/support/v4/content/FileProvider.parsePathStrategy:(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/content/FileProvider$PathStrategy;
        //    32: astore_0       
        //    33: getstatic       android/support/v4/content/FileProvider.sCache:Ljava/util/HashMap;
        //    36: aload_1        
        //    37: aload_0        
        //    38: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    41: pop            
        //    42: goto            23
        //    45: astore_0       
        //    46: aload_2        
        //    47: monitorexit    
        //    48: aload_0        
        //    49: athrow         
        //    50: astore_1       
        //    51: new             Ljava/lang/IllegalArgumentException;
        //    54: astore_0       
        //    55: aload_0        
        //    56: ldc             "Failed to parse android.support.FILE_PROVIDER_PATHS meta-data"
        //    58: aload_1        
        //    59: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    62: aload_0        
        //    63: athrow         
        //    64: astore_1       
        //    65: new             Ljava/lang/IllegalArgumentException;
        //    68: astore_0       
        //    69: aload_0        
        //    70: ldc             "Failed to parse android.support.FILE_PROVIDER_PATHS meta-data"
        //    72: aload_1        
        //    73: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    76: aload_0        
        //    77: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                   
        //  -----  -----  -----  -----  ---------------------------------------
        //  6      17     45     50     Any
        //  23     25     45     50     Any
        //  27     33     50     64     Ljava/io/IOException;
        //  27     33     64     78     Lorg/xmlpull/v1/XmlPullParserException;
        //  27     33     45     50     Any
        //  33     42     45     50     Any
        //  46     48     45     50     Any
        //  51     64     45     50     Any
        //  65     78     45     50     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0027:
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
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static Uri getUriForFile(final Context context, final String s, final File file) {
        return getPathStrategy(context, s).getUriForFile(file);
    }
    
    private static int modeToMode(final String str) {
        int n;
        if (!"r".equals(str)) {
            if (!"w".equals(str) && !"wt".equals(str)) {
                if (!"wa".equals(str)) {
                    if (!"rw".equals(str)) {
                        if (!"rwt".equals(str)) {
                            throw new IllegalArgumentException("Invalid mode: " + str);
                        }
                        n = 1006632960;
                    }
                    else {
                        n = 939524096;
                    }
                }
                else {
                    n = 704643072;
                }
            }
            else {
                n = 738197504;
            }
        }
        else {
            n = 268435456;
        }
        return n;
    }
    
    private static PathStrategy parsePathStrategy(final Context context, String name) throws IOException, XmlPullParserException {
        final SimplePathStrategy simplePathStrategy = new SimplePathStrategy(name);
        final XmlResourceParser loadXmlMetaData = context.getPackageManager().resolveContentProvider(name, 128).loadXmlMetaData(context.getPackageManager(), "android.support.FILE_PROVIDER_PATHS");
        if (loadXmlMetaData != null) {
            while (true) {
                final int next = loadXmlMetaData.next();
                if (next == 1) {
                    break;
                }
                if (next != 2) {
                    continue;
                }
                name = loadXmlMetaData.getName();
                final String attributeValue = loadXmlMetaData.getAttributeValue((String)null, "name");
                final String attributeValue2 = loadXmlMetaData.getAttributeValue((String)null, "path");
                File file;
                if (!"root-path".equals(name)) {
                    if (!"files-path".equals(name)) {
                        if (!"cache-path".equals(name)) {
                            if (!"external-path".equals(name)) {
                                if (!"external-files-path".equals(name)) {
                                    if (!"external-cache-path".equals(name)) {
                                        file = null;
                                    }
                                    else {
                                        final File[] externalCacheDirs = ContextCompat.getExternalCacheDirs(context);
                                        if (externalCacheDirs.length <= 0) {
                                            file = null;
                                        }
                                        else {
                                            file = externalCacheDirs[0];
                                        }
                                    }
                                }
                                else {
                                    final File[] externalFilesDirs = ContextCompat.getExternalFilesDirs(context, null);
                                    if (externalFilesDirs.length <= 0) {
                                        file = null;
                                    }
                                    else {
                                        file = externalFilesDirs[0];
                                    }
                                }
                            }
                            else {
                                file = Environment.getExternalStorageDirectory();
                            }
                        }
                        else {
                            file = context.getCacheDir();
                        }
                    }
                    else {
                        file = context.getFilesDir();
                    }
                }
                else {
                    file = FileProvider.DEVICE_ROOT;
                }
                if (file == null) {
                    continue;
                }
                simplePathStrategy.addRoot(attributeValue, buildPath(file, attributeValue2));
            }
            return (PathStrategy)simplePathStrategy;
        }
        throw new IllegalArgumentException("Missing android.support.FILE_PROVIDER_PATHS meta-data");
    }
    
    public void attachInfo(final Context context, final ProviderInfo providerInfo) {
        super.attachInfo(context, providerInfo);
        if (providerInfo.exported) {
            throw new SecurityException("Provider must not be exported");
        }
        if (providerInfo.grantUriPermissions) {
            this.mStrategy = getPathStrategy(context, providerInfo.authority);
            return;
        }
        throw new SecurityException("Provider must grant uri permissions");
    }
    
    public int delete(final Uri uri, final String s, final String[] array) {
        int n = 0;
        if (this.mStrategy.getFileForUri(uri).delete()) {
            n = 1;
        }
        return n;
    }
    
    public String getType(final Uri uri) {
        final File fileForUri = this.mStrategy.getFileForUri(uri);
        final int lastIndex = fileForUri.getName().lastIndexOf(46);
        if (lastIndex >= 0) {
            final String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileForUri.getName().substring(lastIndex + 1));
            if (mimeTypeFromExtension != null) {
                return mimeTypeFromExtension;
            }
        }
        return "application/octet-stream";
    }
    
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        throw new UnsupportedOperationException("No external inserts");
    }
    
    public boolean onCreate() {
        return true;
    }
    
    public ParcelFileDescriptor openFile(final Uri uri, final String s) throws FileNotFoundException {
        return ParcelFileDescriptor.open(this.mStrategy.getFileForUri(uri), modeToMode(s));
    }
    
    public Cursor query(final Uri uri, String[] columns, final String s, final String[] array, final String s2) {
        int n = 0;
        final File fileForUri = this.mStrategy.getFileForUri(uri);
        if (columns == null) {
            columns = FileProvider.COLUMNS;
        }
        final String[] array2 = new String[columns.length];
        final Object[] array3 = new Object[columns.length];
        for (final String s3 : columns) {
            if (!"_display_name".equals(s3)) {
                if ("_size".equals(s3)) {
                    array2[n] = "_size";
                    array3[n] = fileForUri.length();
                    ++n;
                }
            }
            else {
                array2[n] = "_display_name";
                array3[n] = fileForUri.getName();
                ++n;
            }
        }
        final String[] copy = copyOf(array2, n);
        final Object[] copy2 = copyOf(array3, n);
        final MatrixCursor matrixCursor = new MatrixCursor(copy, 1);
        matrixCursor.addRow(copy2);
        return (Cursor)matrixCursor;
    }
    
    public int update(final Uri uri, final ContentValues contentValues, final String s, final String[] array) {
        throw new UnsupportedOperationException("No external updates");
    }
    
    interface PathStrategy
    {
        File getFileForUri(final Uri p0);
        
        Uri getUriForFile(final File p0);
    }
    
    static class SimplePathStrategy implements PathStrategy
    {
        private final String mAuthority;
        private final HashMap<String, File> mRoots;
        
        public SimplePathStrategy(final String mAuthority) {
            this.mRoots = new HashMap<String, File>();
            this.mAuthority = mAuthority;
        }
        
        public void addRoot(final String key, final File obj) {
            Label_0023: {
                if (TextUtils.isEmpty((CharSequence)key)) {
                    break Label_0023;
                }
                try {
                    this.mRoots.put(key, obj.getCanonicalFile());
                    return;
                    throw new IllegalArgumentException("Name must not be empty");
                }
                catch (final IOException cause) {
                    throw new IllegalArgumentException("Failed to resolve canonical path for " + obj, cause);
                }
            }
        }
        
        @Override
        public File getFileForUri(Uri uri) {
            final String encodedPath = uri.getEncodedPath();
            final int index = encodedPath.indexOf(47, 1);
            final String decode = Uri.decode(encodedPath.substring(1, index));
            final String decode2 = Uri.decode(encodedPath.substring(index + 1));
            final File parent = this.mRoots.get(decode);
            while (true) {
                if (parent != null) {
                    uri = (Uri)new File(parent, decode2);
                    try {
                        final File canonicalFile = ((File)uri).getCanonicalFile();
                        if (canonicalFile.getPath().startsWith(parent.getPath())) {
                            return canonicalFile;
                        }
                        throw new SecurityException("Resolved path jumped beyond configured root");
                        throw new IllegalArgumentException("Unable to find configured root for " + uri);
                    }
                    catch (final IOException ex) {
                        throw new IllegalArgumentException("Failed to resolve canonical path for " + uri);
                    }
                    throw new SecurityException("Resolved path jumped beyond configured root");
                }
                continue;
            }
        }
        
        @Override
        public Uri getUriForFile(File string) {
            String canonicalPath = null;
        Label_0070_Outer:
            while (true) {
                while (true) {
                    String path = null;
                Label_0279:
                    while (true) {
                        Iterator<Map.Entry<String, File>> iterator;
                        try {
                            canonicalPath = string.getCanonicalPath();
                            iterator = this.mRoots.entrySet().iterator();
                            string = null;
                            if (!iterator.hasNext()) {
                                if (string == null) {
                                    break;
                                }
                                path = ((Map.Entry<K, File>)string).getValue().getPath();
                                if (!path.endsWith("/")) {
                                    final String s = canonicalPath.substring(path.length() + 1);
                                    string = (File)(Uri.encode((String)((Map.Entry<String, V>)string).getKey()) + '/' + Uri.encode(s, "/"));
                                    return new Uri$Builder().scheme("content").authority(this.mAuthority).encodedPath((String)string).build();
                                }
                                break Label_0279;
                            }
                        }
                        catch (final IOException ex) {
                            throw new IllegalArgumentException("Failed to resolve canonical path for " + string);
                        }
                        final Map.Entry<K, File> entry = iterator.next();
                        final String path2 = entry.getValue().getPath();
                        Object o;
                        if (!canonicalPath.startsWith(path2)) {
                            o = string;
                        }
                        else {
                            o = entry;
                            if (string != null) {
                                o = entry;
                                if (path2.length() <= ((Map.Entry<K, File>)string).getValue().getPath().length()) {
                                    o = string;
                                }
                            }
                        }
                        string = (File)o;
                        continue Label_0070_Outer;
                    }
                    final String s = canonicalPath.substring(path.length());
                    continue;
                }
            }
            throw new IllegalArgumentException("Failed to find configured root that contains " + canonicalPath);
        }
    }
}
