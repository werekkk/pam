package jwernikowski.pam_lab.ui.song_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.PracticeEntry
import java.time.format.DateTimeFormatter

class PracticeEntryAdapter(val clickListener: (PracticeEntry) -> Unit, val context: Context) :
        RecyclerView.Adapter<PracticeEntryAdapter.PracticeEntryViewHolder>() {

    private var entries: List<PracticeEntry> = ArrayList()

    class PracticeEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            fun imageResourceByRating(rating: PracticeEntry.Rating): Int {
                when (rating) {
                    PracticeEntry.Rating.VERY_LOW -> return R.drawable.rate_very_low
                    PracticeEntry.Rating.LOW -> return R.drawable.rate_low
                    PracticeEntry.Rating.MEDIUM -> return R.drawable.rate_medium
                    PracticeEntry.Rating.HIGH -> return R.drawable.rate_very_high
                    PracticeEntry.Rating.VERY_HIGH -> return R.drawable.rate_very_high
                }
                return 0
            }
        }

        private val date: TextView = itemView.findViewById(R.id.practice_date)
        private val tempo: TextView = itemView.findViewById(R.id.practice_tempo)
        private val rating: ImageView = itemView.findViewById(R.id.practice_rating)

        private lateinit var _entry: PracticeEntry

        var entry: PracticeEntry
            get() = _entry
            set(e) {
                _entry = e
                date.text = e.date.format(format)
                tempo.text = e.tempo.toString()
                rating.setImageResource(imageResourceByRating(e.rating))
            }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeEntryViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.item_practice_entry,
            parent,
            false
        ) as ViewGroup
        val vh =
            PracticeEntryViewHolder(layout)
        vh.itemView.setOnClickListener { v ->
            run {
                clickListener(vh.entry)
            }
        }
        return vh
    }

    override fun getItemCount(): Int = entries.size

    override fun onBindViewHolder(holder: PracticeEntryViewHolder, position: Int) {
        val currentEntry = entries[position]
        holder.entry = currentEntry
    }

    fun setEntries(entries: List<PracticeEntry>) {
        this.entries = entries.sortedWith(Comparator { o1, o2 ->  o2.date.compareTo(o1.date)})
        notifyDataSetChanged()
    }

    fun getItem(position: Int): PracticeEntry {
        return entries[position]
    }
}