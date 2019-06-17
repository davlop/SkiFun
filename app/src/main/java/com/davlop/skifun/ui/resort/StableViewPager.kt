package com.davlop.skifun.ui.resort

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

// Custom ViewPager that overrides onInterceptTouchEvent since the method causes a crash when
// zooming out from a PhotoView
// See issue => https://github.com/chrisbanes/PhotoView#issues-with-viewgroups

class StableViewPager : ViewPager {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            return false
        }
    }

}
