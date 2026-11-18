package com.linkpoint.ui.objects

import com.linkpoint.objects.SceneObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class ObjectPropertiesDialogTest {

    @Test
    fun `ObjectPropertiesDialog newInstance initializes with arguments`() {
        val sceneObject = SceneObject(
            localId = 42,
            fullId = UUID.randomUUID(),
            ownerId = UUID.randomUUID(),
            name = "Test Interactive Object",
            description = "A scene object for testing properties"
        )
        val dialog = ObjectPropertiesDialog.newInstance(sceneObject)

        assertNotNull(dialog.arguments)
        val retrievedObj = dialog.arguments?.getParcelable<SceneObject>("object")
        assertNotNull(retrievedObj)
        assertEquals(sceneObject.name, retrievedObj?.name)
        assertEquals(sceneObject.fullId, retrievedObj?.fullId)
        assertEquals(sceneObject.localId, retrievedObj?.localId)
    }
}
