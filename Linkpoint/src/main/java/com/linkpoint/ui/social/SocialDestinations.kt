package com.linkpoint.ui.social

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

data class FriendsScreenState(
    val title: String = "Friends",
    val description: String = "Friends destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsDestination(
    state: FriendsScreenState = FriendsScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class GroupsScreenState(
    val title: String = "Groups",
    val description: String = "Groups destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsDestination(
    state: GroupsScreenState = GroupsScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class ProfileScreenState(
    val title: String = "Profile",
    val description: String = "Profile destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDestination(
    state: ProfileScreenState = ProfileScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class MyProfileScreenState(
    val title: String = "My Profile",
    val description: String = "My profile destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileDestination(
    state: MyProfileScreenState = MyProfileScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class NearbyPeopleScreenState(
    val title: String = "Nearby People",
    val description: String = "Nearby people destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPeopleDestination(
    state: NearbyPeopleScreenState = NearbyPeopleScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class MyAvatarScreenState(
    val title: String = "My Avatar",
    val description: String = "My avatar destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAvatarDestination(
    state: MyAvatarScreenState = MyAvatarScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DestinationScaffold(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = description)
        }
    }
}
