/*
 * Building System Demo - In package demonstration
 */

package lindenlab.llsd.viewer.secondlife.building;

/**
 * Simple demonstration of the building system functionality
 */
public class BuildingDemo {
    
    public static void main(String[] args) {
        System.out.println("🔨 Building System Demo");
        System.out.println("======================");
        
        BuildSystem builder = new BuildSystem();
        
        // Show building system info
        System.out.println("✅ Building System initialized");
        System.out.println("   Building enabled: " + builder.isBuildingEnabled());
        System.out.println("   Grid snap enabled: " + builder.isGridSnapEnabled());
        System.out.println("   Grid size: " + builder.getGridSize() + "m");
        System.out.println("   Current mode: " + builder.getCurrentMode());
        
        // Create various primitives
        System.out.println("\n📦 Creating Primitives:");
        
        Vector3 position1 = new Vector3(10.0f, 20.0f, 5.0f);
        Vector3 scale = new Vector3(2.0f, 2.0f, 2.0f);
        Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        
        BuildObject box = builder.createPrimitive(PrimitiveType.BOX, position1, scale, rotation);
        System.out.println("   Created BOX: " + box.getId() + " with " + box.getFaceCount() + " faces");
        
        Vector3 position2 = new Vector3(15.0f, 20.0f, 5.0f);
        BuildObject cylinder = builder.createPrimitive(PrimitiveType.CYLINDER, position2, scale, rotation);
        System.out.println("   Created CYLINDER: " + cylinder.getId() + " with " + cylinder.getFaceCount() + " faces");
        
        Vector3 position3 = new Vector3(20.0f, 20.0f, 5.0f);
        BuildObject sphere = builder.createPrimitive(PrimitiveType.SPHERE, position3, scale, rotation);
        System.out.println("   Created SPHERE: " + sphere.getId() + " with " + sphere.getFaceCount() + " faces");
        
        // Test object selection
        System.out.println("\n🎯 Testing Object Selection:");
        builder.selectObject(box);
        builder.selectObject(cylinder);
        System.out.println("   Selected objects (SINGLE mode): " + builder.getSelectedCount());
        
        builder.setSelectionMode(BuildSystem.SelectionMode.MULTIPLE);
        builder.selectObject(box);
        builder.selectObject(cylinder);
        builder.selectObject(sphere);
        System.out.println("   Selected objects (MULTIPLE mode): " + builder.getSelectedCount());
        
        // Test object movement
        System.out.println("\n🚚 Testing Object Movement:");
        Vector3 originalBoxPos = box.getPosition();
        System.out.println("   Box original position: " + originalBoxPos);
        
        Vector3 moveOffset = new Vector3(5.0f, 0.0f, 2.0f);
        builder.moveSelection(moveOffset);
        
        Vector3 newBoxPos = box.getPosition();
        System.out.println("   Box new position: " + newBoxPos);
        
        // Test texture application
        System.out.println("\n🎨 Testing Texture Application:");
        String woodTexture = "wood-texture-uuid-123";
        
        builder.applyTexture(woodTexture, -1);
        System.out.println("   Applied wood texture to all faces");
        System.out.println("   Box face 0 texture: " + box.getTexture(0));
        
        // Test object linking
        System.out.println("\n🔗 Testing Object Linking:");
        box.linkChild(cylinder);
        System.out.println("   Linked box and cylinder:");
        System.out.println("   - Box is linked: " + box.isLinked());
        System.out.println("   - Cylinder is linked: " + cylinder.isLinked());
        System.out.println("   - Total linked objects: " + box.getLinkedObjects().size());
        
        System.out.println("\n✅ Building System demonstration completed successfully!");
    }
}