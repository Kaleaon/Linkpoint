"""
test_texture_decoder.py - Unit and Integration Tests for Asynchronous Texture Decoder.

Verifies worker pool background decoding, OpenGL surface handler dispatch,
placeholder fallbacks on corrupt data, request cancellations, thread safety,
and avatar renderer pipeline integration.
"""

import threading
import time
import unittest
from texture_decoder import TextureDecoder, DecodedTexture, create_placeholder_texture, parse_jp2_dimensions
from inventory_cache import InventoryCache
from avatar_renderer import AvatarRenderer


class TestTextureDecoder(unittest.TestCase):
    def setUp(self):
        self.decoder = TextureDecoder(max_workers=4)

    def tearDown(self):
        self.decoder.shutdown(wait=True)

    def test_async_jpeg2000_decoding(self):
        # Simulated valid JP2 payload
        jp2_header = b"\x00\x00\x00\x0c\x6a\x50\x20\x20\x0d\x0a\x87\x0a" + b"ihdr\x00\x00\x00\x40\x00\x00\x00\x40"
        raw_bytes = jp2_header + b"\x00" * 128

        received = []
        event = threading.Event()

        def on_decoded(decoded: DecodedTexture):
            received.append(decoded)
            event.set()

        future = self.decoder.request_decode("tex_1", raw_bytes, callback=on_decoded)
        completed = event.wait(timeout=2.0)

        self.assertTrue(completed)
        self.assertEqual(len(received), 1)
        decoded = received[0]
        self.assertEqual(decoded.texture_id, "tex_1")
        self.assertEqual(decoded.status, "success")
        self.assertEqual(decoded.width, 64)
        self.assertEqual(decoded.height, 64)
        self.assertEqual(len(decoded.buffer), 64 * 64 * 4)

    def test_opengl_surface_handler_dispatch(self):
        surface_received = []
        event = threading.Event()

        def opengl_handler(decoded: DecodedTexture):
            surface_received.append(decoded)
            event.set()

        self.decoder.register_surface_handler("main_surface", opengl_handler)

        jp2_data = b"\x00\x00\x00\x0c\x6a\x50\x20\x20" + b"ihdr\x00\x00\x00\x20\x00\x00\x00\x20"
        self.decoder.request_decode("tex_gl", jp2_data)

        completed = event.wait(timeout=2.0)
        self.assertTrue(completed)
        self.assertEqual(len(surface_received), 1)
        self.assertEqual(surface_received[0].texture_id, "tex_gl")

    def test_corrupted_data_placeholder_fallback(self):
        corrupt_bytes = b"CORRUPT_JPEG2000_DATA_STREAM"

        event = threading.Event()
        results = []

        def callback(decoded: DecodedTexture):
            results.append(decoded)
            event.set()

        self.decoder.request_decode("tex_corrupt", corrupt_bytes, callback=callback)
        completed = event.wait(timeout=2.0)

        self.assertTrue(completed)
        decoded = results[0]
        self.assertEqual(decoded.status, "fallback")
        self.assertGreater(len(decoded.buffer), 0)  # Contains placeholder texture

    def test_cancellation(self):
        jp2_data = b"\x00\x00\x00\x0c\x6a\x50\x20\x20" + b"ihdr\x00\x00\x00\x20\x00\x00\x00\x20"
        self.decoder.cancel_decode("tex_cancel")

        event = threading.Event()
        results = []

        def callback(decoded: DecodedTexture):
            results.append(decoded)
            event.set()

        self.decoder.request_decode("tex_cancel", jp2_data, callback=callback)
        completed = event.wait(timeout=1.0)

        # Cancelled job should complete with cancelled status
        self.assertTrue(completed)
        self.assertEqual(results[0].status, "cancelled")

    def test_avatar_renderer_cached_pipeline_integration(self):
        cache = InventoryCache(":memory:")
        cache.reload_full_inventory(
            {
                "folders": [{"folder_id": "f_outfit", "name": "Outfit"}],
                "items": [{"item_id": "item_hair", "folder_id": "f_outfit", "name": "Long Hair", "asset_id": "asset_hair_123"}]
            },
            new_token="v1"
        )

        renderer = AvatarRenderer(cache, self.decoder)

        opengl_notifications = []
        event = threading.Event()

        def surface_gl(asset_id, buf, w, h):
            opengl_notifications.append((asset_id, w, h))
            event.set()

        renderer.register_opengl_surface("surface_1", surface_gl)

        jp2_data = b"\x00\x00\x00\x0c\x6a\x50\x20\x20" + b"ihdr\x00\x00\x00\x20\x00\x00\x00\x20"

        # Update attachment - immediately loads cached metadata
        attachment = renderer.update_avatar_attachment(
            avatar_id="avatar_1",
            attachment_point=1,
            item_id="item_hair",
            raw_texture_bytes=None
        )

        # Avatar rendered immediately using cached metadata
        self.assertTrue(renderer.is_avatar_rendered("avatar_1"))
        self.assertEqual(attachment.name, "Long Hair")
        self.assertEqual(attachment.asset_id, "asset_hair_123")
        self.assertTrue(attachment.is_placeholder)

        # Now request async decode for the attachment
        renderer.update_avatar_attachment(
            avatar_id="avatar_1",
            attachment_point=1,
            item_id="item_hair",
            raw_texture_bytes=jp2_data
        )

        # Wait for async decode to finish and update OpenGL surface
        completed = event.wait(timeout=2.0)
        self.assertTrue(completed)
        self.assertEqual(len(opengl_notifications), 1)
        self.assertEqual(opengl_notifications[0][0], "asset_hair_123")

        # Attachment texture is updated
        updated_attachment = renderer.get_avatar_attachment("avatar_1", 1)
        self.assertIsNotNone(updated_attachment)
        self.assertTrue(updated_attachment.is_loaded)
        self.assertFalse(updated_attachment.is_placeholder)


if __name__ == "__main__":
    unittest.main()
