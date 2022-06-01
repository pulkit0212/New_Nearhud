package common.neighbour.nearhud.newUi

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.io.*
import java.nio.charset.Charset
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utils {


    private fun loadJSONFromAsset(context: Activity): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = context.assets.open("countries.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, "UTF-8" as Charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    // Snakbar
    fun snackbar(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    //toast
    fun toast(ctx: Context, msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
    }

    fun convertStringToBitmap(img: String): Bitmap {
        val byt: ByteArray = Base64.decode(img, Base64.DEFAULT)
        Log.d("TAGU1", "convertStringToBitmap: ${byt}")
        return BitmapFactory.decodeByteArray(byt, 0, byt.size, null)
    }

    fun convertBitmapToPng(bit: Bitmap) {
        val filename = "MyImage.png"
        val file: File = Environment.getExternalStorageDirectory()
        val dest = File(file, filename)

        try {
            val out = FileOutputStream(dest)
            bit.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bitmapToFile(
        bitmap: Bitmap,
        fileNameToSave: String
    ): File { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file!! // it will return null
        }
    }

    fun getPathFromUri(uri: Uri?, act: Activity): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            act.contentResolver.query(uri!!, projection, null, null, null) ?: return null
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(column_index)
        cursor.close()
        return s
    }

    fun validUserName(username: String?): Boolean {
        var regexUsername = "^[a-zA-Z0-9_.]{5,20}$"
        return username!!.matches(regexUsername.toRegex())
    }
    fun formateDate(time: String?, inputFormate: String?, outputFormate: String?): String? {
        val inputFormat = SimpleDateFormat(inputFormate)
        val outputFormat = SimpleDateFormat(outputFormate)
        var date: Date? = null
        var str = time
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }
}

