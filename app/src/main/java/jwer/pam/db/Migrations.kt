package jwer.pam.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add columns
        database.execSQL("ALTER TABLE Song ADD COLUMN previousProgress INTEGER DEFAULT 0 NOT NULL")
        // Add columns and foreign key
        database.execSQL("""
            CREATE TABLE `Section_new` (
            `sectionId` INTEGER NOT NULL,
            `songId` INTEGER NOT NULL,
            `name` TEXT NOT NULL,
            `initialTempo` INTEGER NOT NULL,
            `goalTempo` INTEGER NOT NULL,
            `order` INTEGER NOT NULL,
            `previousProgress` INTEGER NOT NULL,
            `previousBpm` INTEGER NOT NULL,
            `previousRhythmId` INTEGER,
            PRIMARY KEY (`sectionId`),
            FOREIGN KEY (`songId`) REFERENCES `Song`(`songId`) ON UPDATE NO ACTION ON DELETE CASCADE,
            FOREIGN KEY (`previousRhythmId`) REFERENCES `Rhythm`(`rhythmId`) ON UPDATE NO ACTION ON DELETE SET NULL
        )
        """.trimIndent())
        database.execSQL("""
            INSERT INTO Section_new (sectionId, songId, name, initialTempo, goalTempo, `order`, previousProgress, previousBpm, previousRhythmId)
            SELECT sectionId, songId, name, initialTempo, goalTempo, `order`, 0 AS previousProgress, initialTempo, NULL AS previousRhythmId FROM Section
        """.trimIndent())
        database.execSQL("DROP TABLE Section")
        database.execSQL("ALTER TABLE Section_new RENAME TO Section")
    }
}