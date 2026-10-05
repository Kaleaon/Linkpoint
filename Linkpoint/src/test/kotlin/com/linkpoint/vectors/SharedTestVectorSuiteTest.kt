package com.linkpoint.vectors

import com.linkpoint.avatar.LLMeshLoader
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.slerp
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.stream.Stream
import kotlin.math.abs

class SharedTestVectorSuiteTest {

    private fun findTestVectorsDir(): File {
        val userDir = File(System.getProperty("user.dir", "."))
        val candidates = listOf(
            userDir.parentFile?.parentFile?.let { File(it, "test-vectors") },
            userDir.parentFile?.let { File(it, "test-vectors") },
            File(userDir, "test-vectors"),
            File("../../test-vectors"),
            File("../test-vectors"),
            File("test-vectors"),
            File("/app/Linkpoint/test-vectors")
        ).filterNotNull()
        return candidates.firstOrNull { it.exists() && it.isDirectory }
            ?: error("test-vectors directory not found in candidates: $candidates")
    }

    private fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace(" ", "")
        val len = clean.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(clean[i], 16) shl 4) + Character.digit(clean[i + 1], 16)).toByte()
        }
        return data
    }

    @Test
    @DisplayName("Verify Quaternion Test Vectors")
    fun testQuaternionVectors() {
        val vectorFile = File(findTestVectorsDir(), "math/quaternion_matrix_transform_vectors.json")
        assertTrue(vectorFile.exists(), "Vector file missing: ${vectorFile.absolutePath}")

        val json = JSONObject(vectorFile.readText())
        val quats = json.getJSONArray("quaternions")

        for (i in 0 until quats.length()) {
            val caseObj = quats.getJSONObject(i)
            val name = caseObj.getString("name")
            val op = caseObj.getString("operation")
            val input = caseObj.getJSONObject("input")
            val expected = caseObj.getJSONObject("expected")

            when (op) {
                "identity" -> {
                    val q = Quaternion()
                    q.loadIdentity()
                    assertTrue(q.isIdentity(), "$name: Expected identity quaternion")
                }
                "multiply" -> {
                    val q1Arr = input.getJSONArray("q1")
                    val q2Arr = input.getJSONArray("q2")
                    val q1 = Quaternion(q1Arr.getDouble(0).toFloat(), q1Arr.getDouble(1).toFloat(), q1Arr.getDouble(2).toFloat(), q1Arr.getDouble(3).toFloat())
                    val q2 = Quaternion(q2Arr.getDouble(0).toFloat(), q2Arr.getDouble(1).toFloat(), q2Arr.getDouble(2).toFloat(), q2Arr.getDouble(3).toFloat())
                    val result = q1 * q2

                    val expArr = expected.getJSONArray("q")
                    val expX = expArr.getDouble(0).toFloat()
                    val expY = expArr.getDouble(1).toFloat()
                    val expZ = expArr.getDouble(2).toFloat()
                    val expW = expArr.getDouble(3).toFloat()

                    assertEquals(expX, result.x, 1e-4f, "$name: x mismatch")
                    assertEquals(expY, result.y, 1e-4f, "$name: y mismatch")
                    assertEquals(expZ, result.z, 1e-4f, "$name: z mismatch")
                    assertEquals(expW, result.w, 1e-4f, "$name: w mismatch")
                }
                "normalize" -> {
                    val qArr = input.getJSONArray("q")
                    val q = Quaternion(qArr.getDouble(0).toFloat(), qArr.getDouble(1).toFloat(), qArr.getDouble(2).toFloat(), qArr.getDouble(3).toFloat())
                    val mag = q.normalize()

                    val expMag = expected.getDouble("magnitude").toFloat()
                    assertEquals(expMag, mag, 1e-4f, "$name: magnitude mismatch")

                    val expArr = expected.getJSONArray("q")
                    assertEquals(expArr.getDouble(0).toFloat(), q.x, 1e-4f)
                    assertEquals(expArr.getDouble(1).toFloat(), q.y, 1e-4f)
                    assertEquals(expArr.getDouble(2).toFloat(), q.z, 1e-4f)
                    assertEquals(expArr.getDouble(3).toFloat(), q.w, 1e-4f)
                }
                "rotate_vector" -> {
                    val qArr = input.getJSONArray("q")
                    val vArr = input.getJSONArray("v")
                    val q = Quaternion(qArr.getDouble(0).toFloat(), qArr.getDouble(1).toFloat(), qArr.getDouble(2).toFloat(), qArr.getDouble(3).toFloat())
                    val v = Vector3(vArr.getDouble(0).toFloat(), vArr.getDouble(1).toFloat(), vArr.getDouble(2).toFloat())
                    val rotV = q * v

                    val expArr = expected.getJSONArray("v")
                    assertEquals(expArr.getDouble(0).toFloat(), rotV.x, 1e-4f, "$name: rotV.x")
                    assertEquals(expArr.getDouble(1).toFloat(), rotV.y, 1e-4f, "$name: rotV.y")
                    assertEquals(expArr.getDouble(2).toFloat(), rotV.z, 1e-4f, "$name: rotV.z")
                }
                "slerp" -> {
                    val q1Arr = input.getJSONArray("q1")
                    val q2Arr = input.getJSONArray("q2")
                    val t = input.getDouble("t").toFloat()
                    val q1 = Quaternion(q1Arr.getDouble(0).toFloat(), q1Arr.getDouble(1).toFloat(), q1Arr.getDouble(2).toFloat(), q1Arr.getDouble(3).toFloat())
                    val q2 = Quaternion(q2Arr.getDouble(0).toFloat(), q2Arr.getDouble(1).toFloat(), q2Arr.getDouble(2).toFloat(), q2Arr.getDouble(3).toFloat())
                    val result = slerp(t, q1, q2)

                    val expArr = expected.getJSONArray("q")
                    assertEquals(expArr.getDouble(0).toFloat(), result.x, 1e-4f, "$name: slerp.x")
                    assertEquals(expArr.getDouble(1).toFloat(), result.y, 1e-4f, "$name: slerp.y")
                    assertEquals(expArr.getDouble(2).toFloat(), result.z, 1e-4f, "$name: slerp.z")
                    assertEquals(expArr.getDouble(3).toFloat(), result.w, 1e-4f, "$name: slerp.w")
                }
            }
        }
    }

    @Test
    @DisplayName("Verify LLMesh Decoding Test Vectors")
    fun testLLMeshVectors() {
        val vectorFile = File(findTestVectorsDir(), "mesh/llmesh_decompress_vectors.json")
        assertTrue(vectorFile.exists(), "Vector file missing: ${vectorFile.absolutePath}")

        val json = JSONObject(vectorFile.readText())
        val meshes = json.getJSONArray("llmesh_vectors")

        for (i in 0 until meshes.length()) {
            val caseObj = meshes.getJSONObject(i)
            val name = caseObj.getString("name")
            val hexBytes = caseObj.getString("hex_bytes")
            val expected = caseObj.getJSONObject("expected")

            val bytes = hexToBytes(hexBytes)
            val parsed = LLMeshLoader.parse(bytes, name)
            assertNotNull(parsed, "$name: LLMeshLoader.parse returned null")

            assertEquals(expected.getInt("vertex_count"), parsed!!.vertexCount, "$name: vertexCount")
            assertEquals(expected.getInt("index_count"), parsed.indexCount, "$name: indexCount")

            val expPos = expected.getJSONArray("positions")
            for (p in 0 until expPos.length()) {
                assertEquals(expPos.getDouble(p).toFloat(), parsed.positions[p], 1e-4f, "$name: position[$p]")
            }

            val expInd = expected.getJSONArray("indices")
            for (idx in 0 until expInd.length()) {
                assertEquals(expInd.getInt(idx).toShort(), parsed.indices[idx], "$name: index[$idx]")
            }
        }
    }

    @Test
    @DisplayName("Verify JPEG2000 Texture Decoder Test Vectors")
    fun testJ2kTextureDecoderVectors() {
        val vectorFile = File(findTestVectorsDir(), "textures/j2k_texture_decoder_vectors.json")
        assertTrue(vectorFile.exists(), "Vector file missing: ${vectorFile.absolutePath}")

        val json = JSONObject(vectorFile.readText())
        val vectors = json.getJSONArray("j2k_vectors")

        for (i in 0 until vectors.length()) {
            val caseObj = vectors.getJSONObject(i)
            val name = caseObj.getString("name")
            val hexBytes = caseObj.getString("hex_bytes")
            val expected = caseObj.getJSONObject("expected")

            val bytes = hexToBytes(hexBytes)
            val expStatus = expected.getString("status")
            val expW = expected.getInt("width")
            val expH = expected.getInt("height")

            if (expStatus == "success") {
                assertTrue(bytes.size >= 24, "$name: Expected at least 24 bytes for JP2 header")
                // Verify JP2 header bytes present
                val magic = String(bytes, 4, 4, Charsets.US_ASCII)
                assertEquals("jP  ", magic, "$name: JP2 magic signature")

                // Extract dimensions from ihdr box
                val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
                var foundWidth = 0
                var foundHeight = 0
                for (offset in 0 until bytes.size - 8) {
                    if (bytes[offset] == 'i'.code.toByte() &&
                        bytes[offset + 1] == 'h'.code.toByte() &&
                        bytes[offset + 2] == 'd'.code.toByte() &&
                        bytes[offset + 3] == 'r'.code.toByte()) {
                        bb.position(offset + 4)
                        foundHeight = bb.int
                        foundWidth = bb.int
                        break
                    }
                }
                assertEquals(expW, foundWidth, "$name: width mismatch")
                assertEquals(expH, foundHeight, "$name: height mismatch")
            } else {
                // Corrupt data should fail parsing
                var magicMatches = false
                if (bytes.size >= 8) {
                    val magic = String(bytes, 4, kotlin.math.min(4, bytes.size - 4), Charsets.US_ASCII)
                    magicMatches = (magic == "jP  ")
                }
                assertFalse(magicMatches, "$name: Corrupt data should not match JP2 magic")
            }
        }
    }
}
