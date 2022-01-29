package org.michaelbel.moviemade.app.data.model

import com.google.gson.annotations.SerializedName

data class Keyword(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("name") val name: String? = null
)