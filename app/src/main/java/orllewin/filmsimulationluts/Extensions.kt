package orllewin.filmsimulationluts

import android.view.View
import java.util.*


fun String.capitaliseAll(): String{
   return split(" ").map { word ->
       word.replaceFirstChar { char ->
           if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
       }
   }.joinToString(" ")
}

fun View.show(){
    visibility = View.VISIBLE
}

fun View.hide(){
    visibility = View.GONE
}