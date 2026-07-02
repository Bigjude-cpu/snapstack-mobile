package com.lensbooks.app.data.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lensbooks.app.data.models.JokeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JokeViewModel : ViewModel() {

    private val jokeRepository = JokeRepository()

    private val _jokeState = MutableStateFlow<JokeState>(JokeState.Loading)
    val jokeState: StateFlow<JokeState> = _jokeState.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    sealed class JokeState {
        object Loading : JokeState()
        data class Success(val joke: JokeResponse) : JokeState()
        data class Error(val message: String) : JokeState()
    }

    fun fetchJoke() {
        _jokeState.value = JokeState.Loading
        _isLiked.value = false
        viewModelScope.launch {
            try {
                val result = jokeRepository.getRandomJoke()
                result.onSuccess { joke ->
                    _jokeState.value = JokeState.Success(joke)
                    Log.d("JokeViewModel", "Joke loaded: ${joke.joke}")
                }
                result.onFailure { error ->
                    _jokeState.value = JokeState.Error(error.localizedMessage ?: "Unknown error occurred")
                    Log.e("JokeViewModel", "Error: ${error.localizedMessage}")
                }
            } catch (e: Exception) {
                _jokeState.value = JokeState.Error(e.localizedMessage ?: "Unknown error")
                Log.e("JokeViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun toggleLike() {
        _isLiked.value = !_isLiked.value
    }
}