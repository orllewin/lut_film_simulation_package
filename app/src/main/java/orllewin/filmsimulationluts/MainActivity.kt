package orllewin.filmsimulationluts

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import orllewin.file_io.OppenFileIO
import orllewin.filmsimulationluts.databinding.ActivityMainBinding
import java.lang.Exception
import java.time.OffsetDateTime
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var fileIO = OppenFileIO()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var requestMultiplePermissions: ActivityResultLauncher<Array<String>>? = null

    private val adapter = LutAdapter { lut ->
        showLutInfo(lut)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.also { uri ->
                importReferencePhoto(uri)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.lut_explorer)

        fileIO.register(this)

        val defaultReferenceBitmap = BitmapFactory.decodeResource(resources, R.drawable.reference_image)
        BitmapHolder.bitmap = defaultReferenceBitmap
        adapter.setReferenceImage(BitmapHolder.bitmap!!)

        binding.lutRecycler.layoutManager = GridLayoutManager(this, 3)
        binding.lutRecycler.adapter = adapter

        loadLuts()
    }

    fun loadLuts(){
        val allLutResources = resources.getStringArray(R.array.all_luts)
        val monoLutCount = allLutResources.size

        val luts = mutableListOf<Lut>()

        repeat(monoLutCount){ i ->

            val resourceIdName = allLutResources[i]
            val resourceId = resources.getIdentifier(resourceIdName, "drawable", packageName)
            val label = resourceIdName.replace("_", " ").capitaliseAll()
            val lut = Lut(label, resourceId)
            luts.add(lut)
        }

        adapter.updateLuts(luts)

    }

    private fun showLutInfo(lut: Lut){
        LutInfoDialog(lut, BitmapHolder.bitmap){ lut ->
            //onExport
            when {
                !fileIO.hasPermissions(this) -> requestFileIOPermissions(lut)
                else -> {
                    //Already has permissions
                    export(lut)
                }
            }
        }.show(supportFragmentManager, "lut_info_dialog")
    }

    private fun requestFileIOPermissions(lut: Lut){
        fileIO.requestPermissions { granted ->
            when {
                granted -> export(lut)
                else -> Toast.makeText(this, "External Storage permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun export(lut: Lut){
        val values = ContentValues()

        val now = OffsetDateTime.now()

        val filename = lut.toFilename()

        values.put(MediaStore.Images.Media.TITLE, filename)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.DATE_ADDED, now.toEpochSecond())
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Luts")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
        }

        val collection = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val uri = contentResolver.insert(collection, values)

        uri?.let {
            this.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.use { os ->
                    val lutBitmap = BitmapFactory.decodeResource(resources, lut.resourceId) ?: throw Exception("Invalid lut: ${lut.label}")
                    lutBitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                }
            }

            values.clear()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                this.contentResolver.update(uri, values, null, null)
            }

            //export done
            Toast.makeText(this, "${lut.label} exported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_set_reference_image -> {
                when {
                    hasPermissions() -> {
                        choosePhoto()
                    }
                    else -> {
                        requestMultiplePermissions =
                            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                                when {
                                    permissions.entries.all { entry ->
                                        entry.value == true
                                    } -> choosePhoto()
                                    else -> Toast.makeText(this, "External Storage permission required", Toast.LENGTH_SHORT).show()
                                }
                            }

                    }

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun choosePhoto(){
        Intent().run {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(this)
        }
    }

    private fun hasPermissions(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun launchPermissions() {
        requestMultiplePermissions?.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
    }

    private fun importReferencePhoto(uri: Uri){
        println("importReferencePhoto: $uri")

        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")

        if(fileDescriptor == null){
            println("Could not get FileDescriptor")
            //todo toast
            return
        }

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true

        BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, Rect(), bmOptions)

        val photoW: Int = bmOptions.outWidth
        val photoH: Int = bmOptions.outHeight

        val targetPixels = Math.max(Resources.getSystem().displayMetrics.widthPixels/3, 400)

        val scaleFactor: Int = photoW/targetPixels

        //Reset Bitmap options for actual import:
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, Rect(), bmOptions)?.also { bitmap ->
            BitmapHolder.bitmap = bitmap
            adapter.setReferenceImage(BitmapHolder.bitmap!!)
        }
    }
}