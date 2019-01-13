package eu.micer.translatetomore.feature.main.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import eu.micer.translatetomore.base.BaseViewModel
import eu.micer.translatetomore.feature.main.model.LanguageItem
import eu.micer.translatetomore.network.YandexApi
import eu.micer.translatetomore.network.model.GetLanguagesResponse
import eu.micer.translatetomore.network.model.TranslationResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


class MainViewModel(private val api: YandexApi) : BaseViewModel() {
    private val languageItemsMapLiveData = MutableLiveData<Map<String, LanguageItem>>()
    private val languageListLiveData = MutableLiveData<List<String>>()
    private lateinit var editableItemCode: String

    val languageItemsMap: LiveData<Map<String, LanguageItem>>
        get() = languageItemsMapLiveData

    val languageList: LiveData<List<String>>
        get() = languageListLiveData

    fun initLanguageItemList() {
        // TODO save in Shared Prefs
        languageItemsMapLiveData.value = hashMapOf(
                "en" to LanguageItem("en", "", 0),
                "cs" to LanguageItem("cs", "", 0),
                "ru" to LanguageItem("ru", "", 0),
                "es" to LanguageItem("es", "", 0)
        )
    }

    fun getLanguageItemList(): ArrayList<LanguageItem> {
        val itemList = ArrayList<LanguageItem>()
        languageItemsMapLiveData.value?.values?.forEach { languageItem: LanguageItem ->
            itemList.add(languageItem)
        }
        itemList.sortWith(compareBy { it.languageCode })
        itemList.sortWith(compareByDescending { it.priority })
        editableItemCode = itemList.first().languageCode
        return itemList
    }

    fun getTranslation(item: LanguageItem, targetLanguageCode: String, apiKey: String) {
        val map = languageItemsMapLiveData.value
        api.getTranslation(apiKey, item.text, item.languageCode + "-$targetLanguageCode")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ translation: TranslationResponse ->
                    d { translation.text[0] }
                    map?.get(targetLanguageCode)?.text = translation.text[0]
                    languageItemsMapLiveData.value = map
                }, { t: Throwable ->
                    e(t)
                })
                .addTo(compositeDisposable)
    }

    fun loadLanguages(apiKey: String) {
        // load available languages from the server
        api.getLanguages(apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ languages: GetLanguagesResponse ->
                    // take first part of pair "en-cs" & distinct values
                    val langList = languages.dirs.map {
                        it.split("-")[0]
                    }
                    languageListLiveData.value = langList.distinct()
                }, { t: Throwable ->
                    e(t)
                })
                .addTo(compositeDisposable)
    }

    fun changeEditableLanguageItem(languageCode: String) {
        val map = languageItemsMapLiveData.value
        val editableItem: LanguageItem? = map?.get(editableItemCode)
        val newEditableItem: LanguageItem? = map?.get(languageCode)
        if (newEditableItem != null) {
            editableItem?.priority = newEditableItem.priority
            newEditableItem.priority = Int.MAX_VALUE
        }
        languageItemsMapLiveData.value = map
    }

    fun addLanguage(code: String) {
        val map = languageItemsMapLiveData.value
        (map as HashMap)[code] = LanguageItem(code, "", 0)
        languageItemsMapLiveData.value = map
    }

    fun removeLanguage(code: String) {
        val map = languageItemsMapLiveData.value
        (map as HashMap).remove(code)
        languageItemsMapLiveData.value = map
    }

    fun clearText(code: String) {
        val map = languageItemsMapLiveData.value
        map?.get(code)?.text = ""
        languageItemsMapLiveData.value = map
    }
}
