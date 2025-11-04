// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import java.net.URLClassLoader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.lang.ref.Reference;
import java.util.logging.Level;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import java.io.Closeable;

public class FinalizableReferenceQueue implements Closeable
{
    private static final String FINALIZER_CLASS_NAME = "com.google.common.base.internal.Finalizer";
    private static final Logger logger;
    private static final Method startFinalizer;
    final PhantomReference<Object> frqRef;
    final ReferenceQueue<Object> queue;
    final boolean threadStarted;
    
    static {
        logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());
        startFinalizer = getStartFinalizer(loadFinalizer((FinalizerLoader)new SystemLoader(), (FinalizerLoader)new DecoupledLoader(), (FinalizerLoader)new DirectLoader()));
    }
    
    public FinalizableReferenceQueue() {
        boolean threadStarted = true;
        this.queue = new ReferenceQueue<Object>();
        this.frqRef = new PhantomReference<Object>(this, this.queue);
        while (true) {
            try {
                FinalizableReferenceQueue.startFinalizer.invoke(null, FinalizableReference.class, this.queue, this.frqRef);
                this.threadStarted = threadStarted;
            }
            catch (final IllegalAccessException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
            catch (final Throwable thrown) {
                FinalizableReferenceQueue.logger.log(Level.INFO, "Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.", thrown);
                threadStarted = false;
                continue;
            }
            break;
        }
    }
    
    static Method getStartFinalizer(final Class<?> clazz) {
        try {
            return clazz.getMethod("startFinalizer", Class.class, ReferenceQueue.class, PhantomReference.class);
        }
        catch (final NoSuchMethodException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    private static Class<?> loadFinalizer(final FinalizerLoader... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            final Class<?> loadFinalizer = array[i].loadFinalizer();
            if (loadFinalizer != null) {
                return loadFinalizer;
            }
        }
        throw new AssertionError();
    }
    
    void cleanUp() {
        if (!this.threadStarted) {
            while (true) {
                final Reference<?> poll = this.queue.poll();
                if (poll == null) {
                    break;
                }
                poll.clear();
                try {
                    ((FinalizableReference)poll).finalizeReferent();
                }
                catch (final Throwable thrown) {
                    FinalizableReferenceQueue.logger.log(Level.SEVERE, "Error cleaning up after reference.", thrown);
                }
            }
        }
    }
    
    @Override
    public void close() {
        this.frqRef.enqueue();
        this.cleanUp();
    }
    
    static class DecoupledLoader implements FinalizerLoader
    {
        private static final String LOADING_ERROR = "Could not load Finalizer in its own class loader. Loading Finalizer in the current class loader instead. As a result, you will not be able to garbage collect this class loader. To support reclaiming this class loader, either resolve the underlying issue, or move Guava to your system class path.";
        
        URL getBaseUrl() throws IOException {
            final String string = "com.google.common.base.internal.Finalizer".replace('.', '/') + ".class";
            final URL resource = this.getClass().getClassLoader().getResource(string);
            if (resource == null) {
                throw new FileNotFoundException(string);
            }
            final String string2 = resource.toString();
            if (string2.endsWith(string)) {
                return new URL(resource, string2.substring(0, string2.length() - string.length()));
            }
            throw new IOException("Unsupported path style: " + string2);
        }
        
        @Override
        public Class<?> loadFinalizer() {
            try {
                return this.newLoader(this.getBaseUrl()).loadClass("com.google.common.base.internal.Finalizer");
            }
            catch (final Exception thrown) {
                FinalizableReferenceQueue.logger.log(Level.WARNING, "Could not load Finalizer in its own class loader. Loading Finalizer in the current class loader instead. As a result, you will not be able to garbage collect this class loader. To support reclaiming this class loader, either resolve the underlying issue, or move Guava to your system class path.", thrown);
                return null;
            }
        }
        
        URLClassLoader newLoader(final URL url) {
            return new URLClassLoader(new URL[] { url }, (ClassLoader)null);
        }
    }
    
    interface FinalizerLoader
    {
        @Nullable
        Class<?> loadFinalizer();
    }
    
    static class DirectLoader implements FinalizerLoader
    {
        @Override
        public Class<?> loadFinalizer() {
            try {
                return Class.forName("com.google.common.base.internal.Finalizer");
            }
            catch (final ClassNotFoundException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
    }
    
    static class SystemLoader implements FinalizerLoader
    {
        @VisibleForTesting
        static boolean disabled;
        
        @Override
        public Class<?> loadFinalizer() {
            Label_0029: {
                ClassLoader systemClassLoader = null;
                Label_0016: {
                    if (SystemLoader.disabled) {
                        break Label_0016;
                    }
                    try {
                        systemClassLoader = ClassLoader.getSystemClassLoader();
                        if (systemClassLoader == null) {
                            return null;
                        }
                        break Label_0029;
                        return null;
                    }
                    catch (final SecurityException ex) {
                        FinalizableReferenceQueue.logger.info("Not allowed to access system class loader.");
                        return null;
                    }
                }
                try {
                    return systemClassLoader.loadClass("com.google.common.base.internal.Finalizer");
                }
                catch (final ClassNotFoundException ex2) {
                    return null;
                }
            }
        }
    }
}
