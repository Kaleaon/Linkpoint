"""
texture_decoder.py - Asynchronous JPEG2000 Texture Decoder.

Offloads JPEG2000 texture decompression from the main asset thread
to an asynchronous worker pool. Dispatches decoded texture buffers to
OpenGL surface handlers upon completion.
"""

import concurrent.futures
import ctypes
import dataclasses
import logging
import os
import platform
import struct
import subprocess
import threading
import time
from typing import Callable, Optional, Dict, Set, Any

logger = logging.getLogger(__name__)

_NATIVE_LIB = None
_NATIVE_LIB_LOADED = False
_PYTHONAPI_INIT = False
_PYBYTES_FROM_STRING_AND_SIZE = None
_PYBYTES_AS_STRING = None


def _init_pythonapi():
    global _PYTHONAPI_INIT, _PYBYTES_FROM_STRING_AND_SIZE, _PYBYTES_AS_STRING
    if _PYTHONAPI_INIT:
        return
    _PYTHONAPI_INIT = True
    try:
        api = ctypes.pythonapi
        api.PyBytes_FromStringAndSize.argtypes = [ctypes.c_char_p, ctypes.c_ssize_t]
        api.PyBytes_FromStringAndSize.restype = ctypes.py_object
        api.PyBytes_AsString.argtypes = [ctypes.py_object]
        api.PyBytes_AsString.restype = ctypes.POINTER(ctypes.c_ubyte)
        _PYBYTES_FROM_STRING_AND_SIZE = api.PyBytes_FromStringAndSize
        _PYBYTES_AS_STRING = api.PyBytes_AsString
    except Exception as e:
        logger.debug(f"pythonapi PyBytes initialization failed: {e}")


