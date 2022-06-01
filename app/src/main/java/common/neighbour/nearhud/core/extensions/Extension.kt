package common.neighbour.nearhud.core.extensions

import android.Manifest
import common.neighbour.nearhud.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.location.Geocoder
import android.location.Location
import android.media.AudioManager
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Patterns
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resumeWithException

var Menu.visibility: Boolean
    get() = false
    set(value) {
        iterator().forEach {
            it.isVisible = value
        }
    }

//@SuppressLint("ResourceType")
//fun Fragment.showLoader(rootView: ViewGroup) {
//    if (rootView.findViewById<LottieAnimationView>(12345) == null) {
//        val loaderAnimation = LottieAnimationView(this).apply {
//            id = 12345
//            elevation = 20.toPx().toFloat()
//            setAnimation(R.raw.loader)
//            repeatMode = LottieDrawable.INFINITE
//            loop(true)
//            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            playAnimation()
//            scaleType = ImageView.ScaleType.CENTER_INSIDE
//            background = ContextCompat.getDrawable(rootView.context,R.drawable.loader_backgorud)
//        }
//        rootView.addView(loaderAnimation)
//    }
//}

//@SuppressLint("ResourceType")
//fun removeLoader(rootView: ViewGroup) {
//    rootView.alpha = 1f
//    val animationView = rootView.findViewById<LottieAnimationView>(12345)
//    if (animationView != null) {
//        rootView.removeView(animationView)
//    }
//}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Fragment.getAsColor(@ColorRes resId: Int) = ContextCompat.getColor(requireActivity(), resId)

fun Fragment.getAsDrawable(@DrawableRes resId: Int) =
    ContextCompat.getDrawable(requireActivity(), resId)!!

fun Activity.getAsDrawable(@DrawableRes resId: Int) =
    ContextCompat.getDrawable(this, resId)!!

fun Fragment.showToast(msg: String = "Something went wrong , try again") {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(@StringRes msg: Int) {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
}

suspend fun Fragment.showCoroutineToast(@StringRes msg: Int) {
    withContext(Dispatchers.Main.immediate) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
    }
}

// hide keyboard
fun Fragment.hideSoftKeyboard() {
    val imm =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

@SuppressLint("ClickableViewAccessibility")
fun View.doOnDoubleTap(action: () -> Unit) {
    val doubleTapListener = object : GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(e: MotionEvent?) = false

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            action.invoke()
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            action.invoke()
            return true
        }
    }
    val gestureDetector =
        GestureDetectorCompat(context, object : GestureDetector.OnGestureListener {
            override fun onDown(e: MotionEvent?) = false

            override fun onShowPress(e: MotionEvent?) {

            }

            override fun onSingleTapUp(e: MotionEvent?) = false

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ) = false

            override fun onLongPress(e: MotionEvent?) {

            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) = false
        })
    gestureDetector.setOnDoubleTapListener(doubleTapListener)
    setOnTouchListener { v, event ->
        gestureDetector.onTouchEvent(event)
        return@setOnTouchListener true
    }
}

fun Uri.toImageFile(context: Context, fileName: String) {
    val outputStream = context.contentResolver.openInputStream(this)
    outputStream.use {
        val buffer = ByteArray(4 * 1024)
        val file = File.createTempFile(fileName, ".jpg")
        while (true) {
            val byteCount = outputStream?.read(buffer)

        }
    }
}

fun Int.secondsToTimeFormat(): String? {
    return DateUtils.formatElapsedTime(this.toLong())
}

fun View.setOnDoubleClickListener(onDoubleClick: (View) -> Unit) {
    val safeClickListener = DoubleClickListener {
        onDoubleClick(it)
    }
    setOnClickListener(safeClickListener)
}

class DoubleClickListener(
    private var defaultInterval: Int = 300,
    private val onDoubleClick: (View) -> Unit
) : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < defaultInterval) {
            onDoubleClick(v)
            lastClickTime = 0
        }
        lastClickTime = clickTime
    }
}

fun View.animateScale(from: Int, to: Int) {
    val valueAnimator = ValueAnimator.ofInt(from, to)
    valueAnimator.duration = 1000
    valueAnimator.addUpdateListener {
        val animatedValue = it.animatedValue as Int
        scaleX = animatedValue / 50f
        scaleY = animatedValue / 50f
    }
    valueAnimator.start()
}

