package jwernikowski.pam_lab.ui.rhythms

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.rhythm.Rhythm
import jwernikowski.pam_lab.ui.rhythm_details.RhythmDetailsActivity

class RhythmsFragment : Fragment() {

    private lateinit var viewModel: RhythmsViewModel

    private lateinit var rhythmsRecyclerView: RecyclerView
    private lateinit var viewAdapter: RhythmAdapter
    private lateinit var viewManager: LinearLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RhythmsViewModel::class.java)
        viewModel.allRhythms.observe(viewLifecycleOwner, Observer { newRhythms -> run{ viewAdapter.rhythms = newRhythms } })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.rhythms_fragment, container, false)
        initFloatingActionButton(root)
        initRecyclerView(root)
        return root
    }

    private fun initFloatingActionButton(root: View) {
        root.findViewById<View>(R.id.add_rhythm_fab).setOnClickListener { v -> run {
            startRhythmDetailsActivity(null)
        } }
    }

    private fun initRecyclerView(root: View) {
        viewManager = LinearLayoutManager(context)
        viewAdapter = RhythmAdapter { rhythm -> run{
            startRhythmDetailsActivity(rhythm)
        } }

        rhythmsRecyclerView = root.findViewById<RecyclerView>(R.id.rhythmsRecyclerView)
        rhythmsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val divider = DividerItemDecoration(rhythmsRecyclerView.context, viewManager.orientation)
        rhythmsRecyclerView.addItemDecoration(divider)
    }

    private fun startRhythmDetailsActivity(rhythm: Rhythm?) {
        val intent = Intent(context, RhythmDetailsActivity::class.java)
        if (rhythm != null)
            intent.putExtra(RhythmDetailsActivity.RHYTHM_ID_TAG, rhythm.rhythmId)
        startActivity(intent)
    }

}
