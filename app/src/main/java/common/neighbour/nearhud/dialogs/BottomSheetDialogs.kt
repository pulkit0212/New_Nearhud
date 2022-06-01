package common.neighbour.nearhud.dialogs

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.google.android.material.bottomsheet.BottomSheetDialog
import common.neighbour.nearhud.R

/**
 *
 * */

object BottomSheetDialogs {


    fun getHomeThreeDotDialog(context: Context): Dialog {
        val dialog = BottomSheetDialog(context, R.style.DialogFragmentTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bottom_home_three_dot)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    fun getOccupationDialog(context: Context): Dialog {
        val dialog = BottomSheetDialog(context, R.style.DialogFragmentTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_occupation)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    fun getHomeFilterDialog(context: Context): Dialog {
        val dialog = BottomSheetDialog(context, R.style.DialogFragmentTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bottom_home_filter)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    fun getLogoutDialog(context: Context): Dialog {
        val dialog = BottomSheetDialog(context, R.style.DialogFragmentTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bottom_logout)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

}