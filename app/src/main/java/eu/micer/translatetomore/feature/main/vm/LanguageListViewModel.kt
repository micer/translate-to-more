package eu.micer.translatetomore.feature.main.vm

import eu.micer.translatetomore.base.BaseViewModel

class LanguageListViewModel : BaseViewModel() {

    // Map of "language code" -> "is already used?".
    private val languageCodesMap = hashMapOf(
            "EN" to false,
            "CS" to false,
            "RU" to false,
            "SP" to false
    )

    fun getUnusedLanguageCodes(): Array<String> {
        return languageCodesMap
                .filterValues { !it }
                .keys
                .sorted()
                .toTypedArray()
    }
}