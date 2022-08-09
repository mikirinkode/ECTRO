package id.ac.unila.ee.himatro.ectro.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Event (
    var name: String = "",
    var desc: String = "",

    var type: String = "",
    var place: String = "",
    var date: String = "",
    var time: String = "",

    var category: String = "",
    var isNeedAttendanceForm: Boolean = false,
    var userId: String = ""
        ): Parcelable