package id.ac.unila.ee.himatro.ectro.utils

object FirestoreUtils {

    // FireStore Table / Collection Name
    const val TABLE_USER = "users"
    const val TABLE_ROLE_REQUEST = "roleRequests"
    const val TABLE_EVENTS = "events"
    const val TABLE_ATTENDANCES = "attendances"
    const val TABLE_NOTES = "notes"

    // User Column Name
    const val TABLE_USER_ID = "userId"
    const val TABLE_USER_NAME = "name"
    const val TABLE_USER_EMAIL = "email"
    const val TABLE_USER_NPM = "npm"
    const val TABLE_USER_INSTAGRAM = "instagram"
    const val TABLE_USER_LINKEDIN = "linkedin"
    const val TABLE_USER_ROLE = "role"
    const val TABLE_USER_DEPARTMENT = "department"
    const val TABLE_USER_DIVISION = "division"
    const val TABLE_USER_POSITION = "position"
    const val TABLE_USER_ACTIVE_PERIOD = "activePeriod"
    const val TABLE_USER_REQUEST_STATUS = "roleRequestStatus"
    const val TABLE_USER_PHOTO_URL = "photoUrl"
    const val TABLE_USER_LAST_LOGIN = "lastLoginAt"

    // Event Column Name
    const val TABLE_EVENT_ID = "eventId"
    const val TABLE_EVENT_NAME = "name"
    const val TABLE_EVENT_DESC = "desc"
    const val TABLE_EVENT_TYPE = "type"
    const val TABLE_EVENT_ONLINE_MEDIA = "onlineEventMedia"
    const val TABLE_EVENT_DATE = "date"
    const val TABLE_EVENT_TIME = "time"
    const val TABLE_EVENT_PLACE = "place"
    const val TABLE_EVENT_CATEGORY = "category"
    const val TABLE_EVENT_NEED_NOTES = "isNeedNotes"
    const val TABLE_EVENT_ATTENDANCE_FORM = "isNeedAttendanceForm"
    const val TABLE_EVENT_EXTRA_ACTION_NAME = "extraActionName"
    const val TABLE_EVENT_EXTRA_ACTION_LINK = "extraActionLink"
    const val TABLE_EVENT_ACTION_AFTER_ATTENDANCE = "actionAfterAttendance"
    const val TABLE_EVENT_CREATOR_ID = "creatorId"
    const val TABLE_EVENT_CREATED_AT = "createdAt"
    const val TABLE_EVENT_UPDATED_AT = "updatedAt"

    // Role Request Column Name
    const val TABLE_RR_REQUEST_ID = "requestId"
    const val TABLE_RR_REQUEST_AT = "requestedAt"
    const val TABLE_RR_APPLICANT_ID = "applicantId"
    const val TABLE_RR_APPLICANT_NAME = "applicantName"
    const val TABLE_RR_APPLICANT_NPM = "applicantNpm"
    const val TABLE_RR_APPLICANT_EMAIL = "applicantEmail"
    const val TABLE_RR_HANDLER_ID = "requestHandlerId"
    const val TABLE_RR_STATUS = "status"
    const val TABLE_RR_UPDATED_AT = "updatedAt"


    // Attendance column
    const val TABLE_ATTENDANCE_ID = "attendanceId"
    const val TABLE_ATTENDANCE_EVENT_ID = "eventId"
    const val TABLE_ATTENDANCE_USER_ID = "userId"
    const val TABLE_ATTENDANCE_USER_NAME = "userName"
    const val TABLE_ATTENDANCE_USER_DEPT = "userDept"
    const val TABLE_ATTENDANCE_STATUS = "status"
    const val TABLE_ATTENDANCE_IS_ATTEND = "isAttend"
    const val TABLE_ATTENDANCE_REASON = "reasonCannotAttend"
    const val TABLE_ATTENDANCE_DATE = "confirmationDate"

    // Notes column
    const val TABLE_NOTE_ID = "noteId"
    const val TABLE_NOTE_EVENT_ID = "eventId"
    const val TABLE_NOTE_CONTENT = "noteContent"
    const val TABLE_NOTE_USER_ID = "userId"
    const val TABLE_NOTE_CREATED_AT = "createdAt"
    const val TABLE_NOTE_UPDATED_AT = "updatedAt"

    // Role Request Status
    const val ROLE_REQUEST_COMPLETED = "COMPLETED"
    const val ROLE_REQUEST_REJECTED = "REJECTED"
}