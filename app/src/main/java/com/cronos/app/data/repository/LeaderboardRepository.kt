package com.cronos.app.data.repository

import com.cronos.app.data.model.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

// Leaderboard uses profiles table directly, ordered by rating (to be added later)
// For now returns top profiles sorted by name as placeholder
@Singleton
class LeaderboardRepository @Inject constructor(private val supabase: SupabaseClient) {

    suspend fun getTop100(): Result<List<Profile>> = runCatching {
        supabase.postgrest["profiles"]
            .select(Columns.ALL) {
                eq("onboarding_completed", true)
                order("first_name", Order.ASCENDING)
                limit(100)
            }
            .decodeList<Profile>()
    }
}
