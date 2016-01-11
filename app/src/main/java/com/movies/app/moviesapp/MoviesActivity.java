package com.movies.app.moviesapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.movies.app.moviesapp.adapters.MoviesAdapter;
import com.movies.app.moviesapp.fragments.DetailFragment;
import com.movies.app.moviesapp.fragments.MoviesFragment;
import com.movies.app.moviesapp.sync.MoviesSyncAdapter;

public class MoviesActivity extends AppCompatActivity implements MoviesAdapter.OnClickCallback {

    private FragmentManager fm;
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DetailFragment.FRAGMENT_TAG)
                        .commit();
            }
        }else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri elementUri) {

        if(mTwoPane){
            Bundle args =  new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, elementUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,fragment,DetailFragment.FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent  = new Intent(this, DetailsActivity.class)
                    .setData(elementUri);
            startActivity(intent);

        }
    }
}
