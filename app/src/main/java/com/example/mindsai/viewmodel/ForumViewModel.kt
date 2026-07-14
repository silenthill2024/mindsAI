package com.example.mindsai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsai.model.ForumPost
import com.example.mindsai.repository.ForumRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ForumViewModel(private val repository: ForumRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.fetchPosts()
        }
    }

    val posts: StateFlow<List<ForumPost>> = repository.getPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun vote(postId: Long?, delta: Int) {
        if (postId == null) return
        viewModelScope.launch {
            repository.votePost(postId, delta)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.fetchPosts()
        }
    }

    fun createPost(title: String, body: String, category: String, author: String) {
        viewModelScope.launch {
            repository.addPost(
                ForumPost(
                    title = title,
                    body = body,
                    category = category,
                    author = author
                )
            )
        }
    }
}
