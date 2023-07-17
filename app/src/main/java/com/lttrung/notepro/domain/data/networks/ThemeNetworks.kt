package com.lttrung.notepro.domain.data.networks

import com.lttrung.notepro.domain.data.networks.models.Theme
import javax.inject.Singleton

@Singleton
interface ThemeNetworks {
    suspend fun fetchThemeList(): ResponseEntity<List<Theme>>
}