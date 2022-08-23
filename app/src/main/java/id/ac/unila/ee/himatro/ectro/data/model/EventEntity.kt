package id.ac.unila.ee.himatro.ectro.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class EventEntity (
    var eventId: String = "",
    var name: String = "",
    var desc: String = "",
    var category: String = "",

    var type: String = "",
    var onlineEventMedia: String = "",
    var place: String = "",
    var date: String = "",
    var time: String = "",

    @field:JvmField
    var isNeedNotes: Boolean? = null,
    @field:JvmField
    var isNeedAttendanceForm: Boolean? = null,
    var extraActionName: String = "",
    var extraActionLink: String = "",
    @field:JvmField
    var actionAfterAttendance: Boolean? = null,

    var creatorId: String = "",
    var createdAt: String = ""

): Parcelable