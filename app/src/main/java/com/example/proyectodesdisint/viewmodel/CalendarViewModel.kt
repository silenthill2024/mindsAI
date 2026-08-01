package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectodesdisint.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CalendarViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    fun addEvent(event: Event) {
        val current = _events.value.toMutableList()
        current.add(event)
        _events.value = current
    }
}
