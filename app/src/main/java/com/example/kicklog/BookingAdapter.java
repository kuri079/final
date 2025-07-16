package com.example.kicklog;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.Booking;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private final List<Booking> bookingList;
    public BookingAdapter(List<Booking> bookingList) { this.bookingList = bookingList; }

    @NonNull @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        String bookingText = booking.getMinute() + "' " + booking.getPlayer().getName();
        holder.bookingInfo.setText(bookingText);

        if ("YELLOW_CARD".equals(booking.getCard())) {
            holder.cardColorView.setBackgroundColor(Color.RED); // ◀️ 黄色に設定
        } else {
            holder.cardColorView.setBackgroundColor(Color.YELLOW);
        }
    }

    @Override
    public int getItemCount() { return bookingList.size(); }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        View cardColorView;
        TextView bookingInfo;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardColorView = itemView.findViewById(R.id.viewCardColor);
            bookingInfo = itemView.findViewById(R.id.textViewBookingInfo);
        }
    }
}