package jwernikowski.pam_lab.ui.activity.song_practice

import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.ActivitySongPracticeBinding
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.ui.fragment.metronome_practice.MetronomePracticeFragment

class SongPracticeActivity : AppCompatActivity() {

    companion object {
        val SONG_TAG = "song"
        val SECTION_TAG = "section"
        val BPM_TAG = "bpm"
    }

    private lateinit var binding: ActivitySongPracticeBinding
    private lateinit var viewModel: SongPracticeViewModel

    private lateinit var song: Song
    private lateinit var section: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC

        viewModel = ViewModelProvider(this).get(SongPracticeViewModel::class.java)

        binding = ActivitySongPracticeBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setContentView(binding.root)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.practice)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        song = intent.extras?.get(SONG_TAG) as Song
        section = intent.extras?.get(SECTION_TAG) as Section

        loadSong(song, section)
        observeViewModel()

    }

    private fun loadSong(newSong: Song, newSection: Section) {
        viewModel.setState(newSong, newSection)
    }

    private fun observeViewModel() {
        val mf = supportFragmentManager.findFragmentById(R.id.metronome_practice) as MetronomePracticeFragment
        viewModel.song.observe(this) { mf.setSong(it) }
        viewModel.currentSection.observe(this) { mf.setSection(it)}
        viewModel.sections.observe(this) {}
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
