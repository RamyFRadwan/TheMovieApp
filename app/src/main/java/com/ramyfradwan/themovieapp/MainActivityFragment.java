package com.ramyfradwan.themovieapp;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ramyfradwan.themovieapp.Adapters.MoviesAdapter;
import com.ramyfradwan.themovieapp.Model.Movie;
import com.ramyfradwan.themovieapp.MovieProvider.MovieContract;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import info.movito.themoviedbapi.TmdbApi;
//import info.movito.themoviedbapi.TmdbMovies;

/**
 * Created by RamyFRadwan on 02/10/15.
 */
public class MainActivityFragment extends Fragment implements EndlessRecyclerViewAdapter.RequestToLoadMoreListener {
    LinearLayout holder;
    Collection<Movie> movies = new ArrayList<>();
    private String sort_type;
    private MoviesAdapter adapter;
    private EndlessRecyclerViewAdapter endlessRecyclerViewAdapter;
    private LinearLayoutManager mLayoutManager;
    private ImageView no_favourites_icon;
    private ImageView disconnected_icon;
    private ImageView imageView ;
    private boolean first_check;
    private RecyclerView moviesRecyclerView;
    private ProgressBar progressBar;
    private int page_num = 0;
    private static final String TAG_FRAGMENT = "MOVIE_FRAGMENT";
    private String title;
    private String image_path , backdrop_path;
    private String overView;
    private String date;
    private double rating;
    private long Movie_id;
    private GridView grid;
    private int _id;
    public static boolean menuItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new MoviesAdapter(getActivity(), null);
        imageView = (ImageView) view.findViewById(R.id.thumbnail);
        holder = (LinearLayout) view.findViewById(R.id.movieHolder);
        moviesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        Configuration config = view.getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600
                && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 1);
        } else if (config.smallestScreenWidthDp < 600
                && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }

        moviesRecyclerView.setLayoutManager(mLayoutManager);

        moviesRecyclerView.setHasFixedSize(true);
        adapter = new MoviesAdapter(getActivity(), null);
        endlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(getActivity(), adapter, this);
        moviesRecyclerView.setAdapter(endlessRecyclerViewAdapter);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        no_favourites_icon = (ImageView) view.findViewById(R.id.no_fav);
        disconnected_icon = (ImageView) view.findViewById(R.id.no_connection);

            sort_type = getResources().getString(R.string.sort_popularity);
        first_check = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        if (!connected && first_check) {
//            menu.findItem(R.id.favs).setChecked(!connected);
//            first_check = false;
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem action_sort_by_popularity = menu.findItem(R.id.sort_popular);
        MenuItem action_sort_by_rating = menu.findItem(R.id.sort_hiRate);
        MenuItem action_show_fav = menu.findItem(R.id.favs);

        if (sort_type.contentEquals(getResources().getString(R.string.sort_popularity))) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_popularity.setChecked(true);
            }
        } else if (sort_type.contentEquals(getResources().getString(R.string.sort_type_rating))) {
            if (!action_sort_by_rating.isChecked()) {
                action_sort_by_rating.setChecked(true);
            }
        } else if (sort_type.contentEquals(getResources().getString(R.string.sort_type_fav))) {
            if (!action_show_fav.isChecked()) {
                action_show_fav.setChecked(true);
                navigateToFavourites();

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_popular:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    prepareUpdate();
                }
                menuItem = false;
                sort_type = getResources().getString(R.string.sort_popularity);
                updateView();
                return true;
            case R.id.sort_hiRate:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    prepareUpdate();
                }
                menuItem =false;
                sort_type = getResources().getString(R.string.sort_type_rating);
                updateView();
                return true;
            case R.id.favs:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    prepareUpdate();
                }
                menuItem =true;
                sort_type = getResources().getString(R.string.sort_type_fav);
                updateView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean getItem(){ return menuItem;}
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getResources().getString(R.string.sort_type), sort_type);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            sort_type = savedInstanceState.getString(getResources().getString(R.string.sort_type));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(getActivity(), 1);

        }
    }

    private void navigateToFavourites() {
        sort_type = getResources().getString(R.string.sort_type_fav);
        updateView();
    }

    private void prepareUpdate() {
        movies.clear();
        adapter.clear();
        page_num = 1;
        Fragment fragment = getActivity().getSupportFragmentManager()
                .findFragmentByTag(TAG_FRAGMENT);
        if (fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(fragment).commit();
    }

    private void updateView() {
        if (sort_type.contentEquals(getResources().getString(R.string.sort_type_fav))) {
            fetchFavouritesMovies();


        }
        else {
            disconnected_icon.setVisibility(View.GONE);
            no_favourites_icon.setVisibility(View.GONE);
            moviesRecyclerView.setVisibility(View.VISIBLE);
            fetchMoviesTask task = new fetchMoviesTask();
            task.execute(String.valueOf(page_num));
//
        }
}

    private void setOnClickListenerOnItems(final Collection<Movie> movies) {
        MoviesAdapter.OnItemClickListener onItemClickListener = new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Movie mMovie;
                if (sort_type.contentEquals(getResources().getString(R.string.sort_type_fav))) {
                    mMovie = (Movie) movies.toArray()[position];
                } else {
                    mMovie = (Movie) movies.toArray()[position];
                }
                ((Callback) getActivity()).onItemSelected(mMovie, v);
            }
        };
        adapter.setOnItemClickListener(onItemClickListener);
    }

    private void fetchFavouritesMovies() {
        List<Movie> favouriteMovies = new ArrayList<>();
        final Cursor c = getActivity().getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//        final Cursor x = getActivity().getContentResolver()
//                .query(MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry.COLUMN_TITLE,);

        //Toast.makeText(getActivity(), c==null?"ok":"no", Toast.LENGTH_SHORT).show();
        if(c.moveToFirst()){

            do{
               image_path =   c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE));
                title =  c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                overView =  c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                date = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE));
                rating = c.getDouble(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
                Movie_id =  c.getLong(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                backdrop_path = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP));

                _id = c.getInt(c.getColumnIndex(MovieContract.MovieEntry._ID));
                Movie movie = new Movie(Movie_id,title,overView,date,image_path,backdrop_path,rating);
                favouriteMovies.add(movie);
            }while(c.moveToNext());
        }

        c.close();
        adapter.clear();
        adapter.addMovies(favouriteMovies);

        endlessRecyclerViewAdapter.onDataReady(true);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        disconnected_icon.setVisibility(View.GONE);
        if (favouriteMovies.size() > 0) {
            no_favourites_icon.setVisibility(View.GONE);
            moviesRecyclerView.setVisibility(View.VISIBLE);
            setOnClickListenerOnItems(favouriteMovies);
        } else {
            no_favourites_icon.setVisibility(View.VISIBLE);


        }

    }

    @Override
    public void onLoadMoreRequested() {
        if (sort_type != null
                && !sort_type.contentEquals(getResources().getString(R.string.sort_type_fav))) {
            page_num += 1;
            updateView();
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * callback for when an item has been selected.
         */
        void onItemSelected(Movie movie, View view);
    }

    public class fetchMoviesTask extends AsyncTask<String, Void, Collection<Movie>> {

        private final String LOG_TAG = fetchMoviesTask.class.getSimpleName();

        @Override
        protected Collection<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String page_num = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJSON = null;

            try {

                final String TMDB_URI_SCHEME = "http";
                final String TMDB_URI_AUTHORITY = "api.themoviedb.org";
                final String TMDB_URI_FIRST_PATH = "3";
                final String TMDB_URI_SECOND_PATH = "discover";
                final String TMDB_URI_THIRD_PATH = "movie";
                final String API_PARAM = "api_key";
                final String SORT_PARAM = "sort_by";
                final String PAGE_PARAM = "page";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(TMDB_URI_SCHEME)
                        .authority(TMDB_URI_AUTHORITY)
                        .appendPath(TMDB_URI_FIRST_PATH)
                        .appendPath(TMDB_URI_SECOND_PATH)
                        .appendPath(TMDB_URI_THIRD_PATH)
                        .appendQueryParameter(API_PARAM, Key.API_KEY)
                        .appendQueryParameter(SORT_PARAM, sort_type)
                        .appendQueryParameter(PAGE_PARAM, page_num);

                String myUrl = builder.build().toString();
                URL url = new URL(myUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJSON = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesData(moviesJSON);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        private Collection<Movie> getMoviesData(String moviesJSON)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_LIST = "results";

            JSONObject forecastJson = new JSONObject(moviesJSON);
            JSONArray moviesArray = forecastJson.getJSONArray(MDB_LIST);

            ArrayList<Movie> movies = new ArrayList<>();
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObj = moviesArray.getJSONObject(i);
                Movie movie = Movie.deserialize(movieObj);
                movies.add(movie);
            }

            return movies;

        }


        @Override
        protected void onPostExecute(final Collection<Movie> movies_per_page) {
            if (movies_per_page != null) {
                movies.addAll(movies_per_page);
                adapter.addMovies(movies_per_page);
                endlessRecyclerViewAdapter.onDataReady(true);
                setOnClickListenerOnItems(movies);
            }
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }




}



