package orllewin.file_io

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class CameraIO {

    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val PERMISSION_REQUEST_CODE = 1
    }

    private var requestCameraPermissions: ActivityResultLauncher<Array<String>>? = null
    private var onPermissions: (granted: Boolean) -> Unit = { _ -> }

    fun register(componentActivity: ComponentActivity){
        requestCameraPermissions = componentActivity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            onPermissions(permissions.entries.all { entry ->
                entry.value == true
            })
        }
    }


    fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(onPermissions: (granted: Boolean) -> Unit) {
        this.onPermissions = onPermissions
        requestCameraPermissions?.launch(REQUIRED_PERMISSIONS)
    }
}