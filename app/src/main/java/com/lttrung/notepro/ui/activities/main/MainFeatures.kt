package com.lttrung.notepro.ui.activities.main

import android.content.Context
import com.lttrung.notepro.R
import com.lttrung.notepro.ui.entities.Feature

object MainFeatures {
    fun get(context: Context): List<Feature> {
        return listOf(
            Feature(0, R.drawable.ic_baseline_settings_24),
            Feature(1, R.drawable.ic_baseline_person_24)
        )
    }
}