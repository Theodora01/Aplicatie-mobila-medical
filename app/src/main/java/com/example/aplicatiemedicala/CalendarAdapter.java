package com.example.aplicatiemedicala;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<String> daysOfWeek;
    private List<String> dates;
    private OnDateClickListener onDateClickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION;

    public CalendarAdapter(List<String> daysOfWeek, List<String> dates) {
        this.daysOfWeek = daysOfWeek;
        this.dates = dates;
    }
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_view, parent, false);
        return new CalendarViewHolder(view);
    }
    public void setOnDateClickListener(OnDateClickListener listener) {
        this.onDateClickListener = listener;
    }

    public interface OnDateClickListener {
        void onDateClick(int position, String dayOfWeek, String date);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder,final int position) {

        String dayOfWeek = daysOfWeek.get(position);
        String date = dates.get(position);

        holder.tvDaysOfWeek.setText(daysOfWeek.get(position));
        holder.tvDates.setText(dates.get(position));

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

                if (clickedPosition != RecyclerView.NO_POSITION && onDateClickListener != null) {
                    onDateClickListener.onDateClick(clickedPosition,dayOfWeek, date);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return daysOfWeek.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder{

        TextView tvDaysOfWeek, tvDates;
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDaysOfWeek = itemView.findViewById(R.id.tvDaysOfWeek);
            tvDates = itemView.findViewById(R.id.tvDates);
        }
    }
}
