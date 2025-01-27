package org.michaelbel.movies.ui.compose.iconbutton

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.preview.DevicePreviews
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun BackIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onContainerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Image(
            imageVector = MoviesIcons.ArrowBack,
            contentDescription = stringResource(MoviesContentDescription.BackIcon),
            colorFilter = ColorFilter.tint(onContainerColor)
        )
    }
}

@Composable
@DevicePreviews
private fun BackIconPreview() {
    MoviesTheme {
        BackIcon(
            onClick = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}

@Composable
@Preview
private fun BackIconAmoledPreview() {
    MoviesTheme(
        theme = AppTheme.Amoled
    ) {
        BackIcon(
            onClick = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}