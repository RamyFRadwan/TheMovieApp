package com.ramyfradwan.themovieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramyfradwan.themovieapp.Model.Trailer;
import com.ramyfradwan.themovieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by RamyFRadwan on 08/10/15.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer> {

    public TrailersAdapter(Context context, ArrayList<Trailer> trailers) {
        super(context, R.layout.list_item_trailers, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailer = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_trailers, parent, false);
            viewHolder.trailer_title = (TextView) convertView.findViewById(R.id.trailer_title);
            viewHolder.trailer_thumbnail = (ImageView) convertView.findViewById(R.id.trailer_thumbnail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.trailer_title.setText(trailer.getName());
        viewHolder.trailer_title.setContentDescription(trailer.getName());

        Picasso.with(getContext()) //
                .load("http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg") //
                .into(viewHolder.trailer_thumbnail);

        return convertView;
    }

    private static class ViewHolder {
        TextView trailer_title;
        ImageView trailer_thumbnail;
    }
}
