// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.log;

public final class LogManager
{
    private static Logger logger;
    
    static {
        LogManager.logger = new LoggerDefault();
    }
    
    public static Logger getLogger() {
        return LogManager.logger;
    }
    
    public static void setLogger(final Logger logger) {
        LogManager.logger = logger;
    }
}
