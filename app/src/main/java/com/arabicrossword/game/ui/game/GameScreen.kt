package com.arabicrossword.game.ui.game

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arabicrossword.game.R
import com.arabicrossword.game.data.model.DifficultyLevel
import com.arabicrossword.game.ui.components.CluesPanel
import com.arabicrossword.game.ui.components.CrosswordGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    difficulty: DifficultyLevel,
    onBackPressed: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    
    LaunchedEffect(difficulty) {
        viewModel.loadGame(difficulty)
    }
    
    // Show message snackbar
    uiState.showMessage?.let { message ->
        LaunchedEffect(message) {
            viewModel.clearMessage()
        }
    }
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.puzzle?.title ?: stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    },
                    actions = {
                        gameState?.let { state ->
                            // Score display
                            Text(
                                text = "النقاط: ${state.score}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                GameBottomBar(
                    onHintClick = { viewModel.getHint() },
                    onCheckClick = { viewModel.checkWord() },
                    hintsUsed = gameState?.hintsUsed ?: 0,
                    isCompleted = gameState?.isCompleted ?: false
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingContent()
                    }
                    
                    uiState.error != null -> {
                        ErrorContent(
                            error = uiState.error,
                            onRetry = { viewModel.loadGame(difficulty) }
                        )
                    }
                    
                    uiState.puzzle != null && gameState != null -> {
                        GameContent(
                            puzzle = uiState.puzzle,
                            gameState = gameState,
                            onCellClick = viewModel::onCellClick,
                            onCellValueChange = viewModel::onCellValueChange,
                            onClueClick = viewModel::onClueClick
                        )
                    }
                }
                
                // Show completion dialog
                if (gameState?.isCompleted == true) {
                    CompletionDialog(
                        score = gameState.score,
                        hintsUsed = gameState.hintsUsed,
                        onNewGame = { viewModel.loadGame(difficulty) },
                        onBackToMenu = onBackPressed
                    )
                }
                
                // Show snackbar for messages
                uiState.showMessage?.let { message ->
                    LaunchedEffect(message) {
                        // Snackbar will be shown automatically
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "جاري تحميل الأحجية...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

@Composable
private fun GameContent(
    puzzle: com.arabicrossword.game.data.model.CrosswordPuzzle,
    gameState: com.arabicrossword.game.data.model.GameState,
    onCellClick: (Int, Int) -> Unit,
    onCellValueChange: (Int, Int, String) -> Unit,
    onClueClick: (com.arabicrossword.game.data.model.Clue) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Crossword Grid
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            CrosswordGrid(
                puzzle = puzzle,
                gameState = gameState,
                onCellClick = onCellClick,
                onCellValueChange = onCellValueChange
            )
        }
        
        // Clues Panel
        CluesPanel(
            puzzle = puzzle,
            gameState = gameState,
            onClueClick = onClueClick,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun GameBottomBar(
    onHintClick: () -> Unit,
    onCheckClick: () -> Unit,
    hintsUsed: Int,
    isCompleted: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hint button
            Button(
                onClick = onHintClick,
                enabled = !isCompleted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.hint))
            }
            
            // Hints used indicator
            Text(
                text = stringResource(R.string.hints_used, hintsUsed),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Check word button
            Button(
                onClick = onCheckClick,
                enabled = !isCompleted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.check))
            }
        }
    }
}

@Composable
private fun CompletionDialog(
    score: Int,
    hintsUsed: Int,
    onNewGame: () -> Unit,
    onBackToMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = stringResource(R.string.congratulations),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(stringResource(R.string.puzzle_completed))
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.your_score, score))
                Text(stringResource(R.string.hints_used, hintsUsed))
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGame) {
                Text(stringResource(R.string.new_game))
            }
        },
        dismissButton = {
            TextButton(onClick = onBackToMenu) {
                Text(stringResource(R.string.menu))
            }
        }
    )
}