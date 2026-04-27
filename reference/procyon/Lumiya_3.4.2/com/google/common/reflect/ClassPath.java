// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import java.util.jar.Attributes;
import javax.annotation.Nullable;
import java.util.jar.Manifest;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.net.URLClassLoader;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.NoSuchElementException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Map;
import java.io.File;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.base.CharMatcher;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import com.google.common.collect.ImmutableSet;
import java.util.logging.Logger;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.annotations.Beta;

@Beta
public final class ClassPath
{
    private static final String CLASS_FILE_NAME_EXTENSION = ".class";
    private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR;
    private static final Predicate<ClassInfo> IS_TOP_LEVEL;
    private static final Logger logger;
    private final ImmutableSet<ResourceInfo> resources;
    
    static {
        logger = Logger.getLogger(ClassPath.class.getName());
        IS_TOP_LEVEL = new Predicate<ClassInfo>() {
            @Override
            public boolean apply(final ClassInfo classInfo) {
                return classInfo.className.indexOf(36) == -1;
            }
        };
        CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
    }
    
    private ClassPath(final ImmutableSet<ResourceInfo> resources) {
        this.resources = resources;
    }
    
    public static ClassPath from(final ClassLoader classLoader) throws IOException {
        final DefaultScanner defaultScanner = new DefaultScanner();
        ((Scanner)defaultScanner).scan(classLoader);
        return new ClassPath(defaultScanner.getResources());
    }
    
    @VisibleForTesting
    static String getClassName(final String s) {
        return s.substring(0, s.length() - ".class".length()).replace('/', '.');
    }
    
    public ImmutableSet<ClassInfo> getAllClasses() {
        return FluentIterable.from(this.resources).filter(ClassInfo.class).toSet();
    }
    
    public ImmutableSet<ResourceInfo> getResources() {
        return this.resources;
    }
    
    public ImmutableSet<ClassInfo> getTopLevelClasses() {
        return FluentIterable.from(this.resources).filter(ClassInfo.class).filter(ClassPath.IS_TOP_LEVEL).toSet();
    }
    
    public ImmutableSet<ClassInfo> getTopLevelClasses(final String anObject) {
        Preconditions.checkNotNull(anObject);
        final ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
        for (final ClassInfo classInfo : this.getTopLevelClasses()) {
            if (classInfo.getPackageName().equals(anObject)) {
                builder.add(classInfo);
            }
        }
        return builder.build();
    }
    
    public ImmutableSet<ClassInfo> getTopLevelClassesRecursive(final String str) {
        Preconditions.checkNotNull(str);
        final String string = str + '.';
        final ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
        for (final ClassInfo classInfo : this.getTopLevelClasses()) {
            if (classInfo.getName().startsWith(string)) {
                builder.add(classInfo);
            }
        }
        return builder.build();
    }
    
    @Beta
    public static final class ClassInfo extends ResourceInfo
    {
        private final String className;
        
        ClassInfo(final String s, final ClassLoader classLoader) {
            super(s, classLoader);
            this.className = ClassPath.getClassName(s);
        }
        
        public String getName() {
            return this.className;
        }
        
        public String getPackageName() {
            return Reflection.getPackageName(this.className);
        }
        
        public String getSimpleName() {
            final int lastIndex = this.className.lastIndexOf(36);
            if (lastIndex != -1) {
                return CharMatcher.DIGIT.trimLeadingFrom(this.className.substring(lastIndex + 1));
            }
            final String packageName = this.getPackageName();
            if (!packageName.isEmpty()) {
                return this.className.substring(packageName.length() + 1);
            }
            return this.className;
        }
        
