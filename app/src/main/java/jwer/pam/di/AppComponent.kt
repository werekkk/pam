package jwer.pam.di

import android.app.Application
import dagger.Component
import jwer.pam.ui.activity.MainActivity
import jwer.pam.db.AppDatabase
import jwer.pam.db.data.dao.PracticeEntryDao
import jwer.pam.db.data.dao.SongDao
import jwer.pam.db.data.dao.RhythmDao
import jwer.pam.db.data.dao.RhythmLineDao
import jwer.pam.db.repository.PracticeEntryRepository
import jwer.pam.db.repository.RhythmLineRepository
import jwer.pam.db.repository.RhythmRepository
import jwer.pam.db.repository.SongRepository
import jwer.pam.sound.SoundPlayer
import jwer.pam.ui.fragment.metronome.MetronomeViewModel
import jwer.pam.ui.activity.rhythm_details.RhythmDetailsActivity
import jwer.pam.ui.activity.rhythm_details.RhythmDetailsViewModel
import jwer.pam.ui.fragment.rhythms.RhythmsViewModel
import jwer.pam.ui.dialog.section_details.SectionDetailsViewModel
import jwer.pam.ui.dialog.section_new.NewSectionViewModel
import jwer.pam.ui.activity.song_details.SongDetailsViewModel
import jwer.pam.ui.activity.song_practice.SongPracticeViewModel
import jwer.pam.ui.dialog.rhythm_change.ChangeRhythmViewModel
import jwer.pam.ui.dialog.section_edit.EditSectionViewModel
import jwer.pam.ui.dialog.song_edit.EditSongViewModel
import jwer.pam.ui.fragment.metronome_practice.MetronomePracticeViewModel
import jwer.pam.ui.dialog.song_new.NewSongViewModel
import jwer.pam.ui.fragment.shared.MetronomeViewViewModel
import jwer.pam.ui.fragment.songs.SongsViewModel
import jwer.pam.utils.ErrorText
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
    fun inject(editSongViewModel: EditSongViewModel)
    fun inject(songPracticeViewModel: SongPracticeViewModel)
    fun inject(changeRhythmViewModel: ChangeRhythmViewModel)
    fun inject(metronomeViewViewModel: MetronomeViewViewModel)

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