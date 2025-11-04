// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.InterruptedIOException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AsyncTimeout extends Timeout
{
    private static final long IDLE_TIMEOUT_MILLIS;
    private static final long IDLE_TIMEOUT_NANOS;
    private static final int TIMEOUT_WRITE_SIZE = 65536;
    private static AsyncTimeout head;
    private boolean inQueue;
    private AsyncTimeout next;
    private long timeoutAt;
    
    static {
        IDLE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60L);
        IDLE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(AsyncTimeout.IDLE_TIMEOUT_MILLIS);
    }
    
    static AsyncTimeout awaitTimeout() throws InterruptedException {
        final int n = 1;
        int n2 = 1;
        AsyncTimeout head = null;
        final AsyncTimeout next = AsyncTimeout.head.next;
        if (next == null) {
            final long nanoTime = System.nanoTime();
            AsyncTimeout.class.wait(AsyncTimeout.IDLE_TIMEOUT_MILLIS);
            if (AsyncTimeout.head.next == null) {
                int n3;
                if (System.nanoTime() - nanoTime < AsyncTimeout.IDLE_TIMEOUT_NANOS) {
                    n3 = n;
                }
                else {
                    n3 = 0;
                }
                if (n3 == 0) {
                    head = AsyncTimeout.head;
                }
            }
            return head;
        }
        final long remainingNanos = next.remainingNanos(System.nanoTime());
        if (remainingNanos > 0L) {
            n2 = 0;
        }
        if (n2 == 0) {
            final long timeoutMillis = remainingNanos / 1000000L;
            AsyncTimeout.class.wait(timeoutMillis, (int)(remainingNanos - timeoutMillis * 1000000L));
            return null;
        }
        AsyncTimeout.head.next = next.next;
        next.next = null;
        return next;
    }
    
    private static boolean cancelScheduledTimeout(final AsyncTimeout asyncTimeout) {
        synchronized (AsyncTimeout.class) {
            for (AsyncTimeout asyncTimeout2 = AsyncTimeout.head; asyncTimeout2 != null; asyncTimeout2 = asyncTimeout2.next) {
                if (asyncTimeout2.next == asyncTimeout) {
                    asyncTimeout2.next = asyncTimeout.next;
                    asyncTimeout.next = null;
                    return false;
                }
            }
            return true;
        }
    }
    
    private long remainingNanos(final long n) {
        return this.timeoutAt - n;
    }
    
    private static void scheduleTimeout(AsyncTimeout head, long remainingNanos, final boolean b) {
        Object o = null;
        long nanoTime = 0L;
        int n;
        Label_0050_Outer:Label_0058_Outer:
        while (true) {
        Label_0211:
            while (true) {
                while (true) {
                Label_0172:
                    while (true) {
                        Label_0147: {
                            synchronized (AsyncTimeout.class) {
                                if (AsyncTimeout.head == null) {
                                    o = (AsyncTimeout.head = new AsyncTimeout());
                                    o = new Watchdog();
                                    ((Thread)o).start();
                                }
                                nanoTime = System.nanoTime();
                                if (remainingNanos == 0L || !b) {
                                    if (remainingNanos == 0L) {
                                        break Label_0147;
                                    }
                                    ((AsyncTimeout)head).timeoutAt = nanoTime + remainingNanos;
                                    remainingNanos = ((AsyncTimeout)head).remainingNanos(nanoTime);
                                    o = AsyncTimeout.head;
                                    if (((AsyncTimeout)o).next != null) {
                                        break Label_0172;
                                    }
                                    ((AsyncTimeout)head).next = ((AsyncTimeout)o).next;
                                    ((AsyncTimeout)o).next = (AsyncTimeout)head;
                                    head = AsyncTimeout.head;
                                    if (o != head) {
                                        return;
                                    }
                                    break Label_0211;
                                }
                            }
                            ((AsyncTimeout)head).timeoutAt = Math.min(remainingNanos, ((Timeout)head).deadlineNanoTime() - nanoTime) + nanoTime;
                            continue Label_0050_Outer;
                        }
                        if (!b) {
                            break;
                        }
                        ((AsyncTimeout)head).timeoutAt = ((Timeout)head).deadlineNanoTime();
                        continue Label_0050_Outer;
                    }
                    if (remainingNanos >= ((AsyncTimeout)o).next.remainingNanos(nanoTime)) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                    if (n != 0) {
                        o = ((AsyncTimeout)o).next;
                        continue Label_0058_Outer;
                    }
                    break;
                }
                continue;
            }
            AsyncTimeout.class.notify();
            return;
        }
        throw new AssertionError();
    }
    
    public final void enter() {
        if (this.inQueue) {
            throw new IllegalStateException("Unbalanced enter/exit");
        }
        final long timeoutNanos = this.timeoutNanos();
        final boolean hasDeadline = this.hasDeadline();
        if (timeoutNanos != 0L || hasDeadline) {
            this.inQueue = true;
            scheduleTimeout(this, timeoutNanos, hasDeadline);
        }
    }
    
    final IOException exit(final IOException ex) throws IOException {
        if (this.exit()) {
            return this.newTimeoutException(ex);
        }
        return ex;
    }
    
    final void exit(final boolean b) throws IOException {
        if (this.exit() && b) {
            throw this.newTimeoutException(null);
        }
    }
    
    public final boolean exit() {
        if (this.inQueue) {
            this.inQueue = false;
            return cancelScheduledTimeout(this);
        }
        return false;
    }
    
    protected IOException newTimeoutException(final IOException cause) {
        final InterruptedIOException ex = new InterruptedIOException("timeout");
        if (cause != null) {
            ex.initCause(cause);
        }
        return ex;
    }
    
    public final Sink sink(final Sink sink) {
        return new Sink() {
            @Override
            public void close() throws IOException {
                AsyncTimeout.this.enter();
                try {
                    sink.close();
                    AsyncTimeout.this.exit(true);
                }
                catch (final IOException ex) {
                    throw AsyncTimeout.this.exit(ex);
                }
                finally {
                    AsyncTimeout.this.exit(false);
                }
            }
            
            @Override
            public void flush() throws IOException {
                AsyncTimeout.this.enter();
                try {
                    sink.flush();
                    AsyncTimeout.this.exit(true);
                }
                catch (final IOException ex) {
                    throw AsyncTimeout.this.exit(ex);
                }
                finally {
                    AsyncTimeout.this.exit(false);
                }
            }
            
            @Override
            public Timeout timeout() {
                return AsyncTimeout.this;
            }
            
            @Override
            public String toString() {
                return "AsyncTimeout.sink(" + sink + ")";
            }
            
            @Override
            public void write(final Buffer buffer, long n) throws IOException {
                Util.checkOffsetAndCount(buffer.size, 0L, n);
            Label_0018_Outer:
                while (true) {
                    Label_0127: {
                        if (n > 0L) {
                            break Label_0127;
                        }
                        int n2 = 1;
                    Label_0032_Outer:
                        while (true) {
                            if (n2 != 0) {
                                break;
                            }
                            Segment segment = buffer.head;
                            long n3 = 0L;
                        Label_0044_Outer:
                            while (true) {
                                Label_0133: {
                                    if (n3 < 65536L) {
                                        break Label_0133;
                                    }
                                    n2 = 1;
                                Label_0084_Outer:
                                    while (true) {
                                        long n4 = n3;
                                        Label_0139: {
                                            while (true) {
                                                Label_0145: {
                                                    Label_0092: {
                                                        if (n2 != 0) {
                                                            break Label_0092;
                                                        }
                                                        n3 += buffer.head.limit - buffer.head.pos;
                                                        if (n3 >= n) {
                                                            break Label_0139;
                                                        }
                                                        n2 = 1;
                                                        if (n2 != 0) {
                                                            break Label_0145;
                                                        }
                                                        n4 = n;
                                                    }
                                                    AsyncTimeout.this.enter();
                                                    try {
                                                        sink.write(buffer, n4);
                                                        n -= n4;
                                                        AsyncTimeout.this.exit(true);
                                                        continue Label_0018_Outer;
                                                        segment = segment.next;
                                                        continue Label_0044_Outer;
                                                        n2 = 0;
                                                        continue;
                                                        n2 = 0;
                                                        continue Label_0032_Outer;
                                                        n2 = 0;
                                                        continue Label_0084_Outer;
                                                    }
                                                    catch (final IOException ex) {
                                                        throw AsyncTimeout.this.exit(ex);
                                                    }
                                                    finally {
                                                        AsyncTimeout.this.exit(false);
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        };
    }
    
    public final Source source(final Source source) {
        return new Source() {
            @Override
            public void close() throws IOException {
                try {
                    source.close();
                    AsyncTimeout.this.exit(true);
                }
                catch (final IOException ex) {
                    throw AsyncTimeout.this.exit(ex);
                }
                finally {
                    AsyncTimeout.this.exit(false);
                }
            }
            
            @Override
            public long read(final Buffer buffer, long read) throws IOException {
                AsyncTimeout.this.enter();
                try {
                    read = source.read(buffer, read);
                    AsyncTimeout.this.exit(true);
                    return read;
                }
                catch (final IOException ex) {
                    throw AsyncTimeout.this.exit(ex);
                }
                finally {
                    AsyncTimeout.this.exit(false);
                }
            }
            
            @Override
            public Timeout timeout() {
                return AsyncTimeout.this;
            }
            
            @Override
            public String toString() {
                return "AsyncTimeout.source(" + source + ")";
            }
        };
    }
    
    protected void timedOut() {
    }
    
    private static final class Watchdog extends Thread
    {
        public Watchdog() {
            super("Okio Watchdog");
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
        Label_0044:
            while (true) {
                Label_0032: {
                    try {
                        while (true) {
                            monitorenter(AsyncTimeout.class);
                            final AsyncTimeout asyncTimeout = AsyncTimeout.awaitTimeout();
                            final AsyncTimeout asyncTimeout3;
                            final AsyncTimeout asyncTimeout2 = asyncTimeout3 = asyncTimeout;
                            if (asyncTimeout3 == null) {
                                break Label_0032;
                            }
                            final AsyncTimeout asyncTimeout4 = asyncTimeout2;
                            final AsyncTimeout asyncTimeout5 = AsyncTimeout.head;
                            if (asyncTimeout4 == asyncTimeout5) {
                                break Label_0044;
                            }
                            final Class<AsyncTimeout> clazz = AsyncTimeout.class;
                            monitorexit(clazz);
                            final AsyncTimeout asyncTimeout6 = asyncTimeout2;
                            asyncTimeout6.timedOut();
                        }
                    }
                    catch (final InterruptedException ex) {
                        continue;
                    }
                    try {
                        final AsyncTimeout asyncTimeout = AsyncTimeout.awaitTimeout();
                        final AsyncTimeout asyncTimeout3;
                        final AsyncTimeout asyncTimeout2 = asyncTimeout3 = asyncTimeout;
                        if (asyncTimeout3 == null) {
                            continue;
                        }
                        final AsyncTimeout asyncTimeout4 = asyncTimeout2;
                        final AsyncTimeout asyncTimeout5 = AsyncTimeout.head;
                        if (asyncTimeout4 != asyncTimeout5) {
                            final Class<AsyncTimeout> clazz = AsyncTimeout.class;
                            monitorexit(clazz);
                            final AsyncTimeout asyncTimeout6 = asyncTimeout2;
                            asyncTimeout6.timedOut();
                            continue;
                        }
                    }
                    finally {
                        monitorexit(AsyncTimeout.class);
                    }
                }
                break;
            }
            AsyncTimeout.head = null;
            monitorexit(AsyncTimeout.class);
        }
    }
}
