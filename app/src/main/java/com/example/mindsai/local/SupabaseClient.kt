package com.example.mindsai.local

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://tvfehijglpgcvozkrjbn.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR2ZmVoaWpnbHBnY3ZvemtyamJuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODM5NTE4MjQsImV4cCI6MjA5OTUyNzgyNH0.3iL5R9c17DQZDLKiauI6Z8_e_AlFTlqOXDyFCzmcivI"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
