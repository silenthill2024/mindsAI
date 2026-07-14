package com.example.mindsai.repository

import com.example.mindsai.local.SupabaseClient
import com.example.mindsai.model.ForumPost
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio que interactúa con Supabase (NoSQL-like via Postgrest).
 */
class ForumRepository {
    
    private val _posts = MutableStateFlow<List<ForumPost>>(emptyList())
    val postsFlow: Flow<List<ForumPost>> = _posts.asStateFlow()

    private val table = SupabaseClient.client.postgrest["posts"]

    /**
     * Carga los posts desde Supabase y actualiza el StateFlow
     */
    suspend fun fetchPosts() {
        println("DEBUG_SUPABASE: Intentando cargar posts...")
        try {
            val remotePosts = table.select {
                order("id", order = Order.DESCENDING)
            }.decodeList<ForumPost>()
            println("DEBUG_SUPABASE: Se cargaron ${remotePosts.size} posts")
            _posts.value = remotePosts
        } catch (e: Exception) {
            println("DEBUG_SUPABASE: ERROR al cargar: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getPosts(): Flow<List<ForumPost>> = postsFlow

    /**
     * Crea un post en Supabase
     */
    suspend fun addPost(post: ForumPost) {
        try {
            // Insertamos en la base de datos remota
            table.insert(post)
            // Recargamos la lista para ver el cambio
            fetchPosts()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Vota un post en Supabase
     */
    suspend fun votePost(postId: Long, delta: Int) {
        try {
            // Obtenemos el post actual para saber sus votos
            val currentPost = _posts.value.find { it.id == postId } ?: return
            val newVotes = currentPost.votes + delta
            
            // Actualizamos en Supabase
            table.update({
                set("votes", newVotes)
            }) {
                filter {
                    eq("id", postId)
                }
            }
            
            // Recargamos localmente
            fetchPosts()
        } catch (e: Exception) {
            println("DEBUG_SUPABASE: ERROR al votar: ${e.message}")
            e.printStackTrace()
        }
    }
}
