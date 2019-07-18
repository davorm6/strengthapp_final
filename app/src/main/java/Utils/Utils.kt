package Utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun createSnackbar(view: View, text: String, color: Int) {
    val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
    val snackbarView = snackbar.view
    snackbarView.setBackgroundColor(color)
    snackbar.show()
}

fun bytesToBitmap(byteArray: ByteArray): Bitmap? {
    var stream: ByteArrayInputStream? = null;
    try {
        stream = ByteArrayInputStream(byteArray)
        return BitmapFactory.decodeStream(stream)
    }
    catch (e: Exception) {
        Log.d("Activity", "fail to decode")
        return null
    }
    finally {
        if(stream != null) {
            try {
                stream.close()
            }
            catch (e: Exception) {}
        }
    }
}

fun bitmapToBytes(bitmap: Bitmap): ByteArray {
    var stream: ByteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
    return stream.toByteArray()
}