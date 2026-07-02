package com.lensbooks.app.data.repository

import android.util.Log
import com.lensbooks.app.data.api.JokeApiService
import com.lensbooks.app.data.models.JokeResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JokeRepository {
    private val jokeApi: JokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://v2.jokeapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JokeApiService::class.java)
    }

    suspend fun getRandomJoke(): Result<JokeResponse> {
        return try {
            val joke = jokeApi.getRandomJoke()
            Log.d("JokeRepository", "Fetched joke: ${joke.joke}")
            Result.success(joke)
        } catch (e: Exception) {
            Log.e("JokeRepository", "Error fetching joke", e)
            Result.failure(e)
        }
    }
}