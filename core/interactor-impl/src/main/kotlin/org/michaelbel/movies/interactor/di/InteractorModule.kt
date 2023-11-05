package org.michaelbel.movies.interactor.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.michaelbel.movies.interactor.AccountInteractor
import org.michaelbel.movies.interactor.AccountInteractorImpl
import org.michaelbel.movies.interactor.AuthenticationInteractor
import org.michaelbel.movies.interactor.AuthenticationInteractorImpl
import org.michaelbel.movies.interactor.ImageInteractor
import org.michaelbel.movies.interactor.ImageInteractorImpl
import org.michaelbel.movies.interactor.MovieInteractor
import org.michaelbel.movies.interactor.MovieInteractorImpl
import org.michaelbel.movies.interactor.SettingsInteractor
import org.michaelbel.movies.interactor.SettingsInteractorImpl

@Module
@InstallIn(SingletonComponent::class)
internal interface InteractorModule {

    @Binds
    @Singleton
    fun provideMovieInteractor(
        interactor: MovieInteractorImpl
    ): MovieInteractor

    @Binds
    @Singleton
    fun provideImageInteractor(
        interactor: ImageInteractorImpl
    ): ImageInteractor

    @Binds
    @Singleton
    fun provideAuthenticationInteractor(
        interactor: AuthenticationInteractorImpl
    ): AuthenticationInteractor

    @Binds
    @Singleton
    fun provideAccountInteractor(
        interactor: AccountInteractorImpl
    ): AccountInteractor

    @Binds
    @Singleton
    fun provideSettingsInteractor(
        interactor: SettingsInteractorImpl
    ): SettingsInteractor
}