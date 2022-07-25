package com.okihita.accenture.di

import android.content.Context
import androidx.room.Room
import com.okihita.accenture.data.local.GitHubUserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ) = Room
        .databaseBuilder(
            context,
            GitHubUserDatabase::class.java,
            "github_users_db"
        )
        .fallbackToDestructiveMigration()
        .build()
}