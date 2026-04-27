package okhttp3;

import okhttp3.internal.Util;

public final class Challenge {
    private final String realm;
    private final String scheme;

    public Challenge(String str, String str2) {
        this.scheme = str;
        this.realm = str2;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Challenge) && Util.equal(this.scheme, ((Challenge) obj).scheme) && Util.equal(this.realm, ((Challenge) obj).realm);
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.realm == null ? 0 : this.realm.hashCode()) + 899) * 31;
        if (this.scheme != null) {
            i = this.scheme.hashCode();
        }
        return hashCode + i;
    }

    public String realm() {
        return this.realm;
    }

    public String scheme() {
        return this.scheme;
    }

    public String toString() {
        return this.scheme + " realm=\"" + this.realm + "\"";
    }
}
