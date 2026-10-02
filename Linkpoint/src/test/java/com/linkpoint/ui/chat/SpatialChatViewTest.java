package com.linkpoint.ui.chat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class SpatialChatViewTest {

    private SpatialChatView spatialChatView;

    @Before
    public void setUp() {
        spatialChatView = new SpatialChatView(RuntimeEnvironment.getApplication());
        spatialChatView.updateCameraPosition(0.0f, 0.0f, 0.0f);
    }

    @Test
    public void testDistanceBasedOpacityAttenuation_Under5Meters() {
        // Distance < 5.0m -> 100% opacity (1.0f)
        float opacityNear = spatialChatView.computeOpacity(2.0f, 0.0f, 0.0f);
        assertEquals(1.0f, opacityNear, 0.001f);

        float opacityAtBoundary = spatialChatView.computeOpacity(5.0f, 0.0f, 0.0f);
        assertEquals(1.0f, opacityAtBoundary, 0.001f);
    }

    @Test
    public void testDistanceBasedOpacityAttenuation_Between5And20Meters() {
        // At 12.5m (halfway between 5m and 20m), opacity should be 1.0 - 0.5 * (1.0 - 0.3) = 0.65
        float opacityMid = spatialChatView.computeOpacity(12.5f, 0.0f, 0.0f);
        assertEquals(0.65f, opacityMid, 0.001f);

        // At 20.0m boundary, opacity should be 0.3f
        float opacityFarBoundary = spatialChatView.computeOpacity(20.0f, 0.0f, 0.0f);
        assertEquals(0.3f, opacityFarBoundary, 0.001f);
    }

    @Test
    public void testDistanceBasedOpacityAttenuation_Beyond20Meters() {
        // Distance > 20.0m -> 30% opacity (0.3f)
        float opacityVeryFar = spatialChatView.computeOpacity(30.0f, 0.0f, 0.0f);
        assertEquals(0.3f, opacityVeryFar, 0.001f);
    }

    @Test
    public void testChannel0ChatCategorization() {
        // Avatar chat packet -> activeAvatarOverlayMessages
        SpatialChatView.ChatMessage avatarMsg = new SpatialChatView.ChatMessage(
                "msg1", 0, SpatialChatView.ChatSourceType.AGENT, "FriendAvatar",
                "Hello world", 2.0f, 0.0f, 0.0f, System.currentTimeMillis()
        );

        // Object script packet -> scriptDrawerMessages
        SpatialChatView.ChatMessage scriptMsg = new SpatialChatView.ChatMessage(
                "msg2", 0, SpatialChatView.ChatSourceType.OBJECT, "VendorObject",
                "Buy item for $L10", 0.0f, 0.0f, 0.0f, System.currentTimeMillis()
        );

        spatialChatView.processChatPacket(avatarMsg);
        spatialChatView.processChatPacket(scriptMsg);

        List<SpatialChatView.ChatMessage> avatarOverlay = spatialChatView.getActiveAvatarOverlayMessages();
        List<SpatialChatView.ChatMessage> scriptDrawer = spatialChatView.getScriptDrawerMessages();
        List<SpatialChatView.ChatMessage> fullHistory = spatialChatView.getChatHistory();

        assertEquals(1, avatarOverlay.size());
        assertEquals("FriendAvatar", avatarOverlay.get(0).getSourceName());
        assertEquals(1.0f, avatarOverlay.get(0).getOpacity(), 0.001f);

        assertEquals(1, scriptDrawer.size());
        assertEquals("VendorObject", scriptDrawer.get(0).getSourceName());

        // Full chat history contains both messages
        assertEquals(2, fullHistory.size());
    }

    @Test
    public void testActiveVisibleAvatarLineLimit() {
        // Max 4 visible lines in active overlay
        for (int i = 1; i <= 6; i++) {
            SpatialChatView.ChatMessage avatarMsg = new SpatialChatView.ChatMessage(
                    "msg" + i, 0, SpatialChatView.ChatSourceType.AGENT, "Avatar" + i,
                    "Message " + i, 1.0f, 0.0f, 0.0f, System.currentTimeMillis()
            );
            spatialChatView.processChatPacket(avatarMsg);
        }

        List<SpatialChatView.ChatMessage> avatarOverlay = spatialChatView.getActiveAvatarOverlayMessages();
        assertEquals(SpatialChatView.MAX_VISIBLE_AVATAR_LINES, avatarOverlay.size());
        // Should keep the 4 most recent messages (Avatar3 to Avatar6)
        assertEquals("Avatar3", avatarOverlay.get(0).getSourceName());
        assertEquals("Avatar6", avatarOverlay.get(3).getSourceName());

        // History still has all 6
        assertEquals(6, spatialChatView.getChatHistory().size());
    }

    @Test
    public void testCollapsibleScriptDrawerAndUnreadCount() {
        assertFalse(spatialChatView.isScriptDrawerExpanded());
        assertEquals(0, spatialChatView.getUnreadScriptCount());

        // Receive script messages while collapsed
        spatialChatView.processChatPacket(new SpatialChatView.ChatMessage(
                "s1", 0, SpatialChatView.ChatSourceType.OBJECT, "Script1", "Notice 1", 0, 0, 0, System.currentTimeMillis()
        ));
        spatialChatView.processChatPacket(new SpatialChatView.ChatMessage(
                "s2", 0, SpatialChatView.ChatSourceType.OBJECT, "Script2", "Notice 2", 0, 0, 0, System.currentTimeMillis()
        ));

        assertEquals(2, spatialChatView.getUnreadScriptCount());

        // Expand drawer -> unread count resets to 0
        spatialChatView.setScriptDrawerExpanded(true);
        assertTrue(spatialChatView.isScriptDrawerExpanded());
        assertEquals(0, spatialChatView.getUnreadScriptCount());

        // Receive script message while expanded -> unread count stays 0
        spatialChatView.processChatPacket(new SpatialChatView.ChatMessage(
                "s3", 0, SpatialChatView.ChatSourceType.OBJECT, "Script3", "Notice 3", 0, 0, 0, System.currentTimeMillis()
        ));
        assertEquals(0, spatialChatView.getUnreadScriptCount());
        assertEquals(3, spatialChatView.getScriptDrawerMessages().size());
    }

    @Test
    public void testOverlayHeightFootprintMax20Percent() {
        int screenHeightPx = 1920; // 5-inch typical mobile screen height
        int singleLineHeightPx = 100;

        // With 4 lines = 400px / 1920px = 0.2083... -> capped to 0.20f (20%)
        for (int i = 1; i <= 4; i++) {
            spatialChatView.processChatPacket(new SpatialChatView.ChatMessage(
                    "msg" + i, 0, SpatialChatView.ChatSourceType.AGENT, "Avatar" + i,
                    "Line " + i, 1.0f, 0.0f, 0.0f, System.currentTimeMillis()
            ));
        }

        float footprintRatio = spatialChatView.calculateOverlayHeightRatio(screenHeightPx, singleLineHeightPx);
        assertTrue(footprintRatio <= SpatialChatView.MAX_OVERLAY_HEIGHT_RATIO);
        assertEquals(SpatialChatView.MAX_OVERLAY_HEIGHT_RATIO, footprintRatio, 0.001f);
    }
}
