package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.BlogPost
import com.example.proyectodesdisint.model.BlogReply
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val blogsCollection = db.collection("blogs")
    private val usersCollection = db.collection("users")

    private fun getTasksCollection(): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        return usersCollection.document(uid).collection("tareas")
    }

    // =========================================================
    // TAREAS
    // =========================================================

    fun addTask(
        task: Task,
        onSuccess: (Task) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val collection = getTasksCollection()
        if (collection == null) {
            onFailure(IllegalStateException("Usuario no autenticado"))
            return
        }

        val docRef = collection.document()
        val taskWithId = task.copy(documentId = docRef.id)

        docRef.set(taskWithId)
            .addOnSuccessListener {
                onSuccess(taskWithId)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun listenTasks(onResult: (List<Task>) -> Unit) {
        val collection = getTasksCollection()
        if (collection == null) {
            onResult(emptyList())
            return
        }

        collection.addSnapshotListener { snapshot, error ->

            if (error != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }

            val tasks = snapshot?.documents?.mapNotNull { document ->
                document.toObject(Task::class.java)?.also { task ->
                    task.documentId = document.id
                }
            } ?: emptyList()

            onResult(tasks)
        }
    }

    fun updateTask(
        task: Task,
        onSuccess: (Task) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val collection = getTasksCollection()
        if (collection == null) {
            onFailure(IllegalStateException("Usuario no autenticado"))
            return
        }

        if (task.documentId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "La tarea no contiene un documentId válido"
                )
            )
            return
        }

        collection.document(task.documentId)
            .update(
                mapOf(
                    "titulo" to task.titulo,
                    "descripcion" to task.descripcion,
                    "fecha" to task.fecha,
                    "hora" to task.hora,
                    "prioridad" to task.prioridad,
                    "completado" to task.completado,
                    "completionTime" to task.completionTime
                )
            )
            .addOnSuccessListener {
                onSuccess(task)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun deleteTask(
        documentId: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val collection = getTasksCollection()
        if (collection == null) {
            onFailure(IllegalStateException("Usuario no autenticado"))
            return
        }

        if (documentId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "El documentId de la tarea está vacío"
                )
            )
            return
        }

        collection.document(documentId)
            .delete()
            .addOnSuccessListener {
                onSuccess(documentId)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    // =========================================================
    // ASIGNACIÓN Y COLABORACIÓN (NUEVO)
    // =========================================================

    /**
     * Permite que un profesor o un compañero asigne una tarea a otro usuario.
     * La tarea se guarda directamente en la colección del destinatario.
     */
    fun assignTaskToUser(
        targetUid: String,
        task: Task,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        if (targetUid.isBlank()) {
            onFailure(IllegalArgumentException("ID de usuario destino no válido"))
            return
        }

        // Apuntamos a la colección de tareas del DESTINATARIO
        val docRef = usersCollection.document(targetUid).collection("tareas").document()
        
        val senderName = auth.currentUser?.displayName ?: "Un usuario"
        val taskWithId = task.copy(
            documentId = docRef.id,
            descripcion = "${task.descripcion}\n\n[Asignada por: $senderName]".trim()
        )

        docRef.set(taskWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // =========================================================
    // BLOG COMUNITARIO
    // =========================================================

    fun listenBlogs(
        onResult: (List<BlogPost>) -> Unit
    ) {
        blogsCollection
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val blogs = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(BlogPost::class.java)?.copy(
                        id = document.id
                    )
                } ?: emptyList()

                onResult(blogs)
            }
    }

    fun addBlogPost(
        blogPost: BlogPost,
        onSuccess: (BlogPost) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val documentReference = blogsCollection.document()

        val postWithId = blogPost.copy(
            id = documentReference.id
        )

        documentReference
            .set(postWithId)
            .addOnSuccessListener {
                onSuccess(postWithId)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun updateBlogPost(
        blogId: String,
        newTitle: String,
        newContent: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection.document(blogId)
            .update(mapOf(
                "titulo" to newTitle,
                "contenido" to newContent
            ))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteBlogPost(
        blogId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection.document(blogId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun listenBlogReplies(
        blogId: String,
        onResult: (List<BlogReply>) -> Unit
    ) {
        if (blogId.isBlank()) {
            onResult(emptyList())
            return
        }

        blogsCollection
            .document(blogId)
            .collection("replies")
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val replies = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(BlogReply::class.java)?.copy(
                        id = document.id
                    )
                } ?: emptyList()

                onResult(replies)
            }
    }

    fun addBlogReply(
        blogId: String,
        reply: BlogReply,
        onSuccess: (BlogReply) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        if (blogId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "El identificador de la publicación está vacío"
                )
            )
            return
        }

        val documentReference = blogsCollection
            .document(blogId)
            .collection("replies")
            .document()

        val replyWithId = reply.copy(
            id = documentReference.id
        )

        documentReference
            .set(replyWithId)
            .addOnSuccessListener {
                onSuccess(replyWithId)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun updateBlogReply(
        blogId: String,
        replyId: String,
        newText: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection.document(blogId).collection("replies").document(replyId)
            .update("texto", newText)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteBlogReply(
        blogId: String,
        replyId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection.document(blogId).collection("replies").document(replyId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // =========================================================
    // PERFIL BÁSICO DEL AUTOR
    // =========================================================

    suspend fun getUserProfile(uid: String): User? =
        suspendCoroutine { continuation ->

            if (uid.isBlank()) {
                continuation.resume(null)
                return@suspendCoroutine
            }

            usersCollection
                .document(uid)
                .get()
                .addOnSuccessListener { document ->

                    val user = document
                        .toObject(User::class.java)
                        ?.copy(uid = uid)

                    continuation.resume(user)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
}
