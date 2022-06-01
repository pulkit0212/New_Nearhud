package common.neighbour.nearhud.database.prefrence

import android.content.Context
import android.content.SharedPreferences

/*
* Copyright (c) Pulkit Sharma
* Pulkit Developer
* pulkit.sharma88548@gmail.com
* 7014586715
*/
class SharedPre private constructor(context: Context) {
    var mContext: Context
    var isSavedAd: Boolean
        get() = GetDataBoolean(IS_ADD_MOB_SAVED)
        set(value) {
            SetDataBoolean(IS_ADD_MOB_SAVED, value)
        }
    var isLoggedIn: Boolean
        get() = GetDataBoolean(IS_LOGGED_IN)
        set(value) {
            SetDataBoolean(IS_LOGGED_IN, value)
        }
    var isGoogleLoggedIn: Boolean
        get() = GetDataBoolean(IS_LOGGED_IN_VIA_GOOGLE)
        set(value) {
            SetDataBoolean(IS_LOGGED_IN_VIA_GOOGLE, value)
        }

    var ScreenType: String
        get() = GetDataString(SCREEN)!!
        set(value) {
            SetDataString(SCREEN, value)
        }

    fun isLogout():Boolean {
        return GetDataBoolean(Is_LOGOUT)
    }

    fun setIsLogout(value: Boolean) {
        SetDataBoolean(Is_LOGOUT, value)
    }


    fun setUserId(uid: String) {
        SetDataString(USER_ID, uid)
    }

    val userId: String?
        get() = GetDataString(USER_ID)

    fun setUserMobile(userMobile: String) {
        SetDataString(MOBILE_NO, userMobile)
    }

    val userMobile: String?
        get() = GetDataString(MOBILE_NO)

    fun setName(name: String) {
        SetDataString(NAME, name)
    }

    val name: String?
        get() = GetDataString(NAME)

    fun setEmailProfile(googleProfile: String) {
        SetDataString(OUR_PROFILE, googleProfile)
    }

    val emailProfile: String?
        get() = GetDataString(OUR_PROFILE)


    fun setUserChoice(choice: String) {
        SetDataString(CHOICE, choice)
    }

    val userChoice: String?
        get() = GetDataString(CHOICE)


    fun setUserEmail(email: String) {
        SetDataString(EMAIL, email)
    }

    val userEmail: String?
        get() = GetDataString(EMAIL)

    fun setUserFName(name: String) {
        SetDataString(NAME, name)
    }

    val userFName: String?
        get() = GetDataString(NAME)

    fun setUserLName(lname: String) {
        SetDataString(L_NAME, lname)
    }

    val userLName: String?
        get() = GetDataString(L_NAME)

    fun setUserNumber(number: String) {
        SetDataString(NUMBER, number)
    }

    val userNumber: String?
        get() = GetDataString(NUMBER)

    fun setUserGroupName(grpName: String) {
        SetDataString(GRP_NAME, grpName)
    }

    val userGroupName: String?
        get() = GetDataString(GRP_NAME)

    fun setUserCity(city: String) {
        SetDataString(CITY, city)
    }

    val userCity: String?
        get() = GetDataString(CITY)

    fun setUserState(state: String) {
        SetDataString(STATE, state)
    }

    val userState: String?
        get() = GetDataString(STATE)

    fun setUserGroupId(groupId: String) {
        SetDataString(GROUP_ID, groupId)
    }

    val userGroupId: String?
        get() = GetDataString(GROUP_ID)

    fun setUserAddress(address: String) {
        SetDataString(ADDRESS, address)
    }

    val userAddress: String?
        get() = GetDataString(ADDRESS)

    fun setUserZip(zip: String) {
        SetDataString(ZIP, zip)
    }

    val userZip: String?
        get() = GetDataString(ZIP)

    fun setUserLatlng(latlng: String) {
        SetDataString(LATLNG, latlng)
    }

    val userLatLng: String?
        get() = GetDataString(LATLNG)

    fun setUserCreateDate(cdate: String) {
        SetDataString(CREATE_DATE, cdate)
    }

    val userCreateDate: String?
        get() = GetDataString(CREATE_DATE)

    fun setUserOccupation(occupation: String) {
        SetDataString(OCCUPATION, occupation)
    }

    val userOccupation: String?
        get() = GetDataString(OCCUPATION)


    var isNotificationMuted: Boolean
        get() = GetDataBoolean(NOTIFICATION_MUTED)
        set(notificationMuted) {
            SetDataBoolean(NOTIFICATION_MUTED, notificationMuted)
        }
    var Gender: String?
        get() = GetDataString(GENDER)
        set(gender) {
            SetDataString(GENDER, gender!!)
        }

    fun setJwtToken(points: String) {
        SetDataString(JWT_TOKEN, points)
    }

    val jwtToken: String?
        get() = GetDataString(JWT_TOKEN)

    fun setEnterOtp(otp: String) {
        SetDataString(OTP, otp)
    }

    val enterOtp: String?
        get() = GetDataString(OTP)

