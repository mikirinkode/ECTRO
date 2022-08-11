package id.ac.unila.ee.himatro.ectro.data

import android.content.Context
import android.content.SharedPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User

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

    fun setValues(key: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getValues(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getValuesInt(key: String): Int? {
        return sharedPreferences.getInt(key, 0)
    }

    fun getBooleanValues(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun startSession(user: User) {
        setValues(LOGIN_STATUS, LOGGED_IN)

        setValues(USER_NAME, user.name)
        setValues(USER_EMAIL, user.email)
        setValues(USER_NPM, user.npm)
        setValues(USER_PHOTO_URL, user.photoUrl)

        setValues(USER_DEPARTMENT, user.role.department)
        setValues(USER_DIVISION, user.role.division)
        setValues(USER_POSITION, user.role.position)
        setValues(ACTIVE_PERIOD, user.role.activePeriod)

        setValues(ROLE_REQUEST_STATUS, user.roleRequestStatus)

        setValues(USER_LINKEDIN_ACCOUNT, user.linkedin)
        setValues(USER_INSTAGRAM_ACCOUNT, user.instagram)
    }

    fun endSession() {
        setValues(LOGIN_STATUS, LOGGED_OUT)

        setValues(USER_EMAIL, null)
        setValues(USER_NAME, null)
        setValues(USER_NPM, null)
        setValues(USER_PHOTO_URL, null)

        setValues(USER_DEPARTMENT, null)
        setValues(USER_DIVISION, null)
        setValues(USER_POSITION, null)
        setValues(ACTIVE_PERIOD, null)

        setValues(USER_LINKEDIN_ACCOUNT, null)
        setValues(USER_INSTAGRAM_ACCOUNT, null)
    }

    companion object {
        const val LOGIN_STATUS = "login_status"
        const val LOGGED_IN = "1"
        const val LOGGED_OUT = "0"

        const val USER_EMAIL = "user_email"
        const val USER_NAME = "user_name"
        const val USER_NPM = "user_npm"
        const val USER_PHOTO_URL = "user_photo_url"

        const val USER_DEPARTMENT = "user_department"
        const val USER_DIVISION = "user_division"
        const val USER_POSITION = "user_position"
        const val ACTIVE_PERIOD = "active_period"

        const val USER_INSTAGRAM_ACCOUNT = "user_instagram_account"
        const val USER_LINKEDIN_ACCOUNT = "user_linkedin_account"

        const val ROLE_REQUEST_STATUS = "role_request_status"
        const val COMPLETED_STATUS = "completed"
        const val WAITING_STATUS = "waiting"

        const val DARK_MODE_PREF: String = "dark_mode_pref"
        private const val USER_PREF: String = "user_pref"
    }
}
