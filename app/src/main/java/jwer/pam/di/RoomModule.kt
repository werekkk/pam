package jwer.pam.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import jwer.pam.db.AppDatabase
import jwer.pam.db.MIGRATION_3_4
import jwer.pam.db.data.dao.PracticeEntryDao
import jwer.pam.db.data.dao.SectionDao
import jwer.pam.db.data.dao.SongDao
import jwer.pam.db.data.dao.RhythmDao
import jwer.pam.db.data.dao.RhythmLineDao
import jwer.pam.db.repository.*
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {

    val database: AppDatabase =
        Room.databaseBuilder(application, AppDatabase::class.java, "pam-db")
            .addMigrations(MIGRATION_3_4)
            .build()

    @Singleton
    @Provides
    fun providesDatabase(): AppDatabase = database

    @Singleton
    @Provides
    fun providesSongDao(database: AppDatabase): SongDao = database.songDao()

    @Singleton
    @Provides
    fun providesSongRepository(songDao: SongDao, sectionDao: SectionDao, database: AppDatabase): SongRepository = SongRepository(songDao, sectionDao, database)

    @Singleton
    @Provides
    fun providesSectionDao(database: AppDatabase): SectionDao = database.sectionDao()

    @Singleton
    @Provides
    fun providesSectionRepository(sectionDao: SectionDao): SectionRepository = SectionRepository(sectionDao)

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