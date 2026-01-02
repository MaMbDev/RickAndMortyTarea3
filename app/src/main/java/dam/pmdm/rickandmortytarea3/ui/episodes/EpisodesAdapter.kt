package dam.pmdm.rickandmortytarea3.ui.episodes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.rickandmortytarea3.R
import dam.pmdm.rickandmortytarea3.data.model.Episode

class EpisodesAdapter(
    private var items: List<Episode>,
    private val isSeen: (Episode) -> Boolean,
    private val onClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {

    fun submitList(newItems: List<Episode>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvEpisodeName)
        private val tvCode: TextView = view.findViewById(R.id.tvEpisodeCode)
        private val tvDate: TextView = view.findViewById(R.id.tvEpisodeDate)
        private val indicator: View = view.findViewById(R.id.viewSeenIndicator)

        fun bind(item: Episode) {
            tvName.text = item.name
            tvCode.text = item.episode
            tvDate.text = item.air_date

            val seen = isSeen(item)
            indicator.setBackgroundColor(if (seen) Color.GREEN else Color.DKGRAY)
            itemView.alpha = if (seen) 1f else 0.7f

            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
