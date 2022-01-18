package orllewin.file_io

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build
import androidx.annotation.RawRes
import java.io.File

class ImageIO {

    fun bitmap(file: File): Bitmap?{
        val opt = BitmapFactory.Options()
        opt.inMutable = true
        return BitmapFactory.decodeStream(file.inputStream(), null, opt)
    }

    fun bitmap(context: Context, resourceId: Int): Bitmap?{
        val inputStream = context.resources.openRawResource(resourceId)
        return BitmapFactory.decodeStream(inputStream)
    }

    fun imageDimens(file: File): Pair<Int, Int> {

        val inputStream = file.inputStream()
        val decoder = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> BitmapRegionDecoder.newInstance(inputStream)
            else -> BitmapRegionDecoder.newInstance(inputStream, false)
        }

        return when (decoder) {
            null -> Pair(-1, -1)
            else -> Pair(decoder.width, decoder.height)
        }
    }

    fun imageDimens(context: Context, @RawRes resourceId: Int): Pair<Int, Int>{

        val inputStream = context.resources.openRawResource(resourceId)
        val decoder = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> BitmapRegionDecoder.newInstance(inputStream)
            else -> BitmapRegionDecoder.newInstance(inputStream, false)
        }

        return when (decoder) {
            null -> Pair(-1, -1)
            else -> Pair(decoder.width, decoder.height)
        }
    }

    fun pixelRow(file: File, row: Int): Bitmap{
        val inputStream = file.inputStream()
        val decoder = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> BitmapRegionDecoder.newInstance(inputStream)
            else -> BitmapRegionDecoder.newInstance(inputStream, false)
        } ?: throw Exception("Could not create Bitmap decoder")

        if(row >= decoder.height) throw Exception("Requested row is greater than image height")

        return  decoder.decodeRegion(Rect(0, row, decoder.width, row +1), BitmapFactory.Options())
    }

    fun pixelRow(context: Context, @RawRes resourceId: Int, row: Int): Bitmap{
        val inputStream = context.resources.openRawResource(resourceId)
        val decoder = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> BitmapRegionDecoder.newInstance(inputStream)
            else -> BitmapRegionDecoder.newInstance(inputStream, false)
        } ?: throw Exception("Could not create Bitmap decoder")

        if(row >= decoder.height) throw Exception("Requested row is greater than image height")

        return  decoder.decodeRegion(Rect(0, row, decoder.width-1, row +1), BitmapFactory.Options())
    }

    fun writeRow(context: Context, file: File, row: Int, bitmap: Bitmap){

    }
}