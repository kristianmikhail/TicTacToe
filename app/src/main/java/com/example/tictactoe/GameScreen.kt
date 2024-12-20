package com.example.tictactoe

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.asStateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, model: GameModel, gameId: String?) {
    val players by model.playerMap.asStateFlow().collectAsStateWithLifecycle()
    val games by model.gameMap.asStateFlow().collectAsStateWithLifecycle()

    var playerName = "Unknown"
    players[model.localPlayerId.value]?.let {
        playerName = it.name
    }
    if (gameId != null && games.containsKey(gameId)) {
        val game = games[gameId]!!
        Scaffold(
            // Styling of the TopAppBar
            topBar = {
                TopAppBar(
                    title = { Text("TicTacToe - $playerName", color = Color.White) }, // White text color
                    colors = androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2196F3)) // Dark Blue background
                )
            }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFF2196F3))
            ) {
                when (game.gameState) {
                    "player1_won", "player2_won", "draw" -> {
                        Text("Game Over!", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.padding(20.dp))

                        if (game.gameState == "draw") {
                            Text("It's a Draw!", style = MaterialTheme.typography.headlineMedium)
                        } else {
                            Text(
                                "Player ${if (game.gameState == "player1_won") "1" else "2"} won!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Green)
                        }

                        Button(
                            onClick = { navController.navigate("lobby") },
                            modifier = Modifier.padding(8.dp), // Add padding around button
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4C6C)) // Dark blue button color
                        ) {
                            Text("Back to lobby", color = Color.White) // White text inside button
                        }
                    }

                    else -> {
                        val myTurn =
                            game.gameState == "player1_turn" && game.player1Id == model.localPlayerId.value || game.gameState == "player2_turn" && game.player2Id ==
                                    model.localPlayerId.value
                        val turn =
                            if (myTurn)
                                "Your turn!"
                            else "Wait for other player"
                        Text(turn, style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1D4C6C)) // Dark blue for turn text
                        Spacer(modifier = Modifier.padding(20.dp))

                        Text("Player 1: ${players[game.player1Id]!!.name}", color = Color(0xFF1D4C6C)) // Dark blue for player text
                        Text("Player 2: ${players[game.player2Id]!!.name}", color = Color(0xFF1D4C6C)) // Dark blue for player text
                    }
                }
                Spacer(modifier = Modifier.padding(20.dp))



                for (i in 0 until rows) {
                    Row {
                        for (j in 0 until cols) {
                            Button(
                                shape = RectangleShape,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4C6C)), // Dark blue button background
                                onClick = {
                                    model.checkGameState(gameId, i * cols + j)
                                }
                            ) {
                                // Icon or Text inside the button
                                if (game.gameBoard[i * cols + j] == 1) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.close),
                                        tint = Color.Red, // Red color for X icon
                                        contentDescription = "X",
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else if (game.gameBoard[i * cols + j] == 2) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_circle_24),
                                        tint = Color.Yellow, // color for O icon
                                        contentDescription = "O",
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else {
                                    Text("") // Empty space if no player has clicked the button
                                }
                            }
                        }
                    }
                }
            }
        }
    }else {
            Log.e(
                "Error!",
                "Error Game not Found: $gameId"
            )
            navController.navigate("lobby")
        }
    }
