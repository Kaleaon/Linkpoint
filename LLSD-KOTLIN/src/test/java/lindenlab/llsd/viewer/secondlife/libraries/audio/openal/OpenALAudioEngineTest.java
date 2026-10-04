package lindenlab.llsd.viewer.secondlife.libraries.audio.openal;

import lindenlab.llsd.viewer.secondlife.engine.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OpenALAudioEngineTest {

    private OpenALAudioEngine engine;
    private UUID testSoundId;

    @BeforeEach
    public void setUp() {
        engine = new OpenALAudioEngine();
        assertTrue(engine.initialize());

        testSoundId = UUID.randomUUID();
        byte[] dummyAudioData = new byte[44100 * 2]; // 1 sec 16-bit mono
        assertNotNull(engine.loadAudioBuffer(testSoundId, dummyAudioData, OpenALAudioEngine.AudioBuffer.AudioFormat.MONO16, 44100));
    }

    @Test
    public void testDistanceCullingSkipsOpenALHandlePositioning() {
        engine.getSettings().maxAudioDistance = 50.0f;
        engine.updateListener(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, -1), new Vector3(0, 1, 0));

        // Source 1: within maxAudioDistance (10 meters away)
        OpenALAudioEngine.AudioSource nearSource = engine.playSound3D(testSoundId, new Vector3(10, 0, 0), 1.0f, 1.0f, true);
        assertNotNull(nearSource);

        // Source 2: beyond maxAudioDistance (100 meters away)
        OpenALAudioEngine.AudioSource farSource = engine.playSound3D(testSoundId, new Vector3(100, 0, 0), 1.0f, 1.0f, true);
        assertNotNull(farSource);

        // Run per-frame update cycle
        engine.update(0.016f);

        // Near source should be hardware active, not virtualized, and have position updates
        assertTrue(nearSource.isHardwareActive());
        assertFalse(nearSource.isVirtualized());
        assertEquals(1, nearSource.getPositionUpdateCount());

        // Far source should be culled/virtualized, not hardware active, and position update count remains 0 (skipped)
        assertFalse(farSource.isHardwareActive());
        assertTrue(farSource.isVirtualized());
        assertEquals(0, farSource.getPositionUpdateCount());
    }

    @Test
    public void testMaxSourcesHardwareChannelCap() {
        engine.getSettings().maxSources = 5;
        engine.getSettings().maxAudioDistance = 200.0f;
        engine.updateListener(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, -1), new Vector3(0, 1, 0));

        // Spawn 20 playing sources in audible range
        OpenALAudioEngine.AudioSource[] sources = new OpenALAudioEngine.AudioSource[20];
        for (int i = 0; i < 20; i++) {
            // Place sources at increasing distances so priority ordering is deterministic
            sources[i] = engine.playSound3D(testSoundId, new Vector3((i + 1) * 5, 0, 0), 1.0f, 1.0f, true);
            assertNotNull(sources[i]);
        }

        engine.update(0.016f);

        int activeCount = 0;
        int virtualCount = 0;
        for (OpenALAudioEngine.AudioSource source : sources) {
            if (source.isHardwareActive()) activeCount++;
            if (source.isVirtualized()) virtualCount++;
        }

        // Hardware active sources must NEVER exceed maxSources (5)
        assertEquals(5, activeCount);
        assertEquals(15, virtualCount);

        // The closest 5 sources (index 0..4) should have received hardware handles
        for (int i = 0; i < 5; i++) {
            assertTrue(sources[i].isHardwareActive());
        }
        for (int i = 5; i < 20; i++) {
            assertTrue(sources[i].isVirtualized());
        }
    }

    @Test
    public void testVirtualizedSourceResumesWhenListenerMovesInRange() {
        engine.getSettings().maxAudioDistance = 50.0f;
        // Listener initially at (0,0,0)
        engine.updateListener(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, -1), new Vector3(0, 1, 0));

        // Source at (80, 0, 0) - initially out of range
        OpenALAudioEngine.AudioSource source = engine.playSound3D(testSoundId, new Vector3(80, 0, 0), 1.0f, 1.0f, true);
        assertNotNull(source);

        engine.update(0.016f);
        assertTrue(source.isVirtualized());
        assertFalse(source.isHardwareActive());
        assertEquals(0, source.getPositionUpdateCount());

        // Listener moves to (70, 0, 0) -> distance becomes 10 meters (< 50)
        engine.updateListener(new Vector3(70, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, -1), new Vector3(0, 1, 0));
        engine.update(0.016f);

        // Playback resumes on hardware channel
        assertFalse(source.isVirtualized());
        assertTrue(source.isHardwareActive());
        assertEquals(1, source.getPositionUpdateCount());
        assertTrue(source.getCurrentGain() > 0.0f);
    }

    @Test
    public void testSourcePoolRecycling() {
        engine.getSettings().maxAudioDistance = 100.0f;
        engine.updateListener(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, -1), new Vector3(0, 1, 0));

        // Play a short non-looping sound (duration = 1.0 second)
        OpenALAudioEngine.AudioSource source = engine.playSound3D(testSoundId, new Vector3(5, 0, 0), 1.0f, 1.0f, false);
        assertNotNull(source);

        // Advance time past buffer duration (1.2 seconds)
        engine.update(1.2f);

        // Source should be finished, state STOPPED
        assertEquals(OpenALAudioEngine.AudioSource.AudioSourceState.STOPPED, source.getState());

        // Spawn a new sound -> should successfully create/reuse an audio source from pool
        OpenALAudioEngine.AudioSource newSource = engine.playSound3D(testSoundId, new Vector3(12, 0, 0), 0.8f, 1.0f, true);
        assertNotNull(newSource);
        assertEquals(OpenALAudioEngine.AudioSource.AudioSourceState.PLAYING, newSource.getState());
        assertEquals(12.0, newSource.getPosition().x);
    }

    @Test
    public void testHighPriorityDistantSourcePreemptsLowerPriorityCloserSource() {
        engine.getSettings().maxSources = 1;
        engine.getSettings().maxAudioDistance = 100.0f;
        engine.updateListener(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, -1), new Vector3(0, 1, 0));

        // Close low priority source at (5, 0, 0), priority = 0.1
        OpenALAudioEngine.AudioSource closeLow = engine.playSound3D(testSoundId, new Vector3(5, 0, 0), 1.0f, 1.0f, true, 0.1f);
        // Slightly farther high priority source at (10, 0, 0), priority = 10.0
        OpenALAudioEngine.AudioSource farHigh = engine.playSound3D(testSoundId, new Vector3(10, 0, 0), 1.0f, 1.0f, true, 10.0f);

        engine.update(0.016f);

        // With maxSources = 1, farHigh pre-empts closeLow due to much higher priority rank
        assertTrue(farHigh.isHardwareActive());
        assertFalse(closeLow.isHardwareActive());
        assertTrue(closeLow.isVirtualized());
    }
}
