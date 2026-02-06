package com.linkpoint.ui.tos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Compose version of TosActivity.
 * 
 * Features:
 * - Terms of Service text display
 * - Accept/Decline buttons
 * - Scrollable content
 */
@Composable
fun TosScreen(
    requireAcceptance: Boolean = true,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrollable ToS content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = """
                        LINKPOINT TERMS OF SERVICE
                        
                        Last Updated: January 2024
                        
                        1. ACCEPTANCE OF TERMS
                        
                        By accessing or using the Linkpoint application ("App"), you agree to be bound by these Terms of Service ("Terms"). If you do not agree to these Terms, please do not use the App.
                        
                        2. DESCRIPTION OF SERVICE
                        
                        Linkpoint is a third-party viewer application that allows you to connect to Second Life® and compatible virtual world platforms. Linkpoint is not affiliated with, endorsed by, or sponsored by Linden Research, Inc.
                        
                        3. USER RESPONSIBILITIES
                        
                        You are responsible for:
                        - Maintaining the confidentiality of your account credentials
                        - All activities that occur under your account
                        - Complying with the Terms of Service of any virtual world you connect to
                        - Ensuring your use of the App complies with all applicable laws
                        
                        4. PRIVACY
                        
                        Your privacy is important to us. Your login credentials are encrypted locally on your device. We do not collect or store your password on our servers. The App may transmit diagnostic data to help improve the service.
                        
                        5. INTELLECTUAL PROPERTY
                        
                        Linkpoint and its original content, features, and functionality are owned by the Linkpoint development team and are protected by international copyright, trademark, patent, trade secret, and other intellectual property laws.
                        
                        6. THIRD-PARTY SERVICES
                        
                        The App connects to third-party services including but not limited to:
                        - Second Life grid servers
                        - OpenSimulator grids
                        - Voice communication services
                        
                        Your use of these services is subject to their respective terms of service.
                        
                        7. DISCLAIMER OF WARRANTIES
                        
                        THE APP IS PROVIDED "AS IS" AND "AS AVAILABLE" WITHOUT WARRANTIES OF ANY KIND. WE DO NOT WARRANT THAT THE APP WILL BE UNINTERRUPTED, ERROR-FREE, OR SECURE.
                        
                        8. LIMITATION OF LIABILITY
                        
                        IN NO EVENT SHALL LINKPOINT BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES ARISING OUT OF YOUR USE OF THE APP.
                        
                        9. CHANGES TO TERMS
                        
                        We reserve the right to modify these Terms at any time. Continued use of the App after changes constitutes acceptance of the new Terms.
                        
                        10. CONTACT
                        
                        For questions about these Terms, please contact us through the App's support channels.
                        
                        Second Life® is a registered trademark of Linden Research, Inc.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action buttons
            if (requireAcceptance) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Decline")
                    }
                    
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept")
                    }
                }
            } else {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}
