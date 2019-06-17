package com.davlop.skifun.utils

import android.content.Context
import android.databinding.BindingAdapter
import android.widget.ImageView
import com.davlop.skifun.R

// translates the API's weather response into user's language
fun getWeatherInLocalLanguage(weather: String, context: Context): String {
    val resourceName = weather.toLowerCase().replace(' ', '_')
    val resourceId = context.resources.getIdentifier(resourceName, "string",
            context.packageName)

    if (resourceId != 0) return context.resources.getString(resourceId)
    else return weather
}

fun getWeatherIcon(weather: String, context: Context): Int =
        when (weather) {
            context.getString(R.string.drizzle), context.getString(R.string.atmosphere),
            context.getString(R.string.clouds) -> R.drawable.ic_clouds
            context.getString(R.string.thunderstorm) -> R.drawable.ic_storm
            context.getString(R.string.rain) -> R.drawable.ic_rain
            context.getString(R.string.snow) -> R.drawable.ic_snow
            context.getString(R.string.clear) -> R.drawable.ic_sun
            else -> R.drawable.ic_sun
        }

fun getWeatherTimeFormatted(time: String, context: Context): String =
        DateTimeUtils.convertStringToDisplayableString(time, context)

fun getTemperatureFormatted(temperature: Double): String =
        "${Integer.toString(Math.round(temperature).toInt())}°"

object WeatherDataBinding {

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageResource(view: ImageView, id: Int) =
            view.setImageResource(id)

}