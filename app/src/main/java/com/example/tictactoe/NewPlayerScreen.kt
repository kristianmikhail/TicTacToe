package com.example.tictactoe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlayerScreen(navController: NavController, model: GameModel) {
    var playerName by remember { mutableStateOf("") }
    val sharedPreference= LocalContext.current.getSharedPreferences("TicTacToePrefs", android.content.Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        model.localPlayerId.value= sharedPreference.getString("playerId", null)
        if (model.localPlayerId.value !=null){
            navController.navigate("lobby")
        }
    }
if (model.localPlayerId.value==null){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to TicTacToe Game!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        OutlinedTextField(
            value = playerName,
            onValueChange = { newValue ->
                playerName = newValue
            },
            label = { Text("Enter your name ") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        Button(
            onClick = {
                if (playerName.isNotBlank()) {
                    // Create new player in firebase
                    val newPlayer = Player(name = playerName)
                    model.db.collection("players").add(newPlayer)
                        .addOnSuccessListener { documentRef ->
                            val newPlayerId = documentRef.id

                            // Save playerId in shared preference
                            sharedPreference.edit().putString("playerId", newPlayerId).apply()

                            model.localPlayerId.value = newPlayerId
                            navController.navigate("lobby")
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = playerName.isNotEmpty()
        ) {
            Text("Enter Game")
        }
    }
}
}
