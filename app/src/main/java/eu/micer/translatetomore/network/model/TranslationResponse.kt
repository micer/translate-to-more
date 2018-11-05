package eu.micer.translatetomore.network.model

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
        @SerializedName("code")
        val code: Int,
        @SerializedName("lang")
        val lang: String,
        @SerializedName("text")
        val text: List<String>
)