package com.ramyfradwan.themovieapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramyfradwan.themovieapp.Model.Movie;
/**
 * Created by RamyFRadwan on 02/10/15.
 */
public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{


    private static final String TAG_FRAGMENT = "MOVIE_FRAGMENT";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, new Detail_ActivityFragment(), TAG_FRAGMENT)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

    }

    @Override
    public void onItemSelected(Movie movie, View view) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putBoolean("twoPane", true);
            args.putParcelable("movie", movie);
            Detail_ActivityFragment fragment = new Detail_ActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, TAG_FRAGMENT)
                    .commit();
            //one-pane mode
        } else {
            Intent intent = new Intent(this, Detail_Activity.class);
            intent.putExtra("movie", movie);

            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ImageView moviePoster = (ImageView) view.findViewById(R.id.thumbnail);
                TextView movieTitle = (TextView) view.findViewById(R.id.title);
                Pair<View, String> p1 = Pair.create((View) moviePoster, "movie_poster");
                Pair<View, String> p2 = Pair.create((View) movieTitle, "movie_title");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, p1, p2);
                startActivity(intent, options.toBundle());

            } else {
                startActivity(intent);
            }
        }
    }

}
