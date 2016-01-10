package com.movies.app.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.app.moviesapp.DetailsActivity;
import com.movies.app.moviesapp.R;
import com.movies.app.moviesapp.data.MoviesContract;
import com.movies.app.moviesapp.fragments.DetailFragment;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private Cursor mCursor;

    public TrailersAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
//        String url = "http://image.tmdb.org/t/p/w185" + mCursor.getString(DetailFragment.COL_THUMB_URL);
//        int movieId = Integer.parseInt(mCursor.getString(DetailFragment.COL_MOVIE_ID));
//        holder.iconView.setTag(movieId);
//        holder.iconView.setOnClickListener(this);
        holder.title.setText(mCursor.getString(DetailFragment.COL_TRAILER_NAME));
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

//        int movie_id = (int) v.getTag();
//        Intent intent = new Intent(context, DetailsActivity.class)
//                .setData(MoviesContract.MoviesEntry.buildMoviesUri(
//                        movie_id));
//        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final ImageView iconView;
        public final TextView title;

        public ViewHolder(View view){
            super(view);
            iconView = (ImageView) view.findViewById(R.id.item_image);
            title = (TextView) view.findViewById(R.id.extra_title);

        }
    }
}
