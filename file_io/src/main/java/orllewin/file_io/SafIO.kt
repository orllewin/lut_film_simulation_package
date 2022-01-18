package orllewin.file_io

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import java.io.FileInputStream
import java.io.FileOutputStream

class SafIO {

    var newFileLauncher: ActivityResultLauncher<Intent>? = null
    var openFileLauncher: ActivityResultLauncher<Intent>? = null
    var onNewFile: (uri: Uri) -> Unit = { _ -> }
    var onOpenFile: (uri: Uri) -> Unit = { _ -> }

    fun registerForFileCreation(activity: FragmentActivity){
        newFileLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                intent?.data?.also { uri ->
                    onNewFile(uri)
                }
            }
        }
    }

    fun registerForFileCreation(activity: AppCompatActivity){
        newFileLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                intent?.data?.also { uri ->
                    onNewFile(uri)
                }
            }
        }
    }

    fun registerForFileOpen(activity: AppCompatActivity){
        openFileLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                intent?.data?.also { uri ->
                    onOpenFile(uri)
                }
            }
        }
    }

    fun newFile(filename: String, mime: String, onNewFile: (uri: Uri) -> Unit){
        this.onNewFile = onNewFile

        newFileLauncher?.launch(Intent(Intent.ACTION_CREATE_DOCUMENT).also { intent ->
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = mime
            intent.putExtra(Intent.EXTRA_TITLE, filename)
        })
    }

    fun saveTextToFile(activity: AppCompatActivity, uri: Uri, content: String){
        activity.contentResolver.openFileDescriptor(uri, "w")?.use {  parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use{ fileOutputStream ->
                fileOutputStream.write(content.toByteArray())
            }
        }
    }

    fun shareFile(activity: AppCompatActivity, uri: Uri, mime: String){
        Intent(Intent.ACTION_SEND).run {
            type = mime
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_STREAM, uri)
            activity.startActivity(Intent.createChooser(this, "Share"))
        }
    }

    fun chooseFile(mime: String, onOpenFile: (uri: Uri) -> Unit) {
        this.onOpenFile = onOpenFile

        openFileLauncher?.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).also { intent ->
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = mime
        })
    }

    fun readFile(activity: AppCompatActivity, uri: Uri, onText: (content: String) -> Unit){
        activity.contentResolver.openFileDescriptor(uri, "r")?.use {  parcelFileDescriptor ->
            FileInputStream(parcelFileDescriptor.fileDescriptor).use{ fileInputStream ->
                onText(fileInputStream.readBytes().toString(Charsets.UTF_8))
            }
        }
    }
}