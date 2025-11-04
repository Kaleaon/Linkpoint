// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.InputStream;
import com.google.common.collect.Lists;
import java.util.List;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.net.URL;
import com.google.common.annotations.Beta;

@Beta
public final class Resources
{
    private Resources() {
    }
    
    public static ByteSource asByteSource(final URL url) {
        return new UrlByteSource(url);
    }
    
    public static CharSource asCharSource(final URL url, final Charset charset) {
        return asByteSource(url).asCharSource(charset);
    }
    
    public static void copy(final URL url, final OutputStream outputStream) throws IOException {
        asByteSource(url).copyTo(outputStream);
    }
    
    public static URL getResource(final Class<?> clazz, final String name) {
        final URL resource = clazz.getResource(name);
        Preconditions.checkArgument(resource != null, "resource %s relative to %s not found.", name, clazz.getName());
        return resource;
    }
    
    public static URL getResource(final String name) {
        final URL resource = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(), Resources.class.getClassLoader()).getResource(name);
        Preconditions.checkArgument(resource != null, "resource %s not found.", name);
        return resource;
    }
    
    public static <T> T readLines(final URL url, final Charset charset, final LineProcessor<T> lineProcessor) throws IOException {
        return asCharSource(url, charset).readLines(lineProcessor);
    }
    
    public static List<String> readLines(final URL url, final Charset charset) throws IOException {
        return readLines(url, charset, (LineProcessor<List<String>>)new LineProcessor<List<String>>() {
            final List<String> result = Lists.newArrayList();
            
            @Override
            public List<String> getResult() {
                return this.result;
            }
            
            @Override
            public boolean processLine(final String s) {
                this.result.add(s);
                return true;
            }
        });
    }
    
    public static byte[] toByteArray(final URL url) throws IOException {
        return asByteSource(url).read();
    }
    
    public static String toString(final URL url, final Charset charset) throws IOException {
        return asCharSource(url, charset).read();
    }
    
    private static final class UrlByteSource extends ByteSource
    {
        private final URL url;
        
        private UrlByteSource(final URL url) {
            this.url = Preconditions.checkNotNull(url);
        }
        
        @Override
        public InputStream openStream() throws IOException {
            return this.url.openStream();
        }
        
        @Override
        public String toString() {
            return "Resources.asByteSource(" + this.url + ")";
        }
    }
}
