package com.lumiyaviewer.lumiya.test;

import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import java.util.UUID;

/**
 * Simple test class to validate LLSD types can be used
 */
public class LLSDTest {
    
    public static void testLLSDTypes() {
        // Test LLSD String
        LLSDString testString = new LLSDString("Hello LLSD");
        String value = testString.asString();
        
        // Test LLSD UUID
        UUID testUUID = UUID.randomUUID();
        LLSDUUID llsdUUID = new LLSDUUID(testUUID);
        UUID retrievedUUID = llsdUUID.asUUID();
        
        // Test LLSD Int
        LLSDInt testInt = new LLSDInt(42);
        int intValue = testInt.asInt();
    }
}