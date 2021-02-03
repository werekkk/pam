package jwernikowski.pam_lab.ui.dialog.section_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.DialogSectionDetailsBinding
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.ui.activity.song_details.EntrySwipeToDeleteCallback
import jwernikowski.pam_lab.ui.activity.song_details.PracticeEntryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SectionDetailsDialogFragment(private val section: Section) : DialogFragment() {

    companion object {
        val TAG = "SectionDetailsDialogFragment"
    }

    private lateinit var binding: DialogSectionDetailsBinding
    private lateinit var viewModel: SectionDetailsViewModel

    private lateinit var practiceEntryRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSectionDetailsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(SectionDetailsViewModel::class.java)
        viewModel.setSection(section)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        val practiceEntryViewManager = LinearLayoutManager(context)
        val practiceEntryAdapter =
            PracticeEntryAdapter({})

        viewModel.practiceEntries.observe(this, Observer { practiceEntryAdapter.entries = it })

        practiceEntryRecyclerView = binding.practiceEntriesRecycler.apply {
            setHasFixedSize(true)
            layoutManager = practiceEntryViewManager
            adapter = practiceEntryAdapter
            val touchHelper = ItemTouchHelper(
                EntrySwipeToDeleteCallback(
                    practiceEntryAdapter,
                    { handleSwipeToDelete(it) },
                    context
                )
            )
            touchHelper.attachToRecyclerView(this)
            isNestedScrollingEnabled = false
        }

        practiceEntryRecyclerView.addItemDecoration(DividerItemDecoration(practiceEntryRecyclerView.context, practiceEntryViewManager.orientation))
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
        val sb = Snackbar.make(binding.snackbarLayout, R.string.entry_deleted, Snackbar.LENGTH_LONG)
        sb.setAction(R.string.undo) {
            undoDeletePracticeEntry(practiceEntry)
        }
        sb.show()
    }

    private fun undoDeletePracticeEntry(practiceEntry: PracticeEntry) {
        viewModel.restorePracticeEntry(practiceEntry)
    }

}