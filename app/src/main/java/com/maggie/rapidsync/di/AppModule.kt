package com.maggie.rapidsync.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.model.Constants
import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.repo.LoginRepository
import com.maggie.rapidsync.repo.ParkingPlanRepository
import com.maggie.rapidsync.repo.ParkingSlotRepository
import com.maggie.rapidsync.repo.UniversityEventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }


    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideLocalDataStore(sharedPreferences: SharedPreferences, gson: Gson): LocalDataStore {
        return LocalDataStore(sharedPreferences, gson)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(apiService: ApiService): LoginRepository {
        return LoginRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideParkingSlotRepository(apiService: ApiService): ParkingSlotRepository {
        return ParkingSlotRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideParkingPlanRepository(apiService: ApiService): ParkingPlanRepository {
        return ParkingPlanRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideUniversityEventRepository(apiService: ApiService): UniversityEventRepository {
        return UniversityEventRepository(apiService)
    }


}