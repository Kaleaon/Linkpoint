// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.Collections;
import java.util.ArrayList;
import javax.annotation.concurrent.GuardedBy;
import java.util.List;
import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractService implements Service
{
    private static final ListenerCallQueue.Callback<Listener> RUNNING_CALLBACK;
    private static final ListenerCallQueue.Callback<Listener> STARTING_CALLBACK;
    private static final ListenerCallQueue.Callback<Listener> STOPPING_FROM_RUNNING_CALLBACK;
    private static final ListenerCallQueue.Callback<Listener> STOPPING_FROM_STARTING_CALLBACK;
    private static final ListenerCallQueue.Callback<Listener> TERMINATED_FROM_NEW_CALLBACK;
    private static final ListenerCallQueue.Callback<Listener> TERMINATED_FROM_RUNNING_CALLBACK;
    private static final ListenerCallQueue.Callback<Listener> TERMINATED_FROM_STOPPING_CALLBACK;
    private final Monitor.Guard hasReachedRunning;
    private final Monitor.Guard isStartable;
    private final Monitor.Guard isStoppable;
    private final Monitor.Guard isStopped;
    @GuardedBy("monitor")
    private final List<ListenerCallQueue<Listener>> listeners;
    private final Monitor monitor;
    @GuardedBy("monitor")
    private volatile StateSnapshot snapshot;
    
    static {
        STARTING_CALLBACK = new ListenerCallQueue.Callback<Listener>("starting()") {
            void call(final Listener listener) {
                listener.starting();
            }
        };
        RUNNING_CALLBACK = new ListenerCallQueue.Callback<Listener>("running()") {
            void call(final Listener listener) {
                listener.running();
            }
        };
        STOPPING_FROM_STARTING_CALLBACK = stoppingCallback(State.STARTING);
        STOPPING_FROM_RUNNING_CALLBACK = stoppingCallback(State.RUNNING);
        TERMINATED_FROM_NEW_CALLBACK = terminatedCallback(State.NEW);
        TERMINATED_FROM_RUNNING_CALLBACK = terminatedCallback(State.RUNNING);
        TERMINATED_FROM_STOPPING_CALLBACK = terminatedCallback(State.STOPPING);
    }
    
    protected AbstractService() {
        this.monitor = new Monitor();
        this.isStartable = new IsStartableGuard();
        this.isStoppable = new IsStoppableGuard();
        this.hasReachedRunning = new HasReachedRunningGuard();
        this.isStopped = new IsStoppedGuard();
        this.listeners = Collections.synchronizedList(new ArrayList<ListenerCallQueue<Listener>>());
        this.snapshot = new StateSnapshot(State.NEW);
    }
    
    @GuardedBy("monitor")
    private void checkCurrentState(final State state) {
        final State state2 = this.state();
        if (state2 == state) {
            return;
        }
        if (state2 != State.FAILED) {
            throw new IllegalStateException("Expected the service to be " + state + ", but was " + state2);
        }
        throw new IllegalStateException("Expected the service to be " + state + ", but the service has FAILED", this.failureCause());
    }
    
    private void executeListeners() {
        if (!this.monitor.isOccupiedByCurrentThread()) {
            for (int i = 0; i < this.listeners.size(); ++i) {
                this.listeners.get(i).execute();
            }
        }
    }
    
    @GuardedBy("monitor")
    private void failed(final State obj, final Throwable obj2) {
        ((ListenerCallQueue.Callback<Listener>)new ListenerCallQueue.Callback<Listener>("failed({from = " + obj + ", cause = " + obj2 + "})") {
            void call(final Listener listener) {
                listener.failed(obj, obj2);
            }
        }).enqueueOn(this.listeners);
    }
    
    @GuardedBy("monitor")
    private void running() {
        AbstractService.RUNNING_CALLBACK.enqueueOn(this.listeners);
    }
    
    @GuardedBy("monitor")
    private void starting() {
        AbstractService.STARTING_CALLBACK.enqueueOn(this.listeners);
    }
    
    @GuardedBy("monitor")
    private void stopping(final State state) {
        if (state != State.STARTING) {
            if (state != State.RUNNING) {
                throw new AssertionError();
            }
            AbstractService.STOPPING_FROM_RUNNING_CALLBACK.enqueueOn(this.listeners);
        }
        else {
            AbstractService.STOPPING_FROM_STARTING_CALLBACK.enqueueOn(this.listeners);
        }
    }
    
    private static ListenerCallQueue.Callback<Listener> stoppingCallback(final State obj) {
        return new ListenerCallQueue.Callback<Listener>("stopping({from = " + obj + "})") {
            void call(final Listener listener) {
                listener.stopping(obj);
            }
        };
    }
    
    @GuardedBy("monitor")
    private void terminated(final State state) {
        switch (state) {
            default: {
                throw new AssertionError();
            }
            case NEW: {
                AbstractService.TERMINATED_FROM_NEW_CALLBACK.enqueueOn(this.listeners);
                break;
            }
            case RUNNING: {
                AbstractService.TERMINATED_FROM_RUNNING_CALLBACK.enqueueOn(this.listeners);
                break;
            }
            case STOPPING: {
                AbstractService.TERMINATED_FROM_STOPPING_CALLBACK.enqueueOn(this.listeners);
                break;
            }
        }
    }
    
    private static ListenerCallQueue.Callback<Listener> terminatedCallback(final State obj) {
        return new ListenerCallQueue.Callback<Listener>("terminated({from = " + obj + "})") {
            void call(final Listener listener) {
                listener.terminated(obj);
            }
        };
    }
    
    @Override
    public final void addListener(final Listener listener, final Executor executor) {
        Preconditions.checkNotNull(listener, (Object)"listener");
        Preconditions.checkNotNull(executor, (Object)"executor");
        this.monitor.enter();
        try {
            if (!this.state().isTerminal()) {
                this.listeners.add(new ListenerCallQueue<Listener>(listener, executor));
            }
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitRunning() {
        this.monitor.enterWhenUninterruptibly(this.hasReachedRunning);
        try {
            this.checkCurrentState(State.RUNNING);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitRunning(final long n, final TimeUnit timeUnit) throws TimeoutException {
        if (!this.monitor.enterWhenUninterruptibly(this.hasReachedRunning, n, timeUnit)) {
            throw new TimeoutException("Timed out waiting for " + this + " to reach the RUNNING state.");
        }
        try {
            this.checkCurrentState(State.RUNNING);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitTerminated() {
        this.monitor.enterWhenUninterruptibly(this.isStopped);
        try {
            this.checkCurrentState(State.TERMINATED);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitTerminated(final long n, final TimeUnit timeUnit) throws TimeoutException {
        if (!this.monitor.enterWhenUninterruptibly(this.isStopped, n, timeUnit)) {
            throw new TimeoutException("Timed out waiting for " + this + " to reach a terminal state. " + "Current state: " + this.state());
        }
        try {
            this.checkCurrentState(State.TERMINATED);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    protected abstract void doStart();
    
    protected abstract void doStop();
    
    @Override
    public final Throwable failureCause() {
        return this.snapshot.failureCause();
    }
    
    @Override
    public final boolean isRunning() {
        return this.state() == State.RUNNING;
    }
    
    protected final void notifyFailed(final Throwable cause) {
        Preconditions.checkNotNull(cause);
        this.monitor.enter();
        Label_0170: {
            State state = null;
            Label_0146: {
                try {
                    state = this.state();
                    switch (state) {
                        default: {
                            throw new AssertionError((Object)("Unexpected state: " + state));
                        }
                        case NEW:
                        case TERMINATED: {
                            break;
                        }
                        case STARTING:
                        case RUNNING:
                        case STOPPING: {
                            break Label_0146;
                        }
                        case FAILED: {
                            break Label_0170;
                        }
                    }
                }
                finally {
                    this.monitor.leave();
                    this.executeListeners();
                }
                throw new IllegalStateException("Failed while in state:" + state, cause);
            }
            this.snapshot = new StateSnapshot(State.FAILED, false, cause);
            this.failed(state, cause);
        }
        this.monitor.leave();
        this.executeListeners();
    }
    
    protected final void notifyStarted() {
        while (true) {
            this.monitor.enter();
            try {
                if (this.snapshot.state != State.STARTING) {
                    final IllegalStateException ex = new IllegalStateException("Cannot notifyStarted() when the service is " + this.snapshot.state);
                    this.notifyFailed(ex);
                    throw ex;
                }
                if (!this.snapshot.shutdownWhenStartupFinishes) {
                    this.snapshot = new StateSnapshot(State.RUNNING);
                    this.running();
                    return;
                }
            }
            finally {
                this.monitor.leave();
                this.executeListeners();
            }
            this.snapshot = new StateSnapshot(State.STOPPING);
            this.doStop();
        }
    }
    
    protected final void notifyStopped() {
        this.monitor.enter();
        try {
            final State state = this.snapshot.state;
            if (state != State.STOPPING && state != State.RUNNING) {
                final IllegalStateException ex = new IllegalStateException("Cannot notifyStopped() when the service is " + state);
                this.notifyFailed(ex);
                throw ex;
            }
            this.snapshot = new StateSnapshot(State.TERMINATED);
            this.terminated(state);
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }
    
    @Override
    public final Service startAsync() {
        if (!this.monitor.enterIf(this.isStartable)) {
            throw new IllegalStateException("Service " + this + " has already been started");
        }
        try {
            this.snapshot = new StateSnapshot(State.STARTING);
            this.starting();
            this.doStart();
            return this;
        }
        catch (final Throwable t) {
            this.notifyFailed(t);
            this.monitor.leave();
            this.executeListeners();
            return this;
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }
    
    @Override
    public final State state() {
        return this.snapshot.externalState();
    }
    
    @Override
    public final Service stopAsync() {
        if (this.monitor.enterIf(this.isStoppable)) {
            State state = null;
        Label_0229:
            while (true) {
                Label_0157: {
                    try {
                        state = this.state();
                        switch (state) {
                            default: {
                                throw new AssertionError((Object)("Unexpected state: " + state));
                            }
                            case NEW: {
                                break Label_0157;
                            }
                            case STARTING: {
                                break Label_0157;
                                break Label_0157;
                            }
                            case RUNNING: {
                                break;
                            }
                            case STOPPING:
                            case TERMINATED:
                            case FAILED: {
                                break Label_0229;
                            }
                        }
                    }
                    catch (final Throwable t) {
                        this.notifyFailed(t);
                        return this;
                        try {
                            this.snapshot = new StateSnapshot(State.TERMINATED);
                            this.terminated(State.NEW);
                            return this;
                            this.snapshot = new StateSnapshot(State.STARTING, true, null);
                            this.stopping(State.STARTING);
                        }
                        finally {
                            this.monitor.leave();
                            this.executeListeners();
                        }
                    }
                }
                this.snapshot = new StateSnapshot(State.STOPPING);
                this.stopping(State.RUNNING);
                this.doStop();
                return this;
            }
            throw new AssertionError((Object)("isStoppable is incorrectly implemented, saw: " + state));
        }
        return this;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" + this.state() + "]";
    }
    
    private final class HasReachedRunningGuard extends Guard
    {
        HasReachedRunningGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            boolean b = false;
            if (AbstractService.this.state().compareTo(State.RUNNING) >= 0) {
                b = true;
            }
            return b;
        }
    }
    
    private final class IsStartableGuard extends Guard
    {
        IsStartableGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            return AbstractService.this.state() == State.NEW;
        }
    }
    
    private final class IsStoppableGuard extends Guard
    {
        IsStoppableGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            boolean b = false;
            if (AbstractService.this.state().compareTo(State.RUNNING) <= 0) {
                b = true;
            }
            return b;
        }
    }
    
    private final class IsStoppedGuard extends Guard
    {
        IsStoppedGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            return AbstractService.this.state().isTerminal();
        }
    }
    
    @Immutable
    private static final class StateSnapshot
    {
        @Nullable
        final Throwable failure;
        final boolean shutdownWhenStartupFinishes;
        final State state;
        
        StateSnapshot(final State state) {
            this(state, false, null);
        }
        
        StateSnapshot(final State state, final boolean shutdownWhenStartupFinishes, @Nullable final Throwable failure) {
            Preconditions.checkArgument(!shutdownWhenStartupFinishes || state == State.STARTING, "shudownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", state);
            boolean b;
            if (failure == null) {
                b = false;
            }
            else {
                b = true;
            }
            boolean b2;
            if (state != State.FAILED) {
                b2 = false;
            }
            else {
                b2 = true;
            }
            Preconditions.checkArgument(!(b ^ b2), "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.", state, failure);
            this.state = state;
            this.shutdownWhenStartupFinishes = shutdownWhenStartupFinishes;
            this.failure = failure;
        }
        
        State externalState() {
            if (this.shutdownWhenStartupFinishes && this.state == State.STARTING) {
                return State.STOPPING;
            }
            return this.state;
        }
        
        Throwable failureCause() {
            Preconditions.checkState(this.state == State.FAILED, "failureCause() is only valid if the service has failed, service is %s", this.state);
            return this.failure;
        }
    }
}
