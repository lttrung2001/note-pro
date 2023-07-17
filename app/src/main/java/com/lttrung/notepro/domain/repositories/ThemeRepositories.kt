package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.networks.ThemeNetworks
import com.lttrung.notepro.domain.data.networks.models.Theme
import javax.inject.Singleton

@Singleton
interface ThemeRepositories {
    val networks: ThemeNetworks
    suspend fun getThemeList(): List<Theme>
}