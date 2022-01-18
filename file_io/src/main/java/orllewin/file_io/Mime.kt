package orllewin.file_io

import android.webkit.MimeTypeMap

object Mime {

    fun fromFilename(filename: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(filename)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "Unknown"
    }
}