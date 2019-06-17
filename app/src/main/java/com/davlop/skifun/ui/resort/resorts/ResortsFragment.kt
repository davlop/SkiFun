package com.davlop.skifun.ui.resort.resorts

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.davlop.skifun.R
import com.davlop.skifun.data.model.ResortSkiPlace
import com.davlop.skifun.databinding.FragmentResortsBinding
import com.davlop.skifun.di.GlideApp
import com.davlop.skifun.ui.resort.ResortCallback
import com.davlop.skifun.utils.getTrailsLegend
import com.davlop.skifun.viewmodel.ResortActivityViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_resorts.*
import javax.inject.Inject

class ResortsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var resortActivityViewModel: ResortActivityViewModel
    private lateinit var binding: FragmentResortsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_resorts, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            resortActivityViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                    .get(ResortActivityViewModel::class.java)
        }

        resortActivityViewModel.loadResort(object : ResortCallback {
            override fun onResortLoaded(resort: ResortSkiPlace?) {
                binding.item = resort
                loadPistesImage()
                resortActivityViewModel.loadResortTrails(object : TrailsCallback {
                    override fun onTrailsReady() {
                        loadTrailsChart()
                    }
                })
            }
        })

        download_icon?.isClickable = true
        download_icon?.setOnClickListener {
            resortActivityViewModel.downloadPistesImage(
                    OnSuccessListener {
                        Toast.makeText(context, R.string.image_saved, Toast.LENGTH_LONG).show()
                        download_icon.visibility = View.GONE
                    },
                    OnFailureListener {
                        Toast.makeText(context, R.string.image_failed, Toast.LENGTH_LONG).show()
                    },
                    object: OnImageSavedListener {
                        override fun onImageReady(uri: Uri) {
                            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                            mediaScanIntent.data = uri
                            context?.sendBroadcast(mediaScanIntent)
                        }
                    }
            )
        }
    }

    private fun loadPistesImage() {
        if (resortActivityViewModel.resort == null || activity == null) return

        val pistesImageReference = resortActivityViewModel.getResortPistesImageReference()

        if (pistesImageReference == null) {
            showNoImageFound()
            return
        }

        if (!isDeviceConnected()) {
            error_msg.text = activity?.getString(R.string.no_internet)
            progress_bar.visibility = View.GONE
        } else {
            GlideApp.with(this)
                    .load(pistesImageReference)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .dontTransform()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            error_msg.text = activity?.getString(R.string.error_unknown)
                            progress_bar.visibility = View.GONE
                            e?.printStackTrace()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            progress_bar.visibility = View.GONE
                            error_msg.visibility = View.GONE
                            download_icon.visibility = View.VISIBLE
                            pistes_map.visibility = View.VISIBLE
                            pistes_map.maximumScale = 8.5f
                            return false
                        }

                    })
                    .into(pistes_map)
        }
    }

    private fun loadTrailsChart() {
        resortActivityViewModel.resort ?: return
        val trails = resortActivityViewModel.resort!!.trails

        if (trails != null) {
            val entries = MutableList(4) { PieEntry(0f) }
            for (i in 1..4) {
                trails.get(i)?.let {
                    entries.add(PieEntry(it.percentage.toFloat(), it.percentage.toString() + "%"))
                }
            }

            val dataSet = PieDataSet(entries, "")
            if (activity != null) {
                dataSet.setColors(
                        activity!!.resources.getColor(R.color.easyTrail),
                        activity!!.resources.getColor(R.color.mediumTrail),
                        activity!!.resources.getColor(R.color.advancedTrail),
                        activity!!.resources.getColor(R.color.expertTrail))

                dataSet.valueLineColor = activity!!.resources.getColor(R.color.spaceChart)
            }
            dataSet.setDrawValues(false)
            dataSet.sliceSpace = 3f

            if (trails_chart != null) {
                trails_chart.data = PieData(dataSet)
                trails_chart.description.isEnabled = false
                trails_chart.holeRadius = 40f
                trails_chart.isRotationEnabled = false
                trails_chart.legend.form = Legend.LegendForm.NONE
                trails_chart.rotationAngle = 205f
                trails_chart.setDrawCenterText(false)
                trails_chart.setEntryLabelTextSize(11f)
                trails_chart.setNoDataText("")
                trails_chart.setTransparentCircleColor(Color.WHITE)
                trails_chart.invalidate()
            }

            trails_legend?.text = getTrailsLegend(trails, context)

            trails_chart?.visibility = View.VISIBLE
            trails_legend?.visibility = View.VISIBLE
            no_trails_tv?.visibility = View.INVISIBLE
        } else {
            hidePistesChart()
        }
    }

    private fun hidePistesChart() {
        val elevationTvParams = elevation_tv?.layoutParams as RelativeLayout.LayoutParams
        elevationTvParams.addRule(RelativeLayout.BELOW, R.id.no_trails_tv)
        elevation_tv?.layoutParams = elevationTvParams

        trails_icon?.layoutParams?.height = resources.getDimensionPixelSize(R.dimen
                .resort_info_tv_height)
        trails_icon?.requestLayout()

        trails_chart?.visibility = View.GONE
        trails_legend?.visibility = View.GONE
        no_trails_tv?.visibility = View.VISIBLE
    }

    private fun showNoImageFound() {
        download_icon?.visibility = View.GONE
        progress_bar?.visibility = View.GONE
        pistes_map?.visibility = View.GONE
        error_msg?.text = activity?.getString(R.string.error_no_pistes_found)
        error_msg?.visibility = View.VISIBLE
    }

    private fun isDeviceConnected() : Boolean {
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

}
interface OnImageSavedListener {
    fun onImageReady(uri: Uri)
}

interface TrailsCallback {
    fun onTrailsReady()
}