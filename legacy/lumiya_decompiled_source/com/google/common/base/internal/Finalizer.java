package com.google.common.base.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public class Finalizer implements Runnable {
    private static final String FINALIZABLE_REFERENCE = "com.google.common.base.FinalizableReference";
    private final WeakReference<Class<?>> finalizableReferenceClassReference;
    private final PhantomReference<Object> frqReference;
    private final ReferenceQueue<Object> queue;
    private static final Logger logger = Logger.getLogger(Finalizer.class.getName());
    private static final Field inheritableThreadLocals = getInheritableThreadLocalsField();

    private Finalizer(Class<?> cls, ReferenceQueue<Object> referenceQueue, PhantomReference<Object> phantomReference) {
        this.queue = referenceQueue;
        this.finalizableReferenceClassReference = new WeakReference<>(cls);
        this.frqReference = phantomReference;
    }

    private boolean cleanUp(Reference<?> reference) {
        Method finalizeReferentMethod = getFinalizeReferentMethod();
        if (finalizeReferentMethod != null) {
            do {
                reference.clear();
                if (reference == this.frqReference) {
                    return false;
                }
                try {
                    finalizeReferentMethod.invoke(reference, new Object[0]);
                } catch (Throwable th) {
                    logger.log(Level.SEVERE, "Error cleaning up after reference.", th);
                }
                reference = this.queue.poll();
            } while (reference != null);
            return true;
        }
        return false;
    }

    private Method getFinalizeReferentMethod() {
        Class<?> cls = this.finalizableReferenceClassReference.get();
        if (cls != null) {
            try {
                return cls.getMethod("finalizeReferent", new Class[0]);
            } catch (NoSuchMethodException e) {
                throw new AssertionError(e);
            }
        }
        return null;
    }

    public static Field getInheritableThreadLocalsField() {
        try {
            Field declaredField = Thread.class.getDeclaredField("inheritableThreadLocals");
            declaredField.setAccessible(true);
            return declaredField;
        } catch (Throwable th) {
            logger.log(Level.INFO, "Couldn't access Thread.inheritableThreadLocals. Reference finalizer threads will inherit thread local values.");
            return null;
        }
    }

    public static void startFinalizer(Class<?> cls, ReferenceQueue<Object> referenceQueue, PhantomReference<Object> phantomReference) {
        if (!cls.getName().equals(FINALIZABLE_REFERENCE)) {
            throw new IllegalArgumentException("Expected com.google.common.base.FinalizableReference.");
        }
        Thread thread = new Thread(new Finalizer(cls, referenceQueue, phantomReference));
        thread.setName(Finalizer.class.getName());
        thread.setDaemon(true);
        try {
            if (inheritableThreadLocals != null) {
                inheritableThreadLocals.set(thread, null);
            }
        } catch (Throwable th) {
            logger.log(Level.INFO, "Failed to clear thread local values inherited by reference finalizer thread.", th);
        }
        thread.start();
    }

    @Override // java.lang.Runnable
    public void run() {
        while (cleanUp(this.queue.remove())) {
        }
    }
}
