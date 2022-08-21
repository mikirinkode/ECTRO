package id.ac.unila.ee.himatro.ectro.utils

import androidx.recyclerview.widget.DiffUtil
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest

class EventDiffUtil(
    private val oldList: List<EventEntity>,
    private val newList: List<EventEntity>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].eventId == newList[newItemPosition].eventId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].eventId != newList[newItemPosition].eventId -> false
            oldList[oldItemPosition].name != newList[newItemPosition].name -> false
            oldList[oldItemPosition].desc != newList[newItemPosition].desc -> false
            oldList[oldItemPosition].category != newList[newItemPosition].category -> false
            oldList[oldItemPosition].type != newList[newItemPosition].type -> false
            oldList[oldItemPosition].place != newList[newItemPosition].place -> false
            oldList[oldItemPosition].date != newList[newItemPosition].date -> false
            oldList[oldItemPosition].time != newList[newItemPosition].time -> false
            oldList[oldItemPosition].isNeedNotes != newList[newItemPosition].isNeedNotes -> false
            oldList[oldItemPosition].isNeedAttendanceForm != newList[newItemPosition].isNeedAttendanceForm -> false
            oldList[oldItemPosition].extraActionName != newList[newItemPosition].extraActionName -> false
            oldList[oldItemPosition].extraActionLink != newList[newItemPosition].extraActionLink -> false
            oldList[oldItemPosition].actionAfterAttendance != newList[newItemPosition].actionAfterAttendance -> false
            oldList[oldItemPosition].creatorId != newList[newItemPosition].creatorId -> false
            oldList[oldItemPosition].createdAt != newList[newItemPosition].createdAt -> false
            else -> true
        }
    }
}