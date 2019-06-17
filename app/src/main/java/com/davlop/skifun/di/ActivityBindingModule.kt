package com.davlop.skifun.di

import com.davlop.skifun.ui.map.MapActivity
import com.davlop.skifun.ui.resort.ResortActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector()
    abstract fun bindMapActivity() : MapActivity

    @ContributesAndroidInjector(modules = [ResortFragmentsModule::class])
    abstract fun bindResortActivity() : ResortActivity

}
