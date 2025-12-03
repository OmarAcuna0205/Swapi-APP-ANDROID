package com.swapi.swapiV1.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.home.model.repository.PostRepository
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.profile.viewmodel.MyPostsUiState
import com.swapi.swapiV1.profile.viewmodel.MyPostsViewModel
import com.swapi.swapiV1.profile.viewmodel.MyPostsViewModelFactory
import com.swapi.swapiV1.utils.ErrorMessageMapper // IMPORTANTE
import com.swapi.swapiV1.utils.dismissKeyboardOnClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsView(navController: NavController) {
    val repository = remember { PostRepository() }
    val factory = MyPostsViewModelFactory(repository)
    val viewModel: MyPostsViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteSuccess by viewModel.deleteSuccess.collectAsStateWithLifecycle()

    // Obtenemos el contexto para el Mapper
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var postToDeleteId by remember { mutableStateOf<String?>(null) }

    val swapiBrandColor = Color(0xFF4A8BFF)

    fun notifyHomeToRefresh() {
        try {
            // Asegúrate de que la ruta coincida con la de tu ScreenNavigation.Home.route (usualmente "home")
            navController.getBackStackEntry(ScreenNavigation.Home.route)
                .savedStateHandle["refresh_home"] = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            notifyHomeToRefresh()
            viewModel.resetDeleteState()
        }
    }

    val currentBackStackEntry = navController.currentBackStackEntry
    val shouldRefreshFromEdit by currentBackStackEntry?.savedStateHandle
        ?.getStateFlow("refresh_home", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefreshFromEdit) {
        if (shouldRefreshFromEdit) {
            viewModel.loadMyPosts()
            notifyHomeToRefresh()
            currentBackStackEntry?.savedStateHandle?.set("refresh_home", false)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.myposts_delete_title)) },
            text = { Text(stringResource(R.string.myposts_delete_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        postToDeleteId?.let { viewModel.deletePost(it) }
                        showDeleteDialog = false
                        postToDeleteId = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.myposts_delete_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.common_cancelar)) }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    )
                )
            )
            .dismissKeyboardOnClick()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                MyPostsTopBar(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onBack = { navController.popBackStack() },
                    brandColor = swapiBrandColor
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is MyPostsUiState.Loading -> {
                        CircularProgressIndicator(color = swapiBrandColor)
                    }
                    is MyPostsUiState.Error -> {
                        // --- CORRECCIÓN: Usamos el Mapper aquí ---
                        val errorMsg = ErrorMessageMapper.getMessage(context, state.message)
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is MyPostsUiState.Success -> {
                        val allPosts = state.posts

                        if (allPosts.isEmpty()) {
                            Text(
                                text = stringResource(R.string.myposts_empty),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            val filteredPosts = if (searchQuery.isBlank()) allPosts else {
                                allPosts.filter {
                                    it.title.contains(searchQuery, ignoreCase = true) ||
                                            it.category.contains(searchQuery, ignoreCase = true)
                                }
                            }

                            if (filteredPosts.isEmpty()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = stringResource(R.string.myposts_search_empty),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(1),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredPosts, key = { it.id }) { post ->
                                        // MyPostCard está en el mismo paquete, no requiere import
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
    }
}

@Composable
private fun MyPostsTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    brandColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .windowInsetsPadding(WindowInsets.statusBars),
        shadowElevation = 10.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_atras_cd),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = stringResource(R.string.profile_mis_publicaciones),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 20.dp),
                placeholder = {
                    Text(
                        stringResource(R.string.myposts_search_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = brandColor,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}