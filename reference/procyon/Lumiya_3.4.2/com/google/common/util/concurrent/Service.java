// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import com.google.common.annotations.Beta;

@Beta
public interface Service
{
    void addListener(final Listener p0, final Executor p1);
    
    void awaitRunning();
    
    void awaitRunning(final long p0, final TimeUnit p1) throws TimeoutException;
    
    void awaitTerminated();
    
    void awaitTerminated(final long p0, final TimeUnit p1) throws TimeoutException;
    
    Throwable failureCause();
    
    boolean isRunning();
    
    Service startAsync();
    
    State state();
    
    Service stopAsync();
    
    @Beta
    public abstract static class Listener
    {
        public void failed(final State state, final Throwable t) {
        }
        
        public void running() {
        }
        
        public void starting() {
        }
        
        public void stopping(final State state) {
        }
        
        public void terminated(final State state) {
        }
    }
    
    @Beta
    public enum State
    {
        FAILED {
            @Override
            boolean isTerminal() {
                return true;
            }
        }, 
        NEW {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        RUNNING {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        STARTING {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        STOPPING {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        TERMINATED {
            @Override
            boolean isTerminal() {
                return true;
            }
        };
        
        abstract boolean isTerminal();
    }
}
