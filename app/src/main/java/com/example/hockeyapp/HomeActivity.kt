package com.example.hockeyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        val welcomeText = findViewById<TextView>(R.id.welcomeTextView)
        welcomeText.text = "Welcome to Namibia Hockey Union!"

        val teamRegBtn = findViewById<Button>(R.id.teamRegButton)
        val playerRegBtn = findViewById<Button>(R.id.playerRegButton)
        val viewPlayersBtn = findViewById<Button>(R.id.viewPlayersButton)
        val viewTeamsBtn = findViewById<Button>(R.id.viewTeamsButton)
        val eventRegBtn = findViewById<Button>(R.id.eventRegistrationButton)
        val logoutBtn = findViewById<Button>(R.id.logoutButton)

        teamRegBtn.setOnClickListener {
            startActivity(Intent(this, TeamRegistrationActivity::class.java))
        }

        playerRegBtn.setOnClickListener {
            startActivity(Intent(this, PlayerManagementActivity::class.java))
        }

        viewPlayersBtn.setOnClickListener {
            startActivity(Intent(this, PlayerListActivity::class.java))
        }

        viewTeamsBtn.setOnClickListener {
            startActivity(Intent(this, TeamListActivity::class.java))
        }

        eventRegBtn.setOnClickListener {
            startActivity(Intent(this, EventRegistrationActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

