package jwernikowski.pam_lab.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jwernikowski.pam_lab.db.data.*
import jwernikowski.pam_lab.db.data.rhythm.Rhythm
import jwernikowski.pam_lab.db.data.rhythm.RhythmDao
import jwernikowski.pam_lab.db.data.rhythm.RhythmLine
import jwernikowski.pam_lab.db.data.rhythm.RhythmLineDao
import jwernikowski.pam_lab.db.data.Converters

@Database(entities = [
    Song::class,
    Section::class,
    PracticeEntry::class,
    Rhythm::class,
    RhythmLine::class],
    version = 3,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun sectionDao(): SectionDao
    abstract fun practiceEntryDao(): PracticeEntryDao
    abstract fun rhythmDao(): RhythmDao
    abstract fun rhythmLineDao(): RhythmLineDao
}