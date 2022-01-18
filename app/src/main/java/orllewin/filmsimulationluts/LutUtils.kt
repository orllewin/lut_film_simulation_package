package orllewin.filmsimulationluts

import android.content.res.Resources

class LutUtils {

    data class Dimension(val sizeX: Int, val sizeY: Int, val sizeZ: Int)

    fun parseLuts(allLutResources: Array<String>, resources: Resources, packageName: String): List<Lut> {
        val monoLutCount = allLutResources.size

        val luts = mutableListOf<Lut>()

        val addedIds = HashMap<String, String>()

        repeat(monoLutCount){ i ->
            val resourceIdName = allLutResources[i]

            if(resourceIdName.startsWith(":")){
                val dividerLut = Lut(resourceIdName.substring(resourceIdName.lastIndexOf(":") + 1), -1, true)
                when {
                    resourceIdName.startsWith(":::") -> dividerLut.divSize = Lut.divSmall
                    resourceIdName.startsWith("::") -> dividerLut.divSize = Lut.divMedium
                    resourceIdName.startsWith(":") -> dividerLut.divSize = Lut.divLarge
                }
                luts.add(dividerLut)
            }else if(!addedIds.contains(resourceIdName)){

                val resourceId = resources.getIdentifier(resourceIdName, "drawable", packageName)
                val label = resourceIdName.replace("_", " ").capitaliseAll()
                val lut = Lut(label, resourceId)

                if(resourceIdName.endsWith("_1")){
                    println("Found lut with variants: $resourceIdName")
                    //Check for subsequent luts with _2, _3 etc
                    var searching = true
                    var searchIndex = i
                    var endInt = 1
                    while (searching){
                        searchIndex++
                        endInt++
                        if(searchIndex < allLutResources.size){
                            val nextLutRes = allLutResources[searchIndex]
                            val lastChar = nextLutRes.last()
                            if(lastChar.isDigit() && nextLutRes.endsWith("_$endInt")){
                                println("Found variant: $nextLutRes")
                                val nextLutResId = resources.getIdentifier(nextLutRes, "drawable", packageName)
                                val nextLutLabel = nextLutRes.replace("_", " ").capitaliseAll()
                                val nextLut = Lut(nextLutLabel, nextLutResId)
                                lut.addVariant(nextLut)
                                addedIds[nextLutRes] = nextLutRes
                            }else{
                                searching = false
                            }
                        }else{
                            searching = false
                        }
                    }
                }

                luts.add(lut)
            } else{
                println("LUT PARSE, ignoring already added sub lut: $resourceIdName")
            }
        }

        return luts
    }
}