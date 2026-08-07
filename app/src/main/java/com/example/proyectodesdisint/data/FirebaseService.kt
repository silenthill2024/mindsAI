package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.BlogPost
import com.example.proyectodesdisint.model.BlogReply
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()

    private val tasksCollection = db.collection("tasks")
    private val blogsCollection = db.collection("blogs")
    private val usersCollection = db.collection("users")

    // =========================================================
    // TAREAS
    // =========================================================

    fun addTask(
        task: Task,
        onSuccess: (Task) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val docRef = tasksCollection.document()
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
        tasksCollection.addSnapshotListener { snapshot, error ->

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
        if (task.documentId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "La tarea no contiene un documentId válido"
                )
            )
            return
        }

        tasksCollection.document(task.documentId)
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
        if (documentId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "El documentId de la tarea está vacío"
                )
            )
            return
        }

        tasksCollection.document(documentId)
            .delete()
            .addOnSuccessListener {
                onSuccess(documentId)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
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

    // =========================================================
    // EDICION Y MODERACION DEL BLOG
    // =========================================================

    fun updateBlogPost(
        blogId: String,
        newTitle: String,
        newContent: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection
            .document(blogId)
            .update(
                mapOf(
                    "titulo" to newTitle,
                    "contenido" to newContent
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun deleteBlogPost(
        blogId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection
            .document(blogId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun updateBlogReply(
        blogId: String,
        replyId: String,
        newText: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection
            .document(blogId)
            .collection("replies")
            .document(replyId)
            .update(
                "texto",
                newText
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun deleteBlogReply(
        blogId: String,
        replyId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        blogsCollection
            .document(blogId)
            .collection("replies")
            .document(replyId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun assignTaskToUser(
        targetUid: String,
        task: com.example.proyectodesdisint.model.Task,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        if (targetUid.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "ID de usuario destino no valido"
                )
            )
            return
        }

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        val docRef = db
            .collection("users")
            .document(targetUid)
            .collection("tareas")
            .document()

        val senderName =
            com.google.firebase.auth.FirebaseAuth
                .getInstance()
                .currentUser
                ?.displayName
                ?: "Un usuario"

        val taskWithId = task.copy(
            documentId = docRef.id,
            descripcion = (
                task.descripcion +
                "\n\n[Asignada por: $senderName]"
            ).trim()
        )

        docRef
            .set(taskWithId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}
