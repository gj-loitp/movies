package org.michaelbel.moviemade.ui.modules.recommendations;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import org.michaelbel.moviemade.BuildConfig;
import org.michaelbel.moviemade.Moviemade;
import org.michaelbel.moviemade.data.entity.Movie;
import org.michaelbel.moviemade.data.service.MoviesService;
import org.michaelbel.moviemade.utils.EmptyViewMode;
import org.michaelbel.moviemade.utils.NetworkUtil;
import org.michaelbel.moviemade.utils.TmdbConfigKt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class RecommendationsMoviesPresenter extends MvpPresenter<RecommendationsMvp> {

    private int page;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    MoviesService service;

    RecommendationsMoviesPresenter() {
        Moviemade.getAppComponent().injest(this);
    }

    void getRecommendations(int movieId) {
        // TODO add to response
        if (NetworkUtil.INSTANCE.notConnected()) {
            getViewState().setError(EmptyViewMode.MODE_NO_CONNECTION);
            return;
        }

        page = 1;
        disposables.add(service.getRecommendations(movieId, BuildConfig.TMDB_API_KEY, TmdbConfigKt.en_US, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(response -> {
                List<Movie> results = new ArrayList<>(response.getMovies());
                if (results.isEmpty()) {
                    getViewState().setError(EmptyViewMode.MODE_NO_MOVIES);
                    return;
                }
                getViewState().setMovies(results);
            }, e -> getViewState().setError(EmptyViewMode.MODE_NO_MOVIES)));
    }

    void getRecommendationsNext(int movieId) {
        page++;
        disposables.add(service.getRecommendations(movieId, BuildConfig.TMDB_API_KEY, TmdbConfigKt.en_US, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(response -> {
                List<Movie> results = new ArrayList<>(response.getMovies());
                if (results.isEmpty()) {
                    getViewState().setError(EmptyViewMode.MODE_NO_MOVIES);
                    return;
                }
                getViewState().setMovies(results);
            }, e -> getViewState().setError(EmptyViewMode.MODE_NO_MOVIES)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}