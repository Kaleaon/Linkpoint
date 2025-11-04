// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.AbstractCollection;
import java.util.Comparator;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import java.util.EnumSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.ArrayList;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multiset;
import com.google.common.base.Stopwatch;
import java.util.Map;
import com.google.common.collect.SetMultimap;
import javax.annotation.concurrent.GuardedBy;
import java.util.Collection;
import com.google.common.collect.Collections2;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import java.util.Iterator;
import java.util.List;
import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import com.google.common.collect.ImmutableCollection;
import java.util.logging.Level;
import com.google.common.collect.ImmutableList;
import java.util.logging.Logger;
import com.google.common.annotations.Beta;

@Beta
public final class ServiceManager
{
    private static final ListenerCallQueue.Callback<Listener> HEALTHY_CALLBACK;
    private static final ListenerCallQueue.Callback<Listener> STOPPED_CALLBACK;
    private static final Logger logger;
    private final ImmutableList<Service> services;
    private final ServiceManagerState state;
    
    static {
        logger = Logger.getLogger(ServiceManager.class.getName());
        HEALTHY_CALLBACK = new ListenerCallQueue.Callback<Listener>("healthy()") {
            void call(final Listener listener) {
                listener.healthy();
            }
        };
        STOPPED_CALLBACK = new ListenerCallQueue.Callback<Listener>("stopped()") {
            void call(final Listener listener) {
                listener.stopped();
            }
        };
    }
    
    public ServiceManager(final Iterable<? extends Service> iterable) {
        List<E> services = (List<E>)ImmutableList.copyOf((Iterable<?>)iterable);
        if (((AbstractCollection)services).isEmpty()) {
            ServiceManager.logger.log(Level.WARNING, "ServiceManager configured with no services.  Is your application configured properly?", new EmptyServiceManagerWarning());
            services = (List<E>)ImmutableList.of(new NoOpService());
        }
        this.state = new ServiceManagerState((ImmutableCollection<Service>)services);
        this.services = (ImmutableList<Service>)services;
        final WeakReference weakReference = new WeakReference((T)this.state);
        for (final Service service : services) {
            service.addListener((Service.Listener)new ServiceListener(service, weakReference), MoreExecutors.directExecutor());
            Preconditions.checkArgument(service.state() == Service.State.NEW, "Can only manage NEW services, %s", service);
        }
        this.state.markReady();
    }
    
    public void addListener(final Listener listener) {
        this.state.addListener(listener, MoreExecutors.directExecutor());
    }
    
    public void addListener(final Listener listener, final Executor executor) {
        this.state.addListener(listener, executor);
    }
    
    public void awaitHealthy() {
        this.state.awaitHealthy();
    }
    
    public void awaitHealthy(final long n, final TimeUnit timeUnit) throws TimeoutException {
        this.state.awaitHealthy(n, timeUnit);
    }
    
    public void awaitStopped() {
        this.state.awaitStopped();
    }
    
    public void awaitStopped(final long n, final TimeUnit timeUnit) throws TimeoutException {
        this.state.awaitStopped(n, timeUnit);
    }
    
    public boolean isHealthy() {
        final Iterator iterator = this.services.iterator();
        while (iterator.hasNext()) {
            if (!((Service)iterator.next()).isRunning()) {
                return false;
            }
        }
        return true;
    }
    
    public ImmutableMultimap<Service.State, Service> servicesByState() {
        return this.state.servicesByState();
    }
    
    public ServiceManager startAsync() {
        for (final Service service : this.services) {
            final Service.State state = service.state();
            Preconditions.checkState(state == Service.State.NEW, "Service %s is %s, cannot start it.", service, state);
        }
        for (final Service obj : this.services) {
            try {
                this.state.tryStartTiming(obj);
                obj.startAsync();
            }
            catch (final IllegalStateException thrown) {
                ServiceManager.logger.log(Level.WARNING, "Unable to start Service " + obj, thrown);
            }
        }
        return this;
    }
    
    public ImmutableMap<Service, Long> startupTimes() {
        return this.state.startupTimes();
    }
    
    public ServiceManager stopAsync() {
        final Iterator iterator = this.services.iterator();
        while (iterator.hasNext()) {
            ((Service)iterator.next()).stopAsync();
        }
        return this;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(ServiceManager.class).add("services", Collections2.filter(this.services, (Predicate<? super Service>)Predicates.not((Predicate<? super E>)Predicates.instanceOf(NoOpService.class)))).toString();
    }
    
    private static final class EmptyServiceManagerWarning extends Throwable
    {
    }
    
