package jwernikowski.pam_lab.ui.song_details

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.ui.song_practice.SongPracticeActivity
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.view.LineChartView

class SongDetailsActivity : AppCompatActivity() {

    companion object {
        val SONG_TAG = "song"
    }

    private lateinit var viewModel: SongDetailsViewModel
    private lateinit var song: Song

    private lateinit var practiceEntriesRecyclerView: RecyclerView
    private lateinit var viewAdapter: PracticeEntryAdapter
    private lateinit var viewManager: LinearLayoutManager

    private lateinit var progressText: TextView
    private lateinit var daysPracticedText: TextView

    private lateinit var chart: LineChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_details)

        initChart()

        daysPracticedText = findViewById(R.id.days_practiced)
        progressText = findViewById(R.id.progress)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(SongDetailsViewModel::class.java)

        viewModel.practiceEntries.observe(this, Observer { list -> run{
            viewAdapter.setEntries(list)
            daysPracticedText.text = PracticeEntry.calculateDaysPracticed(list).toString()
            progressText.text = (PracticeEntry.calculateProgress(list) * 100).toInt().toString() + "%"
        } })

        song = intent.extras?.get(SONG_TAG) as Song
        loadSong()

        initRecyclerView()
        findViewById<Button>(R.id.practice_now).setOnClickListener { v -> run {handlePracticeNow()} }
    }

    private fun initRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = PracticeEntryAdapter({ entry ->
            run {
                onPracticeEntryClicked(entry)
            }
        }, this)

        viewModel.practiceEntries.observe(this, Observer { newEntries -> run{
            viewAdapter.setEntries(newEntries)
            updateGraph(newEntries)
        } })

        practiceEntriesRecyclerView = findViewById<RecyclerView>(R.id.practice_entries_recycler).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            val touchHelper = ItemTouchHelper(EntrySwipeToDeleteCallback(viewAdapter, viewModel))
            touchHelper.attachToRecyclerView(this)
            isNestedScrollingEnabled = false
        }

        val divider = DividerItemDecoration(practiceEntriesRecyclerView.context, viewManager.orientation)
        practiceEntriesRecyclerView.addItemDecoration(divider)
    }

    private fun initChart() {
        return
//        chart = findViewById(R.id.chart)
    }

    private fun updateGraph(entries: List<PracticeEntry>) {
        return
        var values = PracticeEntry.getPointValues(entries, song.initialTempo, song.goalTempo)
        val line = Line(values)
        line.color = Color.RED
        val lines = ArrayList<Line>()
        lines.add(line)
        val data = LineChartData()
        data.lines = lines
        chart.lineChartData = data
    }

    private fun onPracticeEntryClicked(entry: PracticeEntry) {

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
        viewModel.song.observe(this, Observer { song -> supportActionBar?.title = song.name })
        viewModel.song.value = song
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

    private fun handlePracticeNow() {
        val intent = Intent(this, SongPracticeActivity::class.java)
        intent.putExtra(SongPracticeActivity.SONG_TAG, viewModel.song.value)
        if (viewModel.practiceEntries.value!!.isNotEmpty())
            intent.putExtra(SongPracticeActivity.BPM_TAG, viewModel.practiceEntries.value!!.last().tempo)
        startActivity(intent)
    }
}
