// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.connection;

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.lang.reflect.Method;

public final class RouteException extends RuntimeException
{
    private static final Method addSuppressedExceptionMethod;
    private IOException lastException;
    
    static {
        while (true) {
            try {
                final Method declaredMethod = Throwable.class.getDeclaredMethod("addSuppressed", Throwable.class);
                addSuppressedExceptionMethod = declaredMethod;
            }
            catch (final Exception ex) {
                final Method declaredMethod = null;
                continue;
            }
            break;
        }
    }
    
    public RouteException(final IOException ex) {
        super(ex);
        this.lastException = ex;
    }
    
    private void addSuppressedIfPossible(final IOException obj, final IOException ex) {
        if (RouteException.addSuppressedExceptionMethod != null) {
            try {
                RouteException.addSuppressedExceptionMethod.invoke(obj, ex);
            }
            catch (final InvocationTargetException | IllegalAccessException ex2) {}
        }
    }
    
    public void addConnectException(final IOException lastException) {
        this.addSuppressedIfPossible(lastException, this.lastException);
        this.lastException = lastException;
    }
    
    public IOException getLastConnectException() {
        return this.lastException;
    }
}
