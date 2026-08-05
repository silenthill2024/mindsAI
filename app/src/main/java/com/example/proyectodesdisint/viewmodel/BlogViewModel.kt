package com.example.proyectodesdisint.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectodesdisint.data.BlogDatabase
import com.example.proyectodesdisint.data.BlogEntity
import com.example.proyectodesdisint.data.ReplyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BlogViewModel(context: Context) : ViewModel() {
    private val db = BlogDatabase.getDatabase(context)
    private val dao = db.blogDao()

    val allBlogs: Flow<List<BlogEntity>> = dao.getAllBlogs()

    fun addBlog(titulo: String, contenido: String) {
        viewModelScope.launch {
            dao.insertBlog(BlogEntity(titulo = titulo, contenido = contenido))
        }
    }

    fun addReply(blogId: Int, texto: String) {
        viewModelScope.launch {
            dao.insertReply(ReplyEntity(blogId = blogId, texto = texto))
        }
    }

    fun getReplies(blogId: Int): Flow<List<ReplyEntity>> = dao.getRepliesForBlog(blogId)
}

class BlogViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