    @Beta
    public abstract static class Listener
    {
        public void failure(final Service service) {
        }
        
        public void healthy() {
        }
        
        public void stopped() {
        }
    }
    
    private static final class NoOpService extends AbstractService
    {
        @Override
        protected void doStart() {
            this.notifyStarted();
        }
        
        @Override
        protected void doStop() {
            this.notifyStopped();
        }
    }
    
    private static final class ServiceListener extends Service.Listener
    {
        final Service service;
        final WeakReference<ServiceManagerState> state;
        
        ServiceListener(final Service service, final WeakReference<ServiceManagerState> state) {
            this.service = service;
            this.state = state;
        }
        
        @Override
        public void failed(final State obj, final Throwable thrown) {
            final ServiceManagerState serviceManagerState = this.state.get();
            if (serviceManagerState != null) {
                if (!(this.service instanceof NoOpService)) {
                    ServiceManager.logger.log(Level.SEVERE, "Service " + this.service + " has failed in the " + obj + " state.", thrown);
                }
                serviceManagerState.transitionService(this.service, obj, State.FAILED);
            }
        }
        
        @Override
        public void running() {
            final ServiceManagerState serviceManagerState = this.state.get();
            if (serviceManagerState != null) {
                serviceManagerState.transitionService(this.service, State.STARTING, State.RUNNING);
            }
        }
        
        @Override
        public void starting() {
            final ServiceManagerState serviceManagerState = this.state.get();
            if (serviceManagerState != null) {
                serviceManagerState.transitionService(this.service, State.NEW, State.STARTING);
                if (!(this.service instanceof NoOpService)) {
                    ServiceManager.logger.log(Level.FINE, "Starting {0}.", this.service);
                }
            }
        }
        
        @Override
        public void stopping(final State state) {
            final ServiceManagerState serviceManagerState = this.state.get();
            if (serviceManagerState != null) {
                serviceManagerState.transitionService(this.service, state, State.STOPPING);
            }
        }
        
        @Override
        public void terminated(final State state) {
            final ServiceManagerState serviceManagerState = this.state.get();
            if (serviceManagerState != null) {
                if (!(this.service instanceof NoOpService)) {
                    ServiceManager.logger.log(Level.FINE, "Service {0} has terminated. Previous state was: {1}", new Object[] { this.service, state });
                }
                serviceManagerState.transitionService(this.service, state, State.TERMINATED);
            }
        }
    }
    
    private static final class ServiceManagerState
    {
        final Monitor.Guard awaitHealthGuard;
        @GuardedBy("monitor")
        final List<ListenerCallQueue<Listener>> listeners;
        final Monitor monitor;
        final int numberOfServices;
        @GuardedBy("monitor")
        boolean ready;
        @GuardedBy("monitor")
        final SetMultimap<Service.State, Service> servicesByState;
        @GuardedBy("monitor")
        final Map<Service, Stopwatch> startupTimers;
        @GuardedBy("monitor")
        final Multiset<Service.State> states;
        final Monitor.Guard stoppedGuard;
        @GuardedBy("monitor")
        boolean transitioned;
        
        ServiceManagerState(final ImmutableCollection<Service> collection) {
            this.monitor = new Monitor();
            this.servicesByState = MultimapBuilder.enumKeys(Service.State.class).linkedHashSetValues().build();
            this.states = this.servicesByState.keys();
            this.startupTimers = (Map<Service, Stopwatch>)Maps.newIdentityHashMap();
            this.awaitHealthGuard = new AwaitHealthGuard();
            this.stoppedGuard = new StoppedGuard();
            this.listeners = Collections.synchronizedList(new ArrayList<ListenerCallQueue<Listener>>());
            this.numberOfServices = collection.size();
            this.servicesByState.putAll(Service.State.NEW, (Iterable<?>)collection);
        }
        
        void addListener(final Listener listener, final Executor executor) {
            Preconditions.checkNotNull(listener, (Object)"listener");
            Preconditions.checkNotNull(executor, (Object)"executor");
            this.monitor.enter();
            try {
                if (!this.stoppedGuard.isSatisfied()) {
                    this.listeners.add(new ListenerCallQueue<Listener>(listener, executor));
                }
            }
            finally {
                this.monitor.leave();
            }
        }
        
        void awaitHealthy() {
            this.monitor.enterWhenUninterruptibly(this.awaitHealthGuard);
            try {
                this.checkHealthy();
            }
            finally {
                this.monitor.leave();
            }
        }
        
