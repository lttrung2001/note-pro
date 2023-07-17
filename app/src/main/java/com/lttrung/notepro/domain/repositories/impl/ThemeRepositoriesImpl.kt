package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.networks.ThemeNetworks
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.lttrung.notepro.domain.repositories.ThemeRepositories
import javax.inject.Inject

class ThemeRepositoriesImpl @Inject constructor(
    override val networks: ThemeNetworks
) : ThemeRepositories {
    override suspend fun getThemeList(): List<Theme> {
        return networks.fetchThemeList().data
    }
}