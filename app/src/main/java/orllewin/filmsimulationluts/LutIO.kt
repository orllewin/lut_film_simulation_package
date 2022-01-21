package orllewin.filmsimulationluts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class LutIO {

    var decodedLabel: String? = null
    var decodedFilename: String? = null
    var decodedBitmap: Bitmap? = null

    fun isValid(encoded: String): Boolean = encoded.count { char -> char == '|' } == 2

    fun lentoDecode(encoded: String, onReady: () -> Unit){
        val segments = encoded.split("|")
        decodedLabel = segments.first()
        decodedFilename = segments[1]
        val bytes = Base64.getDecoder().decode(segments.last())
        decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        onReady()
    }

    fun lentoEncode(context: Context, lut: Lut): String{
        val lutBitmap = BitmapFactory.decodeResource(context.resources, lut.resourceId) ?: throw Exception("Invalid lut: ${lut.label}")
        val stream = ByteArrayOutputStream()
        lutBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()


        val base64 = Base64.getEncoder().encodeToString(byteArray)
        return "${lut.label}|${lut.toFilename()}|$base64"
    }
}