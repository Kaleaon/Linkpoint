/*
 * Unit and Performance Tests for Spatial Grid Broadphase Algorithm
 */

package lindenlab.llsd.viewer.secondlife.libraries.physics;

import lindenlab.llsd.viewer.secondlife.engine.Quaternion;
import lindenlab.llsd.viewer.secondlife.engine.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpatialGridBroadphaseTest {

    private PhysicsEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new PhysicsEngine();
        assertTrue(engine.initialize(), "Engine should initialize successfully");
    }

    @Test
    public void testInitialization() {
        assertNotNull(engine.getBroadphase(), "Broadphase should not be null");
        assertTrue(
                engine.getBroadphase() instanceof PhysicsEngine.SpatialGridBroadphase,
                "Broadphase should be an instance of SpatialGridBroadphase");
    }

    @Test
    public void testNoCollisionsFarObjects() {
        PhysicsEngine.BoxShape shape = new PhysicsEngine.BoxShape(new Vector3(1, 1, 1));
        engine.createBody(UUID.randomUUID(), shape, new Vector3(0, 0, 0), Quaternion.IDENTITY, 1.0f);
        engine.createBody(UUID.randomUUID(), shape, new Vector3(500, 500, 500), Quaternion.IDENTITY, 1.0f);

        List<PhysicsEngine.CollisionPair> pairs = engine.getBroadphase().detectPotentialCollisions();
        assertEquals(0, pairs.size(), "Far objects should not produce potential collision pairs");
    }

    @Test
    public void testOverlappingObjectsDetected() {
        PhysicsEngine.BoxShape shape = new PhysicsEngine.BoxShape(new Vector3(1, 1, 1));
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        PhysicsEngine.PhysicsBody body1 = engine.createBody(id1, shape, new Vector3(10, 10, 10), Quaternion.IDENTITY, 1.0f);
        PhysicsEngine.PhysicsBody body2 = engine.createBody(id2, shape, new Vector3(10.5, 10.5, 10.5), Quaternion.IDENTITY, 1.0f);

        List<PhysicsEngine.CollisionPair> pairs = engine.getBroadphase().detectPotentialCollisions();
        assertEquals(1, pairs.size(), "Overlapping objects must produce exactly 1 collision pair");

        PhysicsEngine.CollisionPair pair = pairs.get(0);
        Set<UUID> pairIds = new HashSet<>(Arrays.asList(pair.getBodyA().getBodyId(), pair.getBodyB().getBodyId()));
        assertTrue(pairIds.contains(id1), "Pair should contain body1 ID");
        assertTrue(pairIds.contains(id2), "Pair should contain body2 ID");
    }

    @Test
    public void testPairUniquenessAcrossMultipleCells() {
        // Create large objects that span across multiple cell boundaries (cell size = 10)
        PhysicsEngine.BoxShape largeShape = new PhysicsEngine.BoxShape(new Vector3(8, 8, 8));
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        // Placed at boundary (10, 10, 10) so bounds span from (2,2,2) to (18,18,18) covering cells 0 and 1
        engine.createBody(id1, largeShape, new Vector3(10, 10, 10), Quaternion.IDENTITY, 1.0f);
        engine.createBody(id2, largeShape, new Vector3(12, 12, 12), Quaternion.IDENTITY, 1.0f);

        List<PhysicsEngine.CollisionPair> pairs = engine.getBroadphase().detectPotentialCollisions();
        assertEquals(1, pairs.size(), "Pair spanning multiple cells must enter the list exactly once");
    }

    @Test
    public void testSparseAndDenseClusterAccuracyAgainstBaseline() {
        PhysicsEngine.BoxShape smallShape = new PhysicsEngine.BoxShape(new Vector3(1, 1, 1));
        Random rand = new Random(42);

        // Cluster 1: Dense cluster around (0, 0, 0)
        for (int i = 0; i < 25; i++) {
            Vector3 pos = new Vector3(
                    rand.nextDouble() * 4.0 - 2.0,
                    rand.nextDouble() * 4.0 - 2.0,
                    rand.nextDouble() * 4.0 - 2.0
            );
            engine.createBody(UUID.randomUUID(), smallShape, pos, Quaternion.IDENTITY, 1.0f);
        }

        // Cluster 2: Dense cluster around (300, 300, 300)
        for (int i = 0; i < 25; i++) {
            Vector3 pos = new Vector3(
                    300.0 + rand.nextDouble() * 4.0 - 2.0,
                    300.0 + rand.nextDouble() * 4.0 - 2.0,
                    300.0 + rand.nextDouble() * 4.0 - 2.0
            );
            engine.createBody(UUID.randomUUID(), smallShape, pos, Quaternion.IDENTITY, 1.0f);
        }

        // Sparse isolated objects
        for (int i = 0; i < 20; i++) {
            Vector3 pos = new Vector3(
                    (i + 1) * 40.0,
                    (i + 1) * 40.0,
                    (i + 1) * 40.0
            );
            engine.createBody(UUID.randomUUID(), smallShape, pos, Quaternion.IDENTITY, 1.0f);
        }

        PhysicsEngine.SpatialGridBroadphase spatialBroadphase = (PhysicsEngine.SpatialGridBroadphase) engine.getBroadphase();
        PhysicsEngine.DefaultBroadphase baselineBroadphase = new PhysicsEngine.DefaultBroadphase(engine.getWorld());

        List<PhysicsEngine.CollisionPair> spatialPairs = spatialBroadphase.detectPotentialCollisions();
        List<PhysicsEngine.CollisionPair> baselinePairs = baselineBroadphase.detectPotentialCollisions();

        assertEquals(
                baselinePairs.size(), spatialPairs.size(),
                "Spatial grid pair count should match brute-force baseline pair count");

        // Verify set of pairs match
        Set<String> baselineSet = canonicalPairSet(baselinePairs);
        Set<String> spatialSet = canonicalPairSet(spatialPairs);

        assertEquals(
                baselineSet, spatialSet,
                "Spatial grid pair set should match brute-force baseline pair set exactly");
    }

    @Test
    public void testFastMovingAndLargeObjects() {
        // Large object covering grid cells [0..3]
        PhysicsEngine.BoxShape hugeShape = new PhysicsEngine.BoxShape(new Vector3(20, 20, 20));
        UUID hugeId = UUID.randomUUID();
        engine.createBody(hugeId, hugeShape, new Vector3(20, 20, 20), Quaternion.IDENTITY, 10.0f);

        // Small object positioned near edge inside large object
        PhysicsEngine.BoxShape smallShape = new PhysicsEngine.BoxShape(new Vector3(0.5, 0.5, 0.5));
        UUID insideId = UUID.randomUUID();
        engine.createBody(insideId, smallShape, new Vector3(5, 5, 5), Quaternion.IDENTITY, 1.0f);

        // Small object positioned far outside
        UUID outsideId = UUID.randomUUID();
        engine.createBody(outsideId, smallShape, new Vector3(100, 100, 100), Quaternion.IDENTITY, 1.0f);

        List<PhysicsEngine.CollisionPair> pairs = engine.getBroadphase().detectPotentialCollisions();
        assertEquals(1, pairs.size(), "Only huge object and inside object should collide");

        PhysicsEngine.CollisionPair pair = pairs.get(0);
        Set<UUID> ids = new HashSet<>(Arrays.asList(pair.getBodyA().getBodyId(), pair.getBodyB().getBodyId()));
        assertTrue(ids.contains(hugeId));
        assertTrue(ids.contains(insideId));
    }

    @Test
    public void testPerformanceBenchmark() {
        PhysicsEngine.BoxShape shape = new PhysicsEngine.BoxShape(new Vector3(1, 1, 1));
        Random rand = new Random(12345);

        // Populate 400 bodies across a 500x500x500 space
        for (int i = 0; i < 400; i++) {
            Vector3 pos = new Vector3(
                    rand.nextDouble() * 500.0 - 250.0,
                    rand.nextDouble() * 500.0 - 250.0,
                    rand.nextDouble() * 500.0 - 250.0
            );
            engine.createBody(UUID.randomUUID(), shape, pos, Quaternion.IDENTITY, 1.0f);
        }

        PhysicsEngine.SpatialGridBroadphase spatialBroadphase = (PhysicsEngine.SpatialGridBroadphase) engine.getBroadphase();
        PhysicsEngine.DefaultBroadphase baselineBroadphase = new PhysicsEngine.DefaultBroadphase(engine.getWorld());

        // Warm up
        spatialBroadphase.detectPotentialCollisions();
        baselineBroadphase.detectPotentialCollisions();

        // Measure baseline brute force O(N^2) time
        long startBaseline = System.nanoTime();
        List<PhysicsEngine.CollisionPair> baselinePairs = null;
        for (int i = 0; i < 20; i++) {
            baselinePairs = baselineBroadphase.detectPotentialCollisions();
        }
        long durationBaseline = System.nanoTime() - startBaseline;

        // Measure spatial grid time
        long startSpatial = System.nanoTime();
        List<PhysicsEngine.CollisionPair> spatialPairs = null;
        for (int i = 0; i < 20; i++) {
            spatialPairs = spatialBroadphase.detectPotentialCollisions();
        }
        long durationSpatial = System.nanoTime() - startSpatial;

        assertEquals(
                baselinePairs.size(), spatialPairs.size(),
                "Pair counts must be identical between baseline and spatial grid");

        System.out.println("Performance Benchmark over 20 runs (N=400):");
        System.out.println("  Brute-force Baseline time: " + (durationBaseline / 1_000_000.0) + " ms");
        System.out.println("  Spatial Hash Grid time:   " + (durationSpatial / 1_000_000.0) + " ms");

        assertTrue(
                durationSpatial < durationBaseline,
                "Spatial grid broadphase should be faster than brute force baseline");
    }

    private Set<String> canonicalPairSet(List<PhysicsEngine.CollisionPair> pairs) {
        Set<String> set = new HashSet<>();
        for (PhysicsEngine.CollisionPair pair : pairs) {
            String idA = pair.getBodyA().getBodyId().toString();
            String idB = pair.getBodyB().getBodyId().toString();
            if (idA.compareTo(idB) <= 0) {
                set.add(idA + ":" + idB);
            } else {
                set.add(idB + ":" + idA);
            }
        }
        return set;
    }
}

