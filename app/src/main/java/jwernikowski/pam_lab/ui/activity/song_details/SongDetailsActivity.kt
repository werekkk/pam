package jwernikowski.pam_lab.ui.activity.song_details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
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
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.ui.dialog.section_details.SectionDetailsDialogFragment
import jwernikowski.pam_lab.ui.activity.song_practice.SongPracticeActivity
import jwernikowski.pam_lab.ui.dialog.song_delete.DeleteSongDialogFragment
import jwernikowski.pam_lab.ui.dialog.song_edit_name.EditSongNameDialogFragment
import jwernikowski.pam_lab.ui.dialog.section_new.NewSectionDialogFragment
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

        song = intent.extras?.get(SONG_TAG) as Song
        loadSong()

        initRecyclerViews()
        binding.practiceNow.setOnClickListener { handlePracticeNow() }
        binding.newSection.setOnClickListener { displayNewSectionDialog() }
    }

    private fun initRecyclerViews() {
        practiceEntryViewManager = LinearLayoutManager(this)
        practiceEntryViewAdapter =
            PracticeEntryAdapter {}
        sectionViewManager = LinearLayoutManager(this)
        sectionsViewAdapter =
            SectionAdapter({
                handlePracticeNow(it)
            }, {
                handleShowSectionDetails(it)
            })

        viewModel.practiceEntries.observe(this, Observer { practiceEntryViewAdapter.entries = it })
        viewModel.sections.observe(this, Observer { sectionsViewAdapter.sections = it })

        practiceEntriesRecyclerView = binding.practiceEntriesRecycler.apply {
            setHasFixedSize(true)
            layoutManager = practiceEntryViewManager
            adapter = practiceEntryViewAdapter
            isNestedScrollingEnabled = false

            val touchHelper = ItemTouchHelper(
                EntrySwipeToDeleteCallback(
                    practiceEntryViewAdapter,
                    { handleSwipeToDelete(it) },
                    context
                )
            )
            touchHelper.attachToRecyclerView(this)
        }
        sectionsRecyclerView = binding.sectionsRecycler.apply {
            setHasFixedSize(true)
            layoutManager = sectionViewManager
            adapter = sectionsViewAdapter
            isNestedScrollingEnabled = false

            val touchHelper = ItemTouchHelper(
                DragCallback { onReorderedSections(sectionsViewAdapter.sections) }
            )
            touchHelper.attachToRecyclerView(this)
        }

        practiceEntriesRecyclerView.addItemDecoration(DividerItemDecoration(practiceEntriesRecyclerView.context, practiceEntryViewManager.orientation))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_song_details, menu)
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
        val dialog =
            EditSongNameDialogFragment(
                viewModel
            )
        dialog.onSongEdited = {run{finish()}}
        dialog.show(supportFragmentManager,
            EditSongNameDialogFragment.TAG
        )
    }

    private fun displayDeleteSongDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_song)
            .setMessage(R.string.delete_song_sure)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteSong()
                finish()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> Unit}
            .show()
    }

    private fun displayNewSectionDialog() {
        viewModel.song.value?.let { song -> run{
                viewModel.sections.value?.let {
                    val dialog =
                        NewSectionDialogFragment(
                            song,
                            it
                        )
                    dialog.show(supportFragmentManager,
                        NewSectionDialogFragment.TAG
                    )
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
        val dialog = SectionDetailsDialogFragment(section)
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

    private fun onReorderedSections(reorderedSections: List<Section>) {
        viewModel.reorderSections(reorderedSections)
    }
}