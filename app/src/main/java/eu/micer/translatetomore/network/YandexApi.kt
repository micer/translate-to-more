package eu.micer.translatetomore.network

import eu.micer.translatetomore.network.model.GetLanguagesResponse
import eu.micer.translatetomore.network.model.TranslationResponse
import eu.micer.translatetomore.util.Constants
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexApi {

    @GET(Constants.URL_YANDEX_TRANSLATE)
    fun getTranslation(@Query("key") apiKey: String,
                       @Query("text") text: String,
                       @Query("lang") lang: String): Single<TranslationResponse>

    @GET(Constants.URL_YANDEX_LANGUAGES)
    fun getLanguages(@Query("key") apiKey: String) : Single<GetLanguagesResponse>

}