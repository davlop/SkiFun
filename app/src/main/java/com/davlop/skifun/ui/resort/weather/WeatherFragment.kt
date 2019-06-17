package com.davlop.skifun.ui.resort.weather

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.davlop.skifun.R
import com.davlop.skifun.data.model.ResortSkiPlace
import com.davlop.skifun.ui.resort.ResortCallback
import com.davlop.skifun.utils.DateTimeUtils
import com.davlop.skifun.viewmodel.ResortActivityViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_weather.*
import timber.log.Timber
import javax.inject.Inject

class WeatherFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var resortActivityViewModel: ResortActivityViewModel
    private lateinit var adapter: WeatherAdapter

    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = WeatherAdapter()
        weather_list?.adapter = adapter
        weather_list?.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager
                .VERTICAL))

        activity?.let {
            resortActivityViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                    .get(ResortActivityViewModel::class.java)
        }

        resortActivityViewModel.loadResort(object : ResortCallback {
            override fun onResortLoaded(resort: ResortSkiPlace?) {
                subscribeToWeatherUpdates()
            }
        })
    }

    private fun subscribeToWeatherUpdates() {
        resortActivityViewModel.forceWeatherUpdate()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                        {},
                        { Timber.w(it) }
                )?.let {
                    disposable.add(it)
                    changeLastUpdate()
                }

        resortActivityViewModel.getWeatherForecast()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                        { adapter.setWeatherItemsList(it.filterNotNull()) },
                        { Timber.w(it) }
                )
                ?.let {
                    disposable.add(it)
                }
    }

    private fun changeLastUpdate() {
        update_tv?.text = "${resources.getString(R.string.last_updated)} ${DateTimeUtils
                .getNowFormatted()}"
    }

    override fun onDetach() {
        super.onDetach()
        disposable.clear()
    }

}
