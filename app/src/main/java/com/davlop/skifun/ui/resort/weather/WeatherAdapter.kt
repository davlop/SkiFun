package com.davlop.skifun.ui.resort.weather

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.davlop.skifun.R
import com.davlop.skifun.data.model.WeatherItem
import com.davlop.skifun.databinding.ItemWeatherBinding

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private var weatherItemsList: List<WeatherItem>? = null

    fun setWeatherItemsList(list: List<WeatherItem>) {
        if (weatherItemsList == null) {
            weatherItemsList = list
            notifyItemRangeInserted(0, list.size)
        } else {
            val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = weatherItemsList?.size ?: 0

                override fun getNewListSize(): Int = list.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        weatherItemsList?.get(oldItemPosition)?.id?.equals(list.get
                        (newItemPosition).id) ?: false

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int):
                        Boolean =
                        weatherItemsList?.get(oldItemPosition)?.cityName.equals(list.get
                        (newItemPosition).cityName)
                                && weatherItemsList?.get(oldItemPosition)?.weather.equals(list.get
                        (newItemPosition).weather)
                                && weatherItemsList?.get(oldItemPosition)?.weatherDescription
                                .equals(list.get(newItemPosition).weatherDescription)

            })
            weatherItemsList = list
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemWeatherBinding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.context), R.layout.item_weather, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = weatherItemsList?.get(position)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = weatherItemsList?.size ?: 0

    class ViewHolder(val binding: ItemWeatherBinding) : RecyclerView.ViewHolder(binding.root)
}