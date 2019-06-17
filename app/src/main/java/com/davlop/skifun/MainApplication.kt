package com.davlop.skifun

import android.app.Activity
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import dagger.android.DispatchingAndroidInjector
import com.davlop.skifun.di.DaggerAppComponent
//import com.squareup.leakcanary.LeakCanary
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class MainApplication : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Fabric.with(this, Crashlytics())

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

    }

    override fun activityInjector() = dispatchingAndroidInjector

}