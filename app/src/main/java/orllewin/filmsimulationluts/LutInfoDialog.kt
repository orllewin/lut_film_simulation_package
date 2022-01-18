package orllewin.filmsimulationluts

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import orllewin.filmsimulationluts.databinding.LutInfoBottomsheetBinding

class LutInfoDialog(
    val lut: Lut,
    val referenceBitmap: Bitmap?,
    val onExport: (lut: Lut) -> Unit): BottomSheetDialogFragment() {

    lateinit var binding: LutInfoBottomsheetBinding

    private val toolkit = UnrealLutToolkit()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LutInfoBottomsheetBinding.inflate(inflater)
        binding.lutLabel.text = "Label:\n${lut.label}\n\nFilename: ${lut.toFilename()}"

        referenceBitmap?.let{ bitmap ->
            toolkit.loadLut(requireContext(), lut)
            binding.lutPreviewImage.setImageBitmap(toolkit.process(referenceBitmap))
        }

        binding.exportLutButton.setOnClickListener {
            onExport(lut)
        }

        return binding.root
    }

}