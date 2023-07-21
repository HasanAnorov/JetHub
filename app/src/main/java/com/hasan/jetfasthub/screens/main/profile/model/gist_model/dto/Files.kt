package com.hasan.jetfasthub.screens.main.profile.model.gist_model.dto

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.File
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.Files
import java.lang.reflect.Type


//can implement this
class DemoFiles{

//    private val jsonData = "{\n" +
//            "            \"refactoring.kt\": {\n" +
//            "                \"filename\": \"refactoring.kt\",\n" +
//            "                \"type\": \"text/plain\",\n" +
//            "                \"language\": \"Kotlin\",\n" +
//            "                \"raw_url\": \"https://gist.githubusercontent.com/kirich1409/330eaf442c77f93889ec1d6dd53a70a1/raw/af5f317baff34ada3d03171ce7f54506ec6aa29e/refactoring.kt\",\n" +
//            "                \"size\": 420\n" +
//            "            }\n" +
//            "        }"
            private val jsonData = " {\n" +
        "                \"filename\": \"refactoring.kt\",\n" +
        "                \"type\": \"text/plain\",\n" +
        "                \"language\": \"Kotlin\",\n" +
        "                \"raw_url\": \"https://gist.githubusercontent.com/kirich1409/330eaf442c77f93889ec1d6dd53a70a1/raw/af5f317baff34ada3d03171ce7f54506ec6aa29e/refactoring.kt\",\n" +
        "                \"size\": 420\n" +
        "            }"

    private var deserializer: JsonDeserializer<Files> =
        JsonDeserializer<Files> { json, typeOfT, context ->
            val jsonObject = json.asJsonObject
            val file = File(
                jsonObject["filename"].asString,
                jsonObject["language"].asString,
                jsonObject["raw_url"].asString,
                jsonObject["size"].asInt,
                jsonObject["type"].asString,

                )
            Files(
                file
            )
        }

    private var gsonBuilder: GsonBuilder = GsonBuilder().registerTypeAdapter(Files::class.java, deserializer)

    private var customGson: Gson = gsonBuilder.create()
    var customObject: File = customGson.fromJson(jsonData, File::class.java)

}


internal class FilesResponse {

    var files: Map<String, File>? = null

    class File {
        var filename: String? = null
        var language: String? = null
        var raw_url: String? = null
        var size = 0
        var type: String? = null

        override fun toString(): String {
            return " {\n" +
                    "                \"filename\": \"$filename\",\n" +
                    "                \"type\": \"$type\",\n" +
                    "                \"language\": \"$language\",\n" +
                    "                \"raw_url\": \"$raw_url\",\n" +
                    "                \"size\": $size\n" +
                    "            }"

//            "Number{" +
//                    "id=" + size +
//                    ", name='" + filename + '\'' +
//                    '}'
        }
    }

    class FilesResponseDeserializer : JsonDeserializer<FilesResponse?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): FilesResponse {
            val response = Gson().fromJson(json, FilesResponse::class.java)
            var map: Map<String, File> = HashMap()
            map = Gson().fromJson(json, map.javaClass) as Map<String, File>
            response.files = map
            return response
        }
    }
}


