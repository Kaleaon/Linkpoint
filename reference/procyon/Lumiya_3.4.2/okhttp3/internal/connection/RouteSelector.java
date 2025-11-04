// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.connection;

import java.util.NoSuchElementException;
import okhttp3.internal.Util;
import okhttp3.HttpUrl;
import java.net.SocketAddress;
import java.io.IOException;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import okhttp3.Route;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import okhttp3.Address;

public final class RouteSelector
{
    private final Address address;
    private List<InetSocketAddress> inetSocketAddresses;
    private InetSocketAddress lastInetSocketAddress;
    private Proxy lastProxy;
    private int nextInetSocketAddressIndex;
    private int nextProxyIndex;
    private final List<Route> postponedRoutes;
    private List<Proxy> proxies;
    private final RouteDatabase routeDatabase;
    
    public RouteSelector(final Address address, final RouteDatabase routeDatabase) {
        this.proxies = Collections.emptyList();
        this.inetSocketAddresses = Collections.emptyList();
        this.postponedRoutes = new ArrayList<Route>();
        this.address = address;
        this.routeDatabase = routeDatabase;
        this.resetNextProxy(address.url(), address.proxy());
    }
    
    static String getHostString(final InetSocketAddress inetSocketAddress) {
        final InetAddress address = inetSocketAddress.getAddress();
        if (address != null) {
            return address.getHostAddress();
        }
        return inetSocketAddress.getHostName();
    }
    
    private boolean hasNextInetSocketAddress() {
        return this.nextInetSocketAddressIndex < this.inetSocketAddresses.size();
    }
    
    private boolean hasNextPostponed() {
        boolean b = false;
        if (!this.postponedRoutes.isEmpty()) {
            b = true;
        }
        return b;
    }
    
    private boolean hasNextProxy() {
        return this.nextProxyIndex < this.proxies.size();
    }
    
    private InetSocketAddress nextInetSocketAddress() throws IOException {
        if (this.hasNextInetSocketAddress()) {
            return this.inetSocketAddresses.get(this.nextInetSocketAddressIndex++);
        }
        throw new SocketException("No route to " + this.address.url().host() + "; exhausted inet socket addresses: " + this.inetSocketAddresses);
    }
    
    private Route nextPostponed() {
        return this.postponedRoutes.remove(0);
    }
    
    private Proxy nextProxy() throws IOException {
        if (this.hasNextProxy()) {
            final Proxy proxy = this.proxies.get(this.nextProxyIndex++);
            this.resetNextInetSocketAddress(proxy);
            return proxy;
        }
        throw new SocketException("No route to " + this.address.url().host() + "; exhausted proxy configurations: " + this.proxies);
    }
    
    private void resetNextInetSocketAddress(final Proxy proxy) throws IOException {
        this.inetSocketAddresses = new ArrayList<InetSocketAddress>();
        String s;
        int i;
        if (proxy.type() != Proxy.Type.DIRECT && proxy.type() != Proxy.Type.SOCKS) {
            final SocketAddress address = proxy.address();
            if (!(address instanceof InetSocketAddress)) {
                throw new IllegalArgumentException("Proxy.address() is not an InetSocketAddress: " + ((InetSocketAddress)address).getClass());
            }
            final InetSocketAddress inetSocketAddress = (InetSocketAddress)address;
            s = getHostString(inetSocketAddress);
            i = inetSocketAddress.getPort();
        }
        else {
            s = this.address.url().host();
            i = this.address.url().port();
        }
        if (i >= 1 && i <= 65535) {
            if (proxy.type() != Proxy.Type.SOCKS) {
                final List<InetAddress> lookup = this.address.dns().lookup(s);
                for (int size = lookup.size(), j = 0; j < size; ++j) {
                    this.inetSocketAddresses.add(new InetSocketAddress((InetAddress)lookup.get(j), i));
                }
            }
            else {
                this.inetSocketAddresses.add(InetSocketAddress.createUnresolved(s, i));
            }
            this.nextInetSocketAddressIndex = 0;
            return;
        }
        throw new SocketException("No route to " + s + ":" + i + "; port is out of range");
    }
    
    private void resetNextProxy(final HttpUrl httpUrl, final Proxy o) {
        if (o == null) {
            final List<Proxy> select = this.address.proxySelector().select(httpUrl.uri());
            List<Proxy> proxies;
            if (select != null && !select.isEmpty()) {
                proxies = Util.immutableList(select);
            }
            else {
                proxies = Util.immutableList(Proxy.NO_PROXY);
            }
            this.proxies = proxies;
        }
        else {
            this.proxies = Collections.singletonList(o);
        }
        this.nextProxyIndex = 0;
    }
    
    public void connectFailed(final Route route, final IOException ex) {
        if (route.proxy().type() != Proxy.Type.DIRECT && this.address.proxySelector() != null) {
            this.address.proxySelector().connectFailed(this.address.url().uri(), route.proxy().address(), ex);
        }
        this.routeDatabase.failed(route);
    }
    
    public boolean hasNext() {
        boolean b = false;
        if (this.hasNextInetSocketAddress() || this.hasNextProxy() || this.hasNextPostponed()) {
            b = true;
        }
        return b;
    }
    
    public Route next() throws IOException {
        if (!this.hasNextInetSocketAddress()) {
            if (this.hasNextProxy()) {
                this.lastProxy = this.nextProxy();
            }
            else {
                if (this.hasNextPostponed()) {
                    return this.nextPostponed();
                }
                throw new NoSuchElementException();
            }
        }
        this.lastInetSocketAddress = this.nextInetSocketAddress();
        final Route route = new Route(this.address, this.lastProxy, this.lastInetSocketAddress);
        if (!this.routeDatabase.shouldPostpone(route)) {
            return route;
        }
        this.postponedRoutes.add(route);
        return this.next();
    }
}
