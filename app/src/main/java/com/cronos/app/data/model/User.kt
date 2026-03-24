package com.cronos.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String = "",
    val email: String? = null,
    val username: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val role: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("birth_date") val birthDate: String? = null,
    val city: String? = null,
    val interests: List<String>? = null,
    val motivation: String? = null,
    val organization: String? = null,
    val position: String? = null,
    val experience: String? = null,
    @SerialName("event_types") val eventTypes: List<String>? = null,
    @SerialName("observer_role") val observerRole: String? = null,
    @SerialName("onboarding_completed") val onboardingCompleted: Boolean = false,
    @SerialName("verification_status") val verificationStatus: String = "pending",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("events_organized") val eventsOrganized: Int? = null,
    @SerialName("events_at") val eventsAt: String? = null,
    @SerialName("total_points") val totalPoints: Int? = null,
    @SerialName("rating_score") val ratingScore: Float? = null,
    @SerialName("level") val level: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("social_links") val socialLinks: List<String>? = null
) {
    val displayName: String
        get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { email ?: "Пользователь" }
}

@Serializable
data class PortfolioItem(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val title: String = "",
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("project_url") val projectUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

// Локальная модель для заявки на событие
data class EventApplication(
    val eventId: String,
    val eventTitle: String,
    val direction: String,
    val date: String,
    val status: String = "pending" // pending / approved / rejected
)

enum class UserLevel { BRONZE, SILVER, GOLD, RESERVE }
