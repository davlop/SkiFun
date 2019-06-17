package com.davlop.skifun.ui.resort

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.davlop.skifun.R
import com.davlop.skifun.ui.resort.accommodation.AccommodationFragment
import com.davlop.skifun.ui.resort.resorts.ResortsFragment
import com.davlop.skifun.ui.resort.weather.WeatherFragment
import com.davlop.skifun.viewmodel.ResortActivityViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_resort.*
import javax.inject.Inject

class ResortActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var resortName: String
    private lateinit var parentId: String
    private var resortId: String? = null

    private lateinit var resortActivityViewModel: ResortActivityViewModel

    private lateinit var fragmentAdapter: ResortsFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resort)

        fragmentAdapter = ResortsFragmentAdapter(supportFragmentManager)
        (view_pager as ViewPager).adapter = fragmentAdapter

        // preserve all three fragments
        (view_pager as ViewPager).offscreenPageLimit = 2

        resortName = intent.extras.getString(getString(R.string.extra_key_resortname))
        resortId = intent.extras.getString(getString(R.string.extra_key_resortId))
        parentId = intent.extras.getString(getString(R.string.extra_key_parentId))

        setTitle(resortName)

        resortActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ResortActivityViewModel::class.java)
        resortActivityViewModel.resortId = resortId
        resortActivityViewModel.parentId = parentId
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    private inner class ResortsFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int = 3

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> AccommodationFragment()
                1 -> ResortsFragment()
                2 -> WeatherFragment()
                else -> null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> resources.getStringArray(R.array.resort_tabs)[0]
                1 -> resources.getStringArray(R.array.resort_tabs)[1]
                2 -> resources.getStringArray(R.array.resort_tabs)[2]
                else -> null
            }
        }

    }
}
