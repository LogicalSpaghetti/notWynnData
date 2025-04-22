package file_management

import org.json.JSONObject


fun reformatItems(allItems: JSONObject) {


    for (name in allItems.names()) {
        val item = allItems.getJSONObject(name as String?)
        item.remove("")
        with (item.getString("type")) {

        }
    }
}

fun getReformattingHashMap(): HashMap<String, String> {
    val map = HashMap<String, String>()

    map.put("", "")

    return map
}