        void awaitHealthy(final long n, final TimeUnit timeUnit) throws TimeoutException {
            this.monitor.enter();
            try {
                if (this.monitor.waitForUninterruptibly(this.awaitHealthGuard, n, timeUnit)) {
                    this.checkHealthy();
                    return;
                }
                throw new TimeoutException("Timeout waiting for the services to become healthy. The following services have not started: " + Multimaps.filterKeys(this.servicesByState, Predicates.in((Collection<? extends Service.State>)ImmutableSet.of(Service.State.NEW, Service.State.STARTING))));
            }
            finally {
                this.monitor.leave();
            }
        }
        
        void awaitStopped() {
            this.monitor.enterWhenUninterruptibly(this.stoppedGuard);
            this.monitor.leave();
        }
        
        void awaitStopped(final long n, final TimeUnit timeUnit) throws TimeoutException {
            this.monitor.enter();
            try {
                if (this.monitor.waitForUninterruptibly(this.stoppedGuard, n, timeUnit)) {
                    return;
                }
                throw new TimeoutException("Timeout waiting for the services to stop. The following services have not stopped: " + Multimaps.filterKeys(this.servicesByState, (Predicate<? super Service.State>)Predicates.not(Predicates.in((Collection<? extends K>)EnumSet.of(Service.State.TERMINATED, Service.State.FAILED)))));
            }
            finally {
                this.monitor.leave();
            }
        }
        
        @GuardedBy("monitor")
        void checkHealthy() {
            if (this.states.count(Service.State.RUNNING) == this.numberOfServices) {
                return;
            }
            throw new IllegalStateException("Expected to be healthy after starting. The following services are not running: " + Multimaps.filterKeys(this.servicesByState, (Predicate<? super Service.State>)Predicates.not((Predicate<? super K>)Predicates.equalTo((T)Service.State.RUNNING))));
        }
        
        void executeListeners() {
            int i = 0;
            Preconditions.checkState(!this.monitor.isOccupiedByCurrentThread(), (Object)"It is incorrect to execute listeners with the monitor held.");
            while (i < this.listeners.size()) {
                this.listeners.get(i).execute();
                ++i;
            }
        }
        
        @GuardedBy("monitor")
        void fireFailedListeners(final Service obj) {
            ((ListenerCallQueue.Callback<Listener>)new ListenerCallQueue.Callback<Listener>("failed({service=" + obj + "})") {
                void call(final Listener listener) {
                    listener.failure(obj);
                }
            }).enqueueOn(this.listeners);
        }
        
        @GuardedBy("monitor")
        void fireHealthyListeners() {
            ServiceManager.HEALTHY_CALLBACK.enqueueOn(this.listeners);
        }
        
        @GuardedBy("monitor")
        void fireStoppedListeners() {
            ServiceManager.STOPPED_CALLBACK.enqueueOn(this.listeners);
        }
        
        void markReady() {
            while (true) {
                this.monitor.enter();
                while (true) {
                    ArrayList<Object> arrayList = null;
                    Object iterator = null;
                    Label_0093: {
                        try {
                            if (this.transitioned) {
                                arrayList = Lists.newArrayList();
                                iterator = this.servicesByState().values().iterator();
                                if (!((Iterator)iterator).hasNext()) {
                                    iterator = new IllegalArgumentException("Services started transitioning asynchronously before the ServiceManager was constructed: " + arrayList);
                                    throw iterator;
                                }
                                break Label_0093;
                            }
                        }
                        finally {
                            this.monitor.leave();
                        }
                        break;
                    }
                    final Service service = (Service)((Iterator)iterator).next();
                    if (service.state() != Service.State.NEW) {
                        arrayList.add(service);
                        continue;
                    }
                    continue;
                }
            }
            this.ready = true;
            this.monitor.leave();
        }
        
        ImmutableMultimap<Service.State, Service> servicesByState() {
            final ImmutableSetMultimap.Builder<Object, Object> builder = ImmutableSetMultimap.builder();
            this.monitor.enter();
            try {
                for (final Map.Entry entry : this.servicesByState.entries()) {
                    if (!(entry.getValue() instanceof NoOpService)) {
                        builder.put(entry);
                    }
                }
                this.monitor.leave();
                return (ImmutableMultimap<Service.State, Service>)builder.build();
            }
            finally {
                this.monitor.leave();
            }
        }
        
