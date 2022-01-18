package orllewin.filmsimulationluts

class Lut(val label: String, val resourceId: Int){

    fun toFilename(): String{
        return "${label.lowercase().replace(" ", "_")}.png"
    }
}