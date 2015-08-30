package com.movies.app.moviesapp;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.movies.app.moviesapp.data.CommonTasks;
import com.movies.app.moviesapp.data.MoviesContract;
import com.movies.app.moviesapp.sync.MoviesSyncAdapter;

public class MoviesFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MOVIES_LOADER = 0;
    private MoviesAdapter mMoviesAdapter;
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
    public static final String FRAGMENT_TAG = MoviesFragment.class.getSimpleName();
    private AdjustableRecyclerView mRecyclerView;
    private int mPosition = 0;
    private static final String SELECTED_KEY = "selected_position";
    private String settings_key = "settings";


    public static MoviesFragment newInstance() {
        MoviesFragment fragment = new MoviesFragment();
        return fragment;
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMoviesAdapter = new MoviesAdapter(getActivity());
        View rootView = inflater.inflate(R.layout.movies_fragment,container, false);
        mRecyclerView = (AdjustableRecyclerView) rootView.findViewById(R.id.moviesRecycler);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mRecyclerView.setAdapter(mMoviesAdapter);
        if(savedInstanceState !=null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri weatherForLocationUri = MoviesContract.MoviesEntry.buildWeatherLocation("*");
        if(CommonTasks.isPopularFilter(getActivity())){
            settings_key = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        }
        else{
            settings_key = MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        return  new CursorLoader(getActivity(),
                weatherForLocationUri,
                MOVIES_COLUMNS,
                null,
                null,
                settings_key);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
//        if(mPosition !=ListView.INVALID_POSITION){
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    void onLocationChanged( ) {
        updateWeather();

        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    private void updateWeather(){

        MoviesSyncAdapter.syncImmediately(getActivity());
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.pref_sort_key)) ) {
            if(CommonTasks.isPopularFilter(getActivity())){
                settings_key = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
            }else{
                settings_key = MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
            }
        }
    }

}
