package id.ac.unila.ee.himatro.ectro.ui.member

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ItemUserBinding
import id.ac.unila.ee.himatro.ectro.ui.profile.DetailUserActivity
import id.ac.unila.ee.himatro.ectro.utils.UserDiffUtil

class MemberAdapter : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    private val userList: ArrayList<User> = ArrayList()

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                tvUserName.text = user.name

                if (user.photoUrl.isEmpty()) {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_default_profile)
                        .into(ivUserPhoto)
                } else {
                    Glide.with(itemView.context)
                        .load(user.photoUrl)
                        .placeholder(R.drawable.ic_image_loading)
                        .error(R.drawable.ic_image_default)
                        .into(ivUserPhoto)
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USER_ID, user.userId)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun setData(newList: List<User>) {
        val diffCallback = UserDiffUtil(this.userList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        userList.clear()
        userList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}