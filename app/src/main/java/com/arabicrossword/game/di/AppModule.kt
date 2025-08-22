package com.arabicrossword.game.di

import com.arabicrossword.game.data.network.LocalAIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideLocalAIService(): LocalAIService {
        return LocalAIService()
    }
}