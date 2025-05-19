package com.example.hockeyapplive.data

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDatabaseManager(context: Context) {
    private val database: FirebaseDatabase = Firebase.database

    companion object {
        // Database paths
        const val PATH_USERS = "users"
        const val PATH_TEAMS = "teams"
        const val PATH_PLAYERS = "players"
        const val PATH_EVENTS = "events"
        const val PATH_POSTS = "posts"
        const val PATH_SUBSCRIPTIONS = "subscriptions"
        const val PATH_NOTIFICATIONS = "notifications"
        const val PATH_TEAM_REGISTRATIONS = "teamRegistrations"

        // Initialize Firebase with persistence
        fun initialize(context: Context) {
            Firebase.database.setPersistenceEnabled(true)
        }
    }

    // Database reference shortcuts
    fun usersRef() = database.getReference(PATH_USERS)
    fun teamsRef() = database.getReference(PATH_TEAMS)
    fun playersRef() = database.getReference(PATH_PLAYERS)
    fun eventsRef() = database.getReference(PATH_EVENTS)
    fun postsRef() = database.getReference(PATH_POSTS)
    fun subscriptionsRef() = database.getReference(PATH_SUBSCRIPTIONS)
    fun notificationsRef() = database.getReference(PATH_NOTIFICATIONS)
    fun teamRegistrationsRef() = database.getReference(PATH_TEAM_REGISTRATIONS)

    // Helper methods for common operations
    fun getUserRef(userId: String) = usersRef().child(userId)
    fun getTeamRef(teamId: String) = teamsRef().child(teamId)
    fun getPlayerRef(playerId: String) = playersRef().child(playerId)

    // Initialize database structure (optional)
    fun initializeDatabaseStructure() {
        // This is optional as Firebase creates paths automatically
        // Can be used to set initial values if needed
    }
}