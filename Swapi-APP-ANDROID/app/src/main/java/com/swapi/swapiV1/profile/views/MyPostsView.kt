package com.swapi.swapiV1.profile.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.swapi.swapiV1.home.model.repository.PostRepository
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.profile.viewmodel.MyPostsUiState
import com.swapi.swapiV1.profile.viewmodel.MyPostsViewModel
import com.swapi.swapiV1.profile.viewmodel.MyPostsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsView(navController: NavController) {
    val repository = remember { PostRepository() }
    val factory = MyPostsViewModelFactory(repository)
    val viewModel: MyPostsViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteSuccess by viewModel.deleteSuccess.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var postToDeleteId by remember { mutableStateOf<String?>(null) }

    // --- HELPER FUNCTION TO NOTIFY HOME ---
    // This finds the Home entry in the backstack and sets the flag
    fun notifyHomeToRefresh() {
        try {
            // IMPORTANT: Replace "home_route" with your actual Home route string
            // e.g. ScreenNavigation.Home.route or just "home"
            val homeRoute = "home"
            navController.getBackStackEntry(homeRoute)
                .savedStateHandle["refresh_home"] = true
        } catch (e: Exception) {
            // Home is not in the backstack (should not happen in normal flow)
            e.printStackTrace()
        }
    }

    // --- LOGIC: HANDLE DELETE SUCCESS ---
    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            notifyHomeToRefresh() // <--- Notify Home
            viewModel.resetDeleteState()
        }
    }

    // --- LOGIC: HANDLE RETURN FROM EDIT ---
    val currentBackStackEntry = navController.currentBackStackEntry
    val shouldRefreshFromEdit by currentBackStackEntry?.savedStateHandle
        ?.getStateFlow("refresh_home", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefreshFromEdit) {
        if (shouldRefreshFromEdit) {
            viewModel.loadMyPosts() // Refresh this list
            notifyHomeToRefresh()   // <--- PROPAGATE signal to Home
            currentBackStackEntry?.savedStateHandle?.set("refresh_home", false)
        }
    }
    // ------------------------------------

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        postToDeleteId?.let { viewModel.deletePost(it) }
                        showDeleteDialog = false
                        postToDeleteId = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Posts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is MyPostsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MyPostsUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MyPostsUiState.Success -> {
                    if (state.posts.isEmpty()) {
                        Text(
                            text = "You haven't posted anything yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.posts) { post ->
                                MyPostCard(
                                    product = post,
                                    onClick = {
                                        navController.navigate(ScreenNavigation.ProductDetail.createRoute(post.id))
                                    },
                                    onEditClick = {
                                        navController.navigate(ScreenNavigation.EditPost.createRoute(post.id))
                                    },
                                    onDeleteClick = {
                                        postToDeleteId = post.id
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}