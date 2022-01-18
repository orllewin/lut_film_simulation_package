package orllewin.filmsimulationluts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import orllewin.filmsimulationluts.databinding.AboutBottomsheetBinding

class AboutDialog: BottomSheetDialogFragment() {

    lateinit var binding: AboutBottomsheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AboutBottomsheetBinding.inflate(inflater)



        return binding.root
    }
}