    //--------------------------------------Boolean Values--------------------------------------------
    //------------------------------------------------------------------------------------------------
    private fun GetDataString(key: String): String? {
        var cbValue: String? = null
        try {
            cbValue = getSharedPreferences(mContext).getString(key, "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cbValue
    }

    private fun GetDataStringZero(key: String): String? {
        var cbValue: String? = null
        try {
            cbValue = getSharedPreferences(mContext).getString(key, "0.0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cbValue
    }

    private fun SetDataString(key: String, value: String) {
        val edit = getSharedPreferences(mContext).edit()
        edit.putString(key, value)
        edit.commit()
    }

    private fun GetDataInt(key: String): Int {
        return getSharedPreferences(mContext).getInt(key, 0)
    }
    private fun GetDataFloat(key: String):Float {
        return getSharedPreferences(mContext).getFloat(key, 0.0f)
    }
    private fun SetDataFloat(key: String, value: Float) {
        val edit = getSharedPreferences(mContext).edit()
        edit.putFloat(key, value)
        edit.commit()
    }

    private fun SetDataInt(key: String, value: Int) {
        val edit = getSharedPreferences(mContext).edit()
        edit.putInt(key, value)
        edit.commit()
    }

    private fun GetDataLong(key: String): Long {
        return getSharedPreferences(mContext).getLong(key, 0)
    }

    private fun SetDataLong(key: String, value: Long) {
        val sp = getSharedPreferences(mContext)
        val edit = sp.edit()
        edit.putLong(key, value)
        edit.commit()
    }

    private fun GetDataBoolean(key: String): Boolean {
        val cbValue = getSharedPreferences(mContext).getBoolean(key, false)
        return cbValue
    }

    private fun SetDataBoolean(key: String, value: Boolean) {
        val edit = getSharedPreferences(mContext).edit()
        edit.putBoolean(key, value)
        edit.commit()
    }

    fun Logout() {

        getSharedPreferences(mContext).edit().clear().commit()
        LogoutPrefrences()
    }

    private fun LogoutPrefrences() {
        removePreferences(NAME, mContext)
        removePreferences(EMAIL, mContext)
        removePreferences(OUR_PROFILE, mContext)
        removePreferences(CLIENT_PROFILE, mContext)
        removePreferences(CLIENT_ID, mContext)
        removePreferences(MOBILE_NO, mContext)
        removePreferences(IS_ADD_MOB_SAVED, mContext)
        removePreferences(IS_LOGGED_IN, mContext)
        removePreferences(IS_REGISTER, mContext)
        removePreferences(USER_ID, mContext)
        removePreferences(FIREBASE_TOKEN, mContext)
        removePreferences(RINGTON_PATH, mContext)
        removePreferences(NOTIFICATION_MUTED, mContext)
        removePreferences(IS_LOGGED_IN_VIA_EMAIL, mContext)
        removePreferences(IS_LOGGED_IN_VIA_GOOGLE, mContext)
        removePreferences(IS_LOGGED_IN_VIA_FACEBOOK, mContext)
        setIsLogout(true)
    }

    companion object {
        private const val ITI = "Nearhud"
        private const val EMAIL = "email"
        private const val CHOICE = "choice"
        private const val NAME = "name"
        private const val GRP_NAME = "grp_name"
        private const val CITY = "city"
        private const val GROUP_ID = "group_id"
        private const val ADDRESS = "address"
        private const val OCCUPATION = "occupation"
        private const val ZIP = "zip"
        private const val CREATE_DATE = "cdate"
        private const val LATLNG = "latlng"
        private const val STATE = "state"
        private const val L_NAME = "lname"
        private const val NUMBER = "number"
        private const val OUR_PROFILE = "myProfilePicture"
        private const val CLIENT_PROFILE = "profile"
        private const val CLIENT_ID = "clientId"
        private const val MOBILE_NO = "mobile_no"
        private const val APP_BACKGROUND = "app_in_background"
        private const val IS_ADD_MOB_SAVED = "isSvedAddMobData"
        private const val IS_LOGGED_IN = "login"
        private const val IS_REGISTER = "register"
        private const val USER_ID = "userId"
        private const val SCREEN = "screen"
        private const val FIREBASE_TOKEN = "firebaseToken"
        private const val RINGTON_PATH = "rington"
        private const val NOTIFICATION_MUTED = "notification_muted"
        private const val IS_LOGGED_IN_VIA_EMAIL = "emailLoggedin"
        private const val IS_LOGGED_IN_VIA_GOOGLE = "googleLoggedIn"
        private const val IS_LOGGED_IN_VIA_FACEBOOK = "facebookLoggedin"
        private const val FB_ACCESS_TOKEN = "facebookToken"
        private const val JWT_TOKEN = "jwt"
        private const val OTP = "otp"
        private const val Is_LOGOUT = "logout"
        private const val THEMES = "themePref"
        private const val DARK_MODE = "darkmoded"
        private const val FONT = "fontsize"
        private const val ISFONTAPPLICABLE = "fontApplied"
        private const val ISPLANACTIVATED = "planActivated"
        private const val PLAN_NAME = "planName"
        private const val IS_BLOCKED = "isBlockedUser"
        private const val GENDER = "userGender"
        private const val PINCODE = "pincode"
        private var Instance: SharedPre? = null

        @Synchronized
        fun getInstance(context: Context): SharedPre? {
            if (Instance == null) {
                Instance = SharedPre(context)
            }
            return Instance
        }

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(ITI, Context.MODE_PRIVATE)
        }

        private fun removePreferences(key: String, cntxt: Context) {
            getSharedPreferences(cntxt).edit().remove(key).commit()
        }
    }

    init {
        if (Instance != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class( Mr.professional - Ishant ).")
        }
        mContext = context.applicationContext
    }
}