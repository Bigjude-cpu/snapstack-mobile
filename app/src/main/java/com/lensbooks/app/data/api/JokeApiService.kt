package com.lensbooks.app.data.api

import com.lensbooks.app.data.models.JokeResponse
import retrofit2.http.GET

interface JokeApiService {
    @GET("random_joke")
    suspend fun getRandomJoke(): JokeResponse

    @GET("jokes/random")
    suspend fun getRandomJokeAlternate(): JokeResponse
}

interface RandomJokeApiService {
    @GET("jokes/random")
    suspend fun getRandomJoke(): com.lensbooks.app.data.models.RandomJokeResponse
}