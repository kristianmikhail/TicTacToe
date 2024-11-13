package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tictactoe.ui.theme.TicTacToeTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


data class Player(
     var playerID: String ="",
     var name: String=""
)

fun test() {
    //example working now
    // berra
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

                MainScreen()
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val db = Firebase.firestore
    val playerList = MutableStateFlow<List<Player>>(emptyList())

    db.collection("players").addSnapshotListener { value, error ->
        if (error != null) {
            return@addSnapshotListener
        }
        if (value != null) {
            playerList.value = value.toObjects()
        }
    }

    val players by playerList.asStateFlow().collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(players) { player ->
                Text("Name: ${player.name}")
            }
        }
    }
}
