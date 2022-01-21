package orllewin.filmsimulationluts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64.encodeToString
import android.view.View
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*


fun String.capitaliseAll(): String{
   return split(" ").map { word ->
       word.replaceFirstChar { char ->
           if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
       }
   }.joinToString(" ")
}

fun View.show(){
    visibility = View.VISIBLE
}

fun View.hide(){
    visibility = View.GONE
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun Bitmap.toBase64String():String{
    return Base64.getEncoder().encodeToString(this.toByteArray())
}

fun ByteArray.toBitmap(): Bitmap{
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}