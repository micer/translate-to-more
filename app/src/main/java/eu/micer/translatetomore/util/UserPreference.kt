package eu.micer.translatetomore.util

import com.chibatching.kotpref.KotprefModel

object UserPreference : KotprefModel() {
    var accessToken by stringPref()
}