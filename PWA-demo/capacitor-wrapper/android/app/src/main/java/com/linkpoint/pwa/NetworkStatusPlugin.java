package com.linkpoint.pwa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Network Status Plugin for Linkpoint PWA
 * Provides real-time network connectivity monitoring
 */
@CapacitorPlugin(name = "NetworkStatus")
public class NetworkStatusPlugin extends Plugin {

    private ConnectivityManager connectivityManager;
    private NetworkCallback networkCallback;
    private BroadcastReceiver networkReceiver;

    @Override
    public void load() {
        super.load();
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        startNetworkMonitoring();
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        stopNetworkMonitoring();
    }

    /**
     * Get current network status
     */
    @PluginMethod
    public void getStatus(PluginCall call) {
        JSObject status = getCurrentNetworkStatus();
        call.resolve(status);
    }

    /**
     * Start monitoring network changes
     */
    @PluginMethod
    public void startMonitoring(PluginCall call) {
        startNetworkMonitoring();
        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Stop monitoring network changes
     */
    @PluginMethod
    public void stopMonitoring(PluginCall call) {
        stopNetworkMonitoring();
        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    // Helper methods

    private void startNetworkMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use NetworkCallback for Android N+
            if (networkCallback == null) {
                networkCallback = new NetworkCallback();
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            }
        } else {
            // Use BroadcastReceiver for older versions
            if (networkReceiver == null) {
                networkReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        notifyNetworkChange();
                    }
                };
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                getContext().registerReceiver(networkReceiver, filter);
            }
        }
    }

    private void stopNetworkMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                networkCallback = null;
            }
        } else {
            if (networkReceiver != null) {
                getContext().unregisterReceiver(networkReceiver);
                networkReceiver = null;
            }
        }
    }

    private JSObject getCurrentNetworkStatus() {
        JSObject status = new JSObject();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                status.put("connected", false);
                status.put("connectionType", "none");
                return status;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                status.put("connected", false);
                status.put("connectionType", "none");
                return status;
            }

            status.put("connected", true);

            // Determine connection type
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                status.put("connectionType", "wifi");
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                status.put("connectionType", "cellular");
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                status.put("connectionType", "ethernet");
            } else {
                status.put("connectionType", "unknown");
            }

            // Network capabilities
            status.put("hasInternet", capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
            status.put("hasValidated", capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                status.put("linkDownBandwidth", capabilities.getLinkDownstreamBandwidthKbps());
                status.put("linkUpBandwidth", capabilities.getLinkUpstreamBandwidthKbps());
            }

        } else {
            // Legacy method for older Android versions
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                status.put("connected", false);
                status.put("connectionType", "none");
                return status;
            }

            status.put("connected", true);

            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                status.put("connectionType", "wifi");
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                status.put("connectionType", "cellular");
            } else if (type == ConnectivityManager.TYPE_ETHERNET) {
                status.put("connectionType", "ethernet");
            } else {
                status.put("connectionType", "unknown");
            }

            status.put("hasInternet", networkInfo.isConnected());
        }

        return status;
    }

    private void notifyNetworkChange() {
        JSObject status = getCurrentNetworkStatus();
        notifyListeners("networkStatusChange", status);
    }

    // Network Callback for Android N+
    private class NetworkCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            notifyNetworkChange();
        }

        @Override
        public void onLost(Network network) {
            notifyNetworkChange();
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            notifyNetworkChange();
        }
    }
}
