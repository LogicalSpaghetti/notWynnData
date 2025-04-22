import api_access.getJSONFromAPI
import file_management.writeStringToFile
import org.json.JSONObject

fun item() {
    var allItems = getItemAPIData()

}

fun getItemAPIData(): JSONObject {
    return getJSONFromAPI("https://api.wynncraft.com/v3/item/database?fullResult")
}

fun sortAndWriteItems(allItems: JSONObject) {
    if (allItems.isEmpty) return

    val groupedItems = JSONObject()

    val allItemNames = allItems.names()
    for (name in allItemNames) {
        if (name !is String) continue
        val item = allItems.get(name)
        if (item !is JSONObject) continue
        val type = item.getString("type")
        if (!item.has("${type}Type")) continue
        val typeType = item.getString("${type}Type")
        if (typeType == "") continue

        if (!groupedItems.has(type)) groupedItems.put(type, JSONObject())
        groupedItems.getJSONObject(type).put(name, item)

        if (!groupedItems.has(typeType)) groupedItems.put(typeType, JSONObject())
        groupedItems.getJSONObject(typeType).put(name, item)
    }
}

fun writeGroupsToFiles(groupedItems: JSONObject) {
    writeStringToFile("groupedItems.json", groupedItems.toString())
    writeStringToFile("groupedItems.js", "const itemGroups = $groupedItems")

    val groupNames = groupedItems.names()

    for (i in 0..<groupNames.length()) {
        val name = groupNames.getString(i)
        writeStringToFile("$name.json", groupedItems.get(name).toString())
        writeStringToFile("$name.js", "const ${name}Group = " + groupedItems.get(name).toString())
    }
}