package id.ac.unila.ee.himatro.ectro.utils

import androidx.recyclerview.widget.DiffUtil
import id.ac.unila.ee.himatro.ectro.data.model.User

class UserDiffUtil(
    private val oldList: List<User>,
    private val newList: List<User>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].userId == newList[newItemPosition].userId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].userId != newList[newItemPosition].userId -> false
            oldList[oldItemPosition].email != newList[newItemPosition].email -> false
            oldList[oldItemPosition].name != newList[newItemPosition].name -> false
            oldList[oldItemPosition].npm != newList[newItemPosition].npm -> false
            oldList[oldItemPosition].photoUrl != newList[newItemPosition].photoUrl -> false
            oldList[oldItemPosition].linkedin != newList[newItemPosition].linkedin -> false
            oldList[oldItemPosition].instagram != newList[newItemPosition].instagram -> false
            oldList[oldItemPosition].role != newList[newItemPosition].role -> false
            oldList[oldItemPosition].roleRequestStatus != newList[newItemPosition].roleRequestStatus -> false
            else -> true
        }
    }
}