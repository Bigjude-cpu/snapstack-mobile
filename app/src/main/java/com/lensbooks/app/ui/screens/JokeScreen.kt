package com.lensbooks.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lensbooks.app.data.repository.JokeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokeScreen(
    viewModel: JokeViewModel = viewModel()
) {
    val jokeState by viewModel.jokeState.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchJoke()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "😂 JOKE GENERATOR",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Get a laugh every day",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Joke Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (jokeState) {
                    is JokeViewModel.JokeState.Loading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Loading joke...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }

                    is JokeViewModel.JokeState.Success -> {
                        val joke = (jokeState as JokeViewModel.JokeState.Success).joke
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Setup
                            if (joke.setup.isNotEmpty()) {
                                Text(
                                    text = joke.setup,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp
                                )

                                Divider(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            // Punchline or Joke
                            Text(
                                text = joke.punchline.ifEmpty { joke.joke },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    is JokeViewModel.JokeState.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Text(
                                "😅 Oops! Failed to load joke",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                (jokeState as JokeViewModel.JokeState.Error).message,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like Button
            Button(
                onClick = { viewModel.toggleLike() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isLiked) Color.White else MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.White else MaterialTheme.colorScheme.error
                )
            }

            // Share Button
            Button(
                onClick = {
                    val jokeText = when (jokeState) {
                        is JokeViewModel.JokeState.Success -> {
                            val joke = (jokeState as JokeViewModel.JokeState.Success).joke
                            "${joke.setup}\n\n${joke.punchline}".ifEmpty { joke.joke }
                        }
                        else -> "Check out this joke!"
                    }
                    val sendIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(android.content.Intent.EXTRA_TEXT, "😂 $jokeText\n\nShared from Lensbooks")
                        type = "text/plain"
                    }
                    context.startActivity(
                        android.content.Intent.createChooser(sendIntent, "Share Joke")
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }

            // Refresh Button
            Button(
                onClick = { viewModel.fetchJoke() },
                enabled = jokeState !is JokeViewModel.JokeState.Loading,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Get Another Joke",
                    tint = Color.White
                )
            }
        }

        // Fun Facts Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "💡 Did You Know?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Laughing is good for your health! It reduces stress and increases endorphins. 🧠",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}