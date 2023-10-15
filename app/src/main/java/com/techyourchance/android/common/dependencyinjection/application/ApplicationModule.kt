package com.techyourchance.android.common.dependencyinjection.application

import android.app.Application
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import androidx.biometric.BiometricManager
import androidx.work.WorkManager
import com.google.gson.Gson
import com.ncapdevi.fragnav.BuildConfig
import com.techyourchance.android.backgroundwork.ForegroundServiceStateManager
import com.techyourchance.android.backgroundwork.workmanager.MyWorkerManager
import com.techyourchance.android.common.Constants
import com.techyourchance.android.common.eventbus.EventBusPoster
import com.techyourchance.android.common.eventbus.EventBusSubscriber
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.settings.SettingsManager
import com.techyourchance.android.common.toasts.ToastsHelper
import com.techyourchance.android.ndk.NdkManager
import com.techyourchance.android.networking.StackoverflowApi
import com.techyourchance.android.networking.TechYourChanceApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @ApplicationScope
    fun application(): Application {
        return application
    }

    @Provides
    fun resources(application: Application): Resources {
        return application.resources
    }

    @Provides
    fun contentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @ApplicationScope
    fun eventBusPoster(): EventBusPoster {
        return EventBusPoster(EventBus.getDefault())
    }

    @Provides
    @ApplicationScope
    fun eventBusSubscriber(): EventBusSubscriber {
        return EventBusSubscriber(EventBus.getDefault())
    }

    @Provides
    fun toastsHelper(application: Application): ToastsHelper {
        return ToastsHelper(application)
    }

    @Provides
    @ApplicationScope
    fun gson(): Gson {
        return Gson()
    }

    @Provides
    @ApplicationScope
    @Named("stackoverflow")
    fun retrofitStackoverflow(settingsManager: SettingsManager): Retrofit {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor { chain ->
            try {
                val originalRequest = chain.request()
                val newRequestBuilder = originalRequest.newBuilder()
                newRequestBuilder.method(originalRequest.method, originalRequest.body)
                val authToken = settingsManager.authToken().value
                if (authToken.isNotBlank()) {
                    newRequestBuilder.header("Authorization", authToken)
                }

                chain.proceed(newRequestBuilder.build())
            } catch (e: Exception) {
                MyLogger.e("exception in OkHttp interceptor", e)
                return@addInterceptor Response.Builder()
                    .code(444)
                    .body(ResponseBody.create(null, "")) // Whatever body
                    .protocol(Protocol.HTTP_2)
                    .message("internal exception in OkHttp: ${e.message}")
                    .request(chain.request())
                    .build()
            }
        }

        return Retrofit.Builder()
            .baseUrl(Constants.STACKOVERFLOW_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClientBuilder.build())
            .build()
    }


    @Provides
    @ApplicationScope
    @Named("techyourchance")
    fun retrofitTechyourchance(settingsManager: SettingsManager): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        } else {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(Constants.TECHYOURCHANCE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides
    @ApplicationScope
    fun stackOverflowApi(@Named("stackoverflow") retrofit: Retrofit): StackoverflowApi {
        return retrofit.create(StackoverflowApi::class.java)
    }

    @Provides
    @ApplicationScope
    fun techyourchanceApi(@Named("techyourchance") retrofit: Retrofit): TechYourChanceApi {
        return retrofit.create(TechYourChanceApi::class.java)
    }

    @Provides
    fun biometricManager(application: Application): BiometricManager {
        return BiometricManager.from(application)
    }

    @Provides
    @ApplicationScope
    fun ndkManager(): NdkManager {
        return NdkManager()
    }

    @Provides
    @ApplicationScope
    fun foregroundServiceStateManager(): ForegroundServiceStateManager {
        return ForegroundServiceStateManager()
    }

    @Provides
    fun workManager(application: Application): WorkManager {
        return WorkManager.getInstance(application)
    }

    @Provides
    @ApplicationScope
    fun myWorkerManager(workManager: WorkManager, settingsManager: SettingsManager): MyWorkerManager {
        return MyWorkerManager(workManager, settingsManager)
    }

    @Provides
    fun notificationManager(application: Application): NotificationManager {
        return application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}