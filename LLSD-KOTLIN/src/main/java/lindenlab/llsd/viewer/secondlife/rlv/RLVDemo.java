/*
 * RLV System Demo - In package demonstration
 */

package lindenlab.llsd.viewer.secondlife.rlv;

import java.util.Map;

/**
 * Simple demonstration of the RLV system functionality
 */
public class RLVDemo {
    
    public static void main(String[] args) {
        System.out.println("🔒 RLV (Restrained Life Viewer) System Demo");
        System.out.println("==========================================");
        
        RLVSystem rlv = new RLVSystem();
        String objectId = "demo-collar-12345";
        
        // Show RLV system info
        System.out.println("✅ RLV System initialized");
        System.out.println("   Version: " + RLVSystem.RLV_VERSION);
        System.out.println("   Protocol: " + RLVSystem.RLV_PROTOCOL_VERSION);
        System.out.println("   Enabled: " + rlv.isRLVEnabled());
        
        // Test version command
        var versionResult = rlv.processCommand(objectId, "@version");
        System.out.println("📡 Version Command: " + versionResult.getMessage());
        
        // Test movement restrictions
        System.out.println("\n🚶 Testing Movement Restrictions:");
        System.out.println("   Before restrictions - Can sit: " + rlv.canSit() + ", Can stand: " + rlv.canStand() + ", Can teleport: " + rlv.canTeleport());
        
        rlv.processCommand(objectId, "@sit=n");
        rlv.processCommand(objectId, "@unsit=n");
        rlv.processCommand(objectId, "@tplm=n");
        
        System.out.println("   After restrictions  - Can sit: " + rlv.canSit() + ", Can stand: " + rlv.canStand() + ", Can teleport: " + rlv.canTeleport());
        
        // Test communication restrictions
        System.out.println("\n💬 Testing Communication Restrictions:");
        System.out.println("   Before restrictions - Can chat: " + rlv.canChat() + ", Can IM: " + rlv.canIM());
        
        rlv.processCommand(objectId, "@sendchat=n");
        rlv.processCommand(objectId, "@sendim=n");
        
        System.out.println("   After restrictions  - Can chat: " + rlv.canChat() + ", Can IM: " + rlv.canIM());
        
        // Test inventory restrictions
        System.out.println("\n🎒 Testing Inventory Restrictions:");
        System.out.println("   Before restrictions - Can open inventory: " + rlv.canOpenInventory() + ", Can wear clothes: " + rlv.canWear("clothing"));
        
        rlv.processCommand(objectId, "@showinv=n");
        rlv.processCommand(objectId, "@addattach:clothing=n");
        
        System.out.println("   After restrictions  - Can open inventory: " + rlv.canOpenInventory() + ", Can wear clothes: " + rlv.canWear("clothing"));
        
        // Show RLV status
        System.out.println("\n📊 RLV System Status:");
        Map<String, Object> status = rlv.getRLVStatus();
        for (Map.Entry<String, Object> entry : status.entrySet()) {
            System.out.println("   " + entry.getKey() + ": " + entry.getValue());
        }
        
        // Test removing restrictions
        System.out.println("\n🔓 Removing Restrictions:");
        rlv.processCommand(objectId, "@sit:rem");
        rlv.processCommand(objectId, "@sendchat:rem");
        
        System.out.println("   After removal - Can sit: " + rlv.canSit() + ", Can chat: " + rlv.canChat());
        
        System.out.println("\n✅ RLV System demonstration completed successfully!");
    }
}