package com.example.hockeyapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PlayerListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var playerList: ArrayList<Player>
    private lateinit var playerIds: ArrayList<String>
    private lateinit var adapter: PlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_list)

        recyclerView = findViewById(R.id.playerRecyclerView)  // RecyclerView in your XML
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        playerList = ArrayList()
        playerIds = ArrayList()

        adapter = PlayerAdapter(
            playerList,
            playerIds,
            onEditClick = { player, id -> showEditPlayerDialog(player, id) },
            onDeleteClick = { id -> deletePlayer(id) }
        )

        recyclerView.adapter = adapter

        loadPlayers()
    }

    private fun loadPlayers() {
        db.collection("players")
            .get()
            .addOnSuccessListener { result ->
                playerList.clear()
                playerIds.clear()
                for (document in result) {
                    val player = Player(
                        name = document.getString("name") ?: "",
                        age = (document.getLong("age") ?: 0L).toInt(),
                        position = document.getString("position") ?: "",
                        team = document.getString("team") ?: ""
                    )
                    playerList.add(player)
                    playerIds.add(document.id)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading players: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditPlayerDialog(player: Player, playerId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_player, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
        val ageEdit = dialogView.findViewById<EditText>(R.id.editAge)
        val positionEdit = dialogView.findViewById<EditText>(R.id.editPosition)
        val teamEdit = dialogView.findViewById<EditText>(R.id.editTeam)

        nameEdit.setText(player.name)
        ageEdit.setText(player.age.toString())
        positionEdit.setText(player.position)
        teamEdit.setText(player.team)

        AlertDialog.Builder(this)
            .setTitle("Edit Player")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedPlayer = hashMapOf(
                    "name" to nameEdit.text.toString().trim(),
                    "age" to ageEdit.text.toString().trim().toInt(),
                    "position" to positionEdit.text.toString().trim(),
                    "team" to teamEdit.text.toString().trim()
                )

                db.collection("players").document(playerId)
                    .set(updatedPlayer)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Player Updated", Toast.LENGTH_SHORT).show()
                        loadPlayers()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePlayer(playerId: String) {
        db.collection("players").document(playerId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Player Deleted", Toast.LENGTH_SHORT).show()
                loadPlayers()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show()
            }
    }
}


