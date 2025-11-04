// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.net;

import com.google.common.base.Objects;
import javax.annotation.Nullable;
import com.google.common.base.Strings;
import com.google.common.base.Preconditions;
import javax.annotation.concurrent.Immutable;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
@GwtCompatible
@Immutable
public final class HostAndPort implements Serializable
{
    private static final int NO_PORT = -1;
    private static final long serialVersionUID = 0L;
    private final boolean hasBracketlessColons;
    private final String host;
    private final int port;
    
    private HostAndPort(final String host, final int port, final boolean hasBracketlessColons) {
        this.host = host;
        this.port = port;
        this.hasBracketlessColons = hasBracketlessColons;
    }
    
    public static HostAndPort fromHost(final String s) {
        final HostAndPort fromString = fromString(s);
        Preconditions.checkArgument(!fromString.hasPort(), "Host has a port: %s", s);
        return fromString;
    }
    
    public static HostAndPort fromParts(final String s, final int i) {
        Preconditions.checkArgument(isValidPort(i), "Port out of range: %s", i);
        final HostAndPort fromString = fromString(s);
        Preconditions.checkArgument(!fromString.hasPort(), "Host has a port: %s", s);
        return new HostAndPort(fromString.host, i, fromString.hasBracketlessColons);
    }
    
    public static HostAndPort fromString(final String str) {
        Preconditions.checkNotNull(str);
        String substring;
        String substring2;
        boolean b;
        if (!str.startsWith("[")) {
            final int index = str.indexOf(58);
            if (index >= 0 && str.indexOf(58, index + 1) == -1) {
                substring = str.substring(0, index);
                substring2 = str.substring(index + 1);
                b = false;
            }
            else {
                b = (index >= 0);
                substring2 = null;
                substring = str;
            }
        }
        else {
            final String[] hostAndPortFromBracketedHost = getHostAndPortFromBracketedHost(str);
            substring = hostAndPortFromBracketedHost[0];
            substring2 = hostAndPortFromBracketedHost[1];
            b = false;
        }
        int int1 = 0;
        if (Strings.isNullOrEmpty(substring2)) {
            int1 = -1;
        }
        else {
            Label_0167: {
                if (!substring2.startsWith("+")) {
                    break Label_0167;
                }
                boolean b2 = false;
                while (true) {
                    Preconditions.checkArgument(b2, "Unparseable port number: %s", str);
                    try {
                        int1 = Integer.parseInt(substring2);
                        Preconditions.checkArgument(isValidPort(int1), "Port number out of range: %s", str);
                        break;
                        b2 = true;
                    }
                    catch (final NumberFormatException ex) {
                        throw new IllegalArgumentException("Unparseable port number: " + str);
                    }
                }
            }
        }
        return new HostAndPort(substring, int1, b);
    }
    
    private static String[] getHostAndPortFromBracketedHost(final String s) {
        Preconditions.checkArgument(s.charAt(0) == '[', "Bracketed host-port string must start with a bracket: %s", s);
        final int index = s.indexOf(58);
        final int lastIndex = s.lastIndexOf(93);
        Preconditions.checkArgument(index > -1 && lastIndex > index, "Invalid bracketed host/port: %s", s);
        final String substring = s.substring(1, lastIndex);
        if (lastIndex + 1 != s.length()) {
            Preconditions.checkArgument(s.charAt(lastIndex + 1) == ':', "Only a colon may follow a close bracket: %s", s);
            for (int i = lastIndex + 2; i < s.length(); ++i) {
                Preconditions.checkArgument(Character.isDigit(s.charAt(i)), "Port must be numeric: %s", s);
            }
            return new String[] { substring, s.substring(lastIndex + 2) };
        }
        return new String[] { substring, "" };
    }
    
    private static boolean isValidPort(final int n) {
        boolean b = false;
        if (n >= 0 && n <= 65535) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof HostAndPort)) {
            return false;
        }
        final HostAndPort hostAndPort = (HostAndPort)o;
        if (!Objects.equal(this.host, hostAndPort.host) || this.port != hostAndPort.port || this.hasBracketlessColons != hostAndPort.hasBracketlessColons) {
            b = false;
        }
        return b;
    }
    
    public String getHostText() {
        return this.host;
    }
    
    public int getPort() {
        Preconditions.checkState(this.hasPort());
        return this.port;
    }
    
    public int getPortOrDefault(int port) {
        if (this.hasPort()) {
            port = this.port;
        }
        return port;
    }
    
    public boolean hasPort() {
        boolean b = false;
        if (this.port >= 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.host, this.port, this.hasBracketlessColons);
    }
    
    public HostAndPort requireBracketsForIPv6() {
        Preconditions.checkArgument(!this.hasBracketlessColons, "Possible bracketless IPv6 literal: %s", this.host);
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.host.length() + 8);
        if (this.host.indexOf(58) < 0) {
            sb.append(this.host);
        }
        else {
            sb.append('[').append(this.host).append(']');
        }
        if (this.hasPort()) {
            sb.append(':').append(this.port);
        }
        return sb.toString();
    }
    
    public HostAndPort withDefaultPort(final int n) {
        Preconditions.checkArgument(isValidPort(n));
        if (!this.hasPort() && this.port != n) {
            return new HostAndPort(this.host, n, this.hasBracketlessColons);
        }
        return this;
    }
}
