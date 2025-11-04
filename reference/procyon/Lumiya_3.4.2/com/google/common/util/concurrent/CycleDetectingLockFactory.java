// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.logging.Level;
import com.google.common.collect.Sets;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Arrays;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import com.google.j2objc.annotations.Weak;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import com.google.common.base.MoreObjects;
import com.google.common.annotations.VisibleForTesting;
import java.util.EnumMap;
import java.util.Collections;
import com.google.common.collect.Maps;
import java.util.List;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import java.util.logging.Logger;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.ArrayList;
import javax.annotation.concurrent.ThreadSafe;
import com.google.common.annotations.Beta;

@Beta
@ThreadSafe
public class CycleDetectingLockFactory
{
    private static final ThreadLocal<ArrayList<LockGraphNode>> acquiredLocks;
    private static final ConcurrentMap<Class<? extends Enum>, Map<? extends Enum, LockGraphNode>> lockGraphNodesPerType;
    private static final Logger logger;
    final Policy policy;
    
    static {
        lockGraphNodesPerType = new MapMaker().weakKeys().makeMap();
        logger = Logger.getLogger(CycleDetectingLockFactory.class.getName());
        acquiredLocks = new ThreadLocal<ArrayList<LockGraphNode>>() {
            @Override
            protected ArrayList<LockGraphNode> initialValue() {
                return Lists.newArrayListWithCapacity(3);
            }
        };
    }
    
    private CycleDetectingLockFactory(final Policy policy) {
        this.policy = Preconditions.checkNotNull(policy);
    }
    
    private void aboutToAcquire(final CycleDetectingLock cycleDetectingLock) {
        if (!cycleDetectingLock.isAcquiredByCurrentThread()) {
            final ArrayList list = CycleDetectingLockFactory.acquiredLocks.get();
            final LockGraphNode lockGraphNode = cycleDetectingLock.getLockGraphNode();
            lockGraphNode.checkAcquiredLocks(this.policy, list);
            list.add(lockGraphNode);
        }
    }
    
    @VisibleForTesting
    static <E extends Enum<E>> Map<E, LockGraphNode> createNodes(final Class<E> clazz) {
        final int n = 0;
        final EnumMap<E, Object> enumMap = Maps.newEnumMap(clazz);
        final E[] array = clazz.getEnumConstants();
        final int length = array.length;
        final ArrayList<Object> arrayListWithCapacity = Lists.newArrayListWithCapacity(length);
        for (final Enum<E> key : array) {
            final LockGraphNode lockGraphNode = new LockGraphNode(getLockName(key));
            arrayListWithCapacity.add(lockGraphNode);
            enumMap.put((E)key, lockGraphNode);
        }
        for (int j = 1; j < length; ++j) {
            arrayListWithCapacity.get(j).checkAcquiredLocks(Policies.THROW, arrayListWithCapacity.subList(0, j));
        }
        for (int k = n; k < length - 1; ++k) {
            arrayListWithCapacity.get(k).checkAcquiredLocks(Policies.DISABLED, arrayListWithCapacity.subList(k + 1, length));
        }
        return Collections.unmodifiableMap((Map<? extends E, ? extends LockGraphNode>)enumMap);
    }
    
    private static String getLockName(final Enum<?> enum1) {
        return enum1.getDeclaringClass().getSimpleName() + "." + enum1.name();
    }
    
    private static Map<? extends Enum, LockGraphNode> getOrCreateNodes(final Class<? extends Enum> clazz) {
        final Map map = CycleDetectingLockFactory.lockGraphNodesPerType.get(clazz);
        if (map == null) {
            final Map<Enum, LockGraphNode> nodes = createNodes(clazz);
            return MoreObjects.firstNonNull(CycleDetectingLockFactory.lockGraphNodesPerType.putIfAbsent(clazz, nodes), nodes);
        }
        return map;
    }
    
    private void lockStateChanged(final CycleDetectingLock cycleDetectingLock) {
        if (!cycleDetectingLock.isAcquiredByCurrentThread()) {
            final ArrayList list = CycleDetectingLockFactory.acquiredLocks.get();
            final LockGraphNode lockGraphNode = cycleDetectingLock.getLockGraphNode();
            int size = list.size();
            int n;
            do {
                n = size - 1;
                if (n < 0) {
                    return;
                }
                size = n;
            } while (list.get(n) != lockGraphNode);
            list.remove(n);
        }
    }
    
