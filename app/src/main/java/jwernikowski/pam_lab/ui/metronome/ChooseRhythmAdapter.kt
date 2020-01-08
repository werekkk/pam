package jwernikowski.pam_lab.ui.metronome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.rhythm.Rhythm

class ChooseRhythmAdapter(private val clickListener: (Rhythm) -> Unit):
    RecyclerView.Adapter<ChooseRhythmAdapter.RhythmViewHolder>() {

    var rhythms: ArrayList<Rhythm> = arrayListOf()
        set(value) {
            value.add(0, Rhythm.DEFAULT_RHYTHM)
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RhythmViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.item_choose_rhythm,
            parent,
            false
        ) as ViewGroup
        val vh = RhythmViewHolder(layout)
        vh.itemView.setOnClickListener {run{clickListener(vh.rhythm)}}
        return vh
    }

    override fun getItemCount(): Int = rhythms.size

    override fun onBindViewHolder(holder: RhythmViewHolder, position: Int) {
        holder.rhythm = rhythms[position]
    }

    class RhythmViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val rhythmName: TextView = itemView.findViewById(R.id.rhythmName)

        private lateinit var _rhythm: Rhythm

        var rhythm: Rhythm
            get() = _rhythm
            set(r) {
                _rhythm = r
                rhythmName.text = r.name
            }
    }
}