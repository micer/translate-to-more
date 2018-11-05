package eu.micer.translatetomore

import eu.micer.translatetomore.feature.main.vm.LanguageListViewModel
import eu.micer.translatetomore.feature.main.vm.MainViewModel
import eu.micer.translatetomore.network.YandexApi
import eu.micer.translatetomore.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * KOIN - keywords:
 * applicationContext — declare your Koin application context
 * bean — declare a singleton instance component (unique instance)
 * factory — declare a factory instance component (new instance on each demand)
 * bind — declare an assignable class/interface to the provided component
 * get — retrieve a component, for provided definition function
 */

val appModule = applicationContext {
    viewModel { MainViewModel(get()) }
    viewModel { LanguageListViewModel() }
}

val networkModule = applicationContext {
    bean { (get() as Retrofit).create(YandexApi::class.java) as YandexApi }
    bean {
        Retrofit.Builder()
                .addCallAdapterFactory(get())
                .addConverterFactory(get())
                .baseUrl(Constants.URL_YANDEX_BASE)
                .client((get() as OkHttpClient.Builder).build())
                .build() as Retrofit
    }
    bean { RxJava2CallAdapterFactory.create() as retrofit2.CallAdapter.Factory }
    bean { GsonConverterFactory.create() as retrofit2.Converter.Factory }
    bean {
        OkHttpClient.Builder()
//                .addInterceptor(AuthInterceptor())
                .addInterceptor(HttpLoggingInterceptor()
                        .apply { level = HttpLoggingInterceptor.Level.BODY }
                )
                as OkHttpClient.Builder
    }
}

// Gather all app modules
val allModules = listOf(
        appModule,
        networkModule
)