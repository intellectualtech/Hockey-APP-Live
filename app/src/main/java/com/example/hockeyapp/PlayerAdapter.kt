package com.example.hockeyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(
    private val playerList: List<Player>,
    private val playerIds: List<String>,
    private val onEditClick: (Player, String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.playerName)
        val ageText: TextView = view.findViewById(R.id.playerAge)
        val positionText: TextView = view.findViewById(R.id.playerPosition)
        val teamText: TextView = view.findViewById(R.id.playerTeam)
        val editBtn: Button = view.findViewById(R.id.editPlayerButton)
        val deleteBtn: Button = view.findViewById(R.id.deletePlayerButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = playerList[position]
        val playerId = playerIds[position]

        holder.nameText.text = "Name: ${player.name}"
        holder.ageText.text = "Age: ${player.age}"
        holder.positionText.text = "Position: ${player.position}"
        holder.teamText.text = "Team: ${player.team}"

        holder.editBtn.setOnClickListener {
            onEditClick(player, playerId)
        }

        holder.deleteBtn.setOnClickListener {
            onDeleteClick(playerId)
        }
    }

    override fun getItemCount() = playerList.size
}