        public Class<?> load() {
            try {
                return this.loader.loadClass(this.className);
            }
            catch (final ClassNotFoundException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public String toString() {
            return this.className;
        }
    }
    
    @VisibleForTesting
    static final class DefaultScanner extends Scanner
    {
        private final SetMultimap<ClassLoader, String> resources;
        
        DefaultScanner() {
            this.resources = MultimapBuilder.hashKeys().linkedHashSetValues().build();
        }
        
        private void scanDirectory(final File obj, final ClassLoader classLoader, final String s) throws IOException {
            int i = 0;
            final File[] listFiles = obj.listFiles();
            if (listFiles != null) {
                while (i < listFiles.length) {
                    final File file = listFiles[i];
                    final String name = file.getName();
                    if (!file.isDirectory()) {
                        final String string = s + name;
                        if (!string.equals("META-INF/MANIFEST.MF")) {
                            this.resources.get(classLoader).add(string);
                        }
                    }
                    else {
                        this.scanDirectory(file, classLoader, s + name + "/");
                    }
                    ++i;
                }
                return;
            }
            ClassPath.logger.warning("Cannot read directory " + obj);
        }
        
        ImmutableSet<ResourceInfo> getResources() {
            final ImmutableSet.Builder<ResourceInfo> builder = ImmutableSet.builder();
            for (final Map.Entry entry : this.resources.entries()) {
                builder.add(ResourceInfo.of((String)entry.getValue(), (ClassLoader)entry.getKey()));
            }
            return builder.build();
        }
        
        @Override
        protected void scanDirectory(final ClassLoader classLoader, final File file) throws IOException {
            this.scanDirectory(file, classLoader, "");
        }
        
        @Override
        protected void scanJarFile(final ClassLoader classLoader, final JarFile jarFile) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory() && !jarEntry.getName().equals("META-INF/MANIFEST.MF")) {
                    this.resources.get(classLoader).add(jarEntry.getName());
                }
            }
        }
    }
    
    @Beta
    public static class ResourceInfo
    {
        final ClassLoader loader;
        private final String resourceName;
        
        ResourceInfo(final String s, final ClassLoader classLoader) {
            this.resourceName = Preconditions.checkNotNull(s);
            this.loader = Preconditions.checkNotNull(classLoader);
        }
        
        static ResourceInfo of(final String s, final ClassLoader classLoader) {
            if (!s.endsWith(".class")) {
                return new ResourceInfo(s, classLoader);
            }
            return (ResourceInfo)new ClassInfo(s, classLoader);
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = false;
            if (!(o instanceof ResourceInfo)) {
                return false;
            }
            final ResourceInfo resourceInfo = (ResourceInfo)o;
            if (this.resourceName.equals(resourceInfo.resourceName) && this.loader == resourceInfo.loader) {
                b = true;
            }
            return b;
        }
        
        public final String getResourceName() {
            return this.resourceName;
        }
        
        @Override
        public int hashCode() {
            return this.resourceName.hashCode();
        }
        
        @Override
        public String toString() {
            return this.resourceName;
        }
        
        public final URL url() throws NoSuchElementException {
            final URL resource = this.loader.getResource(this.resourceName);
            if (resource != null) {
                return resource;
            }
            throw new NoSuchElementException(this.resourceName);
        }
    }
    
    abstract static class Scanner
    {
        private final Set<File> scannedUris;
        
        Scanner() {
            this.scannedUris = (Set<File>)Sets.newHashSet();
        }
        
        @VisibleForTesting
        static ImmutableMap<File, ClassLoader> getClassPathEntries(final ClassLoader value) {
            final LinkedHashMap<Object, Object> linkedHashMap = Maps.newLinkedHashMap();
            final ClassLoader parent = value.getParent();
            if (parent != null) {
                linkedHashMap.putAll(getClassPathEntries(parent));
            }
            if (value instanceof URLClassLoader) {
                for (final URL url : ((URLClassLoader)value).getURLs()) {
                    if (url.getProtocol().equals("file")) {
                        final File file = new File(url.getFile());
                        if (!linkedHashMap.containsKey(file)) {
                            linkedHashMap.put(file, value);
                        }
                    }
                }
            }
            return ImmutableMap.copyOf((Map<? extends File, ? extends ClassLoader>)linkedHashMap);
        }
        
        @VisibleForTesting
        static URL getClassPathEntry(final File file, final String spec) throws MalformedURLException {
            return new URL(file.toURI().toURL(), spec);
        }
        
        @VisibleForTesting
        static ImmutableSet<File> getClassPathFromManifest(final File file, @Nullable Manifest iterator) {
            if (iterator != null) {
                final ImmutableSet.Builder<File> builder = ImmutableSet.builder();
                final String value = iterator.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
                if (value != null) {
                    iterator = (Manifest)ClassPath.CLASS_PATH_ATTRIBUTE_SEPARATOR.split(value).iterator();
                    while (((Iterator)iterator).hasNext()) {
                        final String str = ((Iterator<String>)iterator).next();
                        try {
                            final URL classPathEntry = getClassPathEntry(file, str);
                            if (!classPathEntry.getProtocol().equals("file")) {
                                continue;
                            }
                            builder.add(new File(classPathEntry.getFile()));
                        }
                        catch (final MalformedURLException ex) {
                            ClassPath.logger.warning("Invalid Class-Path entry: " + str);
                        }
                    }
                }
                return builder.build();
            }
            return ImmutableSet.of();
        }
        
        private void scanFrom(final File file, final ClassLoader classLoader) throws IOException {
            if (file.exists()) {
                if (!file.isDirectory()) {
                    this.scanJar(file, classLoader);
                }
                else {
                    this.scanDirectory(classLoader, file);
                }
            }
        }
        
        private void scanJar(final File p0, final ClassLoader p1) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: dup            
            //     4: aload_1        
            //     5: invokespecial   java/util/jar/JarFile.<init>:(Ljava/io/File;)V
            //     8: astore_3       
            //     9: aload_1        
            //    10: aload_3        
            //    11: invokevirtual   java/util/jar/JarFile.getManifest:()Ljava/util/jar/Manifest;
            //    14: invokestatic    com/google/common/reflect/ClassPath$Scanner.getClassPathFromManifest:(Ljava/io/File;Ljava/util/jar/Manifest;)Lcom/google/common/collect/ImmutableSet;
            //    17: invokevirtual   com/google/common/collect/ImmutableSet.iterator:()Ljava/util/Iterator;
            //    20: astore_1       
            //    21: aload_1        
            //    22: invokeinterface java/util/Iterator.hasNext:()Z
            //    27: ifne            43
            //    30: aload_0        
            //    31: aload_2        
            //    32: aload_3        
            //    33: invokevirtual   com/google/common/reflect/ClassPath$Scanner.scanJarFile:(Ljava/lang/ClassLoader;Ljava/util/jar/JarFile;)V
            //    36: aload_3        
            //    37: invokevirtual   java/util/jar/JarFile.close:()V
            //    40: return         
            //    41: astore_1       
            //    42: return         
            //    43: aload_0        
            //    44: aload_1        
            //    45: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
            //    50: checkcast       Ljava/io/File;
            //    53: aload_2        
            //    54: invokevirtual   com/google/common/reflect/ClassPath$Scanner.scan:(Ljava/io/File;Ljava/lang/ClassLoader;)V
            //    57: goto            21
            //    60: astore_1       
            //    61: aload_3        
            //    62: invokevirtual   java/util/jar/JarFile.close:()V
            //    65: aload_1        
            //    66: athrow         
            //    67: astore_1       
            //    68: goto            40
            //    71: astore_2       
            //    72: goto            65
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  0      9      41     43     Ljava/io/IOException;
            //  9      21     60     67     Any
            //  21     36     60     67     Any
            //  36     40     67     71     Ljava/io/IOException;
            //  43     57     60     67     Any
            //  61     65     71     75     Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IndexOutOfBoundsException: Index 39 out of bounds for length 39
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
        
        @VisibleForTesting
        final void scan(final File file, final ClassLoader classLoader) throws IOException {
            if (this.scannedUris.add(file.getCanonicalFile())) {
                this.scanFrom(file, classLoader);
            }
        }
        
        public final void scan(final ClassLoader classLoader) throws IOException {
            for (final Map.Entry entry : getClassPathEntries(classLoader).entrySet()) {
                this.scan((File)entry.getKey(), (ClassLoader)entry.getValue());
            }
        }
        
        protected abstract void scanDirectory(final ClassLoader p0, final File p1) throws IOException;
        
        protected abstract void scanJarFile(final ClassLoader p0, final JarFile p1) throws IOException;
    }
}
