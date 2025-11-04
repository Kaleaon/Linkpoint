// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import okhttp3.internal.Util;

public final class Challenge
{
    private final String realm;
    private final String scheme;
    
    public Challenge(final String scheme, final String realm) {
        this.scheme = scheme;
        this.realm = realm;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Challenge && Util.equal(this.scheme, ((Challenge)o).scheme) && Util.equal(this.realm, ((Challenge)o).realm);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        int hashCode2;
        if (this.realm == null) {
            hashCode2 = 0;
        }
        else {
            hashCode2 = this.realm.hashCode();
        }
        if (this.scheme != null) {
            hashCode = this.scheme.hashCode();
        }
        return (hashCode2 + 899) * 31 + hashCode;
    }
    
    public String realm() {
        return this.realm;
    }
    
    public String scheme() {
        return this.scheme;
    }
    
    @Override
    public String toString() {
        return this.scheme + " realm=\"" + this.realm + "\"";
    }
}
