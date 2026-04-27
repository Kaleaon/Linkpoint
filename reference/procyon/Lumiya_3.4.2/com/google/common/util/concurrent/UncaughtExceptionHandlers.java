// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.annotations.VisibleForTesting;

public final class UncaughtExceptionHandlers
{
    private UncaughtExceptionHandlers() {
    }
    
    public static Thread.UncaughtExceptionHandler systemExit() {
        return new Exiter(Runtime.getRuntime());
    }
    
    @VisibleForTesting
    static final class Exiter implements UncaughtExceptionHandler
    {
        private static final Logger logger;
        private final Runtime runtime;
        
        static {
            logger = Logger.getLogger(Exiter.class.getName());
        }
        
        Exiter(final Runtime runtime) {
            this.runtime = runtime;
        }
        
        @Override
        public void uncaughtException(final Thread thread, final Throwable thrown) {
            try {
                Exiter.logger.log(Level.SEVERE, String.format(Locale.ROOT, "Caught an exception in %s.  Shutting down.", thread), thrown);
            }
            catch (final Throwable t) {
                System.err.println(thrown.getMessage());
                System.err.println(t.getMessage());
                this.runtime.exit(1);
            }
            finally {
                this.runtime.exit(1);
            }
        }
    }
}
