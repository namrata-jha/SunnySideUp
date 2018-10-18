package com.example.android.sunnysideup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder> {

    private View.OnClickListener mOnClickListener;
    private ArrayList<Bundle> bundleArrayList;
    private Context ctx;
    private int code;
    private Typeface fonts;

    public WeatherAdapter(ArrayList<Bundle> bundleArrayList, Context ctx, int code) {
        this.bundleArrayList = bundleArrayList;
        this.ctx = ctx;
        this.code = code;
        try {
            fonts = ResourcesCompat.getFont(ctx, R.font.boldfont);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(ctx);
        final View myOwnView = myInflater.inflate(R.layout.display_row, parent, false);

        return new WeatherHolder(myOwnView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
        if(code == 1)
            holder.list_time.setText(bundleArrayList.get(position).getString("date")
                    +"\n"+ bundleArrayList.get(position).getString("time"));
        else if(code == 2)
            holder.list_time.setText(bundleArrayList.get(position).getString("date"));
        holder.list_temp.setText(String.valueOf(bundleArrayList.get(position).getInt("temperature"))+"°");
        holder.list_summary.setText(bundleArrayList.get(position).getString("summary"));
        holder.list_icon.setImageResource(bundleArrayList.get(position).getInt("iconID"));
    }

    @Override
    public int getItemCount() {
        return bundleArrayList.size();
    }

    public class WeatherHolder extends RecyclerView.ViewHolder{
        TextView list_time;
        TextView list_summary;
        TextView list_temp;
        ImageView list_icon;


        public WeatherHolder(final View itemView) {
            super(itemView);
            list_time = itemView.findViewById(R.id.row_time);
            list_summary = itemView.findViewById(R.id.row_summary);
            list_temp = itemView.findViewById(R.id.row_temp);
            list_icon = itemView.findViewById(R.id.row_icon);

            this.list_time.setTypeface(fonts);
            this.list_temp.setTypeface(fonts);
            this.list_summary.setTypeface(fonts);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailedWeatherActivity.class);
                    intent.putExtra("bundle", bundleArrayList.get(getAdapterPosition()));
                    String title = null;
                    if(code == 1)
                        title = bundleArrayList.get(getAdapterPosition()).getString("date")
                    +"\n"+ bundleArrayList.get(getAdapterPosition()).getString("time");
                    else if(code == 2)
                        title = bundleArrayList.get(getAdapterPosition()).getString("date");
                    intent.putExtra("title", title);
                    ctx.startActivity(intent);
                }
            });



        }
    }
}