fun Fragment.setVolume(level: Int) {
    val audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0)
}

fun TextView?.lengthOfTextBeforeEllipsize(): String? {

        // test that we have a textview and it has text
        if (this == null || TextUtils.isEmpty(getText())) return null;
        val l = getLayout();
        if (l!=null) {
            // find the last visible position
            val end = l.getLineEnd(getMaxLines() - 1);
            // get only the text after that position
            return getText().toString().substring(end);
        }
        return null;
}

fun String.linkify(linkColor:Int = Color.BLUE,linkClickAction:((String) -> Unit)? = null): SpannableStringBuilder {
    val builder = SpannableStringBuilder(this)
    val matcher = Patterns.WEB_URL.matcher(this)
    while(matcher.find()){
        val start = matcher.start()
        val end = matcher.end()
        builder.setSpan(ForegroundColorSpan(Color.BLUE),start,end,0)
        val onClick = object : ClickableSpan(){
            override fun onClick(p0: View) {
                    linkClickAction?.invoke(matcher.group())
            }
        }
        //builder.setSpan(onClick,start,end,0)
    }
    return builder
}
fun ImageView.likeDoubleTap() {

    this.visibility = View.VISIBLE
    val animSet = AnimatorSet()
    animSet.duration = 200
    animSet.playTogether(
        ObjectAnimator.ofFloat(this, "alpha", 0f, 1f),
        ObjectAnimator.ofFloat(this, "scaleX", 0.8f, 1.4f),
        ObjectAnimator.ofFloat(this, "scaleY", 0.8f, 1.4f)
    )
    val animSet2 = AnimatorSet()
    animSet2.duration = 100
    animSet2.playTogether(
        ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.9f),
        ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.9f)
    )
    val animSet3 = AnimatorSet()
    animSet2.duration = 50
    animSet2.startDelay = 400
    animSet2.playTogether(
        ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
    )
    val animSetFinal = AnimatorSet()
    animSetFinal.playSequentially(animSet, animSet2, animSet3)
    animSetFinal.start()
}

fun ImageView.flipDrawable(drawableRes1 : Int,drawableRes2 : Int){
    if(tag != drawableRes1 && tag!= drawableRes2){
        tag = drawableRes1
        setImageDrawable(ContextCompat.getDrawable(context,drawableRes1))
    }else{
        if(tag == drawableRes1){
            tag = drawableRes2
            setImageDrawable(ContextCompat.getDrawable(context,drawableRes2))
        }else{
            tag = drawableRes1
            setImageDrawable(ContextCompat.getDrawable(context,drawableRes1))
        }
    }

}

//fun VneGroup.allAreValid():Boolean{
//    referencedViews.forEach {
//        if(!it.isValid){
//            return  false
//        }
//    }
//    return true
//}
//
//fun VneGroup.allAreNotValid():Boolean{
//    referencedViews.forEach {
//        if(!it.isValid){
//            return true
//        }
//    }
//    return false
//}
//
//fun VneGroup.firstInvalidChild():ValidationNotifierEditText?{
//    referencedViews.forEach {
//        if(!it.isValid){
//            return it
//        }
//    }
//    return null
//}

//@SuppressLint("MissingPermission")
//suspend fun Fragment.awaitLastLocation(): Location? {
//    val fusedLocationProviderClient =
//        LocationServices.getFusedLocationProviderClient(requireActivity())
//    return suspendCoroutine { continuation ->
//        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
//            .addOnSuccessListener { location ->
//                continuation.resume(location)
//            }.addOnFailureListener { exception ->
//            exception.printStackTrace()
//            continuation.resumeWithException(exception)
//        }
//    }
//}


fun MotionLayout.doOnTransitionCompleted(action: (motionLayout: MotionLayout, id: Int) -> Unit) {
    addTransitionListener(object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

        }

        override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

        }

        override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            action.invoke(this@doOnTransitionCompleted, p1)
        }

        override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

        }
    })
}

