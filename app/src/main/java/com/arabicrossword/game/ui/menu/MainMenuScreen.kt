package com.arabicrossword.game.ui.menu

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.arabicrossword.game.R
import com.arabicrossword.game.data.model.DifficultyLevel

@Composable
fun MainMenuScreen(
    onStartGame: (DifficultyLevel) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                
                // App Title
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "ذكاء اصطناعي • تصميم أنيق • تحديث مستمر",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Difficulty Selection
                Text(
                    text = stringResource(R.string.difficulty_selection),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DifficultyButton(
                        difficulty = DifficultyLevel.EASY,
                        icon = Icons.Default.SentimentSatisfied,
                        description = "مثالي للمبتدئين • 8 كلمات",
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = { onStartGame(DifficultyLevel.EASY) }
                    )
                    
                    DifficultyButton(
                        difficulty = DifficultyLevel.MEDIUM,
                        icon = Icons.Default.Psychology,
                        description = "تحدي متوسط • 12 كلمة",
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = { onStartGame(DifficultyLevel.MEDIUM) }
                    )
                    
                    DifficultyButton(
                        difficulty = DifficultyLevel.HARD,
                        icon = Icons.Default.LocalFire,
                        description = "للخبراء • 15 كلمة",
                        color = MaterialTheme.colorScheme.error,
                        onClick = { onStartGame(DifficultyLevel.HARD) }
                    )
                    
                    DifficultyButton(
                        difficulty = DifficultyLevel.EXPERT,
                        icon = Icons.Default.EmojiEvents,
                        description = "التحدي الأقصى • 18 كلمة",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onStartGame(DifficultyLevel.EXPERT) }
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Footer
                Text(
                    text = "مدعوم بالذكاء الاصطناعي لتوليد أسئلة متجددة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DifficultyButton(
    difficulty: DifficultyLevel,
    icon: ImageVector,
    description: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 2.dp else 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        onClick = {
            isPressed = true
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getDifficultyName(difficulty),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Arrow
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun getDifficultyName(difficulty: DifficultyLevel): String {
    return when (difficulty) {
        DifficultyLevel.EASY -> stringResource(R.string.easy)
        DifficultyLevel.MEDIUM -> stringResource(R.string.medium)
        DifficultyLevel.HARD -> stringResource(R.string.hard)
        DifficultyLevel.EXPERT -> stringResource(R.string.expert)
    }
}