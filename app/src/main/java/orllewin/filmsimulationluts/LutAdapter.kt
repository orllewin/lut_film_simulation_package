package orllewin.filmsimulationluts

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import orllewin.filmsimulationluts.databinding.LutCellBinding

@SuppressLint("NotifyDataSetChanged")
class LutAdapter(val onLutSelected: (lut: Lut) -> Unit): RecyclerView.Adapter<LutAdapter.ViewHolder>(){

    companion object{
        val viewTypeDivider = 0
        val viewTypeCell = 1
    }

    private val luts = mutableListOf<Lut>()
    private val toolkit = UnrealLutToolkit()

    private var referenceBitmap: Bitmap? = null

    override fun getItemViewType(position: Int): Int {
        val lut = luts[position]
        return when {
            lut.isDivider -> viewTypeDivider
            else -> viewTypeCell
        }
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LutCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lut = luts[position]

        val layoutParams = holder.binding.root.layoutParams as StaggeredGridLayoutManager.LayoutParams

        if(lut.isDivider){
            holder.binding.label.show()
            holder.binding.label.text = lut.label
            layoutParams.isFullSpan = true

            when (lut.divSize) {
                Lut.divLarge -> holder.binding.label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                Lut.divMedium -> holder.binding.label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                Lut.divSmall -> holder.binding.label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            }
        }else {
            layoutParams.isFullSpan = false
            holder.binding.label.hide()
            toolkit.loadLut(holder.binding.root.context, lut)
            holder.binding.lutImage.setImageBitmap(toolkit.process(referenceBitmap))
            holder.binding.root.setOnClickListener {
                onLutSelected(luts[holder.adapterPosition])
            }

            if(lut.hasVariants()){
                holder.binding.variantCountlabel.show()
                holder.binding.variantCountlabel.text = "+${lut.variants.size}"
            }else{
                holder.binding.variantCountlabel.hide()
            }
        }
    }

    override fun getItemCount(): Int = luts.size
}