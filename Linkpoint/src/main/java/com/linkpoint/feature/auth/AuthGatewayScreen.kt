package com.linkpoint.feature.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class AuthGatewayUiState {
    Loading,
    Error,
    Empty,
    Authenticated
}

@Composable
fun AuthGatewayScreen(state: AuthGatewayUiState) {
    Text("AuthGatewayScreen stub: $state")
}
