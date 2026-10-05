package lindenlab.llsd.vectors

import lindenlab.llsd.viewer.secondlife.engine.Quaternion
import lindenlab.llsd.viewer.secondlife.engine.Vector3
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class SharedTestVectorSuiteTest {

    private fun findTestVectorsDir(): File {
        val userDir = File(System.getProperty("user.dir") ?: ".")
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

    @Test
    @DisplayName("LLSD-KOTLIN - Verify Quaternion Math Test Vectors")
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
                    val q = Quaternion.IDENTITY
                    assertTrue(q.isIdentity(), "$name: Expected identity quaternion")
                }
                "multiply" -> {
                    val q1Arr = input.getJSONArray("q1")
                    val q2Arr = input.getJSONArray("q2")
                    val q1 = Quaternion(q1Arr.getDouble(0), q1Arr.getDouble(1), q1Arr.getDouble(2), q1Arr.getDouble(3))
                    val q2 = Quaternion(q2Arr.getDouble(0), q2Arr.getDouble(1), q2Arr.getDouble(2), q2Arr.getDouble(3))
                    val result = q1.multiply(q2)

                    val expArr = expected.getJSONArray("q")
                    assertEquals(expArr.getDouble(0), result.x, 1e-4, "$name: x mismatch")
                    assertEquals(expArr.getDouble(1), result.y, 1e-4, "$name: y mismatch")
                    assertEquals(expArr.getDouble(2), result.z, 1e-4, "$name: z mismatch")
                    assertEquals(expArr.getDouble(3), result.w, 1e-4, "$name: w mismatch")
                }
                "normalize" -> {
                    val qArr = input.getJSONArray("q")
                    val q = Quaternion(qArr.getDouble(0), qArr.getDouble(1), qArr.getDouble(2), qArr.getDouble(3))
                    val mag = q.norm()
                    val normQ = q.normalize()

                    val expMag = expected.getDouble("magnitude")
                    assertEquals(expMag, mag, 1e-4, "$name: magnitude mismatch")

                    val expArr = expected.getJSONArray("q")
                    assertEquals(expArr.getDouble(0), normQ.x, 1e-4)
                    assertEquals(expArr.getDouble(1), normQ.y, 1e-4)
                    assertEquals(expArr.getDouble(2), normQ.z, 1e-4)
                    assertEquals(expArr.getDouble(3), normQ.w, 1e-4)
                }
                "rotate_vector" -> {
                    val qArr = input.getJSONArray("q")
                    val vArr = input.getJSONArray("v")
                    val q = Quaternion(qArr.getDouble(0), qArr.getDouble(1), qArr.getDouble(2), qArr.getDouble(3))
                    val v = Vector3(vArr.getDouble(0), vArr.getDouble(1), vArr.getDouble(2))
                    val rotV = q.rotate(v)

                    val expArr = expected.getJSONArray("v")
                    assertEquals(expArr.getDouble(0), rotV.x, 1e-4, "$name: rotV.x")
                    assertEquals(expArr.getDouble(1), rotV.y, 1e-4, "$name: rotV.y")
                    assertEquals(expArr.getDouble(2), rotV.z, 1e-4, "$name: rotV.z")
                }
                "slerp" -> {
                    val q1Arr = input.getJSONArray("q1")
                    val q2Arr = input.getJSONArray("q2")
                    val t = input.getDouble("t")
                    val q1 = Quaternion(q1Arr.getDouble(0), q1Arr.getDouble(1), q1Arr.getDouble(2), q1Arr.getDouble(3))
                    val q2 = Quaternion(q2Arr.getDouble(0), q2Arr.getDouble(1), q2Arr.getDouble(2), q2Arr.getDouble(3))
                    val result = q1.slerp(q2, t)

                    val expArr = expected.getJSONArray("q")
                    assertEquals(expArr.getDouble(0), result.x, 1e-4, "$name: slerp.x")
                    assertEquals(expArr.getDouble(1), result.y, 1e-4, "$name: slerp.y")
                    assertEquals(expArr.getDouble(2), result.z, 1e-4, "$name: slerp.z")
                    assertEquals(expArr.getDouble(3), result.w, 1e-4, "$name: slerp.w")
                }
            }
        }
    }
}
