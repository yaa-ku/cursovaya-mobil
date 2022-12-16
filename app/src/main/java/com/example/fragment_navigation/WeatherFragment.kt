package com.example.fragment_navigation

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.fragment_navigation.databinding.FragmentWeatherBinding
import org.json.JSONObject


const val API_KEY = "70e3202ca944469089552814220912"
class WeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            var city = "Khabarovsk"
            getResult(city)
        //binding.getWeather.setOnClickListener {
        //    var city = binding.searchCity.text.toString()
        //    getResult(city)
        //}
    }

    private fun getResult(city: String){
        val url = "https://api.weatherapi.com/v1/forecast.json" +
                "?key=$API_KEY&q=$city&days=10&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                    result-> parseWeatherData(result)
            },
            {
                    error -> Log.d("MyLog","Error: $error")
            }
        )
        queue.add(request)
    }
    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val day = daysArray[0] as JSONObject
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            day.getJSONObject("day").getString("maxtemp_c"),
            day.getJSONObject("day").getString("mintemp_c"),
            mainObject.getJSONObject("current").getString("wind_mph"),
            day.getJSONObject("astro").getString("sunrise"),
            day.getJSONObject("astro").getString("sunset"),
            mainObject.getJSONObject("current").getString("pressure_mb"),
            mainObject.getJSONObject("current").getString("humidity"),
            mainObject.getJSONObject("current").getString("cloud")
        )
        binding.sunrise.text = item.sunrise
        binding.sunset.text = item.sunset
        binding.city.text = item.city
        binding.temp.text = item.currentTemp + "°C"
        binding.tempMax.text = item.maxTemp
        binding.tempMin.text = item.minTemp
        binding.humidity.text = item.humidity
        binding.pressure.text = item.pressure
        binding.wind.text = item.wind
        binding.status.text = item.condition
        binding.cloud.text = item.cloud
        binding.timeDate.text = item.time

        /*Log.d("MyLog", "City: ${item.city}")
        Log.d("MyLog", "Time: ${item.time}")
        Log.d("MyLog", "Condition: ${item.condition}")
        Log.d("MyLog", "Temp: ${item.currentTemp}")
        Log.d("MyLog", "Wind: ${item.wind}")
        Log.d("MyLog", "Max: ${item.maxTemp}")
        Log.d("MyLog", "Min: ${item.minTemp}")
        Log.d("MyLog", "SunRise: ${item.sunrise}")
        Log.d("MyLog", "SunSet: ${item.sunset}")
        Log.d("MyLog", "pressure_mb: ${item.pressure}")
        Log.d("MyLog", "humidity: ${item.humidity}")*/
    }


    companion object {
        @JvmStatic
        fun newInstance() = WeatherFragment()
    }
}