package org.michaelbel.movies.details

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.ktx.require
import org.michaelbel.movies.common.viewmodel.BaseViewModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.network.ScreenState
import org.michaelbel.movies.network.connectivity.NetworkManager
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.network.handle

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interactor: Interactor,
    networkManager: NetworkManager
): BaseViewModel() {

    private val movieId: Int = savedStateHandle.require("movieId")

    private val _detailsState: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Loading)
    val detailsState: StateFlow<ScreenState> = _detailsState.asStateFlow()

    val networkStatus: StateFlow<NetworkStatus> = networkManager.status
        .stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = NetworkStatus.Unavailable
        )

    init {
        loadMovie()
    }

    fun retry() = loadMovie()

    private fun loadMovie() = launch {
        interactor.movieDetails(movieId).handle(
            success = { movieDetailsData ->
                _detailsState.emit(ScreenState.Content(movieDetailsData))
            },
            failure = { throwable ->
                _detailsState.emit(ScreenState.Failure(throwable))
            }
        )
    }
}