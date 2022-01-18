package orllewin.filmsimulationluts

import java.util.*


fun String.capitaliseAll(): String{
   return split(" ").map { word ->
       word.replaceFirstChar { char ->
           if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
       }
   }.joinToString(" ")
}