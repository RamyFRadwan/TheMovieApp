package com.ramyfradwan.themovieapp;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ramyfradwan.themovieapp.Adapters.ReviewsAdapter;
import com.ramyfradwan.themovieapp.Adapters.TrailersAdapter;
import com.ramyfradwan.themovieapp.Model.Movie;
import com.ramyfradwan.themovieapp.Model.Review;
import com.ramyfradwan.themovieapp.Model.Trailer;
import com.ramyfradwan.themovieapp.MovieProvider.DbHelper;
import com.ramyfradwan.themovieapp.MovieProvider.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * Created by RamyFRadwan on 02/10/15.
 */

public class Detail_ActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = Detail_ActivityFragment.class.getSimpleName();
    public Movie movie;
    private ArrayList<Trailer> trailers;
    private boolean hasArguments;
    private boolean isFavoured;
    private MenuItem menuItem;
    private String trailer_title;
    private String trailer_key;
    private boolean trailerFound;
    private ArrayList<Review> mReviews;
    public static String Poster;
    private boolean mTwoPane;
    private SharedPreferences sh;
    private String im_path;
    private String pd_path;

    public Detail_ActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
       sh = view.getContext().getSharedPreferences("shared", Context.MODE_PRIVATE);
         im_path = sh.getString("posterpath", "no");
         pd_path = sh.getString("backdroppath","no");
        final DbHelper db = new DbHelper(getActivity())   ;
        Bundle arguments = getArguments();
        if (arguments != null) {
            hasArguments = true;
            if (arguments.getBoolean("twoPane")) {
                mTwoPane = true;
                movie = arguments.getParcelable("movie");
                setMovieData(movie,view);

            } else {
                movie = getActivity().getIntent().getExtras().getParcelable("movie");
                setMovieData(movie,view);
                Configuration config = getActivity().getResources().getConfiguration();
                Toolbar toolbar;
                if (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {

                    toolbar = (Toolbar) view.findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    activity.setSupportActionBar(toolbar);
                    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout)
                            view.findViewById(R.id.collapsingToolbarLayout);
                    toolbarLayout.setTitle(movie.getTitle());
                    final ImageView header_imageView = (ImageView) view.findViewById(R.id.poster);
                    if(MainActivityFragment.getItem() == false) {
                        Picasso
                                .with(getActivity())
                                .load(movie.getPosterURI("w500", "backdrop"))
                                .into(header_imageView);
                    }else if(MainActivityFragment.getItem() == true){
                        Picasso
                                .with(getActivity())
                                .load(new File(pd_path))
                                .into(header_imageView);

                        Log.v("ssssssssssss", "" + pd_path);
                        Log.v("ssssssssssss",Environment.getExternalStorageDirectory().getPath() + "/" + movie.getBackdrop_path());

                    }
//
//                    Picasso
//                            .with(getActivity())
//                            .load(movie.getPosterURI("w500", "backdrop"))
//                            .into(header_imageView);
//
                }
                toolbar = (Toolbar) view.findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.setSupportActionBar(toolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            assert movie != null;


//            isFavoured = Utility.isFavoured(movie.getId());
            setMovieData(movie, view);

            final ToggleButton add_bookmark = (ToggleButton) view.findViewById(R.id.favouriteButton);
            add_bookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                    if (isChecked) {
                        com.squareup.picasso.Target target = new com.squareup.picasso.Target() {

                            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {

                                        File file = new File(Environment.getExternalStorageDirectory().getPath() +"/"+movie.getPoster_path());
                                        Poster= file.getPath();
                                        movie.setPoster(Environment.getExternalStorageDirectory().getPath() +"/"+movie.getPoster_path());
                                        Log.i("ff",file.getPath());
                                        try {
                                            file.createNewFile();
                                            FileOutputStream ostream = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
//                                movie.setBackdrop(Environment.getExternalStorageDirectory().getPath() + "/" + movie.getTitle());
                                            ostream.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();
                            }

                            public void onBitmapFailed(Drawable errorDrawable) {}

                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                if (placeHolderDrawable != null) {}
                            }
                        };
                        com.squareup.picasso.Target target2 = new com.squareup.picasso.Target() {

                            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {

                                        File file = new File(Environment.getExternalStorageDirectory().getPath() +"/"+movie.getBackdrop_path());
                                        Log.i("file",file.getName());
                                        movie.setBackdrop(Environment.getExternalStorageDirectory().getPath() + "/" + movie.getBackdrop_path());

                                        try {
                                            file.createNewFile();
                                            FileOutputStream ostream = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                            movie.setBackdrop(Environment.getExternalStorageDirectory().getPath() + "/" + movie.getTitle());
                                            ostream.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();
                            }

                            public void onBitmapFailed(Drawable errorDrawable) {}

                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                if (placeHolderDrawable != null) {}
                            }
                        };

                        Picasso.with(getActivity())
                                .load(movie.getPosterURI("w500", "poster"))
                                .into(target);
                        Picasso.with(getActivity())
                                .load(movie.getPosterURI("w500", "backdrop"))
                                .into(target2);


//                        db.getWritableDatabase();
                        String[] columns = {MovieContract.MovieEntry.COLUMN_TITLE };
                        String [] args ={movie.getTitle()};
                        Cursor c = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,columns,"title=?",args,null);
                        if(c.getCount() > 0 && c!= null){
                            final String[] SELCTION_COLUMNS = {
                                    Movie.getKeyTitle(),
                            };
                            int row = getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, "title=?", SELCTION_COLUMNS);
                            if (row > 0) {
                                Toast.makeText(getActivity(), "Movie delete Success", Toast.LENGTH_SHORT).show();
                                toggleButton.setVisibility(View.INVISIBLE);
                            }

                            isFavoured = false;
                            add_bookmark.setText("OFF");
                        }else{
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdrop());
                        values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getVote_average());
                        values.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getRelease_date());
                        values.put(MovieContract.MovieEntry.COLUMN_IMAGE, movie.getPoster());

                            Log.v("Imageessssss", "Image" + movie.getPoster() + ",,,,, backdrop_path" + movie.getBackdrop());
                        getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

                            isFavoured = true;
                            add_bookmark.setText("ON");
                    }

                        add_bookmark.setChecked(isFavoured);

