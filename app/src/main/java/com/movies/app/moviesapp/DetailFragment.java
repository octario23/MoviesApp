package com.movies.app.moviesapp;

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
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.app.moviesapp.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by omarin on 8/29/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private String mForecast;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;
    private MoviesAdapter mDetailAdapter;
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_THUMB_URL,
            MoviesContract.MoviesEntry.COLUMN_POPULARITY,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
    };

    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_MOVIE_ID = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_THUMB_URL = 4;
    static final int COL_POPULARITY = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_VOTE_AVERAGE = 7;

    private ImageView mImageView;
    private TextView mMovieTitle;
    private TextView mReleaseDate;
    private TextView mRateVoting;
    private TextView mDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.image_movie);
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title_textview);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mRateVoting = (TextView) rootView.findViewById(R.id.rate);
        mDescription = (TextView) rootView.findViewById(R.id.description);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
