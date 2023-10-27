package org.michaelbel.movies.settings.ktx

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.settings_impl.R

internal val FeedView.feedViewText: String
    @Composable get() = when (this) {
        is FeedView.List -> stringResource(R.string.settings_appearance_list)
        is FeedView.Grid -> stringResource(R.string.settings_appearance_grid)
    }