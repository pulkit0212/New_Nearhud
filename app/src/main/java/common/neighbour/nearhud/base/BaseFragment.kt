package common.neighbour.nearhud.base

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.base.RetrySnackBarClickListener
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.repositories.methods.MethodsRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseFragment : Fragment(){

    public var methods: MethodsRepo?=null
    private lateinit var snackBar: Snackbar
    private var addToBackStack = false
    private var manager: FragmentManager? = null
    private var transaction: FragmentTransaction? = null
    private var fragment: Fragment? = null
    abstract fun WorkStation();
    private lateinit var mActivity: BaseActivity
    private val internetDialog: Dialog? = null
    private  var dialog:Dialog? = null
    val CAMERA_REQUEST = 1888
    val SELECT_IMAGE = 1
    var finalFile: File? = null
    private var disposable: Disposable? = null
    private var rxPermissions: RxPermissions? = null
    val CAPTURE_IMAGE_REQUEST = 1
    val PICK_IMAGE_REQUEST = 2


    private val FIRBASE_REALTIME_URL="https://apponiti-default-rtdb.firebaseio.com/"
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val activity: BaseActivity = context as BaseActivity
            this.mActivity = activity
            activity.onFragmentAttached()
        }
    }
    fun GetSharedPre():SharedPre{
        return SharedPre.getInstance(requireActivity())!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance=true
        manager = mActivity.supportFragmentManager
        methods=mActivity.methods

    }
    open fun getBaseActivity(): BaseActivity? {
        return mActivity
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

    fun getSharedPre():SharedPre{
        return SharedPre.getInstance(requireContext())!!
    }

    open fun showCustomAlert(msg: String?, v: View?) {
        snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.getView()
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }
    open fun onBackPressed(){
        (mActivity ).onBackPressed()
    }
    open fun getRxPermissions(): RxPermissions? {
        if (rxPermissions == null) {
            rxPermissions = RxPermissions(requireActivity())
        }
        return rxPermissions
    }
    open fun showImgDialog(isCancelable: Boolean): Dialog? {
        dialog = Dialog(requireActivity())
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
                   Toast.makeText(requireContext(),"Required Camera Permission",Toast.LENGTH_LONG).show()
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
                    Toast.makeText(requireContext(),"Required Read Permission",Toast.LENGTH_LONG).show()
                }
            }
        dialog!!.setCancelable(isCancelable)
        return dialog
    }

     open fun captureFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
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
                    requireContext(), common.neighbour.nearhud.BuildConfig.APPLICATION_ID.toString() + ".provider",
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
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
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
                            requireContext(), common.neighbour.nearhud.BuildConfig.APPLICATION_ID.toString() + ".provider",
                            photoFile
                        )
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
        val builder = AlertDialog.Builder(requireActivity())
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
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    @Throws(IOException::class)
     open fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "DIZ-Q" + timeStamp + "_"
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        finalFile = File.createTempFile(mFileName, ".jpg", storageDir)
        return finalFile
    }
    open fun startFragment(fragment: Fragment?, backStackTag: String?, addToBackStack: Boolean) {
        if(manager==null){
            manager = requireActivity().supportFragmentManager
        }
        transaction = manager!!.beginTransaction()
        this.addToBackStack = addToBackStack
        transaction!!.addToBackStack(backStackTag)
        transaction!!.replace(R.id.container, fragment!!)
        transaction!!.commit()

    }
    open fun CustomstartFragment(fragment: Fragment?, backStackTag: String?, addToBackStack: Boolean) {
        if(manager==null){
            manager = requireActivity().supportFragmentManager
        }
        transaction = manager!!.beginTransaction()
        this.addToBackStack = addToBackStack
        transaction!!.replace(R.id.container, fragment!!)
        transaction!!.commit()

    }

    open fun startFragment(fragment: Fragment?, addToBackStack: Boolean, backStackTag: String?) {
        this.addToBackStack = addToBackStack
        if(manager==null){
            manager = requireActivity().supportFragmentManager
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
            manager!!.executePendingTransactions()
        }
    }
    fun loading(value:Boolean){
        if(value){
            methods!!.showLoadingDialog(requireContext())!!.show()
        }else{
            methods!!.showLoadingDialog(requireContext())!!.dismiss()
        }
    }
}