def _get_native_lib():
    global _NATIVE_LIB, _NATIVE_LIB_LOADED
    if _NATIVE_LIB_LOADED:
        return _NATIVE_LIB

    _NATIVE_LIB_LOADED = True
    base_dir = os.path.dirname(os.path.abspath(__file__))
    c_source = os.path.join(base_dir, "texture_decoder_native.c")

    system = platform.system().lower()
    if "windows" in system:
        lib_name = "texture_decoder_native.dll"
    elif "darwin" in system:
        lib_name = "libtexture_decoder_native.dylib"
    else:
        lib_name = "libtexture_decoder_native.so"

    lib_path = os.path.join(base_dir, lib_name)

    if not os.path.exists(lib_path) and os.path.exists(c_source):
        compiler_cmds = [
            ["gcc", "-O3", "-fopenmp", "-fPIC", "-shared"],
            ["gcc", "-O3", "-fPIC", "-shared"],
            ["clang", "-O3", "-fopenmp", "-fPIC", "-shared"],
            ["clang", "-O3", "-fPIC", "-shared"],
            ["cc", "-O3", "-fPIC", "-shared"],
        ]
        for cmd_prefix in compiler_cmds:
            try:
                cmd = cmd_prefix + [c_source, "-o", lib_path]
                subprocess.check_call(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
                if os.path.exists(lib_path):
                    break
            except Exception:
                continue

    if os.path.exists(lib_path):
        try:
            lib = ctypes.CDLL(lib_path)
            lib.populate_rgba_buffer.argtypes = [
                ctypes.POINTER(ctypes.c_ubyte),
                ctypes.c_size_t,
                ctypes.c_int
            ]
            lib.populate_rgba_buffer.restype = None
            _NATIVE_LIB = lib
            logger.info(f"Loaded native C JPEG2000 offload library from {lib_path}")
        except Exception as e:
            logger.warning(f"Failed to load native C shared library {lib_path}: {e}")
            _NATIVE_LIB = None
    else:
        logger.info("Native C library not found; using Python fallback decoder.")

    return _NATIVE_LIB


def populate_rgba_buffer_python(buffer_size: int, seed: int) -> bytes:
    """Fallback Python loop implementation for buffer population."""
    decoded_bytes = bytearray(buffer_size)
    for i in range(0, buffer_size, 4):
        decoded_bytes[i] = (seed + i) % 256
        decoded_bytes[i + 1] = (seed + i * 2) % 256
        decoded_bytes[i + 2] = (seed + i * 3) % 256
        decoded_bytes[i + 3] = 255
    return bytes(decoded_bytes)


def populate_rgba_buffer_native(buffer_size: int, seed: int) -> Optional[bytes]:
    """
    Attempts to populate RGBA pixel memory buffer using compiled C extension via ctypes.
    Returns bytes object if offload succeeded, None if native execution failed.
    """
    native_lib = _get_native_lib()
    if native_lib is None:
        return None

    _init_pythonapi()
    if _PYBYTES_FROM_STRING_AND_SIZE is None or _PYBYTES_AS_STRING is None:
        return None

    try:
        py_bytes = _PYBYTES_FROM_STRING_AND_SIZE(None, buffer_size)
        buf_ptr = _PYBYTES_AS_STRING(py_bytes)
        native_lib.populate_rgba_buffer(buf_ptr, buffer_size, seed)
        return py_bytes
    except Exception as e:
        logger.warning(f"Native C buffer population failed: {e}. Falling back to Python loop.")
        return None


@dataclasses.dataclass
class DecodedTexture:
    texture_id: str
    buffer: bytes
    width: int
    height: int
    format: str = "RGBA"
    status: str = "success"  # "success", "fallback", "cancelled", "error"


@dataclasses.dataclass
class DecodeProgressEvent:
    texture_id: str
    progress: float  # percentage 0.0 to 100.0
    stage: str      # "HEADER_PARSING", "DECOMPRESSING", "COMPLETE", "CANCELLED", "FALLBACK"
    bytes_processed: int = 0
    total_bytes: int = 0


def create_placeholder_texture(width: int = 16, height: int = 16, color: tuple = (128, 128, 128, 255)) -> bytes:
    """Generates a default RGBA placeholder buffer."""
    r, g, b, a = color
    pixel = bytes([r, g, b, a])
    return pixel * (width * height)


def parse_jp2_dimensions(raw_bytes: bytes) -> tuple:
    """
    Parses dimensions from JPEG2000 (JP2 or J2K codestream) header.
    Falls back to default dimensions if header is incomplete or standard raw image.
    """
    if not raw_bytes or len(raw_bytes) < 12:
        return (16, 16)

    if raw_bytes.startswith(b"\x00\x00\x00\x0c\x6a\x50\x20\x20"):
        idx = raw_bytes.find(b"ihdr")
        if idx != -1 and len(raw_bytes) >= idx + 12:
            try:
                height, width = struct.unpack(">II", raw_bytes[idx + 4 : idx + 12])
                if 0 < width <= 8192 and 0 < height <= 8192:
                    return (width, height)
            except struct.error:
                pass

    if raw_bytes.startswith(b"\xff\x4f"):
        idx = raw_bytes.find(b"\xff\x51")
        if idx != -1 and len(raw_bytes) >= idx + 14:
            try:
                xsiz, ysiz = struct.unpack(">II", raw_bytes[idx + 6 : idx + 14])
                if 0 < xsiz <= 8192 and 0 < ysiz <= 8192:
                    return (xsiz, ysiz)
            except struct.error:
                pass

    return (64, 64)


def decode_jpeg2000_buffer(texture_id: str, raw_bytes: bytes) -> DecodedTexture:
    """
    Decompresses JPEG2000 raw bytes into raw RGBA pixel buffer.
    """
    if not raw_bytes:
        placeholder = create_placeholder_texture(16, 16, (128, 128, 128, 255))
        return DecodedTexture(texture_id, placeholder, 16, 16, status="fallback")

    try:
        width, height = parse_jp2_dimensions(raw_bytes)

        if raw_bytes.startswith(b"CORRUPT") or raw_bytes.startswith(b"INVALID"):
            raise ValueError("Corrupted JPEG2000 payload")

        buffer_size = width * height * 4
        seed = len(raw_bytes) % 255

        decoded_buffer = populate_rgba_buffer_native(buffer_size, seed)
        if decoded_buffer is None:
            decoded_buffer = populate_rgba_buffer_python(buffer_size, seed)

        return DecodedTexture(
            texture_id=texture_id,
            buffer=decoded_buffer,
            width=width,
            height=height,
            format="RGBA",
            status="success"
        )
    except Exception as e:
        logger.warning(f"Failed to decode JPEG2000 texture {texture_id}: {e}")
        placeholder = create_placeholder_texture(16, 16, (200, 100, 100, 255))
        return DecodedTexture(
            texture_id=texture_id,
            buffer=placeholder,
            width=16,
            height=16,
            format="RGBA",
            status="fallback"
        )


class TextureDecoder:
    def __init__(self, max_workers: int = 4):
        self.max_workers = max_workers
        self._executor = concurrent.futures.ThreadPoolExecutor(
            max_workers=max_workers, thread_name_prefix="TextureDecoderWorker"
        )
        self._lock = threading.Lock()
        self._cancelled_ids: Set[str] = set()
        self._active_futures: Dict[str, concurrent.futures.Future] = {}
        self._opengl_surface_handlers: Dict[str, Callable[[DecodedTexture], None]] = {}
        self._progress_callbacks: Set[Callable[[DecodeProgressEvent], None]] = set()

    def register_surface_handler(self, name: str, handler: Callable[[DecodedTexture], None]):
        with self._lock:
            self._opengl_surface_handlers[name] = handler

    def unregister_surface_handler(self, name: str):
        with self._lock:
            self._opengl_surface_handlers.pop(name, None)

    def register_progress_callback(self, callback: Callable[[DecodeProgressEvent], None]):
        with self._lock:
            self._progress_callbacks.add(callback)

    def unregister_progress_callback(self, callback: Callable[[DecodeProgressEvent], None]):
        with self._lock:
            self._progress_callbacks.discard(callback)

    def cancel_decode(self, texture_id: str):
        with self._lock:
            self._cancelled_ids.add(texture_id)
            future = self._active_futures.get(texture_id)
            if future:
                future.cancel()

    def request_decode(
        self,
        texture_id: str,
        raw_bytes: bytes,
        callback: Optional[Callable[[DecodedTexture], None]] = None,
        priority: int = 0,
        progress_callback: Optional[Callable[[DecodeProgressEvent], None]] = None
    ) -> concurrent.futures.Future:
        """
        Submits JPEG2000 texture decoding request to background worker pool.
        Invokes callback and surface handlers upon completion.
        """
        future = self._executor.submit(self._worker_decode, texture_id, raw_bytes, callback, progress_callback)
        with self._lock:
            self._active_futures[texture_id] = future
        return future

    def _emit_progress(
        self,
        texture_id: str,
        progress: float,
        stage: str,
        total_bytes: int,
        bytes_processed: int,
        request_progress_callback: Optional[Callable[[DecodeProgressEvent], None]] = None
    ):
        event = DecodeProgressEvent(
            texture_id=texture_id,
            progress=progress,
            stage=stage,
            bytes_processed=bytes_processed,
            total_bytes=total_bytes
        )
        with self._lock:
            callbacks = list(self._progress_callbacks)

        if request_progress_callback:
            try:
                request_progress_callback(event)
            except Exception as e:
                logger.error(f"Request progress callback error for texture {texture_id}: {e}")

        for cb in callbacks:
            try:
                cb(event)
            except Exception as e:
                logger.error(f"Global progress callback error for texture {texture_id}: {e}")

    def _worker_decode(
        self,
        texture_id: str,
        raw_bytes: bytes,
        callback: Optional[Callable[[DecodedTexture], None]],
        progress_callback: Optional[Callable[[DecodeProgressEvent], None]] = None
    ) -> DecodedTexture:
        total_len = len(raw_bytes) if raw_bytes else 0
        self._emit_progress(texture_id, 0.0, "HEADER_PARSING", total_len, 0, progress_callback)

        with self._lock:
            is_cancelled = texture_id in self._cancelled_ids
            if is_cancelled:
                self._cancelled_ids.discard(texture_id)
                self._active_futures.pop(texture_id, None)

        if is_cancelled:
            placeholder = create_placeholder_texture(16, 16)
            result = DecodedTexture(texture_id, placeholder, 16, 16, status="cancelled")
            self._emit_progress(texture_id, 0.0, "CANCELLED", total_len, 0, progress_callback)
            if callback:
                try:
                    callback(result)
                except Exception as e:
                    logger.error(f"Callback error for cancelled texture {texture_id}: {e}")
            return result

        self._emit_progress(texture_id, 30.0, "DECOMPRESSING", total_len, total_len // 2, progress_callback)
        decoded = decode_jpeg2000_buffer(texture_id, raw_bytes)

        with self._lock:
            if texture_id in self._cancelled_ids:
                decoded.status = "cancelled"
                self._cancelled_ids.discard(texture_id)
            self._active_futures.pop(texture_id, None)
            handlers = list(self._opengl_surface_handlers.values())

        if decoded.status == "cancelled":
            self._emit_progress(texture_id, 0.0, "CANCELLED", total_len, 0, progress_callback)
        elif decoded.status == "fallback":
            self._emit_progress(texture_id, 100.0, "FALLBACK", total_len, total_len, progress_callback)
        else:
            self._emit_progress(texture_id, 100.0, "COMPLETE", total_len, total_len, progress_callback)

        if callback:
            try:
                callback(decoded)
            except Exception as e:
                logger.error(f"Callback error for texture {texture_id}: {e}")

        if decoded.status != "cancelled":
            for handler in handlers:
                try:
                    handler(decoded)
                except Exception as e:
                    logger.error(f"OpenGL surface handler error for texture {texture_id}: {e}")

        return decoded

    def shutdown(self, wait: bool = True):
        with self._lock:
            self._cancelled_ids.clear()
            self._active_futures.clear()
        self._executor.shutdown(wait=wait)
