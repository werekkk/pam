package jwernikowski.pam_lab.ui.dialog.rhythm_change

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.DialogChangeRhythmBinding
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.ui.fragment.metronome.MetronomeFragment
import jwernikowski.pam_lab.ui.fragment.metronome.MetronomeViewModel
import java.lang.IllegalStateException

class ChangeRhythmDialogFragment(val onRhythmSelected: (Rhythm) -> Unit) : DialogFragment() {

    companion object {
        val TAG = "ChangeRhythmDialogFragment"
    }

    private lateinit var adapter: ChangeRhythmAdapter
    private lateinit var viewManager: LinearLayoutManager

    private lateinit var binding: DialogChangeRhythmBinding
    private lateinit var viewModel: ChangeRhythmViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogChangeRhythmBinding.inflate(layoutInflater)

            AlertDialog.Builder(it)
                .setView(binding.root)
                .setTitle(R.string.choose_rhythm)
                .create()
        } ?: throw IllegalStateException()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ChangeRhythmViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        viewManager = LinearLayoutManager(context)
        adapter = ChangeRhythmAdapter { onRhythmClicked(it) }

        binding.rhythmList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@ChangeRhythmDialogFragment.adapter
        }

        val divider = DividerItemDecoration(context, viewManager.orientation)
        binding.rhythmList.addItemDecoration(divider)

        viewModel.allRhythms.observe(this) {
            adapter.rhythms = ArrayList(it)
        }
    }

    private fun onRhythmClicked(rhythm: Rhythm) {
        onRhythmSelected(rhythm)
        dismiss()
    }
}