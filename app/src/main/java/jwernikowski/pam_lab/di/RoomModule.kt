package jwernikowski.pam_lab.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import jwernikowski.pam_lab.db.AppDatabase
import jwernikowski.pam_lab.db.data.PracticeEntryDao
import jwernikowski.pam_lab.db.data.SongDao
import jwernikowski.pam_lab.db.data.rhythm.RhythmDao
import jwernikowski.pam_lab.db.data.rhythm.RhythmLineDao
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.db.repository.RhythmLineRepository
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.db.repository.SongRepository
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {

    val database: AppDatabase =
        Room.databaseBuilder(application, AppDatabase::class.java, "pam-db").build()

    @Singleton
    @Provides
    fun providesDatabase(): AppDatabase = database

    @Singleton
    @Provides
    fun providesSongDao(database: AppDatabase): SongDao = database.songDao()

    @Singleton
    @Provides
    fun providesSongRepository(songDao: SongDao): SongRepository = SongRepository(songDao)

    @Singleton
    @Provides
    fun providesPracticeEntryDao(database: AppDatabase): PracticeEntryDao = database.practiceEntryDao()

    @Singleton
    @Provides
    fun providesPracticeEntryRepository(dao: PracticeEntryDao): PracticeEntryRepository = PracticeEntryRepository(dao)

    @Singleton
    @Provides
    fun providesRhythmDao(database: AppDatabase): RhythmDao = database.rhythmDao()

    @Singleton
    @Provides
    fun providesRhythmRepository(rhythmDao: RhythmDao): RhythmRepository = RhythmRepository(rhythmDao)

    @Singleton
    @Provides
    fun providesRhythmLineDao(database: AppDatabase): RhythmLineDao = database.rhythmLineDao()

    @Singleton
    @Provides
    fun providesRhythmLineRepository(rhythmLineDao: RhythmLineDao): RhythmLineRepository = RhythmLineRepository(rhythmLineDao)

}