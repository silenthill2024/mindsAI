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
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
        
    private const val API_KEY = "Y1d6D4TLRw95mHMh"

    // Cache simple para evitar peticiones excesivas
    private var lastResult: WeatherData? = null
    private var lastFetchTime: Long = 0

    suspend fun fetchWeather(lat: Double = 20.6597, lon: Double = -103.3496): WeatherData? = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        // Cache por 30 minutos
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
            
            val current = json.optJSONObject("data_current") ?: return@withContext null
            val tempVal = current.optDouble("temperature", 0.0).toInt().toString() + "°C"
            val pictocode = current.optInt("pictocode", 1)
            
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
            1 -> "Soleado"
            2 -> "Despejado"
            3 -> "Nubes dispersas"
            4 -> "Nublado"
            5 -> "Cubierto"
            6 -> "Lluvia ligera"
            7 -> "Lluvia"
            8 -> "Tormenta"
            else -> "Despejado"
        }
    }
}
