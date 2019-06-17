package com.davlop.skifun.ui.resort

import com.davlop.skifun.data.model.ResortSkiPlace

interface ResortCallback {
    fun onResortLoaded(resort: ResortSkiPlace?)
}