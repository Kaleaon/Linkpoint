package lindenlab.llsd.vectors

import lindenlab.llsd.Quaternion
import lindenlab.llsd.Vector3
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
            val input = caseObj.getJSONObject("input")

            if (input.has("q")) {
                val qArr = input.getJSONArray("q")
                val q = Quaternion(qArr.getDouble(0).toFloat(), qArr.getDouble(1).toFloat(), qArr.getDouble(2).toFloat(), qArr.getDouble(3).toFloat())
                assertEquals(qArr.getDouble(0).toFloat(), q.x, 1e-4f, "$name: q.x mismatch")
                assertEquals(qArr.getDouble(1).toFloat(), q.y, 1e-4f, "$name: q.y mismatch")
                assertEquals(qArr.getDouble(2).toFloat(), q.z, 1e-4f, "$name: q.z mismatch")
                assertEquals(qArr.getDouble(3).toFloat(), q.w, 1e-4f, "$name: q.w mismatch")
            }
            if (input.has("v")) {
                val vArr = input.getJSONArray("v")
                val v = Vector3(vArr.getDouble(0).toFloat(), vArr.getDouble(1).toFloat(), vArr.getDouble(2).toFloat())
                assertEquals(vArr.getDouble(0).toFloat(), v.x, 1e-4f, "$name: v.x mismatch")
                assertEquals(vArr.getDouble(1).toFloat(), v.y, 1e-4f, "$name: v.y mismatch")
                assertEquals(vArr.getDouble(2).toFloat(), v.z, 1e-4f, "$name: v.z mismatch")
            }
        }
    }
}
