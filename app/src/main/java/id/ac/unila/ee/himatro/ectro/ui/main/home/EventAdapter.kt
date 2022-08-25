package id.ac.unila.ee.himatro.ectro.ui.main.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.databinding.ItemEventBinding
import id.ac.unila.ee.himatro.ectro.ui.event.DetailEventActivity
import id.ac.unila.ee.himatro.ectro.utils.EventDiffUtil

class EventAdapter : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    private val eventEntityList: ArrayList<EventEntity> = ArrayList()

    inner class ViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(eventEntity: EventEntity) {
            binding.apply {
                tvEventTitle.text = eventEntity.name
                tvEventDate.text = eventEntity.date
                tvEventTime.text = eventEntity.time
                tvEventPlace.text = eventEntity.place
                tvDummyCategory.text = "#${eventEntity.category}"
                tvEventCategory.text = "#${eventEntity.category}"
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_ENTITY, eventEntity)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(eventEntityList[position])
    }

    override fun getItemCount(): Int = eventEntityList.size

    fun setData(newList: List<EventEntity>) {
        val diffCallback = EventDiffUtil(this.eventEntityList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        eventEntityList.clear()
        eventEntityList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}