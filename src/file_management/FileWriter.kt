package file_management

import java.io.*

fun writeStringToFile(fileName: String, text: String) {
    File("output").mkdirs()
    val writer = BufferedWriter(FileWriter("output/$fileName"))
    writer.write(text)
    writer.close()
}

fun readStringFromFile(fileName: String): String {
    val reader = BufferedReader(FileReader(fileName))
    val text = reader.readText()
    reader.close()
    return text
}
