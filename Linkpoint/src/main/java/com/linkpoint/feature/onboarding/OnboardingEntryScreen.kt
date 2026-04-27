package com.linkpoint.feature.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface OnboardingEntryUiState {
    data object Loading : OnboardingEntryUiState
    data object Error : OnboardingEntryUiState
    data object EmptyStartLocations : OnboardingEntryUiState
    data object Ready : OnboardingEntryUiState
}

@Composable
fun OnboardingEntryScreen(state: OnboardingEntryUiState) {
    Text("OnboardingEntryScreen stub: $state")
}
