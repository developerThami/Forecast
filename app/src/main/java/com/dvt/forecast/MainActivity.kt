package com.dvt.forecast

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.forecastlibrary.network.response.ForecastResponse
import com.dvt.forecastlibrary.network.response.WeatherResponse
import com.entersekt.citylibrary.network.RetrofitForecastClient
import com.google.android.gms.location.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.temperature_information.*
import java.text.SimpleDateFormat
import java.util.*
import com.dvt.forecastlibrary.network.response.model.Forecast


class MainActivity : AppCompatActivity() {

    private val requestCode = 104

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherAdapter: WeatherAdapter

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                // Update UI with location data
                fetchCurrentWeather(location)
                fetchForecast(location)
                mFusedLocationClient.removeLocationUpdates(this)
                break
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherAdapter = WeatherAdapter(emptyList())

        weather_list.setHasFixedSize(true)
        weather_list.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        weather_list.adapter = weatherAdapter

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationUpdates()
    }

    @SuppressLint("CheckResult")
    private fun fetchForecast(location: Location) {

        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()

        RetrofitForecastClient().fetchForecast(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { forecastResponse, throwable ->
                run {

                    if (forecastResponse != null) {
                        onForecastResponse(forecastResponse)
                    }

                    if (throwable != null) {
                        onError(throwable)
                    }

                }
            }
    }

    @SuppressLint("CheckResult")
    private fun fetchCurrentWeather(location: Location) {

        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()

        RetrofitForecastClient().fetchCurrentWeather(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response, throwable ->
                run {

                    if (response != null) {
                        onWeatherResponse(response)
                    }

                    if (throwable != null) {
                        onError(throwable)
                    }

                }
            }
    }

    private fun onWeatherResponse(response: WeatherResponse) {

        val mainInformation = response.mainInformation
        val weather = response.weather

        val weatherInfo = weather[0]

        val displayTemperature = String.format("%s %s", mainInformation.tempurature, "\u00B0")

        when (weatherInfo.main.toLowerCase()) {

            "rain" -> {

                val color = ContextCompat.getColor(this, R.color.rainy)
                val backgroundImageDrawable = ContextCompat.getDrawable(this, R.drawable.forest_rainy)

                showCurrentWeatherInformation(color, "RAINY", displayTemperature, backgroundImageDrawable!!)
            }

            "clear" -> {
                val color = ContextCompat.getColor(this, R.color.sunny)
                val backgroundImageDrawable = ContextCompat.getDrawable(this, R.drawable.forest_sunny)

                showCurrentWeatherInformation(color, "SUNNY", displayTemperature, backgroundImageDrawable!!)

            }
            "clouds" -> {
                val color = ContextCompat.getColor(this, R.color.cloudy)
                val backgroundImageDrawable = ContextCompat.getDrawable(this, R.drawable.forest_cloudy)

                showCurrentWeatherInformation(color, "CLOUDY", displayTemperature, backgroundImageDrawable!!)
            }
        }


        val currentTemperature = mainInformation.tempurature
        val minTemperature = mainInformation.minimumTempurature
        val maxTemperature = mainInformation.maximumTempurature

        val formattedTemperature = String.format("%s %s", currentTemperature, "\u00B0")
        val formattedMinTemperature = String.format("%s %s", minTemperature, "\u00B0")
        val formattedMaxTemperature = String.format("%s %s", maxTemperature, "\u00B0")

        temperature.text = formattedTemperature
        min_temperature.text = formattedMinTemperature
        max_temperature.text = formattedMaxTemperature
    }

    private fun showCurrentWeatherInformation(color: Int, temperatureDescription: String, temperature: String, backgroundDrawable: Drawable) {

        weather_background.setImageDrawable(backgroundDrawable)

        temperature_information.setBackgroundColor(color)
        weather_list.setBackgroundColor(color)

        current_temperature.text = temperature
        temperature_description.text = temperatureDescription
    }

    private fun requestLocationUpdates() {

        val fineLocationPermissionCheck = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)

        if (fineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), requestCode)
            return
        }

        val locationRequest = LocationRequest()

        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == requestCode) {
            requestLocationUpdates()
        }
    }

    private fun onForecastResponse(forecastResponse: ForecastResponse) {

        progressBar.visibility = View.GONE

        val forecastList: List<Forecast> = forecastResponse.forecast

        val list: MutableList<Forecast> = mutableListOf()
        val daysOfTheWeek: MutableList<String> = mutableListOf()

        forecastList.forEach { forecast ->

            val date = Date(forecast.dt * 1000)
            val dayOfTheWeek = SimpleDateFormat("EEEE").format(date)

            val timeOfDay = SimpleDateFormat("HH:mm:ss").format(date)


            if (!daysOfTheWeek.contains(dayOfTheWeek)) {

                val minTimeOfDayCalendar = Calendar.getInstance()
                minTimeOfDayCalendar.time = SimpleDateFormat("HH:mm:ss").parse("10:00:00")
                minTimeOfDayCalendar.add(Calendar.DATE, 1)

                val maxTimeOfDayCalendar = Calendar.getInstance()
                maxTimeOfDayCalendar.time = SimpleDateFormat("HH:mm:ss").parse("14:00:00")
                maxTimeOfDayCalendar.add(Calendar.DATE, 1)

                val currentTimeOfDaycalendar = Calendar.getInstance()
                currentTimeOfDaycalendar.time = SimpleDateFormat("HH:mm:ss").parse(timeOfDay)
                currentTimeOfDaycalendar.add(Calendar.DATE, 1)

                val currentTimeOfDay = currentTimeOfDaycalendar.time
                if (currentTimeOfDay.after(minTimeOfDayCalendar.time) && currentTimeOfDay.before(maxTimeOfDayCalendar.time)) {
                    //checks whether the current time is between 14:00:00 and 10:00:00
                    daysOfTheWeek.add(dayOfTheWeek)
                    list.add(forecast)
                }

            }

        }

        weatherAdapter.setForecastList(list)
        weatherAdapter.notifyDataSetChanged()

    }

    private fun onError(throwable: Throwable?) {

        Log.d("TAG", throwable.toString())
    }
}
