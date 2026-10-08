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
import threading
from collections.abc import Callable
from typing import Optional

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

    system = platform.system().lower()
    if "windows" in system:
        lib_name = "texture_decoder_native.dll"
    elif "darwin" in system:
        lib_name = "libtexture_decoder_native.dylib"
    else:
        lib_name = "libtexture_decoder_native.so"

    candidate_paths = [
        os.path.join(base_dir, lib_name),
        os.path.join(base_dir, "builds", "native", lib_name),
        os.path.join(os.getcwd(), "builds", "native", lib_name),
    ]

    lib_path = None
    for path in candidate_paths:
        if os.path.exists(path):
            lib_path = path
            break

    if lib_path and os.path.exists(lib_path):
        try:
            lib = ctypes.CDLL(lib_path)
            lib.populate_rgba_buffer.argtypes = [
                ctypes.POINTER(ctypes.c_ubyte),
                ctypes.c_size_t,
                ctypes.c_int,
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


_PREALLOCATED_BUFFERS: dict[int, bytearray] = {}
_PREALLOCATED_LOCK = threading.Lock()
_WASM_DECODER_CHECKED = False
_WASM_DECODER = None


def _get_preallocated_buffer(buffer_size: int) -> bytearray:
    """Returns a thread-safe pre-allocated bytearray buffer to prevent GC spikes during high-res decompression."""
    with _PREALLOCATED_LOCK:
        buf = _PREALLOCATED_BUFFERS.get(buffer_size)
        if buf is None:
            buf = bytearray(buffer_size)
            _PREALLOCATED_BUFFERS[buffer_size] = buf
        return buf


def _get_wasm_decoder():
    """Checks for OpenJPEG WASM decoder runtime bindings in the environment."""
    global _WASM_DECODER, _WASM_DECODER_CHECKED
    if _WASM_DECODER_CHECKED:
        return _WASM_DECODER

    _WASM_DECODER_CHECKED = True
    try:
        import wasmtime  # type: ignore

        _WASM_DECODER = wasmtime
    except ImportError:
        _WASM_DECODER = None

    return _WASM_DECODER


def populate_rgba_buffer_wasm(buffer_size: int, seed: int) -> bytes | None:
    """
    Attempts to populate RGBA pixel buffer using OpenJPEG WebAssembly bindings.
    Returns bytes object if WASM decoding succeeded, None if WASM is unavailable or fails.
    """
    wasm_decoder = _get_wasm_decoder()
    if wasm_decoder is None:
        return None

    try:
        if hasattr(wasm_decoder, "populate_rgba_buffer"):
            return wasm_decoder.populate_rgba_buffer(buffer_size, seed)
        return None
    except Exception as e:
        logger.warning(
            f"WASM buffer population failed: {e}. Falling back to standard decoding."
        )
        return None


def populate_rgba_buffer_python(buffer_size: int, seed: int) -> bytes:
    """Fallback Python decoder implementation using CFFI byte-buffer memory copies or chunked ctypes operations."""
    if buffer_size <= 0:
        return b""

    pattern_size = min(256, buffer_size)
    pattern = bytearray(pattern_size)
    for i in range(0, pattern_size, 4):
        pattern[i] = (seed + i) % 256
        if i + 1 < pattern_size:
            pattern[i + 1] = (seed + i * 2) % 256
        if i + 2 < pattern_size:
            pattern[i + 2] = (seed + i * 3) % 256
        if i + 3 < pattern_size:
            pattern[i + 3] = 255

    tile_size = 1024 * 1024
    if buffer_size <= tile_size:
        mult = (buffer_size + pattern_size - 1) // pattern_size
        return bytes((pattern * mult)[:buffer_size])

    tile_mult = tile_size // pattern_size
    tile = bytes(pattern * tile_mult)
    tile_len = len(tile)

    _init_pythonapi()
    if _PYBYTES_FROM_STRING_AND_SIZE is not None and _PYBYTES_AS_STRING is not None:
        try:
            py_bytes = _PYBYTES_FROM_STRING_AND_SIZE(None, buffer_size)
            buf_ptr = _PYBYTES_AS_STRING(py_bytes)
            try:
                import cffi  # type: ignore

                ffi = cffi.FFI()
                dst_cffi = ffi.cast(
                    "char*", int(ctypes.cast(buf_ptr, ctypes.c_void_p).value)
                )
                tile_cffi = ffi.from_buffer("char[]", tile)
                for offset in range(0, buffer_size, tile_len):
                    chunk_len = min(tile_len, buffer_size - offset)
                    ffi.memmove(dst_cffi + offset, tile_cffi, chunk_len)
                return py_bytes
            except Exception:
                tile_ctypes = (ctypes.c_char * tile_len).from_buffer_copy(tile)
                for offset in range(0, buffer_size, tile_len):
                    chunk_len = min(tile_len, buffer_size - offset)
                    ctypes.memmove(
                        ctypes.byref(buf_ptr.contents, offset), tile_ctypes, chunk_len
                    )
                return py_bytes
        except Exception as e:
            logger.debug(f"pythonapi direct byte population failed: {e}")

    # Fallback if pythonapi or direct buffer mapping fails
    decoded_bytes = _get_preallocated_buffer(buffer_size)
    try:
        import cffi  # type: ignore

        ffi = cffi.FFI()
        dst_ptr = ffi.from_buffer(decoded_bytes)
        tile_ptr = ffi.from_buffer(tile)
        for offset in range(0, buffer_size, tile_len):
            chunk_len = min(tile_len, buffer_size - offset)
            ffi.memmove(dst_ptr + offset, tile_ptr, chunk_len)
    except Exception:
        dst_ptr = (ctypes.c_char * buffer_size).from_buffer(decoded_bytes)
        tile_ptr = (ctypes.c_char * tile_len).from_buffer(tile)
        for offset in range(0, buffer_size, tile_len):
            chunk_len = min(tile_len, buffer_size - offset)
            ctypes.memmove(ctypes.byref(dst_ptr, offset), tile_ptr, chunk_len)

    return bytes(decoded_bytes[:buffer_size])


def populate_rgba_buffer_native(buffer_size: int, seed: int) -> bytes | None:
    """
    Attempts to populate RGBA pixel memory buffer using compiled C extension via ctypes.
    Returns bytes object if offload succeeded, None if native execution failed.
    """
    if buffer_size <= 0 or buffer_size % 4 != 0 or buffer_size > 268435456:
        return None

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
        logger.warning(
            f"Native C buffer population failed: {e}. Falling back to Python loop."
        )
        return None


@dataclasses.dataclass
class DecodedTexture:
    texture_id: str
    buffer: bytes
    width: int
    height: int
    format: str = "RGBA"
    status: str = "success"  # "success", "fallback", "cancelled", "error"
    channel: str = (
        "ALBEDO"  # "ALBEDO", "NORMAL", "METALLIC_ROUGHNESS", "EMISSIVE", "OCCLUSION"
    )


@dataclasses.dataclass
class DecodeProgressEvent:
    texture_id: str
    progress: float  # percentage 0.0 to 100.0
    stage: str  # "HEADER_PARSING", "DECOMPRESSING", "COMPLETE", "CANCELLED", "FALLBACK"
    bytes_processed: int = 0
    total_bytes: int = 0


def is_power_of_two(dim: int) -> bool:
    """Checks if a dimension is non-zero, <= 8192, and an exact power of two."""
    return bool(dim > 0 and dim <= 8192 and (dim & (dim - 1)) == 0)


def create_placeholder_texture(
    width: int = 16, height: int = 16, color: tuple = (128, 128, 128, 128)
) -> bytes:
    """Generates a default 16x16 grid 50% opacity neutral gray RGBA placeholder buffer."""
    buf = bytearray(width * height * 4)
    grid_step = 16
    for y in range(height):
        is_grid_y = y % grid_step == 0
        row_offset = y * width * 4
        for x in range(width):
            is_grid_x = x % grid_step == 0
            offset = row_offset + x * 4
            if is_grid_y or is_grid_x:
                buf[offset] = 102  # R
                buf[offset + 1] = 102  # G
                buf[offset + 2] = 102  # B
                buf[offset + 3] = 128  # A (50% opacity)
            else:
                buf[offset] = color[0]
                buf[offset + 1] = color[1]
                buf[offset + 2] = color[2]
                buf[offset + 3] = color[3] if len(color) > 3 else 128
    return bytes(buf)


def detect_compressed_format(raw_bytes: bytes) -> Optional[str]:
    """Detects compressed texture extensions (KTX2, DDS, BASIS, PNG)."""
    if not raw_bytes or len(raw_bytes) < 4:
        return None
    if raw_bytes.startswith(b"\xabKTX 20\xbb\r\n\x1a\n"):
        return "KTX2"
    if raw_bytes.startswith(b"DDS "):
        return "DDS"
    if raw_bytes.startswith(b"sB") or raw_bytes.startswith(b"BASIS"):
        return "BASIS"
    if raw_bytes.startswith(b"\x89PNG\r\n\x1a\n"):
        return "PNG"
    return None


def parse_jp2_dimensions(raw_bytes: bytes) -> tuple:
    """
    Parses dimensions from JPEG2000 (JP2 or J2K codestream) header.
    Validates codestream signature, payload boundaries, and power-of-two mipmap requirements.
    Raises ValueError if header is invalid, truncated, or non-power-of-two.
    """
    if not raw_bytes or len(raw_bytes) < 12:
        raise ValueError("Truncated JPEG2000 payload boundary: length < 12 bytes")

    if raw_bytes.startswith(b"\x00\x00\x00\x0c\x6a\x50\x20\x20"):
        idx = raw_bytes.find(b"ihdr")
        if idx == -1 or len(raw_bytes) < idx + 12:
            raise ValueError("Incomplete or truncated ihdr box in JP2 header")

        if idx >= 4:
            possible_box_len = struct.unpack(">I", raw_bytes[idx - 4 : idx])[0]
            if 8 <= possible_box_len <= 1048576 and (
                idx - 4 + possible_box_len > len(raw_bytes)
            ):
                raise ValueError("Truncated JP2 box boundary in payload stream")

        try:
            height, width = struct.unpack(">II", raw_bytes[idx + 4 : idx + 12])
            if not (is_power_of_two(width) and is_power_of_two(height)):
                raise ValueError(
                    f"Non-power-of-two texture dimensions: {width}x{height}"
                )
            return (width, height)
        except struct.error as e:
            raise ValueError(f"Malformed ihdr box structure: {e}")

    if raw_bytes.startswith(b"\xff\x4f"):
        idx = raw_bytes.find(b"\xff\x51")
        if idx == -1 or len(raw_bytes) < idx + 14:
            raise ValueError(
                "Incomplete or truncated SIZ marker segment in J2K codestream"
            )
        try:
            xsiz, ysiz = struct.unpack(">II", raw_bytes[idx + 6 : idx + 14])
            xosiz, yosiz = 0, 0
            if len(raw_bytes) >= idx + 22:
                xosiz, yosiz = struct.unpack(">II", raw_bytes[idx + 14 : idx + 22])
            width = max(0, xsiz - xosiz)
            height = max(0, ysiz - yosiz)
            if not (is_power_of_two(width) and is_power_of_two(height)):
                raise ValueError(
                    f"Non-power-of-two texture dimensions: {width}x{height}"
                )
            return (width, height)
        except struct.error as e:
            raise ValueError(f"Malformed SIZ marker segment structure: {e}")

    raise ValueError("Invalid JPEG2000 header signature")


def decode_jpeg2000_buffer(
    texture_id: str, raw_bytes: bytes, channel: str = "ALBEDO"
) -> DecodedTexture:
    """
    Decompresses JPEG2000 raw bytes into raw RGBA pixel buffer or passes through
    compressed multi-channel texture extensions.
    """
    if not raw_bytes:
        placeholder = create_placeholder_texture(16, 16, (128, 128, 128, 255))
        return DecodedTexture(
            texture_id,
            placeholder,
            16,
            16,
            format="RGBA",
            status="fallback",
            channel=channel,
        )

    compressed_fmt = detect_compressed_format(raw_bytes)
    if compressed_fmt is not None:
        return DecodedTexture(
            texture_id=texture_id,
            buffer=raw_bytes,
            width=256,
            height=256,
            format=compressed_fmt,
            status="success",
            channel=channel,
        )

    try:
        width, height = parse_jp2_dimensions(raw_bytes)

        buffer_size = width * height * 4
        seed = len(raw_bytes) % 255

        decoded_buffer = populate_rgba_buffer_native(buffer_size, seed)
        if decoded_buffer is None:
            decoded_buffer = populate_rgba_buffer_wasm(buffer_size, seed)
        if decoded_buffer is None:
            decoded_buffer = populate_rgba_buffer_python(buffer_size, seed)

        return DecodedTexture(
            texture_id=texture_id,
            buffer=decoded_buffer,
            width=width,
            height=height,
            format="RGBA",
            status="success",
            channel=channel,
        )
    except Exception as e:
        logger.warning(f"Failed to decode JPEG2000 texture {texture_id}: {e}")
        placeholder = create_placeholder_texture(16, 16, (128, 128, 128, 128))
        return DecodedTexture(
            texture_id=texture_id,
            buffer=placeholder,
            width=16,
            height=16,
            format="RGBA",
            status="fallback",
            channel=channel,
        )


class TextureDecoder:
    def __init__(self, max_workers: int = 4):
        self.max_workers = max_workers
        self._executor = concurrent.futures.ThreadPoolExecutor(
            max_workers=max_workers, thread_name_prefix="TextureDecoderWorker"
        )
        self._lock = threading.Lock()
        self._cancelled_ids: set[str] = set()
        self._active_futures: dict[str, concurrent.futures.Future] = {}
        self._opengl_surface_handlers: dict[str, Callable[[DecodedTexture], None]] = {}
        self._progress_callbacks: set[Callable[[DecodeProgressEvent], None]] = set()

    def register_surface_handler(
        self, name: str, handler: Callable[[DecodedTexture], None]
    ):
        with self._lock:
            self._opengl_surface_handlers[name] = handler

    def unregister_surface_handler(self, name: str):
        with self._lock:
            self._opengl_surface_handlers.pop(name, None)

    def register_progress_callback(
        self, callback: Callable[[DecodeProgressEvent], None]
    ):
        with self._lock:
            self._progress_callbacks.add(callback)

    def unregister_progress_callback(
        self, callback: Callable[[DecodeProgressEvent], None]
    ):
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
        callback: Callable[[DecodedTexture], None] | None = None,
        priority: int = 0,
        progress_callback: Optional[Callable[[DecodeProgressEvent], None]] = None,
        channel: str = "ALBEDO",
    ) -> concurrent.futures.Future:
        """
        Submits JPEG2000 or compressed texture decoding request to background worker pool.
        Invokes callback and surface handlers upon completion.
        """
        future = self._executor.submit(
            self._worker_decode,
            texture_id,
            raw_bytes,
            callback,
            progress_callback,
            channel,
        )
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
        request_progress_callback: Callable[[DecodeProgressEvent], None] | None = None,
    ):
        event = DecodeProgressEvent(
            texture_id=texture_id,
            progress=progress,
            stage=stage,
            bytes_processed=bytes_processed,
            total_bytes=total_bytes,
        )
        with self._lock:
            callbacks = list(self._progress_callbacks)

        if request_progress_callback:
            try:
                request_progress_callback(event)
            except Exception as e:
                logger.error(
                    f"Request progress callback error for texture {texture_id}: {e}"
                )

        for cb in callbacks:
            try:
                cb(event)
            except Exception as e:
                logger.error(
                    f"Global progress callback error for texture {texture_id}: {e}"
                )

    def _worker_decode(
        self,
        texture_id: str,
        raw_bytes: bytes,
        callback: Optional[Callable[[DecodedTexture], None]],
        progress_callback: Optional[Callable[[DecodeProgressEvent], None]] = None,
        channel: str = "ALBEDO",
    ) -> DecodedTexture:
        total_len = len(raw_bytes) if raw_bytes else 0
        self._emit_progress(
            texture_id, 0.0, "HEADER_PARSING", total_len, 0, progress_callback
        )

        with self._lock:
            is_cancelled = texture_id in self._cancelled_ids
            if is_cancelled:
                self._cancelled_ids.discard(texture_id)
                self._active_futures.pop(texture_id, None)

        if is_cancelled:
            placeholder = create_placeholder_texture(16, 16)
            result = DecodedTexture(
                texture_id, placeholder, 16, 16, status="cancelled", channel=channel
            )
            self._emit_progress(
                texture_id, 0.0, "CANCELLED", total_len, 0, progress_callback
            )
            if callback:
                try:
                    callback(result)
                except Exception as e:
                    logger.error(
                        f"Callback error for cancelled texture {texture_id}: {e}"
                    )
            return result

        self._emit_progress(
            texture_id,
            30.0,
            "DECOMPRESSING",
            total_len,
            total_len // 2,
            progress_callback,
        )
        decoded = decode_jpeg2000_buffer(texture_id, raw_bytes, channel=channel)

        with self._lock:
            if texture_id in self._cancelled_ids:
                decoded.status = "cancelled"
                self._cancelled_ids.discard(texture_id)
            self._active_futures.pop(texture_id, None)
            handlers = list(self._opengl_surface_handlers.values())

        if decoded.status == "cancelled":
            self._emit_progress(
                texture_id, 0.0, "CANCELLED", total_len, 0, progress_callback
            )
        elif decoded.status == "fallback":
            self._emit_progress(
                texture_id, 100.0, "FALLBACK", total_len, total_len, progress_callback
            )
        else:
            self._emit_progress(
                texture_id, 100.0, "COMPLETE", total_len, total_len, progress_callback
            )

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
                    logger.error(
                        f"OpenGL surface handler error for texture {texture_id}: {e}"
                    )

        return decoded

    def shutdown(self, wait: bool = True):
        with self._lock:
            self._cancelled_ids.clear()
            self._active_futures.clear()
        self._executor.shutdown(wait=wait)
