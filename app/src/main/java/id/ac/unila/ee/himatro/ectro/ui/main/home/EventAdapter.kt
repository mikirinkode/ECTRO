package id.ac.unila.ee.himatro.ectro.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.ac.unila.ee.himatro.ectro.data.model.Event
import id.ac.unila.ee.himatro.ectro.databinding.ItemEventBinding

class EventAdapter: RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    private val eventList: ArrayList<Event> = ArrayList()

    inner class ViewHolder(private val binding: ItemEventBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event){
            binding.apply {
                tvEventTitle.text = event.name
                tvEventDate.text = event.date
                tvEventTime.text = event.time
                tvEventPlace.text = event.place
                tvDummyCategory.text = "#${event.category}"
                tvEventCategory.text = "#${event.category}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    fun setData(newList: ArrayList<Event>){
        eventList.clear()
        eventList.addAll(newList)
        notifyDataSetChanged()
    }
}