package id.ac.unila.ee.himatro.ectro.utils

import androidx.recyclerview.widget.DiffUtil
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest

class RoleReqDiffUtil(
    private val oldList: List<RoleRequest>,
    private val newList: List<RoleRequest>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].requestId == newList[newItemPosition].requestId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].updatedAt != newList[newItemPosition].updatedAt -> false
            oldList[oldItemPosition].status != newList[newItemPosition].status -> false
            oldList[oldItemPosition].requestHandlerId != newList[newItemPosition].requestHandlerId -> false
            else -> true
        }
    }
}