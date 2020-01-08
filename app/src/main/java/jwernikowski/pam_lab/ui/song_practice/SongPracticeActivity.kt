package jwernikowski.pam_lab.ui.song_practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.MenuItem
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.Song

class SongPracticeActivity : AppCompatActivity() {

    companion object {
        val SONG_TAG = "song"
        val BPM_TAG = "bpm"
    }

    lateinit private var song: Song
    private var bpm: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_practice)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        song = intent.extras?.get(SONG_TAG) as Song
        bpm = intent.extras?.getInt(BPM_TAG, song.initialTempo)!!
        loadSong(song)

    }

    override fun onStart() {
        super.onStart()
        val mf = supportFragmentManager.findFragmentById(R.id.metronome_practice) as MetronomePracticeFragment
        mf.setSong(song)
        mf.setBpm(bpm)

    }

    private fun loadSong(newSong: Song) {
        supportActionBar?.title = newSong.name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
