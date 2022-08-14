package id.ac.unila.ee.himatro.ectro.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RoleRequest (
    var requestId: String = "",
    var requestedAt: String = "",
    var updatedAt: String = "",
    var status: String = "",
    var applicantId: String = "",
    var applicantName: String = "",
    var applicantNpm: String = "",
    var applicantEmail: String = "",
    var requestHandlerId: String = ""
        ):Parcelable