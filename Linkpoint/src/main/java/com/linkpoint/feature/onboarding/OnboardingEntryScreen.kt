package com.linkpoint.feature.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class OnboardingEntryUiState {
    Loading,
    Error,
    Empty,
    Ready
}

@Composable
fun OnboardingEntryScreen(state: OnboardingEntryUiState) {
    Text("OnboardingEntryScreen stub: $state")
}
