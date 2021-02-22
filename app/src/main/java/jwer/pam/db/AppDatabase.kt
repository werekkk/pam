package jwer.pam.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jwer.pam.db.data.Converters
import jwer.pam.db.data.dao.*
import jwer.pam.db.data.entity.*

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