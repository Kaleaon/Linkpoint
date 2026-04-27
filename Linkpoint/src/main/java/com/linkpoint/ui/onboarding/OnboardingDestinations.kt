package com.linkpoint.ui.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class OnboardingEntryScreenState(
    val title: String = "Onboarding",
    val description: String = "Onboarding handoff destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingEntryDestination(
    state: OnboardingEntryScreenState = OnboardingEntryScreenState(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(state.title) })
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = state.description)
        }
    }
}

data class StartLocationScreenState(
    val title: String = "Start Location",
    val description: String = "Start location selection destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartLocationDestination(
    state: StartLocationScreenState = StartLocationScreenState(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(state.title) })
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = state.description)
        }
    }
}
