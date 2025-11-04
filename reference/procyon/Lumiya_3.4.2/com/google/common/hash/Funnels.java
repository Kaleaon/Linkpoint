// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.io.OutputStream;
import com.google.common.annotations.Beta;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@Beta
public final class Funnels
{
    private Funnels() {
    }
    
    public static OutputStream asOutputStream(final PrimitiveSink primitiveSink) {
        return new SinkAsStream(primitiveSink);
    }
    
    public static Funnel<byte[]> byteArrayFunnel() {
        return ByteArrayFunnel.INSTANCE;
    }
    
    public static Funnel<Integer> integerFunnel() {
        return IntegerFunnel.INSTANCE;
    }
    
    public static Funnel<Long> longFunnel() {
        return LongFunnel.INSTANCE;
    }
    
    public static <E> Funnel<Iterable<? extends E>> sequentialFunnel(final Funnel<E> funnel) {
        return (Funnel<Iterable<? extends E>>)new SequentialFunnel((Funnel<Object>)funnel);
    }
    
    public static Funnel<CharSequence> stringFunnel(final Charset charset) {
        return new StringCharsetFunnel(charset);
    }
    
    public static Funnel<CharSequence> unencodedCharsFunnel() {
        return UnencodedCharsFunnel.INSTANCE;
    }
    
    private enum ByteArrayFunnel implements Funnel<byte[]>
    {
        INSTANCE;
        
        @Override
        public void funnel(final byte[] array, final PrimitiveSink primitiveSink) {
            primitiveSink.putBytes(array);
        }
        
        @Override
        public String toString() {
            return "Funnels.byteArrayFunnel()";
        }
    }
    
    private enum IntegerFunnel implements Funnel<Integer>
    {
        INSTANCE;
        
        @Override
        public void funnel(final Integer n, final PrimitiveSink primitiveSink) {
            primitiveSink.putInt(n);
        }
        
        @Override
        public String toString() {
            return "Funnels.integerFunnel()";
        }
    }
    
    private enum LongFunnel implements Funnel<Long>
    {
        INSTANCE;
        
        @Override
        public void funnel(final Long n, final PrimitiveSink primitiveSink) {
            primitiveSink.putLong(n);
        }
        
        @Override
        public String toString() {
            return "Funnels.longFunnel()";
        }
    }
    
    private static class SequentialFunnel<E> implements Funnel<Iterable<? extends E>>, Serializable
    {
        private final Funnel<E> elementFunnel;
        
        SequentialFunnel(final Funnel<E> funnel) {
            this.elementFunnel = Preconditions.checkNotNull(funnel);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof SequentialFunnel && this.elementFunnel.equals(((SequentialFunnel)o).elementFunnel);
        }
        
        @Override
        public void funnel(final Iterable<? extends E> iterable, final PrimitiveSink primitiveSink) {
            final Iterator<? extends E> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                this.elementFunnel.funnel((E)iterator.next(), primitiveSink);
            }
        }
        
        @Override
        public int hashCode() {
            return SequentialFunnel.class.hashCode() ^ this.elementFunnel.hashCode();
        }
        
        @Override
        public String toString() {
            return "Funnels.sequentialFunnel(" + this.elementFunnel + ")";
        }
    }
    
    private static class SinkAsStream extends OutputStream
    {
        final PrimitiveSink sink;
        
        SinkAsStream(final PrimitiveSink primitiveSink) {
            this.sink = Preconditions.checkNotNull(primitiveSink);
        }
        
        @Override
        public String toString() {
            return "Funnels.asOutputStream(" + this.sink + ")";
        }
        
        @Override
        public void write(final int n) {
            this.sink.putByte((byte)n);
        }
        
        @Override
        public void write(final byte[] array) {
            this.sink.putBytes(array);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) {
            this.sink.putBytes(array, n, n2);
        }
    }
    
    private static class StringCharsetFunnel implements Funnel<CharSequence>, Serializable
    {
        private final Charset charset;
        
        StringCharsetFunnel(final Charset charset) {
            this.charset = Preconditions.checkNotNull(charset);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof StringCharsetFunnel && this.charset.equals(((StringCharsetFunnel)o).charset);
        }
        
        @Override
        public void funnel(final CharSequence charSequence, final PrimitiveSink primitiveSink) {
            primitiveSink.putString(charSequence, this.charset);
        }
        
        @Override
        public int hashCode() {
            return StringCharsetFunnel.class.hashCode() ^ this.charset.hashCode();
        }
        
        @Override
        public String toString() {
            return "Funnels.stringFunnel(" + this.charset.name() + ")";
        }
        
        Object writeReplace() {
            return new SerializedForm(this.charset);
        }
        
        private static class SerializedForm implements Serializable
        {
            private static final long serialVersionUID = 0L;
            private final String charsetCanonicalName;
            
            SerializedForm(final Charset charset) {
                this.charsetCanonicalName = charset.name();
            }
            
            private Object readResolve() {
                return Funnels.stringFunnel(Charset.forName(this.charsetCanonicalName));
            }
        }
    }
    
    private enum UnencodedCharsFunnel implements Funnel<CharSequence>
    {
        INSTANCE;
        
        @Override
        public void funnel(final CharSequence charSequence, final PrimitiveSink primitiveSink) {
            primitiveSink.putUnencodedChars(charSequence);
        }
        
        @Override
        public String toString() {
            return "Funnels.unencodedCharsFunnel()";
        }
    }
}
