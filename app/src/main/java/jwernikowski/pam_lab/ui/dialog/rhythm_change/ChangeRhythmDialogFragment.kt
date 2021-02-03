package jwernikowski.pam_lab.ui.dialog.rhythm_change

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.ui.fragment.metronome.MetronomeFragment
import jwernikowski.pam_lab.ui.fragment.metronome.MetronomeViewModel

class ChangeRhythmDialogFragment(private val parent: MetronomeFragment) : DialogFragment() {

    companion object {
        val TAG = "ChangeRhythmDialogFragment"
    }

    private lateinit var rhythmsRecyclerView: RecyclerView
    private lateinit var adapter: ChangeRhythmAdapter
    private lateinit var viewManager: LinearLayoutManager

    private lateinit var viewModel: MetronomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_change_rhythm, container)

        initRecyclerView(root)

        parentFragment?.let {
            viewModel = ViewModelProviders.of(it).get(MetronomeViewModel::class.java)
        }

        viewModel.rhythms.observe(this, Observer { newRhythms -> run{
            adapter.rhythms = ArrayList(newRhythms)
        } })
        return root
    }

    private fun initRecyclerView(root: View) {
        viewManager = LinearLayoutManager(context)
        adapter =
            ChangeRhythmAdapter { r ->
                run {
                    onRhythmClicked(r)
                }
            }

        rhythmsRecyclerView = root.findViewById(R.id.rhythmList)
        rhythmsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@ChangeRhythmDialogFragment.adapter
        }

        val divider = DividerItemDecoration(rhythmsRecyclerView.context, viewManager.orientation)
        rhythmsRecyclerView.addItemDecoration(divider)
    }

    private fun onRhythmClicked(rhythm: Rhythm) {
        parent.setRhythm(rhythm)
        dismiss()
    }
}