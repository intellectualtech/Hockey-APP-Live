package com.example.hockeyapp

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class TeamListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var db: FirebaseFirestore
    private lateinit var teamList: ArrayList<HashMap<String, Any>>
    private lateinit var teamNames: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_list)

        listView = findViewById(R.id.teamListView)
        db = FirebaseFirestore.getInstance()
        teamList = ArrayList()
        teamNames = ArrayList()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, teamNames)
        listView.adapter = adapter

        loadTeams()

        listView.setOnItemClickListener { _, _, position, _ ->
            val team = teamList[position]
            showEditDeleteDialog(team)
        }
    }

    private fun loadTeams() {
        db.collection("teams")
            .get()
            .addOnSuccessListener { result ->
                teamList.clear()
                teamNames.clear()
                for (doc in result) {
                    val team = doc.data as HashMap<String, Any>
                    team["id"] = doc.id
                    teamList.add(team)
                    teamNames.add("${team["teamName"]} - ${team["coachName"]}")
                }

                if (teamList.isEmpty()) {
                    Toast.makeText(this, "No teams found", Toast.LENGTH_SHORT).show()
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading teams", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditDeleteDialog(team: HashMap<String, Any>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_team, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editTeamName)
        val coachEdit = dialogView.findViewById<EditText>(R.id.editCoachName)
        val locationEdit = dialogView.findViewById<EditText>(R.id.editLocation)

        nameEdit.setText(team["teamName"].toString())
        coachEdit.setText(team["coachName"].toString())
        locationEdit.setText(team["location"].toString())

        AlertDialog.Builder(this)
            .setTitle("Edit or Delete Team")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val teamName = nameEdit.text.toString().trim()
                val coachName = coachEdit.text.toString().trim()
                val location = locationEdit.text.toString().trim()

                if (teamName.isEmpty() || coachName.isEmpty() || location.isEmpty()) {
                    Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updated = hashMapOf(
                    "teamName" to teamName,
                    "coachName" to coachName,
                    "location" to location
                )

                val docId = team["id"].toString()
                db.collection("teams").document(docId)
                    .set(updated)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Team Updated", Toast.LENGTH_SHORT).show()
                        loadTeams()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Delete") { _, _ ->
                val docId = team["id"].toString()
                db.collection("teams").document(docId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Team Deleted", Toast.LENGTH_SHORT).show()
                        loadTeams()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
}

