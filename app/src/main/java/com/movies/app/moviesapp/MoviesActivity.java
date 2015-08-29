package com.movies.app.moviesapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.movies.app.moviesapp.data.CommonTasks;
import com.movies.app.moviesapp.sync.MoviesSyncAdapter;

public class MoviesActivity extends AppCompatActivity {

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MoviesFragment moviesFragment = (MoviesFragment) fm
                .findFragmentById(R.id.fragment_movies);
        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        String location = CommonTasks.getDefaultFilter(this);
        // update the location in our second pane using the fragment manager
//        if (location != null && !location.equals(mLocation)) {
            MoviesFragment ff = (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
//            mLocation = location;
//        }

    }
}
