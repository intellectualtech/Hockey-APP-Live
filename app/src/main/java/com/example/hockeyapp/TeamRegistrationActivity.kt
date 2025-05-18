package com.example.hockeyapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class TeamRegistrationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_registration)

        db = FirebaseFirestore.getInstance()

        val teamNameEditText = findViewById<EditText>(R.id.teamNameEditText)
        val coachNameEditText = findViewById<EditText>(R.id.coachNameEditText)
        val locationEditText = findViewById<EditText>(R.id.locationEditText)
        val registerButton = findViewById<Button>(R.id.registerTeamButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.GONE

        registerButton.setOnClickListener {
            val teamName = teamNameEditText.text.toString().trim()
            val coachName = coachNameEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()

            if (teamName.isEmpty() || coachName.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val team = hashMapOf(
                "teamName" to teamName,
                "coachName" to coachName,
                "location" to location
            )

            // Show progress bar and disable button
            progressBar.visibility = View.VISIBLE
            registerButton.isEnabled = false

            db.collection("teams")
                .add(team)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ Team registered successfully", Toast.LENGTH_LONG).show()
                    teamNameEditText.text.clear()
                    coachNameEditText.text.clear()
                    locationEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "❌ Failed to register team: ${e.message}", Toast.LENGTH_LONG).show()
                }
                .addOnCompleteListener {
                    progressBar.visibility = View.GONE
                    registerButton.isEnabled = true
                }
        }
    }
}
