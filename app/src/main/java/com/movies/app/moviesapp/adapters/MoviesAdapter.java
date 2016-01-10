package com.movies.app.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.movies.app.moviesapp.DetailsActivity;
import com.movies.app.moviesapp.fragments.MoviesFragment;
import com.movies.app.moviesapp.R;
import com.movies.app.moviesapp.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by omarin on 8/22/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private Cursor mCursor;

    public interface OnClickCallback{
        void onItemSelected(Uri elementUri);
    }

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
        int movieId = Integer.parseInt(mCursor.getString(MoviesFragment.COL_MOVIE_ID));

        Picasso.with(context).load(url)
                .fit()
                .into(holder.iconView);
        holder.iconView.setTag(movieId);
        holder.iconView.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {

        int movie_id = (int) v.getTag();
        ((OnClickCallback) context).onItemSelected(
                MoviesContract.MoviesEntry.buildMoviesUriQuery(String.valueOf(movie_id))
        );

    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final ImageView iconView;

        public ViewHolder(View view){
            super(view);
            iconView = (ImageView) view.findViewById(R.id.movie_poster);

        }
    }



}
