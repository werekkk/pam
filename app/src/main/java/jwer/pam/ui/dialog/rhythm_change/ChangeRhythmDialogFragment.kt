package jwer.pam.ui.dialog.rhythm_change

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jwer.pam.R
import jwer.pam.databinding.DialogChangeRhythmBinding
import jwer.pam.db.data.entity.Rhythm
import java.lang.IllegalStateException

class ChangeRhythmDialogFragment(
    val onRhythmSelected: (Rhythm) -> Unit,
    private val currentRhythmId: Long,
) : DialogFragment() {

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
    ): View {
        viewModel = ViewModelProvider(this).get(ChangeRhythmViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        viewManager = LinearLayoutManager(context)
        adapter = ChangeRhythmAdapter({ onRhythmClicked(it) }, currentRhythmId)

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