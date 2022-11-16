package com.boosters.promise.data.promise.di

import android.content.Context
import androidx.room.Room
import com.boosters.promise.data.database.PromiseDatabase
import com.boosters.promise.data.database.UserTypeConverter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    private const val PROMISE_DATABASE_NAME = "Promise_Database"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun providePromiseLocalDatabase(
        @ApplicationContext appContext: Context,
        gson: Gson
    ): PromiseDatabase {
        return Room.databaseBuilder(appContext, PromiseDatabase::class.java, PROMISE_DATABASE_NAME)
            .addTypeConverter(UserTypeConverter(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providePromiseRemoteDatabase(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}