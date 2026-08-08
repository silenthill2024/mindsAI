package com.example.proyectodesdisint.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

data class WeatherData(
    val temp: String,
    val description: String,
    val icon: String
)

object WeatherService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
        
    private const val API_KEY = "Y1d6D4TLRw95mHMh"

    private var lastResult: WeatherData? = null
    private var lastFetchTime: Long = 0

    suspend fun fetchWeather(lat: Double = 20.6597, lon: Double = -103.3496): WeatherData? = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        if (lastResult != null && (currentTime - lastFetchTime) < 30 * 60 * 1000) {
            return@withContext lastResult
        }

        val url = "https://my.meteoblue.com/packages/basic-1h_basic-day?lat=$lat&lon=$lon&apikey=$API_KEY&format=json"
        val request = Request.Builder().url(url).build()
        
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null
            
            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)
            
            // Priority: data_1h (first element) else data_day (first element)
            val data1h = json.optJSONObject("data_1h")
            val dataDay = json.optJSONObject("data_day")
            
            val tempVal: String
            val pictocode: Int
            
            if (data1h != null && data1h.has("temperature")) {
                val temps = data1h.optJSONArray("temperature")
                val pictos = data1h.optJSONArray("pictocode")
                tempVal = (temps?.optDouble(0, 24.0)?.toInt() ?: 24).toString() + "°C"
                pictocode = pictos?.optInt(0, 1) ?: 1
            } else if (dataDay != null && dataDay.has("temperature_max")) {
                val temps = dataDay.optJSONArray("temperature_max")
                val pictos = dataDay.optJSONArray("pictocode")
                tempVal = (temps?.optDouble(0, 24.0)?.toInt() ?: 24).toString() + "°C"
                pictocode = pictos?.optInt(0, 1) ?: 1
            } else {
                tempVal = "24°C"
                pictocode = 1
            }
            
            val description = mapPictocodeToDescription(pictocode)
            
            lastResult = WeatherData(tempVal, description, pictocode.toString())
            lastFetchTime = currentTime
            lastResult
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mapPictocodeToDescription(code: Int): String {
        return when (code) {
            1 -> "Despejado"
            2 -> "Mayormente despejado"
            3 -> "Nubes dispersas"
            4 -> "Nublado"
            5 -> "Muy nublado"
            6 -> "Lluvia ligera"
            7 -> "Lluvia"
            8 -> "Tormenta"
            else -> "Despejado"
        }
    }
}
