package okio;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class Segment {
    static final int SHARE_MINIMUM = 1024;
    static final int SIZE = 8192;
    final byte[] data;
    int limit;
    Segment next;
    boolean owner;
    int pos;
    Segment prev;
    boolean shared;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Segment() {
        this.data = new byte[8192];
        this.owner = true;
        this.shared = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Segment(Segment segment) {
        this(segment.data, segment.pos, segment.limit);
        segment.shared = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Segment(byte[] bArr, int i, int i2) {
        this.data = bArr;
        this.pos = i;
        this.limit = i2;
        this.owner = false;
        this.shared = true;
    }

    public void compact() {
        if (this.prev == this) {
            throw new IllegalStateException();
        }
        if (this.prev.owner) {
            int i = this.limit - this.pos;
            if (i <= (this.prev.shared ? 0 : this.prev.pos) + (8192 - this.prev.limit)) {
                writeTo(this.prev, i);
                pop();
                SegmentPool.recycle(this);
            }
        }
    }

    public Segment pop() {
        Segment segment = this.next == this ? null : this.next;
        this.prev.next = this.next;
        this.next.prev = this.prev;
        this.next = null;
        this.prev = null;
        return segment;
    }

    public Segment push(Segment segment) {
        segment.prev = this;
        segment.next = this.next;
        this.next.prev = segment;
        this.next = segment;
        return segment;
    }

    public Segment split(int i) {
        Segment segment;
        if (i > 0 && i <= this.limit - this.pos) {
            if (i < 1024) {
                segment = SegmentPool.take();
                System.arraycopy(this.data, this.pos, segment.data, 0, i);
            } else {
                segment = new Segment(this);
            }
            segment.limit = segment.pos + i;
            this.pos += i;
            this.prev.push(segment);
            return segment;
        }
        throw new IllegalArgumentException();
    }

    public void writeTo(Segment segment, int i) {
        if (!segment.owner) {
            throw new IllegalArgumentException();
        }
        if (segment.limit + i > 8192) {
            if (segment.shared) {
                throw new IllegalArgumentException();
            }
            if ((segment.limit + i) - segment.pos > 8192) {
                throw new IllegalArgumentException();
            }
            System.arraycopy(segment.data, segment.pos, segment.data, 0, segment.limit - segment.pos);
            segment.limit -= segment.pos;
            segment.pos = 0;
        }
        System.arraycopy(this.data, this.pos, segment.data, segment.limit, i);
        segment.limit += i;
        this.pos += i;
    }
}
