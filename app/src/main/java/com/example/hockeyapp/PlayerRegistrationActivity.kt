package com.example.hockeyapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PlayerRegistrationActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var positionInput: EditText
    private lateinit var teamInput: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_registration)

        // Link UI elements
        nameInput = findViewById(R.id.editTextName)
        ageInput = findViewById(R.id.editTextAge)
        positionInput = findViewById(R.id.editTextPosition)
        teamInput = findViewById(R.id.editTextTeam)
        registerButton = findViewById(R.id.buttonRegister)
        progressBar = findViewById(R.id.playerProgressBar)

        progressBar.visibility = ProgressBar.GONE

        // Set click listener
        registerButton.setOnClickListener {
            registerPlayer()
        }
    }

    private fun registerPlayer() {
        val name = nameInput.text.toString().trim()
        val ageStr = ageInput.text.toString().trim()
        val position = positionInput.text.toString().trim()
        val team = teamInput.text.toString().trim()

        if (name.isEmpty() || ageStr.isEmpty() || position.isEmpty() || team.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val age = try {
            ageStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show()
            return
        }

        val player = Player(name, age, position, team)

        progressBar.visibility = ProgressBar.VISIBLE
        registerButton.isEnabled = false

        db.collection("players")
            .add(player)
            .addOnSuccessListener {
                Toast.makeText(this, "Player registered successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressBar.visibility = ProgressBar.GONE
                registerButton.isEnabled = true
            }
    }

    private fun clearFields() {
        nameInput.text.clear()
        ageInput.text.clear()
        positionInput.text.clear()
        teamInput.text.clear()
    }
}
