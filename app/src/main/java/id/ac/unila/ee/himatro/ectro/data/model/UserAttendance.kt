package id.ac.unila.ee.himatro.ectro.data.model

class UserAttendance(
    var attendanceId: String = "",
    var eventId: String = "",
    var userId: String = "",
    var userName: String = "",

    @field:JvmField
    var isAttend: Boolean? = null,
    var status: String = "",
    var reasonCannotAttend: String = "",
    var confirmationDate: String = ""
    )
