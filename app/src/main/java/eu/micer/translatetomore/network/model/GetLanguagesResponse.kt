package eu.micer.translatetomore.network.model

import com.google.gson.annotations.SerializedName

data class GetLanguagesResponse(
        @SerializedName("dirs")
        val dirs: List<String>
)