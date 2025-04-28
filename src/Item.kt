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

    val database = updateItemDatabase(groupedItems)

    removeUnnecessaryGroups(database)

    // for use in PedBuilderBuilder
    writeStringToFile("groupedItems.js", "const itemGroups = $database")
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
    //  // itemData:
    //  {
    //    "id": 0,
    //    "name": "",
    //    "item": {}
    //  }

    val databases = JSONObject(readStringFromFile("database/database.json"))

    val groupNames = groupedItems.names()
    for (i in 0..<groupNames.length()) {
        val databaseName = groupNames.getString(i)
        val apiGroup = groupedItems.getJSONObject(databaseName)
        if (!databases.has(databaseName)) databases.put(databaseName, JSONArray())
        val databaseGroup = databases.getJSONArray(databaseName)

        // loop through, adding to and updating the database
        for (name in apiGroup.names()) {
            name as String

            // grab from database
            val databaseEntry = databaseGroup.find { (it as JSONObject).getString("name") == name }
            if (databaseEntry == null) {
                // new item, add entry and work out id
                val newEntry = JSONObject()
                newEntry.put("name", name)
                val id = if (databaseGroup.length() == 0) 0 else ((databaseGroup[databaseGroup.length() - 1] as JSONObject).getInt(
                        "id") + 1)
                newEntry.put("id", id)

                newEntry.put("item", apiGroup.getJSONObject(name))
                databaseGroup.put(newEntry)
            } else {
                databaseEntry as JSONObject
                // existing item, update item data
                (databaseGroup[databaseGroup.indexOf(databaseEntry)] as JSONObject).put("item", apiGroup.get(name))
            }
        }
    }

    // for use in PedBuilderSearch
    writeStringToFile("database.js", "const itemDatabase = $databases")
    // for logging changes
    writeStringToFile("database.json", databases.toString(2))
    return databases
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
        writeStringToFile("reference/$name.json", groupedItems.getJSONObject(name).toString(2))
    }
}
