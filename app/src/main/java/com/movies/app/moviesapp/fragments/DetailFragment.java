package com.movies.app.moviesapp.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.app.moviesapp.AdjustableRecyclerView;
import com.movies.app.moviesapp.R;
import com.movies.app.moviesapp.adapters.MoviesAdapter;
import com.movies.app.moviesapp.adapters.TrailersAdapter;
import com.movies.app.moviesapp.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by omarin on 8/29/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String FRAGMENT_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private String mForecast;
    private Uri mUri;
    private TrailersAdapter mTrailersAdapter;

    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private MoviesAdapter mDetailAdapter;
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_THUMB_URL,
            MoviesContract.MoviesEntry.COLUMN_POPULARITY,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE
    };

    private static final String[] TRAILER_COLUMNS = {
            MoviesContract.TrailersEntry.TABLE_NAME + "." + MoviesContract.TrailersEntry._ID,
            MoviesContract.TrailersEntry.COLUMN_NAME,
            MoviesContract.TrailersEntry.TABLE_NAME + "." + MoviesContract.TrailersEntry.COLUMN_MOVIE_ID,
            MoviesContract.TrailersEntry.COLUMN_TRAILER_ID,
            MoviesContract.TrailersEntry.COLUMN_FORMAT,
            MoviesContract.TrailersEntry.COLUMN_KEY,
            MoviesContract.TrailersEntry.COLUMN_SIZE,
            MoviesContract.TrailersEntry.COLUMN_SITE
    };

    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_MOVIE_ID = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_THUMB_URL = 4;
    static final int COL_POPULARITY = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_VOTE_AVERAGE = 7;

    public static final int COL_TRAILER_TABLE_ID = 0;
    public static final int COL_TRAILER_NAME = 1;
    public static final int COL_TRAILER_MOVIE_ID = 2;
    public static final int COL_TRAILER_ID = 3;
    public static final int COL_TRAILER_FORMAT = 4;
    public static final int COL_TRAILER_KEY = 5;
    public static final int COL_TRAILER_SIZE = 6;
    public static final int COL_TRAILER_SITE = 7;

    private ImageView mImageView;
    private TextView mMovieTitle;
    private TextView mReleaseDate;
    private TextView mRateVoting;
    private TextView mDescription;
    private AdjustableRecyclerView mRecyclerView;
    private String mTrailerId;
    private Uri mTrailerUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        mTrailerId =  MoviesContract.MoviesEntry.getMovieIdFromUri(mUri);

        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.image_movie);
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title_textview);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mRateVoting = (TextView) rootView.findViewById(R.id.rate);
        mDescription = (TextView) rootView.findViewById(R.id.description);
        mRecyclerView = (AdjustableRecyclerView) rootView.findViewById(R.id.trailersRecycler);
        mTrailersAdapter = new TrailersAdapter(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mTrailersAdapter);
        mTrailerUri = MoviesContract.TrailersEntry.buildTrailerUriQuery(String.valueOf(mTrailerId));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == DETAIL_LOADER) {
            if (null != mUri) {
                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        MOVIES_COLUMNS,
                        null,
                        null,
                        null
                );
            }
        }else if(id == TRAILER_LOADER){
            if(mTrailerUri !=null){
                return new CursorLoader(
                        getActivity(),
                        mTrailerUri,
                        TRAILER_COLUMNS,
                        null,
                        null,
                        null
                );
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        loader.
        switch(loader.getId()){
            case DETAIL_LOADER:
                if (data != null && data.moveToFirst()) {
                    // Read weather condition ID from cursor
                    String title = data.getString(COL_TITLE);
                    mMovieTitle.setText(title);
                    mReleaseDate.setText(data.getString(COL_RELEASE_DATE));
                    mRateVoting.setText(data.getString(COL_VOTE_AVERAGE));
                    mDescription.setText(data.getString(COL_OVERVIEW));
                    String url = "http://image.tmdb.org/t/p/w185" + data.getString(COL_THUMB_URL);
                    Picasso.with(getActivity())
                            .load(url)
                            .fit()
                            .into(mImageView);
                }
                break;
            case TRAILER_LOADER:
                mTrailersAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailersAdapter.swapCursor(null);
    }
}
