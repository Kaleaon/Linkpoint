// 
// Decompiled by Procyon v0.6.0
// 

package okio;

final class SegmentPool
{
    static final long MAX_SIZE = 65536L;
    static long byteCount;
    static Segment next;
    
    private SegmentPool() {
    }
    
    static void recycle(final Segment next) {
        boolean b = false;
        if (next.next != null || next.prev != null) {
            throw new IllegalArgumentException();
        }
        if (next.shared) {
            return;
        }
        synchronized (SegmentPool.class) {
            if (SegmentPool.byteCount + 8192L <= 65536L) {
                b = true;
            }
            if (!b) {
                return;
            }
            SegmentPool.byteCount += 8192L;
            next.next = SegmentPool.next;
            next.limit = 0;
            next.pos = 0;
            SegmentPool.next = next;
        }
    }
    
    static Segment take() {
        synchronized (SegmentPool.class) {
            if (SegmentPool.next == null) {
                monitorexit(SegmentPool.class);
                return new Segment();
            }
            final Segment next = SegmentPool.next;
            SegmentPool.next = next.next;
            next.next = null;
            SegmentPool.byteCount -= 8192L;
            return next;
        }
    }
}
