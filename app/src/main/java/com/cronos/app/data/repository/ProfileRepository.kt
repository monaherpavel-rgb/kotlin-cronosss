package com.cronos.app.data.repository

import com.cronos.app.data.model.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class ParticipantUpdate(
    val role: String = "participant",
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("birth_date") val birthDate: String,
    val city: String,
    val interests: List<String>,
    val motivation: String,
    @SerialName("onboarding_completed") val onboardingCompleted: Boolean = true,
    @SerialName("verification_status") val verificationStatus: String = "approved"
)

@Serializable
private data class OrganizerUpdate(
    val role: String = "organizer",
    @SerialName("first_name") val firstName: String,
    val organization: String,
    val position: String,
    @SerialName("event_types") val eventTypes: List<String>,
    @SerialName("onboarding_completed") val onboardingCompleted: Boolean = true,
    @SerialName("verification_status") val verificationStatus: String = "pending"
)

@Serializable
private data class ObserverUpdate(
    val role: String = "observer",
    @SerialName("first_name") val firstName: String,
    val organization: String,
    @SerialName("observer_role") val observerRole: String,
    @SerialName("onboarding_completed") val onboardingCompleted: Boolean = true,
    @SerialName("verification_status") val verificationStatus: String = "pending"
)

@Serializable
private data class UsernameUpdate(val username: String)

@Serializable
private data class AvatarUpdate(@SerialName("avatar_url") val avatarUrl: String)

@Serializable
private data class PortfolioInsert(
    @SerialName("user_id") val userId: String,
    val title: String,
    val description: String,
    @SerialName("project_url") val projectUrl: String? = null
)

@Singleton
class ProfileRepository @Inject constructor(private val supabase: SupabaseClient) {

    suspend fun getMyProfile(): Result<Profile> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["profiles"]
            .select(Columns.ALL) { eq("id", uid) }
            .decodeSingle<Profile>()
    }

    suspend fun updateUsername(username: String): Result<Unit> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["profiles"].update(
            UsernameUpdate(username = username)
        ) { eq("id", uid) }
    }

    suspend fun updateAvatarUrl(avatarUrl: String): Result<Unit> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["profiles"].update(
            AvatarUpdate(avatarUrl = avatarUrl)
        ) { eq("id", uid) }
    }

    suspend fun addPortfolioItem(title: String, description: String, projectUrl: String): Result<Unit> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["portfolio"].insert(
            PortfolioInsert(userId = uid, title = title, description = description, projectUrl = projectUrl.ifBlank { null })
        )
    }

    suspend fun saveParticipantOnboarding(
        firstName: String, lastName: String, birthDate: String,
        city: String, interests: List<String>, motivation: String
    ): Result<Unit> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["profiles"].update(
            ParticipantUpdate(
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDate,
                city = city,
                interests = interests,
                motivation = motivation
            )
        ) { eq("id", uid) }
    }

    suspend fun saveOrganizerOnboarding(
        firstName: String, organization: String,
        position: String, eventTypes: List<String>
    ): Result<Unit> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["profiles"].update(
            OrganizerUpdate(
                firstName = firstName,
                organization = organization,
                position = position,
                eventTypes = eventTypes
            )
        ) { eq("id", uid) }
    }

    suspend fun saveObserverOnboarding(
        firstName: String, organization: String, observerRole: String
    ): Result<Unit> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["profiles"].update(
            ObserverUpdate(
                firstName = firstName,
                organization = organization,
                observerRole = observerRole
            )
        ) { eq("id", uid) }
    }
}
