package org.michaelbel.moviemade.ui.modules.movie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.transition.GestureTransitions;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.michaelbel.moviemade.Moviemade;
import org.michaelbel.moviemade.R;
import org.michaelbel.moviemade.utils.Browser;
import org.michaelbel.moviemade.data.dao.Movie;
import org.michaelbel.moviemade.moxy.MvpAppCompatFragment;
import org.michaelbel.moviemade.room.dao.MovieDao;
import org.michaelbel.moviemade.room.database.MoviesDatabase;
import org.michaelbel.moviemade.ui.modules.movie.views.RatingView;
import org.michaelbel.moviemade.ui.widgets.EmptyView;
import org.michaelbel.moviemade.utils.ConstantsKt;
import org.michaelbel.moviemade.utils.DrawableUtil;

import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

@SuppressWarnings("all")
public class MovieFragment extends MvpAppCompatFragment implements MovieMvp, View.OnClickListener {

    private Menu actionMenu;
    private MenuItem menu_share;
    private MenuItem menu_tmdb;
    private MenuItem menu_imdb;
    private MenuItem menu_homepage;

    private View view;
    private String imdbId;
    private String homepage;
    private String posterPath;
    private boolean connectionError;
    private MovieActivity activity;
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    @Inject MoviesDatabase moviesDatabase;
    @InjectPresenter MoviePresenter presenter;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.empty_view) EmptyView emptyView;
    @BindView(R.id.poster_image) ImageView posterImage;
    @BindView(R.id.info_layout) LinearLayout infoLayout;
    @BindView(R.id.rating_view) RatingView ratingView;
    @BindView(R.id.rating_text) TextView ratingText;
    @BindView(R.id.vote_count_text) AppCompatTextView voteCountText;
    @BindView(R.id.date_layout) LinearLayout releaseDateLayout;
    @BindView(R.id.release_date_icon) ImageView releaseDateIcon;
    @BindView(R.id.release_date_text) TextView releaseDateText;
    @BindView(R.id.runtime_icon) ImageView runtimeIcon;
    @BindView(R.id.runtime_text) TextView runtimeText;
    @BindView(R.id.lang_layout) LinearLayout langLayout;
    @BindView(R.id.lang_icon) ImageView langIcon;
    @BindView(R.id.lang_text) TextView langText;
    @BindView(R.id.title_layout) LinearLayout titleLayout;
    @BindView(R.id.title_text) TextView titleText;
    @BindView(R.id.tagline_text) TextView taglineText;
    @BindView(R.id.overview_text) TextView overviewText;
    @BindView(R.id.watchlist_layout) LinearLayout watchLayout;
    @BindView(R.id.watchlist_icon) ImageView watchIcon;
    @BindView(R.id.watchlist_text) AppCompatTextView watchText;
    @BindView(R.id.trailers_layout) FrameLayout trailersLayout;
    @BindView(R.id.reviews_layout) FrameLayout reviewsLayout;
    @BindView(R.id.reviews_icon) AppCompatImageView reviewsIcon;
    @BindView(R.id.reviews_text) AppCompatTextView reviewsText;

    @BindView(R.id.crew_layout) LinearLayoutCompat crewLayout;
    @BindView(R.id.starring_text) AppCompatTextView starringText;
    @BindView(R.id.directed_text) AppCompatTextView directedText;
    @BindView(R.id.written_text) AppCompatTextView writtenText;
    @BindView(R.id.produced_text) AppCompatTextView producedText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MovieActivity) getActivity();
        activity.registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        Moviemade.getComponent().injest(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        actionMenu = menu;
        menu_share = menu.add(R.string.share).setIcon(R.drawable.ic_anim_share).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu_tmdb = menu.add(R.string.view_on_tmdb).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == menu_share) {
            Drawable icon = actionMenu.getItem(0).getIcon();
            if (icon instanceof Animatable) {
                ((Animatable) icon).start();
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.US, ConstantsKt.TMDB_MOVIE, activity.movie.getId()));
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        } else if (item == menu_tmdb) {
            Browser.INSTANCE.openUrl(activity, String.format(Locale.US, ConstantsKt.TMDB_MOVIE, activity.movie.getId()));
        } else if (item == menu_imdb) {
            Browser.INSTANCE.openUrl(activity, String.format(Locale.US, ConstantsKt.IMDB_MOVIE, imdbId));
        } else if (item == menu_homepage) {
            Browser.INSTANCE.openUrl(activity, homepage);
        }

        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle args) {
        view = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, view);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        runtimeText.setText(R.string.loading_status);
        runtimeIcon.setImageDrawable(DrawableUtil.INSTANCE.getIcon(activity, R.drawable.ic_clock, ContextCompat.getColor(activity, R.color.iconActive)));

        taglineText.setText(R.string.loading_tagline);

        starringText.setText("Loading starring...");
        directedText.setText("Loading directors...");
        writtenText.setText("Loading writers...");
        producedText.setText("Loading producers...");

        posterImage.setOnClickListener(this);
        trailersLayout.setOnClickListener(this);
        reviewsLayout.setOnClickListener(this);
        watchLayout.setOnClickListener(this);

        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void setPoster(String posterPath) {
        this.posterPath = posterPath;

        posterImage.setVisibility(VISIBLE);
        Glide.with(activity).load(String.format(Locale.US, ConstantsKt.TMDB_IMAGE, "w342", posterPath)).thumbnail(0.1F).into(posterImage);
    }

    @Override
    public void setMovieTitle(String title) {
        titleText.setText(title);
    }

    @Override
    public void setOverview(String overview) {
        if (TextUtils.isEmpty(overview)) {
            overviewText.setText(R.string.no_overview);
            return;
        }

        overviewText.setText(overview);
    }

    @Override
    public void setVoteAverage(float voteAverage) {
        ratingView.setRating(voteAverage);
        ratingText.setText(String.valueOf(voteAverage));
    }

    @Override
    public void setVoteCount(int voteCount) {
        voteCountText.setText(String.valueOf(voteCount));
    }

    @Override
    public void setReleaseDate(String releaseDate) {
        if (TextUtils.isEmpty(releaseDate)) {
            infoLayout.removeView(releaseDateLayout);
            return;
        }

        releaseDateIcon.setImageDrawable(DrawableUtil.INSTANCE.getIcon(activity, R.drawable.ic_calendar, ContextCompat.getColor(activity, R.color.iconActive)));
        releaseDateText.setText(releaseDate);
    }

    @Override
    public void setOriginalLanguage(String originalLanguage) {
        if (TextUtils.isEmpty(originalLanguage)) {
            infoLayout.removeView(langLayout);
            return;
        }

        langIcon.setImageDrawable(DrawableUtil.INSTANCE.getIcon(activity, R.drawable.ic_earth, ContextCompat.getColor(activity, R.color.iconActive)));
        langText.setText(originalLanguage);
    }

    @Override
    public void setRuntime(String runtime) {
        if (runtime == null) {
            runtimeText.setText(R.string.unknown);
            return;
        }

        runtimeText.setText(runtime);
    }

    @Override
    public void setTagline(String tagline) {
        if (tagline == null || TextUtils.isEmpty(tagline)) {
            titleLayout.removeView(taglineText);
            return;
        }

        taglineText.setText(tagline);
    }

    @Override
    public void setURLs(String imdbId, String homepage) {
        this.imdbId = imdbId;
        this.homepage = homepage;

        if (imdbId != null && !TextUtils.isEmpty(imdbId)) {
            menu_imdb = actionMenu.add(R.string.view_on_imdb).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        if (homepage != null && !TextUtils.isEmpty(homepage)) {
            menu_homepage = actionMenu.add(R.string.view_homepage).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        }
    }

    @Override
    public void setWatching(boolean watch) {

    }

    @Override
    public void showConnectionError() {
        Snackbar.make(view, R.string.no_connection, Snackbar.LENGTH_SHORT).show();
        connectionError = true;
    }

    Movie watchMovie;

    @Override
    public void showComplete(Movie movie) {
        connectionError = false;
        //setWatch();
        watchMovie = movie;
    }

    void setWatch() {
        watchIcon.setImageResource(R.drawable.ic_bookmark_plus_outline);
        watchText.setText("Add to Watchlist");
        watchLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == trailersLayout) {
            activity.startTrailers(activity.movie);
            /*AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    MovieDao movieDao = moviesDatabase.movieDao();
                    List<org.michaelbel.moviemade.room.entity.Movie> parts = movieDao.getAll();
                    for (org.michaelbel.moviemade.room.entity.Movie m : parts) {
                        Log.e("2580", m.title);
                    }
                }
            });*/
        } else if (v == watchLayout) {
            addToRoom();
        } else if (v == posterImage) {
            activity.imageAnimator = GestureTransitions.from(posterImage).into(activity.fullImage);
            activity.imageAnimator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
                @Override
                public void onPositionUpdate(float position, boolean isLeaving) {
                    activity.fullBackground.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
                    activity.fullBackground.setAlpha(position);

                    activity.fullImageToolbar.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
                    activity.fullImageToolbar.setAlpha(position);

                    activity.fullImage.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);

                    Glide.with(activity).load(String.format(Locale.US, ConstantsKt.TMDB_IMAGE, "original", posterPath)).thumbnail(0.1F).into(activity.fullImage);

                    if (position == 0f && isLeaving) {
                        activity.showSystemStatusBar(true);
                    }
                }
            });

            activity.fullImage.getController().getSettings()
                .setGravity(Gravity.CENTER)
                .setZoomEnabled(true)
                .setAnimationsDuration(300L)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(false)
                .setFitMethod(Settings.Fit.INSIDE)
                .setPanEnabled(true)
                .setRestrictRotation(false)
                .setOverscrollDistance(activity, 32F, 32F)
                .setOverzoomFactor(Settings.OVERZOOM_FACTOR)
                .setFillViewport(true);
            activity.imageAnimator.enterSingle(true);
        } else if (v == reviewsLayout) {
            activity.startReviews(activity.movie);
        }
    }

    void addToRoom() {
        org.michaelbel.moviemade.room.entity.Movie movie = new org.michaelbel.moviemade.room.entity.Movie();
        movie.movieId = watchMovie.getId();
        movie.posterPath = watchMovie.getPosterPath();
        movie.title = watchMovie.getTitle();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MovieDao movieDao = moviesDatabase.movieDao();
                movieDao.insert(movie);
            }
        });
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

            if (networkInfo != null && networkInfo.isConnected()) {
                if (connectionError) {
                    presenter.loadMovieDetails(activity.movie.getId());
                }
            }
        }
    }

    /*@Override
    public void showMovie(Movie movie) {
        //buttonsLayout.setVisibility(VISIBLE);

        movieView.favoriteButtonVisibility(loaded ? View.VISIBLE : View.INVISIBLE);
        movieView.watchingButtonVisibility(loaded ? View.VISIBLE : View.INVISIBLE);
        movieView.setFavoriteButton();
        movieView.setWatchingButton();
        movieView.topLayoutLoaded();
        movieView.addStatus(movie.status);
        movieView.addBudget(movie.budget);
        movieView.addRevenue(movie.revenue);
        movieView.addCountries(AndroidUtils.formatCountries(movie.countries));
        if (movieView.getCompanies().isEmpty()) {
            movieView.addCompanies(movie.companies);
        }

        movieView.addGenres(movie.genres);

        movieView.addImdbpage(movie.imdbId);
        movieView.addHomepage(movie.homepage);
        movieView.addCollection(movie.belongsToCollection);*//*

        //presenter.loadCredits(movie.id);
        //presenter.loadTrailers(movie.id);
        //presenter.loadImages(movie.id);
        //presenter.loadKeywords(movie.id);

        //genres.clear();
        //genres.addAll(movie.genres);
        //movieView.getGenresView().setClickable(true);
    }

    @Override
    public void onWatchingButtonClick(View view) {
        *//*if (loadedMovie != null) {
            presenter.setMovieWatching(loadedMovie);
        } else if (extraMovieRealm != null) {
            presenter.setMovieWatching(extraMovieRealm);
        }*//*
    }

    @Override
    public void onMovieUrlClick(View view, int position) {
        *//*if (extraMovieRealm != null) {
            if (position == 1) {
                Browser.openUrl(activity, String.format(Locale.US, Url.TMDB_MOVIE, extraMovieRealm.id));
            } else if (position == 2) {
                Browser.openUrl(activity, String.format(Locale.US, Url.IMDB_MOVIE, extraMovieRealm.imdbId));
            } else if (position == 3) {
                Browser.openUrl(activity, extraMovieRealm.homepage);
            }
        } else if (position == 1) {
            Browser.openUrl(activity, String.format(Locale.US, Url.TMDB_MOVIE, extraMovie.id));
        } else if (position == 2) {
            Browser.openUrl(activity, String.format(Locale.US, Url.IMDB_MOVIE, loadedMovie.imdbId));
        } else if (position == 3) {
            Browser.openUrl(activity, loadedMovie.homepage);
        }*//*
    }

    @Override
    public void onPostersClick(View view) {
        *//*if (extraMovie != null) {
            Browser.openUrl(activity, String.format(Locale.US, Url.TMDB_MOVIE_POSTERS, extraMovie.id));
        } else if (extraMovieRealm != null) {
            Browser.openUrl(activity, String.format(Locale.US, Url.TMDB_MOVIE_POSTERS, extraMovieRealm.id));
        }*//*
    }

    @Override
    public void onBackdropsClick(View view) {
        *//*if (extraMovie != null) {
            Browser.openUrl(activity, String.format(Locale.US, Url.TMDB_MOVIE_BACKDROPS, extraMovie.id));
        } else if (extraMovieRealm != null) {
            Browser.openUrl(activity, String.format(Locale.US, Url.TMDB_MOVIE_BACKDROPS, extraMovieRealm.id));
        }*//*
    }

    */
}