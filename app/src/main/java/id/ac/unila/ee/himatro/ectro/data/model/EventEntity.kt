package id.ac.unila.ee.himatro.ectro.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class EventEntity (
    var name: String = "",
    var desc: String = "",
    var category: String = "",

    var type: String = "",
    var place: String = "",
    var date: String = "",
    var time: String = "",

    @field:JvmField
    var isNeedAttendanceForm: Boolean? = null,
    var extraActionName: String = "",
    var extraActionLink: String = "",
    @field:JvmField
    var actionAfterAttendance: Boolean? = null,

    var creatorUid: String = "",
    var createdAt: String = ""

): Parcelable