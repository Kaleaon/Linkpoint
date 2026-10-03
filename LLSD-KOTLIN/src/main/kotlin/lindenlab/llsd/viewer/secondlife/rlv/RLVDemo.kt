/*
 * RLV System Demo - In package demonstration
 */

package lindenlab.llsd.viewer.secondlife.rlv

/**
 * Simple demonstration of the RLV system functionality
 */
object RLVDemo {
    @JvmStatic
    fun main(args: Array<String>) {
        println("🔒 RLV (Restrained Life Viewer) System Demo")
        println("==========================================")
        
        val rlv = RLVSystem()
        val objectId = "demo-collar-12345"
        
        // Show RLV system info
        println("✅ RLV System initialized")
        println("   Version: ${RLVSystem.RLV_VERSION}")
        println("   Protocol: ${RLVSystem.RLV_PROTOCOL_VERSION}")
        println("   Enabled: ${rlv.isRLVEnabled}")
        
        // Test version command
        val versionResult = rlv.processCommand(objectId, "@version")
        println("📡 Version Command: ${versionResult.message}")
        
        // Test movement restrictions
        println("\n🚶 Testing Movement Restrictions:")
        println("   Before restrictions - Can sit: ${rlv.canSit()}, Can stand: ${rlv.canStand()}, Can teleport: ${rlv.canTeleport()}")
        
        rlv.processCommand(objectId, "@sit=n")
        rlv.processCommand(objectId, "@unsit=n")
        rlv.processCommand(objectId, "@tplm=n")
        
        println("   After restrictions  - Can sit: ${rlv.canSit()}, Can stand: ${rlv.canStand()}, Can teleport: ${rlv.canTeleport()}")
        
        // Test communication restrictions
        println("\n💬 Testing Communication Restrictions:")
        println("   Before restrictions - Can chat: ${rlv.canChat()}, Can IM: ${rlv.canIM()}")
        
        rlv.processCommand(objectId, "@sendchat=n")
        rlv.processCommand(objectId, "@sendim=n")
        
        println("   After restrictions  - Can chat: ${rlv.canChat()}, Can IM: ${rlv.canIM()}")
        
        // Test inventory restrictions
        println("\n🎒 Testing Inventory Restrictions:")
        println("   Before restrictions - Can open inventory: ${rlv.canOpenInventory()}, Can wear clothes: ${rlv.canWear("clothing")}")
        
        rlv.processCommand(objectId, "@showinv=n")
        rlv.processCommand(objectId, "@addattach:clothing=n")
        
        println("   After restrictions  - Can open inventory: ${rlv.canOpenInventory()}, Can wear clothes: ${rlv.canWear("clothing")}")
        
        // Show RLV status
        println("\n📊 RLV System Status:")
        val status = rlv.rlvStatus
        for ((key, value) in status) {
            println("   $key: $value")
        }
        
        // Test removing restrictions
        println("\n🔓 Removing Restrictions:")
        rlv.processCommand(objectId, "@sit:rem")
        rlv.processCommand(objectId, "@sendchat:rem")
        
        println("   After removal - Can sit: ${rlv.canSit()}, Can chat: ${rlv.canChat()}")
        
        println("\n✅ RLV System demonstration completed successfully!")
    }
}
