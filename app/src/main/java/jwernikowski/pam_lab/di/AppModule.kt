package jwernikowski.pam_lab.di

import android.app.Application
import dagger.Component
import dagger.Module
import dagger.Provides
import jwernikowski.pam_lab.sound.SoundPlayer
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AppModule (val application: Application){

    @Singleton
    @Provides
    fun providesApplication(): Application = application

    @Singleton
    @Provides
    fun providesSoundPlayer(): SoundPlayer = SoundPlayer(application)
}