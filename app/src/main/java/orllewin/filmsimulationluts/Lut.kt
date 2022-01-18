package orllewin.filmsimulationluts

class Lut(val label: String, val resourceId: Int, val isDivider: Boolean = false){

    companion object{
        const val divLarge = 0
        const val divMedium = 1
        const val divSmall = 2
    }

    var divSize = divMedium

    val variants = mutableListOf<Lut>()

    fun toFilename(): String{
        return "${label.lowercase().replace(" ", "_")}.png"
    }

    fun addVariant(lut: Lut) {
        variants.add(lut)
    }
}