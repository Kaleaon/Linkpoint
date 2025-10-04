package com.lumiyaviewer.lumiya.slproto.https;

import android.util.Log;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.integration.LLSDIntegrationBridge;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Simple CAPS seed fetcher using OkHttp + LLSDIntegrationBridge.
 */
public class CapSeedFetcher {
    private static final String TAG = "CapSeedFetcher";
    private final OkHttpClient client;

    public CapSeedFetcher(OkHttpClient client) {
        this.client = client != null ? client : new OkHttpClient();
    }

    public LLSDNode fetchSeed(String seedUrl) throws IOException {
        Request request = new Request.Builder()
            .get()
            .url(seedUrl)
            .header("User-Agent", "Linkpoint/3.4.3 (Android)")
            .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("HTTP " + resp.code());
            }
            String body = resp.body() != null ? resp.body().string() : "";
            // Let the bridge auto-detect (XML/JSON/Notation)
            return LLSDIntegrationBridge.parseAuto(body);
        } catch (IOException ioe) {
            Log.e(TAG, "Seed fetch error", ioe);
            throw ioe;
        } catch (Exception e) {
            Log.e(TAG, "Seed parse error", e);
            throw new IOException("Seed parse error: " + e.getMessage(), e);
        }
    }
}

