package com.example.tictactoe


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(navController: NavController, model: GameModel) {

    val players by model.playerMap.asStateFlow().collectAsStateWithLifecycle()
    val games by model.gameMap.asStateFlow().collectAsStateWithLifecycle()

    LaunchedEffect(games) {
        games.forEach { (gameId, game) ->
            // TODO: Popup with accept invite?
            if ((game.player1Id == model.localPlayerId.value || game.player2Id == model.localPlayerId.value)
                && (game.gameState == "player1_turn" || game.gameState == "player2_turn")) {
                navController.navigate("game/${gameId}")
            }
        }
    }

    var playerName = "Unknown?"
    players[model.localPlayerId.value]?.let {
        playerName = it.name
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TicTacToe - $playerName",
                        color = Color.White // Vit text för TopAppBar
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF2196F3) // Ljusblå bakgrund för TopAppBar
                )
            )
        }
    ) {innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(players.entries.toList()) { (documentId, player) ->
                if (documentId != model.localPlayerId.value) { //Don't show yourself
                    ListItem(
                        headlineText = {
                            Text(
                                "Player Name: ${player.name}",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black) // Ljusblå färg på spelarens namn
                            )
                        },
                        supportingText = {
                            Text(
                                "Online",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Green) // Ljusgrå status
                            )
                        },
                        trailingContent = {

                            var hasGame = false
                            games.forEach { (gameId, game) ->
                                if (game.player1Id == model.localPlayerId.value
                                    && game.player2Id == documentId &&  game.gameState == "invite"
                                ) {
                                    Text("Waiting for accept...", color = Color(0xFFFFC107)) // Gult meddelande
                                    hasGame = true
                                } else if (game.player2Id == model.localPlayerId.value
                                    && game.player1Id == documentId && game.gameState == "invite") {
                                    Button(onClick = {
                                        model.db.collection("games").document(gameId)
                                            .update("gameState", "player1_turn")
                                            .addOnSuccessListener {
                                                navController.navigate("game/${gameId}")
                                            }
                                            .addOnFailureListener {
                                                Log.e(
                                                    "Error",
                                                    "Error updating game: $gameId")
                                            }
                                    },
                                        colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF5722), // Orange knappfärg
                                        contentColor = Color.White // Vit text på knappen
                                    )
                                    )
                                    {
                                        Text("Accept invite")
                                    }
                                    hasGame = true
                                }
                            }
                            if (!hasGame) {
                                Button(onClick = {
                                    model.db.collection("games")
                                        .add(Game(gameState = "invite",
                                            player1Id = model.localPlayerId.value!!,
                                            player2Id = documentId))
                                        .addOnSuccessListener { documentRef ->
                                            // lyssnar på framgång gällande databasen
                                        }
                                },colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3), // Blå färg på knappen
                                    contentColor = Color.White // Vit text på knappen
                                )
                                    ) {
                                    Text("Challenge")
                                }
                            }
                        }
                    )
                }
            }
        }

    }
}






