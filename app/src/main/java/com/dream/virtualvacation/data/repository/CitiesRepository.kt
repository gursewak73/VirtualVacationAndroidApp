package com.dream.virtualvacation.data.repository

import android.content.Context
import com.dream.virtualvacation.data.model.CitiesResponse
import com.google.gson.Gson
import java.io.IOException

class CitiesRepository(private val context: Context) {
    
    private val gson = Gson()
    
    fun getCities(): List<com.dream.virtualvacation.data.model.City> {
        return try {
            val jsonString = loadJSONFromAsset("cities.json")
            val response = gson.fromJson(jsonString, CitiesResponse::class.java)
            response.cities
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun loadJSONFromAsset(fileName: String): String {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            ""
        }
    }
}
