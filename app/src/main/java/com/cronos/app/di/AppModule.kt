package com.cronos.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://legdckzssqwpvdaztbzv.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxlZ2Rja3pzc3F3cHZkYXp0Ynp2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQyNzc0MTQsImV4cCI6MjA4OTg1MzQxNH0.ABUxupkfaFs_ZXrHfhEyuo4n-vWBn46Xsqo1De0elKE"
        ) {
            // Игнорируем неизвестные поля из Supabase (updated_at, events_organized и т.д.)
            defaultSerializer = KotlinXSerializer(Json { ignoreUnknownKeys = true })
            install(GoTrue)
            install(Postgrest)
            install(Realtime)
        }
    }
}
