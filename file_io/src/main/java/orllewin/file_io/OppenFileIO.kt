package orllewin.file_io

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class OppenFileIO {

    sealed class RequestType{
        object Image: RequestType()
        object Video: RequestType()
    }

    private var requestMultiplePermissions: ActivityResultLauncher<Array<String>>? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var onUri: (uri: Uri) -> Unit = { _ -> }
    private var onPermissions: (granted: Boolean) -> Unit = { _ -> }

    fun hasPermissions(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    fun register(componentActivity: ComponentActivity){
        resultLauncher = componentActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.also { uri ->
                onUri(uri)
            }
        }
        requestMultiplePermissions = componentActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            onPermissions(permissions.entries.all { entry ->
                entry.value == true
            })
        }
    }

    fun requestPermissions(onPermissions: (granted: Boolean) -> Unit){
        this.onPermissions = onPermissions
        requestMultiplePermissions?.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    fun launch(intent: Intent, onUri: (uri: Uri) -> Unit) {
        this.onUri = onUri
        resultLauncher.launch(intent)
    }

    private fun chooseFile(mimeType: String) {
        Intent().run {
            type = mimeType
            action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(this)
        }
    }

    fun chooseImage(onUri: (uri: Uri) -> Unit) {
        this.onUri = onUri
        chooseFile("image/*")
    }

    fun chooseVideo(onUri: (uri: Uri) -> Unit) {
        this.onUri = onUri
        chooseFile("video/*")
    }

    fun chooseImage(context: Context, onPermissionDenied: () -> Unit, onUri: (uri: Uri) -> Unit) {
        chooseFile(context, RequestType.Image, onPermissionDenied, onUri)
    }

    fun chooseVideo(context: Context, onPermissionDenied: () -> Unit, onUri: (uri: Uri) -> Unit) {
        chooseFile(context, RequestType.Video, onPermissionDenied, onUri)
    }

    private fun chooseFile(context: Context, type: RequestType, onPermissionDenied: () -> Unit, onUri: (uri: Uri) -> Unit) =
        when {
            hasPermissions(context) -> {
                when (type) {
                    RequestType.Image -> chooseImage(onUri)
                    RequestType.Video -> chooseVideo(onUri)
                }
            }
            else -> {
                requestPermissions { granted ->
                    when {
                        granted -> chooseImage(onUri)
                        else -> onPermissionDenied()
                    }
                }
            }
        }

}