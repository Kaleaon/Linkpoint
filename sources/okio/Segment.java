package okio;

final class Segment {
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

    Segment(Segment segment) {
        this(segment.data, segment.pos, segment.limit);
        segment.shared = true;
    }

    Segment(byte[] bArr, int i, int i2) {
        this.data = bArr;
        this.pos = i;
        this.limit = i2;
        this.owner = false;
        this.shared = true;
    }

    public void compact() {
        int i = 0;
        if (this.prev == this) {
            throw new IllegalStateException();
        } else if (this.prev.owner) {
            int i2 = this.limit - this.pos;
            int i3 = 8192 - this.prev.limit;
            if (!this.prev.shared) {
                i = this.prev.pos;
            }
            if (i2 <= i + i3) {
                writeTo(this.prev, i2);
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
        if (segment.owner) {
            if (segment.limit + i > 8192) {
                if (segment.shared) {
                    throw new IllegalArgumentException();
                } else if ((segment.limit + i) - segment.pos <= 8192) {
                    System.arraycopy(segment.data, segment.pos, segment.data, 0, segment.limit - segment.pos);
                    segment.limit -= segment.pos;
                    segment.pos = 0;
                } else {
                    throw new IllegalArgumentException();
                }
            }
            System.arraycopy(this.data, this.pos, segment.data, segment.limit, i);
            segment.limit += i;
            this.pos += i;
            return;
        }
        throw new IllegalArgumentException();
    }
}
