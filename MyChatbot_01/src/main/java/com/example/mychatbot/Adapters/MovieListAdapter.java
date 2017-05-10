package com.example.mychatbot.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychatbot.Entities.Movie;
import com.example.mychatbot.Entities.Restaurant;
import com.example.mychatbot.R;
import com.example.mychatbot.Utilities.DownloadImageTask;
import com.example.mychatbot.Utilities.IntentUtils;

import java.util.ArrayList;

/**
 * Created by david on 15/04/2017.
 */

public class MovieListAdapter extends ArrayAdapter<Movie> {

    private ArrayList<Movie> mMovieList;
    private Context mContext = null;
    private Activity mActivity = null;
    private int mLayout;
    private String lat;
    private String lon;

    public MovieListAdapter(Context context, Activity activity, int layoutId, ArrayList<Movie> movieList) {
        super(context, layoutId, movieList);
        mMovieList = movieList;
        mContext = context;
        mActivity = activity;
        mLayout = layoutId;
    }

    public void reset(){
        mMovieList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        final Movie m = mMovieList.get(position);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(m.getName());

        TextView length = (TextView) view.findViewById(R.id.length);
        length.setText(Html.fromHtml("<b>Length:</b> "+m.getLength()), TextView.BufferType.SPANNABLE);

        TextView category = (TextView) view.findViewById(R.id.category);
        category.setText(Html.fromHtml("<b>Categories:</b> "+m.getCategory()), TextView.BufferType.SPANNABLE);

        TextView desc = (TextView) view.findViewById(R.id.desc);
        desc.setText("\""+m.getDesc()+"\"");

        ImageView image = (ImageView) view.findViewById(R.id.image);

        new DownloadImageTask(image)
                .execute(m.getImage());

        return view;
    }
}

