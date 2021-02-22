package jwer.pam.ui.fragment.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jwer.pam.ui.activity.MainActivity
import jwer.pam.db.data.entity.Song
import jwer.pam.db.repository.SongRepository
import javax.inject.Inject

class SongsViewModel : ViewModel() {

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var songsRepository: SongRepository

    val songsLoaded = MutableLiveData(false)
    val allSongs: LiveData<List<Song>> =
        Transformations.map(songsRepository.getAll()) {
            songsLoaded.postValue(true)
            it
        }

    fun delete(song: Song) = songsRepository.delete(song)

}
