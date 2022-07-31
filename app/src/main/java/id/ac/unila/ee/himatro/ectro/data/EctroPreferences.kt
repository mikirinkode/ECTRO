package id.ac.unila.ee.himatro.ectro.data

import android.content.Context
import android.content.SharedPreferences

class EctroPreferences(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(USER_PREF, 0)

    fun setValues(key: String, value: String?) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setValues(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getValues(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getBooleanValues(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun startSession(userName: String, userEmail: String, userNPM: String?, userPhotoUrl: String?){
        setValues(LOGIN_STATUS, LOGGED_IN)

        setValues(USER_NAME, userName)
        setValues(USER_EMAIL, userEmail)
        setValues(USER_NPM, userNPM)
        setValues(USER_PHOTO_URL, userPhotoUrl)
    }

    fun endSession(){
        setValues(LOGIN_STATUS, LOGGED_OUT)

        setValues(USER_EMAIL, null)
        setValues(USER_NAME, null)
        setValues(USER_NPM, null)
        setValues(USER_PHOTO_URL, null)
    }

    companion object {
        const val LOGIN_STATUS = "login_status"
        const val LOGGED_IN = "1"
        const val LOGGED_OUT = "0"

        const val USER_EMAIL = "user_email"
        const val USER_NAME = "user_name"
        const val USER_NPM = "user_npm"
        const val USER_PHOTO_URL = "user_photo_url"

        const val USER_INSTAGRAM_ACCOUNT = "user_instagram_account"
        const val USER_LINKEDIN_ACCOUNT = "user_linkedin_account"

        const val DARK_MODE_PREF: String = "dark_mode_pref"
        private const val USER_PREF: String = "user_pref"
    }
}
