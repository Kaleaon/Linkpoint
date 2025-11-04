// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

public class Pair<F, S>
{
    public final F first;
    public final S second;
    
    public Pair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }
    
    public static <A, B> Pair<A, B> create(final A a, final B b) {
        return new Pair<A, B>(a, b);
    }
    
    private static boolean objectsEqual(final Object o, final Object obj) {
        final boolean b = false;
        if (o != obj) {
            boolean b2 = b;
            if (o == null) {
                return b2;
            }
            if (!o.equals(obj)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof Pair) {
            final Pair pair = (Pair)o;
            if (objectsEqual(pair.first, this.first) && objectsEqual(pair.second, this.second)) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        int hashCode2;
        if (this.first != null) {
            hashCode2 = this.first.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        if (this.second != null) {
            hashCode = this.second.hashCode();
        }
        return hashCode2 ^ hashCode;
    }
    
    @Override
    public String toString() {
        return "Pair{" + String.valueOf(this.first) + " " + String.valueOf(this.second) + "}";
    }
}
