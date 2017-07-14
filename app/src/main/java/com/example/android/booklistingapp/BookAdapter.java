package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.BinderThread;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for the Book class.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(@NonNull Context context, @NonNull List<Book> objects) {
        super(context, -1, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        View listItemView = convertView;
        Book book = getItem(position);
        /* Check if we have a spare view */
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_book, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        }
        else
            holder = (ViewHolder) listItemView.getTag();

        /* Display book title */
        holder.titleTextView.setText(book.getTitle());
        /* Display book author */
        holder.authorTextView.setText(book.getAuthorsLine());
        /*Display book publisher */
        holder.publisherTextView.setText(book.getPublisher());
        /* Display book publish date */
        //TODO: Format Date
        //TODO: Display Image
        //holder.publishDateTextView.setText(book.getPublishDate());

        return listItemView;
    }


    /* Implement ViewHolder pattern for increased performance */
    static class ViewHolder{
        @BindView(R.id.book_title)
        TextView titleTextView;
        @BindView(R.id.book_author)
        TextView authorTextView;
        @BindView(R.id.book_image)
        ImageView bookImageView;
        @BindView(R.id.book_publisher)
        TextView publisherTextView;
        @BindView(R.id.book_publish_date)
        TextView publishDateTextView;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
