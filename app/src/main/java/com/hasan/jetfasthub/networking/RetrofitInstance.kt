package com.hasan.jetfasthub.networking

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.File
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.Files
import com.hasan.jetfasthub.utility.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class RetrofitInstance(context: Context) {

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor(context))
            //.addInterceptor(AuthenticationInterceptor())
            .addInterceptor(logging).build()

        val deserializer: JsonDeserializer<Files> =
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

        val gson = GsonBuilder()
//            .registerTypeAdapter(Files::class.java, deserializer)
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
    val gitHubService: GitHubService by lazy {
        retrofit.create(GitHubService::class.java)
    }
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