package com.movies.app.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.movies.app.moviesapp.DetailsActivity;
import com.movies.app.moviesapp.R;
import com.movies.app.moviesapp.data.MoviesContract;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private Cursor mCursor;

    public TrailersAdapter(Context context){
        this.context = context;
    }

    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TrailersAdapter.ViewHolder holder, int position) {

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
        Intent intent = new Intent(context, DetailsActivity.class)
                .setData(MoviesContract.MoviesEntry.buildMoviesUri(
                        movie_id));
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final ImageView iconView;

        public ViewHolder(View view){
            super(view);
            iconView = (ImageView) view.findViewById(R.id.movie_poster);

        }
    }
}
