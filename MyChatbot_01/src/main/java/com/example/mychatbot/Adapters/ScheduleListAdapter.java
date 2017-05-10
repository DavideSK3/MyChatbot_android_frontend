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
import com.example.mychatbot.Entities.Schedule;
import com.example.mychatbot.R;
import com.example.mychatbot.Utilities.DownloadImageTask;

import java.util.ArrayList;

/**
 * Created by david on 15/04/2017.
 */

public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

    private ArrayList<Schedule> mScheduleList;
    private Context mContext = null;
    private Activity mActivity = null;
    private int mLayout;

    public ScheduleListAdapter(Context context, Activity activity, int layoutId, ArrayList<Schedule> scheduleList) {
        super(context, layoutId, scheduleList);
        mScheduleList = scheduleList;
        mContext = context;
        mActivity = activity;
        mLayout = layoutId;
    }

    public void reset(){
        mScheduleList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        final Schedule s = mScheduleList.get(position);

        TextView cinema = (TextView) view.findViewById(R.id.cinema);
        cinema.setText(s.getCinema());

        TextView hour = (TextView) view.findViewById(R.id.hour);
        hour.setText(Html.fromHtml("<b>"+s.getHour()+"</b>"),TextView.BufferType.SPANNABLE);

        return view;
    }
}

