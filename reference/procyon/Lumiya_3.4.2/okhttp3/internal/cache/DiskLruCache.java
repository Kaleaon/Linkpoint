// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.cache;

import java.util.Arrays;
import okio.Source;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.ArrayList;
import okhttp3.internal.platform.Platform;
import java.io.Serializable;
import okio.BufferedSource;
import java.io.EOFException;
import java.util.Iterator;
import java.io.FileNotFoundException;
import okio.Okio;
import java.io.IOException;
import okio.Sink;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.internal.Util;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.LinkedHashMap;
import okio.BufferedSink;
import okhttp3.internal.io.FileSystem;
import java.util.concurrent.Executor;
import java.io.File;
import java.util.regex.Pattern;
import java.io.Flushable;
import java.io.Closeable;

public final class DiskLruCache implements Closeable, Flushable
{
    static final long ANY_SEQUENCE_NUMBER = -1L;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final Pattern LEGAL_KEY_PATTERN;
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Runnable cleanupRunnable;
    boolean closed;
    final File directory;
    private final Executor executor;
    final FileSystem fileSystem;
    boolean hasJournalErrors;
    boolean initialized;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    BufferedSink journalWriter;
    final LinkedHashMap<String, Entry> lruEntries;
    private long maxSize;
    boolean mostRecentRebuildFailed;
    boolean mostRecentTrimFailed;
    private long nextSequenceNumber;
    int redundantOpCount;
    private long size;
    final int valueCount;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!DiskLruCache.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
        LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
    }
    
    DiskLruCache(final FileSystem fileSystem, final File file, final int appVersion, final int valueCount, final long maxSize, final Executor executor) {
        this.size = 0L;
        this.lruEntries = new LinkedHashMap<String, Entry>(0, 0.75f, true);
        this.nextSequenceNumber = 0L;
        this.cleanupRunnable = new Runnable() {
            @Override
            public void run() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: istore_1       
                //     2: aload_0        
                //     3: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //     6: astore_2       
                //     7: aload_2        
                //     8: monitorenter   
                //     9: aload_0        
                //    10: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //    13: getfield        okhttp3/internal/cache/DiskLruCache.initialized:Z
                //    16: ifeq            55
                //    19: aload_0        
                //    20: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //    23: getfield        okhttp3/internal/cache/DiskLruCache.closed:Z
                //    26: istore_3       
                //    27: iload_1        
                //    28: iload_3        
                //    29: ior            
                //    30: ifne            60
                //    33: aload_0        
                //    34: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //    37: invokevirtual   okhttp3/internal/cache/DiskLruCache.trimToSize:()V
                //    40: aload_0        
                //    41: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //    44: invokevirtual   okhttp3/internal/cache/DiskLruCache.journalRebuildRequired:()Z
                //    47: istore_3       
                //    48: iload_3        
                //    49: ifne            83
                //    52: aload_2        
                //    53: monitorexit    
                //    54: return         
                //    55: iconst_1       
                //    56: istore_1       
                //    57: goto            19
                //    60: aload_2        
                //    61: monitorexit    
                //    62: return         
                //    63: astore          4
                //    65: aload_0        
                //    66: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //    69: iconst_1       
                //    70: putfield        okhttp3/internal/cache/DiskLruCache.mostRecentTrimFailed:Z
                //    73: goto            40
                //    76: astore          4
                //    78: aload_2        
                //    79: monitorexit    
                //    80: aload           4
                //    82: athrow         
                //    83: aload_0        
                //    84: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //    87: invokevirtual   okhttp3/internal/cache/DiskLruCache.rebuildJournal:()V
                //    90: aload_0        
                //    91: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //    94: iconst_0       
                //    95: putfield        okhttp3/internal/cache/DiskLruCache.redundantOpCount:I
                //    98: goto            52
                //   101: astore          4
                //   103: aload_0        
                //   104: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //   107: iconst_1       
                //   108: putfield        okhttp3/internal/cache/DiskLruCache.mostRecentRebuildFailed:Z
                //   111: aload_0        
                //   112: getfield        okhttp3/internal/cache/DiskLruCache$1.this$0:Lokhttp3/internal/cache/DiskLruCache;
                //   115: invokestatic    okio/Okio.blackhole:()Lokio/Sink;
                //   118: invokestatic    okio/Okio.buffer:(Lokio/Sink;)Lokio/BufferedSink;
                //   121: putfield        okhttp3/internal/cache/DiskLruCache.journalWriter:Lokio/BufferedSink;
                //   124: goto            52
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                 
                //  -----  -----  -----  -----  ---------------------
                //  9      19     76     83     Any
                //  19     27     76     83     Any
                //  33     40     63     76     Ljava/io/IOException;
                //  33     40     76     83     Any
                //  40     48     101    127    Ljava/io/IOException;
                //  40     48     76     83     Any
                //  52     54     76     83     Any
                //  60     62     76     83     Any
                //  65     73     76     83     Any
                //  78     80     76     83     Any
                //  83     98     101    127    Ljava/io/IOException;
                //  83     98     76     83     Any
                //  103    124    76     83     Any
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0040:
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
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
        };
        this.fileSystem = fileSystem;
        this.directory = file;
        this.appVersion = appVersion;
        this.journalFile = new File(file, "journal");
        this.journalFileTmp = new File(file, "journal.tmp");
        this.journalFileBackup = new File(file, "journal.bkp");
        this.valueCount = valueCount;
        this.maxSize = maxSize;
        this.executor = executor;
    }
    
    private void checkNotClosed() {
        synchronized (this) {
            if (!this.isClosed()) {
                return;
            }
            throw new IllegalStateException("cache is closed");
        }
    }
    
    public static DiskLruCache create(final FileSystem fileSystem, final File file, final int n, final int n2, final long n3) {
        int n4;
        if (n3 > 0L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (n2 > 0) {
            return new DiskLruCache(fileSystem, file, n, n2, n3, new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Util.threadFactory("OkHttp DiskLruCache", true)));
        }
        throw new IllegalArgumentException("valueCount <= 0");
    }
    
    private BufferedSink newJournalWriter() throws FileNotFoundException {
        return Okio.buffer(new FaultHidingSink(this.fileSystem.appendingSink(this.journalFile)) {
            static {
                boolean $assertionsDisabled2 = false;
                if (!DiskLruCache.class.desiredAssertionStatus()) {
                    $assertionsDisabled2 = true;
                }
                $assertionsDisabled = $assertionsDisabled2;
            }
            
            @Override
            protected void onException(final IOException ex) {
                assert Thread.holdsLock(DiskLruCache.this);
                DiskLruCache.this.hasJournalErrors = true;
            }
        });
    }
    
    private void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        final Iterator<Entry> iterator = this.lruEntries.values().iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.currentEditor != null) {
                entry.currentEditor = null;
                for (int i = 0; i < this.valueCount; ++i) {
                    this.fileSystem.delete(entry.cleanFiles[i]);
                    this.fileSystem.delete(entry.dirtyFiles[i]);
                }
                iterator.remove();
            }
            else {
                for (int j = 0; j < this.valueCount; ++j) {
                    this.size += entry.lengths[j];
                }
            }
        }
    }
    
    private void readJournal() throws IOException {
        int n;
        BufferedSource buffer;
        while (true) {
            n = 0;
            buffer = Okio.buffer(this.fileSystem.source(this.journalFile));
            while (true) {
                String utf8LineStrict2;
                Serializable utf8LineStrict3;
                String utf8LineStrict4;
                String utf8LineStrict5;
                try {
                    final String utf8LineStrict = buffer.readUtf8LineStrict();
                    utf8LineStrict2 = buffer.readUtf8LineStrict();
                    utf8LineStrict3 = buffer.readUtf8LineStrict();
                    utf8LineStrict4 = buffer.readUtf8LineStrict();
                    utf8LineStrict5 = buffer.readUtf8LineStrict();
                    if (!"libcore.io.DiskLruCache".equals(utf8LineStrict)) {
                        utf8LineStrict3 = new StringBuilder();
                        throw new IOException(((StringBuilder)utf8LineStrict3).append("unexpected journal header: [").append(utf8LineStrict).append(", ").append(utf8LineStrict2).append(", ").append(utf8LineStrict4).append(", ").append(utf8LineStrict5).append("]").toString());
                    }
                }
                finally {
                    Util.closeQuietly(buffer);
                }
                if ("1".equals(utf8LineStrict2) && Integer.toString(this.appVersion).equals(utf8LineStrict3) && Integer.toString(this.valueCount).equals(utf8LineStrict4) && "".equals(utf8LineStrict5)) {
                    break;
                }
                continue;
            }
        }
        try {
            while (true) {
                this.readJournalLine(buffer.readUtf8LineStrict());
                ++n;
            }
        }
        catch (final EOFException ex) {
            this.redundantOpCount = n - this.lruEntries.size();
            if (buffer.exhausted()) {
                this.journalWriter = this.newJournalWriter();
            }
            else {
                this.rebuildJournal();
            }
            Util.closeQuietly(buffer);
        }
    }
    
    private void readJournalLine(final String s) throws IOException {
        final int index = s.indexOf(32);
        if (index != -1) {
            final int beginIndex = index + 1;
            final int index2 = s.indexOf(32, beginIndex);
            String key;
            if (index2 != -1) {
                key = s.substring(beginIndex, index2);
            }
            else {
                key = s.substring(beginIndex);
                if (index == "REMOVE".length() && s.startsWith("REMOVE")) {
                    this.lruEntries.remove(key);
                    return;
                }
            }
            final Entry entry = this.lruEntries.get(key);
            Entry entry2;
            if (entry != null) {
                entry2 = entry;
            }
            else {
                final Entry value = new Entry(key);
                this.lruEntries.put(key, value);
                entry2 = value;
            }
            if (index2 != -1 && index == "CLEAN".length() && s.startsWith("CLEAN")) {
                final String[] split = s.substring(index2 + 1).split(" ");
                entry2.readable = true;
                entry2.currentEditor = null;
                entry2.setLengths(split);
            }
            else if (index2 == -1 && index == "DIRTY".length() && s.startsWith("DIRTY")) {
                entry2.currentEditor = new Editor(entry2);
            }
            else if (index2 != -1 || index != "READ".length() || !s.startsWith("READ")) {
                throw new IOException("unexpected journal line: " + s);
            }
            return;
        }
        throw new IOException("unexpected journal line: " + s);
    }
    
    private void validateKey(final String s) {
        if (DiskLruCache.LEGAL_KEY_PATTERN.matcher(s).matches()) {
            return;
        }
        throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,120}: \"" + s + "\"");
    }
    
    @Override
    public void close() throws IOException {
        int i = 0;
        synchronized (this) {
            if (this.initialized && !this.closed) {
                for (Entry[] array = this.lruEntries.values().toArray(new Entry[this.lruEntries.size()]); i < array.length; ++i) {
                    final Entry entry = array[i];
                    if (entry.currentEditor != null) {
                        entry.currentEditor.abort();
                    }
                }
                this.trimToSize();
                this.journalWriter.close();
                this.journalWriter = null;
                this.closed = true;
                return;
            }
            this.closed = true;
        }
    }
    
    void completeEdit(final Editor editor, boolean journalRebuildRequired) throws IOException {
        int n;
        Entry entry = null;
        int i = 0;
        final Editor editor2;
        File file;
        File file2;
        long n2;
        long size;
        long nextSequenceNumber;
        Label_0026_Outer:Label_0120_Outer:Label_0144_Outer:
        while (true) {
            n = 1;
        Label_0489:
            while (true) {
            Label_0483:
                while (true) {
                Label_0394:
                    while (true) {
                    Label_0279:
                        while (true) {
                            synchronized (this) {
                                entry = editor.entry;
                                if (entry.currentEditor != editor) {
                                    throw new IllegalStateException();
                                }
                                if (!journalRebuildRequired) {
                                    i = 0;
                                    if (i < this.valueCount) {
                                        break Label_0279;
                                    }
                                    ++this.redundantOpCount;
                                    entry.currentEditor = null;
                                    if (entry.readable | journalRebuildRequired) {
                                        break Label_0394;
                                    }
                                    this.lruEntries.remove(entry.key);
                                    this.journalWriter.writeUtf8("REMOVE").writeByte(32);
                                    this.journalWriter.writeUtf8(entry.key);
                                    this.journalWriter.writeByte(10);
                                    this.journalWriter.flush();
                                    if (this.size <= this.maxSize) {
                                        break Label_0483;
                                    }
                                    i = n;
                                    if (i != 0) {
                                        break Label_0489;
                                    }
                                    journalRebuildRequired = this.journalRebuildRequired();
                                    if (!journalRebuildRequired) {
                                        return;
                                    }
                                    break Label_0489;
                                }
                            }
                            if (!entry.readable) {
                                for (int i = 0; i < this.valueCount; ++i) {
                                    if (!editor2.written[i]) {
                                        editor2.abort();
                                        throw new IllegalStateException("Newly created entry didn't create value for index " + i);
                                    }
                                    if (!this.fileSystem.exists(entry.dirtyFiles[i])) {
                                        editor2.abort();
                                        monitorexit(this);
                                        return;
                                    }
                                }
                                continue Label_0026_Outer;
                            }
                            continue Label_0026_Outer;
                        }
                        file = entry.dirtyFiles[i];
                        if (!journalRebuildRequired) {
                            this.fileSystem.delete(file);
                        }
                        else if (this.fileSystem.exists(file)) {
                            file2 = entry.cleanFiles[i];
                            this.fileSystem.rename(file, file2);
                            n2 = entry.lengths[i];
                            size = this.fileSystem.size(file2);
                            entry.lengths[i] = size;
                            this.size = size + (this.size - n2);
                        }
                        ++i;
                        continue Label_0120_Outer;
                    }
                    entry.readable = true;
                    this.journalWriter.writeUtf8("CLEAN").writeByte(32);
                    this.journalWriter.writeUtf8(entry.key);
                    entry.writeLengths(this.journalWriter);
                    this.journalWriter.writeByte(10);
                    if (journalRebuildRequired) {
                        nextSequenceNumber = this.nextSequenceNumber;
                        this.nextSequenceNumber = 1L + nextSequenceNumber;
                        entry.sequenceNumber = nextSequenceNumber;
                        continue Label_0144_Outer;
                    }
                    continue Label_0144_Outer;
                }
                i = 0;
                continue;
            }
            this.executor.execute(this.cleanupRunnable);
        }
    }
    
    public void delete() throws IOException {
        this.close();
        this.fileSystem.deleteContents(this.directory);
    }
    
    public Editor edit(final String s) throws IOException {
        return this.edit(s, -1L);
    }
    
    Editor edit(final String s, final long n) throws IOException {
        synchronized (this) {
            this.initialize();
            this.checkNotClosed();
            this.validateKey(s);
            final Entry entry = this.lruEntries.get(s);
            if (n != -1L && (entry == null || entry.sequenceNumber != n)) {
                return null;
            }
            if (entry != null && entry.currentEditor != null) {
                return null;
            }
            if (this.mostRecentTrimFailed || this.mostRecentRebuildFailed) {
                this.executor.execute(this.cleanupRunnable);
                return null;
            }
            this.journalWriter.writeUtf8("DIRTY").writeByte(32).writeUtf8(s).writeByte(10);
            this.journalWriter.flush();
            if (!this.hasJournalErrors) {
                Object o;
                if (entry != null) {
                    o = entry;
                }
                else {
                    final Entry value = new Entry(s);
                    this.lruEntries.put(s, value);
                    o = value;
                }
                return ((Entry)o).currentEditor = new Editor((Entry)o);
            }
            return null;
        }
    }
    
    public void evictAll() throws IOException {
        int i = 0;
        synchronized (this) {
            this.initialize();
            for (Entry[] array = this.lruEntries.values().toArray(new Entry[this.lruEntries.size()]); i < array.length; ++i) {
                this.removeEntry(array[i]);
            }
            this.mostRecentTrimFailed = false;
        }
    }
    
    @Override
    public void flush() throws IOException {
        synchronized (this) {
            if (this.initialized) {
                this.checkNotClosed();
                this.trimToSize();
                this.journalWriter.flush();
            }
        }
    }
    
    public Snapshot get(final String key) throws IOException {
        synchronized (this) {
            this.initialize();
            this.checkNotClosed();
            this.validateKey(key);
            final Entry entry = this.lruEntries.get(key);
            if (entry == null || !entry.readable) {
                return null;
            }
            final Snapshot snapshot = entry.snapshot();
            if (snapshot != null) {
                ++this.redundantOpCount;
                this.journalWriter.writeUtf8("READ").writeByte(32).writeUtf8(key).writeByte(10);
                if (this.journalRebuildRequired()) {
                    this.executor.execute(this.cleanupRunnable);
                }
                return snapshot;
            }
            return null;
        }
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public long getMaxSize() {
        synchronized (this) {
            return this.maxSize;
        }
    }
    
    public void initialize() throws IOException {
    Label_0047_Outer:
        while (true) {
            while (true) {
                Label_0136: {
                    while (true) {
                        Label_0084: {
                            synchronized (this) {
                                assert Thread.holdsLock(this);
                                if (!this.initialized) {
                                    if (this.fileSystem.exists(this.journalFileBackup)) {
                                        break Label_0084;
                                    }
                                    if (!this.fileSystem.exists(this.journalFile)) {
                                        this.rebuildJournal();
                                        this.initialized = true;
                                        return;
                                    }
                                    break Label_0136;
                                }
                            }
                            break;
                        }
                        if (!this.fileSystem.exists(this.journalFile)) {
                            this.fileSystem.rename(this.journalFileBackup, this.journalFile);
                            continue Label_0047_Outer;
                        }
                        this.fileSystem.delete(this.journalFileBackup);
                        continue Label_0047_Outer;
                    }
                    try {
                        this.readJournal();
                        this.processJournal();
                        this.initialized = true;
                        monitorexit(this);
                        return;
                    }
                    catch (final IOException ex) {
                        Platform.get().log(5, "DiskLruCache " + this.directory + " is corrupt: " + ex.getMessage() + ", removing", ex);
                        this.delete();
                        this.closed = false;
                        continue;
                    }
                }
                break;
            }
        }
        monitorexit(this);
    }
    
    public boolean isClosed() {
        synchronized (this) {
            return this.closed;
        }
    }
    
    boolean journalRebuildRequired() {
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }
    
    void rebuildJournal() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: aload_0        
        //     3: getfield        okhttp3/internal/cache/DiskLruCache.journalWriter:Lokio/BufferedSink;
        //     6: ifnonnull       202
        //     9: aload_0        
        //    10: getfield        okhttp3/internal/cache/DiskLruCache.fileSystem:Lokhttp3/internal/io/FileSystem;
        //    13: aload_0        
        //    14: getfield        okhttp3/internal/cache/DiskLruCache.journalFileTmp:Ljava/io/File;
        //    17: invokeinterface okhttp3/internal/io/FileSystem.sink:(Ljava/io/File;)Lokio/Sink;
        //    22: invokestatic    okio/Okio.buffer:(Lokio/Sink;)Lokio/BufferedSink;
        //    25: astore_1       
        //    26: aload_1        
        //    27: ldc             "libcore.io.DiskLruCache"
        //    29: invokeinterface okio/BufferedSink.writeUtf8:(Ljava/lang/String;)Lokio/BufferedSink;
        //    34: bipush          10
        //    36: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //    41: pop            
        //    42: aload_1        
        //    43: ldc             "1"
        //    45: invokeinterface okio/BufferedSink.writeUtf8:(Ljava/lang/String;)Lokio/BufferedSink;
        //    50: bipush          10
        //    52: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //    57: pop            
        //    58: aload_1        
        //    59: aload_0        
        //    60: getfield        okhttp3/internal/cache/DiskLruCache.appVersion:I
        //    63: i2l            
        //    64: invokeinterface okio/BufferedSink.writeDecimalLong:(J)Lokio/BufferedSink;
        //    69: bipush          10
        //    71: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //    76: pop            
        //    77: aload_1        
        //    78: aload_0        
        //    79: getfield        okhttp3/internal/cache/DiskLruCache.valueCount:I
        //    82: i2l            
        //    83: invokeinterface okio/BufferedSink.writeDecimalLong:(J)Lokio/BufferedSink;
        //    88: bipush          10
        //    90: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //    95: pop            
        //    96: aload_1        
        //    97: bipush          10
        //    99: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //   104: pop            
        //   105: aload_0        
        //   106: getfield        okhttp3/internal/cache/DiskLruCache.lruEntries:Ljava/util/LinkedHashMap;
        //   109: invokevirtual   java/util/LinkedHashMap.values:()Ljava/util/Collection;
        //   112: invokeinterface java/util/Collection.iterator:()Ljava/util/Iterator;
        //   117: astore_2       
        //   118: aload_2        
        //   119: invokeinterface java/util/Iterator.hasNext:()Z
        //   124: istore_3       
        //   125: iload_3        
        //   126: ifne            219
        //   129: aload_1        
        //   130: invokeinterface okio/BufferedSink.close:()V
        //   135: aload_0        
        //   136: getfield        okhttp3/internal/cache/DiskLruCache.fileSystem:Lokhttp3/internal/io/FileSystem;
        //   139: aload_0        
        //   140: getfield        okhttp3/internal/cache/DiskLruCache.journalFile:Ljava/io/File;
        //   143: invokeinterface okhttp3/internal/io/FileSystem.exists:(Ljava/io/File;)Z
        //   148: ifne            333
        //   151: aload_0        
        //   152: getfield        okhttp3/internal/cache/DiskLruCache.fileSystem:Lokhttp3/internal/io/FileSystem;
        //   155: aload_0        
        //   156: getfield        okhttp3/internal/cache/DiskLruCache.journalFileTmp:Ljava/io/File;
        //   159: aload_0        
        //   160: getfield        okhttp3/internal/cache/DiskLruCache.journalFile:Ljava/io/File;
        //   163: invokeinterface okhttp3/internal/io/FileSystem.rename:(Ljava/io/File;Ljava/io/File;)V
        //   168: aload_0        
        //   169: getfield        okhttp3/internal/cache/DiskLruCache.fileSystem:Lokhttp3/internal/io/FileSystem;
        //   172: aload_0        
        //   173: getfield        okhttp3/internal/cache/DiskLruCache.journalFileBackup:Ljava/io/File;
        //   176: invokeinterface okhttp3/internal/io/FileSystem.delete:(Ljava/io/File;)V
        //   181: aload_0        
        //   182: aload_0        
        //   183: invokespecial   okhttp3/internal/cache/DiskLruCache.newJournalWriter:()Lokio/BufferedSink;
        //   186: putfield        okhttp3/internal/cache/DiskLruCache.journalWriter:Lokio/BufferedSink;
        //   189: aload_0        
        //   190: iconst_0       
        //   191: putfield        okhttp3/internal/cache/DiskLruCache.hasJournalErrors:Z
        //   194: aload_0        
        //   195: iconst_0       
        //   196: putfield        okhttp3/internal/cache/DiskLruCache.mostRecentRebuildFailed:Z
        //   199: aload_0        
        //   200: monitorexit    
        //   201: return         
        //   202: aload_0        
        //   203: getfield        okhttp3/internal/cache/DiskLruCache.journalWriter:Lokio/BufferedSink;
        //   206: invokeinterface okio/BufferedSink.close:()V
        //   211: goto            9
        //   214: astore_1       
        //   215: aload_0        
        //   216: monitorexit    
        //   217: aload_1        
        //   218: athrow         
        //   219: aload_2        
        //   220: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   225: checkcast       Lokhttp3/internal/cache/DiskLruCache$Entry;
        //   228: astore          4
        //   230: aload           4
        //   232: getfield        okhttp3/internal/cache/DiskLruCache$Entry.currentEditor:Lokhttp3/internal/cache/DiskLruCache$Editor;
        //   235: ifnonnull       293
        //   238: aload_1        
        //   239: ldc             "CLEAN"
        //   241: invokeinterface okio/BufferedSink.writeUtf8:(Ljava/lang/String;)Lokio/BufferedSink;
        //   246: bipush          32
        //   248: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //   253: pop            
        //   254: aload_1        
        //   255: aload           4
        //   257: getfield        okhttp3/internal/cache/DiskLruCache$Entry.key:Ljava/lang/String;
        //   260: invokeinterface okio/BufferedSink.writeUtf8:(Ljava/lang/String;)Lokio/BufferedSink;
        //   265: pop            
        //   266: aload           4
        //   268: aload_1        
        //   269: invokevirtual   okhttp3/internal/cache/DiskLruCache$Entry.writeLengths:(Lokio/BufferedSink;)V
        //   272: aload_1        
        //   273: bipush          10
        //   275: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //   280: pop            
        //   281: goto            118
        //   284: astore_2       
        //   285: aload_1        
        //   286: invokeinterface okio/BufferedSink.close:()V
        //   291: aload_2        
        //   292: athrow         
        //   293: aload_1        
        //   294: ldc             "DIRTY"
        //   296: invokeinterface okio/BufferedSink.writeUtf8:(Ljava/lang/String;)Lokio/BufferedSink;
        //   301: bipush          32
        //   303: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //   308: pop            
        //   309: aload_1        
        //   310: aload           4
        //   312: getfield        okhttp3/internal/cache/DiskLruCache$Entry.key:Ljava/lang/String;
        //   315: invokeinterface okio/BufferedSink.writeUtf8:(Ljava/lang/String;)Lokio/BufferedSink;
        //   320: pop            
        //   321: aload_1        
        //   322: bipush          10
        //   324: invokeinterface okio/BufferedSink.writeByte:(I)Lokio/BufferedSink;
        //   329: pop            
        //   330: goto            118
        //   333: aload_0        
        //   334: getfield        okhttp3/internal/cache/DiskLruCache.fileSystem:Lokhttp3/internal/io/FileSystem;
        //   337: aload_0        
        //   338: getfield        okhttp3/internal/cache/DiskLruCache.journalFile:Ljava/io/File;
        //   341: aload_0        
        //   342: getfield        okhttp3/internal/cache/DiskLruCache.journalFileBackup:Ljava/io/File;
        //   345: invokeinterface okhttp3/internal/io/FileSystem.rename:(Ljava/io/File;Ljava/io/File;)V
        //   350: goto            151
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  2      9      214    219    Any
        //  9      26     214    219    Any
        //  26     118    284    293    Any
        //  118    125    284    293    Any
        //  129    151    214    219    Any
        //  151    199    214    219    Any
        //  202    211    214    219    Any
        //  219    281    284    293    Any
        //  285    293    214    219    Any
        //  293    330    284    293    Any
        //  333    350    214    219    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0118:
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
    
    public boolean remove(final String key) throws IOException {
        while (true) {
            while (true) {
                Label_0080: {
                    synchronized (this) {
                        this.initialize();
                        this.checkNotClosed();
                        this.validateKey(key);
                        final Entry entry = this.lruEntries.get(key);
                        if (entry != null) {
                            final boolean removeEntry = this.removeEntry(entry);
                            if (removeEntry) {
                                if (this.size <= this.maxSize) {
                                    break Label_0080;
                                }
                                final int n = 1;
                                if (n == 0) {
                                    this.mostRecentTrimFailed = false;
                                }
                            }
                            return removeEntry;
                        }
                        return false;
                    }
                }
                final int n = 0;
                continue;
            }
        }
    }
    
    boolean removeEntry(final Entry entry) throws IOException {
        int i = 0;
        if (entry.currentEditor != null) {
            entry.currentEditor.detach();
        }
        while (i < this.valueCount) {
            this.fileSystem.delete(entry.cleanFiles[i]);
            this.size -= entry.lengths[i];
            entry.lengths[i] = 0L;
            ++i;
        }
        ++this.redundantOpCount;
        this.journalWriter.writeUtf8("REMOVE").writeByte(32).writeUtf8(entry.key).writeByte(10);
        this.lruEntries.remove(entry.key);
        if (this.journalRebuildRequired()) {
            this.executor.execute(this.cleanupRunnable);
        }
        return true;
    }
    
    public void setMaxSize(final long maxSize) {
        synchronized (this) {
            this.maxSize = maxSize;
            if (this.initialized) {
                this.executor.execute(this.cleanupRunnable);
            }
        }
    }
    
    public long size() throws IOException {
        synchronized (this) {
            this.initialize();
            return this.size;
        }
    }
    
    public Iterator<Snapshot> snapshots() throws IOException {
        synchronized (this) {
            this.initialize();
            return new Iterator<Snapshot>() {
                final Iterator<Entry> delegate = new ArrayList<Entry>((Collection<? extends Entry>)DiskLruCache.this.lruEntries.values()).iterator();
                Snapshot nextSnapshot;
                Snapshot removeSnapshot;
                
                @Override
                public boolean hasNext() {
                    if (this.nextSnapshot != null) {
                        return true;
                    }
                    synchronized (DiskLruCache.this) {
                        if (!DiskLruCache.this.closed) {
                            while (this.delegate.hasNext()) {
                                final Snapshot snapshot = this.delegate.next().snapshot();
                                if (snapshot != null) {
                                    this.nextSnapshot = snapshot;
                                    return true;
                                }
                            }
                            return false;
                        }
                        return false;
                    }
                }
                
                @Override
                public Snapshot next() {
                    if (this.hasNext()) {
                        this.removeSnapshot = this.nextSnapshot;
                        this.nextSnapshot = null;
                        return this.removeSnapshot;
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public void remove() {
                    Label_0028: {
                        if (this.removeSnapshot == null) {
                            break Label_0028;
                        }
                        try {
                            DiskLruCache.this.remove(this.removeSnapshot.key);
                            return;
                            throw new IllegalStateException("remove() before next()");
                        }
                        catch (final IOException ex) {}
                        finally {
                            this.removeSnapshot = null;
                        }
                    }
                }
            };
        }
    }
    
    void trimToSize() throws IOException {
        while (true) {
            int n;
            if (this.size <= this.maxSize) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n != 0) {
                break;
            }
            this.removeEntry(this.lruEntries.values().iterator().next());
        }
        this.mostRecentTrimFailed = false;
    }
    
    public final class Editor
    {
        private boolean done;
        final Entry entry;
        final boolean[] written;
        
        Editor(final Entry entry) {
            this.entry = entry;
            boolean[] written;
            if (!entry.readable) {
                written = new boolean[DiskLruCache.this.valueCount];
            }
            else {
                written = null;
            }
            this.written = written;
        }
        
        public void abort() throws IOException {
            while (true) {
                while (true) {
                    synchronized (DiskLruCache.this) {
                        if (this.done) {
                            throw new IllegalStateException();
                        }
                        if (this.entry.currentEditor != this) {
                            this.done = true;
                            return;
                        }
                    }
                    DiskLruCache.this.completeEdit(this, false);
                    continue;
                }
            }
        }
        
        public void abortUnlessCommitted() {
            synchronized (DiskLruCache.this) {
                if (!this.done && this.entry.currentEditor == this) {
                    try {
                        DiskLruCache.this.completeEdit(this, false);
                    }
                    catch (final IOException ex) {}
                }
            }
        }
        
        public void commit() throws IOException {
            while (true) {
                while (true) {
                    synchronized (DiskLruCache.this) {
                        if (this.done) {
                            throw new IllegalStateException();
                        }
                        if (this.entry.currentEditor != this) {
                            this.done = true;
                            return;
                        }
                    }
                    DiskLruCache.this.completeEdit(this, true);
                    continue;
                }
            }
        }
        
        void detach() {
            if (this.entry.currentEditor == this) {
                int i = 0;
                while (i < DiskLruCache.this.valueCount) {
                    while (true) {
                        try {
                            DiskLruCache.this.fileSystem.delete(this.entry.dirtyFiles[i]);
                            ++i;
                        }
                        catch (final IOException ex) {
                            continue;
                        }
                        break;
                    }
                }
                this.entry.currentEditor = null;
            }
        }
        
        public Sink newSink(final int p0) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        okhttp3/internal/cache/DiskLruCache$Editor.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //     4: astore_2       
            //     5: aload_2        
            //     6: monitorenter   
            //     7: aload_0        
            //     8: getfield        okhttp3/internal/cache/DiskLruCache$Editor.done:Z
            //    11: ifne            75
            //    14: aload_0        
            //    15: getfield        okhttp3/internal/cache/DiskLruCache$Editor.entry:Lokhttp3/internal/cache/DiskLruCache$Entry;
            //    18: getfield        okhttp3/internal/cache/DiskLruCache$Entry.currentEditor:Lokhttp3/internal/cache/DiskLruCache$Editor;
            //    21: aload_0        
            //    22: if_acmpne       90
            //    25: aload_0        
            //    26: getfield        okhttp3/internal/cache/DiskLruCache$Editor.entry:Lokhttp3/internal/cache/DiskLruCache$Entry;
            //    29: getfield        okhttp3/internal/cache/DiskLruCache$Entry.readable:Z
            //    32: ifeq            98
            //    35: aload_0        
            //    36: getfield        okhttp3/internal/cache/DiskLruCache$Editor.entry:Lokhttp3/internal/cache/DiskLruCache$Entry;
            //    39: getfield        okhttp3/internal/cache/DiskLruCache$Entry.dirtyFiles:[Ljava/io/File;
            //    42: iload_1        
            //    43: aaload         
            //    44: astore_3       
            //    45: aload_0        
            //    46: getfield        okhttp3/internal/cache/DiskLruCache$Editor.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //    49: getfield        okhttp3/internal/cache/DiskLruCache.fileSystem:Lokhttp3/internal/io/FileSystem;
            //    52: aload_3        
            //    53: invokeinterface okhttp3/internal/io/FileSystem.sink:(Ljava/io/File;)Lokio/Sink;
            //    58: astore          4
            //    60: new             Lokhttp3/internal/cache/DiskLruCache$Editor$1;
            //    63: astore_3       
            //    64: aload_3        
            //    65: aload_0        
            //    66: aload           4
            //    68: invokespecial   okhttp3/internal/cache/DiskLruCache$Editor$1.<init>:(Lokhttp3/internal/cache/DiskLruCache$Editor;Lokio/Sink;)V
            //    71: aload_2        
            //    72: monitorexit    
            //    73: aload_3        
            //    74: areturn        
            //    75: new             Ljava/lang/IllegalStateException;
            //    78: astore_3       
            //    79: aload_3        
            //    80: invokespecial   java/lang/IllegalStateException.<init>:()V
            //    83: aload_3        
            //    84: athrow         
            //    85: astore_3       
            //    86: aload_2        
            //    87: monitorexit    
            //    88: aload_3        
            //    89: athrow         
            //    90: invokestatic    okio/Okio.blackhole:()Lokio/Sink;
            //    93: astore_3       
            //    94: aload_2        
            //    95: monitorexit    
            //    96: aload_3        
            //    97: areturn        
            //    98: aload_0        
            //    99: getfield        okhttp3/internal/cache/DiskLruCache$Editor.written:[Z
            //   102: iload_1        
            //   103: iconst_1       
            //   104: bastore        
            //   105: goto            35
            //   108: astore_3       
            //   109: invokestatic    okio/Okio.blackhole:()Lokio/Sink;
            //   112: astore_3       
            //   113: aload_2        
            //   114: monitorexit    
            //   115: aload_3        
            //   116: areturn        
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                           
            //  -----  -----  -----  -----  -------------------------------
            //  7      35     85     90     Any
            //  35     45     85     90     Any
            //  45     60     108    117    Ljava/io/FileNotFoundException;
            //  45     60     85     90     Any
            //  60     73     85     90     Any
            //  75     85     85     90     Any
            //  86     88     85     90     Any
            //  90     96     85     90     Any
            //  98     105    85     90     Any
            //  109    115    85     90     Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0075:
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
        
        public Source newSource(final int n) {
            while (true) {
                while (true) {
                    synchronized (DiskLruCache.this) {
                        if (this.done) {
                            throw new IllegalStateException();
                        }
                        if (!this.entry.readable) {
                            return null;
                        }
                    }
                    if (this.entry.currentEditor == this) {
                        break;
                    }
                    continue;
                }
            }
            try {
                final Source source = DiskLruCache.this.fileSystem.source(this.entry.cleanFiles[n]);
                final DiskLruCache diskLruCache;
                monitorexit(diskLruCache);
                return source;
            }
            catch (final FileNotFoundException ex) {
                final DiskLruCache diskLruCache;
                monitorexit(diskLruCache);
                return null;
            }
        }
    }
    
    private final class Entry
    {
        final File[] cleanFiles;
        Editor currentEditor;
        final File[] dirtyFiles;
        final String key;
        final long[] lengths;
        boolean readable;
        long sequenceNumber;
        
        Entry(final String s) {
            this.key = s;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            final StringBuilder append = new StringBuilder(s).append('.');
            final int length = append.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; ++i) {
                append.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, append.toString());
                append.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, append.toString());
                append.setLength();
            }
        }
        
        private IOException invalidLengths(final String[] a) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(a));
        }
        
        void setLengths(final String[] array) throws IOException {
            Label_0023: {
                if (array.length != DiskLruCache.this.valueCount) {
                    break Label_0023;
                }
                int i = 0;
                try {
                    while (i < array.length) {
                        this.lengths[i] = Long.parseLong(array[i]);
                        ++i;
                    }
                    return;
                    throw this.invalidLengths(array);
                }
                catch (final NumberFormatException ex) {
                    throw this.invalidLengths(array);
                }
            }
        }
        
        Snapshot snapshot() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     2: aload_0        
            //     3: getfield        okhttp3/internal/cache/DiskLruCache$Entry.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //     6: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
            //     9: ifeq            73
            //    12: aload_0        
            //    13: getfield        okhttp3/internal/cache/DiskLruCache$Entry.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //    16: getfield        okhttp3/internal/cache/DiskLruCache.valueCount:I
            //    19: anewarray       Lokio/Source;
            //    22: astore_2       
            //    23: aload_0        
            //    24: getfield        okhttp3/internal/cache/DiskLruCache$Entry.lengths:[J
            //    27: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
            //    30: checkcast       [J
            //    33: astore_3       
            //    34: iconst_0       
            //    35: istore          4
            //    37: iload           4
            //    39: aload_0        
            //    40: getfield        okhttp3/internal/cache/DiskLruCache$Entry.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //    43: getfield        okhttp3/internal/cache/DiskLruCache.valueCount:I
            //    46: if_icmplt       81
            //    49: new             Lokhttp3/internal/cache/DiskLruCache$Snapshot;
            //    52: dup            
            //    53: aload_0        
            //    54: getfield        okhttp3/internal/cache/DiskLruCache$Entry.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //    57: aload_0        
            //    58: getfield        okhttp3/internal/cache/DiskLruCache$Entry.key:Ljava/lang/String;
            //    61: aload_0        
            //    62: getfield        okhttp3/internal/cache/DiskLruCache$Entry.sequenceNumber:J
            //    65: aload_2        
            //    66: aload_3        
            //    67: invokespecial   okhttp3/internal/cache/DiskLruCache$Snapshot.<init>:(Lokhttp3/internal/cache/DiskLruCache;Ljava/lang/String;J[Lokio/Source;[J)V
            //    70: astore_3       
            //    71: aload_3        
            //    72: areturn        
            //    73: new             Ljava/lang/AssertionError;
            //    76: dup            
            //    77: invokespecial   java/lang/AssertionError.<init>:()V
            //    80: athrow         
            //    81: aload_2        
            //    82: iload           4
            //    84: aload_0        
            //    85: getfield        okhttp3/internal/cache/DiskLruCache$Entry.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //    88: getfield        okhttp3/internal/cache/DiskLruCache.fileSystem:Lokhttp3/internal/io/FileSystem;
            //    91: aload_0        
            //    92: getfield        okhttp3/internal/cache/DiskLruCache$Entry.cleanFiles:[Ljava/io/File;
            //    95: iload           4
            //    97: aaload         
            //    98: invokeinterface okhttp3/internal/io/FileSystem.source:(Ljava/io/File;)Lokio/Source;
            //   103: aastore        
            //   104: iinc            4, 1
            //   107: goto            37
            //   110: aload_2        
            //   111: iload           4
            //   113: aaload         
            //   114: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
            //   117: iinc            4, 1
            //   120: iload           4
            //   122: aload_0        
            //   123: getfield        okhttp3/internal/cache/DiskLruCache$Entry.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //   126: getfield        okhttp3/internal/cache/DiskLruCache.valueCount:I
            //   129: if_icmplt       143
            //   132: aload_0        
            //   133: getfield        okhttp3/internal/cache/DiskLruCache$Entry.this$0:Lokhttp3/internal/cache/DiskLruCache;
            //   136: aload_0        
            //   137: invokevirtual   okhttp3/internal/cache/DiskLruCache.removeEntry:(Lokhttp3/internal/cache/DiskLruCache$Entry;)Z
            //   140: pop            
            //   141: aconst_null    
            //   142: areturn        
            //   143: aload_2        
            //   144: iload           4
            //   146: aaload         
            //   147: ifnonnull       110
            //   150: goto            132
            //   153: astore_2       
            //   154: goto            141
            //   157: astore_3       
            //   158: iload_1        
            //   159: istore          4
            //   161: goto            120
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                           
            //  -----  -----  -----  -----  -------------------------------
            //  37     71     157    164    Ljava/io/FileNotFoundException;
            //  81     104    157    164    Ljava/io/FileNotFoundException;
            //  132    141    153    157    Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
            //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
            //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
            //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
            //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
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
        
        void writeLengths(final BufferedSink bufferedSink) throws IOException {
            final long[] lengths = this.lengths;
            for (int length = lengths.length, i = 0; i < length; ++i) {
                bufferedSink.writeByte(32).writeDecimalLong(lengths[i]);
            }
        }
    }
    
    public final class Snapshot implements Closeable
    {
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;
        private final Source[] sources;
        
        Snapshot(final String key, final long sequenceNumber, final Source[] sources, final long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.sources = sources;
            this.lengths = lengths;
        }
        
        @Override
        public void close() {
            final Source[] sources = this.sources;
            for (int length = sources.length, i = 0; i < length; ++i) {
                Util.closeQuietly(sources[i]);
            }
        }
        
        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }
        
        public long getLength(final int n) {
            return this.lengths[n];
        }
        
        public Source getSource(final int n) {
            return this.sources[n];
        }
        
        public String key() {
            return this.key;
        }
    }
}
