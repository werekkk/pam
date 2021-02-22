package jwernikowski.pam_lab.ui.fragment.rhythms

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.FragmentRhythmsBinding
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.ui.activity.rhythm_details.RhythmDetailsActivity

class RhythmsFragment : Fragment() {

    private lateinit var binding: FragmentRhythmsBinding
    private lateinit var viewModel: RhythmsViewModel

    private lateinit var viewAdapter: RhythmsAdapter
    private lateinit var viewManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(RhythmsViewModel::class.java)

        binding = FragmentRhythmsBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.allRhythms.observe(viewLifecycleOwner)  { viewAdapter.rhythms = it }

        initFloatingActionButton()
        initRecyclerView()

        return binding.root
    }

    private fun initFloatingActionButton() {
        binding.addRhythmFab.setOnClickListener { startRhythmDetailsActivity(null) }
    }

    private fun initRecyclerView() {
        viewManager = LinearLayoutManager(context)
        viewAdapter = RhythmsAdapter { startRhythmDetailsActivity(it) }

        binding.rhythmsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    binding.addRhythmFab.apply { if (dy > 0) hide() else show() }
                }
            })
        }

        val divider = DividerItemDecoration(binding.rhythmsRecyclerView.context, viewManager.orientation)
        binding.rhythmsRecyclerView.addItemDecoration(divider)
    }

    private fun startRhythmDetailsActivity(rhythm: Rhythm?) {
        val intent = Intent(context, RhythmDetailsActivity::class.java)
        if (rhythm != null)
            intent.putExtra(RhythmDetailsActivity.RHYTHM_ID_TAG, rhythm.rhythmId)
        startActivity(intent)
    }

}