    public static CycleDetectingLockFactory newInstance(final Policy policy) {
        return new CycleDetectingLockFactory(policy);
    }
    
    public static <E extends Enum<E>> WithExplicitOrdering<E> newInstanceWithExplicitOrdering(final Class<E> clazz, final Policy policy) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(policy);
        return new WithExplicitOrdering<E>(policy, (Map<E, LockGraphNode>)getOrCreateNodes(clazz));
    }
    
    public ReentrantLock newReentrantLock(final String s) {
        return this.newReentrantLock(s, false);
    }
    
    public ReentrantLock newReentrantLock(final String s, final boolean fair) {
        ReentrantLock reentrantLock;
        if (this.policy != Policies.DISABLED) {
            reentrantLock = new CycleDetectingReentrantLock(new LockGraphNode(s), fair);
        }
        else {
            reentrantLock = new ReentrantLock(fair);
        }
        return reentrantLock;
    }
    
    public ReentrantReadWriteLock newReentrantReadWriteLock(final String s) {
        return this.newReentrantReadWriteLock(s, false);
    }
    
    public ReentrantReadWriteLock newReentrantReadWriteLock(final String s, final boolean fair) {
        ReentrantReadWriteLock reentrantReadWriteLock;
        if (this.policy != Policies.DISABLED) {
            reentrantReadWriteLock = new CycleDetectingReentrantReadWriteLock(new LockGraphNode(s), fair);
        }
        else {
            reentrantReadWriteLock = new ReentrantReadWriteLock(fair);
        }
        return reentrantReadWriteLock;
    }
    
    private interface CycleDetectingLock
    {
        LockGraphNode getLockGraphNode();
        
        boolean isAcquiredByCurrentThread();
    }
    
    final class CycleDetectingReentrantLock extends ReentrantLock implements CycleDetectingLock
    {
        private final LockGraphNode lockGraphNode;
        
        private CycleDetectingReentrantLock(final LockGraphNode lockGraphNode, final boolean fair) {
            super(fair);
            this.lockGraphNode = Preconditions.checkNotNull(lockGraphNode);
        }
        
        @Override
        public LockGraphNode getLockGraphNode() {
            return this.lockGraphNode;
        }
        
        @Override
        public boolean isAcquiredByCurrentThread() {
            return this.isHeldByCurrentThread();
        }
        
        @Override
        public void lock() {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this);
            try {
                super.lock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this);
            }
        }
        
        @Override
        public void lockInterruptibly() throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this);
            try {
                super.lockInterruptibly();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this);
            }
        }
        
        @Override
        public boolean tryLock() {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this);
            try {
                return super.tryLock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this);
            }
        }
        
        @Override
        public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this);
            try {
                return super.tryLock(timeout, unit);
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this);
            }
        }
        
        @Override
        public void unlock() {
            try {
                super.unlock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this);
            }
        }
    }
    
    private class CycleDetectingReentrantReadLock extends ReadLock
    {
        @Weak
        final CycleDetectingReentrantReadWriteLock readWriteLock;
        
        CycleDetectingReentrantReadLock(final CycleDetectingReentrantReadWriteLock cycleDetectingReentrantReadWriteLock) {
            super(cycleDetectingReentrantReadWriteLock);
            this.readWriteLock = cycleDetectingReentrantReadWriteLock;
        }
        
        @Override
        public void lock() {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                super.lock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public void lockInterruptibly() throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                super.lockInterruptibly();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public boolean tryLock() {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                return super.tryLock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                return super.tryLock(timeout, unit);
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public void unlock() {
            try {
                super.unlock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
    }
    
    final class CycleDetectingReentrantReadWriteLock extends ReentrantReadWriteLock implements CycleDetectingLock
    {
        private final LockGraphNode lockGraphNode;
        private final CycleDetectingReentrantReadLock readLock;
        private final CycleDetectingReentrantWriteLock writeLock;
        
        private CycleDetectingReentrantReadWriteLock(final LockGraphNode lockGraphNode, final boolean fair) {
            super(fair);
            this.readLock = new CycleDetectingReentrantReadLock(this);
            this.writeLock = new CycleDetectingReentrantWriteLock(this);
            this.lockGraphNode = Preconditions.checkNotNull(lockGraphNode);
        }
        
        @Override
        public LockGraphNode getLockGraphNode() {
            return this.lockGraphNode;
        }
        
        @Override
        public boolean isAcquiredByCurrentThread() {
            boolean b = false;
            if (this.isWriteLockedByCurrentThread() || this.getReadHoldCount() > 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public ReadLock readLock() {
            return this.readLock;
        }
        
        @Override
        public WriteLock writeLock() {
            return this.writeLock;
        }
    }
    
    private class CycleDetectingReentrantWriteLock extends WriteLock
    {
        @Weak
        final CycleDetectingReentrantReadWriteLock readWriteLock;
        
        CycleDetectingReentrantWriteLock(final CycleDetectingReentrantReadWriteLock cycleDetectingReentrantReadWriteLock) {
            super(cycleDetectingReentrantReadWriteLock);
            this.readWriteLock = cycleDetectingReentrantReadWriteLock;
        }
        
        @Override
        public void lock() {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                super.lock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public void lockInterruptibly() throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                super.lockInterruptibly();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public boolean tryLock() {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                return super.tryLock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire((CycleDetectingLock)this.readWriteLock);
            try {
                return super.tryLock(timeout, unit);
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
        
        @Override
        public void unlock() {
            try {
                super.unlock();
            }
            finally {
                CycleDetectingLockFactory.this.lockStateChanged((CycleDetectingLock)this.readWriteLock);
            }
        }
    }
    
    private static class ExampleStackTrace extends IllegalStateException
    {
        static final StackTraceElement[] EMPTY_STACK_TRACE;
        static final Set<String> EXCLUDED_CLASS_NAMES;
        
        static {
            EMPTY_STACK_TRACE = new StackTraceElement[0];
            EXCLUDED_CLASS_NAMES = ImmutableSet.of(CycleDetectingLockFactory.class.getName(), ExampleStackTrace.class.getName(), LockGraphNode.class.getName());
        }
        
        ExampleStackTrace(final LockGraphNode lockGraphNode, final LockGraphNode lockGraphNode2) {
            int i = 0;
            super(lockGraphNode.getLockName() + " -> " + lockGraphNode2.getLockName());
            final StackTraceElement[] stackTrace = this.getStackTrace();
            for (int length = stackTrace.length; i < length; ++i) {
                if (WithExplicitOrdering.class.getName().equals(stackTrace[i].getClassName())) {
                    this.setStackTrace(ExampleStackTrace.EMPTY_STACK_TRACE);
                    break;
                }
                if (!ExampleStackTrace.EXCLUDED_CLASS_NAMES.contains(stackTrace[i].getClassName())) {
                    this.setStackTrace(Arrays.copyOfRange(stackTrace, i, length));
                    break;
                }
            }
        }
    }
    
    private static class LockGraphNode
    {
        final Map<LockGraphNode, ExampleStackTrace> allowedPriorLocks;
        final Map<LockGraphNode, PotentialDeadlockException> disallowedPriorLocks;
        final String lockName;
        
        LockGraphNode(final String s) {
            this.allowedPriorLocks = (Map<LockGraphNode, ExampleStackTrace>)new MapMaker().weakKeys().makeMap();
            this.disallowedPriorLocks = (Map<LockGraphNode, PotentialDeadlockException>)new MapMaker().weakKeys().makeMap();
            this.lockName = Preconditions.checkNotNull(s);
        }
        
        @Nullable
        private ExampleStackTrace findPathTo(final LockGraphNode lockGraphNode, final Set<LockGraphNode> set) {
            if (!set.add(this)) {
                return null;
            }
            final ExampleStackTrace exampleStackTrace = this.allowedPriorLocks.get(lockGraphNode);
            if (exampleStackTrace == null) {
                for (final Map.Entry<LockGraphNode, V> entry : this.allowedPriorLocks.entrySet()) {
                    final LockGraphNode lockGraphNode2 = entry.getKey();
                    final ExampleStackTrace pathTo = lockGraphNode2.findPathTo(lockGraphNode, set);
                    if (pathTo != null) {
                        final ExampleStackTrace exampleStackTrace2 = new ExampleStackTrace(lockGraphNode2, this);
                        exampleStackTrace2.setStackTrace(((ExampleStackTrace)entry.getValue()).getStackTrace());
                        exampleStackTrace2.initCause(pathTo);
                        return exampleStackTrace2;
                    }
                }
                return null;
            }
            return exampleStackTrace;
        }
        
        void checkAcquiredLock(final Policy policy, final LockGraphNode lockGraphNode) {
            Preconditions.checkState(this != lockGraphNode, "Attempted to acquire multiple locks with the same rank %s", lockGraphNode.getLockName());
            if (this.allowedPriorLocks.containsKey(lockGraphNode)) {
                return;
            }
            final PotentialDeadlockException ex = this.disallowedPriorLocks.get(lockGraphNode);
            if (ex == null) {
                final ExampleStackTrace pathTo = lockGraphNode.findPathTo(this, Sets.newIdentityHashSet());
                if (pathTo != null) {
                    final PotentialDeadlockException ex2 = new PotentialDeadlockException(lockGraphNode, this, pathTo);
                    this.disallowedPriorLocks.put(lockGraphNode, ex2);
                    policy.handlePotentialDeadlock(ex2);
                }
                else {
                    this.allowedPriorLocks.put(lockGraphNode, new ExampleStackTrace(lockGraphNode, this));
                }
                return;
            }
            policy.handlePotentialDeadlock(new PotentialDeadlockException(lockGraphNode, this, ex.getConflictingStackTrace()));
        }
        
        void checkAcquiredLocks(final Policy policy, final List<LockGraphNode> list) {
            for (int size = list.size(), i = 0; i < size; ++i) {
                this.checkAcquiredLock(policy, (LockGraphNode)list.get(i));
            }
        }
        
        String getLockName() {
            return this.lockName;
        }
    }
    
    @Beta
    public enum Policies implements Policy
    {
        DISABLED {
            @Override
            public void handlePotentialDeadlock(final PotentialDeadlockException ex) {
            }
        }, 
        THROW {
            @Override
            public void handlePotentialDeadlock(final PotentialDeadlockException ex) {
                throw ex;
            }
        }, 
        WARN {
            @Override
            public void handlePotentialDeadlock(final PotentialDeadlockException thrown) {
                CycleDetectingLockFactory.logger.log(Level.SEVERE, "Detected potential deadlock", thrown);
            }
        };
    }
    
    @Beta
    @ThreadSafe
    public interface Policy
    {
        void handlePotentialDeadlock(final PotentialDeadlockException p0);
    }
    
    @Beta
    public static final class PotentialDeadlockException extends ExampleStackTrace
    {
        private final ExampleStackTrace conflictingStackTrace;
        
        private PotentialDeadlockException(final LockGraphNode lockGraphNode, final LockGraphNode lockGraphNode2, final ExampleStackTrace conflictingStackTrace) {
            super(lockGraphNode, lockGraphNode2);
            this.initCause(this.conflictingStackTrace = conflictingStackTrace);
        }
        
        public ExampleStackTrace getConflictingStackTrace() {
            return this.conflictingStackTrace;
        }
        
        @Override
        public String getMessage() {
            final StringBuilder sb = new StringBuilder(super.getMessage());
            for (Throwable t = this.conflictingStackTrace; t != null; t = t.getCause()) {
                sb.append(", ").append(t.getMessage());
            }
            return sb.toString();
        }
    }
    
    @Beta
    public static final class WithExplicitOrdering<E extends Enum<E>> extends CycleDetectingLockFactory
    {
        private final Map<E, LockGraphNode> lockGraphNodes;
        
        @VisibleForTesting
        WithExplicitOrdering(final Policy policy, final Map<E, LockGraphNode> lockGraphNodes) {
            super(policy, null);
            this.lockGraphNodes = lockGraphNodes;
        }
        
        public ReentrantLock newReentrantLock(final E e) {
            return this.newReentrantLock(e, false);
        }
        
        public ReentrantLock newReentrantLock(final E e, final boolean fair) {
            ReentrantLock reentrantLock;
            if (this.policy != Policies.DISABLED) {
                reentrantLock = new CycleDetectingReentrantLock((LockGraphNode)this.lockGraphNodes.get(e), fair);
            }
            else {
                reentrantLock = new ReentrantLock(fair);
            }
            return reentrantLock;
        }
        
        public ReentrantReadWriteLock newReentrantReadWriteLock(final E e) {
            return this.newReentrantReadWriteLock(e, false);
        }
        
        public ReentrantReadWriteLock newReentrantReadWriteLock(final E e, final boolean fair) {
            ReentrantReadWriteLock reentrantReadWriteLock;
            if (this.policy != Policies.DISABLED) {
                reentrantReadWriteLock = new CycleDetectingReentrantReadWriteLock((LockGraphNode)this.lockGraphNodes.get(e), fair);
            }
            else {
                reentrantReadWriteLock = new ReentrantReadWriteLock(fair);
            }
            return reentrantReadWriteLock;
        }
    }
}
