package orllewin.filmsimulationluts


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.google.android.renderscript.Rgba3dArray
import com.google.android.renderscript.Toolkit
import java.lang.Exception

/**
 * Converts Unreal format 256x16 LUT image to 3d Cube LUT
 */
class UnrealLutToolkit {

    private val unrealCube = LutUtils.Dimension(16, 16, 16)
    private var cubeArray: ByteArray? = null
    private var toolkitCube: Rgba3dArray? = null
    var lutBitmap: Bitmap? = null

    fun loadLut(context: Context, lut: Lut){
        lutBitmap = BitmapFactory.decodeResource(context.resources, lut.resourceId) ?: throw Exception("Invalid lut: ${lut.label}")
        cubeArray = generateLutCube(lutBitmap!!, unrealCube)
        toolkitCube = Rgba3dArray(cubeArray!!, unrealCube.sizeX, unrealCube.sizeY, unrealCube.sizeZ)
    }

    fun process(source: Bitmap?): Bitmap?{
        return when (source) {
            null -> null
            else -> Toolkit.lut3d(source, toolkitCube!!)
        }
    }

    private fun generateLutCube(lutBitmap: Bitmap, cubeSize: LutUtils.Dimension): ByteArray {
        val lutWidth = lutBitmap.width
        val lutHeight = lutBitmap.height
        val lutPixels = IntArray(lutWidth * lutHeight)
        lutBitmap.getPixels(lutPixels, 0, lutWidth, 0, 0, lutWidth, lutHeight)
        lutBitmap.recycle()//Done with Lut bitmap

        val data = ByteArray(cubeSize.sizeX * cubeSize.sizeY * cubeSize.sizeZ * 4)
        val cube = Rgba3dArray(data, cubeSize.sizeX, cubeSize.sizeY, cubeSize.sizeZ)

        for (red in 0 until 16) {
            for (green in 0 until 16) {
                val p = red + green * lutWidth
                for (blue in 0 until 16) {
                    val c = lutPixels[p + blue * 16]
                    cube[red, green, blue] = byteArrayOf(Color.red(c).toByte(), Color.green(c).toByte(), Color.blue(c).toByte(), (255).toByte())
                }
            }
        }

        return data
    }
}