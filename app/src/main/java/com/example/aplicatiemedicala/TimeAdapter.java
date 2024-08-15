package com.example.aplicatiemedicala;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {

    private List<Time> timeList;
    private Context context;
    private OnTimeClickListener onTimeClickListener;
    private  int selectedItemPosition = RecyclerView.NO_POSITION;

    public TimeAdapter(Context context) {
        this.context = context;
        this.timeList = generateHourList();
    }

    public void setOnTimeClickListener(TimeAdapter.OnTimeClickListener listener) {
        this.onTimeClickListener = listener;
    }
    public interface OnTimeClickListener {
        void onTimeClick(int position, Time time);
    }
    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_view, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        Time timeItem = timeList.get(position);
        holder.tvTime.setText(String.format("%02d:00", timeItem.getHour()));

        if (position == selectedItemPosition) {
            holder.itemView.setBackgroundResource(R.drawable.pressed_yellow);
            Log.d("CalendarAdapter","Color - yellow");
        } else {
            holder.itemView.setBackgroundResource(R.drawable.normal_view);
            Log.d("CalendarAdapter","Color - white");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                selectedItemPosition = clickedPosition;
                notifyDataSetChanged();

                if(clickedPosition != RecyclerView.NO_POSITION && onTimeClickListener != null){
                    onTimeClickListener.onTimeClick(clickedPosition,timeItem);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return timeList.size();
    }

    private List<Time> generateHourList() {
        List<Time> hours = new ArrayList<>();
        for (int hour = 9; hour < 18; hour++) {
            hours.add(new Time(hour));
        }
        return hours;
    }
    public static class TimeViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime;
        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

}
