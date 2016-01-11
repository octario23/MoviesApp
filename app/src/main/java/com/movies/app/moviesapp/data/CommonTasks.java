package com.movies.app.moviesapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.movies.app.moviesapp.R;

/**
 * Created by omarin on 8/22/15.
 */
public class CommonTasks {

    public static boolean isPopularFilter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_most_popular))
                .equals(context.getString(R.string.pref_sort_most_popular));
    }

    public static boolean isFavoriteFilter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_favorite_movies))
                .equals(context.getString(R.string.pref_sort_favorite_movies));
    }


}
