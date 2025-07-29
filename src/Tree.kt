import api.getJSONFromAPI
import file.writeStringToFile
import org.json.JSONArray
import org.json.JSONObject

fun tree() {
    writeTrees()
}

fun writeTrees(): JSONObject {
    val classes = JSONObject()

    val classNames = arrayOf("archer", "warrior", "assassin", "mage", "shaman")
    for (className in classNames) {
        val wynnClass = JSONObject()
        wynnClass.put("tree", getJSONFromAPI("https://api.wynncraft.com/v3/ability/tree/$className"))
        wynnClass.put("map", dePageMap(getJSONFromAPI("https://api.wynncraft.com/v3/ability/map/$className")))
        wynnClass.put("aspects", getJSONFromAPI("https://api.wynncraft.com/v3/aspects/$className"))
        classes.put(className, wynnClass)
    }

    writeStringToFile("database/tree/classTrees.js", "const classAbilities = $classes")
    writeStringToFile("database/tree/reference/classTrees.json", classes.toString(2))

    return classes
}

fun dePageMap(map: JSONObject): JSONArray {
    val unPagedMap = JSONArray()
    for (i: Int in 0..<map.length()) {
        val pageName = map.names().getString(i)
        val page = map.getJSONArray(pageName)

        for (j: Int in 0..<page.length()) {
            unPagedMap.put(page.getJSONObject(j))
        }
    }
    return unPagedMap
}