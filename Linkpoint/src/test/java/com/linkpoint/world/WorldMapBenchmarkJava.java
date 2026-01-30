package com.linkpoint.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Collections;

public class WorldMapBenchmarkJava {

    // Stubs
    static class StubVector3 {
        float x, y, z;
        StubVector3(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }

        float distance(StubVector3 other) {
            float dx = x - other.x;
            float dy = y - other.y;
            float dz = z - other.z;
            return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        }
    }

    static class StubAvatar {
        UUID agentId;
        StubVector3 position;
        String displayName;

        StubAvatar(UUID agentId, StubVector3 position) {
            this.agentId = agentId;
            this.position = position;
            this.displayName = "User " + agentId;
        }
    }

    static class StubNearbyUser {
        UUID agentId;
        String name;
        float distance;
        boolean isFriend;
        float[] position;

        StubNearbyUser(UUID agentId, String name, float distance, boolean isFriend, float[] position) {
            this.agentId = agentId;
            this.name = name;
            this.distance = distance;
            this.isFriend = isFriend;
            this.position = position;
        }
    }

    public static void main(String[] args) {
        int avatarCount = 100000;
        List<StubAvatar> allAvatars = new ArrayList<>(avatarCount);
        Random rand = new Random(12345);

        // Generate avatars in a 1000x1000x1000 box
        for (int i = 0; i < avatarCount; i++) {
            allAvatars.add(new StubAvatar(
                UUID.randomUUID(),
                new StubVector3(rand.nextFloat() * 1000, rand.nextFloat() * 1000, rand.nextFloat() * 1000)
            ));
        }

        StubVector3 myPosition = new StubVector3(500, 500, 500);
        float maxDistance = 100f; // 100 meters
        int maxResults = 100;

        // Warmup
        System.out.println("Warming up...");
        for (int i = 0; i < 5; i++) {
            runOld(allAvatars, myPosition, maxDistance, maxResults);
            runNew(allAvatars, myPosition, maxDistance, maxResults);
        }

        // Benchmark Old
        System.out.println("Running Old Implementation (Map -> Filter)...");
        long startOld = System.nanoTime();
        for (int i = 0; i < 20; i++) {
            runOld(allAvatars, myPosition, maxDistance, maxResults);
        }
        long endOld = System.nanoTime();
        double avgOld = (endOld - startOld) / 20.0 / 1_000_000.0;
        System.out.println("Old Avg Time: " + avgOld + " ms");

        // Benchmark New
        System.out.println("Running New Implementation (Filter -> Map)...");
        long startNew = System.nanoTime();
        for (int i = 0; i < 20; i++) {
            runNew(allAvatars, myPosition, maxDistance, maxResults);
        }
        long endNew = System.nanoTime();
        double avgNew = (endNew - startNew) / 20.0 / 1_000_000.0;
        System.out.println("New Avg Time: " + avgNew + " ms");

        System.out.println("Improvement: " + (avgOld - avgNew) + " ms (" + String.format("%.2f", (avgOld - avgNew)/avgOld * 100) + "%)");
    }

    // Simulates Kotlin: allAvatars.map { ... }.filter { ... }
    // Creates full intermediate list of NearbyUsers
    static List<StubNearbyUser> runOld(List<StubAvatar> avatars, StubVector3 myPos, float maxDist, int maxResults) {
        List<StubNearbyUser> mapped = new ArrayList<>(avatars.size());
        for (StubAvatar avatar : avatars) {
            float distance = avatar.position.distance(myPos);
            // Simulate cost of other fields
            boolean isFriend = false;
            String name = avatar.displayName;
            float[] posArray = new float[] { avatar.position.x, avatar.position.y, avatar.position.z };

            mapped.add(new StubNearbyUser(
                avatar.agentId, name, distance, isFriend, posArray
            ));
        }

        List<StubNearbyUser> filtered = new ArrayList<>();
        for (StubNearbyUser user : mapped) {
            if (user.distance <= maxDist) {
                filtered.add(user);
            }
        }

        // Sort and take
        Collections.sort(filtered, (a, b) -> Float.compare(a.distance, b.distance));
        if (filtered.size() > maxResults) {
            return filtered.subList(0, maxResults);
        }
        return filtered;
    }

    // Simulates Kotlin: allAvatars.filter { ... }.map { ... }
    // Creates intermediate list of Avatars (survivors), then maps to NearbyUsers
    static List<StubNearbyUser> runNew(List<StubAvatar> avatars, StubVector3 myPos, float maxDist, int maxResults) {
        List<StubAvatar> filteredAvatars = new ArrayList<>();
        for (StubAvatar avatar : avatars) {
            if (avatar.position.distance(myPos) <= maxDist) {
                filteredAvatars.add(avatar);
            }
        }

        List<StubNearbyUser> mapped = new ArrayList<>(filteredAvatars.size());
        for (StubAvatar avatar : filteredAvatars) {
             float distance = avatar.position.distance(myPos); // Recalculate distance
             boolean isFriend = false;
             String name = avatar.displayName;
             float[] posArray = new float[] { avatar.position.x, avatar.position.y, avatar.position.z };

             mapped.add(new StubNearbyUser(
                 avatar.agentId, name, distance, isFriend, posArray
             ));
        }

        Collections.sort(mapped, (a, b) -> Float.compare(a.distance, b.distance));
        if (mapped.size() > maxResults) {
            return mapped.subList(0, maxResults);
        }
        return mapped;
    }
}