fun Fragment.getAddressForLocation(location: Location): String {

        val geoCoder = Geocoder(requireActivity(), Locale.getDefault())
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        println(address)
        val addresses = address[0].getAddressLine(0).split(",")
        return addresses[addresses.size - 4] + "," + addresses[addresses.size - 3] + "," + addresses[addresses.size - 2].replace(
            "[0-9]+".toRegex(),
            ""
        )
}


// check location on off
fun Fragment.locationPermissionAllowed() = ActivityCompat.checkSelfPermission(
    requireActivity(),
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
    requireActivity(),
    Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED

// check location on off
fun Activity.locationPermissionAllowed() = ActivityCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED

fun Fragment.locationPermissionIsNotAllowed() = !(ActivityCompat.checkSelfPermission(
    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
    requireActivity(),
    Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED)

fun Int.formatToString(): String {
    if (this >= 1000) {
        val thousands = this / 1000
        val hundreds = this % 1000
        return "$thousands${hundreds.toString().substring(0, 1)}"
    }
    return this.toString()
}

fun Int.toKsuffix() = if (this < 1000) toString() else (this / 1000).toString() + "k"

suspend fun Fragment.showGpsDialog(): Boolean {
    val locationSettingsRequest = LocationSettingsRequest
        .Builder()
        .setAlwaysShow(true)
        .addLocationRequest(
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        )
        .build()
    val settingsClient = LocationServices.getSettingsClient(requireActivity())
    var settingsResult: LocationSettingsResponse? = null
    val deferred = lifecycleScope.async {
        try {
            settingsResult =
                settingsClient.checkLocationSettings(locationSettingsRequest).awaitGpsState()
        } catch (e: ApiException) {
            val resolvableException = e as ResolvableApiException
            startIntentSenderForResult(
                resolvableException.resolution.intentSender,
                123,
                null,
                0,
                0,
                0,
                null
            )
        } catch (e: Exception) {
        }
    }
    deferred.await()
    if (settingsResult == null) {
        return false
    }
    return settingsResult!!.locationSettingsStates.isGpsUsable
}

suspend fun Activity.showGpsDialog(): Boolean {
    val locationSettingsRequest = LocationSettingsRequest
        .Builder()
        .setAlwaysShow(true)
        .addLocationRequest(
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        )
        .build()
    val settingsClient = LocationServices.getSettingsClient(this)
    var settingsResult: LocationSettingsResponse? = null
    try {
        settingsResult =
            settingsClient.checkLocationSettings(locationSettingsRequest).awaitGpsState()
    } catch (e: ApiException) {
        val resolvableException = e as ResolvableApiException
        startIntentSenderForResult(
            resolvableException.resolution.intentSender,
            123,
            null,
            0,
            0,
            0,
            null
        )
    } catch (e: Exception) {
    }
   // deferred.await()
    if (settingsResult == null) {
        return false
    }
    return settingsResult!!.locationSettingsStates.isGpsUsable
}
//
//// Send location updates to the consumer
//@ExperimentalCoroutinesApi
//@SuppressLint("MissingPermission")
//fun FusedLocationProviderClient.locationFlow() = callbackFlow<Location> {
//
//    val callback = object : LocationCallback() {
//        override fun onLocationResult(result: LocationResult?) {
//            result ?: return
//            for (location in result.locations) {
//                try {
//                    trySend(location)
//                } catch (t: Throwable) {
//                }
//            }
//        }
//    }
//    requestLocationUpdates(
//        LocationRequest.create(),
//        callback,
//        Looper.getMainLooper()
//    ).addOnFailureListener { e ->
//        close(e)
//    }
//    awaitClose {
//        removeLocationUpdates(callback)
//    }
//}

suspend fun Task<LocationSettingsResponse>.awaitGpsState() =
    suspendCancellableCoroutine<LocationSettingsResponse> { continuation ->
        addOnSuccessListener {
            val result: LocationSettingsResponse
            try {
                result = getResult(ApiException::class.java)
                continuation.resume(result, null)
            } catch (e: ApiException) {
                continuation.resumeWithException(e)
            }
        }
        addOnFailureListener {
            continuation.resumeWithException(it)
        }
    }

// recieve in
fun String.decode(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)
}

//send in --
fun String.encode(): String {
    return Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

fun Long.toDateAndTimeString(): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    return sdf.format(Date(this))
}

//fun Long.toDate() = org.threeten.bp.LocalDate.ofEpochDay(this)

fun disableTouch(activity: Activity) {
    activity.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun enableTouch(activity: Activity) {
    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

//@ExperimentalCoroutinesApi
//fun EditText.afterTextChangedFlow(debounce: Long): Flow<String> = callbackFlow<String> {
//    val watcher = object : TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//            trySend(s.toString())
//        }
//
//        override fun beforeTextChanged(
//            s: CharSequence?,
//            start: Int, count: Int, after: Int
//        ) {
//        }
//
//        override fun onTextChanged(
//            s: CharSequence?,
//            start: Int, before: Int, count: Int
//        ) {
//        }
//    }
//    addTextChangedListener(watcher)
//    awaitClose { removeTextChangedListener(watcher) }
//}.debounce(debounce)

/**
 * returns a bitmap from the given string if the string is a valid image url
 * @return null , if bitmap can not be loaded from the string, possible image is not image url
 */
fun String.getBitmapIfImageUrl(): Bitmap? {
    return try {
        val url = URL(this)
        BitmapFactory.decodeStream(url.openConnection().getInputStream())
    } catch (e: IOException) {
        null
    }
}

@SuppressLint("ClickableViewAccessibility")
inline fun View.doOnMotionEventUp(crossinline action: () -> Unit) {
    setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            action.invoke()
        }
        return@setOnTouchListener true
    }
}

fun List<LatLng>.toStaticMapApiFormat() =
    buildString {
        if (size >= 2) {
            for (i in 0..size - 2) {
                val latLng = this@toStaticMapApiFormat[i]
                append("${latLng.latitude},${latLng.longitude}|")
            }
            val latLng = this@toStaticMapApiFormat[size - 1]
            append("${latLng.latitude},${latLng.longitude}")
        }
    }



fun InputStream.putIn(file: File): File {
    use { iStream ->
        val outputStream = FileOutputStream(file)
        outputStream.use { oStream ->
            val buffer = ByteArray(4 * 1024)
            while (true) {
                val byteCount = read(buffer)
                if (byteCount < 0) break
                oStream.write(buffer, 0, byteCount)
            }
            oStream.flush()
        }
    }
    return file
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Fragment.convertToFile(uri: Uri, fileName: String, extension: String): File? {
    val inputStream = requireActivity().contentResolver.openInputStream(uri)
    val file = File.createTempFile(fileName, extension)
    return inputStream?.putIn(file)
}
fun Activity.convertToFile(uri: Uri, fileName: String, extension: String): File {
    val inputStream = this.contentResolver.openInputStream(uri)
    val file = File.createTempFile(fileName, extension)
    return inputStream!!.putIn(file)
}

fun Fragment.getExtension(uri: Uri): String? {
    val cR = requireActivity().contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(uri))
}

fun Activity.getExtension(uri: Uri): String? {
    val cR = this.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(uri))
}

fun Bitmap.blendWith(otherBitmap: Bitmap): Bitmap {
    val blended = Bitmap.createBitmap(otherBitmap.width, otherBitmap.height, otherBitmap.config)
    for (i in 0..otherBitmap.width) {
        for (j in 0..otherBitmap.height) {
            val p1 = otherBitmap.getPixel(i, j)
            val p2 = getPixel(i, j)

            val r1 = Color.red(p1)
            val g1 = Color.green(p1)
            val b1 = Color.blue(p1)

            val r2 = Color.red(p2)
            val g2 = Color.green(p2)
            val b2 = Color.blue(p2)

            val r = (r1 + r2) / 2
            val g = (g1 + g2) / 2
            val b = (b1 + b2) / 2
            blended.setPixel(i, j, Color.argb(Color.alpha(p1), r, g, b))
        }
    }
    return blended
}

fun Toolbar.centerTitle() {
    doOnLayout {
        children.forEach {
            if (it is TextView) {
                it.x = width / 2f - it.width / 2f
                it.typeface = ResourcesCompat.getFont(context, R.font.poppins_semi_bold)
                return@forEach
            }
        }
    }
}
