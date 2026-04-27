// 
// Decompiled by Procyon v0.6.0
// 

package okio;

final class Segment
{
    static final int SHARE_MINIMUM = 1024;
    static final int SIZE = 8192;
    final byte[] data;
    int limit;
    Segment next;
    boolean owner;
    int pos;
    Segment prev;
    boolean shared;
    
    Segment() {
        this.data = new byte[8192];
        this.owner = true;
        this.shared = false;
    }
    
    Segment(final Segment segment) {
        this(segment.data, segment.pos, segment.limit);
        segment.shared = true;
    }
    
    Segment(final byte[] data, final int pos, final int limit) {
        this.data = data;
        this.pos = pos;
        this.limit = limit;
        this.owner = false;
        this.shared = true;
    }
    
    public void compact() {
        int pos = 0;
        if (this.prev == this) {
            throw new IllegalStateException();
        }
        if (!this.prev.owner) {
            return;
        }
        final int n = this.limit - this.pos;
        final int limit = this.prev.limit;
        if (!this.prev.shared) {
            pos = this.prev.pos;
        }
        if (n <= pos + (8192 - limit)) {
            this.writeTo(this.prev, n);
            this.pop();
            SegmentPool.recycle(this);
        }
    }
    
    public Segment pop() {
        Segment next;
        if (this.next == this) {
            next = null;
        }
        else {
            next = this.next;
        }
        this.prev.next = this.next;
        this.next.prev = this.prev;
        this.next = null;
        this.prev = null;
        return next;
    }
    
    public Segment push(final Segment segment) {
        segment.prev = this;
        segment.next = this.next;
        this.next.prev = segment;
        return this.next = segment;
    }
    
    public Segment split(final int n) {
        if (n > 0 && n <= this.limit - this.pos) {
            Segment take;
            if (n < 1024) {
                take = SegmentPool.take();
                System.arraycopy(this.data, this.pos, take.data, 0, n);
            }
            else {
                take = new Segment(this);
            }
            take.limit = take.pos + n;
            this.pos += n;
            this.prev.push(take);
            return take;
        }
        throw new IllegalArgumentException();
    }
    
    public void writeTo(final Segment segment, final int n) {
        if (segment.owner) {
            if (segment.limit + n > 8192) {
                if (segment.shared) {
                    throw new IllegalArgumentException();
                }
                if (segment.limit + n - segment.pos > 8192) {
                    throw new IllegalArgumentException();
                }
                System.arraycopy(segment.data, segment.pos, segment.data, 0, segment.limit - segment.pos);
                segment.limit -= segment.pos;
                segment.pos = 0;
            }
            System.arraycopy(this.data, this.pos, segment.data, segment.limit, n);
            segment.limit += n;
            this.pos += n;
            return;
        }
        throw new IllegalArgumentException();
    }
}
