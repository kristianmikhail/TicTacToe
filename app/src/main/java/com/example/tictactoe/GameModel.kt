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

const val rows = 3
const val cols = 3


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
                val updatedMap = value.documents.associate { doc ->
                    doc.id to doc.toObject(Player::class.java)!!
                }
                playerMap.value = updatedMap
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

    // Kontrollera rader, kolumner, och diagonaler för att se om en spelare har vunnit.
    // Kontrollera om spelet är oavgjort (alla rutor är fyllda och ingen har vunnit).

    fun checkWinner(board: List<Int>): Int {
        // Check rows
        for (i in 0..2) {
            if (board[i * 3] != 0 && board[i * 3] == board[i * 3 + 1] && board[i * 3] == board[i * 3 + 2]) {
                return board[i * 3]
            }

        }

        // Check columns
        for (i in 0..2) {
            if (board[i] != 0 && board[i] == board[i * 3] && board[i] == board[i + 6]) {
                return board[i]
            }
        }

        // Check diagonals
        if (board[0] != 0 && board[0] == board[4] && board[0] == board[8]) {
            return board[0]
        }
        if (board[2] != 0 && board[2] == board[4] && board[2] == board[6]) {
            return board[2]
        }

        // Check draw
        if (!board.contains(0)) { // Check if all cells are filled  and no winner
            return 3
        }

        // No winner
        return 0
    }


}