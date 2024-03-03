package com.example.mapexercise.networking

import android.util.Log
import com.mapbox.geojson.FeatureCollection
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class RemoteApi {

    private val _baseUrl = "https://dev.api.mevo.co.nz/public/vehicles/Wellington"

    private val _tag = "remoteApi"
    var featCollection: FeatureCollection? = null

   suspend fun getCarLocations(){
        Thread {

            val connection = URL(_baseUrl).openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")

            connection.connectTimeout = 1000
            connection.readTimeout = 1000
            connection.doInput = true

            try {
                val reader = InputStreamReader(connection.inputStream)
                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)
                    bufferedReader.forEachLine {
                        response.appendLine(it)
                    }
                    Log.d(_tag, "In_Success $response")
                    val jsonString = response.toString().trim()
                    Log.d("json_string", jsonString)

                    val jsonObject = JSONObject(jsonString)
                    val geoJsonString = jsonObject.getJSONObject("data").toString()


                    featCollection = FeatureCollection.fromJson(geoJsonString)
                    if (featCollection != null) {
                        Log.d("feat collection populated?", "success: $featCollection")
                        // Do something with the populated featCollection
                    } else {
                        Log.d("feat collection populated?", "failure")
                    }
                }
            } catch (e: Exception) {
                Log.d(_tag, "In_Error ${e.localizedMessage}")
            }

            connection.disconnect()

        }.start()
    }

}