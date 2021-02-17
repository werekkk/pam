package jwernikowski.pam_lab.ui.dialog.rhythm_change

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.entity.Rhythm

class ChangeRhythmAdapter(private val clickListener: (Rhythm) -> Unit):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var rhythms: ArrayList<Rhythm> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            ItemType.fromInt(viewType)!!.getLayout(),
            parent,
            false
        ) as ViewGroup

        return when(ItemType.fromInt(viewType)!!) {
            ItemType.NO_RHYTHM_ITEM -> {
                val vh = NoRhythmViewHolder(layout)
                vh.itemView.setOnClickListener { clickListener(Rhythm.DEFAULT_RHYTHM) }
                vh
            }
            ItemType.RHYTHM_ITEM -> {
                val vh = RhythmViewHolder(layout)
                vh.itemView.setOnClickListener {run{clickListener(vh.rhythm)}}
                vh
            }
        }

    }

    override fun getItemCount(): Int = rhythms.size + 1

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> ItemType.NO_RHYTHM_ITEM.value
            else -> ItemType.RHYTHM_ITEM.value
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ItemType.RHYTHM_ITEM.value) {
            (holder as RhythmViewHolder).rhythm = rhythms[position - 1]
        }
    }

    class RhythmViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val rhythmName: TextView = itemView.findViewById(R.id.rhythmName)
        private val rhythmMeter: TextView = itemView.findViewById(R.id.meter)

        private lateinit var _rhythm: Rhythm

        var rhythm: Rhythm
            get() = _rhythm
            set(r) {
                _rhythm = r
                rhythmName.text = r.name
                if (r != Rhythm.DEFAULT_RHYTHM)
                    rhythmMeter.text = r.meter.toString()
            }
    }

    class NoRhythmViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    enum class ItemType(val value: Int) {
        NO_RHYTHM_ITEM(0),
        RHYTHM_ITEM(1);

        companion object {
            private val map = values().associateBy(ItemType::value)
            fun fromInt(type: Int) = map[type]
        }

        fun getLayout(): Int {
            return when(value) {
                0 -> R.layout.item_rhythm_default
                1 -> R.layout.item_rhythm
                else -> 0
            }
        }
    }
}