package jwernikowski.pam_lab.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jwernikowski.pam_lab.db.data.Converters
import jwernikowski.pam_lab.db.data.dao.*
import jwernikowski.pam_lab.db.data.entity.*

@Database(entities = [
    Song::class,
    Section::class,
    PracticeEntry::class,
    Rhythm::class,
    RhythmLine::class],
    version = 4,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun sectionDao(): SectionDao
    abstract fun practiceEntryDao(): PracticeEntryDao
    abstract fun rhythmDao(): RhythmDao
    abstract fun rhythmLineDao(): RhythmLineDao
}