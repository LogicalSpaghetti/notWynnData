import api_access.getJSONFromAPI
import file_management.readStringFromFile
import file_management.reformatItems
import file_management.writeStringToFile
import org.json.JSONArray
import org.json.JSONObject

fun item() {
    val allItems = getItemAPIData()
    reformatItems(allItems)

    // for use in PedAtlas
    writeStringToFile("allItems.js", "const items = " + allItems.toString(2))

    val groupedItems = sortItems(allItems)

    writeGroupsToFiles(groupedItems)

//    val database = updateItemDatabase(groupedItems)
//    writeStringToFile("database.js", "const itemDatabase = $database")

    removeUnnecessaryGroups(groupedItems)

    // for use in interpreting item names as items
    writeStringToFile("groupedItems.js", "const itemGroups = $groupedItems")
}

fun getItemAPIData(): JSONObject {
    return getJSONFromAPI("https://api.wynncraft.com/v3/item/database?fullResult")
}

fun sortItems(allItems: JSONObject): JSONObject {
    if (allItems.isEmpty) return JSONObject()

    val groupedItems = JSONObject()

    val subNamesList = ArrayList<String>()
    val dropRestrictions = ArrayList<String>()

    val allItemNames = allItems.names()
    for (name in allItemNames) {
        if (name !is String) continue
        val item = allItems.get(name)
        if (item !is JSONObject) continue
        val type = item.getString("type")
        if (!item.has("subType")) continue
        val subType = item.getString("subType")

        if (type !in arrayOf("tool", "armour", "accessory")) {
            if (!groupedItems.has(type)) {
                groupedItems.put(type, JSONObject())
            }
            groupedItems.getJSONObject(type).put(name, item)
        }

        if (subType !in arrayOf("relik", "spear", "bow", "wand", "dagger", "axe", "pickaxe", "rod", "scythe")) {
            if (!groupedItems.has(subType)) {
                groupedItems.put(subType, JSONObject())
            }
            groupedItems.getJSONObject(subType).put(name, item)
        }

        val subNames = item.names()
        for (subName in subNames) {
            if (subNamesList.contains(subName)) continue
            subNamesList.add(subName.toString())
        }

        if (item.has("dropRestriction")) if (!dropRestrictions.contains(
                        item.getString("dropRestriction"))) dropRestrictions.add(item.getString("dropRestriction"))
    }

    println(subNamesList)
    println(dropRestrictions)
    return groupedItems
}

fun updateItemDatabase(groupedItems: JSONObject): JSONObject {
    val database = JSONObject(readStringFromFile("database/database.json"))
//
//    val groupNames = groupedItems.names()
//    for (i in 0..<groupNames.length()) {
//        val name = groupNames.getString(i)
//        val group = groupedItems.getJSONObject(name)
//        if (!database.has(name)) database.put(name, JSONArray())
//        val databaseGroup = database.getJSONArray(name)
//
//        val apiNames = group.names()
//        for (j in 0..<apiNames.length()) {
//            val itemName = apiNames.getString(j)
//
//            val itemIndex = databaseGroup.indexOf(itemName)
//            group.getJSONObject(itemName).put("groupId", if (itemIndex != -1) {
//                databaseGroup.getJSONObject(itemName).getInt("groupId")
//            } else {
//                databaseGroup.length()
//            })
//            databaseGroup.put(itemName, group.getJSONObject(itemName))
//        }
//    }
//
    return database
}

fun removeUnnecessaryGroups(groupedItems: JSONObject) {
    val groupNames = groupedItems.names()
    for (i in 0..<groupNames.length()) {
        if (groupNames.getString(i) in arrayOf("relik", "spear", "bow", "wand", "dagger", "axe", "pickaxe", "rod",
                        "scythe", "tool", "armour", "accessory")) {
            groupedItems.remove(groupNames.getString(i))
        }
    }
}

fun writeGroupsToFiles(groupedItems: JSONObject) {
    val groupNames = groupedItems.names()
    for (i in 0..<groupNames.length()) {
        val name = groupNames.getString(i)
        writeStringToFile("sections/$name.json", groupedItems.getJSONObject(name).toString(2))
    }
}
