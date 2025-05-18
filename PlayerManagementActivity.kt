package com.example.hockeyapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PlayerManagementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_management)

        db = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.playerNameEditText)
        val ageEditText = findViewById<EditText>(R.id.playerAgeEditText)
        val positionEditText = findViewById<EditText>(R.id.playerPositionEditText)
        val teamEditText = findViewById<EditText>(R.id.playerTeamEditText)
        val registerButton = findViewById<Button>(R.id.registerPlayerButton)
        val viewPlayersButton = findViewById<Button>(R.id.viewPlayersButton)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val position = positionEditText.text.toString().trim()
            val team = teamEditText.text.toString().trim()

            if (name.isEmpty() || age.isEmpty() || position.isEmpty() || team.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val player = hashMapOf(
                "name" to name,
                "age" to age.toInt(),
                "position" to position,
                "team" to team
            )

            db.collection("players")
                .add(player)
                .addOnSuccessListener {
                    Toast.makeText(this, "Player Registered", Toast.LENGTH_SHORT).show()
                    nameEditText.text.clear()
                    ageEditText.text.clear()
                    positionEditText.text.clear()
                    teamEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        viewPlayersButton.setOnClickListener {
            startActivity(Intent(this, PlayerListActivity::class.java))
        }
    }
}

