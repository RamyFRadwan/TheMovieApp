package com.ramyfradwan.themovieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.florent37.picassopalette.PicassoPalette;
import com.ramyfradwan.themovieapp.MainActivityFragment;
import com.ramyfradwan.themovieapp.Model.Movie;
import com.ramyfradwan.themovieapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collection;


/**
 * Created by RamyFRadwan on 06/10/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    OnItemClickListener mItemClickListener;
    private Collection<Movie> movies;
    private Context context;
    Intent intent;
    public static String path;
    private SharedPreferences shared_detail;
    SharedPreferences.Editor editor ;

    public MoviesAdapter(Context context, Collection<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }


    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_row, null);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder moviesViewHolder, int i) {
        final Movie movie = (Movie) movies.toArray()[i];

        //Download image using picasso library


        if(MainActivityFragment.getItem() == false) {
            Picasso.with(context)
                    .load(movie.getPosterURI("w500", "poster"))
                    .into(moviesViewHolder.imageView,
                            PicassoPalette
                                    .with(movie.getPosterURI("w500", "poster").toString(),
                                            moviesViewHolder.imageView)
                                    .use(PicassoPalette.Profile.VIBRANT_LIGHT)
                                    .intoBackground(moviesViewHolder.full_layout,
                                            PicassoPalette.Swatch.RGB)
                                    .intoTextColor(moviesViewHolder.titleView,
                                            PicassoPalette.Swatch.TITLE_TEXT_COLOR));
        }else {
            Picasso.with(context)
                    .load(new File(movie.getPoster()))
                    .into(moviesViewHolder.imageView,
                            PicassoPalette
                                    .with(new File(movie.getPoster()).toString(),
                                            moviesViewHolder.imageView)
                                    .use(PicassoPalette.Profile.VIBRANT_LIGHT)
                                    .intoBackground(moviesViewHolder.full_layout,
                                            PicassoPalette.Swatch.RGB)
                                    .intoTextColor(moviesViewHolder.titleView,
                                            PicassoPalette.Swatch.TITLE_TEXT_COLOR));

            shared_detail = context.getSharedPreferences("shared", Context.MODE_PRIVATE);


            editor = shared_detail.edit();
            editor.putString("posterpath", movie.getPoster());
            editor.putString("backdroppath",movie.getBackdrop());
            editor.commit();
        }


        //Setting text view title
        moviesViewHolder.titleView.setText(movie.getTitle());
        moviesViewHolder.titleView.setContentDescription(movie.getTitle());

    }


    public int getItemCount() {
        return (null != movies ? movies.size() : 0);
    }

    public void addMovies(Collection<Movie> movies) {
        if (this.movies == null) {
            this.movies = movies;
        } else {
            this.movies.addAll(movies);
        }
    }

    public void clear() {
        if (this.movies != null) {
            int size = this.movies.size();
            this.movies.clear();
//            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LinearLayout full_layout;
        protected LinearLayout movieHolder;
        protected LinearLayout movieTitleHolder;
        protected ImageView imageView;
        protected TextView titleView;
        protected ToggleButton favouriteButton;

        public MoviesViewHolder(View view) {
            super(view);
            this.full_layout = (LinearLayout) view.findViewById(R.id.movie_layout);
            this.movieHolder = (LinearLayout) view.findViewById(R.id.movieHolder);
            this.movieTitleHolder = (LinearLayout) view.findViewById(R.id.movieTitleHolder);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.titleView = (TextView) view.findViewById(R.id.title);
            this.favouriteButton = (ToggleButton) view.findViewById(R.id.favouriteButton);
            this.full_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }
}