        ImmutableMap<Service, Long> startupTimes() {
            this.monitor.enter();
            try {
                final ArrayList<Object> arrayListWithCapacity = Lists.newArrayListWithCapacity(this.startupTimers.size());
                for (final Map.Entry<Service, V> entry : this.startupTimers.entrySet()) {
                    final Service service = entry.getKey();
                    final Stopwatch stopwatch = (Stopwatch)entry.getValue();
                    if (!stopwatch.isRunning() && !(service instanceof NoOpService)) {
                        arrayListWithCapacity.add(Maps.immutableEntry(service, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
                    }
                }
                this.monitor.leave();
                Collections.sort(arrayListWithCapacity, Ordering.natural().onResultOf((Function<Object, ? extends Comparable>)new Function<Map.Entry<Service, Long>, Long>() {
                    @Override
                    public Long apply(final Map.Entry<Service, Long> entry) {
                        return entry.getValue();
                    }
                }));
                return ImmutableMap.copyOf((Iterable<? extends Map.Entry<? extends Service, ? extends Long>>)arrayListWithCapacity);
            }
            finally {
                this.monitor.leave();
            }
        }
        
        void transitionService(final Service service, Service.State started, final Service.State state) {
            boolean b = false;
            Preconditions.checkNotNull(service);
            Label_0205: {
                if (started != state) {
                    break Label_0205;
                }
                Label_0123_Outer:Label_0130_Outer:
                while (true) {
                    Preconditions.checkArgument(b);
                    this.monitor.enter();
                    Label_0317: {
                    Label_0310:
                        while (true) {
                        Label_0302:
                            while (true) {
                                Label_0256: {
                                    try {
                                        this.transitioned = true;
                                        if (!this.ready) {
                                            return;
                                        }
                                        Preconditions.checkState(this.servicesByState.remove(started, service), "Service %s not at the expected location in the state map %s", service, started);
                                        Preconditions.checkState(this.servicesByState.put(state, service), "Service %s in the state map unexpectedly at %s", service, state);
                                        started = (Service.State)this.startupTimers.get(service);
                                        if (started == null) {
                                            started = (Service.State)Stopwatch.createStarted();
                                            this.startupTimers.put(service, (Stopwatch)started);
                                        }
                                        if (state.compareTo(Service.State.RUNNING) >= 0) {
                                            break Label_0256;
                                        }
                                        if (state == Service.State.FAILED) {
                                            break Label_0302;
                                        }
                                        if (this.states.count(Service.State.RUNNING) == this.numberOfServices) {
                                            break Label_0310;
                                        }
                                        if (this.states.count(Service.State.TERMINATED) + this.states.count(Service.State.FAILED) != this.numberOfServices) {
                                            return;
                                        }
                                        break Label_0317;
                                        b = true;
                                        continue Label_0123_Outer;
                                    }
                                    finally {
                                        this.monitor.leave();
                                        this.executeListeners();
                                    }
                                }
                                if (!((Stopwatch)started).isRunning()) {
                                    continue Label_0130_Outer;
                                }
                                ((Stopwatch)started).stop();
                                if (!(service instanceof NoOpService)) {
                                    ServiceManager.logger.log(Level.FINE, "Started {0} in {1}.", new Object[] { service, started });
                                    continue Label_0130_Outer;
                                }
                                continue Label_0130_Outer;
                            }
                            this.fireFailedListeners(service);
                            continue;
                        }
                        this.fireHealthyListeners();
                        return;
                    }
                    this.fireStoppedListeners();
                }
            }
        }
        
        void tryStartTiming(final Service service) {
            this.monitor.enter();
            try {
                if (this.startupTimers.get(service) == null) {
                    this.startupTimers.put(service, Stopwatch.createStarted());
                }
            }
            finally {
                this.monitor.leave();
            }
        }
        
        final class AwaitHealthGuard extends Guard
        {
            AwaitHealthGuard() {
                super(ServiceManagerState.this.monitor);
            }
            
            @Override
            public boolean isSatisfied() {
                boolean b = false;
                if (ServiceManagerState.this.states.count(Service.State.RUNNING) == ServiceManagerState.this.numberOfServices || ServiceManagerState.this.states.contains(Service.State.STOPPING) || ServiceManagerState.this.states.contains(Service.State.TERMINATED) || ServiceManagerState.this.states.contains(Service.State.FAILED)) {
                    b = true;
                }
                return b;
            }
        }
        
        final class StoppedGuard extends Guard
        {
            StoppedGuard() {
                super(ServiceManagerState.this.monitor);
            }
            
            @Override
            public boolean isSatisfied() {
                return ServiceManagerState.this.states.count(Service.State.TERMINATED) + ServiceManagerState.this.states.count(Service.State.FAILED) == ServiceManagerState.this.numberOfServices;
            }
        }
    }
}
