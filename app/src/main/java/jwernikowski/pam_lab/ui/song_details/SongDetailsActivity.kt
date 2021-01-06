package jwernikowski.pam_lab.ui.song_details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.ActivitySongDetailsBinding
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.ui.section_details.SectionDetailsDialogFragment
import jwernikowski.pam_lab.ui.song_practice.SongPracticeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongDetailsActivity : AppCompatActivity() {

    companion object {
        val SONG_TAG = "song"
    }

    private lateinit var binding: ActivitySongDetailsBinding
    private lateinit var viewModel: SongDetailsViewModel

    private lateinit var song: Song

    private lateinit var practiceEntriesRecyclerView: RecyclerView
    private lateinit var practiceEntryViewManager: LinearLayoutManager
    private lateinit var practiceEntryViewAdapter: PracticeEntryAdapter
    private lateinit var sectionsRecyclerView: RecyclerView
    private lateinit var sectionViewManager: LinearLayoutManager
    private lateinit var sectionsViewAdapter: SectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SongDetailsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)


//        viewModel.practiceEntries.observe(this, Observer { list -> run{
//            viewAdapter.setEntries(list)
//            daysPracticedText.text = PracticeEntry.calculateDaysPracticed(list).toString()
//            viewModel.song.value!!.let {
//                progressText.text = (PracticeEntry.calculateProgress(list, it.initialTempo, it.goalTempo) * 100).toInt().toString() + "%"
//            }
//        } }) TODO("calculate progress based on sections")

        song = intent.extras?.get(SONG_TAG) as Song
        loadSong()

        initRecyclerViews()
        binding.practiceNow.setOnClickListener { handlePracticeNow() }
        binding.newSection.setOnClickListener { displayNewSectionDialog() }
    }

    private fun initRecyclerViews() {
        practiceEntryViewManager = LinearLayoutManager(this)
        practiceEntryViewAdapter = PracticeEntryAdapter {}
        sectionViewManager = LinearLayoutManager(this)
        sectionsViewAdapter = SectionAdapter({
            handlePracticeNow(it)
        }, {
            handleShowSectionDetails(it)
        })

        viewModel.practiceEntries.observe(this, Observer { practiceEntryViewAdapter.setEntries(it) })
        viewModel.sections.observe(this, Observer { sectionsViewAdapter.setSections(it) })

        practiceEntriesRecyclerView = binding.practiceEntriesRecycler.apply {
            setHasFixedSize(true)
            layoutManager = practiceEntryViewManager
            adapter = practiceEntryViewAdapter
            val touchHelper = ItemTouchHelper(EntrySwipeToDeleteCallback(practiceEntryViewAdapter, {handleSwipeToDelete(it)}, context))
            touchHelper.attachToRecyclerView(this)
            isNestedScrollingEnabled = false
        }
        sectionsRecyclerView = binding.sectionsRecycler.apply {
            setHasFixedSize(true)
            layoutManager = sectionViewManager
            adapter = sectionsViewAdapter
            isNestedScrollingEnabled = false
        }

        practiceEntriesRecyclerView.addItemDecoration(DividerItemDecoration(practiceEntriesRecyclerView.context, practiceEntryViewManager.orientation))
        sectionsRecyclerView.addItemDecoration(DividerItemDecoration(sectionsRecyclerView.context, sectionViewManager.orientation))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.song_details_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.rename -> displayEditNameDialog()
            R.id.delete -> displayDeleteSongDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadSong() {
        viewModel.song.postValue(song)
        viewModel.song.observe(this, Observer { song -> supportActionBar?.title = song.name })
    }

    private fun displayEditNameDialog() {
        val dialog = EditSongNameDialogFragment(viewModel)
        dialog.onSongEdited = {run{finish()}}
        dialog.show(supportFragmentManager, EditSongNameDialogFragment.TAG)
    }

    private fun displayDeleteSongDialog() {
        val dialog = DeleteSongDialogFragment(viewModel)
        dialog.onDelete = {run{finish()}}
        dialog.show(supportFragmentManager, DeleteSongDialogFragment.TAG)
    }

    private fun displayNewSectionDialog() {
        viewModel.song.value?.let { song -> run{
                viewModel.sections.value?.let {
                    val dialog = NewSectionDialogFragment(song, it)
                    dialog.show(supportFragmentManager, NewSectionDialogFragment.TAG)
                }
            }
        }
    }

    private fun handlePracticeNow(section: Section = viewModel.sections.value!!.first()) {
        val intent = Intent(this, SongPracticeActivity::class.java)
        intent.putExtra(SongPracticeActivity.SONG_TAG, viewModel.song.value)
        intent.putExtra(SongPracticeActivity.SECTION_TAG, section)
        startActivity(intent)
    }

    private fun handleShowSectionDetails(section: Section) {
        displaySectionDetailsDialog(section)
    }

    private fun displaySectionDetailsDialog(section: Section) {
        val dialog = SectionDetailsDialogFragment(section, {handleSwipeToDelete(it)})
        dialog.show(supportFragmentManager, SectionDetailsDialogFragment.TAG)
    }

    private fun handleSwipeToDelete(practiceEntry: PracticeEntry) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.deletePracticeEntry(practiceEntry)
                showUndoDeletePracticeEntrySnackbar(practiceEntry)
            }
        }
    }

    private fun showUndoDeletePracticeEntrySnackbar(practiceEntry: PracticeEntry) {
        val sb = Snackbar.make(binding.layout, R.string.entry_deleted, Snackbar.LENGTH_LONG)
        sb.setAction(R.string.undo) {
            undoDeletePracticeEntry(practiceEntry)
        }
        sb.show()
    }

    private fun undoDeletePracticeEntry(practiceEntry: PracticeEntry) {
        viewModel.restorePracticeEntry(practiceEntry)
    }
}
