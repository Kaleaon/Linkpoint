package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Beta
public final class Files {
    private static final TreeTraverser<File> FILE_TREE_TRAVERSER = new TreeTraverser<File>() {
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000b, code lost:
            r0 = r2.listFiles();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Iterable<java.io.File> children(java.io.File r2) {
            /*
                r1 = this;
                boolean r0 = r2.isDirectory()
                if (r0 != 0) goto L_0x000b
            L_0x0006:
                java.util.List r0 = java.util.Collections.emptyList()
                return r0
            L_0x000b:
                java.io.File[] r0 = r2.listFiles()
                if (r0 == 0) goto L_0x0006
                java.util.List r0 = java.util.Arrays.asList(r0)
                java.util.List r0 = java.util.Collections.unmodifiableList(r0)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.io.Files.AnonymousClass2.children(java.io.File):java.lang.Iterable");
        }

        public String toString() {
            return "Files.fileTreeTraverser()";
        }
    };
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    private static final class FileByteSink extends ByteSink {
        private final File file;
        private final ImmutableSet<FileWriteMode> modes;

        private FileByteSink(File file2, FileWriteMode... fileWriteModeArr) {
            this.file = (File) Preconditions.checkNotNull(file2);
            this.modes = ImmutableSet.copyOf((E[]) fileWriteModeArr);
        }

        public FileOutputStream openStream() throws IOException {
            return new FileOutputStream(this.file, this.modes.contains(FileWriteMode.APPEND));
        }

        public String toString() {
            return "Files.asByteSink(" + this.file + ", " + this.modes + ")";
        }
    }

    private static final class FileByteSource extends ByteSource {
        private final File file;

        private FileByteSource(File file2) {
            this.file = (File) Preconditions.checkNotNull(file2);
        }

        public FileInputStream openStream() throws IOException {
            return new FileInputStream(this.file);
        }

        public byte[] read() throws IOException {
            Closer create = Closer.create();
            try {
                FileInputStream fileInputStream = (FileInputStream) create.register(openStream());
                byte[] readFile = Files.readFile(fileInputStream, fileInputStream.getChannel().size());
                create.close();
                return readFile;
            } catch (Throwable th) {
                create.close();
                throw th;
            }
        }

        public long size() throws IOException {
            if (this.file.isFile()) {
                return this.file.length();
            }
            throw new FileNotFoundException(this.file.toString());
        }

        public Optional<Long> sizeIfKnown() {
            return !this.file.isFile() ? Optional.absent() : Optional.of(Long.valueOf(this.file.length()));
        }

        public String toString() {
            return "Files.asByteSource(" + this.file + ")";
        }
    }

    private enum FilePredicate implements Predicate<File> {
        IS_DIRECTORY {
            public boolean apply(File file) {
                return file.isDirectory();
            }

            public String toString() {
                return "Files.isDirectory()";
            }
        },
        IS_FILE {
            public boolean apply(File file) {
                return file.isFile();
            }

            public String toString() {
                return "Files.isFile()";
            }
        }
    }

    private Files() {
    }

    public static void append(CharSequence charSequence, File file, Charset charset) throws IOException {
        write(charSequence, file, charset, true);
    }

    public static ByteSink asByteSink(File file, FileWriteMode... fileWriteModeArr) {
        return new FileByteSink(file, fileWriteModeArr);
    }

    public static ByteSource asByteSource(File file) {
        return new FileByteSource(file);
    }

    public static CharSink asCharSink(File file, Charset charset, FileWriteMode... fileWriteModeArr) {
        return asByteSink(file, fileWriteModeArr).asCharSink(charset);
    }

    public static CharSource asCharSource(File file, Charset charset) {
        return asByteSource(file).asCharSource(charset);
    }

    public static void copy(File file, File file2) throws IOException {
        Preconditions.checkArgument(!file.equals(file2), "Source %s and destination %s must be different", file, file2);
        asByteSource(file).copyTo(asByteSink(file2, new FileWriteMode[0]));
    }

    public static void copy(File file, OutputStream outputStream) throws IOException {
        asByteSource(file).copyTo(outputStream);
    }

    public static void copy(File file, Charset charset, Appendable appendable) throws IOException {
        asCharSource(file, charset).copyTo(appendable);
    }

    public static void createParentDirs(File file) throws IOException {
        Preconditions.checkNotNull(file);
        File parentFile = file.getCanonicalFile().getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
            if (!parentFile.isDirectory()) {
                throw new IOException("Unable to create parent directories of " + file);
            }
        }
    }

    public static File createTempDir() {
        File file = new File(System.getProperty("java.io.tmpdir"));
        String str = System.currentTimeMillis() + "-";
        for (int i = 0; i < 10000; i++) {
            File file2 = new File(file, str + i);
            if (file2.mkdir()) {
                return file2;
            }
        }
        throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + str + "0 to " + str + 9999 + ')');
    }

    public static boolean equal(File file, File file2) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(file2);
        if (file == file2 || file.equals(file2)) {
            return true;
        }
        long length = file.length();
        long length2 = file2.length();
        if (length == 0 || length2 == 0 || length == length2) {
            return asByteSource(file).contentEquals(asByteSource(file2));
        }
        return false;
    }

    public static TreeTraverser<File> fileTreeTraverser() {
        return FILE_TREE_TRAVERSER;
    }

    public static String getFileExtension(String str) {
        Preconditions.checkNotNull(str);
        String name = new File(str).getName();
        int lastIndexOf = name.lastIndexOf(46);
        return lastIndexOf != -1 ? name.substring(lastIndexOf + 1) : "";
    }

    public static String getNameWithoutExtension(String str) {
        Preconditions.checkNotNull(str);
        String name = new File(str).getName();
        int lastIndexOf = name.lastIndexOf(46);
        return lastIndexOf != -1 ? name.substring(0, lastIndexOf) : name;
    }

    public static HashCode hash(File file, HashFunction hashFunction) throws IOException {
        return asByteSource(file).hash(hashFunction);
    }

    public static Predicate<File> isDirectory() {
        return FilePredicate.IS_DIRECTORY;
    }

    public static Predicate<File> isFile() {
        return FilePredicate.IS_FILE;
    }

    public static MappedByteBuffer map(File file) throws IOException {
        Preconditions.checkNotNull(file);
        return map(file, FileChannel.MapMode.READ_ONLY);
    }

    public static MappedByteBuffer map(File file, FileChannel.MapMode mapMode) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(mapMode);
        if (file.exists()) {
            return map(file, mapMode, file.length());
        }
        throw new FileNotFoundException(file.toString());
    }

    public static MappedByteBuffer map(File file, FileChannel.MapMode mapMode, long j) throws FileNotFoundException, IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(mapMode);
        Closer create = Closer.create();
        try {
            MappedByteBuffer map = map((RandomAccessFile) create.register(new RandomAccessFile(file, mapMode != FileChannel.MapMode.READ_ONLY ? "rw" : "r")), mapMode, j);
            create.close();
            return map;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    private static MappedByteBuffer map(RandomAccessFile randomAccessFile, FileChannel.MapMode mapMode, long j) throws IOException {
        Closer create = Closer.create();
        try {
            MappedByteBuffer map = ((FileChannel) create.register(randomAccessFile.getChannel())).map(mapMode, 0, j);
            create.close();
            return map;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    private static FileWriteMode[] modes(boolean z) {
        if (!z) {
            return new FileWriteMode[0];
        }
        return new FileWriteMode[]{FileWriteMode.APPEND};
    }

    public static void move(File file, File file2) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(file2);
        Preconditions.checkArgument(!file.equals(file2), "Source %s and destination %s must be different", file, file2);
        if (!file.renameTo(file2)) {
            copy(file, file2);
            if (file.delete()) {
                return;
            }
            if (file2.delete()) {
                throw new IOException("Unable to delete " + file);
            }
            throw new IOException("Unable to delete " + file2);
        }
    }

    public static BufferedReader newReader(File file, Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    }

    public static BufferedWriter newWriter(File file, Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
    }

    public static <T> T readBytes(File file, ByteProcessor<T> byteProcessor) throws IOException {
        return asByteSource(file).read(byteProcessor);
    }

    static byte[] readFile(InputStream inputStream, long j) throws IOException {
        if (j <= 2147483647L) {
            return j == 0 ? ByteStreams.toByteArray(inputStream) : ByteStreams.toByteArray(inputStream, (int) j);
        }
        throw new OutOfMemoryError("file is too large to fit in a byte array: " + j + " bytes");
    }

    public static String readFirstLine(File file, Charset charset) throws IOException {
        return asCharSource(file, charset).readFirstLine();
    }

    public static <T> T readLines(File file, Charset charset, LineProcessor<T> lineProcessor) throws IOException {
        return asCharSource(file, charset).readLines(lineProcessor);
    }

    public static List<String> readLines(File file, Charset charset) throws IOException {
        return (List) readLines(file, charset, new LineProcessor<List<String>>() {
            final List<String> result = Lists.newArrayList();

            public List<String> getResult() {
                return this.result;
            }

            public boolean processLine(String str) {
                this.result.add(str);
                return true;
            }
        });
    }

    public static String simplifyPath(String str) {
        Preconditions.checkNotNull(str);
        if (str.length() == 0) {
            return ".";
        }
        Iterable<String> split = Splitter.on('/').omitEmptyStrings().split(str);
        ArrayList arrayList = new ArrayList();
        for (String next : split) {
            if (!next.equals(".")) {
                if (!next.equals("..")) {
                    arrayList.add(next);
                } else if (arrayList.size() > 0 && !((String) arrayList.get(arrayList.size() - 1)).equals("..")) {
                    arrayList.remove(arrayList.size() - 1);
                } else {
                    arrayList.add("..");
                }
            }
        }
        String join = Joiner.on('/').join((Iterable<?>) arrayList);
        if (str.charAt(0) == '/') {
            join = "/" + join;
        }
        while (join.startsWith("/../")) {
            join = join.substring(3);
        }
        return !join.equals("/..") ? !"".equals(join) ? join : "." : "/";
    }

    public static byte[] toByteArray(File file) throws IOException {
        return asByteSource(file).read();
    }

    public static String toString(File file, Charset charset) throws IOException {
        return asCharSource(file, charset).read();
    }

    public static void touch(File file) throws IOException {
        Preconditions.checkNotNull(file);
        if (!file.createNewFile() && !file.setLastModified(System.currentTimeMillis())) {
            throw new IOException("Unable to update modification time of " + file);
        }
    }

    public static void write(CharSequence charSequence, File file, Charset charset) throws IOException {
        asCharSink(file, charset, new FileWriteMode[0]).write(charSequence);
    }

    private static void write(CharSequence charSequence, File file, Charset charset, boolean z) throws IOException {
        asCharSink(file, charset, modes(z)).write(charSequence);
    }

    public static void write(byte[] bArr, File file) throws IOException {
        asByteSink(file, new FileWriteMode[0]).write(bArr);
    }
}
