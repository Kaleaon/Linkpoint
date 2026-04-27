package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

public class Timeout {
    public static final Timeout NONE = new Timeout() {
        public Timeout deadlineNanoTime(long j) {
            return this;
        }

        public void throwIfReached() throws IOException {
        }

        public Timeout timeout(long j, TimeUnit timeUnit) {
            return this;
        }
    };
    private long deadlineNanoTime;
    private boolean hasDeadline;
    private long timeoutNanos;

    public Timeout clearDeadline() {
        this.hasDeadline = false;
        return this;
    }

    public Timeout clearTimeout() {
        this.timeoutNanos = 0;
        return this;
    }

    public final Timeout deadline(long j, TimeUnit timeUnit) {
        if (!(j > 0)) {
            throw new IllegalArgumentException("duration <= 0: " + j);
        } else if (timeUnit != null) {
            return deadlineNanoTime(System.nanoTime() + timeUnit.toNanos(j));
        } else {
            throw new IllegalArgumentException("unit == null");
        }
    }

    public long deadlineNanoTime() {
        if (this.hasDeadline) {
            return this.deadlineNanoTime;
        }
        throw new IllegalStateException("No deadline");
    }

    public Timeout deadlineNanoTime(long j) {
        this.hasDeadline = true;
        this.deadlineNanoTime = j;
        return this;
    }

    public boolean hasDeadline() {
        return this.hasDeadline;
    }

    public void throwIfReached() throws IOException {
        boolean z = false;
        if (Thread.interrupted()) {
            throw new InterruptedIOException("thread interrupted");
        } else if (this.hasDeadline) {
            if (this.deadlineNanoTime - System.nanoTime() > 0) {
                z = true;
            }
            if (!z) {
                throw new InterruptedIOException("deadline reached");
            }
        }
    }

    public Timeout timeout(long j, TimeUnit timeUnit) {
        if (!(j >= 0)) {
            throw new IllegalArgumentException("timeout < 0: " + j);
        } else if (timeUnit != null) {
            this.timeoutNanos = timeUnit.toNanos(j);
            return this;
        } else {
            throw new IllegalArgumentException("unit == null");
        }
    }

    public long timeoutNanos() {
        return this.timeoutNanos;
    }

    public final void waitUntilNotified(Object obj) throws InterruptedIOException {
        long j = 0;
        try {
            boolean hasDeadline2 = hasDeadline();
            long timeoutNanos2 = timeoutNanos();
            if (!hasDeadline2 && timeoutNanos2 == 0) {
                obj.wait();
                return;
            }
            long nanoTime = System.nanoTime();
            if (hasDeadline2 && timeoutNanos2 != 0) {
                timeoutNanos2 = Math.min(timeoutNanos2, deadlineNanoTime() - nanoTime);
            } else if (hasDeadline2) {
                timeoutNanos2 = deadlineNanoTime() - nanoTime;
            }
            if (!(timeoutNanos2 <= 0)) {
                long j2 = timeoutNanos2 / 1000000;
                obj.wait(j2, (int) (timeoutNanos2 - (j2 * 1000000)));
                j = System.nanoTime() - nanoTime;
            }
            if (!(j < timeoutNanos2)) {
                throw new InterruptedIOException("timeout");
            }
        } catch (InterruptedException e) {
            throw new InterruptedIOException("interrupted");
        }
    }
}
