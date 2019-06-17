package com.davlop.skifun.di

import com.davlop.skifun.ui.resort.accommodation.AccommodationFragment
import com.davlop.skifun.ui.resort.resorts.ResortsFragment
import com.davlop.skifun.ui.resort.weather.WeatherFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ResortFragmentsModule {

    @ContributesAndroidInjector
    abstract fun contributeAccomodationFragment(): AccommodationFragment

    @ContributesAndroidInjector
    abstract fun contributeResortsFragment(): ResortsFragment

    @ContributesAndroidInjector
    abstract fun contributeWeatherFragment(): WeatherFragment

}