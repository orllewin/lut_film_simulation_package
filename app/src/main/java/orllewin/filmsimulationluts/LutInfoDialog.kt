package orllewin.filmsimulationluts

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import orllewin.filmsimulationluts.databinding.LutInfoBottomsheetBinding
import android.content.ClipData
import android.content.ClipboardManager

import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast

import androidx.core.content.ContextCompat.getSystemService
import android.content.Intent
import android.net.Uri


class LutInfoDialog(
    val lut: Lut,
    val referenceBitmap: Bitmap?,
    val onExport: (lut: Lut) -> Unit): BottomSheetDialogFragment() {

    lateinit var binding: LutInfoBottomsheetBinding

    private val toolkit = UnrealLutToolkit()

    private var variantIndex = -1//The original/main

    private var activeLut = lut

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LutInfoBottomsheetBinding.inflate(inflater)


        refresh()

        binding.exportLutButton.setOnClickListener {
            onExport(activeLut)
        }

        binding.exportLentoButton.setOnClickListener {
            val lutIO = LutIO()
            val encoded = lutIO.lentoEncode(requireContext(), activeLut)

            lutIO.lentoDecode(encoded){
                println("Decoded lut label: ${lutIO.decodedLabel}")
                println("Decoded lut filname: ${lutIO.decodedFilename}")
            }

            val clip = ClipData.newPlainText("Lento LUT", encoded)
            val clipboard: ClipboardManager? = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            clipboard?.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "${activeLut.label} copied to Clipboard", Toast.LENGTH_SHORT).show()
        }

        if(lut.variants.isNotEmpty()){
            println("Lut has variants")
            binding.prevVariant.show()
            binding.nextVariant.show()

            binding.prevVariant.setOnClickListener {
                if(variantIndex > -1) variantIndex--

                when (variantIndex) {
                    -1 -> {
                        activeLut = lut
                        refresh()
                    }
                    else -> {
                        activeLut = lut.variants[variantIndex]
                        refresh()
                    }
                }
            }

            binding.nextVariant.setOnClickListener {
                when (lut.variants.size) {
                    1 -> variantIndex = 0
                    else -> if(variantIndex < lut.variants.size-2) variantIndex++
                }


                activeLut = lut.variants[variantIndex]
                refresh()
            }
        }

        binding.portableLutInfoButton.setOnClickListener {
            openPortableLutFormatWebpage()
        }

        return binding.root
    }

    private fun refresh(){
        binding.lutLabel.text = "Label:\n${activeLut.label}\n\nFilename:\n${activeLut.toFilename()}"
        referenceBitmap?.let{ bitmap ->
            toolkit.loadLut(requireContext(), activeLut)
            binding.lutPreviewImage.setImageBitmap(toolkit.process(referenceBitmap))
        }

        if(lut.hasVariants()){
            when (variantIndex) {
                -1 -> binding.prevVariant.alpha = 0f
                else -> binding.prevVariant.alpha = 1f
            }

            when (variantIndex) {
                (lut.variants.size - 2) -> binding.nextVariant.alpha = 0f
                else -> binding.nextVariant.alpha = 1f
            }
        }
    }

    private fun openPortableLutFormatWebpage(){
        val url = "https://orllewin.uk/portable_lut_format/"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}