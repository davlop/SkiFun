package com.davlop.skifun.utils

import android.content.Context
import android.telephony.PhoneNumberUtils
import com.davlop.skifun.R
import com.davlop.skifun.data.model.TrailType

fun getElevationFormatted(low: Int?, high: Int?, context: Context): String {
    if (low != null && high != null) {
        return context.getString(R.string.elevation) + ": $low" + "-" + "$high" + "m"
    }
    else return context.getString(R.string.no_elevation)
}

fun getPriceFormatted(adults: Int?, kids: Int?, context: Context): String {
    if (adults != null && kids != null) {
        return context.getString(R.string.price_rates) + ": " + context.getString(
                R.string.adults) + ": "+ adults + "€ " + context .getString(R.string.children) +
                ": " + kids + "€"
    }
    else if (adults != null) {
        return context.getString(R.string.price_rates) + ": " + context.getString(
                R.string.adults) + ": " + adults + "€" + context.getString(R.string.children) +
                ": " + context.getString(R.string.not_available)
    }
    else if (kids != null) {
        return context.getString(R.string.price_rates) + ": " + context.getString(
                R.string.not_available) + ": " + context.getString(R.string.not_available) + " " +
                context.getString(R.string.children) + ": " + kids + "€"
    }
    else return context.getString(R.string.no_price)
}

fun getWebsiteFormatted(website: String?, context: Context): String {
    if (website != null) return website
    else return context.getString(R.string.no_website)
}

fun getPhoneFormatted(phone: String?, context: Context): String {
    if (phone != null) return PhoneNumberUtils.formatNumber(phone)
    else return context.getString(R.string.no_phone)
}

fun getTrailsLegend(trails: HashMap<Int,TrailType>, context: Context?): String {
    if (context == null) return ""

    val builder = StringBuilder()
    builder.append(context.getString(R.string.trails).toUpperCase())
    builder.append("\n\n")

    for (i in 0..3) {
        val trail = trails[i+1]
        builder.append("•")
        builder.append(context.resources.getStringArray(R.array.trail_levels)[i])
        builder.append(": ")
        builder.append(trail?.trailsnumber ?: 0)
        if (trail?.totallength != null) {
            builder.append(" (")
            builder.append(trail.totallength.toString())
            builder.append("km)")
        }
        builder.append("\n")
    }

    return builder.toString()
}