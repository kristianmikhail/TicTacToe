package com.example.tictactoe

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow



data class Player(
    var name: String=""
)

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
    //val playerList = MutableStateFlow<List<Player>>(emptyList())
    val playerMap = MutableStateFlow<Map<String, Player>>(emptyMap())
    val gameMap = MutableStateFlow<Map<String, Game>>(emptyMap())



    fun initGame() {

// här skapar jag tabeller både för players men även varje game. sparas såklart i firebase

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

    // Kontrollera rader, kolumner och diagonaler för att se om en spelare har vunnit.
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
            if (board[i] != 0 && board[i] == board[i + 3] && board[i] == board[i + 6]) {
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


// update i firebase baserat på vad spelarna placeras sig, de olika index mellan 0-8
// player1 placeras med nummer 1 och player 2 med 2 och allt sparas i firebase,
//
    fun checkGameState(gameId: String?, cell: Int) {

        if (gameId != null) {
            val game: Game? = gameMap.value[gameId]
            if (game != null) {

                val myTurn = game.gameState == "player1_turn" && game.player1Id == localPlayerId.value || game.gameState == "player2_turn" && game.player2Id == localPlayerId.value
                if (!myTurn) return

                val list: MutableList<Int> = game.gameBoard.toMutableList() // // Skapar en muterbar kopia av gameBoard för att kunna ändra innehållet.

                if (list[cell]!=0){
                    return
                }

                if (game.gameState == "player1_turn") {
                    list[cell] = 1

                } else if (game.gameState == "player2_turn") {
                    list[cell] = 2
                }
                var turn = ""
                if (game.gameState == "player1_turn") {
                    turn = "player2_turn"
                } else {
                    turn = "player1_turn"
                }

                val winner = checkWinner(list.toList())
                // sen anropar vi checkwinner, om man uppfyller kraven så vinner den spelaren.
                if (winner == 1) {
                    turn = "player1_won"
                } else if (winner == 2) {
                    turn = "player2_won"
                } else if (winner == 3) {
                    turn = "draw"
                }

                db.collection("games").document(gameId)
                    .update(
                        "gameBoard", list,
                        "gameState", turn
                    )
                // Uppdaterar dokumentet med nytt gameBoard och gameState i databasen.
            }
        }
    }


}