package jwernikowski.pam_lab.ui.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongsViewModel : ViewModel() {

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var songsRepository: SongRepository

    private var allSongs: LiveData<List<Song>> = songsRepository.getAll()

    fun delete(song: Song) = songsRepository.delete(song)

    fun getAllSongs(): LiveData<List<Song>> = allSongs

}
