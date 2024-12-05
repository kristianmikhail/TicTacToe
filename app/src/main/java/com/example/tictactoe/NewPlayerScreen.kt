package com.example.tictactoe

import android.util.Log
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
import android.content.Context
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlayerScreen(navController: NavController, model: GameModel) {
    var playerName by remember { mutableStateOf("") } // Skapar en state-variabel `playerName`.
    val sharedPreference= LocalContext.current.getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE) // som används för att lagra och läsa små data lokalt på enheten.
// den här är mer eller mindre som live data.
    LaunchedEffect(Unit) {
        model.localPlayerId.value= sharedPreference.getString("playerId", null)
        if (model.localPlayerId.value !=null){
            navController.navigate("lobby")
        }
    }
if (model.localPlayerId.value==null){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color(0xFF2196F3) // Ljusblå bakgrund för en mer lugn känsla
                //
                 ){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to TicTacToe Game!",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = androidx.compose.ui.graphics.Color.White // Vit text för rubrik
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        OutlinedTextField(
            value = playerName,
            onValueChange = { newValue ->
                playerName = newValue
            },
            label = { Text("Enter your name ", color = androidx.compose.ui.graphics.Color.White)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFC107), // Gul fokuserad kant
            unfocusedBorderColor = Color(0xFFB0BEC5), // Ljusgrå kant när inte fokuserad
            textColor = Color.White // Vit text i textfältet
        ))
        Button(
            onClick = { if (playerName.isNotBlank()) {
                // Create new player in Firestore
                val newPlayer = Player(name = playerName)

                model.db.collection("players")
                    .add(newPlayer)
                    .addOnSuccessListener { documentRef ->
                        val newPlayerId = documentRef.id

                        // Save playerId in SharedPreferences

                        sharedPreference.edit().putString("playerId", newPlayerId).apply()

                        // Update local variable and navigate to lobby
                        model.localPlayerId.value = newPlayerId
                        navController.navigate("lobby")
                    }.addOnFailureListener { error ->
                        Log.e("Error", "Error creating player: ${error.message}")
                    }
            } },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF5722), // Orange knappfärg
                contentColor = Color.White // Vit text på knappen
            )
        ) {
            Text("Create Player")
        }
    }
}
}}

