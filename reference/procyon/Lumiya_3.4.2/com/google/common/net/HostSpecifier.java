// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.net;

import javax.annotation.Nullable;
import java.net.InetAddress;
import com.google.common.base.Preconditions;
import java.text.ParseException;
import com.google.common.annotations.Beta;

@Beta
public final class HostSpecifier
{
    private final String canonicalForm;
    
    private HostSpecifier(final String canonicalForm) {
        this.canonicalForm = canonicalForm;
    }
    
    public static HostSpecifier from(final String str) throws ParseException {
        try {
            return fromValid(str);
        }
        catch (final IllegalArgumentException cause) {
            final ParseException ex = new ParseException("Invalid host specifier: " + str, 0);
            ex.initCause(cause);
            throw ex;
        }
    }
    
    public static HostSpecifier fromValid(final String s) {
        boolean b = false;
        final HostAndPort fromString = HostAndPort.fromString(s);
        Label_0071: {
            if (!fromString.hasPort()) {
                break Label_0071;
            }
        Label_0028_Outer:
            while (true) {
                Preconditions.checkArgument(b);
                final String hostText = fromString.getHostText();
                InetAddress forString;
                while (true) {
                    try {
                        forString = InetAddresses.forString(hostText);
                        if (forString != null) {
                            return new HostSpecifier(InetAddresses.toUriString(forString));
                        }
                        final InternetDomainName from = InternetDomainName.from(hostText);
                        if (!from.hasPublicSuffix()) {
                            throw new IllegalArgumentException("Domain name does not have a recognized public suffix: " + hostText);
                        }
                        return new HostSpecifier(from.toString());
                        b = true;
                        continue Label_0028_Outer;
                    }
                    catch (final IllegalArgumentException ex) {
                        forString = null;
                        continue;
                    }
                    break;
                }
                return new HostSpecifier(InetAddresses.toUriString(forString));
            }
        }
    }
    
    public static boolean isValid(final String s) {
        try {
            fromValid(s);
            return true;
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return this == o || (o instanceof HostSpecifier && this.canonicalForm.equals(((HostSpecifier)o).canonicalForm));
    }
    
    @Override
    public int hashCode() {
        return this.canonicalForm.hashCode();
    }
    
    @Override
    public String toString() {
        return this.canonicalForm;
    }
}
