package id.ac.unila.ee.himatro.ectro.ui.member

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.COMPLETED_STATUS
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.databinding.ItemRoleRequestBinding
import id.ac.unila.ee.himatro.ectro.utils.RoleReqDiffUtil

class RoleRequestAdapter: RecyclerView.Adapter<RoleRequestAdapter.ViewHolder>() {

    private val requestList: ArrayList<RoleRequest> = ArrayList()

    inner class ViewHolder(private val binding: ItemRoleRequestBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(roleRequest: RoleRequest){
            binding.apply {
                tvUserName.text = roleRequest.applicantName
                tvDummyRequestStatus.text = roleRequest.status
                tvRequestStatus.text = roleRequest.status
            }

            if (roleRequest.status == COMPLETED_STATUS){
                Toast.makeText(itemView.context, itemView.context.getString(R.string.status_is_completed), Toast.LENGTH_SHORT).show()
            } else {
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, EditRoleActivity::class.java)
                    intent.putExtra(EditRoleActivity.EXTRA_ENTITY, roleRequest)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoleRequestBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    override fun getItemCount(): Int = requestList.size

    fun setData(newList: ArrayList<RoleRequest>){
        val diffUtil = RoleReqDiffUtil(requestList, newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        requestList.clear()
        requestList.addAll(newList)
        diffResults.dispatchUpdatesTo(this)
    }
}