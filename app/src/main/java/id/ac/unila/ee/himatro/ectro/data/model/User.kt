package id.ac.unila.ee.himatro.ectro.data.model

class User (
    var userId:String = "",
    var email:String = "",
    var name:String = "",
    var npm: String = "",
    var photoUrl: String = "",

    var linkedin:String = "",
    var instagram:String = "",

    var role: UserRole = UserRole(),
    var roleRequestStatus:String = "",

    var lastLoginAt:String = "",
)
