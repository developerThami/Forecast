package com.dvt.forecast

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*

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

        val appId = "b6907d289e10d714a6e88b30761fae22"

        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()

        RetrofitForecastClient().fetchForecast(latitude, longitude, appId)
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

        val appId = "b6907d289e10d714a6e88b30761fae22"

        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()

        RetrofitForecastClient().fetchCurrentWeather(latitude, longitude, appId)
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

        when(weatherInfo.main.toLowerCase()){

            "rain" -> {
                val backgroundImageDrawable = ContextCompat.getDrawable(this, R.drawable.forest_rainy)
                weather_background.setImageDrawable(backgroundImageDrawable)

                val color = ContextCompat.getColor(this, R.color.rainy)
                temperature_information.setBackgroundColor(color)
                weather_list.setBackgroundColor(color)

                val displayTemperature = String.format("%s %s", mainInformation.tempurature, "\u00B0")
                current_temperature.text = displayTemperature
                temperature_description.text = "RAINY"
            }

            "clear"->{
                val backgroundImageDrawable = ContextCompat.getDrawable(this, R.drawable.forest_sunny)
                weather_background.setImageDrawable(backgroundImageDrawable)

                val color = ContextCompat.getColor(this, R.color.sunny)
                temperature_information.setBackgroundColor(color)
                weather_list.setBackgroundColor(color)

                val displayTemperature = String.format("%s %s", mainInformation.tempurature, "\u00B0")
                current_temperature.text = displayTemperature
                temperature_description.text = "SUNNY"

            }

            "clouds"->{
                val backgroundImageDrawable = ContextCompat.getDrawable(this, R.drawable.forest_cloudy)
                weather_background.setImageDrawable(backgroundImageDrawable)

                val color = ContextCompat.getColor(this, R.color.cloudy)
                temperature_information.setBackgroundColor(color)
                weather_list.setBackgroundColor(color)

                val displayTemperature = String.format("%s %s", mainInformation.tempurature, "\u00B0")
                current_temperature.text = displayTemperature
                temperature_description.text = "CLOUDY"
            }
        }



        val currentTemperature = mainInformation.tempurature
        val minTemperature = mainInformation.minimumTempurature
        val maxTemperature = mainInformation.maximumTempurature

        val date = Date(response.dt * 1000)
        val formattedDate = SimpleDateFormat("EEEE").format(date)

        val formattedTemperature = String.format("%s %s", currentTemperature, "\u00B0")
        val formattedMinTemperature = String.format("%s %s", minTemperature, "\u00B0")
        val formattedMaxTemperature = String.format("%s %s", maxTemperature, "\u00B0")

        temperature.text = formattedTemperature
        min_temperature.text = formattedMinTemperature
        max_temperature.text = formattedMaxTemperature
    }

    private fun requestLocationUpdates() {

        val fineLocationPermissionCheck = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
        val coarseLocationPermissionCheck = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)

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

        weatherAdapter.setForecastList(forecastResponse.forecast)
        weatherAdapter.notifyDataSetChanged()

    }

    private fun onError(throwable: Throwable?) {

        Log.d("TAG", throwable.toString())
    }
}
