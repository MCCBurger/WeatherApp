package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val CITY: String = "Huntley, US"
    val API: String = "7f1c1f9b7eaa530b67e7c9bebb9a527d" // API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWeather().execute()

    }

    inner class getWeather() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
           loader.visibility = View.VISIBLE
            mainContainer.visibility = View.GONE
            errorText.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=imperial&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°F"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°F"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°F"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val feels = main.getString("feels_like")+"°F"
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */
                currentLocation.text = address
                updated_at.text =  updatedAtText
                sky.text = weatherDescription.capitalize()
                tempMain.text = temp
                temp_min.text = tempMin
                temp_max.text = tempMax
                sunriseTime.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                sunsetTime.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                windSp.text = windSpeed
                pressureCurr.text = pressure
                humidityCurr.text = humidity
                feelsLike.text = feels

                /* Views populated, Hiding the loader, Showing the main design */
                loader.visibility = View.GONE
                mainContainer.visibility = View.VISIBLE

            } catch (e: Exception) {
                loader.visibility = View.GONE
                errorText.visibility = View.VISIBLE
            }

        }
    }
}