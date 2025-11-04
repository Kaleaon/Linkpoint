// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.os;

public class OperationCanceledException extends RuntimeException
{
    public OperationCanceledException() {
        this((String)null);
    }
    
    public OperationCanceledException(final String s) {
        String message = s;
        if (s == null) {
            message = "The operation has been canceled.";
        }
        super(message);
    }
}
