package com.linkpoint.feature.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface AuthGatewayUiState {
    data object Loading : AuthGatewayUiState
    data object Error : AuthGatewayUiState
    data object EmptyGridList : AuthGatewayUiState
    data object Authenticated : AuthGatewayUiState
}

@Composable
fun AuthGatewayScreen(state: AuthGatewayUiState) {
    Text("AuthGatewayScreen stub: $state")
}
