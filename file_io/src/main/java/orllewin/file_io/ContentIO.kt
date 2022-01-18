package orllewin.file_io

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

object ContentIO {
    @SuppressLint("Range")
    fun getContentFilename(contentResolver: ContentResolver, uri: Uri): String {
        var filename = uri.toString()
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        when {
            cursor != null && cursor.moveToFirst() -> filename = cursor.getString(cursor.getColumnIndex(
                OpenableColumns.DISPLAY_NAME))
        }
        cursor?.close()
        return filename
    }

    fun getContentFilesize(contentResolver: ContentResolver, uri: Uri): Long {
        val fileDescriptor: AssetFileDescriptor? = contentResolver.openAssetFileDescriptor(uri, "r")
        return fileDescriptor?.length ?: 0

    }
}