// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.connection;

import java.util.LinkedHashSet;
import okhttp3.Route;
import java.util.Set;

public final class RouteDatabase
{
    private final Set<Route> failedRoutes;
    
    public RouteDatabase() {
        this.failedRoutes = new LinkedHashSet<Route>();
    }
    
    public void connected(final Route route) {
        synchronized (this) {
            this.failedRoutes.remove(route);
        }
    }
    
    public void failed(final Route route) {
        synchronized (this) {
            this.failedRoutes.add(route);
        }
    }
    
    public boolean shouldPostpone(final Route route) {
        synchronized (this) {
            return this.failedRoutes.contains(route);
        }
    }
}
