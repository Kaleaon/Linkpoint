package okio;

final class SegmentPool {
    static final long MAX_SIZE = 65536;
    static long byteCount;
    static Segment next;

    private SegmentPool() {
    }

    static void recycle(Segment segment) {
        boolean z = false;
        if (segment.next != null || segment.prev != null) {
            throw new IllegalArgumentException();
        } else if (!segment.shared) {
            Class<SegmentPool> cls = SegmentPool.class;
            synchronized (SegmentPool.class) {
                if (byteCount + 8192 <= 65536) {
                    z = true;
                }
                if (!z) {
                    return;
                }
                byteCount += 8192;
                segment.next = next;
                segment.limit = 0;
                segment.pos = 0;
                next = segment;
            }
        }
    }

    static Segment take() {
        Class<SegmentPool> cls = SegmentPool.class;
        synchronized (SegmentPool.class) {
            if (next == null) {
                return new Segment();
            }
            Segment segment = next;
            next = segment.next;
            segment.next = null;
            byteCount -= 8192;
            return segment;
        }
    }
}
