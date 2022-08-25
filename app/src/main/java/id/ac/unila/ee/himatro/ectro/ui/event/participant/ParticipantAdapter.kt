package id.ac.unila.ee.himatro.ectro.ui.event.participant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.UserAttendance
import id.ac.unila.ee.himatro.ectro.databinding.ItemAttendanceBinding

class ParticipantAdapter : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    private val attendanceList: ArrayList<UserAttendance> = ArrayList()

    inner class ViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: UserAttendance) {
            binding.apply {
                tvAttendanceStatus.text = attendance.status
                tvDummyAttendanceStatus.text = attendance.status

                tvConfirmationDate.text = attendance.confirmationDate
                tvUserName.text = attendance.userName
                layoutAttendanceStatus.setCardBackgroundColor(itemView.context.resources.getColor(R.color.primary_500))

                if (attendance.isAttend == false) {
                    tvUserReason.visibility = View.VISIBLE
                    tvUserReason.text = attendance.reasonCannotAttend
                    tvAttendanceStatus.setTextColor(itemView.context.resources.getColor(R.color.orange))
                    tvDummyAttendanceStatus.setTextColor(itemView.context.resources.getColor(R.color.orange))

                    tvDummyAttendanceStatus.setBackgroundColor(itemView.context.resources.getColor(R.color.orange))

                    layoutAttendanceStatus.setCardBackgroundColor(
                        itemView.context.resources.getColor(
                            R.color.orange
                        )
                    )
                }
            }

            itemView.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attendanceList[position])
    }

    override fun getItemCount(): Int = attendanceList.size

    fun setData(newList: List<UserAttendance>) {
        attendanceList.clear()
        attendanceList.addAll(newList)
        notifyDataSetChanged()
    }
}