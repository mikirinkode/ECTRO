package id.ac.unila.ee.himatro.ectro.data.model

class User (
    var email:String = "",
    var name:String = "",
    var npm: String = "",
    var userPhotoUrl: String = "",

    var linkedin:String = "",
    var instagram:String = "",

    var Role: UserRole = UserRole(),

    var lastLoginAt:String = "",
)