//                            String[] selection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID, String.valueOf(movie.getId())};
//                            getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,"where",selection);
                        }
                    }

            });

        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        if (isAdded()) {
//            if (isFavoured && !Utility.isNetworkAvailable()) {
//                fetchOfflineTrailers();
//                fetchOfflineReviews();
//            } else
        if(hasArguments) {
                fetchTrailersTask fetchTrailersTask = new fetchTrailersTask();
                fetchTrailersTask.execute();
                fetchReviewsTask fetchReviewsTask = new fetchReviewsTask();
                fetchReviewsTask.execute();


        }
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        menuItem = menu.findItem(R.id.share_action);
        menuItem.setVisible(trailerFound);
    }

    private Intent createShareIntent(String movie_name, String trailer_name, String key) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Watch " + movie_name + ": " + trailer_name
                        + " http://www.youtube.com/watch?v="
                        + key + " #Udacity_Movies_App");
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().invalidateOptionsMenu();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getActivity().supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareShare() {
        ShareActionProvider mShareActionProvider = new ShareActionProvider(getActivity());
        MenuItemCompat.setActionProvider(menuItem, mShareActionProvider);
        if (trailer_title != null && trailer_key != null) {
            trailerFound = true;
            mShareActionProvider.setShareIntent(createShareIntent(movie.getTitle(),
                    trailer_title, trailer_key));
        } else {
            trailerFound = false;
        }
    }

    private void setMovieData(Movie movie, View view) {
        ImageView poster_imageView = (ImageView) view.findViewById(R.id.moviePoster);
        if(MainActivityFragment.getItem() == false) {
            Picasso
                    .with(getActivity())
                    .load(movie.getPosterURI("w500", "poster"))
                    .into(poster_imageView);
        }else{
            Picasso
                    .with(getActivity())
                    .load(new File(im_path))
                    .into(poster_imageView);
            Log.v("qqqqqqqqqqqqqqweqwe", Environment.getExternalStorageDirectory().getPath() + "/" + movie.getBackdrop_path());

        }

        if (mTwoPane) {
            TextView movie_title = (TextView) view.findViewById(R.id.movieTitle);
            movie_title.setText(movie.getTitle());
            movie_title.setContentDescription(movie.getTitle());
        }

        String release_date = movie.getRelease_date();
        if (release_date.contains("-")) {
            release_date = release_date.split("-")[0];
        }

        TextView release_year = (TextView) view.findViewById(R.id.detail_release);
        release_year.setText(release_date);
        release_year.setContentDescription("Release Date " + release_date);

        TextView vote = (TextView) view.findViewById(R.id.detail_rating);
        vote.setText(String.format("%.1f", movie.getVote_average()) + "/10");
        vote.setContentDescription("Rating " + movie.getVote_average() + "/ 10");

        TextView overview = (TextView) view.findViewById(R.id.detail_overview);
        overview.setText(movie.getOverview());
        overview.setContentDescription(movie.getOverview());

    }


    private void addReviewsToUI(List<Review> favouriteReviews) {
        if (favouriteReviews.size() > 0) {
            if (isAdded()) {
                TextView reviews_title = (TextView) getActivity().findViewById(R.id.reviews_title);
                reviews_title.setVisibility(View.VISIBLE);
                mReviews = new ArrayList<>();
                mReviews.addAll(favouriteReviews);
                ReviewsAdapter mReviewsAdapter = new ReviewsAdapter(getActivity(), mReviews);
                LinearLayout reviewsListView = (LinearLayout) getActivity()
                        .findViewById(R.id.list_item_reviews);
                for (int i = 0; i < mReviewsAdapter.getCount(); i++) {
                    View view = mReviewsAdapter.getView(i, null, null);
                    reviewsListView.addView(view);
                }
            }
        }
    }

    private void addTrailersToUI(List<Trailer> trailers) {
        if (trailers.size() > 0) {
            if (isAdded()) {
                TextView trailers_title = (TextView) getActivity().findViewById(R.id.trailers_title);
                trailers_title.setVisibility(View.VISIBLE);
                final ArrayList<Trailer> mTrailers = new ArrayList<>();
                mTrailers.addAll(trailers);
                TrailersAdapter mTrailersAdapter = new TrailersAdapter(getActivity(), mTrailers);
                LinearLayout trailersListView = (LinearLayout) getActivity().findViewById(R.id.list_item_trailers);

                for (int i = 0; i < mTrailersAdapter.getCount(); i++) {
                    View view = mTrailersAdapter.getView(i, null, null);
                    final int finalI = i;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String youtubeLink = "https://www.youtube.com/watch?v=" + mTrailers.get(finalI).getKey();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
                            Utility.preferPackageForIntent(getActivity(), intent,
                                    Utility.YOUTUBE_PACKAGE_NAME);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(intent);
                        }
                    });
                    trailersListView.addView(view);
                    trailer_title = trailers.get(0).getName();
                    trailer_key = trailers.get(0).getKey();
                }
            }
        }
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
    }

    public class fetchTrailersTask extends AsyncTask<Void, Void, Collection<Trailer>> {


        @Override
        protected Collection<Trailer> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJSON = null;

            try {

                final String TMDB_URI_SCHEME = "http";
                final String TMDB_URI_AUTHORITY = "api.themoviedb.org";
                final String TMDB_URI_FIRST_PATH = "3";
                final String TMDB_URI_SECOND_PATH = "movie";
                final String TMDB_URI_THIRD_PATH = movie.getId() + "";
                final String TMDB_URI_FOURTH_PATH = "videos";
                final String API_PARAM = "api_key";


                Uri.Builder builder = new Uri.Builder();
                builder.scheme(TMDB_URI_SCHEME)
                        .authority(TMDB_URI_AUTHORITY)
                        .appendPath(TMDB_URI_FIRST_PATH)
                        .appendPath(TMDB_URI_SECOND_PATH)
                        .appendPath(TMDB_URI_THIRD_PATH)
                        .appendPath(TMDB_URI_FOURTH_PATH)
                        .appendQueryParameter(API_PARAM, Key.API_KEY);

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
                return getYoutubeVideos(moviesJSON);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        private Collection<Trailer> getYoutubeVideos(String trailersObject)
                throws JSONException {

            final String MDB_LIST = "results";

            JSONObject trailersJSON = new JSONObject(trailersObject);
            JSONArray trailersArray = trailersJSON.getJSONArray(MDB_LIST);

            trailers = new ArrayList<>();
            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailerObj = trailersArray.getJSONObject(i);
                if (trailerObj.getString("site").contentEquals("YouTube")) {
                    Trailer trailer = new Trailer(trailerObj);
                    trailers.add(trailer);
                }
            }
            return trailers;

        }

        @Override
        protected void onPostExecute(final Collection<Trailer> trailers) {
            if (trailers != null) {
                if (isAdded()) {
                    List<Trailer> trailerList = new ArrayList<>(trailers);
                    addTrailersToUI(trailerList);
                    prepareShare();
                    getActivity().invalidateOptionsMenu();

                }
            }
        }
    }
    public class fetchReviewsTask extends AsyncTask<Void, Void, Collection<Review>> {

        private final String LOG_TAG = fetchReviewsTask.class.getSimpleName();

        @Override
        protected Collection<Review> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJSON = null;

            try {

                final String TMDB_URI_SCHEME = "http";
                final String TMDB_URI_AUTHORITY = "api.themoviedb.org";
                final String TMDB_URI_FIRST_PATH = "3";
                final String TMDB_URI_SECOND_PATH = "movie";
                final String TMDB_URI_THIRD_PATH = movie.getId() + "";
                final String TMDB_URI_FOURTH_PATH = "reviews";
                final String API_PARAM = "api_key";


                Uri.Builder builder = new Uri.Builder();
                builder.scheme(TMDB_URI_SCHEME)
                        .authority(TMDB_URI_AUTHORITY)
                        .appendPath(TMDB_URI_FIRST_PATH)
                        .appendPath(TMDB_URI_SECOND_PATH)
                        .appendPath(TMDB_URI_THIRD_PATH)
                        .appendPath(TMDB_URI_FOURTH_PATH)
                        .appendQueryParameter(API_PARAM, Key.API_KEY);

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
                return getReviews(moviesJSON);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        private Collection<Review> getReviews(String trailersObject)
                throws JSONException {

            final String MDB_LIST = "results";

            JSONObject reviewsJSON = new JSONObject(trailersObject);
            JSONArray reviewsArray = reviewsJSON.getJSONArray(MDB_LIST);

            Collection<Review> reviews = new ArrayList<>();
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject reviewObj = reviewsArray.getJSONObject(i);
                Review review = new Review(reviewObj);
                reviews.add(review);
            }
            return reviews;

        }

        @Override
        protected void onPostExecute(Collection<Review> reviews) {
            if (reviews != null) {
                if (isAdded()) {
                    List<Review> reviewList = new ArrayList<>(reviews);
                    addReviewsToUI(reviewList);
//                    if (isFavoured) {
//                        ReviewSelection where = new ReviewSelection();
//                        where.movieId(movie.getId());
//                        ReviewCursor reviewCursor = where.query(getActivity());
//                        if (!reviewCursor.moveToFirst()) {
//                            for (Review review : reviewList) {
//                                com.rfmr.ramyfradwan.kikoko.Utility.addReviewToContentProvider(movie.getId(), review);
//                            }
//                        }
//                    }
                }
            }
        }
    }

}


