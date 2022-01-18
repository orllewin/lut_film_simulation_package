package orllewin.file_io

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PhotoIO {

    private var photoChooserLauncher: ActivityResultLauncher<Intent>? = null
    var onPhoto: (uri: Uri) -> Unit = { _ -> }

    fun registerForPhotoChooser(activity: AppCompatActivity){
        photoChooserLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                intent?.data?.also { uri ->
                    onPhoto(uri)
                }
            }
        }
    }

    fun choosePhoto(onPhoto: (uri: Uri) -> Unit){
        this.onPhoto = onPhoto
        photoChooserLauncher?.launch(Intent().also { intent ->
            intent.type = "image/*"
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        })
    }
}