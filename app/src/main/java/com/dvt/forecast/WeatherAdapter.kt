package com.dvt.forecast

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dvt.forecastlibrary.network.response.model.Forecast
import com.dvt.forecastlibrary.network.response.model.MainInformation
import com.dvt.forecastlibrary.network.response.model.Weather


import java.text.SimpleDateFormat
import java.util.Date

class WeatherAdapter(private var forecastList: List<Forecast>) : RecyclerView.Adapter<WeatherAdapter.ItemViewHolder>() {

    private lateinit var context: Context


    fun setForecastList(forecastList: List<Forecast>) {
        this.forecastList = forecastList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {

        context = viewGroup.context
        val inflater = LayoutInflater.from(context)

        val itemView = inflater.inflate(R.layout.weather_tem, viewGroup, false)

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, i: Int) {
        itemViewHolder.bind(forecastList[i])
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dayOfForecastTv: TextView = itemView.findViewById(R.id.day)
        private val weatherIconIv: ImageView = itemView.findViewById(R.id.weather_icon)
        private val temperatureTv: TextView = itemView.findViewById(R.id.temperature)

        fun bind(forecast: Forecast) {

            val mainInformation = forecast.mainInformation
            val temperature = mainInformation.tempurature

            val date = Date(forecast.dt * 1000)
            val formattedDate = SimpleDateFormat("EEEE").format(date)

            dayOfForecastTv.text = formattedDate
            weatherIconIv.setImageResource(R.drawable.clear3x)
            temperatureTv.text = String.format("%s %s", temperature, "\u00B0")

            val weatherInfo = forecast.weather[0]
            when (weatherInfo.main.toLowerCase()) {

                "rain" -> {
                    val weatherIcon = ContextCompat.getDrawable(context, R.drawable.rain2x)
                    weatherIconIv.setImageDrawable(weatherIcon)
                }

                "clear" -> {
                    val weatherIcon = ContextCompat.getDrawable(context, R.drawable.clear2x)
                    weatherIconIv.setImageDrawable(weatherIcon)
                }

                "clouds" -> {
                    val weatherIcon = ContextCompat.getDrawable(context, R.drawable.partlysunny2x)
                    weatherIconIv.setImageDrawable(weatherIcon)
                }
            }

        }
    }
}
