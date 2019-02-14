package com.player.guru.appusagetracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.player.guru.appusagetracker.R;
import com.player.guru.appusagetracker.Utility;
import com.player.guru.appusagetracker.row.StatRow;

import java.util.ArrayList;

/**
 * Created by guru on 2/19/16.
 */
public class StatAdapter extends BaseAdapter {
    Context context;
    ArrayList<StatRow> list;

    public StatAdapter(Context context, ArrayList<StatRow> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }else if (position == list.size()) {
            return 2;
        }

        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView app_name_textview, time_spent_textview, time_spent_percent_textview, opens_per_day_textview;
        int viewType = getItemViewType(position);

        if (convertView == null) {
            if (viewType == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.header_row, parent, false);
            } else {
                if (viewType == 1) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.stat_row, parent, false);
                    app_name_textview = (TextView) convertView.findViewById(R.id.stat_app_name_textview);
                    time_spent_textview = (TextView) convertView.findViewById(R.id.stat_time_spent_textview);
                    time_spent_percent_textview = (TextView) convertView.findViewById(R.id.stat_time_spent_percent_textview);
                    opens_per_day_textview = (TextView) convertView.findViewById(R.id.stat_opens_per_day_textview);
                } else {
                    convertView = LayoutInflater.from(context).inflate(R.layout.total_row, parent, false);
                    app_name_textview = (TextView) convertView.findViewById(R.id.total_app_name_textview);
                    time_spent_textview = (TextView) convertView.findViewById(R.id.total_time_spent_textview);
                    time_spent_percent_textview = (TextView) convertView.findViewById(R.id.total_time_spent_percent_textview);
                    opens_per_day_textview = (TextView) convertView.findViewById(R.id.total_opens_per_day_textview);
                }

                ViewHolder holder = new ViewHolder(app_name_textview, time_spent_textview, time_spent_percent_textview, opens_per_day_textview);
                convertView.setTag(holder);
            }
        }

        if (viewType != 0) {
            StatRow row = list.get(position - 1);
            ViewHolder holder = (ViewHolder) convertView.getTag();

            if (viewType == 1) {
                holder.app_name_textview.setText(row.app_name);
                holder.time_spent_textview.setText(Utility.stringFromSeconds(row.time_spent));
                holder.time_spent_percent_textview.setText((int)row.time_spent_percent + "%");
                holder.opens_per_day_textview.setText((int)row.opens_per_day + "");
            }
            else {
                holder.app_name_textview.setText("Total");
                holder.time_spent_textview.setText(Utility.stringFromSeconds(row.time_spent));
                holder.time_spent_percent_textview.setText("-");
                holder.opens_per_day_textview.setText("-");
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView app_name_textview, time_spent_textview, time_spent_percent_textview, opens_per_day_textview;
        public ViewHolder(TextView app_name_textview, TextView time_spent_textview, TextView time_spent_percent_textview, TextView opens_per_day_textview){
            this.app_name_textview = app_name_textview;
            this.time_spent_textview = time_spent_textview;
            this.time_spent_percent_textview = time_spent_percent_textview;
            this.opens_per_day_textview = opens_per_day_textview;
        }
    }
}
