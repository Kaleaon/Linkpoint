// Quick test to verify XML-RPC format
// Run with: kotlinc -script test_xml_format.kt

fun buildLoginXml(
    firstName: String,
    lastName: String,
    password: String,
    startLocation: String
): String {
    val safeFirst = firstName
    val safeLast = lastName
    val safePass = password
    val safeStart = startLocation
    
    val macAddress = "00:11:22:33:44:55"
    val id0 = "test-id0"
    val viewerDigest = "test-digest"
    
    return buildString {
        append("<?xml version=\"1.0\"?>")
        append("<methodCall>")
        append("<methodName>login_to_simulator</methodName>")
        append("<params><param><value><struct>")
        
        // Core fields
        append("<member><name>first</name><value><string>$safeFirst</string></value></member>")
        append("<member><name>last</name><value><string>$safeLast</string></value></member>")
        append("<member><name>passwd</name><value><string>$safePass</string></value></member>")
        append("<member><name>start</name><value><string>$safeStart</string></value></member>")
        
        // Viewer info
        append("<member><name>channel</name><value><string>Lumiya</string></value></member>")
        append("<member><name>version</name><value><string>Lumiya 1.0.0</string></value></member>")
        append("<member><name>platform</name><value><string>Android</string></value></member>")
        append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
        append("<member><name>id0</name><value><string>$id0</string></value></member>")
        append("<member><name>viewer_digest</name><value><string>$viewerDigest</string></value></member>")
        
        // Agreements - NEW FORMAT
        append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
        append("<member><name>read_critical</name><value><string>true</string></value></member>")
        
        // Options (essential only)
        append("<member><name>options</name><value><array><data>")
        append("<value><string>inventory-root</string></value>")
        append("<value><string>inventory-skeleton</string></value>")
        append("<value><string>buddy-list</string></value>")
        append("<value><string>login-flags</string></value>")
        append("</data></array></value></member>")
        
        append("</struct></value></param></params>")
        append("</methodCall>")
    }
}

// Test the XML generation
val xml = buildLoginXml(
    firstName = "Test",
    lastName = "User",
    password = "\$1\$testpasswordhash",
    startLocation = "last"
)

println("Generated XML-RPC Request:")
println("=" * 80)
println(xml)
println("=" * 80)
println()

// Check for the correct format
if (xml.contains("<member><name>agree_to_tos</name><value><string>true</string></value></member>")) {
    println("✓ agree_to_tos uses correct <string>true</string> format")
} else {
    println("✗ agree_to_tos format is INCORRECT")
}

if (xml.contains("<member><name>read_critical</name><value><string>true</string></value></member>")) {
    println("✓ read_critical uses correct <string>true</string> format")
} else {
    println("✗ read_critical format is INCORRECT")
}

// Check that old format is NOT present
if (xml.contains("<boolean>1</boolean>")) {
    println("✗ WARNING: Old <boolean>1</boolean> format still present!")
} else {
    println("✓ Old <boolean>1</boolean> format not present")
}

println()
println("Key sections:")
println("-" * 80)

// Extract and show the agreements section
val agreeStart = xml.indexOf("<member><name>agree_to_tos")
val readEnd = xml.indexOf("</member>", xml.indexOf("<member><name>read_critical")) + 9
if (agreeStart > 0 && readEnd > agreeStart) {
    println(xml.substring(agreeStart, readEnd))
}
