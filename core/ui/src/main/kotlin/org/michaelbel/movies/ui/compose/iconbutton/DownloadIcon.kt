package org.michaelbel.movies.ui.compose.iconbutton

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.preview.DevicePreviews
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun DownloadIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Image(
            imageVector = MoviesIcons.FileDownload,
            contentDescription = stringResource(MoviesContentDescription.DownloadIcon),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
        )
    }
}

@Composable
@DevicePreviews
private fun DownloadIconPreview() {
    MoviesTheme {
        DownloadIcon(
            onClick = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}

@Composable
@Preview
private fun DownloadIconAmoledPreview() {
    MoviesTheme(
        theme = AppTheme.Amoled
    ) {
        DownloadIcon(
            onClick = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}