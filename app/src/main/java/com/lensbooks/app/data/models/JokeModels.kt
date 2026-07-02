package com.lensbooks.app.data.models

import com.google.gson.annotations.SerializedName

data class JokeResponse(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("setup")
    val setup: String = "",
    @SerializedName("punchline")
    val punchline: String = "",
    @SerializedName("joke")
    val joke: String = ""
)

data class RandomJokeResponse(
    @SerializedName("value")
    val value: String = ""
)