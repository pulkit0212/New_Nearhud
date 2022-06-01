package common.neighbour.nearhud.base

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import common.neighbour.nearhud.BuildConfig
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding3.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.ui.home.ui.MainFragment
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.repositories.methods.MethodsRepo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    private var addToBackStack = false
    private var manager: FragmentManager? = null
    private var transaction: FragmentTransaction? = null
    private var fragment: Fragment? = null
    private var rxPermissions: RxPermissions? = null
    private var doubleBackToExitPressedOnce = false
    private var auth: FirebaseAuth? = null
    lateinit var snackBar: Snackbar
    private lateinit var actiVityView: View
    private  var dialog: Dialog? = null
    val CAMERA_REQUEST = 1888
    val SELECT_IMAGE = 1
    val CAPTURE_IMAGE_REQUEST = 1
    val PICK_IMAGE_REQUEST = 2
    var finalFile: File? = null
    private var disposable: Disposable? = null
    @Inject
    lateinit var methods: MethodsRepo
//    @Inject
//    lateinit var sharedPre: SharedPre
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    fun sharedPre():SharedPre{
        return SharedPre.getInstance(this)!!
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        manager = supportFragmentManager
    }
//    open fun setView(view: View){
//        this.actiVityView=view
//    }
    open fun onFragmentAttached() {}

    fun loading(value:Boolean){
        if(value){
            methods.showLoadingDialog(this)!!.show()
        }else{
            methods.showLoadingDialog(this)!!.dismiss()
        }
    }


    override fun finish() {
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    open fun GetApplicationContext(): Context? {
        return applicationContext
    }

    open fun getRxPermissions(): RxPermissions? {
        if (rxPermissions == null) {
            rxPermissions = RxPermissions(this)
        }
        return rxPermissions
    }


    open fun startFragment(fragment: Fragment?, backStackTag: String?, addToBackStack: Boolean) {
        if(manager==null){
            manager = supportFragmentManager
        }
        transaction = manager!!.beginTransaction()
        this.addToBackStack = addToBackStack
        transaction!!.addToBackStack(backStackTag)
        transaction!!.replace(R.id.container, fragment!!)
        if (!isFinishing && !isDestroyed) {
            transaction!!.commit()
        }
    }

    open fun startFragment(fragment: Fragment?, addToBackStack: Boolean, backStackTag: String?) {
        this.addToBackStack = addToBackStack
        if(manager==null){
            manager = supportFragmentManager
        }
        val fragmentPopped = manager!!.popBackStackImmediate(backStackTag, 0)
        if (!fragmentPopped) {
            transaction = manager!!.beginTransaction()
            if (addToBackStack) {
                transaction!!.addToBackStack(backStackTag)
            } else {
                transaction!!.addToBackStack(null)
            }
            transaction!!.replace(R.id.container, fragment!!)
            transaction!!.commit()
        }
    }

    open fun startFragment(
        fragment: Fragment?,
        addToBackStack: Boolean,
        backStackTag: String?,
        wantAnimation: Boolean
    ) {
        if(manager==null){
            manager = supportFragmentManager
        }
        this.addToBackStack = addToBackStack
        val fragmentPopped = manager!!.popBackStackImmediate(backStackTag, 0)
        if (!fragmentPopped) {
            transaction = manager!!.beginTransaction()
            if (wantAnimation) {
               // transaction!!.setCustomAnimations(R.anim.slide_right, R.anim.slide_left, 0, 0)
            }
            if (addToBackStack) {
                transaction!!.addToBackStack(backStackTag)
            } else {
                transaction!!.addToBackStack(null)
            }
            transaction!!.replace(R.id.container, fragment!!)
            transaction!!.commit()
        }
    }

    open fun showCustomAlert(
        msg: String?,
        v: View?,
        button: String?,
        isRetryOptionAvailable: Boolean,
        listener: RetrySnackBarClickListener
    ) {
        if (isRetryOptionAvailable) {
            snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
                .setAction(button) { listener.onClickRetry() }
        } else {
            snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        }
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.getView()
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    open fun showCustomAlert(msg: String?, v: View?) {
        snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.getView()
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    override fun onBackPressed() {
        fragment = getCurrentFragment()
        if(manager==null){
            manager = supportFragmentManager
        }
        if (addToBackStack) {
            if (fragment is MainFragment) {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    return
                }
                doubleBackToExitPressedOnce = true
                //showCustomAlert("Press back again", actiVityView)
                Toast.makeText(this, "Press back again", Toast.LENGTH_SHORT)
                lifecycleScope.launch(Dispatchers.Default) {
                    delay(2000)
                    doubleBackToExitPressedOnce = false
                }
            } else {
                if (manager != null && manager!!.backStackEntryCount > 0) {
                    manager!!.popBackStackImmediate()
                } else {
                    super.onBackPressed()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    open fun getCurrentFragment(): Fragment? {
        fragment = supportFragmentManager.findFragmentById(R.id.container)
        return fragment
    }

    open fun hideSoftKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    open fun startActivityWithAnimation(intent: Intent?, animationType: Int) {
        startActivity(intent)
        when (animationType) {
            Appconstants.SLIDE_IN_RIGHT -> overridePendingTransition(
                R.anim.slide_right,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_BOTTOM -> overridePendingTransition(
                R.anim.slide_down,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_LEFT -> overridePendingTransition(
                R.anim.slide_left,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_TOP -> overridePendingTransition(
                R.anim.slide_up,
                R.anim.slide_out_up
            )
            Appconstants.SCALE -> overridePendingTransition(R.anim.grow_in, R.anim.grow_out)
        }
    }


    open fun startActivityForResultWithAnimation(
        intent: Intent?,
        requestCode: Int,
        animationType: Int
    ) {
        startActivityForResult(intent, requestCode)
        when (animationType) {
            Appconstants.SLIDE_IN_RIGHT -> overridePendingTransition(
                R.anim.slide_right,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_BOTTOM -> overridePendingTransition(
                R.anim.slide_down,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_LEFT -> overridePendingTransition(
                R.anim.slide_left,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_TOP -> overridePendingTransition(
                R.anim.slide_up,
                R.anim.slide_out_up
            )
            Appconstants.SCALE -> overridePendingTransition(R.anim.grow_in, R.anim.grow_out)
        }
    }
    open fun showImgDialog(isCancelable: Boolean): Dialog? {
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (isCancelable) {
            dialog!!.setCancelable(true)
        } else {
            dialog!!.setCancelable(false)
        }
        dialog!!.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        dialog!!.setContentView(R.layout.upload_img_lay)
        val camera: ImageView = dialog!!.findViewById(R.id.img_camera)
        val gallery: ImageView = dialog!!.findViewById(R.id.img_gallery)
        val close: ImageView = dialog!!.findViewById(R.id.closeAttach)
        close.setOnClickListener { v: View? -> dialog!!.dismiss() }
        disposable = camera.clicks()
            .compose<Boolean>(getRxPermissions()!!.ensure<Unit>(Manifest.permission.CAMERA))
            .observeOn(AndroidSchedulers.mainThread()).subscribe { aBoolean ->
                if (aBoolean) {
                    dialog!!.dismiss()
                    captureFromCamera()
                } else {
                    Toast.makeText(this,"Required Camera Permission", Toast.LENGTH_LONG).show()
                }
            }
        disposable = gallery.clicks()
            .compose<Boolean>(getRxPermissions()!!.ensure<Unit>(Manifest.permission.READ_EXTERNAL_STORAGE))
            .observeOn(AndroidSchedulers.mainThread()).subscribe { aBoolean ->
                if (aBoolean) {
                    var pictureActionIntent: Intent? = null
                    pictureActionIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pictureActionIntent, SELECT_IMAGE)
                    dialog!!.dismiss()
                } else {
                    Toast.makeText(this,"Required Read Permission", Toast.LENGTH_LONG).show()
                }
            }
        dialog!!.setCancelable(isCancelable)
        return dialog
    }

    open fun captureFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this, common.neighbour.nearhud.BuildConfig.APPLICATION_ID.toString() + ".provider",
                    photoFile
                )
                finalFile = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST)
            }
        }
    }

    open fun showImagePickerOptions() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
//                    startActivityForResult(
//                        takePictureIntent,
//                        CAPTURE_IMAGE_REQUEST
//                    )

                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            this, BuildConfig.APPLICATION_ID + ".provider",
                            photoFile)
                        finalFile = photoFile
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                }
            } else if (options[item] == "Choose from Gallery") {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select an Image"),
                    PICK_IMAGE_REQUEST
                )
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    open fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    open fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    @Throws(IOException::class)
    open fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "DIZ-Q" + timeStamp + "_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        finalFile = File.createTempFile(mFileName, ".jpg", storageDir)
        return finalFile
    }
}