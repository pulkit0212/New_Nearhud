package common.neighbour.nearhud.module

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.gson.Gson
import common.neighbour.nearhud.R
import common.neighbour.nearhud.retrofit.model.post.Data
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object Utility {


    fun getCommentTimeGap(created: Long): String {
        var timeGap = System.currentTimeMillis() - created
        //Minutes
        var timeGapInMin = timeGap / (1000 * 60)
        if (timeGapInMin < 60) {
            return "${timeGapInMin}m"
        }
        //Hour
        var timeGapInHr = timeGapInMin / 60
        if (timeGapInHr < 24) {
            return "${timeGapInHr}hr"
        }
        //Days
        var timeGapInDays = timeGapInHr / 24
        if (timeGapInDays < 7) {
            return "${timeGapInDays}d"
        }
        //Weeks
        var timeGapInWeeks = timeGapInDays / 7
        if (timeGapInWeeks < 7) {
            return "${timeGapInWeeks}w"
        }
        //Months
        var timeGapInMonths = timeGapInWeeks / 4
        if (timeGapInMonths < 12) {
            return "${timeGapInMonths}M"
        }
        var timeGapInYears = timeGapInMonths / 12
        return "${timeGapInYears}y"
        //Years
        return " "
    }

    fun getTimeDay(created:Long):String{
        var timeGap = System.currentTimeMillis() - created
        //Minutes
        var timeGapInMin = timeGap / (1000 * 60)
        //Hour
        var timeGapInHr = timeGapInMin / 60
        //Days
        var timeGapInDays = timeGapInHr / 24

        return timeGapInDays.toString()
    }

    fun getTimeGap(created: Long): String {
        var timeGap = System.currentTimeMillis() - created
        //Minutes
        var timeGapInMin = timeGap / (1000 * 60)
        if (timeGapInMin < 60) {
            return if (timeGapInMin.toInt() == 1) {
                "a minute ago"
            } else {
                "$timeGapInMin minutes ago"
            }
        }
        //Hour
        var timeGapInHr = timeGapInMin / 60
        if (timeGapInHr < 24) {
            return if (timeGapInHr.toInt() == 1) {
                "an hour ago"
            } else {
                "$timeGapInHr hours ago"
            }
        }
        //Days
        var timeGapInDays = timeGapInHr / 24
        if (timeGapInDays < 7) {
            return if (timeGapInDays.toInt() == 1) {
                "a day ago"
            } else {
                "$timeGapInDays days ago"
            }
        }
        //Weeks
        var timeGapInWeeks = timeGapInDays / 7
        if (timeGapInWeeks < 7) {
            return if (timeGapInWeeks.toInt() == 1) {
                "a week ago"
            } else {
                "$timeGapInWeeks weeks ago"
            }
        }
        //Months
        var timeGapInMonths = timeGapInWeeks / 4
        if (timeGapInMonths < 12) {
            return if (timeGapInMonths.toInt() == 1) {
                "a month ago"
            } else {
                "$timeGapInMonths months ago"
            }
        }
        var timeGapInYears = timeGapInMonths / 12
        return if (timeGapInYears.toInt() == 1) {
            "a year ago"
        } else {
            "$timeGapInYears years ago"
        }
        //Years
        return " "
    }



    fun getPostHomeObject(id: Int): Data {
        return Data(
            id,
            "",
            "",
            0,
            0,
            "",
            "",
            "",
            "",
            "", "", "", "", null,
            0,
            false,
            false,
            "",
            ""
        )
    }

    fun convertToRequestBody(params: Map<String, Any>): RequestBody? {
        var requestBody: RequestBody? = null
        try {
            requestBody = Gson().toJson(params)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return requestBody
    }

    fun getCustomTab(context: Context, position: Int): View {
        var view = LayoutInflater.from(context).inflate(R.layout.item_search_tab, null)
        when (position) {
            0 -> {
                val ivTab = view.findViewById<ImageView>(R.id.ivTabIcon)
                ivTab.setImageDrawable(context.resources.getDrawable(tabIcons[0]))
            }
            1 -> {
                val ivTab = view.findViewById<ImageView>(R.id.ivTabIcon)
                ivTab.setImageDrawable(context.resources.getDrawable(tabIcons[1]))
            }
        }
        return view
    }

    fun getSelectedCustomTab(context: Context, position: Int): View {
        var view = LayoutInflater.from(context).inflate(R.layout.item_search_tab, null)
        when (position) {
            0 -> {
                val ivTab = view.findViewById<ImageView>(R.id.ivTabIcon)
                ivTab.setImageDrawable(context.resources.getDrawable(tabIconsActive[1]))
            }
            1 -> {
                val ivTab = view.findViewById<ImageView>(R.id.ivTabIcon)
                ivTab.setImageDrawable(context.resources.getDrawable(tabIconsActive[1]))
            }
        }
        return view
    }
    private val tabIcons = intArrayOf(
        R.drawable.svg_tab_images,
        R.drawable.svg_tab_text
    )

    private val tabIconsActive = intArrayOf(
        R.drawable.svg_tab_images_active,
        R.drawable.svg_tab_text_active
    )

//    fun convertToRequestBody(obj: SelectedUsersList): RequestBody? {
//        var requestBody: RequestBody? = null
//        try {
//            requestBody = Gson().toJson(obj)
//                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return requestBody
//    }
}