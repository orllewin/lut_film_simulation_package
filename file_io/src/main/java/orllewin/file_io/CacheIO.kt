package orllewin.file_io

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class CacheIO {

    fun saveTextToFile(context: Context, filename: String, content: String, onFile: (file: File) -> Unit){

        val file: File = File.createTempFile(filename, null, context.cacheDir)

        FileOutputStream(file).use { fileOutputStream ->
            fileOutputStream.write(content.toByteArray())
        }

        onFile(file)
    }


}