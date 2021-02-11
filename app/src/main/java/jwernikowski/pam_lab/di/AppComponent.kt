package jwernikowski.pam_lab.di

import android.app.Application
import dagger.Component
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.AppDatabase
import jwernikowski.pam_lab.db.data.dao.PracticeEntryDao
import jwernikowski.pam_lab.db.data.dao.SongDao
import jwernikowski.pam_lab.db.data.dao.RhythmDao
import jwernikowski.pam_lab.db.data.dao.RhythmLineDao
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.db.repository.RhythmLineRepository
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.db.repository.SongRepository
import jwernikowski.pam_lab.sound.SoundPlayer
import jwernikowski.pam_lab.ui.fragment.metronome.MetronomeViewModel
import jwernikowski.pam_lab.ui.activity.rhythm_details.RhythmDetailsActivity
import jwernikowski.pam_lab.ui.activity.rhythm_details.RhythmDetailsViewModel
import jwernikowski.pam_lab.ui.fragment.rhythms.RhythmsViewModel
import jwernikowski.pam_lab.ui.dialog.section_details.SectionDetailsViewModel
import jwernikowski.pam_lab.ui.dialog.section_new.NewSectionViewModel
import jwernikowski.pam_lab.ui.activity.song_details.SongDetailsViewModel
import jwernikowski.pam_lab.ui.dialog.section_edit.EditSectionViewModel
import jwernikowski.pam_lab.ui.fragment.metronome_practice.MetronomePracticeViewModel
import jwernikowski.pam_lab.ui.dialog.song_new.NewSongViewModel
import jwernikowski.pam_lab.ui.fragment.songs.SongsViewModel
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
    fun inject(editSectionViewModel: EditSectionViewModel)

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