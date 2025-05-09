package file_management

import org.json.JSONObject


fun reformatItems(allItems: JSONObject) {

    // for each item Object,
    for (name in allItems.names()) {
        val item = allItems.getJSONObject(name as String?)
        item.put("name", name)
        // remove useless data
        if (item.has("internalName")) item.remove("internalName")
        if (item.has("identified")) item.remove("identified")
        if (item.has("type")) {
            val subType = "${item.getString("type")}Type"

            if (item.has(subType)) {
                item.put("subType", item.getString(subType))
                item.remove(subType)
            }
        }

        // modify according to type
        with(item.getString("type")) {

        }
    }
}
