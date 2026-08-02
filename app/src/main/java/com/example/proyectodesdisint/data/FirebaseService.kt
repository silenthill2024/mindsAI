package com.example.proyectodesdisint.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.example.mindsai.model.Task
import com.example.proyectodesdisint.model.BlogPost
import com.example.proyectodesdisint.model.BlogReply
import com.example.proyectodesdisint.model.MaterialApoyo
import com.example.proyectodesdisint.model.User
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val tasksCollection = db.collection("tasks")
    private val blogsCollection = db.collection("blogs")
    private val usersCollection = db.collection("usuarios")
    private val materialCollection = db.collection("material_apoyo")

    fun addTask(task: Task) {
        val docRef = tasksCollection.document()
        val taskWithId = task.copy(documentId = docRef.id)
        docRef.set(taskWithId)
    }

    fun listenTasks(onResult: (List<Task>) -> Unit) {
        tasksCollection.addSnapshotListener { snapshot, _ ->
            val tasks = snapshot?.mapNotNull { doc ->
                val task = doc.toObject(Task::class.java)
                task.documentId = doc.id
                task
            } ?: emptyList()
            onResult(tasks)
        }
    }

    fun updateTask(task: Task) {
        tasksCollection.document(task.documentId).update(
            mapOf(
                "titulo" to task.titulo,
                "descripcion" to task.descripcion,
                "fecha" to task.fecha,
                "hora" to task.hora,
                "completado" to task.completado,
                "completionTime" to task.completionTime
            )
        )
    }

    fun deleteTask(documentId: String) {
        tasksCollection.document(documentId).delete()
    }

    // --- BLOG FIREBASE LOGIC ---
    fun addBlogPost(post: BlogPost) {
        val docRef = blogsCollection.document()
        val postWithId = post.copy(id = docRef.id)
        docRef.set(postWithId)
    }

    fun listenBlogs(onResult: (List<BlogPost>) -> Unit) {
        blogsCollection.orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val posts = snapshot?.mapNotNull { doc ->
                    doc.toObject(BlogPost::class.java).apply { id = doc.id }
                } ?: emptyList()
                onResult(posts)
            }
    }

    fun addBlogReply(blogId: String, reply: BlogReply) {
        blogsCollection.document(blogId).collection("replies").add(reply)
    }

    fun listenBlogReplies(blogId: String, onResult: (List<BlogReply>) -> Unit) {
        blogsCollection.document(blogId).collection("replies")
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val replies = snapshot?.mapNotNull { doc ->
                    doc.toObject(BlogReply::class.java).apply { id = doc.id }
                } ?: emptyList()
                onResult(replies)
            }
    }

    suspend fun getUserProfile(uid: String): User? {
        return try {
            usersCollection.document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // --- MATERIAL DE APOYO ---
    suspend fun addMaterial(material: MaterialApoyo): Boolean {
        return try {
            val docRef = materialCollection.document()
            val materialWithId = material.copy(id = docRef.id)
            docRef.set(materialWithId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun listenMaterial(onResult: (List<MaterialApoyo>) -> Unit) {
        materialCollection.orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val materials = snapshot?.mapNotNull { doc ->
                    doc.toObject(MaterialApoyo::class.java).apply { id = doc.id }
                } ?: emptyList()
                onResult(materials)
            }
    }

    fun deleteMaterial(materialId: String) {
        materialCollection.document(materialId).delete()
    }

    suspend fun uploadFile(uri: Uri, fileName: String): String? {
        return try {
            // Añadimos un timestamp para evitar colisiones de nombres y asegurar que el archivo se suba como nuevo
            val uniqueName = "${System.currentTimeMillis()}_$fileName"
            val fileRef = storage.reference.child("material/$uniqueName")
            
            fileRef.putFile(uri).await()
            fileRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- USER MANAGEMENT (ADMIN) ---
    fun listenAllUsers(onResult: (List<User>) -> Unit) {
        usersCollection.addSnapshotListener { snapshot, _ ->
            val users = snapshot?.mapNotNull { doc -> doc.toObject(User::class.java) } ?: emptyList()
            onResult(users)
        }
    }

    fun updateUserRole(uid: String, newRole: String) {
        usersCollection.document(uid).update("role", newRole)
    }
}
