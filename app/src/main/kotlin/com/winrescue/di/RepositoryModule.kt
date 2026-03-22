package com.winrescue.di

import com.winrescue.data.repository.ScriptRepository
import com.winrescue.data.repository.ScriptRepositoryImpl
import com.winrescue.data.settings.SettingsRepository
import com.winrescue.data.settings.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScriptRepository(
        impl: ScriptRepositoryImpl
    ): ScriptRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
