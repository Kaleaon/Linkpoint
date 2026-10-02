"""
avatar_renderer.py - Avatar Renderer with Cached Asset Pipeline.

Integrates local SQLite inventory cache and asynchronous JPEG2000 texture decoder
for fast avatar rendering and attachment loading without default grey states.
"""

import logging
import threading
from typing import Dict, List, Optional, Any, Callable
from inventory_cache import InventoryCache
from texture_decoder import TextureDecoder, DecodedTexture, create_placeholder_texture

logger = logging.getLogger(__name__)


class AvatarAttachment:
    def __init__(self, attachment_point: int, item_id: str, name: str, asset_id: str):
        self.attachment_point = attachment_point
        self.item_id = item_id
        self.name = name
        self.asset_id = asset_id
        self.texture_buffer: Optional[bytes] = None
        self.texture_width: int = 16
        self.texture_height: int = 16
        self.is_loaded: bool = False
        self.is_placeholder: bool = True


class AvatarRenderer:
    def __init__(self, inventory_cache: InventoryCache, texture_decoder: TextureDecoder):
        self.inventory_cache = inventory_cache
        self.texture_decoder = texture_decoder
        self._lock = threading.Lock()
        self.avatars: Dict[str, Dict[str, Any]] = {}
        self.opengl_surfaces: Dict[str, Any] = {}

    def register_opengl_surface(self, surface_id: str, surface_handler: Callable[[str, bytes, int, int], None]):
        """Registers OpenGL surface handler callback for texture rendering."""
        with self._lock:
            self.opengl_surfaces[surface_id] = surface_handler

    def update_avatar_attachment(
        self,
        avatar_id: str,
        attachment_point: int,
        item_id: str,
        raw_texture_bytes: Optional[bytes] = None
    ) -> AvatarAttachment:
        """
        Updates avatar attachment logic using cached inventory items and async texture decoding.
        Immediately applies cached metadata and requests async texture decoding.
        """
        cached_item = self.inventory_cache.get_item(item_id)
        if cached_item:
            name = cached_item["name"]
            asset_id = cached_item["asset_id"]
        else:
            name = f"Attachment_{item_id[:8]}"
            asset_id = item_id

        attachment = AvatarAttachment(attachment_point, item_id, name, asset_id)
        attachment.texture_buffer = create_placeholder_texture(16, 16, (180, 180, 180, 255))
        attachment.is_placeholder = True

        with self._lock:
            if avatar_id not in self.avatars:
                self.avatars[avatar_id] = {
                    "attachments": {},
                    "is_rendered": False
                }
            self.avatars[avatar_id]["attachments"][attachment_point] = attachment
            self.avatars[avatar_id]["is_rendered"] = True  # Display cached visuals right away

        if raw_texture_bytes is not None:
            def _on_texture_decoded(decoded: DecodedTexture):
                self._apply_decoded_texture(avatar_id, attachment_point, decoded)

            self.texture_decoder.request_decode(
                texture_id=asset_id,
                raw_bytes=raw_texture_bytes,
                callback=_on_texture_decoded
            )

        return attachment

    def _apply_decoded_texture(self, avatar_id: str, attachment_point: int, decoded: DecodedTexture):
        with self._lock:
            avatar = self.avatars.get(avatar_id)
            if not avatar:
                return
            attachment: Optional[AvatarAttachment] = avatar["attachments"].get(attachment_point)
            if not attachment:
                return

            attachment.texture_buffer = decoded.buffer
            attachment.texture_width = decoded.width
            attachment.texture_height = decoded.height
            attachment.is_loaded = (decoded.status == "success")
            attachment.is_placeholder = (decoded.status != "success")

            surfaces = list(self.opengl_surfaces.values())

        for handler in surfaces:
            try:
                handler(attachment.asset_id, decoded.buffer, decoded.width, decoded.height)
            except Exception as e:
                logger.error(f"Error updating OpenGL surface for avatar {avatar_id}: {e}")

    def get_avatar_attachment(self, avatar_id: str, attachment_point: int) -> Optional[AvatarAttachment]:
        with self._lock:
            avatar = self.avatars.get(avatar_id)
            if avatar:
                return avatar["attachments"].get(attachment_point)
            return None

    def is_avatar_rendered(self, avatar_id: str) -> bool:
        with self._lock:
            avatar = self.avatars.get(avatar_id)
            return avatar["is_rendered"] if avatar else False
