package org.michaelbel.movies.account.ui

import androidx.compose.foundation.background
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import org.michaelbel.movies.account_impl.R
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.ui.compose.iconbutton.CloseIcon
import org.michaelbel.movies.ui.preview.DevicePreviews
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun AccountToolbar(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.account_title),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
            )
        },
        modifier = modifier,
        navigationIcon = {
            CloseIcon(
                onClick = onNavigationIconClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
    )
}

@Composable
@DevicePreviews
private fun AccountToolbarPreview() {
    MoviesTheme {
        AccountToolbar(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            onNavigationIconClick = {}
        )
    }
}

@Composable
@Preview
private fun AccountToolbarAmoledPreview() {
    MoviesTheme(
        theme = AppTheme.Amoled
    ) {
        AccountToolbar(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            onNavigationIconClick = {}
        )
    }
}