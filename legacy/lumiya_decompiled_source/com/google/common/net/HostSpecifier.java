package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.net.InetAddress;
import java.text.ParseException;
import javax.annotation.Nullable;
@Beta
/* loaded from: classes.dex */
public final class HostSpecifier {
    private final String canonicalForm;

    private HostSpecifier(String str) {
        this.canonicalForm = str;
    }

    public static HostSpecifier from(String str) throws ParseException {
        try {
            return fromValid(str);
        } catch (IllegalArgumentException e) {
            ParseException parseException = new ParseException("Invalid host specifier: " + str, 0);
            parseException.initCause(e);
            throw parseException;
        }
    }

    public static HostSpecifier fromValid(String str) {
        InetAddress inetAddress;
        HostAndPort fromString = HostAndPort.fromString(str);
        Preconditions.checkArgument(fromString.hasPort() ? false : true);
        String hostText = fromString.getHostText();
        try {
            inetAddress = InetAddresses.forString(hostText);
        } catch (IllegalArgumentException e) {
            inetAddress = null;
        }
        if (inetAddress == null) {
            InternetDomainName from = InternetDomainName.from(hostText);
            if (from.hasPublicSuffix()) {
                return new HostSpecifier(from.toString());
            }
            throw new IllegalArgumentException("Domain name does not have a recognized public suffix: " + hostText);
        }
        return new HostSpecifier(InetAddresses.toUriString(inetAddress));
    }

    public static boolean isValid(String str) {
        try {
            fromValid(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof HostSpecifier) {
                return this.canonicalForm.equals(((HostSpecifier) obj).canonicalForm);
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.canonicalForm.hashCode();
    }

    public String toString() {
        return this.canonicalForm;
    }
}
