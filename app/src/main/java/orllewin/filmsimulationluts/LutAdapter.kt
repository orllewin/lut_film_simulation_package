package orllewin.filmsimulationluts

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import orllewin.filmsimulationluts.databinding.LutCellBinding

@SuppressLint("NotifyDataSetChanged")
class LutAdapter(val onLutSelected: (lut: Lut) -> Unit): RecyclerView.Adapter<LutAdapter.ViewHolder>(){

    private val luts = mutableListOf<Lut>()
    private val toolkit = UnrealLutToolkit()

    private var referenceBitmap: Bitmap? = null


    fun setReferenceImage(referenceBitmap: Bitmap){
        this@LutAdapter.referenceBitmap = referenceBitmap

        when {
            luts.isNotEmpty() -> notifyDataSetChanged()
        }
    }

    fun updateLuts(_luts: List<Lut>){
        luts.clear()
        luts.addAll(_luts)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: LutCellBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LutCellBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lut = luts[position]
        toolkit.loadLut(holder.binding.root.context, lut)
        holder.binding.lutImage.setImageBitmap(toolkit.process(referenceBitmap))
        holder.binding.root.setOnClickListener {
            onLutSelected(luts[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = luts.size
}