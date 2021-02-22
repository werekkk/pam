package jwer.pam.di

import android.app.Application
import dagger.Module
import dagger.Provides
import jwer.pam.db.repository.SharedPreferencesRepository
import jwer.pam.sound.SoundPlayer
import javax.inject.Singleton

@Module
class AppModule (val application: Application){

    @Singleton
    @Provides
    fun providesApplication(): Application = application

    @Singleton
    @Provides
    fun providesSoundPlayer(): SoundPlayer = SoundPlayer(application)

    @Singleton
    @Provides
    fun providesSharedPreferencesRepository(): SharedPreferencesRepository = SharedPreferencesRepository(application)
}