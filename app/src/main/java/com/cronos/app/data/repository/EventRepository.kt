package com.cronos.app.data.repository

import com.cronos.app.data.model.Event
import com.cronos.app.data.model.EventParticipation
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(private val supabase: SupabaseClient) {

    suspend fun getEvents(): Result<List<Event>> = runCatching {
        supabase.postgrest["events"]
            .select(Columns.ALL) { order("date", Order.ASCENDING) }
            .decodeList<Event>()
    }

    suspend fun getMyParticipations(): Result<List<EventParticipation>> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["event_participations"]
            .select(Columns.ALL) { eq("user_id", uid) }
            .decodeList<EventParticipation>()
    }

    suspend fun joinEvent(eventId: String): Result<Unit> = runCatching {
        val uid = supabase.gotrue.currentUserOrNull()?.id ?: error("Not logged in")
        supabase.postgrest["event_participations"].insert(
            mapOf("event_id" to eventId, "user_id" to uid)
        )
    }
}
