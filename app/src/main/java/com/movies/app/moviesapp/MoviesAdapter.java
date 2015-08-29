package com.movies.app.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.app.moviesapp.data.CommonTasks;
import com.movies.app.moviesapp.data.MoviesContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by omarin on 8/22/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Context context;
    private Cursor mCursor;

    public MoviesAdapter(Context context){
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_poster_item, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String url = "http://image.tmdb.org/t/p/w185" + mCursor.getString(MoviesFragment.COL_THUMB_URL);
        Picasso.with(context).load(url)
                .fit()
                .into(holder.iconView);
    }

    @Override
    public int getItemCount() {
        if(mCursor== null){
            return 0;
        }else{
           return mCursor.getCount();
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final ImageView iconView;

        public ViewHolder(View view){
            super(view);
            iconView = (ImageView) view.findViewById(R.id.movie_poster);

        }
    }



}
