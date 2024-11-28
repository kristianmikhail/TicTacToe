package com.example.tictactoe

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow





data class Game(
    var gameBoard: List<Int> = List(9) { 0 }, // 0: empty, 1: player1's move, 2: player2's move
    var gameState: String = "invite", // Possible values: "invite", "player1_turn", "player2_turn" "player1_won", "player2_won", "draw"
    var player1Id: String = "",
    var player2Id: String = ""
)



class GameModel: ViewModel() {
    val db = Firebase.firestore
    var localPlayerId = mutableStateOf<String?>(null)
    val playerList = MutableStateFlow<List<Player>>(emptyList())
    val playerMap = MutableStateFlow<Map<String, Player>>(emptyMap())
    val gameMap = MutableStateFlow<Map<String, Game>>(emptyMap())



    fun initGame() {

        db.collection("players").addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (value != null) {
                playerList.value = value.toObjects()
            }
        }

        // Listen for games
        db.collection("games")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    val updatedMap = value.documents.associate { doc ->
                        doc.id to doc.toObject(Game::class.java)!!
                    }
                    gameMap.value = updatedMap
                }
            }

    }
}