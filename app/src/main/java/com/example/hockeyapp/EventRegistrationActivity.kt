package com.example.hockeyapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EventRegistrationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_registration)

        db = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.eventNameEditText)
        val dateEditText = findViewById<EditText>(R.id.eventDateEditText)
        val locationEditText = findViewById<EditText>(R.id.eventLocationEditText)
        val registerButton = findViewById<Button>(R.id.registerEventButton)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()

            if (name.isEmpty() || date.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "⚠️ Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = hashMapOf(
                "name" to name,
                "date" to date,
                "location" to location
            )

            // Show loading

            db.collection("events")
                .add(event)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ Event Registered Successfully", Toast.LENGTH_SHORT).show()
                    nameEditText.text.clear()
                    dateEditText.text.clear()
                    locationEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "❌ Failed to register event: ${e.message}", Toast.LENGTH_LONG).show()
                }

        }
    }
}