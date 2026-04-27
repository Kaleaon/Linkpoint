/*
 * Building System Demo - In package demonstration
 */

package lindenlab.llsd.viewer.secondlife.building

import lindenlab.llsd.Vector3
import lindenlab.llsd.Quaternion

/**
 * Simple demonstration of the building system functionality
 */
object BuildingDemo {
    @JvmStatic
    fun main(args: Array<String>) {
        println("🔨 Building System Demo")
        println("======================")
        
        val builder = BuildSystem()
        
        // Show building system info
        println("✅ Building System initialized")
        println("   Building enabled: ${builder.isBuildingEnabled}")
        println("   Grid snap enabled: ${builder.isGridSnapEnabled}")
        println("   Grid size: ${builder.gridSize}m")
        println("   Current mode: ${builder.currentMode}")
        
        // Create various primitives
        println("\n📦 Creating Primitives:")
        
        val position1 = Vector3(10.0f, 20.0f, 5.0f)
        val scale = Vector3(2.0f, 2.0f, 2.0f)
        val rotation = Quaternion(0.0f, 0.0f, 0.0f, 1.0f)
        
        val box = builder.createPrimitive(PrimitiveType.BOX, position1, scale, rotation)
        println("   Created BOX: ${box.id} with ${box.faceCount} faces")
        
        val position2 = Vector3(15.0f, 20.0f, 5.0f)
        val cylinder = builder.createPrimitive(PrimitiveType.CYLINDER, position2, scale, rotation)
        println("   Created CYLINDER: ${cylinder.id} with ${cylinder.faceCount} faces")
        
        val position3 = Vector3(20.0f, 20.0f, 5.0f)
        val sphere = builder.createPrimitive(PrimitiveType.SPHERE, position3, scale, rotation)
        println("   Created SPHERE: ${sphere.id} with ${sphere.faceCount} faces")
        
        // Test object selection
        println("\n🎯 Testing Object Selection:")
        builder.selectObject(box)
        builder.selectObject(cylinder)
        println("   Selected objects (SINGLE mode): ${builder.selectedCount}")
        
        builder.selectionMode = BuildSystem.SelectionMode.MULTIPLE
        builder.selectObject(box)
        builder.selectObject(cylinder)
        builder.selectObject(sphere)
        println("   Selected objects (MULTIPLE mode): ${builder.selectedCount}")
        
        // Test object movement
        println("\n🚚 Testing Object Movement:")
        val originalBoxPos = box.position
        println("   Box original position: $originalBoxPos")
        
        val moveOffset = Vector3(5.0f, 0.0f, 2.0f)
        builder.moveSelection(moveOffset)
        
        val newBoxPos = box.position
        println("   Box new position: $newBoxPos")
        
        // Test texture application
        println("\n🎨 Testing Texture Application:")
        val woodTexture = "wood-texture-uuid-123"
        
        builder.applyTexture(woodTexture, -1)
        println("   Applied wood texture to all faces")
        println("   Box face 0 texture: ${box.getTexture(0)}")
        
        // Test object linking
        println("\n🔗 Testing Object Linking:")
        box.linkChild(cylinder)
        println("   Linked box and cylinder:")
        println("   - Box is linked: ${box.isLinked}")
        println("   - Cylinder is linked: ${cylinder.isLinked}")
        println("   - Total linked objects: ${box.linkedObjects.size}")
        
        println("\n✅ Building System demonstration completed successfully!")
    }
}
