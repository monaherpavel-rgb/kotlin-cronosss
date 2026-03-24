package com.cronos.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val date: String = "",
    val format: String = "offline",
    val direction: String = "it",
    val difficulty: Int = 1,
    @SerialName("max_participants") val maxParticipants: Int = 100,
    val points: Int = 0,
    @SerialName("organizer_id") val organizerId: String = "",
    @SerialName("organizer_name") val organizerName: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class EventParticipation(
    val id: String = "",
    @SerialName("event_id") val eventId: String = "",
    @SerialName("user_id") val userId: String = "",
    val role: String = "participant",
    @SerialName("points_earned") val pointsEarned: Int = 0,
    val confirmed: Boolean = false
)

@Serializable
data class LeaderboardEntry(
    @SerialName("user_id") val userId: String = "",
    val rating: Int = 0,
    val rank: Int = 0,
    val level: String = "bronze",
    val profiles: Profile? = null
)
