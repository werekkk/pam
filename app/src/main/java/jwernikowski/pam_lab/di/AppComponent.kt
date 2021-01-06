package jwernikowski.pam_lab.di

import android.app.Application
import dagger.Component
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.db.AppDatabase
import jwernikowski.pam_lab.db.data.PracticeEntryDao
import jwernikowski.pam_lab.db.data.SongDao
import jwernikowski.pam_lab.db.data.rhythm.RhythmDao
import jwernikowski.pam_lab.db.data.rhythm.RhythmLineDao
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.db.repository.RhythmLineRepository
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.db.repository.SongRepository
import jwernikowski.pam_lab.sound.SoundPlayer
import jwernikowski.pam_lab.ui.metronome.MetronomeViewModel
import jwernikowski.pam_lab.ui.rhythm_details.RhythmDetailsActivity
import jwernikowski.pam_lab.ui.rhythm_details.RhythmDetailsViewModel
import jwernikowski.pam_lab.ui.rhythms.RhythmsViewModel
import jwernikowski.pam_lab.ui.section_details.SectionDetailsViewModel
import jwernikowski.pam_lab.ui.song_details.NewSectionViewModel
import jwernikowski.pam_lab.ui.song_details.SongDetailsViewModel
import jwernikowski.pam_lab.ui.song_practice.MetronomePracticeViewModel
import jwernikowski.pam_lab.ui.songs.NewSongViewModel
import jwernikowski.pam_lab.ui.songs.SongsViewModel
import jwernikowski.pam_lab.utils.ErrorText
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, RoomModule::class]
)
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(songsViewModel: SongsViewModel)
    fun inject(songDetailsViewModel: SongDetailsViewModel)
    fun inject(errorText: ErrorText)
    fun inject(metronomePracticeViewModel: MetronomePracticeViewModel)
    fun inject(rhythmDetailsViewModel: RhythmDetailsViewModel)
    fun inject(rhythmDetailsActivity: RhythmDetailsActivity)
    fun inject(rhythmsViewModel: RhythmsViewModel)
    fun inject(metronomeViewModel: MetronomeViewModel)
    fun inject(newSongViewModel: NewSongViewModel)
    fun inject(newSectionViewModel: NewSectionViewModel)
    fun inject(sectionDetailsViewModel: SectionDetailsViewModel)

    val songDao: SongDao
    val songRepository: SongRepository

    val practiceEntryDao: PracticeEntryDao
    val practiceEntryRepository: PracticeEntryRepository

    val rhythmDao: RhythmDao
    val rhythmRepository: RhythmRepository

    val rhythmLineDao: RhythmLineDao
    val rhythmLineRepository: RhythmLineRepository

    val database: AppDatabase

    val application: Application

    val soundPlayer: SoundPlayer
}