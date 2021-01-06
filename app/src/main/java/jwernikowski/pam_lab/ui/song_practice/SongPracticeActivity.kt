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
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.Song

class SongPracticeActivity : AppCompatActivity() {

    companion object {
        val SONG_TAG = "song"
        val SECTION_TAG = "section"
        val BPM_TAG = "bpm"
    }

    private lateinit var song: Song
    private lateinit var section: Section
    private var bpm: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_practice)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        song = intent.extras?.get(SONG_TAG) as Song
        section = intent.extras?.get(SECTION_TAG) as Section
        loadSong(song, section)

    }

    override fun onStart() {
        super.onStart()
        val mf = supportFragmentManager.findFragmentById(R.id.metronome_practice) as MetronomePracticeFragment
        mf.setSong(song)
        mf.setSection(section)
    }

    private fun loadSong(newSong: Song, newSection: Section) {
        supportActionBar?.title = createTitle(newSong, newSection)
    }

    private fun createTitle(newSong: Song, newSection: Section): String {
        return if(newSong.hasSections) "${newSong.name} - ${newSection.name}"
            else newSong.name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
