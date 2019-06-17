package com.davlop.skifun.di

import android.app.Application
import android.arch.persistence.room.Room
import com.davlop.skifun.data.source.local.AppDatabase
import com.davlop.skifun.data.source.local.room.WeatherItemsDao
import com.davlop.skifun.data.source.remote.service.GooglePlacesAPI
import com.davlop.skifun.data.source.remote.service.OpenWeatherAPI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideDatabase(app: Application) : AppDatabase =
            Room.databaseBuilder(app, AppDatabase::class.java,"weather-db")
                    .build()

    @Singleton
    @Provides
    fun provideWeatherItemsDao(database: AppDatabase) : WeatherItemsDao = database.weatherItemsDao()


    @Singleton
    @Provides
    fun provideOpenWeatherApi() : OpenWeatherAPI {
        // adding interceptor to log responses
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        return Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .client(client)
                .build()
                .create(OpenWeatherAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideGooglePlacesApi() : GooglePlacesAPI {
        // adding interceptor to log responses
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        return Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .client(client)
                .build()
                .create(GooglePlacesAPI::class.java)
    }

}