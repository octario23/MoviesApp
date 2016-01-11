package com.movies.app.moviesapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.app.moviesapp.R;
import com.movies.app.moviesapp.fragments.DetailFragment;

/**
 * Created by omarin on 1/10/16.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private Cursor mCursor;

    public ReviewsAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.title.setText(mCursor.getString(DetailFragment.COL_REVIEW_CONTENT));
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
