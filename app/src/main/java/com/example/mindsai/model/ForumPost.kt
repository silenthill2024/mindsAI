package com.example.mindsai.model

import kotlinx.serialization.Serializable

/**
 * Representa un "Documento" en una base de datos No Relacional (como Firebase).
 * A diferencia de Room, aquí podemos tener estructuras más flexibles.
 */
@Serializable
data class ForumPost(
    val id: Long? = null,
    val title: String = "",
    val body: String = "",
    val category: String = "",
    val author: String = "",
    val votes: Int = 0,
    // En NoSQL es fácil tener campos opcionales o dinámicos
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap() 
)
